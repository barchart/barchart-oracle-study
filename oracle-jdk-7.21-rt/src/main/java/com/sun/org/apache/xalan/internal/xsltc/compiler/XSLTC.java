/*     */ package com.sun.org.apache.xalan.internal.xsltc.compiler;
/*     */ 
/*     */ import com.sun.org.apache.bcel.internal.classfile.JavaClass;
/*     */ import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Attributes.Name;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import java.util.jar.Manifest;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.XMLReader;
/*     */ 
/*     */ public final class XSLTC
/*     */ {
/*     */   private Parser _parser;
/*  66 */   private XMLReader _reader = null;
/*     */ 
/*  69 */   private SourceLoader _loader = null;
/*     */   private Stylesheet _stylesheet;
/*  76 */   private int _modeSerial = 1;
/*  77 */   private int _stylesheetSerial = 1;
/*  78 */   private int _stepPatternSerial = 1;
/*  79 */   private int _helperClassSerial = 0;
/*  80 */   private int _attributeSetSerial = 0;
/*     */   private int[] _numberFieldIndexes;
/*     */   private int _nextGType;
/*     */   private Vector _namesIndex;
/*     */   private Hashtable _elements;
/*     */   private Hashtable _attributes;
/*     */   private int _nextNSType;
/*     */   private Vector _namespaceIndex;
/*     */   private Hashtable _namespaces;
/*     */   private Hashtable _namespacePrefixes;
/*     */   private Vector m_characterData;
/*     */   public static final int FILE_OUTPUT = 0;
/*     */   public static final int JAR_OUTPUT = 1;
/*     */   public static final int BYTEARRAY_OUTPUT = 2;
/*     */   public static final int CLASSLOADER_OUTPUT = 3;
/*     */   public static final int BYTEARRAY_AND_FILE_OUTPUT = 4;
/*     */   public static final int BYTEARRAY_AND_JAR_OUTPUT = 5;
/* 110 */   private boolean _debug = false;
/* 111 */   private String _jarFileName = null;
/* 112 */   private String _className = null;
/* 113 */   private String _packageName = null;
/* 114 */   private File _destDir = null;
/* 115 */   private int _outputType = 0;
/*     */   private Vector _classes;
/*     */   private Vector _bcelClasses;
/* 119 */   private boolean _callsNodeset = false;
/* 120 */   private boolean _multiDocument = false;
/* 121 */   private boolean _hasIdCall = false;
/*     */ 
/* 129 */   private boolean _templateInlining = false;
/*     */ 
/* 134 */   private boolean _isSecureProcessing = false;
/*     */ 
/* 136 */   private boolean _useServicesMechanism = true;
/*     */ 
/*     */   public XSLTC(boolean useServicesMechanism)
/*     */   {
/* 142 */     this._parser = new Parser(this, useServicesMechanism);
/*     */   }
/*     */ 
/*     */   public void setSecureProcessing(boolean flag)
/*     */   {
/* 149 */     this._isSecureProcessing = flag;
/*     */   }
/*     */ 
/*     */   public boolean isSecureProcessing()
/*     */   {
/* 156 */     return this._isSecureProcessing;
/*     */   }
/*     */ 
/*     */   public boolean useServicesMechnism()
/*     */   {
/* 162 */     return this._useServicesMechanism;
/*     */   }
/*     */ 
/*     */   public void setServicesMechnism(boolean flag)
/*     */   {
/* 169 */     this._useServicesMechanism = flag;
/*     */   }
/*     */ 
/*     */   public Parser getParser()
/*     */   {
/* 176 */     return this._parser;
/*     */   }
/*     */ 
/*     */   public void setOutputType(int type)
/*     */   {
/* 183 */     this._outputType = type;
/*     */   }
/*     */ 
/*     */   public Properties getOutputProperties()
/*     */   {
/* 190 */     return this._parser.getOutputProperties();
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/* 197 */     reset();
/* 198 */     this._reader = null;
/* 199 */     this._classes = new Vector();
/* 200 */     this._bcelClasses = new Vector();
/*     */   }
/*     */ 
/*     */   private void reset()
/*     */   {
/* 207 */     this._nextGType = 14;
/* 208 */     this._elements = new Hashtable();
/* 209 */     this._attributes = new Hashtable();
/* 210 */     this._namespaces = new Hashtable();
/* 211 */     this._namespaces.put("", new Integer(this._nextNSType));
/* 212 */     this._namesIndex = new Vector(128);
/* 213 */     this._namespaceIndex = new Vector(32);
/* 214 */     this._namespacePrefixes = new Hashtable();
/* 215 */     this._stylesheet = null;
/* 216 */     this._parser.init();
/*     */ 
/* 218 */     this._modeSerial = 1;
/* 219 */     this._stylesheetSerial = 1;
/* 220 */     this._stepPatternSerial = 1;
/* 221 */     this._helperClassSerial = 0;
/* 222 */     this._attributeSetSerial = 0;
/* 223 */     this._multiDocument = false;
/* 224 */     this._hasIdCall = false;
/* 225 */     this._numberFieldIndexes = new int[] { -1, -1, -1 };
/*     */   }
/*     */ 
/*     */   public void setSourceLoader(SourceLoader loader)
/*     */   {
/* 238 */     this._loader = loader;
/*     */   }
/*     */ 
/*     */   public void setTemplateInlining(boolean templateInlining)
/*     */   {
/* 248 */     this._templateInlining = templateInlining;
/*     */   }
/*     */ 
/*     */   public boolean getTemplateInlining()
/*     */   {
/* 254 */     return this._templateInlining;
/*     */   }
/*     */ 
/*     */   public void setPIParameters(String media, String title, String charset)
/*     */   {
/* 267 */     this._parser.setPIParameters(media, title, charset);
/*     */   }
/*     */ 
/*     */   public boolean compile(URL url)
/*     */   {
/*     */     try
/*     */     {
/* 277 */       InputStream stream = url.openStream();
/* 278 */       InputSource input = new InputSource(stream);
/* 279 */       input.setSystemId(url.toString());
/* 280 */       return compile(input, this._className);
/*     */     }
/*     */     catch (IOException e) {
/* 283 */       this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", e));
/* 284 */     }return false;
/*     */   }
/*     */ 
/*     */   public boolean compile(URL url, String name)
/*     */   {
/*     */     try
/*     */     {
/* 296 */       InputStream stream = url.openStream();
/* 297 */       InputSource input = new InputSource(stream);
/* 298 */       input.setSystemId(url.toString());
/* 299 */       return compile(input, name);
/*     */     }
/*     */     catch (IOException e) {
/* 302 */       this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", e));
/* 303 */     }return false;
/*     */   }
/*     */ 
/*     */   public boolean compile(InputStream stream, String name)
/*     */   {
/* 314 */     InputSource input = new InputSource(stream);
/* 315 */     input.setSystemId(name);
/* 316 */     return compile(input, name);
/*     */   }
/*     */ 
/*     */   public boolean compile(InputSource input, String name)
/*     */   {
/*     */     try
/*     */     {
/* 328 */       reset();
/*     */ 
/* 331 */       String systemId = null;
/* 332 */       if (input != null) {
/* 333 */         systemId = input.getSystemId();
/*     */       }
/*     */ 
/* 337 */       if (this._className == null) {
/* 338 */         if (name != null) {
/* 339 */           setClassName(name);
/*     */         }
/* 341 */         else if ((systemId != null) && (!systemId.equals(""))) {
/* 342 */           setClassName(Util.baseName(systemId));
/*     */         }
/*     */ 
/* 346 */         if ((this._className == null) || (this._className.length() == 0)) {
/* 347 */           setClassName("GregorSamsa");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 352 */       SyntaxTreeNode element = null;
/* 353 */       if (this._reader == null) {
/* 354 */         element = this._parser.parse(input);
/*     */       }
/*     */       else {
/* 357 */         element = this._parser.parse(this._reader, input);
/*     */       }
/*     */ 
/* 361 */       if ((!this._parser.errorsFound()) && (element != null))
/*     */       {
/* 363 */         this._stylesheet = this._parser.makeStylesheet(element);
/* 364 */         this._stylesheet.setSourceLoader(this._loader);
/* 365 */         this._stylesheet.setSystemId(systemId);
/* 366 */         this._stylesheet.setParentStylesheet(null);
/* 367 */         this._stylesheet.setTemplateInlining(this._templateInlining);
/* 368 */         this._parser.setCurrentStylesheet(this._stylesheet);
/*     */ 
/* 371 */         this._parser.createAST(this._stylesheet);
/*     */       }
/*     */ 
/* 374 */       if ((!this._parser.errorsFound()) && (this._stylesheet != null)) {
/* 375 */         this._stylesheet.setCallsNodeset(this._callsNodeset);
/* 376 */         this._stylesheet.setMultiDocument(this._multiDocument);
/* 377 */         this._stylesheet.setHasIdCall(this._hasIdCall);
/*     */ 
/* 380 */         synchronized (getClass()) {
/* 381 */           this._stylesheet.translate();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 386 */       e.printStackTrace();
/* 387 */       this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", e));
/*     */     }
/*     */     catch (Error e) {
/* 390 */       if (this._debug) e.printStackTrace();
/* 391 */       this._parser.reportError(2, new ErrorMsg("JAXP_COMPILE_ERR", e));
/*     */     }
/*     */     finally {
/* 394 */       this._reader = null;
/*     */     }
/* 396 */     return !this._parser.errorsFound();
/*     */   }
/*     */ 
/*     */   public boolean compile(Vector stylesheets)
/*     */   {
/* 406 */     int count = stylesheets.size();
/*     */ 
/* 409 */     if (count == 0) return true;
/*     */ 
/* 413 */     if (count == 1) {
/* 414 */       Object url = stylesheets.firstElement();
/* 415 */       if ((url instanceof URL)) {
/* 416 */         return compile((URL)url);
/*     */       }
/* 418 */       return false;
/*     */     }
/*     */ 
/* 422 */     Enumeration urls = stylesheets.elements();
/* 423 */     while (urls.hasMoreElements()) {
/* 424 */       this._className = null;
/* 425 */       Object url = urls.nextElement();
/* 426 */       if (((url instanceof URL)) && 
/* 427 */         (!compile((URL)url))) return false;
/*     */ 
/*     */     }
/*     */ 
/* 431 */     return true;
/*     */   }
/*     */ 
/*     */   public byte[][] getBytecodes()
/*     */   {
/* 439 */     int count = this._classes.size();
/* 440 */     byte[][] result = new byte[count][1];
/* 441 */     for (int i = 0; i < count; i++)
/* 442 */       result[i] = ((byte[])(byte[])this._classes.elementAt(i));
/* 443 */     return result;
/*     */   }
/*     */ 
/*     */   public byte[][] compile(String name, InputSource input, int outputType)
/*     */   {
/* 455 */     this._outputType = outputType;
/* 456 */     if (compile(input, name)) {
/* 457 */       return getBytecodes();
/*     */     }
/* 459 */     return (byte[][])null;
/*     */   }
/*     */ 
/*     */   public byte[][] compile(String name, InputSource input)
/*     */   {
/* 470 */     return compile(name, input, 2);
/*     */   }
/*     */ 
/*     */   public void setXMLReader(XMLReader reader)
/*     */   {
/* 478 */     this._reader = reader;
/*     */   }
/*     */ 
/*     */   public XMLReader getXMLReader()
/*     */   {
/* 485 */     return this._reader;
/*     */   }
/*     */ 
/*     */   public Vector getErrors()
/*     */   {
/* 493 */     return this._parser.getErrors();
/*     */   }
/*     */ 
/*     */   public Vector getWarnings()
/*     */   {
/* 501 */     return this._parser.getWarnings();
/*     */   }
/*     */ 
/*     */   public void printErrors()
/*     */   {
/* 508 */     this._parser.printErrors();
/*     */   }
/*     */ 
/*     */   public void printWarnings()
/*     */   {
/* 515 */     this._parser.printWarnings();
/*     */   }
/*     */ 
/*     */   protected void setMultiDocument(boolean flag)
/*     */   {
/* 523 */     this._multiDocument = flag;
/*     */   }
/*     */ 
/*     */   public boolean isMultiDocument() {
/* 527 */     return this._multiDocument;
/*     */   }
/*     */ 
/*     */   protected void setCallsNodeset(boolean flag)
/*     */   {
/* 535 */     if (flag) setMultiDocument(flag);
/* 536 */     this._callsNodeset = flag;
/*     */   }
/*     */ 
/*     */   public boolean callsNodeset() {
/* 540 */     return this._callsNodeset;
/*     */   }
/*     */ 
/*     */   protected void setHasIdCall(boolean flag) {
/* 544 */     this._hasIdCall = flag;
/*     */   }
/*     */ 
/*     */   public boolean hasIdCall() {
/* 548 */     return this._hasIdCall;
/*     */   }
/*     */ 
/*     */   public void setClassName(String className)
/*     */   {
/* 558 */     String base = Util.baseName(className);
/* 559 */     String noext = Util.noExtName(base);
/* 560 */     String name = Util.toJavaName(noext);
/*     */ 
/* 562 */     if (this._packageName == null)
/* 563 */       this._className = name;
/*     */     else
/* 565 */       this._className = (this._packageName + '.' + name);
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/* 572 */     return this._className;
/*     */   }
/*     */ 
/*     */   private String classFileName(String className)
/*     */   {
/* 580 */     return className.replace('.', File.separatorChar) + ".class";
/*     */   }
/*     */ 
/*     */   private File getOutputFile(String className)
/*     */   {
/* 587 */     if (this._destDir != null) {
/* 588 */       return new File(this._destDir, classFileName(className));
/*     */     }
/* 590 */     return new File(classFileName(className));
/*     */   }
/*     */ 
/*     */   public boolean setDestDirectory(String dstDirName)
/*     */   {
/* 598 */     File dir = new File(dstDirName);
/* 599 */     if ((SecuritySupport.getFileExists(dir)) || (dir.mkdirs())) {
/* 600 */       this._destDir = dir;
/* 601 */       return true;
/*     */     }
/*     */ 
/* 604 */     this._destDir = null;
/* 605 */     return false;
/*     */   }
/*     */ 
/*     */   public void setPackageName(String packageName)
/*     */   {
/* 613 */     this._packageName = packageName;
/* 614 */     if (this._className != null) setClassName(this._className);
/*     */   }
/*     */ 
/*     */   public void setJarFileName(String jarFileName)
/*     */   {
/* 622 */     String JAR_EXT = ".jar";
/* 623 */     if (jarFileName.endsWith(".jar"))
/* 624 */       this._jarFileName = jarFileName;
/*     */     else
/* 626 */       this._jarFileName = (jarFileName + ".jar");
/* 627 */     this._outputType = 1;
/*     */   }
/*     */ 
/*     */   public String getJarFileName() {
/* 631 */     return this._jarFileName;
/*     */   }
/*     */ 
/*     */   public void setStylesheet(Stylesheet stylesheet)
/*     */   {
/* 638 */     if (this._stylesheet == null) this._stylesheet = stylesheet;
/*     */   }
/*     */ 
/*     */   public Stylesheet getStylesheet()
/*     */   {
/* 645 */     return this._stylesheet;
/*     */   }
/*     */ 
/*     */   public int registerAttribute(QName name)
/*     */   {
/* 653 */     Integer code = (Integer)this._attributes.get(name.toString());
/* 654 */     if (code == null) {
/* 655 */       code = new Integer(this._nextGType++);
/* 656 */       this._attributes.put(name.toString(), code);
/* 657 */       String uri = name.getNamespace();
/* 658 */       String local = "@" + name.getLocalPart();
/* 659 */       if ((uri != null) && (!uri.equals("")))
/* 660 */         this._namesIndex.addElement(uri + ":" + local);
/*     */       else
/* 662 */         this._namesIndex.addElement(local);
/* 663 */       if (name.getLocalPart().equals("*")) {
/* 664 */         registerNamespace(name.getNamespace());
/*     */       }
/*     */     }
/* 667 */     return code.intValue();
/*     */   }
/*     */ 
/*     */   public int registerElement(QName name)
/*     */   {
/* 676 */     Integer code = (Integer)this._elements.get(name.toString());
/* 677 */     if (code == null) {
/* 678 */       this._elements.put(name.toString(), code = new Integer(this._nextGType++));
/* 679 */       this._namesIndex.addElement(name.toString());
/*     */     }
/* 681 */     if (name.getLocalPart().equals("*")) {
/* 682 */       registerNamespace(name.getNamespace());
/*     */     }
/* 684 */     return code.intValue();
/*     */   }
/*     */ 
/*     */   public int registerNamespacePrefix(QName name)
/*     */   {
/* 694 */     Integer code = (Integer)this._namespacePrefixes.get(name.toString());
/* 695 */     if (code == null) {
/* 696 */       code = new Integer(this._nextGType++);
/* 697 */       this._namespacePrefixes.put(name.toString(), code);
/* 698 */       String uri = name.getNamespace();
/* 699 */       if ((uri != null) && (!uri.equals("")))
/*     */       {
/* 701 */         this._namesIndex.addElement("?");
/*     */       }
/* 703 */       else this._namesIndex.addElement("?" + name.getLocalPart());
/*     */     }
/*     */ 
/* 706 */     return code.intValue();
/*     */   }
/*     */ 
/*     */   public int registerNamespace(String namespaceURI)
/*     */   {
/* 714 */     Integer code = (Integer)this._namespaces.get(namespaceURI);
/* 715 */     if (code == null) {
/* 716 */       code = new Integer(this._nextNSType++);
/* 717 */       this._namespaces.put(namespaceURI, code);
/* 718 */       this._namespaceIndex.addElement(namespaceURI);
/*     */     }
/* 720 */     return code.intValue();
/*     */   }
/*     */ 
/*     */   public int nextModeSerial() {
/* 724 */     return this._modeSerial++;
/*     */   }
/*     */ 
/*     */   public int nextStylesheetSerial() {
/* 728 */     return this._stylesheetSerial++;
/*     */   }
/*     */ 
/*     */   public int nextStepPatternSerial() {
/* 732 */     return this._stepPatternSerial++;
/*     */   }
/*     */ 
/*     */   public int[] getNumberFieldIndexes() {
/* 736 */     return this._numberFieldIndexes;
/*     */   }
/*     */ 
/*     */   public int nextHelperClassSerial() {
/* 740 */     return this._helperClassSerial++;
/*     */   }
/*     */ 
/*     */   public int nextAttributeSetSerial() {
/* 744 */     return this._attributeSetSerial++;
/*     */   }
/*     */ 
/*     */   public Vector getNamesIndex() {
/* 748 */     return this._namesIndex;
/*     */   }
/*     */ 
/*     */   public Vector getNamespaceIndex() {
/* 752 */     return this._namespaceIndex;
/*     */   }
/*     */ 
/*     */   public String getHelperClassName()
/*     */   {
/* 760 */     return getClassName() + '$' + this._helperClassSerial++;
/*     */   }
/*     */ 
/*     */   public void dumpClass(JavaClass clazz)
/*     */   {
/* 765 */     if ((this._outputType == 0) || (this._outputType == 4))
/*     */     {
/* 768 */       File outFile = getOutputFile(clazz.getClassName());
/* 769 */       String parentDir = outFile.getParent();
/* 770 */       if (parentDir != null) {
/* 771 */         File parentFile = new File(parentDir);
/* 772 */         if (!SecuritySupport.getFileExists(parentFile))
/* 773 */           parentFile.mkdirs();
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 778 */       switch (this._outputType) {
/*     */       case 0:
/* 780 */         clazz.dump(new BufferedOutputStream(new FileOutputStream(getOutputFile(clazz.getClassName()))));
/*     */ 
/* 784 */         break;
/*     */       case 1:
/* 786 */         this._bcelClasses.addElement(clazz);
/* 787 */         break;
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/* 792 */         ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
/* 793 */         clazz.dump(out);
/* 794 */         this._classes.addElement(out.toByteArray());
/*     */ 
/* 796 */         if (this._outputType == 4) {
/* 797 */           clazz.dump(new BufferedOutputStream(new FileOutputStream(getOutputFile(clazz.getClassName()))));
/*     */         }
/* 799 */         else if (this._outputType == 5)
/* 800 */           this._bcelClasses.addElement(clazz);
/*     */         break;
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 806 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private String entryName(File f)
/*     */     throws IOException
/*     */   {
/* 814 */     return f.getName().replace(File.separatorChar, '/');
/*     */   }
/*     */ 
/*     */   public void outputToJar()
/*     */     throws IOException
/*     */   {
/* 822 */     Manifest manifest = new Manifest();
/* 823 */     Attributes atrs = manifest.getMainAttributes();
/* 824 */     atrs.put(Attributes.Name.MANIFEST_VERSION, "1.2");
/*     */ 
/* 826 */     Map map = manifest.getEntries();
/*     */ 
/* 828 */     Enumeration classes = this._bcelClasses.elements();
/* 829 */     String now = new Date().toString();
/* 830 */     Attributes.Name dateAttr = new Attributes.Name("Date");
/*     */ 
/* 832 */     while (classes.hasMoreElements()) {
/* 833 */       JavaClass clazz = (JavaClass)classes.nextElement();
/* 834 */       String className = clazz.getClassName().replace('.', '/');
/* 835 */       Attributes attr = new Attributes();
/* 836 */       attr.put(dateAttr, now);
/* 837 */       map.put(className + ".class", attr);
/*     */     }
/*     */ 
/* 840 */     File jarFile = new File(this._destDir, this._jarFileName);
/* 841 */     JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest);
/*     */ 
/* 843 */     classes = this._bcelClasses.elements();
/* 844 */     while (classes.hasMoreElements()) {
/* 845 */       JavaClass clazz = (JavaClass)classes.nextElement();
/* 846 */       String className = clazz.getClassName().replace('.', '/');
/* 847 */       jos.putNextEntry(new JarEntry(className + ".class"));
/* 848 */       ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
/* 849 */       clazz.dump(out);
/* 850 */       out.writeTo(jos);
/*     */     }
/* 852 */     jos.close();
/*     */   }
/*     */ 
/*     */   public void setDebug(boolean debug)
/*     */   {
/* 859 */     this._debug = debug;
/*     */   }
/*     */ 
/*     */   public boolean debug()
/*     */   {
/* 866 */     return this._debug;
/*     */   }
/*     */ 
/*     */   public String getCharacterData(int index)
/*     */   {
/* 879 */     return ((StringBuffer)this.m_characterData.elementAt(index)).toString();
/*     */   }
/*     */ 
/*     */   public int getCharacterDataCount()
/*     */   {
/* 887 */     return this.m_characterData != null ? this.m_characterData.size() : 0;
/*     */   }
/*     */ 
/*     */   public int addCharacterData(String newData)
/*     */   {
/*     */     StringBuffer currData;
/* 899 */     if (this.m_characterData == null) {
/* 900 */       this.m_characterData = new Vector();
/* 901 */       StringBuffer currData = new StringBuffer();
/* 902 */       this.m_characterData.addElement(currData);
/*     */     } else {
/* 904 */       currData = (StringBuffer)this.m_characterData.elementAt(this.m_characterData.size() - 1);
/*     */     }
/*     */ 
/* 912 */     if (newData.length() + currData.length() > 21845) {
/* 913 */       currData = new StringBuffer();
/* 914 */       this.m_characterData.addElement(currData);
/*     */     }
/*     */ 
/* 917 */     int newDataOffset = currData.length();
/* 918 */     currData.append(newData);
/*     */ 
/* 920 */     return newDataOffset;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC
 * JD-Core Version:    0.6.2
 */