/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ class Printer
/*     */ {
/*     */   static final boolean err = false;
/*     */   static final boolean debug = false;
/*     */   static final boolean trace = false;
/*     */   static final boolean verbose = false;
/*     */   static final boolean release = false;
/*     */   static final boolean SHOW_THREADID = false;
/*     */   static final boolean SHOW_TIMESTAMP = false;
/* 101 */   private static long startTime = 0L;
/*     */ 
/*     */   public static void err(String paramString) {  } 
/*     */   public static void debug(String paramString) {  } 
/*     */   public static void trace(String paramString) {  } 
/*     */   public static void verbose(String paramString) {  } 
/*     */   public static void release(String paramString) {  } 
/* 104 */   public static void println(String paramString) { String str = "";
/*     */ 
/* 114 */     System.out.println(str + paramString); }
/*     */ 
/*     */   public static void println()
/*     */   {
/* 118 */     System.out.println();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.Printer
 * JD-Core Version:    0.6.2
 */