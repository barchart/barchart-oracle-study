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
/*     */ final class Import extends TopLevelElement
/*     */ {
/*  49 */   private Stylesheet _imported = null;
/*     */ 
/*     */   public Stylesheet getImportedStylesheet() {
/*  52 */     return this._imported;
/*     */   }
/*     */ 
/*     */   public void parseContents(Parser parser) {
/*  56 */     XSLTC xsltc = parser.getXSLTC();
/*  57 */     Stylesheet context = parser.getCurrentStylesheet();
/*     */     try
/*     */     {
/*  60 */       String docToLoad = getAttribute("href");
/*  61 */       if (context.checkForLoop(docToLoad)) {
/*  62 */         ErrorMsg msg = new ErrorMsg("CIRCULAR_INCLUDE_ERR", docToLoad, this);
/*     */ 
/*  64 */         parser.reportError(2, msg);
/*     */       }
/*     */       else
/*     */       {
/*  68 */         InputSource input = null;
/*  69 */         XMLReader reader = null;
/*  70 */         String currLoadedDoc = context.getSystemId();
/*  71 */         SourceLoader loader = context.getSourceLoader();
/*     */ 
/*  74 */         if (loader != null) {
/*  75 */           input = loader.loadSource(docToLoad, currLoadedDoc, xsltc);
/*  76 */           if (input != null) {
/*  77 */             docToLoad = input.getSystemId();
/*  78 */             reader = xsltc.getXMLReader();
/*  79 */           } else if (parser.errorsFound())
/*     */           {
/*     */             return;
/*     */           }
/*     */         }
/*     */ 
/*  85 */         if (input == null) {
/*  86 */           docToLoad = SystemIDResolver.getAbsoluteURI(docToLoad, currLoadedDoc);
/*  87 */           input = new InputSource(docToLoad);
/*     */         }
/*     */ 
/*  91 */         if (input == null) {
/*  92 */           ErrorMsg msg = new ErrorMsg("FILE_NOT_FOUND_ERR", docToLoad, this);
/*     */ 
/*  94 */           parser.reportError(2, msg);
/*     */         }
/*     */         else
/*     */         {
/*     */           SyntaxTreeNode root;
/*     */           SyntaxTreeNode root;
/*  99 */           if (reader != null) {
/* 100 */             root = parser.parse(reader, input);
/*     */           }
/*     */           else {
/* 103 */             root = parser.parse(input);
/*     */           }
/*     */ 
/* 106 */           if (root == null) return;
/* 107 */           this._imported = parser.makeStylesheet(root);
/* 108 */           if (this._imported == null)
/*     */             return;
/* 110 */           this._imported.setSourceLoader(loader);
/* 111 */           this._imported.setSystemId(docToLoad);
/* 112 */           this._imported.setParentStylesheet(context);
/* 113 */           this._imported.setImportingStylesheet(context);
/* 114 */           this._imported.setTemplateInlining(context.getTemplateInlining());
/*     */ 
/* 117 */           int currPrecedence = parser.getCurrentImportPrecedence();
/* 118 */           int nextPrecedence = parser.getNextImportPrecedence();
/* 119 */           this._imported.setImportPrecedence(currPrecedence);
/* 120 */           context.setImportPrecedence(nextPrecedence);
/* 121 */           parser.setCurrentStylesheet(this._imported);
/* 122 */           this._imported.parseContents(parser);
/*     */ 
/* 124 */           Enumeration elements = this._imported.elements();
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
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.Import
 * JD-Core Version:    0.6.2
 */