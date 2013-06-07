/*     */ package sun.org.mozilla.javascript.internal.optimizer;
/*     */ 
/*     */ class DataFlowBitSet
/*     */ {
/*     */   private int[] itsBits;
/*     */   private int itsSize;
/*     */ 
/*     */   DataFlowBitSet(int paramInt)
/*     */   {
/*  49 */     this.itsSize = paramInt;
/*  50 */     this.itsBits = new int[paramInt + 31 >> 5];
/*     */   }
/*     */ 
/*     */   void set(int paramInt)
/*     */   {
/*  55 */     if ((0 > paramInt) || (paramInt >= this.itsSize)) badIndex(paramInt);
/*  56 */     this.itsBits[(paramInt >> 5)] |= 1 << (paramInt & 0x1F);
/*     */   }
/*     */ 
/*     */   boolean test(int paramInt)
/*     */   {
/*  61 */     if ((0 > paramInt) || (paramInt >= this.itsSize)) badIndex(paramInt);
/*  62 */     return (this.itsBits[(paramInt >> 5)] & 1 << (paramInt & 0x1F)) != 0;
/*     */   }
/*     */ 
/*     */   void not()
/*     */   {
/*  67 */     int i = this.itsBits.length;
/*  68 */     for (int j = 0; j < i; j++)
/*  69 */       this.itsBits[j] ^= -1;
/*     */   }
/*     */ 
/*     */   void clear(int paramInt)
/*     */   {
/*  74 */     if ((0 > paramInt) || (paramInt >= this.itsSize)) badIndex(paramInt);
/*  75 */     this.itsBits[(paramInt >> 5)] &= (1 << (paramInt & 0x1F) ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   void clear()
/*     */   {
/*  80 */     int i = this.itsBits.length;
/*  81 */     for (int j = 0; j < i; j++)
/*  82 */       this.itsBits[j] = 0;
/*     */   }
/*     */ 
/*     */   void or(DataFlowBitSet paramDataFlowBitSet)
/*     */   {
/*  87 */     int i = this.itsBits.length;
/*  88 */     for (int j = 0; j < i; j++)
/*  89 */       this.itsBits[j] |= paramDataFlowBitSet.itsBits[j];
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  95 */     StringBuffer localStringBuffer = new StringBuffer();
/*  96 */     localStringBuffer.append("DataFlowBitSet, size = ");
/*  97 */     localStringBuffer.append(this.itsSize);
/*  98 */     localStringBuffer.append('\n');
/*  99 */     int i = this.itsBits.length;
/* 100 */     for (int j = 0; j < i; j++) {
/* 101 */       localStringBuffer.append(Integer.toHexString(this.itsBits[j]));
/* 102 */       localStringBuffer.append(' ');
/*     */     }
/* 104 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   boolean df(DataFlowBitSet paramDataFlowBitSet1, DataFlowBitSet paramDataFlowBitSet2, DataFlowBitSet paramDataFlowBitSet3)
/*     */   {
/* 109 */     int i = this.itsBits.length;
/* 110 */     boolean bool = false;
/* 111 */     for (int j = 0; j < i; j++) {
/* 112 */       int k = this.itsBits[j];
/* 113 */       this.itsBits[j] = ((paramDataFlowBitSet1.itsBits[j] | paramDataFlowBitSet2.itsBits[j]) & paramDataFlowBitSet3.itsBits[j]);
/* 114 */       bool |= k != this.itsBits[j];
/*     */     }
/* 116 */     return bool;
/*     */   }
/*     */ 
/*     */   boolean df2(DataFlowBitSet paramDataFlowBitSet1, DataFlowBitSet paramDataFlowBitSet2, DataFlowBitSet paramDataFlowBitSet3)
/*     */   {
/* 121 */     int i = this.itsBits.length;
/* 122 */     boolean bool = false;
/* 123 */     for (int j = 0; j < i; j++) {
/* 124 */       int k = this.itsBits[j];
/* 125 */       this.itsBits[j] = (paramDataFlowBitSet1.itsBits[j] & paramDataFlowBitSet3.itsBits[j] | paramDataFlowBitSet2.itsBits[j]);
/* 126 */       bool |= k != this.itsBits[j];
/*     */     }
/* 128 */     return bool;
/*     */   }
/*     */ 
/*     */   private void badIndex(int paramInt)
/*     */   {
/* 133 */     throw new RuntimeException("DataFlowBitSet bad index " + paramInt);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.optimizer.DataFlowBitSet
 * JD-Core Version:    0.6.2
 */