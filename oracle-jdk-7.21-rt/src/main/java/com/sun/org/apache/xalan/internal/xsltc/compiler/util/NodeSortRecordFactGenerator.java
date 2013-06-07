/*    */ package com.sun.org.apache.xalan.internal.xsltc.compiler.util;
/*    */ 
/*    */ import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
/*    */ 
/*    */ public final class NodeSortRecordFactGenerator extends ClassGenerator
/*    */ {
/*    */   public NodeSortRecordFactGenerator(String className, String superClassName, String fileName, int accessFlags, String[] interfaces, Stylesheet stylesheet)
/*    */   {
/* 38 */     super(className, superClassName, fileName, accessFlags, interfaces, stylesheet);
/*    */   }
/*    */ 
/*    */   public boolean isExternal()
/*    */   {
/* 47 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSortRecordFactGenerator
 * JD-Core Version:    0.6.2
 */