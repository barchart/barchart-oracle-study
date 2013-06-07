/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.FileStore;
/*     */ import java.nio.file.FileSystem;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.PathMatcher;
/*     */ import java.nio.file.attribute.GroupPrincipal;
/*     */ import java.nio.file.attribute.UserPrincipal;
/*     */ import java.nio.file.attribute.UserPrincipalLookupService;
/*     */ import java.nio.file.spi.FileSystemProvider;
/*     */ import java.security.AccessController;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ abstract class UnixFileSystem extends FileSystem
/*     */ {
/*     */   private final UnixFileSystemProvider provider;
/*     */   private final byte[] defaultDirectory;
/*     */   private final boolean needToResolveAgainstDefaultDirectory;
/*     */   private final UnixPath rootDirectory;
/*     */   private static final String GLOB_SYNTAX = "glob";
/*     */   private static final String REGEX_SYNTAX = "regex";
/*     */ 
/*     */   UnixFileSystem(UnixFileSystemProvider paramUnixFileSystemProvider, String paramString)
/*     */   {
/*  51 */     this.provider = paramUnixFileSystemProvider;
/*  52 */     this.defaultDirectory = UnixPath.normalizeAndCheck(paramString).getBytes();
/*  53 */     if (this.defaultDirectory[0] != 47) {
/*  54 */       throw new RuntimeException("default directory must be absolute");
/*     */     }
/*     */ 
/*  60 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.fs.chdirAllowed", "false"));
/*     */ 
/*  62 */     boolean bool = str.length() == 0 ? true : Boolean.valueOf(str).booleanValue();
/*     */ 
/*  64 */     if (bool) {
/*  65 */       this.needToResolveAgainstDefaultDirectory = true;
/*     */     } else {
/*  67 */       byte[] arrayOfByte = UnixNativeDispatcher.getcwd();
/*  68 */       int i = arrayOfByte.length == this.defaultDirectory.length ? 1 : 0;
/*  69 */       if (i != 0) {
/*  70 */         for (int j = 0; j < arrayOfByte.length; j++) {
/*  71 */           if (arrayOfByte[j] != this.defaultDirectory[j]) {
/*  72 */             i = 0;
/*  73 */             break;
/*     */           }
/*     */         }
/*     */       }
/*  77 */       this.needToResolveAgainstDefaultDirectory = (i == 0);
/*     */     }
/*     */ 
/*  81 */     this.rootDirectory = new UnixPath(this, "/");
/*     */   }
/*     */ 
/*     */   byte[] defaultDirectory()
/*     */   {
/*  86 */     return this.defaultDirectory;
/*     */   }
/*     */ 
/*     */   boolean needToResolveAgainstDefaultDirectory() {
/*  90 */     return this.needToResolveAgainstDefaultDirectory;
/*     */   }
/*     */ 
/*     */   UnixPath rootDirectory() {
/*  94 */     return this.rootDirectory;
/*     */   }
/*     */ 
/*     */   boolean isSolaris() {
/*  98 */     return false;
/*     */   }
/*     */ 
/*     */   static List<String> standardFileAttributeViews() {
/* 102 */     return Arrays.asList(new String[] { "basic", "posix", "unix", "owner" });
/*     */   }
/*     */ 
/*     */   public final FileSystemProvider provider()
/*     */   {
/* 107 */     return this.provider;
/*     */   }
/*     */ 
/*     */   public final String getSeparator()
/*     */   {
/* 112 */     return "/";
/*     */   }
/*     */ 
/*     */   public final boolean isOpen()
/*     */   {
/* 117 */     return true;
/*     */   }
/*     */ 
/*     */   public final boolean isReadOnly()
/*     */   {
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */   public final void close() throws IOException
/*     */   {
/* 127 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   void copyNonPosixAttributes(int paramInt1, int paramInt2)
/*     */   {
/*     */   }
/*     */ 
/*     */   public final Iterable<Path> getRootDirectories()
/*     */   {
/* 153 */     final List localList = Collections.unmodifiableList(Arrays.asList(new Path[] { this.rootDirectory }));
/*     */ 
/* 155 */     return new Iterable() {
/*     */       public Iterator<Path> iterator() {
/*     */         try {
/* 158 */           SecurityManager localSecurityManager = System.getSecurityManager();
/* 159 */           if (localSecurityManager != null)
/* 160 */             localSecurityManager.checkRead(UnixFileSystem.this.rootDirectory.toString());
/* 161 */           return localList.iterator();
/*     */         } catch (SecurityException localSecurityException) {
/* 163 */           List localList = Collections.emptyList();
/* 164 */           return localList.iterator();
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   abstract Iterable<UnixMountEntry> getMountEntries();
/*     */ 
/*     */   abstract FileStore getFileStore(UnixMountEntry paramUnixMountEntry)
/*     */     throws IOException;
/*     */ 
/*     */   public final Iterable<FileStore> getFileStores()
/*     */   {
/* 249 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 250 */     if (localSecurityManager != null) {
/*     */       try {
/* 252 */         localSecurityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
/*     */       } catch (SecurityException localSecurityException) {
/* 254 */         return Collections.emptyList();
/*     */       }
/*     */     }
/* 257 */     return new Iterable() {
/*     */       public Iterator<FileStore> iterator() {
/* 259 */         return new UnixFileSystem.FileStoreIterator(UnixFileSystem.this);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public final Path getPath(String paramString, String[] paramArrayOfString)
/*     */   {
/*     */     String str1;
/* 267 */     if (paramArrayOfString.length == 0) {
/* 268 */       str1 = paramString;
/*     */     } else {
/* 270 */       StringBuilder localStringBuilder = new StringBuilder();
/* 271 */       localStringBuilder.append(paramString);
/* 272 */       for (String str2 : paramArrayOfString) {
/* 273 */         if (str2.length() > 0) {
/* 274 */           if (localStringBuilder.length() > 0)
/* 275 */             localStringBuilder.append('/');
/* 276 */           localStringBuilder.append(str2);
/*     */         }
/*     */       }
/* 279 */       str1 = localStringBuilder.toString();
/*     */     }
/* 281 */     return new UnixPath(this, str1);
/*     */   }
/*     */ 
/*     */   public PathMatcher getPathMatcher(String paramString)
/*     */   {
/* 286 */     int i = paramString.indexOf(':');
/* 287 */     if ((i <= 0) || (i == paramString.length()))
/* 288 */       throw new IllegalArgumentException();
/* 289 */     String str1 = paramString.substring(0, i);
/* 290 */     String str2 = paramString.substring(i + 1);
/*     */     String str3;
/* 293 */     if (str1.equals("glob")) {
/* 294 */       str3 = Globs.toUnixRegexPattern(str2);
/*     */     }
/* 296 */     else if (str1.equals("regex"))
/* 297 */       str3 = str2;
/*     */     else {
/* 299 */       throw new UnsupportedOperationException("Syntax '" + str1 + "' not recognized");
/*     */     }
/*     */ 
/* 305 */     final Pattern localPattern = Pattern.compile(str3);
/* 306 */     return new PathMatcher()
/*     */     {
/*     */       public boolean matches(Path paramAnonymousPath) {
/* 309 */         return localPattern.matcher(paramAnonymousPath.toString()).matches();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public final UserPrincipalLookupService getUserPrincipalLookupService()
/*     */   {
/* 320 */     return LookupService.instance;
/*     */   }
/*     */ 
/*     */   private class FileStoreIterator
/*     */     implements Iterator<FileStore>
/*     */   {
/*     */     private final Iterator<UnixMountEntry> entries;
/*     */     private FileStore next;
/*     */ 
/*     */     FileStoreIterator()
/*     */     {
/* 189 */       this.entries = UnixFileSystem.this.getMountEntries().iterator();
/*     */     }
/*     */ 
/*     */     private FileStore readNext() {
/* 193 */       assert (Thread.holdsLock(this));
/*     */       while (true) {
/* 195 */         if (!this.entries.hasNext())
/* 196 */           return null;
/* 197 */         UnixMountEntry localUnixMountEntry = (UnixMountEntry)this.entries.next();
/*     */ 
/* 200 */         if (!localUnixMountEntry.isIgnored())
/*     */         {
/* 204 */           SecurityManager localSecurityManager = System.getSecurityManager();
/* 205 */           if (localSecurityManager != null)
/*     */             try {
/* 207 */               localSecurityManager.checkRead(new String(localUnixMountEntry.dir()));
/*     */             }
/*     */             catch (SecurityException localSecurityException) {
/*     */             }
/*     */           else
/*     */             try {
/* 213 */               return UnixFileSystem.this.getFileStore(localUnixMountEntry);
/*     */             }
/*     */             catch (IOException localIOException) {
/*     */             }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public synchronized boolean hasNext() {
/* 222 */       if (this.next != null)
/* 223 */         return true;
/* 224 */       this.next = readNext();
/* 225 */       return this.next != null;
/*     */     }
/*     */ 
/*     */     public synchronized FileStore next()
/*     */     {
/* 230 */       if (this.next == null)
/* 231 */         this.next = readNext();
/* 232 */       if (this.next == null) {
/* 233 */         throw new NoSuchElementException();
/*     */       }
/* 235 */       FileStore localFileStore = this.next;
/* 236 */       this.next = null;
/* 237 */       return localFileStore;
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/* 243 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class LookupService
/*     */   {
/* 324 */     static final UserPrincipalLookupService instance = new UserPrincipalLookupService()
/*     */     {
/*     */       public UserPrincipal lookupPrincipalByName(String paramAnonymousString)
/*     */         throws IOException
/*     */       {
/* 330 */         return UnixUserPrincipals.lookupUser(paramAnonymousString);
/*     */       }
/*     */ 
/*     */       public GroupPrincipal lookupPrincipalByGroupName(String paramAnonymousString)
/*     */         throws IOException
/*     */       {
/* 337 */         return UnixUserPrincipals.lookupGroup(paramAnonymousString);
/*     */       }
/* 324 */     };
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixFileSystem
 * JD-Core Version:    0.6.2
 */