/*     */ package java.io;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class ByteArrayOutputStream extends OutputStream
/*     */ {
/*     */   protected byte[] buf;
/*     */   protected int count;
/*     */ 
/*     */   public ByteArrayOutputStream()
/*     */   {
/*  62 */     this(32);
/*     */   }
/*     */ 
/*     */   public ByteArrayOutputStream(int paramInt)
/*     */   {
/*  73 */     if (paramInt < 0) {
/*  74 */       throw new IllegalArgumentException("Negative initial size: " + paramInt);
/*     */     }
/*     */ 
/*  77 */     this.buf = new byte[paramInt];
/*     */   }
/*     */ 
/*     */   private void ensureCapacity(int paramInt)
/*     */   {
/*  92 */     if (paramInt - this.buf.length > 0)
/*  93 */       grow(paramInt);
/*     */   }
/*     */ 
/*     */   private void grow(int paramInt)
/*     */   {
/* 104 */     int i = this.buf.length;
/* 105 */     int j = i << 1;
/* 106 */     if (j - paramInt < 0)
/* 107 */       j = paramInt;
/* 108 */     if (j < 0) {
/* 109 */       if (paramInt < 0)
/* 110 */         throw new OutOfMemoryError();
/* 111 */       j = 2147483647;
/*     */     }
/* 113 */     this.buf = Arrays.copyOf(this.buf, j);
/*     */   }
/*     */ 
/*     */   public synchronized void write(int paramInt)
/*     */   {
/* 122 */     ensureCapacity(this.count + 1);
/* 123 */     this.buf[this.count] = ((byte)paramInt);
/* 124 */     this.count += 1;
/*     */   }
/*     */ 
/*     */   public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 136 */     if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 - paramArrayOfByte.length > 0))
/*     */     {
/* 138 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 140 */     ensureCapacity(this.count + paramInt2);
/* 141 */     System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.count, paramInt2);
/* 142 */     this.count += paramInt2;
/*     */   }
/*     */ 
/*     */   public synchronized void writeTo(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 154 */     paramOutputStream.write(this.buf, 0, this.count);
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 166 */     this.count = 0;
/*     */   }
/*     */ 
/*     */   public synchronized byte[] toByteArray()
/*     */   {
/* 178 */     return Arrays.copyOf(this.buf, this.count);
/*     */   }
/*     */ 
/*     */   public synchronized int size()
/*     */   {
/* 189 */     return this.count;
/*     */   }
/*     */ 
/*     */   public synchronized String toString()
/*     */   {
/* 208 */     return new String(this.buf, 0, this.count);
/*     */   }
/*     */ 
/*     */   public synchronized String toString(String paramString)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 232 */     return new String(this.buf, 0, this.count, paramString);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public synchronized String toString(int paramInt)
/*     */   {
/* 259 */     return new String(this.buf, paramInt, 0, this.count);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.ByteArrayOutputStream
 * JD-Core Version:    0.6.2
 */