/*     */ package java.util.zip;
/*     */ 
/*     */ public class Inflater
/*     */ {
/*     */   private final ZStreamRef zsRef;
/*  77 */   private byte[] buf = defaultBuf;
/*     */   private int off;
/*     */   private int len;
/*     */   private boolean finished;
/*     */   private boolean needDict;
/*     */   private static final byte[] defaultBuf;
/*     */ 
/*     */   public Inflater(boolean paramBoolean)
/*     */   {
/* 101 */     this.zsRef = new ZStreamRef(init(paramBoolean));
/*     */   }
/*     */ 
/*     */   public Inflater()
/*     */   {
/* 108 */     this(false);
/*     */   }
/*     */ 
/*     */   public void setInput(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 121 */     if (paramArrayOfByte == null) {
/* 122 */       throw new NullPointerException();
/*     */     }
/* 124 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
/* 125 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 127 */     synchronized (this.zsRef) {
/* 128 */       this.buf = paramArrayOfByte;
/* 129 */       this.off = paramInt1;
/* 130 */       this.len = paramInt2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setInput(byte[] paramArrayOfByte)
/*     */   {
/* 142 */     setInput(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public void setDictionary(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 157 */     if (paramArrayOfByte == null) {
/* 158 */       throw new NullPointerException();
/*     */     }
/* 160 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
/* 161 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 163 */     synchronized (this.zsRef) {
/* 164 */       ensureOpen();
/* 165 */       setDictionary(this.zsRef.address(), paramArrayOfByte, paramInt1, paramInt2);
/* 166 */       this.needDict = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDictionary(byte[] paramArrayOfByte)
/*     */   {
/* 180 */     setDictionary(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int getRemaining()
/*     */   {
/* 190 */     synchronized (this.zsRef) {
/* 191 */       return this.len;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean needsInput()
/*     */   {
/* 202 */     synchronized (this.zsRef) {
/* 203 */       return this.len <= 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean needsDictionary()
/*     */   {
/* 213 */     synchronized (this.zsRef) {
/* 214 */       return this.needDict;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean finished()
/*     */   {
/* 225 */     synchronized (this.zsRef) {
/* 226 */       return this.finished;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int inflate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws DataFormatException
/*     */   {
/* 248 */     if (paramArrayOfByte == null) {
/* 249 */       throw new NullPointerException();
/*     */     }
/* 251 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
/* 252 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 254 */     synchronized (this.zsRef) {
/* 255 */       ensureOpen();
/* 256 */       return inflateBytes(this.zsRef.address(), paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int inflate(byte[] paramArrayOfByte)
/*     */     throws DataFormatException
/*     */   {
/* 274 */     return inflate(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int getAdler()
/*     */   {
/* 282 */     synchronized (this.zsRef) {
/* 283 */       ensureOpen();
/* 284 */       return getAdler(this.zsRef.address());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getTotalIn()
/*     */   {
/* 298 */     return (int)getBytesRead();
/*     */   }
/*     */ 
/*     */   public long getBytesRead()
/*     */   {
/* 308 */     synchronized (this.zsRef) {
/* 309 */       ensureOpen();
/* 310 */       return getBytesRead(this.zsRef.address());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getTotalOut()
/*     */   {
/* 324 */     return (int)getBytesWritten();
/*     */   }
/*     */ 
/*     */   public long getBytesWritten()
/*     */   {
/* 334 */     synchronized (this.zsRef) {
/* 335 */       ensureOpen();
/* 336 */       return getBytesWritten(this.zsRef.address());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 344 */     synchronized (this.zsRef) {
/* 345 */       ensureOpen();
/* 346 */       reset(this.zsRef.address());
/* 347 */       this.buf = defaultBuf;
/* 348 */       this.finished = false;
/* 349 */       this.needDict = false;
/* 350 */       this.off = (this.len = 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void end()
/*     */   {
/* 362 */     synchronized (this.zsRef) {
/* 363 */       long l = this.zsRef.address();
/* 364 */       this.zsRef.clear();
/* 365 */       if (l != 0L) {
/* 366 */         end(l);
/* 367 */         this.buf = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 376 */     end();
/*     */   }
/*     */ 
/*     */   private void ensureOpen() {
/* 380 */     assert (Thread.holdsLock(this.zsRef));
/* 381 */     if (this.zsRef.address() == 0L)
/* 382 */       throw new NullPointerException("Inflater has been closed");
/*     */   }
/*     */ 
/*     */   boolean ended() {
/* 386 */     synchronized (this.zsRef) {
/* 387 */       return this.zsRef.address() == 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   private static native long init(boolean paramBoolean);
/*     */ 
/*     */   private static native void setDictionary(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */   private native int inflateBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws DataFormatException;
/*     */ 
/*     */   private static native int getAdler(long paramLong);
/*     */ 
/*     */   private static native long getBytesRead(long paramLong);
/*     */ 
/*     */   private static native long getBytesWritten(long paramLong);
/*     */ 
/*     */   private static native void reset(long paramLong);
/*     */ 
/*     */   private static native void end(long paramLong);
/*     */ 
/*     */   static
/*     */   {
/*  82 */     defaultBuf = new byte[0];
/*     */ 
/*  86 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.zip.Inflater
 * JD-Core Version:    0.6.2
 */