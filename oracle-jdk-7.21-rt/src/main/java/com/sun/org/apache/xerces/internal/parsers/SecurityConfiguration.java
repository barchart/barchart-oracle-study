/*     */ package com.sun.org.apache.xerces.internal.parsers;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*     */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*     */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
/*     */ import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
/*     */ 
/*     */ public class SecurityConfiguration extends XIncludeAwareParserConfiguration
/*     */ {
/*     */   protected static final String SECURITY_MANAGER_PROPERTY = "http://apache.org/xml/properties/security-manager";
/*     */ 
/*     */   public SecurityConfiguration()
/*     */   {
/*  64 */     this(null, null, null);
/*     */   }
/*     */ 
/*     */   public SecurityConfiguration(SymbolTable symbolTable)
/*     */   {
/*  73 */     this(symbolTable, null, null);
/*     */   }
/*     */ 
/*     */   public SecurityConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool)
/*     */   {
/*  89 */     this(symbolTable, grammarPool, null);
/*     */   }
/*     */ 
/*     */   public SecurityConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings)
/*     */   {
/* 107 */     super(symbolTable, grammarPool, parentSettings);
/*     */ 
/* 110 */     setProperty("http://apache.org/xml/properties/security-manager", new SecurityManager());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.parsers.SecurityConfiguration
 * JD-Core Version:    0.6.2
 */