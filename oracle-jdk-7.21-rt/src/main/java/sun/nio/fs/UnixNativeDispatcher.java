/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ class UnixNativeDispatcher
/*     */ {
/* 565 */   private static final boolean hasAtSysCalls = (i & 0x1) > 0;
/*     */   private static final int HAS_AT_SYSCALLS = 1;
/*     */ 
/*     */   private static NativeBuffer copyToNativeBuffer(UnixPath paramUnixPath)
/*     */   {
/*  40 */     byte[] arrayOfByte = paramUnixPath.getByteArrayForSysCalls();
/*  41 */     int i = arrayOfByte.length + 1;
/*  42 */     NativeBuffer localNativeBuffer = NativeBuffers.getNativeBufferFromCache(i);
/*  43 */     if (localNativeBuffer == null) {
/*  44 */       localNativeBuffer = NativeBuffers.allocNativeBuffer(i);
/*     */     }
/*  47 */     else if (localNativeBuffer.owner() == paramUnixPath) {
/*  48 */       return localNativeBuffer;
/*     */     }
/*  50 */     NativeBuffers.copyCStringToNativeBuffer(arrayOfByte, localNativeBuffer);
/*  51 */     localNativeBuffer.setOwner(paramUnixPath);
/*  52 */     return localNativeBuffer;
/*     */   }
/*     */ 
/*     */   static native byte[] getcwd();
/*     */ 
/*     */   static native int dup(int paramInt)
/*     */     throws UnixException;
/*     */ 
/*     */   static int open(UnixPath paramUnixPath, int paramInt1, int paramInt2)
/*     */     throws UnixException
/*     */   {
/*  69 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/*  71 */       return open0(localNativeBuffer.address(), paramInt1, paramInt2);
/*     */     } finally {
/*  73 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native int open0(long paramLong, int paramInt1, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static int openat(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
/*     */     throws UnixException
/*     */   {
/*  83 */     NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(paramArrayOfByte);
/*     */     try {
/*  85 */       return openat0(paramInt1, localNativeBuffer.address(), paramInt2, paramInt3);
/*     */     } finally {
/*  87 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native int openat0(int paramInt1, long paramLong, int paramInt2, int paramInt3)
/*     */     throws UnixException;
/*     */ 
/*     */   static native void close(int paramInt);
/*     */ 
/*     */   static long fopen(UnixPath paramUnixPath, String paramString)
/*     */     throws UnixException
/*     */   {
/* 102 */     NativeBuffer localNativeBuffer1 = copyToNativeBuffer(paramUnixPath);
/* 103 */     NativeBuffer localNativeBuffer2 = NativeBuffers.asNativeBuffer(paramString.getBytes());
/*     */     try {
/* 105 */       return fopen0(localNativeBuffer1.address(), localNativeBuffer2.address());
/*     */     } finally {
/* 107 */       localNativeBuffer2.release();
/* 108 */       localNativeBuffer1.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native long fopen0(long paramLong1, long paramLong2)
/*     */     throws UnixException;
/*     */ 
/*     */   static native void fclose(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static void link(UnixPath paramUnixPath1, UnixPath paramUnixPath2)
/*     */     throws UnixException
/*     */   {
/* 123 */     NativeBuffer localNativeBuffer1 = copyToNativeBuffer(paramUnixPath1);
/* 124 */     NativeBuffer localNativeBuffer2 = copyToNativeBuffer(paramUnixPath2);
/*     */     try {
/* 126 */       link0(localNativeBuffer1.address(), localNativeBuffer2.address());
/*     */     } finally {
/* 128 */       localNativeBuffer2.release();
/* 129 */       localNativeBuffer1.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void link0(long paramLong1, long paramLong2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void unlink(UnixPath paramUnixPath)
/*     */     throws UnixException
/*     */   {
/* 139 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 141 */       unlink0(localNativeBuffer.address());
/*     */     } finally {
/* 143 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void unlink0(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static void unlinkat(int paramInt1, byte[] paramArrayOfByte, int paramInt2) throws UnixException
/*     */   {
/* 152 */     NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(paramArrayOfByte);
/*     */     try {
/* 154 */       unlinkat0(paramInt1, localNativeBuffer.address(), paramInt2);
/*     */     } finally {
/* 156 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void unlinkat0(int paramInt1, long paramLong, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void mknod(UnixPath paramUnixPath, int paramInt, long paramLong)
/*     */     throws UnixException
/*     */   {
/* 166 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 168 */       mknod0(localNativeBuffer.address(), paramInt, paramLong);
/*     */     } finally {
/* 170 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void mknod0(long paramLong1, int paramInt, long paramLong2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void rename(UnixPath paramUnixPath1, UnixPath paramUnixPath2)
/*     */     throws UnixException
/*     */   {
/* 180 */     NativeBuffer localNativeBuffer1 = copyToNativeBuffer(paramUnixPath1);
/* 181 */     NativeBuffer localNativeBuffer2 = copyToNativeBuffer(paramUnixPath2);
/*     */     try {
/* 183 */       rename0(localNativeBuffer1.address(), localNativeBuffer2.address());
/*     */     } finally {
/* 185 */       localNativeBuffer2.release();
/* 186 */       localNativeBuffer1.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void rename0(long paramLong1, long paramLong2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void renameat(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2)
/*     */     throws UnixException
/*     */   {
/* 196 */     NativeBuffer localNativeBuffer1 = NativeBuffers.asNativeBuffer(paramArrayOfByte1);
/* 197 */     NativeBuffer localNativeBuffer2 = NativeBuffers.asNativeBuffer(paramArrayOfByte2);
/*     */     try {
/* 199 */       renameat0(paramInt1, localNativeBuffer1.address(), paramInt2, localNativeBuffer2.address());
/*     */     } finally {
/* 201 */       localNativeBuffer2.release();
/* 202 */       localNativeBuffer1.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void renameat0(int paramInt1, long paramLong1, int paramInt2, long paramLong2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void mkdir(UnixPath paramUnixPath, int paramInt)
/*     */     throws UnixException
/*     */   {
/* 212 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 214 */       mkdir0(localNativeBuffer.address(), paramInt);
/*     */     } finally {
/* 216 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void mkdir0(long paramLong, int paramInt)
/*     */     throws UnixException;
/*     */ 
/*     */   static void rmdir(UnixPath paramUnixPath) throws UnixException
/*     */   {
/* 225 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 227 */       rmdir0(localNativeBuffer.address());
/*     */     } finally {
/* 229 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void rmdir0(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static byte[] readlink(UnixPath paramUnixPath)
/*     */     throws UnixException
/*     */   {
/* 240 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 242 */       return readlink0(localNativeBuffer.address());
/*     */     } finally {
/* 244 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native byte[] readlink0(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static byte[] realpath(UnixPath paramUnixPath)
/*     */     throws UnixException
/*     */   {
/* 255 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 257 */       return realpath0(localNativeBuffer.address());
/*     */     } finally {
/* 259 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native byte[] realpath0(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static void symlink(byte[] paramArrayOfByte, UnixPath paramUnixPath) throws UnixException
/*     */   {
/* 268 */     NativeBuffer localNativeBuffer1 = NativeBuffers.asNativeBuffer(paramArrayOfByte);
/* 269 */     NativeBuffer localNativeBuffer2 = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 271 */       symlink0(localNativeBuffer1.address(), localNativeBuffer2.address());
/*     */     } finally {
/* 273 */       localNativeBuffer2.release();
/* 274 */       localNativeBuffer1.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void symlink0(long paramLong1, long paramLong2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void stat(UnixPath paramUnixPath, UnixFileAttributes paramUnixFileAttributes)
/*     */     throws UnixException
/*     */   {
/* 284 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 286 */       stat0(localNativeBuffer.address(), paramUnixFileAttributes);
/*     */     } finally {
/* 288 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void stat0(long paramLong, UnixFileAttributes paramUnixFileAttributes)
/*     */     throws UnixException;
/*     */ 
/*     */   static void lstat(UnixPath paramUnixPath, UnixFileAttributes paramUnixFileAttributes)
/*     */     throws UnixException
/*     */   {
/* 298 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 300 */       lstat0(localNativeBuffer.address(), paramUnixFileAttributes);
/*     */     } finally {
/* 302 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void lstat0(long paramLong, UnixFileAttributes paramUnixFileAttributes)
/*     */     throws UnixException;
/*     */ 
/*     */   static native void fstat(int paramInt, UnixFileAttributes paramUnixFileAttributes)
/*     */     throws UnixException;
/*     */ 
/*     */   static void fstatat(int paramInt1, byte[] paramArrayOfByte, int paramInt2, UnixFileAttributes paramUnixFileAttributes)
/*     */     throws UnixException
/*     */   {
/* 319 */     NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(paramArrayOfByte);
/*     */     try {
/* 321 */       fstatat0(paramInt1, localNativeBuffer.address(), paramInt2, paramUnixFileAttributes);
/*     */     } finally {
/* 323 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void fstatat0(int paramInt1, long paramLong, int paramInt2, UnixFileAttributes paramUnixFileAttributes)
/*     */     throws UnixException;
/*     */ 
/*     */   static void chown(UnixPath paramUnixPath, int paramInt1, int paramInt2)
/*     */     throws UnixException
/*     */   {
/* 333 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 335 */       chown0(localNativeBuffer.address(), paramInt1, paramInt2);
/*     */     } finally {
/* 337 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void chown0(long paramLong, int paramInt1, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void lchown(UnixPath paramUnixPath, int paramInt1, int paramInt2)
/*     */     throws UnixException
/*     */   {
/* 347 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 349 */       lchown0(localNativeBuffer.address(), paramInt1, paramInt2);
/*     */     } finally {
/* 351 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void lchown0(long paramLong, int paramInt1, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static native void fchown(int paramInt1, int paramInt2, int paramInt3)
/*     */     throws UnixException;
/*     */ 
/*     */   static void chmod(UnixPath paramUnixPath, int paramInt)
/*     */     throws UnixException
/*     */   {
/* 366 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 368 */       chmod0(localNativeBuffer.address(), paramInt);
/*     */     } finally {
/* 370 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void chmod0(long paramLong, int paramInt)
/*     */     throws UnixException;
/*     */ 
/*     */   static native void fchmod(int paramInt1, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void utimes(UnixPath paramUnixPath, long paramLong1, long paramLong2)
/*     */     throws UnixException
/*     */   {
/* 387 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 389 */       utimes0(localNativeBuffer.address(), paramLong1, paramLong2);
/*     */     } finally {
/* 391 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void utimes0(long paramLong1, long paramLong2, long paramLong3)
/*     */     throws UnixException;
/*     */ 
/*     */   static native void futimes(int paramInt, long paramLong1, long paramLong2)
/*     */     throws UnixException;
/*     */ 
/*     */   static long opendir(UnixPath paramUnixPath)
/*     */     throws UnixException
/*     */   {
/* 406 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 408 */       return opendir0(localNativeBuffer.address());
/*     */     } finally {
/* 410 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native long opendir0(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static native long fdopendir(int paramInt)
/*     */     throws UnixException;
/*     */ 
/*     */   static native void closedir(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static native byte[] readdir(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static native int read(int paramInt1, long paramLong, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static native int write(int paramInt1, long paramLong, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void access(UnixPath paramUnixPath, int paramInt)
/*     */     throws UnixException
/*     */   {
/* 447 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 449 */       access0(localNativeBuffer.address(), paramInt);
/*     */     } finally {
/* 451 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void access0(long paramLong, int paramInt)
/*     */     throws UnixException;
/*     */ 
/*     */   static native byte[] getpwuid(int paramInt)
/*     */     throws UnixException;
/*     */ 
/*     */   static native byte[] getgrgid(int paramInt)
/*     */     throws UnixException;
/*     */ 
/*     */   static int getpwnam(String paramString)
/*     */     throws UnixException
/*     */   {
/* 476 */     NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(paramString.getBytes());
/*     */     try {
/* 478 */       return getpwnam0(localNativeBuffer.address());
/*     */     } finally {
/* 480 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native int getpwnam0(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static int getgrnam(String paramString)
/*     */     throws UnixException
/*     */   {
/* 491 */     NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(paramString.getBytes());
/*     */     try {
/* 493 */       return getgrnam0(localNativeBuffer.address());
/*     */     } finally {
/* 495 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native int getgrnam0(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static native int getextmntent(long paramLong, UnixMountEntry paramUnixMountEntry)
/*     */     throws UnixException;
/*     */ 
/*     */   static void statvfs(UnixPath paramUnixPath, UnixFileStoreAttributes paramUnixFileStoreAttributes)
/*     */     throws UnixException
/*     */   {
/* 511 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 513 */       statvfs0(localNativeBuffer.address(), paramUnixFileStoreAttributes);
/*     */     } finally {
/* 515 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void statvfs0(long paramLong, UnixFileStoreAttributes paramUnixFileStoreAttributes)
/*     */     throws UnixException;
/*     */ 
/*     */   static long pathconf(UnixPath paramUnixPath, int paramInt)
/*     */     throws UnixException
/*     */   {
/* 525 */     NativeBuffer localNativeBuffer = copyToNativeBuffer(paramUnixPath);
/*     */     try {
/* 527 */       return pathconf0(localNativeBuffer.address(), paramInt);
/*     */     } finally {
/* 529 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native long pathconf0(long paramLong, int paramInt)
/*     */     throws UnixException;
/*     */ 
/*     */   static native long fpathconf(int paramInt1, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static native byte[] strerror(int paramInt);
/*     */ 
/*     */   static boolean supportsAtSysCalls()
/*     */   {
/* 548 */     return hasAtSysCalls;
/*     */   }
/*     */ 
/*     */   private static native int init();
/*     */ 
/*     */   static
/*     */   {
/* 558 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/* 560 */         System.loadLibrary("nio");
/* 561 */         return null;
/*     */       }
/*     */     });
/* 563 */     int i = init();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixNativeDispatcher
 * JD-Core Version:    0.6.2
 */