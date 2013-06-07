/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.AtomicMoveNotSupportedException;
/*     */ import java.nio.file.ClosedDirectoryStreamException;
/*     */ import java.nio.file.DirectoryNotEmptyException;
/*     */ import java.nio.file.DirectoryStream.Filter;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.NotDirectoryException;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.ProviderMismatchException;
/*     */ import java.nio.file.SecureDirectoryStream;
/*     */ import java.nio.file.attribute.BasicFileAttributeView;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.nio.file.attribute.FileAttributeView;
/*     */ import java.nio.file.attribute.FileOwnerAttributeView;
/*     */ import java.nio.file.attribute.FileTime;
/*     */ import java.nio.file.attribute.GroupPrincipal;
/*     */ import java.nio.file.attribute.PosixFileAttributeView;
/*     */ import java.nio.file.attribute.PosixFileAttributes;
/*     */ import java.nio.file.attribute.PosixFilePermission;
/*     */ import java.nio.file.attribute.UserPrincipal;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ 
/*     */ class UnixSecureDirectoryStream
/*     */   implements SecureDirectoryStream<Path>
/*     */ {
/*     */   private final UnixDirectoryStream ds;
/*     */   private final int dfd;
/*     */ 
/*     */   UnixSecureDirectoryStream(UnixPath paramUnixPath, long paramLong, int paramInt, DirectoryStream.Filter<? super Path> paramFilter)
/*     */   {
/*  53 */     this.ds = new UnixDirectoryStream(paramUnixPath, paramLong, paramFilter);
/*  54 */     this.dfd = paramInt;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  61 */     this.ds.writeLock().lock();
/*     */     try {
/*  63 */       if (this.ds.closeImpl())
/*  64 */         UnixNativeDispatcher.close(this.dfd);
/*     */     }
/*     */     finally {
/*  67 */       this.ds.writeLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Iterator<Path> iterator()
/*     */   {
/*  73 */     return this.ds.iterator(this);
/*     */   }
/*     */ 
/*     */   private UnixPath getName(Path paramPath) {
/*  77 */     if (paramPath == null)
/*  78 */       throw new NullPointerException();
/*  79 */     if (!(paramPath instanceof UnixPath))
/*  80 */       throw new ProviderMismatchException();
/*  81 */     return (UnixPath)paramPath;
/*     */   }
/*     */ 
/*     */   public SecureDirectoryStream<Path> newDirectoryStream(Path paramPath, LinkOption[] paramArrayOfLinkOption)
/*     */     throws IOException
/*     */   {
/*  92 */     UnixPath localUnixPath1 = getName(paramPath);
/*  93 */     UnixPath localUnixPath2 = this.ds.directory().resolve(localUnixPath1);
/*  94 */     boolean bool = Util.followLinks(paramArrayOfLinkOption);
/*     */ 
/*  97 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  98 */     if (localSecurityManager != null) {
/*  99 */       localUnixPath2.checkRead();
/*     */     }
/*     */ 
/* 102 */     this.ds.readLock().lock();
/*     */     try {
/* 104 */       if (!this.ds.isOpen()) {
/* 105 */         throw new ClosedDirectoryStreamException();
/*     */       }
/*     */ 
/* 108 */       int i = -1;
/* 109 */       int j = -1;
/* 110 */       long l = 0L;
/*     */       try {
/* 112 */         int k = 0;
/* 113 */         if (!bool)
/* 114 */           k |= 131072;
/* 115 */         i = UnixNativeDispatcher.openat(this.dfd, localUnixPath1.asByteArray(), k, 0);
/* 116 */         j = UnixNativeDispatcher.dup(i);
/* 117 */         l = UnixNativeDispatcher.fdopendir(i);
/*     */       } catch (UnixException localUnixException) {
/* 119 */         if (i != -1)
/* 120 */           UnixNativeDispatcher.close(i);
/* 121 */         if (j != -1)
/* 122 */           UnixNativeDispatcher.close(j);
/* 123 */         if (localUnixException.errno() == 20)
/* 124 */           throw new NotDirectoryException(localUnixPath1.toString());
/* 125 */         localUnixException.rethrowAsIOException(localUnixPath1);
/*     */       }
/* 127 */       return new UnixSecureDirectoryStream(localUnixPath2, l, j, null);
/*     */     } finally {
/* 129 */       this.ds.readLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public SeekableByteChannel newByteChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>[] paramArrayOfFileAttribute)
/*     */     throws IOException
/*     */   {
/* 142 */     UnixPath localUnixPath = getName(paramPath);
/*     */ 
/* 144 */     int i = UnixFileModeAttribute.toUnixMode(438, paramArrayOfFileAttribute);
/*     */ 
/* 148 */     String str = this.ds.directory().resolve(localUnixPath).getPathForPermissionCheck();
/*     */ 
/* 150 */     this.ds.readLock().lock();
/*     */     try {
/* 152 */       if (!this.ds.isOpen())
/* 153 */         throw new ClosedDirectoryStreamException();
/*     */       try {
/* 155 */         return UnixChannelFactory.newFileChannel(this.dfd, localUnixPath, str, paramSet, i);
/*     */       } catch (UnixException localUnixException) {
/* 157 */         localUnixException.rethrowAsIOException(localUnixPath);
/* 158 */         return null;
/*     */       }
/*     */     } finally {
/* 161 */       this.ds.readLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void implDelete(Path paramPath, boolean paramBoolean, int paramInt)
/*     */     throws IOException
/*     */   {
/* 172 */     UnixPath localUnixPath = getName(paramPath);
/*     */ 
/* 175 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 176 */     if (localSecurityManager != null) {
/* 177 */       this.ds.directory().resolve(localUnixPath).checkDelete();
/*     */     }
/*     */ 
/* 180 */     this.ds.readLock().lock();
/*     */     try {
/* 182 */       if (!this.ds.isOpen()) {
/* 183 */         throw new ClosedDirectoryStreamException();
/*     */       }
/* 185 */       if (!paramBoolean)
/*     */       {
/* 190 */         UnixFileAttributes localUnixFileAttributes = null;
/*     */         try {
/* 192 */           localUnixFileAttributes = UnixFileAttributes.get(this.dfd, localUnixPath, false);
/*     */         } catch (UnixException localUnixException2) {
/* 194 */           localUnixException2.rethrowAsIOException(localUnixPath);
/*     */         }
/* 196 */         paramInt = localUnixFileAttributes.isDirectory() ? 512 : 0;
/*     */       }
/*     */       try
/*     */       {
/* 200 */         UnixNativeDispatcher.unlinkat(this.dfd, localUnixPath.asByteArray(), paramInt);
/*     */       } catch (UnixException localUnixException1) {
/* 202 */         if (((paramInt & 0x200) != 0) && (
/* 203 */           (localUnixException1.errno() == 17) || (localUnixException1.errno() == 39))) {
/* 204 */           throw new DirectoryNotEmptyException(null);
/*     */         }
/*     */ 
/* 207 */         localUnixException1.rethrowAsIOException(localUnixPath);
/*     */       }
/*     */     } finally {
/* 210 */       this.ds.readLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteFile(Path paramPath) throws IOException
/*     */   {
/* 216 */     implDelete(paramPath, true, 0);
/*     */   }
/*     */ 
/*     */   public void deleteDirectory(Path paramPath) throws IOException
/*     */   {
/* 221 */     implDelete(paramPath, true, 512);
/*     */   }
/*     */ 
/*     */   public void move(Path paramPath1, SecureDirectoryStream<Path> paramSecureDirectoryStream, Path paramPath2)
/*     */     throws IOException
/*     */   {
/* 231 */     UnixPath localUnixPath1 = getName(paramPath1);
/* 232 */     UnixPath localUnixPath2 = getName(paramPath2);
/* 233 */     if (paramSecureDirectoryStream == null)
/* 234 */       throw new NullPointerException();
/* 235 */     if (!(paramSecureDirectoryStream instanceof UnixSecureDirectoryStream))
/* 236 */       throw new ProviderMismatchException();
/* 237 */     UnixSecureDirectoryStream localUnixSecureDirectoryStream = (UnixSecureDirectoryStream)paramSecureDirectoryStream;
/*     */ 
/* 240 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 241 */     if (localSecurityManager != null) {
/* 242 */       this.ds.directory().resolve(localUnixPath1).checkWrite();
/* 243 */       localUnixSecureDirectoryStream.ds.directory().resolve(localUnixPath2).checkWrite();
/*     */     }
/*     */ 
/* 247 */     this.ds.readLock().lock();
/*     */     try {
/* 249 */       localUnixSecureDirectoryStream.ds.readLock().lock();
/*     */       try {
/* 251 */         if ((!this.ds.isOpen()) || (!localUnixSecureDirectoryStream.ds.isOpen()))
/* 252 */           throw new ClosedDirectoryStreamException();
/*     */         try {
/* 254 */           UnixNativeDispatcher.renameat(this.dfd, localUnixPath1.asByteArray(), localUnixSecureDirectoryStream.dfd, localUnixPath2.asByteArray());
/*     */         } catch (UnixException localUnixException) {
/* 256 */           if (localUnixException.errno() == 18) {
/* 257 */             throw new AtomicMoveNotSupportedException(localUnixPath1.toString(), localUnixPath2.toString(), localUnixException.errorString());
/*     */           }
/*     */ 
/* 260 */           localUnixException.rethrowAsIOException(localUnixPath1, localUnixPath2);
/*     */         }
/*     */       } finally {
/*     */       }
/*     */     }
/*     */     finally {
/* 266 */       this.ds.readLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private <V extends FileAttributeView> V getFileAttributeViewImpl(UnixPath paramUnixPath, Class<V> paramClass, boolean paramBoolean)
/*     */   {
/* 275 */     if (paramClass == null)
/* 276 */       throw new NullPointerException();
/* 277 */     Class<V> localClass = paramClass;
/* 278 */     if (localClass == BasicFileAttributeView.class) {
/* 279 */       return new BasicFileAttributeViewImpl(paramUnixPath, paramBoolean);
/*     */     }
/* 281 */     if ((localClass == PosixFileAttributeView.class) || (localClass == FileOwnerAttributeView.class)) {
/* 282 */       return new PosixFileAttributeViewImpl(paramUnixPath, paramBoolean);
/*     */     }
/*     */ 
/* 285 */     return (FileAttributeView)null;
/*     */   }
/*     */ 
/*     */   public <V extends FileAttributeView> V getFileAttributeView(Class<V> paramClass)
/*     */   {
/* 293 */     return getFileAttributeViewImpl(null, paramClass, false);
/*     */   }
/*     */ 
/*     */   public <V extends FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption[] paramArrayOfLinkOption)
/*     */   {
/* 304 */     UnixPath localUnixPath = getName(paramPath);
/* 305 */     boolean bool = Util.followLinks(paramArrayOfLinkOption);
/* 306 */     return getFileAttributeViewImpl(localUnixPath, paramClass, bool);
/*     */   }
/*     */ 
/*     */   private class BasicFileAttributeViewImpl
/*     */     implements BasicFileAttributeView
/*     */   {
/*     */     final UnixPath file;
/*     */     final boolean followLinks;
/*     */ 
/*     */     BasicFileAttributeViewImpl(UnixPath paramBoolean, boolean arg3)
/*     */     {
/* 320 */       this.file = paramBoolean;
/*     */       boolean bool;
/* 321 */       this.followLinks = bool;
/*     */     }
/*     */ 
/*     */     int open() throws IOException {
/* 325 */       int i = 0;
/* 326 */       if (!this.followLinks)
/* 327 */         i |= 131072;
/*     */       try {
/* 329 */         return UnixNativeDispatcher.openat(UnixSecureDirectoryStream.this.dfd, this.file.asByteArray(), i, 0);
/*     */       } catch (UnixException localUnixException) {
/* 331 */         localUnixException.rethrowAsIOException(this.file);
/* 332 */       }return -1;
/*     */     }
/*     */ 
/*     */     private void checkWriteAccess()
/*     */     {
/* 337 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 338 */       if (localSecurityManager != null)
/* 339 */         if (this.file == null)
/* 340 */           UnixSecureDirectoryStream.this.ds.directory().checkWrite();
/*     */         else
/* 342 */           UnixSecureDirectoryStream.this.ds.directory().resolve(this.file).checkWrite();
/*     */     }
/*     */ 
/*     */     public String name()
/*     */     {
/* 349 */       return "basic";
/*     */     }
/*     */ 
/*     */     public BasicFileAttributes readAttributes() throws IOException
/*     */     {
/* 354 */       UnixSecureDirectoryStream.this.ds.readLock().lock();
/*     */       try {
/* 356 */         if (!UnixSecureDirectoryStream.this.ds.isOpen()) {
/* 357 */           throw new ClosedDirectoryStreamException();
/*     */         }
/* 359 */         SecurityManager localSecurityManager = System.getSecurityManager();
/* 360 */         if (localSecurityManager != null) {
/* 361 */           if (this.file == null)
/* 362 */             UnixSecureDirectoryStream.this.ds.directory().checkRead();
/*     */           else
/* 364 */             UnixSecureDirectoryStream.this.ds.directory().resolve(this.file).checkRead();
/*     */         }
/*     */         try
/*     */         {
/* 368 */           UnixFileAttributes localUnixFileAttributes = this.file == null ? UnixFileAttributes.get(UnixSecureDirectoryStream.this.dfd) : UnixFileAttributes.get(UnixSecureDirectoryStream.this.dfd, this.file, this.followLinks);
/*     */ 
/* 373 */           return localUnixFileAttributes.asBasicFileAttributes();
/*     */         }
/*     */         catch (UnixException localUnixException)
/*     */         {
/*     */           BasicFileAttributes localBasicFileAttributes;
/* 375 */           localUnixException.rethrowAsIOException(this.file);
/* 376 */           return null;
/*     */         }
/*     */       } finally {
/* 379 */         UnixSecureDirectoryStream.this.ds.readLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setTimes(FileTime paramFileTime1, FileTime paramFileTime2, FileTime paramFileTime3)
/*     */       throws IOException
/*     */     {
/* 389 */       checkWriteAccess();
/*     */ 
/* 391 */       UnixSecureDirectoryStream.this.ds.readLock().lock();
/*     */       try {
/* 393 */         if (!UnixSecureDirectoryStream.this.ds.isOpen()) {
/* 394 */           throw new ClosedDirectoryStreamException();
/*     */         }
/* 396 */         int i = this.file == null ? UnixSecureDirectoryStream.this.dfd : open();
/*     */         try
/*     */         {
/* 399 */           if ((paramFileTime1 == null) || (paramFileTime2 == null)) {
/*     */             try {
/* 401 */               UnixFileAttributes localUnixFileAttributes = UnixFileAttributes.get(i);
/* 402 */               if (paramFileTime1 == null)
/* 403 */                 paramFileTime1 = localUnixFileAttributes.lastModifiedTime();
/* 404 */               if (paramFileTime2 == null)
/* 405 */                 paramFileTime2 = localUnixFileAttributes.lastAccessTime();
/*     */             } catch (UnixException localUnixException1) {
/* 407 */               localUnixException1.rethrowAsIOException(this.file);
/*     */             }
/*     */           }
/*     */           try
/*     */           {
/* 412 */             UnixNativeDispatcher.futimes(i, paramFileTime2.to(TimeUnit.MICROSECONDS), paramFileTime1.to(TimeUnit.MICROSECONDS));
/*     */           }
/*     */           catch (UnixException localUnixException2)
/*     */           {
/* 416 */             localUnixException2.rethrowAsIOException(this.file);
/*     */           }
/*     */         } finally {
/* 419 */           if (this.file != null)
/* 420 */             UnixNativeDispatcher.close(i);
/*     */         }
/*     */       } finally {
/* 423 */         UnixSecureDirectoryStream.this.ds.readLock().unlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PosixFileAttributeViewImpl extends UnixSecureDirectoryStream.BasicFileAttributeViewImpl
/*     */     implements PosixFileAttributeView
/*     */   {
/*     */     PosixFileAttributeViewImpl(UnixPath paramBoolean, boolean arg3)
/*     */     {
/* 435 */       super(paramBoolean, bool);
/*     */     }
/*     */ 
/*     */     private void checkWriteAndUserAccess() {
/* 439 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 440 */       if (localSecurityManager != null) {
/* 441 */         super.checkWriteAccess();
/* 442 */         localSecurityManager.checkPermission(new RuntimePermission("accessUserInformation"));
/*     */       }
/*     */     }
/*     */ 
/*     */     public String name()
/*     */     {
/* 448 */       return "posix";
/*     */     }
/*     */ 
/*     */     public PosixFileAttributes readAttributes() throws IOException
/*     */     {
/* 453 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 454 */       if (localSecurityManager != null) {
/* 455 */         if (this.file == null)
/* 456 */           UnixSecureDirectoryStream.this.ds.directory().checkRead();
/*     */         else
/* 458 */           UnixSecureDirectoryStream.this.ds.directory().resolve(this.file).checkRead();
/* 459 */         localSecurityManager.checkPermission(new RuntimePermission("accessUserInformation"));
/*     */       }
/*     */ 
/* 462 */       UnixSecureDirectoryStream.this.ds.readLock().lock();
/*     */       try {
/* 464 */         if (!UnixSecureDirectoryStream.this.ds.isOpen())
/* 465 */           throw new ClosedDirectoryStreamException();
/*     */         try
/*     */         {
/* 468 */           UnixFileAttributes localUnixFileAttributes1 = this.file == null ? UnixFileAttributes.get(UnixSecureDirectoryStream.this.dfd) : UnixFileAttributes.get(UnixSecureDirectoryStream.this.dfd, this.file, this.followLinks);
/*     */ 
/* 471 */           return localUnixFileAttributes1;
/*     */         }
/*     */         catch (UnixException localUnixException)
/*     */         {
/*     */           UnixFileAttributes localUnixFileAttributes2;
/* 473 */           localUnixException.rethrowAsIOException(this.file);
/* 474 */           return null;
/*     */         }
/*     */       } finally {
/* 477 */         UnixSecureDirectoryStream.this.ds.readLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setPermissions(Set<PosixFilePermission> paramSet)
/*     */       throws IOException
/*     */     {
/* 486 */       checkWriteAndUserAccess();
/*     */ 
/* 488 */       UnixSecureDirectoryStream.this.ds.readLock().lock();
/*     */       try {
/* 490 */         if (!UnixSecureDirectoryStream.this.ds.isOpen()) {
/* 491 */           throw new ClosedDirectoryStreamException();
/*     */         }
/* 493 */         int i = this.file == null ? UnixSecureDirectoryStream.this.dfd : open();
/*     */         try {
/* 495 */           UnixNativeDispatcher.fchmod(i, UnixFileModeAttribute.toUnixMode(paramSet));
/*     */         } catch (UnixException localUnixException) {
/* 497 */           localUnixException.rethrowAsIOException(this.file);
/*     */         } finally {
/* 499 */           if ((this.file != null) && (i >= 0))
/* 500 */             UnixNativeDispatcher.close(i);
/*     */         }
/*     */       } finally {
/* 503 */         UnixSecureDirectoryStream.this.ds.readLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     private void setOwners(int paramInt1, int paramInt2) throws IOException
/*     */     {
/* 509 */       checkWriteAndUserAccess();
/*     */ 
/* 511 */       UnixSecureDirectoryStream.this.ds.readLock().lock();
/*     */       try {
/* 513 */         if (!UnixSecureDirectoryStream.this.ds.isOpen()) {
/* 514 */           throw new ClosedDirectoryStreamException();
/*     */         }
/* 516 */         int i = this.file == null ? UnixSecureDirectoryStream.this.dfd : open();
/*     */         try {
/* 518 */           UnixNativeDispatcher.fchown(i, paramInt1, paramInt2);
/*     */         } catch (UnixException localUnixException) {
/* 520 */           localUnixException.rethrowAsIOException(this.file);
/*     */         } finally {
/* 522 */           if ((this.file != null) && (i >= 0))
/* 523 */             UnixNativeDispatcher.close(i);
/*     */         }
/*     */       } finally {
/* 526 */         UnixSecureDirectoryStream.this.ds.readLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public UserPrincipal getOwner() throws IOException
/*     */     {
/* 532 */       return readAttributes().owner();
/*     */     }
/*     */ 
/*     */     public void setOwner(UserPrincipal paramUserPrincipal)
/*     */       throws IOException
/*     */     {
/* 539 */       if (!(paramUserPrincipal instanceof UnixUserPrincipals.User))
/* 540 */         throw new ProviderMismatchException();
/* 541 */       if ((paramUserPrincipal instanceof UnixUserPrincipals.Group))
/* 542 */         throw new IOException("'owner' parameter can't be a group");
/* 543 */       int i = ((UnixUserPrincipals.User)paramUserPrincipal).uid();
/* 544 */       setOwners(i, -1);
/*     */     }
/*     */ 
/*     */     public void setGroup(GroupPrincipal paramGroupPrincipal)
/*     */       throws IOException
/*     */     {
/* 551 */       if (!(paramGroupPrincipal instanceof UnixUserPrincipals.Group))
/* 552 */         throw new ProviderMismatchException();
/* 553 */       int i = ((UnixUserPrincipals.Group)paramGroupPrincipal).gid();
/* 554 */       setOwners(-1, i);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixSecureDirectoryStream
 * JD-Core Version:    0.6.2
 */