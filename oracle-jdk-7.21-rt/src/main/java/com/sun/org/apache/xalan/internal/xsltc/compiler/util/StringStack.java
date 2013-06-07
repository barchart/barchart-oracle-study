/*    */ package com.sun.org.apache.xalan.internal.xsltc.compiler.util;
/*    */ 
/*    */ import java.util.Stack;
/*    */ 
/*    */ public final class StringStack extends Stack
/*    */ {
/*    */   static final long serialVersionUID = -1506910875640317898L;
/*    */ 
/*    */   public String peekString()
/*    */   {
/* 35 */     return (String)super.peek();
/*    */   }
/*    */ 
/*    */   public String popString() {
/* 39 */     return (String)super.pop();
/*    */   }
/*    */ 
/*    */   public String pushString(String val) {
/* 43 */     return (String)super.push(val);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack
 * JD-Core Version:    0.6.2
 */