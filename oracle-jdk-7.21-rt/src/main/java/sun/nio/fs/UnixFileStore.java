/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.FileStore;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.attribute.BasicFileAttributeView;
/*     */ import java.nio.file.attribute.FileAttributeView;
/*     */ import java.nio.file.attribute.FileOwnerAttributeView;
/*     */ import java.nio.file.attribute.FileStoreAttributeView;
/*     */ import java.nio.file.attribute.PosixFileAttributeView;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Properties;
/*     */ 
/*     */ abstract class UnixFileStore extends FileStore
/*     */ {
/*     */   private final UnixPath file;
/*     */   private final long dev;
/*     */   private final UnixMountEntry entry;
/* 208 */   private static final Object loadLock = new Object();
/*     */   private static volatile Properties props;
/*     */ 
/*     */   private static long devFor(UnixPath paramUnixPath)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  55 */       return UnixFileAttributes.get(paramUnixPath, true).dev();
/*     */     } catch (UnixException localUnixException) {
/*  57 */       localUnixException.rethrowAsIOException(paramUnixPath);
/*  58 */     }return 0L;
/*     */   }
/*     */ 
/*     */   UnixFileStore(UnixPath paramUnixPath) throws IOException
/*     */   {
/*  63 */     this.file = paramUnixPath;
/*  64 */     this.dev = devFor(paramUnixPath);
/*  65 */     this.entry = findMountEntry();
/*     */   }
/*     */ 
/*     */   UnixFileStore(UnixFileSystem paramUnixFileSystem, UnixMountEntry paramUnixMountEntry) throws IOException {
/*  69 */     this.file = new UnixPath(paramUnixFileSystem, paramUnixMountEntry.dir());
/*  70 */     this.dev = (paramUnixMountEntry.dev() == 0L ? devFor(this.file) : paramUnixMountEntry.dev());
/*  71 */     this.entry = paramUnixMountEntry;
/*     */   }
/*     */ 
/*     */   abstract UnixMountEntry findMountEntry()
/*     */     throws IOException;
/*     */ 
/*     */   UnixPath file()
/*     */   {
/*  80 */     return this.file;
/*     */   }
/*     */ 
/*     */   long dev() {
/*  84 */     return this.dev;
/*     */   }
/*     */ 
/*     */   UnixMountEntry entry() {
/*  88 */     return this.entry;
/*     */   }
/*     */ 
/*     */   public String name()
/*     */   {
/*  93 */     return this.entry.name();
/*     */   }
/*     */ 
/*     */   public String type()
/*     */   {
/*  98 */     return this.entry.fstype();
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly()
/*     */   {
/* 103 */     return this.entry.isReadOnly();
/*     */   }
/*     */ 
/*     */   private UnixFileStoreAttributes readAttributes() throws IOException
/*     */   {
/*     */     try {
/* 109 */       return UnixFileStoreAttributes.get(this.file);
/*     */     } catch (UnixException localUnixException) {
/* 111 */       localUnixException.rethrowAsIOException(this.file);
/* 112 */     }return null;
/*     */   }
/*     */ 
/*     */   public long getTotalSpace()
/*     */     throws IOException
/*     */   {
/* 118 */     UnixFileStoreAttributes localUnixFileStoreAttributes = readAttributes();
/* 119 */     return localUnixFileStoreAttributes.blockSize() * localUnixFileStoreAttributes.totalBlocks();
/*     */   }
/*     */ 
/*     */   public long getUsableSpace() throws IOException
/*     */   {
/* 124 */     UnixFileStoreAttributes localUnixFileStoreAttributes = readAttributes();
/* 125 */     return localUnixFileStoreAttributes.blockSize() * localUnixFileStoreAttributes.availableBlocks();
/*     */   }
/*     */ 
/*     */   public long getUnallocatedSpace() throws IOException
/*     */   {
/* 130 */     UnixFileStoreAttributes localUnixFileStoreAttributes = readAttributes();
/* 131 */     return localUnixFileStoreAttributes.blockSize() * localUnixFileStoreAttributes.freeBlocks();
/*     */   }
/*     */ 
/*     */   public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> paramClass)
/*     */   {
/* 137 */     if (paramClass == null)
/* 138 */       throw new NullPointerException();
/* 139 */     return (FileStoreAttributeView)null;
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String paramString) throws IOException
/*     */   {
/* 144 */     if (paramString.equals("totalSpace"))
/* 145 */       return Long.valueOf(getTotalSpace());
/* 146 */     if (paramString.equals("usableSpace"))
/* 147 */       return Long.valueOf(getUsableSpace());
/* 148 */     if (paramString.equals("unallocatedSpace"))
/* 149 */       return Long.valueOf(getUnallocatedSpace());
/* 150 */     throw new UnsupportedOperationException("'" + paramString + "' not recognized");
/*     */   }
/*     */ 
/*     */   public boolean supportsFileAttributeView(Class<? extends FileAttributeView> paramClass)
/*     */   {
/* 155 */     if (paramClass == null)
/* 156 */       throw new NullPointerException();
/* 157 */     if (paramClass == BasicFileAttributeView.class)
/* 158 */       return true;
/* 159 */     if ((paramClass == PosixFileAttributeView.class) || (paramClass == FileOwnerAttributeView.class))
/*     */     {
/* 163 */       FeatureStatus localFeatureStatus = checkIfFeaturePresent("posix");
/*     */ 
/* 165 */       return localFeatureStatus != FeatureStatus.NOT_PRESENT;
/*     */     }
/* 167 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean supportsFileAttributeView(String paramString)
/*     */   {
/* 172 */     if ((paramString.equals("basic")) || (paramString.equals("unix")))
/* 173 */       return true;
/* 174 */     if (paramString.equals("posix"))
/* 175 */       return supportsFileAttributeView(PosixFileAttributeView.class);
/* 176 */     if (paramString.equals("owner"))
/* 177 */       return supportsFileAttributeView(FileOwnerAttributeView.class);
/* 178 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 183 */     if (paramObject == this)
/* 184 */       return true;
/* 185 */     if (!(paramObject instanceof UnixFileStore))
/* 186 */       return false;
/* 187 */     UnixFileStore localUnixFileStore = (UnixFileStore)paramObject;
/* 188 */     return (this.dev == localUnixFileStore.dev) && (Arrays.equals(this.entry.dir(), localUnixFileStore.entry.dir()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 194 */     return (int)(this.dev ^ this.dev >>> 32) ^ Arrays.hashCode(this.entry.dir());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 199 */     StringBuilder localStringBuilder = new StringBuilder(new String(this.entry.dir()));
/* 200 */     localStringBuilder.append(" (");
/* 201 */     localStringBuilder.append(this.entry.name());
/* 202 */     localStringBuilder.append(")");
/* 203 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   FeatureStatus checkIfFeaturePresent(String paramString)
/*     */   {
/* 221 */     if (props == null) {
/* 222 */       synchronized (loadLock) {
/* 223 */         if (props == null) {
/* 224 */           props = (Properties)AccessController.doPrivileged(new PrivilegedAction()
/*     */           {
/*     */             public Properties run()
/*     */             {
/* 228 */               return UnixFileStore.access$000();
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     }
/* 234 */     ??? = props.getProperty(type());
/* 235 */     if (??? != null) {
/* 236 */       String[] arrayOfString1 = ((String)???).split("\\s");
/* 237 */       for (String str : arrayOfString1) {
/* 238 */         str = str.trim().toLowerCase();
/* 239 */         if (str.equals(paramString)) {
/* 240 */           return FeatureStatus.PRESENT;
/*     */         }
/* 242 */         if (str.startsWith("no")) {
/* 243 */           str = str.substring(2);
/* 244 */           if (str.equals(paramString)) {
/* 245 */             return FeatureStatus.NOT_PRESENT;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 250 */     return FeatureStatus.UNKNOWN;
/*     */   }
/*     */ 
/*     */   private static Properties loadProperties() {
/* 254 */     Properties localProperties = new Properties();
/* 255 */     String str = System.getProperty("java.home") + "/lib/fstypes.properties";
/* 256 */     Path localPath = Paths.get(str, new String[0]);
/*     */     try {
/* 258 */       SeekableByteChannel localSeekableByteChannel = Files.newByteChannel(localPath, new OpenOption[0]); Object localObject1 = null;
/*     */       try { localProperties.load(Channels.newReader(localSeekableByteChannel, "UTF-8")); }
/*     */       catch (Throwable localThrowable2)
/*     */       {
/* 258 */         localObject1 = localThrowable2; throw localThrowable2;
/*     */       } finally {
/* 260 */         if (localSeekableByteChannel != null) if (localObject1 != null) try { localSeekableByteChannel.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localSeekableByteChannel.close();  
/*     */       }
/*     */     } catch (IOException localIOException) {  }
/*     */ 
/* 263 */     return localProperties;
/*     */   }
/*     */ 
/*     */   static enum FeatureStatus
/*     */   {
/* 212 */     PRESENT, 
/* 213 */     NOT_PRESENT, 
/* 214 */     UNKNOWN;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixFileStore
 * JD-Core Version:    0.6.2
 */