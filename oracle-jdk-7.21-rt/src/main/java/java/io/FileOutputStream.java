/*     */ package java.io;
/*     */ 
/*     */ import java.nio.channels.FileChannel;
/*     */ import sun.nio.ch.FileChannelImpl;
/*     */ 
/*     */ public class FileOutputStream extends OutputStream
/*     */ {
/*     */   private final FileDescriptor fd;
/*     */   private final boolean append;
/*     */   private FileChannel channel;
/*  70 */   private final Object closeLock = new Object();
/*  71 */   private volatile boolean closed = false;
/*  72 */   private static final ThreadLocal<Boolean> runningFinalize = new ThreadLocal();
/*     */ 
/*     */   private static boolean isRunningFinalize()
/*     */   {
/*     */     Boolean localBoolean;
/*  77 */     if ((localBoolean = (Boolean)runningFinalize.get()) != null)
/*  78 */       return localBoolean.booleanValue();
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */   public FileOutputStream(String paramString)
/*     */     throws FileNotFoundException
/*     */   {
/* 104 */     this(paramString != null ? new File(paramString) : null, false);
/*     */   }
/*     */ 
/*     */   public FileOutputStream(String paramString, boolean paramBoolean)
/*     */     throws FileNotFoundException
/*     */   {
/* 136 */     this(paramString != null ? new File(paramString) : null, paramBoolean);
/*     */   }
/*     */ 
/*     */   public FileOutputStream(File paramFile)
/*     */     throws FileNotFoundException
/*     */   {
/* 165 */     this(paramFile, false);
/*     */   }
/*     */ 
/*     */   public FileOutputStream(File paramFile, boolean paramBoolean)
/*     */     throws FileNotFoundException
/*     */   {
/* 200 */     String str = paramFile != null ? paramFile.getPath() : null;
/* 201 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 202 */     if (localSecurityManager != null) {
/* 203 */       localSecurityManager.checkWrite(str);
/*     */     }
/* 205 */     if (str == null) {
/* 206 */       throw new NullPointerException();
/*     */     }
/* 208 */     this.fd = new FileDescriptor();
/* 209 */     this.append = paramBoolean;
/*     */ 
/* 211 */     this.fd.incrementAndGetUseCount();
/* 212 */     open(str, paramBoolean);
/*     */   }
/*     */ 
/*     */   public FileOutputStream(FileDescriptor paramFileDescriptor)
/*     */   {
/* 239 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 240 */     if (paramFileDescriptor == null) {
/* 241 */       throw new NullPointerException();
/*     */     }
/* 243 */     if (localSecurityManager != null) {
/* 244 */       localSecurityManager.checkWrite(paramFileDescriptor);
/*     */     }
/* 246 */     this.fd = paramFileDescriptor;
/* 247 */     this.append = false;
/*     */ 
/* 254 */     this.fd.incrementAndGetUseCount();
/*     */   }
/*     */ 
/*     */   private native void open(String paramString, boolean paramBoolean)
/*     */     throws FileNotFoundException;
/*     */ 
/*     */   private native void write(int paramInt, boolean paramBoolean)
/*     */     throws IOException;
/*     */ 
/*     */   public void write(int paramInt)
/*     */     throws IOException
/*     */   {
/* 282 */     write(paramInt, this.append);
/*     */   }
/*     */ 
/*     */   private native void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
/*     */     throws IOException;
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/* 305 */     writeBytes(paramArrayOfByte, 0, paramArrayOfByte.length, this.append);
/*     */   }
/*     */ 
/*     */   public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 318 */     writeBytes(paramArrayOfByte, paramInt1, paramInt2, this.append);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 335 */     synchronized (this.closeLock) {
/* 336 */       if (this.closed) {
/* 337 */         return;
/*     */       }
/* 339 */       this.closed = true;
/*     */     }
/*     */ 
/* 342 */     if (this.channel != null)
/*     */     {
/* 348 */       this.fd.decrementAndGetUseCount();
/* 349 */       this.channel.close();
/*     */     }
/*     */ 
/* 355 */     int i = this.fd.decrementAndGetUseCount();
/*     */ 
/* 361 */     if ((i <= 0) || (!isRunningFinalize()))
/* 362 */       close0();
/*     */   }
/*     */ 
/*     */   public final FileDescriptor getFD()
/*     */     throws IOException
/*     */   {
/* 377 */     if (this.fd != null) return this.fd;
/* 378 */     throw new IOException();
/*     */   }
/*     */ 
/*     */   public FileChannel getChannel()
/*     */   {
/* 399 */     synchronized (this) {
/* 400 */       if (this.channel == null) {
/* 401 */         this.channel = FileChannelImpl.open(this.fd, false, true, this.append, this);
/*     */ 
/* 408 */         this.fd.incrementAndGetUseCount();
/*     */       }
/* 410 */       return this.channel;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */     throws IOException
/*     */   {
/* 423 */     if (this.fd != null)
/* 424 */       if ((this.fd == FileDescriptor.out) || (this.fd == FileDescriptor.err)) {
/* 425 */         flush();
/*     */       }
/*     */       else
/*     */       {
/* 433 */         runningFinalize.set(Boolean.TRUE);
/*     */         try {
/* 435 */           close();
/*     */         } finally {
/* 437 */           runningFinalize.set(Boolean.FALSE);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private native void close0() throws IOException;
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   static
/*     */   {
/* 448 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.FileOutputStream
 * JD-Core Version:    0.6.2
 */