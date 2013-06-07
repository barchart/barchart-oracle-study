/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.file.FileSystemException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.nio.ch.DirectBuffer;
/*     */ 
/*     */ class LinuxUserDefinedFileAttributeView extends AbstractUserDefinedFileAttributeView
/*     */ {
/*  44 */   private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */   private static final String USER_NAMESPACE = "user.";
/*     */   private static final int XATTR_NAME_MAX = 255;
/*     */   private final UnixPath file;
/*     */   private final boolean followLinks;
/*     */ 
/*     */   private byte[] nameAsBytes(UnixPath paramUnixPath, String paramString)
/*     */     throws IOException
/*     */   {
/*  53 */     if (paramString == null)
/*  54 */       throw new NullPointerException("'name' is null");
/*  55 */     paramString = "user." + paramString;
/*  56 */     byte[] arrayOfByte = paramString.getBytes();
/*  57 */     if (arrayOfByte.length > 255) {
/*  58 */       throw new FileSystemException(paramUnixPath.getPathForExecptionMessage(), null, "'" + paramString + "' is too big");
/*     */     }
/*     */ 
/*  61 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   private List<String> asList(long paramLong, int paramInt)
/*     */   {
/*  66 */     ArrayList localArrayList = new ArrayList();
/*  67 */     int i = 0;
/*  68 */     int j = 0;
/*  69 */     while (j < paramInt) {
/*  70 */       if (unsafe.getByte(paramLong + j) == 0) {
/*  71 */         int k = j - i;
/*  72 */         byte[] arrayOfByte = new byte[k];
/*  73 */         unsafe.copyMemory(null, paramLong + i, arrayOfByte, Unsafe.ARRAY_BYTE_BASE_OFFSET, k);
/*     */ 
/*  75 */         String str = new String(arrayOfByte);
/*  76 */         if (str.startsWith("user.")) {
/*  77 */           str = str.substring("user.".length());
/*  78 */           localArrayList.add(str);
/*     */         }
/*  80 */         i = j + 1;
/*     */       }
/*  82 */       j++;
/*     */     }
/*  84 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   LinuxUserDefinedFileAttributeView(UnixPath paramUnixPath, boolean paramBoolean)
/*     */   {
/*  91 */     this.file = paramUnixPath;
/*  92 */     this.followLinks = paramBoolean;
/*     */   }
/*     */ 
/*     */   public List<String> list() throws IOException
/*     */   {
/*  97 */     if (System.getSecurityManager() != null) {
/*  98 */       checkAccess(this.file.getPathForPermissionCheck(), true, false);
/*     */     }
/* 100 */     int i = this.file.openForAttributeAccess(this.followLinks);
/* 101 */     NativeBuffer localNativeBuffer = null;
/*     */     try {
/* 103 */       int j = 1024;
/* 104 */       localNativeBuffer = NativeBuffers.getNativeBuffer(j);
/*     */       try
/*     */       {
/* 107 */         int k = LinuxNativeDispatcher.flistxattr(i, localNativeBuffer.address(), j);
/* 108 */         List localList1 = asList(localNativeBuffer.address(), k);
/* 109 */         return Collections.unmodifiableList(localList1);
/*     */       }
/*     */       catch (UnixException localUnixException) {
/* 112 */         while ((localUnixException.errno() == 34) && (j < 32768)) {
/* 113 */           localNativeBuffer.release();
/* 114 */           j *= 2;
/* 115 */           localNativeBuffer = null;
/* 116 */           localNativeBuffer = NativeBuffers.getNativeBuffer(j);
/*     */         }
/*     */ 
/* 119 */         throw new FileSystemException(this.file.getPathForExecptionMessage(), null, "Unable to get list of extended attributes: " + localUnixException.getMessage());
/*     */       }
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 125 */       if (localNativeBuffer != null)
/* 126 */         localNativeBuffer.release();
/* 127 */       LinuxNativeDispatcher.close(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size(String paramString) throws IOException
/*     */   {
/* 133 */     if (System.getSecurityManager() != null) {
/* 134 */       checkAccess(this.file.getPathForPermissionCheck(), true, false);
/*     */     }
/* 136 */     int i = this.file.openForAttributeAccess(this.followLinks);
/*     */     try
/*     */     {
/* 139 */       return LinuxNativeDispatcher.fgetxattr(i, nameAsBytes(this.file, paramString), 0L, 0);
/*     */     } catch (UnixException localUnixException) {
/* 141 */       throw new FileSystemException(this.file.getPathForExecptionMessage(), null, "Unable to get size of extended attribute '" + paramString + "': " + localUnixException.getMessage());
/*     */     }
/*     */     finally
/*     */     {
/* 145 */       LinuxNativeDispatcher.close(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read(String paramString, ByteBuffer paramByteBuffer) throws IOException
/*     */   {
/* 151 */     if (System.getSecurityManager() != null) {
/* 152 */       checkAccess(this.file.getPathForPermissionCheck(), true, false);
/*     */     }
/* 154 */     if (paramByteBuffer.isReadOnly())
/* 155 */       throw new IllegalArgumentException("Read-only buffer");
/* 156 */     int i = paramByteBuffer.position();
/* 157 */     int j = paramByteBuffer.limit();
/* 158 */     assert (i <= j);
/* 159 */     int k = i <= j ? j - i : 0;
/*     */     NativeBuffer localNativeBuffer;
/*     */     long l;
/* 163 */     if ((paramByteBuffer instanceof DirectBuffer)) {
/* 164 */       localNativeBuffer = null;
/* 165 */       l = ((DirectBuffer)paramByteBuffer).address() + i;
/*     */     }
/*     */     else {
/* 168 */       localNativeBuffer = NativeBuffers.getNativeBuffer(k);
/* 169 */       l = localNativeBuffer.address();
/*     */     }
/*     */ 
/* 172 */     int m = this.file.openForAttributeAccess(this.followLinks);
/*     */     try {
/*     */       try {
/* 175 */         int n = LinuxNativeDispatcher.fgetxattr(m, nameAsBytes(this.file, paramString), l, k);
/*     */ 
/* 178 */         if (k == 0) {
/* 179 */           if (n > 0)
/* 180 */             throw new UnixException(34);
/* 181 */           i1 = 0;
/*     */ 
/* 197 */           LinuxNativeDispatcher.close(m);
/*     */ 
/* 201 */           return i1;
/*     */         }
/* 185 */         if (localNativeBuffer != null) {
/* 186 */           i1 = paramByteBuffer.arrayOffset() + i + Unsafe.ARRAY_BYTE_BASE_OFFSET;
/* 187 */           unsafe.copyMemory(null, l, paramByteBuffer.array(), i1, n);
/*     */         }
/* 189 */         paramByteBuffer.position(i + n);
/* 190 */         int i1 = n;
/*     */ 
/* 197 */         LinuxNativeDispatcher.close(m);
/*     */ 
/* 201 */         return i1;
/*     */       }
/*     */       catch (UnixException localUnixException)
/*     */       {
/* 192 */         String str = localUnixException.errno() == 34 ? "Insufficient space in buffer" : localUnixException.getMessage();
/*     */ 
/* 194 */         throw new FileSystemException(this.file.getPathForExecptionMessage(), null, "Error reading extended attribute '" + paramString + "': " + str);
/*     */       }
/*     */       finally {
/* 197 */         LinuxNativeDispatcher.close(m);
/*     */       }
/*     */     } finally {
/* 200 */       if (localNativeBuffer != null)
/* 201 */         localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int write(String paramString, ByteBuffer paramByteBuffer) throws IOException
/*     */   {
/* 207 */     if (System.getSecurityManager() != null) {
/* 208 */       checkAccess(this.file.getPathForPermissionCheck(), false, true);
/*     */     }
/* 210 */     int i = paramByteBuffer.position();
/* 211 */     int j = paramByteBuffer.limit();
/* 212 */     assert (i <= j);
/* 213 */     int k = i <= j ? j - i : 0;
/*     */     NativeBuffer localNativeBuffer;
/*     */     long l;
/* 217 */     if ((paramByteBuffer instanceof DirectBuffer)) {
/* 218 */       localNativeBuffer = null;
/* 219 */       l = ((DirectBuffer)paramByteBuffer).address() + i;
/*     */     }
/*     */     else {
/* 222 */       localNativeBuffer = NativeBuffers.getNativeBuffer(k);
/* 223 */       l = localNativeBuffer.address();
/*     */ 
/* 225 */       if (paramByteBuffer.hasArray())
/*     */       {
/* 227 */         int m = paramByteBuffer.arrayOffset() + i + Unsafe.ARRAY_BYTE_BASE_OFFSET;
/* 228 */         unsafe.copyMemory(paramByteBuffer.array(), m, null, l, k);
/*     */       }
/*     */       else {
/* 231 */         byte[] arrayOfByte = new byte[k];
/* 232 */         paramByteBuffer.get(arrayOfByte);
/* 233 */         paramByteBuffer.position(i);
/* 234 */         unsafe.copyMemory(arrayOfByte, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, l, k);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 239 */     int n = this.file.openForAttributeAccess(this.followLinks);
/*     */     try {
/*     */       try {
/* 242 */         LinuxNativeDispatcher.fsetxattr(n, nameAsBytes(this.file, paramString), l, k);
/* 243 */         paramByteBuffer.position(i + k);
/* 244 */         int i1 = k;
/*     */ 
/* 250 */         LinuxNativeDispatcher.close(n);
/*     */ 
/* 254 */         return i1;
/*     */       }
/*     */       catch (UnixException localUnixException)
/*     */       {
/* 246 */         throw new FileSystemException(this.file.getPathForExecptionMessage(), null, "Error writing extended attribute '" + paramString + "': " + localUnixException.getMessage());
/*     */       }
/*     */       finally
/*     */       {
/* 250 */         LinuxNativeDispatcher.close(n);
/*     */       }
/*     */     } finally {
/* 253 */       if (localNativeBuffer != null)
/* 254 */         localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void delete(String paramString) throws IOException
/*     */   {
/* 260 */     if (System.getSecurityManager() != null) {
/* 261 */       checkAccess(this.file.getPathForPermissionCheck(), false, true);
/*     */     }
/* 263 */     int i = this.file.openForAttributeAccess(this.followLinks);
/*     */     try {
/* 265 */       LinuxNativeDispatcher.fremovexattr(i, nameAsBytes(this.file, paramString));
/*     */     } catch (UnixException localUnixException) {
/* 267 */       throw new FileSystemException(this.file.getPathForExecptionMessage(), null, "Unable to delete extended attribute '" + paramString + "': " + localUnixException.getMessage());
/*     */     }
/*     */     finally {
/* 270 */       LinuxNativeDispatcher.close(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void copyExtendedAttributes(int paramInt1, int paramInt2)
/*     */   {
/* 283 */     NativeBuffer localNativeBuffer = null;
/*     */     try
/*     */     {
/* 287 */       int i = 1024;
/* 288 */       localNativeBuffer = NativeBuffers.getNativeBuffer(i);
/*     */       try
/*     */       {
/* 291 */         i = LinuxNativeDispatcher.flistxattr(paramInt1, localNativeBuffer.address(), i);
/*     */       }
/*     */       catch (UnixException localUnixException1)
/*     */       {
/* 295 */         while ((localUnixException1.errno() == 34) && (i < 32768)) {
/* 296 */           localNativeBuffer.release();
/* 297 */           i *= 2;
/* 298 */           localNativeBuffer = null;
/* 299 */           localNativeBuffer = NativeBuffers.getNativeBuffer(i);
/*     */         }
/*     */ 
/*     */         return;
/*     */       }
/*     */ 
/* 309 */       long l = localNativeBuffer.address();
/* 310 */       int j = 0;
/* 311 */       int k = 0;
/* 312 */       while (k < i) {
/* 313 */         if (unsafe.getByte(l + k) == 0)
/*     */         {
/* 317 */           int m = k - j;
/* 318 */           byte[] arrayOfByte = new byte[m];
/* 319 */           unsafe.copyMemory(null, l + j, arrayOfByte, Unsafe.ARRAY_BYTE_BASE_OFFSET, m);
/*     */           try
/*     */           {
/* 322 */             copyExtendedAttribute(paramInt1, arrayOfByte, paramInt2);
/*     */           }
/*     */           catch (UnixException localUnixException2) {
/*     */           }
/* 326 */           j = k + 1;
/*     */         }
/* 328 */         k++;
/*     */       }
/*     */     }
/*     */     finally {
/* 332 */       if (localNativeBuffer != null)
/* 333 */         localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void copyExtendedAttribute(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
/*     */     throws UnixException
/*     */   {
/* 340 */     int i = LinuxNativeDispatcher.fgetxattr(paramInt1, paramArrayOfByte, 0L, 0);
/* 341 */     NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(i);
/*     */     try {
/* 343 */       long l = localNativeBuffer.address();
/* 344 */       i = LinuxNativeDispatcher.fgetxattr(paramInt1, paramArrayOfByte, l, i);
/* 345 */       LinuxNativeDispatcher.fsetxattr(paramInt2, paramArrayOfByte, l, i);
/*     */     } finally {
/* 347 */       localNativeBuffer.release();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.LinuxUserDefinedFileAttributeView
 * JD-Core Version:    0.6.2
 */