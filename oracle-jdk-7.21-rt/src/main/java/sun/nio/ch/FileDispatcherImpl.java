/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ 
/*     */ class FileDispatcherImpl extends FileDispatcher
/*     */ {
/*     */   FileDispatcherImpl(boolean paramBoolean)
/*     */   {
/*     */   }
/*     */ 
/*     */   FileDispatcherImpl()
/*     */   {
/*     */   }
/*     */ 
/*     */   int read(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*     */     throws IOException
/*     */   {
/*  46 */     return read0(paramFileDescriptor, paramLong, paramInt);
/*     */   }
/*     */ 
/*     */   int pread(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2, Object paramObject) throws IOException
/*     */   {
/*  51 */     return pread0(paramFileDescriptor, paramLong1, paramInt, paramLong2);
/*     */   }
/*     */ 
/*     */   long readv(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
/*  55 */     return readv0(paramFileDescriptor, paramLong, paramInt);
/*     */   }
/*     */ 
/*     */   int write(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
/*  59 */     return write0(paramFileDescriptor, paramLong, paramInt);
/*     */   }
/*     */ 
/*     */   int pwrite(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2, Object paramObject)
/*     */     throws IOException
/*     */   {
/*  65 */     return pwrite0(paramFileDescriptor, paramLong1, paramInt, paramLong2);
/*     */   }
/*     */ 
/*     */   long writev(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*     */     throws IOException
/*     */   {
/*  71 */     return writev0(paramFileDescriptor, paramLong, paramInt);
/*     */   }
/*     */ 
/*     */   int force(FileDescriptor paramFileDescriptor, boolean paramBoolean) throws IOException {
/*  75 */     return force0(paramFileDescriptor, paramBoolean);
/*     */   }
/*     */ 
/*     */   int truncate(FileDescriptor paramFileDescriptor, long paramLong) throws IOException {
/*  79 */     return truncate0(paramFileDescriptor, paramLong);
/*     */   }
/*     */ 
/*     */   long size(FileDescriptor paramFileDescriptor) throws IOException {
/*  83 */     return size0(paramFileDescriptor);
/*     */   }
/*     */ 
/*     */   int lock(FileDescriptor paramFileDescriptor, boolean paramBoolean1, long paramLong1, long paramLong2, boolean paramBoolean2)
/*     */     throws IOException
/*     */   {
/*  89 */     return lock0(paramFileDescriptor, paramBoolean1, paramLong1, paramLong2, paramBoolean2);
/*     */   }
/*     */ 
/*     */   void release(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2) throws IOException {
/*  93 */     release0(paramFileDescriptor, paramLong1, paramLong2);
/*     */   }
/*     */ 
/*     */   void close(FileDescriptor paramFileDescriptor) throws IOException {
/*  97 */     close0(paramFileDescriptor);
/*     */   }
/*     */ 
/*     */   void preClose(FileDescriptor paramFileDescriptor) throws IOException {
/* 101 */     preClose0(paramFileDescriptor);
/*     */   }
/*     */ 
/*     */   FileDescriptor duplicateForMapping(FileDescriptor paramFileDescriptor)
/*     */   {
/* 107 */     return new FileDescriptor();
/*     */   }
/*     */ 
/*     */   static native int read0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native int pread0(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2)
/*     */     throws IOException;
/*     */ 
/*     */   static native long readv0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native int write0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native int pwrite0(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2)
/*     */     throws IOException;
/*     */ 
/*     */   static native long writev0(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native int force0(FileDescriptor paramFileDescriptor, boolean paramBoolean)
/*     */     throws IOException;
/*     */ 
/*     */   static native int truncate0(FileDescriptor paramFileDescriptor, long paramLong)
/*     */     throws IOException;
/*     */ 
/*     */   static native long size0(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */ 
/*     */   static native int lock0(FileDescriptor paramFileDescriptor, boolean paramBoolean1, long paramLong1, long paramLong2, boolean paramBoolean2)
/*     */     throws IOException;
/*     */ 
/*     */   static native void release0(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2)
/*     */     throws IOException;
/*     */ 
/*     */   static native void close0(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */ 
/*     */   static native void preClose0(FileDescriptor paramFileDescriptor)
/*     */     throws IOException;
/*     */ 
/*     */   static native void closeIntFD(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   static native void init();
/*     */ 
/*     */   static
/*     */   {
/*  34 */     Util.load();
/*  35 */     init();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.FileDispatcherImpl
 * JD-Core Version:    0.6.2
 */