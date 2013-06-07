/*    */ package com.sun.org.apache.xalan.internal.xsltc.compiler.util;
/*    */ 
/*    */ import com.sun.org.apache.bcel.internal.generic.Instruction;
/*    */ 
/*    */ class OutlineableChunkStart extends MarkerInstruction
/*    */ {
/* 46 */   public static final Instruction OUTLINEABLECHUNKSTART = new OutlineableChunkStart();
/*    */ 
/*    */   public String getName()
/*    */   {
/* 62 */     return OutlineableChunkStart.class.getName();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 70 */     return getName();
/*    */   }
/*    */ 
/*    */   public String toString(boolean verbose)
/*    */   {
/* 78 */     return getName();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.util.OutlineableChunkStart
 * JD-Core Version:    0.6.2
 */