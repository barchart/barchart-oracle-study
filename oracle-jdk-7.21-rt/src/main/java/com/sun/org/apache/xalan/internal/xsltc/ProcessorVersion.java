/*    */ package com.sun.org.apache.xalan.internal.xsltc;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class ProcessorVersion
/*    */ {
/* 43 */   private static int MAJOR = 1;
/* 44 */   private static int MINOR = 0;
/* 45 */   private static int DELTA = 0;
/*    */ 
/*    */   public static void main(String[] args) {
/* 48 */     System.out.println("XSLTC version " + MAJOR + "." + MINOR + (DELTA > 0 ? "." + DELTA : ""));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.ProcessorVersion
 * JD-Core Version:    0.6.2
 */