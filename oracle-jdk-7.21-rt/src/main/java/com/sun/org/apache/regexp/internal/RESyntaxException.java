/*    */ package com.sun.org.apache.regexp.internal;
/*    */ 
/*    */ public class RESyntaxException extends RuntimeException
/*    */ {
/*    */   public RESyntaxException(String s)
/*    */   {
/* 41 */     super("Syntax error: " + s);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.regexp.internal.RESyntaxException
 * JD-Core Version:    0.6.2
 */