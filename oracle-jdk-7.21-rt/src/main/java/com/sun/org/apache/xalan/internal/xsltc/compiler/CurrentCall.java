/*    */ package com.sun.org.apache.xalan.internal.xsltc.compiler;
/*    */ 
/*    */ import com.sun.org.apache.bcel.internal.generic.InstructionList;
/*    */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
/*    */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
/*    */ 
/*    */ final class CurrentCall extends FunctionCall
/*    */ {
/*    */   public CurrentCall(QName fname)
/*    */   {
/* 35 */     super(fname);
/*    */   }
/*    */ 
/*    */   public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
/* 39 */     methodGen.getInstructionList().append(methodGen.loadCurrentNode());
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.CurrentCall
 * JD-Core Version:    0.6.2
 */