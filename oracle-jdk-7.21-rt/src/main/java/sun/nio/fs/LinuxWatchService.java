/*     */ package sun.nio.fs;
/*     */ 
/*     */ import com.sun.nio.file.SensitivityWatchEventModifier;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.NotDirectoryException;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardWatchEventKinds;
/*     */ import java.nio.file.WatchEvent.Kind;
/*     */ import java.nio.file.WatchEvent.Modifier;
/*     */ import java.nio.file.WatchKey;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ class LinuxWatchService extends AbstractWatchService
/*     */ {
/*  50 */   private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */   private final Poller poller;
/*     */ 
/*     */   LinuxWatchService(UnixFileSystem paramUnixFileSystem)
/*     */     throws IOException
/*     */   {
/*  57 */     int i = -1;
/*     */     try {
/*  59 */       i = inotifyInit();
/*     */     } catch (UnixException localUnixException1) {
/*  61 */       throw new IOException(localUnixException1.errorString());
/*     */     }
/*     */ 
/*  66 */     int[] arrayOfInt = new int[2];
/*     */     try {
/*  68 */       configureBlocking(i, false);
/*  69 */       socketpair(arrayOfInt);
/*  70 */       configureBlocking(arrayOfInt[0], false);
/*     */     } catch (UnixException localUnixException2) {
/*  72 */       UnixNativeDispatcher.close(i);
/*  73 */       throw new IOException(localUnixException2.errorString());
/*     */     }
/*     */ 
/*  76 */     this.poller = new Poller(paramUnixFileSystem, this, i, arrayOfInt);
/*  77 */     this.poller.start();
/*     */   }
/*     */ 
/*     */   WatchKey register(Path paramPath, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier[] paramArrayOfModifier)
/*     */     throws IOException
/*     */   {
/*  87 */     return this.poller.register(paramPath, paramArrayOfKind, paramArrayOfModifier);
/*     */   }
/*     */ 
/*     */   void implClose()
/*     */     throws IOException
/*     */   {
/*  93 */     this.poller.close();
/*     */   }
/*     */ 
/*     */   private static native int eventSize();
/*     */ 
/*     */   private static native int[] eventOffsets();
/*     */ 
/*     */   private static native int inotifyInit()
/*     */     throws UnixException;
/*     */ 
/*     */   private static native int inotifyAddWatch(int paramInt1, long paramLong, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   private static native void inotifyRmWatch(int paramInt1, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   private static native void configureBlocking(int paramInt, boolean paramBoolean)
/*     */     throws UnixException;
/*     */ 
/*     */   private static native void socketpair(int[] paramArrayOfInt)
/*     */     throws UnixException;
/*     */ 
/*     */   private static native int poll(int paramInt1, int paramInt2)
/*     */     throws UnixException;
/*     */ 
/*     */   static
/*     */   {
/* 457 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/* 459 */         System.loadLibrary("nio");
/* 460 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static class LinuxWatchKey extends AbstractWatchKey
/*     */   {
/*     */     private final int ifd;
/*     */     private volatile int wd;
/*     */ 
/*     */     LinuxWatchKey(UnixPath paramUnixPath, LinuxWatchService paramLinuxWatchService, int paramInt1, int paramInt2)
/*     */     {
/* 106 */       super(paramLinuxWatchService);
/* 107 */       this.ifd = paramInt1;
/* 108 */       this.wd = paramInt2;
/*     */     }
/*     */ 
/*     */     int descriptor() {
/* 112 */       return this.wd;
/*     */     }
/*     */ 
/*     */     void invalidate(boolean paramBoolean) {
/* 116 */       if (paramBoolean)
/*     */         try {
/* 118 */           LinuxWatchService.inotifyRmWatch(this.ifd, this.wd);
/*     */         }
/*     */         catch (UnixException localUnixException)
/*     */         {
/*     */         }
/* 123 */       this.wd = -1;
/*     */     }
/*     */ 
/*     */     public boolean isValid()
/*     */     {
/* 128 */       return this.wd != -1;
/*     */     }
/*     */ 
/*     */     public void cancel()
/*     */     {
/* 133 */       if (isValid())
/*     */       {
/* 135 */         ((LinuxWatchService)watcher()).poller.cancel(this);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Poller extends AbstractPoller {
/* 152 */     private static final int SIZEOF_INOTIFY_EVENT = LinuxWatchService.access$200();
/* 153 */     private static final int[] offsets = LinuxWatchService.access$300();
/* 154 */     private static final int OFFSETOF_WD = offsets[0];
/* 155 */     private static final int OFFSETOF_MASK = offsets[1];
/* 156 */     private static final int OFFSETOF_LEN = offsets[3];
/* 157 */     private static final int OFFSETOF_NAME = offsets[4];
/*     */     private static final int IN_MODIFY = 2;
/*     */     private static final int IN_ATTRIB = 4;
/*     */     private static final int IN_MOVED_FROM = 64;
/*     */     private static final int IN_MOVED_TO = 128;
/*     */     private static final int IN_CREATE = 256;
/*     */     private static final int IN_DELETE = 512;
/*     */     private static final int IN_UNMOUNT = 8192;
/*     */     private static final int IN_Q_OVERFLOW = 16384;
/*     */     private static final int IN_IGNORED = 32768;
/*     */     private static final int BUFFER_SIZE = 8192;
/*     */     private final UnixFileSystem fs;
/*     */     private final LinuxWatchService watcher;
/*     */     private final int ifd;
/*     */     private final int[] socketpair;
/*     */     private final Map<Integer, LinuxWatchService.LinuxWatchKey> wdToKey;
/*     */     private final long address;
/*     */ 
/*     */     Poller(UnixFileSystem paramUnixFileSystem, LinuxWatchService paramLinuxWatchService, int paramInt, int[] paramArrayOfInt) {
/* 186 */       this.fs = paramUnixFileSystem;
/* 187 */       this.watcher = paramLinuxWatchService;
/* 188 */       this.ifd = paramInt;
/* 189 */       this.socketpair = paramArrayOfInt;
/* 190 */       this.wdToKey = new HashMap();
/* 191 */       this.address = LinuxWatchService.unsafe.allocateMemory(8192L);
/*     */     }
/*     */ 
/*     */     void wakeup() throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 198 */         UnixNativeDispatcher.write(this.socketpair[1], this.address, 1);
/*     */       } catch (UnixException localUnixException) {
/* 200 */         throw new IOException(localUnixException.errorString());
/*     */       }
/*     */     }
/*     */ 
/*     */     Object implRegister(Path paramPath, Set<? extends WatchEvent.Kind<?>> paramSet, WatchEvent.Modifier[] paramArrayOfModifier)
/*     */     {
/* 209 */       UnixPath localUnixPath = (UnixPath)paramPath;
/*     */ 
/* 211 */       int i = 0;
/* 212 */       for (Object localObject1 = paramSet.iterator(); ((Iterator)localObject1).hasNext(); ) { WatchEvent.Kind localKind = (WatchEvent.Kind)((Iterator)localObject1).next();
/* 213 */         if (localKind == StandardWatchEventKinds.ENTRY_CREATE) {
/* 214 */           i |= 384;
/*     */         }
/* 217 */         else if (localKind == StandardWatchEventKinds.ENTRY_DELETE) {
/* 218 */           i |= 576;
/*     */         }
/* 221 */         else if (localKind == StandardWatchEventKinds.ENTRY_MODIFY) {
/* 222 */           i |= 6;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 228 */       if (paramArrayOfModifier.length > 0) {
/* 229 */         for (Object localObject2 : paramArrayOfModifier) {
/* 230 */           if (localObject2 == null)
/* 231 */             return new NullPointerException();
/* 232 */           if (!(localObject2 instanceof SensitivityWatchEventModifier))
/*     */           {
/* 234 */             return new UnsupportedOperationException("Modifier not supported");
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 239 */       localObject1 = null;
/*     */       try {
/* 241 */         localObject1 = UnixFileAttributes.get(localUnixPath, true);
/*     */       } catch (UnixException localUnixException1) {
/* 243 */         return localUnixException1.asIOException(localUnixPath);
/*     */       }
/* 245 */       if (!((UnixFileAttributes)localObject1).isDirectory()) {
/* 246 */         return new NotDirectoryException(localUnixPath.getPathForExecptionMessage());
/*     */       }
/*     */ 
/* 250 */       int k = -1;
/*     */       try {
/* 252 */         NativeBuffer localNativeBuffer = NativeBuffers.asNativeBuffer(localUnixPath.getByteArrayForSysCalls());
/*     */         try
/*     */         {
/* 255 */           k = LinuxWatchService.inotifyAddWatch(this.ifd, localNativeBuffer.address(), i);
/*     */         } finally {
/* 257 */           localNativeBuffer.release();
/*     */         }
/*     */       } catch (UnixException localUnixException2) {
/* 260 */         if (localUnixException2.errno() == 28) {
/* 261 */           return new IOException("User limit of inotify watches reached");
/*     */         }
/* 263 */         return localUnixException2.asIOException(localUnixPath);
/*     */       }
/*     */ 
/* 267 */       LinuxWatchService.LinuxWatchKey localLinuxWatchKey = (LinuxWatchService.LinuxWatchKey)this.wdToKey.get(Integer.valueOf(k));
/* 268 */       if (localLinuxWatchKey == null) {
/* 269 */         localLinuxWatchKey = new LinuxWatchService.LinuxWatchKey(localUnixPath, this.watcher, this.ifd, k);
/* 270 */         this.wdToKey.put(Integer.valueOf(k), localLinuxWatchKey);
/*     */       }
/* 272 */       return localLinuxWatchKey;
/*     */     }
/*     */ 
/*     */     void implCancelKey(WatchKey paramWatchKey)
/*     */     {
/* 278 */       LinuxWatchService.LinuxWatchKey localLinuxWatchKey = (LinuxWatchService.LinuxWatchKey)paramWatchKey;
/* 279 */       if (localLinuxWatchKey.isValid()) {
/* 280 */         this.wdToKey.remove(Integer.valueOf(localLinuxWatchKey.descriptor()));
/* 281 */         localLinuxWatchKey.invalidate(true);
/*     */       }
/*     */     }
/*     */ 
/*     */     void implCloseAll()
/*     */     {
/* 289 */       for (Map.Entry localEntry : this.wdToKey.entrySet()) {
/* 290 */         ((LinuxWatchService.LinuxWatchKey)localEntry.getValue()).invalidate(true);
/*     */       }
/* 292 */       this.wdToKey.clear();
/*     */ 
/* 295 */       LinuxWatchService.unsafe.freeMemory(this.address);
/* 296 */       UnixNativeDispatcher.close(this.socketpair[0]);
/* 297 */       UnixNativeDispatcher.close(this.socketpair[1]);
/* 298 */       UnixNativeDispatcher.close(this.ifd);
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*     */         while (true)
/*     */         {
/* 311 */           int i = LinuxWatchService.poll(this.ifd, this.socketpair[0]);
/*     */           int j;
/*     */           try
/*     */           {
/* 315 */             j = UnixNativeDispatcher.read(this.ifd, this.address, 8192);
/*     */           } catch (UnixException localUnixException2) {
/* 317 */             if (localUnixException2.errno() != 11)
/* 318 */               throw localUnixException2;
/* 319 */             j = 0;
/*     */           }
/*     */ 
/* 323 */           if ((i > 1) || ((i == 1) && (j == 0))) {
/*     */             try {
/* 325 */               UnixNativeDispatcher.read(this.socketpair[0], this.address, 8192);
/* 326 */               boolean bool = processRequests();
/* 327 */               if (bool)
/* 328 */                 break;
/*     */             } catch (UnixException localUnixException3) {
/* 330 */               if (localUnixException3.errno() != 11) {
/* 331 */                 throw localUnixException3;
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 336 */           int k = 0;
/* 337 */           while (k < j) {
/* 338 */             long l1 = this.address + k;
/* 339 */             int m = LinuxWatchService.unsafe.getInt(l1 + OFFSETOF_WD);
/* 340 */             int n = LinuxWatchService.unsafe.getInt(l1 + OFFSETOF_MASK);
/* 341 */             int i1 = LinuxWatchService.unsafe.getInt(l1 + OFFSETOF_LEN);
/*     */ 
/* 344 */             UnixPath localUnixPath = null;
/* 345 */             if (i1 > 0) {
/* 346 */               int i2 = i1;
/*     */ 
/* 350 */               while (i2 > 0) {
/* 351 */                 long l2 = l1 + OFFSETOF_NAME + i2 - 1L;
/* 352 */                 if (LinuxWatchService.unsafe.getByte(l2) != 0)
/*     */                   break;
/* 354 */                 i2--;
/*     */               }
/* 356 */               if (i2 > 0) {
/* 357 */                 byte[] arrayOfByte = new byte[i2];
/* 358 */                 LinuxWatchService.unsafe.copyMemory(null, l1 + OFFSETOF_NAME, arrayOfByte, Unsafe.ARRAY_BYTE_BASE_OFFSET, i2);
/*     */ 
/* 360 */                 localUnixPath = new UnixPath(this.fs, arrayOfByte);
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 365 */             processEvent(m, n, localUnixPath);
/*     */ 
/* 367 */             k += SIZEOF_INOTIFY_EVENT + i1;
/*     */           }
/*     */         }
/*     */       } catch (UnixException localUnixException1) {
/* 371 */         localUnixException1.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/*     */     private WatchEvent.Kind<?> maskToEventKind(int paramInt)
/*     */     {
/* 380 */       if ((paramInt & 0x2) > 0)
/* 381 */         return StandardWatchEventKinds.ENTRY_MODIFY;
/* 382 */       if ((paramInt & 0x4) > 0)
/* 383 */         return StandardWatchEventKinds.ENTRY_MODIFY;
/* 384 */       if ((paramInt & 0x100) > 0)
/* 385 */         return StandardWatchEventKinds.ENTRY_CREATE;
/* 386 */       if ((paramInt & 0x80) > 0)
/* 387 */         return StandardWatchEventKinds.ENTRY_CREATE;
/* 388 */       if ((paramInt & 0x200) > 0)
/* 389 */         return StandardWatchEventKinds.ENTRY_DELETE;
/* 390 */       if ((paramInt & 0x40) > 0)
/* 391 */         return StandardWatchEventKinds.ENTRY_DELETE;
/* 392 */       return null;
/*     */     }
/*     */ 
/*     */     private void processEvent(int paramInt1, int paramInt2, UnixPath paramUnixPath)
/*     */     {
/* 400 */       if ((paramInt2 & 0x4000) > 0) {
/* 401 */         for (localObject1 = this.wdToKey.entrySet().iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (Map.Entry)((Iterator)localObject1).next();
/* 402 */           ((LinuxWatchService.LinuxWatchKey)((Map.Entry)localObject2).getValue()).signalEvent(StandardWatchEventKinds.OVERFLOW, null);
/*     */         }
/*     */ 
/* 405 */         return;
/*     */       }
/*     */ 
/* 409 */       Object localObject1 = (LinuxWatchService.LinuxWatchKey)this.wdToKey.get(Integer.valueOf(paramInt1));
/* 410 */       if (localObject1 == null) {
/* 411 */         return;
/*     */       }
/*     */ 
/* 414 */       if ((paramInt2 & 0x8000) > 0) {
/* 415 */         this.wdToKey.remove(Integer.valueOf(paramInt1));
/* 416 */         ((LinuxWatchService.LinuxWatchKey)localObject1).invalidate(false);
/* 417 */         ((LinuxWatchService.LinuxWatchKey)localObject1).signal();
/* 418 */         return;
/*     */       }
/*     */ 
/* 422 */       if (paramUnixPath == null) {
/* 423 */         return;
/*     */       }
/*     */ 
/* 426 */       Object localObject2 = maskToEventKind(paramInt2);
/* 427 */       if (localObject2 != null)
/* 428 */         ((LinuxWatchService.LinuxWatchKey)localObject1).signalEvent((WatchEvent.Kind)localObject2, paramUnixPath);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.LinuxWatchService
 * JD-Core Version:    0.6.2
 */