/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.FileStore;
/*     */ import java.nio.file.WatchService;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ class LinuxFileSystem extends UnixFileSystem
/*     */ {
/*     */   LinuxFileSystem(UnixFileSystemProvider paramUnixFileSystemProvider, String paramString)
/*     */   {
/*  39 */     super(paramUnixFileSystemProvider, paramString);
/*     */   }
/*     */ 
/*     */   public WatchService newWatchService()
/*     */     throws IOException
/*     */   {
/*  47 */     return new LinuxWatchService(this);
/*     */   }
/*     */ 
/*     */   public Set<String> supportedFileAttributeViews()
/*     */   {
/*  67 */     return SupportedFileFileAttributeViewsHolder.supportedFileAttributeViews;
/*     */   }
/*     */ 
/*     */   void copyNonPosixAttributes(int paramInt1, int paramInt2)
/*     */   {
/*  72 */     LinuxUserDefinedFileAttributeView.copyExtendedAttributes(paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   Iterable<UnixMountEntry> getMountEntries(String paramString)
/*     */   {
/*  79 */     ArrayList localArrayList = new ArrayList();
/*     */     try {
/*  81 */       long l = LinuxNativeDispatcher.setmntent(paramString.getBytes(), "r".getBytes());
/*     */       try {
/*     */         while (true) {
/*  84 */           UnixMountEntry localUnixMountEntry = new UnixMountEntry();
/*  85 */           int i = LinuxNativeDispatcher.getextmntent(l, localUnixMountEntry);
/*  86 */           if (i < 0)
/*     */             break;
/*  88 */           localArrayList.add(localUnixMountEntry);
/*     */         }
/*     */       } finally {
/*  91 */         LinuxNativeDispatcher.endmntent(l);
/*     */       }
/*     */     }
/*     */     catch (UnixException localUnixException)
/*     */     {
/*     */     }
/*  97 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   Iterable<UnixMountEntry> getMountEntries()
/*     */   {
/* 105 */     return getMountEntries("/etc/mtab");
/*     */   }
/*     */ 
/*     */   FileStore getFileStore(UnixMountEntry paramUnixMountEntry)
/*     */     throws IOException
/*     */   {
/* 112 */     return new LinuxFileStore(this, paramUnixMountEntry);
/*     */   }
/*     */ 
/*     */   private static class SupportedFileFileAttributeViewsHolder
/*     */   {
/*  53 */     static final Set<String> supportedFileAttributeViews = supportedFileAttributeViews();
/*     */ 
/*     */     private static Set<String> supportedFileAttributeViews() {
/*  56 */       HashSet localHashSet = new HashSet();
/*  57 */       localHashSet.addAll(UnixFileSystem.standardFileAttributeViews());
/*     */ 
/*  59 */       localHashSet.add("dos");
/*  60 */       localHashSet.add("user");
/*  61 */       return Collections.unmodifiableSet(localHashSet);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.LinuxFileSystem
 * JD-Core Version:    0.6.2
 */