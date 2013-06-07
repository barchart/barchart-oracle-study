/*     */ package java.util.zip;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.WeakHashMap;
/*     */ import sun.misc.PerfCounter;
/*     */ import sun.misc.VM;
/*     */ 
/*     */ public class ZipFile
/*     */   implements ZipConstants, Closeable
/*     */ {
/*     */   private long jzfile;
/*     */   private String name;
/*     */   private int total;
/*  60 */   private volatile boolean closeRequested = false;
/*     */   private static final int STORED = 0;
/*     */   private static final int DEFLATED = 8;
/*     */   public static final int OPEN_READ = 1;
/*     */   public static final int OPEN_DELETE = 4;
/*  92 */   private static final boolean usemmap = (str == null) || ((str.length() != 0) && (!str.equalsIgnoreCase("true")));
/*     */   private ZipCoder zc;
/* 322 */   private final Map<InputStream, Inflater> streams = new WeakHashMap();
/*     */ 
/* 464 */   private Deque<Inflater> inflaterCache = new ArrayDeque();
/*     */   private static final int JZENTRY_NAME = 0;
/*     */   private static final int JZENTRY_EXTRA = 1;
/*     */   private static final int JZENTRY_COMMENT = 2;
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   public ZipFile(String paramString)
/*     */     throws IOException
/*     */   {
/* 115 */     this(new File(paramString), 1);
/*     */   }
/*     */ 
/*     */   public ZipFile(File paramFile, int paramInt)
/*     */     throws IOException
/*     */   {
/* 144 */     this(paramFile, paramInt, StandardCharsets.UTF_8);
/*     */   }
/*     */ 
/*     */   public ZipFile(File paramFile)
/*     */     throws ZipException, IOException
/*     */   {
/* 158 */     this(paramFile, 1);
/*     */   }
/*     */ 
/*     */   public ZipFile(File paramFile, int paramInt, Charset paramCharset)
/*     */     throws IOException
/*     */   {
/* 197 */     if (((paramInt & 0x1) == 0) || ((paramInt & 0xFFFFFFFA) != 0))
/*     */     {
/* 199 */       throw new IllegalArgumentException("Illegal mode: 0x" + Integer.toHexString(paramInt));
/*     */     }
/*     */ 
/* 202 */     String str = paramFile.getPath();
/* 203 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 204 */     if (localSecurityManager != null) {
/* 205 */       localSecurityManager.checkRead(str);
/* 206 */       if ((paramInt & 0x4) != 0) {
/* 207 */         localSecurityManager.checkDelete(str);
/*     */       }
/*     */     }
/* 210 */     if (paramCharset == null)
/* 211 */       throw new NullPointerException("charset is null");
/* 212 */     this.zc = ZipCoder.get(paramCharset);
/* 213 */     long l = System.nanoTime();
/* 214 */     this.jzfile = open(str, paramInt, paramFile.lastModified(), usemmap);
/* 215 */     PerfCounter.getZipFileOpenTime().addElapsedTimeFrom(l);
/* 216 */     PerfCounter.getZipFileCount().increment();
/* 217 */     this.name = str;
/* 218 */     this.total = getTotal(this.jzfile);
/*     */   }
/*     */ 
/*     */   public ZipFile(String paramString, Charset paramCharset)
/*     */     throws IOException
/*     */   {
/* 247 */     this(new File(paramString), 1, paramCharset);
/*     */   }
/*     */ 
/*     */   public ZipFile(File paramFile, Charset paramCharset)
/*     */     throws IOException
/*     */   {
/* 267 */     this(paramFile, 1, paramCharset);
/*     */   }
/*     */ 
/*     */   public String getComment()
/*     */   {
/* 280 */     synchronized (this) {
/* 281 */       ensureOpen();
/* 282 */       byte[] arrayOfByte = getCommentBytes(this.jzfile);
/* 283 */       if (arrayOfByte == null)
/* 284 */         return null;
/* 285 */       return this.zc.toString(arrayOfByte, arrayOfByte.length);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ZipEntry getEntry(String paramString)
/*     */   {
/* 298 */     if (paramString == null) {
/* 299 */       throw new NullPointerException("name");
/*     */     }
/* 301 */     long l = 0L;
/* 302 */     synchronized (this) {
/* 303 */       ensureOpen();
/* 304 */       l = getEntry(this.jzfile, this.zc.getBytes(paramString), true);
/* 305 */       if (l != 0L) {
/* 306 */         ZipEntry localZipEntry = getZipEntry(paramString, l);
/* 307 */         freeEntry(this.jzfile, l);
/* 308 */         return localZipEntry;
/*     */       }
/*     */     }
/* 311 */     return null;
/*     */   }
/*     */ 
/*     */   private static native long getEntry(long paramLong, byte[] paramArrayOfByte, boolean paramBoolean);
/*     */ 
/*     */   private static native void freeEntry(long paramLong1, long paramLong2);
/*     */ 
/*     */   public InputStream getInputStream(ZipEntry paramZipEntry)
/*     */     throws IOException
/*     */   {
/* 339 */     if (paramZipEntry == null) {
/* 340 */       throw new NullPointerException("entry");
/*     */     }
/* 342 */     long l1 = 0L;
/* 343 */     ZipFileInputStream localZipFileInputStream = null;
/* 344 */     synchronized (this) {
/* 345 */       ensureOpen();
/* 346 */       if ((!this.zc.isUTF8()) && ((paramZipEntry.flag & 0x800) != 0))
/* 347 */         l1 = getEntry(this.jzfile, this.zc.getBytesUTF8(paramZipEntry.name), false);
/*     */       else {
/* 349 */         l1 = getEntry(this.jzfile, this.zc.getBytes(paramZipEntry.name), false);
/*     */       }
/* 351 */       if (l1 == 0L) {
/* 352 */         return null;
/*     */       }
/* 354 */       localZipFileInputStream = new ZipFileInputStream(l1);
/*     */ 
/* 356 */       switch (getEntryMethod(l1)) {
/*     */       case 0:
/* 358 */         synchronized (this.streams) {
/* 359 */           this.streams.put(localZipFileInputStream, null);
/*     */         }
/* 361 */         return localZipFileInputStream;
/*     */       case 8:
/* 364 */         long l2 = getEntrySize(l1) + 2L;
/* 365 */         if (l2 > 65536L) l2 = 8192L;
/* 366 */         if (l2 <= 0L) l2 = 4096L;
/* 367 */         Inflater localInflater = getInflater();
/* 368 */         ZipFileInflaterInputStream localZipFileInflaterInputStream = new ZipFileInflaterInputStream(localZipFileInputStream, localInflater, (int)l2);
/*     */ 
/* 370 */         synchronized (this.streams) {
/* 371 */           this.streams.put(localZipFileInflaterInputStream, localInflater);
/*     */         }
/* 373 */         return localZipFileInflaterInputStream;
/*     */       }
/* 375 */       throw new ZipException("invalid compression method");
/*     */     }
/*     */   }
/*     */ 
/*     */   private Inflater getInflater()
/*     */   {
/* 441 */     synchronized (this.inflaterCache)
/*     */     {
/*     */       Inflater localInflater;
/* 442 */       while (null != (localInflater = (Inflater)this.inflaterCache.poll())) {
/* 443 */         if (false == localInflater.ended()) {
/* 444 */           return localInflater;
/*     */         }
/*     */       }
/*     */     }
/* 448 */     return new Inflater(true);
/*     */   }
/*     */ 
/*     */   private void releaseInflater(Inflater paramInflater)
/*     */   {
/* 455 */     if (false == paramInflater.ended()) {
/* 456 */       paramInflater.reset();
/* 457 */       synchronized (this.inflaterCache) {
/* 458 */         this.inflaterCache.add(paramInflater);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 471 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Enumeration<? extends ZipEntry> entries()
/*     */   {
/* 480 */     ensureOpen();
/* 481 */     return new Enumeration() {
/* 482 */       private int i = 0;
/*     */ 
/* 484 */       public boolean hasMoreElements() { synchronized (ZipFile.this) {
/* 485 */           ZipFile.this.ensureOpen();
/* 486 */           return this.i < ZipFile.this.total;
/*     */         } }
/*     */ 
/*     */       public ZipEntry nextElement() throws NoSuchElementException {
/* 490 */         synchronized (ZipFile.this) {
/* 491 */           ZipFile.this.ensureOpen();
/* 492 */           if (this.i >= ZipFile.this.total) {
/* 493 */             throw new NoSuchElementException();
/*     */           }
/* 495 */           long l = ZipFile.getNextEntry(ZipFile.this.jzfile, this.i++);
/* 496 */           if (l == 0L)
/*     */           {
/* 498 */             if (ZipFile.this.closeRequested)
/* 499 */               localObject1 = "ZipFile concurrently closed";
/*     */             else {
/* 501 */               localObject1 = ZipFile.getZipMessage(ZipFile.this.jzfile);
/*     */             }
/* 503 */             throw new ZipError("jzentry == 0,\n jzfile = " + ZipFile.this.jzfile + ",\n total = " + ZipFile.this.total + ",\n name = " + ZipFile.this.name + ",\n i = " + this.i + ",\n message = " + (String)localObject1);
/*     */           }
/*     */ 
/* 511 */           Object localObject1 = ZipFile.this.getZipEntry(null, l);
/* 512 */           ZipFile.freeEntry(ZipFile.this.jzfile, l);
/* 513 */           return localObject1;
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private ZipEntry getZipEntry(String paramString, long paramLong) {
/* 520 */     ZipEntry localZipEntry = new ZipEntry();
/* 521 */     localZipEntry.flag = getEntryFlag(paramLong);
/* 522 */     if (paramString != null) {
/* 523 */       localZipEntry.name = paramString;
/*     */     } else {
/* 525 */       arrayOfByte = getEntryBytes(paramLong, 0);
/* 526 */       if ((!this.zc.isUTF8()) && ((localZipEntry.flag & 0x800) != 0))
/* 527 */         localZipEntry.name = this.zc.toStringUTF8(arrayOfByte, arrayOfByte.length);
/*     */       else {
/* 529 */         localZipEntry.name = this.zc.toString(arrayOfByte, arrayOfByte.length);
/*     */       }
/*     */     }
/* 532 */     localZipEntry.time = getEntryTime(paramLong);
/* 533 */     localZipEntry.crc = getEntryCrc(paramLong);
/* 534 */     localZipEntry.size = getEntrySize(paramLong);
/* 535 */     localZipEntry.csize = getEntryCSize(paramLong);
/* 536 */     localZipEntry.method = getEntryMethod(paramLong);
/* 537 */     localZipEntry.extra = getEntryBytes(paramLong, 1);
/* 538 */     byte[] arrayOfByte = getEntryBytes(paramLong, 2);
/* 539 */     if (arrayOfByte == null) {
/* 540 */       localZipEntry.comment = null;
/*     */     }
/* 542 */     else if ((!this.zc.isUTF8()) && ((localZipEntry.flag & 0x800) != 0))
/* 543 */       localZipEntry.comment = this.zc.toStringUTF8(arrayOfByte, arrayOfByte.length);
/*     */     else {
/* 545 */       localZipEntry.comment = this.zc.toString(arrayOfByte, arrayOfByte.length);
/*     */     }
/*     */ 
/* 548 */     return localZipEntry;
/*     */   }
/*     */ 
/*     */   private static native long getNextEntry(long paramLong, int paramInt);
/*     */ 
/*     */   public int size()
/*     */   {
/* 559 */     ensureOpen();
/* 560 */     return this.total;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 572 */     if (this.closeRequested)
/* 573 */       return;
/* 574 */     this.closeRequested = true;
/*     */ 
/* 576 */     synchronized (this)
/*     */     {
/* 578 */       synchronized (this.streams) {
/* 579 */         if (false == this.streams.isEmpty()) {
/* 580 */           HashMap localHashMap = new HashMap(this.streams);
/* 581 */           this.streams.clear();
/* 582 */           for (Map.Entry localEntry : localHashMap.entrySet()) {
/* 583 */             ((InputStream)localEntry.getKey()).close();
/* 584 */             Inflater localInflater = (Inflater)localEntry.getValue();
/* 585 */             if (localInflater != null) {
/* 586 */               localInflater.end();
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 594 */       synchronized (this.inflaterCache) {
/* 595 */         while (null != ( = (Inflater)this.inflaterCache.poll())) {
/* 596 */           ((Inflater)???).end();
/*     */         }
/*     */       }
/*     */ 
/* 600 */       if (this.jzfile != 0L)
/*     */       {
/* 602 */         long l = this.jzfile;
/* 603 */         this.jzfile = 0L;
/*     */ 
/* 605 */         close(l);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */     throws IOException
/*     */   {
/* 625 */     close();
/*     */   }
/*     */ 
/*     */   private static native void close(long paramLong);
/*     */ 
/*     */   private void ensureOpen() {
/* 631 */     if (this.closeRequested) {
/* 632 */       throw new IllegalStateException("zip file closed");
/*     */     }
/*     */ 
/* 635 */     if (this.jzfile == 0L)
/* 636 */       throw new IllegalStateException("The object is not initialized.");
/*     */   }
/*     */ 
/*     */   private void ensureOpenOrZipException() throws IOException
/*     */   {
/* 641 */     if (this.closeRequested)
/* 642 */       throw new ZipException("ZipFile closed");
/*     */   }
/*     */ 
/*     */   private static native long open(String paramString, int paramInt, long paramLong, boolean paramBoolean)
/*     */     throws IOException;
/*     */ 
/*     */   private static native int getTotal(long paramLong);
/*     */ 
/*     */   private static native int read(long paramLong1, long paramLong2, long paramLong3, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */   private static native long getEntryTime(long paramLong);
/*     */ 
/*     */   private static native long getEntryCrc(long paramLong);
/*     */ 
/*     */   private static native long getEntryCSize(long paramLong);
/*     */ 
/*     */   private static native long getEntrySize(long paramLong);
/*     */ 
/*     */   private static native int getEntryMethod(long paramLong);
/*     */ 
/*     */   private static native int getEntryFlag(long paramLong);
/*     */ 
/*     */   private static native byte[] getCommentBytes(long paramLong);
/*     */ 
/*     */   private static native byte[] getEntryBytes(long paramLong, int paramInt);
/*     */ 
/*     */   private static native String getZipMessage(long paramLong);
/*     */ 
/*     */   static
/*     */   {
/*  81 */     initIDs();
/*     */ 
/*  91 */     String str = VM.getSavedProperty("sun.zip.disableMemoryMapping");
/*     */   }
/*     */ 
/*     */   private class ZipFileInflaterInputStream extends InflaterInputStream
/*     */   {
/* 381 */     private volatile boolean closeRequested = false;
/* 382 */     private boolean eof = false;
/*     */     private final ZipFile.ZipFileInputStream zfin;
/*     */ 
/*     */     ZipFileInflaterInputStream(ZipFile.ZipFileInputStream paramInflater, Inflater paramInt, int arg4)
/*     */     {
/* 387 */       super(paramInt, i);
/* 388 */       this.zfin = paramInflater;
/*     */     }
/*     */ 
/*     */     public void close() throws IOException {
/* 392 */       if (this.closeRequested)
/* 393 */         return;
/* 394 */       this.closeRequested = true;
/*     */ 
/* 396 */       super.close();
/*     */       Inflater localInflater;
/* 398 */       synchronized (ZipFile.this.streams) {
/* 399 */         localInflater = (Inflater)ZipFile.this.streams.remove(this);
/*     */       }
/* 401 */       if (localInflater != null)
/* 402 */         ZipFile.this.releaseInflater(localInflater);
/*     */     }
/*     */ 
/*     */     protected void fill()
/*     */       throws IOException
/*     */     {
/* 410 */       if (this.eof) {
/* 411 */         throw new EOFException("Unexpected end of ZLIB input stream");
/*     */       }
/* 413 */       this.len = this.in.read(this.buf, 0, this.buf.length);
/* 414 */       if (this.len == -1) {
/* 415 */         this.buf[0] = 0;
/* 416 */         this.len = 1;
/* 417 */         this.eof = true;
/*     */       }
/* 419 */       this.inf.setInput(this.buf, 0, this.len);
/*     */     }
/*     */ 
/*     */     public int available() throws IOException {
/* 423 */       if (this.closeRequested)
/* 424 */         return 0;
/* 425 */       long l = this.zfin.size() - this.inf.getBytesWritten();
/* 426 */       return l > 2147483647L ? 2147483647 : (int)l;
/*     */     }
/*     */ 
/*     */     protected void finalize() throws Throwable
/*     */     {
/* 431 */       close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ZipFileInputStream extends InputStream
/*     */   {
/* 651 */     private volatile boolean closeRequested = false;
/*     */     protected long jzentry;
/* 658 */     private long pos = 0L;
/*     */     protected long rem;
/*     */     protected long size;
/*     */ 
/*     */     ZipFileInputStream(long arg2)
/*     */     {
/*     */       Object localObject;
/* 659 */       this.rem = ZipFile.getEntryCSize(localObject);
/* 660 */       this.size = ZipFile.getEntrySize(localObject);
/* 661 */       this.jzentry = localObject;
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 665 */       if (this.rem == 0L) {
/* 666 */         return -1;
/*     */       }
/* 668 */       if (paramInt2 <= 0) {
/* 669 */         return 0;
/*     */       }
/* 671 */       if (paramInt2 > this.rem) {
/* 672 */         paramInt2 = (int)this.rem;
/*     */       }
/* 674 */       synchronized (ZipFile.this) {
/* 675 */         ZipFile.this.ensureOpenOrZipException();
/*     */ 
/* 677 */         paramInt2 = ZipFile.read(ZipFile.this.jzfile, this.jzentry, this.pos, paramArrayOfByte, paramInt1, paramInt2);
/*     */       }
/*     */ 
/* 680 */       if (paramInt2 > 0) {
/* 681 */         this.pos += paramInt2;
/* 682 */         this.rem -= paramInt2;
/*     */       }
/* 684 */       if (this.rem == 0L) {
/* 685 */         close();
/*     */       }
/* 687 */       return paramInt2;
/*     */     }
/*     */ 
/*     */     public int read() throws IOException {
/* 691 */       byte[] arrayOfByte = new byte[1];
/* 692 */       if (read(arrayOfByte, 0, 1) == 1) {
/* 693 */         return arrayOfByte[0] & 0xFF;
/*     */       }
/* 695 */       return -1;
/*     */     }
/*     */ 
/*     */     public long skip(long paramLong)
/*     */     {
/* 700 */       if (paramLong > this.rem)
/* 701 */         paramLong = this.rem;
/* 702 */       this.pos += paramLong;
/* 703 */       this.rem -= paramLong;
/* 704 */       if (this.rem == 0L) {
/* 705 */         close();
/*     */       }
/* 707 */       return paramLong;
/*     */     }
/*     */ 
/*     */     public int available() {
/* 711 */       return this.rem > 2147483647L ? 2147483647 : (int)this.rem;
/*     */     }
/*     */ 
/*     */     public long size() {
/* 715 */       return this.size;
/*     */     }
/*     */ 
/*     */     public void close() {
/* 719 */       if (this.closeRequested)
/* 720 */         return;
/* 721 */       this.closeRequested = true;
/*     */ 
/* 723 */       this.rem = 0L;
/* 724 */       synchronized (ZipFile.this) {
/* 725 */         if ((this.jzentry != 0L) && (ZipFile.this.jzfile != 0L)) {
/* 726 */           ZipFile.freeEntry(ZipFile.this.jzfile, this.jzentry);
/* 727 */           this.jzentry = 0L;
/*     */         }
/*     */       }
/* 730 */       synchronized (ZipFile.this.streams) {
/* 731 */         ZipFile.this.streams.remove(this);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected void finalize() {
/* 736 */       close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.zip.ZipFile
 * JD-Core Version:    0.6.2
 */