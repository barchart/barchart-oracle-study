/*      */ package java.nio.file;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.nio.channels.SeekableByteChannel;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetDecoder;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.file.attribute.BasicFileAttributeView;
/*      */ import java.nio.file.attribute.BasicFileAttributes;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.nio.file.attribute.FileAttributeView;
/*      */ import java.nio.file.attribute.FileOwnerAttributeView;
/*      */ import java.nio.file.attribute.FileTime;
/*      */ import java.nio.file.attribute.PosixFileAttributeView;
/*      */ import java.nio.file.attribute.PosixFileAttributes;
/*      */ import java.nio.file.attribute.PosixFilePermission;
/*      */ import java.nio.file.attribute.UserPrincipal;
/*      */ import java.nio.file.spi.FileSystemProvider;
/*      */ import java.nio.file.spi.FileTypeDetector;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.EnumSet;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.ServiceLoader;
/*      */ import java.util.Set;
/*      */ import sun.nio.fs.DefaultFileTypeDetector;
/*      */ 
/*      */ public final class Files
/*      */ {
/*      */   private static final int BUFFER_SIZE = 8192;
/*      */ 
/*      */   private static FileSystemProvider provider(Path paramPath)
/*      */   {
/*   65 */     return paramPath.getFileSystem().provider();
/*      */   }
/*      */ 
/*      */   public static InputStream newInputStream(Path paramPath, OpenOption[] paramArrayOfOpenOption)
/*      */     throws IOException
/*      */   {
/*  106 */     return provider(paramPath).newInputStream(paramPath, paramArrayOfOpenOption);
/*      */   }
/*      */ 
/*      */   public static OutputStream newOutputStream(Path paramPath, OpenOption[] paramArrayOfOpenOption)
/*      */     throws IOException
/*      */   {
/*  170 */     return provider(paramPath).newOutputStream(paramPath, paramArrayOfOpenOption);
/*      */   }
/*      */ 
/*      */   public static SeekableByteChannel newByteChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*  315 */     return provider(paramPath).newByteChannel(paramPath, paramSet, paramArrayOfFileAttribute);
/*      */   }
/*      */ 
/*      */   public static SeekableByteChannel newByteChannel(Path paramPath, OpenOption[] paramArrayOfOpenOption)
/*      */     throws IOException
/*      */   {
/*  359 */     HashSet localHashSet = new HashSet(paramArrayOfOpenOption.length);
/*  360 */     Collections.addAll(localHashSet, paramArrayOfOpenOption);
/*  361 */     return newByteChannel(paramPath, localHashSet, new FileAttribute[0]);
/*      */   }
/*      */ 
/*      */   public static DirectoryStream<Path> newDirectoryStream(Path paramPath)
/*      */     throws IOException
/*      */   {
/*  411 */     return provider(paramPath).newDirectoryStream(paramPath, AcceptAllFilter.FILTER);
/*      */   }
/*      */ 
/*      */   public static DirectoryStream<Path> newDirectoryStream(Path paramPath, String paramString)
/*      */     throws IOException
/*      */   {
/*  467 */     if (paramString.equals("*")) {
/*  468 */       return newDirectoryStream(paramPath);
/*      */     }
/*      */ 
/*  471 */     FileSystem localFileSystem = paramPath.getFileSystem();
/*  472 */     PathMatcher localPathMatcher = localFileSystem.getPathMatcher("glob:" + paramString);
/*  473 */     DirectoryStream.Filter local1 = new DirectoryStream.Filter()
/*      */     {
/*      */       public boolean accept(Path paramAnonymousPath) {
/*  476 */         return this.val$matcher.matches(paramAnonymousPath.getFileName());
/*      */       }
/*      */     };
/*  479 */     return localFileSystem.provider().newDirectoryStream(paramPath, local1);
/*      */   }
/*      */ 
/*      */   public static DirectoryStream<Path> newDirectoryStream(Path paramPath, DirectoryStream.Filter<? super Path> paramFilter)
/*      */     throws IOException
/*      */   {
/*  543 */     return provider(paramPath).newDirectoryStream(paramPath, paramFilter);
/*      */   }
/*      */ 
/*      */   public static Path createFile(Path paramPath, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*  584 */     EnumSet localEnumSet = EnumSet.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
/*      */ 
/*  586 */     newByteChannel(paramPath, localEnumSet, paramArrayOfFileAttribute).close();
/*  587 */     return paramPath;
/*      */   }
/*      */ 
/*      */   public static Path createDirectory(Path paramPath, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*  628 */     provider(paramPath).createDirectory(paramPath, paramArrayOfFileAttribute);
/*  629 */     return paramPath;
/*      */   }
/*      */ 
/*      */   public static Path createDirectories(Path paramPath, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*      */     Path localPath2;
/*      */     try
/*      */     {
/*  681 */       createAndCheckIsDirectory(paramPath, paramArrayOfFileAttribute);
/*  682 */       return paramPath;
/*      */     }
/*      */     catch (FileAlreadyExistsException localFileAlreadyExistsException) {
/*  685 */       throw localFileAlreadyExistsException;
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  689 */       Object localObject = null;
/*      */       try {
/*  691 */         paramPath = paramPath.toAbsolutePath();
/*      */       }
/*      */       catch (SecurityException localSecurityException) {
/*  694 */         localObject = localSecurityException;
/*      */       }
/*      */ 
/*  697 */       Path localPath1 = paramPath.getParent();
/*  698 */       while (localPath1 != null) {
/*      */         try {
/*  700 */           provider(localPath1).checkAccess(localPath1, new AccessMode[0]);
/*      */         }
/*      */         catch (NoSuchFileException localNoSuchFileException)
/*      */         {
/*      */         }
/*  705 */         localPath1 = localPath1.getParent();
/*      */       }
/*  707 */       if (localPath1 == null)
/*      */       {
/*  709 */         if (localObject != null)
/*  710 */           throw localObject;
/*  711 */         throw new IOException("Root directory does not exist");
/*      */       }
/*      */ 
/*  715 */       localPath2 = localPath1;
/*  716 */       for (Path localPath3 : localPath1.relativize(paramPath)) {
/*  717 */         localPath2 = localPath2.resolve(localPath3);
/*  718 */         createAndCheckIsDirectory(localPath2, paramArrayOfFileAttribute);
/*      */       }
/*      */     }
/*  720 */     return paramPath;
/*      */   }
/*      */ 
/*      */   private static void createAndCheckIsDirectory(Path paramPath, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  732 */       createDirectory(paramPath, paramArrayOfFileAttribute);
/*      */     } catch (FileAlreadyExistsException localFileAlreadyExistsException) {
/*  734 */       if (!isDirectory(paramPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
/*  735 */         throw localFileAlreadyExistsException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Path createTempFile(Path paramPath, String paramString1, String paramString2, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*  803 */     return TempFileHelper.createTempFile((Path)Objects.requireNonNull(paramPath), paramString1, paramString2, paramArrayOfFileAttribute);
/*      */   }
/*      */ 
/*      */   public static Path createTempFile(String paramString1, String paramString2, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*  848 */     return TempFileHelper.createTempFile(null, paramString1, paramString2, paramArrayOfFileAttribute);
/*      */   }
/*      */ 
/*      */   public static Path createTempDirectory(Path paramPath, String paramString, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*  901 */     return TempFileHelper.createTempDirectory((Path)Objects.requireNonNull(paramPath), paramString, paramArrayOfFileAttribute);
/*      */   }
/*      */ 
/*      */   public static Path createTempDirectory(String paramString, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*  942 */     return TempFileHelper.createTempDirectory(null, paramString, paramArrayOfFileAttribute);
/*      */   }
/*      */ 
/*      */   public static Path createSymbolicLink(Path paramPath1, Path paramPath2, FileAttribute<?>[] paramArrayOfFileAttribute)
/*      */     throws IOException
/*      */   {
/*  994 */     provider(paramPath1).createSymbolicLink(paramPath1, paramPath2, paramArrayOfFileAttribute);
/*  995 */     return paramPath1;
/*      */   }
/*      */ 
/*      */   public static Path createLink(Path paramPath1, Path paramPath2)
/*      */     throws IOException
/*      */   {
/* 1037 */     provider(paramPath1).createLink(paramPath1, paramPath2);
/* 1038 */     return paramPath1;
/*      */   }
/*      */ 
/*      */   public static void delete(Path paramPath)
/*      */     throws IOException
/*      */   {
/* 1077 */     provider(paramPath).delete(paramPath);
/*      */   }
/*      */ 
/*      */   public static boolean deleteIfExists(Path paramPath)
/*      */     throws IOException
/*      */   {
/* 1116 */     return provider(paramPath).deleteIfExists(paramPath);
/*      */   }
/*      */ 
/*      */   public static Path copy(Path paramPath1, Path paramPath2, CopyOption[] paramArrayOfCopyOption)
/*      */     throws IOException
/*      */   {
/* 1222 */     FileSystemProvider localFileSystemProvider = provider(paramPath1);
/* 1223 */     if (provider(paramPath2) == localFileSystemProvider)
/*      */     {
/* 1225 */       localFileSystemProvider.copy(paramPath1, paramPath2, paramArrayOfCopyOption);
/*      */     }
/*      */     else {
/* 1228 */       CopyMoveHelper.copyToForeignTarget(paramPath1, paramPath2, paramArrayOfCopyOption);
/*      */     }
/* 1230 */     return paramPath2;
/*      */   }
/*      */ 
/*      */   public static Path move(Path paramPath1, Path paramPath2, CopyOption[] paramArrayOfCopyOption)
/*      */     throws IOException
/*      */   {
/* 1342 */     FileSystemProvider localFileSystemProvider = provider(paramPath1);
/* 1343 */     if (provider(paramPath2) == localFileSystemProvider)
/*      */     {
/* 1345 */       localFileSystemProvider.move(paramPath1, paramPath2, paramArrayOfCopyOption);
/*      */     }
/*      */     else {
/* 1348 */       CopyMoveHelper.moveToForeignTarget(paramPath1, paramPath2, paramArrayOfCopyOption);
/*      */     }
/* 1350 */     return paramPath2;
/*      */   }
/*      */ 
/*      */   public static Path readSymbolicLink(Path paramPath)
/*      */     throws IOException
/*      */   {
/* 1382 */     return provider(paramPath).readSymbolicLink(paramPath);
/*      */   }
/*      */ 
/*      */   public static FileStore getFileStore(Path paramPath)
/*      */     throws IOException
/*      */   {
/* 1411 */     return provider(paramPath).getFileStore(paramPath);
/*      */   }
/*      */ 
/*      */   public static boolean isSameFile(Path paramPath1, Path paramPath2)
/*      */     throws IOException
/*      */   {
/* 1454 */     return provider(paramPath1).isSameFile(paramPath1, paramPath2);
/*      */   }
/*      */ 
/*      */   public static boolean isHidden(Path paramPath)
/*      */     throws IOException
/*      */   {
/* 1481 */     return provider(paramPath).isHidden(paramPath);
/*      */   }
/*      */ 
/*      */   public static String probeContentType(Path paramPath)
/*      */     throws IOException
/*      */   {
/* 1558 */     for (FileTypeDetector localFileTypeDetector : FileTypeDetectors.installeDetectors) {
/* 1559 */       String str = localFileTypeDetector.probeContentType(paramPath);
/* 1560 */       if (str != null) {
/* 1561 */         return str;
/*      */       }
/*      */     }
/*      */ 
/* 1565 */     return FileTypeDetectors.defaultFileTypeDetector.probeContentType(paramPath);
/*      */   }
/*      */ 
/*      */   public static <V extends FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption[] paramArrayOfLinkOption)
/*      */   {
/* 1615 */     return provider(paramPath).getFileAttributeView(paramPath, paramClass, paramArrayOfLinkOption);
/*      */   }
/*      */ 
/*      */   public static <A extends BasicFileAttributes> A readAttributes(Path paramPath, Class<A> paramClass, LinkOption[] paramArrayOfLinkOption)
/*      */     throws IOException
/*      */   {
/* 1675 */     return provider(paramPath).readAttributes(paramPath, paramClass, paramArrayOfLinkOption);
/*      */   }
/*      */ 
/*      */   public static Path setAttribute(Path paramPath, String paramString, Object paramObject, LinkOption[] paramArrayOfLinkOption)
/*      */     throws IOException
/*      */   {
/* 1743 */     provider(paramPath).setAttribute(paramPath, paramString, paramObject, paramArrayOfLinkOption);
/* 1744 */     return paramPath;
/*      */   }
/*      */ 
/*      */   public static Object getAttribute(Path paramPath, String paramString, LinkOption[] paramArrayOfLinkOption)
/*      */     throws IOException
/*      */   {
/* 1805 */     if ((paramString.indexOf('*') >= 0) || (paramString.indexOf(',') >= 0))
/* 1806 */       throw new IllegalArgumentException(paramString);
/* 1807 */     Map localMap = readAttributes(paramPath, paramString, paramArrayOfLinkOption);
/* 1808 */     assert (localMap.size() == 1);
/*      */ 
/* 1810 */     int i = paramString.indexOf(':');
/*      */     String str;
/* 1811 */     if (i == -1)
/* 1812 */       str = paramString;
/*      */     else {
/* 1814 */       str = i == paramString.length() ? "" : paramString.substring(i + 1);
/*      */     }
/* 1816 */     return localMap.get(str);
/*      */   }
/*      */ 
/*      */   public static Map<String, Object> readAttributes(Path paramPath, String paramString, LinkOption[] paramArrayOfLinkOption)
/*      */     throws IOException
/*      */   {
/* 1902 */     return provider(paramPath).readAttributes(paramPath, paramString, paramArrayOfLinkOption);
/*      */   }
/*      */ 
/*      */   public static Set<PosixFilePermission> getPosixFilePermissions(Path paramPath, LinkOption[] paramArrayOfLinkOption)
/*      */     throws IOException
/*      */   {
/* 1942 */     return ((PosixFileAttributes)readAttributes(paramPath, PosixFileAttributes.class, paramArrayOfLinkOption)).permissions();
/*      */   }
/*      */ 
/*      */   public static Path setPosixFilePermissions(Path paramPath, Set<PosixFilePermission> paramSet)
/*      */     throws IOException
/*      */   {
/* 1977 */     PosixFileAttributeView localPosixFileAttributeView = (PosixFileAttributeView)getFileAttributeView(paramPath, PosixFileAttributeView.class, new LinkOption[0]);
/*      */ 
/* 1979 */     if (localPosixFileAttributeView == null)
/* 1980 */       throw new UnsupportedOperationException();
/* 1981 */     localPosixFileAttributeView.setPermissions(paramSet);
/* 1982 */     return paramPath;
/*      */   }
/*      */ 
/*      */   public static UserPrincipal getOwner(Path paramPath, LinkOption[] paramArrayOfLinkOption)
/*      */     throws IOException
/*      */   {
/* 2011 */     FileOwnerAttributeView localFileOwnerAttributeView = (FileOwnerAttributeView)getFileAttributeView(paramPath, FileOwnerAttributeView.class, paramArrayOfLinkOption);
/*      */ 
/* 2013 */     if (localFileOwnerAttributeView == null)
/* 2014 */       throw new UnsupportedOperationException();
/* 2015 */     return localFileOwnerAttributeView.getOwner();
/*      */   }
/*      */ 
/*      */   public static Path setOwner(Path paramPath, UserPrincipal paramUserPrincipal)
/*      */     throws IOException
/*      */   {
/* 2057 */     FileOwnerAttributeView localFileOwnerAttributeView = (FileOwnerAttributeView)getFileAttributeView(paramPath, FileOwnerAttributeView.class, new LinkOption[0]);
/*      */ 
/* 2059 */     if (localFileOwnerAttributeView == null)
/* 2060 */       throw new UnsupportedOperationException();
/* 2061 */     localFileOwnerAttributeView.setOwner(paramUserPrincipal);
/* 2062 */     return paramPath;
/*      */   }
/*      */ 
/*      */   public static boolean isSymbolicLink(Path paramPath)
/*      */   {
/*      */     try
/*      */     {
/* 2085 */       return readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }).isSymbolicLink();
/*      */     }
/*      */     catch (IOException localIOException) {
/*      */     }
/* 2089 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean isDirectory(Path paramPath, LinkOption[] paramArrayOfLinkOption)
/*      */   {
/*      */     try
/*      */     {
/* 2124 */       return readAttributes(paramPath, BasicFileAttributes.class, paramArrayOfLinkOption).isDirectory(); } catch (IOException localIOException) {
/*      */     }
/* 2126 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean isRegularFile(Path paramPath, LinkOption[] paramArrayOfLinkOption)
/*      */   {
/*      */     try
/*      */     {
/* 2161 */       return readAttributes(paramPath, BasicFileAttributes.class, paramArrayOfLinkOption).isRegularFile(); } catch (IOException localIOException) {
/*      */     }
/* 2163 */     return false;
/*      */   }
/*      */ 
/*      */   public static FileTime getLastModifiedTime(Path paramPath, LinkOption[] paramArrayOfLinkOption)
/*      */     throws IOException
/*      */   {
/* 2198 */     return readAttributes(paramPath, BasicFileAttributes.class, paramArrayOfLinkOption).lastModifiedTime();
/*      */   }
/*      */ 
/*      */   public static Path setLastModifiedTime(Path paramPath, FileTime paramFileTime)
/*      */     throws IOException
/*      */   {
/* 2237 */     ((BasicFileAttributeView)getFileAttributeView(paramPath, BasicFileAttributeView.class, new LinkOption[0])).setTimes(paramFileTime, null, null);
/*      */ 
/* 2239 */     return paramPath;
/*      */   }
/*      */ 
/*      */   public static long size(Path paramPath)
/*      */     throws IOException
/*      */   {
/* 2264 */     return readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[0]).size();
/*      */   }
/*      */ 
/*      */   private static boolean followLinks(LinkOption[] paramArrayOfLinkOption)
/*      */   {
/* 2273 */     boolean bool = true;
/* 2274 */     for (LinkOption localLinkOption : paramArrayOfLinkOption)
/* 2275 */       if (localLinkOption == LinkOption.NOFOLLOW_LINKS) {
/* 2276 */         bool = false;
/*      */       }
/*      */       else {
/* 2279 */         if (localLinkOption == null)
/* 2280 */           throw new NullPointerException();
/* 2281 */         throw new AssertionError("Should not get here");
/*      */       }
/* 2283 */     return bool;
/*      */   }
/*      */ 
/*      */   public static boolean exists(Path paramPath, LinkOption[] paramArrayOfLinkOption)
/*      */   {
/*      */     try
/*      */     {
/* 2316 */       if (followLinks(paramArrayOfLinkOption)) {
/* 2317 */         provider(paramPath).checkAccess(paramPath, new AccessMode[0]);
/*      */       }
/*      */       else {
/* 2320 */         readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
/*      */       }
/*      */ 
/* 2324 */       return true;
/*      */     } catch (IOException localIOException) {
/*      */     }
/* 2327 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean notExists(Path paramPath, LinkOption[] paramArrayOfLinkOption)
/*      */   {
/*      */     try
/*      */     {
/* 2365 */       if (followLinks(paramArrayOfLinkOption)) {
/* 2366 */         provider(paramPath).checkAccess(paramPath, new AccessMode[0]);
/*      */       }
/*      */       else {
/* 2369 */         readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
/*      */       }
/*      */ 
/* 2373 */       return false;
/*      */     }
/*      */     catch (NoSuchFileException localNoSuchFileException) {
/* 2376 */       return true; } catch (IOException localIOException) {
/*      */     }
/* 2378 */     return false;
/*      */   }
/*      */ 
/*      */   private static boolean isAccessible(Path paramPath, AccessMode[] paramArrayOfAccessMode)
/*      */   {
/*      */     try
/*      */     {
/* 2387 */       provider(paramPath).checkAccess(paramPath, paramArrayOfAccessMode);
/* 2388 */       return true; } catch (IOException localIOException) {
/*      */     }
/* 2390 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean isReadable(Path paramPath)
/*      */   {
/* 2422 */     return isAccessible(paramPath, new AccessMode[] { AccessMode.READ });
/*      */   }
/*      */ 
/*      */   public static boolean isWritable(Path paramPath)
/*      */   {
/* 2453 */     return isAccessible(paramPath, new AccessMode[] { AccessMode.WRITE });
/*      */   }
/*      */ 
/*      */   public static boolean isExecutable(Path paramPath)
/*      */   {
/* 2488 */     return isAccessible(paramPath, new AccessMode[] { AccessMode.EXECUTE });
/*      */   }
/*      */ 
/*      */   public static Path walkFileTree(Path paramPath, Set<FileVisitOption> paramSet, int paramInt, FileVisitor<? super Path> paramFileVisitor)
/*      */     throws IOException
/*      */   {
/* 2589 */     if (paramInt < 0)
/* 2590 */       throw new IllegalArgumentException("'maxDepth' is negative");
/* 2591 */     new FileTreeWalker(paramSet, paramFileVisitor, paramInt).walk(paramPath);
/* 2592 */     return paramPath;
/*      */   }
/*      */ 
/*      */   public static Path walkFileTree(Path paramPath, FileVisitor<? super Path> paramFileVisitor)
/*      */     throws IOException
/*      */   {
/* 2624 */     return walkFileTree(paramPath, EnumSet.noneOf(FileVisitOption.class), 2147483647, paramFileVisitor);
/*      */   }
/*      */ 
/*      */   public static BufferedReader newBufferedReader(Path paramPath, Charset paramCharset)
/*      */     throws IOException
/*      */   {
/* 2665 */     CharsetDecoder localCharsetDecoder = paramCharset.newDecoder();
/* 2666 */     InputStreamReader localInputStreamReader = new InputStreamReader(newInputStream(paramPath, new OpenOption[0]), localCharsetDecoder);
/* 2667 */     return new BufferedReader(localInputStreamReader);
/*      */   }
/*      */ 
/*      */   public static BufferedWriter newBufferedWriter(Path paramPath, Charset paramCharset, OpenOption[] paramArrayOfOpenOption)
/*      */     throws IOException
/*      */   {
/* 2710 */     CharsetEncoder localCharsetEncoder = paramCharset.newEncoder();
/* 2711 */     OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(newOutputStream(paramPath, paramArrayOfOpenOption), localCharsetEncoder);
/* 2712 */     return new BufferedWriter(localOutputStreamWriter);
/*      */   }
/*      */ 
/*      */   private static long copy(InputStream paramInputStream, OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/* 2721 */     long l = 0L;
/* 2722 */     byte[] arrayOfByte = new byte[8192];
/*      */     int i;
/* 2724 */     while ((i = paramInputStream.read(arrayOfByte)) > 0) {
/* 2725 */       paramOutputStream.write(arrayOfByte, 0, i);
/* 2726 */       l += i;
/*      */     }
/* 2728 */     return l;
/*      */   }
/*      */ 
/*      */   public static long copy(InputStream paramInputStream, Path paramPath, CopyOption[] paramArrayOfCopyOption)
/*      */     throws IOException
/*      */   {
/* 2800 */     Objects.requireNonNull(paramInputStream);
/*      */ 
/* 2803 */     int i = 0;
/* 2804 */     for (localObject2 : paramArrayOfCopyOption) {
/* 2805 */       if (localObject2 == StandardCopyOption.REPLACE_EXISTING) {
/* 2806 */         i = 1;
/*      */       } else {
/* 2808 */         if (localObject2 == null) {
/* 2809 */           throw new NullPointerException("options contains 'null'");
/*      */         }
/* 2811 */         throw new UnsupportedOperationException(localObject2 + " not supported");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2817 */     ??? = null;
/* 2818 */     if (i != 0) {
/*      */       try {
/* 2820 */         deleteIfExists(paramPath);
/*      */       } catch (SecurityException localSecurityException) {
/* 2822 */         ??? = localSecurityException;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     OutputStream localOutputStream1;
/*      */     try
/*      */     {
/* 2832 */       localOutputStream1 = newOutputStream(paramPath, new OpenOption[] { StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE });
/*      */     }
/*      */     catch (FileAlreadyExistsException localFileAlreadyExistsException) {
/* 2835 */       if (??? != null) {
/* 2836 */         throw ((Throwable)???);
/*      */       }
/* 2838 */       throw localFileAlreadyExistsException;
/*      */     }
/*      */ 
/* 2842 */     OutputStream localOutputStream2 = localOutputStream1; Object localObject2 = null;
/*      */     try { return copy(paramInputStream, localOutputStream2); }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/* 2842 */       localObject2 = localThrowable1; throw localThrowable1;
/*      */     } finally {
/* 2844 */       if (localOutputStream2 != null) if (localObject2 != null) try { localOutputStream2.close(); } catch (Throwable localThrowable3) { localObject2.addSuppressed(localThrowable3); } else localOutputStream2.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static long copy(Path paramPath, OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/* 2882 */     Objects.requireNonNull(paramOutputStream);
/*      */ 
/* 2884 */     InputStream localInputStream = newInputStream(paramPath, new OpenOption[0]); Object localObject1 = null;
/*      */     try { return copy(localInputStream, paramOutputStream); }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/* 2884 */       localObject1 = localThrowable1; throw localThrowable1;
/*      */     } finally {
/* 2886 */       if (localInputStream != null) if (localObject1 != null) try { localInputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localInputStream.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static byte[] read(InputStream paramInputStream, int paramInt)
/*      */     throws IOException
/*      */   {
/* 2896 */     int i = paramInt;
/* 2897 */     byte[] arrayOfByte = new byte[i];
/* 2898 */     int j = 0;
/* 2899 */     int k = arrayOfByte.length;
/*      */     int m;
/* 2903 */     while ((m = paramInputStream.read(arrayOfByte, j, k)) > 0) {
/* 2904 */       j += m;
/* 2905 */       k -= m;
/* 2906 */       assert (k >= 0);
/* 2907 */       if (k == 0)
/*      */       {
/* 2909 */         int n = i << 1;
/* 2910 */         if (n < 0) {
/* 2911 */           if (i == 2147483647)
/* 2912 */             throw new OutOfMemoryError("Required array size too large");
/* 2913 */           n = 2147483647;
/*      */         }
/* 2915 */         k = n - i;
/* 2916 */         arrayOfByte = Arrays.copyOf(arrayOfByte, n);
/* 2917 */         i = n;
/*      */       }
/*      */     }
/* 2920 */     return i == j ? arrayOfByte : Arrays.copyOf(arrayOfByte, j);
/*      */   }
/*      */ 
/*      */   public static byte[] readAllBytes(Path paramPath)
/*      */     throws IOException
/*      */   {
/* 2948 */     long l = size(paramPath);
/* 2949 */     if (l > 2147483647L) {
/* 2950 */       throw new OutOfMemoryError("Required array size too large");
/*      */     }
/* 2952 */     InputStream localInputStream = newInputStream(paramPath, new OpenOption[0]); Object localObject1 = null;
/*      */     try { return read(localInputStream, (int)l); }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/* 2952 */       localObject1 = localThrowable1; throw localThrowable1;
/*      */     } finally {
/* 2954 */       if (localInputStream != null) if (localObject1 != null) try { localInputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localInputStream.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static List<String> readAllLines(Path paramPath, Charset paramCharset)
/*      */     throws IOException
/*      */   {
/* 2999 */     BufferedReader localBufferedReader = newBufferedReader(paramPath, paramCharset); Object localObject1 = null;
/*      */     try { ArrayList localArrayList = new ArrayList();
/*      */       Object localObject2;
/*      */       while (true) { localObject2 = localBufferedReader.readLine();
/* 3003 */         if (localObject2 == null)
/*      */           break;
/* 3005 */         localArrayList.add(localObject2);
/*      */       }
/* 3007 */       return localArrayList;
/*      */     }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/* 2999 */       localObject1 = localThrowable1; throw localThrowable1;
/*      */     }
/*      */     finally
/*      */     {
/* 3008 */       if (localBufferedReader != null) if (localObject1 != null) try { localBufferedReader.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localBufferedReader.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Path write(Path paramPath, byte[] paramArrayOfByte, OpenOption[] paramArrayOfOpenOption)
/*      */     throws IOException
/*      */   {
/* 3056 */     Objects.requireNonNull(paramArrayOfByte);
/*      */ 
/* 3058 */     OutputStream localOutputStream = newOutputStream(paramPath, paramArrayOfOpenOption); Object localObject1 = null;
/*      */     try { int i = paramArrayOfByte.length;
/* 3060 */       int j = i;
/* 3061 */       while (j > 0) {
/* 3062 */         int k = Math.min(j, 8192);
/* 3063 */         localOutputStream.write(paramArrayOfByte, i - j, k);
/* 3064 */         j -= k;
/*      */       }
/*      */     }
/*      */     catch (Throwable localThrowable2)
/*      */     {
/* 3058 */       localObject1 = localThrowable2; throw localThrowable2;
/*      */     }
/*      */     finally
/*      */     {
/* 3066 */       if (localOutputStream != null) if (localObject1 != null) try { localOutputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localOutputStream.close(); 
/*      */     }
/* 3067 */     return paramPath;
/*      */   }
/*      */ 
/*      */   public static Path write(Path paramPath, Iterable<? extends CharSequence> paramIterable, Charset paramCharset, OpenOption[] paramArrayOfOpenOption)
/*      */     throws IOException
/*      */   {
/* 3115 */     Objects.requireNonNull(paramIterable);
/* 3116 */     CharsetEncoder localCharsetEncoder = paramCharset.newEncoder();
/* 3117 */     OutputStream localOutputStream = newOutputStream(paramPath, paramArrayOfOpenOption);
/* 3118 */     BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(localOutputStream, localCharsetEncoder)); Object localObject1 = null;
/*      */     try { for (CharSequence localCharSequence : paramIterable) {
/* 3120 */         localBufferedWriter.append(localCharSequence);
/* 3121 */         localBufferedWriter.newLine();
/*      */       }
/*      */     }
/*      */     catch (Throwable localThrowable2)
/*      */     {
/* 3118 */       localObject1 = localThrowable2; throw localThrowable2;
/*      */     }
/*      */     finally
/*      */     {
/* 3123 */       if (localBufferedWriter != null) if (localObject1 != null) try { localBufferedWriter.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localBufferedWriter.close(); 
/*      */     }
/* 3124 */     return paramPath;
/*      */   }
/*      */ 
/*      */   private static class AcceptAllFilter
/*      */     implements DirectoryStream.Filter<Path>
/*      */   {
/*  374 */     static final AcceptAllFilter FILTER = new AcceptAllFilter();
/*      */ 
/*      */     public boolean accept(Path paramPath)
/*      */     {
/*  372 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class FileTypeDetectors
/*      */   {
/* 1486 */     static final FileTypeDetector defaultFileTypeDetector = DefaultFileTypeDetector.create();
/*      */ 
/* 1488 */     static final List<FileTypeDetector> installeDetectors = loadInstalledDetectors();
/*      */ 
/*      */     private static List<FileTypeDetector> loadInstalledDetectors()
/*      */     {
/* 1493 */       return (List)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public List<FileTypeDetector> run() {
/* 1496 */           ArrayList localArrayList = new ArrayList();
/* 1497 */           ServiceLoader localServiceLoader = ServiceLoader.load(FileTypeDetector.class, ClassLoader.getSystemClassLoader());
/*      */ 
/* 1499 */           for (FileTypeDetector localFileTypeDetector : localServiceLoader) {
/* 1500 */             localArrayList.add(localFileTypeDetector);
/*      */           }
/* 1502 */           return localArrayList;
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.file.Files
 * JD-Core Version:    0.6.2
 */