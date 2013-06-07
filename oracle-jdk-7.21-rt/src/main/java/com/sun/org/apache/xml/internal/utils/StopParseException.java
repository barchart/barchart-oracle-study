/*    */ package com.sun.org.apache.xml.internal.utils;
/*    */ 
/*    */ import org.xml.sax.SAXException;
/*    */ 
/*    */ public class StopParseException extends SAXException
/*    */ {
/*    */   static final long serialVersionUID = 210102479218258961L;
/*    */ 
/*    */   StopParseException()
/*    */   {
/* 40 */     super("Stylesheet PIs found, stop the parse");
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.utils.StopParseException
 * JD-Core Version:    0.6.2
 */