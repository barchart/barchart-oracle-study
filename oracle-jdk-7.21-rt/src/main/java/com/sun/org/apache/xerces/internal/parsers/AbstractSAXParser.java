/*      */ package com.sun.org.apache.xerces.internal.parsers;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.util.EntityResolver2Wrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
/*      */ import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
/*      */ import com.sun.org.apache.xerces.internal.util.Status;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolHash;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLSymbols;
/*      */ import com.sun.org.apache.xerces.internal.xni.Augmentations;
/*      */ import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
/*      */ import com.sun.org.apache.xerces.internal.xni.QName;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLLocator;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
/*      */ import com.sun.org.apache.xerces.internal.xni.XMLString;
/*      */ import com.sun.org.apache.xerces.internal.xni.XNIException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
/*      */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*      */ import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
/*      */ import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
/*      */ import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
/*      */ import java.io.IOException;
/*      */ import java.util.Locale;
/*      */ import org.xml.sax.AttributeList;
/*      */ import org.xml.sax.ContentHandler;
/*      */ import org.xml.sax.DTDHandler;
/*      */ import org.xml.sax.DocumentHandler;
/*      */ import org.xml.sax.EntityResolver;
/*      */ import org.xml.sax.ErrorHandler;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.Parser;
/*      */ import org.xml.sax.SAXException;
/*      */ import org.xml.sax.SAXNotRecognizedException;
/*      */ import org.xml.sax.SAXNotSupportedException;
/*      */ import org.xml.sax.SAXParseException;
/*      */ import org.xml.sax.XMLReader;
/*      */ import org.xml.sax.ext.Attributes2;
/*      */ import org.xml.sax.ext.DeclHandler;
/*      */ import org.xml.sax.ext.EntityResolver2;
/*      */ import org.xml.sax.ext.LexicalHandler;
/*      */ import org.xml.sax.ext.Locator2;
/*      */ import org.xml.sax.helpers.LocatorImpl;
/*      */ 
/*      */ public abstract class AbstractSAXParser extends AbstractXMLDocumentParser
/*      */   implements PSVIProvider, Parser, XMLReader
/*      */ {
/*      */   protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
/*      */   protected static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
/*      */   protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
/*      */   protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
/*  114 */   private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/namespace-prefixes", "http://xml.org/sax/features/string-interning" };
/*      */   protected static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
/*      */   protected static final String DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";
/*      */   protected static final String DOM_NODE = "http://xml.org/sax/properties/dom-node";
/*  135 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://xml.org/sax/properties/lexical-handler", "http://xml.org/sax/properties/declaration-handler", "http://xml.org/sax/properties/dom-node" };
/*      */   protected boolean fNamespaces;
/*  151 */   protected boolean fNamespacePrefixes = false;
/*      */ 
/*  154 */   protected boolean fLexicalHandlerParameterEntities = true;
/*      */   protected boolean fStandalone;
/*  160 */   protected boolean fResolveDTDURIs = true;
/*      */ 
/*  163 */   protected boolean fUseEntityResolver2 = true;
/*      */ 
/*  169 */   protected boolean fXMLNSURIs = false;
/*      */   protected ContentHandler fContentHandler;
/*      */   protected DocumentHandler fDocumentHandler;
/*      */   protected NamespaceContext fNamespaceContext;
/*      */   protected DTDHandler fDTDHandler;
/*      */   protected DeclHandler fDeclHandler;
/*      */   protected LexicalHandler fLexicalHandler;
/*  191 */   protected QName fQName = new QName();
/*      */ 
/*  200 */   protected boolean fParseInProgress = false;
/*      */   protected String fVersion;
/*  206 */   private final AttributesProxy fAttributesProxy = new AttributesProxy();
/*  207 */   private Augmentations fAugmentations = null;
/*      */   private static final int BUFFER_SIZE = 20;
/*  213 */   private char[] fCharBuffer = new char[20];
/*      */ 
/*  218 */   protected SymbolHash fDeclaredAttrs = null;
/*      */ 
/*      */   protected AbstractSAXParser(XMLParserConfiguration config)
/*      */   {
/*  226 */     super(config);
/*      */ 
/*  228 */     config.addRecognizedFeatures(RECOGNIZED_FEATURES);
/*  229 */     config.addRecognizedProperties(RECOGNIZED_PROPERTIES);
/*      */     try
/*      */     {
/*  232 */       config.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", false);
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  273 */     this.fNamespaceContext = namespaceContext;
/*      */     try
/*      */     {
/*  277 */       if (this.fDocumentHandler != null) {
/*  278 */         if (locator != null) {
/*  279 */           this.fDocumentHandler.setDocumentLocator(new LocatorProxy(locator));
/*      */         }
/*  281 */         this.fDocumentHandler.startDocument();
/*      */       }
/*      */ 
/*  285 */       if (this.fContentHandler != null) {
/*  286 */         if (locator != null) {
/*  287 */           this.fContentHandler.setDocumentLocator(new LocatorProxy(locator));
/*      */         }
/*  289 */         this.fContentHandler.startDocument();
/*      */       }
/*      */     }
/*      */     catch (SAXException e) {
/*  293 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  315 */     this.fVersion = version;
/*  316 */     this.fStandalone = "yes".equals(standalone);
/*      */   }
/*      */ 
/*      */   public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  334 */     this.fInDTD = true;
/*      */     try
/*      */     {
/*  338 */       if (this.fLexicalHandler != null)
/*  339 */         this.fLexicalHandler.startDTD(rootElement, publicId, systemId);
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  343 */       throw new XNIException(e);
/*      */     }
/*      */ 
/*  347 */     if (this.fDeclHandler != null)
/*  348 */       this.fDeclaredAttrs = new SymbolHash();
/*      */   }
/*      */ 
/*      */   public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  383 */       if ((augs != null) && (Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))))
/*      */       {
/*  385 */         if (this.fContentHandler != null) {
/*  386 */           this.fContentHandler.skippedEntity(name);
/*      */         }
/*      */ 
/*      */       }
/*  391 */       else if (this.fLexicalHandler != null) {
/*  392 */         this.fLexicalHandler.startEntity(name);
/*      */       }
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  397 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endGeneralEntity(String name, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  425 */       if ((augs == null) || (!Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))))
/*      */       {
/*  427 */         if (this.fLexicalHandler != null)
/*  428 */           this.fLexicalHandler.endEntity(name);
/*      */       }
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  433 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  454 */       if (this.fDocumentHandler != null)
/*      */       {
/*  457 */         this.fAttributesProxy.setAttributes(attributes);
/*  458 */         this.fDocumentHandler.startElement(element.rawname, this.fAttributesProxy);
/*      */       }
/*      */ 
/*  462 */       if (this.fContentHandler != null)
/*      */       {
/*  464 */         if (this.fNamespaces)
/*      */         {
/*  466 */           startNamespaceMapping();
/*      */ 
/*  474 */           int len = attributes.getLength();
/*  475 */           if (!this.fNamespacePrefixes) {
/*  476 */             for (int i = len - 1; i >= 0; i--) {
/*  477 */               attributes.getName(i, this.fQName);
/*  478 */               if ((this.fQName.prefix == XMLSymbols.PREFIX_XMLNS) || (this.fQName.rawname == XMLSymbols.PREFIX_XMLNS))
/*      */               {
/*  481 */                 attributes.removeAttributeAt(i);
/*      */               }
/*      */             }
/*      */           }
/*  485 */           else if (!this.fXMLNSURIs) {
/*  486 */             for (int i = len - 1; i >= 0; i--) {
/*  487 */               attributes.getName(i, this.fQName);
/*  488 */               if ((this.fQName.prefix == XMLSymbols.PREFIX_XMLNS) || (this.fQName.rawname == XMLSymbols.PREFIX_XMLNS))
/*      */               {
/*  492 */                 this.fQName.prefix = "";
/*  493 */                 this.fQName.uri = "";
/*  494 */                 this.fQName.localpart = "";
/*  495 */                 attributes.setName(i, this.fQName);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  501 */         this.fAugmentations = augs;
/*      */ 
/*  503 */         String uri = element.uri != null ? element.uri : "";
/*  504 */         String localpart = this.fNamespaces ? element.localpart : "";
/*  505 */         this.fAttributesProxy.setAttributes(attributes);
/*  506 */         this.fContentHandler.startElement(uri, localpart, element.rawname, this.fAttributesProxy);
/*      */       }
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  511 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void characters(XMLString text, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  528 */     if (text.length == 0) {
/*  529 */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  535 */       if (this.fDocumentHandler != null)
/*      */       {
/*  538 */         this.fDocumentHandler.characters(text.ch, text.offset, text.length);
/*      */       }
/*      */ 
/*  542 */       if (this.fContentHandler != null)
/*  543 */         this.fContentHandler.characters(text.ch, text.offset, text.length);
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  547 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ignorableWhitespace(XMLString text, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  569 */       if (this.fDocumentHandler != null) {
/*  570 */         this.fDocumentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
/*      */       }
/*      */ 
/*  574 */       if (this.fContentHandler != null)
/*  575 */         this.fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  579 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endElement(QName element, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  597 */       if (this.fDocumentHandler != null) {
/*  598 */         this.fDocumentHandler.endElement(element.rawname);
/*      */       }
/*      */ 
/*  602 */       if (this.fContentHandler != null) {
/*  603 */         this.fAugmentations = augs;
/*  604 */         String uri = element.uri != null ? element.uri : "";
/*  605 */         String localpart = this.fNamespaces ? element.localpart : "";
/*  606 */         this.fContentHandler.endElement(uri, localpart, element.rawname);
/*      */ 
/*  608 */         if (this.fNamespaces)
/*  609 */           endNamespaceMapping();
/*      */       }
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  614 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startCDATA(Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  629 */       if (this.fLexicalHandler != null)
/*  630 */         this.fLexicalHandler.startCDATA();
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  634 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endCDATA(Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  649 */       if (this.fLexicalHandler != null)
/*  650 */         this.fLexicalHandler.endCDATA();
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  654 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void comment(XMLString text, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  671 */       if (this.fLexicalHandler != null)
/*  672 */         this.fLexicalHandler.comment(text.ch, 0, text.length);
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  676 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void processingInstruction(String target, XMLString data, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  709 */       if (this.fDocumentHandler != null) {
/*  710 */         this.fDocumentHandler.processingInstruction(target, data.toString());
/*      */       }
/*      */ 
/*  715 */       if (this.fContentHandler != null)
/*  716 */         this.fContentHandler.processingInstruction(target, data.toString());
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  720 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endDocument(Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  736 */       if (this.fDocumentHandler != null) {
/*  737 */         this.fDocumentHandler.endDocument();
/*      */       }
/*      */ 
/*  741 */       if (this.fContentHandler != null)
/*  742 */         this.fContentHandler.endDocument();
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  746 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startExternalSubset(XMLResourceIdentifier identifier, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  765 */     startParameterEntity("[dtd]", null, null, augs);
/*      */   }
/*      */ 
/*      */   public void endExternalSubset(Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*  777 */     endParameterEntity("[dtd]", augs);
/*      */   }
/*      */ 
/*      */   public void startParameterEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  812 */       if ((augs != null) && (Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))))
/*      */       {
/*  814 */         if (this.fContentHandler != null) {
/*  815 */           this.fContentHandler.skippedEntity(name);
/*      */         }
/*      */ 
/*      */       }
/*  820 */       else if ((this.fLexicalHandler != null) && (this.fLexicalHandlerParameterEntities)) {
/*  821 */         this.fLexicalHandler.startEntity(name);
/*      */       }
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  826 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endParameterEntity(String name, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  855 */       if ((augs == null) || (!Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))))
/*      */       {
/*  857 */         if ((this.fLexicalHandler != null) && (this.fLexicalHandlerParameterEntities))
/*  858 */           this.fLexicalHandler.endEntity(name);
/*      */       }
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  863 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void elementDecl(String name, String contentModel, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  884 */       if (this.fDeclHandler != null)
/*  885 */         this.fDeclHandler.elementDecl(name, contentModel);
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  889 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void attributeDecl(String elementName, String attributeName, String type, String[] enumeration, String defaultType, XMLString defaultValue, XMLString nonNormalizedDefaultValue, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  927 */       if (this.fDeclHandler != null)
/*      */       {
/*  929 */         String elemAttr = elementName + "<" + attributeName;
/*  930 */         if (this.fDeclaredAttrs.get(elemAttr) != null)
/*      */         {
/*  932 */           return;
/*      */         }
/*  934 */         this.fDeclaredAttrs.put(elemAttr, Boolean.TRUE);
/*  935 */         if ((type.equals("NOTATION")) || (type.equals("ENUMERATION")))
/*      */         {
/*  938 */           StringBuffer str = new StringBuffer();
/*  939 */           if (type.equals("NOTATION")) {
/*  940 */             str.append(type);
/*  941 */             str.append(" (");
/*      */           }
/*      */           else {
/*  944 */             str.append("(");
/*      */           }
/*  946 */           for (int i = 0; i < enumeration.length; i++) {
/*  947 */             str.append(enumeration[i]);
/*  948 */             if (i < enumeration.length - 1) {
/*  949 */               str.append('|');
/*      */             }
/*      */           }
/*  952 */           str.append(')');
/*  953 */           type = str.toString();
/*      */         }
/*  955 */         String value = defaultValue == null ? null : defaultValue.toString();
/*  956 */         this.fDeclHandler.attributeDecl(elementName, attributeName, type, defaultType, value);
/*      */       }
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  961 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void internalEntityDecl(String name, XMLString text, XMLString nonNormalizedText, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/*  989 */       if (this.fDeclHandler != null)
/*  990 */         this.fDeclHandler.internalEntityDecl(name, text.toString());
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/*  994 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void externalEntityDecl(String name, XMLResourceIdentifier identifier, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/* 1016 */       if (this.fDeclHandler != null) {
/* 1017 */         String publicId = identifier.getPublicId();
/* 1018 */         String systemId = this.fResolveDTDURIs ? identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
/*      */ 
/* 1020 */         this.fDeclHandler.externalEntityDecl(name, publicId, systemId);
/*      */       }
/*      */     }
/*      */     catch (SAXException e) {
/* 1024 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier, String notation, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/* 1047 */       if (this.fDTDHandler != null) {
/* 1048 */         String publicId = identifier.getPublicId();
/* 1049 */         String systemId = this.fResolveDTDURIs ? identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
/*      */ 
/* 1051 */         this.fDTDHandler.unparsedEntityDecl(name, publicId, systemId, notation);
/*      */       }
/*      */     }
/*      */     catch (SAXException e) {
/* 1055 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void notationDecl(String name, XMLResourceIdentifier identifier, Augmentations augs)
/*      */     throws XNIException
/*      */   {
/*      */     try
/*      */     {
/* 1075 */       if (this.fDTDHandler != null) {
/* 1076 */         String publicId = identifier.getPublicId();
/* 1077 */         String systemId = this.fResolveDTDURIs ? identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
/*      */ 
/* 1079 */         this.fDTDHandler.notationDecl(name, publicId, systemId);
/*      */       }
/*      */     }
/*      */     catch (SAXException e) {
/* 1083 */       throw new XNIException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void endDTD(Augmentations augs)
/*      */     throws XNIException
/*      */   {
/* 1097 */     this.fInDTD = false;
/*      */     try
/*      */     {
/* 1101 */       if (this.fLexicalHandler != null)
/* 1102 */         this.fLexicalHandler.endDTD();
/*      */     }
/*      */     catch (SAXException e)
/*      */     {
/* 1106 */       throw new XNIException(e);
/*      */     }
/* 1108 */     if (this.fDeclaredAttrs != null)
/*      */     {
/* 1110 */       this.fDeclaredAttrs.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void parse(String systemId)
/*      */     throws SAXException, IOException
/*      */   {
/* 1135 */     XMLInputSource source = new XMLInputSource(null, systemId, null);
/*      */     try {
/* 1137 */       parse(source);
/*      */     }
/*      */     catch (XMLParseException e)
/*      */     {
/* 1142 */       Exception ex = e.getException();
/* 1143 */       if (ex == null)
/*      */       {
/* 1146 */         LocatorImpl locatorImpl = new LocatorImpl() {
/*      */           public String getXMLVersion() {
/* 1148 */             return AbstractSAXParser.this.fVersion;
/*      */           }
/*      */ 
/*      */           public String getEncoding()
/*      */           {
/* 1156 */             return null;
/*      */           }
/*      */         };
/* 1159 */         locatorImpl.setPublicId(e.getPublicId());
/* 1160 */         locatorImpl.setSystemId(e.getExpandedSystemId());
/* 1161 */         locatorImpl.setLineNumber(e.getLineNumber());
/* 1162 */         locatorImpl.setColumnNumber(e.getColumnNumber());
/* 1163 */         throw new SAXParseException(e.getMessage(), locatorImpl);
/*      */       }
/* 1165 */       if ((ex instanceof SAXException))
/*      */       {
/* 1167 */         throw ((SAXException)ex);
/*      */       }
/* 1169 */       if ((ex instanceof IOException)) {
/* 1170 */         throw ((IOException)ex);
/*      */       }
/* 1172 */       throw new SAXException(ex);
/*      */     }
/*      */     catch (XNIException e) {
/* 1175 */       Exception ex = e.getException();
/* 1176 */       if (ex == null) {
/* 1177 */         throw new SAXException(e.getMessage());
/*      */       }
/* 1179 */       if ((ex instanceof SAXException)) {
/* 1180 */         throw ((SAXException)ex);
/*      */       }
/* 1182 */       if ((ex instanceof IOException)) {
/* 1183 */         throw ((IOException)ex);
/*      */       }
/* 1185 */       throw new SAXException(ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void parse(InputSource inputSource)
/*      */     throws SAXException, IOException
/*      */   {
/*      */     try
/*      */     {
/* 1203 */       XMLInputSource xmlInputSource = new XMLInputSource(inputSource.getPublicId(), inputSource.getSystemId(), null);
/*      */ 
/* 1207 */       xmlInputSource.setByteStream(inputSource.getByteStream());
/* 1208 */       xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
/* 1209 */       xmlInputSource.setEncoding(inputSource.getEncoding());
/* 1210 */       parse(xmlInputSource);
/*      */     }
/*      */     catch (XMLParseException e)
/*      */     {
/* 1215 */       Exception ex = e.getException();
/* 1216 */       if (ex == null)
/*      */       {
/* 1219 */         LocatorImpl locatorImpl = new LocatorImpl() {
/*      */           public String getXMLVersion() {
/* 1221 */             return AbstractSAXParser.this.fVersion;
/*      */           }
/*      */ 
/*      */           public String getEncoding()
/*      */           {
/* 1229 */             return null;
/*      */           }
/*      */         };
/* 1232 */         locatorImpl.setPublicId(e.getPublicId());
/* 1233 */         locatorImpl.setSystemId(e.getExpandedSystemId());
/* 1234 */         locatorImpl.setLineNumber(e.getLineNumber());
/* 1235 */         locatorImpl.setColumnNumber(e.getColumnNumber());
/* 1236 */         throw new SAXParseException(e.getMessage(), locatorImpl);
/*      */       }
/* 1238 */       if ((ex instanceof SAXException))
/*      */       {
/* 1240 */         throw ((SAXException)ex);
/*      */       }
/* 1242 */       if ((ex instanceof IOException)) {
/* 1243 */         throw ((IOException)ex);
/*      */       }
/* 1245 */       throw new SAXException(ex);
/*      */     }
/*      */     catch (XNIException e) {
/* 1248 */       Exception ex = e.getException();
/* 1249 */       if (ex == null) {
/* 1250 */         throw new SAXException(e.getMessage());
/*      */       }
/* 1252 */       if ((ex instanceof SAXException)) {
/* 1253 */         throw ((SAXException)ex);
/*      */       }
/* 1255 */       if ((ex instanceof IOException)) {
/* 1256 */         throw ((IOException)ex);
/*      */       }
/* 1258 */       throw new SAXException(ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setEntityResolver(EntityResolver resolver)
/*      */   {
/*      */     try
/*      */     {
/* 1273 */       XMLEntityResolver xer = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
/* 1274 */       if ((this.fUseEntityResolver2) && ((resolver instanceof EntityResolver2))) {
/* 1275 */         if ((xer instanceof EntityResolver2Wrapper)) {
/* 1276 */           EntityResolver2Wrapper er2w = (EntityResolver2Wrapper)xer;
/* 1277 */           er2w.setEntityResolver((EntityResolver2)resolver);
/*      */         }
/*      */         else {
/* 1280 */           this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)resolver));
/*      */         }
/*      */ 
/*      */       }
/* 1285 */       else if ((xer instanceof EntityResolverWrapper)) {
/* 1286 */         EntityResolverWrapper erw = (EntityResolverWrapper)xer;
/* 1287 */         erw.setEntityResolver(resolver);
/*      */       }
/*      */       else {
/* 1290 */         this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(resolver));
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public EntityResolver getEntityResolver()
/*      */   {
/* 1310 */     EntityResolver entityResolver = null;
/*      */     try {
/* 1312 */       XMLEntityResolver xmlEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
/*      */ 
/* 1314 */       if (xmlEntityResolver != null) {
/* 1315 */         if ((xmlEntityResolver instanceof EntityResolverWrapper)) {
/* 1316 */           entityResolver = ((EntityResolverWrapper)xmlEntityResolver).getEntityResolver();
/*      */         }
/* 1319 */         else if ((xmlEntityResolver instanceof EntityResolver2Wrapper)) {
/* 1320 */           entityResolver = ((EntityResolver2Wrapper)xmlEntityResolver).getEntityResolver();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*      */ 
/* 1328 */     return entityResolver;
/*      */   }
/*      */ 
/*      */   public void setErrorHandler(ErrorHandler errorHandler)
/*      */   {
/*      */     try
/*      */     {
/* 1351 */       XMLErrorHandler xeh = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
/* 1352 */       if ((xeh instanceof ErrorHandlerWrapper)) {
/* 1353 */         ErrorHandlerWrapper ehw = (ErrorHandlerWrapper)xeh;
/* 1354 */         ehw.setErrorHandler(errorHandler);
/*      */       }
/*      */       else {
/* 1357 */         this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(errorHandler));
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public ErrorHandler getErrorHandler()
/*      */   {
/* 1376 */     ErrorHandler errorHandler = null;
/*      */     try {
/* 1378 */       XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
/*      */ 
/* 1380 */       if ((xmlErrorHandler != null) && ((xmlErrorHandler instanceof ErrorHandlerWrapper)))
/*      */       {
/* 1382 */         errorHandler = ((ErrorHandlerWrapper)xmlErrorHandler).getErrorHandler();
/*      */       }
/*      */     }
/*      */     catch (XMLConfigurationException e)
/*      */     {
/*      */     }
/* 1388 */     return errorHandler;
/*      */   }
/*      */ 
/*      */   public void setLocale(Locale locale)
/*      */     throws SAXException
/*      */   {
/* 1405 */     this.fConfiguration.setLocale(locale);
/*      */   }
/*      */ 
/*      */   public void setDTDHandler(DTDHandler dtdHandler)
/*      */   {
/* 1425 */     this.fDTDHandler = dtdHandler;
/*      */   }
/*      */ 
/*      */   public void setDocumentHandler(DocumentHandler documentHandler)
/*      */   {
/* 1447 */     this.fDocumentHandler = documentHandler;
/*      */   }
/*      */ 
/*      */   public void setContentHandler(ContentHandler contentHandler)
/*      */   {
/* 1470 */     this.fContentHandler = contentHandler;
/*      */   }
/*      */ 
/*      */   public ContentHandler getContentHandler()
/*      */   {
/* 1482 */     return this.fContentHandler;
/*      */   }
/*      */ 
/*      */   public DTDHandler getDTDHandler()
/*      */   {
/* 1493 */     return this.fDTDHandler;
/*      */   }
/*      */ 
/*      */   public void setFeature(String featureId, boolean state)
/*      */     throws SAXNotRecognizedException, SAXNotSupportedException
/*      */   {
/*      */     try
/*      */     {
/* 1518 */       if (featureId.startsWith("http://xml.org/sax/features/")) {
/* 1519 */         int suffixLength = featureId.length() - "http://xml.org/sax/features/".length();
/*      */ 
/* 1522 */         if ((suffixLength == "namespaces".length()) && (featureId.endsWith("namespaces")))
/*      */         {
/* 1524 */           this.fConfiguration.setFeature(featureId, state);
/* 1525 */           this.fNamespaces = state;
/* 1526 */           return;
/*      */         }
/*      */ 
/* 1535 */         if ((suffixLength == "namespace-prefixes".length()) && (featureId.endsWith("namespace-prefixes")))
/*      */         {
/* 1537 */           this.fConfiguration.setFeature(featureId, state);
/* 1538 */           this.fNamespacePrefixes = state;
/* 1539 */           return;
/*      */         }
/*      */ 
/* 1546 */         if ((suffixLength == "string-interning".length()) && (featureId.endsWith("string-interning")))
/*      */         {
/* 1548 */           if (!state) {
/* 1549 */             throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "false-not-supported", new Object[] { featureId }));
/*      */           }
/*      */ 
/* 1553 */           return;
/*      */         }
/*      */ 
/* 1560 */         if ((suffixLength == "lexical-handler/parameter-entities".length()) && (featureId.endsWith("lexical-handler/parameter-entities")))
/*      */         {
/* 1562 */           this.fLexicalHandlerParameterEntities = state;
/* 1563 */           return;
/*      */         }
/*      */ 
/* 1570 */         if ((suffixLength == "resolve-dtd-uris".length()) && (featureId.endsWith("resolve-dtd-uris")))
/*      */         {
/* 1572 */           this.fResolveDTDURIs = state;
/* 1573 */           return;
/*      */         }
/*      */ 
/* 1580 */         if ((suffixLength == "unicode-normalization-checking".length()) && (featureId.endsWith("unicode-normalization-checking")))
/*      */         {
/* 1584 */           if (state) {
/* 1585 */             throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "true-not-supported", new Object[] { featureId }));
/*      */           }
/*      */ 
/* 1589 */           return;
/*      */         }
/*      */ 
/* 1596 */         if ((suffixLength == "xmlns-uris".length()) && (featureId.endsWith("xmlns-uris")))
/*      */         {
/* 1598 */           this.fXMLNSURIs = state;
/* 1599 */           return;
/*      */         }
/*      */ 
/* 1606 */         if ((suffixLength == "use-entity-resolver2".length()) && (featureId.endsWith("use-entity-resolver2")))
/*      */         {
/* 1608 */           if (state != this.fUseEntityResolver2) {
/* 1609 */             this.fUseEntityResolver2 = state;
/*      */ 
/* 1611 */             setEntityResolver(getEntityResolver());
/*      */           }
/* 1613 */           return;
/*      */         }
/*      */ 
/* 1630 */         if (((suffixLength == "is-standalone".length()) && (featureId.endsWith("is-standalone"))) || ((suffixLength == "use-attributes2".length()) && (featureId.endsWith("use-attributes2"))) || ((suffixLength == "use-locator2".length()) && (featureId.endsWith("use-locator2"))) || ((suffixLength == "xml-1.1".length()) && (featureId.endsWith("xml-1.1"))))
/*      */         {
/* 1638 */           throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-read-only", new Object[] { featureId }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1666 */       this.fConfiguration.setFeature(featureId, state);
/*      */     }
/*      */     catch (XMLConfigurationException e) {
/* 1669 */       String identifier = e.getIdentifier();
/* 1670 */       if (e.getType() == Status.NOT_RECOGNIZED) {
/* 1671 */         throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
/*      */       }
/*      */ 
/* 1676 */       throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier })); }  } 
/*      */   public boolean getFeature(String featureId) throws SAXNotRecognizedException, SAXNotSupportedException { // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: ldc 22
/*      */     //   3: invokevirtual 770	java/lang/String:startsWith	(Ljava/lang/String;)Z
/*      */     //   6: ifeq +264 -> 270
/*      */     //   9: aload_1
/*      */     //   10: invokevirtual 767	java/lang/String:length	()I
/*      */     //   13: ldc 22
/*      */     //   15: invokevirtual 767	java/lang/String:length	()I
/*      */     //   18: isub
/*      */     //   19: istore_2
/*      */     //   20: iload_2
/*      */     //   21: ldc 35
/*      */     //   23: invokevirtual 767	java/lang/String:length	()I
/*      */     //   26: if_icmpne +25 -> 51
/*      */     //   29: aload_1
/*      */     //   30: ldc 35
/*      */     //   32: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   35: ifeq +16 -> 51
/*      */     //   38: aload_0
/*      */     //   39: getfield 697	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*      */     //   42: aload_1
/*      */     //   43: invokeinterface 803 2 0
/*      */     //   48: istore_3
/*      */     //   49: iload_3
/*      */     //   50: ireturn
/*      */     //   51: iload_2
/*      */     //   52: ldc 44
/*      */     //   54: invokevirtual 767	java/lang/String:length	()I
/*      */     //   57: if_icmpne +14 -> 71
/*      */     //   60: aload_1
/*      */     //   61: ldc 44
/*      */     //   63: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   66: ifeq +5 -> 71
/*      */     //   69: iconst_1
/*      */     //   70: ireturn
/*      */     //   71: iload_2
/*      */     //   72: ldc 32
/*      */     //   74: invokevirtual 767	java/lang/String:length	()I
/*      */     //   77: if_icmpne +17 -> 94
/*      */     //   80: aload_1
/*      */     //   81: ldc 32
/*      */     //   83: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   86: ifeq +8 -> 94
/*      */     //   89: aload_0
/*      */     //   90: getfield 688	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fStandalone	Z
/*      */     //   93: ireturn
/*      */     //   94: iload_2
/*      */     //   95: ldc 50
/*      */     //   97: invokevirtual 767	java/lang/String:length	()I
/*      */     //   100: if_icmpne +20 -> 120
/*      */     //   103: aload_1
/*      */     //   104: ldc 50
/*      */     //   106: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   109: ifeq +11 -> 120
/*      */     //   112: aload_0
/*      */     //   113: getfield 697	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*      */     //   116: instanceof 403
/*      */     //   119: ireturn
/*      */     //   120: iload_2
/*      */     //   121: ldc 34
/*      */     //   123: invokevirtual 767	java/lang/String:length	()I
/*      */     //   126: if_icmpne +17 -> 143
/*      */     //   129: aload_1
/*      */     //   130: ldc 34
/*      */     //   132: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   135: ifeq +8 -> 143
/*      */     //   138: aload_0
/*      */     //   139: getfield 683	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fLexicalHandlerParameterEntities	Z
/*      */     //   142: ireturn
/*      */     //   143: iload_2
/*      */     //   144: ldc 43
/*      */     //   146: invokevirtual 767	java/lang/String:length	()I
/*      */     //   149: if_icmpne +17 -> 166
/*      */     //   152: aload_1
/*      */     //   153: ldc 43
/*      */     //   155: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   158: ifeq +8 -> 166
/*      */     //   161: aload_0
/*      */     //   162: getfield 687	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fResolveDTDURIs	Z
/*      */     //   165: ireturn
/*      */     //   166: iload_2
/*      */     //   167: ldc 51
/*      */     //   169: invokevirtual 767	java/lang/String:length	()I
/*      */     //   172: if_icmpne +17 -> 189
/*      */     //   175: aload_1
/*      */     //   176: ldc 51
/*      */     //   178: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   181: ifeq +8 -> 189
/*      */     //   184: aload_0
/*      */     //   185: getfield 690	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fXMLNSURIs	Z
/*      */     //   188: ireturn
/*      */     //   189: iload_2
/*      */     //   190: ldc 46
/*      */     //   192: invokevirtual 767	java/lang/String:length	()I
/*      */     //   195: if_icmpne +14 -> 209
/*      */     //   198: aload_1
/*      */     //   199: ldc 46
/*      */     //   201: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   204: ifeq +5 -> 209
/*      */     //   207: iconst_0
/*      */     //   208: ireturn
/*      */     //   209: iload_2
/*      */     //   210: ldc 48
/*      */     //   212: invokevirtual 767	java/lang/String:length	()I
/*      */     //   215: if_icmpne +17 -> 232
/*      */     //   218: aload_1
/*      */     //   219: ldc 48
/*      */     //   221: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   224: ifeq +8 -> 232
/*      */     //   227: aload_0
/*      */     //   228: getfield 689	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fUseEntityResolver2	Z
/*      */     //   231: ireturn
/*      */     //   232: iload_2
/*      */     //   233: ldc 47
/*      */     //   235: invokevirtual 767	java/lang/String:length	()I
/*      */     //   238: if_icmpne +12 -> 250
/*      */     //   241: aload_1
/*      */     //   242: ldc 47
/*      */     //   244: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   247: ifne +21 -> 268
/*      */     //   250: iload_2
/*      */     //   251: ldc 49
/*      */     //   253: invokevirtual 767	java/lang/String:length	()I
/*      */     //   256: if_icmpne +14 -> 270
/*      */     //   259: aload_1
/*      */     //   260: ldc 49
/*      */     //   262: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   265: ifeq +5 -> 270
/*      */     //   268: iconst_1
/*      */     //   269: ireturn
/*      */     //   270: aload_0
/*      */     //   271: getfield 697	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*      */     //   274: aload_1
/*      */     //   275: invokeinterface 803 2 0
/*      */     //   280: ireturn
/*      */     //   281: astore_2
/*      */     //   282: aload_2
/*      */     //   283: invokevirtual 755	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getIdentifier	()Ljava/lang/String;
/*      */     //   286: astore_3
/*      */     //   287: aload_2
/*      */     //   288: invokevirtual 754	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getType	()Lcom/sun/org/apache/xerces/internal/util/Status;
/*      */     //   291: getstatic 707	com/sun/org/apache/xerces/internal/util/Status:NOT_RECOGNIZED	Lcom/sun/org/apache/xerces/internal/util/Status;
/*      */     //   294: if_acmpne +33 -> 327
/*      */     //   297: new 442	org/xml/sax/SAXNotRecognizedException
/*      */     //   300: dup
/*      */     //   301: aload_0
/*      */     //   302: getfield 697	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*      */     //   305: invokeinterface 807 1 0
/*      */     //   310: ldc 17
/*      */     //   312: iconst_1
/*      */     //   313: anewarray 431	java/lang/Object
/*      */     //   316: dup
/*      */     //   317: iconst_0
/*      */     //   318: aload_3
/*      */     //   319: aastore
/*      */     //   320: invokestatic 744	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   323: invokespecial 783	org/xml/sax/SAXNotRecognizedException:<init>	(Ljava/lang/String;)V
/*      */     //   326: athrow
/*      */     //   327: new 443	org/xml/sax/SAXNotSupportedException
/*      */     //   330: dup
/*      */     //   331: aload_0
/*      */     //   332: getfield 697	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*      */     //   335: invokeinterface 807 1 0
/*      */     //   340: ldc 18
/*      */     //   342: iconst_1
/*      */     //   343: anewarray 431	java/lang/Object
/*      */     //   346: dup
/*      */     //   347: iconst_0
/*      */     //   348: aload_3
/*      */     //   349: aastore
/*      */     //   350: invokestatic 744	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   353: invokespecial 784	org/xml/sax/SAXNotSupportedException:<init>	(Ljava/lang/String;)V
/*      */     //   356: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	50	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   51	70	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   71	93	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   94	119	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   120	142	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   143	165	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   166	188	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   189	208	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   209	231	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   232	269	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   270	280	281	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException } 
/*      */   public void setProperty(String propertyId, Object value) throws SAXNotRecognizedException, SAXNotSupportedException { try { if (propertyId.startsWith("http://xml.org/sax/properties/")) {
/* 1865 */         int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
/*      */ 
/* 1873 */         if ((suffixLength == "lexical-handler".length()) && (propertyId.endsWith("lexical-handler")))
/*      */         {
/*      */           try {
/* 1876 */             setLexicalHandler((LexicalHandler)value);
/*      */           }
/*      */           catch (ClassCastException e) {
/* 1879 */             throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[] { propertyId, "org.xml.sax.ext.LexicalHandler" }));
/*      */           }
/*      */ 
/* 1883 */           return;
/*      */         }
/*      */ 
/* 1891 */         if ((suffixLength == "declaration-handler".length()) && (propertyId.endsWith("declaration-handler")))
/*      */         {
/*      */           try {
/* 1894 */             setDeclHandler((DeclHandler)value);
/*      */           }
/*      */           catch (ClassCastException e) {
/* 1897 */             throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[] { propertyId, "org.xml.sax.ext.DeclHandler" }));
/*      */           }
/*      */ 
/* 1901 */           return;
/*      */         }
/*      */ 
/* 1917 */         if (((suffixLength == "dom-node".length()) && (propertyId.endsWith("dom-node"))) || ((suffixLength == "document-xml-version".length()) && (propertyId.endsWith("document-xml-version"))))
/*      */         {
/* 1921 */           throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-read-only", new Object[] { propertyId }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1946 */       this.fConfiguration.setProperty(propertyId, value);
/*      */     } catch (XMLConfigurationException e)
/*      */     {
/* 1949 */       String identifier = e.getIdentifier();
/* 1950 */       if (e.getType() == Status.NOT_RECOGNIZED) {
/* 1951 */         throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
/*      */       }
/*      */ 
/* 1956 */       throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier })); }  } 
/*      */   public Object getProperty(String propertyId) throws SAXNotRecognizedException, SAXNotSupportedException { // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: ldc 27
/*      */     //   3: invokevirtual 770	java/lang/String:startsWith	(Ljava/lang/String;)Z
/*      */     //   6: ifeq +124 -> 130
/*      */     //   9: aload_1
/*      */     //   10: invokevirtual 767	java/lang/String:length	()I
/*      */     //   13: ldc 27
/*      */     //   15: invokevirtual 767	java/lang/String:length	()I
/*      */     //   18: isub
/*      */     //   19: istore_2
/*      */     //   20: iload_2
/*      */     //   21: ldc 13
/*      */     //   23: invokevirtual 767	java/lang/String:length	()I
/*      */     //   26: if_icmpne +17 -> 43
/*      */     //   29: aload_1
/*      */     //   30: ldc 13
/*      */     //   32: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   35: ifeq +8 -> 43
/*      */     //   38: aload_0
/*      */     //   39: getfield 698	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fVersion	Ljava/lang/String;
/*      */     //   42: areturn
/*      */     //   43: iload_2
/*      */     //   44: ldc 33
/*      */     //   46: invokevirtual 767	java/lang/String:length	()I
/*      */     //   49: if_icmpne +17 -> 66
/*      */     //   52: aload_1
/*      */     //   53: ldc 33
/*      */     //   55: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   58: ifeq +8 -> 66
/*      */     //   61: aload_0
/*      */     //   62: invokevirtual 724	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:getLexicalHandler	()Lorg/xml/sax/ext/LexicalHandler;
/*      */     //   65: areturn
/*      */     //   66: iload_2
/*      */     //   67: ldc 12
/*      */     //   69: invokevirtual 767	java/lang/String:length	()I
/*      */     //   72: if_icmpne +17 -> 89
/*      */     //   75: aload_1
/*      */     //   76: ldc 12
/*      */     //   78: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   81: ifeq +8 -> 89
/*      */     //   84: aload_0
/*      */     //   85: invokevirtual 722	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:getDeclHandler	()Lorg/xml/sax/ext/DeclHandler;
/*      */     //   88: areturn
/*      */     //   89: iload_2
/*      */     //   90: ldc 14
/*      */     //   92: invokevirtual 767	java/lang/String:length	()I
/*      */     //   95: if_icmpne +35 -> 130
/*      */     //   98: aload_1
/*      */     //   99: ldc 14
/*      */     //   101: invokevirtual 769	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*      */     //   104: ifeq +26 -> 130
/*      */     //   107: new 443	org/xml/sax/SAXNotSupportedException
/*      */     //   110: dup
/*      */     //   111: aload_0
/*      */     //   112: getfield 697	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*      */     //   115: invokeinterface 807 1 0
/*      */     //   120: ldc 15
/*      */     //   122: aconst_null
/*      */     //   123: invokestatic 744	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   126: invokespecial 784	org/xml/sax/SAXNotSupportedException:<init>	(Ljava/lang/String;)V
/*      */     //   129: athrow
/*      */     //   130: aload_0
/*      */     //   131: getfield 697	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*      */     //   134: aload_1
/*      */     //   135: invokeinterface 809 2 0
/*      */     //   140: areturn
/*      */     //   141: astore_2
/*      */     //   142: aload_2
/*      */     //   143: invokevirtual 755	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getIdentifier	()Ljava/lang/String;
/*      */     //   146: astore_3
/*      */     //   147: aload_2
/*      */     //   148: invokevirtual 754	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException:getType	()Lcom/sun/org/apache/xerces/internal/util/Status;
/*      */     //   151: getstatic 707	com/sun/org/apache/xerces/internal/util/Status:NOT_RECOGNIZED	Lcom/sun/org/apache/xerces/internal/util/Status;
/*      */     //   154: if_acmpne +33 -> 187
/*      */     //   157: new 442	org/xml/sax/SAXNotRecognizedException
/*      */     //   160: dup
/*      */     //   161: aload_0
/*      */     //   162: getfield 697	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*      */     //   165: invokeinterface 807 1 0
/*      */     //   170: ldc 40
/*      */     //   172: iconst_1
/*      */     //   173: anewarray 431	java/lang/Object
/*      */     //   176: dup
/*      */     //   177: iconst_0
/*      */     //   178: aload_3
/*      */     //   179: aastore
/*      */     //   180: invokestatic 744	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   183: invokespecial 783	org/xml/sax/SAXNotRecognizedException:<init>	(Ljava/lang/String;)V
/*      */     //   186: athrow
/*      */     //   187: new 443	org/xml/sax/SAXNotSupportedException
/*      */     //   190: dup
/*      */     //   191: aload_0
/*      */     //   192: getfield 697	com/sun/org/apache/xerces/internal/parsers/AbstractSAXParser:fConfiguration	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLParserConfiguration;
/*      */     //   195: invokeinterface 807 1 0
/*      */     //   200: ldc 41
/*      */     //   202: iconst_1
/*      */     //   203: anewarray 431	java/lang/Object
/*      */     //   206: dup
/*      */     //   207: iconst_0
/*      */     //   208: aload_3
/*      */     //   209: aastore
/*      */     //   210: invokestatic 744	com/sun/org/apache/xerces/internal/util/SAXMessageFormatter:formatMessage	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   213: invokespecial 784	org/xml/sax/SAXNotSupportedException:<init>	(Ljava/lang/String;)V
/*      */     //   216: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	42	141	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   43	65	141	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   66	88	141	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException
/*      */     //   89	140	141	com/sun/org/apache/xerces/internal/xni/parser/XMLConfigurationException } 
/* 2100 */   protected void setDeclHandler(DeclHandler handler) throws SAXNotRecognizedException, SAXNotSupportedException { if (this.fParseInProgress) {
/* 2101 */       throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[] { "http://xml.org/sax/properties/declaration-handler" }));
/*      */     }
/*      */ 
/* 2106 */     this.fDeclHandler = handler;
/*      */   }
/*      */ 
/*      */   protected DeclHandler getDeclHandler()
/*      */     throws SAXNotRecognizedException, SAXNotSupportedException
/*      */   {
/* 2117 */     return this.fDeclHandler;
/*      */   }
/*      */ 
/*      */   protected void setLexicalHandler(LexicalHandler handler)
/*      */     throws SAXNotRecognizedException, SAXNotSupportedException
/*      */   {
/* 2136 */     if (this.fParseInProgress) {
/* 2137 */       throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[] { "http://xml.org/sax/properties/lexical-handler" }));
/*      */     }
/*      */ 
/* 2142 */     this.fLexicalHandler = handler;
/*      */   }
/*      */ 
/*      */   protected LexicalHandler getLexicalHandler()
/*      */     throws SAXNotRecognizedException, SAXNotSupportedException
/*      */   {
/* 2153 */     return this.fLexicalHandler;
/*      */   }
/*      */ 
/*      */   protected final void startNamespaceMapping()
/*      */     throws SAXException
/*      */   {
/* 2160 */     int count = this.fNamespaceContext.getDeclaredPrefixCount();
/* 2161 */     if (count > 0) {
/* 2162 */       String prefix = null;
/* 2163 */       String uri = null;
/* 2164 */       for (int i = 0; i < count; i++) {
/* 2165 */         prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
/* 2166 */         uri = this.fNamespaceContext.getURI(prefix);
/* 2167 */         this.fContentHandler.startPrefixMapping(prefix, uri == null ? "" : uri);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final void endNamespaceMapping()
/*      */     throws SAXException
/*      */   {
/* 2177 */     int count = this.fNamespaceContext.getDeclaredPrefixCount();
/* 2178 */     if (count > 0)
/* 2179 */       for (int i = 0; i < count; i++)
/* 2180 */         this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(i));
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */     throws XNIException
/*      */   {
/* 2195 */     super.reset();
/*      */ 
/* 2198 */     this.fInDTD = false;
/* 2199 */     this.fVersion = "1.0";
/* 2200 */     this.fStandalone = false;
/*      */ 
/* 2203 */     this.fNamespaces = this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces");
/* 2204 */     this.fNamespacePrefixes = this.fConfiguration.getFeature("http://xml.org/sax/features/namespace-prefixes");
/* 2205 */     this.fAugmentations = null;
/* 2206 */     this.fDeclaredAttrs = null;
/*      */   }
/*      */ 
/*      */   public ElementPSVI getElementPSVI()
/*      */   {
/* 2405 */     return this.fAugmentations != null ? (ElementPSVI)this.fAugmentations.getItem("ELEMENT_PSVI") : null;
/*      */   }
/*      */ 
/*      */   public AttributePSVI getAttributePSVI(int index)
/*      */   {
/* 2411 */     return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_PSVI");
/*      */   }
/*      */ 
/*      */   public AttributePSVI getAttributePSVIByName(String uri, String localname)
/*      */   {
/* 2417 */     return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(uri, localname).getItem("ATTRIBUTE_PSVI");
/*      */   }
/*      */ 
/*      */   protected static final class AttributesProxy
/*      */     implements AttributeList, Attributes2
/*      */   {
/*      */     protected XMLAttributes fAttributes;
/*      */ 
/*      */     public void setAttributes(XMLAttributes attributes)
/*      */     {
/* 2283 */       this.fAttributes = attributes;
/*      */     }
/*      */ 
/*      */     public int getLength() {
/* 2287 */       return this.fAttributes.getLength();
/*      */     }
/*      */ 
/*      */     public String getName(int i) {
/* 2291 */       return this.fAttributes.getQName(i);
/*      */     }
/*      */ 
/*      */     public String getQName(int index) {
/* 2295 */       return this.fAttributes.getQName(index);
/*      */     }
/*      */ 
/*      */     public String getURI(int index)
/*      */     {
/* 2302 */       String uri = this.fAttributes.getURI(index);
/* 2303 */       return uri != null ? uri : "";
/*      */     }
/*      */ 
/*      */     public String getLocalName(int index) {
/* 2307 */       return this.fAttributes.getLocalName(index);
/*      */     }
/*      */ 
/*      */     public String getType(int i) {
/* 2311 */       return this.fAttributes.getType(i);
/*      */     }
/*      */ 
/*      */     public String getType(String name) {
/* 2315 */       return this.fAttributes.getType(name);
/*      */     }
/*      */ 
/*      */     public String getType(String uri, String localName) {
/* 2319 */       return uri.equals("") ? this.fAttributes.getType(null, localName) : this.fAttributes.getType(uri, localName);
/*      */     }
/*      */ 
/*      */     public String getValue(int i)
/*      */     {
/* 2324 */       return this.fAttributes.getValue(i);
/*      */     }
/*      */ 
/*      */     public String getValue(String name) {
/* 2328 */       return this.fAttributes.getValue(name);
/*      */     }
/*      */ 
/*      */     public String getValue(String uri, String localName) {
/* 2332 */       return uri.equals("") ? this.fAttributes.getValue(null, localName) : this.fAttributes.getValue(uri, localName);
/*      */     }
/*      */ 
/*      */     public int getIndex(String qName)
/*      */     {
/* 2337 */       return this.fAttributes.getIndex(qName);
/*      */     }
/*      */ 
/*      */     public int getIndex(String uri, String localPart) {
/* 2341 */       return uri.equals("") ? this.fAttributes.getIndex(null, localPart) : this.fAttributes.getIndex(uri, localPart);
/*      */     }
/*      */ 
/*      */     public boolean isDeclared(int index)
/*      */     {
/* 2348 */       if ((index < 0) || (index >= this.fAttributes.getLength())) {
/* 2349 */         throw new ArrayIndexOutOfBoundsException(index);
/*      */       }
/* 2351 */       return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
/*      */     }
/*      */ 
/*      */     public boolean isDeclared(String qName)
/*      */     {
/* 2357 */       int index = getIndex(qName);
/* 2358 */       if (index == -1) {
/* 2359 */         throw new IllegalArgumentException(qName);
/*      */       }
/* 2361 */       return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
/*      */     }
/*      */ 
/*      */     public boolean isDeclared(String uri, String localName)
/*      */     {
/* 2367 */       int index = getIndex(uri, localName);
/* 2368 */       if (index == -1) {
/* 2369 */         throw new IllegalArgumentException(localName);
/*      */       }
/* 2371 */       return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
/*      */     }
/*      */ 
/*      */     public boolean isSpecified(int index)
/*      */     {
/* 2377 */       if ((index < 0) || (index >= this.fAttributes.getLength())) {
/* 2378 */         throw new ArrayIndexOutOfBoundsException(index);
/*      */       }
/* 2380 */       return this.fAttributes.isSpecified(index);
/*      */     }
/*      */ 
/*      */     public boolean isSpecified(String qName) {
/* 2384 */       int index = getIndex(qName);
/* 2385 */       if (index == -1) {
/* 2386 */         throw new IllegalArgumentException(qName);
/*      */       }
/* 2388 */       return this.fAttributes.isSpecified(index);
/*      */     }
/*      */ 
/*      */     public boolean isSpecified(String uri, String localName) {
/* 2392 */       int index = getIndex(uri, localName);
/* 2393 */       if (index == -1) {
/* 2394 */         throw new IllegalArgumentException(localName);
/*      */       }
/* 2396 */       return this.fAttributes.isSpecified(index);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class LocatorProxy
/*      */     implements Locator2
/*      */   {
/*      */     protected XMLLocator fLocator;
/*      */ 
/*      */     public LocatorProxy(XMLLocator locator)
/*      */     {
/* 2230 */       this.fLocator = locator;
/*      */     }
/*      */ 
/*      */     public String getPublicId()
/*      */     {
/* 2239 */       return this.fLocator.getPublicId();
/*      */     }
/*      */ 
/*      */     public String getSystemId()
/*      */     {
/* 2244 */       return this.fLocator.getExpandedSystemId();
/*      */     }
/*      */ 
/*      */     public int getLineNumber() {
/* 2248 */       return this.fLocator.getLineNumber();
/*      */     }
/*      */ 
/*      */     public int getColumnNumber()
/*      */     {
/* 2253 */       return this.fLocator.getColumnNumber();
/*      */     }
/*      */ 
/*      */     public String getXMLVersion()
/*      */     {
/* 2258 */       return this.fLocator.getXMLVersion();
/*      */     }
/*      */ 
/*      */     public String getEncoding() {
/* 2262 */       return this.fLocator.getEncoding();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.parsers.AbstractSAXParser
 * JD-Core Version:    0.6.2
 */