/*      */ package com.sun.org.apache.xerces.internal.impl;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
/*      */ import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
/*      */ import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
/*      */ import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
/*      */ import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.EncodingMap;
/*      */ import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
/*      */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLEntityDescriptionImpl;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
/*      */ import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
/*      */ import com.sun.org.apache.xerces.internal.xni.Augmentations;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.xml.internal.stream.Entity;
/*      */ import com.sun.xml.internal.stream.Entity.ExternalEntity;
/*      */ import com.sun.xml.internal.stream.Entity.InternalEntity;
/*      */ import com.sun.xml.internal.stream.Entity.ScannedEntity;
/*      */ import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
/*      */ import com.sun.xml.internal.stream.StaxXMLInputSource;
/*      */ import com.sun.xml.internal.stream.XMLEntityStorage;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.EOFException;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Stack;
/*      */ 
/*      */ public class XMLEntityManager
/*      */   implements XMLComponent, XMLEntityResolver
/*      */ {
/*      */   public static final int DEFAULT_BUFFER_SIZE = 8192;
/*      */   public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
/*      */   public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
/*      */   protected static final String VALIDATION = "http://xml.org/sax/features/validation";
/*      */   protected boolean fStrictURI;
/*      */   protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
/*      */   protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
/*      */   protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
/*      */   protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
/*      */   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*      */   protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
/*      */   protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
/*      */   protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
/*      */   protected static final String STAX_ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/stax-entity-resolver";
/*      */   protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
/*      */   protected static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
/*      */   protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
/*      */   protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
/*  181 */   private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/validation", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/warn-on-duplicate-entitydef", "http://apache.org/xml/features/standard-uri-conformant" };
/*      */ 
/*  191 */   private static final Boolean[] FEATURE_DEFAULTS = { null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE };
/*      */ 
/*  201 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/input-buffer-size", "http://apache.org/xml/properties/security-manager" };
/*      */ 
/*  212 */   private static final Object[] PROPERTY_DEFAULTS = { null, null, null, null, new Integer(8192), null };
/*      */ 
/*  221 */   private static final String XMLEntity = "[xml]".intern();
/*  222 */   private static final String DTDEntity = "[dtd]".intern();
/*      */   private static final boolean DEBUG_BUFFER = false;
/*      */   protected boolean fWarnDuplicateEntityDef;
/*      */   private static final boolean DEBUG_ENTITIES = false;
/*      */   private static final boolean DEBUG_ENCODINGS = false;
/*      */   private static final boolean DEBUG_RESOLVER = false;
/*      */   protected boolean fValidation;
/*      */   protected boolean fExternalGeneralEntities;
/*      */   protected boolean fExternalParameterEntities;
/*  275 */   protected boolean fAllowJavaEncodings = true;
/*      */   protected SymbolTable fSymbolTable;
/*      */   protected XMLErrorReporter fErrorReporter;
/*      */   protected XMLEntityResolver fEntityResolver;
/*      */   protected StaxEntityResolverWrapper fStaxEntityResolver;
/*      */   protected PropertyManager fPropertyManager;
/*      */   protected ValidationManager fValidationManager;
/*  321 */   protected int fBufferSize = 8192;
/*      */ 
/*  325 */   protected SecurityManager fSecurityManager = null;
/*      */   protected boolean fStandalone;
/*  335 */   protected boolean fInExternalSubset = false;
/*      */   protected XMLEntityHandler fEntityHandler;
/*      */   protected XMLEntityScanner fEntityScanner;
/*      */   protected XMLEntityScanner fXML10EntityScanner;
/*      */   protected XMLEntityScanner fXML11EntityScanner;
/*  353 */   protected int fEntityExpansionLimit = 0;
/*      */ 
/*  356 */   protected int fEntityExpansionCount = 0;
/*      */ 
/*  361 */   protected Hashtable fEntities = new Hashtable();
/*      */ 
/*  364 */   protected Stack fEntityStack = new Stack();
/*      */ 
/*  367 */   protected Entity.ScannedEntity fCurrentEntity = null;
/*      */   protected XMLEntityStorage fEntityStorage;
/*  373 */   protected final Object[] defaultEncoding = { "UTF-8", null };
/*      */ 
/*  379 */   private final XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
/*      */ 
/*  382 */   private final Augmentations fEntityAugs = new AugmentationsImpl();
/*      */ 
/*  385 */   private CharacterBufferPool fBufferPool = new CharacterBufferPool(this.fBufferSize, 1024);
/*      */   private static String gUserDir;
/*      */   private static com.sun.org.apache.xerces.internal.util.URI gUserDirURI;
/* 1688 */   private static boolean[] gNeedEscaping = new boolean[''];
/*      */ 
/* 1690 */   private static char[] gAfterEscaping1 = new char[''];
/*      */ 
/* 1692 */   private static char[] gAfterEscaping2 = new char[''];
/* 1693 */   private static char[] gHexChs = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*      */ 
/*      */   public XMLEntityManager()
/*      */   {
/*  395 */     this.fEntityStorage = new XMLEntityStorage(this);
/*  396 */     setScannerVersion((short)1);
/*      */   }
/*      */ 
/*      */   public XMLEntityManager(PropertyManager propertyManager)
/*      */   {
/*  401 */     this.fPropertyManager = propertyManager;
/*      */ 
/*  404 */     this.fEntityStorage = new XMLEntityStorage(this);
/*  405 */     this.fEntityScanner = new XMLEntityScanner(propertyManager, this);
/*  406 */     reset(propertyManager);
/*      */   }
/*      */ 
/*      */   public void addInternalEntity(String name, String text)
/*      */   {
/*  424 */     if (!this.fEntities.containsKey(name)) {
/*  425 */       Entity entity = new Entity.InternalEntity(name, text, this.fInExternalSubset);
/*  426 */       this.fEntities.put(name, entity);
/*      */     }
/*  428 */     else if (this.fWarnDuplicateEntityDef) {
/*  429 */       this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addExternalEntity(String name, String publicId, String literalSystemId, String baseSystemId)
/*      */     throws IOException
/*      */   {
/*  463 */     if (!this.fEntities.containsKey(name)) {
/*  464 */       if (baseSystemId == null)
/*      */       {
/*  466 */         int size = this.fEntityStack.size();
/*  467 */         if ((size == 0) && (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null)) {
/*  468 */           baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
/*      */         }
/*  470 */         for (int i = size - 1; i >= 0; i--) {
/*  471 */           Entity.ScannedEntity externalEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(i);
/*      */ 
/*  473 */           if ((externalEntity.entityLocation != null) && (externalEntity.entityLocation.getExpandedSystemId() != null)) {
/*  474 */             baseSystemId = externalEntity.entityLocation.getExpandedSystemId();
/*  475 */             break;
/*      */           }
/*      */         }
/*      */       }
/*  479 */       Entity entity = new Entity.ExternalEntity(name, new XMLEntityDescriptionImpl(name, publicId, literalSystemId, baseSystemId, expandSystemId(literalSystemId, baseSystemId, false)), null, this.fInExternalSubset);
/*      */ 
/*  482 */       this.fEntities.put(name, entity);
/*      */     }
/*  484 */     else if (this.fWarnDuplicateEntityDef) {
/*  485 */       this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addUnparsedEntity(String name, String publicId, String systemId, String baseSystemId, String notation)
/*      */   {
/*  514 */     if (!this.fEntities.containsKey(name)) {
/*  515 */       Entity.ExternalEntity entity = new Entity.ExternalEntity(name, new XMLEntityDescriptionImpl(name, publicId, systemId, baseSystemId, null), notation, this.fInExternalSubset);
/*      */ 
/*  518 */       this.fEntities.put(name, entity);
/*      */     }
/*  520 */     else if (this.fWarnDuplicateEntityDef) {
/*  521 */       this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public XMLEntityStorage getEntityStore()
/*      */   {
/*  532 */     return this.fEntityStorage;
/*      */   }
/*      */ 
/*      */   public XMLEntityScanner getEntityScanner()
/*      */   {
/*  537 */     if (this.fEntityScanner == null)
/*      */     {
/*  539 */       if (this.fXML10EntityScanner == null) {
/*  540 */         this.fXML10EntityScanner = new XMLEntityScanner();
/*      */       }
/*  542 */       this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
/*  543 */       this.fEntityScanner = this.fXML10EntityScanner;
/*      */     }
/*  545 */     return this.fEntityScanner;
/*      */   }
/*      */ 
/*      */   public void setScannerVersion(short version)
/*      */   {
/*  551 */     if (version == 1) {
/*  552 */       if (this.fXML10EntityScanner == null) {
/*  553 */         this.fXML10EntityScanner = new XMLEntityScanner();
/*      */       }
/*  555 */       this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
/*  556 */       this.fEntityScanner = this.fXML10EntityScanner;
/*  557 */       this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
/*      */     } else {
/*  559 */       if (this.fXML11EntityScanner == null) {
/*  560 */         this.fXML11EntityScanner = new XML11EntityScanner();
/*      */       }
/*  562 */       this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
/*  563 */       this.fEntityScanner = this.fXML11EntityScanner;
/*  564 */       this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String setupCurrentEntity(String name, XMLInputSource xmlInputSource, boolean literal, boolean isExternal)
/*      */     throws IOException, XNIException
/*      */   {
/*  587 */     String publicId = xmlInputSource.getPublicId();
/*  588 */     String literalSystemId = xmlInputSource.getSystemId();
/*  589 */     String baseSystemId = xmlInputSource.getBaseSystemId();
/*  590 */     String encoding = xmlInputSource.getEncoding();
/*  591 */     boolean encodingExternallySpecified = encoding != null;
/*  592 */     Boolean isBigEndian = null;
/*      */ 
/*  595 */     InputStream stream = null;
/*  596 */     Reader reader = xmlInputSource.getCharacterStream();
/*      */ 
/*  599 */     String expandedSystemId = expandSystemId(literalSystemId, baseSystemId, this.fStrictURI);
/*  600 */     if (baseSystemId == null) {
/*  601 */       baseSystemId = expandedSystemId;
/*      */     }
/*  603 */     if (reader == null) {
/*  604 */       stream = xmlInputSource.getByteStream();
/*  605 */       if (stream == null) {
/*  606 */         URL location = new URL(expandedSystemId);
/*  607 */         URLConnection connect = location.openConnection();
/*  608 */         if (!(connect instanceof HttpURLConnection)) {
/*  609 */           stream = connect.getInputStream();
/*      */         }
/*      */         else {
/*  612 */           boolean followRedirects = true;
/*      */ 
/*  615 */           if ((xmlInputSource instanceof HTTPInputSource)) {
/*  616 */             HttpURLConnection urlConnection = (HttpURLConnection)connect;
/*  617 */             HTTPInputSource httpInputSource = (HTTPInputSource)xmlInputSource;
/*      */ 
/*  620 */             Iterator propIter = httpInputSource.getHTTPRequestProperties();
/*  621 */             while (propIter.hasNext()) {
/*  622 */               Map.Entry entry = (Map.Entry)propIter.next();
/*  623 */               urlConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
/*      */             }
/*      */ 
/*  627 */             followRedirects = httpInputSource.getFollowHTTPRedirects();
/*  628 */             if (!followRedirects) {
/*  629 */               setInstanceFollowRedirects(urlConnection, followRedirects);
/*      */             }
/*      */           }
/*      */ 
/*  633 */           stream = connect.getInputStream();
/*      */ 
/*  639 */           if (followRedirects) {
/*  640 */             String redirect = connect.getURL().toString();
/*      */ 
/*  643 */             if (!redirect.equals(expandedSystemId)) {
/*  644 */               literalSystemId = redirect;
/*  645 */               expandedSystemId = redirect;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  652 */       stream = new RewindableInputStream(stream);
/*      */ 
/*  655 */       if (encoding == null)
/*      */       {
/*  657 */         byte[] b4 = new byte[4];
/*  658 */         for (int count = 0; 
/*  659 */           count < 4; count++) {
/*  660 */           b4[count] = ((byte)stream.read());
/*      */         }
/*  662 */         if (count == 4) {
/*  663 */           Object[] encodingDesc = getEncodingName(b4, count);
/*  664 */           encoding = (String)encodingDesc[0];
/*  665 */           isBigEndian = (Boolean)encodingDesc[1];
/*      */ 
/*  667 */           stream.reset();
/*      */ 
/*  671 */           if ((count > 2) && (encoding.equals("UTF-8"))) {
/*  672 */             int b0 = b4[0] & 0xFF;
/*  673 */             int b1 = b4[1] & 0xFF;
/*  674 */             int b2 = b4[2] & 0xFF;
/*  675 */             if ((b0 == 239) && (b1 == 187) && (b2 == 191))
/*      */             {
/*  677 */               stream.skip(3L);
/*      */             }
/*      */           }
/*  680 */           reader = createReader(stream, encoding, isBigEndian);
/*      */         } else {
/*  682 */           reader = createReader(stream, encoding, isBigEndian);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  688 */         encoding = encoding.toUpperCase(Locale.ENGLISH);
/*      */ 
/*  691 */         if (encoding.equals("UTF-8")) {
/*  692 */           int[] b3 = new int[3];
/*  693 */           for (int count = 0; 
/*  694 */             count < 3; count++) {
/*  695 */             b3[count] = stream.read();
/*  696 */             if (b3[count] == -1)
/*      */               break;
/*      */           }
/*  699 */           if (count == 3) {
/*  700 */             if ((b3[0] != 239) || (b3[1] != 187) || (b3[2] != 191))
/*      */             {
/*  702 */               stream.reset();
/*      */             }
/*      */           }
/*  705 */           else stream.reset();
/*      */ 
/*      */         }
/*  710 */         else if (encoding.equals("UTF-16")) {
/*  711 */           int[] b4 = new int[4];
/*  712 */           for (int count = 0; 
/*  713 */             count < 4; count++) {
/*  714 */             b4[count] = stream.read();
/*  715 */             if (b4[count] == -1)
/*      */               break;
/*      */           }
/*  718 */           stream.reset();
/*      */ 
/*  720 */           String utf16Encoding = "UTF-16";
/*  721 */           if (count >= 2) {
/*  722 */             int b0 = b4[0];
/*  723 */             int b1 = b4[1];
/*  724 */             if ((b0 == 254) && (b1 == 255))
/*      */             {
/*  726 */               utf16Encoding = "UTF-16BE";
/*  727 */               isBigEndian = Boolean.TRUE;
/*      */             }
/*  729 */             else if ((b0 == 255) && (b1 == 254))
/*      */             {
/*  731 */               utf16Encoding = "UTF-16LE";
/*  732 */               isBigEndian = Boolean.FALSE;
/*      */             }
/*  734 */             else if (count == 4) {
/*  735 */               int b2 = b4[2];
/*  736 */               int b3 = b4[3];
/*  737 */               if ((b0 == 0) && (b1 == 60) && (b2 == 0) && (b3 == 63))
/*      */               {
/*  739 */                 utf16Encoding = "UTF-16BE";
/*  740 */                 isBigEndian = Boolean.TRUE;
/*      */               }
/*  742 */               if ((b0 == 60) && (b1 == 0) && (b2 == 63) && (b3 == 0))
/*      */               {
/*  744 */                 utf16Encoding = "UTF-16LE";
/*  745 */                 isBigEndian = Boolean.FALSE;
/*      */               }
/*      */             }
/*      */           }
/*  749 */           reader = createReader(stream, utf16Encoding, isBigEndian);
/*      */         }
/*  753 */         else if (encoding.equals("ISO-10646-UCS-4")) {
/*  754 */           int[] b4 = new int[4];
/*  755 */           for (int count = 0; 
/*  756 */             count < 4; count++) {
/*  757 */             b4[count] = stream.read();
/*  758 */             if (b4[count] == -1)
/*      */               break;
/*      */           }
/*  761 */           stream.reset();
/*      */ 
/*  764 */           if (count == 4)
/*      */           {
/*  766 */             if ((b4[0] == 0) && (b4[1] == 0) && (b4[2] == 0) && (b4[3] == 60)) {
/*  767 */               isBigEndian = Boolean.TRUE;
/*      */             }
/*  770 */             else if ((b4[0] == 60) && (b4[1] == 0) && (b4[2] == 0) && (b4[3] == 0)) {
/*  771 */               isBigEndian = Boolean.FALSE;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*  777 */         else if (encoding.equals("ISO-10646-UCS-2")) {
/*  778 */           int[] b4 = new int[4];
/*  779 */           for (int count = 0; 
/*  780 */             count < 4; count++) {
/*  781 */             b4[count] = stream.read();
/*  782 */             if (b4[count] == -1)
/*      */               break;
/*      */           }
/*  785 */           stream.reset();
/*      */ 
/*  787 */           if (count == 4)
/*      */           {
/*  789 */             if ((b4[0] == 0) && (b4[1] == 60) && (b4[2] == 0) && (b4[3] == 63)) {
/*  790 */               isBigEndian = Boolean.TRUE;
/*      */             }
/*  793 */             else if ((b4[0] == 60) && (b4[1] == 0) && (b4[2] == 63) && (b4[3] == 0)) {
/*  794 */               isBigEndian = Boolean.FALSE;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  799 */         reader = createReader(stream, encoding, isBigEndian);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  816 */     if (this.fCurrentEntity != null) {
/*  817 */       this.fEntityStack.push(this.fCurrentEntity);
/*      */     }
/*      */ 
/*  825 */     this.fCurrentEntity = new Entity.ScannedEntity(name, new XMLResourceIdentifierImpl(publicId, literalSystemId, baseSystemId, expandedSystemId), stream, reader, encoding, literal, encodingExternallySpecified, isExternal);
/*  826 */     this.fCurrentEntity.setEncodingExternallySpecified(encodingExternallySpecified);
/*  827 */     this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
/*  828 */     this.fResourceIdentifier.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
/*  829 */     return encoding;
/*      */   }
/*      */ 
/*      */   public boolean isExternalEntity(String entityName)
/*      */   {
/*  842 */     Entity entity = (Entity)this.fEntities.get(entityName);
/*  843 */     if (entity == null) {
/*  844 */       return false;
/*      */     }
/*  846 */     return entity.isExternal();
/*      */   }
/*      */ 
/*      */   public boolean isEntityDeclInExternalSubset(String entityName)
/*      */   {
/*  859 */     Entity entity = (Entity)this.fEntities.get(entityName);
/*  860 */     if (entity == null) {
/*  861 */       return false;
/*      */     }
/*  863 */     return entity.isEntityDeclInExternalSubset();
/*      */   }
/*      */ 
/*      */   public void setStandalone(boolean standalone)
/*      */   {
/*  878 */     this.fStandalone = standalone;
/*      */   }
/*      */ 
/*      */   public boolean isStandalone()
/*      */   {
/*  884 */     return this.fStandalone;
/*      */   }
/*      */ 
/*      */   public boolean isDeclaredEntity(String entityName)
/*      */   {
/*  889 */     Entity entity = (Entity)this.fEntities.get(entityName);
/*  890 */     return entity != null;
/*      */   }
/*      */ 
/*      */   public boolean isUnparsedEntity(String entityName)
/*      */   {
/*  895 */     Entity entity = (Entity)this.fEntities.get(entityName);
/*  896 */     if (entity == null) {
/*  897 */       return false;
/*      */     }
/*  899 */     return entity.isUnparsed();
/*      */   }
/*      */ 
/*      */   public XMLResourceIdentifier getCurrentResourceIdentifier()
/*      */   {
/*  910 */     return this.fResourceIdentifier;
/*      */   }
/*      */ 
/*      */   public void setEntityHandler(XMLEntityHandler entityHandler)
/*      */   {
/*  921 */     this.fEntityHandler = entityHandler;
/*      */   }
/*      */ 
/*      */   public StaxXMLInputSource resolveEntityAsPerStax(XMLResourceIdentifier resourceIdentifier)
/*      */     throws IOException
/*      */   {
/*  927 */     if (resourceIdentifier == null) return null;
/*      */ 
/*  929 */     String publicId = resourceIdentifier.getPublicId();
/*  930 */     String literalSystemId = resourceIdentifier.getLiteralSystemId();
/*  931 */     String baseSystemId = resourceIdentifier.getBaseSystemId();
/*  932 */     String expandedSystemId = resourceIdentifier.getExpandedSystemId();
/*      */ 
/*  939 */     boolean needExpand = expandedSystemId == null;
/*      */ 
/*  943 */     if ((baseSystemId == null) && (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null)) {
/*  944 */       baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
/*  945 */       if (baseSystemId != null)
/*  946 */         needExpand = true;
/*      */     }
/*  948 */     if (needExpand) {
/*  949 */       expandedSystemId = expandSystemId(literalSystemId, baseSystemId, false);
/*      */     }
/*      */ 
/*  952 */     StaxXMLInputSource staxInputSource = null;
/*  953 */     XMLInputSource xmlInputSource = null;
/*      */ 
/*  955 */     XMLResourceIdentifierImpl ri = null;
/*      */ 
/*  957 */     if ((resourceIdentifier instanceof XMLResourceIdentifierImpl)) {
/*  958 */       ri = (XMLResourceIdentifierImpl)resourceIdentifier;
/*      */     } else {
/*  960 */       this.fResourceIdentifier.clear();
/*  961 */       ri = this.fResourceIdentifier;
/*      */     }
/*  963 */     ri.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
/*      */ 
/*  969 */     if (this.fStaxEntityResolver != null) {
/*  970 */       staxInputSource = this.fStaxEntityResolver.resolveEntity(ri);
/*      */     }
/*      */ 
/*  973 */     if (this.fEntityResolver != null) {
/*  974 */       xmlInputSource = this.fEntityResolver.resolveEntity(ri);
/*      */     }
/*      */ 
/*  977 */     if (xmlInputSource != null)
/*      */     {
/*  979 */       staxInputSource = new StaxXMLInputSource(xmlInputSource);
/*      */     }
/*      */ 
/*  984 */     if (staxInputSource == null)
/*      */     {
/*  988 */       staxInputSource = new StaxXMLInputSource(new XMLInputSource(publicId, literalSystemId, baseSystemId));
/*  989 */     } else if (!staxInputSource.hasXMLStreamOrXMLEventReader());
/*  998 */     return staxInputSource;
/*      */   }
/*      */ 
/*      */   public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier)
/*      */     throws IOException, XNIException
/*      */   {
/* 1025 */     if (resourceIdentifier == null) return null;
/* 1026 */     String publicId = resourceIdentifier.getPublicId();
/* 1027 */     String literalSystemId = resourceIdentifier.getLiteralSystemId();
/* 1028 */     String baseSystemId = resourceIdentifier.getBaseSystemId();
/* 1029 */     String expandedSystemId = resourceIdentifier.getExpandedSystemId();
/* 1030 */     String namespace = resourceIdentifier.getNamespace();
/*      */ 
/* 1038 */     boolean needExpand = expandedSystemId == null;
/*      */ 
/* 1042 */     if ((baseSystemId == null) && (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null)) {
/* 1043 */       baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
/* 1044 */       if (baseSystemId != null)
/* 1045 */         needExpand = true;
/*      */     }
/* 1047 */     if (needExpand) {
/* 1048 */       expandedSystemId = expandSystemId(literalSystemId, baseSystemId, false);
/*      */     }
/*      */ 
/* 1051 */     XMLInputSource xmlInputSource = null;
/*      */ 
/* 1053 */     if (this.fEntityResolver != null) {
/* 1054 */       resourceIdentifier.setBaseSystemId(baseSystemId);
/* 1055 */       resourceIdentifier.setExpandedSystemId(expandedSystemId);
/* 1056 */       xmlInputSource = this.fEntityResolver.resolveEntity(resourceIdentifier);
/*      */     }
/*      */ 
/* 1064 */     if (xmlInputSource == null)
/*      */     {
/* 1068 */       xmlInputSource = new XMLInputSource(publicId, literalSystemId, baseSystemId);
/*      */     }
/*      */ 
/* 1076 */     return xmlInputSource;
/*      */   }
/*      */ 
/*      */   public void startEntity(String entityName, boolean literal)
/*      */     throws IOException, XNIException
/*      */   {
/* 1094 */     Entity entity = this.fEntityStorage.getEntity(entityName);
/* 1095 */     if (entity == null) {
/* 1096 */       if (this.fEntityHandler != null) {
/* 1097 */         String encoding = null;
/* 1098 */         this.fResourceIdentifier.clear();
/* 1099 */         this.fEntityAugs.removeAllItems();
/* 1100 */         this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
/* 1101 */         this.fEntityHandler.startEntity(entityName, this.fResourceIdentifier, encoding, this.fEntityAugs);
/* 1102 */         this.fEntityAugs.removeAllItems();
/* 1103 */         this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
/* 1104 */         this.fEntityHandler.endEntity(entityName, this.fEntityAugs);
/*      */       }
/* 1106 */       return;
/*      */     }
/*      */ 
/* 1110 */     boolean external = entity.isExternal();
/* 1111 */     if (external) {
/* 1112 */       boolean unparsed = entity.isUnparsed();
/* 1113 */       boolean parameter = entityName.startsWith("%");
/* 1114 */       boolean general = !parameter;
/* 1115 */       if ((unparsed) || ((general) && (!this.fExternalGeneralEntities)) || ((parameter) && (!this.fExternalParameterEntities)))
/*      */       {
/* 1118 */         if (this.fEntityHandler != null) {
/* 1119 */           this.fResourceIdentifier.clear();
/* 1120 */           String encoding = null;
/* 1121 */           Entity.ExternalEntity externalEntity = (Entity.ExternalEntity)entity;
/*      */ 
/* 1125 */           String extLitSysId = externalEntity.entityLocation != null ? externalEntity.entityLocation.getLiteralSystemId() : null;
/* 1126 */           String extBaseSysId = externalEntity.entityLocation != null ? externalEntity.entityLocation.getBaseSystemId() : null;
/* 1127 */           String expandedSystemId = expandSystemId(extLitSysId, extBaseSysId);
/* 1128 */           this.fResourceIdentifier.setValues(externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null, extLitSysId, extBaseSysId, expandedSystemId);
/*      */ 
/* 1131 */           this.fEntityAugs.removeAllItems();
/* 1132 */           this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
/* 1133 */           this.fEntityHandler.startEntity(entityName, this.fResourceIdentifier, encoding, this.fEntityAugs);
/* 1134 */           this.fEntityAugs.removeAllItems();
/* 1135 */           this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
/* 1136 */           this.fEntityHandler.endEntity(entityName, this.fEntityAugs);
/*      */         }
/* 1138 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1143 */     int size = this.fEntityStack.size();
/* 1144 */     for (int i = size; i >= 0; i--) {
/* 1145 */       Entity activeEntity = i == size ? this.fCurrentEntity : (Entity)this.fEntityStack.elementAt(i);
/*      */ 
/* 1148 */       if (activeEntity.name == entityName) {
/* 1149 */         String path = entityName;
/* 1150 */         for (int j = i + 1; j < size; j++) {
/* 1151 */           activeEntity = (Entity)this.fEntityStack.elementAt(j);
/* 1152 */           path = path + " -> " + activeEntity.name;
/*      */         }
/* 1154 */         path = path + " -> " + this.fCurrentEntity.name;
/* 1155 */         path = path + " -> " + entityName;
/* 1156 */         this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "RecursiveReference", new Object[] { entityName, path }, (short)2);
/*      */ 
/* 1161 */         if (this.fEntityHandler != null) {
/* 1162 */           this.fResourceIdentifier.clear();
/* 1163 */           String encoding = null;
/* 1164 */           if (external) {
/* 1165 */             Entity.ExternalEntity externalEntity = (Entity.ExternalEntity)entity;
/*      */ 
/* 1167 */             String extLitSysId = externalEntity.entityLocation != null ? externalEntity.entityLocation.getLiteralSystemId() : null;
/* 1168 */             String extBaseSysId = externalEntity.entityLocation != null ? externalEntity.entityLocation.getBaseSystemId() : null;
/* 1169 */             String expandedSystemId = expandSystemId(extLitSysId, extBaseSysId);
/* 1170 */             this.fResourceIdentifier.setValues(externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null, extLitSysId, extBaseSysId, expandedSystemId);
/*      */           }
/*      */ 
/* 1174 */           this.fEntityAugs.removeAllItems();
/* 1175 */           this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
/* 1176 */           this.fEntityHandler.startEntity(entityName, this.fResourceIdentifier, encoding, this.fEntityAugs);
/* 1177 */           this.fEntityAugs.removeAllItems();
/* 1178 */           this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
/* 1179 */           this.fEntityHandler.endEntity(entityName, this.fEntityAugs);
/*      */         }
/*      */ 
/* 1182 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1187 */     StaxXMLInputSource staxInputSource = null;
/* 1188 */     XMLInputSource xmlInputSource = null;
/*      */ 
/* 1190 */     if (external) {
/* 1191 */       Entity.ExternalEntity externalEntity = (Entity.ExternalEntity)entity;
/* 1192 */       staxInputSource = resolveEntityAsPerStax(externalEntity.entityLocation);
/*      */ 
/* 1198 */       xmlInputSource = staxInputSource.getXMLInputSource();
/*      */     }
/*      */     else
/*      */     {
/* 1202 */       Entity.InternalEntity internalEntity = (Entity.InternalEntity)entity;
/* 1203 */       Reader reader = new StringReader(internalEntity.text);
/* 1204 */       xmlInputSource = new XMLInputSource(null, null, null, reader, null);
/*      */     }
/*      */ 
/* 1208 */     startEntity(entityName, xmlInputSource, literal, external);
/*      */   }
/*      */ 
/*      */   public void startDocumentEntity(XMLInputSource xmlInputSource)
/*      */     throws IOException, XNIException
/*      */   {
/* 1223 */     startEntity(XMLEntity, xmlInputSource, false, true);
/*      */   }
/*      */ 
/*      */   public void startDTDEntity(XMLInputSource xmlInputSource)
/*      */     throws IOException, XNIException
/*      */   {
/* 1238 */     startEntity(DTDEntity, xmlInputSource, false, true);
/*      */   }
/*      */ 
/*      */   public void startExternalSubset()
/*      */   {
/* 1244 */     this.fInExternalSubset = true;
/*      */   }
/*      */ 
/*      */   public void endExternalSubset() {
/* 1248 */     this.fInExternalSubset = false;
/*      */   }
/*      */ 
/*      */   public void startEntity(String name, XMLInputSource xmlInputSource, boolean literal, boolean isExternal)
/*      */     throws IOException, XNIException
/*      */   {
/* 1271 */     String encoding = setupCurrentEntity(name, xmlInputSource, literal, isExternal);
/*      */ 
/* 1277 */     this.fEntityExpansionCount += 1;
/* 1278 */     if ((this.fSecurityManager != null) && (this.fEntityExpansionCount > this.fEntityExpansionLimit)) {
/* 1279 */       this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityExpansionLimitExceeded", new Object[] { new Integer(this.fEntityExpansionLimit) }, (short)2);
/*      */ 
/* 1286 */       this.fEntityExpansionCount = 0;
/*      */     }
/*      */ 
/* 1290 */     if (this.fEntityHandler != null)
/* 1291 */       this.fEntityHandler.startEntity(name, this.fResourceIdentifier, encoding, null);
/*      */   }
/*      */ 
/*      */   public Entity.ScannedEntity getCurrentEntity()
/*      */   {
/* 1302 */     return this.fCurrentEntity;
/*      */   }
/*      */ 
/*      */   public Entity.ScannedEntity getTopLevelEntity()
/*      */   {
/* 1310 */     return (Entity.ScannedEntity)(this.fEntityStack.empty() ? null : this.fEntityStack.elementAt(0));
/*      */   }
/*      */ 
/*      */   public void closeReaders()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void endEntity()
/*      */     throws IOException, XNIException
/*      */   {
/* 1335 */     Entity.ScannedEntity entity = this.fEntityStack.size() > 0 ? (Entity.ScannedEntity)this.fEntityStack.pop() : null;
/*      */ 
/* 1342 */     if (this.fCurrentEntity != null) {
/*      */       try
/*      */       {
/* 1345 */         this.fCurrentEntity.close();
/*      */       } catch (IOException ex) {
/* 1347 */         throw new XNIException(ex);
/*      */       }
/*      */     }
/*      */ 
/* 1351 */     if (this.fEntityHandler != null)
/*      */     {
/* 1353 */       if (entity == null) {
/* 1354 */         this.fEntityAugs.removeAllItems();
/* 1355 */         this.fEntityAugs.putItem("LAST_ENTITY", Boolean.TRUE);
/* 1356 */         this.fEntityHandler.endEntity(this.fCurrentEntity.name, this.fEntityAugs);
/* 1357 */         this.fEntityAugs.removeAllItems();
/*      */       } else {
/* 1359 */         this.fEntityHandler.endEntity(this.fCurrentEntity.name, null);
/*      */       }
/*      */     }
/*      */ 
/* 1363 */     boolean documentEntity = this.fCurrentEntity.name == XMLEntity;
/*      */ 
/* 1366 */     this.fCurrentEntity = entity;
/* 1367 */     this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
/*      */ 
/* 1373 */     if (((this.fCurrentEntity == null ? 1 : 0) & (!documentEntity ? 1 : 0)) != 0)
/* 1374 */       throw new EOFException();
/*      */   }
/*      */ 
/*      */   public void reset(PropertyManager propertyManager)
/*      */   {
/* 1391 */     this.fEntityStorage.reset(propertyManager);
/*      */ 
/* 1393 */     this.fEntityScanner.reset(propertyManager);
/*      */ 
/* 1395 */     this.fSymbolTable = ((SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
/* 1396 */     this.fErrorReporter = ((XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/*      */     try {
/* 1398 */       this.fStaxEntityResolver = ((StaxEntityResolverWrapper)propertyManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver"));
/*      */     } catch (XMLConfigurationException e) {
/* 1400 */       this.fStaxEntityResolver = null;
/*      */     }
/*      */ 
/* 1405 */     this.fEntities.clear();
/* 1406 */     this.fEntityStack.removeAllElements();
/* 1407 */     this.fCurrentEntity = null;
/* 1408 */     this.fValidation = false;
/* 1409 */     this.fExternalGeneralEntities = true;
/* 1410 */     this.fExternalParameterEntities = true;
/* 1411 */     this.fAllowJavaEncodings = true;
/*      */   }
/*      */ 
/*      */   public void reset(XMLComponentManager componentManager)
/*      */     throws XMLConfigurationException
/*      */   {
/* 1433 */     boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
/*      */ 
/* 1435 */     if (!parser_settings)
/*      */     {
/* 1437 */       reset();
/* 1438 */       if (this.fEntityScanner != null) {
/* 1439 */         this.fEntityScanner.reset(componentManager);
/*      */       }
/* 1441 */       if (this.fEntityStorage != null) {
/* 1442 */         this.fEntityStorage.reset(componentManager);
/*      */       }
/* 1444 */       return;
/*      */     }
/*      */ 
/* 1448 */     this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
/* 1449 */     this.fExternalGeneralEntities = componentManager.getFeature("http://xml.org/sax/features/external-general-entities", true);
/* 1450 */     this.fExternalParameterEntities = componentManager.getFeature("http://xml.org/sax/features/external-parameter-entities", true);
/*      */ 
/* 1453 */     this.fAllowJavaEncodings = componentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
/* 1454 */     this.fWarnDuplicateEntityDef = componentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
/* 1455 */     this.fStrictURI = componentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false);
/*      */ 
/* 1458 */     this.fSymbolTable = ((SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
/* 1459 */     this.fErrorReporter = ((XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
/* 1460 */     this.fEntityResolver = ((XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null));
/* 1461 */     this.fStaxEntityResolver = ((StaxEntityResolverWrapper)componentManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver", null));
/* 1462 */     this.fValidationManager = ((ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null));
/* 1463 */     this.fSecurityManager = ((SecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager", null));
/*      */ 
/* 1466 */     reset();
/*      */ 
/* 1468 */     this.fEntityScanner.reset(componentManager);
/* 1469 */     this.fEntityStorage.reset(componentManager);
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/* 1477 */     this.fEntityExpansionLimit = (this.fSecurityManager != null ? this.fSecurityManager.getEntityExpansionLimit() : 0);
/*      */ 
/* 1480 */     this.fStandalone = false;
/* 1481 */     this.fEntities.clear();
/* 1482 */     this.fEntityStack.removeAllElements();
/* 1483 */     this.fEntityExpansionCount = 0;
/*      */ 
/* 1485 */     this.fCurrentEntity = null;
/*      */ 
/* 1487 */     if (this.fXML10EntityScanner != null) {
/* 1488 */       this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
/*      */     }
/* 1490 */     if (this.fXML11EntityScanner != null) {
/* 1491 */       this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
/*      */     }
/*      */ 
/* 1516 */     this.fEntityHandler = null;
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedFeatures()
/*      */   {
/* 1529 */     return (String[])RECOGNIZED_FEATURES.clone();
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean state)
/*      */     throws XMLConfigurationException
/*      */   {
/* 1551 */     if (featureId.startsWith("http://apache.org/xml/features/")) {
/* 1552 */       int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
/* 1553 */       if ((suffixLength == "allow-java-encodings".length()) && (featureId.endsWith("allow-java-encodings")))
/*      */       {
/* 1555 */         this.fAllowJavaEncodings = state;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setProperty(String propertyId, Object value)
/*      */   {
/* 1578 */     if (propertyId.startsWith("http://apache.org/xml/properties/")) {
/* 1579 */       int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
/*      */ 
/* 1581 */       if ((suffixLength == "internal/symbol-table".length()) && (propertyId.endsWith("internal/symbol-table")))
/*      */       {
/* 1583 */         this.fSymbolTable = ((SymbolTable)value);
/* 1584 */         return;
/*      */       }
/* 1586 */       if ((suffixLength == "internal/error-reporter".length()) && (propertyId.endsWith("internal/error-reporter")))
/*      */       {
/* 1588 */         this.fErrorReporter = ((XMLErrorReporter)value);
/* 1589 */         return;
/*      */       }
/* 1591 */       if ((suffixLength == "internal/entity-resolver".length()) && (propertyId.endsWith("internal/entity-resolver")))
/*      */       {
/* 1593 */         this.fEntityResolver = ((XMLEntityResolver)value);
/* 1594 */         return;
/*      */       }
/* 1596 */       if ((suffixLength == "input-buffer-size".length()) && (propertyId.endsWith("input-buffer-size")))
/*      */       {
/* 1598 */         Integer bufferSize = (Integer)value;
/* 1599 */         if ((bufferSize != null) && (bufferSize.intValue() > 64))
/*      */         {
/* 1601 */           this.fBufferSize = bufferSize.intValue();
/* 1602 */           this.fEntityScanner.setBufferSize(this.fBufferSize);
/* 1603 */           this.fBufferPool.setExternalBufferSize(this.fBufferSize);
/*      */         }
/*      */       }
/* 1606 */       if ((suffixLength == "security-manager".length()) && (propertyId.endsWith("security-manager")))
/*      */       {
/* 1608 */         this.fSecurityManager = ((SecurityManager)value);
/* 1609 */         this.fEntityExpansionLimit = (this.fSecurityManager != null ? this.fSecurityManager.getEntityExpansionLimit() : 0);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String[] getRecognizedProperties()
/*      */   {
/* 1620 */     return (String[])RECOGNIZED_PROPERTIES.clone();
/*      */   }
/*      */ 
/*      */   public Boolean getFeatureDefault(String featureId)
/*      */   {
/* 1632 */     for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
/* 1633 */       if (RECOGNIZED_FEATURES[i].equals(featureId)) {
/* 1634 */         return FEATURE_DEFAULTS[i];
/*      */       }
/*      */     }
/* 1637 */     return null;
/*      */   }
/*      */ 
/*      */   public Object getPropertyDefault(String propertyId)
/*      */   {
/* 1650 */     for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
/* 1651 */       if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
/* 1652 */         return PROPERTY_DEFAULTS[i];
/*      */       }
/*      */     }
/* 1655 */     return null;
/*      */   }
/*      */ 
/*      */   public static String expandSystemId(String systemId)
/*      */   {
/* 1676 */     return expandSystemId(systemId, null);
/*      */   }
/*      */ 
/*      */   private static synchronized com.sun.org.apache.xerces.internal.util.URI getUserDir()
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1729 */     String userDir = "";
/*      */     try {
/* 1731 */       userDir = SecuritySupport.getSystemProperty("user.dir");
/*      */     }
/*      */     catch (SecurityException se)
/*      */     {
/*      */     }
/*      */ 
/* 1737 */     if (userDir.length() == 0) {
/* 1738 */       return new com.sun.org.apache.xerces.internal.util.URI("file", "", "", null, null);
/*      */     }
/*      */ 
/* 1741 */     if ((gUserDirURI != null) && (userDir.equals(gUserDir))) {
/* 1742 */       return gUserDirURI;
/*      */     }
/*      */ 
/* 1746 */     gUserDir = userDir;
/*      */ 
/* 1748 */     char separator = File.separatorChar;
/* 1749 */     userDir = userDir.replace(separator, '/');
/*      */ 
/* 1751 */     int len = userDir.length();
/* 1752 */     StringBuffer buffer = new StringBuffer(len * 3);
/*      */ 
/* 1754 */     if ((len >= 2) && (userDir.charAt(1) == ':')) {
/* 1755 */       int ch = Character.toUpperCase(userDir.charAt(0));
/* 1756 */       if ((ch >= 65) && (ch <= 90)) {
/* 1757 */         buffer.append('/');
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1762 */     for (int i = 0; 
/* 1763 */       i < len; i++) {
/* 1764 */       int ch = userDir.charAt(i);
/*      */ 
/* 1766 */       if (ch >= 128)
/*      */         break;
/* 1768 */       if (gNeedEscaping[ch] != 0) {
/* 1769 */         buffer.append('%');
/* 1770 */         buffer.append(gAfterEscaping1[ch]);
/* 1771 */         buffer.append(gAfterEscaping2[ch]);
/*      */       }
/*      */       else
/*      */       {
/* 1775 */         buffer.append((char)ch);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1780 */     if (i < len)
/*      */     {
/* 1782 */       byte[] bytes = null;
/*      */       try
/*      */       {
/* 1785 */         bytes = userDir.substring(i).getBytes("UTF-8");
/*      */       }
/*      */       catch (UnsupportedEncodingException e) {
/* 1788 */         return new com.sun.org.apache.xerces.internal.util.URI("file", "", userDir, null, null);
/*      */       }
/* 1790 */       len = bytes.length;
/*      */ 
/* 1793 */       for (i = 0; i < len; i++) {
/* 1794 */         byte b = bytes[i];
/*      */ 
/* 1796 */         if (b < 0) {
/* 1797 */           int ch = b + 256;
/* 1798 */           buffer.append('%');
/* 1799 */           buffer.append(gHexChs[(ch >> 4)]);
/* 1800 */           buffer.append(gHexChs[(ch & 0xF)]);
/*      */         }
/* 1802 */         else if (gNeedEscaping[b] != 0) {
/* 1803 */           buffer.append('%');
/* 1804 */           buffer.append(gAfterEscaping1[b]);
/* 1805 */           buffer.append(gAfterEscaping2[b]);
/*      */         }
/*      */         else {
/* 1808 */           buffer.append((char)b);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1814 */     if (!userDir.endsWith("/")) {
/* 1815 */       buffer.append('/');
/*      */     }
/* 1817 */     gUserDirURI = new com.sun.org.apache.xerces.internal.util.URI("file", "", buffer.toString(), null, null);
/*      */ 
/* 1819 */     return gUserDirURI;
/*      */   }
/*      */ 
/*      */   public static void absolutizeAgainstUserDir(com.sun.org.apache.xerces.internal.util.URI uri)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1831 */     uri.absolutize(getUserDir());
/*      */   }
/*      */ 
/*      */   public static String expandSystemId(String systemId, String baseSystemId)
/*      */   {
/* 1850 */     if ((systemId == null) || (systemId.length() == 0)) {
/* 1851 */       return systemId;
/*      */     }
/*      */     try
/*      */     {
/* 1855 */       com.sun.org.apache.xerces.internal.util.URI uri = new com.sun.org.apache.xerces.internal.util.URI(systemId);
/* 1856 */       if (uri != null) {
/* 1857 */         return systemId;
/*      */       }
/*      */     }
/*      */     catch (URI.MalformedURIException e)
/*      */     {
/*      */     }
/* 1863 */     String id = fixURI(systemId);
/*      */ 
/* 1866 */     com.sun.org.apache.xerces.internal.util.URI base = null;
/* 1867 */     com.sun.org.apache.xerces.internal.util.URI uri = null;
/*      */     try {
/* 1869 */       if ((baseSystemId == null) || (baseSystemId.length() == 0) || (baseSystemId.equals(systemId)))
/*      */       {
/* 1871 */         String dir = getUserDir().toString();
/* 1872 */         base = new com.sun.org.apache.xerces.internal.util.URI("file", "", dir, null, null);
/*      */       } else {
/*      */         try {
/* 1875 */           base = new com.sun.org.apache.xerces.internal.util.URI(fixURI(baseSystemId));
/*      */         } catch (URI.MalformedURIException e) {
/* 1877 */           if (baseSystemId.indexOf(':') != -1)
/*      */           {
/* 1880 */             base = new com.sun.org.apache.xerces.internal.util.URI("file", "", fixURI(baseSystemId), null, null);
/*      */           } else {
/* 1882 */             String dir = getUserDir().toString();
/* 1883 */             dir = dir + fixURI(baseSystemId);
/* 1884 */             base = new com.sun.org.apache.xerces.internal.util.URI("file", "", dir, null, null);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1889 */       uri = new com.sun.org.apache.xerces.internal.util.URI(base, id);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*      */ 
/* 1895 */     if (uri == null) {
/* 1896 */       return systemId;
/*      */     }
/* 1898 */     return uri.toString();
/*      */   }
/*      */ 
/*      */   public static String expandSystemId(String systemId, String baseSystemId, boolean strict)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 1921 */     if (systemId == null) {
/* 1922 */       return null;
/*      */     }
/*      */ 
/* 1926 */     if (strict)
/*      */     {
/* 1931 */       if (systemId == null) {
/* 1932 */         return null;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1937 */         new com.sun.org.apache.xerces.internal.util.URI(systemId);
/* 1938 */         return systemId;
/*      */       }
/*      */       catch (URI.MalformedURIException ex)
/*      */       {
/* 1942 */         com.sun.org.apache.xerces.internal.util.URI base = null;
/*      */ 
/* 1944 */         if ((baseSystemId == null) || (baseSystemId.length() == 0)) {
/* 1945 */           base = new com.sun.org.apache.xerces.internal.util.URI("file", "", getUserDir().toString(), null, null);
/*      */         }
/*      */         else {
/*      */           try
/*      */           {
/* 1950 */             base = new com.sun.org.apache.xerces.internal.util.URI(baseSystemId);
/*      */           }
/*      */           catch (URI.MalformedURIException e)
/*      */           {
/* 1954 */             String dir = getUserDir().toString();
/* 1955 */             dir = dir + baseSystemId;
/* 1956 */             base = new com.sun.org.apache.xerces.internal.util.URI("file", "", dir, null, null);
/*      */           }
/*      */         }
/*      */ 
/* 1960 */         com.sun.org.apache.xerces.internal.util.URI uri = new com.sun.org.apache.xerces.internal.util.URI(base, systemId);
/*      */ 
/* 1962 */         return uri.toString();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1969 */       return expandSystemIdStrictOff(systemId, baseSystemId);
/*      */     }
/*      */     catch (URI.MalformedURIException e)
/*      */     {
/*      */       try
/*      */       {
/* 1978 */         return expandSystemIdStrictOff1(systemId, baseSystemId);
/*      */       }
/*      */       catch (URISyntaxException ex)
/*      */       {
/* 1984 */         if (systemId.length() == 0) {
/* 1985 */           return systemId;
/*      */         }
/*      */ 
/* 1989 */         String id = fixURI(systemId);
/*      */ 
/* 1992 */         com.sun.org.apache.xerces.internal.util.URI base = null;
/* 1993 */         com.sun.org.apache.xerces.internal.util.URI uri = null;
/*      */         try {
/* 1995 */           if ((baseSystemId == null) || (baseSystemId.length() == 0) || (baseSystemId.equals(systemId)))
/*      */           {
/* 1997 */             base = getUserDir();
/*      */           }
/*      */           else {
/*      */             try {
/* 2001 */               base = new com.sun.org.apache.xerces.internal.util.URI(fixURI(baseSystemId).trim());
/*      */             }
/*      */             catch (URI.MalformedURIException e) {
/* 2004 */               if (baseSystemId.indexOf(':') != -1)
/*      */               {
/* 2007 */                 base = new com.sun.org.apache.xerces.internal.util.URI("file", "", fixURI(baseSystemId).trim(), null, null);
/*      */               }
/*      */               else {
/* 2010 */                 base = new com.sun.org.apache.xerces.internal.util.URI(getUserDir(), fixURI(baseSystemId));
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 2015 */           uri = new com.sun.org.apache.xerces.internal.util.URI(base, id.trim());
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*      */         }
/*      */ 
/* 2022 */         if (uri == null) {
/* 2023 */           return systemId;
/*      */         }
/* 2025 */         return uri.toString();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static String expandSystemIdStrictOn(String systemId, String baseSystemId)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 2035 */     com.sun.org.apache.xerces.internal.util.URI systemURI = new com.sun.org.apache.xerces.internal.util.URI(systemId, true);
/*      */ 
/* 2037 */     if (systemURI.isAbsoluteURI()) {
/* 2038 */       return systemId;
/*      */     }
/*      */ 
/* 2042 */     com.sun.org.apache.xerces.internal.util.URI baseURI = null;
/* 2043 */     if ((baseSystemId == null) || (baseSystemId.length() == 0)) {
/* 2044 */       baseURI = getUserDir();
/*      */     }
/*      */     else {
/* 2047 */       baseURI = new com.sun.org.apache.xerces.internal.util.URI(baseSystemId, true);
/* 2048 */       if (!baseURI.isAbsoluteURI())
/*      */       {
/* 2050 */         baseURI.absolutize(getUserDir());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2055 */     systemURI.absolutize(baseURI);
/*      */ 
/* 2058 */     return systemURI.toString();
/*      */   }
/*      */ 
/*      */   public static void setInstanceFollowRedirects(HttpURLConnection urlCon, boolean followRedirects)
/*      */   {
/*      */     try
/*      */     {
/* 2070 */       Method method = HttpURLConnection.class.getMethod("setInstanceFollowRedirects", new Class[] { Boolean.TYPE });
/* 2071 */       method.invoke(urlCon, new Object[] { followRedirects ? Boolean.TRUE : Boolean.FALSE });
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private static String expandSystemIdStrictOff(String systemId, String baseSystemId)
/*      */     throws URI.MalformedURIException
/*      */   {
/* 2084 */     com.sun.org.apache.xerces.internal.util.URI systemURI = new com.sun.org.apache.xerces.internal.util.URI(systemId, true);
/*      */ 
/* 2086 */     if (systemURI.isAbsoluteURI()) {
/* 2087 */       if (systemURI.getScheme().length() > 1) {
/* 2088 */         return systemId;
/*      */       }
/*      */ 
/* 2096 */       throw new URI.MalformedURIException();
/*      */     }
/*      */ 
/* 2100 */     com.sun.org.apache.xerces.internal.util.URI baseURI = null;
/* 2101 */     if ((baseSystemId == null) || (baseSystemId.length() == 0)) {
/* 2102 */       baseURI = getUserDir();
/*      */     }
/*      */     else {
/* 2105 */       baseURI = new com.sun.org.apache.xerces.internal.util.URI(baseSystemId, true);
/* 2106 */       if (!baseURI.isAbsoluteURI())
/*      */       {
/* 2108 */         baseURI.absolutize(getUserDir());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2113 */     systemURI.absolutize(baseURI);
/*      */ 
/* 2116 */     return systemURI.toString();
/*      */   }
/*      */ 
/*      */   private static String expandSystemIdStrictOff1(String systemId, String baseSystemId)
/*      */     throws URISyntaxException, URI.MalformedURIException
/*      */   {
/* 2125 */     java.net.URI systemURI = new java.net.URI(systemId);
/*      */ 
/* 2127 */     if (systemURI.isAbsolute()) {
/* 2128 */       if (systemURI.getScheme().length() > 1) {
/* 2129 */         return systemId;
/*      */       }
/*      */ 
/* 2137 */       throw new URISyntaxException(systemId, "the scheme's length is only one character");
/*      */     }
/*      */ 
/* 2141 */     com.sun.org.apache.xerces.internal.util.URI baseURI = null;
/* 2142 */     if ((baseSystemId == null) || (baseSystemId.length() == 0)) {
/* 2143 */       baseURI = getUserDir();
/*      */     }
/*      */     else {
/* 2146 */       baseURI = new com.sun.org.apache.xerces.internal.util.URI(baseSystemId, true);
/* 2147 */       if (!baseURI.isAbsoluteURI())
/*      */       {
/* 2149 */         baseURI.absolutize(getUserDir());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2155 */     systemURI = new java.net.URI(baseURI.toString()).resolve(systemURI);
/*      */ 
/* 2158 */     return systemURI.toString();
/*      */   }
/*      */ 
/*      */   protected Object[] getEncodingName(byte[] b4, int count)
/*      */   {
/* 2181 */     if (count < 2) {
/* 2182 */       return this.defaultEncoding;
/*      */     }
/*      */ 
/* 2186 */     int b0 = b4[0] & 0xFF;
/* 2187 */     int b1 = b4[1] & 0xFF;
/* 2188 */     if ((b0 == 254) && (b1 == 255))
/*      */     {
/* 2190 */       return new Object[] { "UTF-16BE", new Boolean(true) };
/*      */     }
/* 2192 */     if ((b0 == 255) && (b1 == 254))
/*      */     {
/* 2194 */       return new Object[] { "UTF-16LE", new Boolean(false) };
/*      */     }
/*      */ 
/* 2199 */     if (count < 3) {
/* 2200 */       return this.defaultEncoding;
/*      */     }
/*      */ 
/* 2204 */     int b2 = b4[2] & 0xFF;
/* 2205 */     if ((b0 == 239) && (b1 == 187) && (b2 == 191)) {
/* 2206 */       return this.defaultEncoding;
/*      */     }
/*      */ 
/* 2211 */     if (count < 4) {
/* 2212 */       return this.defaultEncoding;
/*      */     }
/*      */ 
/* 2216 */     int b3 = b4[3] & 0xFF;
/* 2217 */     if ((b0 == 0) && (b1 == 0) && (b2 == 0) && (b3 == 60))
/*      */     {
/* 2219 */       return new Object[] { "ISO-10646-UCS-4", new Boolean(true) };
/*      */     }
/* 2221 */     if ((b0 == 60) && (b1 == 0) && (b2 == 0) && (b3 == 0))
/*      */     {
/* 2223 */       return new Object[] { "ISO-10646-UCS-4", new Boolean(false) };
/*      */     }
/* 2225 */     if ((b0 == 0) && (b1 == 0) && (b2 == 60) && (b3 == 0))
/*      */     {
/* 2228 */       return new Object[] { "ISO-10646-UCS-4", null };
/*      */     }
/* 2230 */     if ((b0 == 0) && (b1 == 60) && (b2 == 0) && (b3 == 0))
/*      */     {
/* 2233 */       return new Object[] { "ISO-10646-UCS-4", null };
/*      */     }
/* 2235 */     if ((b0 == 0) && (b1 == 60) && (b2 == 0) && (b3 == 63))
/*      */     {
/* 2239 */       return new Object[] { "UTF-16BE", new Boolean(true) };
/*      */     }
/* 2241 */     if ((b0 == 60) && (b1 == 0) && (b2 == 63) && (b3 == 0))
/*      */     {
/* 2244 */       return new Object[] { "UTF-16LE", new Boolean(false) };
/*      */     }
/* 2246 */     if ((b0 == 76) && (b1 == 111) && (b2 == 167) && (b3 == 148))
/*      */     {
/* 2249 */       return new Object[] { "CP037", null };
/*      */     }
/*      */ 
/* 2252 */     return this.defaultEncoding;
/*      */   }
/*      */ 
/*      */   protected Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian)
/*      */     throws IOException
/*      */   {
/* 2276 */     if (encoding == null) {
/* 2277 */       encoding = "UTF-8";
/*      */     }
/*      */ 
/* 2281 */     String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
/* 2282 */     if (ENCODING.equals("UTF-8"))
/*      */     {
/* 2286 */       return new UTF8Reader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
/*      */     }
/* 2288 */     if (ENCODING.equals("US-ASCII"))
/*      */     {
/* 2292 */       return new ASCIIReader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
/*      */     }
/* 2294 */     if (ENCODING.equals("ISO-10646-UCS-4")) {
/* 2295 */       if (isBigEndian != null) {
/* 2296 */         boolean isBE = isBigEndian.booleanValue();
/* 2297 */         if (isBE) {
/* 2298 */           return new UCSReader(inputStream, (short)8);
/*      */         }
/* 2300 */         return new UCSReader(inputStream, (short)4);
/*      */       }
/*      */ 
/* 2303 */       this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
/*      */     }
/*      */ 
/* 2309 */     if (ENCODING.equals("ISO-10646-UCS-2")) {
/* 2310 */       if (isBigEndian != null) {
/* 2311 */         boolean isBE = isBigEndian.booleanValue();
/* 2312 */         if (isBE) {
/* 2313 */           return new UCSReader(inputStream, (short)2);
/*      */         }
/* 2315 */         return new UCSReader(inputStream, (short)1);
/*      */       }
/*      */ 
/* 2318 */       this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
/*      */     }
/*      */ 
/* 2326 */     boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
/* 2327 */     boolean validJava = XMLChar.isValidJavaEncoding(encoding);
/* 2328 */     if ((!validIANA) || ((this.fAllowJavaEncodings) && (!validJava))) {
/* 2329 */       this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
/*      */ 
/* 2341 */       encoding = "ISO-8859-1";
/*      */     }
/*      */ 
/* 2345 */     String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
/* 2346 */     if (javaEncoding == null) {
/* 2347 */       if (this.fAllowJavaEncodings) {
/* 2348 */         javaEncoding = encoding;
/*      */       } else {
/* 2350 */         this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
/*      */ 
/* 2355 */         javaEncoding = "ISO8859_1";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2365 */     return new BufferedReader(new InputStreamReader(inputStream, javaEncoding));
/*      */   }
/*      */ 
/*      */   public String getPublicId()
/*      */   {
/* 2381 */     return (this.fCurrentEntity != null) && (this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null;
/*      */   }
/*      */ 
/*      */   public String getExpandedSystemId()
/*      */   {
/* 2398 */     if (this.fCurrentEntity != null) {
/* 2399 */       if ((this.fCurrentEntity.entityLocation != null) && (this.fCurrentEntity.entityLocation.getExpandedSystemId() != null))
/*      */       {
/* 2401 */         return this.fCurrentEntity.entityLocation.getExpandedSystemId();
/*      */       }
/*      */ 
/* 2404 */       int size = this.fEntityStack.size();
/* 2405 */       for (int i = size - 1; i >= 0; i--) {
/* 2406 */         Entity.ScannedEntity externalEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(i);
/*      */ 
/* 2409 */         if ((externalEntity.entityLocation != null) && (externalEntity.entityLocation.getExpandedSystemId() != null))
/*      */         {
/* 2411 */           return externalEntity.entityLocation.getExpandedSystemId();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2416 */     return null;
/*      */   }
/*      */ 
/*      */   public String getLiteralSystemId()
/*      */   {
/* 2430 */     if (this.fCurrentEntity != null) {
/* 2431 */       if ((this.fCurrentEntity.entityLocation != null) && (this.fCurrentEntity.entityLocation.getLiteralSystemId() != null))
/*      */       {
/* 2433 */         return this.fCurrentEntity.entityLocation.getLiteralSystemId();
/*      */       }
/*      */ 
/* 2436 */       int size = this.fEntityStack.size();
/* 2437 */       for (int i = size - 1; i >= 0; i--) {
/* 2438 */         Entity.ScannedEntity externalEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(i);
/*      */ 
/* 2441 */         if ((externalEntity.entityLocation != null) && (externalEntity.entityLocation.getLiteralSystemId() != null))
/*      */         {
/* 2443 */           return externalEntity.entityLocation.getLiteralSystemId();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2448 */     return null;
/*      */   }
/*      */ 
/*      */   public int getLineNumber()
/*      */   {
/* 2470 */     if (this.fCurrentEntity != null) {
/* 2471 */       if (this.fCurrentEntity.isExternal()) {
/* 2472 */         return this.fCurrentEntity.lineNumber;
/*      */       }
/*      */ 
/* 2475 */       int size = this.fEntityStack.size();
/* 2476 */       for (int i = size - 1; i > 0; i--) {
/* 2477 */         Entity.ScannedEntity firstExternalEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(i);
/* 2478 */         if (firstExternalEntity.isExternal()) {
/* 2479 */           return firstExternalEntity.lineNumber;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2485 */     return -1;
/*      */   }
/*      */ 
/*      */   public int getColumnNumber()
/*      */   {
/* 2512 */     if (this.fCurrentEntity != null) {
/* 2513 */       if (this.fCurrentEntity.isExternal()) {
/* 2514 */         return this.fCurrentEntity.columnNumber;
/*      */       }
/*      */ 
/* 2517 */       int size = this.fEntityStack.size();
/* 2518 */       for (int i = size - 1; i > 0; i--) {
/* 2519 */         Entity.ScannedEntity firstExternalEntity = (Entity.ScannedEntity)this.fEntityStack.elementAt(i);
/* 2520 */         if (firstExternalEntity.isExternal()) {
/* 2521 */           return firstExternalEntity.columnNumber;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2527 */     return -1;
/*      */   }
/*      */ 
/*      */   protected static String fixURI(String str)
/*      */   {
/* 2545 */     str = str.replace(File.separatorChar, '/');
/*      */ 
/* 2548 */     if (str.length() >= 2) {
/* 2549 */       char ch1 = str.charAt(1);
/*      */ 
/* 2551 */       if (ch1 == ':') {
/* 2552 */         char ch0 = Character.toUpperCase(str.charAt(0));
/* 2553 */         if ((ch0 >= 'A') && (ch0 <= 'Z')) {
/* 2554 */           str = "/" + str;
/*      */         }
/*      */ 
/*      */       }
/* 2558 */       else if ((ch1 == '/') && (str.charAt(0) == '/')) {
/* 2559 */         str = "file:" + str;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2567 */     int pos = str.indexOf(' ');
/* 2568 */     if (pos >= 0) {
/* 2569 */       StringBuilder sb = new StringBuilder(str.length());
/*      */ 
/* 2571 */       for (int i = 0; i < pos; i++) {
/* 2572 */         sb.append(str.charAt(i));
/*      */       }
/* 2574 */       sb.append("%20");
/*      */ 
/* 2576 */       for (int i = pos + 1; i < str.length(); i++) {
/* 2577 */         if (str.charAt(i) == ' ')
/* 2578 */           sb.append("%20");
/*      */         else
/* 2580 */           sb.append(str.charAt(i));
/*      */       }
/* 2582 */       str = sb.toString();
/*      */     }
/*      */ 
/* 2586 */     return str;
/*      */   }
/*      */ 
/*      */   final void print()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void test()
/*      */   {
/* 2939 */     this.fEntityStorage.addExternalEntity("entityUsecase1", null, "/space/home/stax/sun/6thJan2004/zephyr/data/test.txt", "/space/home/stax/sun/6thJan2004/zephyr/data/entity.xml");
/*      */ 
/* 2944 */     this.fEntityStorage.addInternalEntity("entityUsecase2", "<Test>value</Test>");
/* 2945 */     this.fEntityStorage.addInternalEntity("entityUsecase3", "value3");
/* 2946 */     this.fEntityStorage.addInternalEntity("text", "Hello World.");
/* 2947 */     this.fEntityStorage.addInternalEntity("empty-element", "<foo/>");
/* 2948 */     this.fEntityStorage.addInternalEntity("balanced-element", "<foo></foo>");
/* 2949 */     this.fEntityStorage.addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
/* 2950 */     this.fEntityStorage.addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
/* 2951 */     this.fEntityStorage.addInternalEntity("unbalanced-entity", "<foo>");
/* 2952 */     this.fEntityStorage.addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
/* 2953 */     this.fEntityStorage.addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
/* 2954 */     this.fEntityStorage.addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
/* 2955 */     this.fEntityStorage.addInternalEntity("ch", "&#x00A9;");
/* 2956 */     this.fEntityStorage.addInternalEntity("ch1", "&#84;");
/* 2957 */     this.fEntityStorage.addInternalEntity("% ch2", "param");
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/* 1697 */     for (int i = 0; i <= 31; i++) {
/* 1698 */       gNeedEscaping[i] = true;
/* 1699 */       gAfterEscaping1[i] = gHexChs[(i >> 4)];
/* 1700 */       gAfterEscaping2[i] = gHexChs[(i & 0xF)];
/*      */     }
/* 1702 */     gNeedEscaping[127] = true;
/* 1703 */     gAfterEscaping1[127] = '7';
/* 1704 */     gAfterEscaping2[127] = 'F';
/* 1705 */     char[] escChs = { ' ', '<', '>', '#', '%', '"', '{', '}', '|', '\\', '^', '~', '[', ']', '`' };
/*      */ 
/* 1707 */     int len = escChs.length;
/*      */ 
/* 1709 */     for (int i = 0; i < len; i++) {
/* 1710 */       char ch = escChs[i];
/* 1711 */       gNeedEscaping[ch] = true;
/* 1712 */       gAfterEscaping1[ch] = gHexChs[(ch >> '\004')];
/* 1713 */       gAfterEscaping2[ch] = gHexChs[(ch & 0xF)];
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CharacterBuffer
/*      */   {
/*      */     private char[] ch;
/*      */     private boolean isExternal;
/*      */ 
/*      */     public CharacterBuffer(boolean isExternal, int size)
/*      */     {
/* 2664 */       this.isExternal = isExternal;
/* 2665 */       this.ch = new char[size];
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CharacterBufferPool
/*      */   {
/*      */     private static final int DEFAULT_POOL_SIZE = 3;
/*      */     private XMLEntityManager.CharacterBuffer[] fInternalBufferPool;
/*      */     private XMLEntityManager.CharacterBuffer[] fExternalBufferPool;
/*      */     private int fExternalBufferSize;
/*      */     private int fInternalBufferSize;
/*      */     private int poolSize;
/*      */     private int fInternalTop;
/*      */     private int fExternalTop;
/*      */ 
/*      */     public CharacterBufferPool(int externalBufferSize, int internalBufferSize)
/*      */     {
/* 2693 */       this(3, externalBufferSize, internalBufferSize);
/*      */     }
/*      */ 
/*      */     public CharacterBufferPool(int poolSize, int externalBufferSize, int internalBufferSize) {
/* 2697 */       this.fExternalBufferSize = externalBufferSize;
/* 2698 */       this.fInternalBufferSize = internalBufferSize;
/* 2699 */       this.poolSize = poolSize;
/* 2700 */       init();
/*      */     }
/*      */ 
/*      */     private void init()
/*      */     {
/* 2705 */       this.fInternalBufferPool = new XMLEntityManager.CharacterBuffer[this.poolSize];
/* 2706 */       this.fExternalBufferPool = new XMLEntityManager.CharacterBuffer[this.poolSize];
/* 2707 */       this.fInternalTop = -1;
/* 2708 */       this.fExternalTop = -1;
/*      */     }
/*      */ 
/*      */     public XMLEntityManager.CharacterBuffer getBuffer(boolean external)
/*      */     {
/* 2713 */       if (external) {
/* 2714 */         if (this.fExternalTop > -1) {
/* 2715 */           return this.fExternalBufferPool[(this.fExternalTop--)];
/*      */         }
/*      */ 
/* 2718 */         return new XMLEntityManager.CharacterBuffer(true, this.fExternalBufferSize);
/*      */       }
/*      */ 
/* 2722 */       if (this.fInternalTop > -1) {
/* 2723 */         return this.fInternalBufferPool[(this.fInternalTop--)];
/*      */       }
/*      */ 
/* 2726 */       return new XMLEntityManager.CharacterBuffer(false, this.fInternalBufferSize);
/*      */     }
/*      */ 
/*      */     public void returnToPool(XMLEntityManager.CharacterBuffer buffer)
/*      */     {
/* 2733 */       if (buffer.isExternal) {
/* 2734 */         if (this.fExternalTop < this.fExternalBufferPool.length - 1) {
/* 2735 */           this.fExternalBufferPool[(++this.fExternalTop)] = buffer;
/*      */         }
/*      */       }
/* 2738 */       else if (this.fInternalTop < this.fInternalBufferPool.length - 1)
/* 2739 */         this.fInternalBufferPool[(++this.fInternalTop)] = buffer;
/*      */     }
/*      */ 
/*      */     public void setExternalBufferSize(int bufferSize)
/*      */     {
/* 2745 */       this.fExternalBufferSize = bufferSize;
/* 2746 */       this.fExternalBufferPool = new XMLEntityManager.CharacterBuffer[this.poolSize];
/* 2747 */       this.fExternalTop = -1;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final class RewindableInputStream extends InputStream
/*      */   {
/*      */     private InputStream fInputStream;
/*      */     private byte[] fData;
/*      */     private int fStartOffset;
/*      */     private int fEndOffset;
/*      */     private int fOffset;
/*      */     private int fLength;
/*      */     private int fMark;
/*      */ 
/*      */     public RewindableInputStream(InputStream is)
/*      */     {
/* 2785 */       this.fData = new byte[64];
/* 2786 */       this.fInputStream = is;
/* 2787 */       this.fStartOffset = 0;
/* 2788 */       this.fEndOffset = -1;
/* 2789 */       this.fOffset = 0;
/* 2790 */       this.fLength = 0;
/* 2791 */       this.fMark = 0;
/*      */     }
/*      */ 
/*      */     public void setStartOffset(int offset) {
/* 2795 */       this.fStartOffset = offset;
/*      */     }
/*      */ 
/*      */     public void rewind() {
/* 2799 */       this.fOffset = this.fStartOffset;
/*      */     }
/*      */ 
/*      */     public int read() throws IOException {
/* 2803 */       int b = 0;
/* 2804 */       if (this.fOffset < this.fLength) {
/* 2805 */         return this.fData[(this.fOffset++)] & 0xFF;
/*      */       }
/* 2807 */       if (this.fOffset == this.fEndOffset) {
/* 2808 */         return -1;
/*      */       }
/* 2810 */       if (this.fOffset == this.fData.length) {
/* 2811 */         byte[] newData = new byte[this.fOffset << 1];
/* 2812 */         System.arraycopy(this.fData, 0, newData, 0, this.fOffset);
/* 2813 */         this.fData = newData;
/*      */       }
/* 2815 */       b = this.fInputStream.read();
/* 2816 */       if (b == -1) {
/* 2817 */         this.fEndOffset = this.fOffset;
/* 2818 */         return -1;
/*      */       }
/* 2820 */       this.fData[(this.fLength++)] = ((byte)b);
/* 2821 */       this.fOffset += 1;
/* 2822 */       return b & 0xFF;
/*      */     }
/*      */ 
/*      */     public int read(byte[] b, int off, int len) throws IOException {
/* 2826 */       int bytesLeft = this.fLength - this.fOffset;
/* 2827 */       if (bytesLeft == 0) {
/* 2828 */         if (this.fOffset == this.fEndOffset) {
/* 2829 */           return -1;
/*      */         }
/*      */ 
/* 2837 */         if ((XMLEntityManager.this.fCurrentEntity.mayReadChunks) || (!XMLEntityManager.this.fCurrentEntity.xmlDeclChunkRead))
/*      */         {
/* 2839 */           if (!XMLEntityManager.this.fCurrentEntity.xmlDeclChunkRead)
/*      */           {
/* 2841 */             XMLEntityManager.this.fCurrentEntity.xmlDeclChunkRead = true;
/* 2842 */             len = 28;
/*      */           }
/* 2844 */           return this.fInputStream.read(b, off, len);
/*      */         }
/*      */ 
/* 2847 */         int returnedVal = read();
/* 2848 */         if (returnedVal == -1) {
/* 2849 */           this.fEndOffset = this.fOffset;
/* 2850 */           return -1;
/*      */         }
/* 2852 */         b[off] = ((byte)returnedVal);
/* 2853 */         return 1;
/*      */       }
/*      */ 
/* 2856 */       if (len < bytesLeft) {
/* 2857 */         if (len <= 0)
/* 2858 */           return 0;
/*      */       }
/*      */       else {
/* 2861 */         len = bytesLeft;
/*      */       }
/* 2863 */       if (b != null) {
/* 2864 */         System.arraycopy(this.fData, this.fOffset, b, off, len);
/*      */       }
/* 2866 */       this.fOffset += len;
/* 2867 */       return len;
/*      */     }
/*      */ 
/*      */     public long skip(long n)
/*      */       throws IOException
/*      */     {
/* 2873 */       if (n <= 0L) {
/* 2874 */         return 0L;
/*      */       }
/* 2876 */       int bytesLeft = this.fLength - this.fOffset;
/* 2877 */       if (bytesLeft == 0) {
/* 2878 */         if (this.fOffset == this.fEndOffset) {
/* 2879 */           return 0L;
/*      */         }
/* 2881 */         return this.fInputStream.skip(n);
/*      */       }
/* 2883 */       if (n <= bytesLeft) {
/* 2884 */         this.fOffset = ((int)(this.fOffset + n));
/* 2885 */         return n;
/*      */       }
/* 2887 */       this.fOffset += bytesLeft;
/* 2888 */       if (this.fOffset == this.fEndOffset) {
/* 2889 */         return bytesLeft;
/*      */       }
/* 2891 */       n -= bytesLeft;
/*      */ 
/* 2900 */       return this.fInputStream.skip(n) + bytesLeft;
/*      */     }
/*      */ 
/*      */     public int available() throws IOException {
/* 2904 */       int bytesLeft = this.fLength - this.fOffset;
/* 2905 */       if (bytesLeft == 0) {
/* 2906 */         if (this.fOffset == this.fEndOffset) {
/* 2907 */           return -1;
/*      */         }
/* 2909 */         return XMLEntityManager.this.fCurrentEntity.mayReadChunks ? this.fInputStream.available() : 0;
/*      */       }
/*      */ 
/* 2912 */       return bytesLeft;
/*      */     }
/*      */ 
/*      */     public void mark(int howMuch) {
/* 2916 */       this.fMark = this.fOffset;
/*      */     }
/*      */ 
/*      */     public void reset() {
/* 2920 */       this.fOffset = this.fMark;
/*      */     }
/*      */ 
/*      */     public boolean markSupported()
/*      */     {
/* 2925 */       return true;
/*      */     }
/*      */ 
/*      */     public void close() throws IOException {
/* 2929 */       if (this.fInputStream != null) {
/* 2930 */         this.fInputStream.close();
/* 2931 */         this.fInputStream = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.XMLEntityManager
 * JD-Core Version:    0.6.2
 */