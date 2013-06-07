/*    */ package com.sun.org.apache.xerces.internal.parsers;
/*    */ 
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
/*    */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*    */ import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
/*    */ 
/*    */ public abstract class XMLGrammarParser extends XMLParser
/*    */ {
/*    */   protected DTDDVFactory fDatatypeValidatorFactory;
/*    */ 
/*    */   protected XMLGrammarParser(SymbolTable symbolTable)
/*    */   {
/* 50 */     super(new XIncludeAwareParserConfiguration());
/* 51 */     this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.parsers.XMLGrammarParser
 * JD-Core Version:    0.6.2
 */