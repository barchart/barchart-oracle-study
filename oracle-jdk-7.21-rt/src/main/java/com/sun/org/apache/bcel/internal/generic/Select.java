/*     */ package com.sun.org.apache.bcel.internal.generic;
/*     */ 
/*     */ import com.sun.org.apache.bcel.internal.util.ByteSequence;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public abstract class Select extends BranchInstruction
/*     */   implements VariableLengthInstruction, StackProducer
/*     */ {
/*     */   protected int[] match;
/*     */   protected int[] indices;
/*     */   protected InstructionHandle[] targets;
/*     */   protected int fixed_length;
/*     */   protected int match_length;
/*  79 */   protected int padding = 0;
/*     */ 
/*     */   Select()
/*     */   {
/*     */   }
/*     */ 
/*     */   Select(short opcode, int[] match, InstructionHandle[] targets, InstructionHandle target)
/*     */   {
/*  97 */     super(opcode, target);
/*     */ 
/*  99 */     this.targets = targets;
/* 100 */     for (int i = 0; i < targets.length; i++) {
/* 101 */       notifyTarget(null, targets[i], this);
/*     */     }
/* 103 */     this.match = match;
/*     */ 
/* 105 */     if ((this.match_length = match.length) != targets.length) {
/* 106 */       throw new ClassGenException("Match and target array have not the same length");
/*     */     }
/* 108 */     this.indices = new int[this.match_length];
/*     */   }
/*     */ 
/*     */   protected int updatePosition(int offset, int max_offset)
/*     */   {
/* 125 */     this.position += offset;
/*     */ 
/* 127 */     short old_length = this.length;
/*     */ 
/* 131 */     this.padding = ((4 - (this.position + 1) % 4) % 4);
/* 132 */     this.length = ((short)(this.fixed_length + this.padding));
/*     */ 
/* 134 */     return this.length - old_length;
/*     */   }
/*     */ 
/*     */   public void dump(DataOutputStream out)
/*     */     throws IOException
/*     */   {
/* 142 */     out.writeByte(this.opcode);
/*     */ 
/* 144 */     for (int i = 0; i < this.padding; i++) {
/* 145 */       out.writeByte(0);
/*     */     }
/* 147 */     this.index = getTargetOffset();
/* 148 */     out.writeInt(this.index);
/*     */   }
/*     */ 
/*     */   protected void initFromFile(ByteSequence bytes, boolean wide)
/*     */     throws IOException
/*     */   {
/* 156 */     this.padding = ((4 - bytes.getIndex() % 4) % 4);
/*     */ 
/* 158 */     for (int i = 0; i < this.padding; i++) {
/* 159 */       bytes.readByte();
/*     */     }
/*     */ 
/* 163 */     this.index = bytes.readInt();
/*     */   }
/*     */ 
/*     */   public String toString(boolean verbose)
/*     */   {
/* 170 */     StringBuffer buf = new StringBuffer(super.toString(verbose));
/*     */ 
/* 172 */     if (verbose) {
/* 173 */       for (int i = 0; i < this.match_length; i++) {
/* 174 */         String s = "null";
/*     */ 
/* 176 */         if (this.targets[i] != null) {
/* 177 */           s = this.targets[i].getInstruction().toString();
/*     */         }
/* 179 */         buf.append("(" + this.match[i] + ", " + s + " = {" + this.indices[i] + "})");
/*     */       }
/*     */     }
/*     */     else {
/* 183 */       buf.append(" ...");
/*     */     }
/* 185 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public void setTarget(int i, InstructionHandle target)
/*     */   {
/* 192 */     notifyTarget(this.targets[i], target, this);
/* 193 */     this.targets[i] = target;
/*     */   }
/*     */ 
/*     */   public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih)
/*     */   {
/* 201 */     boolean targeted = false;
/*     */ 
/* 203 */     if (this.target == old_ih) {
/* 204 */       targeted = true;
/* 205 */       setTarget(new_ih);
/*     */     }
/*     */ 
/* 208 */     for (int i = 0; i < this.targets.length; i++) {
/* 209 */       if (this.targets[i] == old_ih) {
/* 210 */         targeted = true;
/* 211 */         setTarget(i, new_ih);
/*     */       }
/*     */     }
/*     */ 
/* 215 */     if (!targeted)
/* 216 */       throw new ClassGenException("Not targeting " + old_ih);
/*     */   }
/*     */ 
/*     */   public boolean containsTarget(InstructionHandle ih)
/*     */   {
/* 223 */     if (this.target == ih) {
/* 224 */       return true;
/*     */     }
/* 226 */     for (int i = 0; i < this.targets.length; i++) {
/* 227 */       if (this.targets[i] == ih)
/* 228 */         return true;
/*     */     }
/* 230 */     return false;
/*     */   }
/*     */ 
/*     */   void dispose()
/*     */   {
/* 237 */     super.dispose();
/*     */ 
/* 239 */     for (int i = 0; i < this.targets.length; i++)
/* 240 */       this.targets[i].removeTargeter(this);
/*     */   }
/*     */ 
/*     */   public int[] getMatchs()
/*     */   {
/* 246 */     return this.match;
/*     */   }
/*     */ 
/*     */   public int[] getIndices()
/*     */   {
/* 251 */     return this.indices;
/*     */   }
/*     */ 
/*     */   public InstructionHandle[] getTargets()
/*     */   {
/* 256 */     return this.targets;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.bcel.internal.generic.Select
 * JD-Core Version:    0.6.2
 */