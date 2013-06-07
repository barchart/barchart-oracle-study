/*     */ package com.sun.org.apache.bcel.internal.generic;
/*     */ 
/*     */ import com.sun.org.apache.bcel.internal.classfile.LineNumber;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class LineNumberGen
/*     */   implements InstructionTargeter, Cloneable, Serializable
/*     */ {
/*     */   private InstructionHandle ih;
/*     */   private int src_line;
/*     */ 
/*     */   public LineNumberGen(InstructionHandle ih, int src_line)
/*     */   {
/*  84 */     setInstruction(ih);
/*  85 */     setSourceLine(src_line);
/*     */   }
/*     */ 
/*     */   public boolean containsTarget(InstructionHandle ih)
/*     */   {
/*  92 */     return this.ih == ih;
/*     */   }
/*     */ 
/*     */   public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih)
/*     */   {
/* 100 */     if (old_ih != this.ih) {
/* 101 */       throw new ClassGenException("Not targeting " + old_ih + ", but " + this.ih + "}");
/*     */     }
/* 103 */     setInstruction(new_ih);
/*     */   }
/*     */ 
/*     */   public LineNumber getLineNumber()
/*     */   {
/* 113 */     return new LineNumber(this.ih.getPosition(), this.src_line);
/*     */   }
/*     */ 
/*     */   public void setInstruction(InstructionHandle ih) {
/* 117 */     BranchInstruction.notifyTarget(this.ih, ih, this);
/*     */ 
/* 119 */     this.ih = ih;
/*     */   }
/*     */ 
/*     */   public Object clone() {
/*     */     try {
/* 124 */       return super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/* 126 */       System.err.println(e);
/* 127 */     }return null;
/*     */   }
/*     */ 
/*     */   public InstructionHandle getInstruction() {
/* 131 */     return this.ih; } 
/* 132 */   public void setSourceLine(int src_line) { this.src_line = src_line; } 
/* 133 */   public int getSourceLine() { return this.src_line; }
/*     */ 
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.LineNumberGen
 * JD-Core Version:    0.6.2
 */