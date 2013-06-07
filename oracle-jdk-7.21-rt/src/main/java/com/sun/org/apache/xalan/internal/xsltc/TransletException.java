/*    */ package com.sun.org.apache.xalan.internal.xsltc;
/*    */ 
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public final class TransletException extends SAXException
/*    */ {
/*    */   static final long serialVersionUID = -878916829521217293L;
/*    */ 
/*    */   public TransletException()
/*    */   {
/* 37 */     super("Translet error");
/*    */   }
/*    */ 
/*    */   public TransletException(Exception e) {
/* 41 */     super(e.toString());
/* 42 */     initCause(e);
/*    */   }
/*    */ 
/*    */   public TransletException(String message) {
/* 46 */     super(message);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.TransletException
 * JD-Core Version:    0.6.2
 */