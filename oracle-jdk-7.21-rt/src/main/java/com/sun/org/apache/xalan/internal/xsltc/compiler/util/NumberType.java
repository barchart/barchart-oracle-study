/*    */ package com.sun.org.apache.xalan.internal.xsltc.compiler.util;
/*    */ 
/*    */ public abstract class NumberType extends Type
/*    */ {
/*    */   public boolean isNumber()
/*    */   {
/* 33 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean isSimple() {
/* 37 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType
 * JD-Core Version:    0.6.2
 */