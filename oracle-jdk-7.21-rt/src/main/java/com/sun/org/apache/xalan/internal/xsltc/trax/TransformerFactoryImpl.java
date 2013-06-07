/*      */ package com.sun.org.apache.xalan.internal.xsltc.trax;
/*      */ 
/*      */ import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
/*      */ import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
/*      */ import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.SourceLoader;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
/*      */ import com.sun.org.apache.xml.internal.utils.StopParseException;
/*      */ import com.sun.org.apache.xml.internal.utils.StylesheetPIHandler;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FilenameFilter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Properties;
/*      */ import java.util.Vector;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipFile;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import javax.xml.parsers.SAXParser;
/*      */ import javax.xml.parsers.SAXParserFactory;
/*      */ import javax.xml.transform.ErrorListener;
/*      */ import javax.xml.transform.Source;
/*      */ import javax.xml.transform.Templates;
/*      */ import javax.xml.transform.Transformer;
/*      */ import javax.xml.transform.TransformerConfigurationException;
/*      */ import javax.xml.transform.TransformerException;
/*      */ import javax.xml.transform.TransformerFactory;
/*      */ import javax.xml.transform.URIResolver;
/*      */ import javax.xml.transform.dom.DOMSource;
/*      */ import javax.xml.transform.sax.SAXSource;
/*      */ import javax.xml.transform.sax.SAXTransformerFactory;
/*      */ import javax.xml.transform.sax.TemplatesHandler;
/*      */ import javax.xml.transform.sax.TransformerHandler;
/*      */ import org.w3c.dom.Node;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.XMLFilter;
/*      */ import org.xml.sax.XMLReader;
/*      */ import org.xml.sax.helpers.XMLReaderFactory;
/*      */ 
/*      */ public class TransformerFactoryImpl extends SAXTransformerFactory
/*      */   implements SourceLoader, ErrorListener
/*      */ {
/*      */   public static final String TRANSLET_NAME = "translet-name";
/*      */   public static final String DESTINATION_DIRECTORY = "destination-directory";
/*      */   public static final String PACKAGE_NAME = "package-name";
/*      */   public static final String JAR_NAME = "jar-name";
/*      */   public static final String GENERATE_TRANSLET = "generate-translet";
/*      */   public static final String AUTO_TRANSLET = "auto-translet";
/*      */   public static final String USE_CLASSPATH = "use-classpath";
/*      */   public static final String DEBUG = "debug";
/*      */   public static final String ENABLE_INLINING = "enable-inlining";
/*      */   public static final String INDENT_NUMBER = "indent-number";
/*  108 */   private ErrorListener _errorListener = this;
/*      */ 
/*  113 */   private URIResolver _uriResolver = null;
/*      */   protected static final String DEFAULT_TRANSLET_NAME = "GregorSamsa";
/*  130 */   private String _transletName = "GregorSamsa";
/*      */ 
/*  135 */   private String _destinationDirectory = null;
/*      */ 
/*  140 */   private String _packageName = null;
/*      */ 
/*  145 */   private String _jarFileName = null;
/*      */ 
/*  151 */   private Hashtable _piParams = null;
/*      */ 
/*  171 */   private boolean _debug = false;
/*      */ 
/*  176 */   private boolean _enableInlining = false;
/*      */ 
/*  182 */   private boolean _generateTranslet = false;
/*      */ 
/*  190 */   private boolean _autoTranslet = false;
/*      */ 
/*  196 */   private boolean _useClasspath = false;
/*      */ 
/*  201 */   private int _indentNumber = -1;
/*      */   private Class m_DTMManagerClass;
/*  214 */   private boolean _isNotSecureProcessing = true;
/*      */ 
/*  218 */   private boolean _isSecureMode = false;
/*      */   private boolean _useServicesMechanism;
/*      */ 
/*      */   public TransformerFactoryImpl()
/*      */   {
/*  231 */     this(true);
/*      */   }
/*      */ 
/*      */   public static TransformerFactory newTransformerFactoryNoServiceLoader() {
/*  235 */     return new TransformerFactoryImpl(false);
/*      */   }
/*      */ 
/*      */   private TransformerFactoryImpl(boolean useServicesMechanism) {
/*  239 */     this.m_DTMManagerClass = XSLTCDTMManager.getDTMManagerClass(useServicesMechanism);
/*  240 */     this._useServicesMechanism = useServicesMechanism;
/*  241 */     if (System.getSecurityManager() != null) {
/*  242 */       this._isSecureMode = true;
/*  243 */       this._isNotSecureProcessing = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setErrorListener(ErrorListener listener)
/*      */     throws IllegalArgumentException
/*      */   {
/*  259 */     if (listener == null) {
/*  260 */       ErrorMsg err = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "TransformerFactory");
/*      */ 
/*  262 */       throw new IllegalArgumentException(err.toString());
/*      */     }
/*  264 */     this._errorListener = listener;
/*      */   }
/*      */ 
/*      */   public ErrorListener getErrorListener()
/*      */   {
/*  274 */     return this._errorListener;
/*      */   }
/*      */ 
/*      */   public Object getAttribute(String name)
/*      */     throws IllegalArgumentException
/*      */   {
/*  289 */     if (name.equals("translet-name")) {
/*  290 */       return this._transletName;
/*      */     }
/*  292 */     if (name.equals("generate-translet")) {
/*  293 */       return new Boolean(this._generateTranslet);
/*      */     }
/*  295 */     if (name.equals("auto-translet")) {
/*  296 */       return new Boolean(this._autoTranslet);
/*      */     }
/*  298 */     if (name.equals("enable-inlining")) {
/*  299 */       if (this._enableInlining) {
/*  300 */         return Boolean.TRUE;
/*      */       }
/*  302 */       return Boolean.FALSE;
/*      */     }
/*      */ 
/*  306 */     ErrorMsg err = new ErrorMsg("JAXP_INVALID_ATTR_ERR", name);
/*  307 */     throw new IllegalArgumentException(err.toString());
/*      */   }
/*      */ 
/*      */   public void setAttribute(String name, Object value)
/*      */     throws IllegalArgumentException
/*      */   {
/*  323 */     if ((name.equals("translet-name")) && ((value instanceof String))) {
/*  324 */       this._transletName = ((String)value);
/*  325 */       return;
/*      */     }
/*  327 */     if ((name.equals("destination-directory")) && ((value instanceof String))) {
/*  328 */       this._destinationDirectory = ((String)value);
/*  329 */       return;
/*      */     }
/*  331 */     if ((name.equals("package-name")) && ((value instanceof String))) {
/*  332 */       this._packageName = ((String)value);
/*  333 */       return;
/*      */     }
/*  335 */     if ((name.equals("jar-name")) && ((value instanceof String))) {
/*  336 */       this._jarFileName = ((String)value);
/*  337 */       return;
/*      */     }
/*  339 */     if (name.equals("generate-translet")) {
/*  340 */       if ((value instanceof Boolean)) {
/*  341 */         this._generateTranslet = ((Boolean)value).booleanValue();
/*  342 */         return;
/*      */       }
/*  344 */       if ((value instanceof String)) {
/*  345 */         this._generateTranslet = ((String)value).equalsIgnoreCase("true");
/*      */       }
/*      */ 
/*      */     }
/*  349 */     else if (name.equals("auto-translet")) {
/*  350 */       if ((value instanceof Boolean)) {
/*  351 */         this._autoTranslet = ((Boolean)value).booleanValue();
/*  352 */         return;
/*      */       }
/*  354 */       if ((value instanceof String)) {
/*  355 */         this._autoTranslet = ((String)value).equalsIgnoreCase("true");
/*      */       }
/*      */ 
/*      */     }
/*  359 */     else if (name.equals("use-classpath")) {
/*  360 */       if ((value instanceof Boolean)) {
/*  361 */         this._useClasspath = ((Boolean)value).booleanValue();
/*  362 */         return;
/*      */       }
/*  364 */       if ((value instanceof String)) {
/*  365 */         this._useClasspath = ((String)value).equalsIgnoreCase("true");
/*      */       }
/*      */ 
/*      */     }
/*  369 */     else if (name.equals("debug")) {
/*  370 */       if ((value instanceof Boolean)) {
/*  371 */         this._debug = ((Boolean)value).booleanValue();
/*  372 */         return;
/*      */       }
/*  374 */       if ((value instanceof String)) {
/*  375 */         this._debug = ((String)value).equalsIgnoreCase("true");
/*      */       }
/*      */ 
/*      */     }
/*  379 */     else if (name.equals("enable-inlining")) {
/*  380 */       if ((value instanceof Boolean)) {
/*  381 */         this._enableInlining = ((Boolean)value).booleanValue();
/*  382 */         return;
/*      */       }
/*  384 */       if ((value instanceof String)) {
/*  385 */         this._enableInlining = ((String)value).equalsIgnoreCase("true");
/*      */       }
/*      */ 
/*      */     }
/*  389 */     else if (name.equals("indent-number")) {
/*  390 */       if ((value instanceof String)) {
/*      */         try {
/*  392 */           this._indentNumber = Integer.parseInt((String)value);
/*  393 */           return;
/*      */         }
/*      */         catch (NumberFormatException e)
/*      */         {
/*      */         }
/*      */       }
/*  399 */       else if ((value instanceof Integer)) {
/*  400 */         this._indentNumber = ((Integer)value).intValue();
/*  401 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  406 */     ErrorMsg err = new ErrorMsg("JAXP_INVALID_ATTR_ERR", name);
/*      */ 
/*  408 */     throw new IllegalArgumentException(err.toString());
/*      */   }
/*      */ 
/*      */   public void setFeature(String name, boolean value)
/*      */     throws TransformerConfigurationException
/*      */   {
/*  436 */     if (name == null) {
/*  437 */       ErrorMsg err = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
/*  438 */       throw new NullPointerException(err.toString());
/*      */     }
/*      */ 
/*  441 */     if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
/*  442 */       if ((this._isSecureMode) && (!value)) {
/*  443 */         ErrorMsg err = new ErrorMsg("JAXP_SECUREPROCESSING_FEATURE");
/*  444 */         throw new TransformerConfigurationException(err.toString());
/*      */       }
/*  446 */       this._isNotSecureProcessing = (!value);
/*      */ 
/*  448 */       return;
/*      */     }
/*  450 */     if (name.equals("http://www.oracle.com/feature/use-service-mechanism"))
/*      */     {
/*  452 */       if (!this._isSecureMode)
/*  453 */         this._useServicesMechanism = value;
/*      */     }
/*      */     else
/*      */     {
/*  457 */       ErrorMsg err = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", name);
/*  458 */       throw new TransformerConfigurationException(err.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean getFeature(String name)
/*      */   {
/*  473 */     String[] features = { "http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stax.StAXSource/feature", "http://javax.xml.transform.stax.StAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter", "http://www.oracle.com/feature/use-service-mechanism" };
/*      */ 
/*  488 */     if (name == null) {
/*  489 */       ErrorMsg err = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
/*  490 */       throw new NullPointerException(err.toString());
/*      */     }
/*      */ 
/*  494 */     for (int i = 0; i < features.length; i++) {
/*  495 */       if (name.equals(features[i])) {
/*  496 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  500 */     if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
/*  501 */       return !this._isNotSecureProcessing;
/*      */     }
/*      */ 
/*  505 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean useServicesMechnism()
/*      */   {
/*  511 */     return this._useServicesMechanism;
/*      */   }
/*      */ 
/*      */   public URIResolver getURIResolver()
/*      */   {
/*  523 */     return this._uriResolver;
/*      */   }
/*      */ 
/*      */   public void setURIResolver(URIResolver resolver)
/*      */   {
/*  537 */     this._uriResolver = resolver;
/*      */   }
/*      */ 
/*      */   public Source getAssociatedStylesheet(Source source, String media, String title, String charset)
/*      */     throws TransformerConfigurationException
/*      */   {
/*  560 */     XMLReader reader = null;
/*  561 */     InputSource isource = null;
/*      */ 
/*  567 */     StylesheetPIHandler _stylesheetPIHandler = new StylesheetPIHandler(null, media, title, charset);
/*      */     try
/*      */     {
/*  571 */       if ((source instanceof DOMSource)) {
/*  572 */         DOMSource domsrc = (DOMSource)source;
/*  573 */         String baseId = domsrc.getSystemId();
/*  574 */         Node node = domsrc.getNode();
/*  575 */         DOM2SAX dom2sax = new DOM2SAX(node);
/*      */ 
/*  577 */         _stylesheetPIHandler.setBaseId(baseId);
/*      */ 
/*  579 */         dom2sax.setContentHandler(_stylesheetPIHandler);
/*  580 */         dom2sax.parse();
/*      */       } else {
/*  582 */         isource = SAXSource.sourceToInputSource(source);
/*  583 */         String baseId = isource.getSystemId();
/*      */ 
/*  585 */         SAXParserFactory factory = FactoryImpl.getSAXFactory(this._useServicesMechanism);
/*  586 */         factory.setNamespaceAware(true);
/*      */ 
/*  588 */         if (!this._isNotSecureProcessing)
/*      */           try {
/*  590 */             factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
/*      */           }
/*      */           catch (SAXException e)
/*      */           {
/*      */           }
/*  595 */         SAXParser jaxpParser = factory.newSAXParser();
/*      */ 
/*  597 */         reader = jaxpParser.getXMLReader();
/*  598 */         if (reader == null) {
/*  599 */           reader = XMLReaderFactory.createXMLReader();
/*      */         }
/*      */ 
/*  602 */         _stylesheetPIHandler.setBaseId(baseId);
/*  603 */         reader.setContentHandler(_stylesheetPIHandler);
/*  604 */         reader.parse(isource);
/*      */       }
/*      */ 
/*  608 */       if (this._uriResolver != null) {
/*  609 */         _stylesheetPIHandler.setURIResolver(this._uriResolver);
/*      */       }
/*      */     }
/*      */     catch (StopParseException e)
/*      */     {
/*      */     }
/*      */     catch (ParserConfigurationException e)
/*      */     {
/*  617 */       throw new TransformerConfigurationException("getAssociatedStylesheets failed", e);
/*      */     }
/*      */     catch (SAXException se)
/*      */     {
/*  622 */       throw new TransformerConfigurationException("getAssociatedStylesheets failed", se);
/*      */     }
/*      */     catch (IOException ioe)
/*      */     {
/*  627 */       throw new TransformerConfigurationException("getAssociatedStylesheets failed", ioe);
/*      */     }
/*      */ 
/*  632 */     return _stylesheetPIHandler.getAssociatedStylesheet();
/*      */   }
/*      */ 
/*      */   public Transformer newTransformer()
/*      */     throws TransformerConfigurationException
/*      */   {
/*  646 */     TransformerImpl result = new TransformerImpl(new Properties(), this._indentNumber, this);
/*      */ 
/*  648 */     if (this._uriResolver != null) {
/*  649 */       result.setURIResolver(this._uriResolver);
/*      */     }
/*      */ 
/*  652 */     if (!this._isNotSecureProcessing) {
/*  653 */       result.setSecureProcessing(true);
/*      */     }
/*  655 */     return result;
/*      */   }
/*      */ 
/*      */   public Transformer newTransformer(Source source)
/*      */     throws TransformerConfigurationException
/*      */   {
/*  671 */     Templates templates = newTemplates(source);
/*  672 */     Transformer transformer = templates.newTransformer();
/*  673 */     if (this._uriResolver != null) {
/*  674 */       transformer.setURIResolver(this._uriResolver);
/*      */     }
/*  676 */     return transformer;
/*      */   }
/*      */ 
/*      */   private void passWarningsToListener(Vector messages)
/*      */     throws TransformerException
/*      */   {
/*  685 */     if ((this._errorListener == null) || (messages == null)) {
/*  686 */       return;
/*      */     }
/*      */ 
/*  689 */     int count = messages.size();
/*  690 */     for (int pos = 0; pos < count; pos++) {
/*  691 */       ErrorMsg msg = (ErrorMsg)messages.elementAt(pos);
/*      */ 
/*  693 */       if (msg.isWarningError()) {
/*  694 */         this._errorListener.error(new TransformerConfigurationException(msg.toString()));
/*      */       }
/*      */       else
/*  697 */         this._errorListener.warning(new TransformerConfigurationException(msg.toString()));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void passErrorsToListener(Vector messages)
/*      */   {
/*      */     try
/*      */     {
/*  707 */       if ((this._errorListener == null) || (messages == null)) {
/*  708 */         return;
/*      */       }
/*      */ 
/*  711 */       int count = messages.size();
/*  712 */       for (int pos = 0; pos < count; pos++) {
/*  713 */         String message = messages.elementAt(pos).toString();
/*  714 */         this._errorListener.error(new TransformerException(message));
/*      */       }
/*      */     }
/*      */     catch (TransformerException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public Templates newTemplates(Source source)
/*      */     throws TransformerConfigurationException
/*      */   {
/*  737 */     if (this._useClasspath) {
/*  738 */       String transletName = getTransletBaseName(source);
/*      */ 
/*  740 */       if (this._packageName != null)
/*  741 */         transletName = this._packageName + "." + transletName;
/*      */       try
/*      */       {
/*  744 */         Class clazz = ObjectFactory.findProviderClass(transletName, true);
/*  745 */         resetTransientAttributes();
/*      */ 
/*  747 */         return new TemplatesImpl(new Class[] { clazz }, transletName, null, this._indentNumber, this);
/*      */       }
/*      */       catch (ClassNotFoundException cnfe) {
/*  750 */         ErrorMsg err = new ErrorMsg("CLASS_NOT_FOUND_ERR", transletName);
/*  751 */         throw new TransformerConfigurationException(err.toString());
/*      */       }
/*      */       catch (Exception e) {
/*  754 */         ErrorMsg err = new ErrorMsg(new ErrorMsg("RUNTIME_ERROR_KEY") + e.getMessage());
/*      */ 
/*  757 */         throw new TransformerConfigurationException(err.toString());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  763 */     if (this._autoTranslet) {
/*  764 */       byte[][] bytecodes = (byte[][])null;
/*  765 */       String transletClassName = getTransletBaseName(source);
/*      */ 
/*  767 */       if (this._packageName != null) {
/*  768 */         transletClassName = this._packageName + "." + transletClassName;
/*      */       }
/*  770 */       if (this._jarFileName != null)
/*  771 */         bytecodes = getBytecodesFromJar(source, transletClassName);
/*      */       else {
/*  773 */         bytecodes = getBytecodesFromClasses(source, transletClassName);
/*      */       }
/*  775 */       if (bytecodes != null) {
/*  776 */         if (this._debug) {
/*  777 */           if (this._jarFileName != null) {
/*  778 */             System.err.println(new ErrorMsg("TRANSFORM_WITH_JAR_STR", transletClassName, this._jarFileName));
/*      */           }
/*      */           else {
/*  781 */             System.err.println(new ErrorMsg("TRANSFORM_WITH_TRANSLET_STR", transletClassName));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  787 */         resetTransientAttributes();
/*      */ 
/*  789 */         return new TemplatesImpl(bytecodes, transletClassName, null, this._indentNumber, this);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  794 */     XSLTC xsltc = new XSLTC(this._useServicesMechanism);
/*  795 */     if (this._debug) xsltc.setDebug(true);
/*  796 */     if (this._enableInlining)
/*  797 */       xsltc.setTemplateInlining(true);
/*      */     else {
/*  799 */       xsltc.setTemplateInlining(false);
/*      */     }
/*  801 */     if (!this._isNotSecureProcessing) xsltc.setSecureProcessing(true);
/*  802 */     xsltc.init();
/*      */ 
/*  805 */     if (this._uriResolver != null) {
/*  806 */       xsltc.setSourceLoader(this);
/*      */     }
/*      */ 
/*  811 */     if ((this._piParams != null) && (this._piParams.get(source) != null))
/*      */     {
/*  813 */       PIParamWrapper p = (PIParamWrapper)this._piParams.get(source);
/*      */ 
/*  815 */       if (p != null) {
/*  816 */         xsltc.setPIParameters(p._media, p._title, p._charset);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  821 */     int outputType = 2;
/*  822 */     if ((this._generateTranslet) || (this._autoTranslet))
/*      */     {
/*  824 */       xsltc.setClassName(getTransletBaseName(source));
/*      */ 
/*  826 */       if (this._destinationDirectory != null) {
/*  827 */         xsltc.setDestDirectory(this._destinationDirectory);
/*      */       } else {
/*  829 */         String xslName = getStylesheetFileName(source);
/*  830 */         if (xslName != null) {
/*  831 */           File xslFile = new File(xslName);
/*  832 */           String xslDir = xslFile.getParent();
/*      */ 
/*  834 */           if (xslDir != null) {
/*  835 */             xsltc.setDestDirectory(xslDir);
/*      */           }
/*      */         }
/*      */       }
/*  839 */       if (this._packageName != null) {
/*  840 */         xsltc.setPackageName(this._packageName);
/*      */       }
/*  842 */       if (this._jarFileName != null) {
/*  843 */         xsltc.setJarFileName(this._jarFileName);
/*  844 */         outputType = 5;
/*      */       }
/*      */       else {
/*  847 */         outputType = 4;
/*      */       }
/*      */     }
/*      */ 
/*  851 */     InputSource input = Util.getInputSource(xsltc, source);
/*  852 */     byte[][] bytecodes = xsltc.compile(null, input, outputType);
/*  853 */     String transletName = xsltc.getClassName();
/*      */ 
/*  856 */     if (((this._generateTranslet) || (this._autoTranslet)) && (bytecodes != null) && (this._jarFileName != null)) {
/*      */       try
/*      */       {
/*  859 */         xsltc.outputToJar();
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*      */       }
/*      */     }
/*      */ 
/*  866 */     resetTransientAttributes();
/*      */ 
/*  869 */     if (this._errorListener != this) {
/*      */       try {
/*  871 */         passWarningsToListener(xsltc.getWarnings());
/*      */       }
/*      */       catch (TransformerException e) {
/*  874 */         throw new TransformerConfigurationException(e);
/*      */       }
/*      */     }
/*      */     else {
/*  878 */       xsltc.printWarnings();
/*      */     }
/*      */ 
/*  882 */     if (bytecodes == null)
/*      */     {
/*  884 */       Vector errs = xsltc.getErrors();
/*  885 */       ErrorMsg err = null;
/*  886 */       if (errs != null)
/*  887 */         err = (ErrorMsg)errs.get(errs.size() - 1);
/*      */       else {
/*  889 */         err = new ErrorMsg("JAXP_COMPILE_ERR");
/*      */       }
/*  891 */       TransformerConfigurationException exc = new TransformerConfigurationException(err.toString(), err.getCause());
/*      */ 
/*  894 */       if (this._errorListener != null) {
/*  895 */         passErrorsToListener(xsltc.getErrors());
/*      */         try
/*      */         {
/*  901 */           this._errorListener.fatalError(exc);
/*      */         }
/*      */         catch (TransformerException te) {
/*      */         }
/*      */       }
/*      */       else {
/*  907 */         xsltc.printErrors();
/*      */       }
/*  909 */       throw exc;
/*      */     }
/*      */ 
/*  912 */     return new TemplatesImpl(bytecodes, transletName, xsltc.getOutputProperties(), this._indentNumber, this);
/*      */   }
/*      */ 
/*      */   public TemplatesHandler newTemplatesHandler()
/*      */     throws TransformerConfigurationException
/*      */   {
/*  927 */     TemplatesHandlerImpl handler = new TemplatesHandlerImpl(this._indentNumber, this);
/*      */ 
/*  929 */     if (this._uriResolver != null) {
/*  930 */       handler.setURIResolver(this._uriResolver);
/*      */     }
/*  932 */     return handler;
/*      */   }
/*      */ 
/*      */   public TransformerHandler newTransformerHandler()
/*      */     throws TransformerConfigurationException
/*      */   {
/*  946 */     Transformer transformer = newTransformer();
/*  947 */     if (this._uriResolver != null) {
/*  948 */       transformer.setURIResolver(this._uriResolver);
/*      */     }
/*  950 */     return new TransformerHandlerImpl((TransformerImpl)transformer);
/*      */   }
/*      */ 
/*      */   public TransformerHandler newTransformerHandler(Source src)
/*      */     throws TransformerConfigurationException
/*      */   {
/*  966 */     Transformer transformer = newTransformer(src);
/*  967 */     if (this._uriResolver != null) {
/*  968 */       transformer.setURIResolver(this._uriResolver);
/*      */     }
/*  970 */     return new TransformerHandlerImpl((TransformerImpl)transformer);
/*      */   }
/*      */ 
/*      */   public TransformerHandler newTransformerHandler(Templates templates)
/*      */     throws TransformerConfigurationException
/*      */   {
/*  986 */     Transformer transformer = templates.newTransformer();
/*  987 */     TransformerImpl internal = (TransformerImpl)transformer;
/*  988 */     return new TransformerHandlerImpl(internal);
/*      */   }
/*      */ 
/*      */   public XMLFilter newXMLFilter(Source src)
/*      */     throws TransformerConfigurationException
/*      */   {
/* 1003 */     Templates templates = newTemplates(src);
/* 1004 */     if (templates == null) return null;
/* 1005 */     return newXMLFilter(templates);
/*      */   }
/*      */ 
/*      */   public XMLFilter newXMLFilter(Templates templates)
/*      */     throws TransformerConfigurationException
/*      */   {
/*      */     try
/*      */     {
/* 1021 */       return new TrAXFilter(templates);
/*      */     }
/*      */     catch (TransformerConfigurationException e1) {
/* 1024 */       if (this._errorListener != null) {
/*      */         try {
/* 1026 */           this._errorListener.fatalError(e1);
/* 1027 */           return null;
/*      */         }
/*      */         catch (TransformerException e2) {
/* 1030 */           new TransformerConfigurationException(e2);
/*      */         }
/*      */       }
/* 1033 */       throw e1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void error(TransformerException e)
/*      */     throws TransformerException
/*      */   {
/* 1051 */     Throwable wrapped = e.getException();
/* 1052 */     if (wrapped != null) {
/* 1053 */       System.err.println(new ErrorMsg("ERROR_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage()));
/*      */     }
/*      */     else
/*      */     {
/* 1057 */       System.err.println(new ErrorMsg("ERROR_MSG", e.getMessageAndLocation()));
/*      */     }
/*      */ 
/* 1060 */     throw e;
/*      */   }
/*      */ 
/*      */   public void fatalError(TransformerException e)
/*      */     throws TransformerException
/*      */   {
/* 1079 */     Throwable wrapped = e.getException();
/* 1080 */     if (wrapped != null) {
/* 1081 */       System.err.println(new ErrorMsg("FATAL_ERR_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage()));
/*      */     }
/*      */     else
/*      */     {
/* 1085 */       System.err.println(new ErrorMsg("FATAL_ERR_MSG", e.getMessageAndLocation()));
/*      */     }
/*      */ 
/* 1088 */     throw e;
/*      */   }
/*      */ 
/*      */   public void warning(TransformerException e)
/*      */     throws TransformerException
/*      */   {
/* 1107 */     Throwable wrapped = e.getException();
/* 1108 */     if (wrapped != null) {
/* 1109 */       System.err.println(new ErrorMsg("WARNING_PLUS_WRAPPED_MSG", e.getMessageAndLocation(), wrapped.getMessage()));
/*      */     }
/*      */     else
/*      */     {
/* 1113 */       System.err.println(new ErrorMsg("WARNING_MSG", e.getMessageAndLocation()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public InputSource loadSource(String href, String context, XSLTC xsltc)
/*      */   {
/*      */     try
/*      */     {
/* 1129 */       if (this._uriResolver != null) {
/* 1130 */         Source source = this._uriResolver.resolve(href, context);
/* 1131 */         if (source != null) {
/* 1132 */           return Util.getInputSource(xsltc, source);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (TransformerException e)
/*      */     {
/* 1138 */       ErrorMsg msg = new ErrorMsg("INVALID_URI_ERR", href + "\n" + e.getMessage(), this);
/* 1139 */       xsltc.getParser().reportError(2, msg);
/*      */     }
/*      */ 
/* 1142 */     return null;
/*      */   }
/*      */ 
/*      */   private void resetTransientAttributes()
/*      */   {
/* 1149 */     this._transletName = "GregorSamsa";
/* 1150 */     this._destinationDirectory = null;
/* 1151 */     this._packageName = null;
/* 1152 */     this._jarFileName = null;
/*      */   }
/*      */ 
/*      */   private byte[][] getBytecodesFromClasses(Source source, String fullClassName)
/*      */   {
/* 1165 */     if (fullClassName == null) {
/* 1166 */       return (byte[][])null;
/*      */     }
/* 1168 */     String xslFileName = getStylesheetFileName(source);
/* 1169 */     File xslFile = null;
/* 1170 */     if (xslFileName != null) {
/* 1171 */       xslFile = new File(xslFileName);
/*      */     }
/*      */ 
/* 1175 */     int lastDotIndex = fullClassName.lastIndexOf('.');
/*      */     String transletName;
/*      */     String transletName;
/* 1176 */     if (lastDotIndex > 0)
/* 1177 */       transletName = fullClassName.substring(lastDotIndex + 1);
/*      */     else {
/* 1179 */       transletName = fullClassName;
/*      */     }
/*      */ 
/* 1182 */     String transletPath = fullClassName.replace('.', '/');
/* 1183 */     if (this._destinationDirectory != null) {
/* 1184 */       transletPath = this._destinationDirectory + "/" + transletPath + ".class";
/*      */     }
/* 1187 */     else if ((xslFile != null) && (xslFile.getParent() != null))
/* 1188 */       transletPath = xslFile.getParent() + "/" + transletPath + ".class";
/*      */     else {
/* 1190 */       transletPath = transletPath + ".class";
/*      */     }
/*      */ 
/* 1194 */     File transletFile = new File(transletPath);
/* 1195 */     if (!transletFile.exists()) {
/* 1196 */       return (byte[][])null;
/*      */     }
/*      */ 
/* 1202 */     if ((xslFile != null) && (xslFile.exists())) {
/* 1203 */       long xslTimestamp = xslFile.lastModified();
/* 1204 */       long transletTimestamp = transletFile.lastModified();
/* 1205 */       if (transletTimestamp < xslTimestamp) {
/* 1206 */         return (byte[][])null;
/*      */       }
/*      */     }
/*      */ 
/* 1210 */     Vector bytecodes = new Vector();
/* 1211 */     int fileLength = (int)transletFile.length();
/* 1212 */     if (fileLength > 0) {
/* 1213 */       FileInputStream input = null;
/*      */       try {
/* 1215 */         input = new FileInputStream(transletFile);
/*      */       }
/*      */       catch (FileNotFoundException e) {
/* 1218 */         return (byte[][])null;
/*      */       }
/*      */ 
/* 1221 */       byte[] bytes = new byte[fileLength];
/*      */       try {
/* 1223 */         readFromInputStream(bytes, input, fileLength);
/* 1224 */         input.close();
/*      */       }
/*      */       catch (IOException e) {
/* 1227 */         return (byte[][])null;
/*      */       }
/*      */ 
/* 1230 */       bytecodes.addElement(bytes);
/*      */     }
/*      */     else {
/* 1233 */       return (byte[][])null;
/*      */     }
/*      */ 
/* 1236 */     String transletParentDir = transletFile.getParent();
/* 1237 */     if (transletParentDir == null) {
/* 1238 */       transletParentDir = SecuritySupport.getSystemProperty("user.dir");
/*      */     }
/* 1240 */     File transletParentFile = new File(transletParentDir);
/*      */ 
/* 1243 */     final String transletAuxPrefix = transletName + "$";
/* 1244 */     File[] auxfiles = transletParentFile.listFiles(new FilenameFilter()
/*      */     {
/*      */       public boolean accept(File dir, String name) {
/* 1247 */         return (name.endsWith(".class")) && (name.startsWith(transletAuxPrefix));
/*      */       }
/*      */     });
/* 1252 */     for (int i = 0; i < auxfiles.length; i++)
/*      */     {
/* 1254 */       File auxfile = auxfiles[i];
/* 1255 */       int auxlength = (int)auxfile.length();
/* 1256 */       if (auxlength > 0) {
/* 1257 */         FileInputStream auxinput = null;
/*      */         try {
/* 1259 */           auxinput = new FileInputStream(auxfile);
/*      */         }
/*      */         catch (FileNotFoundException e) {
/* 1262 */           continue;
/*      */         }
/*      */ 
/* 1265 */         byte[] bytes = new byte[auxlength];
/*      */         try
/*      */         {
/* 1268 */           readFromInputStream(bytes, auxinput, auxlength);
/* 1269 */           auxinput.close();
/*      */         }
/*      */         catch (IOException e) {
/* 1272 */           continue;
/*      */         }
/*      */ 
/* 1275 */         bytecodes.addElement(bytes);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1280 */     int count = bytecodes.size();
/* 1281 */     if (count > 0) {
/* 1282 */       byte[][] result = new byte[count][1];
/* 1283 */       for (int i = 0; i < count; i++) {
/* 1284 */         result[i] = ((byte[])(byte[])bytecodes.elementAt(i));
/*      */       }
/*      */ 
/* 1287 */       return result;
/*      */     }
/*      */ 
/* 1290 */     return (byte[][])null;
/*      */   }
/*      */ 
/*      */   private byte[][] getBytecodesFromJar(Source source, String fullClassName)
/*      */   {
/* 1302 */     String xslFileName = getStylesheetFileName(source);
/* 1303 */     File xslFile = null;
/* 1304 */     if (xslFileName != null) {
/* 1305 */       xslFile = new File(xslFileName);
/*      */     }
/*      */ 
/* 1308 */     String jarPath = null;
/* 1309 */     if (this._destinationDirectory != null) {
/* 1310 */       jarPath = this._destinationDirectory + "/" + this._jarFileName;
/*      */     }
/* 1312 */     else if ((xslFile != null) && (xslFile.getParent() != null))
/* 1313 */       jarPath = xslFile.getParent() + "/" + this._jarFileName;
/*      */     else {
/* 1315 */       jarPath = this._jarFileName;
/*      */     }
/*      */ 
/* 1319 */     File file = new File(jarPath);
/* 1320 */     if (!file.exists()) {
/* 1321 */       return (byte[][])null;
/*      */     }
/*      */ 
/* 1325 */     if ((xslFile != null) && (xslFile.exists())) {
/* 1326 */       long xslTimestamp = xslFile.lastModified();
/* 1327 */       long transletTimestamp = file.lastModified();
/* 1328 */       if (transletTimestamp < xslTimestamp) {
/* 1329 */         return (byte[][])null;
/*      */       }
/*      */     }
/*      */ 
/* 1333 */     ZipFile jarFile = null;
/*      */     try {
/* 1335 */       jarFile = new ZipFile(file);
/*      */     }
/*      */     catch (IOException e) {
/* 1338 */       return (byte[][])null;
/*      */     }
/*      */ 
/* 1341 */     String transletPath = fullClassName.replace('.', '/');
/* 1342 */     String transletAuxPrefix = transletPath + "$";
/* 1343 */     String transletFullName = transletPath + ".class";
/*      */ 
/* 1345 */     Vector bytecodes = new Vector();
/*      */ 
/* 1349 */     Enumeration entries = jarFile.entries();
/* 1350 */     while (entries.hasMoreElements())
/*      */     {
/* 1352 */       ZipEntry entry = (ZipEntry)entries.nextElement();
/* 1353 */       String entryName = entry.getName();
/* 1354 */       if ((entry.getSize() > 0L) && ((entryName.equals(transletFullName)) || ((entryName.endsWith(".class")) && (entryName.startsWith(transletAuxPrefix)))))
/*      */       {
/*      */         try
/*      */         {
/* 1360 */           InputStream input = jarFile.getInputStream(entry);
/* 1361 */           int size = (int)entry.getSize();
/* 1362 */           byte[] bytes = new byte[size];
/* 1363 */           readFromInputStream(bytes, input, size);
/* 1364 */           input.close();
/* 1365 */           bytecodes.addElement(bytes);
/*      */         }
/*      */         catch (IOException e) {
/* 1368 */           return (byte[][])null;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1374 */     int count = bytecodes.size();
/* 1375 */     if (count > 0) {
/* 1376 */       byte[][] result = new byte[count][1];
/* 1377 */       for (int i = 0; i < count; i++) {
/* 1378 */         result[i] = ((byte[])(byte[])bytecodes.elementAt(i));
/*      */       }
/*      */ 
/* 1381 */       return result;
/*      */     }
/*      */ 
/* 1384 */     return (byte[][])null;
/*      */   }
/*      */ 
/*      */   private void readFromInputStream(byte[] bytes, InputStream input, int size)
/*      */     throws IOException
/*      */   {
/* 1397 */     int n = 0;
/* 1398 */     int offset = 0;
/* 1399 */     int length = size;
/* 1400 */     while ((length > 0) && ((n = input.read(bytes, offset, length)) > 0)) {
/* 1401 */       offset += n;
/* 1402 */       length -= n;
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getTransletBaseName(Source source)
/*      */   {
/* 1419 */     String transletBaseName = null;
/* 1420 */     if (!this._transletName.equals("GregorSamsa")) {
/* 1421 */       return this._transletName;
/*      */     }
/* 1423 */     String systemId = source.getSystemId();
/* 1424 */     if (systemId != null) {
/* 1425 */       String baseName = Util.baseName(systemId);
/* 1426 */       if (baseName != null) {
/* 1427 */         baseName = Util.noExtName(baseName);
/* 1428 */         transletBaseName = Util.toJavaName(baseName);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1433 */     return transletBaseName != null ? transletBaseName : "GregorSamsa";
/*      */   }
/*      */ 
/*      */   private String getStylesheetFileName(Source source)
/*      */   {
/* 1445 */     String systemId = source.getSystemId();
/* 1446 */     if (systemId != null) {
/* 1447 */       File file = new File(systemId);
/* 1448 */       if (file.exists()) {
/* 1449 */         return systemId;
/*      */       }
/* 1451 */       URL url = null;
/*      */       try {
/* 1453 */         url = new URL(systemId);
/*      */       }
/*      */       catch (MalformedURLException e) {
/* 1456 */         return null;
/*      */       }
/*      */ 
/* 1459 */       if ("file".equals(url.getProtocol())) {
/* 1460 */         return url.getFile();
/*      */       }
/* 1462 */       return null;
/*      */     }
/*      */ 
/* 1466 */     return null;
/*      */   }
/*      */ 
/*      */   protected Class getDTMManagerClass()
/*      */   {
/* 1473 */     return this.m_DTMManagerClass;
/*      */   }
/*      */ 
/*      */   private static class PIParamWrapper
/*      */   {
/*  157 */     public String _media = null;
/*  158 */     public String _title = null;
/*  159 */     public String _charset = null;
/*      */ 
/*      */     public PIParamWrapper(String media, String title, String charset) {
/*  162 */       this._media = media;
/*  163 */       this._title = title;
/*  164 */       this._charset = charset;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl
 * JD-Core Version:    0.6.2
 */