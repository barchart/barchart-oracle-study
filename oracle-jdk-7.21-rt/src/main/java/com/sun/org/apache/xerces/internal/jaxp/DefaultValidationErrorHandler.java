/*    */ package com.sun.org.apache.xerces.internal.jaxp;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.SAXParseException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ class DefaultValidationErrorHandler extends DefaultHandler
/*    */ {
/* 31 */   private static int ERROR_COUNT_LIMIT = 10;
/* 32 */   private int errorCount = 0;
/*    */ 
/*    */   public void error(SAXParseException e) throws SAXException
/*    */   {
/* 36 */     if (this.errorCount >= ERROR_COUNT_LIMIT)
/*    */     {
/* 38 */       return;
/* 39 */     }if (this.errorCount == 0)
/*    */     {
/* 41 */       System.err.println("Warning: validation was turned on but an org.xml.sax.ErrorHandler was not");
/* 42 */       System.err.println("set, which is probably not what is desired.  Parser will use a default");
/* 43 */       System.err.println("ErrorHandler to print the first " + ERROR_COUNT_LIMIT + " errors.  Please call");
/*    */ 
/* 45 */       System.err.println("the 'setErrorHandler' method to fix this.");
/*    */     }
/*    */ 
/* 48 */     String systemId = e.getSystemId();
/* 49 */     if (systemId == null) {
/* 50 */       systemId = "null";
/*    */     }
/* 52 */     String message = "Error: URI=" + systemId + " Line=" + e.getLineNumber() + ": " + e.getMessage();
/*    */ 
/* 55 */     System.err.println(message);
/* 56 */     this.errorCount += 1;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.jaxp.DefaultValidationErrorHandler
 * JD-Core Version:    0.6.2
 */