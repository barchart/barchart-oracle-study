/*      */ package com.sun.org.apache.xalan.internal.xsltc.compiler;
/*      */ 
/*      */ import com.sun.java_cup.internal.runtime.Symbol;
/*      */ import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
/*      */ import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
/*      */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.StringReader;
/*      */ import java.util.Dictionary;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Properties;
/*      */ import java.util.Stack;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import javax.xml.parsers.ParserConfigurationException;
/*      */ import javax.xml.parsers.SAXParser;
/*      */ import javax.xml.parsers.SAXParserFactory;
/*      */ import org.xml.sax.Attributes;
/*      */ import org.xml.sax.ContentHandler;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.Locator;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.SAXParseException;
/*      */ import org.xml.sax.XMLReader;
/*      */ import org.xml.sax.helpers.AttributesImpl;
/*      */ 
/*      */ public class Parser
/*      */   implements Constants, ContentHandler
/*      */ {
/*      */   private static final String XSL = "xsl";
/*      */   private static final String TRANSLET = "translet";
/*   70 */   private Locator _locator = null;
/*      */   private XSLTC _xsltc;
/*      */   private XPathParser _xpathParser;
/*      */   private Vector _errors;
/*      */   private Vector _warnings;
/*      */   private Hashtable _instructionClasses;
/*      */   private Hashtable _instructionAttrs;
/*      */   private Hashtable _qNames;
/*      */   private Hashtable _namespaces;
/*      */   private QName _useAttributeSets;
/*      */   private QName _excludeResultPrefixes;
/*      */   private QName _extensionElementPrefixes;
/*      */   private Hashtable _variableScope;
/*      */   private Stylesheet _currentStylesheet;
/*      */   private SymbolTable _symbolTable;
/*      */   private Output _output;
/*      */   private Template _template;
/*      */   private boolean _rootNamespaceDef;
/*      */   private SyntaxTreeNode _root;
/*      */   private String _target;
/*      */   private int _currentImportPrecedence;
/*   98 */   private boolean _useServicesMechanism = true;
/*      */ 
/*  498 */   private String _PImedia = null;
/*  499 */   private String _PItitle = null;
/*  500 */   private String _PIcharset = null;
/*      */ 
/*  902 */   private int _templateIndex = 0;
/*      */ 
/*  920 */   private boolean versionIsOne = true;
/*      */ 
/* 1207 */   private Stack _parentStack = null;
/* 1208 */   private Hashtable _prefixMapping = null;
/*      */ 
/*      */   public Parser(XSLTC xsltc, boolean useServicesMechanism)
/*      */   {
/*  101 */     this._xsltc = xsltc;
/*  102 */     this._useServicesMechanism = useServicesMechanism;
/*      */   }
/*      */ 
/*      */   public void init() {
/*  106 */     this._qNames = new Hashtable(512);
/*  107 */     this._namespaces = new Hashtable();
/*  108 */     this._instructionClasses = new Hashtable();
/*  109 */     this._instructionAttrs = new Hashtable();
/*  110 */     this._variableScope = new Hashtable();
/*  111 */     this._template = null;
/*  112 */     this._errors = new Vector();
/*  113 */     this._warnings = new Vector();
/*  114 */     this._symbolTable = new SymbolTable();
/*  115 */     this._xpathParser = new XPathParser(this);
/*  116 */     this._currentStylesheet = null;
/*  117 */     this._output = null;
/*  118 */     this._root = null;
/*  119 */     this._rootNamespaceDef = false;
/*  120 */     this._currentImportPrecedence = 1;
/*      */ 
/*  122 */     initStdClasses();
/*  123 */     initInstructionAttrs();
/*  124 */     initExtClasses();
/*  125 */     initSymbolTable();
/*      */ 
/*  127 */     this._useAttributeSets = getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "use-attribute-sets");
/*      */ 
/*  129 */     this._excludeResultPrefixes = getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "exclude-result-prefixes");
/*      */ 
/*  131 */     this._extensionElementPrefixes = getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "extension-element-prefixes");
/*      */   }
/*      */ 
/*      */   public void setOutput(Output output)
/*      */   {
/*  136 */     if (this._output != null) {
/*  137 */       if (this._output.getImportPrecedence() <= output.getImportPrecedence()) {
/*  138 */         String cdata = this._output.getCdata();
/*  139 */         output.mergeOutput(this._output);
/*  140 */         this._output.disable();
/*  141 */         this._output = output;
/*      */       }
/*      */       else {
/*  144 */         output.disable();
/*      */       }
/*      */     }
/*      */     else
/*  148 */       this._output = output;
/*      */   }
/*      */ 
/*      */   public Output getOutput()
/*      */   {
/*  153 */     return this._output;
/*      */   }
/*      */ 
/*      */   public Properties getOutputProperties() {
/*  157 */     return getTopLevelStylesheet().getOutputProperties();
/*      */   }
/*      */ 
/*      */   public void addVariable(Variable var) {
/*  161 */     addVariableOrParam(var);
/*      */   }
/*      */ 
/*      */   public void addParameter(Param param) {
/*  165 */     addVariableOrParam(param);
/*      */   }
/*      */ 
/*      */   private void addVariableOrParam(VariableBase var) {
/*  169 */     Object existing = this._variableScope.get(var.getName());
/*  170 */     if (existing != null) {
/*  171 */       if ((existing instanceof Stack)) {
/*  172 */         Stack stack = (Stack)existing;
/*  173 */         stack.push(var);
/*      */       }
/*  175 */       else if ((existing instanceof VariableBase)) {
/*  176 */         Stack stack = new Stack();
/*  177 */         stack.push(existing);
/*  178 */         stack.push(var);
/*  179 */         this._variableScope.put(var.getName(), stack);
/*      */       }
/*      */     }
/*      */     else
/*  183 */       this._variableScope.put(var.getName(), var);
/*      */   }
/*      */ 
/*      */   public void removeVariable(QName name)
/*      */   {
/*  188 */     Object existing = this._variableScope.get(name);
/*  189 */     if ((existing instanceof Stack)) {
/*  190 */       Stack stack = (Stack)existing;
/*  191 */       if (!stack.isEmpty()) stack.pop();
/*  192 */       if (!stack.isEmpty()) return;
/*      */     }
/*  194 */     this._variableScope.remove(name);
/*      */   }
/*      */ 
/*      */   public VariableBase lookupVariable(QName name) {
/*  198 */     Object existing = this._variableScope.get(name);
/*  199 */     if ((existing instanceof VariableBase)) {
/*  200 */       return (VariableBase)existing;
/*      */     }
/*  202 */     if ((existing instanceof Stack)) {
/*  203 */       Stack stack = (Stack)existing;
/*  204 */       return (VariableBase)stack.peek();
/*      */     }
/*  206 */     return null;
/*      */   }
/*      */ 
/*      */   public void setXSLTC(XSLTC xsltc) {
/*  210 */     this._xsltc = xsltc;
/*      */   }
/*      */ 
/*      */   public XSLTC getXSLTC() {
/*  214 */     return this._xsltc;
/*      */   }
/*      */ 
/*      */   public int getCurrentImportPrecedence() {
/*  218 */     return this._currentImportPrecedence;
/*      */   }
/*      */ 
/*      */   public int getNextImportPrecedence() {
/*  222 */     return ++this._currentImportPrecedence;
/*      */   }
/*      */ 
/*      */   public void setCurrentStylesheet(Stylesheet stylesheet) {
/*  226 */     this._currentStylesheet = stylesheet;
/*      */   }
/*      */ 
/*      */   public Stylesheet getCurrentStylesheet() {
/*  230 */     return this._currentStylesheet;
/*      */   }
/*      */ 
/*      */   public Stylesheet getTopLevelStylesheet() {
/*  234 */     return this._xsltc.getStylesheet();
/*      */   }
/*      */ 
/*      */   public QName getQNameSafe(String stringRep)
/*      */   {
/*  239 */     int colon = stringRep.lastIndexOf(':');
/*  240 */     if (colon != -1) {
/*  241 */       String prefix = stringRep.substring(0, colon);
/*  242 */       String localname = stringRep.substring(colon + 1);
/*  243 */       String namespace = null;
/*      */ 
/*  246 */       if (!prefix.equals("xmlns")) {
/*  247 */         namespace = this._symbolTable.lookupNamespace(prefix);
/*  248 */         if (namespace == null) namespace = "";
/*      */       }
/*  250 */       return getQName(namespace, prefix, localname);
/*      */     }
/*      */ 
/*  253 */     String uri = stringRep.equals("xmlns") ? null : this._symbolTable.lookupNamespace("");
/*      */ 
/*  255 */     return getQName(uri, null, stringRep);
/*      */   }
/*      */ 
/*      */   public QName getQName(String stringRep)
/*      */   {
/*  260 */     return getQName(stringRep, true, false);
/*      */   }
/*      */ 
/*      */   public QName getQNameIgnoreDefaultNs(String stringRep) {
/*  264 */     return getQName(stringRep, true, true);
/*      */   }
/*      */ 
/*      */   public QName getQName(String stringRep, boolean reportError) {
/*  268 */     return getQName(stringRep, reportError, false);
/*      */   }
/*      */ 
/*      */   private QName getQName(String stringRep, boolean reportError, boolean ignoreDefaultNs)
/*      */   {
/*  275 */     int colon = stringRep.lastIndexOf(':');
/*  276 */     if (colon != -1) {
/*  277 */       String prefix = stringRep.substring(0, colon);
/*  278 */       String localname = stringRep.substring(colon + 1);
/*  279 */       String namespace = null;
/*      */ 
/*  282 */       if (!prefix.equals("xmlns")) {
/*  283 */         namespace = this._symbolTable.lookupNamespace(prefix);
/*  284 */         if ((namespace == null) && (reportError)) {
/*  285 */           int line = getLineNumber();
/*  286 */           ErrorMsg err = new ErrorMsg("NAMESPACE_UNDEF_ERR", line, prefix);
/*      */ 
/*  288 */           reportError(3, err);
/*      */         }
/*      */       }
/*  291 */       return getQName(namespace, prefix, localname);
/*      */     }
/*      */ 
/*  294 */     if (stringRep.equals("xmlns")) {
/*  295 */       ignoreDefaultNs = true;
/*      */     }
/*  297 */     String defURI = ignoreDefaultNs ? null : this._symbolTable.lookupNamespace("");
/*      */ 
/*  299 */     return getQName(defURI, null, stringRep);
/*      */   }
/*      */ 
/*      */   public QName getQName(String namespace, String prefix, String localname)
/*      */   {
/*  304 */     if ((namespace == null) || (namespace.equals(""))) {
/*  305 */       QName name = (QName)this._qNames.get(localname);
/*  306 */       if (name == null) {
/*  307 */         name = new QName(null, prefix, localname);
/*  308 */         this._qNames.put(localname, name);
/*      */       }
/*  310 */       return name;
/*      */     }
/*      */ 
/*  313 */     Dictionary space = (Dictionary)this._namespaces.get(namespace);
/*  314 */     String lexicalQName = prefix + ':' + localname;
/*      */ 
/*  319 */     if (space == null) {
/*  320 */       QName name = new QName(namespace, prefix, localname);
/*  321 */       this._namespaces.put(namespace, space = new Hashtable());
/*  322 */       space.put(lexicalQName, name);
/*  323 */       return name;
/*      */     }
/*      */ 
/*  326 */     QName name = (QName)space.get(lexicalQName);
/*  327 */     if (name == null) {
/*  328 */       name = new QName(namespace, prefix, localname);
/*  329 */       space.put(lexicalQName, name);
/*      */     }
/*  331 */     return name;
/*      */   }
/*      */ 
/*      */   public QName getQName(String scope, String name)
/*      */   {
/*  337 */     return getQName(scope + name);
/*      */   }
/*      */ 
/*      */   public QName getQName(QName scope, QName name) {
/*  341 */     return getQName(scope.toString() + name.toString());
/*      */   }
/*      */ 
/*      */   public QName getUseAttributeSets() {
/*  345 */     return this._useAttributeSets;
/*      */   }
/*      */ 
/*      */   public QName getExtensionElementPrefixes() {
/*  349 */     return this._extensionElementPrefixes;
/*      */   }
/*      */ 
/*      */   public QName getExcludeResultPrefixes() {
/*  353 */     return this._excludeResultPrefixes;
/*      */   }
/*      */ 
/*      */   public Stylesheet makeStylesheet(SyntaxTreeNode element)
/*      */     throws CompilerException
/*      */   {
/*      */     try
/*      */     {
/*      */       Stylesheet stylesheet;
/*      */       Stylesheet stylesheet;
/*  366 */       if ((element instanceof Stylesheet)) {
/*  367 */         stylesheet = (Stylesheet)element;
/*      */       }
/*      */       else {
/*  370 */         stylesheet = new Stylesheet();
/*  371 */         stylesheet.setSimplified();
/*  372 */         stylesheet.addElement(element);
/*  373 */         stylesheet.setAttributes((AttributesImpl)element.getAttributes());
/*      */ 
/*  376 */         if (element.lookupNamespace("") == null) {
/*  377 */           element.addPrefixMapping("", "");
/*      */         }
/*      */       }
/*  380 */       stylesheet.setParser(this);
/*  381 */       return stylesheet;
/*      */     }
/*      */     catch (ClassCastException e) {
/*  384 */       ErrorMsg err = new ErrorMsg("NOT_STYLESHEET_ERR", element);
/*  385 */       throw new CompilerException(err.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void createAST(Stylesheet stylesheet)
/*      */   {
/*      */     try
/*      */     {
/*  394 */       if (stylesheet != null) {
/*  395 */         stylesheet.parseContents(this);
/*  396 */         int precedence = stylesheet.getImportPrecedence();
/*  397 */         Enumeration elements = stylesheet.elements();
/*  398 */         while (elements.hasMoreElements()) {
/*  399 */           Object child = elements.nextElement();
/*  400 */           if ((child instanceof Text)) {
/*  401 */             int l = getLineNumber();
/*  402 */             ErrorMsg err = new ErrorMsg("ILLEGAL_TEXT_NODE_ERR", l, null);
/*      */ 
/*  404 */             reportError(3, err);
/*      */           }
/*      */         }
/*  407 */         if (!errorsFound())
/*  408 */           stylesheet.typeCheck(this._symbolTable);
/*      */       }
/*      */     }
/*      */     catch (TypeCheckError e)
/*      */     {
/*  413 */       reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e));
/*      */     }
/*      */   }
/*      */ 
/*      */   public SyntaxTreeNode parse(XMLReader reader, InputSource input)
/*      */   {
/*      */     try
/*      */     {
/*  426 */       reader.setContentHandler(this);
/*  427 */       reader.parse(input);
/*      */ 
/*  429 */       return getStylesheet(this._root);
/*      */     }
/*      */     catch (IOException e) {
/*  432 */       if (this._xsltc.debug()) e.printStackTrace();
/*  433 */       reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e));
/*      */     }
/*      */     catch (SAXException e) {
/*  436 */       Throwable ex = e.getException();
/*  437 */       if (this._xsltc.debug()) {
/*  438 */         e.printStackTrace();
/*  439 */         if (ex != null) ex.printStackTrace();
/*      */       }
/*  441 */       reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e));
/*      */     }
/*      */     catch (CompilerException e) {
/*  444 */       if (this._xsltc.debug()) e.printStackTrace();
/*  445 */       reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e));
/*      */     }
/*      */     catch (Exception e) {
/*  448 */       if (this._xsltc.debug()) e.printStackTrace();
/*  449 */       reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e));
/*      */     }
/*  451 */     return null;
/*      */   }
/*      */ 
/*      */   public SyntaxTreeNode parse(InputSource input)
/*      */   {
/*      */     try
/*      */     {
/*  462 */       SAXParserFactory factory = FactoryImpl.getSAXFactory(this._useServicesMechanism);
/*      */ 
/*  464 */       if (this._xsltc.isSecureProcessing())
/*      */         try {
/*  466 */           factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
/*      */         }
/*      */         catch (SAXException e)
/*      */         {
/*      */         }
/*      */       try {
/*  472 */         factory.setFeature("http://xml.org/sax/features/namespaces", true);
/*      */       }
/*      */       catch (Exception e) {
/*  475 */         factory.setNamespaceAware(true);
/*      */       }
/*  477 */       SAXParser parser = factory.newSAXParser();
/*  478 */       XMLReader reader = parser.getXMLReader();
/*  479 */       return parse(reader, input);
/*      */     }
/*      */     catch (ParserConfigurationException e) {
/*  482 */       ErrorMsg err = new ErrorMsg("SAX_PARSER_CONFIG_ERR");
/*  483 */       reportError(3, err);
/*      */     }
/*      */     catch (SAXParseException e) {
/*  486 */       reportError(3, new ErrorMsg(e.getMessage(), e.getLineNumber()));
/*      */     }
/*      */     catch (SAXException e) {
/*  489 */       reportError(3, new ErrorMsg(e.getMessage()));
/*      */     }
/*  491 */     return null;
/*      */   }
/*      */ 
/*      */   public SyntaxTreeNode getDocumentRoot() {
/*  495 */     return this._root;
/*      */   }
/*      */ 
/*      */   protected void setPIParameters(String media, String title, String charset)
/*      */   {
/*  512 */     this._PImedia = media;
/*  513 */     this._PItitle = title;
/*  514 */     this._PIcharset = charset;
/*      */   }
/*      */ 
/*      */   private SyntaxTreeNode getStylesheet(SyntaxTreeNode root)
/*      */     throws CompilerException
/*      */   {
/*  531 */     if (this._target == null) {
/*  532 */       if (!this._rootNamespaceDef) {
/*  533 */         ErrorMsg msg = new ErrorMsg("MISSING_XSLT_URI_ERR");
/*  534 */         throw new CompilerException(msg.toString());
/*      */       }
/*  536 */       return root;
/*      */     }
/*      */ 
/*  540 */     if (this._target.charAt(0) == '#') {
/*  541 */       SyntaxTreeNode element = findStylesheet(root, this._target.substring(1));
/*  542 */       if (element == null) {
/*  543 */         ErrorMsg msg = new ErrorMsg("MISSING_XSLT_TARGET_ERR", this._target, root);
/*      */ 
/*  545 */         throw new CompilerException(msg.toString());
/*      */       }
/*  547 */       return element;
/*      */     }
/*      */ 
/*  550 */     return loadExternalStylesheet(this._target);
/*      */   }
/*      */ 
/*      */   private SyntaxTreeNode findStylesheet(SyntaxTreeNode root, String href)
/*      */   {
/*  561 */     if (root == null) return null;
/*      */ 
/*  563 */     if ((root instanceof Stylesheet)) {
/*  564 */       String id = root.getAttribute("id");
/*  565 */       if (id.equals(href)) return root;
/*      */     }
/*  567 */     Vector children = root.getContents();
/*  568 */     if (children != null) {
/*  569 */       int count = children.size();
/*  570 */       for (int i = 0; i < count; i++) {
/*  571 */         SyntaxTreeNode child = (SyntaxTreeNode)children.elementAt(i);
/*  572 */         SyntaxTreeNode node = findStylesheet(child, href);
/*  573 */         if (node != null) return node;
/*      */       }
/*      */     }
/*  576 */     return null;
/*      */   }
/*      */ 
/*      */   private SyntaxTreeNode loadExternalStylesheet(String location)
/*      */     throws CompilerException
/*      */   {
/*      */     InputSource source;
/*      */     InputSource source;
/*  588 */     if (new File(location).exists())
/*  589 */       source = new InputSource("file:" + location);
/*      */     else {
/*  591 */       source = new InputSource(location);
/*      */     }
/*  593 */     SyntaxTreeNode external = parse(source);
/*  594 */     return external;
/*      */   }
/*      */ 
/*      */   private void initAttrTable(String elementName, String[] attrs) {
/*  598 */     this._instructionAttrs.put(getQName("http://www.w3.org/1999/XSL/Transform", "xsl", elementName), attrs);
/*      */   }
/*      */ 
/*      */   private void initInstructionAttrs()
/*      */   {
/*  603 */     initAttrTable("template", new String[] { "match", "name", "priority", "mode" });
/*      */ 
/*  605 */     initAttrTable("stylesheet", new String[] { "id", "version", "extension-element-prefixes", "exclude-result-prefixes" });
/*      */ 
/*  608 */     initAttrTable("transform", new String[] { "id", "version", "extension-element-prefixes", "exclude-result-prefixes" });
/*      */ 
/*  611 */     initAttrTable("text", new String[] { "disable-output-escaping" });
/*  612 */     initAttrTable("if", new String[] { "test" });
/*  613 */     initAttrTable("choose", new String[0]);
/*  614 */     initAttrTable("when", new String[] { "test" });
/*  615 */     initAttrTable("otherwise", new String[0]);
/*  616 */     initAttrTable("for-each", new String[] { "select" });
/*  617 */     initAttrTable("message", new String[] { "terminate" });
/*  618 */     initAttrTable("number", new String[] { "level", "count", "from", "value", "format", "lang", "letter-value", "grouping-separator", "grouping-size" });
/*      */ 
/*  621 */     initAttrTable("comment", new String[0]);
/*  622 */     initAttrTable("copy", new String[] { "use-attribute-sets" });
/*  623 */     initAttrTable("copy-of", new String[] { "select" });
/*  624 */     initAttrTable("param", new String[] { "name", "select" });
/*  625 */     initAttrTable("with-param", new String[] { "name", "select" });
/*  626 */     initAttrTable("variable", new String[] { "name", "select" });
/*  627 */     initAttrTable("output", new String[] { "method", "version", "encoding", "omit-xml-declaration", "standalone", "doctype-public", "doctype-system", "cdata-section-elements", "indent", "media-type" });
/*      */ 
/*  632 */     initAttrTable("sort", new String[] { "select", "order", "case-order", "lang", "data-type" });
/*      */ 
/*  634 */     initAttrTable("key", new String[] { "name", "match", "use" });
/*  635 */     initAttrTable("fallback", new String[0]);
/*  636 */     initAttrTable("attribute", new String[] { "name", "namespace" });
/*  637 */     initAttrTable("attribute-set", new String[] { "name", "use-attribute-sets" });
/*      */ 
/*  639 */     initAttrTable("value-of", new String[] { "select", "disable-output-escaping" });
/*      */ 
/*  641 */     initAttrTable("element", new String[] { "name", "namespace", "use-attribute-sets" });
/*      */ 
/*  643 */     initAttrTable("call-template", new String[] { "name" });
/*  644 */     initAttrTable("apply-templates", new String[] { "select", "mode" });
/*  645 */     initAttrTable("apply-imports", new String[0]);
/*  646 */     initAttrTable("decimal-format", new String[] { "name", "decimal-separator", "grouping-separator", "infinity", "minus-sign", "NaN", "percent", "per-mille", "zero-digit", "digit", "pattern-separator" });
/*      */ 
/*  650 */     initAttrTable("import", new String[] { "href" });
/*  651 */     initAttrTable("include", new String[] { "href" });
/*  652 */     initAttrTable("strip-space", new String[] { "elements" });
/*  653 */     initAttrTable("preserve-space", new String[] { "elements" });
/*  654 */     initAttrTable("processing-instruction", new String[] { "name" });
/*  655 */     initAttrTable("namespace-alias", new String[] { "stylesheet-prefix", "result-prefix" });
/*      */   }
/*      */ 
/*      */   private void initStdClasses()
/*      */   {
/*  666 */     initStdClass("template", "Template");
/*  667 */     initStdClass("stylesheet", "Stylesheet");
/*  668 */     initStdClass("transform", "Stylesheet");
/*  669 */     initStdClass("text", "Text");
/*  670 */     initStdClass("if", "If");
/*  671 */     initStdClass("choose", "Choose");
/*  672 */     initStdClass("when", "When");
/*  673 */     initStdClass("otherwise", "Otherwise");
/*  674 */     initStdClass("for-each", "ForEach");
/*  675 */     initStdClass("message", "Message");
/*  676 */     initStdClass("number", "Number");
/*  677 */     initStdClass("comment", "Comment");
/*  678 */     initStdClass("copy", "Copy");
/*  679 */     initStdClass("copy-of", "CopyOf");
/*  680 */     initStdClass("param", "Param");
/*  681 */     initStdClass("with-param", "WithParam");
/*  682 */     initStdClass("variable", "Variable");
/*  683 */     initStdClass("output", "Output");
/*  684 */     initStdClass("sort", "Sort");
/*  685 */     initStdClass("key", "Key");
/*  686 */     initStdClass("fallback", "Fallback");
/*  687 */     initStdClass("attribute", "XslAttribute");
/*  688 */     initStdClass("attribute-set", "AttributeSet");
/*  689 */     initStdClass("value-of", "ValueOf");
/*  690 */     initStdClass("element", "XslElement");
/*  691 */     initStdClass("call-template", "CallTemplate");
/*  692 */     initStdClass("apply-templates", "ApplyTemplates");
/*  693 */     initStdClass("apply-imports", "ApplyImports");
/*  694 */     initStdClass("decimal-format", "DecimalFormatting");
/*  695 */     initStdClass("import", "Import");
/*  696 */     initStdClass("include", "Include");
/*  697 */     initStdClass("strip-space", "Whitespace");
/*  698 */     initStdClass("preserve-space", "Whitespace");
/*  699 */     initStdClass("processing-instruction", "ProcessingInstruction");
/*  700 */     initStdClass("namespace-alias", "NamespaceAlias");
/*      */   }
/*      */ 
/*      */   private void initStdClass(String elementName, String className) {
/*  704 */     this._instructionClasses.put(getQName("http://www.w3.org/1999/XSL/Transform", "xsl", elementName), "com.sun.org.apache.xalan.internal.xsltc.compiler." + className);
/*      */   }
/*      */ 
/*      */   public boolean elementSupported(String namespace, String localName)
/*      */   {
/*  709 */     return this._instructionClasses.get(getQName(namespace, "xsl", localName)) != null;
/*      */   }
/*      */ 
/*      */   public boolean functionSupported(String fname) {
/*  713 */     return this._symbolTable.lookupPrimop(fname) != null;
/*      */   }
/*      */ 
/*      */   private void initExtClasses() {
/*  717 */     initExtClass("output", "TransletOutput");
/*  718 */     initExtClass("http://xml.apache.org/xalan/redirect", "write", "TransletOutput");
/*      */   }
/*      */ 
/*      */   private void initExtClass(String elementName, String className) {
/*  722 */     this._instructionClasses.put(getQName("http://xml.apache.org/xalan/xsltc", "translet", elementName), "com.sun.org.apache.xalan.internal.xsltc.compiler." + className);
/*      */   }
/*      */ 
/*      */   private void initExtClass(String namespace, String elementName, String className)
/*      */   {
/*  727 */     this._instructionClasses.put(getQName(namespace, "translet", elementName), "com.sun.org.apache.xalan.internal.xsltc.compiler." + className);
/*      */   }
/*      */ 
/*      */   private void initSymbolTable()
/*      */   {
/*  735 */     MethodType I_V = new MethodType(Type.Int, Type.Void);
/*  736 */     MethodType I_R = new MethodType(Type.Int, Type.Real);
/*  737 */     MethodType I_S = new MethodType(Type.Int, Type.String);
/*  738 */     MethodType I_D = new MethodType(Type.Int, Type.NodeSet);
/*  739 */     MethodType R_I = new MethodType(Type.Real, Type.Int);
/*  740 */     MethodType R_V = new MethodType(Type.Real, Type.Void);
/*  741 */     MethodType R_R = new MethodType(Type.Real, Type.Real);
/*  742 */     MethodType R_D = new MethodType(Type.Real, Type.NodeSet);
/*  743 */     MethodType R_O = new MethodType(Type.Real, Type.Reference);
/*  744 */     MethodType I_I = new MethodType(Type.Int, Type.Int);
/*  745 */     MethodType D_O = new MethodType(Type.NodeSet, Type.Reference);
/*  746 */     MethodType D_V = new MethodType(Type.NodeSet, Type.Void);
/*  747 */     MethodType D_S = new MethodType(Type.NodeSet, Type.String);
/*  748 */     MethodType D_D = new MethodType(Type.NodeSet, Type.NodeSet);
/*  749 */     MethodType A_V = new MethodType(Type.Node, Type.Void);
/*  750 */     MethodType S_V = new MethodType(Type.String, Type.Void);
/*  751 */     MethodType S_S = new MethodType(Type.String, Type.String);
/*  752 */     MethodType S_A = new MethodType(Type.String, Type.Node);
/*  753 */     MethodType S_D = new MethodType(Type.String, Type.NodeSet);
/*  754 */     MethodType S_O = new MethodType(Type.String, Type.Reference);
/*  755 */     MethodType B_O = new MethodType(Type.Boolean, Type.Reference);
/*  756 */     MethodType B_V = new MethodType(Type.Boolean, Type.Void);
/*  757 */     MethodType B_B = new MethodType(Type.Boolean, Type.Boolean);
/*  758 */     MethodType B_S = new MethodType(Type.Boolean, Type.String);
/*  759 */     MethodType D_X = new MethodType(Type.NodeSet, Type.Object);
/*  760 */     MethodType R_RR = new MethodType(Type.Real, Type.Real, Type.Real);
/*  761 */     MethodType I_II = new MethodType(Type.Int, Type.Int, Type.Int);
/*  762 */     MethodType B_RR = new MethodType(Type.Boolean, Type.Real, Type.Real);
/*  763 */     MethodType B_II = new MethodType(Type.Boolean, Type.Int, Type.Int);
/*  764 */     MethodType S_SS = new MethodType(Type.String, Type.String, Type.String);
/*  765 */     MethodType S_DS = new MethodType(Type.String, Type.Real, Type.String);
/*  766 */     MethodType S_SR = new MethodType(Type.String, Type.String, Type.Real);
/*  767 */     MethodType O_SO = new MethodType(Type.Reference, Type.String, Type.Reference);
/*      */ 
/*  769 */     MethodType D_SS = new MethodType(Type.NodeSet, Type.String, Type.String);
/*      */ 
/*  771 */     MethodType D_SD = new MethodType(Type.NodeSet, Type.String, Type.NodeSet);
/*      */ 
/*  773 */     MethodType B_BB = new MethodType(Type.Boolean, Type.Boolean, Type.Boolean);
/*      */ 
/*  775 */     MethodType B_SS = new MethodType(Type.Boolean, Type.String, Type.String);
/*      */ 
/*  777 */     MethodType S_SD = new MethodType(Type.String, Type.String, Type.NodeSet);
/*      */ 
/*  779 */     MethodType S_DSS = new MethodType(Type.String, Type.Real, Type.String, Type.String);
/*      */ 
/*  781 */     MethodType S_SRR = new MethodType(Type.String, Type.String, Type.Real, Type.Real);
/*      */ 
/*  783 */     MethodType S_SSS = new MethodType(Type.String, Type.String, Type.String, Type.String);
/*      */ 
/*  794 */     this._symbolTable.addPrimop("current", A_V);
/*  795 */     this._symbolTable.addPrimop("last", I_V);
/*  796 */     this._symbolTable.addPrimop("position", I_V);
/*  797 */     this._symbolTable.addPrimop("true", B_V);
/*  798 */     this._symbolTable.addPrimop("false", B_V);
/*  799 */     this._symbolTable.addPrimop("not", B_B);
/*  800 */     this._symbolTable.addPrimop("name", S_V);
/*  801 */     this._symbolTable.addPrimop("name", S_A);
/*  802 */     this._symbolTable.addPrimop("generate-id", S_V);
/*  803 */     this._symbolTable.addPrimop("generate-id", S_A);
/*  804 */     this._symbolTable.addPrimop("ceiling", R_R);
/*  805 */     this._symbolTable.addPrimop("floor", R_R);
/*  806 */     this._symbolTable.addPrimop("round", R_R);
/*  807 */     this._symbolTable.addPrimop("contains", B_SS);
/*  808 */     this._symbolTable.addPrimop("number", R_O);
/*  809 */     this._symbolTable.addPrimop("number", R_V);
/*  810 */     this._symbolTable.addPrimop("boolean", B_O);
/*  811 */     this._symbolTable.addPrimop("string", S_O);
/*  812 */     this._symbolTable.addPrimop("string", S_V);
/*  813 */     this._symbolTable.addPrimop("translate", S_SSS);
/*  814 */     this._symbolTable.addPrimop("string-length", I_V);
/*  815 */     this._symbolTable.addPrimop("string-length", I_S);
/*  816 */     this._symbolTable.addPrimop("starts-with", B_SS);
/*  817 */     this._symbolTable.addPrimop("format-number", S_DS);
/*  818 */     this._symbolTable.addPrimop("format-number", S_DSS);
/*  819 */     this._symbolTable.addPrimop("unparsed-entity-uri", S_S);
/*  820 */     this._symbolTable.addPrimop("key", D_SS);
/*  821 */     this._symbolTable.addPrimop("key", D_SD);
/*  822 */     this._symbolTable.addPrimop("id", D_S);
/*  823 */     this._symbolTable.addPrimop("id", D_D);
/*  824 */     this._symbolTable.addPrimop("namespace-uri", S_V);
/*  825 */     this._symbolTable.addPrimop("function-available", B_S);
/*  826 */     this._symbolTable.addPrimop("element-available", B_S);
/*  827 */     this._symbolTable.addPrimop("document", D_S);
/*  828 */     this._symbolTable.addPrimop("document", D_V);
/*      */ 
/*  831 */     this._symbolTable.addPrimop("count", I_D);
/*  832 */     this._symbolTable.addPrimop("sum", R_D);
/*  833 */     this._symbolTable.addPrimop("local-name", S_V);
/*  834 */     this._symbolTable.addPrimop("local-name", S_D);
/*  835 */     this._symbolTable.addPrimop("namespace-uri", S_V);
/*  836 */     this._symbolTable.addPrimop("namespace-uri", S_D);
/*  837 */     this._symbolTable.addPrimop("substring", S_SR);
/*  838 */     this._symbolTable.addPrimop("substring", S_SRR);
/*  839 */     this._symbolTable.addPrimop("substring-after", S_SS);
/*  840 */     this._symbolTable.addPrimop("substring-before", S_SS);
/*  841 */     this._symbolTable.addPrimop("normalize-space", S_V);
/*  842 */     this._symbolTable.addPrimop("normalize-space", S_S);
/*  843 */     this._symbolTable.addPrimop("system-property", S_S);
/*      */ 
/*  846 */     this._symbolTable.addPrimop("nodeset", D_O);
/*  847 */     this._symbolTable.addPrimop("objectType", S_O);
/*  848 */     this._symbolTable.addPrimop("cast", O_SO);
/*      */ 
/*  851 */     this._symbolTable.addPrimop("+", R_RR);
/*  852 */     this._symbolTable.addPrimop("-", R_RR);
/*  853 */     this._symbolTable.addPrimop("*", R_RR);
/*  854 */     this._symbolTable.addPrimop("/", R_RR);
/*  855 */     this._symbolTable.addPrimop("%", R_RR);
/*      */ 
/*  859 */     this._symbolTable.addPrimop("+", I_II);
/*  860 */     this._symbolTable.addPrimop("-", I_II);
/*  861 */     this._symbolTable.addPrimop("*", I_II);
/*      */ 
/*  864 */     this._symbolTable.addPrimop("<", B_RR);
/*  865 */     this._symbolTable.addPrimop("<=", B_RR);
/*  866 */     this._symbolTable.addPrimop(">", B_RR);
/*  867 */     this._symbolTable.addPrimop(">=", B_RR);
/*      */ 
/*  870 */     this._symbolTable.addPrimop("<", B_II);
/*  871 */     this._symbolTable.addPrimop("<=", B_II);
/*  872 */     this._symbolTable.addPrimop(">", B_II);
/*  873 */     this._symbolTable.addPrimop(">=", B_II);
/*      */ 
/*  876 */     this._symbolTable.addPrimop("<", B_BB);
/*  877 */     this._symbolTable.addPrimop("<=", B_BB);
/*  878 */     this._symbolTable.addPrimop(">", B_BB);
/*  879 */     this._symbolTable.addPrimop(">=", B_BB);
/*      */ 
/*  882 */     this._symbolTable.addPrimop("or", B_BB);
/*  883 */     this._symbolTable.addPrimop("and", B_BB);
/*      */ 
/*  886 */     this._symbolTable.addPrimop("u-", R_R);
/*  887 */     this._symbolTable.addPrimop("u-", I_I);
/*      */   }
/*      */ 
/*      */   public SymbolTable getSymbolTable() {
/*  891 */     return this._symbolTable;
/*      */   }
/*      */ 
/*      */   public Template getTemplate() {
/*  895 */     return this._template;
/*      */   }
/*      */ 
/*      */   public void setTemplate(Template template) {
/*  899 */     this._template = template;
/*      */   }
/*      */ 
/*      */   public int getTemplateIndex()
/*      */   {
/*  905 */     return this._templateIndex++;
/*      */   }
/*      */ 
/*      */   public SyntaxTreeNode makeInstance(String uri, String prefix, String local, Attributes attributes)
/*      */   {
/*  925 */     SyntaxTreeNode node = null;
/*  926 */     QName qname = getQName(uri, prefix, local);
/*  927 */     String className = (String)this._instructionClasses.get(qname);
/*      */ 
/*  929 */     if (className != null) {
/*      */       try {
/*  931 */         Class clazz = ObjectFactory.findProviderClass(className, true);
/*  932 */         node = (SyntaxTreeNode)clazz.newInstance();
/*  933 */         node.setQName(qname);
/*  934 */         node.setParser(this);
/*  935 */         if (this._locator != null) {
/*  936 */           node.setLineNumber(getLineNumber());
/*      */         }
/*  938 */         if ((node instanceof Stylesheet)) {
/*  939 */           this._xsltc.setStylesheet((Stylesheet)node);
/*      */         }
/*  941 */         checkForSuperfluousAttributes(node, attributes);
/*      */       }
/*      */       catch (ClassNotFoundException e) {
/*  944 */         ErrorMsg err = new ErrorMsg("CLASS_NOT_FOUND_ERR", node);
/*  945 */         reportError(3, err);
/*      */       }
/*      */       catch (Exception e) {
/*  948 */         ErrorMsg err = new ErrorMsg("INTERNAL_ERR", e.getMessage(), node);
/*      */ 
/*  950 */         reportError(2, err);
/*      */       }
/*      */     }
/*      */     else {
/*  954 */       if (uri != null)
/*      */       {
/*  956 */         if (uri.equals("http://www.w3.org/1999/XSL/Transform")) {
/*  957 */           node = new UnsupportedElement(uri, prefix, local, false);
/*  958 */           UnsupportedElement element = (UnsupportedElement)node;
/*  959 */           ErrorMsg msg = new ErrorMsg("UNSUPPORTED_XSL_ERR", getLineNumber(), local);
/*      */ 
/*  961 */           element.setErrorMessage(msg);
/*  962 */           if (this.versionIsOne) {
/*  963 */             reportError(1, msg);
/*      */           }
/*      */ 
/*      */         }
/*  967 */         else if (uri.equals("http://xml.apache.org/xalan/xsltc")) {
/*  968 */           node = new UnsupportedElement(uri, prefix, local, true);
/*  969 */           UnsupportedElement element = (UnsupportedElement)node;
/*  970 */           ErrorMsg msg = new ErrorMsg("UNSUPPORTED_EXT_ERR", getLineNumber(), local);
/*      */ 
/*  972 */           element.setErrorMessage(msg);
/*      */         }
/*      */         else
/*      */         {
/*  976 */           Stylesheet sheet = this._xsltc.getStylesheet();
/*  977 */           if ((sheet != null) && (sheet.isExtension(uri)) && 
/*  978 */             (sheet != (SyntaxTreeNode)this._parentStack.peek())) {
/*  979 */             node = new UnsupportedElement(uri, prefix, local, true);
/*  980 */             UnsupportedElement elem = (UnsupportedElement)node;
/*  981 */             ErrorMsg msg = new ErrorMsg("UNSUPPORTED_EXT_ERR", getLineNumber(), prefix + ":" + local);
/*      */ 
/*  985 */             elem.setErrorMessage(msg);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  990 */       if (node == null) {
/*  991 */         node = new LiteralElement();
/*  992 */         node.setLineNumber(getLineNumber());
/*      */       }
/*      */     }
/*  995 */     if ((node != null) && ((node instanceof LiteralElement))) {
/*  996 */       ((LiteralElement)node).setQName(qname);
/*      */     }
/*  998 */     return node;
/*      */   }
/*      */ 
/*      */   private void checkForSuperfluousAttributes(SyntaxTreeNode node, Attributes attrs)
/*      */   {
/* 1008 */     QName qname = node.getQName();
/* 1009 */     boolean isStylesheet = node instanceof Stylesheet;
/* 1010 */     String[] legal = (String[])this._instructionAttrs.get(qname);
/* 1011 */     if ((this.versionIsOne) && (legal != null))
/*      */     {
/* 1013 */       int n = attrs.getLength();
/*      */ 
/* 1015 */       for (int i = 0; i < n; i++) {
/* 1016 */         String attrQName = attrs.getQName(i);
/*      */ 
/* 1018 */         if ((isStylesheet) && (attrQName.equals("version"))) {
/* 1019 */           this.versionIsOne = attrs.getValue(i).equals("1.0");
/*      */         }
/*      */ 
/* 1023 */         if ((!attrQName.startsWith("xml")) && (attrQName.indexOf(':') <= 0))
/*      */         {
/* 1026 */           for (int j = 0; (j < legal.length) && 
/* 1027 */             (!attrQName.equalsIgnoreCase(legal[j])); j++);
/* 1031 */           if (j == legal.length) {
/* 1032 */             ErrorMsg err = new ErrorMsg("ILLEGAL_ATTRIBUTE_ERR", attrQName, node);
/*      */ 
/* 1036 */             err.setWarningError(true);
/* 1037 */             reportError(4, err);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Expression parseExpression(SyntaxTreeNode parent, String exp)
/*      */   {
/* 1050 */     return (Expression)parseTopLevel(parent, "<EXPRESSION>" + exp, null);
/*      */   }
/*      */ 
/*      */   public Expression parseExpression(SyntaxTreeNode parent, String attr, String def)
/*      */   {
/* 1062 */     String exp = parent.getAttribute(attr);
/*      */ 
/* 1064 */     if ((exp.length() == 0) && (def != null)) exp = def;
/*      */ 
/* 1066 */     return (Expression)parseTopLevel(parent, "<EXPRESSION>" + exp, exp);
/*      */   }
/*      */ 
/*      */   public Pattern parsePattern(SyntaxTreeNode parent, String pattern)
/*      */   {
/* 1075 */     return (Pattern)parseTopLevel(parent, "<PATTERN>" + pattern, pattern);
/*      */   }
/*      */ 
/*      */   public Pattern parsePattern(SyntaxTreeNode parent, String attr, String def)
/*      */   {
/* 1087 */     String pattern = parent.getAttribute(attr);
/*      */ 
/* 1089 */     if ((pattern.length() == 0) && (def != null)) pattern = def;
/*      */ 
/* 1091 */     return (Pattern)parseTopLevel(parent, "<PATTERN>" + pattern, pattern);
/*      */   }
/*      */ 
/*      */   private SyntaxTreeNode parseTopLevel(SyntaxTreeNode parent, String text, String expression)
/*      */   {
/* 1100 */     int line = getLineNumber();
/*      */     try
/*      */     {
/* 1103 */       this._xpathParser.setScanner(new XPathLexer(new StringReader(text)));
/* 1104 */       Symbol result = this._xpathParser.parse(expression, line);
/* 1105 */       if (result != null) {
/* 1106 */         SyntaxTreeNode node = (SyntaxTreeNode)result.value;
/* 1107 */         if (node != null) {
/* 1108 */           node.setParser(this);
/* 1109 */           node.setParent(parent);
/* 1110 */           node.setLineNumber(line);
/*      */ 
/* 1112 */           return node;
/*      */         }
/*      */       }
/* 1115 */       reportError(3, new ErrorMsg("XPATH_PARSER_ERR", expression, parent));
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1119 */       if (this._xsltc.debug()) e.printStackTrace();
/* 1120 */       reportError(3, new ErrorMsg("XPATH_PARSER_ERR", expression, parent));
/*      */     }
/*      */ 
/* 1125 */     SyntaxTreeNode.Dummy.setParser(this);
/* 1126 */     return SyntaxTreeNode.Dummy;
/*      */   }
/*      */ 
/*      */   public boolean errorsFound()
/*      */   {
/* 1135 */     return this._errors.size() > 0;
/*      */   }
/*      */ 
/*      */   public void printErrors()
/*      */   {
/* 1142 */     int size = this._errors.size();
/* 1143 */     if (size > 0) {
/* 1144 */       System.err.println(new ErrorMsg("COMPILER_ERROR_KEY"));
/* 1145 */       for (int i = 0; i < size; i++)
/* 1146 */         System.err.println("  " + this._errors.elementAt(i));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void printWarnings()
/*      */   {
/* 1155 */     int size = this._warnings.size();
/* 1156 */     if (size > 0) {
/* 1157 */       System.err.println(new ErrorMsg("COMPILER_WARNING_KEY"));
/* 1158 */       for (int i = 0; i < size; i++)
/* 1159 */         System.err.println("  " + this._warnings.elementAt(i));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reportError(int category, ErrorMsg error)
/*      */   {
/* 1168 */     switch (category)
/*      */     {
/*      */     case 0:
/* 1172 */       this._errors.addElement(error);
/* 1173 */       break;
/*      */     case 1:
/* 1177 */       this._errors.addElement(error);
/* 1178 */       break;
/*      */     case 2:
/* 1182 */       this._errors.addElement(error);
/* 1183 */       break;
/*      */     case 3:
/* 1187 */       this._errors.addElement(error);
/* 1188 */       break;
/*      */     case 4:
/* 1192 */       this._warnings.addElement(error);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Vector getErrors()
/*      */   {
/* 1198 */     return this._errors;
/*      */   }
/*      */ 
/*      */   public Vector getWarnings() {
/* 1202 */     return this._warnings;
/*      */   }
/*      */ 
/*      */   public void startDocument()
/*      */   {
/* 1214 */     this._root = null;
/* 1215 */     this._target = null;
/* 1216 */     this._prefixMapping = null;
/* 1217 */     this._parentStack = new Stack();
/*      */   }
/*      */ 
/*      */   public void endDocument()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void startPrefixMapping(String prefix, String uri)
/*      */   {
/* 1231 */     if (this._prefixMapping == null) {
/* 1232 */       this._prefixMapping = new Hashtable();
/*      */     }
/* 1234 */     this._prefixMapping.put(prefix, uri);
/*      */   }
/*      */ 
/*      */   public void endPrefixMapping(String prefix)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void startElement(String uri, String localname, String qname, Attributes attributes)
/*      */     throws SAXException
/*      */   {
/* 1251 */     int col = qname.lastIndexOf(':');
/* 1252 */     String prefix = col == -1 ? null : qname.substring(0, col);
/*      */ 
/* 1254 */     SyntaxTreeNode element = makeInstance(uri, prefix, localname, attributes);
/*      */ 
/* 1256 */     if (element == null) {
/* 1257 */       ErrorMsg err = new ErrorMsg("ELEMENT_PARSE_ERR", prefix + ':' + localname);
/*      */ 
/* 1259 */       throw new SAXException(err.toString());
/*      */     }
/*      */ 
/* 1264 */     if (this._root == null) {
/* 1265 */       if ((this._prefixMapping == null) || (!this._prefixMapping.containsValue("http://www.w3.org/1999/XSL/Transform")))
/*      */       {
/* 1267 */         this._rootNamespaceDef = false;
/*      */       }
/* 1269 */       else this._rootNamespaceDef = true;
/* 1270 */       this._root = element;
/*      */     }
/*      */     else {
/* 1273 */       SyntaxTreeNode parent = (SyntaxTreeNode)this._parentStack.peek();
/* 1274 */       parent.addElement(element);
/* 1275 */       element.setParent(parent);
/*      */     }
/* 1277 */     element.setAttributes(new AttributesImpl(attributes));
/* 1278 */     element.setPrefixMapping(this._prefixMapping);
/*      */ 
/* 1280 */     if ((element instanceof Stylesheet))
/*      */     {
/* 1284 */       getSymbolTable().setCurrentNode(element);
/* 1285 */       ((Stylesheet)element).declareExtensionPrefixes(this);
/*      */     }
/*      */ 
/* 1288 */     this._prefixMapping = null;
/* 1289 */     this._parentStack.push(element);
/*      */   }
/*      */ 
/*      */   public void endElement(String uri, String localname, String qname)
/*      */   {
/* 1296 */     this._parentStack.pop();
/*      */   }
/*      */ 
/*      */   public void characters(char[] ch, int start, int length)
/*      */   {
/* 1303 */     String string = new String(ch, start, length);
/* 1304 */     SyntaxTreeNode parent = (SyntaxTreeNode)this._parentStack.peek();
/*      */ 
/* 1306 */     if (string.length() == 0) return;
/*      */ 
/* 1310 */     if ((parent instanceof Text)) {
/* 1311 */       ((Text)parent).setText(string);
/* 1312 */       return;
/*      */     }
/*      */ 
/* 1316 */     if ((parent instanceof Stylesheet)) return;
/*      */ 
/* 1318 */     SyntaxTreeNode bro = parent.lastChild();
/* 1319 */     if ((bro != null) && ((bro instanceof Text))) {
/* 1320 */       Text text = (Text)bro;
/* 1321 */       if ((!text.isTextElement()) && (
/* 1322 */         (length > 1) || (ch[0] < 'Ā'))) {
/* 1323 */         text.setText(string);
/* 1324 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1330 */     parent.addElement(new Text(string));
/*      */   }
/*      */ 
/*      */   private String getTokenValue(String token) {
/* 1334 */     int start = token.indexOf('"');
/* 1335 */     int stop = token.lastIndexOf('"');
/* 1336 */     return token.substring(start + 1, stop);
/*      */   }
/*      */ 
/*      */   public void processingInstruction(String name, String value)
/*      */   {
/* 1345 */     if ((this._target == null) && (name.equals("xml-stylesheet")))
/*      */     {
/* 1347 */       String href = null;
/* 1348 */       String media = null;
/* 1349 */       String title = null;
/* 1350 */       String charset = null;
/*      */ 
/* 1353 */       StringTokenizer tokens = new StringTokenizer(value);
/* 1354 */       while (tokens.hasMoreElements()) {
/* 1355 */         String token = (String)tokens.nextElement();
/* 1356 */         if (token.startsWith("href"))
/* 1357 */           href = getTokenValue(token);
/* 1358 */         else if (token.startsWith("media"))
/* 1359 */           media = getTokenValue(token);
/* 1360 */         else if (token.startsWith("title"))
/* 1361 */           title = getTokenValue(token);
/* 1362 */         else if (token.startsWith("charset")) {
/* 1363 */           charset = getTokenValue(token);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1368 */       if (((this._PImedia == null) || (this._PImedia.equals(media))) && ((this._PItitle == null) || (this._PImedia.equals(title))) && ((this._PIcharset == null) || (this._PImedia.equals(charset))))
/*      */       {
/* 1371 */         this._target = href;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ignorableWhitespace(char[] ch, int start, int length)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void skippedEntity(String name)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setDocumentLocator(Locator locator)
/*      */   {
/* 1391 */     this._locator = locator;
/*      */   }
/*      */ 
/*      */   private int getLineNumber()
/*      */   {
/* 1399 */     int line = 0;
/* 1400 */     if (this._locator != null)
/* 1401 */       line = this._locator.getLineNumber();
/* 1402 */     return line;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.Parser
 * JD-Core Version:    0.6.2
 */