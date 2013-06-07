/*     */ package com.sun.org.apache.xerces.internal.parsers;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*     */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*     */ 
/*     */ public class SAXParser extends AbstractSAXParser
/*     */ {
/*     */   protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
/*     */   protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
/*  55 */   private static final String[] RECOGNIZED_FEATURES = { "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace" };
/*     */   protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
/*     */   protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
/*  71 */   private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool" };
/*     */ 
/*     */   public SAXParser(XMLParserConfiguration config)
/*     */   {
/*  84 */     super(config);
/*     */   }
/*     */ 
/*     */   public SAXParser()
/*     */   {
/*  91 */     this(null, null);
/*     */   }
/*     */ 
/*     */   public SAXParser(SymbolTable symbolTable)
/*     */   {
/*  98 */     this(symbolTable, null);
/*     */   }
/*     */ 
/*     */   public SAXParser(SymbolTable symbolTable, XMLGrammarPool grammarPool)
/*     */   {
/* 106 */     super(new XIncludeAwareParserConfiguration());
/*     */ 
/* 109 */     this.fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
/* 110 */     this.fConfiguration.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
/*     */ 
/* 113 */     this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
/* 114 */     if (symbolTable != null) {
/* 115 */       this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
/*     */     }
/* 117 */     if (grammarPool != null)
/* 118 */       this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", grammarPool);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.parsers.SAXParser
 * JD-Core Version:    0.6.2
 */