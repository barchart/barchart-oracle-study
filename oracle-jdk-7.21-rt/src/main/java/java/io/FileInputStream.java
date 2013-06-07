/*     */ package java.io;
/*     */ 
/*     */ import java.nio.channels.FileChannel;
/*     */ import sun.nio.ch.FileChannelImpl;
/*     */ 
/*     */ public class FileInputStream extends InputStream
/*     */ {
/*     */   private final FileDescriptor fd;
/*  54 */   private FileChannel channel = null;
/*     */ 
/*  56 */   private final Object closeLock = new Object();
/*  57 */   private volatile boolean closed = false;
/*     */ 
/*  59 */   private static final ThreadLocal<Boolean> runningFinalize = new ThreadLocal();
/*     */ 
/*     */   private static boolean isRunningFinalize()
/*     */   {
/*     */     Boolean localBoolean;
/*  64 */     if ((localBoolean = (Boolean)runningFinalize.get()) != null)
/*  65 */       return localBoolean.booleanValue();
/*  66 */     return false;
/*     */   }
/*     */ 
/*     */   public FileInputStream(String paramString)
/*     */     throws FileNotFoundException
/*     */   {
/*  97 */     this(paramString != null ? new File(paramString) : null);
/*     */   }
/*     */ 
/*     */   public FileInputStream(File paramFile)
/*     */     throws FileNotFoundException
/*     */   {
/* 128 */     String str = paramFile != null ? paramFile.getPath() : null;
/* 129 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 130 */     if (localSecurityManager != null) {
/* 131 */       localSecurityManager.checkRead(str);
/*     */     }
/* 133 */     if (str == null) {
/* 134 */       throw new NullPointerException();
/*     */     }
/* 136 */     this.fd = new FileDescriptor();
/* 137 */     this.fd.incrementAndGetUseCount();
/* 138 */     open(str);
/*     */   }
/*     */ 
/*     */   public FileInputStream(FileDescriptor paramFileDescriptor)
/*     */   {
/* 166 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 167 */     if (paramFileDescriptor == null) {
/* 168 */       throw new NullPointerException();
/*     */     }
/* 170 */     if (localSecurityManager != null) {
/* 171 */       localSecurityManager.checkRead(paramFileDescriptor);
/*     */     }
/* 173 */     this.fd = paramFileDescriptor;
/*     */ 
/* 180 */     this.fd.incrementAndGetUseCount();
/*     */   }
/*     */ 
/*     */   private native void open(String paramString)
/*     */     throws FileNotFoundException;
/*     */ 
/*     */   public native int read()
/*     */     throws IOException;
/*     */ 
/*     */   private native int readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 220 */     return readBytes(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 242 */     return readBytes(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   public native long skip(long paramLong)
/*     */     throws IOException;
/*     */ 
/*     */   public native int available()
/*     */     throws IOException;
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 300 */     synchronized (this.closeLock) {
/* 301 */       if (this.closed) {
/* 302 */         return;
/*     */       }
/* 304 */       this.closed = true;
/*     */     }
/* 306 */     if (this.channel != null)
/*     */     {
/* 312 */       this.fd.decrementAndGetUseCount();
/* 313 */       this.channel.close();
/*     */     }
/*     */ 
/* 319 */     int i = this.fd.decrementAndGetUseCount();
/*     */ 
/* 325 */     if ((i <= 0) || (!isRunningFinalize()))
/* 326 */       close0();
/*     */   }
/*     */ 
/*     */   public final FileDescriptor getFD()
/*     */     throws IOException
/*     */   {
/* 341 */     if (this.fd != null) return this.fd;
/* 342 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   public FileChannel getChannel()
/*     */   {
/* 362 */     synchronized (this) {
/* 363 */       if (this.channel == null) {
/* 364 */         this.channel = FileChannelImpl.open(this.fd, true, false, this);
/*     */ 
/* 371 */         this.fd.incrementAndGetUseCount();
/*     */       }
/* 373 */       return this.channel;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   private native void close0()
/*     */     throws IOException;
/*     */ 
/*     */   protected void finalize()
/*     */     throws IOException
/*     */   {
/* 393 */     if ((this.fd != null) && (this.fd != FileDescriptor.in))
/*     */     {
/* 400 */       runningFinalize.set(Boolean.TRUE);
/*     */       try {
/* 402 */         close();
/*     */       } finally {
/* 404 */         runningFinalize.set(Boolean.FALSE);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 382 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.FileInputStream
 * JD-Core Version:    0.6.2
 */