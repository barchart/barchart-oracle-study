/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.attribute.DosFileAttributeView;
/*     */ import java.nio.file.attribute.FileAttributeView;
/*     */ import java.nio.file.attribute.UserDefinedFileAttributeView;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ class LinuxFileStore extends UnixFileStore
/*     */ {
/*     */   private volatile boolean xattrChecked;
/*     */   private volatile boolean xattrEnabled;
/*     */ 
/*     */   LinuxFileStore(UnixPath paramUnixPath)
/*     */     throws IOException
/*     */   {
/*  44 */     super(paramUnixPath);
/*     */   }
/*     */ 
/*     */   LinuxFileStore(UnixFileSystem paramUnixFileSystem, UnixMountEntry paramUnixMountEntry) throws IOException {
/*  48 */     super(paramUnixFileSystem, paramUnixMountEntry);
/*     */   }
/*     */ 
/*     */   UnixMountEntry findMountEntry()
/*     */     throws IOException
/*     */   {
/*  57 */     LinuxFileSystem localLinuxFileSystem = (LinuxFileSystem)file().getFileSystem();
/*     */ 
/*  60 */     Object localObject1 = null;
/*     */     try {
/*  62 */       byte[] arrayOfByte = UnixNativeDispatcher.realpath(file());
/*  63 */       localObject1 = new UnixPath(localLinuxFileSystem, arrayOfByte);
/*     */     } catch (UnixException localUnixException1) {
/*  65 */       localUnixException1.rethrowAsIOException(file());
/*     */     }
/*     */ 
/*  69 */     UnixPath localUnixPath = ((UnixPath)localObject1).getParent();
/*  70 */     while (localUnixPath != null) {
/*  71 */       localObject2 = null;
/*     */       try {
/*  73 */         localObject2 = UnixFileAttributes.get(localUnixPath, true);
/*     */       } catch (UnixException localUnixException2) {
/*  75 */         localUnixException2.rethrowAsIOException(localUnixPath);
/*     */       }
/*  77 */       if (((UnixFileAttributes)localObject2).dev() != dev())
/*     */         break;
/*  79 */       localObject1 = localUnixPath;
/*  80 */       localUnixPath = localUnixPath.getParent();
/*     */     }
/*     */ 
/*  85 */     Object localObject2 = ((UnixPath)localObject1).asByteArray();
/*  86 */     for (UnixMountEntry localUnixMountEntry : localLinuxFileSystem.getMountEntries("/proc/mounts")) {
/*  87 */       if (Arrays.equals((byte[])localObject2, localUnixMountEntry.dir())) {
/*  88 */         return localUnixMountEntry;
/*     */       }
/*     */     }
/*  91 */     throw new IOException("Mount point not found");
/*     */   }
/*     */ 
/*     */   private boolean isExtendedAttributesEnabled(UnixPath paramUnixPath)
/*     */   {
/*     */     try
/*     */     {
/*  98 */       int i = paramUnixPath.openForAttributeAccess(false);
/*     */       try
/*     */       {
/* 101 */         LinuxNativeDispatcher.fgetxattr(i, "user.java".getBytes(), 0L, 0);
/* 102 */         return true;
/*     */       }
/*     */       catch (UnixException localUnixException) {
/* 105 */         if (localUnixException.errno() == 61)
/* 106 */           return true;
/*     */       } finally {
/* 108 */         UnixNativeDispatcher.close(i);
/*     */       }
/*     */     }
/*     */     catch (IOException localIOException) {
/*     */     }
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean supportsFileAttributeView(Class<? extends FileAttributeView> paramClass)
/*     */   {
/* 120 */     if ((paramClass == DosFileAttributeView.class) || (paramClass == UserDefinedFileAttributeView.class))
/*     */     {
/* 124 */       UnixFileStore.FeatureStatus localFeatureStatus = checkIfFeaturePresent("user_xattr");
/* 125 */       if (localFeatureStatus == UnixFileStore.FeatureStatus.PRESENT)
/* 126 */         return true;
/* 127 */       if (localFeatureStatus == UnixFileStore.FeatureStatus.NOT_PRESENT) {
/* 128 */         return false;
/*     */       }
/*     */ 
/* 132 */       if (entry().hasOption("user_xattr")) {
/* 133 */         return true;
/*     */       }
/*     */ 
/* 137 */       if ((entry().fstype().equals("ext3")) || (entry().fstype().equals("ext4"))) {
/* 138 */         return false;
/*     */       }
/*     */ 
/* 141 */       if (!this.xattrChecked) {
/* 142 */         UnixPath localUnixPath = new UnixPath(file().getFileSystem(), entry().dir());
/* 143 */         this.xattrEnabled = isExtendedAttributesEnabled(localUnixPath);
/* 144 */         this.xattrChecked = true;
/*     */       }
/* 146 */       return this.xattrEnabled;
/*     */     }
/* 148 */     return super.supportsFileAttributeView(paramClass);
/*     */   }
/*     */ 
/*     */   public boolean supportsFileAttributeView(String paramString)
/*     */   {
/* 153 */     if (paramString.equals("dos"))
/* 154 */       return supportsFileAttributeView(DosFileAttributeView.class);
/* 155 */     if (paramString.equals("user"))
/* 156 */       return supportsFileAttributeView(UserDefinedFileAttributeView.class);
/* 157 */     return super.supportsFileAttributeView(paramString);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.LinuxFileStore
 * JD-Core Version:    0.6.2
 */