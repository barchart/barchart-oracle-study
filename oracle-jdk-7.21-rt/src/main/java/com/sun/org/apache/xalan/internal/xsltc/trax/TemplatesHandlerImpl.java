/*     */ package com.sun.org.apache.xalan.internal.xsltc.trax;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.SourceLoader;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
/*     */ import java.util.Vector;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Templates;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.URIResolver;
/*     */ import javax.xml.transform.sax.TemplatesHandler;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.ContentHandler;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.Locator;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class TemplatesHandlerImpl
/*     */   implements ContentHandler, TemplatesHandler, SourceLoader
/*     */ {
/*     */   private String _systemId;
/*     */   private int _indentNumber;
/*  70 */   private URIResolver _uriResolver = null;
/*     */ 
/*  76 */   private TransformerFactoryImpl _tfactory = null;
/*     */ 
/*  81 */   private Parser _parser = null;
/*     */ 
/*  86 */   private TemplatesImpl _templates = null;
/*     */ 
/*     */   protected TemplatesHandlerImpl(int indentNumber, TransformerFactoryImpl tfactory)
/*     */   {
/*  94 */     this._indentNumber = indentNumber;
/*  95 */     this._tfactory = tfactory;
/*     */ 
/*  98 */     XSLTC xsltc = new XSLTC(tfactory.useServicesMechnism());
/*  99 */     if (tfactory.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
/* 100 */       xsltc.setSecureProcessing(true);
/*     */     }
/* 102 */     if ("true".equals(tfactory.getAttribute("enable-inlining")))
/* 103 */       xsltc.setTemplateInlining(true);
/*     */     else {
/* 105 */       xsltc.setTemplateInlining(false);
/*     */     }
/* 107 */     this._parser = xsltc.getParser();
/*     */   }
/*     */ 
/*     */   public String getSystemId()
/*     */   {
/* 117 */     return this._systemId;
/*     */   }
/*     */ 
/*     */   public void setSystemId(String id)
/*     */   {
/* 127 */     this._systemId = id;
/*     */   }
/*     */ 
/*     */   public void setURIResolver(URIResolver resolver)
/*     */   {
/* 134 */     this._uriResolver = resolver;
/*     */   }
/*     */ 
/*     */   public Templates getTemplates()
/*     */   {
/* 147 */     return this._templates;
/*     */   }
/*     */ 
/*     */   public InputSource loadSource(String href, String context, XSLTC xsltc)
/*     */   {
/*     */     try
/*     */     {
/* 162 */       Source source = this._uriResolver.resolve(href, context);
/* 163 */       if (source != null) {
/* 164 */         return Util.getInputSource(xsltc, source);
/*     */       }
/*     */     }
/*     */     catch (TransformerException e)
/*     */     {
/*     */     }
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */   public void startDocument()
/*     */   {
/* 179 */     XSLTC xsltc = this._parser.getXSLTC();
/* 180 */     xsltc.init();
/* 181 */     xsltc.setOutputType(2);
/* 182 */     this._parser.startDocument();
/*     */   }
/*     */ 
/*     */   public void endDocument()
/*     */     throws SAXException
/*     */   {
/* 189 */     this._parser.endDocument();
/*     */     try
/*     */     {
/* 193 */       XSLTC xsltc = this._parser.getXSLTC();
/*     */       String transletName;
/* 197 */       if (this._systemId != null) {
/* 198 */         transletName = Util.baseName(this._systemId);
/*     */       }
/*     */       else {
/* 201 */         transletName = (String)this._tfactory.getAttribute("translet-name");
/*     */       }
/* 203 */       xsltc.setClassName(transletName);
/*     */ 
/* 206 */       String transletName = xsltc.getClassName();
/*     */ 
/* 208 */       Stylesheet stylesheet = null;
/* 209 */       SyntaxTreeNode root = this._parser.getDocumentRoot();
/*     */ 
/* 212 */       if ((!this._parser.errorsFound()) && (root != null))
/*     */       {
/* 214 */         stylesheet = this._parser.makeStylesheet(root);
/* 215 */         stylesheet.setSystemId(this._systemId);
/* 216 */         stylesheet.setParentStylesheet(null);
/*     */ 
/* 218 */         if (xsltc.getTemplateInlining())
/* 219 */           stylesheet.setTemplateInlining(true);
/*     */         else {
/* 221 */           stylesheet.setTemplateInlining(false);
/*     */         }
/*     */ 
/* 224 */         if (this._uriResolver != null) {
/* 225 */           stylesheet.setSourceLoader(this);
/*     */         }
/*     */ 
/* 228 */         this._parser.setCurrentStylesheet(stylesheet);
/*     */ 
/* 231 */         xsltc.setStylesheet(stylesheet);
/*     */ 
/* 234 */         this._parser.createAST(stylesheet);
/*     */       }
/*     */ 
/* 238 */       if ((!this._parser.errorsFound()) && (stylesheet != null)) {
/* 239 */         stylesheet.setMultiDocument(xsltc.isMultiDocument());
/* 240 */         stylesheet.setHasIdCall(xsltc.hasIdCall());
/*     */ 
/* 243 */         synchronized (xsltc.getClass()) {
/* 244 */           stylesheet.translate();
/*     */         }
/*     */       }
/*     */ 
/* 248 */       if (!this._parser.errorsFound())
/*     */       {
/* 250 */         byte[][] bytecodes = xsltc.getBytecodes();
/* 251 */         if (bytecodes != null) {
/* 252 */           this._templates = new TemplatesImpl(xsltc.getBytecodes(), transletName, this._parser.getOutputProperties(), this._indentNumber, this._tfactory);
/*     */ 
/* 257 */           if (this._uriResolver != null)
/* 258 */             this._templates.setURIResolver(this._uriResolver);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 263 */         StringBuffer errorMessage = new StringBuffer();
/* 264 */         Vector errors = this._parser.getErrors();
/* 265 */         int count = errors.size();
/* 266 */         for (int i = 0; i < count; i++) {
/* 267 */           if (errorMessage.length() > 0)
/* 268 */             errorMessage.append('\n');
/* 269 */           errorMessage.append(errors.elementAt(i).toString());
/*     */         }
/* 271 */         throw new SAXException("JAXP_COMPILE_ERR", new TransformerException(errorMessage.toString()));
/*     */       }
/*     */     }
/*     */     catch (CompilerException e) {
/* 275 */       throw new SAXException("JAXP_COMPILE_ERR", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startPrefixMapping(String prefix, String uri)
/*     */   {
/* 283 */     this._parser.startPrefixMapping(prefix, uri);
/*     */   }
/*     */ 
/*     */   public void endPrefixMapping(String prefix)
/*     */   {
/* 290 */     this._parser.endPrefixMapping(prefix);
/*     */   }
/*     */ 
/*     */   public void startElement(String uri, String localname, String qname, Attributes attributes)
/*     */     throws SAXException
/*     */   {
/* 299 */     this._parser.startElement(uri, localname, qname, attributes);
/*     */   }
/*     */ 
/*     */   public void endElement(String uri, String localname, String qname)
/*     */   {
/* 306 */     this._parser.endElement(uri, localname, qname);
/*     */   }
/*     */ 
/*     */   public void characters(char[] ch, int start, int length)
/*     */   {
/* 313 */     this._parser.characters(ch, start, length);
/*     */   }
/*     */ 
/*     */   public void processingInstruction(String name, String value)
/*     */   {
/* 320 */     this._parser.processingInstruction(name, value);
/*     */   }
/*     */ 
/*     */   public void ignorableWhitespace(char[] ch, int start, int length)
/*     */   {
/* 327 */     this._parser.ignorableWhitespace(ch, start, length);
/*     */   }
/*     */ 
/*     */   public void skippedEntity(String name)
/*     */   {
/* 334 */     this._parser.skippedEntity(name);
/*     */   }
/*     */ 
/*     */   public void setDocumentLocator(Locator locator)
/*     */   {
/* 341 */     setSystemId(locator.getSystemId());
/* 342 */     this._parser.setDocumentLocator(locator);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesHandlerImpl
 * JD-Core Version:    0.6.2
 */