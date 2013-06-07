/*      */ package com.sun.org.apache.xalan.internal.xsltc.trax;
/*      */ 
/*      */ import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.DOM;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.Translet;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.TransletException;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.runtime.MessageHandler;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
/*      */ import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
/*      */ import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
/*      */ import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
/*      */ import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
/*      */ import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Reader;
/*      */ import java.io.Writer;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.net.UnknownServiceException;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Properties;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import javax.xml.stream.XMLEventReader;
/*      */ import javax.xml.stream.XMLEventWriter;
/*      */ import javax.xml.stream.XMLStreamReader;
/*      */ import javax.xml.stream.XMLStreamWriter;
/*      */ import javax.xml.transform.ErrorListener;
/*      */ import javax.xml.transform.Result;
/*      */ import javax.xml.transform.Source;
/*      */ import javax.xml.transform.Transformer;
/*      */ import javax.xml.transform.TransformerException;
/*      */ import javax.xml.transform.URIResolver;
/*      */ import javax.xml.transform.dom.DOMResult;
/*      */ import javax.xml.transform.dom.DOMSource;
/*      */ import javax.xml.transform.sax.SAXResult;
/*      */ import javax.xml.transform.sax.SAXSource;
/*      */ import javax.xml.transform.stax.StAXResult;
/*      */ import javax.xml.transform.stax.StAXSource;
/*      */ import javax.xml.transform.stream.StreamResult;
/*      */ import javax.xml.transform.stream.StreamSource;
/*      */ import org.xml.sax.ContentHandler;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.XMLReader;
/*      */ import org.xml.sax.ext.LexicalHandler;
/*      */ 
/*      */ public final class TransformerImpl extends Transformer
/*      */   implements DOMCache, ErrorListener
/*      */ {
/*      */   private static final String EMPTY_STRING = "";
/*      */   private static final String NO_STRING = "no";
/*      */   private static final String YES_STRING = "yes";
/*      */   private static final String XML_STRING = "xml";
/*      */   private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
/*      */   private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
/*      */   private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
/*  119 */   private AbstractTranslet _translet = null;
/*      */ 
/*  124 */   private String _method = null;
/*      */ 
/*  129 */   private String _encoding = null;
/*      */ 
/*  134 */   private String _sourceSystemId = null;
/*      */ 
/*  139 */   private ErrorListener _errorListener = this;
/*      */ 
/*  144 */   private URIResolver _uriResolver = null;
/*      */   private Properties _properties;
/*      */   private Properties _propertiesClone;
/*  154 */   private TransletOutputHandlerFactory _tohFactory = null;
/*      */ 
/*  159 */   private DOM _dom = null;
/*      */   private int _indentNumber;
/*  170 */   private TransformerFactoryImpl _tfactory = null;
/*      */ 
/*  175 */   private OutputStream _ostream = null;
/*      */ 
/*  181 */   private XSLTCDTMManager _dtmManager = null;
/*      */   private XMLReaderManager _readerManager;
/*  197 */   private boolean _isIdentity = false;
/*      */ 
/*  202 */   private boolean _isSecureProcessing = false;
/*      */   private boolean _useServicesMechanism;
/*  216 */   private Hashtable _parameters = null;
/*      */ 
/*      */   protected TransformerImpl(Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory)
/*      */   {
/*  249 */     this(null, outputProperties, indentNumber, tfactory);
/*  250 */     this._isIdentity = true;
/*      */   }
/*      */ 
/*      */   protected TransformerImpl(Translet translet, Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory)
/*      */   {
/*  257 */     this._translet = ((AbstractTranslet)translet);
/*  258 */     this._properties = createOutputProperties(outputProperties);
/*  259 */     this._propertiesClone = ((Properties)this._properties.clone());
/*  260 */     this._indentNumber = indentNumber;
/*  261 */     this._tfactory = tfactory;
/*  262 */     this._useServicesMechanism = this._tfactory.useServicesMechnism();
/*  263 */     this._readerManager = XMLReaderManager.getInstance(this._useServicesMechanism);
/*      */   }
/*      */ 
/*      */   public boolean isSecureProcessing()
/*      */   {
/*  271 */     return this._isSecureProcessing;
/*      */   }
/*      */ 
/*      */   public void setSecureProcessing(boolean flag)
/*      */   {
/*  278 */     this._isSecureProcessing = flag;
/*      */   }
/*      */ 
/*      */   public boolean useServicesMechnism()
/*      */   {
/*  284 */     return this._useServicesMechanism;
/*      */   }
/*      */ 
/*      */   public void setServicesMechnism(boolean flag)
/*      */   {
/*  291 */     this._useServicesMechanism = flag;
/*      */   }
/*      */ 
/*      */   protected AbstractTranslet getTranslet()
/*      */   {
/*  299 */     return this._translet;
/*      */   }
/*      */ 
/*      */   public boolean isIdentity() {
/*  303 */     return this._isIdentity;
/*      */   }
/*      */ 
/*      */   public void transform(Source source, Result result)
/*      */     throws TransformerException
/*      */   {
/*  316 */     if (!this._isIdentity) {
/*  317 */       if (this._translet == null) {
/*  318 */         ErrorMsg err = new ErrorMsg("JAXP_NO_TRANSLET_ERR");
/*  319 */         throw new TransformerException(err.toString());
/*      */       }
/*      */ 
/*  322 */       transferOutputProperties(this._translet);
/*      */     }
/*      */ 
/*  325 */     SerializationHandler toHandler = getOutputHandler(result);
/*  326 */     if (toHandler == null) {
/*  327 */       ErrorMsg err = new ErrorMsg("JAXP_NO_HANDLER_ERR");
/*  328 */       throw new TransformerException(err.toString());
/*      */     }
/*      */ 
/*  331 */     if ((this._uriResolver != null) && (!this._isIdentity)) {
/*  332 */       this._translet.setDOMCache(this);
/*      */     }
/*      */ 
/*  336 */     if (this._isIdentity) {
/*  337 */       transferOutputProperties(toHandler);
/*      */     }
/*      */ 
/*  340 */     transform(source, toHandler, this._encoding);
/*      */     try {
/*  342 */       if ((result instanceof DOMResult))
/*  343 */         ((DOMResult)result).setNode(this._tohFactory.getNode());
/*  344 */       else if ((result instanceof StAXResult)) {
/*  345 */         if (((StAXResult)result).getXMLEventWriter() != null)
/*      */         {
/*  347 */           this._tohFactory.getXMLEventWriter().flush();
/*      */         }
/*  349 */         else if (((StAXResult)result).getXMLStreamWriter() != null)
/*  350 */           this._tohFactory.getXMLStreamWriter().flush();
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  355 */       System.out.println("Result writing error");
/*      */     }
/*      */   }
/*      */ 
/*      */   public SerializationHandler getOutputHandler(Result result)
/*      */     throws TransformerException
/*      */   {
/*  368 */     this._method = ((String)this._properties.get("method"));
/*      */ 
/*  371 */     this._encoding = this._properties.getProperty("encoding");
/*      */ 
/*  373 */     this._tohFactory = TransletOutputHandlerFactory.newInstance(this._useServicesMechanism);
/*  374 */     this._tohFactory.setEncoding(this._encoding);
/*  375 */     if (this._method != null) {
/*  376 */       this._tohFactory.setOutputMethod(this._method);
/*      */     }
/*      */ 
/*  380 */     if (this._indentNumber >= 0) {
/*  381 */       this._tohFactory.setIndentNumber(this._indentNumber);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  387 */       if ((result instanceof SAXResult)) {
/*  388 */         SAXResult target = (SAXResult)result;
/*  389 */         ContentHandler handler = target.getHandler();
/*      */ 
/*  391 */         this._tohFactory.setHandler(handler);
/*      */ 
/*  398 */         LexicalHandler lexicalHandler = target.getLexicalHandler();
/*      */ 
/*  400 */         if (lexicalHandler != null) {
/*  401 */           this._tohFactory.setLexicalHandler(lexicalHandler);
/*      */         }
/*      */ 
/*  404 */         this._tohFactory.setOutputType(1);
/*  405 */         return this._tohFactory.getSerializationHandler();
/*      */       }
/*  407 */       if ((result instanceof StAXResult)) {
/*  408 */         if (((StAXResult)result).getXMLEventWriter() != null)
/*  409 */           this._tohFactory.setXMLEventWriter(((StAXResult)result).getXMLEventWriter());
/*  410 */         else if (((StAXResult)result).getXMLStreamWriter() != null)
/*  411 */           this._tohFactory.setXMLStreamWriter(((StAXResult)result).getXMLStreamWriter());
/*  412 */         this._tohFactory.setOutputType(3);
/*  413 */         return this._tohFactory.getSerializationHandler();
/*      */       }
/*  415 */       if ((result instanceof DOMResult)) {
/*  416 */         this._tohFactory.setNode(((DOMResult)result).getNode());
/*  417 */         this._tohFactory.setNextSibling(((DOMResult)result).getNextSibling());
/*  418 */         this._tohFactory.setOutputType(2);
/*  419 */         return this._tohFactory.getSerializationHandler();
/*      */       }
/*  421 */       if ((result instanceof StreamResult))
/*      */       {
/*  423 */         StreamResult target = (StreamResult)result;
/*      */ 
/*  429 */         this._tohFactory.setOutputType(0);
/*      */ 
/*  432 */         Writer writer = target.getWriter();
/*  433 */         if (writer != null) {
/*  434 */           this._tohFactory.setWriter(writer);
/*  435 */           return this._tohFactory.getSerializationHandler();
/*      */         }
/*      */ 
/*  439 */         OutputStream ostream = target.getOutputStream();
/*  440 */         if (ostream != null) {
/*  441 */           this._tohFactory.setOutputStream(ostream);
/*  442 */           return this._tohFactory.getSerializationHandler();
/*      */         }
/*      */ 
/*  446 */         String systemId = result.getSystemId();
/*  447 */         if (systemId == null) {
/*  448 */           ErrorMsg err = new ErrorMsg("JAXP_NO_RESULT_ERR");
/*  449 */           throw new TransformerException(err.toString());
/*      */         }
/*      */ 
/*  455 */         URL url = null;
/*  456 */         if (systemId.startsWith("file:"))
/*      */         {
/*      */           try
/*      */           {
/*  461 */             URI uri = new URI(systemId);
/*  462 */             systemId = "file:";
/*      */ 
/*  464 */             String host = uri.getHost();
/*  465 */             String path = uri.getPath();
/*  466 */             if (path == null) {
/*  467 */               path = "";
/*      */             }
/*      */ 
/*  472 */             if (host != null)
/*  473 */               systemId = systemId + "//" + host + path;
/*      */             else {
/*  475 */               systemId = systemId + "//" + path;
/*      */             }
/*      */           }
/*      */           catch (Exception exception)
/*      */           {
/*      */           }
/*      */ 
/*  482 */           url = new URL(systemId);
/*  483 */           this._ostream = new FileOutputStream(url.getFile());
/*  484 */           this._tohFactory.setOutputStream(this._ostream);
/*  485 */           return this._tohFactory.getSerializationHandler();
/*      */         }
/*  487 */         if (systemId.startsWith("http:")) {
/*  488 */           url = new URL(systemId);
/*  489 */           URLConnection connection = url.openConnection();
/*  490 */           this._tohFactory.setOutputStream(this._ostream = connection.getOutputStream());
/*  491 */           return this._tohFactory.getSerializationHandler();
/*      */         }
/*      */ 
/*  495 */         this._tohFactory.setOutputStream(this._ostream = new FileOutputStream(new File(systemId)));
/*      */ 
/*  497 */         return this._tohFactory.getSerializationHandler();
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (UnknownServiceException e)
/*      */     {
/*  503 */       throw new TransformerException(e);
/*      */     }
/*      */     catch (ParserConfigurationException e) {
/*  506 */       throw new TransformerException(e);
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/*  510 */       throw new TransformerException(e);
/*      */     }
/*  512 */     return null;
/*      */   }
/*      */ 
/*      */   protected void setDOM(DOM dom)
/*      */   {
/*  519 */     this._dom = dom;
/*      */   }
/*      */ 
/*      */   private DOM getDOM(Source source)
/*      */     throws TransformerException
/*      */   {
/*      */     try
/*      */     {
/*  527 */       DOM dom = null;
/*      */ 
/*  529 */       if (source != null)
/*      */       {
/*      */         DTMWSFilter wsfilter;
/*      */         DTMWSFilter wsfilter;
/*  531 */         if ((this._translet != null) && ((this._translet instanceof StripFilter)))
/*  532 */           wsfilter = new DOMWSFilter(this._translet);
/*      */         else {
/*  534 */           wsfilter = null;
/*      */         }
/*      */ 
/*  537 */         boolean hasIdCall = this._translet != null ? this._translet.hasIdCall() : false;
/*      */ 
/*  540 */         if (this._dtmManager == null) {
/*  541 */           this._dtmManager = ((XSLTCDTMManager)this._tfactory.getDTMManagerClass().newInstance());
/*      */ 
/*  544 */           this._dtmManager.setServicesMechnism(this._useServicesMechanism);
/*      */         }
/*  546 */         dom = (DOM)this._dtmManager.getDTM(source, false, wsfilter, true, false, false, 0, hasIdCall);
/*      */       }
/*  548 */       else if (this._dom != null) {
/*  549 */         dom = this._dom;
/*  550 */         this._dom = null;
/*      */       } else {
/*  552 */         return null;
/*      */       }
/*      */ 
/*  555 */       if (!this._isIdentity)
/*      */       {
/*  558 */         this._translet.prepassDocument(dom);
/*      */       }
/*      */ 
/*  561 */       return dom;
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  565 */       if (this._errorListener != null) {
/*  566 */         postErrorToListener(e.getMessage());
/*      */       }
/*  568 */       throw new TransformerException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected TransformerFactoryImpl getTransformerFactory()
/*      */   {
/*  577 */     return this._tfactory;
/*      */   }
/*      */ 
/*      */   protected TransletOutputHandlerFactory getTransletOutputHandlerFactory()
/*      */   {
/*  585 */     return this._tohFactory;
/*      */   }
/*      */ 
/*      */   private void transformIdentity(Source source, SerializationHandler handler)
/*      */     throws Exception
/*      */   {
/*  592 */     if (source != null) {
/*  593 */       this._sourceSystemId = source.getSystemId();
/*      */     }
/*      */ 
/*  596 */     if ((source instanceof StreamSource)) {
/*  597 */       StreamSource stream = (StreamSource)source;
/*  598 */       InputStream streamInput = stream.getInputStream();
/*  599 */       Reader streamReader = stream.getReader();
/*  600 */       XMLReader reader = this._readerManager.getXMLReader();
/*      */       try
/*      */       {
/*      */         try
/*      */         {
/*  605 */           reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
/*  606 */           reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
/*      */         }
/*      */         catch (SAXException e) {
/*      */         }
/*  610 */         reader.setContentHandler(handler);
/*      */ 
/*  614 */         if (streamInput != null) {
/*  615 */           InputSource input = new InputSource(streamInput);
/*  616 */           input.setSystemId(this._sourceSystemId);
/*      */         }
/*  618 */         else if (streamReader != null) {
/*  619 */           InputSource input = new InputSource(streamReader);
/*  620 */           input.setSystemId(this._sourceSystemId);
/*      */         }
/*      */         else
/*      */         {
/*      */           InputSource input;
/*  622 */           if (this._sourceSystemId != null) {
/*  623 */             input = new InputSource(this._sourceSystemId);
/*      */           }
/*      */           else {
/*  626 */             ErrorMsg err = new ErrorMsg("JAXP_NO_SOURCE_ERR");
/*  627 */             throw new TransformerException(err.toString());
/*      */           }
/*      */         }
/*      */         InputSource input;
/*  631 */         reader.parse(input);
/*      */       } finally {
/*  633 */         this._readerManager.releaseXMLReader(reader);
/*      */       }
/*  635 */     } else if ((source instanceof SAXSource)) {
/*  636 */       SAXSource sax = (SAXSource)source;
/*  637 */       XMLReader reader = sax.getXMLReader();
/*  638 */       InputSource input = sax.getInputSource();
/*  639 */       boolean userReader = true;
/*      */       try
/*      */       {
/*  643 */         if (reader == null) {
/*  644 */           reader = this._readerManager.getXMLReader();
/*  645 */           userReader = false;
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  650 */           reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
/*  651 */           reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
/*      */         }
/*      */         catch (SAXException e) {
/*      */         }
/*  655 */         reader.setContentHandler(handler);
/*      */ 
/*  658 */         reader.parse(input);
/*      */       } finally {
/*  660 */         if (!userReader)
/*  661 */           this._readerManager.releaseXMLReader(reader);
/*      */       }
/*      */     }
/*  664 */     else if ((source instanceof StAXSource)) {
/*  665 */       StAXSource staxSource = (StAXSource)source;
/*  666 */       StAXEvent2SAX staxevent2sax = null;
/*  667 */       StAXStream2SAX staxStream2SAX = null;
/*  668 */       if (staxSource.getXMLEventReader() != null) {
/*  669 */         XMLEventReader xmlEventReader = staxSource.getXMLEventReader();
/*  670 */         staxevent2sax = new StAXEvent2SAX(xmlEventReader);
/*  671 */         staxevent2sax.setContentHandler(handler);
/*  672 */         staxevent2sax.parse();
/*  673 */         handler.flushPending();
/*  674 */       } else if (staxSource.getXMLStreamReader() != null) {
/*  675 */         XMLStreamReader xmlStreamReader = staxSource.getXMLStreamReader();
/*  676 */         staxStream2SAX = new StAXStream2SAX(xmlStreamReader);
/*  677 */         staxStream2SAX.setContentHandler(handler);
/*  678 */         staxStream2SAX.parse();
/*  679 */         handler.flushPending();
/*      */       }
/*  681 */     } else if ((source instanceof DOMSource)) {
/*  682 */       DOMSource domsrc = (DOMSource)source;
/*  683 */       new DOM2TO(domsrc.getNode(), handler).parse();
/*  684 */     } else if ((source instanceof XSLTCSource)) {
/*  685 */       DOM dom = ((XSLTCSource)source).getDOM(null, this._translet);
/*  686 */       ((SAXImpl)dom).copy(handler);
/*      */     } else {
/*  688 */       ErrorMsg err = new ErrorMsg("JAXP_NO_SOURCE_ERR");
/*  689 */       throw new TransformerException(err.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void transform(Source source, SerializationHandler handler, String encoding)
/*      */     throws TransformerException
/*      */   {
/*      */     try
/*      */     {
/*  708 */       if ((((source instanceof StreamSource)) && (source.getSystemId() == null) && (((StreamSource)source).getInputStream() == null) && (((StreamSource)source).getReader() == null)) || (((source instanceof SAXSource)) && (((SAXSource)source).getInputSource() == null) && (((SAXSource)source).getXMLReader() == null)) || (((source instanceof DOMSource)) && (((DOMSource)source).getNode() == null)))
/*      */       {
/*  716 */         DocumentBuilderFactory builderF = FactoryImpl.getDOMFactory(this._useServicesMechanism);
/*  717 */         DocumentBuilder builder = builderF.newDocumentBuilder();
/*  718 */         String systemID = source.getSystemId();
/*  719 */         source = new DOMSource(builder.newDocument());
/*      */ 
/*  722 */         if (systemID != null) {
/*  723 */           source.setSystemId(systemID);
/*      */         }
/*      */       }
/*  726 */       if (this._isIdentity)
/*  727 */         transformIdentity(source, handler);
/*      */       else
/*  729 */         this._translet.transform(getDOM(source), handler);
/*      */     }
/*      */     catch (TransletException e) {
/*  732 */       if (this._errorListener != null) postErrorToListener(e.getMessage());
/*  733 */       throw new TransformerException(e);
/*      */     } catch (RuntimeException e) {
/*  735 */       if (this._errorListener != null) postErrorToListener(e.getMessage());
/*  736 */       throw new TransformerException(e);
/*      */     } catch (Exception e) {
/*  738 */       if (this._errorListener != null) postErrorToListener(e.getMessage());
/*  739 */       throw new TransformerException(e);
/*      */     } finally {
/*  741 */       this._dtmManager = null;
/*      */     }
/*      */ 
/*  745 */     if (this._ostream != null) {
/*      */       try {
/*  747 */         this._ostream.close();
/*      */       } catch (IOException e) {
/*      */       }
/*  750 */       this._ostream = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public ErrorListener getErrorListener()
/*      */   {
/*  761 */     return this._errorListener;
/*      */   }
/*      */ 
/*      */   public void setErrorListener(ErrorListener listener)
/*      */     throws IllegalArgumentException
/*      */   {
/*  775 */     if (listener == null) {
/*  776 */       ErrorMsg err = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "Transformer");
/*      */ 
/*  778 */       throw new IllegalArgumentException(err.toString());
/*      */     }
/*  780 */     this._errorListener = listener;
/*      */ 
/*  783 */     if (this._translet != null)
/*  784 */       this._translet.setMessageHandler(new MessageHandler(this._errorListener));
/*      */   }
/*      */ 
/*      */   private void postErrorToListener(String message)
/*      */   {
/*      */     try
/*      */     {
/*  792 */       this._errorListener.error(new TransformerException(message));
/*      */     }
/*      */     catch (TransformerException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private void postWarningToListener(String message)
/*      */   {
/*      */     try
/*      */     {
/*  804 */       this._errorListener.warning(new TransformerException(message));
/*      */     }
/*      */     catch (TransformerException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private String makeCDATAString(Hashtable cdata)
/*      */   {
/*  818 */     if (cdata == null) return null;
/*      */ 
/*  820 */     StringBuffer result = new StringBuffer();
/*      */ 
/*  823 */     Enumeration elements = cdata.keys();
/*  824 */     if (elements.hasMoreElements()) {
/*  825 */       result.append((String)elements.nextElement());
/*  826 */       while (elements.hasMoreElements()) {
/*  827 */         String element = (String)elements.nextElement();
/*  828 */         result.append(' ');
/*  829 */         result.append(element);
/*      */       }
/*      */     }
/*      */ 
/*  833 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public Properties getOutputProperties()
/*      */   {
/*  848 */     return (Properties)this._properties.clone();
/*      */   }
/*      */ 
/*      */   public String getOutputProperty(String name)
/*      */     throws IllegalArgumentException
/*      */   {
/*  863 */     if (!validOutputProperty(name)) {
/*  864 */       ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", name);
/*  865 */       throw new IllegalArgumentException(err.toString());
/*      */     }
/*  867 */     return this._properties.getProperty(name);
/*      */   }
/*      */ 
/*      */   public void setOutputProperties(Properties properties)
/*      */     throws IllegalArgumentException
/*      */   {
/*  882 */     if (properties != null) {
/*  883 */       Enumeration names = properties.propertyNames();
/*      */ 
/*  885 */       while (names.hasMoreElements()) {
/*  886 */         String name = (String)names.nextElement();
/*      */ 
/*  889 */         if (!isDefaultProperty(name, properties))
/*      */         {
/*  891 */           if (validOutputProperty(name)) {
/*  892 */             this._properties.setProperty(name, properties.getProperty(name));
/*      */           }
/*      */           else {
/*  895 */             ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", name);
/*  896 */             throw new IllegalArgumentException(err.toString());
/*      */           }
/*      */         }
/*      */       }
/*      */     } else {
/*  901 */       this._properties = this._propertiesClone;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setOutputProperty(String name, String value)
/*      */     throws IllegalArgumentException
/*      */   {
/*  918 */     if (!validOutputProperty(name)) {
/*  919 */       ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", name);
/*  920 */       throw new IllegalArgumentException(err.toString());
/*      */     }
/*  922 */     this._properties.setProperty(name, value);
/*      */   }
/*      */ 
/*      */   private void transferOutputProperties(AbstractTranslet translet)
/*      */   {
/*  932 */     if (this._properties == null) return;
/*      */ 
/*  935 */     Enumeration names = this._properties.propertyNames();
/*  936 */     while (names.hasMoreElements())
/*      */     {
/*  938 */       String name = (String)names.nextElement();
/*  939 */       String value = (String)this._properties.get(name);
/*      */ 
/*  942 */       if (value != null)
/*      */       {
/*  945 */         if (name.equals("encoding")) {
/*  946 */           translet._encoding = value;
/*      */         }
/*  948 */         else if (name.equals("method")) {
/*  949 */           translet._method = value;
/*      */         }
/*  951 */         else if (name.equals("doctype-public")) {
/*  952 */           translet._doctypePublic = value;
/*      */         }
/*  954 */         else if (name.equals("doctype-system")) {
/*  955 */           translet._doctypeSystem = value;
/*      */         }
/*  957 */         else if (name.equals("media-type")) {
/*  958 */           translet._mediaType = value;
/*      */         }
/*  960 */         else if (name.equals("standalone")) {
/*  961 */           translet._standalone = value;
/*      */         }
/*  963 */         else if (name.equals("version")) {
/*  964 */           translet._version = value;
/*      */         }
/*  966 */         else if (name.equals("omit-xml-declaration")) {
/*  967 */           translet._omitHeader = ((value != null) && (value.toLowerCase().equals("yes")));
/*      */         }
/*  970 */         else if (name.equals("indent")) {
/*  971 */           translet._indent = ((value != null) && (value.toLowerCase().equals("yes")));
/*      */         }
/*  974 */         else if (name.equals("{http://xml.apache.org/xslt}indent-amount")) {
/*  975 */           if (value != null) {
/*  976 */             translet._indentamount = Integer.parseInt(value);
/*      */           }
/*      */         }
/*  979 */         else if (name.equals("{http://xml.apache.org/xalan}indent-amount")) {
/*  980 */           if (value != null) {
/*  981 */             translet._indentamount = Integer.parseInt(value);
/*      */           }
/*      */         }
/*  984 */         else if (name.equals("cdata-section-elements")) {
/*  985 */           if (value != null) {
/*  986 */             translet._cdata = null;
/*  987 */             StringTokenizer e = new StringTokenizer(value);
/*  988 */             while (e.hasMoreTokens()) {
/*  989 */               translet.addCdataElement(e.nextToken());
/*      */             }
/*      */           }
/*      */         }
/*  993 */         else if ((name.equals("http://www.oracle.com/xml/is-standalone")) && 
/*  994 */           (value != null) && (value.equals("yes")))
/*  995 */           translet._isStandalone = true;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void transferOutputProperties(SerializationHandler handler)
/*      */   {
/* 1008 */     if (this._properties == null) return;
/*      */ 
/* 1010 */     String doctypePublic = null;
/* 1011 */     String doctypeSystem = null;
/*      */ 
/* 1014 */     Enumeration names = this._properties.propertyNames();
/* 1015 */     while (names.hasMoreElements())
/*      */     {
/* 1017 */       String name = (String)names.nextElement();
/* 1018 */       String value = (String)this._properties.get(name);
/*      */ 
/* 1021 */       if (value != null)
/*      */       {
/* 1024 */         if (name.equals("doctype-public")) {
/* 1025 */           doctypePublic = value;
/*      */         }
/* 1027 */         else if (name.equals("doctype-system")) {
/* 1028 */           doctypeSystem = value;
/*      */         }
/* 1030 */         else if (name.equals("media-type")) {
/* 1031 */           handler.setMediaType(value);
/*      */         }
/* 1033 */         else if (name.equals("standalone")) {
/* 1034 */           handler.setStandalone(value);
/*      */         }
/* 1036 */         else if (name.equals("version")) {
/* 1037 */           handler.setVersion(value);
/*      */         }
/* 1039 */         else if (name.equals("omit-xml-declaration")) {
/* 1040 */           handler.setOmitXMLDeclaration((value != null) && (value.toLowerCase().equals("yes")));
/*      */         }
/* 1043 */         else if (name.equals("indent")) {
/* 1044 */           handler.setIndent((value != null) && (value.toLowerCase().equals("yes")));
/*      */         }
/* 1047 */         else if (name.equals("{http://xml.apache.org/xslt}indent-amount")) {
/* 1048 */           if (value != null) {
/* 1049 */             handler.setIndentAmount(Integer.parseInt(value));
/*      */           }
/*      */         }
/* 1052 */         else if (name.equals("{http://xml.apache.org/xalan}indent-amount")) {
/* 1053 */           if (value != null) {
/* 1054 */             handler.setIndentAmount(Integer.parseInt(value));
/*      */           }
/*      */         }
/* 1057 */         else if (name.equals("http://www.oracle.com/xml/is-standalone")) {
/* 1058 */           if ((value != null) && (value.equals("yes"))) {
/* 1059 */             handler.setIsStandalone(true);
/*      */           }
/*      */         }
/* 1062 */         else if ((name.equals("cdata-section-elements")) && 
/* 1063 */           (value != null)) {
/* 1064 */           StringTokenizer e = new StringTokenizer(value);
/* 1065 */           Vector uriAndLocalNames = null;
/* 1066 */           while (e.hasMoreTokens()) {
/* 1067 */             String token = e.nextToken();
/*      */ 
/* 1071 */             int lastcolon = token.lastIndexOf(':');
/*      */             String localName;
/*      */             String uri;
/*      */             String localName;
/* 1074 */             if (lastcolon > 0) {
/* 1075 */               String uri = token.substring(0, lastcolon);
/* 1076 */               localName = token.substring(lastcolon + 1);
/*      */             }
/*      */             else
/*      */             {
/* 1080 */               uri = null;
/* 1081 */               localName = token;
/*      */             }
/*      */ 
/* 1084 */             if (uriAndLocalNames == null) {
/* 1085 */               uriAndLocalNames = new Vector();
/*      */             }
/*      */ 
/* 1088 */             uriAndLocalNames.addElement(uri);
/* 1089 */             uriAndLocalNames.addElement(localName);
/*      */           }
/* 1091 */           handler.setCdataSectionElements(uriAndLocalNames);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1097 */     if ((doctypePublic != null) || (doctypeSystem != null))
/* 1098 */       handler.setDoctype(doctypeSystem, doctypePublic);
/*      */   }
/*      */ 
/*      */   private Properties createOutputProperties(Properties outputProperties)
/*      */   {
/* 1109 */     Properties defaults = new Properties();
/* 1110 */     setDefaults(defaults, "xml");
/*      */ 
/* 1113 */     Properties base = new Properties(defaults);
/* 1114 */     if (outputProperties != null) {
/* 1115 */       Enumeration names = outputProperties.propertyNames();
/* 1116 */       while (names.hasMoreElements()) {
/* 1117 */         String name = (String)names.nextElement();
/* 1118 */         base.setProperty(name, outputProperties.getProperty(name));
/*      */       }
/*      */     }
/*      */     else {
/* 1122 */       base.setProperty("encoding", this._translet._encoding);
/* 1123 */       if (this._translet._method != null) {
/* 1124 */         base.setProperty("method", this._translet._method);
/*      */       }
/*      */     }
/*      */ 
/* 1128 */     String method = base.getProperty("method");
/* 1129 */     if (method != null) {
/* 1130 */       if (method.equals("html")) {
/* 1131 */         setDefaults(defaults, "html");
/*      */       }
/* 1133 */       else if (method.equals("text")) {
/* 1134 */         setDefaults(defaults, "text");
/*      */       }
/*      */     }
/*      */ 
/* 1138 */     return base;
/*      */   }
/*      */ 
/*      */   private void setDefaults(Properties props, String method)
/*      */   {
/* 1149 */     Properties method_props = OutputPropertiesFactory.getDefaultMethodProperties(method);
/*      */ 
/* 1152 */     Enumeration names = method_props.propertyNames();
/* 1153 */     while (names.hasMoreElements())
/*      */     {
/* 1155 */       String name = (String)names.nextElement();
/* 1156 */       props.setProperty(name, method_props.getProperty(name));
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean validOutputProperty(String name)
/*      */   {
/* 1165 */     return (name.equals("encoding")) || (name.equals("method")) || (name.equals("indent")) || (name.equals("doctype-public")) || (name.equals("doctype-system")) || (name.equals("cdata-section-elements")) || (name.equals("media-type")) || (name.equals("omit-xml-declaration")) || (name.equals("standalone")) || (name.equals("version")) || (name.equals("http://www.oracle.com/xml/is-standalone")) || (name.charAt(0) == '{');
/*      */   }
/*      */ 
/*      */   private boolean isDefaultProperty(String name, Properties properties)
/*      */   {
/* 1183 */     return properties.get(name) == null;
/*      */   }
/*      */ 
/*      */   public void setParameter(String name, Object value)
/*      */   {
/* 1197 */     if (value == null) {
/* 1198 */       ErrorMsg err = new ErrorMsg("JAXP_INVALID_SET_PARAM_VALUE", name);
/* 1199 */       throw new IllegalArgumentException(err.toString());
/*      */     }
/*      */ 
/* 1202 */     if (this._isIdentity) {
/* 1203 */       if (this._parameters == null) {
/* 1204 */         this._parameters = new Hashtable();
/*      */       }
/* 1206 */       this._parameters.put(name, value);
/*      */     }
/*      */     else {
/* 1209 */       this._translet.addParameter(name, value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */   {
/* 1219 */     if ((this._isIdentity) && (this._parameters != null)) {
/* 1220 */       this._parameters.clear();
/*      */     }
/*      */     else
/* 1223 */       this._translet.clearParameters();
/*      */   }
/*      */ 
/*      */   public final Object getParameter(String name)
/*      */   {
/* 1236 */     if (this._isIdentity) {
/* 1237 */       return this._parameters != null ? this._parameters.get(name) : null;
/*      */     }
/*      */ 
/* 1240 */     return this._translet.getParameter(name);
/*      */   }
/*      */ 
/*      */   public URIResolver getURIResolver()
/*      */   {
/* 1251 */     return this._uriResolver;
/*      */   }
/*      */ 
/*      */   public void setURIResolver(URIResolver resolver)
/*      */   {
/* 1261 */     this._uriResolver = resolver;
/*      */   }
/*      */ 
/*      */   public DOM retrieveDocument(String baseURI, String href, Translet translet)
/*      */   {
/*      */     try
/*      */     {
/* 1281 */       if (href.length() == 0) {
/* 1282 */         href = baseURI;
/*      */       }
/*      */ 
/* 1293 */       Source resolvedSource = this._uriResolver.resolve(href, baseURI);
/* 1294 */       if (resolvedSource == null) {
/* 1295 */         StreamSource streamSource = new StreamSource(SystemIDResolver.getAbsoluteURI(href, baseURI));
/*      */ 
/* 1297 */         return getDOM(streamSource);
/*      */       }
/*      */ 
/* 1300 */       return getDOM(resolvedSource);
/*      */     }
/*      */     catch (TransformerException e) {
/* 1303 */       if (this._errorListener != null)
/* 1304 */         postErrorToListener("File not found: " + e.getMessage()); 
/*      */     }
/* 1305 */     return null;
/*      */   }
/*      */ 
/*      */   public void error(TransformerException e)
/*      */     throws TransformerException
/*      */   {
/* 1323 */     Throwable wrapped = e.getException();
/* 1324 */     if (wrapped != null) {
/* 1325 */       System.err.println(new ErrorMsg("ERROR_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage()));
/*      */     }
/*      */     else
/*      */     {
/* 1329 */       System.err.println(new ErrorMsg("ERROR_MSG", e.getMessageAndLocation()));
/*      */     }
/*      */ 
/* 1332 */     throw e;
/*      */   }
/*      */ 
/*      */   public void fatalError(TransformerException e)
/*      */     throws TransformerException
/*      */   {
/* 1351 */     Throwable wrapped = e.getException();
/* 1352 */     if (wrapped != null) {
/* 1353 */       System.err.println(new ErrorMsg("FATAL_ERR_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage()));
/*      */     }
/*      */     else
/*      */     {
/* 1357 */       System.err.println(new ErrorMsg("FATAL_ERR_MSG", e.getMessageAndLocation()));
/*      */     }
/*      */ 
/* 1360 */     throw e;
/*      */   }
/*      */ 
/*      */   public void warning(TransformerException e)
/*      */     throws TransformerException
/*      */   {
/* 1379 */     Throwable wrapped = e.getException();
/* 1380 */     if (wrapped != null) {
/* 1381 */       System.err.println(new ErrorMsg("WARNING_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage()));
/*      */     }
/*      */     else
/*      */     {
/* 1385 */       System.err.println(new ErrorMsg("WARNING_MSG", e.getMessageAndLocation()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/* 1398 */     this._method = null;
/* 1399 */     this._encoding = null;
/* 1400 */     this._sourceSystemId = null;
/* 1401 */     this._errorListener = this;
/* 1402 */     this._uriResolver = null;
/* 1403 */     this._dom = null;
/* 1404 */     this._parameters = null;
/* 1405 */     this._indentNumber = 0;
/* 1406 */     setOutputProperties(null);
/* 1407 */     this._tohFactory = null;
/* 1408 */     this._ostream = null;
/*      */   }
/*      */ 
/*      */   static class MessageHandler extends MessageHandler
/*      */   {
/*      */     private ErrorListener _errorListener;
/*      */ 
/*      */     public MessageHandler(ErrorListener errorListener)
/*      */     {
/*  228 */       this._errorListener = errorListener;
/*      */     }
/*      */ 
/*      */     public void displayMessage(String msg) {
/*  232 */       if (this._errorListener == null)
/*  233 */         System.err.println(msg);
/*      */       else
/*      */         try
/*      */         {
/*  237 */           this._errorListener.warning(new TransformerException(msg));
/*      */         }
/*      */         catch (TransformerException e)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.trax.TransformerImpl
 * JD-Core Version:    0.6.2
 */