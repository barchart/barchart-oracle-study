/*      */ package com.sun.org.apache.xml.internal.serialize;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
/*      */ import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
/*      */ import com.sun.org.apache.xerces.internal.dom.DOMLocatorImpl;
/*      */ import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
/*      */ import com.sun.org.apache.xerces.internal.dom.DOMNormalizer;
/*      */ import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
/*      */ import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
/*      */ import com.sun.org.apache.xerces.internal.util.DOMUtil;
/*      */ import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.XML11Char;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.StringWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import org.w3c.dom.Attr;
/*      */ import org.w3c.dom.Comment;
/*      */ import org.w3c.dom.DOMConfiguration;
/*      */ import org.w3c.dom.DOMErrorHandler;
/*      */ import org.w3c.dom.DOMException;
/*      */ import org.w3c.dom.DOMStringList;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.DocumentFragment;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.ProcessingInstruction;
/*      */ import org.w3c.dom.ls.LSException;
/*      */ import org.w3c.dom.ls.LSOutput;
/*      */ import org.w3c.dom.ls.LSSerializer;
/*      */ import org.w3c.dom.ls.LSSerializerFilter;
/*      */ 
/*      */ public class DOMSerializerImpl
/*      */   implements LSSerializer, DOMConfiguration
/*      */ {
/*      */   private XMLSerializer serializer;
/*      */   private XML11Serializer xml11Serializer;
/*      */   private DOMStringList fRecognizedParameters;
/*  101 */   protected short features = 0;
/*      */   protected static final short NAMESPACES = 1;
/*      */   protected static final short WELLFORMED = 2;
/*      */   protected static final short ENTITIES = 4;
/*      */   protected static final short CDATA = 8;
/*      */   protected static final short SPLITCDATA = 16;
/*      */   protected static final short COMMENTS = 32;
/*      */   protected static final short DISCARDDEFAULT = 64;
/*      */   protected static final short INFOSET = 128;
/*      */   protected static final short XMLDECL = 256;
/*      */   protected static final short NSDECL = 512;
/*      */   protected static final short DOM_ELEMENT_CONTENT_WHITESPACE = 1024;
/*      */   protected static final short FORMAT_PRETTY_PRINT = 2048;
/*  117 */   private DOMErrorHandler fErrorHandler = null;
/*  118 */   private final DOMErrorImpl fError = new DOMErrorImpl();
/*  119 */   private final DOMLocatorImpl fLocator = new DOMLocatorImpl();
/*  120 */   private static final RuntimeException abort = new RuntimeException();
/*      */ 
/*      */   public DOMSerializerImpl()
/*      */   {
/*  130 */     this.features = ((short)(this.features | 0x1));
/*  131 */     this.features = ((short)(this.features | 0x4));
/*  132 */     this.features = ((short)(this.features | 0x20));
/*  133 */     this.features = ((short)(this.features | 0x8));
/*  134 */     this.features = ((short)(this.features | 0x10));
/*  135 */     this.features = ((short)(this.features | 0x2));
/*  136 */     this.features = ((short)(this.features | 0x200));
/*  137 */     this.features = ((short)(this.features | 0x400));
/*  138 */     this.features = ((short)(this.features | 0x40));
/*  139 */     this.features = ((short)(this.features | 0x100));
/*      */ 
/*  141 */     this.serializer = new XMLSerializer();
/*  142 */     initSerializer(this.serializer);
/*      */   }
/*      */ 
/*      */   public DOMConfiguration getDomConfig()
/*      */   {
/*  152 */     return this;
/*      */   }
/*      */ 
/*      */   public void setParameter(String name, Object value)
/*      */     throws DOMException
/*      */   {
/*  159 */     if ((value instanceof Boolean)) {
/*  160 */       boolean state = ((Boolean)value).booleanValue();
/*  161 */       if (name.equalsIgnoreCase("infoset")) {
/*  162 */         if (state) {
/*  163 */           this.features = ((short)(this.features & 0xFFFFFFFB));
/*  164 */           this.features = ((short)(this.features & 0xFFFFFFF7));
/*  165 */           this.features = ((short)(this.features | 0x1));
/*  166 */           this.features = ((short)(this.features | 0x200));
/*  167 */           this.features = ((short)(this.features | 0x2));
/*  168 */           this.features = ((short)(this.features | 0x20));
/*      */         }
/*      */       }
/*  171 */       else if (name.equalsIgnoreCase("xml-declaration")) {
/*  172 */         this.features = ((short)(state ? this.features | 0x100 : this.features & 0xFFFFFEFF));
/*      */       }
/*  174 */       else if (name.equalsIgnoreCase("namespaces")) {
/*  175 */         this.features = ((short)(state ? this.features | 0x1 : this.features & 0xFFFFFFFE));
/*      */ 
/*  179 */         this.serializer.fNamespaces = state;
/*  180 */       } else if (name.equalsIgnoreCase("split-cdata-sections")) {
/*  181 */         this.features = ((short)(state ? this.features | 0x10 : this.features & 0xFFFFFFEF));
/*      */       }
/*  185 */       else if (name.equalsIgnoreCase("discard-default-content")) {
/*  186 */         this.features = ((short)(state ? this.features | 0x40 : this.features & 0xFFFFFFBF));
/*      */       }
/*  190 */       else if (name.equalsIgnoreCase("well-formed")) {
/*  191 */         this.features = ((short)(state ? this.features | 0x2 : this.features & 0xFFFFFFFD));
/*      */       }
/*  195 */       else if (name.equalsIgnoreCase("entities")) {
/*  196 */         this.features = ((short)(state ? this.features | 0x4 : this.features & 0xFFFFFFFB));
/*      */       }
/*  201 */       else if (name.equalsIgnoreCase("cdata-sections")) {
/*  202 */         this.features = ((short)(state ? this.features | 0x8 : this.features & 0xFFFFFFF7));
/*      */       }
/*  207 */       else if (name.equalsIgnoreCase("comments")) {
/*  208 */         this.features = ((short)(state ? this.features | 0x20 : this.features & 0xFFFFFFDF));
/*      */       }
/*  213 */       else if (name.equalsIgnoreCase("format-pretty-print")) {
/*  214 */         this.features = ((short)(state ? this.features | 0x800 : this.features & 0xFFFFF7FF));
/*      */       }
/*  219 */       else if ((name.equalsIgnoreCase("canonical-form")) || (name.equalsIgnoreCase("validate-if-schema")) || (name.equalsIgnoreCase("validate")) || (name.equalsIgnoreCase("check-character-normalization")) || (name.equalsIgnoreCase("datatype-normalization")))
/*      */       {
/*  226 */         if (state) {
/*  227 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/*  232 */           throw new DOMException((short)9, msg);
/*      */         }
/*  234 */       } else if (name.equalsIgnoreCase("namespace-declarations"))
/*      */       {
/*  237 */         this.features = ((short)(state ? this.features | 0x200 : this.features & 0xFFFFFDFF));
/*      */ 
/*  241 */         this.serializer.fNamespacePrefixes = state;
/*  242 */       } else if ((name.equalsIgnoreCase("element-content-whitespace")) || (name.equalsIgnoreCase("ignore-unknown-character-denormalizations")))
/*      */       {
/*  245 */         if (!state) {
/*  246 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/*  251 */           throw new DOMException((short)9, msg);
/*      */         }
/*      */       } else {
/*  254 */         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
/*      */ 
/*  259 */         throw new DOMException((short)9, msg);
/*      */       }
/*  261 */     } else if (name.equalsIgnoreCase("error-handler")) {
/*  262 */       if ((value == null) || ((value instanceof DOMErrorHandler))) {
/*  263 */         this.fErrorHandler = ((DOMErrorHandler)value);
/*      */       } else {
/*  265 */         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
/*      */ 
/*  270 */         throw new DOMException((short)17, msg);
/*      */       }
/*      */     } else { if ((name.equalsIgnoreCase("resource-resolver")) || (name.equalsIgnoreCase("schema-location")) || (name.equalsIgnoreCase("schema-type")) || ((name.equalsIgnoreCase("normalize-characters")) && (value != null)))
/*      */       {
/*  278 */         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/*  283 */         throw new DOMException((short)9, msg);
/*      */       }
/*  285 */       String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
/*      */ 
/*  290 */       throw new DOMException((short)8, msg);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean canSetParameter(String name, Object state)
/*      */   {
/*  299 */     if (state == null) {
/*  300 */       return true;
/*      */     }
/*      */ 
/*  303 */     if ((state instanceof Boolean)) {
/*  304 */       boolean value = ((Boolean)state).booleanValue();
/*      */ 
/*  306 */       if ((name.equalsIgnoreCase("namespaces")) || (name.equalsIgnoreCase("split-cdata-sections")) || (name.equalsIgnoreCase("discard-default-content")) || (name.equalsIgnoreCase("xml-declaration")) || (name.equalsIgnoreCase("well-formed")) || (name.equalsIgnoreCase("infoset")) || (name.equalsIgnoreCase("entities")) || (name.equalsIgnoreCase("cdata-sections")) || (name.equalsIgnoreCase("comments")) || (name.equalsIgnoreCase("namespace-declarations")) || (name.equalsIgnoreCase("format-pretty-print")))
/*      */       {
/*  318 */         return true;
/*  319 */       }if ((name.equalsIgnoreCase("canonical-form")) || (name.equalsIgnoreCase("validate-if-schema")) || (name.equalsIgnoreCase("validate")) || (name.equalsIgnoreCase("check-character-normalization")) || (name.equalsIgnoreCase("datatype-normalization")))
/*      */       {
/*  326 */         return !value;
/*  327 */       }if ((name.equalsIgnoreCase("element-content-whitespace")) || (name.equalsIgnoreCase("ignore-unknown-character-denormalizations")))
/*      */       {
/*  330 */         return value;
/*      */       }
/*  332 */     } else if (((name.equalsIgnoreCase("error-handler")) && (state == null)) || ((state instanceof DOMErrorHandler)))
/*      */     {
/*  334 */       return true;
/*      */     }
/*      */ 
/*  337 */     return false;
/*      */   }
/*      */ 
/*      */   public DOMStringList getParameterNames()
/*      */   {
/*  350 */     if (this.fRecognizedParameters == null) {
/*  351 */       Vector parameters = new Vector();
/*      */ 
/*  356 */       parameters.add("namespaces");
/*  357 */       parameters.add("split-cdata-sections");
/*  358 */       parameters.add("discard-default-content");
/*  359 */       parameters.add("xml-declaration");
/*  360 */       parameters.add("canonical-form");
/*  361 */       parameters.add("validate-if-schema");
/*  362 */       parameters.add("validate");
/*  363 */       parameters.add("check-character-normalization");
/*  364 */       parameters.add("datatype-normalization");
/*  365 */       parameters.add("format-pretty-print");
/*      */ 
/*  367 */       parameters.add("well-formed");
/*  368 */       parameters.add("infoset");
/*  369 */       parameters.add("namespace-declarations");
/*  370 */       parameters.add("element-content-whitespace");
/*  371 */       parameters.add("entities");
/*  372 */       parameters.add("cdata-sections");
/*  373 */       parameters.add("comments");
/*  374 */       parameters.add("ignore-unknown-character-denormalizations");
/*  375 */       parameters.add("error-handler");
/*      */ 
/*  381 */       this.fRecognizedParameters = new DOMStringListImpl(parameters);
/*      */     }
/*      */ 
/*  385 */     return this.fRecognizedParameters;
/*      */   }
/*      */ 
/*      */   public Object getParameter(String name)
/*      */     throws DOMException
/*      */   {
/*  393 */     if (name.equalsIgnoreCase("normalize-characters"))
/*  394 */       return null;
/*  395 */     if (name.equalsIgnoreCase("comments"))
/*  396 */       return (this.features & 0x20) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  397 */     if (name.equalsIgnoreCase("namespaces"))
/*  398 */       return (this.features & 0x1) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  399 */     if (name.equalsIgnoreCase("xml-declaration"))
/*  400 */       return (this.features & 0x100) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  401 */     if (name.equalsIgnoreCase("cdata-sections"))
/*  402 */       return (this.features & 0x8) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  403 */     if (name.equalsIgnoreCase("entities"))
/*  404 */       return (this.features & 0x4) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  405 */     if (name.equalsIgnoreCase("split-cdata-sections"))
/*  406 */       return (this.features & 0x10) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  407 */     if (name.equalsIgnoreCase("well-formed"))
/*  408 */       return (this.features & 0x2) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  409 */     if (name.equalsIgnoreCase("namespace-declarations"))
/*  410 */       return (this.features & 0x200) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  411 */     if (name.equalsIgnoreCase("format-pretty-print"))
/*  412 */       return (this.features & 0x800) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  413 */     if ((name.equalsIgnoreCase("element-content-whitespace")) || (name.equalsIgnoreCase("ignore-unknown-character-denormalizations")))
/*      */     {
/*  415 */       return Boolean.TRUE;
/*  416 */     }if (name.equalsIgnoreCase("discard-default-content"))
/*  417 */       return (this.features & 0x40) != 0 ? Boolean.TRUE : Boolean.FALSE;
/*  418 */     if (name.equalsIgnoreCase("infoset")) {
/*  419 */       if (((this.features & 0x4) == 0) && ((this.features & 0x8) == 0) && ((this.features & 0x1) != 0) && ((this.features & 0x200) != 0) && ((this.features & 0x2) != 0) && ((this.features & 0x20) != 0))
/*      */       {
/*  425 */         return Boolean.TRUE;
/*      */       }
/*  427 */       return Boolean.FALSE;
/*  428 */     }if ((name.equalsIgnoreCase("canonical-form")) || (name.equalsIgnoreCase("validate-if-schema")) || (name.equalsIgnoreCase("check-character-normalization")) || (name.equalsIgnoreCase("validate")) || (name.equalsIgnoreCase("validate-if-schema")) || (name.equalsIgnoreCase("datatype-normalization")))
/*      */     {
/*  434 */       return Boolean.FALSE;
/*  435 */     }if (name.equalsIgnoreCase("error-handler"))
/*  436 */       return this.fErrorHandler;
/*  437 */     if ((name.equalsIgnoreCase("resource-resolver")) || (name.equalsIgnoreCase("schema-location")) || (name.equalsIgnoreCase("schema-type")))
/*      */     {
/*  441 */       String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
/*      */ 
/*  446 */       throw new DOMException((short)9, msg);
/*      */     }
/*  448 */     String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
/*      */ 
/*  453 */     throw new DOMException((short)8, msg);
/*      */   }
/*      */ 
/*      */   public String writeToString(Node wnode)
/*      */     throws DOMException, LSException
/*      */   {
/*  479 */     Document doc = wnode.getNodeType() == 9 ? (Document)wnode : wnode.getOwnerDocument();
/*  480 */     Method getVersion = null;
/*  481 */     XMLSerializer ser = null;
/*  482 */     String ver = null;
/*      */     try
/*      */     {
/*  485 */       getVersion = doc.getClass().getMethod("getXmlVersion", new Class[0]);
/*  486 */       if (getVersion != null) {
/*  487 */         ver = (String)getVersion.invoke(doc, (Object[])null);
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*  493 */     if ((ver != null) && (ver.equals("1.1"))) {
/*  494 */       if (this.xml11Serializer == null) {
/*  495 */         this.xml11Serializer = new XML11Serializer();
/*  496 */         initSerializer(this.xml11Serializer);
/*      */       }
/*      */ 
/*  499 */       copySettings(this.serializer, this.xml11Serializer);
/*  500 */       ser = this.xml11Serializer;
/*      */     } else {
/*  502 */       ser = this.serializer;
/*      */     }
/*      */ 
/*  505 */     StringWriter destination = new StringWriter();
/*      */     try {
/*  507 */       prepareForSerialization(ser, wnode);
/*  508 */       ser._format.setEncoding("UTF-16");
/*  509 */       ser.setOutputCharStream(destination);
/*  510 */       if (wnode.getNodeType() == 9) {
/*  511 */         ser.serialize((Document)wnode);
/*      */       }
/*  513 */       else if (wnode.getNodeType() == 11) {
/*  514 */         ser.serialize((DocumentFragment)wnode);
/*      */       }
/*  516 */       else if (wnode.getNodeType() == 1) {
/*  517 */         ser.serialize((Element)wnode);
/*      */       }
/*  519 */       else if ((wnode.getNodeType() == 3) || (wnode.getNodeType() == 8) || (wnode.getNodeType() == 5) || (wnode.getNodeType() == 4) || (wnode.getNodeType() == 7))
/*      */       {
/*  524 */         ser.serialize(wnode);
/*      */       }
/*      */       else {
/*  527 */         String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unable-to-serialize-node", null);
/*      */ 
/*  530 */         if (ser.fDOMErrorHandler != null) {
/*  531 */           DOMErrorImpl error = new DOMErrorImpl();
/*  532 */           error.fType = "unable-to-serialize-node";
/*  533 */           error.fMessage = msg;
/*  534 */           error.fSeverity = 3;
/*  535 */           ser.fDOMErrorHandler.handleError(error);
/*      */         }
/*  537 */         throw new LSException((short)82, msg);
/*      */       }
/*      */     }
/*      */     catch (LSException lse) {
/*  541 */       throw lse;
/*      */     } catch (RuntimeException e) {
/*  543 */       if (e == DOMNormalizer.abort)
/*      */       {
/*  545 */         return null;
/*      */       }
/*  547 */       throw ((LSException)new LSException((short)82, e.toString()).initCause(e));
/*      */     }
/*      */     catch (IOException ioe)
/*      */     {
/*  552 */       String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "STRING_TOO_LONG", new Object[] { ioe.getMessage() });
/*      */ 
/*  556 */       throw ((DOMException)new DOMException((short)2, msg).initCause(ioe));
/*      */     }
/*      */ 
/*  559 */     return destination.toString();
/*      */   }
/*      */ 
/*      */   public void setNewLine(String newLine)
/*      */   {
/*  586 */     this.serializer._format.setLineSeparator(newLine);
/*      */   }
/*      */ 
/*      */   public String getNewLine()
/*      */   {
/*  614 */     return this.serializer._format.getLineSeparator();
/*      */   }
/*      */ 
/*      */   public LSSerializerFilter getFilter()
/*      */   {
/*  625 */     return this.serializer.fDOMFilter;
/*      */   }
/*      */ 
/*      */   public void setFilter(LSSerializerFilter filter)
/*      */   {
/*  634 */     this.serializer.fDOMFilter = filter;
/*      */   }
/*      */ 
/*      */   private void initSerializer(XMLSerializer ser)
/*      */   {
/*  639 */     ser.fNSBinder = new NamespaceSupport();
/*  640 */     ser.fLocalNSBinder = new NamespaceSupport();
/*  641 */     ser.fSymbolTable = new SymbolTable();
/*      */   }
/*      */ 
/*      */   private void copySettings(XMLSerializer src, XMLSerializer dest)
/*      */   {
/*  650 */     dest.fDOMErrorHandler = this.fErrorHandler;
/*  651 */     dest._format.setEncoding(src._format.getEncoding());
/*  652 */     dest._format.setLineSeparator(src._format.getLineSeparator());
/*  653 */     dest.fDOMFilter = src.fDOMFilter;
/*      */   }
/*      */ 
/*      */   public boolean write(Node node, LSOutput destination)
/*      */     throws LSException
/*      */   {
/*  688 */     if (node == null) {
/*  689 */       return false;
/*      */     }
/*  691 */     Method getVersion = null;
/*  692 */     XMLSerializer ser = null;
/*  693 */     String ver = null;
/*  694 */     Document fDocument = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument();
/*      */     try
/*      */     {
/*  699 */       getVersion = fDocument.getClass().getMethod("getXmlVersion", new Class[0]);
/*  700 */       if (getVersion != null) {
/*  701 */         ver = (String)getVersion.invoke(fDocument, (Object[])null);
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*      */ 
/*  708 */     if ((ver != null) && (ver.equals("1.1"))) {
/*  709 */       if (this.xml11Serializer == null) {
/*  710 */         this.xml11Serializer = new XML11Serializer();
/*  711 */         initSerializer(this.xml11Serializer);
/*      */       }
/*      */ 
/*  714 */       copySettings(this.serializer, this.xml11Serializer);
/*  715 */       ser = this.xml11Serializer;
/*      */     } else {
/*  717 */       ser = this.serializer;
/*      */     }
/*      */ 
/*  720 */     String encoding = null;
/*  721 */     if ((encoding = destination.getEncoding()) == null) {
/*      */       try {
/*  723 */         Method getEncoding = fDocument.getClass().getMethod("getInputEncoding", new Class[0]);
/*      */ 
/*  725 */         if (getEncoding != null)
/*  726 */           encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/*  731 */       if (encoding == null) {
/*      */         try {
/*  733 */           Method getEncoding = fDocument.getClass().getMethod("getXmlEncoding", new Class[0]);
/*      */ 
/*  735 */           if (getEncoding != null)
/*  736 */             encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */         }
/*  741 */         if (encoding == null)
/*  742 */           encoding = "UTF-8";
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  747 */       prepareForSerialization(ser, node);
/*  748 */       ser._format.setEncoding(encoding);
/*  749 */       OutputStream outputStream = destination.getByteStream();
/*  750 */       Writer writer = destination.getCharacterStream();
/*  751 */       String uri = destination.getSystemId();
/*  752 */       if (writer == null) {
/*  753 */         if (outputStream == null) {
/*  754 */           if (uri == null) {
/*  755 */             String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "no-output-specified", null);
/*      */ 
/*  758 */             if (ser.fDOMErrorHandler != null) {
/*  759 */               DOMErrorImpl error = new DOMErrorImpl();
/*  760 */               error.fType = "no-output-specified";
/*  761 */               error.fMessage = msg;
/*  762 */               error.fSeverity = 3;
/*  763 */               ser.fDOMErrorHandler.handleError(error);
/*      */             }
/*  765 */             throw new LSException((short)82, msg);
/*      */           }
/*      */ 
/*  769 */           String expanded = XMLEntityManager.expandSystemId(uri, null, true);
/*  770 */           URL url = new URL(expanded != null ? expanded : uri);
/*  771 */           OutputStream out = null;
/*  772 */           String protocol = url.getProtocol();
/*  773 */           String host = url.getHost();
/*      */ 
/*  775 */           if ((protocol.equals("file")) && ((host == null) || (host.length() == 0) || (host.equals("localhost"))))
/*      */           {
/*  777 */             out = new FileOutputStream(getPathWithoutEscapes(url.getFile()));
/*      */           }
/*      */           else
/*      */           {
/*  782 */             URLConnection urlCon = url.openConnection();
/*  783 */             urlCon.setDoInput(false);
/*  784 */             urlCon.setDoOutput(true);
/*  785 */             urlCon.setUseCaches(false);
/*  786 */             if ((urlCon instanceof HttpURLConnection))
/*      */             {
/*  789 */               HttpURLConnection httpCon = (HttpURLConnection)urlCon;
/*  790 */               httpCon.setRequestMethod("PUT");
/*      */             }
/*  792 */             out = urlCon.getOutputStream();
/*      */           }
/*  794 */           ser.setOutputByteStream(out);
/*      */         }
/*      */         else
/*      */         {
/*  799 */           ser.setOutputByteStream(outputStream);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  804 */         ser.setOutputCharStream(writer);
/*      */       }
/*      */ 
/*  807 */       if (node.getNodeType() == 9)
/*  808 */         ser.serialize((Document)node);
/*  809 */       else if (node.getNodeType() == 11)
/*  810 */         ser.serialize((DocumentFragment)node);
/*  811 */       else if (node.getNodeType() == 1)
/*  812 */         ser.serialize((Element)node);
/*  813 */       else if ((node.getNodeType() == 3) || (node.getNodeType() == 8) || (node.getNodeType() == 5) || (node.getNodeType() == 4) || (node.getNodeType() == 7))
/*      */       {
/*  818 */         ser.serialize(node);
/*      */       }
/*      */       else
/*  821 */         return false;
/*      */     } catch (UnsupportedEncodingException ue) {
/*  823 */       if (ser.fDOMErrorHandler != null) {
/*  824 */         DOMErrorImpl error = new DOMErrorImpl();
/*  825 */         error.fException = ue;
/*  826 */         error.fType = "unsupported-encoding";
/*  827 */         error.fMessage = ue.getMessage();
/*  828 */         error.fSeverity = 3;
/*  829 */         ser.fDOMErrorHandler.handleError(error);
/*      */       }
/*  831 */       throw new LSException((short)82, DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unsupported-encoding", null));
/*      */     }
/*      */     catch (LSException lse)
/*      */     {
/*  838 */       throw lse;
/*      */     } catch (RuntimeException e) {
/*  840 */       if (e == DOMNormalizer.abort)
/*      */       {
/*  842 */         return false;
/*      */       }
/*  844 */       throw ((LSException)DOMUtil.createLSException((short)82, e).fillInStackTrace());
/*      */     } catch (Exception e) {
/*  846 */       if (ser.fDOMErrorHandler != null) {
/*  847 */         DOMErrorImpl error = new DOMErrorImpl();
/*  848 */         error.fException = e;
/*  849 */         error.fMessage = e.getMessage();
/*  850 */         error.fSeverity = 2;
/*  851 */         ser.fDOMErrorHandler.handleError(error);
/*      */       }
/*      */ 
/*  854 */       e.printStackTrace();
/*  855 */       throw ((LSException)DOMUtil.createLSException((short)82, e).fillInStackTrace());
/*      */     }
/*  857 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean writeToURI(Node node, String URI)
/*      */     throws LSException
/*      */   {
/*  887 */     if (node == null) {
/*  888 */       return false;
/*      */     }
/*      */ 
/*  891 */     Method getXmlVersion = null;
/*  892 */     XMLSerializer ser = null;
/*  893 */     String ver = null;
/*  894 */     String encoding = null;
/*      */ 
/*  896 */     Document fDocument = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument();
/*      */     try
/*      */     {
/*  901 */       getXmlVersion = fDocument.getClass().getMethod("getXmlVersion", new Class[0]);
/*      */ 
/*  903 */       if (getXmlVersion != null) {
/*  904 */         ver = (String)getXmlVersion.invoke(fDocument, (Object[])null);
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*  910 */     if ((ver != null) && (ver.equals("1.1"))) {
/*  911 */       if (this.xml11Serializer == null) {
/*  912 */         this.xml11Serializer = new XML11Serializer();
/*  913 */         initSerializer(this.xml11Serializer);
/*      */       }
/*      */ 
/*  916 */       copySettings(this.serializer, this.xml11Serializer);
/*  917 */       ser = this.xml11Serializer;
/*      */     } else {
/*  919 */       ser = this.serializer;
/*      */     }
/*      */     try
/*      */     {
/*  923 */       Method getEncoding = fDocument.getClass().getMethod("getInputEncoding", new Class[0]);
/*      */ 
/*  925 */       if (getEncoding != null)
/*  926 */         encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*  931 */     if (encoding == null) {
/*      */       try {
/*  933 */         Method getEncoding = fDocument.getClass().getMethod("getXmlEncoding", new Class[0]);
/*      */ 
/*  935 */         if (getEncoding != null)
/*  936 */           encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/*  941 */       if (encoding == null) {
/*  942 */         encoding = "UTF-8";
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  947 */       prepareForSerialization(ser, node);
/*  948 */       ser._format.setEncoding(encoding);
/*      */ 
/*  951 */       String expanded = XMLEntityManager.expandSystemId(URI, null, true);
/*  952 */       URL url = new URL(expanded != null ? expanded : URI);
/*  953 */       OutputStream out = null;
/*  954 */       String protocol = url.getProtocol();
/*  955 */       String host = url.getHost();
/*      */ 
/*  957 */       if ((protocol.equals("file")) && ((host == null) || (host.length() == 0) || (host.equals("localhost"))))
/*      */       {
/*  959 */         out = new FileOutputStream(getPathWithoutEscapes(url.getFile()));
/*      */       }
/*      */       else
/*      */       {
/*  964 */         URLConnection urlCon = url.openConnection();
/*  965 */         urlCon.setDoInput(false);
/*  966 */         urlCon.setDoOutput(true);
/*  967 */         urlCon.setUseCaches(false);
/*  968 */         if ((urlCon instanceof HttpURLConnection))
/*      */         {
/*  971 */           HttpURLConnection httpCon = (HttpURLConnection)urlCon;
/*  972 */           httpCon.setRequestMethod("PUT");
/*      */         }
/*  974 */         out = urlCon.getOutputStream();
/*      */       }
/*  976 */       ser.setOutputByteStream(out);
/*      */ 
/*  978 */       if (node.getNodeType() == 9)
/*  979 */         ser.serialize((Document)node);
/*  980 */       else if (node.getNodeType() == 11)
/*  981 */         ser.serialize((DocumentFragment)node);
/*  982 */       else if (node.getNodeType() == 1)
/*  983 */         ser.serialize((Element)node);
/*  984 */       else if ((node.getNodeType() == 3) || (node.getNodeType() == 8) || (node.getNodeType() == 5) || (node.getNodeType() == 4) || (node.getNodeType() == 7))
/*      */       {
/*  989 */         ser.serialize(node);
/*      */       }
/*      */       else
/*  992 */         return false;
/*      */     }
/*      */     catch (LSException lse) {
/*  995 */       throw lse;
/*      */     } catch (RuntimeException e) {
/*  997 */       if (e == DOMNormalizer.abort)
/*      */       {
/*  999 */         return false;
/*      */       }
/* 1001 */       throw ((LSException)DOMUtil.createLSException((short)82, e).fillInStackTrace());
/*      */     } catch (Exception e) {
/* 1003 */       if (ser.fDOMErrorHandler != null) {
/* 1004 */         DOMErrorImpl error = new DOMErrorImpl();
/* 1005 */         error.fException = e;
/* 1006 */         error.fMessage = e.getMessage();
/* 1007 */         error.fSeverity = 2;
/* 1008 */         ser.fDOMErrorHandler.handleError(error);
/*      */       }
/* 1010 */       throw ((LSException)DOMUtil.createLSException((short)82, e).fillInStackTrace());
/*      */     }
/* 1012 */     return true;
/*      */   }
/*      */ 
/*      */   private void prepareForSerialization(XMLSerializer ser, Node node)
/*      */   {
/* 1021 */     ser.reset();
/* 1022 */     ser.features = this.features;
/* 1023 */     ser.fDOMErrorHandler = this.fErrorHandler;
/* 1024 */     ser.fNamespaces = ((this.features & 0x1) != 0);
/* 1025 */     ser.fNamespacePrefixes = ((this.features & 0x200) != 0);
/* 1026 */     ser._format.setOmitComments((this.features & 0x20) == 0);
/* 1027 */     ser._format.setOmitXMLDeclaration((this.features & 0x100) == 0);
/* 1028 */     ser._format.setIndenting((this.features & 0x800) != 0);
/*      */ 
/* 1030 */     if ((this.features & 0x2) != 0)
/*      */     {
/* 1034 */       Node root = node;
/*      */ 
/* 1036 */       boolean verifyNames = true;
/* 1037 */       Document document = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument();
/*      */       try
/*      */       {
/* 1041 */         Method versionChanged = document.getClass().getMethod("isXMLVersionChanged()", new Class[0]);
/* 1042 */         if (versionChanged != null) {
/* 1043 */           verifyNames = ((Boolean)versionChanged.invoke(document, (Object[])null)).booleanValue();
/*      */         }
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/* 1049 */       if (node.getFirstChild() != null) {
/* 1050 */         while (node != null) {
/* 1051 */           verify(node, verifyNames, false);
/*      */ 
/* 1053 */           Node next = node.getFirstChild();
/*      */ 
/* 1055 */           while (next == null)
/*      */           {
/* 1057 */             next = node.getNextSibling();
/* 1058 */             if (next == null) {
/* 1059 */               node = node.getParentNode();
/* 1060 */               if (root == node) {
/* 1061 */                 next = null;
/* 1062 */                 break;
/*      */               }
/* 1064 */               next = node.getNextSibling();
/*      */             }
/*      */           }
/* 1067 */           node = next;
/*      */         }
/*      */       }
/*      */ 
/* 1071 */       verify(node, verifyNames, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void verify(Node node, boolean verifyNames, boolean xml11Version)
/*      */   {
/* 1079 */     int type = node.getNodeType();
/* 1080 */     this.fLocator.fRelatedNode = node;
/*      */ 
/* 1082 */     switch (type) {
/*      */     case 9:
/* 1084 */       break;
/*      */     case 10:
/* 1087 */       break;
/*      */     case 1:
/* 1090 */       if (verifyNames)
/*      */       {
/*      */         boolean wellformed;
/*      */         boolean wellformed;
/* 1091 */         if ((this.features & 0x1) != 0) {
/* 1092 */           wellformed = CoreDocumentImpl.isValidQName(node.getPrefix(), node.getLocalName(), xml11Version);
/*      */         }
/*      */         else {
/* 1095 */           wellformed = CoreDocumentImpl.isXMLName(node.getNodeName(), xml11Version);
/*      */         }
/* 1097 */         if ((!wellformed) && 
/* 1098 */           (!wellformed) && 
/* 1099 */           (this.fErrorHandler != null)) {
/* 1100 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", node.getNodeName() });
/*      */ 
/* 1104 */           DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)3, "wf-invalid-character-in-node-name");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1112 */       NamedNodeMap attributes = node.hasAttributes() ? node.getAttributes() : null;
/* 1113 */       if (attributes != null)
/* 1114 */         for (int i = 0; i < attributes.getLength(); i++) {
/* 1115 */           Attr attr = (Attr)attributes.item(i);
/* 1116 */           this.fLocator.fRelatedNode = attr;
/* 1117 */           DOMNormalizer.isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributes, attr, attr.getValue(), xml11Version);
/*      */ 
/* 1119 */           if (verifyNames) {
/* 1120 */             boolean wellformed = CoreDocumentImpl.isXMLName(attr.getNodeName(), xml11Version);
/* 1121 */             if (!wellformed) {
/* 1122 */               String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attr", node.getNodeName() });
/*      */ 
/* 1127 */               DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)3, "wf-invalid-character-in-node-name");
/*      */             }
/*      */           }
/*      */         }
/* 1114 */       break;
/*      */     case 8:
/* 1140 */       if ((this.features & 0x20) != 0)
/* 1141 */         DOMNormalizer.isCommentWF(this.fErrorHandler, this.fError, this.fLocator, ((Comment)node).getData(), xml11Version); break;
/*      */     case 5:
/* 1146 */       if ((verifyNames) && ((this.features & 0x4) != 0))
/* 1147 */         CoreDocumentImpl.isXMLName(node.getNodeName(), xml11Version); break;
/*      */     case 4:
/* 1154 */       DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, node.getNodeValue(), xml11Version);
/*      */ 
/* 1156 */       break;
/*      */     case 3:
/* 1159 */       DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, node.getNodeValue(), xml11Version);
/* 1160 */       break;
/*      */     case 7:
/* 1163 */       ProcessingInstruction pinode = (ProcessingInstruction)node;
/* 1164 */       String target = pinode.getTarget();
/* 1165 */       if (verifyNames)
/*      */       {
/*      */         boolean wellformed;
/*      */         boolean wellformed;
/* 1166 */         if (xml11Version)
/* 1167 */           wellformed = XML11Char.isXML11ValidName(target);
/*      */         else {
/* 1169 */           wellformed = XMLChar.isValidName(target);
/*      */         }
/*      */ 
/* 1172 */         if (!wellformed) {
/* 1173 */           String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", node.getNodeName() });
/*      */ 
/* 1178 */           DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)3, "wf-invalid-character-in-node-name");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1187 */       DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, pinode.getData(), xml11Version);
/* 1188 */       break;
/*      */     case 2:
/*      */     case 6:
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getPathWithoutEscapes(String origPath) {
/* 1195 */     if ((origPath != null) && (origPath.length() != 0) && (origPath.indexOf('%') != -1))
/*      */     {
/* 1197 */       StringTokenizer tokenizer = new StringTokenizer(origPath, "%");
/* 1198 */       StringBuffer result = new StringBuffer(origPath.length());
/* 1199 */       int size = tokenizer.countTokens();
/* 1200 */       result.append(tokenizer.nextToken());
/* 1201 */       for (int i = 1; i < size; i++) {
/* 1202 */         String token = tokenizer.nextToken();
/*      */ 
/* 1204 */         result.append((char)Integer.valueOf(token.substring(0, 2), 16).intValue());
/* 1205 */         result.append(token.substring(2));
/*      */       }
/* 1207 */       return result.toString();
/*      */     }
/* 1209 */     return origPath;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl
 * JD-Core Version:    0.6.2
 */