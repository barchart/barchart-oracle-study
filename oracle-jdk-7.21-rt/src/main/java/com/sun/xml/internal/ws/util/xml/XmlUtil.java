/*     */ package com.sun.xml.internal.ws.util.xml;
/*     */ 
/*     */ import com.sun.istack.internal.Nullable;
/*     */ import com.sun.org.apache.xml.internal.resolver.Catalog;
/*     */ import com.sun.org.apache.xml.internal.resolver.CatalogManager;
/*     */ import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
/*     */ import com.sun.xml.internal.ws.server.ServerRtException;
/*     */ import com.sun.xml.internal.ws.util.ByteArrayBuffer;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.sax.SAXTransformerFactory;
/*     */ import javax.xml.transform.sax.TransformerHandler;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import javax.xml.ws.WebServiceException;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.EntityReference;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.Text;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.ErrorHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ import org.xml.sax.XMLReader;
/*     */ 
/*     */ public class XmlUtil
/*     */ {
/*     */   private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
/* 202 */   static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
/*     */ 
/* 204 */   static final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
/*     */ 
/* 328 */   public static final ErrorHandler DRACONIAN_ERROR_HANDLER = new ErrorHandler() {
/*     */     public void warning(SAXParseException exception) {
/*     */     }
/*     */ 
/*     */     public void error(SAXParseException exception) throws SAXException {
/* 333 */       throw exception;
/*     */     }
/*     */ 
/*     */     public void fatalError(SAXParseException exception) throws SAXException {
/* 337 */       throw exception;
/*     */     }
/* 328 */   };
/*     */ 
/*     */   public static String getPrefix(String s)
/*     */   {
/*  79 */     int i = s.indexOf(':');
/*  80 */     if (i == -1)
/*  81 */       return null;
/*  82 */     return s.substring(0, i);
/*     */   }
/*     */ 
/*     */   public static String getLocalPart(String s) {
/*  86 */     int i = s.indexOf(':');
/*  87 */     if (i == -1)
/*  88 */       return s;
/*  89 */     return s.substring(i + 1);
/*     */   }
/*     */ 
/*     */   public static String getAttributeOrNull(Element e, String name)
/*     */   {
/*  95 */     Attr a = e.getAttributeNode(name);
/*  96 */     if (a == null)
/*  97 */       return null;
/*  98 */     return a.getValue();
/*     */   }
/*     */ 
/*     */   public static String getAttributeNSOrNull(Element e, String name, String nsURI)
/*     */   {
/* 105 */     Attr a = e.getAttributeNodeNS(nsURI, name);
/* 106 */     if (a == null)
/* 107 */       return null;
/* 108 */     return a.getValue();
/*     */   }
/*     */ 
/*     */   public static String getAttributeNSOrNull(Element e, QName name)
/*     */   {
/* 114 */     Attr a = e.getAttributeNodeNS(name.getNamespaceURI(), name.getLocalPart());
/* 115 */     if (a == null)
/* 116 */       return null;
/* 117 */     return a.getValue();
/*     */   }
/*     */ 
/*     */   public static Iterator getAllChildren(Element element)
/*     */   {
/* 149 */     return new NodeListIterator(element.getChildNodes());
/*     */   }
/*     */ 
/*     */   public static Iterator getAllAttributes(Element element) {
/* 153 */     return new NamedNodeMapIterator(element.getAttributes());
/*     */   }
/*     */ 
/*     */   public static List<String> parseTokenList(String tokenList) {
/* 157 */     List result = new ArrayList();
/* 158 */     StringTokenizer tokenizer = new StringTokenizer(tokenList, " ");
/* 159 */     while (tokenizer.hasMoreTokens()) {
/* 160 */       result.add(tokenizer.nextToken());
/*     */     }
/* 162 */     return result;
/*     */   }
/*     */ 
/*     */   public static String getTextForNode(Node node) {
/* 166 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 168 */     NodeList children = node.getChildNodes();
/* 169 */     if (children.getLength() == 0) {
/* 170 */       return null;
/*     */     }
/* 172 */     for (int i = 0; i < children.getLength(); i++) {
/* 173 */       Node n = children.item(i);
/*     */ 
/* 175 */       if ((n instanceof Text)) {
/* 176 */         sb.append(n.getNodeValue());
/* 177 */       } else if ((n instanceof EntityReference)) {
/* 178 */         String s = getTextForNode(n);
/* 179 */         if (s == null) {
/* 180 */           return null;
/*     */         }
/* 182 */         sb.append(s);
/*     */       } else {
/* 184 */         return null;
/*     */       }
/*     */     }
/* 187 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static InputStream getUTF8Stream(String s) {
/*     */     try {
/* 192 */       ByteArrayBuffer bab = new ByteArrayBuffer();
/* 193 */       Writer w = new OutputStreamWriter(bab, "utf-8");
/* 194 */       w.write(s);
/* 195 */       w.close();
/* 196 */       return bab.newInputStream(); } catch (IOException e) {
/*     */     }
/* 198 */     throw new RuntimeException("should not happen");
/*     */   }
/*     */ 
/*     */   public static Transformer newTransformer()
/*     */   {
/*     */     try
/*     */     {
/* 215 */       return transformerFactory.newTransformer(); } catch (TransformerConfigurationException tex) {
/*     */     }
/* 217 */     throw new IllegalStateException("Unable to create a JAXP transformer");
/*     */   }
/*     */ 
/*     */   public static <T extends Result> T identityTransform(Source src, T result)
/*     */     throws TransformerException, SAXException, ParserConfigurationException, IOException
/*     */   {
/* 226 */     if ((src instanceof StreamSource))
/*     */     {
/* 229 */       StreamSource ssrc = (StreamSource)src;
/* 230 */       TransformerHandler th = ((SAXTransformerFactory)transformerFactory).newTransformerHandler();
/* 231 */       th.setResult(result);
/* 232 */       XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();
/* 233 */       reader.setContentHandler(th);
/* 234 */       reader.setProperty("http://xml.org/sax/properties/lexical-handler", th);
/* 235 */       reader.parse(toInputSource(ssrc));
/*     */     } else {
/* 237 */       newTransformer().transform(src, result);
/*     */     }
/* 239 */     return result;
/*     */   }
/*     */ 
/*     */   private static InputSource toInputSource(StreamSource src) {
/* 243 */     InputSource is = new InputSource();
/* 244 */     is.setByteStream(src.getInputStream());
/* 245 */     is.setCharacterStream(src.getReader());
/* 246 */     is.setPublicId(src.getPublicId());
/* 247 */     is.setSystemId(src.getSystemId());
/* 248 */     return is;
/*     */   }
/*     */ 
/*     */   public static EntityResolver createEntityResolver(@Nullable URL catalogUrl)
/*     */   {
/* 256 */     CatalogManager manager = new CatalogManager();
/* 257 */     manager.setIgnoreMissingProperties(true);
/*     */ 
/* 259 */     manager.setUseStaticCatalog(false);
/* 260 */     Catalog catalog = manager.getCatalog();
/*     */     try {
/* 262 */       if (catalogUrl != null)
/* 263 */         catalog.parseCatalog(catalogUrl);
/*     */     }
/*     */     catch (IOException e) {
/* 266 */       throw new ServerRtException("server.rt.err", new Object[] { e });
/*     */     }
/* 268 */     return workaroundCatalogResolver(catalog);
/*     */   }
/*     */ 
/*     */   public static EntityResolver createDefaultCatalogResolver()
/*     */   {
/* 277 */     CatalogManager manager = new CatalogManager();
/* 278 */     manager.setIgnoreMissingProperties(true);
/*     */ 
/* 280 */     manager.setUseStaticCatalog(false);
/*     */ 
/* 282 */     ClassLoader cl = Thread.currentThread().getContextClassLoader();
/*     */ 
/* 284 */     Catalog catalog = manager.getCatalog();
/*     */     try
/*     */     {
/*     */       Enumeration catalogEnum;
/*     */       Enumeration catalogEnum;
/* 286 */       if (cl == null)
/* 287 */         catalogEnum = ClassLoader.getSystemResources("META-INF/jax-ws-catalog.xml");
/*     */       else {
/* 289 */         catalogEnum = cl.getResources("META-INF/jax-ws-catalog.xml");
/*     */       }
/*     */ 
/* 292 */       while (catalogEnum.hasMoreElements()) {
/* 293 */         URL url = (URL)catalogEnum.nextElement();
/* 294 */         catalog.parseCatalog(url);
/*     */       }
/*     */     } catch (IOException e) {
/* 297 */       throw new WebServiceException(e);
/*     */     }
/*     */ 
/* 300 */     return workaroundCatalogResolver(catalog);
/*     */   }
/*     */ 
/*     */   private static CatalogResolver workaroundCatalogResolver(Catalog catalog)
/*     */   {
/* 312 */     CatalogManager manager = new CatalogManager()
/*     */     {
/*     */       public Catalog getCatalog() {
/* 315 */         return this.val$catalog;
/*     */       }
/*     */     };
/* 318 */     manager.setIgnoreMissingProperties(true);
/*     */ 
/* 320 */     manager.setUseStaticCatalog(false);
/*     */ 
/* 322 */     return new CatalogResolver(manager);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 207 */     saxParserFactory.setNamespaceAware(true);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.util.xml.XmlUtil
 * JD-Core Version:    0.6.2
 */