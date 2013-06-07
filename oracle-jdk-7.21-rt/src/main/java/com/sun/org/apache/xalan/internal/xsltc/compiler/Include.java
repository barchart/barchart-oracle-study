/*     */ package com.sun.org.apache.xalan.internal.xsltc.compiler;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
/*     */ import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
/*     */ import java.util.Enumeration;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.XMLReader;
/*     */ 
/*     */ final class Include extends TopLevelElement
/*     */ {
/*  50 */   private Stylesheet _included = null;
/*     */ 
/*     */   public Stylesheet getIncludedStylesheet() {
/*  53 */     return this._included;
/*     */   }
/*     */ 
/*     */   public void parseContents(Parser parser) {
/*  57 */     XSLTC xsltc = parser.getXSLTC();
/*  58 */     Stylesheet context = parser.getCurrentStylesheet();
/*     */ 
/*  60 */     String docToLoad = getAttribute("href");
/*     */     try {
/*  62 */       if (context.checkForLoop(docToLoad)) {
/*  63 */         ErrorMsg msg = new ErrorMsg("CIRCULAR_INCLUDE_ERR", docToLoad, this);
/*     */ 
/*  65 */         parser.reportError(2, msg);
/*     */       }
/*     */       else
/*     */       {
/*  69 */         InputSource input = null;
/*  70 */         XMLReader reader = null;
/*  71 */         String currLoadedDoc = context.getSystemId();
/*  72 */         SourceLoader loader = context.getSourceLoader();
/*     */ 
/*  75 */         if (loader != null) {
/*  76 */           input = loader.loadSource(docToLoad, currLoadedDoc, xsltc);
/*  77 */           if (input != null) {
/*  78 */             docToLoad = input.getSystemId();
/*  79 */             reader = xsltc.getXMLReader();
/*  80 */           } else if (parser.errorsFound())
/*     */           {
/*     */             return;
/*     */           }
/*     */         }
/*     */ 
/*  86 */         if (input == null) {
/*  87 */           docToLoad = SystemIDResolver.getAbsoluteURI(docToLoad, currLoadedDoc);
/*  88 */           input = new InputSource(docToLoad);
/*     */         }
/*     */ 
/*  92 */         if (input == null) {
/*  93 */           ErrorMsg msg = new ErrorMsg("FILE_NOT_FOUND_ERR", docToLoad, this);
/*     */ 
/*  95 */           parser.reportError(2, msg);
/*     */         }
/*     */         else
/*     */         {
/*     */           SyntaxTreeNode root;
/*     */           SyntaxTreeNode root;
/* 100 */           if (reader != null) {
/* 101 */             root = parser.parse(reader, input);
/*     */           }
/*     */           else {
/* 104 */             root = parser.parse(input);
/*     */           }
/*     */ 
/* 107 */           if (root == null) return;
/* 108 */           this._included = parser.makeStylesheet(root);
/* 109 */           if (this._included == null)
/*     */             return;
/* 111 */           this._included.setSourceLoader(loader);
/* 112 */           this._included.setSystemId(docToLoad);
/* 113 */           this._included.setParentStylesheet(context);
/* 114 */           this._included.setIncludingStylesheet(context);
/* 115 */           this._included.setTemplateInlining(context.getTemplateInlining());
/*     */ 
/* 119 */           int precedence = context.getImportPrecedence();
/* 120 */           this._included.setImportPrecedence(precedence);
/* 121 */           parser.setCurrentStylesheet(this._included);
/* 122 */           this._included.parseContents(parser);
/*     */ 
/* 124 */           Enumeration elements = this._included.elements();
/* 125 */           Stylesheet topStylesheet = parser.getTopLevelStylesheet();
/* 126 */           while (elements.hasMoreElements()) {
/* 127 */             Object element = elements.nextElement();
/* 128 */             if ((element instanceof TopLevelElement))
/* 129 */               if ((element instanceof Variable)) {
/* 130 */                 topStylesheet.addVariable((Variable)element);
/*     */               }
/* 132 */               else if ((element instanceof Param)) {
/* 133 */                 topStylesheet.addParam((Param)element);
/*     */               }
/*     */               else
/* 136 */                 topStylesheet.addElement((TopLevelElement)element);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 142 */       e.printStackTrace();
/*     */     }
/*     */     finally {
/* 145 */       parser.setCurrentStylesheet(context);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Type typeCheck(SymbolTable stable) throws TypeCheckError {
/* 150 */     return Type.Void;
/*     */   }
/*     */ 
/*     */   public void translate(ClassGenerator classGen, MethodGenerator methodGen)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.Include
 * JD-Core Version:    0.6.2
 */