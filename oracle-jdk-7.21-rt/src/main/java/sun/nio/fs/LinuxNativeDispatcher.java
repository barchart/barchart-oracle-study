/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ class LinuxNativeDispatcher extends UnixNativeDispatcher
/*     */ {
/*     */   static long setmntent(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*     */     throws UnixException
/*     */   {
/*  42 */     NativeBuffer localNativeBuffer1 = NativeBuffers.asNativeBuffer(paramArrayOfByte1);
/*  43 */     NativeBuffer localNativeBuffer2 = NativeBuffers.asNativeBuffer(paramArrayOfByte2);
/*     */     try {
/*  45 */       return setmntent0(localNativeBuffer1.address(), localNativeBuffer2.address());
/*     */     } finally {
/*  47 */       localNativeBuffer2.release();
/*  48 */       localNativeBuffer1.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native long setmntent0(long paramLong1, long paramLong2)
/*     */     throws UnixException;
/*     */ 
/*     */   static native void endmntent(long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static int fgetxattr(int paramInt1, byte[] paramArrayOfByte, long paramLong, int paramInt2)
/*     */     throws UnixException
/*     */   {
/*  65 */     NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(paramArrayOfByte);
/*     */     try {
/*  67 */       return fgetxattr0(paramInt1, localNativeBuffer.address(), paramLong, paramInt2);
/*     */     } finally {
/*  69 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native int fgetxattr0(int paramInt1, long paramLong1, long paramLong2, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void fsetxattr(int paramInt1, byte[] paramArrayOfByte, long paramLong, int paramInt2)
/*     */     throws UnixException
/*     */   {
/*  82 */     NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(paramArrayOfByte);
/*     */     try {
/*  84 */       fsetxattr0(paramInt1, localNativeBuffer.address(), paramLong, paramInt2);
/*     */     } finally {
/*  86 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void fsetxattr0(int paramInt1, long paramLong1, long paramLong2, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static void fremovexattr(int paramInt, byte[] paramArrayOfByte)
/*     */     throws UnixException
/*     */   {
/*  98 */     NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(paramArrayOfByte);
/*     */     try {
/* 100 */       fremovexattr0(paramInt, localNativeBuffer.address());
/*     */     } finally {
/* 102 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void fremovexattr0(int paramInt, long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static native int flistxattr(int paramInt1, long paramLong, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   private static native void init();
/*     */ 
/*     */   static
/*     */   {
/* 119 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/* 121 */         System.loadLibrary("nio");
/* 122 */         return null;
/*     */       }
/*     */     });
/* 124 */     init();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.LinuxNativeDispatcher
 * JD-Core Version:    0.6.2
 */