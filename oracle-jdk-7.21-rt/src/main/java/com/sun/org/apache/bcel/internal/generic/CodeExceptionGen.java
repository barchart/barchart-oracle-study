/*     */ package com.sun.org.apache.bcel.internal.generic;
/*     */ 
/*     */ import com.sun.org.apache.bcel.internal.classfile.CodeException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public final class CodeExceptionGen
/*     */   implements InstructionTargeter, Cloneable, Serializable
/*     */ {
/*     */   private InstructionHandle start_pc;
/*     */   private InstructionHandle end_pc;
/*     */   private InstructionHandle handler_pc;
/*     */   private ObjectType catch_type;
/*     */ 
/*     */   public CodeExceptionGen(InstructionHandle start_pc, InstructionHandle end_pc, InstructionHandle handler_pc, ObjectType catch_type)
/*     */   {
/*  96 */     setStartPC(start_pc);
/*  97 */     setEndPC(end_pc);
/*  98 */     setHandlerPC(handler_pc);
/*  99 */     this.catch_type = catch_type;
/*     */   }
/*     */ 
/*     */   public CodeException getCodeException(ConstantPoolGen cp)
/*     */   {
/* 112 */     return new CodeException(this.start_pc.getPosition(), this.end_pc.getPosition() + this.end_pc.getInstruction().getLength(), this.handler_pc.getPosition(), this.catch_type == null ? 0 : cp.addClass(this.catch_type));
/*     */   }
/*     */ 
/*     */   public void setStartPC(InstructionHandle start_pc)
/*     */   {
/* 122 */     BranchInstruction.notifyTarget(this.start_pc, start_pc, this);
/* 123 */     this.start_pc = start_pc;
/*     */   }
/*     */ 
/*     */   public void setEndPC(InstructionHandle end_pc)
/*     */   {
/* 130 */     BranchInstruction.notifyTarget(this.end_pc, end_pc, this);
/* 131 */     this.end_pc = end_pc;
/*     */   }
/*     */ 
/*     */   public void setHandlerPC(InstructionHandle handler_pc)
/*     */   {
/* 138 */     BranchInstruction.notifyTarget(this.handler_pc, handler_pc, this);
/* 139 */     this.handler_pc = handler_pc;
/*     */   }
/*     */ 
/*     */   public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih)
/*     */   {
/* 147 */     boolean targeted = false;
/*     */ 
/* 149 */     if (this.start_pc == old_ih) {
/* 150 */       targeted = true;
/* 151 */       setStartPC(new_ih);
/*     */     }
/*     */ 
/* 154 */     if (this.end_pc == old_ih) {
/* 155 */       targeted = true;
/* 156 */       setEndPC(new_ih);
/*     */     }
/*     */ 
/* 159 */     if (this.handler_pc == old_ih) {
/* 160 */       targeted = true;
/* 161 */       setHandlerPC(new_ih);
/*     */     }
/*     */ 
/* 164 */     if (!targeted)
/* 165 */       throw new ClassGenException("Not targeting " + old_ih + ", but {" + this.start_pc + ", " + this.end_pc + ", " + this.handler_pc + "}");
/*     */   }
/*     */ 
/*     */   public boolean containsTarget(InstructionHandle ih)
/*     */   {
/* 173 */     return (this.start_pc == ih) || (this.end_pc == ih) || (this.handler_pc == ih);
/*     */   }
/*     */ 
/*     */   public void setCatchType(ObjectType catch_type) {
/* 177 */     this.catch_type = catch_type;
/*     */   }
/* 179 */   public ObjectType getCatchType() { return this.catch_type; }
/*     */ 
/*     */   public InstructionHandle getStartPC()
/*     */   {
/* 183 */     return this.start_pc;
/*     */   }
/*     */ 
/*     */   public InstructionHandle getEndPC() {
/* 187 */     return this.end_pc;
/*     */   }
/*     */ 
/*     */   public InstructionHandle getHandlerPC() {
/* 191 */     return this.handler_pc;
/*     */   }
/*     */   public String toString() {
/* 194 */     return "CodeExceptionGen(" + this.start_pc + ", " + this.end_pc + ", " + this.handler_pc + ")";
/*     */   }
/*     */ 
/*     */   public Object clone() {
/*     */     try {
/* 199 */       return super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/* 201 */       System.err.println(e);
/* 202 */     }return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.CodeExceptionGen
 * JD-Core Version:    0.6.2
 */