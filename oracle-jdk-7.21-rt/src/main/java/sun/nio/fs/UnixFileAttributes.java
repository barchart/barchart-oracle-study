/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.FileTime;
/*     */ import java.nio.file.attribute.GroupPrincipal;
/*     */ import java.nio.file.attribute.PosixFileAttributes;
/*     */ import java.nio.file.attribute.PosixFilePermission;
/*     */ import java.nio.file.attribute.UserPrincipal;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ class UnixFileAttributes
/*     */   implements PosixFileAttributes
/*     */ {
/*     */   private int st_mode;
/*     */   private long st_ino;
/*     */   private long st_dev;
/*     */   private long st_rdev;
/*     */   private int st_nlink;
/*     */   private int st_uid;
/*     */   private int st_gid;
/*     */   private long st_size;
/*     */   private long st_atime;
/*     */   private long st_mtime;
/*     */   private long st_ctime;
/*     */   private volatile UserPrincipal owner;
/*     */   private volatile GroupPrincipal group;
/*     */   private volatile UnixFileKey key;
/*     */ 
/*     */   static UnixFileAttributes get(UnixPath paramUnixPath, boolean paramBoolean)
/*     */     throws UnixException
/*     */   {
/*  64 */     UnixFileAttributes localUnixFileAttributes = new UnixFileAttributes();
/*  65 */     if (paramBoolean)
/*  66 */       UnixNativeDispatcher.stat(paramUnixPath, localUnixFileAttributes);
/*     */     else {
/*  68 */       UnixNativeDispatcher.lstat(paramUnixPath, localUnixFileAttributes);
/*     */     }
/*  70 */     return localUnixFileAttributes;
/*     */   }
/*     */ 
/*     */   static UnixFileAttributes get(int paramInt) throws UnixException
/*     */   {
/*  75 */     UnixFileAttributes localUnixFileAttributes = new UnixFileAttributes();
/*  76 */     UnixNativeDispatcher.fstat(paramInt, localUnixFileAttributes);
/*  77 */     return localUnixFileAttributes;
/*     */   }
/*     */ 
/*     */   static UnixFileAttributes get(int paramInt, UnixPath paramUnixPath, boolean paramBoolean)
/*     */     throws UnixException
/*     */   {
/*  84 */     UnixFileAttributes localUnixFileAttributes = new UnixFileAttributes();
/*  85 */     int i = paramBoolean ? 0 : 256;
/*  86 */     UnixNativeDispatcher.fstatat(paramInt, paramUnixPath.asByteArray(), i, localUnixFileAttributes);
/*  87 */     return localUnixFileAttributes;
/*     */   }
/*     */ 
/*     */   boolean isSameFile(UnixFileAttributes paramUnixFileAttributes)
/*     */   {
/*  92 */     return (this.st_ino == paramUnixFileAttributes.st_ino) && (this.st_dev == paramUnixFileAttributes.st_dev);
/*     */   }
/*     */ 
/*     */   int mode() {
/*  96 */     return this.st_mode; } 
/*  97 */   long ino() { return this.st_ino; } 
/*  98 */   long dev() { return this.st_dev; } 
/*  99 */   long rdev() { return this.st_rdev; } 
/* 100 */   int nlink() { return this.st_nlink; } 
/* 101 */   int uid() { return this.st_uid; } 
/* 102 */   int gid() { return this.st_gid; }
/*     */ 
/*     */   FileTime ctime() {
/* 105 */     return FileTime.from(this.st_ctime, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */   boolean isDevice() {
/* 109 */     int i = this.st_mode & 0xF000;
/* 110 */     return (i == 8192) || (i == 24576) || (i == 4096);
/*     */   }
/*     */ 
/*     */   public FileTime lastModifiedTime()
/*     */   {
/* 117 */     return FileTime.from(this.st_mtime, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */   public FileTime lastAccessTime()
/*     */   {
/* 122 */     return FileTime.from(this.st_atime, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */   public FileTime creationTime()
/*     */   {
/* 127 */     return lastModifiedTime();
/*     */   }
/*     */ 
/*     */   public boolean isRegularFile()
/*     */   {
/* 132 */     return (this.st_mode & 0xF000) == 32768;
/*     */   }
/*     */ 
/*     */   public boolean isDirectory()
/*     */   {
/* 137 */     return (this.st_mode & 0xF000) == 16384;
/*     */   }
/*     */ 
/*     */   public boolean isSymbolicLink()
/*     */   {
/* 142 */     return (this.st_mode & 0xF000) == 40960;
/*     */   }
/*     */ 
/*     */   public boolean isOther()
/*     */   {
/* 147 */     int i = this.st_mode & 0xF000;
/* 148 */     return (i != 32768) && (i != 16384) && (i != 40960);
/*     */   }
/*     */ 
/*     */   public long size()
/*     */   {
/* 155 */     return this.st_size;
/*     */   }
/*     */ 
/*     */   public UnixFileKey fileKey()
/*     */   {
/* 160 */     if (this.key == null) {
/* 161 */       synchronized (this) {
/* 162 */         if (this.key == null) {
/* 163 */           this.key = new UnixFileKey(this.st_dev, this.st_ino);
/*     */         }
/*     */       }
/*     */     }
/* 167 */     return this.key;
/*     */   }
/*     */ 
/*     */   public UserPrincipal owner()
/*     */   {
/* 172 */     if (this.owner == null) {
/* 173 */       synchronized (this) {
/* 174 */         if (this.owner == null) {
/* 175 */           this.owner = UnixUserPrincipals.fromUid(this.st_uid);
/*     */         }
/*     */       }
/*     */     }
/* 179 */     return this.owner;
/*     */   }
/*     */ 
/*     */   public GroupPrincipal group()
/*     */   {
/* 184 */     if (this.group == null) {
/* 185 */       synchronized (this) {
/* 186 */         if (this.group == null) {
/* 187 */           this.group = UnixUserPrincipals.fromGid(this.st_gid);
/*     */         }
/*     */       }
/*     */     }
/* 191 */     return this.group;
/*     */   }
/*     */ 
/*     */   public Set<PosixFilePermission> permissions()
/*     */   {
/* 196 */     int i = this.st_mode & 0x1FF;
/* 197 */     HashSet localHashSet = new HashSet();
/*     */ 
/* 199 */     if ((i & 0x100) > 0)
/* 200 */       localHashSet.add(PosixFilePermission.OWNER_READ);
/* 201 */     if ((i & 0x80) > 0)
/* 202 */       localHashSet.add(PosixFilePermission.OWNER_WRITE);
/* 203 */     if ((i & 0x40) > 0) {
/* 204 */       localHashSet.add(PosixFilePermission.OWNER_EXECUTE);
/*     */     }
/* 206 */     if ((i & 0x20) > 0)
/* 207 */       localHashSet.add(PosixFilePermission.GROUP_READ);
/* 208 */     if ((i & 0x10) > 0)
/* 209 */       localHashSet.add(PosixFilePermission.GROUP_WRITE);
/* 210 */     if ((i & 0x8) > 0) {
/* 211 */       localHashSet.add(PosixFilePermission.GROUP_EXECUTE);
/*     */     }
/* 213 */     if ((i & 0x4) > 0)
/* 214 */       localHashSet.add(PosixFilePermission.OTHERS_READ);
/* 215 */     if ((i & 0x2) > 0)
/* 216 */       localHashSet.add(PosixFilePermission.OTHERS_WRITE);
/* 217 */     if ((i & 0x1) > 0) {
/* 218 */       localHashSet.add(PosixFilePermission.OTHERS_EXECUTE);
/*     */     }
/* 220 */     return localHashSet;
/*     */   }
/*     */ 
/*     */   BasicFileAttributes asBasicFileAttributes()
/*     */   {
/* 226 */     return UnixAsBasicFileAttributes.wrap(this);
/*     */   }
/*     */ 
/*     */   static UnixFileAttributes toUnixFileAttributes(BasicFileAttributes paramBasicFileAttributes)
/*     */   {
/* 232 */     if ((paramBasicFileAttributes instanceof UnixFileAttributes))
/* 233 */       return (UnixFileAttributes)paramBasicFileAttributes;
/* 234 */     if ((paramBasicFileAttributes instanceof UnixAsBasicFileAttributes)) {
/* 235 */       return ((UnixAsBasicFileAttributes)paramBasicFileAttributes).unwrap();
/*     */     }
/* 237 */     return null;
/*     */   }
/*     */ 
/*     */   private static class UnixAsBasicFileAttributes implements BasicFileAttributes
/*     */   {
/*     */     private final UnixFileAttributes attrs;
/*     */ 
/*     */     private UnixAsBasicFileAttributes(UnixFileAttributes paramUnixFileAttributes) {
/* 245 */       this.attrs = paramUnixFileAttributes;
/*     */     }
/*     */ 
/*     */     static UnixAsBasicFileAttributes wrap(UnixFileAttributes paramUnixFileAttributes) {
/* 249 */       return new UnixAsBasicFileAttributes(paramUnixFileAttributes);
/*     */     }
/*     */ 
/*     */     UnixFileAttributes unwrap() {
/* 253 */       return this.attrs;
/*     */     }
/*     */ 
/*     */     public FileTime lastModifiedTime()
/*     */     {
/* 258 */       return this.attrs.lastModifiedTime();
/*     */     }
/*     */ 
/*     */     public FileTime lastAccessTime() {
/* 262 */       return this.attrs.lastAccessTime();
/*     */     }
/*     */ 
/*     */     public FileTime creationTime() {
/* 266 */       return this.attrs.creationTime();
/*     */     }
/*     */ 
/*     */     public boolean isRegularFile() {
/* 270 */       return this.attrs.isRegularFile();
/*     */     }
/*     */ 
/*     */     public boolean isDirectory() {
/* 274 */       return this.attrs.isDirectory();
/*     */     }
/*     */ 
/*     */     public boolean isSymbolicLink() {
/* 278 */       return this.attrs.isSymbolicLink();
/*     */     }
/*     */ 
/*     */     public boolean isOther() {
/* 282 */       return this.attrs.isOther();
/*     */     }
/*     */ 
/*     */     public long size() {
/* 286 */       return this.attrs.size();
/*     */     }
/*     */ 
/*     */     public Object fileKey() {
/* 290 */       return this.attrs.fileKey();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixFileAttributes
 * JD-Core Version:    0.6.2
 */