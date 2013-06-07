/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.attribute.DosFileAttributeView;
/*     */ import java.nio.file.attribute.DosFileAttributes;
/*     */ import java.nio.file.attribute.FileTime;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ class LinuxDosFileAttributeView extends UnixFileAttributeViews.Basic
/*     */   implements DosFileAttributeView
/*     */ {
/*  46 */   private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */   private static final String READONLY_NAME = "readonly";
/*     */   private static final String ARCHIVE_NAME = "archive";
/*     */   private static final String SYSTEM_NAME = "system";
/*     */   private static final String HIDDEN_NAME = "hidden";
/*     */   private static final String DOS_XATTR_NAME = "user.DOSATTRIB";
/*  54 */   private static final byte[] DOS_XATTR_NAME_AS_BYTES = "user.DOSATTRIB".getBytes();
/*     */   private static final int DOS_XATTR_READONLY = 1;
/*     */   private static final int DOS_XATTR_HIDDEN = 2;
/*     */   private static final int DOS_XATTR_SYSTEM = 4;
/*     */   private static final int DOS_XATTR_ARCHIVE = 32;
/*  62 */   private static final Set<String> dosAttributeNames = Util.newSet(basicAttributeNames, new String[] { "readonly", "archive", "system", "hidden" });
/*     */ 
/*     */   LinuxDosFileAttributeView(UnixPath paramUnixPath, boolean paramBoolean)
/*     */   {
/*  66 */     super(paramUnixPath, paramBoolean);
/*     */   }
/*     */ 
/*     */   public String name()
/*     */   {
/*  71 */     return "dos";
/*     */   }
/*     */ 
/*     */   public void setAttribute(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/*  78 */     if (paramString.equals("readonly")) {
/*  79 */       setReadOnly(((Boolean)paramObject).booleanValue());
/*  80 */       return;
/*     */     }
/*  82 */     if (paramString.equals("archive")) {
/*  83 */       setArchive(((Boolean)paramObject).booleanValue());
/*  84 */       return;
/*     */     }
/*  86 */     if (paramString.equals("system")) {
/*  87 */       setSystem(((Boolean)paramObject).booleanValue());
/*  88 */       return;
/*     */     }
/*  90 */     if (paramString.equals("hidden")) {
/*  91 */       setHidden(((Boolean)paramObject).booleanValue());
/*  92 */       return;
/*     */     }
/*  94 */     super.setAttribute(paramString, paramObject);
/*     */   }
/*     */ 
/*     */   public Map<String, Object> readAttributes(String[] paramArrayOfString)
/*     */     throws IOException
/*     */   {
/* 101 */     AbstractBasicFileAttributeView.AttributesBuilder localAttributesBuilder = AbstractBasicFileAttributeView.AttributesBuilder.create(dosAttributeNames, paramArrayOfString);
/*     */ 
/* 103 */     DosFileAttributes localDosFileAttributes = readAttributes();
/* 104 */     addRequestedBasicAttributes(localDosFileAttributes, localAttributesBuilder);
/* 105 */     if (localAttributesBuilder.match("readonly"))
/* 106 */       localAttributesBuilder.add("readonly", Boolean.valueOf(localDosFileAttributes.isReadOnly()));
/* 107 */     if (localAttributesBuilder.match("archive"))
/* 108 */       localAttributesBuilder.add("archive", Boolean.valueOf(localDosFileAttributes.isArchive()));
/* 109 */     if (localAttributesBuilder.match("system"))
/* 110 */       localAttributesBuilder.add("system", Boolean.valueOf(localDosFileAttributes.isSystem()));
/* 111 */     if (localAttributesBuilder.match("hidden"))
/* 112 */       localAttributesBuilder.add("hidden", Boolean.valueOf(localDosFileAttributes.isHidden()));
/* 113 */     return localAttributesBuilder.unmodifiableMap();
/*     */   }
/*     */ 
/*     */   public DosFileAttributes readAttributes() throws IOException
/*     */   {
/* 118 */     this.file.checkRead();
/*     */ 
/* 120 */     int i = this.file.openForAttributeAccess(this.followLinks);
/*     */     try {
/* 122 */       final UnixFileAttributes localUnixFileAttributes = UnixFileAttributes.get(i);
/* 123 */       final int j = getDosAttribute(i);
/*     */ 
/* 125 */       return new DosFileAttributes()
/*     */       {
/*     */         public FileTime lastModifiedTime() {
/* 128 */           return localUnixFileAttributes.lastModifiedTime();
/*     */         }
/*     */ 
/*     */         public FileTime lastAccessTime() {
/* 132 */           return localUnixFileAttributes.lastAccessTime();
/*     */         }
/*     */ 
/*     */         public FileTime creationTime() {
/* 136 */           return localUnixFileAttributes.creationTime();
/*     */         }
/*     */ 
/*     */         public boolean isRegularFile() {
/* 140 */           return localUnixFileAttributes.isRegularFile();
/*     */         }
/*     */ 
/*     */         public boolean isDirectory() {
/* 144 */           return localUnixFileAttributes.isDirectory();
/*     */         }
/*     */ 
/*     */         public boolean isSymbolicLink() {
/* 148 */           return localUnixFileAttributes.isSymbolicLink();
/*     */         }
/*     */ 
/*     */         public boolean isOther() {
/* 152 */           return localUnixFileAttributes.isOther();
/*     */         }
/*     */ 
/*     */         public long size() {
/* 156 */           return localUnixFileAttributes.size();
/*     */         }
/*     */ 
/*     */         public Object fileKey() {
/* 160 */           return localUnixFileAttributes.fileKey();
/*     */         }
/*     */ 
/*     */         public boolean isReadOnly() {
/* 164 */           return (j & 0x1) != 0;
/*     */         }
/*     */ 
/*     */         public boolean isHidden() {
/* 168 */           return (j & 0x2) != 0;
/*     */         }
/*     */ 
/*     */         public boolean isArchive() {
/* 172 */           return (j & 0x20) != 0;
/*     */         }
/*     */ 
/*     */         public boolean isSystem() {
/* 176 */           return (j & 0x4) != 0;
/*     */         }
/*     */       };
/*     */     }
/*     */     catch (UnixException localUnixException) {
/* 181 */       localUnixException.rethrowAsIOException(this.file);
/* 182 */       return null;
/*     */     } finally {
/* 184 */       UnixNativeDispatcher.close(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setReadOnly(boolean paramBoolean) throws IOException
/*     */   {
/* 190 */     updateDosAttribute(1, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void setHidden(boolean paramBoolean) throws IOException
/*     */   {
/* 195 */     updateDosAttribute(2, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void setArchive(boolean paramBoolean) throws IOException
/*     */   {
/* 200 */     updateDosAttribute(32, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void setSystem(boolean paramBoolean) throws IOException
/*     */   {
/* 205 */     updateDosAttribute(4, paramBoolean);
/*     */   }
/*     */ 
/*     */   private int getDosAttribute(int paramInt)
/*     */     throws UnixException
/*     */   {
/* 214 */     NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(24);
/*     */     try {
/* 216 */       int i = LinuxNativeDispatcher.fgetxattr(paramInt, DOS_XATTR_NAME_AS_BYTES, localNativeBuffer.address(), 24);
/*     */ 
/* 219 */       if (i > 0)
/*     */       {
/* 221 */         if (unsafe.getByte(localNativeBuffer.address() + i - 1L) == 0) {
/* 222 */           i--;
/*     */         }
/*     */ 
/* 225 */         byte[] arrayOfByte = new byte[i];
/* 226 */         unsafe.copyMemory(null, localNativeBuffer.address(), arrayOfByte, Unsafe.ARRAY_BYTE_BASE_OFFSET, i);
/*     */ 
/* 228 */         String str = new String(arrayOfByte);
/*     */ 
/* 231 */         if ((str.length() >= 3) && (str.startsWith("0x")))
/*     */           try {
/* 233 */             return Integer.parseInt(str.substring(2), 16);
/*     */           }
/*     */           catch (NumberFormatException localNumberFormatException)
/*     */           {
/*     */           }
/*     */       }
/* 239 */       throw new UnixException("Value of user.DOSATTRIB attribute is invalid");
/*     */     }
/*     */     catch (UnixException localUnixException) {
/* 242 */       if (localUnixException.errno() == 61)
/* 243 */         return 0;
/* 244 */       throw localUnixException;
/*     */     } finally {
/* 246 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateDosAttribute(int paramInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 254 */     this.file.checkWrite();
/*     */ 
/* 256 */     int i = this.file.openForAttributeAccess(this.followLinks);
/*     */     try {
/* 258 */       int j = getDosAttribute(i);
/* 259 */       int k = j;
/* 260 */       if (paramBoolean)
/* 261 */         k |= paramInt;
/*     */       else {
/* 263 */         k &= (paramInt ^ 0xFFFFFFFF);
/*     */       }
/* 265 */       if (k != j) {
/* 266 */         byte[] arrayOfByte = ("0x" + Integer.toHexString(k)).getBytes();
/* 267 */         NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(arrayOfByte);
/*     */         try {
/* 269 */           LinuxNativeDispatcher.fsetxattr(i, DOS_XATTR_NAME_AS_BYTES, localNativeBuffer.address(), arrayOfByte.length + 1);
/*     */         }
/*     */         finally {
/* 272 */           localNativeBuffer.release();
/*     */         }
/*     */       }
/*     */     } catch (UnixException localUnixException) {
/* 276 */       localUnixException.rethrowAsIOException(this.file);
/*     */     } finally {
/* 278 */       UnixNativeDispatcher.close(i);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.LinuxDosFileAttributeView
 * JD-Core Version:    0.6.2
 */