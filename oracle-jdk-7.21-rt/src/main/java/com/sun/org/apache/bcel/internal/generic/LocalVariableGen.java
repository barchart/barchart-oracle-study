/*     */ package com.sun.org.apache.bcel.internal.generic;
/*     */ 
/*     */ import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class LocalVariableGen
/*     */   implements InstructionTargeter, NamedAndTyped, Cloneable, Serializable
/*     */ {
/*     */   private int index;
/*     */   private String name;
/*     */   private Type type;
/*     */   private InstructionHandle start;
/*     */   private InstructionHandle end;
/*     */ 
/*     */   public LocalVariableGen(int index, String name, Type type, InstructionHandle start, InstructionHandle end)
/*     */   {
/*  95 */     if ((index < 0) || (index > 65535)) {
/*  96 */       throw new ClassGenException("Invalid index index: " + index);
/*     */     }
/*  98 */     this.name = name;
/*  99 */     this.type = type;
/* 100 */     this.index = index;
/* 101 */     setStart(start);
/* 102 */     setEnd(end);
/*     */   }
/*     */ 
/*     */   public LocalVariable getLocalVariable(ConstantPoolGen cp)
/*     */   {
/* 121 */     int start_pc = this.start.getPosition();
/* 122 */     int length = this.end.getPosition() - start_pc;
/*     */ 
/* 124 */     if (length > 0) {
/* 125 */       length += this.end.getInstruction().getLength();
/*     */     }
/* 127 */     int name_index = cp.addUtf8(this.name);
/* 128 */     int signature_index = cp.addUtf8(this.type.getSignature());
/*     */ 
/* 130 */     return new LocalVariable(start_pc, length, name_index, signature_index, this.index, cp.getConstantPool());
/*     */   }
/*     */ 
/*     */   public void setIndex(int index) {
/* 134 */     this.index = index; } 
/* 135 */   public int getIndex() { return this.index; } 
/* 136 */   public void setName(String name) { this.name = name; } 
/* 137 */   public String getName() { return this.name; } 
/* 138 */   public void setType(Type type) { this.type = type; } 
/* 139 */   public Type getType() { return this.type; } 
/*     */   public InstructionHandle getStart() {
/* 141 */     return this.start; } 
/* 142 */   public InstructionHandle getEnd() { return this.end; }
/*     */ 
/*     */   public void setStart(InstructionHandle start) {
/* 145 */     BranchInstruction.notifyTarget(this.start, start, this);
/* 146 */     this.start = start;
/*     */   }
/*     */ 
/*     */   public void setEnd(InstructionHandle end) {
/* 150 */     BranchInstruction.notifyTarget(this.end, end, this);
/* 151 */     this.end = end;
/*     */   }
/*     */ 
/*     */   public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih)
/*     */   {
/* 159 */     boolean targeted = false;
/*     */ 
/* 161 */     if (this.start == old_ih) {
/* 162 */       targeted = true;
/* 163 */       setStart(new_ih);
/*     */     }
/*     */ 
/* 166 */     if (this.end == old_ih) {
/* 167 */       targeted = true;
/* 168 */       setEnd(new_ih);
/*     */     }
/*     */ 
/* 171 */     if (!targeted)
/* 172 */       throw new ClassGenException("Not targeting " + old_ih + ", but {" + this.start + ", " + this.end + "}");
/*     */   }
/*     */ 
/*     */   public boolean containsTarget(InstructionHandle ih)
/*     */   {
/* 180 */     return (this.start == ih) || (this.end == ih);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 188 */     if (!(o instanceof LocalVariableGen)) {
/* 189 */       return false;
/*     */     }
/* 191 */     LocalVariableGen l = (LocalVariableGen)o;
/* 192 */     return (l.index == this.index) && (l.start == this.start) && (l.end == this.end);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 196 */     return "LocalVariableGen(" + this.name + ", " + this.type + ", " + this.start + ", " + this.end + ")";
/*     */   }
/*     */ 
/*     */   public Object clone() {
/*     */     try {
/* 201 */       return super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/* 203 */       System.err.println(e);
/* 204 */     }return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.LocalVariableGen
 * JD-Core Version:    0.6.2
 */