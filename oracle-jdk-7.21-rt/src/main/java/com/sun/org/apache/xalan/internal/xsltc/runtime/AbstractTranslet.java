/*     */ package com.sun.org.apache.xalan.internal.xsltc.runtime;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.DOM;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.Translet;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.TransletException;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.dom.KeyIndex;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
/*     */ import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
/*     */ import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.transform.Templates;
/*     */ import org.w3c.dom.DOMImplementation;
/*     */ import org.w3c.dom.Document;
/*     */ 
/*     */ public abstract class AbstractTranslet
/*     */   implements Translet
/*     */ {
/*  65 */   public String _version = "1.0";
/*  66 */   public String _method = null;
/*  67 */   public String _encoding = "UTF-8";
/*  68 */   public boolean _omitHeader = false;
/*  69 */   public String _standalone = null;
/*     */ 
/*  71 */   public boolean _isStandalone = false;
/*  72 */   public String _doctypePublic = null;
/*  73 */   public String _doctypeSystem = null;
/*  74 */   public boolean _indent = false;
/*  75 */   public String _mediaType = null;
/*  76 */   public Vector _cdata = null;
/*  77 */   public int _indentamount = -1;
/*     */   public static final int FIRST_TRANSLET_VERSION = 100;
/*     */   public static final int VER_SPLIT_NAMES_ARRAY = 101;
/*     */   public static final int CURRENT_TRANSLET_VERSION = 101;
/*  88 */   protected int transletVersion = 100;
/*     */   protected String[] namesArray;
/*     */   protected String[] urisArray;
/*     */   protected int[] typesArray;
/*     */   protected String[] namespaceArray;
/*  97 */   protected Templates _templates = null;
/*     */ 
/* 100 */   protected boolean _hasIdCall = false;
/*     */ 
/* 103 */   protected StringValueHandler stringValueHandler = new StringValueHandler();
/*     */   private static final String EMPTYSTRING = "";
/*     */   private static final String ID_INDEX_NAME = "##id";
/*     */   private boolean _useServicesMechanism;
/* 145 */   protected int pbase = 0; protected int pframe = 0;
/* 146 */   protected ArrayList paramsStack = new ArrayList();
/*     */ 
/* 242 */   private MessageHandler _msgHandler = null;
/*     */ 
/* 268 */   public Hashtable _formatSymbols = null;
/*     */ 
/* 422 */   private Hashtable _keyIndexes = null;
/* 423 */   private KeyIndex _emptyKeyIndex = null;
/* 424 */   private int _indexSize = 0;
/* 425 */   private int _currentRootForKeys = 0;
/*     */ 
/* 525 */   private DOMCache _domCache = null;
/*     */ 
/* 703 */   private Hashtable _auxClasses = null;
/*     */ 
/* 764 */   protected DOMImplementation _domImplementation = null;
/*     */ 
/*     */   public void printInternalState()
/*     */   {
/* 117 */     System.out.println("-------------------------------------");
/* 118 */     System.out.println("AbstractTranslet this = " + this);
/* 119 */     System.out.println("pbase = " + this.pbase);
/* 120 */     System.out.println("vframe = " + this.pframe);
/* 121 */     System.out.println("paramsStack.size() = " + this.paramsStack.size());
/* 122 */     System.out.println("namesArray.size = " + this.namesArray.length);
/* 123 */     System.out.println("namespaceArray.size = " + this.namespaceArray.length);
/* 124 */     System.out.println("");
/* 125 */     System.out.println("Total memory = " + Runtime.getRuntime().totalMemory());
/*     */   }
/*     */ 
/*     */   public final DOMAdapter makeDOMAdapter(DOM dom)
/*     */     throws TransletException
/*     */   {
/* 135 */     setRootForKeys(dom.getDocument());
/* 136 */     return new DOMAdapter(dom, this.namesArray, this.urisArray, this.typesArray, this.namespaceArray);
/*     */   }
/*     */ 
/*     */   public final void pushParamFrame()
/*     */   {
/* 152 */     this.paramsStack.add(this.pframe, new Integer(this.pbase));
/* 153 */     this.pbase = (++this.pframe);
/*     */   }
/*     */ 
/*     */   public final void popParamFrame()
/*     */   {
/* 160 */     if (this.pbase > 0) {
/* 161 */       int oldpbase = ((Integer)this.paramsStack.get(--this.pbase)).intValue();
/* 162 */       for (int i = this.pframe - 1; i >= this.pbase; i--) {
/* 163 */         this.paramsStack.remove(i);
/*     */       }
/* 165 */       this.pframe = this.pbase; this.pbase = oldpbase;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Object addParameter(String name, Object value)
/*     */   {
/* 178 */     name = BasisLibrary.mapQNameToJavaName(name);
/* 179 */     return addParameter(name, value, false);
/*     */   }
/*     */ 
/*     */   public final Object addParameter(String name, Object value, boolean isDefault)
/*     */   {
/* 192 */     for (int i = this.pframe - 1; i >= this.pbase; i--) {
/* 193 */       Parameter param = (Parameter)this.paramsStack.get(i);
/*     */ 
/* 195 */       if (param._name.equals(name))
/*     */       {
/* 198 */         if ((param._isDefault) || (!isDefault)) {
/* 199 */           param._value = value;
/* 200 */           param._isDefault = isDefault;
/* 201 */           return value;
/*     */         }
/* 203 */         return param._value;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 208 */     this.paramsStack.add(this.pframe++, new Parameter(name, value, isDefault));
/* 209 */     return value;
/*     */   }
/*     */ 
/*     */   public void clearParameters()
/*     */   {
/* 216 */     this.pbase = (this.pframe = 0);
/* 217 */     this.paramsStack.clear();
/*     */   }
/*     */ 
/*     */   public final Object getParameter(String name)
/*     */   {
/* 226 */     name = BasisLibrary.mapQNameToJavaName(name);
/*     */ 
/* 228 */     for (int i = this.pframe - 1; i >= this.pbase; i--) {
/* 229 */       Parameter param = (Parameter)this.paramsStack.get(i);
/* 230 */       if (param._name.equals(name)) return param._value;
/*     */     }
/* 232 */     return null;
/*     */   }
/*     */ 
/*     */   public final void setMessageHandler(MessageHandler handler)
/*     */   {
/* 248 */     this._msgHandler = handler;
/*     */   }
/*     */ 
/*     */   public final void displayMessage(String msg)
/*     */   {
/* 255 */     if (this._msgHandler == null) {
/* 256 */       System.err.println(msg);
/*     */     }
/*     */     else
/* 259 */       this._msgHandler.displayMessage(msg);
/*     */   }
/*     */ 
/*     */   public void addDecimalFormat(String name, DecimalFormatSymbols symbols)
/*     */   {
/* 276 */     if (this._formatSymbols == null) this._formatSymbols = new Hashtable();
/*     */ 
/* 279 */     if (name == null) name = "";
/*     */ 
/* 282 */     DecimalFormat df = new DecimalFormat();
/* 283 */     if (symbols != null) {
/* 284 */       df.setDecimalFormatSymbols(symbols);
/*     */     }
/* 286 */     this._formatSymbols.put(name, df);
/*     */   }
/*     */ 
/*     */   public final DecimalFormat getDecimalFormat(String name)
/*     */   {
/* 294 */     if (this._formatSymbols != null)
/*     */     {
/* 296 */       if (name == null) name = "";
/*     */ 
/* 298 */       DecimalFormat df = (DecimalFormat)this._formatSymbols.get(name);
/* 299 */       if (df == null) df = (DecimalFormat)this._formatSymbols.get("");
/* 300 */       return df;
/*     */     }
/* 302 */     return null;
/*     */   }
/*     */ 
/*     */   public final void prepassDocument(DOM document)
/*     */   {
/* 312 */     setIndexSize(document.getSize());
/* 313 */     buildIDIndex(document);
/*     */   }
/*     */ 
/*     */   private final void buildIDIndex(DOM document)
/*     */   {
/* 322 */     setRootForKeys(document.getDocument());
/*     */ 
/* 324 */     if ((document instanceof DOMEnhancedForDTM)) {
/* 325 */       DOMEnhancedForDTM enhancedDOM = (DOMEnhancedForDTM)document;
/*     */ 
/* 330 */       if (enhancedDOM.hasDOMSource()) {
/* 331 */         buildKeyIndex("##id", document);
/* 332 */         return;
/*     */       }
/*     */ 
/* 335 */       Hashtable elementsByID = enhancedDOM.getElementsWithIDs();
/*     */ 
/* 337 */       if (elementsByID == null) {
/* 338 */         return;
/*     */       }
/*     */ 
/* 344 */       Enumeration idValues = elementsByID.keys();
/* 345 */       boolean hasIDValues = false;
/*     */ 
/* 347 */       while (idValues.hasMoreElements()) {
/* 348 */         Object idValue = idValues.nextElement();
/* 349 */         int element = document.getNodeHandle(((Integer)elementsByID.get(idValue)).intValue());
/*     */ 
/* 354 */         buildKeyIndex("##id", element, idValue);
/* 355 */         hasIDValues = true;
/*     */       }
/*     */ 
/* 358 */       if (hasIDValues)
/* 359 */         setKeyIndexDom("##id", document);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void postInitialization()
/*     */   {
/* 372 */     if (this.transletVersion < 101) {
/* 373 */       int arraySize = this.namesArray.length;
/* 374 */       String[] newURIsArray = new String[arraySize];
/* 375 */       String[] newNamesArray = new String[arraySize];
/* 376 */       int[] newTypesArray = new int[arraySize];
/*     */ 
/* 378 */       for (int i = 0; i < arraySize; i++) {
/* 379 */         String name = this.namesArray[i];
/* 380 */         int colonIndex = name.lastIndexOf(':');
/* 381 */         int lNameStartIdx = colonIndex + 1;
/*     */ 
/* 383 */         if (colonIndex > -1) {
/* 384 */           newURIsArray[i] = name.substring(0, colonIndex);
/*     */         }
/*     */ 
/* 389 */         if (name.charAt(lNameStartIdx) == '@') {
/* 390 */           lNameStartIdx++;
/* 391 */           newTypesArray[i] = 2;
/* 392 */         } else if (name.charAt(lNameStartIdx) == '?') {
/* 393 */           lNameStartIdx++;
/* 394 */           newTypesArray[i] = 13;
/*     */         } else {
/* 396 */           newTypesArray[i] = 1;
/*     */         }
/* 398 */         newNamesArray[i] = (lNameStartIdx == 0 ? name : name.substring(lNameStartIdx));
/*     */       }
/*     */ 
/* 403 */       this.namesArray = newNamesArray;
/* 404 */       this.urisArray = newURIsArray;
/* 405 */       this.typesArray = newTypesArray;
/*     */     }
/*     */ 
/* 411 */     if (this.transletVersion > 101)
/* 412 */       BasisLibrary.runTimeError("UNKNOWN_TRANSLET_VERSION_ERR", getClass().getName());
/*     */   }
/*     */ 
/*     */   public void setIndexSize(int size)
/*     */   {
/* 432 */     if (size > this._indexSize) this._indexSize = size;
/*     */   }
/*     */ 
/*     */   public KeyIndex createKeyIndex()
/*     */   {
/* 439 */     return new KeyIndex(this._indexSize);
/*     */   }
/*     */ 
/*     */   public void buildKeyIndex(String name, int node, Object value)
/*     */   {
/* 449 */     if (this._keyIndexes == null) this._keyIndexes = new Hashtable();
/*     */ 
/* 451 */     KeyIndex index = (KeyIndex)this._keyIndexes.get(name);
/* 452 */     if (index == null) {
/* 453 */       this._keyIndexes.put(name, index = new KeyIndex(this._indexSize));
/*     */     }
/* 455 */     index.add(value, node, this._currentRootForKeys);
/*     */   }
/*     */ 
/*     */   public void buildKeyIndex(String name, DOM dom)
/*     */   {
/* 464 */     if (this._keyIndexes == null) this._keyIndexes = new Hashtable();
/*     */ 
/* 466 */     KeyIndex index = (KeyIndex)this._keyIndexes.get(name);
/* 467 */     if (index == null) {
/* 468 */       this._keyIndexes.put(name, index = new KeyIndex(this._indexSize));
/*     */     }
/* 470 */     index.setDom(dom, dom.getDocument());
/*     */   }
/*     */ 
/*     */   public KeyIndex getKeyIndex(String name)
/*     */   {
/* 479 */     if (this._keyIndexes == null) {
/* 480 */       return this._emptyKeyIndex = new KeyIndex(1);
/*     */     }
/*     */ 
/* 486 */     KeyIndex index = (KeyIndex)this._keyIndexes.get(name);
/*     */ 
/* 489 */     if (index == null) {
/* 490 */       return this._emptyKeyIndex = new KeyIndex(1);
/*     */     }
/*     */ 
/* 495 */     return index;
/*     */   }
/*     */ 
/*     */   private void setRootForKeys(int root) {
/* 499 */     this._currentRootForKeys = root;
/*     */   }
/*     */ 
/*     */   public void buildKeys(DOM document, DTMAxisIterator iterator, SerializationHandler handler, int root)
/*     */     throws TransletException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setKeyIndexDom(String name, DOM document)
/*     */   {
/* 517 */     getKeyIndex(name).setDom(document, document.getDocument());
/*     */   }
/*     */ 
/*     */   public void setDOMCache(DOMCache cache)
/*     */   {
/* 532 */     this._domCache = cache;
/*     */   }
/*     */ 
/*     */   public DOMCache getDOMCache()
/*     */   {
/* 540 */     return this._domCache;
/*     */   }
/*     */ 
/*     */   public SerializationHandler openOutputHandler(String filename, boolean append)
/*     */     throws TransletException
/*     */   {
/*     */     try
/*     */     {
/* 552 */       TransletOutputHandlerFactory factory = TransletOutputHandlerFactory.newInstance();
/*     */ 
/* 555 */       String dirStr = new File(filename).getParent();
/* 556 */       if ((null != dirStr) && (dirStr.length() > 0)) {
/* 557 */         File dir = new File(dirStr);
/* 558 */         dir.mkdirs();
/*     */       }
/*     */ 
/* 561 */       factory.setEncoding(this._encoding);
/* 562 */       factory.setOutputMethod(this._method);
/* 563 */       factory.setOutputStream(new BufferedOutputStream(new FileOutputStream(filename, append)));
/* 564 */       factory.setOutputType(0);
/*     */ 
/* 566 */       SerializationHandler handler = factory.getSerializationHandler();
/*     */ 
/* 569 */       transferOutputSettings(handler);
/* 570 */       handler.startDocument();
/* 571 */       return handler;
/*     */     }
/*     */     catch (Exception e) {
/* 574 */       throw new TransletException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SerializationHandler openOutputHandler(String filename)
/*     */     throws TransletException
/*     */   {
/* 581 */     return openOutputHandler(filename, false);
/*     */   }
/*     */ 
/*     */   public void closeOutputHandler(SerializationHandler handler) {
/*     */     try {
/* 586 */       handler.endDocument();
/* 587 */       handler.close();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract void transform(DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler)
/*     */     throws TransletException;
/*     */ 
/*     */   public final void transform(DOM document, SerializationHandler handler)
/*     */     throws TransletException
/*     */   {
/*     */     try
/*     */     {
/* 611 */       transform(document, document.getIterator(), handler);
/*     */     } finally {
/* 613 */       this._keyIndexes = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void characters(String string, SerializationHandler handler)
/*     */     throws TransletException
/*     */   {
/* 624 */     if (string != null)
/*     */       try
/*     */       {
/* 627 */         handler.characters(string);
/*     */       } catch (Exception e) {
/* 629 */         throw new TransletException(e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void addCdataElement(String name)
/*     */   {
/* 638 */     if (this._cdata == null) {
/* 639 */       this._cdata = new Vector();
/*     */     }
/*     */ 
/* 642 */     int lastColon = name.lastIndexOf(':');
/*     */ 
/* 644 */     if (lastColon > 0) {
/* 645 */       String uri = name.substring(0, lastColon);
/* 646 */       String localName = name.substring(lastColon + 1);
/* 647 */       this._cdata.addElement(uri);
/* 648 */       this._cdata.addElement(localName);
/*     */     } else {
/* 650 */       this._cdata.addElement(null);
/* 651 */       this._cdata.addElement(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void transferOutputSettings(SerializationHandler handler)
/*     */   {
/* 659 */     if (this._method != null) {
/* 660 */       if (this._method.equals("xml")) {
/* 661 */         if (this._standalone != null) {
/* 662 */           handler.setStandalone(this._standalone);
/*     */         }
/* 664 */         if (this._omitHeader) {
/* 665 */           handler.setOmitXMLDeclaration(true);
/*     */         }
/* 667 */         handler.setCdataSectionElements(this._cdata);
/* 668 */         if (this._version != null) {
/* 669 */           handler.setVersion(this._version);
/*     */         }
/* 671 */         handler.setIndent(this._indent);
/* 672 */         handler.setIndentAmount(this._indentamount);
/* 673 */         if (this._doctypeSystem != null) {
/* 674 */           handler.setDoctype(this._doctypeSystem, this._doctypePublic);
/*     */         }
/* 676 */         handler.setIsStandalone(this._isStandalone);
/*     */       }
/* 678 */       else if (this._method.equals("html")) {
/* 679 */         handler.setIndent(this._indent);
/* 680 */         handler.setDoctype(this._doctypeSystem, this._doctypePublic);
/* 681 */         if (this._mediaType != null)
/* 682 */           handler.setMediaType(this._mediaType);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 687 */       handler.setCdataSectionElements(this._cdata);
/* 688 */       if (this._version != null) {
/* 689 */         handler.setVersion(this._version);
/*     */       }
/* 691 */       if (this._standalone != null) {
/* 692 */         handler.setStandalone(this._standalone);
/*     */       }
/* 694 */       if (this._omitHeader) {
/* 695 */         handler.setOmitXMLDeclaration(true);
/*     */       }
/* 697 */       handler.setIndent(this._indent);
/* 698 */       handler.setDoctype(this._doctypeSystem, this._doctypePublic);
/* 699 */       handler.setIsStandalone(this._isStandalone);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addAuxiliaryClass(Class auxClass)
/*     */   {
/* 706 */     if (this._auxClasses == null) this._auxClasses = new Hashtable();
/* 707 */     this._auxClasses.put(auxClass.getName(), auxClass);
/*     */   }
/*     */ 
/*     */   public void setAuxiliaryClasses(Hashtable auxClasses) {
/* 711 */     this._auxClasses = auxClasses;
/*     */   }
/*     */ 
/*     */   public Class getAuxiliaryClass(String className) {
/* 715 */     if (this._auxClasses == null) return null;
/* 716 */     return (Class)this._auxClasses.get(className);
/*     */   }
/*     */ 
/*     */   public String[] getNamesArray()
/*     */   {
/* 721 */     return this.namesArray;
/*     */   }
/*     */ 
/*     */   public String[] getUrisArray() {
/* 725 */     return this.urisArray;
/*     */   }
/*     */ 
/*     */   public int[] getTypesArray() {
/* 729 */     return this.typesArray;
/*     */   }
/*     */ 
/*     */   public String[] getNamespaceArray() {
/* 733 */     return this.namespaceArray;
/*     */   }
/*     */ 
/*     */   public boolean hasIdCall() {
/* 737 */     return this._hasIdCall;
/*     */   }
/*     */ 
/*     */   public Templates getTemplates() {
/* 741 */     return this._templates;
/*     */   }
/*     */ 
/*     */   public void setTemplates(Templates templates) {
/* 745 */     this._templates = templates;
/*     */   }
/*     */ 
/*     */   public boolean useServicesMechnism()
/*     */   {
/* 751 */     return this._useServicesMechanism;
/*     */   }
/*     */ 
/*     */   public void setServicesMechnism(boolean flag)
/*     */   {
/* 758 */     this._useServicesMechanism = flag;
/*     */   }
/*     */ 
/*     */   public Document newDocument(String uri, String qname)
/*     */     throws ParserConfigurationException
/*     */   {
/* 769 */     if (this._domImplementation == null) {
/* 770 */       DocumentBuilderFactory dbf = FactoryImpl.getDOMFactory(this._useServicesMechanism);
/* 771 */       this._domImplementation = dbf.newDocumentBuilder().getDOMImplementation();
/*     */     }
/* 773 */     return this._domImplementation.createDocument(uri, qname, null);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet
 * JD-Core Version:    0.6.2
 */