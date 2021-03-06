/*     */ package java.nio;
/*     */ 
/*     */ class ByteBufferAsDoubleBufferRL extends ByteBufferAsDoubleBufferL
/*     */ {
/*     */   ByteBufferAsDoubleBufferRL(ByteBuffer paramByteBuffer)
/*     */   {
/*  55 */     super(paramByteBuffer);
/*     */   }
/*     */ 
/*     */   ByteBufferAsDoubleBufferRL(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/*  68 */     super(paramByteBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
/*     */   }
/*     */ 
/*     */   public DoubleBuffer slice()
/*     */   {
/*  73 */     int i = position();
/*  74 */     int j = limit();
/*  75 */     assert (i <= j);
/*  76 */     int k = i <= j ? j - i : 0;
/*  77 */     int m = (i << 3) + this.offset;
/*  78 */     assert (m >= 0);
/*  79 */     return new ByteBufferAsDoubleBufferRL(this.bb, -1, 0, k, k, m);
/*     */   }
/*     */ 
/*     */   public DoubleBuffer duplicate() {
/*  83 */     return new ByteBufferAsDoubleBufferRL(this.bb, markValue(), position(), limit(), capacity(), this.offset);
/*     */   }
/*     */ 
/*     */   public DoubleBuffer asReadOnlyBuffer()
/*     */   {
/* 100 */     return duplicate();
/*     */   }
/*     */ 
/*     */   public DoubleBuffer put(double paramDouble)
/*     */   {
/* 125 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   public DoubleBuffer put(int paramInt, double paramDouble)
/*     */   {
/* 134 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   public DoubleBuffer compact()
/*     */   {
/* 156 */     throw new ReadOnlyBufferException();
/*     */   }
/*     */ 
/*     */   public boolean isDirect()
/*     */   {
/* 161 */     return this.bb.isDirect();
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly() {
/* 165 */     return true;
/*     */   }
/*     */ 
/*     */   public ByteOrder order()
/*     */   {
/* 215 */     return ByteOrder.LITTLE_ENDIAN;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.ByteBufferAsDoubleBufferRL
 * JD-Core Version:    0.6.2
 */