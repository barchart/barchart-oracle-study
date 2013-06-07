/*     */ package com.sun.org.apache.xalan.internal.xsltc.dom;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.xsltc.DOM;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.TransletException;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
/*     */ import com.sun.org.apache.xml.internal.dtm.DTM;
/*     */ import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
/*     */ import com.sun.org.apache.xml.internal.dtm.DTMManager;
/*     */ import com.sun.org.apache.xml.internal.dtm.ref.EmptyIterator;
/*     */ import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
/*     */ import java.io.FileNotFoundException;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ 
/*     */ public final class LoadDocument
/*     */ {
/*     */   private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
/*     */ 
/*     */   public static DTMAxisIterator documentF(Object arg1, DTMAxisIterator arg2, String xslURI, AbstractTranslet translet, DOM dom)
/*     */     throws TransletException
/*     */   {
/*  64 */     String baseURI = null;
/*  65 */     int arg2FirstNode = arg2.next();
/*  66 */     if (arg2FirstNode == -1)
/*     */     {
/*  68 */       return EmptyIterator.getInstance();
/*     */     }
/*     */ 
/*  73 */     baseURI = dom.getDocumentURI(arg2FirstNode);
/*  74 */     if (!SystemIDResolver.isAbsoluteURI(baseURI)) {
/*  75 */       baseURI = SystemIDResolver.getAbsoluteURIFromRelative(baseURI);
/*     */     }
/*     */     try
/*     */     {
/*  79 */       if ((arg1 instanceof String)) {
/*  80 */         if (((String)arg1).length() == 0) {
/*  81 */           return document(xslURI, "", translet, dom);
/*     */         }
/*  83 */         return document((String)arg1, baseURI, translet, dom);
/*     */       }
/*  85 */       if ((arg1 instanceof DTMAxisIterator)) {
/*  86 */         return document((DTMAxisIterator)arg1, baseURI, translet, dom);
/*     */       }
/*  88 */       String err = "document(" + arg1.toString() + ")";
/*  89 */       throw new IllegalArgumentException(err);
/*     */     }
/*     */     catch (Exception e) {
/*  92 */       throw new TransletException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static DTMAxisIterator documentF(Object arg, String xslURI, AbstractTranslet translet, DOM dom)
/*     */     throws TransletException
/*     */   {
/*     */     try
/*     */     {
/* 106 */       if ((arg instanceof String)) {
/* 107 */         if (xslURI == null) {
/* 108 */           xslURI = "";
/*     */         }
/* 110 */         String baseURI = xslURI;
/* 111 */         if (!SystemIDResolver.isAbsoluteURI(xslURI)) {
/* 112 */           baseURI = SystemIDResolver.getAbsoluteURIFromRelative(xslURI);
/*     */         }
/* 114 */         String href = (String)arg;
/* 115 */         if (href.length() == 0) {
/* 116 */           href = "";
/*     */ 
/* 120 */           TemplatesImpl templates = (TemplatesImpl)translet.getTemplates();
/* 121 */           DOM sdom = null;
/* 122 */           if (templates != null) {
/* 123 */             sdom = templates.getStylesheetDOM();
/*     */           }
/*     */ 
/* 129 */           if (sdom != null) {
/* 130 */             return document(sdom, translet, dom);
/*     */           }
/*     */ 
/* 133 */           return document(href, baseURI, translet, dom, true);
/*     */         }
/*     */ 
/* 137 */         return document(href, baseURI, translet, dom);
/*     */       }
/* 139 */       if ((arg instanceof DTMAxisIterator)) {
/* 140 */         return document((DTMAxisIterator)arg, null, translet, dom);
/*     */       }
/* 142 */       String err = "document(" + arg.toString() + ")";
/* 143 */       throw new IllegalArgumentException(err);
/*     */     }
/*     */     catch (Exception e) {
/* 146 */       throw new TransletException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static DTMAxisIterator document(String uri, String base, AbstractTranslet translet, DOM dom)
/*     */     throws Exception
/*     */   {
/* 154 */     return document(uri, base, translet, dom, false);
/*     */   }
/*     */ 
/*     */   private static DTMAxisIterator document(String uri, String base, AbstractTranslet translet, DOM dom, boolean cacheDOM)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 163 */       String originalUri = uri;
/* 164 */       MultiDOM multiplexer = (MultiDOM)dom;
/*     */ 
/* 167 */       if ((base != null) && (!base.equals(""))) {
/* 168 */         uri = SystemIDResolver.getAbsoluteURI(uri, base);
/*     */       }
/*     */ 
/* 173 */       if ((uri == null) || (uri.equals(""))) {
/* 174 */         return EmptyIterator.getInstance();
/*     */       }
/*     */ 
/* 178 */       int mask = multiplexer.getDocumentMask(uri);
/* 179 */       if (mask != -1) {
/* 180 */         DOM newDom = ((DOMAdapter)multiplexer.getDOMAdapter(uri)).getDOMImpl();
/*     */ 
/* 182 */         if ((newDom instanceof DOMEnhancedForDTM)) {
/* 183 */           return new SingletonIterator(((DOMEnhancedForDTM)newDom).getDocument(), true);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 190 */       DOMCache cache = translet.getDOMCache();
/*     */ 
/* 193 */       mask = multiplexer.nextMask();
/*     */       DOM newdom;
/* 195 */       if (cache != null) {
/* 196 */         DOM newdom = cache.retrieveDocument(base, originalUri, translet);
/* 197 */         if (newdom == null) {
/* 198 */           Exception e = new FileNotFoundException(originalUri);
/* 199 */           throw new TransletException(e);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 205 */         XSLTCDTMManager dtmManager = (XSLTCDTMManager)multiplexer.getDTMManager();
/*     */ 
/* 207 */         DOMEnhancedForDTM enhancedDOM = (DOMEnhancedForDTM)dtmManager.getDTM(new StreamSource(uri), false, null, true, false, translet.hasIdCall(), cacheDOM);
/*     */ 
/* 211 */         newdom = enhancedDOM;
/*     */ 
/* 214 */         if (cacheDOM) {
/* 215 */           TemplatesImpl templates = (TemplatesImpl)translet.getTemplates();
/* 216 */           if (templates != null) {
/* 217 */             templates.setStylesheetDOM(enhancedDOM);
/*     */           }
/*     */         }
/*     */ 
/* 221 */         translet.prepassDocument(enhancedDOM);
/* 222 */         enhancedDOM.setDocumentURI(uri);
/*     */       }
/*     */ 
/* 226 */       DOMAdapter domAdapter = translet.makeDOMAdapter(newdom);
/* 227 */       multiplexer.addDOMAdapter(domAdapter);
/*     */ 
/* 230 */       translet.buildKeys(domAdapter, null, null, newdom.getDocument());
/*     */ 
/* 233 */       return new SingletonIterator(newdom.getDocument(), true);
/*     */     } catch (Exception e) {
/* 235 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static DTMAxisIterator document(DTMAxisIterator arg1, String baseURI, AbstractTranslet translet, DOM dom)
/*     */     throws Exception
/*     */   {
/* 245 */     UnionIterator union = new UnionIterator(dom);
/* 246 */     int node = -1;
/*     */ 
/* 248 */     while ((node = arg1.next()) != -1) {
/* 249 */       String uri = dom.getStringValueX(node);
/*     */ 
/* 251 */       if (baseURI == null) {
/* 252 */         baseURI = dom.getDocumentURI(node);
/* 253 */         if (!SystemIDResolver.isAbsoluteURI(baseURI))
/* 254 */           baseURI = SystemIDResolver.getAbsoluteURIFromRelative(baseURI);
/*     */       }
/* 256 */       union.addIterator(document(uri, baseURI, translet, dom));
/*     */     }
/* 258 */     return union;
/*     */   }
/*     */ 
/*     */   private static DTMAxisIterator document(DOM newdom, AbstractTranslet translet, DOM dom)
/*     */     throws Exception
/*     */   {
/* 275 */     DTMManager dtmManager = ((MultiDOM)dom).getDTMManager();
/*     */ 
/* 277 */     if ((dtmManager != null) && ((newdom instanceof DTM))) {
/* 278 */       ((DTM)newdom).migrateTo(dtmManager);
/*     */     }
/*     */ 
/* 281 */     translet.prepassDocument(newdom);
/*     */ 
/* 284 */     DOMAdapter domAdapter = translet.makeDOMAdapter(newdom);
/* 285 */     ((MultiDOM)dom).addDOMAdapter(domAdapter);
/*     */ 
/* 288 */     translet.buildKeys(domAdapter, null, null, newdom.getDocument());
/*     */ 
/* 292 */     return new SingletonIterator(newdom.getDocument(), true);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.dom.LoadDocument
 * JD-Core Version:    0.6.2
 */