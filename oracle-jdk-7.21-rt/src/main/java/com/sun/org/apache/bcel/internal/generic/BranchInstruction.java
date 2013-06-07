/*     */ package com.sun.org.apache.bcel.internal.generic;
/*     */ 
/*     */ import com.sun.org.apache.bcel.internal.util.ByteSequence;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public abstract class BranchInstruction extends Instruction
/*     */   implements InstructionTargeter
/*     */ {
/*     */   protected int index;
/*     */   protected InstructionHandle target;
/*     */   protected int position;
/*     */ 
/*     */   BranchInstruction()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected BranchInstruction(short opcode, InstructionHandle target)
/*     */   {
/*  88 */     super(opcode, (short)3);
/*  89 */     setTarget(target);
/*     */   }
/*     */ 
/*     */   public void dump(DataOutputStream out)
/*     */     throws IOException
/*     */   {
/*  97 */     out.writeByte(this.opcode);
/*     */ 
/*  99 */     this.index = getTargetOffset();
/*     */ 
/* 101 */     if (Math.abs(this.index) >= 32767) {
/* 102 */       throw new ClassGenException("Branch target offset too large for short");
/*     */     }
/* 104 */     out.writeShort(this.index);
/*     */   }
/*     */ 
/*     */   protected int getTargetOffset(InstructionHandle target)
/*     */   {
/* 112 */     if (target == null) {
/* 113 */       throw new ClassGenException("Target of " + super.toString(true) + " is invalid null handle");
/*     */     }
/*     */ 
/* 116 */     int t = target.getPosition();
/*     */ 
/* 118 */     if (t < 0) {
/* 119 */       throw new ClassGenException("Invalid branch target position offset for " + super.toString(true) + ":" + t + ":" + target);
/*     */     }
/*     */ 
/* 122 */     return t - this.position;
/*     */   }
/*     */ 
/*     */   protected int getTargetOffset()
/*     */   {
/* 128 */     return getTargetOffset(this.target);
/*     */   }
/*     */ 
/*     */   protected int updatePosition(int offset, int max_offset)
/*     */   {
/* 141 */     this.position += offset;
/* 142 */     return 0;
/*     */   }
/*     */ 
/*     */   public String toString(boolean verbose)
/*     */   {
/* 157 */     String s = super.toString(verbose);
/* 158 */     String t = "null";
/*     */ 
/* 160 */     if (verbose) {
/* 161 */       if (this.target != null) {
/* 162 */         if (this.target.getInstruction() == this)
/* 163 */           t = "<points to itself>";
/* 164 */         else if (this.target.getInstruction() == null)
/* 165 */           t = "<null instruction!!!?>";
/*     */         else
/* 167 */           t = this.target.getInstruction().toString(false);
/*     */       }
/*     */     }
/* 170 */     else if (this.target != null) {
/* 171 */       this.index = getTargetOffset();
/* 172 */       t = "" + (this.index + this.position);
/*     */     }
/*     */ 
/* 176 */     return s + " -> " + t;
/*     */   }
/*     */ 
/*     */   protected void initFromFile(ByteSequence bytes, boolean wide)
/*     */     throws IOException
/*     */   {
/* 189 */     this.length = 3;
/* 190 */     this.index = bytes.readShort();
/*     */   }
/*     */ 
/*     */   public final int getIndex()
/*     */   {
/* 196 */     return this.index;
/*     */   }
/*     */ 
/*     */   public InstructionHandle getTarget()
/*     */   {
/* 201 */     return this.target;
/*     */   }
/*     */ 
/*     */   public void setTarget(InstructionHandle target)
/*     */   {
/* 208 */     notifyTarget(this.target, target, this);
/* 209 */     this.target = target;
/*     */   }
/*     */ 
/*     */   static final void notifyTarget(InstructionHandle old_ih, InstructionHandle new_ih, InstructionTargeter t)
/*     */   {
/* 217 */     if (old_ih != null)
/* 218 */       old_ih.removeTargeter(t);
/* 219 */     if (new_ih != null)
/* 220 */       new_ih.addTargeter(t);
/*     */   }
/*     */ 
/*     */   public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih)
/*     */   {
/* 228 */     if (this.target == old_ih)
/* 229 */       setTarget(new_ih);
/*     */     else
/* 231 */       throw new ClassGenException("Not targeting " + old_ih + ", but " + this.target);
/*     */   }
/*     */ 
/*     */   public boolean containsTarget(InstructionHandle ih)
/*     */   {
/* 238 */     return this.target == ih;
/*     */   }
/*     */ 
/*     */   void dispose()
/*     */   {
/* 245 */     setTarget(null);
/* 246 */     this.index = -1;
/* 247 */     this.position = -1;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.BranchInstruction
 * JD-Core Version:    0.6.2
 */