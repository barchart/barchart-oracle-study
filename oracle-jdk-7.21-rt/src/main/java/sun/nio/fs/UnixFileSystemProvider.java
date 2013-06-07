/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.FilePermission;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.nio.channels.AsynchronousFileChannel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.AccessMode;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.DirectoryNotEmptyException;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.DirectoryStream.Filter;
/*     */ import java.nio.file.FileStore;
/*     */ import java.nio.file.FileSystem;
/*     */ import java.nio.file.FileSystemAlreadyExistsException;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.LinkPermission;
/*     */ import java.nio.file.NotDirectoryException;
/*     */ import java.nio.file.NotLinkException;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.ProviderMismatchException;
/*     */ import java.nio.file.attribute.BasicFileAttributeView;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.nio.file.attribute.FileAttributeView;
/*     */ import java.nio.file.attribute.FileOwnerAttributeView;
/*     */ import java.nio.file.attribute.PosixFileAttributeView;
/*     */ import java.nio.file.attribute.PosixFileAttributes;
/*     */ import java.security.AccessController;
/*     */ import java.security.Permission;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import sun.nio.ch.ThreadPool;
/*     */ 
/*     */ public abstract class UnixFileSystemProvider extends AbstractFileSystemProvider
/*     */ {
/*     */   private static final String USER_DIR = "user.dir";
/*     */   private final UnixFileSystem theFileSystem;
/*     */ 
/*     */   public UnixFileSystemProvider()
/*     */   {
/*  54 */     String str = System.getProperty("user.dir");
/*  55 */     this.theFileSystem = newFileSystem(str);
/*     */   }
/*     */ 
/*     */   abstract UnixFileSystem newFileSystem(String paramString);
/*     */ 
/*     */   public final String getScheme()
/*     */   {
/*  65 */     return "file";
/*     */   }
/*     */ 
/*     */   private void checkUri(URI paramURI) {
/*  69 */     if (!paramURI.getScheme().equalsIgnoreCase(getScheme()))
/*  70 */       throw new IllegalArgumentException("URI does not match this provider");
/*  71 */     if (paramURI.getAuthority() != null)
/*  72 */       throw new IllegalArgumentException("Authority component present");
/*  73 */     if (paramURI.getPath() == null)
/*  74 */       throw new IllegalArgumentException("Path component is undefined");
/*  75 */     if (!paramURI.getPath().equals("/"))
/*  76 */       throw new IllegalArgumentException("Path component should be '/'");
/*  77 */     if (paramURI.getQuery() != null)
/*  78 */       throw new IllegalArgumentException("Query component present");
/*  79 */     if (paramURI.getFragment() != null)
/*  80 */       throw new IllegalArgumentException("Fragment component present");
/*     */   }
/*     */ 
/*     */   public final FileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap)
/*     */   {
/*  85 */     checkUri(paramURI);
/*  86 */     throw new FileSystemAlreadyExistsException();
/*     */   }
/*     */ 
/*     */   public final FileSystem getFileSystem(URI paramURI)
/*     */   {
/*  91 */     checkUri(paramURI);
/*  92 */     return this.theFileSystem;
/*     */   }
/*     */ 
/*     */   public Path getPath(URI paramURI)
/*     */   {
/*  97 */     return UnixUriUtils.fromUri(this.theFileSystem, paramURI);
/*     */   }
/*     */ 
/*     */   UnixPath checkPath(Path paramPath) {
/* 101 */     if (paramPath == null)
/* 102 */       throw new NullPointerException();
/* 103 */     if (!(paramPath instanceof UnixPath))
/* 104 */       throw new ProviderMismatchException();
/* 105 */     return (UnixPath)paramPath;
/*     */   }
/*     */ 
/*     */   public <V extends FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption[] paramArrayOfLinkOption)
/*     */   {
/* 114 */     UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
/* 115 */     boolean bool = Util.followLinks(paramArrayOfLinkOption);
/* 116 */     if (paramClass == BasicFileAttributeView.class)
/* 117 */       return UnixFileAttributeViews.createBasicView(localUnixPath, bool);
/* 118 */     if (paramClass == PosixFileAttributeView.class)
/* 119 */       return UnixFileAttributeViews.createPosixView(localUnixPath, bool);
/* 120 */     if (paramClass == FileOwnerAttributeView.class)
/* 121 */       return UnixFileAttributeViews.createOwnerView(localUnixPath, bool);
/* 122 */     if (paramClass == null)
/* 123 */       throw new NullPointerException();
/* 124 */     return (FileAttributeView)null;
/*     */   }
/*     */ 
/*     */   public <A extends BasicFileAttributes> A readAttributes(Path paramPath, Class<A> paramClass, LinkOption[] paramArrayOfLinkOption)
/*     */     throws IOException
/*     */   {
/*     */     Object localObject;
/* 135 */     if (paramClass == BasicFileAttributes.class) {
/* 136 */       localObject = BasicFileAttributeView.class;
/* 137 */     } else if (paramClass == PosixFileAttributes.class) {
/* 138 */       localObject = PosixFileAttributeView.class; } else {
/* 139 */       if (paramClass == null) {
/* 140 */         throw new NullPointerException();
/*     */       }
/* 142 */       throw new UnsupportedOperationException();
/* 143 */     }return ((BasicFileAttributeView)getFileAttributeView(paramPath, (Class)localObject, paramArrayOfLinkOption)).readAttributes();
/*     */   }
/*     */ 
/*     */   protected DynamicFileAttributeView getFileAttributeView(Path paramPath, String paramString, LinkOption[] paramArrayOfLinkOption)
/*     */   {
/* 151 */     UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
/* 152 */     boolean bool = Util.followLinks(paramArrayOfLinkOption);
/* 153 */     if (paramString.equals("basic"))
/* 154 */       return UnixFileAttributeViews.createBasicView(localUnixPath, bool);
/* 155 */     if (paramString.equals("posix"))
/* 156 */       return UnixFileAttributeViews.createPosixView(localUnixPath, bool);
/* 157 */     if (paramString.equals("unix"))
/* 158 */       return UnixFileAttributeViews.createUnixView(localUnixPath, bool);
/* 159 */     if (paramString.equals("owner"))
/* 160 */       return UnixFileAttributeViews.createOwnerView(localUnixPath, bool);
/* 161 */     return null;
/*     */   }
/*     */ 
/*     */   public FileChannel newFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>[] paramArrayOfFileAttribute)
/*     */     throws IOException
/*     */   {
/* 170 */     UnixPath localUnixPath = checkPath(paramPath);
/* 171 */     int i = UnixFileModeAttribute.toUnixMode(438, paramArrayOfFileAttribute);
/*     */     try
/*     */     {
/* 174 */       return UnixChannelFactory.newFileChannel(localUnixPath, paramSet, i);
/*     */     } catch (UnixException localUnixException) {
/* 176 */       localUnixException.rethrowAsIOException(localUnixPath);
/* 177 */     }return null;
/*     */   }
/*     */ 
/*     */   public AsynchronousFileChannel newAsynchronousFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, ExecutorService paramExecutorService, FileAttribute<?>[] paramArrayOfFileAttribute)
/*     */     throws IOException
/*     */   {
/* 187 */     UnixPath localUnixPath = checkPath(paramPath);
/* 188 */     int i = UnixFileModeAttribute.toUnixMode(438, paramArrayOfFileAttribute);
/*     */ 
/* 190 */     ThreadPool localThreadPool = paramExecutorService == null ? null : ThreadPool.wrap(paramExecutorService, 0);
/*     */     try {
/* 192 */       return UnixChannelFactory.newAsynchronousFileChannel(localUnixPath, paramSet, i, localThreadPool);
/*     */     }
/*     */     catch (UnixException localUnixException) {
/* 195 */       localUnixException.rethrowAsIOException(localUnixPath);
/* 196 */     }return null;
/*     */   }
/*     */ 
/*     */   public SeekableByteChannel newByteChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>[] paramArrayOfFileAttribute)
/*     */     throws IOException
/*     */   {
/* 207 */     UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
/* 208 */     int i = UnixFileModeAttribute.toUnixMode(438, paramArrayOfFileAttribute);
/*     */     try
/*     */     {
/* 211 */       return UnixChannelFactory.newFileChannel(localUnixPath, paramSet, i);
/*     */     } catch (UnixException localUnixException) {
/* 213 */       localUnixException.rethrowAsIOException(localUnixPath);
/* 214 */     }return null;
/*     */   }
/*     */ 
/*     */   boolean implDelete(Path paramPath, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 220 */     UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
/* 221 */     localUnixPath.checkDelete();
/*     */ 
/* 224 */     UnixFileAttributes localUnixFileAttributes = null;
/*     */     try {
/* 226 */       localUnixFileAttributes = UnixFileAttributes.get(localUnixPath, false);
/* 227 */       if (localUnixFileAttributes.isDirectory())
/* 228 */         UnixNativeDispatcher.rmdir(localUnixPath);
/*     */       else {
/* 230 */         UnixNativeDispatcher.unlink(localUnixPath);
/*     */       }
/* 232 */       return true;
/*     */     }
/*     */     catch (UnixException localUnixException) {
/* 235 */       if ((!paramBoolean) && (localUnixException.errno() == 2)) {
/* 236 */         return false;
/*     */       }
/*     */ 
/* 239 */       if ((localUnixFileAttributes != null) && (localUnixFileAttributes.isDirectory()) && ((localUnixException.errno() == 17) || (localUnixException.errno() == 39)))
/*     */       {
/* 241 */         throw new DirectoryNotEmptyException(localUnixPath.getPathForExecptionMessage());
/*     */       }
/* 243 */       localUnixException.rethrowAsIOException(localUnixPath);
/* 244 */     }return false;
/*     */   }
/*     */ 
/*     */   public void copy(Path paramPath1, Path paramPath2, CopyOption[] paramArrayOfCopyOption)
/*     */     throws IOException
/*     */   {
/* 252 */     UnixCopyFile.copy(UnixPath.toUnixPath(paramPath1), UnixPath.toUnixPath(paramPath2), paramArrayOfCopyOption);
/*     */   }
/*     */ 
/*     */   public void move(Path paramPath1, Path paramPath2, CopyOption[] paramArrayOfCopyOption)
/*     */     throws IOException
/*     */   {
/* 261 */     UnixCopyFile.move(UnixPath.toUnixPath(paramPath1), UnixPath.toUnixPath(paramPath2), paramArrayOfCopyOption);
/*     */   }
/*     */ 
/*     */   public void checkAccess(Path paramPath, AccessMode[] paramArrayOfAccessMode)
/*     */     throws IOException
/*     */   {
/* 268 */     UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
/* 269 */     int i = 0;
/* 270 */     int j = 0;
/* 271 */     int k = 0;
/* 272 */     int m = 0;
/*     */ 
/* 274 */     if (paramArrayOfAccessMode.length == 0)
/* 275 */       i = 1;
/*     */     else {
/* 277 */       for (AccessMode localAccessMode : paramArrayOfAccessMode) {
/* 278 */         switch (1.$SwitchMap$java$nio$file$AccessMode[localAccessMode.ordinal()]) { case 1:
/* 279 */           j = 1; break;
/*     */         case 2:
/* 280 */           k = 1; break;
/*     */         case 3:
/* 281 */           m = 1; break;
/*     */         default:
/* 282 */           throw new AssertionError("Should not get here");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 287 */     int n = 0;
/* 288 */     if ((i != 0) || (j != 0)) {
/* 289 */       localUnixPath.checkRead();
/* 290 */       n |= (j != 0 ? 4 : 0);
/*     */     }
/* 292 */     if (k != 0) {
/* 293 */       localUnixPath.checkWrite();
/* 294 */       n |= 2;
/*     */     }
/* 296 */     if (m != 0) {
/* 297 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 298 */       if (localSecurityManager != null)
/*     */       {
/* 300 */         localSecurityManager.checkExec(localUnixPath.getPathForPermissionCheck());
/*     */       }
/* 302 */       n |= 1;
/*     */     }
/*     */     try {
/* 305 */       UnixNativeDispatcher.access(localUnixPath, n);
/*     */     } catch (UnixException localUnixException) {
/* 307 */       localUnixException.rethrowAsIOException(localUnixPath);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isSameFile(Path paramPath1, Path paramPath2) throws IOException
/*     */   {
/* 313 */     UnixPath localUnixPath1 = UnixPath.toUnixPath(paramPath1);
/* 314 */     if (localUnixPath1.equals(paramPath2))
/* 315 */       return true;
/* 316 */     if (paramPath2 == null)
/* 317 */       throw new NullPointerException();
/* 318 */     if (!(paramPath2 instanceof UnixPath))
/* 319 */       return false;
/* 320 */     UnixPath localUnixPath2 = (UnixPath)paramPath2;
/*     */ 
/* 323 */     localUnixPath1.checkRead();
/* 324 */     localUnixPath2.checkRead();
/*     */     UnixFileAttributes localUnixFileAttributes1;
/*     */     try {
/* 329 */       localUnixFileAttributes1 = UnixFileAttributes.get(localUnixPath1, true);
/*     */     } catch (UnixException localUnixException1) {
/* 331 */       localUnixException1.rethrowAsIOException(localUnixPath1);
/* 332 */       return false;
/*     */     }UnixFileAttributes localUnixFileAttributes2;
/*     */     try {
/* 335 */       localUnixFileAttributes2 = UnixFileAttributes.get(localUnixPath2, true);
/*     */     } catch (UnixException localUnixException2) {
/* 337 */       localUnixException2.rethrowAsIOException(localUnixPath2);
/* 338 */       return false;
/*     */     }
/* 340 */     return localUnixFileAttributes1.isSameFile(localUnixFileAttributes2);
/*     */   }
/*     */ 
/*     */   public boolean isHidden(Path paramPath)
/*     */   {
/* 345 */     UnixPath localUnixPath1 = UnixPath.toUnixPath(paramPath);
/* 346 */     localUnixPath1.checkRead();
/* 347 */     UnixPath localUnixPath2 = localUnixPath1.getFileName();
/* 348 */     if (localUnixPath2 == null)
/* 349 */       return false;
/* 350 */     return localUnixPath2.asByteArray()[0] == 46;
/*     */   }
/*     */ 
/*     */   abstract FileStore getFileStore(UnixPath paramUnixPath)
/*     */     throws IOException;
/*     */ 
/*     */   public FileStore getFileStore(Path paramPath)
/*     */     throws IOException
/*     */   {
/* 361 */     UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
/* 362 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 363 */     if (localSecurityManager != null) {
/* 364 */       localSecurityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
/* 365 */       localUnixPath.checkRead();
/*     */     }
/* 367 */     return getFileStore(localUnixPath);
/*     */   }
/*     */ 
/*     */   public void createDirectory(Path paramPath, FileAttribute<?>[] paramArrayOfFileAttribute)
/*     */     throws IOException
/*     */   {
/* 374 */     UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
/* 375 */     localUnixPath.checkWrite();
/*     */ 
/* 377 */     int i = UnixFileModeAttribute.toUnixMode(511, paramArrayOfFileAttribute);
/*     */     try
/*     */     {
/* 380 */       UnixNativeDispatcher.mkdir(localUnixPath, i);
/*     */     } catch (UnixException localUnixException) {
/* 382 */       localUnixException.rethrowAsIOException(localUnixPath);
/*     */     }
/*     */   }
/*     */ 
/*     */   public DirectoryStream<Path> newDirectoryStream(Path paramPath, DirectoryStream.Filter<? super Path> paramFilter)
/*     */     throws IOException
/*     */   {
/* 391 */     UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
/* 392 */     localUnixPath.checkRead();
/* 393 */     if (paramFilter == null) {
/* 394 */       throw new NullPointerException();
/*     */     }
/*     */ 
/* 398 */     if (!UnixNativeDispatcher.supportsAtSysCalls()) {
/*     */       try {
/* 400 */         long l1 = UnixNativeDispatcher.opendir(localUnixPath);
/* 401 */         return new UnixDirectoryStream(localUnixPath, l1, paramFilter);
/*     */       } catch (UnixException localUnixException1) {
/* 403 */         if (localUnixException1.errno() == 20)
/* 404 */           throw new NotDirectoryException(localUnixPath.getPathForExecptionMessage());
/* 405 */         localUnixException1.rethrowAsIOException(localUnixPath);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 411 */     int i = -1;
/* 412 */     int j = -1;
/* 413 */     long l2 = 0L;
/*     */     try {
/* 415 */       i = UnixNativeDispatcher.open(localUnixPath, 0, 0);
/* 416 */       j = UnixNativeDispatcher.dup(i);
/* 417 */       l2 = UnixNativeDispatcher.fdopendir(i);
/*     */     } catch (UnixException localUnixException2) {
/* 419 */       if (i != -1)
/* 420 */         UnixNativeDispatcher.close(i);
/* 421 */       if (j != -1)
/* 422 */         UnixNativeDispatcher.close(j);
/* 423 */       if (localUnixException2.errno() == 20)
/* 424 */         throw new NotDirectoryException(localUnixPath.getPathForExecptionMessage());
/* 425 */       localUnixException2.rethrowAsIOException(localUnixPath);
/*     */     }
/* 427 */     return new UnixSecureDirectoryStream(localUnixPath, l2, j, paramFilter);
/*     */   }
/*     */ 
/*     */   public void createSymbolicLink(Path paramPath1, Path paramPath2, FileAttribute<?>[] paramArrayOfFileAttribute)
/*     */     throws IOException
/*     */   {
/* 434 */     UnixPath localUnixPath1 = UnixPath.toUnixPath(paramPath1);
/* 435 */     UnixPath localUnixPath2 = UnixPath.toUnixPath(paramPath2);
/*     */ 
/* 438 */     if (paramArrayOfFileAttribute.length > 0) {
/* 439 */       UnixFileModeAttribute.toUnixMode(0, paramArrayOfFileAttribute);
/* 440 */       throw new UnsupportedOperationException("Initial file attributesnot supported when creating symbolic link");
/*     */     }
/*     */ 
/* 445 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 446 */     if (localSecurityManager != null) {
/* 447 */       localSecurityManager.checkPermission(new LinkPermission("symbolic"));
/* 448 */       localUnixPath1.checkWrite();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 453 */       UnixNativeDispatcher.symlink(localUnixPath2.asByteArray(), localUnixPath1);
/*     */     } catch (UnixException localUnixException) {
/* 455 */       localUnixException.rethrowAsIOException(localUnixPath1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createLink(Path paramPath1, Path paramPath2) throws IOException
/*     */   {
/* 461 */     UnixPath localUnixPath1 = UnixPath.toUnixPath(paramPath1);
/* 462 */     UnixPath localUnixPath2 = UnixPath.toUnixPath(paramPath2);
/*     */ 
/* 465 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 466 */     if (localSecurityManager != null) {
/* 467 */       localSecurityManager.checkPermission(new LinkPermission("hard"));
/* 468 */       localUnixPath1.checkWrite();
/* 469 */       localUnixPath2.checkWrite();
/*     */     }
/*     */     try {
/* 472 */       UnixNativeDispatcher.link(localUnixPath2, localUnixPath1);
/*     */     } catch (UnixException localUnixException) {
/* 474 */       localUnixException.rethrowAsIOException(localUnixPath1, localUnixPath2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Path readSymbolicLink(Path paramPath) throws IOException
/*     */   {
/* 480 */     UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
/*     */ 
/* 482 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*     */     Object localObject;
/* 483 */     if (localSecurityManager != null) {
/* 484 */       localObject = new FilePermission(localUnixPath.getPathForPermissionCheck(), "readlink");
/*     */ 
/* 486 */       AccessController.checkPermission((Permission)localObject);
/*     */     }
/*     */     try {
/* 489 */       localObject = UnixNativeDispatcher.readlink(localUnixPath);
/* 490 */       return new UnixPath(localUnixPath.getFileSystem(), (byte[])localObject);
/*     */     } catch (UnixException localUnixException) {
/* 492 */       if (localUnixException.errno() == 22)
/* 493 */         throw new NotLinkException(localUnixPath.getPathForExecptionMessage());
/* 494 */       localUnixException.rethrowAsIOException(localUnixPath);
/* 495 */     }return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixFileSystemProvider
 * JD-Core Version:    0.6.2
 */