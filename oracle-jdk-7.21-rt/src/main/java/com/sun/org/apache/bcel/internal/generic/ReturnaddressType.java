/*     */ package com.sun.org.apache.bcel.internal.generic;
/*     */ 
/*     */ public class ReturnaddressType extends Type
/*     */ {
/*  71 */   public static final ReturnaddressType NO_TARGET = new ReturnaddressType();
/*     */   private InstructionHandle returnTarget;
/*     */ 
/*     */   private ReturnaddressType()
/*     */   {
/*  78 */     super((byte)16, "<return address>");
/*     */   }
/*     */ 
/*     */   public ReturnaddressType(InstructionHandle returnTarget)
/*     */   {
/*  85 */     super((byte)16, "<return address targeting " + returnTarget + ">");
/*  86 */     this.returnTarget = returnTarget;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object rat)
/*     */   {
/*  93 */     if (!(rat instanceof ReturnaddressType)) {
/*  94 */       return false;
/*     */     }
/*  96 */     return ((ReturnaddressType)rat).returnTarget.equals(this.returnTarget);
/*     */   }
/*     */ 
/*     */   public InstructionHandle getTarget()
/*     */   {
/* 103 */     return this.returnTarget;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.ReturnaddressType
 * JD-Core Version:    0.6.2
 */