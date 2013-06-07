/*      */ package sun.org.mozilla.javascript.internal;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.IOException;
/*      */ import java.io.Reader;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import sun.org.mozilla.javascript.internal.ast.ArrayComprehension;
/*      */ import sun.org.mozilla.javascript.internal.ast.ArrayComprehensionLoop;
/*      */ import sun.org.mozilla.javascript.internal.ast.ArrayLiteral;
/*      */ import sun.org.mozilla.javascript.internal.ast.Assignment;
/*      */ import sun.org.mozilla.javascript.internal.ast.AstNode;
/*      */ import sun.org.mozilla.javascript.internal.ast.AstRoot;
/*      */ import sun.org.mozilla.javascript.internal.ast.Block;
/*      */ import sun.org.mozilla.javascript.internal.ast.BreakStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.CatchClause;
/*      */ import sun.org.mozilla.javascript.internal.ast.Comment;
/*      */ import sun.org.mozilla.javascript.internal.ast.ConditionalExpression;
/*      */ import sun.org.mozilla.javascript.internal.ast.ContinueStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.DestructuringForm;
/*      */ import sun.org.mozilla.javascript.internal.ast.DoLoop;
/*      */ import sun.org.mozilla.javascript.internal.ast.ElementGet;
/*      */ import sun.org.mozilla.javascript.internal.ast.EmptyExpression;
/*      */ import sun.org.mozilla.javascript.internal.ast.ErrorNode;
/*      */ import sun.org.mozilla.javascript.internal.ast.ExpressionStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.ForInLoop;
/*      */ import sun.org.mozilla.javascript.internal.ast.ForLoop;
/*      */ import sun.org.mozilla.javascript.internal.ast.FunctionCall;
/*      */ import sun.org.mozilla.javascript.internal.ast.FunctionNode;
/*      */ import sun.org.mozilla.javascript.internal.ast.IdeErrorReporter;
/*      */ import sun.org.mozilla.javascript.internal.ast.IfStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.InfixExpression;
/*      */ import sun.org.mozilla.javascript.internal.ast.Jump;
/*      */ import sun.org.mozilla.javascript.internal.ast.KeywordLiteral;
/*      */ import sun.org.mozilla.javascript.internal.ast.Label;
/*      */ import sun.org.mozilla.javascript.internal.ast.LabeledStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.LetNode;
/*      */ import sun.org.mozilla.javascript.internal.ast.Loop;
/*      */ import sun.org.mozilla.javascript.internal.ast.Name;
/*      */ import sun.org.mozilla.javascript.internal.ast.NewExpression;
/*      */ import sun.org.mozilla.javascript.internal.ast.NumberLiteral;
/*      */ import sun.org.mozilla.javascript.internal.ast.ObjectLiteral;
/*      */ import sun.org.mozilla.javascript.internal.ast.ObjectProperty;
/*      */ import sun.org.mozilla.javascript.internal.ast.ParenthesizedExpression;
/*      */ import sun.org.mozilla.javascript.internal.ast.PropertyGet;
/*      */ import sun.org.mozilla.javascript.internal.ast.RegExpLiteral;
/*      */ import sun.org.mozilla.javascript.internal.ast.ReturnStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.Scope;
/*      */ import sun.org.mozilla.javascript.internal.ast.ScriptNode;
/*      */ import sun.org.mozilla.javascript.internal.ast.StringLiteral;
/*      */ import sun.org.mozilla.javascript.internal.ast.SwitchCase;
/*      */ import sun.org.mozilla.javascript.internal.ast.SwitchStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.Symbol;
/*      */ import sun.org.mozilla.javascript.internal.ast.ThrowStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.TryStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.UnaryExpression;
/*      */ import sun.org.mozilla.javascript.internal.ast.VariableDeclaration;
/*      */ import sun.org.mozilla.javascript.internal.ast.VariableInitializer;
/*      */ import sun.org.mozilla.javascript.internal.ast.WhileLoop;
/*      */ import sun.org.mozilla.javascript.internal.ast.WithStatement;
/*      */ import sun.org.mozilla.javascript.internal.ast.XmlDotQuery;
/*      */ import sun.org.mozilla.javascript.internal.ast.XmlElemRef;
/*      */ import sun.org.mozilla.javascript.internal.ast.XmlExpression;
/*      */ import sun.org.mozilla.javascript.internal.ast.XmlLiteral;
/*      */ import sun.org.mozilla.javascript.internal.ast.XmlMemberGet;
/*      */ import sun.org.mozilla.javascript.internal.ast.XmlPropRef;
/*      */ import sun.org.mozilla.javascript.internal.ast.XmlRef;
/*      */ import sun.org.mozilla.javascript.internal.ast.XmlString;
/*      */ import sun.org.mozilla.javascript.internal.ast.Yield;
/*      */ 
/*      */ public class Parser
/*      */ {
/*      */   public static final int ARGC_LIMIT = 65536;
/*      */   static final int CLEAR_TI_MASK = 65535;
/*      */   static final int TI_AFTER_EOL = 65536;
/*      */   static final int TI_CHECK_LABEL = 131072;
/*      */   CompilerEnvirons compilerEnv;
/*      */   private ErrorReporter errorReporter;
/*      */   private IdeErrorReporter errorCollector;
/*      */   private String sourceURI;
/*      */   private char[] sourceChars;
/*      */   boolean calledByCompileFunction;
/*      */   private boolean parseFinished;
/*      */   private TokenStream ts;
/*  108 */   private int currentFlaggedToken = 0;
/*      */   private int currentToken;
/*      */   private int syntaxErrorCount;
/*      */   private List<Comment> scannedComments;
/*      */   private String currentJsDocComment;
/*      */   protected int nestingOfFunction;
/*      */   private LabeledStatement currentLabel;
/*      */   private boolean inDestructuringAssignment;
/*      */   protected boolean inUseStrictDirective;
/*      */   ScriptNode currentScriptOrFn;
/*      */   Scope currentScope;
/*      */   int nestingOfWith;
/*      */   private int endFlags;
/*      */   private boolean inForInit;
/*      */   private Map<String, LabeledStatement> labelSet;
/*      */   private List<Loop> loopSet;
/*      */   private List<Jump> loopAndSwitchSet;
/*      */   private int prevNameTokenStart;
/*  136 */   private String prevNameTokenString = "";
/*      */   private int prevNameTokenLineno;
/*      */ 
/*      */   public Parser()
/*      */   {
/*  146 */     this(new CompilerEnvirons());
/*      */   }
/*      */ 
/*      */   public Parser(CompilerEnvirons paramCompilerEnvirons) {
/*  150 */     this(paramCompilerEnvirons, paramCompilerEnvirons.getErrorReporter());
/*      */   }
/*      */ 
/*      */   public Parser(CompilerEnvirons paramCompilerEnvirons, ErrorReporter paramErrorReporter) {
/*  154 */     this.compilerEnv = paramCompilerEnvirons;
/*  155 */     this.errorReporter = paramErrorReporter;
/*  156 */     if ((paramErrorReporter instanceof IdeErrorReporter))
/*  157 */       this.errorCollector = ((IdeErrorReporter)paramErrorReporter);
/*      */   }
/*      */ 
/*      */   void addStrictWarning(String paramString1, String paramString2)
/*      */   {
/*  163 */     int i = -1; int j = -1;
/*  164 */     if (this.ts != null) {
/*  165 */       i = this.ts.tokenBeg;
/*  166 */       j = this.ts.tokenEnd - this.ts.tokenBeg;
/*      */     }
/*  168 */     addStrictWarning(paramString1, paramString2, i, j);
/*      */   }
/*      */ 
/*      */   void addStrictWarning(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*      */   {
/*  173 */     if (this.compilerEnv.isStrictMode())
/*  174 */       addWarning(paramString1, paramString2, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   void addWarning(String paramString1, String paramString2) {
/*  178 */     int i = -1; int j = -1;
/*  179 */     if (this.ts != null) {
/*  180 */       i = this.ts.tokenBeg;
/*  181 */       j = this.ts.tokenEnd - this.ts.tokenBeg;
/*      */     }
/*  183 */     addWarning(paramString1, paramString2, i, j);
/*      */   }
/*      */ 
/*      */   void addWarning(String paramString, int paramInt1, int paramInt2) {
/*  187 */     addWarning(paramString, null, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   void addWarning(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*      */   {
/*  193 */     String str = lookupMessage(paramString1, paramString2);
/*  194 */     if (this.compilerEnv.reportWarningAsError())
/*  195 */       addError(paramString1, paramString2, paramInt1, paramInt2);
/*  196 */     else if (this.errorCollector != null)
/*  197 */       this.errorCollector.warning(str, this.sourceURI, paramInt1, paramInt2);
/*      */     else
/*  199 */       this.errorReporter.warning(str, this.sourceURI, this.ts.getLineno(), this.ts.getLine(), this.ts.getOffset());
/*      */   }
/*      */ 
/*      */   void addError(String paramString)
/*      */   {
/*  205 */     addError(paramString, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
/*      */   }
/*      */ 
/*      */   void addError(String paramString, int paramInt1, int paramInt2) {
/*  209 */     addError(paramString, null, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   void addError(String paramString1, String paramString2) {
/*  213 */     addError(paramString1, paramString2, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
/*      */   }
/*      */ 
/*      */   void addError(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*      */   {
/*  219 */     this.syntaxErrorCount += 1;
/*  220 */     String str1 = lookupMessage(paramString1, paramString2);
/*  221 */     if (this.errorCollector != null) {
/*  222 */       this.errorCollector.error(str1, this.sourceURI, paramInt1, paramInt2);
/*      */     } else {
/*  224 */       int i = 1; int j = 1;
/*  225 */       String str2 = "";
/*  226 */       if (this.ts != null) {
/*  227 */         i = this.ts.getLineno();
/*  228 */         str2 = this.ts.getLine();
/*  229 */         j = this.ts.getOffset();
/*      */       }
/*  231 */       this.errorReporter.error(str1, this.sourceURI, i, str2, j);
/*      */     }
/*      */   }
/*      */ 
/*      */   String lookupMessage(String paramString) {
/*  236 */     return lookupMessage(paramString, null);
/*      */   }
/*      */ 
/*      */   String lookupMessage(String paramString1, String paramString2) {
/*  240 */     return paramString2 == null ? ScriptRuntime.getMessage0(paramString1) : ScriptRuntime.getMessage1(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   void reportError(String paramString)
/*      */   {
/*  246 */     reportError(paramString, null);
/*      */   }
/*      */ 
/*      */   void reportError(String paramString1, String paramString2) {
/*  250 */     if (this.ts == null)
/*  251 */       reportError(paramString1, paramString2, 1, 1);
/*      */     else
/*  253 */       reportError(paramString1, paramString2, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
/*      */   }
/*      */ 
/*      */   void reportError(String paramString, int paramInt1, int paramInt2)
/*      */   {
/*  260 */     reportError(paramString, null, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   void reportError(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*      */   {
/*  266 */     addError(paramString1, paramInt1, paramInt2);
/*      */ 
/*  268 */     if (!this.compilerEnv.recoverFromErrors())
/*  269 */       throw new ParserException(null);
/*      */   }
/*      */ 
/*      */   private int getNodeEnd(AstNode paramAstNode)
/*      */   {
/*  277 */     return paramAstNode.getPosition() + paramAstNode.getLength();
/*      */   }
/*      */ 
/*      */   private void recordComment(int paramInt) {
/*  281 */     if (this.scannedComments == null) {
/*  282 */       this.scannedComments = new ArrayList();
/*      */     }
/*  284 */     String str = this.ts.getAndResetCurrentComment();
/*  285 */     if ((this.ts.commentType == Token.CommentType.JSDOC) && (this.compilerEnv.isRecordingLocalJsDocComments()))
/*      */     {
/*  287 */       this.currentJsDocComment = str;
/*      */     }
/*  289 */     Comment localComment = new Comment(this.ts.tokenBeg, this.ts.getTokenLength(), this.ts.commentType, str);
/*      */ 
/*  293 */     localComment.setLineno(paramInt);
/*  294 */     this.scannedComments.add(localComment);
/*      */   }
/*      */ 
/*      */   private String getAndResetJsDoc() {
/*  298 */     String str = this.currentJsDocComment;
/*  299 */     this.currentJsDocComment = null;
/*  300 */     return str;
/*      */   }
/*      */ 
/*      */   private int peekToken()
/*      */     throws IOException
/*      */   {
/*  323 */     if (this.currentFlaggedToken != 0) {
/*  324 */       return this.currentToken;
/*      */     }
/*      */ 
/*  327 */     int i = this.ts.getLineno();
/*  328 */     int j = this.ts.getToken();
/*  329 */     int k = 0;
/*      */ 
/*  332 */     while ((j == 1) || (j == 161)) {
/*  333 */       if (j == 1) {
/*  334 */         i++;
/*  335 */         k = 1;
/*      */       } else {
/*  337 */         k = 0;
/*  338 */         if (this.compilerEnv.isRecordingComments()) {
/*  339 */           recordComment(i);
/*      */         }
/*      */       }
/*  342 */       j = this.ts.getToken();
/*      */     }
/*      */ 
/*  345 */     this.currentToken = j;
/*  346 */     this.currentFlaggedToken = (j | (k != 0 ? 65536 : 0));
/*  347 */     return this.currentToken;
/*      */   }
/*      */ 
/*      */   private int peekFlaggedToken()
/*      */     throws IOException
/*      */   {
/*  353 */     peekToken();
/*  354 */     return this.currentFlaggedToken;
/*      */   }
/*      */ 
/*      */   private void consumeToken() {
/*  358 */     this.currentFlaggedToken = 0;
/*      */   }
/*      */ 
/*      */   private int nextToken()
/*      */     throws IOException
/*      */   {
/*  364 */     int i = peekToken();
/*  365 */     consumeToken();
/*  366 */     return i;
/*      */   }
/*      */ 
/*      */   private int nextFlaggedToken()
/*      */     throws IOException
/*      */   {
/*  372 */     peekToken();
/*  373 */     int i = this.currentFlaggedToken;
/*  374 */     consumeToken();
/*  375 */     return i;
/*      */   }
/*      */ 
/*      */   private boolean matchToken(int paramInt)
/*      */     throws IOException
/*      */   {
/*  381 */     if (peekToken() != paramInt) {
/*  382 */       return false;
/*      */     }
/*  384 */     consumeToken();
/*  385 */     return true;
/*      */   }
/*      */ 
/*      */   private int peekTokenOrEOL()
/*      */     throws IOException
/*      */   {
/*  396 */     int i = peekToken();
/*      */ 
/*  398 */     if ((this.currentFlaggedToken & 0x10000) != 0) {
/*  399 */       i = 1;
/*      */     }
/*  401 */     return i;
/*      */   }
/*      */ 
/*      */   private boolean mustMatchToken(int paramInt, String paramString)
/*      */     throws IOException
/*      */   {
/*  407 */     return mustMatchToken(paramInt, paramString, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
/*      */   }
/*      */ 
/*      */   private boolean mustMatchToken(int paramInt1, String paramString, int paramInt2, int paramInt3)
/*      */     throws IOException
/*      */   {
/*  414 */     if (matchToken(paramInt1)) {
/*  415 */       return true;
/*      */     }
/*  417 */     reportError(paramString, paramInt2, paramInt3);
/*  418 */     return false;
/*      */   }
/*      */ 
/*      */   private void mustHaveXML() {
/*  422 */     if (!this.compilerEnv.isXmlAvailable())
/*  423 */       reportError("msg.XML.not.available");
/*      */   }
/*      */ 
/*      */   public boolean eof()
/*      */   {
/*  428 */     return this.ts.eof();
/*      */   }
/*      */ 
/*      */   boolean insideFunction() {
/*  432 */     return this.nestingOfFunction != 0;
/*      */   }
/*      */ 
/*      */   void pushScope(Scope paramScope) {
/*  436 */     Scope localScope = paramScope.getParentScope();
/*      */ 
/*  439 */     if (localScope != null) {
/*  440 */       if (localScope != this.currentScope)
/*  441 */         codeBug();
/*      */     }
/*  443 */     else this.currentScope.addChildScope(paramScope);
/*      */ 
/*  445 */     this.currentScope = paramScope;
/*      */   }
/*      */ 
/*      */   void popScope() {
/*  449 */     this.currentScope = this.currentScope.getParentScope();
/*      */   }
/*      */ 
/*      */   private void enterLoop(Loop paramLoop) {
/*  453 */     if (this.loopSet == null)
/*  454 */       this.loopSet = new ArrayList();
/*  455 */     this.loopSet.add(paramLoop);
/*  456 */     if (this.loopAndSwitchSet == null)
/*  457 */       this.loopAndSwitchSet = new ArrayList();
/*  458 */     this.loopAndSwitchSet.add(paramLoop);
/*  459 */     pushScope(paramLoop);
/*  460 */     if (this.currentLabel != null) {
/*  461 */       this.currentLabel.setStatement(paramLoop);
/*  462 */       this.currentLabel.getFirstLabel().setLoop(paramLoop);
/*      */ 
/*  467 */       paramLoop.setRelative(-this.currentLabel.getPosition());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void exitLoop() {
/*  472 */     Loop localLoop = (Loop)this.loopSet.remove(this.loopSet.size() - 1);
/*  473 */     this.loopAndSwitchSet.remove(this.loopAndSwitchSet.size() - 1);
/*  474 */     if (localLoop.getParent() != null) {
/*  475 */       localLoop.setRelative(localLoop.getParent().getPosition());
/*      */     }
/*  477 */     popScope();
/*      */   }
/*      */ 
/*      */   private void enterSwitch(SwitchStatement paramSwitchStatement) {
/*  481 */     if (this.loopAndSwitchSet == null)
/*  482 */       this.loopAndSwitchSet = new ArrayList();
/*  483 */     this.loopAndSwitchSet.add(paramSwitchStatement);
/*      */   }
/*      */ 
/*      */   private void exitSwitch() {
/*  487 */     this.loopAndSwitchSet.remove(this.loopAndSwitchSet.size() - 1);
/*      */   }
/*      */ 
/*      */   public AstRoot parse(String paramString1, String paramString2, int paramInt)
/*      */   {
/*  500 */     if (this.parseFinished) throw new IllegalStateException("parser reused");
/*  501 */     this.sourceURI = paramString2;
/*  502 */     if (this.compilerEnv.isIdeMode()) {
/*  503 */       this.sourceChars = paramString1.toCharArray();
/*      */     }
/*  505 */     this.ts = new TokenStream(this, null, paramString1, paramInt);
/*      */     try {
/*  507 */       return parse();
/*      */     }
/*      */     catch (IOException localIOException) {
/*  510 */       throw new IllegalStateException();
/*      */     } finally {
/*  512 */       this.parseFinished = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public AstRoot parse(Reader paramReader, String paramString, int paramInt)
/*      */     throws IOException
/*      */   {
/*  524 */     if (this.parseFinished) throw new IllegalStateException("parser reused");
/*  525 */     if (this.compilerEnv.isIdeMode())
/*  526 */       return parse(readFully(paramReader), paramString, paramInt);
/*      */     try
/*      */     {
/*  529 */       this.sourceURI = paramString;
/*  530 */       this.ts = new TokenStream(this, paramReader, null, paramInt);
/*  531 */       return parse();
/*      */     } finally {
/*  533 */       this.parseFinished = true; }  } 
/*  539 */   private AstRoot parse() throws IOException { int i = 0;
/*  540 */     AstRoot localAstRoot = new AstRoot(i);
/*  541 */     this.currentScope = (this.currentScriptOrFn = localAstRoot);
/*      */ 
/*  543 */     int j = this.ts.lineno;
/*  544 */     int k = i;
/*      */ 
/*  546 */     int m = 1;
/*  547 */     boolean bool = this.inUseStrictDirective;
/*      */ 
/*  549 */     this.inUseStrictDirective = false;
/*      */     Object localObject1;
/*      */     Object localObject2;
/*      */     try { while (true) { int n = peekToken();
/*  554 */         if (n <= 0)
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*  559 */         if (n == 109) {
/*  560 */           consumeToken();
/*      */           try {
/*  562 */             localObject1 = function(this.calledByCompileFunction ? 2 : 1);
/*      */           }
/*      */           catch (ParserException localParserException)
/*      */           {
/*  566 */             break;
/*      */           }
/*      */         } else {
/*  569 */           localObject1 = statement();
/*  570 */           if (m != 0) {
/*  571 */             localObject2 = getDirective((AstNode)localObject1);
/*  572 */             if (localObject2 == null) {
/*  573 */               m = 0;
/*  574 */             } else if (((String)localObject2).equals("use strict")) {
/*  575 */               this.inUseStrictDirective = true;
/*  576 */               localAstRoot.setInStrictMode(true);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  581 */         k = getNodeEnd((AstNode)localObject1);
/*  582 */         localAstRoot.addChildToBack((Node)localObject1);
/*  583 */         ((AstNode)localObject1).setParent(localAstRoot); }
/*      */     } catch (StackOverflowError localStackOverflowError)
/*      */     {
/*  586 */       localObject1 = lookupMessage("msg.too.deep.parser.recursion");
/*  587 */       if (!this.compilerEnv.isIdeMode())
/*  588 */         throw Context.reportRuntimeError((String)localObject1, this.sourceURI, this.ts.lineno, null, 0);
/*      */     }
/*      */     finally {
/*  591 */       this.inUseStrictDirective = bool;
/*      */     }
/*      */ 
/*  594 */     if (this.syntaxErrorCount != 0) {
/*  595 */       String str = String.valueOf(this.syntaxErrorCount);
/*  596 */       str = lookupMessage("msg.got.syntax.errors", str);
/*  597 */       if (!this.compilerEnv.isIdeMode()) {
/*  598 */         throw this.errorReporter.runtimeError(str, this.sourceURI, j, null, 0);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  603 */     if (this.scannedComments != null)
/*      */     {
/*  606 */       int i1 = this.scannedComments.size() - 1;
/*  607 */       k = Math.max(k, getNodeEnd((AstNode)this.scannedComments.get(i1)));
/*  608 */       for (localObject1 = this.scannedComments.iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (Comment)((Iterator)localObject1).next();
/*  609 */         localAstRoot.addComment((Comment)localObject2);
/*      */       }
/*      */     }
/*      */ 
/*  613 */     localAstRoot.setLength(k - i);
/*  614 */     localAstRoot.setSourceName(this.sourceURI);
/*  615 */     localAstRoot.setBaseLineno(j);
/*  616 */     localAstRoot.setEndLineno(this.ts.lineno);
/*  617 */     return localAstRoot;
/*      */   }
/*      */ 
/*      */   private AstNode parseFunctionBody()
/*      */     throws IOException
/*      */   {
/*  623 */     if (!matchToken(85)) {
/*  624 */       if (this.compilerEnv.getLanguageVersion() < 180) {
/*  625 */         reportError("msg.no.brace.body");
/*      */       }
/*  627 */       return parseFunctionBodyExpr();
/*      */     }
/*  629 */     this.nestingOfFunction += 1;
/*  630 */     int i = this.ts.tokenBeg;
/*  631 */     Block localBlock = new Block(i);
/*      */ 
/*  633 */     int j = 1;
/*  634 */     boolean bool = this.inUseStrictDirective;
/*      */ 
/*  637 */     localBlock.setLineno(this.ts.lineno);
/*      */     try
/*      */     {
/*      */       while (true) {
/*  641 */         int m = peekToken();
/*      */         Object localObject1;
/*  642 */         switch (m) {
/*      */         case -1:
/*      */         case 0:
/*      */         case 86:
/*  646 */           break;
/*      */         case 109:
/*  649 */           consumeToken();
/*  650 */           localObject1 = function(1);
/*  651 */           break;
/*      */         default:
/*  653 */           localObject1 = statement();
/*  654 */           if (j != 0) {
/*  655 */             String str = getDirective((AstNode)localObject1);
/*  656 */             if (str == null)
/*  657 */               j = 0;
/*  658 */             else if (str.equals("use strict")) {
/*  659 */               this.inUseStrictDirective = true;
/*      */             }
/*      */           }
/*      */           break;
/*      */         }
/*  664 */         localBlock.addStatement((AstNode)localObject1);
/*      */       }
/*      */     } catch (ParserException localParserException) {
/*      */     }
/*      */     finally {
/*  669 */       this.nestingOfFunction -= 1;
/*  670 */       this.inUseStrictDirective = bool;
/*      */     }
/*      */ 
/*  673 */     int k = this.ts.tokenEnd;
/*  674 */     getAndResetJsDoc();
/*  675 */     if (mustMatchToken(86, "msg.no.brace.after.body"))
/*  676 */       k = this.ts.tokenEnd;
/*  677 */     localBlock.setLength(k - i);
/*  678 */     return localBlock;
/*      */   }
/*      */ 
/*      */   private String getDirective(AstNode paramAstNode) {
/*  682 */     if ((paramAstNode instanceof ExpressionStatement)) {
/*  683 */       AstNode localAstNode = ((ExpressionStatement)paramAstNode).getExpression();
/*  684 */       if ((localAstNode instanceof StringLiteral)) {
/*  685 */         return ((StringLiteral)localAstNode).getValue();
/*      */       }
/*      */     }
/*  688 */     return null;
/*      */   }
/*      */ 
/*      */   private void parseFunctionParams(FunctionNode paramFunctionNode)
/*      */     throws IOException
/*      */   {
/*  694 */     if (matchToken(88)) {
/*  695 */       paramFunctionNode.setRp(this.ts.tokenBeg - paramFunctionNode.getPosition());
/*  696 */       return; } 
/*      */ HashMap localHashMap = null;
/*  701 */     HashSet localHashSet = new HashSet();
/*      */     Object localObject1;
/*      */     Object localObject2;
/*      */     do { int i = peekToken();
/*  704 */       if ((i == 83) || (i == 85)) {
/*  705 */         localObject1 = destructuringPrimaryExpr();
/*  706 */         markDestructuring((AstNode)localObject1);
/*  707 */         paramFunctionNode.addParam((AstNode)localObject1);
/*      */ 
/*  711 */         if (localHashMap == null) {
/*  712 */           localHashMap = new HashMap();
/*      */         }
/*  714 */         localObject2 = this.currentScriptOrFn.getNextTempName();
/*  715 */         defineSymbol(87, (String)localObject2, false);
/*  716 */         localHashMap.put(localObject2, localObject1);
/*      */       }
/*  718 */       else if (mustMatchToken(39, "msg.no.parm")) {
/*  719 */         paramFunctionNode.addParam(createNameNode());
/*  720 */         localObject1 = this.ts.getString();
/*  721 */         defineSymbol(87, (String)localObject1);
/*  722 */         if (this.inUseStrictDirective) {
/*  723 */           if (("eval".equals(localObject1)) || ("arguments".equals(localObject1)))
/*      */           {
/*  726 */             reportError("msg.bad.id.strict", (String)localObject1);
/*      */           }
/*  728 */           if (localHashSet.contains(localObject1))
/*  729 */             addError("msg.dup.param.strict", (String)localObject1);
/*  730 */           localHashSet.add(localObject1);
/*      */         }
/*      */       } else {
/*  733 */         paramFunctionNode.addParam(makeErrorNode());
/*      */       }
/*      */     }
/*  736 */     while (matchToken(89));
/*      */ 
/*  738 */     if (localHashMap != null) {
/*  739 */       Node localNode1 = new Node(89);
/*      */ 
/*  741 */       for (localObject1 = localHashMap.entrySet().iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (Map.Entry)((Iterator)localObject1).next();
/*  742 */         Node localNode2 = createDestructuringAssignment(122, (Node)((Map.Entry)localObject2).getValue(), createName((String)((Map.Entry)localObject2).getKey()));
/*      */ 
/*  744 */         localNode1.addChildToBack(localNode2);
/*      */       }
/*      */ 
/*  747 */       paramFunctionNode.putProp(23, localNode1);
/*      */     }
/*      */ 
/*  750 */     if (mustMatchToken(88, "msg.no.paren.after.parms"))
/*  751 */       paramFunctionNode.setRp(this.ts.tokenBeg - paramFunctionNode.getPosition());
/*      */   }
/*      */ 
/*      */   private AstNode parseFunctionBodyExpr()
/*      */     throws IOException
/*      */   {
/*  759 */     this.nestingOfFunction += 1;
/*  760 */     int i = this.ts.getLineno();
/*  761 */     ReturnStatement localReturnStatement = new ReturnStatement(i);
/*  762 */     localReturnStatement.putProp(25, Boolean.TRUE);
/*      */     try {
/*  764 */       localReturnStatement.setReturnValue(assignExpr());
/*      */     } finally {
/*  766 */       this.nestingOfFunction -= 1;
/*      */     }
/*  768 */     return localReturnStatement;
/*      */   }
/*      */ 
/*      */   private FunctionNode function(int paramInt)
/*      */     throws IOException
/*      */   {
/*  774 */     int i = paramInt;
/*  775 */     int j = this.ts.lineno;
/*  776 */     int k = this.ts.tokenBeg;
/*  777 */     Name localName = null;
/*  778 */     AstNode localAstNode = null;
/*      */ 
/*  780 */     if (matchToken(39)) {
/*  781 */       localName = createNameNode(true, 39);
/*      */       Object localObject1;
/*  782 */       if (this.inUseStrictDirective) {
/*  783 */         localObject1 = localName.getIdentifier();
/*  784 */         if (("eval".equals(localObject1)) || ("arguments".equals(localObject1))) {
/*  785 */           reportError("msg.bad.id.strict", (String)localObject1);
/*      */         }
/*      */       }
/*  788 */       if (!matchToken(87)) {
/*  789 */         if (this.compilerEnv.isAllowMemberExprAsFunctionName()) {
/*  790 */           localObject1 = localName;
/*  791 */           localName = null;
/*  792 */           localAstNode = memberExprTail(false, (AstNode)localObject1);
/*      */         }
/*  794 */         mustMatchToken(87, "msg.no.paren.parms");
/*      */       }
/*  796 */     } else if (!matchToken(87))
/*      */     {
/*  799 */       if (this.compilerEnv.isAllowMemberExprAsFunctionName())
/*      */       {
/*  803 */         localAstNode = memberExpr(false);
/*      */       }
/*  805 */       mustMatchToken(87, "msg.no.paren.parms");
/*      */     }
/*  807 */     int m = this.currentToken == 87 ? this.ts.tokenBeg : -1;
/*      */ 
/*  809 */     if (localAstNode != null) {
/*  810 */       i = 2;
/*      */     }
/*      */ 
/*  813 */     if ((i != 2) && (localName != null) && (localName.length() > 0))
/*      */     {
/*  816 */       defineSymbol(109, localName.getIdentifier());
/*      */     }
/*      */ 
/*  819 */     FunctionNode localFunctionNode = new FunctionNode(k, localName);
/*  820 */     localFunctionNode.setFunctionType(paramInt);
/*  821 */     if (m != -1) {
/*  822 */       localFunctionNode.setLp(m - k);
/*      */     }
/*  824 */     if ((insideFunction()) || (this.nestingOfWith > 0))
/*      */     {
/*  830 */       localFunctionNode.setIgnoreDynamicScope();
/*      */     }
/*      */ 
/*  833 */     localFunctionNode.setJsDoc(getAndResetJsDoc());
/*      */ 
/*  835 */     PerFunctionVariables localPerFunctionVariables = new PerFunctionVariables(localFunctionNode);
/*      */     try {
/*  837 */       parseFunctionParams(localFunctionNode);
/*  838 */       localFunctionNode.setBody(parseFunctionBody());
/*  839 */       localFunctionNode.setEncodedSourceBounds(k, this.ts.tokenEnd);
/*  840 */       localFunctionNode.setLength(this.ts.tokenEnd - k);
/*      */ 
/*  842 */       if ((this.compilerEnv.isStrictMode()) && (!localFunctionNode.getBody().hasConsistentReturnUsage()))
/*      */       {
/*  844 */         String str = (localName != null) && (localName.length() > 0) ? "msg.no.return.value" : "msg.anon.no.return.value";
/*      */ 
/*  847 */         addStrictWarning(str, localName == null ? "" : localName.getIdentifier());
/*      */       }
/*      */     } finally {
/*  850 */       localPerFunctionVariables.restore();
/*      */     }
/*      */ 
/*  853 */     if (localAstNode != null)
/*      */     {
/*  855 */       Kit.codeBug();
/*  856 */       localFunctionNode.setMemberExprNode(localAstNode);
/*      */     }
/*      */ 
/*  868 */     localFunctionNode.setSourceName(this.sourceURI);
/*  869 */     localFunctionNode.setBaseLineno(j);
/*  870 */     localFunctionNode.setEndLineno(this.ts.lineno);
/*      */ 
/*  876 */     if (this.compilerEnv.isIdeMode()) {
/*  877 */       localFunctionNode.setParentScope(this.currentScope);
/*      */     }
/*  879 */     return localFunctionNode;
/*      */   }
/*      */ 
/*      */   private AstNode statements(AstNode paramAstNode)
/*      */     throws IOException
/*      */   {
/*  891 */     if ((this.currentToken != 85) && (!this.compilerEnv.isIdeMode()))
/*  892 */       codeBug();
/*  893 */     int i = this.ts.tokenBeg;
/*  894 */     Block localBlock = paramAstNode != null ? paramAstNode : new Block(i);
/*  895 */     localBlock.setLineno(this.ts.lineno);
/*      */     int j;
/*  898 */     while (((j = peekToken()) > 0) && (j != 86)) {
/*  899 */       localBlock.addChild(statement());
/*      */     }
/*  901 */     localBlock.setLength(this.ts.tokenBeg - i);
/*  902 */     return localBlock;
/*      */   }
/*      */ 
/*      */   private AstNode statements() throws IOException {
/*  906 */     return statements(null);
/*      */   }
/*      */ 
/*      */   private ConditionData condition()
/*      */     throws IOException
/*      */   {
/*  919 */     ConditionData localConditionData = new ConditionData(null);
/*      */ 
/*  921 */     if (mustMatchToken(87, "msg.no.paren.cond")) {
/*  922 */       localConditionData.lp = this.ts.tokenBeg;
/*      */     }
/*  924 */     localConditionData.condition = expr();
/*      */ 
/*  926 */     if (mustMatchToken(88, "msg.no.paren.after.cond")) {
/*  927 */       localConditionData.rp = this.ts.tokenBeg;
/*      */     }
/*      */ 
/*  931 */     if ((localConditionData.condition instanceof Assignment)) {
/*  932 */       addStrictWarning("msg.equal.as.assign", "", localConditionData.condition.getPosition(), localConditionData.condition.getLength());
/*      */     }
/*      */ 
/*  936 */     return localConditionData;
/*      */   }
/*      */ 
/*      */   private AstNode statement()
/*      */     throws IOException
/*      */   {
/*  942 */     int i = this.ts.tokenBeg;
/*      */     try {
/*  944 */       AstNode localAstNode = statementHelper();
/*  945 */       if (localAstNode != null) {
/*  946 */         if ((this.compilerEnv.isStrictMode()) && (!localAstNode.hasSideEffects())) {
/*  947 */           int k = localAstNode.getPosition();
/*  948 */           k = Math.max(k, lineBeginningFor(k));
/*  949 */           addStrictWarning((localAstNode instanceof EmptyExpression) ? "msg.extra.trailing.semi" : "msg.no.side.effects", "", k, nodeEnd(localAstNode) - k);
/*      */         }
/*      */ 
/*  954 */         return localAstNode;
/*      */       }
/*      */     }
/*      */     catch (ParserException localParserException)
/*      */     {
/*      */     }
/*      */     while (true)
/*      */     {
/*  962 */       int j = peekTokenOrEOL();
/*  963 */       consumeToken();
/*  964 */       switch (j) {
/*      */       case -1:
/*      */       case 0:
/*      */       case 1:
/*      */       case 82:
/*  969 */         break label142;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  975 */     label142: return new EmptyExpression(i, this.ts.tokenBeg - i);
/*      */   }
/*      */ 
/*      */   private AstNode statementHelper()
/*      */     throws IOException
/*      */   {
/*  982 */     if ((this.currentLabel != null) && (this.currentLabel.getStatement() != null)) {
/*  983 */       this.currentLabel = null;
/*      */     }
/*  985 */     Object localObject = null;
/*  986 */     int i = peekToken(); int j = this.ts.tokenBeg;
/*      */     int k;
/*  988 */     switch (i) {
/*      */     case 112:
/*  990 */       return ifStatement();
/*      */     case 114:
/*  993 */       return switchStatement();
/*      */     case 117:
/*  996 */       return whileLoop();
/*      */     case 118:
/*  999 */       return doLoop();
/*      */     case 119:
/* 1002 */       return forLoop();
/*      */     case 81:
/* 1005 */       return tryStatement();
/*      */     case 50:
/* 1008 */       localObject = throwStatement();
/* 1009 */       break;
/*      */     case 120:
/* 1012 */       localObject = breakStatement();
/* 1013 */       break;
/*      */     case 121:
/* 1016 */       localObject = continueStatement();
/* 1017 */       break;
/*      */     case 123:
/* 1020 */       if (this.inUseStrictDirective) {
/* 1021 */         reportError("msg.no.with.strict");
/*      */       }
/* 1023 */       return withStatement();
/*      */     case 122:
/*      */     case 154:
/* 1027 */       consumeToken();
/* 1028 */       k = this.ts.lineno;
/* 1029 */       localObject = variables(this.currentToken, this.ts.tokenBeg);
/* 1030 */       ((AstNode)localObject).setLineno(k);
/* 1031 */       break;
/*      */     case 153:
/* 1034 */       localObject = letStatement();
/* 1035 */       if ((!(localObject instanceof VariableDeclaration)) || (peekToken() != 82))
/*      */       {
/* 1038 */         return localObject;
/*      */       }break;
/*      */     case 4:
/*      */     case 72:
/* 1042 */       localObject = returnOrYield(i, false);
/* 1043 */       break;
/*      */     case 160:
/* 1046 */       consumeToken();
/* 1047 */       localObject = new KeywordLiteral(this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg, i);
/*      */ 
/* 1049 */       ((AstNode)localObject).setLineno(this.ts.lineno);
/* 1050 */       break;
/*      */     case 85:
/* 1053 */       return block();
/*      */     case -1:
/* 1056 */       consumeToken();
/* 1057 */       return makeErrorNode();
/*      */     case 82:
/* 1060 */       consumeToken();
/* 1061 */       j = this.ts.tokenBeg;
/* 1062 */       localObject = new EmptyExpression(j, this.ts.tokenEnd - j);
/* 1063 */       ((AstNode)localObject).setLineno(this.ts.lineno);
/* 1064 */       return localObject;
/*      */     case 109:
/* 1067 */       consumeToken();
/* 1068 */       return function(3);
/*      */     case 116:
/* 1071 */       localObject = defaultXmlNamespace();
/* 1072 */       break;
/*      */     case 39:
/* 1075 */       localObject = nameOrLabel();
/* 1076 */       if (!(localObject instanceof ExpressionStatement))
/*      */       {
/* 1078 */         return localObject;
/*      */       }break;
/*      */     default:
/* 1081 */       k = this.ts.lineno;
/* 1082 */       localObject = new ExpressionStatement(expr(), !insideFunction());
/* 1083 */       ((AstNode)localObject).setLineno(k);
/*      */     }
/*      */ 
/* 1087 */     autoInsertSemicolon((AstNode)localObject);
/* 1088 */     return localObject;
/*      */   }
/*      */ 
/*      */   private void autoInsertSemicolon(AstNode paramAstNode) throws IOException {
/* 1092 */     int i = peekFlaggedToken();
/* 1093 */     int j = paramAstNode.getPosition();
/* 1094 */     switch (i & 0xFFFF)
/*      */     {
/*      */     case 82:
/* 1097 */       consumeToken();
/*      */ 
/* 1099 */       paramAstNode.setLength(this.ts.tokenEnd - j);
/* 1100 */       break;
/*      */     case -1:
/*      */     case 0:
/*      */     case 86:
/* 1105 */       warnMissingSemi(j, nodeEnd(paramAstNode));
/* 1106 */       break;
/*      */     default:
/* 1108 */       if ((i & 0x10000) == 0)
/*      */       {
/* 1110 */         reportError("msg.no.semi.stmt");
/*      */       }
/* 1112 */       else warnMissingSemi(j, nodeEnd(paramAstNode));
/*      */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   private IfStatement ifStatement()
/*      */     throws IOException
/*      */   {
/* 1121 */     if (this.currentToken != 112) codeBug();
/* 1122 */     consumeToken();
/* 1123 */     int i = this.ts.tokenBeg; int j = this.ts.lineno; int k = -1;
/* 1124 */     ConditionData localConditionData = condition();
/* 1125 */     AstNode localAstNode1 = statement(); AstNode localAstNode2 = null;
/* 1126 */     if (matchToken(113)) {
/* 1127 */       k = this.ts.tokenBeg - i;
/* 1128 */       localAstNode2 = statement();
/*      */     }
/* 1130 */     int m = getNodeEnd(localAstNode2 != null ? localAstNode2 : localAstNode1);
/* 1131 */     IfStatement localIfStatement = new IfStatement(i, m - i);
/* 1132 */     localIfStatement.setCondition(localConditionData.condition);
/* 1133 */     localIfStatement.setParens(localConditionData.lp - i, localConditionData.rp - i);
/* 1134 */     localIfStatement.setThenPart(localAstNode1);
/* 1135 */     localIfStatement.setElsePart(localAstNode2);
/* 1136 */     localIfStatement.setElsePosition(k);
/* 1137 */     localIfStatement.setLineno(j);
/* 1138 */     return localIfStatement;
/*      */   }
/*      */ 
/*      */   private SwitchStatement switchStatement()
/*      */     throws IOException
/*      */   {
/* 1144 */     if (this.currentToken != 114) codeBug();
/* 1145 */     consumeToken();
/* 1146 */     int i = this.ts.tokenBeg;
/*      */ 
/* 1148 */     SwitchStatement localSwitchStatement = new SwitchStatement(i);
/* 1149 */     if (mustMatchToken(87, "msg.no.paren.switch"))
/* 1150 */       localSwitchStatement.setLp(this.ts.tokenBeg - i);
/* 1151 */     localSwitchStatement.setLineno(this.ts.lineno);
/*      */ 
/* 1153 */     AstNode localAstNode1 = expr();
/* 1154 */     localSwitchStatement.setExpression(localAstNode1);
/* 1155 */     enterSwitch(localSwitchStatement);
/*      */     try
/*      */     {
/* 1158 */       if (mustMatchToken(88, "msg.no.paren.after.switch")) {
/* 1159 */         localSwitchStatement.setRp(this.ts.tokenBeg - i);
/*      */       }
/* 1161 */       mustMatchToken(85, "msg.no.brace.switch");
/*      */ 
/* 1163 */       int j = 0;
/*      */       while (true)
/*      */       {
/* 1166 */         int k = nextToken();
/* 1167 */         int m = this.ts.tokenBeg;
/* 1168 */         int n = this.ts.lineno;
/* 1169 */         AstNode localAstNode2 = null;
/* 1170 */         switch (k) {
/*      */         case 86:
/* 1172 */           localSwitchStatement.setLength(this.ts.tokenEnd - i);
/* 1173 */           break;
/*      */         case 115:
/* 1176 */           localAstNode2 = expr();
/* 1177 */           mustMatchToken(103, "msg.no.colon.case");
/* 1178 */           break;
/*      */         case 116:
/* 1181 */           if (j != 0) {
/* 1182 */             reportError("msg.double.switch.default");
/*      */           }
/* 1184 */           j = 1;
/* 1185 */           localAstNode2 = null;
/* 1186 */           mustMatchToken(103, "msg.no.colon.case");
/* 1187 */           break;
/*      */         default:
/* 1190 */           reportError("msg.bad.switch");
/* 1191 */           break;
/*      */         }
/*      */ 
/* 1194 */         SwitchCase localSwitchCase = new SwitchCase(m);
/* 1195 */         localSwitchCase.setExpression(localAstNode2);
/* 1196 */         localSwitchCase.setLength(this.ts.tokenEnd - i);
/* 1197 */         localSwitchCase.setLineno(n);
/*      */ 
/* 1202 */         while (((k = peekToken()) != 86) && (k != 115) && (k != 116) && (k != 0))
/*      */         {
/* 1204 */           localSwitchCase.addStatement(statement());
/*      */         }
/* 1206 */         localSwitchStatement.addCase(localSwitchCase);
/*      */       }
/*      */     } finally {
/* 1209 */       exitSwitch();
/*      */     }
/* 1211 */     return localSwitchStatement;
/*      */   }
/*      */ 
/*      */   private WhileLoop whileLoop()
/*      */     throws IOException
/*      */   {
/* 1217 */     if (this.currentToken != 117) codeBug();
/* 1218 */     consumeToken();
/* 1219 */     int i = this.ts.tokenBeg;
/* 1220 */     WhileLoop localWhileLoop = new WhileLoop(i);
/* 1221 */     localWhileLoop.setLineno(this.ts.lineno);
/* 1222 */     enterLoop(localWhileLoop);
/*      */     try {
/* 1224 */       ConditionData localConditionData = condition();
/* 1225 */       localWhileLoop.setCondition(localConditionData.condition);
/* 1226 */       localWhileLoop.setParens(localConditionData.lp - i, localConditionData.rp - i);
/* 1227 */       AstNode localAstNode = statement();
/* 1228 */       localWhileLoop.setLength(getNodeEnd(localAstNode) - i);
/* 1229 */       localWhileLoop.setBody(localAstNode);
/*      */     } finally {
/* 1231 */       exitLoop();
/*      */     }
/* 1233 */     return localWhileLoop;
/*      */   }
/*      */ 
/*      */   private DoLoop doLoop()
/*      */     throws IOException
/*      */   {
/* 1239 */     if (this.currentToken != 118) codeBug(); consumeToken();
/* 1241 */     int i = this.ts.tokenBeg;
/* 1242 */     DoLoop localDoLoop = new DoLoop(i);
/* 1243 */     localDoLoop.setLineno(this.ts.lineno);
/* 1244 */     enterLoop(localDoLoop);
/*      */     int j;
/*      */     try { AstNode localAstNode = statement();
/* 1247 */       mustMatchToken(117, "msg.no.while.do");
/* 1248 */       localDoLoop.setWhilePosition(this.ts.tokenBeg - i);
/* 1249 */       ConditionData localConditionData = condition();
/* 1250 */       localDoLoop.setCondition(localConditionData.condition);
/* 1251 */       localDoLoop.setParens(localConditionData.lp - i, localConditionData.rp - i);
/* 1252 */       j = getNodeEnd(localAstNode);
/* 1253 */       localDoLoop.setBody(localAstNode);
/*      */     } finally {
/* 1255 */       exitLoop();
/*      */     }
/*      */ 
/* 1260 */     if (matchToken(82)) {
/* 1261 */       j = this.ts.tokenEnd;
/*      */     }
/* 1263 */     localDoLoop.setLength(j - i);
/* 1264 */     return localDoLoop;
/*      */   }
/*      */ 
/*      */   private Loop forLoop()
/*      */     throws IOException
/*      */   {
/* 1270 */     if (this.currentToken != 119) codeBug();
/* 1271 */     consumeToken();
/* 1272 */     int i = this.ts.tokenBeg; int j = this.ts.lineno;
/* 1273 */     boolean bool = false; int k = 0;
/* 1274 */     int m = -1; int n = -1; int i1 = -1; int i2 = -1;
/* 1275 */     AstNode localAstNode = null;
/* 1276 */     Object localObject1 = null;
/* 1277 */     Object localObject2 = null;
/* 1278 */     Object localObject3 = null;
/*      */ 
/* 1280 */     Scope localScope = new Scope();
/* 1281 */     pushScope(localScope);
/*      */     try
/*      */     {
/* 1284 */       if (matchToken(39)) {
/* 1285 */         if ("each".equals(this.ts.getString())) {
/* 1286 */           bool = true;
/* 1287 */           m = this.ts.tokenBeg - i;
/*      */         } else {
/* 1289 */           reportError("msg.no.paren.for");
/*      */         }
/*      */       }
/*      */ 
/* 1293 */       if (mustMatchToken(87, "msg.no.paren.for"))
/* 1294 */         i1 = this.ts.tokenBeg - i;
/* 1295 */       int i3 = peekToken();
/*      */ 
/* 1297 */       localAstNode = forLoopInit(i3);
/*      */ 
/* 1299 */       if (matchToken(52)) {
/* 1300 */         k = 1;
/* 1301 */         n = this.ts.tokenBeg - i;
/* 1302 */         localObject1 = expr();
/*      */       } else {
/* 1304 */         mustMatchToken(82, "msg.no.semi.for");
/* 1305 */         if (peekToken() == 82)
/*      */         {
/* 1307 */           localObject1 = new EmptyExpression(this.ts.tokenBeg, 1);
/* 1308 */           ((AstNode)localObject1).setLineno(this.ts.lineno);
/*      */         } else {
/* 1310 */           localObject1 = expr();
/*      */         }
/*      */ 
/* 1313 */         mustMatchToken(82, "msg.no.semi.for.cond");
/* 1314 */         int i4 = this.ts.tokenEnd;
/* 1315 */         if (peekToken() == 88) {
/* 1316 */           localObject2 = new EmptyExpression(i4, 1);
/* 1317 */           ((AstNode)localObject2).setLineno(this.ts.lineno);
/*      */         } else {
/* 1319 */           localObject2 = expr();
/*      */         }
/*      */       }
/*      */ 
/* 1323 */       if (mustMatchToken(88, "msg.no.paren.for.ctrl"))
/* 1324 */         i2 = this.ts.tokenBeg - i;
/*      */       Object localObject4;
/* 1326 */       if (k != 0) {
/* 1327 */         localObject4 = new ForInLoop(i);
/* 1328 */         if ((localAstNode instanceof VariableDeclaration))
/*      */         {
/* 1330 */           if (((VariableDeclaration)localAstNode).getVariables().size() > 1) {
/* 1331 */             reportError("msg.mult.index");
/*      */           }
/*      */         }
/* 1334 */         ((ForInLoop)localObject4).setIterator(localAstNode);
/* 1335 */         ((ForInLoop)localObject4).setIteratedObject((AstNode)localObject1);
/* 1336 */         ((ForInLoop)localObject4).setInPosition(n);
/* 1337 */         ((ForInLoop)localObject4).setIsForEach(bool);
/* 1338 */         ((ForInLoop)localObject4).setEachPosition(m);
/* 1339 */         localObject3 = localObject4;
/*      */       } else {
/* 1341 */         localObject4 = new ForLoop(i);
/* 1342 */         ((ForLoop)localObject4).setInitializer(localAstNode);
/* 1343 */         ((ForLoop)localObject4).setCondition((AstNode)localObject1);
/* 1344 */         ((ForLoop)localObject4).setIncrement((AstNode)localObject2);
/* 1345 */         localObject3 = localObject4;
/*      */       }
/*      */ 
/* 1349 */       this.currentScope.replaceWith(localObject3);
/* 1350 */       popScope();
/*      */ 
/* 1355 */       enterLoop(localObject3);
/*      */       try {
/* 1357 */         localObject4 = statement();
/* 1358 */         localObject3.setLength(getNodeEnd((AstNode)localObject4) - i);
/* 1359 */         localObject3.setBody((AstNode)localObject4);
/*      */       } finally {
/* 1361 */         exitLoop();
/*      */       }
/*      */ 
/* 1365 */       if (this.currentScope == localScope)
/* 1366 */         popScope();
/*      */     }
/*      */     finally
/*      */     {
/* 1365 */       if (this.currentScope == localScope) {
/* 1366 */         popScope();
/*      */       }
/*      */     }
/* 1369 */     localObject3.setParens(i1, i2);
/* 1370 */     localObject3.setLineno(j);
/* 1371 */     return localObject3;
/*      */   }
/*      */ 
/*      */   private AstNode forLoopInit(int paramInt) throws IOException {
/*      */     try {
/* 1376 */       this.inForInit = true;
/* 1377 */       Object localObject1 = null;
/* 1378 */       if (paramInt == 82) {
/* 1379 */         localObject1 = new EmptyExpression(this.ts.tokenBeg, 1);
/* 1380 */         ((AstNode)localObject1).setLineno(this.ts.lineno);
/* 1381 */       } else if ((paramInt == 122) || (paramInt == 153)) {
/* 1382 */         consumeToken();
/* 1383 */         localObject1 = variables(paramInt, this.ts.tokenBeg);
/*      */       } else {
/* 1385 */         localObject1 = expr();
/* 1386 */         markDestructuring((AstNode)localObject1);
/*      */       }
/* 1388 */       return localObject1;
/*      */     } finally {
/* 1390 */       this.inForInit = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private TryStatement tryStatement()
/*      */     throws IOException
/*      */   {
/* 1397 */     if (this.currentToken != 81) codeBug();
/* 1398 */     consumeToken();
/*      */ 
/* 1401 */     String str1 = getAndResetJsDoc();
/*      */ 
/* 1403 */     int i = this.ts.tokenBeg; int j = this.ts.lineno; int k = -1;
/* 1404 */     if (peekToken() != 85) {
/* 1405 */       reportError("msg.no.brace.try");
/*      */     }
/* 1407 */     AstNode localAstNode1 = statement();
/* 1408 */     int m = getNodeEnd(localAstNode1);
/*      */ 
/* 1410 */     ArrayList localArrayList = null;
/*      */ 
/* 1412 */     int n = 0;
/* 1413 */     int i1 = peekToken();
/* 1414 */     if (i1 == 124)
/* 1415 */       while (matchToken(124)) {
/* 1416 */         int i2 = this.ts.lineno;
/* 1417 */         if (n != 0) {
/* 1418 */           reportError("msg.catch.unreachable");
/*      */         }
/* 1420 */         int i3 = this.ts.tokenBeg; int i4 = -1; int i5 = -1; int i6 = -1;
/* 1421 */         if (mustMatchToken(87, "msg.no.paren.catch")) {
/* 1422 */           i4 = this.ts.tokenBeg;
/*      */         }
/* 1424 */         mustMatchToken(39, "msg.bad.catchcond");
/* 1425 */         Name localName = createNameNode();
/* 1426 */         String str2 = localName.getIdentifier();
/* 1427 */         if ((this.inUseStrictDirective) && (
/* 1428 */           ("eval".equals(str2)) || ("arguments".equals(str2))))
/*      */         {
/* 1431 */           reportError("msg.bad.id.strict", str2);
/*      */         }
/*      */ 
/* 1435 */         AstNode localAstNode3 = null;
/* 1436 */         if (matchToken(112)) {
/* 1437 */           i6 = this.ts.tokenBeg;
/* 1438 */           localAstNode3 = expr();
/*      */         } else {
/* 1440 */           n = 1;
/*      */         }
/*      */ 
/* 1443 */         if (mustMatchToken(88, "msg.bad.catchcond"))
/* 1444 */           i5 = this.ts.tokenBeg;
/* 1445 */         mustMatchToken(85, "msg.no.brace.catchblock");
/*      */ 
/* 1447 */         Block localBlock = (Block)statements();
/* 1448 */         m = getNodeEnd(localBlock);
/* 1449 */         CatchClause localCatchClause = new CatchClause(i3);
/* 1450 */         localCatchClause.setVarName(localName);
/* 1451 */         localCatchClause.setCatchCondition(localAstNode3);
/* 1452 */         localCatchClause.setBody(localBlock);
/* 1453 */         if (i6 != -1) {
/* 1454 */           localCatchClause.setIfPosition(i6 - i3);
/*      */         }
/* 1456 */         localCatchClause.setParens(i4, i5);
/* 1457 */         localCatchClause.setLineno(i2);
/*      */ 
/* 1459 */         if (mustMatchToken(86, "msg.no.brace.after.body"))
/* 1460 */           m = this.ts.tokenEnd;
/* 1461 */         localCatchClause.setLength(m - i3);
/* 1462 */         if (localArrayList == null)
/* 1463 */           localArrayList = new ArrayList();
/* 1464 */         localArrayList.add(localCatchClause);
/*      */       }
/* 1466 */     if (i1 != 125) {
/* 1467 */       mustMatchToken(125, "msg.try.no.catchfinally");
/*      */     }
/*      */ 
/* 1470 */     AstNode localAstNode2 = null;
/* 1471 */     if (matchToken(125)) {
/* 1472 */       k = this.ts.tokenBeg;
/* 1473 */       localAstNode2 = statement();
/* 1474 */       m = getNodeEnd(localAstNode2);
/*      */     }
/*      */ 
/* 1477 */     TryStatement localTryStatement = new TryStatement(i, m - i);
/* 1478 */     localTryStatement.setTryBlock(localAstNode1);
/* 1479 */     localTryStatement.setCatchClauses(localArrayList);
/* 1480 */     localTryStatement.setFinallyBlock(localAstNode2);
/* 1481 */     if (k != -1) {
/* 1482 */       localTryStatement.setFinallyPosition(k - i);
/*      */     }
/* 1484 */     localTryStatement.setLineno(j);
/*      */ 
/* 1486 */     if (str1 != null) {
/* 1487 */       localTryStatement.setJsDoc(str1);
/*      */     }
/*      */ 
/* 1490 */     return localTryStatement;
/*      */   }
/*      */ 
/*      */   private ThrowStatement throwStatement()
/*      */     throws IOException
/*      */   {
/* 1496 */     if (this.currentToken != 50) codeBug();
/* 1497 */     consumeToken();
/* 1498 */     int i = this.ts.tokenBeg; int j = this.ts.lineno;
/* 1499 */     if (peekTokenOrEOL() == 1)
/*      */     {
/* 1502 */       reportError("msg.bad.throw.eol");
/*      */     }
/* 1504 */     AstNode localAstNode = expr();
/* 1505 */     ThrowStatement localThrowStatement = new ThrowStatement(i, getNodeEnd(localAstNode), localAstNode);
/* 1506 */     localThrowStatement.setLineno(j);
/* 1507 */     return localThrowStatement;
/*      */   }
/*      */ 
/*      */   private LabeledStatement matchJumpLabelName()
/*      */     throws IOException
/*      */   {
/* 1519 */     LabeledStatement localLabeledStatement = null;
/*      */ 
/* 1521 */     if (peekTokenOrEOL() == 39) {
/* 1522 */       consumeToken();
/* 1523 */       if (this.labelSet != null) {
/* 1524 */         localLabeledStatement = (LabeledStatement)this.labelSet.get(this.ts.getString());
/*      */       }
/* 1526 */       if (localLabeledStatement == null) {
/* 1527 */         reportError("msg.undef.label");
/*      */       }
/*      */     }
/*      */ 
/* 1531 */     return localLabeledStatement;
/*      */   }
/*      */ 
/*      */   private BreakStatement breakStatement()
/*      */     throws IOException
/*      */   {
/* 1537 */     if (this.currentToken != 120) codeBug();
/* 1538 */     consumeToken();
/* 1539 */     int i = this.ts.lineno; int j = this.ts.tokenBeg; int k = this.ts.tokenEnd;
/* 1540 */     Name localName = null;
/* 1541 */     if (peekTokenOrEOL() == 39) {
/* 1542 */       localName = createNameNode();
/* 1543 */       k = getNodeEnd(localName);
/*      */     }
/*      */ 
/* 1547 */     LabeledStatement localLabeledStatement = matchJumpLabelName();
/*      */ 
/* 1549 */     Object localObject = localLabeledStatement == null ? null : localLabeledStatement.getFirstLabel();
/*      */ 
/* 1551 */     if ((localObject == null) && (localName == null)) {
/* 1552 */       if ((this.loopAndSwitchSet == null) || (this.loopAndSwitchSet.size() == 0)) {
/* 1553 */         if (localName == null)
/* 1554 */           reportError("msg.bad.break", j, k - j);
/*      */       }
/*      */       else {
/* 1557 */         localObject = (Jump)this.loopAndSwitchSet.get(this.loopAndSwitchSet.size() - 1);
/*      */       }
/*      */     }
/*      */ 
/* 1561 */     BreakStatement localBreakStatement = new BreakStatement(j, k - j);
/* 1562 */     localBreakStatement.setBreakLabel(localName);
/*      */ 
/* 1564 */     if (localObject != null)
/* 1565 */       localBreakStatement.setBreakTarget((Jump)localObject);
/* 1566 */     localBreakStatement.setLineno(i);
/* 1567 */     return localBreakStatement;
/*      */   }
/*      */ 
/*      */   private ContinueStatement continueStatement()
/*      */     throws IOException
/*      */   {
/* 1573 */     if (this.currentToken != 121) codeBug();
/* 1574 */     consumeToken();
/* 1575 */     int i = this.ts.lineno; int j = this.ts.tokenBeg; int k = this.ts.tokenEnd;
/* 1576 */     Name localName = null;
/* 1577 */     if (peekTokenOrEOL() == 39) {
/* 1578 */       localName = createNameNode();
/* 1579 */       k = getNodeEnd(localName);
/*      */     }
/*      */ 
/* 1583 */     LabeledStatement localLabeledStatement = matchJumpLabelName();
/* 1584 */     Loop localLoop = null;
/* 1585 */     if ((localLabeledStatement == null) && (localName == null)) {
/* 1586 */       if ((this.loopSet == null) || (this.loopSet.size() == 0))
/* 1587 */         reportError("msg.continue.outside");
/*      */       else
/* 1589 */         localLoop = (Loop)this.loopSet.get(this.loopSet.size() - 1);
/*      */     }
/*      */     else {
/* 1592 */       if ((localLabeledStatement == null) || (!(localLabeledStatement.getStatement() instanceof Loop))) {
/* 1593 */         reportError("msg.continue.nonloop", j, k - j);
/*      */       }
/* 1595 */       localLoop = localLabeledStatement == null ? null : (Loop)localLabeledStatement.getStatement();
/*      */     }
/*      */ 
/* 1598 */     ContinueStatement localContinueStatement = new ContinueStatement(j, k - j);
/* 1599 */     if (localLoop != null)
/* 1600 */       localContinueStatement.setTarget(localLoop);
/* 1601 */     localContinueStatement.setLabel(localName);
/* 1602 */     localContinueStatement.setLineno(i);
/* 1603 */     return localContinueStatement;
/*      */   }
/*      */ 
/*      */   private WithStatement withStatement()
/*      */     throws IOException
/*      */   {
/* 1609 */     if (this.currentToken != 123) codeBug();
/* 1610 */     consumeToken();
/* 1611 */     int i = this.ts.lineno; int j = this.ts.tokenBeg; int k = -1; int m = -1;
/* 1612 */     if (mustMatchToken(87, "msg.no.paren.with")) {
/* 1613 */       k = this.ts.tokenBeg;
/*      */     }
/* 1615 */     AstNode localAstNode1 = expr();
/*      */ 
/* 1617 */     if (mustMatchToken(88, "msg.no.paren.after.with")) {
/* 1618 */       m = this.ts.tokenBeg;
/* 1620 */     }this.nestingOfWith += 1;
/*      */     AstNode localAstNode2;
/*      */     try {
/* 1623 */       localAstNode2 = statement();
/*      */     } finally {
/* 1625 */       this.nestingOfWith -= 1;
/*      */     }
/*      */ 
/* 1628 */     WithStatement localWithStatement = new WithStatement(j, getNodeEnd(localAstNode2) - j);
/* 1629 */     localWithStatement.setJsDoc(getAndResetJsDoc());
/* 1630 */     localWithStatement.setExpression(localAstNode1);
/* 1631 */     localWithStatement.setStatement(localAstNode2);
/* 1632 */     localWithStatement.setParens(k, m);
/* 1633 */     localWithStatement.setLineno(i);
/* 1634 */     return localWithStatement;
/*      */   }
/*      */ 
/*      */   private AstNode letStatement()
/*      */     throws IOException
/*      */   {
/* 1640 */     if (this.currentToken != 153) codeBug();
/* 1641 */     consumeToken();
/* 1642 */     int i = this.ts.lineno; int j = this.ts.tokenBeg;
/*      */     Object localObject;
/* 1644 */     if (peekToken() == 87)
/* 1645 */       localObject = let(true, j);
/*      */     else {
/* 1647 */       localObject = variables(153, j);
/*      */     }
/* 1649 */     ((AstNode)localObject).setLineno(i);
/* 1650 */     return localObject;
/*      */   }
/*      */ 
/*      */   private static final boolean nowAllSet(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1662 */     return ((paramInt1 & paramInt3) != paramInt3) && ((paramInt2 & paramInt3) == paramInt3);
/*      */   }
/*      */ 
/*      */   private AstNode returnOrYield(int paramInt, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1668 */     if (!insideFunction()) {
/* 1669 */       reportError(paramInt == 4 ? "msg.bad.return" : "msg.bad.yield");
/*      */     }
/*      */ 
/* 1672 */     consumeToken();
/* 1673 */     int i = this.ts.lineno; int j = this.ts.tokenBeg; int k = this.ts.tokenEnd;
/*      */ 
/* 1675 */     AstNode localAstNode = null;
/*      */ 
/* 1677 */     switch (peekTokenOrEOL()) { case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     case 72:
/*      */     case 82:
/*      */     case 84:
/*      */     case 86:
/*      */     case 88:
/* 1680 */       break;
/*      */     default:
/* 1682 */       localAstNode = expr();
/* 1683 */       k = getNodeEnd(localAstNode);
/*      */     }
/*      */ 
/* 1686 */     int m = this.endFlags;
/*      */     Object localObject;
/* 1689 */     if (paramInt == 4) {
/* 1690 */       this.endFlags |= (localAstNode == null ? 2 : 4);
/* 1691 */       localObject = new ReturnStatement(j, k - j, localAstNode);
/*      */ 
/* 1694 */       if (nowAllSet(m, this.endFlags, 6))
/*      */       {
/* 1696 */         addStrictWarning("msg.return.inconsistent", "", j, k - j);
/*      */       }
/*      */     } else { if (!insideFunction())
/* 1699 */         reportError("msg.bad.yield");
/* 1700 */       this.endFlags |= 8;
/* 1701 */       localObject = new Yield(j, k - j, localAstNode);
/* 1702 */       setRequiresActivation();
/* 1703 */       setIsGenerator();
/* 1704 */       if (!paramBoolean) {
/* 1705 */         localObject = new ExpressionStatement((AstNode)localObject);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1710 */     if ((insideFunction()) && (nowAllSet(m, this.endFlags, 12)))
/*      */     {
/* 1713 */       Name localName = ((FunctionNode)this.currentScriptOrFn).getFunctionName();
/* 1714 */       if ((localName == null) || (localName.length() == 0))
/* 1715 */         addError("msg.anon.generator.returns", "");
/*      */       else {
/* 1717 */         addError("msg.generator.returns", localName.getIdentifier());
/*      */       }
/*      */     }
/* 1720 */     ((AstNode)localObject).setLineno(i);
/* 1721 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode block()
/*      */     throws IOException
/*      */   {
/* 1727 */     if (this.currentToken != 85) codeBug();
/* 1728 */     consumeToken();
/* 1729 */     int i = this.ts.tokenBeg;
/* 1730 */     Scope localScope1 = new Scope(i);
/* 1731 */     localScope1.setLineno(this.ts.lineno);
/* 1732 */     pushScope(localScope1);
/*      */     try {
/* 1734 */       statements(localScope1);
/* 1735 */       mustMatchToken(86, "msg.no.brace.block");
/* 1736 */       localScope1.setLength(this.ts.tokenEnd - i);
/* 1737 */       return localScope1;
/*      */     } finally {
/* 1739 */       popScope();
/*      */     }
/*      */   }
/*      */ 
/*      */   private AstNode defaultXmlNamespace()
/*      */     throws IOException
/*      */   {
/* 1746 */     if (this.currentToken != 116) codeBug();
/* 1747 */     consumeToken();
/* 1748 */     mustHaveXML();
/* 1749 */     setRequiresActivation();
/* 1750 */     int i = this.ts.lineno; int j = this.ts.tokenBeg;
/*      */ 
/* 1752 */     if ((!matchToken(39)) || (!"xml".equals(this.ts.getString()))) {
/* 1753 */       reportError("msg.bad.namespace");
/*      */     }
/* 1755 */     if ((!matchToken(39)) || (!"namespace".equals(this.ts.getString()))) {
/* 1756 */       reportError("msg.bad.namespace");
/*      */     }
/* 1758 */     if (!matchToken(90)) {
/* 1759 */       reportError("msg.bad.namespace");
/*      */     }
/*      */ 
/* 1762 */     AstNode localAstNode = expr();
/* 1763 */     UnaryExpression localUnaryExpression = new UnaryExpression(j, getNodeEnd(localAstNode) - j);
/* 1764 */     localUnaryExpression.setOperator(74);
/* 1765 */     localUnaryExpression.setOperand(localAstNode);
/* 1766 */     localUnaryExpression.setLineno(i);
/*      */ 
/* 1768 */     ExpressionStatement localExpressionStatement = new ExpressionStatement(localUnaryExpression, true);
/* 1769 */     return localExpressionStatement;
/*      */   }
/*      */ 
/*      */   private void recordLabel(Label paramLabel, LabeledStatement paramLabeledStatement)
/*      */     throws IOException
/*      */   {
/* 1776 */     if (peekToken() != 103) codeBug();
/* 1777 */     consumeToken();
/* 1778 */     String str = paramLabel.getName();
/* 1779 */     if (this.labelSet == null) {
/* 1780 */       this.labelSet = new HashMap();
/*      */     } else {
/* 1782 */       LabeledStatement localLabeledStatement = (LabeledStatement)this.labelSet.get(str);
/* 1783 */       if (localLabeledStatement != null) {
/* 1784 */         if (this.compilerEnv.isIdeMode()) {
/* 1785 */           Label localLabel = localLabeledStatement.getLabelByName(str);
/* 1786 */           reportError("msg.dup.label", localLabel.getAbsolutePosition(), localLabel.getLength());
/*      */         }
/*      */ 
/* 1789 */         reportError("msg.dup.label", paramLabel.getPosition(), paramLabel.getLength());
/*      */       }
/*      */     }
/*      */ 
/* 1793 */     paramLabeledStatement.addLabel(paramLabel);
/* 1794 */     this.labelSet.put(str, paramLabeledStatement);
/*      */   }
/*      */ 
/*      */   private AstNode nameOrLabel()
/*      */     throws IOException
/*      */   {
/* 1806 */     if (this.currentToken != 39) throw codeBug();
/* 1807 */     int i = this.ts.tokenBeg;
/*      */ 
/* 1810 */     this.currentFlaggedToken |= 131072;
/* 1811 */     AstNode localAstNode = expr();
/*      */ 
/* 1813 */     if (localAstNode.getType() != 130) {
/* 1814 */       localObject1 = new ExpressionStatement(localAstNode, !insideFunction());
/* 1815 */       ((AstNode)localObject1).lineno = localAstNode.lineno;
/* 1816 */       return localObject1;
/*      */     }
/*      */ 
/* 1819 */     Object localObject1 = new LabeledStatement(i);
/* 1820 */     recordLabel((Label)localAstNode, (LabeledStatement)localObject1);
/* 1821 */     ((LabeledStatement)localObject1).setLineno(this.ts.lineno);
/*      */ 
/* 1823 */     Object localObject2 = null;
/* 1824 */     while (peekToken() == 39) {
/* 1825 */       this.currentFlaggedToken |= 131072;
/* 1826 */       localAstNode = expr();
/* 1827 */       if (localAstNode.getType() != 130) {
/* 1828 */         localObject2 = new ExpressionStatement(localAstNode, !insideFunction());
/* 1829 */         autoInsertSemicolon((AstNode)localObject2);
/* 1830 */         break;
/*      */       }
/* 1832 */       recordLabel((Label)localAstNode, (LabeledStatement)localObject1);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1837 */       this.currentLabel = ((LabeledStatement)localObject1);
/* 1838 */       if (localObject2 == null)
/* 1839 */         localObject2 = statementHelper();
/*      */     }
/*      */     finally
/*      */     {
/*      */       Iterator localIterator1;
/*      */       Label localLabel1;
/* 1842 */       this.currentLabel = null;
/*      */ 
/* 1844 */       for (Label localLabel2 : ((LabeledStatement)localObject1).getLabels()) {
/* 1845 */         this.labelSet.remove(localLabel2.getName());
/*      */       }
/*      */     }
/*      */ 
/* 1849 */     ((LabeledStatement)localObject1).setLength(getNodeEnd((AstNode)localObject2) - i);
/* 1850 */     ((LabeledStatement)localObject1).setStatement((AstNode)localObject2);
/* 1851 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private VariableDeclaration variables(int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/* 1868 */     VariableDeclaration localVariableDeclaration = new VariableDeclaration(paramInt2);
/* 1869 */     localVariableDeclaration.setType(paramInt1);
/* 1870 */     localVariableDeclaration.setLineno(this.ts.lineno);
/* 1871 */     String str1 = getAndResetJsDoc();
/* 1872 */     if (str1 != null) {
/* 1873 */       localVariableDeclaration.setJsDoc(str1);
/*      */     }
/*      */ 
/*      */     int i;
/*      */     while (true)
/*      */     {
/* 1879 */       AstNode localAstNode1 = null;
/* 1880 */       Name localName = null;
/* 1881 */       int j = peekToken(); int k = this.ts.tokenBeg;
/* 1882 */       i = this.ts.tokenEnd;
/*      */ 
/* 1884 */       if ((j == 83) || (j == 85))
/*      */       {
/* 1886 */         localAstNode1 = destructuringPrimaryExpr();
/* 1887 */         i = getNodeEnd(localAstNode1);
/* 1888 */         if (!(localAstNode1 instanceof DestructuringForm))
/* 1889 */           reportError("msg.bad.assign.left", k, i - k);
/* 1890 */         markDestructuring(localAstNode1);
/*      */       }
/*      */       else {
/* 1893 */         mustMatchToken(39, "msg.bad.var");
/* 1894 */         localName = createNameNode();
/* 1895 */         localName.setLineno(this.ts.getLineno());
/* 1896 */         if (this.inUseStrictDirective) {
/* 1897 */           String str2 = this.ts.getString();
/* 1898 */           if (("eval".equals(str2)) || ("arguments".equals(this.ts.getString())))
/*      */           {
/* 1900 */             reportError("msg.bad.id.strict", str2);
/*      */           }
/*      */         }
/* 1903 */         defineSymbol(paramInt1, this.ts.getString(), this.inForInit);
/*      */       }
/*      */ 
/* 1906 */       int m = this.ts.lineno;
/*      */ 
/* 1908 */       String str3 = getAndResetJsDoc();
/*      */ 
/* 1910 */       AstNode localAstNode2 = null;
/* 1911 */       if (matchToken(90)) {
/* 1912 */         localAstNode2 = assignExpr();
/* 1913 */         i = getNodeEnd(localAstNode2);
/*      */       }
/*      */ 
/* 1916 */       VariableInitializer localVariableInitializer = new VariableInitializer(k, i - k);
/* 1917 */       if (localAstNode1 != null) {
/* 1918 */         if ((localAstNode2 == null) && (!this.inForInit)) {
/* 1919 */           reportError("msg.destruct.assign.no.init");
/*      */         }
/* 1921 */         localVariableInitializer.setTarget(localAstNode1);
/*      */       } else {
/* 1923 */         localVariableInitializer.setTarget(localName);
/*      */       }
/* 1925 */       localVariableInitializer.setInitializer(localAstNode2);
/* 1926 */       localVariableInitializer.setType(paramInt1);
/* 1927 */       localVariableInitializer.setJsDoc(str3);
/* 1928 */       localVariableInitializer.setLineno(m);
/* 1929 */       localVariableDeclaration.addVariable(localVariableInitializer);
/*      */ 
/* 1931 */       if (!matchToken(89))
/*      */         break;
/*      */     }
/* 1934 */     localVariableDeclaration.setLength(i - paramInt2);
/* 1935 */     return localVariableDeclaration;
/*      */   }
/*      */ 
/*      */   private AstNode let(boolean paramBoolean, int paramInt)
/*      */     throws IOException
/*      */   {
/* 1942 */     LetNode localLetNode = new LetNode(paramInt);
/* 1943 */     localLetNode.setLineno(this.ts.lineno);
/* 1944 */     if (mustMatchToken(87, "msg.no.paren.after.let"))
/* 1945 */       localLetNode.setLp(this.ts.tokenBeg - paramInt);
/* 1946 */     pushScope(localLetNode);
/*      */     try {
/* 1948 */       VariableDeclaration localVariableDeclaration = variables(153, this.ts.tokenBeg);
/* 1949 */       localLetNode.setVariables(localVariableDeclaration);
/* 1950 */       if (mustMatchToken(88, "msg.no.paren.let"))
/* 1951 */         localLetNode.setRp(this.ts.tokenBeg - paramInt);
/*      */       Object localObject1;
/* 1953 */       if ((paramBoolean) && (peekToken() == 85))
/*      */       {
/* 1955 */         consumeToken();
/* 1956 */         int i = this.ts.tokenBeg;
/* 1957 */         localObject1 = statements();
/* 1958 */         mustMatchToken(86, "msg.no.curly.let");
/* 1959 */         ((AstNode)localObject1).setLength(this.ts.tokenEnd - i);
/* 1960 */         localLetNode.setLength(this.ts.tokenEnd - paramInt);
/* 1961 */         localLetNode.setBody((AstNode)localObject1);
/* 1962 */         localLetNode.setType(153);
/*      */       }
/*      */       else {
/* 1965 */         AstNode localAstNode = expr();
/* 1966 */         localLetNode.setLength(getNodeEnd(localAstNode) - paramInt);
/* 1967 */         localLetNode.setBody(localAstNode);
/* 1968 */         if (paramBoolean)
/*      */         {
/* 1970 */           localObject1 = new ExpressionStatement(localLetNode, !insideFunction());
/*      */ 
/* 1972 */           ((ExpressionStatement)localObject1).setLineno(localLetNode.getLineno());
/* 1973 */           return localObject1;
/*      */         }
/*      */       }
/*      */     } finally {
/* 1977 */       popScope();
/*      */     }
/* 1979 */     return localLetNode;
/*      */   }
/*      */ 
/*      */   void defineSymbol(int paramInt, String paramString) {
/* 1983 */     defineSymbol(paramInt, paramString, false);
/*      */   }
/*      */ 
/*      */   void defineSymbol(int paramInt, String paramString, boolean paramBoolean) {
/* 1987 */     if (paramString == null) {
/* 1988 */       if (this.compilerEnv.isIdeMode()) {
/* 1989 */         return;
/*      */       }
/* 1991 */       codeBug();
/*      */     }
/*      */ 
/* 1994 */     Scope localScope = this.currentScope.getDefiningScope(paramString);
/* 1995 */     Object localObject = localScope != null ? localScope.getSymbol(paramString) : null;
/*      */ 
/* 1998 */     int i = localObject != null ? localObject.getDeclType() : -1;
/* 1999 */     if ((localObject != null) && ((i == 154) || (paramInt == 154) || ((localScope == this.currentScope) && (i == 153))))
/*      */     {
/* 2004 */       addError(i == 109 ? "msg.fn.redecl" : i == 122 ? "msg.var.redecl" : i == 153 ? "msg.let.redecl" : i == 154 ? "msg.const.redecl" : "msg.parm.redecl", paramString);
/*      */ 
/* 2009 */       return;
/*      */     }
/* 2011 */     switch (paramInt) {
/*      */     case 153:
/* 2013 */       if ((!paramBoolean) && ((this.currentScope.getType() == 112) || ((this.currentScope instanceof Loop))))
/*      */       {
/* 2016 */         addError("msg.let.decl.not.in.block");
/* 2017 */         return;
/*      */       }
/* 2019 */       this.currentScope.putSymbol(new Symbol(paramInt, paramString));
/* 2020 */       return;
/*      */     case 109:
/*      */     case 122:
/*      */     case 154:
/* 2025 */       if (localObject != null) {
/* 2026 */         if (i == 122)
/* 2027 */           addStrictWarning("msg.var.redecl", paramString);
/* 2028 */         else if (i == 87)
/* 2029 */           addStrictWarning("msg.var.hides.arg", paramString);
/*      */       }
/*      */       else {
/* 2032 */         this.currentScriptOrFn.putSymbol(new Symbol(paramInt, paramString));
/*      */       }
/* 2034 */       return;
/*      */     case 87:
/* 2037 */       if (localObject != null)
/*      */       {
/* 2040 */         addWarning("msg.dup.parms", paramString);
/*      */       }
/* 2042 */       this.currentScriptOrFn.putSymbol(new Symbol(paramInt, paramString));
/* 2043 */       return;
/*      */     }
/*      */ 
/* 2046 */     throw codeBug();
/*      */   }
/*      */ 
/*      */   private AstNode expr()
/*      */     throws IOException
/*      */   {
/* 2053 */     Object localObject = assignExpr();
/* 2054 */     int i = ((AstNode)localObject).getPosition();
/* 2055 */     while (matchToken(89)) {
/* 2056 */       int j = this.ts.lineno;
/* 2057 */       int k = this.ts.tokenBeg;
/* 2058 */       if ((this.compilerEnv.isStrictMode()) && (!((AstNode)localObject).hasSideEffects())) {
/* 2059 */         addStrictWarning("msg.no.side.effects", "", i, nodeEnd((AstNode)localObject) - i);
/*      */       }
/* 2061 */       if (peekToken() == 72)
/* 2062 */         reportError("msg.yield.parenthesized");
/* 2063 */       localObject = new InfixExpression(89, (AstNode)localObject, assignExpr(), k);
/* 2064 */       ((AstNode)localObject).setLineno(j);
/*      */     }
/* 2066 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode assignExpr()
/*      */     throws IOException
/*      */   {
/* 2072 */     int i = peekToken();
/* 2073 */     if (i == 72) {
/* 2074 */       return returnOrYield(i, true);
/*      */     }
/* 2076 */     Object localObject = condExpr();
/* 2077 */     i = peekToken();
/* 2078 */     if ((90 <= i) && (i <= 101)) {
/* 2079 */       consumeToken();
/*      */ 
/* 2082 */       String str = getAndResetJsDoc();
/*      */ 
/* 2084 */       markDestructuring((AstNode)localObject);
/* 2085 */       int j = this.ts.tokenBeg;
/* 2086 */       int k = this.ts.getLineno();
/*      */ 
/* 2088 */       localObject = new Assignment(i, (AstNode)localObject, assignExpr(), j);
/*      */ 
/* 2090 */       ((AstNode)localObject).setLineno(k);
/* 2091 */       if (str != null)
/* 2092 */         ((AstNode)localObject).setJsDoc(str);
/*      */     }
/* 2094 */     else if ((i == 82) && (((AstNode)localObject).getType() == 33))
/*      */     {
/* 2097 */       if (this.currentJsDocComment != null) {
/* 2098 */         ((AstNode)localObject).setJsDoc(getAndResetJsDoc());
/*      */       }
/*      */     }
/* 2101 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode condExpr()
/*      */     throws IOException
/*      */   {
/* 2107 */     Object localObject = orExpr();
/* 2108 */     if (matchToken(102)) {
/* 2109 */       int i = this.ts.lineno;
/* 2110 */       int j = this.ts.tokenBeg; int k = -1;
/* 2111 */       AstNode localAstNode1 = assignExpr();
/* 2112 */       if (mustMatchToken(103, "msg.no.colon.cond"))
/* 2113 */         k = this.ts.tokenBeg;
/* 2114 */       AstNode localAstNode2 = assignExpr();
/* 2115 */       int m = ((AstNode)localObject).getPosition(); int n = getNodeEnd(localAstNode2) - m;
/* 2116 */       ConditionalExpression localConditionalExpression = new ConditionalExpression(m, n);
/* 2117 */       localConditionalExpression.setLineno(i);
/* 2118 */       localConditionalExpression.setTestExpression((AstNode)localObject);
/* 2119 */       localConditionalExpression.setTrueExpression(localAstNode1);
/* 2120 */       localConditionalExpression.setFalseExpression(localAstNode2);
/* 2121 */       localConditionalExpression.setQuestionMarkPosition(j - m);
/* 2122 */       localConditionalExpression.setColonPosition(k - m);
/* 2123 */       localObject = localConditionalExpression;
/*      */     }
/* 2125 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode orExpr()
/*      */     throws IOException
/*      */   {
/* 2131 */     Object localObject = andExpr();
/* 2132 */     if (matchToken(104)) {
/* 2133 */       int i = this.ts.tokenBeg;
/* 2134 */       int j = this.ts.lineno;
/* 2135 */       localObject = new InfixExpression(104, (AstNode)localObject, orExpr(), i);
/* 2136 */       ((AstNode)localObject).setLineno(j);
/*      */     }
/* 2138 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode andExpr()
/*      */     throws IOException
/*      */   {
/* 2144 */     Object localObject = bitOrExpr();
/* 2145 */     if (matchToken(105)) {
/* 2146 */       int i = this.ts.tokenBeg;
/* 2147 */       int j = this.ts.lineno;
/* 2148 */       localObject = new InfixExpression(105, (AstNode)localObject, andExpr(), i);
/* 2149 */       ((AstNode)localObject).setLineno(j);
/*      */     }
/* 2151 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode bitOrExpr()
/*      */     throws IOException
/*      */   {
/* 2157 */     Object localObject = bitXorExpr();
/* 2158 */     while (matchToken(9)) {
/* 2159 */       int i = this.ts.tokenBeg;
/* 2160 */       int j = this.ts.lineno;
/* 2161 */       localObject = new InfixExpression(9, (AstNode)localObject, bitXorExpr(), i);
/* 2162 */       ((AstNode)localObject).setLineno(j);
/*      */     }
/* 2164 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode bitXorExpr()
/*      */     throws IOException
/*      */   {
/* 2170 */     Object localObject = bitAndExpr();
/* 2171 */     while (matchToken(10)) {
/* 2172 */       int i = this.ts.tokenBeg;
/* 2173 */       int j = this.ts.lineno;
/* 2174 */       localObject = new InfixExpression(10, (AstNode)localObject, bitAndExpr(), i);
/* 2175 */       ((AstNode)localObject).setLineno(j);
/*      */     }
/* 2177 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode bitAndExpr()
/*      */     throws IOException
/*      */   {
/* 2183 */     Object localObject = eqExpr();
/* 2184 */     while (matchToken(11)) {
/* 2185 */       int i = this.ts.tokenBeg;
/* 2186 */       int j = this.ts.lineno;
/* 2187 */       localObject = new InfixExpression(11, (AstNode)localObject, eqExpr(), i);
/* 2188 */       ((AstNode)localObject).setLineno(j);
/*      */     }
/* 2190 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode eqExpr()
/*      */     throws IOException
/*      */   {
/* 2196 */     Object localObject = relExpr();
/*      */     while (true) {
/* 2198 */       int i = peekToken(); int j = this.ts.tokenBeg;
/* 2199 */       int k = this.ts.lineno;
/* 2200 */       switch (i) {
/*      */       case 12:
/*      */       case 13:
/*      */       case 46:
/*      */       case 47:
/* 2205 */         consumeToken();
/* 2206 */         int m = i;
/* 2207 */         if (this.compilerEnv.getLanguageVersion() == 120)
/*      */         {
/* 2209 */           if (i == 12)
/* 2210 */             m = 46;
/* 2211 */           else if (i == 13)
/* 2212 */             m = 47;
/*      */         }
/* 2214 */         localObject = new InfixExpression(m, (AstNode)localObject, relExpr(), j);
/* 2215 */         ((AstNode)localObject).setLineno(k);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2220 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode relExpr()
/*      */     throws IOException
/*      */   {
/* 2226 */     Object localObject = shiftExpr();
/*      */     while (true) {
/* 2228 */       int i = peekToken(); int j = this.ts.tokenBeg;
/* 2229 */       int k = this.ts.lineno;
/* 2230 */       switch (i) {
/*      */       case 52:
/* 2232 */         if (this.inForInit) {
/*      */           break;
/*      */         }
/*      */       case 14:
/*      */       case 15:
/*      */       case 16:
/*      */       case 17:
/*      */       case 53:
/* 2240 */         consumeToken();
/* 2241 */         localObject = new InfixExpression(i, (AstNode)localObject, shiftExpr(), j);
/* 2242 */         ((AstNode)localObject).setLineno(k);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2247 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode shiftExpr()
/*      */     throws IOException
/*      */   {
/* 2253 */     Object localObject = addExpr();
/*      */     while (true) {
/* 2255 */       int i = peekToken(); int j = this.ts.tokenBeg;
/* 2256 */       int k = this.ts.lineno;
/* 2257 */       switch (i) {
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/* 2261 */         consumeToken();
/* 2262 */         localObject = new InfixExpression(i, (AstNode)localObject, addExpr(), j);
/* 2263 */         ((AstNode)localObject).setLineno(k);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2268 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode addExpr()
/*      */     throws IOException
/*      */   {
/* 2274 */     Object localObject = mulExpr();
/*      */     while (true) {
/* 2276 */       int i = peekToken(); int j = this.ts.tokenBeg;
/* 2277 */       if ((i != 21) && (i != 22)) break;
/* 2278 */       consumeToken();
/* 2279 */       int k = this.ts.lineno;
/* 2280 */       localObject = new InfixExpression(i, (AstNode)localObject, mulExpr(), j);
/* 2281 */       ((AstNode)localObject).setLineno(k);
/*      */     }
/*      */ 
/* 2286 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode mulExpr()
/*      */     throws IOException
/*      */   {
/* 2292 */     Object localObject = unaryExpr();
/*      */     while (true) {
/* 2294 */       int i = peekToken(); int j = this.ts.tokenBeg;
/* 2295 */       switch (i) {
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/* 2299 */         consumeToken();
/* 2300 */         int k = this.ts.lineno;
/* 2301 */         localObject = new InfixExpression(i, (AstNode)localObject, unaryExpr(), j);
/* 2302 */         ((AstNode)localObject).setLineno(k);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2307 */     return localObject;
/*      */   }
/*      */ 
/*      */   private AstNode unaryExpr()
/*      */     throws IOException
/*      */   {
/* 2314 */     int i = peekToken();
/* 2315 */     int j = this.ts.lineno;
/*      */     UnaryExpression localUnaryExpression1;
/* 2317 */     switch (i) {
/*      */     case 26:
/*      */     case 27:
/*      */     case 32:
/*      */     case 126:
/* 2322 */       consumeToken();
/* 2323 */       localUnaryExpression1 = new UnaryExpression(i, this.ts.tokenBeg, unaryExpr());
/* 2324 */       localUnaryExpression1.setLineno(j);
/* 2325 */       return localUnaryExpression1;
/*      */     case 21:
/* 2328 */       consumeToken();
/*      */ 
/* 2330 */       localUnaryExpression1 = new UnaryExpression(28, this.ts.tokenBeg, unaryExpr());
/* 2331 */       localUnaryExpression1.setLineno(j);
/* 2332 */       return localUnaryExpression1;
/*      */     case 22:
/* 2335 */       consumeToken();
/*      */ 
/* 2337 */       localUnaryExpression1 = new UnaryExpression(29, this.ts.tokenBeg, unaryExpr());
/* 2338 */       localUnaryExpression1.setLineno(j);
/* 2339 */       return localUnaryExpression1;
/*      */     case 106:
/*      */     case 107:
/* 2343 */       consumeToken();
/* 2344 */       UnaryExpression localUnaryExpression2 = new UnaryExpression(i, this.ts.tokenBeg, memberExpr(true));
/*      */ 
/* 2346 */       localUnaryExpression2.setLineno(j);
/* 2347 */       checkBadIncDec(localUnaryExpression2);
/* 2348 */       return localUnaryExpression2;
/*      */     case 31:
/* 2351 */       consumeToken();
/* 2352 */       localUnaryExpression1 = new UnaryExpression(i, this.ts.tokenBeg, unaryExpr());
/* 2353 */       localUnaryExpression1.setLineno(j);
/* 2354 */       return localUnaryExpression1;
/*      */     case -1:
/* 2357 */       consumeToken();
/* 2358 */       return makeErrorNode();
/*      */     case 14:
/* 2362 */       if (this.compilerEnv.isXmlAvailable()) {
/* 2363 */         consumeToken();
/* 2364 */         return memberExprTail(true, xmlInitializer());
/*      */       }
/*      */       break;
/*      */     }
/*      */ 
/* 2369 */     AstNode localAstNode = memberExpr(true);
/*      */ 
/* 2371 */     i = peekTokenOrEOL();
/* 2372 */     if ((i != 106) && (i != 107)) {
/* 2373 */       return localAstNode;
/*      */     }
/* 2375 */     consumeToken();
/* 2376 */     UnaryExpression localUnaryExpression3 = new UnaryExpression(i, this.ts.tokenBeg, localAstNode, true);
/*      */ 
/* 2378 */     localUnaryExpression3.setLineno(j);
/* 2379 */     checkBadIncDec(localUnaryExpression3);
/* 2380 */     return localUnaryExpression3;
/*      */   }
/*      */ 
/*      */   private AstNode xmlInitializer()
/*      */     throws IOException
/*      */   {
/* 2387 */     if (this.currentToken != 14) codeBug();
/* 2388 */     int i = this.ts.tokenBeg; int j = this.ts.getFirstXMLToken();
/* 2389 */     if ((j != 145) && (j != 148)) {
/* 2390 */       reportError("msg.syntax");
/* 2391 */       return makeErrorNode();
/*      */     }
/*      */ 
/* 2394 */     XmlLiteral localXmlLiteral = new XmlLiteral(i);
/* 2395 */     localXmlLiteral.setLineno(this.ts.lineno);
/*      */ 
/* 2397 */     for (; ; j = this.ts.getNextXMLToken())
/* 2398 */       switch (j) {
/*      */       case 145:
/* 2400 */         localXmlLiteral.addFragment(new XmlString(this.ts.tokenBeg, this.ts.getString()));
/* 2401 */         mustMatchToken(85, "msg.syntax");
/* 2402 */         int k = this.ts.tokenBeg;
/* 2403 */         AstNode localAstNode = peekToken() == 86 ? new EmptyExpression(k, this.ts.tokenEnd - k) : expr();
/*      */ 
/* 2406 */         mustMatchToken(86, "msg.syntax");
/* 2407 */         XmlExpression localXmlExpression = new XmlExpression(k, localAstNode);
/* 2408 */         localXmlExpression.setIsXmlAttribute(this.ts.isXMLAttribute());
/* 2409 */         localXmlExpression.setLength(this.ts.tokenEnd - k);
/* 2410 */         localXmlLiteral.addFragment(localXmlExpression);
/* 2411 */         break;
/*      */       case 148:
/* 2414 */         localXmlLiteral.addFragment(new XmlString(this.ts.tokenBeg, this.ts.getString()));
/* 2415 */         return localXmlLiteral;
/*      */       default:
/* 2418 */         reportError("msg.syntax");
/* 2419 */         return makeErrorNode();
/*      */       }
/*      */   }
/*      */ 
/*      */   private List<AstNode> argumentList()
/*      */     throws IOException
/*      */   {
/* 2427 */     if (matchToken(88)) {
/* 2428 */       return null;
/*      */     }
/* 2430 */     ArrayList localArrayList = new ArrayList();
/* 2431 */     boolean bool = this.inForInit;
/* 2432 */     this.inForInit = false;
/*      */     try {
/*      */       do {
/* 2435 */         if (peekToken() == 72)
/* 2436 */           reportError("msg.yield.parenthesized");
/* 2437 */         localArrayList.add(assignExpr());
/* 2438 */       }while (matchToken(89));
/*      */     } finally {
/* 2440 */       this.inForInit = bool;
/*      */     }
/*      */ 
/* 2443 */     mustMatchToken(88, "msg.no.paren.arg");
/* 2444 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   private AstNode memberExpr(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 2455 */     int i = peekToken(); int j = this.ts.lineno;
/*      */     Object localObject1;
/* 2458 */     if (i != 30) {
/* 2459 */       localObject1 = primaryExpr();
/*      */     } else {
/* 2461 */       consumeToken();
/* 2462 */       int k = this.ts.tokenBeg;
/* 2463 */       NewExpression localNewExpression = new NewExpression(k);
/*      */ 
/* 2465 */       AstNode localAstNode2 = memberExpr(false);
/* 2466 */       int m = getNodeEnd(localAstNode2);
/* 2467 */       localNewExpression.setTarget(localAstNode2);
/*      */ 
/* 2469 */       int n = -1;
/*      */       Object localObject2;
/* 2470 */       if (matchToken(87)) {
/* 2471 */         n = this.ts.tokenBeg;
/* 2472 */         localObject2 = argumentList();
/* 2473 */         if ((localObject2 != null) && (((List)localObject2).size() > 65536))
/* 2474 */           reportError("msg.too.many.constructor.args");
/* 2475 */         int i1 = this.ts.tokenBeg;
/* 2476 */         m = this.ts.tokenEnd;
/* 2477 */         if (localObject2 != null)
/* 2478 */           localNewExpression.setArguments((List)localObject2);
/* 2479 */         localNewExpression.setParens(n - k, i1 - k);
/*      */       }
/*      */ 
/* 2486 */       if (matchToken(85)) {
/* 2487 */         localObject2 = objectLiteral();
/* 2488 */         m = getNodeEnd((AstNode)localObject2);
/* 2489 */         localNewExpression.setInitializer((ObjectLiteral)localObject2);
/*      */       }
/* 2491 */       localNewExpression.setLength(m - k);
/* 2492 */       localObject1 = localNewExpression;
/*      */     }
/* 2494 */     ((AstNode)localObject1).setLineno(j);
/* 2495 */     AstNode localAstNode1 = memberExprTail(paramBoolean, (AstNode)localObject1);
/* 2496 */     return localAstNode1;
/*      */   }
/*      */ 
/*      */   private AstNode memberExprTail(boolean paramBoolean, AstNode paramAstNode)
/*      */     throws IOException
/*      */   {
/* 2510 */     if (paramAstNode == null) codeBug();
/* 2511 */     int i = paramAstNode.getPosition();
/*      */     while (true)
/*      */     {
/* 2515 */       int k = peekToken();
/*      */       int j;
/*      */       int i1;
/* 2516 */       switch (k) {
/*      */       case 108:
/*      */       case 143:
/* 2519 */         j = this.ts.lineno;
/* 2520 */         paramAstNode = propertyAccess(k, paramAstNode);
/* 2521 */         paramAstNode.setLineno(j);
/* 2522 */         break;
/*      */       case 146:
/* 2525 */         consumeToken();
/* 2526 */         int m = this.ts.tokenBeg; int n = -1;
/* 2527 */         j = this.ts.lineno;
/* 2528 */         mustHaveXML();
/* 2529 */         setRequiresActivation();
/* 2530 */         AstNode localAstNode1 = expr();
/* 2531 */         i1 = getNodeEnd(localAstNode1);
/* 2532 */         if (mustMatchToken(88, "msg.no.paren")) {
/* 2533 */           n = this.ts.tokenBeg;
/* 2534 */           i1 = this.ts.tokenEnd;
/*      */         }
/* 2536 */         XmlDotQuery localXmlDotQuery = new XmlDotQuery(i, i1 - i);
/* 2537 */         localXmlDotQuery.setLeft(paramAstNode);
/* 2538 */         localXmlDotQuery.setRight(localAstNode1);
/* 2539 */         localXmlDotQuery.setOperatorPosition(m);
/* 2540 */         localXmlDotQuery.setRp(n - i);
/* 2541 */         localXmlDotQuery.setLineno(j);
/* 2542 */         paramAstNode = localXmlDotQuery;
/* 2543 */         break;
/*      */       case 83:
/* 2546 */         consumeToken();
/* 2547 */         int i2 = this.ts.tokenBeg; int i3 = -1;
/* 2548 */         j = this.ts.lineno;
/* 2549 */         AstNode localAstNode2 = expr();
/* 2550 */         i1 = getNodeEnd(localAstNode2);
/* 2551 */         if (mustMatchToken(84, "msg.no.bracket.index")) {
/* 2552 */           i3 = this.ts.tokenBeg;
/* 2553 */           i1 = this.ts.tokenEnd;
/*      */         }
/* 2555 */         ElementGet localElementGet = new ElementGet(i, i1 - i);
/* 2556 */         localElementGet.setTarget(paramAstNode);
/* 2557 */         localElementGet.setElement(localAstNode2);
/* 2558 */         localElementGet.setParens(i2, i3);
/* 2559 */         localElementGet.setLineno(j);
/* 2560 */         paramAstNode = localElementGet;
/* 2561 */         break;
/*      */       case 87:
/* 2564 */         if (!paramBoolean) {
/*      */           break label488;
/*      */         }
/* 2567 */         j = this.ts.lineno;
/* 2568 */         consumeToken();
/* 2569 */         checkCallRequiresActivation(paramAstNode);
/* 2570 */         FunctionCall localFunctionCall = new FunctionCall(i);
/* 2571 */         localFunctionCall.setTarget(paramAstNode);
/*      */ 
/* 2574 */         localFunctionCall.setLineno(j);
/* 2575 */         localFunctionCall.setLp(this.ts.tokenBeg - i);
/* 2576 */         List localList = argumentList();
/* 2577 */         if ((localList != null) && (localList.size() > 65536))
/* 2578 */           reportError("msg.too.many.function.args");
/* 2579 */         localFunctionCall.setArguments(localList);
/* 2580 */         localFunctionCall.setRp(this.ts.tokenBeg - i);
/* 2581 */         localFunctionCall.setLength(this.ts.tokenEnd - i);
/* 2582 */         paramAstNode = localFunctionCall;
/* 2583 */         break;
/*      */       default:
/* 2586 */         break label488;
/*      */       }
/*      */     }
/* 2589 */     label488: return paramAstNode;
/*      */   }
/*      */ 
/*      */   private AstNode propertyAccess(int paramInt, AstNode paramAstNode)
/*      */     throws IOException
/*      */   {
/* 2600 */     if (paramAstNode == null) codeBug();
/* 2601 */     int i = 0; int j = this.ts.lineno; int k = this.ts.tokenBeg;
/* 2602 */     consumeToken();
/*      */ 
/* 2604 */     if (paramInt == 143) {
/* 2605 */       mustHaveXML();
/* 2606 */       i = 4;
/*      */     }
/*      */ 
/* 2609 */     if (!this.compilerEnv.isXmlAvailable()) {
/* 2610 */       mustMatchToken(39, "msg.no.name.after.dot");
/* 2611 */       localObject = createNameNode(true, 33);
/* 2612 */       PropertyGet localPropertyGet1 = new PropertyGet(paramAstNode, (Name)localObject, k);
/* 2613 */       localPropertyGet1.setLineno(j);
/* 2614 */       return localPropertyGet1;
/*      */     }
/*      */ 
/* 2617 */     Object localObject = null;
/*      */ 
/* 2619 */     switch (nextToken())
/*      */     {
/*      */     case 50:
/* 2622 */       saveNameTokenData(this.ts.tokenBeg, "throw", this.ts.lineno);
/* 2623 */       localObject = propertyName(-1, "throw", i);
/* 2624 */       break;
/*      */     case 39:
/* 2628 */       localObject = propertyName(-1, this.ts.getString(), i);
/* 2629 */       break;
/*      */     case 23:
/* 2633 */       saveNameTokenData(this.ts.tokenBeg, "*", this.ts.lineno);
/* 2634 */       localObject = propertyName(-1, "*", i);
/* 2635 */       break;
/*      */     case 147:
/* 2640 */       localObject = attributeAccess();
/* 2641 */       break;
/*      */     default:
/* 2644 */       reportError("msg.no.name.after.dot");
/* 2645 */       return makeErrorNode();
/*      */     }
/*      */ 
/* 2648 */     boolean bool = localObject instanceof XmlRef;
/* 2649 */     PropertyGet localPropertyGet2 = bool ? new XmlMemberGet() : new PropertyGet();
/* 2650 */     if ((bool) && (paramInt == 108))
/* 2651 */       localPropertyGet2.setType(108);
/* 2652 */     int m = paramAstNode.getPosition();
/* 2653 */     localPropertyGet2.setPosition(m);
/* 2654 */     localPropertyGet2.setLength(getNodeEnd((AstNode)localObject) - m);
/* 2655 */     localPropertyGet2.setOperatorPosition(k - m);
/* 2656 */     localPropertyGet2.setLineno(j);
/* 2657 */     localPropertyGet2.setLeft(paramAstNode);
/* 2658 */     localPropertyGet2.setRight((AstNode)localObject);
/* 2659 */     return localPropertyGet2;
/*      */   }
/*      */ 
/*      */   private AstNode attributeAccess()
/*      */     throws IOException
/*      */   {
/* 2672 */     int i = nextToken(); int j = this.ts.tokenBeg;
/*      */ 
/* 2674 */     switch (i)
/*      */     {
/*      */     case 39:
/* 2677 */       return propertyName(j, this.ts.getString(), 0);
/*      */     case 23:
/* 2681 */       saveNameTokenData(this.ts.tokenBeg, "*", this.ts.lineno);
/* 2682 */       return propertyName(j, "*", 0);
/*      */     case 83:
/* 2686 */       return xmlElemRef(j, null, -1);
/*      */     }
/*      */ 
/* 2689 */     reportError("msg.no.name.after.xmlAttr");
/* 2690 */     return makeErrorNode();
/*      */   }
/*      */ 
/*      */   private AstNode propertyName(int paramInt1, String paramString, int paramInt2)
/*      */     throws IOException
/*      */   {
/* 2712 */     int i = paramInt1 != -1 ? paramInt1 : this.ts.tokenBeg; int j = this.ts.lineno;
/* 2713 */     int k = -1;
/* 2714 */     Name localName1 = createNameNode(true, this.currentToken);
/* 2715 */     Name localName2 = null;
/*      */ 
/* 2717 */     if (matchToken(144)) {
/* 2718 */       localName2 = localName1;
/* 2719 */       k = this.ts.tokenBeg;
/*      */ 
/* 2721 */       switch (nextToken())
/*      */       {
/*      */       case 39:
/* 2724 */         localName1 = createNameNode();
/* 2725 */         break;
/*      */       case 23:
/* 2729 */         saveNameTokenData(this.ts.tokenBeg, "*", this.ts.lineno);
/* 2730 */         localName1 = createNameNode(false, -1);
/* 2731 */         break;
/*      */       case 83:
/* 2735 */         return xmlElemRef(paramInt1, localName2, k);
/*      */       default:
/* 2738 */         reportError("msg.no.name.after.coloncolon");
/* 2739 */         return makeErrorNode();
/*      */       }
/*      */     }
/*      */ 
/* 2743 */     if ((localName2 == null) && (paramInt2 == 0) && (paramInt1 == -1)) {
/* 2744 */       return localName1;
/*      */     }
/*      */ 
/* 2747 */     XmlPropRef localXmlPropRef = new XmlPropRef(i, getNodeEnd(localName1) - i);
/* 2748 */     localXmlPropRef.setAtPos(paramInt1);
/* 2749 */     localXmlPropRef.setNamespace(localName2);
/* 2750 */     localXmlPropRef.setColonPos(k);
/* 2751 */     localXmlPropRef.setPropName(localName1);
/* 2752 */     localXmlPropRef.setLineno(j);
/* 2753 */     return localXmlPropRef;
/*      */   }
/*      */ 
/*      */   private XmlElemRef xmlElemRef(int paramInt1, Name paramName, int paramInt2)
/*      */     throws IOException
/*      */   {
/* 2763 */     int i = this.ts.tokenBeg; int j = -1; int k = paramInt1 != -1 ? paramInt1 : i;
/* 2764 */     AstNode localAstNode = expr();
/* 2765 */     int m = getNodeEnd(localAstNode);
/* 2766 */     if (mustMatchToken(84, "msg.no.bracket.index")) {
/* 2767 */       j = this.ts.tokenBeg;
/* 2768 */       m = this.ts.tokenEnd;
/*      */     }
/* 2770 */     XmlElemRef localXmlElemRef = new XmlElemRef(k, m - k);
/* 2771 */     localXmlElemRef.setNamespace(paramName);
/* 2772 */     localXmlElemRef.setColonPos(paramInt2);
/* 2773 */     localXmlElemRef.setAtPos(paramInt1);
/* 2774 */     localXmlElemRef.setExpression(localAstNode);
/* 2775 */     localXmlElemRef.setBrackets(i, j);
/* 2776 */     return localXmlElemRef;
/*      */   }
/*      */ 
/*      */   private AstNode destructuringPrimaryExpr() throws IOException, Parser.ParserException
/*      */   {
/*      */     try
/*      */     {
/* 2783 */       this.inDestructuringAssignment = true;
/* 2784 */       return primaryExpr();
/*      */     } finally {
/* 2786 */       this.inDestructuringAssignment = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private AstNode primaryExpr()
/*      */     throws IOException
/*      */   {
/* 2793 */     int i = nextFlaggedToken();
/* 2794 */     int j = i & 0xFFFF;
/*      */     int k;
/*      */     int m;
/* 2796 */     switch (j) {
/*      */     case 109:
/* 2798 */       return function(2);
/*      */     case 83:
/* 2801 */       return arrayLiteral();
/*      */     case 85:
/* 2804 */       return objectLiteral();
/*      */     case 153:
/* 2807 */       return let(false, this.ts.tokenBeg);
/*      */     case 87:
/* 2810 */       return parenExpr();
/*      */     case 147:
/* 2813 */       mustHaveXML();
/* 2814 */       return attributeAccess();
/*      */     case 39:
/* 2817 */       return name(i, j);
/*      */     case 40:
/* 2820 */       String str = this.ts.getString();
/* 2821 */       if ((this.inUseStrictDirective) && (this.ts.isNumberOctal())) {
/* 2822 */         reportError("msg.no.octal.strict");
/*      */       }
/* 2824 */       return new NumberLiteral(this.ts.tokenBeg, str, this.ts.getNumber());
/*      */     case 41:
/* 2830 */       return createStringLiteral();
/*      */     case 24:
/*      */     case 100:
/* 2835 */       this.ts.readRegExp(j);
/* 2836 */       k = this.ts.tokenBeg; m = this.ts.tokenEnd;
/* 2837 */       RegExpLiteral localRegExpLiteral = new RegExpLiteral(k, m - k);
/* 2838 */       localRegExpLiteral.setValue(this.ts.getString());
/* 2839 */       localRegExpLiteral.setFlags(this.ts.readAndClearRegExpFlags());
/* 2840 */       return localRegExpLiteral;
/*      */     case 42:
/*      */     case 43:
/*      */     case 44:
/*      */     case 45:
/* 2846 */       k = this.ts.tokenBeg; m = this.ts.tokenEnd;
/* 2847 */       return new KeywordLiteral(k, m - k, j);
/*      */     case 127:
/* 2850 */       reportError("msg.reserved.id");
/* 2851 */       break;
/*      */     case -1:
/* 2855 */       break;
/*      */     case 0:
/* 2858 */       reportError("msg.unexpected.eof");
/* 2859 */       break;
/*      */     default:
/* 2862 */       reportError("msg.syntax");
/*      */     }
/*      */ 
/* 2866 */     return makeErrorNode();
/*      */   }
/*      */ 
/*      */   private AstNode parenExpr() throws IOException {
/* 2870 */     boolean bool = this.inForInit;
/* 2871 */     this.inForInit = false;
/*      */     try {
/* 2873 */       String str = getAndResetJsDoc();
/* 2874 */       int i = this.ts.lineno;
/* 2875 */       AstNode localAstNode = expr();
/* 2876 */       ParenthesizedExpression localParenthesizedExpression1 = new ParenthesizedExpression(localAstNode);
/* 2877 */       if (str == null) {
/* 2878 */         str = getAndResetJsDoc();
/*      */       }
/* 2880 */       if (str != null) {
/* 2881 */         localParenthesizedExpression1.setJsDoc(str);
/*      */       }
/* 2883 */       mustMatchToken(88, "msg.no.paren");
/* 2884 */       localParenthesizedExpression1.setLength(this.ts.tokenEnd - localParenthesizedExpression1.getPosition());
/* 2885 */       localParenthesizedExpression1.setLineno(i);
/* 2886 */       return localParenthesizedExpression1;
/*      */     } finally {
/* 2888 */       this.inForInit = bool;
/*      */     }
/*      */   }
/*      */ 
/*      */   private AstNode name(int paramInt1, int paramInt2) throws IOException {
/* 2893 */     String str = this.ts.getString();
/* 2894 */     int i = this.ts.tokenBeg; int j = this.ts.lineno;
/* 2895 */     if ((0 != (paramInt1 & 0x20000)) && (peekToken() == 103))
/*      */     {
/* 2898 */       Label localLabel = new Label(i, this.ts.tokenEnd - i);
/* 2899 */       localLabel.setName(str);
/* 2900 */       localLabel.setLineno(this.ts.lineno);
/* 2901 */       return localLabel;
/*      */     }
/*      */ 
/* 2906 */     saveNameTokenData(i, str, j);
/*      */ 
/* 2908 */     if (this.compilerEnv.isXmlAvailable()) {
/* 2909 */       return propertyName(-1, str, 0);
/*      */     }
/* 2911 */     return createNameNode(true, 39);
/*      */   }
/*      */ 
/*      */   private AstNode arrayLiteral()
/*      */     throws IOException
/*      */   {
/* 2921 */     if (this.currentToken != 83) codeBug();
/* 2922 */     int i = this.ts.tokenBeg; int j = this.ts.tokenEnd;
/* 2923 */     ArrayList localArrayList = new ArrayList();
/* 2924 */     ArrayLiteral localArrayLiteral = new ArrayLiteral(i);
/* 2925 */     int k = 1;
/* 2926 */     int m = -1;
/* 2927 */     int n = 0;
/*      */     while (true) {
/* 2929 */       int i1 = peekToken();
/* 2930 */       if (i1 == 89) {
/* 2931 */         consumeToken();
/* 2932 */         m = this.ts.tokenEnd;
/* 2933 */         if (k == 0) {
/* 2934 */           k = 1;
/*      */         } else {
/* 2936 */           localArrayList.add(new EmptyExpression(this.ts.tokenBeg, 1));
/* 2937 */           n++;
/*      */         }
/*      */       } else { if (i1 == 84) {
/* 2940 */           consumeToken();
/*      */ 
/* 2946 */           j = this.ts.tokenEnd;
/* 2947 */           localArrayLiteral.setDestructuringLength(localArrayList.size() + (k != 0 ? 1 : 0));
/*      */ 
/* 2949 */           localArrayLiteral.setSkipCount(n);
/* 2950 */           if (m == -1) break;
/* 2951 */           warnTrailingComma("msg.array.trailing.comma", i, localArrayList, m); break;
/*      */         }
/*      */ 
/* 2954 */         if ((i1 == 119) && (k == 0) && (localArrayList.size() == 1))
/*      */         {
/* 2956 */           return arrayComprehension((AstNode)localArrayList.get(0), i);
/* 2957 */         }if (i1 == 0) {
/* 2958 */           reportError("msg.no.bracket.arg");
/*      */         } else {
/* 2960 */           if (k == 0) {
/* 2961 */             reportError("msg.no.bracket.arg");
/*      */           }
/* 2963 */           localArrayList.add(assignExpr());
/* 2964 */           k = 0;
/* 2965 */           m = -1;
/*      */         } }
/*      */     }
/* 2968 */     for (AstNode localAstNode : localArrayList) {
/* 2969 */       localArrayLiteral.addElement(localAstNode);
/*      */     }
/* 2971 */     localArrayLiteral.setLength(j - i);
/* 2972 */     return localArrayLiteral;
/*      */   }
/*      */ 
/*      */   private AstNode arrayComprehension(AstNode paramAstNode, int paramInt)
/*      */     throws IOException
/*      */   {
/* 2984 */     ArrayList localArrayList = new ArrayList();
/*      */ 
/* 2986 */     while (peekToken() == 119) {
/* 2987 */       localArrayList.add(arrayComprehensionLoop());
/*      */     }
/* 2989 */     int i = -1;
/* 2990 */     ConditionData localConditionData = null;
/* 2991 */     if (peekToken() == 112) {
/* 2992 */       consumeToken();
/* 2993 */       i = this.ts.tokenBeg - paramInt;
/* 2994 */       localConditionData = condition();
/*      */     }
/* 2996 */     mustMatchToken(84, "msg.no.bracket.arg");
/* 2997 */     ArrayComprehension localArrayComprehension = new ArrayComprehension(paramInt, this.ts.tokenEnd - paramInt);
/* 2998 */     localArrayComprehension.setResult(paramAstNode);
/* 2999 */     localArrayComprehension.setLoops(localArrayList);
/* 3000 */     if (localConditionData != null) {
/* 3001 */       localArrayComprehension.setIfPosition(i);
/* 3002 */       localArrayComprehension.setFilter(localConditionData.condition);
/* 3003 */       localArrayComprehension.setFilterLp(localConditionData.lp - paramInt);
/* 3004 */       localArrayComprehension.setFilterRp(localConditionData.rp - paramInt);
/*      */     }
/* 3006 */     return localArrayComprehension;
/*      */   }
/*      */ 
/*      */   private ArrayComprehensionLoop arrayComprehensionLoop()
/*      */     throws IOException
/*      */   {
/* 3012 */     if (nextToken() != 119) codeBug();
/* 3013 */     int i = this.ts.tokenBeg;
/* 3014 */     int j = -1; int k = -1; int m = -1; int n = -1;
/* 3015 */     ArrayComprehensionLoop localArrayComprehensionLoop1 = new ArrayComprehensionLoop(i);
/*      */ 
/* 3017 */     pushScope(localArrayComprehensionLoop1);
/*      */     try {
/* 3019 */       if (matchToken(39)) {
/* 3020 */         if (this.ts.getString().equals("each"))
/* 3021 */           j = this.ts.tokenBeg - i;
/*      */         else {
/* 3023 */           reportError("msg.no.paren.for");
/*      */         }
/*      */       }
/* 3026 */       if (mustMatchToken(87, "msg.no.paren.for")) {
/* 3027 */         k = this.ts.tokenBeg - i;
/*      */       }
/*      */ 
/* 3030 */       Object localObject1 = null;
/* 3031 */       switch (peekToken())
/*      */       {
/*      */       case 83:
/*      */       case 85:
/* 3035 */         localObject1 = destructuringPrimaryExpr();
/* 3036 */         markDestructuring((AstNode)localObject1);
/* 3037 */         break;
/*      */       case 39:
/* 3039 */         consumeToken();
/* 3040 */         localObject1 = createNameNode();
/* 3041 */         break;
/*      */       default:
/* 3043 */         reportError("msg.bad.var");
/*      */       }
/*      */ 
/* 3048 */       if (((AstNode)localObject1).getType() == 39) {
/* 3049 */         defineSymbol(153, this.ts.getString(), true);
/*      */       }
/*      */ 
/* 3052 */       if (mustMatchToken(52, "msg.in.after.for.name"))
/* 3053 */         n = this.ts.tokenBeg - i;
/* 3054 */       AstNode localAstNode = expr();
/* 3055 */       if (mustMatchToken(88, "msg.no.paren.for.ctrl")) {
/* 3056 */         m = this.ts.tokenBeg - i;
/*      */       }
/* 3058 */       localArrayComprehensionLoop1.setLength(this.ts.tokenEnd - i);
/* 3059 */       localArrayComprehensionLoop1.setIterator((AstNode)localObject1);
/* 3060 */       localArrayComprehensionLoop1.setIteratedObject(localAstNode);
/* 3061 */       localArrayComprehensionLoop1.setInPosition(n);
/* 3062 */       localArrayComprehensionLoop1.setEachPosition(j);
/* 3063 */       localArrayComprehensionLoop1.setIsForEach(j != -1);
/* 3064 */       localArrayComprehensionLoop1.setParens(k, m);
/* 3065 */       return localArrayComprehensionLoop1;
/*      */     } finally {
/* 3067 */       popScope();
/*      */     }
/*      */   }
/*      */ 
/*      */   private ObjectLiteral objectLiteral()
/*      */     throws IOException
/*      */   {
/* 3074 */     int i = this.ts.tokenBeg; int j = this.ts.lineno;
/* 3075 */     int k = -1;
/* 3076 */     ArrayList localArrayList = new ArrayList();
/* 3077 */     HashSet localHashSet = new HashSet();
/*      */     while (true)
/*      */     {
/* 3081 */       localObject1 = null;
/* 3082 */       int m = peekToken();
/* 3083 */       String str = getAndResetJsDoc();
/*      */       Object localObject2;
/* 3084 */       switch (m) {
/*      */       case 39:
/*      */       case 41:
/* 3087 */         k = -1;
/* 3088 */         saveNameTokenData(this.ts.tokenBeg, this.ts.getString(), this.ts.lineno);
/* 3089 */         consumeToken();
/* 3090 */         StringLiteral localStringLiteral = null;
/* 3091 */         if (m == 41) {
/* 3092 */           localStringLiteral = createStringLiteral();
/*      */         }
/* 3094 */         Name localName = createNameNode();
/* 3095 */         localObject1 = this.ts.getString();
/* 3096 */         int n = this.ts.tokenBeg;
/*      */ 
/* 3098 */         if ((m == 39) && (peekToken() == 39) && (("get".equals(localObject1)) || ("set".equals(localObject1))))
/*      */         {
/* 3102 */           consumeToken();
/* 3103 */           localName = createNameNode();
/* 3104 */           localName.setJsDoc(str);
/* 3105 */           localObject2 = getterSetterProperty(n, localName, "get".equals(localObject1));
/*      */ 
/* 3107 */           localArrayList.add(localObject2);
/* 3108 */           localObject1 = ((ObjectProperty)localObject2).getLeft().getString();
/*      */         } else {
/* 3110 */           localObject2 = localStringLiteral != null ? localStringLiteral : localName;
/* 3111 */           ((AstNode)localObject2).setJsDoc(str);
/* 3112 */           localArrayList.add(plainProperty((AstNode)localObject2, m));
/*      */         }
/* 3114 */         break;
/*      */       case 40:
/* 3117 */         consumeToken();
/* 3118 */         k = -1;
/* 3119 */         localObject2 = new NumberLiteral(this.ts.tokenBeg, this.ts.getString(), this.ts.getNumber());
/*      */ 
/* 3122 */         ((AstNode)localObject2).setJsDoc(str);
/* 3123 */         localObject1 = this.ts.getString();
/* 3124 */         localArrayList.add(plainProperty((AstNode)localObject2, m));
/* 3125 */         break;
/*      */       case 86:
/* 3128 */         if ((k == -1) || (!this.compilerEnv.getWarnTrailingComma())) break label476;
/* 3129 */         warnTrailingComma("msg.extra.trailing.comma", i, localArrayList, k); break;
/*      */       default:
/* 3134 */         reportError("msg.bad.prop");
/*      */       }
/*      */ 
/* 3138 */       if (this.inUseStrictDirective) {
/* 3139 */         if (localHashSet.contains(localObject1)) {
/* 3140 */           addError("msg.dup.obj.lit.prop.strict", (String)localObject1);
/*      */         }
/* 3142 */         localHashSet.add(localObject1);
/*      */       }
/*      */ 
/* 3146 */       getAndResetJsDoc();
/* 3147 */       str = null;
/*      */ 
/* 3149 */       if (!matchToken(89)) break;
/* 3150 */       k = this.ts.tokenEnd;
/*      */     }
/*      */ 
/* 3156 */     label476: mustMatchToken(86, "msg.no.brace.prop");
/* 3157 */     Object localObject1 = new ObjectLiteral(i, this.ts.tokenEnd - i);
/* 3158 */     ((ObjectLiteral)localObject1).setElements(localArrayList);
/* 3159 */     ((ObjectLiteral)localObject1).setLineno(j);
/* 3160 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private ObjectProperty plainProperty(AstNode paramAstNode, int paramInt)
/*      */     throws IOException
/*      */   {
/* 3168 */     int i = peekToken();
/* 3169 */     if (((i == 89) || (i == 86)) && (paramInt == 39) && (this.compilerEnv.getLanguageVersion() >= 180))
/*      */     {
/* 3171 */       if (!this.inDestructuringAssignment) {
/* 3172 */         reportError("msg.bad.object.init");
/*      */       }
/* 3174 */       localObject = new Name(paramAstNode.getPosition(), paramAstNode.getString());
/* 3175 */       ObjectProperty localObjectProperty = new ObjectProperty();
/* 3176 */       localObjectProperty.putProp(26, Boolean.TRUE);
/* 3177 */       localObjectProperty.setLeftAndRight(paramAstNode, (AstNode)localObject);
/* 3178 */       return localObjectProperty;
/*      */     }
/* 3180 */     mustMatchToken(103, "msg.no.colon.prop");
/* 3181 */     Object localObject = new ObjectProperty();
/* 3182 */     ((ObjectProperty)localObject).setOperatorPosition(this.ts.tokenBeg);
/* 3183 */     ((ObjectProperty)localObject).setLeftAndRight(paramAstNode, assignExpr());
/* 3184 */     return localObject;
/*      */   }
/*      */ 
/*      */   private ObjectProperty getterSetterProperty(int paramInt, AstNode paramAstNode, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 3191 */     FunctionNode localFunctionNode = function(2);
/*      */ 
/* 3193 */     Name localName = localFunctionNode.getFunctionName();
/* 3194 */     if ((localName != null) && (localName.length() != 0)) {
/* 3195 */       reportError("msg.bad.prop");
/*      */     }
/* 3197 */     ObjectProperty localObjectProperty = new ObjectProperty(paramInt);
/* 3198 */     if (paramBoolean)
/* 3199 */       localObjectProperty.setIsGetter();
/*      */     else {
/* 3201 */       localObjectProperty.setIsSetter();
/*      */     }
/* 3203 */     int i = getNodeEnd(localFunctionNode);
/* 3204 */     localObjectProperty.setLeft(paramAstNode);
/* 3205 */     localObjectProperty.setRight(localFunctionNode);
/* 3206 */     localObjectProperty.setLength(i - paramInt);
/* 3207 */     return localObjectProperty;
/*      */   }
/*      */ 
/*      */   private Name createNameNode() {
/* 3211 */     return createNameNode(false, 39);
/*      */   }
/*      */ 
/*      */   private Name createNameNode(boolean paramBoolean, int paramInt)
/*      */   {
/* 3222 */     int i = this.ts.tokenBeg;
/* 3223 */     String str = this.ts.getString();
/* 3224 */     int j = this.ts.lineno;
/* 3225 */     if (!"".equals(this.prevNameTokenString)) {
/* 3226 */       i = this.prevNameTokenStart;
/* 3227 */       str = this.prevNameTokenString;
/* 3228 */       j = this.prevNameTokenLineno;
/* 3229 */       this.prevNameTokenStart = 0;
/* 3230 */       this.prevNameTokenString = "";
/* 3231 */       this.prevNameTokenLineno = 0;
/*      */     }
/* 3233 */     if (str == null) {
/* 3234 */       if (this.compilerEnv.isIdeMode())
/* 3235 */         str = "";
/*      */       else {
/* 3237 */         codeBug();
/*      */       }
/*      */     }
/* 3240 */     Name localName = new Name(i, str);
/* 3241 */     localName.setLineno(j);
/* 3242 */     if (paramBoolean) {
/* 3243 */       checkActivationName(str, paramInt);
/*      */     }
/* 3245 */     return localName;
/*      */   }
/*      */ 
/*      */   private StringLiteral createStringLiteral() {
/* 3249 */     int i = this.ts.tokenBeg; int j = this.ts.tokenEnd;
/* 3250 */     StringLiteral localStringLiteral = new StringLiteral(i, j - i);
/* 3251 */     localStringLiteral.setLineno(this.ts.lineno);
/* 3252 */     localStringLiteral.setValue(this.ts.getString());
/* 3253 */     localStringLiteral.setQuoteCharacter(this.ts.getQuoteChar());
/* 3254 */     return localStringLiteral;
/*      */   }
/*      */ 
/*      */   protected void checkActivationName(String paramString, int paramInt) {
/* 3258 */     if (!insideFunction()) {
/* 3259 */       return;
/*      */     }
/* 3261 */     int i = 0;
/* 3262 */     if (("arguments".equals(paramString)) || ((this.compilerEnv.getActivationNames() != null) && (this.compilerEnv.getActivationNames().contains(paramString))))
/*      */     {
/* 3266 */       i = 1;
/* 3267 */     } else if (("length".equals(paramString)) && 
/* 3268 */       (paramInt == 33) && (this.compilerEnv.getLanguageVersion() == 120))
/*      */     {
/* 3272 */       i = 1;
/*      */     }
/*      */ 
/* 3275 */     if (i != 0)
/* 3276 */       setRequiresActivation();
/*      */   }
/*      */ 
/*      */   protected void setRequiresActivation()
/*      */   {
/* 3281 */     if (insideFunction())
/* 3282 */       ((FunctionNode)this.currentScriptOrFn).setRequiresActivation();
/*      */   }
/*      */ 
/*      */   private void checkCallRequiresActivation(AstNode paramAstNode)
/*      */   {
/* 3287 */     if (((paramAstNode.getType() == 39) && ("eval".equals(((Name)paramAstNode).getIdentifier()))) || ((paramAstNode.getType() == 33) && ("eval".equals(((PropertyGet)paramAstNode).getProperty().getIdentifier()))))
/*      */     {
/* 3291 */       setRequiresActivation();
/*      */     }
/*      */   }
/*      */ 
/* 3295 */   protected void setIsGenerator() { if (insideFunction())
/* 3296 */       ((FunctionNode)this.currentScriptOrFn).setIsGenerator();
/*      */   }
/*      */ 
/*      */   private void checkBadIncDec(UnaryExpression paramUnaryExpression)
/*      */   {
/* 3301 */     AstNode localAstNode = removeParens(paramUnaryExpression.getOperand());
/* 3302 */     int i = localAstNode.getType();
/* 3303 */     if ((i != 39) && (i != 33) && (i != 36) && (i != 67) && (i != 38))
/*      */     {
/* 3308 */       reportError(paramUnaryExpression.getType() == 106 ? "msg.bad.incr" : "msg.bad.decr");
/*      */     }
/*      */   }
/*      */ 
/*      */   private ErrorNode makeErrorNode()
/*      */   {
/* 3314 */     ErrorNode localErrorNode = new ErrorNode(this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
/* 3315 */     localErrorNode.setLineno(this.ts.lineno);
/* 3316 */     return localErrorNode;
/*      */   }
/*      */ 
/*      */   private int nodeEnd(AstNode paramAstNode)
/*      */   {
/* 3321 */     return paramAstNode.getPosition() + paramAstNode.getLength();
/*      */   }
/*      */ 
/*      */   private void saveNameTokenData(int paramInt1, String paramString, int paramInt2) {
/* 3325 */     this.prevNameTokenStart = paramInt1;
/* 3326 */     this.prevNameTokenString = paramString;
/* 3327 */     this.prevNameTokenLineno = paramInt2;
/*      */   }
/*      */ 
/*      */   private int lineBeginningFor(int paramInt)
/*      */   {
/* 3344 */     if (this.sourceChars == null) {
/* 3345 */       return -1;
/*      */     }
/* 3347 */     if (paramInt <= 0) {
/* 3348 */       return 0;
/*      */     }
/* 3350 */     char[] arrayOfChar = this.sourceChars;
/* 3351 */     if (paramInt >= arrayOfChar.length)
/* 3352 */       paramInt = arrayOfChar.length - 1;
/*      */     while (true) {
/* 3354 */       paramInt--; if (paramInt < 0) break;
/* 3355 */       int i = arrayOfChar[paramInt];
/* 3356 */       if ((i == 10) || (i == 13)) {
/* 3357 */         return paramInt + 1;
/*      */       }
/*      */     }
/* 3360 */     return 0;
/*      */   }
/*      */ 
/*      */   private void warnMissingSemi(int paramInt1, int paramInt2)
/*      */   {
/* 3367 */     if (this.compilerEnv.isStrictMode()) {
/* 3368 */       int i = Math.max(paramInt1, lineBeginningFor(paramInt2));
/* 3369 */       if (paramInt2 == -1)
/* 3370 */         paramInt2 = this.ts.cursor;
/* 3371 */       addStrictWarning("msg.missing.semi", "", i, paramInt2 - i);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void warnTrailingComma(String paramString, int paramInt1, List<?> paramList, int paramInt2)
/*      */   {
/* 3378 */     if (this.compilerEnv.getWarnTrailingComma())
/*      */     {
/* 3380 */       if (!paramList.isEmpty()) {
/* 3381 */         paramInt1 = ((AstNode)paramList.get(0)).getPosition();
/*      */       }
/* 3383 */       paramInt1 = Math.max(paramInt1, lineBeginningFor(paramInt2));
/* 3384 */       addWarning("msg.extra.trailing.comma", paramInt1, paramInt2 - paramInt1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private String readFully(Reader paramReader) throws IOException
/*      */   {
/* 3390 */     BufferedReader localBufferedReader = new BufferedReader(paramReader);
/*      */     try {
/* 3392 */       char[] arrayOfChar = new char[1024];
/* 3393 */       StringBuilder localStringBuilder = new StringBuilder(1024);
/*      */       int i;
/* 3395 */       while ((i = localBufferedReader.read(arrayOfChar, 0, 1024)) != -1) {
/* 3396 */         localStringBuilder.append(arrayOfChar, 0, i);
/*      */       }
/* 3398 */       return localStringBuilder.toString();
/*      */     } finally {
/* 3400 */       localBufferedReader.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   Node createDestructuringAssignment(int paramInt, Node paramNode1, Node paramNode2)
/*      */   {
/* 3468 */     String str = this.currentScriptOrFn.getNextTempName();
/* 3469 */     Node localNode1 = destructuringAssignmentHelper(paramInt, paramNode1, paramNode2, str);
/*      */ 
/* 3471 */     Node localNode2 = localNode1.getLastChild();
/* 3472 */     localNode2.addChildToBack(createName(str));
/* 3473 */     return localNode1;
/*      */   }
/*      */ 
/*      */   Node destructuringAssignmentHelper(int paramInt, Node paramNode1, Node paramNode2, String paramString)
/*      */   {
/* 3479 */     Scope localScope = createScopeNode(158, paramNode1.getLineno());
/* 3480 */     localScope.addChildToFront(new Node(153, createName(39, paramString, paramNode2)));
/*      */     try
/*      */     {
/* 3483 */       pushScope(localScope);
/* 3484 */       defineSymbol(153, paramString, true);
/*      */     } finally {
/* 3486 */       popScope();
/*      */     }
/* 3488 */     Node localNode = new Node(89);
/* 3489 */     localScope.addChildToBack(localNode);
/* 3490 */     ArrayList localArrayList = new ArrayList();
/* 3491 */     boolean bool = true;
/* 3492 */     switch (paramNode1.getType()) {
/*      */     case 65:
/* 3494 */       bool = destructuringArray((ArrayLiteral)paramNode1, paramInt, paramString, localNode, localArrayList);
/*      */ 
/* 3497 */       break;
/*      */     case 66:
/* 3499 */       bool = destructuringObject((ObjectLiteral)paramNode1, paramInt, paramString, localNode, localArrayList);
/*      */ 
/* 3502 */       break;
/*      */     case 33:
/*      */     case 36:
/* 3505 */       localNode.addChildToBack(simpleAssignment(paramNode1, createName(paramString)));
/* 3506 */       break;
/*      */     default:
/* 3508 */       reportError("msg.bad.assign.left");
/*      */     }
/* 3510 */     if (bool)
/*      */     {
/* 3512 */       localNode.addChildToBack(createNumber(0.0D));
/*      */     }
/* 3514 */     localScope.putProp(22, localArrayList);
/* 3515 */     return localScope;
/*      */   }
/*      */ 
/*      */   boolean destructuringArray(ArrayLiteral paramArrayLiteral, int paramInt, String paramString, Node paramNode, List<String> paramList)
/*      */   {
/* 3524 */     boolean bool = true;
/* 3525 */     int i = paramInt == 154 ? 155 : 8;
/*      */ 
/* 3527 */     int j = 0;
/* 3528 */     for (AstNode localAstNode : paramArrayLiteral.getElements())
/* 3529 */       if (localAstNode.getType() == 128) {
/* 3530 */         j++;
/*      */       }
/*      */       else {
/* 3533 */         Node localNode = new Node(36, createName(paramString), createNumber(j));
/*      */ 
/* 3536 */         if (localAstNode.getType() == 39) {
/* 3537 */           String str = localAstNode.getString();
/* 3538 */           paramNode.addChildToBack(new Node(i, createName(49, str, null), localNode));
/*      */ 
/* 3542 */           if (paramInt != -1) {
/* 3543 */             defineSymbol(paramInt, str, true);
/* 3544 */             paramList.add(str);
/*      */           }
/*      */         } else {
/* 3547 */           paramNode.addChildToBack(destructuringAssignmentHelper(paramInt, localAstNode, localNode, this.currentScriptOrFn.getNextTempName()));
/*      */         }
/*      */ 
/* 3553 */         j++;
/* 3554 */         bool = false;
/*      */       }
/* 3556 */     return bool;
/*      */   }
/*      */ 
/*      */   boolean destructuringObject(ObjectLiteral paramObjectLiteral, int paramInt, String paramString, Node paramNode, List<String> paramList)
/*      */   {
/* 3565 */     boolean bool = true;
/* 3566 */     int i = paramInt == 154 ? 155 : 8;
/*      */ 
/* 3569 */     for (ObjectProperty localObjectProperty : paramObjectLiteral.getElements()) {
/* 3570 */       int j = 0;
/*      */ 
/* 3574 */       if (this.ts != null) {
/* 3575 */         j = this.ts.lineno;
/*      */       }
/* 3577 */       AstNode localAstNode = localObjectProperty.getLeft();
/* 3578 */       Node localNode = null;
/* 3579 */       if ((localAstNode instanceof Name)) {
/* 3580 */         localObject = Node.newString(((Name)localAstNode).getIdentifier());
/* 3581 */         localNode = new Node(33, createName(paramString), (Node)localObject);
/* 3582 */       } else if ((localAstNode instanceof StringLiteral)) {
/* 3583 */         localObject = Node.newString(((StringLiteral)localAstNode).getValue());
/* 3584 */         localNode = new Node(33, createName(paramString), (Node)localObject);
/* 3585 */       } else if ((localAstNode instanceof NumberLiteral)) {
/* 3586 */         localObject = createNumber((int)((NumberLiteral)localAstNode).getNumber());
/* 3587 */         localNode = new Node(36, createName(paramString), (Node)localObject);
/*      */       } else {
/* 3589 */         throw codeBug();
/*      */       }
/* 3591 */       localNode.setLineno(j);
/* 3592 */       Object localObject = localObjectProperty.getRight();
/* 3593 */       if (((AstNode)localObject).getType() == 39) {
/* 3594 */         String str = ((Name)localObject).getIdentifier();
/* 3595 */         paramNode.addChildToBack(new Node(i, createName(49, str, null), localNode));
/*      */ 
/* 3599 */         if (paramInt != -1) {
/* 3600 */           defineSymbol(paramInt, str, true);
/* 3601 */           paramList.add(str);
/*      */         }
/*      */       } else {
/* 3604 */         paramNode.addChildToBack(destructuringAssignmentHelper(paramInt, (Node)localObject, localNode, this.currentScriptOrFn.getNextTempName()));
/*      */       }
/*      */ 
/* 3609 */       bool = false;
/*      */     }
/* 3611 */     return bool;
/*      */   }
/*      */ 
/*      */   protected Node createName(String paramString) {
/* 3615 */     checkActivationName(paramString, 39);
/* 3616 */     return Node.newString(39, paramString);
/*      */   }
/*      */ 
/*      */   protected Node createName(int paramInt, String paramString, Node paramNode) {
/* 3620 */     Node localNode = createName(paramString);
/* 3621 */     localNode.setType(paramInt);
/* 3622 */     if (paramNode != null)
/* 3623 */       localNode.addChildToBack(paramNode);
/* 3624 */     return localNode;
/*      */   }
/*      */ 
/*      */   protected Node createNumber(double paramDouble) {
/* 3628 */     return Node.newNumber(paramDouble);
/*      */   }
/*      */ 
/*      */   protected Scope createScopeNode(int paramInt1, int paramInt2)
/*      */   {
/* 3640 */     Scope localScope = new Scope();
/* 3641 */     localScope.setType(paramInt1);
/* 3642 */     localScope.setLineno(paramInt2);
/* 3643 */     return localScope;
/*      */   }
/*      */ 
/*      */   protected Node simpleAssignment(Node paramNode1, Node paramNode2)
/*      */   {
/* 3669 */     int i = paramNode1.getType();
/*      */     Object localObject1;
/* 3670 */     switch (i) {
/*      */     case 39:
/* 3672 */       if ((this.inUseStrictDirective) && ("eval".equals(((Name)paramNode1).getIdentifier())))
/*      */       {
/* 3675 */         reportError("msg.bad.id.strict", ((Name)paramNode1).getIdentifier());
/*      */       }
/*      */ 
/* 3678 */       paramNode1.setType(49);
/* 3679 */       return new Node(8, paramNode1, paramNode2);
/*      */     case 33:
/*      */     case 36:
/*      */       Object localObject2;
/* 3688 */       if ((paramNode1 instanceof PropertyGet)) {
/* 3689 */         localObject1 = ((PropertyGet)paramNode1).getTarget();
/* 3690 */         localObject2 = ((PropertyGet)paramNode1).getProperty();
/* 3691 */       } else if ((paramNode1 instanceof ElementGet)) {
/* 3692 */         localObject1 = ((ElementGet)paramNode1).getTarget();
/* 3693 */         localObject2 = ((ElementGet)paramNode1).getElement();
/*      */       }
/*      */       else {
/* 3696 */         localObject1 = paramNode1.getFirstChild();
/* 3697 */         localObject2 = paramNode1.getLastChild();
/*      */       }
/*      */       int j;
/* 3700 */       if (i == 33) {
/* 3701 */         j = 35;
/*      */ 
/* 3707 */         ((Node)localObject2).setType(41);
/*      */       } else {
/* 3709 */         j = 37;
/*      */       }
/* 3711 */       return new Node(j, (Node)localObject1, (Node)localObject2, paramNode2);
/*      */     case 67:
/* 3714 */       localObject1 = paramNode1.getFirstChild();
/* 3715 */       checkMutableReference((Node)localObject1);
/* 3716 */       return new Node(68, (Node)localObject1, paramNode2);
/*      */     }
/*      */ 
/* 3720 */     throw codeBug();
/*      */   }
/*      */ 
/*      */   protected void checkMutableReference(Node paramNode) {
/* 3724 */     int i = paramNode.getIntProp(16, 0);
/* 3725 */     if ((i & 0x4) != 0)
/* 3726 */       reportError("msg.bad.assign.left");
/*      */   }
/*      */ 
/*      */   protected AstNode removeParens(AstNode paramAstNode)
/*      */   {
/* 3732 */     while ((paramAstNode instanceof ParenthesizedExpression)) {
/* 3733 */       paramAstNode = ((ParenthesizedExpression)paramAstNode).getExpression();
/*      */     }
/* 3735 */     return paramAstNode;
/*      */   }
/*      */ 
/*      */   void markDestructuring(AstNode paramAstNode) {
/* 3739 */     if ((paramAstNode instanceof DestructuringForm))
/* 3740 */       ((DestructuringForm)paramAstNode).setIsDestructuring(true);
/* 3741 */     else if ((paramAstNode instanceof ParenthesizedExpression))
/* 3742 */       markDestructuring(((ParenthesizedExpression)paramAstNode).getExpression());
/*      */   }
/*      */ 
/*      */   private RuntimeException codeBug()
/*      */     throws RuntimeException
/*      */   {
/* 3750 */     throw Kit.codeBug("ts.cursor=" + this.ts.cursor + ", ts.tokenBeg=" + this.ts.tokenBeg + ", currentToken=" + this.currentToken);
/*      */   }
/*      */ 
/*      */   private static class ConditionData
/*      */   {
/*      */     AstNode condition;
/*  911 */     int lp = -1;
/*  912 */     int rp = -1;
/*      */   }
/*      */ 
/*      */   private static class ParserException extends RuntimeException
/*      */   {
/*      */     static final long serialVersionUID = 5882582646773765630L;
/*      */   }
/*      */ 
/*      */   protected class PerFunctionVariables
/*      */   {
/* 3417 */     private ScriptNode savedCurrentScriptOrFn = Parser.this.currentScriptOrFn;
/*      */     private Scope savedCurrentScope;
/*      */     private int savedNestingOfWith;
/*      */     private int savedEndFlags;
/*      */     private boolean savedInForInit;
/*      */     private Map<String, LabeledStatement> savedLabelSet;
/*      */     private List<Loop> savedLoopSet;
/*      */     private List<Jump> savedLoopAndSwitchSet;
/*      */ 
/*      */     PerFunctionVariables(FunctionNode arg2)
/*      */     {
/*      */       Object localObject;
/* 3418 */       Parser.this.currentScriptOrFn = localObject;
/*      */ 
/* 3420 */       this.savedCurrentScope = Parser.this.currentScope;
/* 3421 */       Parser.this.currentScope = localObject;
/*      */ 
/* 3423 */       this.savedNestingOfWith = Parser.this.nestingOfWith;
/* 3424 */       Parser.this.nestingOfWith = 0;
/*      */ 
/* 3426 */       this.savedLabelSet = Parser.this.labelSet;
/* 3427 */       Parser.this.labelSet = null;
/*      */ 
/* 3429 */       this.savedLoopSet = Parser.this.loopSet;
/* 3430 */       Parser.this.loopSet = null;
/*      */ 
/* 3432 */       this.savedLoopAndSwitchSet = Parser.this.loopAndSwitchSet;
/* 3433 */       Parser.this.loopAndSwitchSet = null;
/*      */ 
/* 3435 */       this.savedEndFlags = Parser.this.endFlags;
/* 3436 */       Parser.this.endFlags = 0;
/*      */ 
/* 3438 */       this.savedInForInit = Parser.this.inForInit;
/* 3439 */       Parser.this.inForInit = false;
/*      */     }
/*      */ 
/*      */     void restore() {
/* 3443 */       Parser.this.currentScriptOrFn = this.savedCurrentScriptOrFn;
/* 3444 */       Parser.this.currentScope = this.savedCurrentScope;
/* 3445 */       Parser.this.nestingOfWith = this.savedNestingOfWith;
/* 3446 */       Parser.this.labelSet = this.savedLabelSet;
/* 3447 */       Parser.this.loopSet = this.savedLoopSet;
/* 3448 */       Parser.this.loopAndSwitchSet = this.savedLoopAndSwitchSet;
/* 3449 */       Parser.this.endFlags = this.savedEndFlags;
/* 3450 */       Parser.this.inForInit = this.savedInForInit;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.Parser
 * JD-Core Version:    0.6.2
 */