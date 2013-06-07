/*     */ package sun.nio.fs;
/*     */ 
/*     */ import com.sun.nio.file.ExtendedCopyOption;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.AtomicMoveNotSupportedException;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.DirectoryNotEmptyException;
/*     */ import java.nio.file.FileAlreadyExistsException;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.LinkPermission;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.nio.file.attribute.FileTime;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ class UnixCopyFile
/*     */ {
/*     */   private static void copyDirectory(UnixPath paramUnixPath1, UnixFileAttributes paramUnixFileAttributes, UnixPath paramUnixPath2, Flags paramFlags)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 134 */       UnixNativeDispatcher.mkdir(paramUnixPath2, paramUnixFileAttributes.mode());
/*     */     } catch (UnixException localUnixException1) {
/* 136 */       localUnixException1.rethrowAsIOException(paramUnixPath2);
/*     */     }
/*     */ 
/* 140 */     if ((!paramFlags.copyBasicAttributes) && (!paramFlags.copyPosixAttributes) && (!paramFlags.copyNonPosixAttributes))
/*     */     {
/* 142 */       return;
/*     */     }
/*     */ 
/* 146 */     int i = -1;
/*     */     try {
/* 148 */       i = UnixNativeDispatcher.open(paramUnixPath2, 0, 0);
/*     */     }
/*     */     catch (UnixException localUnixException2) {
/* 151 */       if ((paramFlags.copyNonPosixAttributes) && (paramFlags.failIfUnableToCopyNonPosix)) {
/*     */         try { UnixNativeDispatcher.rmdir(paramUnixPath2); } catch (UnixException localUnixException3) {
/* 153 */         }localUnixException2.rethrowAsIOException(paramUnixPath2);
/*     */       }
/*     */     }
/*     */ 
/* 157 */     int j = 0;
/*     */     try
/*     */     {
/* 160 */       if (paramFlags.copyPosixAttributes) {
/*     */         try {
/* 162 */           if (i >= 0) {
/* 163 */             UnixNativeDispatcher.fchown(i, paramUnixFileAttributes.uid(), paramUnixFileAttributes.gid());
/* 164 */             UnixNativeDispatcher.fchmod(i, paramUnixFileAttributes.mode());
/*     */           } else {
/* 166 */             UnixNativeDispatcher.chown(paramUnixPath2, paramUnixFileAttributes.uid(), paramUnixFileAttributes.gid());
/* 167 */             UnixNativeDispatcher.chmod(paramUnixPath2, paramUnixFileAttributes.mode());
/*     */           }
/*     */         }
/*     */         catch (UnixException localUnixException4) {
/* 171 */           if (paramFlags.failIfUnableToCopyPosix) {
/* 172 */             localUnixException4.rethrowAsIOException(paramUnixPath2);
/*     */           }
/*     */         }
/*     */       }
/* 176 */       if ((paramFlags.copyNonPosixAttributes) && (i >= 0)) {
/* 177 */         int k = -1;
/*     */         try {
/* 179 */           k = UnixNativeDispatcher.open(paramUnixPath1, 0, 0);
/*     */         } catch (UnixException localUnixException7) {
/* 181 */           if (paramFlags.failIfUnableToCopyNonPosix)
/* 182 */             localUnixException7.rethrowAsIOException(paramUnixPath1);
/*     */         }
/* 184 */         if (k >= 0) {
/* 185 */           paramUnixPath1.getFileSystem().copyNonPosixAttributes(k, i);
/* 186 */           UnixNativeDispatcher.close(k);
/*     */         }
/*     */       }
/*     */ 
/* 190 */       if (paramFlags.copyBasicAttributes) {
/*     */         try {
/* 192 */           if (i >= 0) {
/* 193 */             UnixNativeDispatcher.futimes(i, paramUnixFileAttributes.lastAccessTime().to(TimeUnit.MICROSECONDS), paramUnixFileAttributes.lastModifiedTime().to(TimeUnit.MICROSECONDS));
/*     */           }
/*     */           else
/*     */           {
/* 197 */             UnixNativeDispatcher.utimes(paramUnixPath2, paramUnixFileAttributes.lastAccessTime().to(TimeUnit.MICROSECONDS), paramUnixFileAttributes.lastModifiedTime().to(TimeUnit.MICROSECONDS));
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (UnixException localUnixException5)
/*     */         {
/* 203 */           if (paramFlags.failIfUnableToCopyBasic)
/* 204 */             localUnixException5.rethrowAsIOException(paramUnixPath2);
/*     */         }
/*     */       }
/* 207 */       j = 1;
/*     */     } finally {
/* 209 */       if (i >= 0)
/* 210 */         UnixNativeDispatcher.close(i);
/* 211 */       if (j == 0)
/*     */         try {
/* 213 */           UnixNativeDispatcher.rmdir(paramUnixPath2);
/*     */         }
/*     */         catch (UnixException localUnixException8)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void copyFile(UnixPath paramUnixPath1, UnixFileAttributes paramUnixFileAttributes, UnixPath paramUnixPath2, Flags paramFlags, long paramLong)
/*     */     throws IOException
/*     */   {
/* 226 */     int i = -1;
/*     */     try {
/* 228 */       i = UnixNativeDispatcher.open(paramUnixPath1, 0, 0);
/*     */     } catch (UnixException localUnixException1) {
/* 230 */       localUnixException1.rethrowAsIOException(paramUnixPath1);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 235 */       int j = -1;
/*     */       try {
/* 237 */         j = UnixNativeDispatcher.open(paramUnixPath2, 193, paramUnixFileAttributes.mode());
/*     */       }
/*     */       catch (UnixException localUnixException2)
/*     */       {
/* 243 */         localUnixException2.rethrowAsIOException(paramUnixPath2);
/*     */       }
/*     */ 
/* 247 */       int k = 0;
/*     */       try
/*     */       {
/*     */         try {
/* 251 */           transfer(j, i, paramLong);
/*     */         } catch (UnixException localUnixException3) {
/* 253 */           localUnixException3.rethrowAsIOException(paramUnixPath1, paramUnixPath2);
/*     */         }
/*     */ 
/* 256 */         if (paramFlags.copyPosixAttributes) {
/*     */           try {
/* 258 */             UnixNativeDispatcher.fchown(j, paramUnixFileAttributes.uid(), paramUnixFileAttributes.gid());
/* 259 */             UnixNativeDispatcher.fchmod(j, paramUnixFileAttributes.mode());
/*     */           } catch (UnixException localUnixException4) {
/* 261 */             if (paramFlags.failIfUnableToCopyPosix) {
/* 262 */               localUnixException4.rethrowAsIOException(paramUnixPath2);
/*     */             }
/*     */           }
/*     */         }
/* 266 */         if (paramFlags.copyNonPosixAttributes) {
/* 267 */           paramUnixPath1.getFileSystem().copyNonPosixAttributes(i, j);
/*     */         }
/*     */ 
/* 270 */         if (paramFlags.copyBasicAttributes) {
/*     */           try {
/* 272 */             UnixNativeDispatcher.futimes(j, paramUnixFileAttributes.lastAccessTime().to(TimeUnit.MICROSECONDS), paramUnixFileAttributes.lastModifiedTime().to(TimeUnit.MICROSECONDS));
/*     */           }
/*     */           catch (UnixException localUnixException5)
/*     */           {
/* 276 */             if (paramFlags.failIfUnableToCopyBasic)
/* 277 */               localUnixException5.rethrowAsIOException(paramUnixPath2);
/*     */           }
/*     */         }
/* 280 */         k = 1;
/*     */       } finally {
/* 282 */         UnixNativeDispatcher.close(j);
/*     */ 
/* 285 */         if (k == 0)
/*     */           try {
/* 287 */             UnixNativeDispatcher.unlink(paramUnixPath2);
/*     */           } catch (UnixException localUnixException7) {
/*     */           }
/*     */       }
/*     */     } finally {
/* 292 */       UnixNativeDispatcher.close(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void copyLink(UnixPath paramUnixPath1, UnixFileAttributes paramUnixFileAttributes, UnixPath paramUnixPath2, Flags paramFlags)
/*     */     throws IOException
/*     */   {
/* 303 */     byte[] arrayOfByte = null;
/*     */     try {
/* 305 */       arrayOfByte = UnixNativeDispatcher.readlink(paramUnixPath1);
/*     */     } catch (UnixException localUnixException1) {
/* 307 */       localUnixException1.rethrowAsIOException(paramUnixPath1);
/*     */     }
/*     */     try {
/* 310 */       UnixNativeDispatcher.symlink(arrayOfByte, paramUnixPath2);
/*     */ 
/* 312 */       if (paramFlags.copyPosixAttributes)
/*     */         try {
/* 314 */           UnixNativeDispatcher.lchown(paramUnixPath2, paramUnixFileAttributes.uid(), paramUnixFileAttributes.gid());
/*     */         }
/*     */         catch (UnixException localUnixException2) {
/*     */         }
/*     */     }
/*     */     catch (UnixException localUnixException3) {
/* 320 */       localUnixException3.rethrowAsIOException(paramUnixPath2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void copySpecial(UnixPath paramUnixPath1, UnixFileAttributes paramUnixFileAttributes, UnixPath paramUnixPath2, Flags paramFlags)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 332 */       UnixNativeDispatcher.mknod(paramUnixPath2, paramUnixFileAttributes.mode(), paramUnixFileAttributes.rdev());
/*     */     } catch (UnixException localUnixException1) {
/* 334 */       localUnixException1.rethrowAsIOException(paramUnixPath2);
/*     */     }
/* 336 */     int i = 0;
/*     */     try {
/* 338 */       if (paramFlags.copyPosixAttributes) {
/*     */         try {
/* 340 */           UnixNativeDispatcher.chown(paramUnixPath2, paramUnixFileAttributes.uid(), paramUnixFileAttributes.gid());
/* 341 */           UnixNativeDispatcher.chmod(paramUnixPath2, paramUnixFileAttributes.mode());
/*     */         } catch (UnixException localUnixException2) {
/* 343 */           if (paramFlags.failIfUnableToCopyPosix)
/* 344 */             localUnixException2.rethrowAsIOException(paramUnixPath2);
/*     */         }
/*     */       }
/* 347 */       if (paramFlags.copyBasicAttributes) {
/*     */         try {
/* 349 */           UnixNativeDispatcher.utimes(paramUnixPath2, paramUnixFileAttributes.lastAccessTime().to(TimeUnit.MICROSECONDS), paramUnixFileAttributes.lastModifiedTime().to(TimeUnit.MICROSECONDS));
/*     */         }
/*     */         catch (UnixException localUnixException3)
/*     */         {
/* 353 */           if (paramFlags.failIfUnableToCopyBasic)
/* 354 */             localUnixException3.rethrowAsIOException(paramUnixPath2);
/*     */         }
/*     */       }
/* 357 */       i = 1;
/*     */     } finally {
/* 359 */       if (i == 0) try {
/* 360 */           UnixNativeDispatcher.unlink(paramUnixPath2);
/*     */         }
/*     */         catch (UnixException localUnixException5)
/*     */         {
/*     */         } 
/*     */     }
/*     */   }
/*     */ 
/*     */   static void move(UnixPath paramUnixPath1, UnixPath paramUnixPath2, CopyOption[] paramArrayOfCopyOption)
/*     */     throws IOException
/*     */   {
/* 370 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 371 */     if (localSecurityManager != null) {
/* 372 */       paramUnixPath1.checkWrite();
/* 373 */       paramUnixPath2.checkWrite();
/*     */     }
/*     */ 
/* 377 */     Flags localFlags = Flags.fromMoveOptions(paramArrayOfCopyOption);
/*     */ 
/* 380 */     if (localFlags.atomicMove) {
/*     */       try {
/* 382 */         UnixNativeDispatcher.rename(paramUnixPath1, paramUnixPath2);
/*     */       } catch (UnixException localUnixException1) {
/* 384 */         if (localUnixException1.errno() == 18) {
/* 385 */           throw new AtomicMoveNotSupportedException(paramUnixPath1.getPathForExecptionMessage(), paramUnixPath2.getPathForExecptionMessage(), localUnixException1.errorString());
/*     */         }
/*     */ 
/* 390 */         localUnixException1.rethrowAsIOException(paramUnixPath1, paramUnixPath2);
/*     */       }
/* 392 */       return;
/*     */     }
/*     */ 
/* 396 */     UnixFileAttributes localUnixFileAttributes1 = null;
/* 397 */     UnixFileAttributes localUnixFileAttributes2 = null;
/*     */     try
/*     */     {
/* 401 */       localUnixFileAttributes1 = UnixFileAttributes.get(paramUnixPath1, false);
/*     */     } catch (UnixException localUnixException2) {
/* 403 */       localUnixException2.rethrowAsIOException(paramUnixPath1);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 408 */       localUnixFileAttributes2 = UnixFileAttributes.get(paramUnixPath2, false);
/*     */     }
/*     */     catch (UnixException localUnixException3) {
/*     */     }
/* 412 */     int i = localUnixFileAttributes2 != null ? 1 : 0;
/*     */ 
/* 418 */     if (i != 0) {
/* 419 */       if (localUnixFileAttributes1.isSameFile(localUnixFileAttributes2))
/* 420 */         return;
/* 421 */       if (!localFlags.replaceExisting) {
/* 422 */         throw new FileAlreadyExistsException(paramUnixPath2.getPathForExecptionMessage());
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 428 */         if (localUnixFileAttributes2.isDirectory())
/* 429 */           UnixNativeDispatcher.rmdir(paramUnixPath2);
/*     */         else
/* 431 */           UnixNativeDispatcher.unlink(paramUnixPath2);
/*     */       }
/*     */       catch (UnixException localUnixException4)
/*     */       {
/* 435 */         if ((localUnixFileAttributes2.isDirectory()) && ((localUnixException4.errno() == 17) || (localUnixException4.errno() == 39)))
/*     */         {
/* 438 */           throw new DirectoryNotEmptyException(paramUnixPath2.getPathForExecptionMessage());
/*     */         }
/*     */ 
/* 441 */         localUnixException4.rethrowAsIOException(paramUnixPath2);
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 447 */       UnixNativeDispatcher.rename(paramUnixPath1, paramUnixPath2);
/* 448 */       return;
/*     */     } catch (UnixException localUnixException5) {
/* 450 */       if ((localUnixException5.errno() != 18) && (localUnixException5.errno() != 21)) {
/* 451 */         localUnixException5.rethrowAsIOException(paramUnixPath1, paramUnixPath2);
/*     */       }
/*     */ 
/* 456 */       if (localUnixFileAttributes1.isDirectory()) {
/* 457 */         copyDirectory(paramUnixPath1, localUnixFileAttributes1, paramUnixPath2, localFlags);
/*     */       }
/* 459 */       else if (localUnixFileAttributes1.isSymbolicLink()) {
/* 460 */         copyLink(paramUnixPath1, localUnixFileAttributes1, paramUnixPath2, localFlags);
/*     */       }
/* 462 */       else if (localUnixFileAttributes1.isDevice())
/* 463 */         copySpecial(paramUnixPath1, localUnixFileAttributes1, paramUnixPath2, localFlags);
/*     */       else {
/* 465 */         copyFile(paramUnixPath1, localUnixFileAttributes1, paramUnixPath2, localFlags, 0L);
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 472 */         if (localUnixFileAttributes1.isDirectory())
/* 473 */           UnixNativeDispatcher.rmdir(paramUnixPath1);
/*     */         else
/* 475 */           UnixNativeDispatcher.unlink(paramUnixPath1);
/*     */       }
/*     */       catch (UnixException localUnixException6)
/*     */       {
/*     */         try
/*     */         {
/* 481 */           if (localUnixFileAttributes1.isDirectory())
/* 482 */             UnixNativeDispatcher.rmdir(paramUnixPath2);
/*     */           else
/* 484 */             UnixNativeDispatcher.unlink(paramUnixPath2);
/*     */         }
/*     */         catch (UnixException localUnixException7) {
/*     */         }
/* 488 */         if ((localUnixFileAttributes1.isDirectory()) && ((localUnixException6.errno() == 17) || (localUnixException6.errno() == 39)))
/*     */         {
/* 491 */           throw new DirectoryNotEmptyException(paramUnixPath1.getPathForExecptionMessage());
/*     */         }
/*     */ 
/* 494 */         localUnixException6.rethrowAsIOException(paramUnixPath1);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void copy(UnixPath paramUnixPath1, final UnixPath paramUnixPath2, CopyOption[] paramArrayOfCopyOption)
/*     */     throws IOException
/*     */   {
/* 504 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 505 */     if (localSecurityManager != null) {
/* 506 */       paramUnixPath1.checkRead();
/* 507 */       paramUnixPath2.checkWrite();
/*     */     }
/*     */ 
/* 511 */     final Flags localFlags = Flags.fromCopyOptions(paramArrayOfCopyOption);
/*     */ 
/* 513 */     UnixFileAttributes localUnixFileAttributes1 = null;
/* 514 */     UnixFileAttributes localUnixFileAttributes2 = null;
/*     */     try
/*     */     {
/* 518 */       localUnixFileAttributes1 = UnixFileAttributes.get(paramUnixPath1, localFlags.followLinks);
/*     */     } catch (UnixException localUnixException1) {
/* 520 */       localUnixException1.rethrowAsIOException(paramUnixPath1);
/*     */     }
/*     */ 
/* 524 */     if ((localSecurityManager != null) && (localUnixFileAttributes1.isSymbolicLink())) {
/* 525 */       localSecurityManager.checkPermission(new LinkPermission("symbolic"));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 530 */       localUnixFileAttributes2 = UnixFileAttributes.get(paramUnixPath2, false);
/*     */     }
/*     */     catch (UnixException localUnixException2) {
/*     */     }
/* 534 */     int i = localUnixFileAttributes2 != null ? 1 : 0;
/*     */ 
/* 540 */     if (i != 0) {
/* 541 */       if (localUnixFileAttributes1.isSameFile(localUnixFileAttributes2))
/* 542 */         return;
/* 543 */       if (!localFlags.replaceExisting)
/* 544 */         throw new FileAlreadyExistsException(paramUnixPath2.getPathForExecptionMessage());
/*     */       try
/*     */       {
/* 547 */         if (localUnixFileAttributes2.isDirectory())
/* 548 */           UnixNativeDispatcher.rmdir(paramUnixPath2);
/*     */         else
/* 550 */           UnixNativeDispatcher.unlink(paramUnixPath2);
/*     */       }
/*     */       catch (UnixException localUnixException3)
/*     */       {
/* 554 */         if ((localUnixFileAttributes2.isDirectory()) && ((localUnixException3.errno() == 17) || (localUnixException3.errno() == 39)))
/*     */         {
/* 557 */           throw new DirectoryNotEmptyException(paramUnixPath2.getPathForExecptionMessage());
/*     */         }
/*     */ 
/* 560 */         localUnixException3.rethrowAsIOException(paramUnixPath2);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 565 */     if (localUnixFileAttributes1.isDirectory()) {
/* 566 */       copyDirectory(paramUnixPath1, localUnixFileAttributes1, paramUnixPath2, localFlags);
/* 567 */       return;
/*     */     }
/* 569 */     if (localUnixFileAttributes1.isSymbolicLink()) {
/* 570 */       copyLink(paramUnixPath1, localUnixFileAttributes1, paramUnixPath2, localFlags);
/* 571 */       return;
/*     */     }
/* 573 */     if (!localFlags.interruptible)
/*     */     {
/* 575 */       copyFile(paramUnixPath1, localUnixFileAttributes1, paramUnixPath2, localFlags, 0L);
/* 576 */       return;
/*     */     }
/*     */ 
/* 580 */     final UnixFileAttributes localUnixFileAttributes3 = localUnixFileAttributes1;
/* 581 */     Cancellable local1 = new Cancellable() {
/*     */       public void implRun() throws IOException {
/* 583 */         UnixCopyFile.copyFile(this.val$source, localUnixFileAttributes3, paramUnixPath2, localFlags, addressToPollForCancel());
/*     */       }
/*     */     };
/*     */     try
/*     */     {
/* 588 */       Cancellable.runInterruptibly(local1);
/*     */     } catch (ExecutionException localExecutionException) {
/* 590 */       Throwable localThrowable = localExecutionException.getCause();
/* 591 */       if ((localThrowable instanceof IOException))
/* 592 */         throw ((IOException)localThrowable);
/* 593 */       throw new IOException(localThrowable);
/*     */     }
/*     */   }
/*     */ 
/*     */   static native void transfer(int paramInt1, int paramInt2, long paramLong)
/*     */     throws UnixException;
/*     */ 
/*     */   static
/*     */   {
/* 603 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 606 */         System.loadLibrary("nio");
/* 607 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static class Flags
/*     */   {
/*     */     boolean replaceExisting;
/*     */     boolean atomicMove;
/*     */     boolean followLinks;
/*     */     boolean interruptible;
/*     */     boolean copyBasicAttributes;
/*     */     boolean copyPosixAttributes;
/*     */     boolean copyNonPosixAttributes;
/*     */     boolean failIfUnableToCopyBasic;
/*     */     boolean failIfUnableToCopyPosix;
/*     */     boolean failIfUnableToCopyNonPosix;
/*     */ 
/*     */     static Flags fromCopyOptions(CopyOption[] paramArrayOfCopyOption)
/*     */     {
/*  65 */       Flags localFlags = new Flags();
/*  66 */       localFlags.followLinks = true;
/*  67 */       for (CopyOption localCopyOption : paramArrayOfCopyOption)
/*  68 */         if (localCopyOption == StandardCopyOption.REPLACE_EXISTING) {
/*  69 */           localFlags.replaceExisting = true;
/*     */         }
/*  72 */         else if (localCopyOption == LinkOption.NOFOLLOW_LINKS) {
/*  73 */           localFlags.followLinks = false;
/*     */         }
/*  76 */         else if (localCopyOption == StandardCopyOption.COPY_ATTRIBUTES)
/*     */         {
/*  79 */           localFlags.copyBasicAttributes = true;
/*  80 */           localFlags.copyPosixAttributes = true;
/*  81 */           localFlags.copyNonPosixAttributes = true;
/*  82 */           localFlags.failIfUnableToCopyBasic = true;
/*     */         }
/*  85 */         else if (localCopyOption == ExtendedCopyOption.INTERRUPTIBLE) {
/*  86 */           localFlags.interruptible = true;
/*     */         }
/*     */         else {
/*  89 */           if (localCopyOption == null)
/*  90 */             throw new NullPointerException();
/*  91 */           throw new UnsupportedOperationException("Unsupported copy option");
/*     */         }
/*  93 */       return localFlags;
/*     */     }
/*     */ 
/*     */     static Flags fromMoveOptions(CopyOption[] paramArrayOfCopyOption) {
/*  97 */       Flags localFlags = new Flags();
/*  98 */       for (CopyOption localCopyOption : paramArrayOfCopyOption) {
/*  99 */         if (localCopyOption == StandardCopyOption.ATOMIC_MOVE) {
/* 100 */           localFlags.atomicMove = true;
/*     */         }
/* 103 */         else if (localCopyOption == StandardCopyOption.REPLACE_EXISTING) {
/* 104 */           localFlags.replaceExisting = true;
/*     */         }
/* 107 */         else if (localCopyOption != LinkOption.NOFOLLOW_LINKS)
/*     */         {
/* 111 */           if (localCopyOption == null)
/* 112 */             throw new NullPointerException();
/* 113 */           throw new UnsupportedOperationException("Unsupported copy option");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 118 */       localFlags.copyBasicAttributes = true;
/* 119 */       localFlags.copyPosixAttributes = true;
/* 120 */       localFlags.copyNonPosixAttributes = true;
/* 121 */       localFlags.failIfUnableToCopyBasic = true;
/* 122 */       return localFlags;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixCopyFile
 * JD-Core Version:    0.6.2
 */