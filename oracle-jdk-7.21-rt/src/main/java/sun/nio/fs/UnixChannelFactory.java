/*     */ package sun.nio.fs;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.nio.channels.AsynchronousFileChannel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.Set;
/*     */ import sun.misc.JavaIOFileDescriptorAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.nio.ch.FileChannelImpl;
/*     */ import sun.nio.ch.SimpleAsynchronousFileChannelImpl;
/*     */ import sun.nio.ch.ThreadPool;
/*     */ 
/*     */ class UnixChannelFactory
/*     */ {
/*  49 */   private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
/*     */ 
/*     */   static FileChannel newFileChannel(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 106 */     FileDescriptor localFileDescriptor = new FileDescriptor();
/* 107 */     fdAccess.set(localFileDescriptor, paramInt);
/* 108 */     return FileChannelImpl.open(localFileDescriptor, paramBoolean1, paramBoolean2, null);
/*     */   }
/*     */ 
/*     */   static FileChannel newFileChannel(int paramInt1, UnixPath paramUnixPath, String paramString, Set<? extends OpenOption> paramSet, int paramInt2)
/*     */     throws UnixException
/*     */   {
/* 121 */     Flags localFlags = Flags.toFlags(paramSet);
/*     */ 
/* 124 */     if ((!localFlags.read) && (!localFlags.write)) {
/* 125 */       if (localFlags.append)
/* 126 */         localFlags.write = true;
/*     */       else {
/* 128 */         localFlags.read = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 133 */     if ((localFlags.read) && (localFlags.append))
/* 134 */       throw new IllegalArgumentException("READ + APPEND not allowed");
/* 135 */     if ((localFlags.append) && (localFlags.truncateExisting)) {
/* 136 */       throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
/*     */     }
/* 138 */     FileDescriptor localFileDescriptor = open(paramInt1, paramUnixPath, paramString, localFlags, paramInt2);
/* 139 */     return FileChannelImpl.open(localFileDescriptor, localFlags.read, localFlags.write, localFlags.append, null);
/*     */   }
/*     */ 
/*     */   static FileChannel newFileChannel(UnixPath paramUnixPath, Set<? extends OpenOption> paramSet, int paramInt)
/*     */     throws UnixException
/*     */   {
/* 150 */     return newFileChannel(-1, paramUnixPath, null, paramSet, paramInt);
/*     */   }
/*     */ 
/*     */   static AsynchronousFileChannel newAsynchronousFileChannel(UnixPath paramUnixPath, Set<? extends OpenOption> paramSet, int paramInt, ThreadPool paramThreadPool)
/*     */     throws UnixException
/*     */   {
/* 162 */     Flags localFlags = Flags.toFlags(paramSet);
/*     */ 
/* 165 */     if ((!localFlags.read) && (!localFlags.write)) {
/* 166 */       localFlags.read = true;
/*     */     }
/*     */ 
/* 170 */     if (localFlags.append) {
/* 171 */       throw new UnsupportedOperationException("APPEND not allowed");
/*     */     }
/*     */ 
/* 174 */     FileDescriptor localFileDescriptor = open(-1, paramUnixPath, null, localFlags, paramInt);
/* 175 */     return SimpleAsynchronousFileChannelImpl.open(localFileDescriptor, localFlags.read, localFlags.write, paramThreadPool);
/*     */   }
/*     */ 
/*     */   protected static FileDescriptor open(int paramInt1, UnixPath paramUnixPath, String paramString, Flags paramFlags, int paramInt2)
/*     */     throws UnixException
/*     */   {
/*     */     int i;
/* 191 */     if ((paramFlags.read) && (paramFlags.write))
/* 192 */       i = 2;
/*     */     else {
/* 194 */       i = paramFlags.write ? 1 : 0;
/*     */     }
/* 196 */     if (paramFlags.write) {
/* 197 */       if (paramFlags.truncateExisting)
/* 198 */         i |= 512;
/* 199 */       if (paramFlags.append) {
/* 200 */         i |= 1024;
/*     */       }
/*     */ 
/* 203 */       if (paramFlags.createNew) {
/* 204 */         byte[] arrayOfByte = paramUnixPath.asByteArray();
/*     */ 
/* 207 */         if ((arrayOfByte[(arrayOfByte.length - 1)] == 46) && ((arrayOfByte.length == 1) || (arrayOfByte[(arrayOfByte.length - 2)] == 47)))
/*     */         {
/* 211 */           throw new UnixException(17);
/*     */         }
/* 213 */         i |= 192;
/*     */       }
/* 215 */       else if (paramFlags.create) {
/* 216 */         i |= 64;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 221 */     int j = 1;
/* 222 */     if ((!paramFlags.createNew) && ((paramFlags.noFollowLinks) || (paramFlags.deleteOnClose))) {
/* 223 */       j = 0;
/* 224 */       i |= 131072;
/*     */     }
/*     */ 
/* 227 */     if (paramFlags.dsync)
/* 228 */       i |= 4096;
/* 229 */     if (paramFlags.sync) {
/* 230 */       i |= 4096;
/*     */     }
/*     */ 
/* 233 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 234 */     if (localSecurityManager != null) {
/* 235 */       if (paramString == null)
/* 236 */         paramString = paramUnixPath.getPathForPermissionCheck();
/* 237 */       if (paramFlags.read)
/* 238 */         localSecurityManager.checkRead(paramString);
/* 239 */       if (paramFlags.write)
/* 240 */         localSecurityManager.checkWrite(paramString);
/* 241 */       if (paramFlags.deleteOnClose)
/* 242 */         localSecurityManager.checkDelete(paramString);
/*     */     }
/*     */     int k;
/*     */     try
/*     */     {
/* 247 */       if (paramInt1 >= 0)
/* 248 */         k = UnixNativeDispatcher.openat(paramInt1, paramUnixPath.asByteArray(), i, paramInt2);
/*     */       else
/* 250 */         k = UnixNativeDispatcher.open(paramUnixPath, i, paramInt2);
/*     */     }
/*     */     catch (UnixException localUnixException1)
/*     */     {
/* 254 */       if ((paramFlags.createNew) && (localUnixException1.errno() == 21))
/* 255 */         localUnixException1.setError(17);
/*     */       UnixException localUnixException2;
/* 259 */       if ((j == 0) && (localUnixException1.errno() == 40)) {
/* 260 */         localUnixException2 = new UnixException(localUnixException1.getMessage() + " (NOFOLLOW_LINKS specified)");
/*     */       }
/*     */ 
/* 263 */       throw localUnixException2;
/*     */     }
/*     */ 
/* 269 */     if (paramFlags.deleteOnClose) {
/*     */       try {
/* 271 */         if (paramInt1 >= 0)
/* 272 */           UnixNativeDispatcher.unlinkat(paramInt1, paramUnixPath.asByteArray(), 0);
/*     */         else {
/* 274 */           UnixNativeDispatcher.unlink(paramUnixPath);
/*     */         }
/*     */       }
/*     */       catch (UnixException localUnixException3)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/* 282 */     FileDescriptor localFileDescriptor = new FileDescriptor();
/* 283 */     fdAccess.set(localFileDescriptor, k);
/* 284 */     return localFileDescriptor;
/*     */   }
/*     */ 
/*     */   protected static class Flags
/*     */   {
/*     */     boolean read;
/*     */     boolean write;
/*     */     boolean append;
/*     */     boolean truncateExisting;
/*     */     boolean noFollowLinks;
/*     */     boolean create;
/*     */     boolean createNew;
/*     */     boolean deleteOnClose;
/*     */     boolean sync;
/*     */     boolean dsync;
/*     */ 
/*     */     static Flags toFlags(Set<? extends OpenOption> paramSet)
/*     */     {
/*  71 */       Flags localFlags = new Flags();
/*  72 */       for (OpenOption localOpenOption : paramSet)
/*  73 */         if ((localOpenOption instanceof StandardOpenOption)) {
/*  74 */           switch (UnixChannelFactory.1.$SwitchMap$java$nio$file$StandardOpenOption[((StandardOpenOption)localOpenOption).ordinal()]) { case 1:
/*  75 */             localFlags.read = true; break;
/*     */           case 2:
/*  76 */             localFlags.write = true; break;
/*     */           case 3:
/*  77 */             localFlags.append = true; break;
/*     */           case 4:
/*  78 */             localFlags.truncateExisting = true; break;
/*     */           case 5:
/*  79 */             localFlags.create = true; break;
/*     */           case 6:
/*  80 */             localFlags.createNew = true; break;
/*     */           case 7:
/*  81 */             localFlags.deleteOnClose = true; break;
/*     */           case 8:
/*  82 */             break;
/*     */           case 9:
/*  83 */             localFlags.sync = true; break;
/*     */           case 10:
/*  84 */             localFlags.dsync = true; break;
/*     */           default:
/*  85 */             throw new UnsupportedOperationException();
/*     */           }
/*     */ 
/*     */         }
/*  89 */         else if (localOpenOption == LinkOption.NOFOLLOW_LINKS) {
/*  90 */           localFlags.noFollowLinks = true;
/*     */         }
/*     */         else {
/*  93 */           if (localOpenOption == null)
/*  94 */             throw new NullPointerException();
/*  95 */           throw new UnsupportedOperationException();
/*     */         }
/*  97 */       return localFlags;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.UnixChannelFactory
 * JD-Core Version:    0.6.2
 */