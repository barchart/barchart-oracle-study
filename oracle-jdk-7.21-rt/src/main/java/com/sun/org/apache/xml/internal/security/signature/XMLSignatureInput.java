/*     */ package com.sun.org.apache.xml.internal.security.signature;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
/*     */ import com.sun.org.apache.xml.internal.security.c14n.implementations.CanonicalizerBase;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
/*     */ import com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler;
/*     */ import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
/*     */ import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class XMLSignatureInput
/*     */   implements Cloneable
/*     */ {
/*  56 */   static Logger log = Logger.getLogger(XMLSignatureInput.class.getName());
/*     */ 
/*  73 */   InputStream _inputOctetStreamProxy = null;
/*     */ 
/*  77 */   Set _inputNodeSet = null;
/*     */ 
/*  81 */   Node _subNode = null;
/*     */ 
/*  85 */   Node excludeNode = null;
/*     */ 
/*  89 */   boolean excludeComments = false;
/*     */ 
/*  91 */   boolean isNodeSet = false;
/*     */ 
/*  95 */   byte[] bytes = null;
/*     */ 
/* 100 */   private String _MIMEType = null;
/*     */ 
/* 105 */   private String _SourceURI = null;
/*     */ 
/* 110 */   List nodeFilters = new ArrayList();
/*     */ 
/* 112 */   boolean needsToBeExpanded = false;
/* 113 */   OutputStream outputStream = null;
/*     */ 
/*     */   public boolean isNeedsToBeExpanded()
/*     */   {
/* 120 */     return this.needsToBeExpanded;
/*     */   }
/*     */ 
/*     */   public void setNeedsToBeExpanded(boolean paramBoolean)
/*     */   {
/* 128 */     this.needsToBeExpanded = paramBoolean;
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput(byte[] paramArrayOfByte)
/*     */   {
/* 144 */     this.bytes = paramArrayOfByte;
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput(InputStream paramInputStream)
/*     */   {
/* 154 */     this._inputOctetStreamProxy = paramInputStream;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public XMLSignatureInput(String paramString)
/*     */   {
/* 168 */     this(paramString.getBytes());
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public XMLSignatureInput(String paramString1, String paramString2)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 184 */     this(paramString1.getBytes(paramString2));
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput(Node paramNode)
/*     */   {
/* 195 */     this._subNode = paramNode;
/*     */   }
/*     */ 
/*     */   public XMLSignatureInput(Set paramSet)
/*     */   {
/* 205 */     this._inputNodeSet = paramSet;
/*     */   }
/*     */ 
/*     */   public Set getNodeSet()
/*     */     throws CanonicalizationException, ParserConfigurationException, IOException, SAXException
/*     */   {
/* 220 */     return getNodeSet(false);
/*     */   }
/*     */ 
/*     */   public Set getNodeSet(boolean paramBoolean)
/*     */     throws ParserConfigurationException, IOException, SAXException, CanonicalizationException
/*     */   {
/* 237 */     if (this._inputNodeSet != null) {
/* 238 */       return this._inputNodeSet;
/*     */     }
/* 240 */     if ((this._inputOctetStreamProxy == null) && (this._subNode != null))
/*     */     {
/* 242 */       if (paramBoolean) {
/* 243 */         XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this._subNode));
/*     */       }
/* 245 */       this._inputNodeSet = new HashSet();
/* 246 */       XMLUtils.getSet(this._subNode, this._inputNodeSet, this.excludeNode, this.excludeComments);
/*     */ 
/* 248 */       return this._inputNodeSet;
/* 249 */     }if (isOctetStream()) {
/* 250 */       convertToNodes();
/* 251 */       HashSet localHashSet = new HashSet();
/* 252 */       XMLUtils.getSet(this._subNode, localHashSet, null, false);
/*     */ 
/* 254 */       return localHashSet;
/*     */     }
/*     */ 
/* 257 */     throw new RuntimeException("getNodeSet() called but no input data present");
/*     */   }
/*     */ 
/*     */   public InputStream getOctetStream()
/*     */     throws IOException
/*     */   {
/* 271 */     return getResetableInputStream();
/*     */   }
/*     */ 
/*     */   public InputStream getOctetStreamReal()
/*     */   {
/* 278 */     return this._inputOctetStreamProxy;
/*     */   }
/*     */ 
/*     */   public byte[] getBytes()
/*     */     throws IOException, CanonicalizationException
/*     */   {
/* 292 */     if (this.bytes != null) {
/* 293 */       return this.bytes;
/*     */     }
/* 295 */     InputStream localInputStream = getResetableInputStream();
/* 296 */     if (localInputStream != null)
/*     */     {
/* 298 */       if (this.bytes == null) {
/* 299 */         localInputStream.reset();
/* 300 */         this.bytes = JavaUtils.getBytesFromStream(localInputStream);
/*     */       }
/* 302 */       return this.bytes;
/*     */     }
/* 304 */     Canonicalizer20010315OmitComments localCanonicalizer20010315OmitComments = new Canonicalizer20010315OmitComments();
/*     */ 
/* 306 */     this.bytes = localCanonicalizer20010315OmitComments.engineCanonicalize(this);
/* 307 */     return this.bytes;
/*     */   }
/*     */ 
/*     */   public boolean isNodeSet()
/*     */   {
/* 316 */     return ((this._inputOctetStreamProxy == null) && (this._inputNodeSet != null)) || (this.isNodeSet);
/*     */   }
/*     */ 
/*     */   public boolean isElement()
/*     */   {
/* 326 */     return (this._inputOctetStreamProxy == null) && (this._subNode != null) && (this._inputNodeSet == null) && (!this.isNodeSet);
/*     */   }
/*     */ 
/*     */   public boolean isOctetStream()
/*     */   {
/* 336 */     return ((this._inputOctetStreamProxy != null) || (this.bytes != null)) && (this._inputNodeSet == null) && (this._subNode == null);
/*     */   }
/*     */ 
/*     */   public boolean isOutputStreamSet()
/*     */   {
/* 348 */     return this.outputStream != null;
/*     */   }
/*     */ 
/*     */   public boolean isByteArray()
/*     */   {
/* 357 */     return (this.bytes != null) && (this._inputNodeSet == null) && (this._subNode == null);
/*     */   }
/*     */ 
/*     */   public boolean isInitialized()
/*     */   {
/* 367 */     return (isOctetStream()) || (isNodeSet());
/*     */   }
/*     */ 
/*     */   public String getMIMEType()
/*     */   {
/* 376 */     return this._MIMEType;
/*     */   }
/*     */ 
/*     */   public void setMIMEType(String paramString)
/*     */   {
/* 385 */     this._MIMEType = paramString;
/*     */   }
/*     */ 
/*     */   public String getSourceURI()
/*     */   {
/* 394 */     return this._SourceURI;
/*     */   }
/*     */ 
/*     */   public void setSourceURI(String paramString)
/*     */   {
/* 403 */     this._SourceURI = paramString;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 411 */     if (isNodeSet()) {
/* 412 */       return "XMLSignatureInput/NodeSet/" + this._inputNodeSet.size() + " nodes/" + getSourceURI();
/*     */     }
/*     */ 
/* 415 */     if (isElement()) {
/* 416 */       return "XMLSignatureInput/Element/" + this._subNode + " exclude " + this.excludeNode + " comments:" + this.excludeComments + "/" + getSourceURI();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 421 */       return "XMLSignatureInput/OctetStream/" + getBytes().length + " octets/" + getSourceURI();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 424 */       return "XMLSignatureInput/OctetStream//" + getSourceURI(); } catch (CanonicalizationException localCanonicalizationException) {
/*     */     }
/* 426 */     return "XMLSignatureInput/OctetStream//" + getSourceURI();
/*     */   }
/*     */ 
/*     */   public String getHTMLRepresentation()
/*     */     throws XMLSignatureException
/*     */   {
/* 438 */     XMLSignatureInputDebugger localXMLSignatureInputDebugger = new XMLSignatureInputDebugger(this);
/*     */ 
/* 440 */     return localXMLSignatureInputDebugger.getHTMLRepresentation();
/*     */   }
/*     */ 
/*     */   public String getHTMLRepresentation(Set paramSet)
/*     */     throws XMLSignatureException
/*     */   {
/* 453 */     XMLSignatureInputDebugger localXMLSignatureInputDebugger = new XMLSignatureInputDebugger(this, paramSet);
/*     */ 
/* 456 */     return localXMLSignatureInputDebugger.getHTMLRepresentation();
/*     */   }
/*     */ 
/*     */   public Node getExcludeNode()
/*     */   {
/* 464 */     return this.excludeNode;
/*     */   }
/*     */ 
/*     */   public void setExcludeNode(Node paramNode)
/*     */   {
/* 472 */     this.excludeNode = paramNode;
/*     */   }
/*     */ 
/*     */   public Node getSubNode()
/*     */   {
/* 480 */     return this._subNode;
/*     */   }
/*     */ 
/*     */   public boolean isExcludeComments()
/*     */   {
/* 487 */     return this.excludeComments;
/*     */   }
/*     */ 
/*     */   public void setExcludeComments(boolean paramBoolean)
/*     */   {
/* 494 */     this.excludeComments = paramBoolean;
/*     */   }
/*     */ 
/*     */   public void updateOutputStream(OutputStream paramOutputStream)
/*     */     throws CanonicalizationException, IOException
/*     */   {
/* 504 */     updateOutputStream(paramOutputStream, false);
/*     */   }
/*     */ 
/*     */   public void updateOutputStream(OutputStream paramOutputStream, boolean paramBoolean) throws CanonicalizationException, IOException
/*     */   {
/* 509 */     if (paramOutputStream == this.outputStream) {
/* 510 */       return;
/*     */     }
/* 512 */     if (this.bytes != null) {
/* 513 */       paramOutputStream.write(this.bytes);
/* 514 */       return;
/* 515 */     }if (this._inputOctetStreamProxy == null) {
/* 516 */       localObject = null;
/* 517 */       if (paramBoolean)
/* 518 */         localObject = new Canonicalizer11_OmitComments();
/*     */       else {
/* 520 */         localObject = new Canonicalizer20010315OmitComments();
/*     */       }
/* 522 */       ((CanonicalizerBase)localObject).setWriter(paramOutputStream);
/* 523 */       ((CanonicalizerBase)localObject).engineCanonicalize(this);
/* 524 */       return;
/*     */     }
/* 526 */     Object localObject = getResetableInputStream();
/* 527 */     if (this.bytes != null)
/*     */     {
/* 529 */       paramOutputStream.write(this.bytes, 0, this.bytes.length);
/* 530 */       return;
/*     */     }
/* 532 */     ((InputStream)localObject).reset();
/*     */ 
/* 534 */     byte[] arrayOfByte = new byte[1024];
/*     */     int i;
/* 535 */     while ((i = ((InputStream)localObject).read(arrayOfByte)) > 0)
/* 536 */       paramOutputStream.write(arrayOfByte, 0, i);
/*     */   }
/*     */ 
/*     */   public void setOutputStream(OutputStream paramOutputStream)
/*     */   {
/* 545 */     this.outputStream = paramOutputStream;
/*     */   }
/*     */ 
/*     */   protected InputStream getResetableInputStream() throws IOException {
/* 549 */     if ((this._inputOctetStreamProxy instanceof ByteArrayInputStream)) {
/* 550 */       if (!this._inputOctetStreamProxy.markSupported()) {
/* 551 */         throw new RuntimeException("Accepted as Markable but not truly been" + this._inputOctetStreamProxy);
/*     */       }
/* 553 */       return this._inputOctetStreamProxy;
/*     */     }
/* 555 */     if (this.bytes != null) {
/* 556 */       this._inputOctetStreamProxy = new ByteArrayInputStream(this.bytes);
/* 557 */       return this._inputOctetStreamProxy;
/*     */     }
/* 559 */     if (this._inputOctetStreamProxy == null)
/* 560 */       return null;
/* 561 */     if (this._inputOctetStreamProxy.markSupported()) {
/* 562 */       log.log(Level.INFO, "Mark Suported but not used as reset");
/*     */     }
/* 564 */     this.bytes = JavaUtils.getBytesFromStream(this._inputOctetStreamProxy);
/* 565 */     this._inputOctetStreamProxy.close();
/* 566 */     this._inputOctetStreamProxy = new ByteArrayInputStream(this.bytes);
/* 567 */     return this._inputOctetStreamProxy;
/*     */   }
/*     */ 
/*     */   public void addNodeFilter(NodeFilter paramNodeFilter)
/*     */   {
/* 574 */     if (isOctetStream()) {
/*     */       try {
/* 576 */         convertToNodes();
/*     */       } catch (Exception localException) {
/* 578 */         throw new XMLSecurityRuntimeException("signature.XMLSignatureInput.nodesetReference", localException);
/*     */       }
/*     */     }
/* 581 */     this.nodeFilters.add(paramNodeFilter);
/*     */   }
/*     */ 
/*     */   public List getNodeFilters()
/*     */   {
/* 589 */     return this.nodeFilters;
/*     */   }
/*     */ 
/*     */   public void setNodeSet(boolean paramBoolean)
/*     */   {
/* 596 */     this.isNodeSet = paramBoolean;
/*     */   }
/*     */ 
/*     */   void convertToNodes() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException
/*     */   {
/* 601 */     DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/* 602 */     localDocumentBuilderFactory.setValidating(false);
/* 603 */     localDocumentBuilderFactory.setNamespaceAware(true);
/* 604 */     DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/*     */     try
/*     */     {
/* 607 */       localDocumentBuilder.setErrorHandler(new IgnoreAllErrorHandler());
/*     */ 
/* 610 */       Document localDocument1 = localDocumentBuilder.parse(getOctetStream());
/*     */ 
/* 612 */       this._subNode = localDocument1.getDocumentElement();
/*     */     }
/*     */     catch (SAXException localSAXException)
/*     */     {
/* 616 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*     */ 
/* 618 */       localByteArrayOutputStream.write("<container>".getBytes());
/* 619 */       localByteArrayOutputStream.write(getBytes());
/* 620 */       localByteArrayOutputStream.write("</container>".getBytes());
/*     */ 
/* 622 */       byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
/* 623 */       Document localDocument2 = localDocumentBuilder.parse(new ByteArrayInputStream(arrayOfByte));
/* 624 */       this._subNode = localDocument2.getDocumentElement().getFirstChild().getFirstChild();
/*     */     }
/* 626 */     this._inputOctetStreamProxy = null;
/* 627 */     this.bytes = null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput
 * JD-Core Version:    0.6.2
 */