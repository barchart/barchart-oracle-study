/*      */ package com.sun.org.apache.xerces.internal.impl;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLSymbols;
/*      */ import com.sun.org.apache.xerces.internal.xni.Augmentations;
/*      */ import com.sun.org.apache.xerces.internal.xni.QName;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.xml.internal.stream.Entity.ScannedEntity;
/*      */ import com.sun.xml.internal.stream.XMLBufferListener;
/*      */ import com.sun.xml.internal.stream.XMLEntityStorage;
/*      */ import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ 
/*      */ public class XMLDocumentFragmentScannerImpl extends XMLScanner
/*      */   implements XMLDocumentScanner, XMLComponent, XMLEntityHandler, XMLBufferListener
/*      */ {
/*      */   protected int fElementAttributeLimit;
/*      */   protected ExternalSubsetResolver fExternalSubsetResolver;
/*      */   protected static final int SCANNER_STATE_START_OF_MARKUP = 21;
/*      */   protected static final int SCANNER_STATE_CONTENT = 22;
/*      */   protected static final int SCANNER_STATE_PI = 23;
/*      */   protected static final int SCANNER_STATE_DOCTYPE = 24;
/*      */   protected static final int SCANNER_STATE_XML_DECL = 25;
/*      */   protected static final int SCANNER_STATE_ROOT_ELEMENT = 26;
/*      */   protected static final int SCANNER_STATE_COMMENT = 27;
/*      */   protected static final int SCANNER_STATE_REFERENCE = 28;
/*      */   protected static final int SCANNER_STATE_ATTRIBUTE = 29;
/*      */   protected static final int SCANNER_STATE_ATTRIBUTE_VALUE = 30;
/*      */   protected static final int SCANNER_STATE_END_OF_INPUT = 33;
/*      */   protected static final int SCANNER_STATE_TERMINATED = 34;
/*      */   protected static final int SCANNER_STATE_CDATA = 35;
/*      */   protected static final int SCANNER_STATE_TEXT_DECL = 36;
/*      */   protected static final int SCANNER_STATE_CHARACTER_DATA = 37;
/*      */   protected static final int SCANNER_STATE_START_ELEMENT_TAG = 38;
/*      */   protected static final int SCANNER_STATE_END_ELEMENT_TAG = 39;
/*      */   protected static final int SCANNER_STATE_CHAR_REFERENCE = 40;
/*      */   protected static final int SCANNER_STATE_BUILT_IN_REFS = 41;
/*      */   protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
/*      */   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*  165 */   private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs", "report-cdata-event" };
/*      */ 
/*  174 */   private static final Boolean[] FEATURE_DEFAULTS = { Boolean.TRUE, null, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE };
/*      */ 
/*  183 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager" };
/*      */ 
/*  190 */   private static final Object[] PROPERTY_DEFAULTS = { null, null, null };
/*      */ 
/*  196 */   protected static final char[] cdata = { '[', 'C', 'D', 'A', 'T', 'A', '[' };
/*  197 */   protected static final char[] xmlDecl = { '<', '?', 'x', 'm', 'l' };
/*  198 */   protected static final char[] endTag = { '<', '/' };
/*      */   private static final boolean DEBUG_SCANNER_STATE = false;
/*      */   private static final boolean DEBUG_DISPATCHER = false;
/*      */   protected static final boolean DEBUG_START_END_ELEMENT = false;
/*      */   protected static final boolean DEBUG_NEXT = false;
/*      */   protected static final boolean DEBUG = false;
/*      */   protected static final boolean DEBUG_COALESCE = false;
/*      */   protected XMLDocumentHandler fDocumentHandler;
/*      */   protected int fScannerLastState;
/*      */   protected XMLEntityStorage fEntityStore;
/*  231 */   protected int[] fEntityStack = new int[4];
/*      */   protected int fMarkupDepth;
/*      */   protected boolean fEmptyElement;
/*  241 */   protected boolean fReadingAttributes = false;
/*      */   protected int fScannerState;
/*  247 */   protected boolean fInScanContent = false;
/*  248 */   protected boolean fLastSectionWasCData = false;
/*  249 */   protected boolean fLastSectionWasEntityReference = false;
/*  250 */   protected boolean fLastSectionWasCharacterData = false;
/*      */   protected boolean fHasExternalDTD;
/*      */   protected boolean fStandaloneSet;
/*      */   protected boolean fStandalone;
/*      */   protected String fVersion;
/*      */   protected QName fCurrentElement;
/*  266 */   protected ElementStack fElementStack = new ElementStack();
/*  267 */   protected ElementStack2 fElementStack2 = new ElementStack2();
/*      */   protected String fPITarget;
/*  279 */   protected XMLString fPIData = new XMLString();
/*      */ 
/*  285 */   protected boolean fNotifyBuiltInRefs = false;
/*      */ 
/*  289 */   protected boolean fSupportDTD = true;
/*  290 */   protected boolean fReplaceEntityReferences = true;
/*  291 */   protected boolean fSupportExternalEntities = false;
/*  292 */   protected boolean fReportCdataEvent = false;
/*  293 */   protected boolean fIsCoalesce = false;
/*  294 */   protected String fDeclaredEncoding = null;
/*      */ 
/*  296 */   protected boolean fDisallowDoctype = false;
/*      */   protected Driver fDriver;
/*  304 */   protected Driver fContentDriver = createContentDriver();
/*      */ 
/*  309 */   protected QName fElementQName = new QName();
/*      */ 
/*  312 */   protected QName fAttributeQName = new QName();
/*      */ 
/*  319 */   protected XMLAttributesIteratorImpl fAttributes = new XMLAttributesIteratorImpl();
/*      */ 
/*  323 */   protected XMLString fTempString = new XMLString();
/*      */ 
/*  326 */   protected XMLString fTempString2 = new XMLString();
/*      */ 
/*  329 */   private String[] fStrings = new String[3];
/*      */ 
/*  332 */   protected XMLStringBuffer fStringBuffer = new XMLStringBuffer();
/*      */ 
/*  335 */   protected XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
/*      */ 
/*  339 */   protected XMLStringBuffer fContentBuffer = new XMLStringBuffer();
/*      */ 
/*  342 */   private final char[] fSingleChar = new char[1];
/*  343 */   private String fCurrentEntityName = null;
/*      */ 
/*  346 */   protected boolean fScanToEnd = false;
/*      */ 
/*  348 */   protected DTDGrammarUtil dtdGrammarUtil = null;
/*      */ 
/*  350 */   protected boolean fAddDefaultAttr = false;
/*      */ 
/*  352 */   protected boolean foundBuiltInRefs = false;
/*      */ 
/*  354 */   protected SecurityManager fSecurityManager = null;
/*      */   static final short MAX_DEPTH_LIMIT = 5;
/*      */   static final short ELEMENT_ARRAY_LENGTH = 200;
/*      */   static final short MAX_POINTER_AT_A_DEPTH = 4;
/*      */   static final boolean DEBUG_SKIP_ALGORITHM = false;
/*  362 */   String[] fElementArray = new String['È'];
/*      */ 
/*  364 */   short fLastPointerLocation = 0;
/*  365 */   short fElementPointer = 0;
/*      */ 
/*  367 */   short[][] fPointerInfo = new short[5][4];
/*      */   protected String fElementRawname;
/*  369 */   protected boolean fShouldSkip = false;
/*  370 */   protected boolean fAdd = false;
/*  371 */   protected boolean fSkip = false;
/*      */ 
/*  374 */   private Augmentations fTempAugmentations = null;
/*      */   protected boolean fUsebuffer;
/*      */ 
/*      */   public void setInputSource(XMLInputSource inputSource)
/*      */     throws IOException
/*      */   {
/*  395 */     this.fEntityManager.setEntityHandler(this);
/*  396 */     this.fEntityManager.startEntity("$fragment$", inputSource, false, true);
/*      */   }
/*      */ 
/*      */   public boolean scanDocument(boolean complete)
/*      */     throws IOException, XNIException
/*      */   {
/*  428 */     this.fEntityManager.setEntityHandler(this);
/*      */ 
/*  431 */     int event = next();
/*      */     do {
/*  433 */       switch (event)
/*      */       {
/*      */       case 7:
/*  436 */         break;
/*      */       case 1:
/*  440 */         break;
/*      */       case 4:
/*  442 */         this.fDocumentHandler.characters(getCharacterData(), null);
/*  443 */         break;
/*      */       case 6:
/*  448 */         break;
/*      */       case 9:
/*  451 */         break;
/*      */       case 3:
/*  453 */         this.fDocumentHandler.processingInstruction(getPITarget(), getPIData(), null);
/*  454 */         break;
/*      */       case 5:
/*  457 */         this.fDocumentHandler.comment(getCharacterData(), null);
/*  458 */         break;
/*      */       case 11:
/*  463 */         break;
/*      */       case 12:
/*  465 */         this.fDocumentHandler.startCDATA(null);
/*      */ 
/*  467 */         this.fDocumentHandler.characters(getCharacterData(), null);
/*  468 */         this.fDocumentHandler.endCDATA(null);
/*      */ 
/*  470 */         break;
/*      */       case 14:
/*  472 */         break;
/*      */       case 15:
/*  474 */         break;
/*      */       case 13:
/*  476 */         break;
/*      */       case 10:
/*  478 */         break;
/*      */       case 2:
/*  483 */         break;
/*      */       case 8:
/*      */       default:
/*  485 */         throw new InternalError("processing event: " + event);
/*      */       }
/*      */ 
/*  489 */       event = next();
/*      */     }
/*  491 */     while ((event != 8) && (complete));
/*      */ 
/*  493 */     if (event == 8) {
/*  494 */       this.fDocumentHandler.endDocument(null);
/*  495 */       return false;
/*      */     }
/*      */ 
/*  498 */     return true;
/*      */   }
/*      */ 
/*      */   public QName getElementQName()
/*      */   {
/*  505 */     if (this.fScannerLastState == 2) {
/*  506 */       this.fElementQName.setValues(this.fElementStack.getLastPoppedElement());
/*      */     }
/*  508 */     return this.fElementQName;
/*      */   }
/*      */ 
/*      */   public int next()
/*      */     throws IOException, XNIException
/*      */   {
/*  516 */     return this.fDriver.next();
/*      */   }
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */     throws XMLConfigurationException
/*      */   {
/*  541 */     super.reset(componentManager);
/*      */ 
/*  550 */     this.fReportCdataEvent = componentManager.getFeature("report-cdata-event", true);
/*      */ 
/*  552 */     this.fSecurityManager = ((SecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager", null));
/*  553 */     this.fElementAttributeLimit = (this.fSecurityManager != null ? this.fSecurityManager.getElementAttrLimit() : 0);
/*      */ 
/*  555 */     this.fNotifyBuiltInRefs = componentManager.getFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", false);
/*      */ 
/*  557 */     Object resolver = componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null);
/*  558 */     this.fExternalSubsetResolver = ((resolver instanceof ExternalSubsetResolver) ? (ExternalSubsetResolver)resolver : null);
/*      */ 
/*  562 */     this.fMarkupDepth = 0;
/*  563 */     this.fCurrentElement = null;
/*  564 */     this.fElementStack.clear();
/*  565 */     this.fHasExternalDTD = false;
/*  566 */     this.fStandaloneSet = false;
/*  567 */     this.fStandalone = false;
/*  568 */     this.fInScanContent = false;
/*      */ 
/*  570 */     this.fShouldSkip = false;
/*  571 */     this.fAdd = false;
/*  572 */     this.fSkip = false;
/*      */ 
/*  575 */     this.fReadingAttributes = false;
/*      */ 
/*  578 */     this.fSupportExternalEntities = true;
/*  579 */     this.fReplaceEntityReferences = true;
/*  580 */     this.fIsCoalesce = false;
/*      */ 
/*  583 */     setScannerState(22);
/*  584 */     setDriver(this.fContentDriver);
/*  585 */     this.fEntityStore = this.fEntityManager.getEntityStore();
/*      */ 
/*  587 */     this.dtdGrammarUtil = null;
/*      */   }
/*      */ 
/*      */   public void reset(PropertyManager propertyManager)
/*      */   {
/*  596 */     super.reset(propertyManager);
/*      */ 
/*  600 */     this.fNamespaces = ((Boolean)propertyManager.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue();
/*  601 */     this.fNotifyBuiltInRefs = false;
/*      */ 
/*  604 */     this.fMarkupDepth = 0;
/*  605 */     this.fCurrentElement = null;
/*  606 */     this.fShouldSkip = false;
/*  607 */     this.fAdd = false;
/*  608 */     this.fSkip = false;
/*  609 */     this.fElementStack.clear();
/*      */ 
/*  611 */     this.fHasExternalDTD = false;
/*  612 */     this.fStandaloneSet = false;
/*  613 */     this.fStandalone = false;
/*      */ 
/*  616 */     Boolean bo = (Boolean)propertyManager.getProperty("javax.xml.stream.isReplacingEntityReferences");
/*  617 */     this.fReplaceEntityReferences = bo.booleanValue();
/*  618 */     bo = (Boolean)propertyManager.getProperty("javax.xml.stream.isSupportingExternalEntities");
/*  619 */     this.fSupportExternalEntities = bo.booleanValue();
/*  620 */     Boolean cdata = (Boolean)propertyManager.getProperty("http://java.sun.com/xml/stream/properties/report-cdata-event");
/*  621 */     if (cdata != null)
/*  622 */       this.fReportCdataEvent = cdata.booleanValue();
/*  623 */     Boolean coalesce = (Boolean)propertyManager.getProperty("javax.xml.stream.isCoalescing");
/*  624 */     if (coalesce != null)
/*  625 */       this.fIsCoalesce = coalesce.booleanValue();
/*  626 */     this.fReportCdataEvent = (!this.fIsCoalesce);
/*      */ 
/*  629 */     this.fReplaceEntityReferences = (this.fIsCoalesce ? true : this.fReplaceEntityReferences);
/*      */ 
/*  634 */     this.fEntityStore = this.fEntityManager.getEntityStore();
/*      */ 
/*  637 */     this.dtdGrammarUtil = null;
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedFeatures()
/*      */   {
/*  647 */     return (String[])RECOGNIZED_FEATURES.clone();
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean state)
/*      */     throws XMLConfigurationException
/*      */   {
/*  668 */     super.setFeature(featureId, state);
/*      */ 
/*  671 */     if (featureId.startsWith("http://apache.org/xml/features/")) {
/*  672 */       String feature = featureId.substring("http://apache.org/xml/features/".length());
/*  673 */       if (feature.equals("scanner/notify-builtin-refs"))
/*  674 */         this.fNotifyBuiltInRefs = state;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedProperties()
/*      */   {
/*  686 */     return (String[])RECOGNIZED_PROPERTIES.clone();
/*      */   }
/*      */ 
/*      */   public void setProperty(String propertyId, Object value)
/*      */     throws XMLConfigurationException
/*      */   {
/*  707 */     super.setProperty(propertyId, value);
/*      */ 
/*  710 */     if (propertyId.startsWith("http://apache.org/xml/properties/")) {
/*  711 */       int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
/*  712 */       if ((suffixLength == "internal/entity-manager".length()) && (propertyId.endsWith("internal/entity-manager")))
/*      */       {
/*  714 */         this.fEntityManager = ((XMLEntityManager)value);
/*  715 */         return;
/*      */       }
/*  717 */       if ((suffixLength == "internal/entity-resolver".length()) && (propertyId.endsWith("internal/entity-resolver")))
/*      */       {
/*  719 */         this.fExternalSubsetResolver = ((value instanceof ExternalSubsetResolver) ? (ExternalSubsetResolver)value : null);
/*      */ 
/*  721 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  727 */     if (propertyId.startsWith("http://apache.org/xml/properties/")) {
/*  728 */       String property = propertyId.substring("http://apache.org/xml/properties/".length());
/*  729 */       if (property.equals("internal/entity-manager")) {
/*  730 */         this.fEntityManager = ((XMLEntityManager)value);
/*      */       }
/*  732 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Boolean getFeatureDefault(String featureId)
/*      */   {
/*  747 */     for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
/*  748 */       if (RECOGNIZED_FEATURES[i].equals(featureId)) {
/*  749 */         return FEATURE_DEFAULTS[i];
/*      */       }
/*      */     }
/*  752 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getPropertyDefault(String propertyId)
/*      */   {
/*  765 */     for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
/*  766 */       if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
/*  767 */         return PROPERTY_DEFAULTS[i];
/*      */       }
/*      */     }
/*  770 */     return null;
/*      */   }
/*      */ 
/*      */   public void setDocumentHandler(XMLDocumentHandler documentHandler)
/*      */   {
/*  783 */     this.fDocumentHandler = documentHandler;
/*      */   }
/*      */ 
/*      */   public XMLDocumentHandler getDocumentHandler()
/*      */   {
/*  790 */     return this.fDocumentHandler;
/*      */   }
/*      */ 
/*      */   public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  817 */     if (this.fEntityDepth == this.fEntityStack.length) {
/*  818 */       int[] entityarray = new int[this.fEntityStack.length * 2];
/*  819 */       System.arraycopy(this.fEntityStack, 0, entityarray, 0, this.fEntityStack.length);
/*  820 */       this.fEntityStack = entityarray;
/*      */     }
/*  822 */     this.fEntityStack[this.fEntityDepth] = this.fMarkupDepth;
/*      */ 
/*  824 */     super.startEntity(name, identifier, encoding, augs);
/*      */ 
/*  827 */     if ((this.fStandalone) && (this.fEntityStore.isEntityDeclInExternalSubset(name))) {
/*  828 */       reportFatalError("MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { name });
/*      */     }
/*      */ 
/*  834 */     if ((this.fDocumentHandler != null) && (!this.fScanningAttribute) && 
/*  835 */       (!name.equals("[xml]")))
/*  836 */       this.fDocumentHandler.startGeneralEntity(name, identifier, encoding, null);
/*      */   }
/*      */ 
/*      */   public void endEntity(String name, Augmentations augs)
/*      */     throws IOException, XNIException
/*      */   {
/*  861 */     super.endEntity(name, augs);
/*      */ 
/*  864 */     if (this.fMarkupDepth != this.fEntityStack[this.fEntityDepth]) {
/*  865 */       reportFatalError("MarkupEntityMismatch", null);
/*      */     }
/*      */ 
/*  870 */     if ((this.fDocumentHandler != null) && (!this.fScanningAttribute) && 
/*  871 */       (!name.equals("[xml]")))
/*  872 */       this.fDocumentHandler.endGeneralEntity(name, null);
/*      */   }
/*      */ 
/*      */   protected Driver createContentDriver()
/*      */   {
/*  887 */     return new FragmentContentDriver();
/*      */   }
/*      */ 
/*      */   protected void scanXMLDeclOrTextDecl(boolean scanningTextDecl)
/*      */     throws IOException, XNIException
/*      */   {
/*  914 */     super.scanXMLDeclOrTextDecl(scanningTextDecl, this.fStrings);
/*  915 */     this.fMarkupDepth -= 1;
/*      */ 
/*  918 */     String version = this.fStrings[0];
/*  919 */     String encoding = this.fStrings[1];
/*  920 */     String standalone = this.fStrings[2];
/*  921 */     this.fDeclaredEncoding = encoding;
/*      */ 
/*  923 */     this.fStandaloneSet = (standalone != null);
/*  924 */     this.fStandalone = ((this.fStandaloneSet) && (standalone.equals("yes")));
/*      */ 
/*  927 */     this.fEntityManager.setStandalone(this.fStandalone);
/*      */ 
/*  931 */     if (this.fDocumentHandler != null) {
/*  932 */       if (scanningTextDecl)
/*  933 */         this.fDocumentHandler.textDecl(version, encoding, null);
/*      */       else {
/*  935 */         this.fDocumentHandler.xmlDecl(version, encoding, standalone, null);
/*      */       }
/*      */     }
/*      */ 
/*  939 */     if (version != null) {
/*  940 */       this.fEntityScanner.setVersion(version);
/*  941 */       this.fEntityScanner.setXMLVersion(version);
/*      */     }
/*      */ 
/*  944 */     if ((encoding != null) && (!this.fEntityScanner.getCurrentEntity().isEncodingExternallySpecified()))
/*  945 */       this.fEntityScanner.setEncoding(encoding);
/*      */   }
/*      */ 
/*      */   public String getPITarget()
/*      */   {
/*  951 */     return this.fPITarget;
/*      */   }
/*      */ 
/*      */   public XMLStringBuffer getPIData() {
/*  955 */     return this.fContentBuffer;
/*      */   }
/*      */ 
/*      */   public XMLString getCharacterData()
/*      */   {
/*  960 */     if (this.fUsebuffer) {
/*  961 */       return this.fContentBuffer;
/*      */     }
/*  963 */     return this.fTempString;
/*      */   }
/*      */ 
/*      */   protected void scanPIData(String target, XMLStringBuffer data)
/*      */     throws IOException, XNIException
/*      */   {
/*  980 */     super.scanPIData(target, data);
/*      */ 
/*  983 */     this.fPITarget = target;
/*      */ 
/*  985 */     this.fMarkupDepth -= 1;
/*      */   }
/*      */ 
/*      */   protected void scanComment()
/*      */     throws IOException, XNIException
/*      */   {
/*  999 */     this.fContentBuffer.clear();
/* 1000 */     scanComment(this.fContentBuffer);
/*      */ 
/* 1002 */     this.fUsebuffer = true;
/* 1003 */     this.fMarkupDepth -= 1;
/*      */   }
/*      */ 
/*      */   public String getComment()
/*      */   {
/* 1009 */     return this.fContentBuffer.toString();
/*      */   }
/*      */ 
/*      */   void addElement(String rawname) {
/* 1013 */     if (this.fElementPointer < 200)
/*      */     {
/* 1015 */       this.fElementArray[this.fElementPointer] = rawname;
/*      */ 
/* 1028 */       if (this.fElementStack.fDepth < 5) {
/* 1029 */         short column = storePointerForADepth(this.fElementPointer);
/* 1030 */         if (column > 0) {
/* 1031 */           short pointer = getElementPointer((short)this.fElementStack.fDepth, (short)(column - 1));
/*      */ 
/* 1034 */           if (rawname == this.fElementArray[pointer]) {
/* 1035 */             this.fShouldSkip = true;
/* 1036 */             this.fLastPointerLocation = pointer;
/*      */ 
/* 1038 */             resetPointer((short)this.fElementStack.fDepth, column);
/* 1039 */             this.fElementArray[this.fElementPointer] = null;
/* 1040 */             return;
/*      */           }
/* 1042 */           this.fShouldSkip = false;
/*      */         }
/*      */       }
/*      */ 
/* 1046 */       this.fElementPointer = ((short)(this.fElementPointer + 1));
/*      */     }
/*      */   }
/*      */ 
/*      */   void resetPointer(short depth, short column)
/*      */   {
/* 1052 */     this.fPointerInfo[depth][column] = 0;
/*      */   }
/*      */ 
/*      */   short storePointerForADepth(short elementPointer)
/*      */   {
/* 1057 */     short depth = (short)this.fElementStack.fDepth;
/*      */ 
/* 1061 */     for (short i = 0; i < 4; i = (short)(i + 1))
/*      */     {
/* 1063 */       if (canStore(depth, i)) {
/* 1064 */         this.fPointerInfo[depth][i] = elementPointer;
/*      */ 
/* 1073 */         return i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1078 */     return -1;
/*      */   }
/*      */ 
/*      */   boolean canStore(short depth, short column)
/*      */   {
/* 1085 */     return this.fPointerInfo[depth][column] == 0;
/*      */   }
/*      */ 
/*      */   short getElementPointer(short depth, short column)
/*      */   {
/* 1093 */     return this.fPointerInfo[depth][column];
/*      */   }
/*      */ 
/*      */   boolean skipFromTheBuffer(String rawname)
/*      */     throws IOException
/*      */   {
/* 1099 */     if (this.fEntityScanner.skipString(rawname)) {
/* 1100 */       char c = (char)this.fEntityScanner.peekChar();
/*      */ 
/* 1103 */       if ((c == ' ') || (c == '/') || (c == '>')) {
/* 1104 */         this.fElementRawname = rawname;
/* 1105 */         return true;
/*      */       }
/* 1107 */       return false;
/*      */     }
/*      */ 
/* 1110 */     return false;
/*      */   }
/*      */ 
/*      */   boolean skipQElement(String rawname) throws IOException
/*      */   {
/* 1115 */     int c = this.fEntityScanner.getChar(rawname.length());
/*      */ 
/* 1117 */     if (XMLChar.isName(c)) {
/* 1118 */       return false;
/*      */     }
/* 1120 */     return this.fEntityScanner.skipString(rawname);
/*      */   }
/*      */ 
/*      */   protected boolean skipElement()
/*      */     throws IOException
/*      */   {
/* 1126 */     if (!this.fShouldSkip) return false;
/*      */ 
/* 1128 */     if (this.fLastPointerLocation != 0)
/*      */     {
/* 1130 */       String rawname = this.fElementArray[(this.fLastPointerLocation + 1)];
/* 1131 */       if ((rawname != null) && (skipFromTheBuffer(rawname))) {
/* 1132 */         this.fLastPointerLocation = ((short)(this.fLastPointerLocation + 1));
/*      */ 
/* 1136 */         return true;
/*      */       }
/*      */ 
/* 1139 */       this.fLastPointerLocation = 0;
/*      */     }
/*      */ 
/* 1147 */     return (this.fShouldSkip) && (skipElement((short)0));
/*      */   }
/*      */ 
/*      */   boolean skipElement(short column)
/*      */     throws IOException
/*      */   {
/* 1153 */     short depth = (short)this.fElementStack.fDepth;
/*      */ 
/* 1155 */     if (depth > 5) {
/* 1156 */       return this.fShouldSkip = 0;
/*      */     }
/* 1158 */     for (short i = column; i < 4; i = (short)(i + 1)) {
/* 1159 */       short pointer = getElementPointer(depth, i);
/*      */ 
/* 1161 */       if (pointer == 0) {
/* 1162 */         return this.fShouldSkip = 0;
/*      */       }
/*      */ 
/* 1165 */       if ((this.fElementArray[pointer] != null) && (skipFromTheBuffer(this.fElementArray[pointer])))
/*      */       {
/* 1171 */         this.fLastPointerLocation = pointer;
/* 1172 */         return this.fShouldSkip = 1;
/*      */       }
/*      */     }
/* 1175 */     return this.fShouldSkip = 0;
/*      */   }
/*      */ 
/*      */   protected boolean scanStartElement()
/*      */     throws IOException, XNIException
/*      */   {
/* 1207 */     if ((this.fSkip) && (!this.fAdd))
/*      */     {
/* 1211 */       QName name = this.fElementStack.getNext();
/*      */ 
/* 1218 */       this.fSkip = this.fEntityScanner.skipString(name.rawname);
/*      */ 
/* 1220 */       if (this.fSkip)
/*      */       {
/* 1224 */         this.fElementStack.push();
/* 1225 */         this.fElementQName = name;
/*      */       }
/*      */       else {
/* 1228 */         this.fElementStack.reposition();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1238 */     if ((!this.fSkip) || (this.fAdd))
/*      */     {
/* 1240 */       this.fElementQName = this.fElementStack.nextElement();
/*      */ 
/* 1242 */       if (this.fNamespaces) {
/* 1243 */         this.fEntityScanner.scanQName(this.fElementQName);
/*      */       } else {
/* 1245 */         String name = this.fEntityScanner.scanName();
/* 1246 */         this.fElementQName.setValues(null, name, name, null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1259 */     if (this.fAdd)
/*      */     {
/* 1261 */       this.fElementStack.matchElement(this.fElementQName);
/*      */     }
/*      */ 
/* 1266 */     this.fCurrentElement = this.fElementQName;
/*      */ 
/* 1268 */     String rawname = this.fElementQName.rawname;
/*      */ 
/* 1270 */     this.fEmptyElement = false;
/*      */ 
/* 1272 */     this.fAttributes.removeAllAttributes();
/*      */ 
/* 1274 */     if (!seekCloseOfStartTag()) {
/* 1275 */       this.fReadingAttributes = true;
/* 1276 */       this.fAttributeCacheUsedCount = 0;
/* 1277 */       this.fStringBufferIndex = 0;
/* 1278 */       this.fAddDefaultAttr = true;
/*      */       do {
/* 1280 */         scanAttribute(this.fAttributes);
/* 1281 */         if ((this.fSecurityManager != null) && (this.fAttributes.getLength() > this.fElementAttributeLimit)) {
/* 1282 */           this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { rawname, new Integer(this.fAttributes.getLength()) }, (short)2);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1288 */       while (!seekCloseOfStartTag());
/* 1289 */       this.fReadingAttributes = false;
/*      */     }
/*      */ 
/* 1292 */     if (this.fEmptyElement)
/*      */     {
/* 1294 */       this.fMarkupDepth -= 1;
/*      */ 
/* 1297 */       if (this.fMarkupDepth < this.fEntityStack[(this.fEntityDepth - 1)]) {
/* 1298 */         reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
/*      */       }
/*      */ 
/* 1302 */       if (this.fDocumentHandler != null) {
/* 1303 */         this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
/*      */       }
/*      */ 
/* 1313 */       this.fElementStack.popElement();
/*      */     }
/*      */     else
/*      */     {
/* 1317 */       if (this.dtdGrammarUtil != null)
/* 1318 */         this.dtdGrammarUtil.startElement(this.fElementQName, this.fAttributes);
/* 1319 */       if (this.fDocumentHandler != null)
/*      */       {
/* 1323 */         this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1329 */     return this.fEmptyElement;
/*      */   }
/*      */ 
/*      */   protected boolean seekCloseOfStartTag()
/*      */     throws IOException, XNIException
/*      */   {
/* 1339 */     boolean sawSpace = this.fEntityScanner.skipSpaces();
/*      */ 
/* 1342 */     int c = this.fEntityScanner.peekChar();
/* 1343 */     if (c == 62) {
/* 1344 */       this.fEntityScanner.scanChar();
/* 1345 */       return true;
/* 1346 */     }if (c == 47) {
/* 1347 */       this.fEntityScanner.scanChar();
/* 1348 */       if (!this.fEntityScanner.skipChar(62)) {
/* 1349 */         reportFatalError("ElementUnterminated", new Object[] { this.fElementQName.rawname });
/*      */       }
/*      */ 
/* 1352 */       this.fEmptyElement = true;
/* 1353 */       return true;
/* 1354 */     }if ((!isValidNameStartChar(c)) || (!sawSpace)) {
/* 1355 */       reportFatalError("ElementUnterminated", new Object[] { this.fElementQName.rawname });
/*      */     }
/*      */ 
/* 1358 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean hasAttributes() {
/* 1362 */     return this.fAttributes.getLength() > 0;
/*      */   }
/*      */ 
/*      */   public XMLAttributesIteratorImpl getAttributeIterator()
/*      */   {
/* 1442 */     if ((this.dtdGrammarUtil != null) && (this.fAddDefaultAttr)) {
/* 1443 */       this.dtdGrammarUtil.addDTDDefaultAttrs(this.fElementQName, this.fAttributes);
/* 1444 */       this.fAddDefaultAttr = false;
/*      */     }
/* 1446 */     return this.fAttributes;
/*      */   }
/*      */ 
/*      */   public boolean standaloneSet()
/*      */   {
/* 1451 */     return this.fStandaloneSet;
/*      */   }
/*      */ 
/*      */   public boolean isStandAlone() {
/* 1455 */     return this.fStandalone;
/*      */   }
/*      */ 
/*      */   protected void scanAttribute(XMLAttributes attributes)
/*      */     throws IOException, XNIException
/*      */   {
/* 1480 */     if (this.fNamespaces) {
/* 1481 */       this.fEntityScanner.scanQName(this.fAttributeQName);
/*      */     } else {
/* 1483 */       String name = this.fEntityScanner.scanName();
/* 1484 */       this.fAttributeQName.setValues(null, name, name, null);
/*      */     }
/*      */ 
/* 1488 */     this.fEntityScanner.skipSpaces();
/* 1489 */     if (!this.fEntityScanner.skipChar(61)) {
/* 1490 */       reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
/*      */     }
/*      */ 
/* 1493 */     this.fEntityScanner.skipSpaces();
/*      */ 
/* 1495 */     int attIndex = 0;
/*      */ 
/* 1497 */     boolean isVC = (this.fHasExternalDTD) && (!this.fStandalone);
/*      */ 
/* 1505 */     XMLString tmpStr = getString();
/*      */ 
/* 1507 */     scanAttributeValue(tmpStr, this.fTempString2, this.fAttributeQName.rawname, attributes, attIndex, isVC);
/*      */ 
/* 1512 */     int oldLen = attributes.getLength();
/*      */ 
/* 1514 */     attIndex = attributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
/*      */ 
/* 1519 */     if (oldLen == attributes.getLength()) {
/* 1520 */       reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
/*      */     }
/*      */ 
/* 1527 */     attributes.setValue(attIndex, null, tmpStr);
/*      */ 
/* 1531 */     attributes.setSpecified(attIndex, true);
/*      */   }
/*      */ 
/*      */   protected int scanContent(XMLStringBuffer content)
/*      */     throws IOException, XNIException
/*      */   {
/* 1550 */     this.fTempString.length = 0;
/* 1551 */     int c = this.fEntityScanner.scanContent(this.fTempString);
/* 1552 */     content.append(this.fTempString);
/* 1553 */     this.fTempString.length = 0;
/* 1554 */     if (c == 13)
/*      */     {
/* 1557 */       this.fEntityScanner.scanChar();
/* 1558 */       content.append((char)c);
/* 1559 */       c = -1;
/* 1560 */     } else if (c == 93)
/*      */     {
/* 1563 */       content.append((char)this.fEntityScanner.scanChar());
/*      */ 
/* 1567 */       this.fInScanContent = true;
/*      */ 
/* 1572 */       if (this.fEntityScanner.skipChar(93)) {
/* 1573 */         content.append(']');
/* 1574 */         while (this.fEntityScanner.skipChar(93)) {
/* 1575 */           content.append(']');
/*      */         }
/* 1577 */         if (this.fEntityScanner.skipChar(62)) {
/* 1578 */           reportFatalError("CDEndInContent", null);
/*      */         }
/*      */       }
/* 1581 */       this.fInScanContent = false;
/* 1582 */       c = -1;
/*      */     }
/* 1584 */     if ((this.fDocumentHandler != null) && (content.length > 0));
/* 1587 */     return c;
/*      */   }
/*      */ 
/*      */   protected boolean scanCDATASection(XMLStringBuffer contentBuffer, boolean complete)
/*      */     throws IOException, XNIException
/*      */   {
/* 1608 */     if (this.fDocumentHandler != null);
/* 1614 */     while (this.fEntityScanner.scanData("]]>", contentBuffer))
/*      */     {
/* 1643 */       int c = this.fEntityScanner.peekChar();
/* 1644 */       if ((c != -1) && (isInvalidLiteral(c))) {
/* 1645 */         if (XMLChar.isHighSurrogate(c))
/*      */         {
/* 1648 */           scanSurrogates(contentBuffer);
/*      */         } else {
/* 1650 */           reportFatalError("InvalidCharInCDSect", new Object[] { Integer.toString(c, 16) });
/*      */ 
/* 1652 */           this.fEntityScanner.scanChar();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1656 */       if (this.fDocumentHandler == null);
/*      */     }
/*      */ 
/* 1661 */     this.fMarkupDepth -= 1;
/*      */ 
/* 1663 */     if (((this.fDocumentHandler == null) || (contentBuffer.length <= 0)) || 
/* 1668 */       (this.fDocumentHandler != null));
/* 1672 */     return true;
/*      */   }
/*      */ 
/*      */   protected int scanEndElement()
/*      */     throws IOException, XNIException
/*      */   {
/* 1694 */     QName endElementName = this.fElementStack.popElement();
/*      */ 
/* 1696 */     String rawname = endElementName.rawname;
/*      */ 
/* 1708 */     if (!this.fEntityScanner.skipString(endElementName.rawname)) {
/* 1709 */       reportFatalError("ETagRequired", new Object[] { rawname });
/*      */     }
/*      */ 
/* 1713 */     this.fEntityScanner.skipSpaces();
/* 1714 */     if (!this.fEntityScanner.skipChar(62)) {
/* 1715 */       reportFatalError("ETagUnterminated", new Object[] { rawname });
/*      */     }
/*      */ 
/* 1718 */     this.fMarkupDepth -= 1;
/*      */ 
/* 1721 */     this.fMarkupDepth -= 1;
/*      */ 
/* 1724 */     if (this.fMarkupDepth < this.fEntityStack[(this.fEntityDepth - 1)]) {
/* 1725 */       reportFatalError("ElementEntityMismatch", new Object[] { rawname });
/*      */     }
/*      */ 
/* 1737 */     if (this.fDocumentHandler != null)
/*      */     {
/* 1742 */       this.fDocumentHandler.endElement(endElementName, null);
/*      */     }
/* 1744 */     if (this.dtdGrammarUtil != null) {
/* 1745 */       this.dtdGrammarUtil.endElement(endElementName);
/*      */     }
/* 1747 */     return this.fMarkupDepth;
/*      */   }
/*      */ 
/*      */   protected void scanCharReference()
/*      */     throws IOException, XNIException
/*      */   {
/* 1761 */     this.fStringBuffer2.clear();
/* 1762 */     int ch = scanCharReferenceValue(this.fStringBuffer2, null);
/* 1763 */     this.fMarkupDepth -= 1;
/* 1764 */     if (ch != -1)
/*      */     {
/* 1767 */       if (this.fDocumentHandler != null) {
/* 1768 */         if (this.fNotifyCharRefs) {
/* 1769 */           this.fDocumentHandler.startGeneralEntity(this.fCharRefLiteral, null, null, null);
/*      */         }
/* 1771 */         Augmentations augs = null;
/* 1772 */         if ((this.fValidation) && (ch <= 32)) {
/* 1773 */           if (this.fTempAugmentations != null) {
/* 1774 */             this.fTempAugmentations.removeAllItems();
/*      */           }
/*      */           else {
/* 1777 */             this.fTempAugmentations = new AugmentationsImpl();
/*      */           }
/* 1779 */           augs = this.fTempAugmentations;
/* 1780 */           augs.putItem("CHAR_REF_PROBABLE_WS", Boolean.TRUE);
/*      */         }
/*      */ 
/* 1785 */         if (this.fNotifyCharRefs)
/* 1786 */           this.fDocumentHandler.endGeneralEntity(this.fCharRefLiteral, null);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void scanEntityReference(XMLStringBuffer content)
/*      */     throws IOException, XNIException
/*      */   {
/* 1804 */     String name = this.fEntityScanner.scanName();
/* 1805 */     if (name == null) {
/* 1806 */       reportFatalError("NameRequiredInReference", null);
/* 1807 */       return;
/*      */     }
/* 1809 */     if (!this.fEntityScanner.skipChar(59)) {
/* 1810 */       reportFatalError("SemicolonRequiredInReference", new Object[] { name });
/*      */     }
/* 1812 */     if (this.fEntityStore.isUnparsedEntity(name)) {
/* 1813 */       reportFatalError("ReferenceToUnparsedEntity", new Object[] { name });
/*      */     }
/* 1815 */     this.fMarkupDepth -= 1;
/* 1816 */     this.fCurrentEntityName = name;
/*      */ 
/* 1819 */     if (name == fAmpSymbol) {
/* 1820 */       handleCharacter('&', fAmpSymbol, content);
/* 1821 */       this.fScannerState = 41;
/* 1822 */       return;
/* 1823 */     }if (name == fLtSymbol) {
/* 1824 */       handleCharacter('<', fLtSymbol, content);
/* 1825 */       this.fScannerState = 41;
/* 1826 */       return;
/* 1827 */     }if (name == fGtSymbol) {
/* 1828 */       handleCharacter('>', fGtSymbol, content);
/* 1829 */       this.fScannerState = 41;
/* 1830 */       return;
/* 1831 */     }if (name == fQuotSymbol) {
/* 1832 */       handleCharacter('"', fQuotSymbol, content);
/* 1833 */       this.fScannerState = 41;
/* 1834 */       return;
/* 1835 */     }if (name == fAposSymbol) {
/* 1836 */       handleCharacter('\'', fAposSymbol, content);
/* 1837 */       this.fScannerState = 41;
/* 1838 */       return;
/*      */     }
/*      */ 
/* 1844 */     if (((this.fEntityStore.isExternalEntity(name)) && (!this.fSupportExternalEntities)) || ((!this.fEntityStore.isExternalEntity(name)) && (!this.fReplaceEntityReferences)) || (this.foundBuiltInRefs)) {
/* 1845 */       this.fScannerState = 28;
/* 1846 */       return;
/*      */     }
/*      */ 
/* 1849 */     if (!this.fEntityStore.isDeclaredEntity(name))
/*      */     {
/* 1851 */       if ((!this.fSupportDTD) && (this.fReplaceEntityReferences)) {
/* 1852 */         reportFatalError("EntityNotDeclared", new Object[] { name });
/* 1853 */         return;
/*      */       }
/*      */ 
/* 1856 */       if ((this.fHasExternalDTD) && (!this.fStandalone)) {
/* 1857 */         if (this.fValidation)
/* 1858 */           this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { name }, (short)1);
/*      */       }
/*      */       else {
/* 1861 */         reportFatalError("EntityNotDeclared", new Object[] { name });
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1867 */     this.fEntityManager.startEntity(name, false);
/*      */   }
/*      */ 
/*      */   private void handleCharacter(char c, String entity, XMLStringBuffer content)
/*      */     throws XNIException
/*      */   {
/* 1889 */     this.foundBuiltInRefs = true;
/* 1890 */     content.append(c);
/* 1891 */     if (this.fDocumentHandler != null) {
/* 1892 */       this.fSingleChar[0] = c;
/* 1893 */       if (this.fNotifyBuiltInRefs) {
/* 1894 */         this.fDocumentHandler.startGeneralEntity(entity, null, null, null);
/*      */       }
/* 1896 */       this.fTempString.setValues(this.fSingleChar, 0, 1);
/*      */ 
/* 1899 */       if (this.fNotifyBuiltInRefs)
/* 1900 */         this.fDocumentHandler.endGeneralEntity(entity, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final void setScannerState(int state)
/*      */   {
/* 1914 */     this.fScannerState = state;
/*      */   }
/*      */ 
/*      */   protected final void setDriver(Driver driver)
/*      */   {
/* 1931 */     this.fDriver = driver;
/*      */   }
/*      */ 
/*      */   protected String getScannerStateName(int state)
/*      */   {
/* 1946 */     switch (state) { case 24:
/* 1947 */       return "SCANNER_STATE_DOCTYPE";
/*      */     case 26:
/* 1948 */       return "SCANNER_STATE_ROOT_ELEMENT";
/*      */     case 21:
/* 1949 */       return "SCANNER_STATE_START_OF_MARKUP";
/*      */     case 27:
/* 1950 */       return "SCANNER_STATE_COMMENT";
/*      */     case 23:
/* 1951 */       return "SCANNER_STATE_PI";
/*      */     case 22:
/* 1952 */       return "SCANNER_STATE_CONTENT";
/*      */     case 28:
/* 1953 */       return "SCANNER_STATE_REFERENCE";
/*      */     case 33:
/* 1954 */       return "SCANNER_STATE_END_OF_INPUT";
/*      */     case 34:
/* 1955 */       return "SCANNER_STATE_TERMINATED";
/*      */     case 35:
/* 1956 */       return "SCANNER_STATE_CDATA";
/*      */     case 36:
/* 1957 */       return "SCANNER_STATE_TEXT_DECL";
/*      */     case 29:
/* 1958 */       return "SCANNER_STATE_ATTRIBUTE";
/*      */     case 30:
/* 1959 */       return "SCANNER_STATE_ATTRIBUTE_VALUE";
/*      */     case 38:
/* 1960 */       return "SCANNER_STATE_START_ELEMENT_TAG";
/*      */     case 39:
/* 1961 */       return "SCANNER_STATE_END_ELEMENT_TAG";
/*      */     case 37:
/* 1962 */       return "SCANNER_STATE_CHARACTER_DATA";
/*      */     case 25:
/*      */     case 31:
/* 1965 */     case 32: } return "??? (" + state + ')';
/*      */   }
/*      */ 
/*      */   public String getEntityName()
/*      */   {
/* 1970 */     return this.fCurrentEntityName;
/*      */   }
/*      */ 
/*      */   public String getDriverName(Driver driver)
/*      */   {
/* 1990 */     return "null";
/*      */   }
/*      */ 
/*      */   static void pr(String str)
/*      */   {
/* 3172 */     System.out.println(str);
/*      */   }
/*      */ 
/*      */   protected XMLString getString()
/*      */   {
/* 3187 */     if ((this.fAttributeCacheUsedCount < this.initialCacheCount) || (this.fAttributeCacheUsedCount < this.attributeValueCache.size())) {
/* 3188 */       return (XMLString)this.attributeValueCache.get(this.fAttributeCacheUsedCount++);
/*      */     }
/* 3190 */     XMLString str = new XMLString();
/* 3191 */     this.fAttributeCacheUsedCount += 1;
/* 3192 */     this.attributeValueCache.add(str);
/* 3193 */     return str;
/*      */   }
/*      */ 
/*      */   public void refresh()
/*      */   {
/* 3202 */     refresh(0);
/*      */   }
/*      */ 
/*      */   public void refresh(int refreshPosition)
/*      */   {
/* 3213 */     if (this.fReadingAttributes) {
/* 3214 */       this.fAttributes.refresh();
/*      */     }
/* 3216 */     if (this.fScannerState == 37)
/*      */     {
/* 3219 */       this.fContentBuffer.append(this.fTempString);
/*      */ 
/* 3221 */       this.fTempString.length = 0;
/* 3222 */       this.fUsebuffer = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static abstract interface Driver
/*      */   {
/*      */     public abstract int next()
/*      */       throws IOException, XNIException;
/*      */   }
/*      */ 
/*      */   protected static final class Element
/*      */   {
/*      */     public QName qname;
/*      */     public char[] fRawname;
/*      */     public Element next;
/*      */ 
/*      */     public Element(QName qname, Element next)
/*      */     {
/* 2025 */       this.qname.setValues(qname);
/* 2026 */       this.fRawname = qname.rawname.toCharArray();
/* 2027 */       this.next = next;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class ElementStack
/*      */   {
/*      */     protected QName[] fElements;
/* 2206 */     protected int[] fInt = new int[20];
/*      */     protected int fDepth;
/*      */     protected int fCount;
/*      */     protected int fPosition;
/*      */     protected int fMark;
/*      */     protected int fLastDepth;
/*      */ 
/*      */     public ElementStack()
/*      */     {
/* 2226 */       this.fElements = new QName[20];
/* 2227 */       for (int i = 0; i < this.fElements.length; i++)
/* 2228 */         this.fElements[i] = new QName();
/*      */     }
/*      */ 
/*      */     public QName pushElement(QName element)
/*      */     {
/* 2251 */       if (this.fDepth == this.fElements.length) {
/* 2252 */         QName[] array = new QName[this.fElements.length * 2];
/* 2253 */         System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
/* 2254 */         this.fElements = array;
/* 2255 */         for (int i = this.fDepth; i < this.fElements.length; i++) {
/* 2256 */           this.fElements[i] = new QName();
/*      */         }
/*      */       }
/* 2259 */       this.fElements[this.fDepth].setValues(element);
/* 2260 */       return this.fElements[(this.fDepth++)];
/*      */     }
/*      */ 
/*      */     public QName getNext()
/*      */     {
/* 2270 */       if (this.fPosition == this.fCount) {
/* 2271 */         this.fPosition = this.fMark;
/*      */       }
/*      */ 
/* 2279 */       return this.fElements[this.fPosition];
/*      */     }
/*      */ 
/*      */     public void push()
/*      */     {
/* 2289 */       this.fInt[(++this.fDepth)] = (this.fPosition++);
/*      */     }
/*      */ 
/*      */     public boolean matchElement(QName element)
/*      */     {
/* 2305 */       boolean match = false;
/* 2306 */       if ((this.fLastDepth > this.fDepth) && (this.fDepth <= 3))
/*      */       {
/* 2311 */         if (element.rawname == this.fElements[(this.fDepth - 1)].rawname) {
/* 2312 */           XMLDocumentFragmentScannerImpl.this.fAdd = false;
/*      */ 
/* 2315 */           this.fMark = (this.fDepth - 1);
/*      */ 
/* 2317 */           this.fPosition = this.fMark;
/* 2318 */           match = true;
/*      */ 
/* 2320 */           this.fCount -= 1;
/*      */         }
/*      */         else
/*      */         {
/* 2331 */           XMLDocumentFragmentScannerImpl.this.fAdd = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2339 */       if (match)
/*      */       {
/* 2341 */         this.fInt[this.fDepth] = (this.fPosition++);
/*      */       }
/*      */       else
/*      */       {
/* 2347 */         this.fInt[this.fDepth] = (this.fCount - 1);
/*      */       }
/*      */ 
/* 2352 */       if (this.fCount == this.fElements.length) {
/* 2353 */         XMLDocumentFragmentScannerImpl.this.fSkip = false;
/* 2354 */         XMLDocumentFragmentScannerImpl.this.fAdd = false;
/*      */ 
/* 2356 */         reposition();
/*      */ 
/* 2363 */         return false;
/*      */       }
/*      */ 
/* 2373 */       this.fLastDepth = this.fDepth;
/* 2374 */       return match;
/*      */     }
/*      */ 
/*      */     public QName nextElement()
/*      */     {
/* 2385 */       if (XMLDocumentFragmentScannerImpl.this.fSkip) {
/* 2386 */         this.fDepth += 1;
/*      */ 
/* 2388 */         return this.fElements[(this.fCount++)];
/* 2389 */       }if (this.fDepth == this.fElements.length) {
/* 2390 */         QName[] array = new QName[this.fElements.length * 2];
/* 2391 */         System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
/* 2392 */         this.fElements = array;
/* 2393 */         for (int i = this.fDepth; i < this.fElements.length; i++) {
/* 2394 */           this.fElements[i] = new QName();
/*      */         }
/*      */       }
/*      */ 
/* 2398 */       return this.fElements[(this.fDepth++)];
/*      */     }
/*      */ 
/*      */     public QName popElement()
/*      */     {
/* 2415 */       if ((XMLDocumentFragmentScannerImpl.this.fSkip) || (XMLDocumentFragmentScannerImpl.this.fAdd))
/*      */       {
/* 2420 */         return this.fElements[this.fInt[(this.fDepth--)]];
/*      */       }
/*      */ 
/* 2425 */       return this.fElements[(--this.fDepth)];
/*      */     }
/*      */ 
/*      */     public void reposition()
/*      */     {
/* 2435 */       for (int i = 2; i <= this.fDepth; i++)
/* 2436 */         this.fElements[(i - 1)] = this.fElements[this.fInt[i]];
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 2447 */       this.fDepth = 0;
/* 2448 */       this.fLastDepth = 0;
/* 2449 */       this.fCount = 0;
/* 2450 */       this.fPosition = (this.fMark = 1);
/*      */     }
/*      */ 
/*      */     public QName getLastPoppedElement()
/*      */     {
/* 2463 */       return this.fElements[this.fDepth];
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class ElementStack2
/*      */   {
/* 2044 */     protected QName[] fQName = new QName[20];
/*      */     protected int fDepth;
/*      */     protected int fCount;
/*      */     protected int fPosition;
/*      */     protected int fMark;
/*      */     protected int fLastDepth;
/*      */ 
/*      */     public ElementStack2()
/*      */     {
/* 2063 */       for (int i = 0; i < this.fQName.length; i++) {
/* 2064 */         this.fQName[i] = new QName();
/*      */       }
/* 2066 */       this.fMark = (this.fPosition = 1);
/*      */     }
/*      */ 
/*      */     public void resize()
/*      */     {
/* 2077 */       int oldLength = this.fQName.length;
/* 2078 */       QName[] tmp = new QName[oldLength * 2];
/* 2079 */       System.arraycopy(this.fQName, 0, tmp, 0, oldLength);
/* 2080 */       this.fQName = tmp;
/*      */ 
/* 2082 */       for (int i = oldLength; i < this.fQName.length; i++)
/* 2083 */         this.fQName[i] = new QName();
/*      */     }
/*      */ 
/*      */     public boolean matchElement(QName element)
/*      */     {
/* 2105 */       boolean match = false;
/* 2106 */       if ((this.fLastDepth > this.fDepth) && (this.fDepth <= 2))
/*      */       {
/* 2110 */         if (element.rawname == this.fQName[this.fDepth].rawname) {
/* 2111 */           XMLDocumentFragmentScannerImpl.this.fAdd = false;
/*      */ 
/* 2114 */           this.fMark = (this.fDepth - 1);
/*      */ 
/* 2116 */           this.fPosition = (this.fMark + 1);
/* 2117 */           match = true;
/*      */ 
/* 2119 */           this.fCount -= 1;
/*      */         }
/*      */         else
/*      */         {
/* 2128 */           XMLDocumentFragmentScannerImpl.this.fAdd = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2133 */       this.fLastDepth = (this.fDepth++);
/* 2134 */       return match;
/*      */     }
/*      */ 
/*      */     public QName nextElement()
/*      */     {
/* 2147 */       if (this.fCount == this.fQName.length) {
/* 2148 */         XMLDocumentFragmentScannerImpl.this.fShouldSkip = false;
/* 2149 */         XMLDocumentFragmentScannerImpl.this.fAdd = false;
/*      */ 
/* 2153 */         return this.fQName[(--this.fCount)];
/*      */       }
/*      */ 
/* 2158 */       return this.fQName[(this.fCount++)];
/*      */     }
/*      */ 
/*      */     public QName getNext()
/*      */     {
/* 2168 */       if (this.fPosition == this.fCount) {
/* 2169 */         this.fPosition = this.fMark;
/*      */       }
/* 2171 */       return this.fQName[(this.fPosition++)];
/*      */     }
/*      */ 
/*      */     public int popElement()
/*      */     {
/* 2177 */       return this.fDepth--;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 2183 */       this.fLastDepth = 0;
/* 2184 */       this.fDepth = 0;
/* 2185 */       this.fCount = 0;
/* 2186 */       this.fPosition = (this.fMark = 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class FragmentContentDriver
/*      */     implements XMLDocumentFragmentScannerImpl.Driver
/*      */   {
/* 2526 */     private boolean fContinueDispatching = true;
/* 2527 */     private boolean fScanningForMarkup = true;
/*      */ 
/*      */     protected FragmentContentDriver() {
/*      */     }
/*      */ 
/*      */     private void startOfMarkup() throws IOException {
/* 2533 */       XMLDocumentFragmentScannerImpl.this.fMarkupDepth += 1;
/* 2534 */       int ch = XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar();
/*      */ 
/* 2536 */       switch (ch) {
/*      */       case 63:
/* 2538 */         XMLDocumentFragmentScannerImpl.this.setScannerState(23);
/* 2539 */         XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(ch);
/* 2540 */         break;
/*      */       case 33:
/* 2543 */         XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(ch);
/* 2544 */         if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45)) {
/* 2545 */           if (!XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45)) {
/* 2546 */             XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
/*      */           }
/*      */ 
/* 2549 */           XMLDocumentFragmentScannerImpl.this.setScannerState(27);
/* 2550 */         } else if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString(XMLDocumentFragmentScannerImpl.cdata)) {
/* 2551 */           XMLDocumentFragmentScannerImpl.this.setScannerState(35);
/* 2552 */         } else if (!scanForDoctypeHook()) {
/* 2553 */           XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null); } break;
/*      */       case 47:
/* 2559 */         XMLDocumentFragmentScannerImpl.this.setScannerState(39);
/* 2560 */         XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(ch);
/* 2561 */         break;
/*      */       default:
/* 2564 */         if (XMLDocumentFragmentScannerImpl.this.isValidNameStartChar(ch))
/* 2565 */           XMLDocumentFragmentScannerImpl.this.setScannerState(38);
/*      */         else
/* 2567 */           XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
/*      */         break;
/*      */       }
/*      */     }
/*      */ 
/*      */     private void startOfContent()
/*      */       throws IOException
/*      */     {
/* 2576 */       if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60))
/* 2577 */         XMLDocumentFragmentScannerImpl.this.setScannerState(21);
/* 2578 */       else if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(38)) {
/* 2579 */         XMLDocumentFragmentScannerImpl.this.setScannerState(28);
/*      */       }
/*      */       else
/* 2582 */         XMLDocumentFragmentScannerImpl.this.setScannerState(37);
/*      */     }
/*      */ 
/*      */     public void decideSubState()
/*      */       throws IOException
/*      */     {
/* 2601 */       while ((XMLDocumentFragmentScannerImpl.this.fScannerState == 22) || (XMLDocumentFragmentScannerImpl.this.fScannerState == 21))
/*      */       {
/* 2603 */         switch (XMLDocumentFragmentScannerImpl.this.fScannerState)
/*      */         {
/*      */         case 22:
/* 2606 */           startOfContent();
/* 2607 */           break;
/*      */         case 21:
/* 2611 */           startOfMarkup();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public int next()
/*      */       throws IOException, XNIException
/*      */     {
/*      */       try
/*      */       {
/*      */         while (true)
/*      */         {
/* 2647 */           switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
/*      */           case 22:
/* 2649 */             int ch = XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar();
/* 2650 */             if (ch == 60) {
/* 2651 */               XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
/* 2652 */               XMLDocumentFragmentScannerImpl.this.setScannerState(21);
/* 2653 */             } else if (ch == 38) {
/* 2654 */               XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
/* 2655 */               XMLDocumentFragmentScannerImpl.this.setScannerState(28);
/*      */             }
/*      */             else
/*      */             {
/* 2659 */               XMLDocumentFragmentScannerImpl.this.setScannerState(37);
/* 2660 */             }break;
/*      */           case 21:
/* 2665 */             startOfMarkup();
/*      */           }
/*      */ 
/* 2673 */           if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
/* 2674 */             XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
/*      */ 
/* 2676 */             if (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)
/*      */             {
/* 2680 */               if ((XMLDocumentFragmentScannerImpl.this.fScannerState != 35) && (XMLDocumentFragmentScannerImpl.this.fScannerState != 28) && (XMLDocumentFragmentScannerImpl.this.fScannerState != 37))
/*      */               {
/* 2682 */                 XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
/* 2683 */                 return 4;
/*      */               }
/*      */ 
/*      */             }
/* 2688 */             else if ((XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData) || (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference))
/*      */             {
/* 2693 */               if ((XMLDocumentFragmentScannerImpl.this.fScannerState != 35) && (XMLDocumentFragmentScannerImpl.this.fScannerState != 28) && (XMLDocumentFragmentScannerImpl.this.fScannerState != 37))
/*      */               {
/* 2696 */                 XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
/* 2697 */                 XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
/* 2698 */                 return 4;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2708 */           switch (XMLDocumentFragmentScannerImpl.this.fScannerState)
/*      */           {
/*      */           case 7:
/* 2711 */             return 7;
/*      */           case 38:
/* 2717 */             XMLDocumentFragmentScannerImpl.this.fEmptyElement = XMLDocumentFragmentScannerImpl.this.scanStartElement();
/*      */ 
/* 2719 */             if (XMLDocumentFragmentScannerImpl.this.fEmptyElement) {
/* 2720 */               XMLDocumentFragmentScannerImpl.this.setScannerState(39);
/*      */             }
/*      */             else {
/* 2723 */               XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/*      */             }
/* 2725 */             return 1;
/*      */           case 37:
/* 2734 */             XMLDocumentFragmentScannerImpl.this.fUsebuffer = ((XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference) || (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData) || (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData));
/*      */ 
/* 2737 */             if ((XMLDocumentFragmentScannerImpl.this.fIsCoalesce) && ((XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference) || (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData) || (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData))) {
/* 2738 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
/* 2739 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
/* 2740 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
/* 2741 */               XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
/*      */             }
/*      */             else {
/* 2744 */               XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
/*      */             }
/*      */ 
/* 2749 */             XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
/* 2750 */             int c = XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanContent(XMLDocumentFragmentScannerImpl.this.fTempString);
/*      */ 
/* 2754 */             if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60))
/*      */             {
/* 2756 */               if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(47))
/*      */               {
/* 2758 */                 XMLDocumentFragmentScannerImpl.this.fMarkupDepth += 1;
/* 2759 */                 XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
/* 2760 */                 XMLDocumentFragmentScannerImpl.this.setScannerState(39);
/*      */               }
/* 2762 */               else if (XMLChar.isNameStart(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
/* 2763 */                 XMLDocumentFragmentScannerImpl.this.fMarkupDepth += 1;
/* 2764 */                 XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
/* 2765 */                 XMLDocumentFragmentScannerImpl.this.setScannerState(38);
/*      */               } else {
/* 2767 */                 XMLDocumentFragmentScannerImpl.this.setScannerState(21);
/*      */ 
/* 2769 */                 if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
/* 2770 */                   XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
/* 2771 */                   XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
/* 2772 */                   XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
/* 2773 */                   XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
/* 2774 */                   continue;
/*      */                 }
/*      */               }
/*      */ 
/* 2778 */               if (XMLDocumentFragmentScannerImpl.this.fUsebuffer) {
/* 2779 */                 XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
/* 2780 */                 XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
/*      */               }
/*      */ 
/* 2785 */               if ((XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil != null) && (XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil.isIgnorableWhiteSpace(XMLDocumentFragmentScannerImpl.this.fContentBuffer)))
/*      */               {
/* 2787 */                 return 6;
/*      */               }
/* 2789 */               return 4;
/*      */             }
/*      */             else {
/* 2792 */               XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
/*      */ 
/* 2797 */               XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
/* 2798 */               XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
/*      */ 
/* 2800 */               if (c == 13)
/*      */               {
/* 2806 */                 XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
/* 2807 */                 XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
/* 2808 */                 XMLDocumentFragmentScannerImpl.this.fContentBuffer.append((char)c);
/* 2809 */                 c = -1;
/* 2810 */               } else if (c == 93)
/*      */               {
/* 2813 */                 XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
/* 2814 */                 XMLDocumentFragmentScannerImpl.this.fContentBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar());
/*      */ 
/* 2818 */                 XMLDocumentFragmentScannerImpl.this.fInScanContent = true;
/*      */ 
/* 2823 */                 if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(93)) {
/* 2824 */                   XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(']');
/* 2825 */                   while (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(93)) {
/* 2826 */                     XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(']');
/*      */                   }
/* 2828 */                   if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(62)) {
/* 2829 */                     XMLDocumentFragmentScannerImpl.this.reportFatalError("CDEndInContent", null);
/*      */                   }
/*      */                 }
/* 2832 */                 c = -1;
/* 2833 */                 XMLDocumentFragmentScannerImpl.this.fInScanContent = false;
/*      */               }
/*      */ 
/*      */               do
/*      */               {
/* 2840 */                 if (c == 60) {
/* 2841 */                   XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
/* 2842 */                   XMLDocumentFragmentScannerImpl.this.setScannerState(21);
/* 2843 */                   break;
/*      */                 }
/* 2845 */                 if (c == 38) {
/* 2846 */                   XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
/* 2847 */                   XMLDocumentFragmentScannerImpl.this.setScannerState(28);
/* 2848 */                   break;
/*      */                 }
/* 2850 */                 if ((c != -1) && (XMLDocumentFragmentScannerImpl.this.isInvalidLiteral(c))) {
/* 2851 */                   if (XMLChar.isHighSurrogate(c))
/*      */                   {
/* 2853 */                     XMLDocumentFragmentScannerImpl.this.scanSurrogates(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
/* 2854 */                     XMLDocumentFragmentScannerImpl.this.setScannerState(22); break;
/*      */                   }
/* 2856 */                   XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(c, 16) });
/*      */ 
/* 2859 */                   XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
/*      */ 
/* 2861 */                   break;
/*      */                 }
/*      */ 
/* 2864 */                 c = XMLDocumentFragmentScannerImpl.this.scanContent(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
/*      */               }
/*      */ 
/* 2867 */               while (XMLDocumentFragmentScannerImpl.this.fIsCoalesce);
/* 2868 */               XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/*      */ 
/* 2879 */               if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
/* 2880 */                 XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
/*      */               }
/*      */               else {
/* 2883 */                 if ((XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil != null) && (XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil.isIgnorableWhiteSpace(XMLDocumentFragmentScannerImpl.this.fContentBuffer)))
/*      */                 {
/* 2885 */                   return 6;
/*      */                 }
/* 2887 */                 return 4;
/*      */               }
/*      */             }
/*      */             break;
/*      */           case 39:
/* 2892 */             if (XMLDocumentFragmentScannerImpl.this.fEmptyElement)
/*      */             {
/* 2894 */               XMLDocumentFragmentScannerImpl.this.fEmptyElement = false;
/* 2895 */               XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/*      */ 
/* 2898 */               return (XMLDocumentFragmentScannerImpl.this.fMarkupDepth == 0) && (elementDepthIsZeroHook()) ? 2 : 2;
/*      */             }
/* 2900 */             if (XMLDocumentFragmentScannerImpl.this.scanEndElement() == 0)
/*      */             {
/* 2902 */               if (elementDepthIsZeroHook())
/*      */               {
/* 2906 */                 return 2;
/*      */               }
/*      */             }
/*      */ 
/* 2910 */             XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/* 2911 */             return 2;
/*      */           case 27:
/* 2915 */             XMLDocumentFragmentScannerImpl.this.scanComment();
/* 2916 */             XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/* 2917 */             return 5;
/*      */           case 23:
/* 2922 */             XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
/*      */ 
/* 2926 */             XMLDocumentFragmentScannerImpl.this.scanPI(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
/* 2927 */             XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/* 2928 */             return 3;
/*      */           case 35:
/* 2937 */             if ((XMLDocumentFragmentScannerImpl.this.fIsCoalesce) && ((XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference) || (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData) || (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData))) {
/* 2938 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = true;
/* 2939 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
/* 2940 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
/*      */             }
/*      */             else {
/* 2943 */               XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
/*      */             }
/* 2945 */             XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
/*      */ 
/* 2947 */             XMLDocumentFragmentScannerImpl.this.scanCDATASection(XMLDocumentFragmentScannerImpl.this.fContentBuffer, true);
/* 2948 */             XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/*      */ 
/* 2956 */             if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
/* 2957 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = true;
/*      */             }
/*      */             else {
/* 2960 */               if (XMLDocumentFragmentScannerImpl.this.fReportCdataEvent) {
/* 2961 */                 return 12;
/*      */               }
/* 2963 */               return 4;
/*      */             }
/*      */ 
/*      */             break;
/*      */           case 28:
/* 2968 */             XMLDocumentFragmentScannerImpl.this.fMarkupDepth += 1;
/* 2969 */             XMLDocumentFragmentScannerImpl.this.foundBuiltInRefs = false;
/*      */ 
/* 2973 */             if ((XMLDocumentFragmentScannerImpl.this.fIsCoalesce) && ((XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference) || (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData) || (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)))
/*      */             {
/* 2976 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
/* 2977 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
/* 2978 */               XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
/*      */             }
/*      */             else {
/* 2981 */               XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
/*      */             }
/* 2983 */             XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
/*      */ 
/* 2985 */             if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(35)) {
/* 2986 */               XMLDocumentFragmentScannerImpl.this.scanCharReferenceValue(XMLDocumentFragmentScannerImpl.this.fContentBuffer, null);
/* 2987 */               XMLDocumentFragmentScannerImpl.this.fMarkupDepth -= 1;
/* 2988 */               if (!XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
/* 2989 */                 XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/* 2990 */                 return 4;
/*      */               }
/*      */             }
/*      */             else {
/* 2994 */               XMLDocumentFragmentScannerImpl.this.scanEntityReference(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
/*      */ 
/* 2997 */               if ((XMLDocumentFragmentScannerImpl.this.fScannerState == 41) && (!XMLDocumentFragmentScannerImpl.this.fIsCoalesce)) {
/* 2998 */                 XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/* 2999 */                 return 4;
/*      */               }
/*      */ 
/* 3003 */               if (XMLDocumentFragmentScannerImpl.this.fScannerState == 36) {
/* 3004 */                 XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
/* 3005 */                 continue;
/*      */               }
/*      */ 
/* 3008 */               if (XMLDocumentFragmentScannerImpl.this.fScannerState == 28) {
/* 3009 */                 XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/* 3010 */                 if ((XMLDocumentFragmentScannerImpl.this.fReplaceEntityReferences) && (XMLDocumentFragmentScannerImpl.this.fEntityStore.isDeclaredEntity(XMLDocumentFragmentScannerImpl.this.fCurrentEntityName)))
/*      */                 {
/*      */                   continue;
/*      */                 }
/* 3014 */                 return 9;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 3019 */             XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/* 3020 */             XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
/* 3021 */             break;
/*      */           case 36:
/* 3026 */             if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString("<?xml")) {
/* 3027 */               XMLDocumentFragmentScannerImpl.this.fMarkupDepth += 1;
/*      */ 
/* 3030 */               if (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
/* 3031 */                 XMLDocumentFragmentScannerImpl.this.fStringBuffer.clear();
/* 3032 */                 XMLDocumentFragmentScannerImpl.this.fStringBuffer.append("xml");
/*      */ 
/* 3034 */                 if (XMLDocumentFragmentScannerImpl.this.fNamespaces) {
/* 3035 */                   while (XMLDocumentFragmentScannerImpl.this.isValidNCName(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
/* 3036 */                     XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar());
/*      */                   }
/*      */                 }
/* 3039 */                 while (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
/* 3040 */                   XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar());
/*      */                 }
/*      */ 
/* 3043 */                 String target = XMLDocumentFragmentScannerImpl.this.fSymbolTable.addSymbol(XMLDocumentFragmentScannerImpl.this.fStringBuffer.ch, XMLDocumentFragmentScannerImpl.this.fStringBuffer.offset, XMLDocumentFragmentScannerImpl.this.fStringBuffer.length);
/* 3044 */                 XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
/* 3045 */                 XMLDocumentFragmentScannerImpl.this.scanPIData(target, XMLDocumentFragmentScannerImpl.this.fContentBuffer);
/*      */               }
/*      */               else
/*      */               {
/* 3051 */                 XMLDocumentFragmentScannerImpl.this.scanXMLDeclOrTextDecl(true);
/*      */               }
/*      */             }
/*      */ 
/* 3055 */             XMLDocumentFragmentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
/* 3056 */             XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/*      */           case 26:
/*      */           case 40:
/*      */           case 8:
/*      */           case 9:
/*      */           case 10:
/*      */           case 11:
/*      */           case 12:
/*      */           case 13:
/*      */           case 14:
/*      */           case 15:
/*      */           case 16:
/*      */           case 17:
/*      */           case 18:
/*      */           case 19:
/*      */           case 20:
/*      */           case 21:
/*      */           case 22:
/*      */           case 24:
/*      */           case 25:
/*      */           case 29:
/*      */           case 30:
/*      */           case 31:
/*      */           case 32:
/*      */           case 33:
/* 3065 */           case 34: }  } if (scanRootElementHook()) {
/* 3066 */           XMLDocumentFragmentScannerImpl.this.fEmptyElement = true;
/*      */ 
/* 3068 */           return 1;
/*      */         }
/* 3070 */         XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/* 3071 */         return 1;
/*      */ 
/* 3074 */         XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
/* 3075 */         XMLDocumentFragmentScannerImpl.this.scanCharReferenceValue(XMLDocumentFragmentScannerImpl.this.fContentBuffer, null);
/* 3076 */         XMLDocumentFragmentScannerImpl.this.fMarkupDepth -= 1;
/* 3077 */         XMLDocumentFragmentScannerImpl.this.setScannerState(22);
/* 3078 */         return 4;
/*      */ 
/* 3081 */         throw new XNIException("Scanner State " + XMLDocumentFragmentScannerImpl.this.fScannerState + " not Recognized ");
/*      */       }
/*      */       catch (EOFException e)
/*      */       {
/* 3087 */         endOfFileHook(e);
/* 3088 */       }return -1;
/*      */     }
/*      */ 
/*      */     protected boolean scanForDoctypeHook()
/*      */       throws IOException, XNIException
/*      */     {
/* 3113 */       return false;
/*      */     }
/*      */ 
/*      */     protected boolean elementDepthIsZeroHook()
/*      */       throws IOException, XNIException
/*      */     {
/* 3131 */       return false;
/*      */     }
/*      */ 
/*      */     protected boolean scanRootElementHook()
/*      */       throws IOException, XNIException
/*      */     {
/* 3148 */       return false;
/*      */     }
/*      */ 
/*      */     protected void endOfFileHook(EOFException e)
/*      */       throws IOException, XNIException
/*      */     {
/* 3163 */       if (XMLDocumentFragmentScannerImpl.this.fMarkupDepth != 0)
/* 3164 */         XMLDocumentFragmentScannerImpl.this.reportFatalError("PrematureEOF", null);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl
 * JD-Core Version:    0.6.2
 */