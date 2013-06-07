/*     */ package java.util.zip;
/*     */ 
/*     */ public class Deflater
/*     */ {
/*     */   private final ZStreamRef zsRef;
/*  77 */   private byte[] buf = new byte[0];
/*     */   private int off;
/*     */   private int len;
/*     */   private int level;
/*     */   private int strategy;
/*     */   private boolean setParams;
/*     */   private boolean finish;
/*     */   private boolean finished;
/*     */   public static final int DEFLATED = 8;
/*     */   public static final int NO_COMPRESSION = 0;
/*     */   public static final int BEST_SPEED = 1;
/*     */   public static final int BEST_COMPRESSION = 9;
/*     */   public static final int DEFAULT_COMPRESSION = -1;
/*     */   public static final int FILTERED = 1;
/*     */   public static final int HUFFMAN_ONLY = 2;
/*     */   public static final int DEFAULT_STRATEGY = 0;
/*     */   public static final int NO_FLUSH = 0;
/*     */   public static final int SYNC_FLUSH = 2;
/*     */   public static final int FULL_FLUSH = 3;
/*     */ 
/*     */   public Deflater(int paramInt, boolean paramBoolean)
/*     */   {
/* 167 */     this.level = paramInt;
/* 168 */     this.strategy = 0;
/* 169 */     this.zsRef = new ZStreamRef(init(paramInt, 0, paramBoolean));
/*     */   }
/*     */ 
/*     */   public Deflater(int paramInt)
/*     */   {
/* 178 */     this(paramInt, false);
/*     */   }
/*     */ 
/*     */   public Deflater()
/*     */   {
/* 186 */     this(-1, false);
/*     */   }
/*     */ 
/*     */   public void setInput(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 198 */     if (paramArrayOfByte == null) {
/* 199 */       throw new NullPointerException();
/*     */     }
/* 201 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
/* 202 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 204 */     synchronized (this.zsRef) {
/* 205 */       this.buf = paramArrayOfByte;
/* 206 */       this.off = paramInt1;
/* 207 */       this.len = paramInt2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setInput(byte[] paramArrayOfByte)
/*     */   {
/* 218 */     setInput(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public void setDictionary(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 234 */     if (paramArrayOfByte == null) {
/* 235 */       throw new NullPointerException();
/*     */     }
/* 237 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
/* 238 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 240 */     synchronized (this.zsRef) {
/* 241 */       ensureOpen();
/* 242 */       setDictionary(this.zsRef.address(), paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDictionary(byte[] paramArrayOfByte)
/*     */   {
/* 257 */     setDictionary(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public void setStrategy(int paramInt)
/*     */   {
/* 267 */     switch (paramInt) {
/*     */     case 0:
/*     */     case 1:
/*     */     case 2:
/* 271 */       break;
/*     */     default:
/* 273 */       throw new IllegalArgumentException();
/*     */     }
/* 275 */     synchronized (this.zsRef) {
/* 276 */       if (this.strategy != paramInt) {
/* 277 */         this.strategy = paramInt;
/* 278 */         this.setParams = true;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setLevel(int paramInt)
/*     */   {
/* 289 */     if (((paramInt < 0) || (paramInt > 9)) && (paramInt != -1)) {
/* 290 */       throw new IllegalArgumentException("invalid compression level");
/*     */     }
/* 292 */     synchronized (this.zsRef) {
/* 293 */       if (this.level != paramInt) {
/* 294 */         this.level = paramInt;
/* 295 */         this.setParams = true;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean needsInput()
/*     */   {
/* 307 */     return this.len <= 0;
/*     */   }
/*     */ 
/*     */   public void finish()
/*     */   {
/* 315 */     synchronized (this.zsRef) {
/* 316 */       this.finish = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean finished()
/*     */   {
/* 327 */     synchronized (this.zsRef) {
/* 328 */       return this.finished;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int deflate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 350 */     return deflate(paramArrayOfByte, paramInt1, paramInt2, 0);
/*     */   }
/*     */ 
/*     */   public int deflate(byte[] paramArrayOfByte)
/*     */   {
/* 369 */     return deflate(paramArrayOfByte, 0, paramArrayOfByte.length, 0);
/*     */   }
/*     */ 
/*     */   public int deflate(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 417 */     if (paramArrayOfByte == null) {
/* 418 */       throw new NullPointerException();
/*     */     }
/* 420 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
/* 421 */       throw new ArrayIndexOutOfBoundsException();
/*     */     }
/* 423 */     synchronized (this.zsRef) {
/* 424 */       ensureOpen();
/* 425 */       if ((paramInt3 == 0) || (paramInt3 == 2) || (paramInt3 == 3))
/*     */       {
/* 427 */         return deflateBytes(this.zsRef.address(), paramArrayOfByte, paramInt1, paramInt2, paramInt3);
/* 428 */       }throw new IllegalArgumentException();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getAdler()
/*     */   {
/* 437 */     synchronized (this.zsRef) {
/* 438 */       ensureOpen();
/* 439 */       return getAdler(this.zsRef.address());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getTotalIn()
/*     */   {
/* 453 */     return (int)getBytesRead();
/*     */   }
/*     */ 
/*     */   public long getBytesRead()
/*     */   {
/* 463 */     synchronized (this.zsRef) {
/* 464 */       ensureOpen();
/* 465 */       return getBytesRead(this.zsRef.address());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getTotalOut()
/*     */   {
/* 479 */     return (int)getBytesWritten();
/*     */   }
/*     */ 
/*     */   public long getBytesWritten()
/*     */   {
/* 489 */     synchronized (this.zsRef) {
/* 490 */       ensureOpen();
/* 491 */       return getBytesWritten(this.zsRef.address());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 500 */     synchronized (this.zsRef) {
/* 501 */       ensureOpen();
/* 502 */       reset(this.zsRef.address());
/* 503 */       this.finish = false;
/* 504 */       this.finished = false;
/* 505 */       this.off = (this.len = 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void end()
/*     */   {
/* 517 */     synchronized (this.zsRef) {
/* 518 */       long l = this.zsRef.address();
/* 519 */       this.zsRef.clear();
/* 520 */       if (l != 0L) {
/* 521 */         end(l);
/* 522 */         this.buf = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 531 */     end();
/*     */   }
/*     */ 
/*     */   private void ensureOpen() {
/* 535 */     assert (Thread.holdsLock(this.zsRef));
/* 536 */     if (this.zsRef.address() == 0L)
/* 537 */       throw new NullPointerException("Deflater has been closed");
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   private static native long init(int paramInt1, int paramInt2, boolean paramBoolean);
/*     */ 
/*     */   private static native void setDictionary(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */   private native int deflateBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
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
/* 155 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.zip.Deflater
 * JD-Core Version:    0.6.2
 */