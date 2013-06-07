/*      */ package com.sun.org.apache.xerces.internal.impl;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
/*      */ import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
/*      */ import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
/*      */ import com.sun.org.apache.xerces.internal.util.EncodingMap;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
/*      */ import com.sun.org.apache.xerces.internal.xni.QName;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLLocator;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.xml.internal.stream.Entity.ScannedEntity;
/*      */ import com.sun.xml.internal.stream.XMLBufferListener;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.Reader;
/*      */ import java.util.Locale;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class XMLEntityScanner
/*      */   implements XMLLocator
/*      */ {
/*   64 */   protected Entity.ScannedEntity fCurrentEntity = null;
/*   65 */   protected int fBufferSize = 8192;
/*      */   protected XMLEntityManager fEntityManager;
/*      */   private static final boolean DEBUG_ENCODINGS = false;
/*   72 */   private Vector listeners = new Vector();
/*      */ 
/*   74 */   public static final boolean[] VALID_NAMES = new boolean[127];
/*      */   private static final boolean DEBUG_BUFFER = false;
/*      */   private static final boolean DEBUG_SKIP_STRING = false;
/*   86 */   private static final EOFException END_OF_DOCUMENT_ENTITY = new EOFException() {
/*      */     private static final long serialVersionUID = 980337771224675268L;
/*      */ 
/*   89 */     public Throwable fillInStackTrace() { return this; }
/*      */ 
/*   86 */   };
/*      */ 
/*   93 */   protected SymbolTable fSymbolTable = null;
/*   94 */   protected XMLErrorReporter fErrorReporter = null;
/*   95 */   int[] whiteSpaceLookup = new int[100];
/*   96 */   int whiteSpaceLen = 0;
/*   97 */   boolean whiteSpaceInfoNeeded = true;
/*      */   protected boolean fAllowJavaEncodings;
/*      */   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
/*  120 */   protected PropertyManager fPropertyManager = null;
/*      */ 
/*  122 */   boolean isExternal = false;
/*      */ 
/*  141 */   boolean xmlVersionSetExplicitly = false;
/*      */ 
/*      */   public XMLEntityScanner()
/*      */   {
/*      */   }
/*      */ 
/*      */   public XMLEntityScanner(PropertyManager propertyManager, XMLEntityManager entityManager)
/*      */   {
/*  157 */     this.fEntityManager = entityManager;
/*  158 */     reset(propertyManager);
/*      */   }
/*      */ 
/*      */   public final void setBufferSize(int size)
/*      */   {
/*  174 */     this.fBufferSize = size;
/*      */   }
/*      */ 
/*      */   public void reset(PropertyManager propertyManager)
/*      */   {
/*  181 */     this.fSymbolTable = ((SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
/*  182 */     this.fErrorReporter = ((XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/*  183 */     this.fCurrentEntity = null;
/*  184 */     this.whiteSpaceLen = 0;
/*  185 */     this.whiteSpaceInfoNeeded = true;
/*  186 */     this.listeners.clear();
/*      */   }
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */     throws XMLConfigurationException
/*      */   {
/*  208 */     this.fAllowJavaEncodings = componentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
/*      */ 
/*  211 */     this.fSymbolTable = ((SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
/*  212 */     this.fErrorReporter = ((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/*  213 */     this.fCurrentEntity = null;
/*  214 */     this.whiteSpaceLen = 0;
/*  215 */     this.whiteSpaceInfoNeeded = true;
/*  216 */     this.listeners.clear();
/*      */   }
/*      */ 
/*      */   public final void reset(SymbolTable symbolTable, XMLEntityManager entityManager, XMLErrorReporter reporter)
/*      */   {
/*  222 */     this.fCurrentEntity = null;
/*  223 */     this.fSymbolTable = symbolTable;
/*  224 */     this.fEntityManager = entityManager;
/*  225 */     this.fErrorReporter = reporter;
/*      */   }
/*      */ 
/*      */   public final String getXMLVersion()
/*      */   {
/*  239 */     if (this.fCurrentEntity != null) {
/*  240 */       return this.fCurrentEntity.xmlVersion;
/*      */     }
/*  242 */     return null;
/*      */   }
/*      */ 
/*      */   public final void setXMLVersion(String xmlVersion)
/*      */   {
/*  253 */     this.xmlVersionSetExplicitly = true;
/*  254 */     this.fCurrentEntity.xmlVersion = xmlVersion;
/*      */   }
/*      */ 
/*      */   public final void setCurrentEntity(Entity.ScannedEntity scannedEntity)
/*      */   {
/*  263 */     this.fCurrentEntity = scannedEntity;
/*  264 */     if (this.fCurrentEntity != null)
/*  265 */       this.isExternal = this.fCurrentEntity.isExternal();
/*      */   }
/*      */ 
/*      */   public Entity.ScannedEntity getCurrentEntity()
/*      */   {
/*  272 */     return this.fCurrentEntity;
/*      */   }
/*      */ 
/*      */   public final String getBaseSystemId()
/*      */   {
/*  283 */     return (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
/*      */   }
/*      */ 
/*      */   public void setBaseSystemId(String systemId)
/*      */   {
/*      */   }
/*      */ 
/*      */   public final int getLineNumber()
/*      */   {
/*  297 */     return this.fCurrentEntity != null ? this.fCurrentEntity.lineNumber : -1;
/*      */   }
/*      */ 
/*      */   public void setLineNumber(int line)
/*      */   {
/*      */   }
/*      */ 
/*      */   public final int getColumnNumber()
/*      */   {
/*  311 */     return this.fCurrentEntity != null ? this.fCurrentEntity.columnNumber : -1;
/*      */   }
/*      */ 
/*      */   public void setColumnNumber(int col)
/*      */   {
/*      */   }
/*      */ 
/*      */   public final int getCharacterOffset()
/*      */   {
/*  323 */     return this.fCurrentEntity != null ? this.fCurrentEntity.fTotalCountTillLastLoad + this.fCurrentEntity.position : -1;
/*      */   }
/*      */ 
/*      */   public final String getExpandedSystemId()
/*      */   {
/*  328 */     return (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
/*      */   }
/*      */ 
/*      */   public void setExpandedSystemId(String systemId)
/*      */   {
/*      */   }
/*      */ 
/*      */   public final String getLiteralSystemId()
/*      */   {
/*  340 */     return (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getLiteralSystemId() : null;
/*      */   }
/*      */ 
/*      */   public void setLiteralSystemId(String systemId)
/*      */   {
/*      */   }
/*      */ 
/*      */   public final String getPublicId()
/*      */   {
/*  352 */     return (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null;
/*      */   }
/*      */ 
/*      */   public void setPublicId(String publicId)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setVersion(String version)
/*      */   {
/*  366 */     this.fCurrentEntity.version = version;
/*      */   }
/*      */ 
/*      */   public String getVersion() {
/*  370 */     if (this.fCurrentEntity != null)
/*  371 */       return this.fCurrentEntity.version;
/*  372 */     return null;
/*      */   }
/*      */ 
/*      */   public final String getEncoding()
/*      */   {
/*  384 */     if (this.fCurrentEntity != null) {
/*  385 */       return this.fCurrentEntity.encoding;
/*      */     }
/*  387 */     return null;
/*      */   }
/*      */ 
/*      */   public final void setEncoding(String encoding)
/*      */     throws IOException
/*      */   {
/*  414 */     if (this.fCurrentEntity.stream != null)
/*      */     {
/*  422 */       if ((this.fCurrentEntity.encoding == null) || (!this.fCurrentEntity.encoding.equals(encoding)))
/*      */       {
/*  428 */         if ((this.fCurrentEntity.encoding != null) && (this.fCurrentEntity.encoding.startsWith("UTF-16"))) {
/*  429 */           String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
/*  430 */           if (ENCODING.equals("UTF-16")) return;
/*  431 */           if (ENCODING.equals("ISO-10646-UCS-4")) {
/*  432 */             if (this.fCurrentEntity.encoding.equals("UTF-16BE"))
/*  433 */               this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)8);
/*      */             else {
/*  435 */               this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)4);
/*      */             }
/*  437 */             return;
/*      */           }
/*  439 */           if (ENCODING.equals("ISO-10646-UCS-2")) {
/*  440 */             if (this.fCurrentEntity.encoding.equals("UTF-16BE"))
/*  441 */               this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)2);
/*      */             else {
/*  443 */               this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)1);
/*      */             }
/*  445 */             return;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  455 */         this.fCurrentEntity.reader = createReader(this.fCurrentEntity.stream, encoding, null);
/*  456 */         this.fCurrentEntity.encoding = encoding;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public final boolean isExternal()
/*      */   {
/*  468 */     return this.fCurrentEntity.isExternal();
/*      */   }
/*      */ 
/*      */   public int getChar(int relative) throws IOException {
/*  472 */     if (arrangeCapacity(relative + 1, false)) {
/*  473 */       return this.fCurrentEntity.ch[(this.fCurrentEntity.position + relative)];
/*      */     }
/*  475 */     return -1;
/*      */   }
/*      */ 
/*      */   public int peekChar()
/*      */     throws IOException
/*      */   {
/*  495 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  496 */       invokeListeners(0);
/*  497 */       load(0, true);
/*      */     }
/*      */ 
/*  501 */     int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*      */ 
/*  513 */     if (this.isExternal) {
/*  514 */       return c != 13 ? c : 10;
/*      */     }
/*  516 */     return c;
/*      */   }
/*      */ 
/*      */   public int scanChar()
/*      */     throws IOException
/*      */   {
/*  537 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  538 */       invokeListeners(0);
/*  539 */       load(0, true);
/*      */     }
/*      */ 
/*  543 */     int c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/*  544 */     if ((c == 10) || ((c == 13) && (this.isExternal)))
/*      */     {
/*  546 */       this.fCurrentEntity.lineNumber += 1;
/*  547 */       this.fCurrentEntity.columnNumber = 1;
/*  548 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  549 */         invokeListeners(1);
/*  550 */         this.fCurrentEntity.ch[0] = ((char)c);
/*  551 */         load(1, false);
/*      */       }
/*  553 */       if ((c == 13) && (this.isExternal)) {
/*  554 */         if (this.fCurrentEntity.ch[(this.fCurrentEntity.position++)] != '\n') {
/*  555 */           this.fCurrentEntity.position -= 1;
/*      */         }
/*  557 */         c = 10;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  567 */     this.fCurrentEntity.columnNumber += 1;
/*  568 */     return c;
/*      */   }
/*      */ 
/*      */   public String scanNmtoken()
/*      */     throws IOException
/*      */   {
/*  595 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  596 */       invokeListeners(0);
/*  597 */       load(0, true);
/*      */     }
/*      */ 
/*  601 */     int offset = this.fCurrentEntity.position;
/*  602 */     boolean vc = false;
/*      */     while (true)
/*      */     {
/*  606 */       char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  607 */       if (c < '')
/*  608 */         vc = VALID_NAMES[c];
/*      */       else {
/*  610 */         vc = XMLChar.isName(c);
/*      */       }
/*  612 */       if (!vc)
/*      */         break;
/*  614 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  615 */         int length = this.fCurrentEntity.position - offset;
/*  616 */         invokeListeners(length);
/*  617 */         if (length == this.fCurrentEntity.fBufferSize)
/*      */         {
/*  619 */           char[] tmp = new char[this.fCurrentEntity.fBufferSize * 2];
/*  620 */           System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  622 */           this.fCurrentEntity.ch = tmp;
/*  623 */           this.fCurrentEntity.fBufferSize *= 2;
/*      */         } else {
/*  625 */           System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */         }
/*      */ 
/*  628 */         offset = 0;
/*  629 */         if (load(length, false)) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*  634 */     int length = this.fCurrentEntity.position - offset;
/*  635 */     this.fCurrentEntity.columnNumber += length;
/*      */ 
/*  638 */     String symbol = null;
/*  639 */     if (length > 0) {
/*  640 */       symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
/*      */     }
/*      */ 
/*  647 */     return symbol;
/*      */   }
/*      */ 
/*      */   public String scanName()
/*      */     throws IOException
/*      */   {
/*  675 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  676 */       invokeListeners(0);
/*  677 */       load(0, true);
/*      */     }
/*      */ 
/*  681 */     int offset = this.fCurrentEntity.position;
/*  682 */     if (XMLChar.isNameStart(this.fCurrentEntity.ch[offset])) {
/*  683 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  684 */         invokeListeners(1);
/*  685 */         this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[offset];
/*  686 */         offset = 0;
/*  687 */         if (load(1, false)) {
/*  688 */           this.fCurrentEntity.columnNumber += 1;
/*  689 */           String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
/*      */ 
/*  696 */           return symbol;
/*      */         }
/*      */       }
/*  699 */       boolean vc = false;
/*      */       while (true)
/*      */       {
/*  702 */         char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  703 */         if (c < '')
/*  704 */           vc = VALID_NAMES[c];
/*      */         else {
/*  706 */           vc = XMLChar.isName(c);
/*      */         }
/*  708 */         if (!vc) break;
/*  709 */         if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  710 */           int length = this.fCurrentEntity.position - offset;
/*  711 */           invokeListeners(length);
/*  712 */           if (length == this.fCurrentEntity.fBufferSize)
/*      */           {
/*  714 */             char[] tmp = new char[this.fCurrentEntity.fBufferSize * 2];
/*  715 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  717 */             this.fCurrentEntity.ch = tmp;
/*  718 */             this.fCurrentEntity.fBufferSize *= 2;
/*      */           } else {
/*  720 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  723 */           offset = 0;
/*  724 */           if (load(length, false)) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  730 */     int length = this.fCurrentEntity.position - offset;
/*  731 */     this.fCurrentEntity.columnNumber += length;
/*      */     String symbol;
/*      */     String symbol;
/*  735 */     if (length > 0)
/*  736 */       symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
/*      */     else {
/*  738 */       symbol = null;
/*      */     }
/*      */ 
/*  744 */     return symbol;
/*      */   }
/*      */ 
/*      */   public boolean scanQName(QName qname)
/*      */     throws IOException
/*      */   {
/*  778 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  779 */       invokeListeners(0);
/*  780 */       load(0, true);
/*      */     }
/*      */ 
/*  784 */     int offset = this.fCurrentEntity.position;
/*      */ 
/*  790 */     if (XMLChar.isNameStart(this.fCurrentEntity.ch[offset])) {
/*  791 */       if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  792 */         invokeListeners(1);
/*  793 */         this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[offset];
/*  794 */         offset = 0;
/*      */ 
/*  796 */         if (load(1, false)) {
/*  797 */           this.fCurrentEntity.columnNumber += 1;
/*      */ 
/*  800 */           String name = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
/*  801 */           qname.setValues(null, name, name, null);
/*      */ 
/*  807 */           return true;
/*      */         }
/*      */       }
/*  810 */       int index = -1;
/*  811 */       boolean vc = false;
/*      */       while (true)
/*      */       {
/*  815 */         char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*  816 */         if (c < '')
/*  817 */           vc = VALID_NAMES[c];
/*      */         else {
/*  819 */           vc = XMLChar.isName(c);
/*      */         }
/*  821 */         if (!vc) break;
/*  822 */         if (c == ':') {
/*  823 */           if (index == -1)
/*      */           {
/*  826 */             index = this.fCurrentEntity.position;
/*      */           }
/*  828 */         } else if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  829 */           int length = this.fCurrentEntity.position - offset;
/*  830 */           invokeListeners(length);
/*  831 */           if (length == this.fCurrentEntity.fBufferSize)
/*      */           {
/*  833 */             char[] tmp = new char[this.fCurrentEntity.fBufferSize * 2];
/*  834 */             System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
/*      */ 
/*  836 */             this.fCurrentEntity.ch = tmp;
/*  837 */             this.fCurrentEntity.fBufferSize *= 2;
/*      */           } else {
/*  839 */             System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
/*      */           }
/*      */ 
/*  842 */           if (index != -1) {
/*  843 */             index -= offset;
/*      */           }
/*  845 */           offset = 0;
/*  846 */           if (load(length, false)) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*  851 */       int length = this.fCurrentEntity.position - offset;
/*  852 */       this.fCurrentEntity.columnNumber += length;
/*  853 */       if (length > 0) {
/*  854 */         String prefix = null;
/*  855 */         String localpart = null;
/*  856 */         String rawname = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
/*      */ 
/*  859 */         if (index != -1) {
/*  860 */           int prefixLength = index - offset;
/*  861 */           prefix = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, prefixLength);
/*      */ 
/*  863 */           int len = length - prefixLength - 1;
/*  864 */           localpart = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, index + 1, len);
/*      */         }
/*      */         else
/*      */         {
/*  868 */           localpart = rawname;
/*      */         }
/*  870 */         qname.setValues(prefix, localpart, rawname, null);
/*      */ 
/*  876 */         return true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  886 */     return false;
/*      */   }
/*      */ 
/*      */   public int scanContent(XMLString content)
/*      */     throws IOException
/*      */   {
/*  920 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  921 */       invokeListeners(0);
/*  922 */       load(0, true);
/*  923 */     } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/*  924 */       invokeListeners(0);
/*  925 */       this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[(this.fCurrentEntity.count - 1)];
/*  926 */       load(1, false);
/*  927 */       this.fCurrentEntity.position = 0;
/*      */     }
/*      */ 
/*  931 */     int offset = this.fCurrentEntity.position;
/*  932 */     int c = this.fCurrentEntity.ch[offset];
/*  933 */     int newlines = 0;
/*  934 */     if ((c == 10) || ((c == 13) && (this.isExternal)))
/*      */     {
/*      */       do
/*      */       {
/*  941 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/*  942 */         if ((c == 13) && (this.isExternal)) {
/*  943 */           newlines++;
/*  944 */           this.fCurrentEntity.lineNumber += 1;
/*  945 */           this.fCurrentEntity.columnNumber = 1;
/*  946 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  947 */             offset = 0;
/*  948 */             invokeListeners(newlines);
/*  949 */             this.fCurrentEntity.position = newlines;
/*  950 */             if (load(newlines, false)) {
/*      */               break;
/*      */             }
/*      */           }
/*  954 */           if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
/*  955 */             this.fCurrentEntity.position += 1;
/*  956 */             offset++;
/*      */           }
/*      */           else
/*      */           {
/*  960 */             newlines++;
/*      */           }
/*  962 */         } else if (c == 10) {
/*  963 */           newlines++;
/*  964 */           this.fCurrentEntity.lineNumber += 1;
/*  965 */           this.fCurrentEntity.columnNumber = 1;
/*  966 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/*  967 */             offset = 0;
/*  968 */             invokeListeners(newlines);
/*  969 */             this.fCurrentEntity.position = newlines;
/*  970 */             if (load(newlines, false))
/*  971 */               break;
/*      */           }
/*      */         }
/*      */         else {
/*  975 */           this.fCurrentEntity.position -= 1;
/*  976 */           break;
/*      */         }
/*      */       }
/*  978 */       while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
/*  979 */       for (int i = offset; i < this.fCurrentEntity.position; i++) {
/*  980 */         this.fCurrentEntity.ch[i] = '\n';
/*      */       }
/*  982 */       int length = this.fCurrentEntity.position - offset;
/*  983 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1)
/*      */       {
/*  986 */         content.setValues(this.fCurrentEntity.ch, offset, length);
/*      */ 
/*  993 */         return -1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1002 */     while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
/* 1003 */       c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1004 */       if (!XMLChar.isContent(c)) {
/* 1005 */         this.fCurrentEntity.position -= 1;
/*      */       }
/*      */     }
/*      */ 
/* 1009 */     int length = this.fCurrentEntity.position - offset;
/* 1010 */     this.fCurrentEntity.columnNumber += length - newlines;
/*      */ 
/* 1014 */     content.setValues(this.fCurrentEntity.ch, offset, length);
/*      */ 
/* 1017 */     if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
/* 1018 */       c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*      */ 
/* 1021 */       if ((c == 13) && (this.isExternal))
/* 1022 */         c = 10;
/*      */     }
/*      */     else {
/* 1025 */       c = -1;
/*      */     }
/*      */ 
/* 1032 */     return c;
/*      */   }
/*      */ 
/*      */   public int scanLiteral(int quote, XMLString content)
/*      */     throws IOException
/*      */   {
/* 1072 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1073 */       invokeListeners(0);
/* 1074 */       load(0, true);
/* 1075 */     } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/* 1076 */       invokeListeners(0);
/* 1077 */       this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[(this.fCurrentEntity.count - 1)];
/*      */ 
/* 1079 */       load(1, false);
/* 1080 */       this.fCurrentEntity.position = 0;
/*      */     }
/*      */ 
/* 1084 */     int offset = this.fCurrentEntity.position;
/* 1085 */     int c = this.fCurrentEntity.ch[offset];
/* 1086 */     int newlines = 0;
/* 1087 */     if (this.whiteSpaceInfoNeeded)
/* 1088 */       this.whiteSpaceLen = 0;
/* 1089 */     if ((c == 10) || ((c == 13) && (this.isExternal)))
/*      */     {
/*      */       do
/*      */       {
/* 1096 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1097 */         if ((c == 13) && (this.isExternal)) {
/* 1098 */           newlines++;
/* 1099 */           this.fCurrentEntity.lineNumber += 1;
/* 1100 */           this.fCurrentEntity.columnNumber = 1;
/* 1101 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1102 */             invokeListeners(newlines);
/* 1103 */             offset = 0;
/* 1104 */             this.fCurrentEntity.position = newlines;
/* 1105 */             if (load(newlines, false)) {
/*      */               break;
/*      */             }
/*      */           }
/* 1109 */           if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
/* 1110 */             this.fCurrentEntity.position += 1;
/* 1111 */             offset++;
/*      */           }
/*      */           else
/*      */           {
/* 1115 */             newlines++;
/*      */           }
/*      */         }
/* 1118 */         else if (c == 10) {
/* 1119 */           newlines++;
/* 1120 */           this.fCurrentEntity.lineNumber += 1;
/* 1121 */           this.fCurrentEntity.columnNumber = 1;
/* 1122 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1123 */             offset = 0;
/* 1124 */             invokeListeners(newlines);
/* 1125 */             this.fCurrentEntity.position = newlines;
/* 1126 */             if (load(newlines, false)) {
/* 1127 */               break;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1138 */           this.fCurrentEntity.position -= 1;
/* 1139 */           break;
/*      */         }
/*      */       }
/* 1141 */       while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
/* 1142 */       int i = 0;
/* 1143 */       for (i = offset; i < this.fCurrentEntity.position; i++) {
/* 1144 */         this.fCurrentEntity.ch[i] = '\n';
/* 1145 */         this.whiteSpaceLookup[(this.whiteSpaceLen++)] = i;
/*      */       }
/*      */ 
/* 1148 */       int length = this.fCurrentEntity.position - offset;
/* 1149 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/* 1150 */         content.setValues(this.fCurrentEntity.ch, offset, length);
/*      */ 
/* 1156 */         return -1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1166 */     while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
/* 1167 */       c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1168 */       if (((c == quote) && ((!this.fCurrentEntity.literal) || (this.isExternal))) || (c == 37) || (!XMLChar.isContent(c)))
/*      */       {
/* 1171 */         this.fCurrentEntity.position -= 1;
/* 1172 */         break;
/*      */       }
/* 1174 */       if ((this.whiteSpaceInfoNeeded) && (
/* 1175 */         (c == 32) || (c == 9))) {
/* 1176 */         if (this.whiteSpaceLen < this.whiteSpaceLookup.length) {
/* 1177 */           this.whiteSpaceLookup[(this.whiteSpaceLen++)] = (this.fCurrentEntity.position - 1);
/*      */         } else {
/* 1179 */           int[] tmp = new int[this.whiteSpaceLookup.length * 2];
/* 1180 */           System.arraycopy(this.whiteSpaceLookup, 0, tmp, 0, this.whiteSpaceLookup.length);
/* 1181 */           this.whiteSpaceLookup = tmp;
/* 1182 */           this.whiteSpaceLookup[(this.whiteSpaceLen++)] = (this.fCurrentEntity.position - 1);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1187 */     int length = this.fCurrentEntity.position - offset;
/* 1188 */     this.fCurrentEntity.columnNumber += length - newlines;
/* 1189 */     content.setValues(this.fCurrentEntity.ch, offset, length);
/*      */ 
/* 1192 */     if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
/* 1193 */       c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/*      */ 
/* 1197 */       if ((c == quote) && (this.fCurrentEntity.literal))
/* 1198 */         c = -1;
/*      */     }
/*      */     else {
/* 1201 */       c = -1;
/*      */     }
/*      */ 
/* 1208 */     return c;
/*      */   }
/*      */ 
/*      */   public boolean scanData(String delimiter, XMLStringBuffer buffer)
/*      */     throws IOException
/*      */   {
/* 1239 */     boolean done = false;
/* 1240 */     int delimLen = delimiter.length();
/* 1241 */     char charAt0 = delimiter.charAt(0);
/*      */     label981: 
/*      */     do
/*      */     {
/* 1251 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1252 */         load(0, true);
/*      */       }
/*      */ 
/* 1255 */       boolean bNextEntity = false;
/*      */ 
/* 1258 */       while ((this.fCurrentEntity.position > this.fCurrentEntity.count - delimLen) && (!bNextEntity))
/*      */       {
/* 1260 */         System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
/*      */ 
/* 1266 */         bNextEntity = load(this.fCurrentEntity.count - this.fCurrentEntity.position, false);
/* 1267 */         this.fCurrentEntity.position = 0;
/* 1268 */         this.fCurrentEntity.startPosition = 0;
/*      */       }
/*      */ 
/* 1271 */       if (this.fCurrentEntity.position > this.fCurrentEntity.count - delimLen)
/*      */       {
/* 1273 */         int length = this.fCurrentEntity.count - this.fCurrentEntity.position;
/* 1274 */         buffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, length);
/* 1275 */         this.fCurrentEntity.columnNumber += this.fCurrentEntity.count;
/* 1276 */         this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
/* 1277 */         this.fCurrentEntity.position = this.fCurrentEntity.count;
/* 1278 */         this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
/* 1279 */         load(0, true);
/* 1280 */         return false;
/*      */       }
/*      */ 
/* 1284 */       int offset = this.fCurrentEntity.position;
/* 1285 */       int c = this.fCurrentEntity.ch[offset];
/* 1286 */       int newlines = 0;
/* 1287 */       if ((c == 10) || ((c == 13) && (this.isExternal)))
/*      */       {
/*      */         do
/*      */         {
/* 1294 */           c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1295 */           if ((c == 13) && (this.isExternal)) {
/* 1296 */             newlines++;
/* 1297 */             this.fCurrentEntity.lineNumber += 1;
/* 1298 */             this.fCurrentEntity.columnNumber = 1;
/* 1299 */             if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1300 */               offset = 0;
/* 1301 */               invokeListeners(newlines);
/* 1302 */               this.fCurrentEntity.position = newlines;
/* 1303 */               if (load(newlines, false)) {
/*      */                 break;
/*      */               }
/*      */             }
/* 1307 */             if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
/* 1308 */               this.fCurrentEntity.position += 1;
/* 1309 */               offset++;
/*      */             }
/*      */             else
/*      */             {
/* 1313 */               newlines++;
/*      */             }
/* 1315 */           } else if (c == 10) {
/* 1316 */             newlines++;
/* 1317 */             this.fCurrentEntity.lineNumber += 1;
/* 1318 */             this.fCurrentEntity.columnNumber = 1;
/* 1319 */             if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1320 */               offset = 0;
/* 1321 */               invokeListeners(newlines);
/* 1322 */               this.fCurrentEntity.position = newlines;
/* 1323 */               this.fCurrentEntity.count = newlines;
/* 1324 */               if (load(newlines, false))
/* 1325 */                 break;
/*      */             }
/*      */           }
/*      */           else {
/* 1329 */             this.fCurrentEntity.position -= 1;
/* 1330 */             break;
/*      */           }
/*      */         }
/* 1332 */         while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
/* 1333 */         for (int i = offset; i < this.fCurrentEntity.position; i++) {
/* 1334 */           this.fCurrentEntity.ch[i] = '\n';
/*      */         }
/* 1336 */         int length = this.fCurrentEntity.position - offset;
/* 1337 */         if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/* 1338 */           buffer.append(this.fCurrentEntity.ch, offset, length);
/*      */ 
/* 1344 */           return true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1354 */       while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
/* 1355 */         c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1356 */         if (c == charAt0)
/*      */         {
/* 1358 */           int delimOffset = this.fCurrentEntity.position - 1;
/* 1359 */           for (int i = 1; i < delimLen; i++) {
/* 1360 */             if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1361 */               this.fCurrentEntity.position -= i;
/* 1362 */               break label981;
/*      */             }
/* 1364 */             c = this.fCurrentEntity.ch[(this.fCurrentEntity.position++)];
/* 1365 */             if (delimiter.charAt(i) != c) {
/* 1366 */               this.fCurrentEntity.position -= i;
/* 1367 */               break;
/*      */             }
/*      */           }
/* 1370 */           if (this.fCurrentEntity.position == delimOffset + delimLen) {
/* 1371 */             done = true;
/*      */           }
/*      */         }
/* 1374 */         else if ((c == 10) || ((this.isExternal) && (c == 13))) {
/* 1375 */           this.fCurrentEntity.position -= 1;
/*      */         }
/* 1377 */         else if (XMLChar.isInvalid(c)) {
/* 1378 */           this.fCurrentEntity.position -= 1;
/* 1379 */           int length = this.fCurrentEntity.position - offset;
/* 1380 */           this.fCurrentEntity.columnNumber += length - newlines;
/* 1381 */           buffer.append(this.fCurrentEntity.ch, offset, length);
/* 1382 */           return true;
/*      */         }
/*      */       }
/* 1385 */       int length = this.fCurrentEntity.position - offset;
/* 1386 */       this.fCurrentEntity.columnNumber += length - newlines;
/* 1387 */       if (done) {
/* 1388 */         length -= delimLen;
/*      */       }
/* 1390 */       buffer.append(this.fCurrentEntity.ch, offset, length);
/*      */     }
/*      */ 
/* 1398 */     while (!done);
/* 1399 */     return !done;
/*      */   }
/*      */ 
/*      */   public boolean skipChar(int c)
/*      */     throws IOException
/*      */   {
/* 1424 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1425 */       invokeListeners(0);
/* 1426 */       load(0, true);
/*      */     }
/*      */ 
/* 1430 */     int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/* 1431 */     if (cc == c) {
/* 1432 */       this.fCurrentEntity.position += 1;
/* 1433 */       if (c == 10) {
/* 1434 */         this.fCurrentEntity.lineNumber += 1;
/* 1435 */         this.fCurrentEntity.columnNumber = 1;
/*      */       } else {
/* 1437 */         this.fCurrentEntity.columnNumber += 1;
/*      */       }
/*      */ 
/* 1444 */       return true;
/* 1445 */     }if ((c == 10) && (cc == 13) && (this.isExternal))
/*      */     {
/* 1447 */       if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1448 */         invokeListeners(1);
/* 1449 */         this.fCurrentEntity.ch[0] = ((char)cc);
/* 1450 */         load(1, false);
/*      */       }
/* 1452 */       this.fCurrentEntity.position += 1;
/* 1453 */       if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
/* 1454 */         this.fCurrentEntity.position += 1;
/*      */       }
/* 1456 */       this.fCurrentEntity.lineNumber += 1;
/* 1457 */       this.fCurrentEntity.columnNumber = 1;
/*      */ 
/* 1463 */       return true;
/*      */     }
/*      */ 
/* 1472 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isSpace(char ch)
/*      */   {
/* 1477 */     return (ch == ' ') || (ch == '\n') || (ch == '\t') || (ch == '\r');
/*      */   }
/*      */ 
/*      */   public boolean skipSpaces()
/*      */     throws IOException
/*      */   {
/* 1500 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1501 */       invokeListeners(0);
/* 1502 */       load(0, true);
/*      */     }
/*      */ 
/* 1511 */     if (this.fCurrentEntity == null) {
/* 1512 */       return false;
/*      */     }
/*      */ 
/* 1516 */     int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/* 1517 */     if (XMLChar.isSpace(c)) {
/*      */       do {
/* 1519 */         boolean entityChanged = false;
/*      */ 
/* 1521 */         if ((c == 10) || ((this.isExternal) && (c == 13))) {
/* 1522 */           this.fCurrentEntity.lineNumber += 1;
/* 1523 */           this.fCurrentEntity.columnNumber = 1;
/* 1524 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/* 1525 */             invokeListeners(0);
/* 1526 */             this.fCurrentEntity.ch[0] = ((char)c);
/* 1527 */             entityChanged = load(1, true);
/* 1528 */             if (!entityChanged)
/*      */             {
/* 1531 */               this.fCurrentEntity.position = 0;
/* 1532 */             } else if (this.fCurrentEntity == null) {
/* 1533 */               return true;
/*      */             }
/*      */           }
/* 1536 */           if ((c == 13) && (this.isExternal))
/*      */           {
/* 1539 */             if (this.fCurrentEntity.ch[(++this.fCurrentEntity.position)] != '\n')
/* 1540 */               this.fCurrentEntity.position -= 1;
/*      */           }
/*      */         }
/*      */         else {
/* 1544 */           this.fCurrentEntity.columnNumber += 1;
/*      */         }
/*      */ 
/* 1547 */         if (!entityChanged) {
/* 1548 */           this.fCurrentEntity.position += 1;
/*      */         }
/*      */ 
/* 1551 */         if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1552 */           invokeListeners(0);
/* 1553 */           load(0, true);
/*      */ 
/* 1562 */           if (this.fCurrentEntity == null) {
/* 1563 */             return true;
/*      */           }
/*      */         }
/*      */       }
/* 1567 */       while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
/*      */ 
/* 1573 */       return true;
/*      */     }
/*      */ 
/* 1582 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean arrangeCapacity(int length)
/*      */     throws IOException
/*      */   {
/* 1593 */     return arrangeCapacity(length, false);
/*      */   }
/*      */ 
/*      */   public boolean arrangeCapacity(int length, boolean changeEntity)
/*      */     throws IOException
/*      */   {
/* 1608 */     if (this.fCurrentEntity.count - this.fCurrentEntity.position >= length) {
/* 1609 */       return true;
/*      */     }
/*      */ 
/* 1616 */     boolean entityChanged = false;
/*      */ 
/* 1618 */     while (this.fCurrentEntity.count - this.fCurrentEntity.position < length) {
/* 1619 */       if (this.fCurrentEntity.ch.length - this.fCurrentEntity.position < length) {
/* 1620 */         invokeListeners(0);
/* 1621 */         System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
/* 1622 */         this.fCurrentEntity.count -= this.fCurrentEntity.position;
/* 1623 */         this.fCurrentEntity.position = 0;
/*      */       }
/*      */ 
/* 1626 */       if (this.fCurrentEntity.count - this.fCurrentEntity.position < length) {
/* 1627 */         int pos = this.fCurrentEntity.position;
/* 1628 */         invokeListeners(pos);
/* 1629 */         entityChanged = load(this.fCurrentEntity.count, changeEntity);
/* 1630 */         this.fCurrentEntity.position = pos;
/* 1631 */         if (entityChanged)
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1642 */     if (this.fCurrentEntity.count - this.fCurrentEntity.position >= length) {
/* 1643 */       return true;
/*      */     }
/* 1645 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean skipString(String s)
/*      */     throws IOException
/*      */   {
/* 1664 */     int length = s.length();
/*      */ 
/* 1667 */     if (arrangeCapacity(length, false)) {
/* 1668 */       int beforeSkip = this.fCurrentEntity.position;
/* 1669 */       int afterSkip = this.fCurrentEntity.position + length - 1;
/*      */ 
/* 1676 */       int i = length - 1;
/*      */ 
/* 1678 */       while (s.charAt(i--) == this.fCurrentEntity.ch[afterSkip]) {
/* 1679 */         if (afterSkip-- == beforeSkip) {
/* 1680 */           this.fCurrentEntity.position += length;
/* 1681 */           this.fCurrentEntity.columnNumber += length;
/* 1682 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1687 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean skipString(char[] s) throws IOException
/*      */   {
/* 1692 */     int length = s.length;
/*      */ 
/* 1694 */     if (arrangeCapacity(length, false)) {
/* 1695 */       int beforeSkip = this.fCurrentEntity.position;
/* 1696 */       int afterSkip = this.fCurrentEntity.position + length;
/*      */ 
/* 1703 */       for (int i = 0; i < length; i++) {
/* 1704 */         if (this.fCurrentEntity.ch[(beforeSkip++)] != s[i]) {
/* 1705 */           return false;
/*      */         }
/*      */       }
/* 1708 */       this.fCurrentEntity.position += length;
/* 1709 */       this.fCurrentEntity.columnNumber += length;
/* 1710 */       return true;
/*      */     }
/*      */ 
/* 1714 */     return false;
/*      */   }
/*      */ 
/*      */   final boolean load(int offset, boolean changeEntity)
/*      */     throws IOException
/*      */   {
/* 1746 */     this.fCurrentEntity.fTotalCountTillLastLoad += this.fCurrentEntity.fLastCount;
/*      */ 
/* 1748 */     int length = this.fCurrentEntity.ch.length - offset;
/* 1749 */     if ((!this.fCurrentEntity.mayReadChunks) && (length > 64)) {
/* 1750 */       length = 64;
/*      */     }
/*      */ 
/* 1753 */     int count = this.fCurrentEntity.reader.read(this.fCurrentEntity.ch, offset, length);
/*      */ 
/* 1757 */     boolean entityChanged = false;
/* 1758 */     if (count != -1) {
/* 1759 */       if (count != 0)
/*      */       {
/* 1761 */         this.fCurrentEntity.fLastCount = count;
/* 1762 */         this.fCurrentEntity.count = (count + offset);
/* 1763 */         this.fCurrentEntity.position = offset;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1768 */       this.fCurrentEntity.count = offset;
/* 1769 */       this.fCurrentEntity.position = offset;
/* 1770 */       entityChanged = true;
/*      */ 
/* 1772 */       if (changeEntity)
/*      */       {
/* 1774 */         this.fEntityManager.endEntity();
/*      */ 
/* 1776 */         if (this.fCurrentEntity == null) {
/* 1777 */           throw END_OF_DOCUMENT_ENTITY;
/*      */         }
/*      */ 
/* 1780 */         if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 1781 */           load(0, true);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1792 */     return entityChanged;
/*      */   }
/*      */ 
/*      */   protected Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian)
/*      */     throws IOException
/*      */   {
/* 1816 */     if (encoding == null) {
/* 1817 */       encoding = "UTF-8";
/*      */     }
/*      */ 
/* 1821 */     String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
/* 1822 */     if (ENCODING.equals("UTF-8"))
/*      */     {
/* 1826 */       return new UTF8Reader(inputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
/*      */     }
/* 1828 */     if (ENCODING.equals("US-ASCII"))
/*      */     {
/* 1832 */       return new ASCIIReader(inputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
/*      */     }
/* 1834 */     if (ENCODING.equals("ISO-10646-UCS-4")) {
/* 1835 */       if (isBigEndian != null) {
/* 1836 */         boolean isBE = isBigEndian.booleanValue();
/* 1837 */         if (isBE) {
/* 1838 */           return new UCSReader(inputStream, (short)8);
/*      */         }
/* 1840 */         return new UCSReader(inputStream, (short)4);
/*      */       }
/*      */ 
/* 1843 */       this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
/*      */     }
/*      */ 
/* 1849 */     if (ENCODING.equals("ISO-10646-UCS-2")) {
/* 1850 */       if (isBigEndian != null) {
/* 1851 */         boolean isBE = isBigEndian.booleanValue();
/* 1852 */         if (isBE) {
/* 1853 */           return new UCSReader(inputStream, (short)2);
/*      */         }
/* 1855 */         return new UCSReader(inputStream, (short)1);
/*      */       }
/*      */ 
/* 1858 */       this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
/*      */     }
/*      */ 
/* 1866 */     boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
/* 1867 */     boolean validJava = XMLChar.isValidJavaEncoding(encoding);
/* 1868 */     if ((!validIANA) || ((this.fAllowJavaEncodings) && (!validJava))) {
/* 1869 */       this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
/*      */ 
/* 1881 */       encoding = "ISO-8859-1";
/*      */     }
/*      */ 
/* 1885 */     String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
/* 1886 */     if (javaEncoding == null) {
/* 1887 */       if (this.fAllowJavaEncodings) {
/* 1888 */         javaEncoding = encoding;
/*      */       } else {
/* 1890 */         this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
/*      */ 
/* 1895 */         javaEncoding = "ISO8859_1";
/*      */       }
/*      */     }
/* 1898 */     else if (javaEncoding.equals("ASCII"))
/*      */     {
/* 1902 */       return new ASCIIReader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
/*      */     }
/*      */ 
/* 1912 */     return new InputStreamReader(inputStream, javaEncoding);
/*      */   }
/*      */ 
/*      */   protected Object[] getEncodingName(byte[] b4, int count)
/*      */   {
/* 1928 */     if (count < 2) {
/* 1929 */       return new Object[] { "UTF-8", null };
/*      */     }
/*      */ 
/* 1933 */     int b0 = b4[0] & 0xFF;
/* 1934 */     int b1 = b4[1] & 0xFF;
/* 1935 */     if ((b0 == 254) && (b1 == 255))
/*      */     {
/* 1937 */       return new Object[] { "UTF-16BE", new Boolean(true) };
/*      */     }
/* 1939 */     if ((b0 == 255) && (b1 == 254))
/*      */     {
/* 1941 */       return new Object[] { "UTF-16LE", new Boolean(false) };
/*      */     }
/*      */ 
/* 1946 */     if (count < 3) {
/* 1947 */       return new Object[] { "UTF-8", null };
/*      */     }
/*      */ 
/* 1951 */     int b2 = b4[2] & 0xFF;
/* 1952 */     if ((b0 == 239) && (b1 == 187) && (b2 == 191)) {
/* 1953 */       return new Object[] { "UTF-8", null };
/*      */     }
/*      */ 
/* 1958 */     if (count < 4) {
/* 1959 */       return new Object[] { "UTF-8", null };
/*      */     }
/*      */ 
/* 1963 */     int b3 = b4[3] & 0xFF;
/* 1964 */     if ((b0 == 0) && (b1 == 0) && (b2 == 0) && (b3 == 60))
/*      */     {
/* 1966 */       return new Object[] { "ISO-10646-UCS-4", new Boolean(true) };
/*      */     }
/* 1968 */     if ((b0 == 60) && (b1 == 0) && (b2 == 0) && (b3 == 0))
/*      */     {
/* 1970 */       return new Object[] { "ISO-10646-UCS-4", new Boolean(false) };
/*      */     }
/* 1972 */     if ((b0 == 0) && (b1 == 0) && (b2 == 60) && (b3 == 0))
/*      */     {
/* 1975 */       return new Object[] { "ISO-10646-UCS-4", null };
/*      */     }
/* 1977 */     if ((b0 == 0) && (b1 == 60) && (b2 == 0) && (b3 == 0))
/*      */     {
/* 1980 */       return new Object[] { "ISO-10646-UCS-4", null };
/*      */     }
/* 1982 */     if ((b0 == 0) && (b1 == 60) && (b2 == 0) && (b3 == 63))
/*      */     {
/* 1986 */       return new Object[] { "UTF-16BE", new Boolean(true) };
/*      */     }
/* 1988 */     if ((b0 == 60) && (b1 == 0) && (b2 == 63) && (b3 == 0))
/*      */     {
/* 1991 */       return new Object[] { "UTF-16LE", new Boolean(false) };
/*      */     }
/* 1993 */     if ((b0 == 76) && (b1 == 111) && (b2 == 167) && (b3 == 148))
/*      */     {
/* 1996 */       return new Object[] { "CP037", null };
/*      */     }
/*      */ 
/* 2000 */     return new Object[] { "UTF-8", null };
/*      */   }
/*      */ 
/*      */   final void print()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void registerListener(XMLBufferListener listener)
/*      */   {
/* 2070 */     if (!this.listeners.contains(listener))
/* 2071 */       this.listeners.add(listener);
/*      */   }
/*      */ 
/*      */   private void invokeListeners(int loadPos)
/*      */   {
/* 2079 */     for (int i = 0; i < this.listeners.size(); i++) {
/* 2080 */       XMLBufferListener listener = (XMLBufferListener)this.listeners.get(i);
/* 2081 */       listener.refresh(loadPos);
/*      */     }
/*      */   }
/*      */ 
/*      */   public final boolean skipDeclSpaces()
/*      */     throws IOException
/*      */   {
/* 2110 */     if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
/* 2111 */       load(0, true);
/*      */     }
/*      */ 
/* 2115 */     int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
/* 2116 */     if (XMLChar.isSpace(c)) {
/* 2117 */       boolean external = this.fCurrentEntity.isExternal();
/*      */       do {
/* 2119 */         boolean entityChanged = false;
/*      */ 
/* 2121 */         if ((c == 10) || ((external) && (c == 13))) {
/* 2122 */           this.fCurrentEntity.lineNumber += 1;
/* 2123 */           this.fCurrentEntity.columnNumber = 1;
/* 2124 */           if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
/* 2125 */             this.fCurrentEntity.ch[0] = ((char)c);
/* 2126 */             entityChanged = load(1, true);
/* 2127 */             if (!entityChanged)
/*      */             {
/* 2130 */               this.fCurrentEntity.position = 0;
/*      */             }
/*      */           }
/* 2132 */           if ((c == 13) && (external))
/*      */           {
/* 2135 */             if (this.fCurrentEntity.ch[(++this.fCurrentEntity.position)] != '\n') {
/* 2136 */               this.fCurrentEntity.position -= 1;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 2148 */           this.fCurrentEntity.columnNumber += 1;
/*      */         }
/*      */ 
/* 2151 */         if (!entityChanged)
/* 2152 */           this.fCurrentEntity.position += 1;
/* 2153 */         if (this.fCurrentEntity.position == this.fCurrentEntity.count)
/* 2154 */           load(0, true);
/*      */       }
/* 2156 */       while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
/*      */ 
/* 2162 */       return true;
/*      */     }
/*      */ 
/* 2171 */     return false;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  125 */     for (int i = 65; i <= 90; i++) {
/*  126 */       VALID_NAMES[i] = true;
/*      */     }
/*  128 */     for (int i = 97; i <= 122; i++) {
/*  129 */       VALID_NAMES[i] = true;
/*      */     }
/*  131 */     for (int i = 48; i <= 57; i++) {
/*  132 */       VALID_NAMES[i] = true;
/*      */     }
/*  134 */     VALID_NAMES[45] = true;
/*  135 */     VALID_NAMES[46] = true;
/*  136 */     VALID_NAMES[58] = true;
/*  137 */     VALID_NAMES[95] = true;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.XMLEntityScanner
 * JD-Core Version:    0.6.2
 */