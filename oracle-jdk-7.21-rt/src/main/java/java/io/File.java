/*      */ package java.io;
/*      */ 
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.nio.file.FileSystems;
/*      */ import java.nio.file.Path;
/*      */ import java.security.AccessController;
/*      */ import java.security.SecureRandom;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ public class File
/*      */   implements Serializable, Comparable<File>
/*      */ {
/*  156 */   private static FileSystem fs = FileSystem.getFileSystem();
/*      */   private String path;
/*      */   private transient int prefixLength;
/*  189 */   public static final char separatorChar = fs.getSeparator();
/*      */ 
/*  196 */   public static final String separator = "" + separatorChar;
/*      */ 
/*  208 */   public static final char pathSeparatorChar = fs.getPathSeparator();
/*      */ 
/*  215 */   public static final String pathSeparator = "" + pathSeparatorChar;
/*      */   private static final long serialVersionUID = 301077366599181567L;
/*      */   private volatile transient Path filePath;
/*      */ 
/*      */   int getPrefixLength()
/*      */   {
/*  178 */     return this.prefixLength;
/*      */   }
/*      */ 
/*      */   private File(String paramString, int paramInt)
/*      */   {
/*  224 */     this.path = paramString;
/*  225 */     this.prefixLength = paramInt;
/*      */   }
/*      */ 
/*      */   private File(String paramString, File paramFile)
/*      */   {
/*  234 */     assert (paramFile.path != null);
/*  235 */     assert (!paramFile.path.equals(""));
/*  236 */     this.path = fs.resolve(paramFile.path, paramString);
/*  237 */     this.prefixLength = paramFile.prefixLength;
/*      */   }
/*      */ 
/*      */   public File(String paramString)
/*      */   {
/*  250 */     if (paramString == null) {
/*  251 */       throw new NullPointerException();
/*      */     }
/*  253 */     this.path = fs.normalize(paramString);
/*  254 */     this.prefixLength = fs.prefixLength(this.path);
/*      */   }
/*      */ 
/*      */   public File(String paramString1, String paramString2)
/*      */   {
/*  290 */     if (paramString2 == null) {
/*  291 */       throw new NullPointerException();
/*      */     }
/*  293 */     if (paramString1 != null) {
/*  294 */       if (paramString1.equals("")) {
/*  295 */         this.path = fs.resolve(fs.getDefaultParent(), fs.normalize(paramString2));
/*      */       }
/*      */       else {
/*  298 */         this.path = fs.resolve(fs.normalize(paramString1), fs.normalize(paramString2));
/*      */       }
/*      */     }
/*      */     else {
/*  302 */       this.path = fs.normalize(paramString2);
/*      */     }
/*  304 */     this.prefixLength = fs.prefixLength(this.path);
/*      */   }
/*      */ 
/*      */   public File(File paramFile, String paramString)
/*      */   {
/*  333 */     if (paramString == null) {
/*  334 */       throw new NullPointerException();
/*      */     }
/*  336 */     if (paramFile != null) {
/*  337 */       if (paramFile.path.equals("")) {
/*  338 */         this.path = fs.resolve(fs.getDefaultParent(), fs.normalize(paramString));
/*      */       }
/*      */       else {
/*  341 */         this.path = fs.resolve(paramFile.path, fs.normalize(paramString));
/*      */       }
/*      */     }
/*      */     else {
/*  345 */       this.path = fs.normalize(paramString);
/*      */     }
/*  347 */     this.prefixLength = fs.prefixLength(this.path);
/*      */   }
/*      */ 
/*      */   public File(URI paramURI)
/*      */   {
/*  389 */     if (!paramURI.isAbsolute())
/*  390 */       throw new IllegalArgumentException("URI is not absolute");
/*  391 */     if (paramURI.isOpaque())
/*  392 */       throw new IllegalArgumentException("URI is not hierarchical");
/*  393 */     String str1 = paramURI.getScheme();
/*  394 */     if ((str1 == null) || (!str1.equalsIgnoreCase("file")))
/*  395 */       throw new IllegalArgumentException("URI scheme is not \"file\"");
/*  396 */     if (paramURI.getAuthority() != null)
/*  397 */       throw new IllegalArgumentException("URI has an authority component");
/*  398 */     if (paramURI.getFragment() != null)
/*  399 */       throw new IllegalArgumentException("URI has a fragment component");
/*  400 */     if (paramURI.getQuery() != null)
/*  401 */       throw new IllegalArgumentException("URI has a query component");
/*  402 */     String str2 = paramURI.getPath();
/*  403 */     if (str2.equals("")) {
/*  404 */       throw new IllegalArgumentException("URI path component is empty");
/*      */     }
/*      */ 
/*  407 */     str2 = fs.fromURIPath(str2);
/*  408 */     if (separatorChar != '/')
/*  409 */       str2 = str2.replace('/', separatorChar);
/*  410 */     this.path = fs.normalize(str2);
/*  411 */     this.prefixLength = fs.prefixLength(this.path);
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  428 */     int i = this.path.lastIndexOf(separatorChar);
/*  429 */     if (i < this.prefixLength) return this.path.substring(this.prefixLength);
/*  430 */     return this.path.substring(i + 1);
/*      */   }
/*      */ 
/*      */   public String getParent()
/*      */   {
/*  447 */     int i = this.path.lastIndexOf(separatorChar);
/*  448 */     if (i < this.prefixLength) {
/*  449 */       if ((this.prefixLength > 0) && (this.path.length() > this.prefixLength))
/*  450 */         return this.path.substring(0, this.prefixLength);
/*  451 */       return null;
/*      */     }
/*  453 */     return this.path.substring(0, i);
/*      */   }
/*      */ 
/*      */   public File getParentFile()
/*      */   {
/*  473 */     String str = getParent();
/*  474 */     if (str == null) return null;
/*  475 */     return new File(str, this.prefixLength);
/*      */   }
/*      */ 
/*      */   public String getPath()
/*      */   {
/*  486 */     return this.path;
/*      */   }
/*      */ 
/*      */   public boolean isAbsolute()
/*      */   {
/*  503 */     return fs.isAbsolute(this);
/*      */   }
/*      */ 
/*      */   public String getAbsolutePath()
/*      */   {
/*  530 */     return fs.resolve(this);
/*      */   }
/*      */ 
/*      */   public File getAbsoluteFile()
/*      */   {
/*  546 */     String str = getAbsolutePath();
/*  547 */     return new File(str, fs.prefixLength(str));
/*      */   }
/*      */ 
/*      */   public String getCanonicalPath()
/*      */     throws IOException
/*      */   {
/*  589 */     return fs.canonicalize(fs.resolve(this));
/*      */   }
/*      */ 
/*      */   public File getCanonicalFile()
/*      */     throws IOException
/*      */   {
/*  614 */     String str = getCanonicalPath();
/*  615 */     return new File(str, fs.prefixLength(str));
/*      */   }
/*      */ 
/*      */   private static String slashify(String paramString, boolean paramBoolean) {
/*  619 */     String str = paramString;
/*  620 */     if (separatorChar != '/')
/*  621 */       str = str.replace(separatorChar, '/');
/*  622 */     if (!str.startsWith("/"))
/*  623 */       str = "/" + str;
/*  624 */     if ((!str.endsWith("/")) && (paramBoolean))
/*  625 */       str = str + "/";
/*  626 */     return str;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public URL toURL()
/*      */     throws MalformedURLException
/*      */   {
/*  654 */     return new URL("file", "", slashify(getAbsolutePath(), isDirectory()));
/*      */   }
/*      */ 
/*      */   public URI toURI()
/*      */   {
/*      */     try
/*      */     {
/*  699 */       File localFile = getAbsoluteFile();
/*  700 */       String str = slashify(localFile.getPath(), localFile.isDirectory());
/*  701 */       if (str.startsWith("//"))
/*  702 */         str = "//" + str;
/*  703 */       return new URI("file", null, str, null);
/*      */     } catch (URISyntaxException localURISyntaxException) {
/*  705 */       throw new Error(localURISyntaxException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean canRead()
/*      */   {
/*  726 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  727 */     if (localSecurityManager != null) {
/*  728 */       localSecurityManager.checkRead(this.path);
/*      */     }
/*  730 */     return fs.checkAccess(this, 4);
/*      */   }
/*      */ 
/*      */   public boolean canWrite()
/*      */   {
/*  748 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  749 */     if (localSecurityManager != null) {
/*  750 */       localSecurityManager.checkWrite(this.path);
/*      */     }
/*  752 */     return fs.checkAccess(this, 2);
/*      */   }
/*      */ 
/*      */   public boolean exists()
/*      */   {
/*  768 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  769 */     if (localSecurityManager != null) {
/*  770 */       localSecurityManager.checkRead(this.path);
/*      */     }
/*  772 */     return (fs.getBooleanAttributes(this) & 0x1) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isDirectory()
/*      */   {
/*  795 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  796 */     if (localSecurityManager != null) {
/*  797 */       localSecurityManager.checkRead(this.path);
/*      */     }
/*  799 */     return (fs.getBooleanAttributes(this) & 0x4) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isFile()
/*      */   {
/*  825 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  826 */     if (localSecurityManager != null) {
/*  827 */       localSecurityManager.checkRead(this.path);
/*      */     }
/*  829 */     return (fs.getBooleanAttributes(this) & 0x2) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isHidden()
/*      */   {
/*  851 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  852 */     if (localSecurityManager != null) {
/*  853 */       localSecurityManager.checkRead(this.path);
/*      */     }
/*  855 */     return (fs.getBooleanAttributes(this) & 0x8) != 0;
/*      */   }
/*      */ 
/*      */   public long lastModified()
/*      */   {
/*  880 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  881 */     if (localSecurityManager != null) {
/*  882 */       localSecurityManager.checkRead(this.path);
/*      */     }
/*  884 */     return fs.getLastModifiedTime(this);
/*      */   }
/*      */ 
/*      */   public long length()
/*      */   {
/*  908 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  909 */     if (localSecurityManager != null) {
/*  910 */       localSecurityManager.checkRead(this.path);
/*      */     }
/*  912 */     return fs.getLength(this);
/*      */   }
/*      */ 
/*      */   public boolean createNewFile()
/*      */     throws IOException
/*      */   {
/*  945 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  946 */     if (localSecurityManager != null) localSecurityManager.checkWrite(this.path);
/*  947 */     return fs.createFileExclusively(this.path);
/*      */   }
/*      */ 
/*      */   public boolean delete()
/*      */   {
/*  969 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  970 */     if (localSecurityManager != null) {
/*  971 */       localSecurityManager.checkDelete(this.path);
/*      */     }
/*  973 */     return fs.delete(this);
/*      */   }
/*      */ 
/*      */   public void deleteOnExit()
/*      */   {
/* 1004 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1005 */     if (localSecurityManager != null) {
/* 1006 */       localSecurityManager.checkDelete(this.path);
/*      */     }
/* 1008 */     DeleteOnExitHook.add(this.path);
/*      */   }
/*      */ 
/*      */   public String[] list()
/*      */   {
/* 1044 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1045 */     if (localSecurityManager != null) {
/* 1046 */       localSecurityManager.checkRead(this.path);
/*      */     }
/* 1048 */     return fs.list(this);
/*      */   }
/*      */ 
/*      */   public String[] list(FilenameFilter paramFilenameFilter)
/*      */   {
/* 1081 */     String[] arrayOfString = list();
/* 1082 */     if ((arrayOfString == null) || (paramFilenameFilter == null)) {
/* 1083 */       return arrayOfString;
/*      */     }
/* 1085 */     ArrayList localArrayList = new ArrayList();
/* 1086 */     for (int i = 0; i < arrayOfString.length; i++) {
/* 1087 */       if (paramFilenameFilter.accept(this, arrayOfString[i])) {
/* 1088 */         localArrayList.add(arrayOfString[i]);
/*      */       }
/*      */     }
/* 1091 */     return (String[])localArrayList.toArray(new String[localArrayList.size()]);
/*      */   }
/*      */ 
/*      */   public File[] listFiles()
/*      */   {
/* 1133 */     String[] arrayOfString = list();
/* 1134 */     if (arrayOfString == null) return null;
/* 1135 */     int i = arrayOfString.length;
/* 1136 */     File[] arrayOfFile = new File[i];
/* 1137 */     for (int j = 0; j < i; j++) {
/* 1138 */       arrayOfFile[j] = new File(arrayOfString[j], this);
/*      */     }
/* 1140 */     return arrayOfFile;
/*      */   }
/*      */ 
/*      */   public File[] listFiles(FilenameFilter paramFilenameFilter)
/*      */   {
/* 1174 */     String[] arrayOfString1 = list();
/* 1175 */     if (arrayOfString1 == null) return null;
/* 1176 */     ArrayList localArrayList = new ArrayList();
/* 1177 */     for (String str : arrayOfString1)
/* 1178 */       if ((paramFilenameFilter == null) || (paramFilenameFilter.accept(this, str)))
/* 1179 */         localArrayList.add(new File(str, this));
/* 1180 */     return (File[])localArrayList.toArray(new File[localArrayList.size()]);
/*      */   }
/*      */ 
/*      */   public File[] listFiles(FileFilter paramFileFilter)
/*      */   {
/* 1212 */     String[] arrayOfString1 = list();
/* 1213 */     if (arrayOfString1 == null) return null;
/* 1214 */     ArrayList localArrayList = new ArrayList();
/* 1215 */     for (String str : arrayOfString1) {
/* 1216 */       File localFile = new File(str, this);
/* 1217 */       if ((paramFileFilter == null) || (paramFileFilter.accept(localFile)))
/* 1218 */         localArrayList.add(localFile);
/*      */     }
/* 1220 */     return (File[])localArrayList.toArray(new File[localArrayList.size()]);
/*      */   }
/*      */ 
/*      */   public boolean mkdir()
/*      */   {
/* 1235 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1236 */     if (localSecurityManager != null) {
/* 1237 */       localSecurityManager.checkWrite(this.path);
/*      */     }
/* 1239 */     return fs.createDirectory(this);
/*      */   }
/*      */ 
/*      */   public boolean mkdirs()
/*      */   {
/* 1263 */     if (exists()) {
/* 1264 */       return false;
/*      */     }
/* 1266 */     if (mkdir()) {
/* 1267 */       return true;
/*      */     }
/* 1269 */     File localFile1 = null;
/*      */     try {
/* 1271 */       localFile1 = getCanonicalFile();
/*      */     } catch (IOException localIOException) {
/* 1273 */       return false;
/*      */     }
/*      */ 
/* 1276 */     File localFile2 = localFile1.getParentFile();
/* 1277 */     return (localFile2 != null) && ((localFile2.mkdirs()) || (localFile2.exists())) && (localFile1.mkdir());
/*      */   }
/*      */ 
/*      */   public boolean renameTo(File paramFile)
/*      */   {
/* 1309 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1310 */     if (localSecurityManager != null) {
/* 1311 */       localSecurityManager.checkWrite(this.path);
/* 1312 */       localSecurityManager.checkWrite(paramFile.path);
/*      */     }
/* 1314 */     return fs.rename(this, paramFile);
/*      */   }
/*      */ 
/*      */   public boolean setLastModified(long paramLong)
/*      */   {
/* 1344 */     if (paramLong < 0L) throw new IllegalArgumentException("Negative time");
/* 1345 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1346 */     if (localSecurityManager != null) {
/* 1347 */       localSecurityManager.checkWrite(this.path);
/*      */     }
/* 1349 */     return fs.setLastModifiedTime(this, paramLong);
/*      */   }
/*      */ 
/*      */   public boolean setReadOnly()
/*      */   {
/* 1370 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1371 */     if (localSecurityManager != null) {
/* 1372 */       localSecurityManager.checkWrite(this.path);
/*      */     }
/* 1374 */     return fs.setReadOnly(this);
/*      */   }
/*      */ 
/*      */   public boolean setWritable(boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 1408 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1409 */     if (localSecurityManager != null) {
/* 1410 */       localSecurityManager.checkWrite(this.path);
/*      */     }
/* 1412 */     return fs.setPermission(this, 2, paramBoolean1, paramBoolean2);
/*      */   }
/*      */ 
/*      */   public boolean setWritable(boolean paramBoolean)
/*      */   {
/* 1441 */     return setWritable(paramBoolean, true);
/*      */   }
/*      */ 
/*      */   public boolean setReadable(boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 1478 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1479 */     if (localSecurityManager != null) {
/* 1480 */       localSecurityManager.checkWrite(this.path);
/*      */     }
/* 1482 */     return fs.setPermission(this, 4, paramBoolean1, paramBoolean2);
/*      */   }
/*      */ 
/*      */   public boolean setReadable(boolean paramBoolean)
/*      */   {
/* 1514 */     return setReadable(paramBoolean, true);
/*      */   }
/*      */ 
/*      */   public boolean setExecutable(boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/* 1551 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1552 */     if (localSecurityManager != null) {
/* 1553 */       localSecurityManager.checkWrite(this.path);
/*      */     }
/* 1555 */     return fs.setPermission(this, 1, paramBoolean1, paramBoolean2);
/*      */   }
/*      */ 
/*      */   public boolean setExecutable(boolean paramBoolean)
/*      */   {
/* 1587 */     return setExecutable(paramBoolean, true);
/*      */   }
/*      */ 
/*      */   public boolean canExecute()
/*      */   {
/* 1605 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1606 */     if (localSecurityManager != null) {
/* 1607 */       localSecurityManager.checkExec(this.path);
/*      */     }
/* 1609 */     return fs.checkAccess(this, 1);
/*      */   }
/*      */ 
/*      */   public static File[] listRoots()
/*      */   {
/* 1658 */     return fs.listRoots();
/*      */   }
/*      */ 
/*      */   public long getTotalSpace()
/*      */   {
/* 1680 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1681 */     if (localSecurityManager != null) {
/* 1682 */       localSecurityManager.checkPermission(new RuntimePermission("getFileSystemAttributes"));
/* 1683 */       localSecurityManager.checkRead(this.path);
/*      */     }
/* 1685 */     return fs.getSpace(this, 0);
/*      */   }
/*      */ 
/*      */   public long getFreeSpace()
/*      */   {
/* 1715 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1716 */     if (localSecurityManager != null) {
/* 1717 */       localSecurityManager.checkPermission(new RuntimePermission("getFileSystemAttributes"));
/* 1718 */       localSecurityManager.checkRead(this.path);
/*      */     }
/* 1720 */     return fs.getSpace(this, 1);
/*      */   }
/*      */ 
/*      */   public long getUsableSpace()
/*      */   {
/* 1753 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1754 */     if (localSecurityManager != null) {
/* 1755 */       localSecurityManager.checkPermission(new RuntimePermission("getFileSystemAttributes"));
/* 1756 */       localSecurityManager.checkRead(this.path);
/*      */     }
/* 1758 */     return fs.getSpace(this, 2);
/*      */   }
/*      */ 
/*      */   public static File createTempFile(String paramString1, String paramString2, File paramFile)
/*      */     throws IOException
/*      */   {
/* 1859 */     if (paramString1.length() < 3)
/* 1860 */       throw new IllegalArgumentException("Prefix string too short");
/* 1861 */     if (paramString2 == null) {
/* 1862 */       paramString2 = ".tmp";
/* 1864 */     }File localFile1 = paramFile != null ? paramFile : TempDirectory.location();
/* 1865 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*      */     File localFile2;
/*      */     do { localFile2 = TempDirectory.generateFile(paramString1, paramString2, localFile1);
/* 1869 */       if (localSecurityManager != null)
/*      */         try {
/* 1871 */           localSecurityManager.checkWrite(localFile2.getPath());
/*      */         }
/*      */         catch (SecurityException localSecurityException) {
/* 1874 */           if (paramFile == null)
/* 1875 */             throw new SecurityException("Unable to create temporary file");
/* 1876 */           throw localSecurityException;
/*      */         }
/*      */     }
/* 1879 */     while (!fs.createFileExclusively(localFile2.getPath()));
/* 1880 */     return localFile2;
/*      */   }
/*      */ 
/*      */   public static File createTempFile(String paramString1, String paramString2)
/*      */     throws IOException
/*      */   {
/* 1923 */     return createTempFile(paramString1, paramString2, null);
/*      */   }
/*      */ 
/*      */   public int compareTo(File paramFile)
/*      */   {
/* 1946 */     return fs.compare(this, paramFile);
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/* 1964 */     if ((paramObject != null) && ((paramObject instanceof File))) {
/* 1965 */       return compareTo((File)paramObject) == 0;
/*      */     }
/* 1967 */     return false;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1985 */     return fs.hashCode(this);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1995 */     return getPath();
/*      */   }
/*      */ 
/*      */   private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/* 2008 */     paramObjectOutputStream.defaultWriteObject();
/* 2009 */     paramObjectOutputStream.writeChar(separatorChar);
/*      */   }
/*      */ 
/*      */   private synchronized void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 2021 */     ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
/* 2022 */     String str = (String)localGetField.get("path", null);
/* 2023 */     char c = paramObjectInputStream.readChar();
/* 2024 */     if (c != separatorChar)
/* 2025 */       str = str.replace(c, separatorChar);
/* 2026 */     this.path = fs.normalize(str);
/* 2027 */     this.prefixLength = fs.prefixLength(this.path);
/*      */   }
/*      */ 
/*      */   public Path toPath()
/*      */   {
/* 2064 */     Path localPath = this.filePath;
/* 2065 */     if (localPath == null) {
/* 2066 */       synchronized (this) {
/* 2067 */         localPath = this.filePath;
/* 2068 */         if (localPath == null) {
/* 2069 */           localPath = FileSystems.getDefault().getPath(this.path, new String[0]);
/* 2070 */           this.filePath = localPath;
/*      */         }
/*      */       }
/*      */     }
/* 2074 */     return localPath;
/*      */   }
/*      */ 
/*      */   private static class TempDirectory
/*      */   {
/* 1767 */     private static final File tmpdir = new File(File.fs.normalize((String)AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir"))));
/*      */ 
/* 1774 */     private static final SecureRandom random = new SecureRandom();
/*      */ 
/*      */     static File location()
/*      */     {
/* 1770 */       return tmpdir;
/*      */     }
/*      */ 
/*      */     static File generateFile(String paramString1, String paramString2, File paramFile)
/*      */     {
/* 1776 */       long l = random.nextLong();
/* 1777 */       if (l == -9223372036854775808L)
/* 1778 */         l = 0L;
/*      */       else {
/* 1780 */         l = Math.abs(l);
/*      */       }
/* 1782 */       return new File(paramFile, paramString1 + Long.toString(l) + paramString2);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.File
 * JD-Core Version:    0.6.2
 */