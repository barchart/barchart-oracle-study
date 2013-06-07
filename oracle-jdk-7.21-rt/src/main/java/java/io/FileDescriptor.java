/*     */ package java.io;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import sun.misc.JavaIOFileDescriptorAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ 
/*     */ public final class FileDescriptor
/*     */ {
/*     */   private int fd;
/*     */   private AtomicInteger useCount;
/*  77 */   public static final FileDescriptor in = new FileDescriptor(0);
/*     */ 
/*  85 */   public static final FileDescriptor out = new FileDescriptor(1);
/*     */ 
/*  94 */   public static final FileDescriptor err = new FileDescriptor(2);
/*     */ 
/*     */   public FileDescriptor()
/*     */   {
/*  61 */     this.fd = -1;
/*  62 */     this.useCount = new AtomicInteger();
/*     */   }
/*     */ 
/*     */   private FileDescriptor(int paramInt) {
/*  66 */     this.fd = paramInt;
/*  67 */     this.useCount = new AtomicInteger();
/*     */   }
/*     */ 
/*     */   public boolean valid()
/*     */   {
/* 104 */     return this.fd != -1;
/*     */   }
/*     */ 
/*     */   public native void sync()
/*     */     throws SyncFailedException;
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   int incrementAndGetUseCount()
/*     */   {
/* 170 */     return this.useCount.incrementAndGet();
/*     */   }
/*     */ 
/*     */   int decrementAndGetUseCount() {
/* 174 */     return this.useCount.decrementAndGet();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 141 */     initIDs();
/*     */ 
/* 146 */     SharedSecrets.setJavaIOFileDescriptorAccess(new JavaIOFileDescriptorAccess()
/*     */     {
/*     */       public void set(FileDescriptor paramAnonymousFileDescriptor, int paramAnonymousInt) {
/* 149 */         paramAnonymousFileDescriptor.fd = paramAnonymousInt;
/*     */       }
/*     */ 
/*     */       public int get(FileDescriptor paramAnonymousFileDescriptor) {
/* 153 */         return paramAnonymousFileDescriptor.fd;
/*     */       }
/*     */ 
/*     */       public void setHandle(FileDescriptor paramAnonymousFileDescriptor, long paramAnonymousLong) {
/* 157 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       public long getHandle(FileDescriptor paramAnonymousFileDescriptor) {
/* 161 */         throw new UnsupportedOperationException();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.FileDescriptor
 * JD-Core Version:    0.6.2
 */