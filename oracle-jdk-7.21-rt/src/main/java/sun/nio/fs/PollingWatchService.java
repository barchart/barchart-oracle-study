/*     */ package sun.nio.fs;
/*     */ 
/*     */ import com.sun.nio.file.SensitivityWatchEventModifier;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.ClosedWatchServiceException;
/*     */ import java.nio.file.DirectoryIteratorException;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.NotDirectoryException;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardWatchEventKinds;
/*     */ import java.nio.file.WatchEvent.Kind;
/*     */ import java.nio.file.WatchEvent.Modifier;
/*     */ import java.nio.file.WatchKey;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.FileTime;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ class PollingWatchService extends AbstractWatchService
/*     */ {
/*  49 */   private final Map<Object, PollingWatchKey> map = new HashMap();
/*     */   private final ScheduledExecutorService scheduledExecutor;
/*     */ 
/*     */   PollingWatchService()
/*     */   {
/*  57 */     this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
/*     */     {
/*     */       public Thread newThread(Runnable paramAnonymousRunnable)
/*     */       {
/*  61 */         Thread localThread = new Thread(paramAnonymousRunnable);
/*  62 */         localThread.setDaemon(true);
/*  63 */         return localThread;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   WatchKey register(final Path paramPath, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier[] paramArrayOfModifier)
/*     */     throws IOException
/*     */   {
/*  77 */     if (paramArrayOfKind.length == 0)
/*  78 */       throw new IllegalArgumentException("No events to register");
/*  79 */     final HashSet localHashSet = new HashSet(paramArrayOfKind.length);
/*     */ 
/*  81 */     for (Object localObject3 : paramArrayOfKind)
/*     */     {
/*  83 */       if ((localObject3 == StandardWatchEventKinds.ENTRY_CREATE) || (localObject3 == StandardWatchEventKinds.ENTRY_MODIFY) || (localObject3 == StandardWatchEventKinds.ENTRY_DELETE))
/*     */       {
/*  87 */         localHashSet.add(localObject3);
/*     */       }
/*  92 */       else if (localObject3 == StandardWatchEventKinds.OVERFLOW) {
/*  93 */         if (paramArrayOfKind.length == 1) {
/*  94 */           throw new IllegalArgumentException("No events to register");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  99 */         if (localObject3 == null)
/* 100 */           throw new NullPointerException("An element in event set is 'null'");
/* 101 */         throw new UnsupportedOperationException(localObject3.name());
/*     */       }
/*     */     }
/*     */ 
/* 105 */     ??? = SensitivityWatchEventModifier.MEDIUM;
/* 106 */     if (paramArrayOfModifier.length > 0) {
/* 107 */       for (Object localObject4 : paramArrayOfModifier) {
/* 108 */         if (localObject4 == null)
/* 109 */           throw new NullPointerException();
/* 110 */         if ((localObject4 instanceof SensitivityWatchEventModifier)) {
/* 111 */           ??? = (SensitivityWatchEventModifier)localObject4;
/*     */         }
/*     */         else {
/* 114 */           throw new UnsupportedOperationException("Modifier not supported");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 119 */     if (!isOpen()) {
/* 120 */       throw new ClosedWatchServiceException();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 125 */       ??? = ???;
/* 126 */       return (WatchKey)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public PollingWatchService.PollingWatchKey run() throws IOException
/*     */         {
/* 130 */           return PollingWatchService.this.doPrivilegedRegister(paramPath, localHashSet, this.val$s);
/*     */         } } );
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {
/* 134 */       Throwable localThrowable = localPrivilegedActionException.getCause();
/* 135 */       if ((localThrowable != null) && ((localThrowable instanceof IOException)))
/* 136 */         throw ((IOException)localThrowable);
/* 137 */       throw new AssertionError(localPrivilegedActionException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private PollingWatchKey doPrivilegedRegister(Path paramPath, Set<? extends WatchEvent.Kind<?>> paramSet, SensitivityWatchEventModifier paramSensitivityWatchEventModifier)
/*     */     throws IOException
/*     */   {
/* 149 */     BasicFileAttributes localBasicFileAttributes = Files.readAttributes(paramPath, BasicFileAttributes.class, new LinkOption[0]);
/* 150 */     if (!localBasicFileAttributes.isDirectory()) {
/* 151 */       throw new NotDirectoryException(paramPath.toString());
/*     */     }
/* 153 */     Object localObject1 = localBasicFileAttributes.fileKey();
/* 154 */     if (localObject1 == null) {
/* 155 */       throw new AssertionError("File keys must be supported");
/*     */     }
/*     */ 
/* 158 */     synchronized (closeLock()) {
/* 159 */       if (!isOpen())
/* 160 */         throw new ClosedWatchServiceException();
/*     */       PollingWatchKey localPollingWatchKey;
/* 163 */       synchronized (this.map) {
/* 164 */         localPollingWatchKey = (PollingWatchKey)this.map.get(localObject1);
/* 165 */         if (localPollingWatchKey == null)
/*     */         {
/* 167 */           localPollingWatchKey = new PollingWatchKey(paramPath, this, localObject1);
/* 168 */           this.map.put(localObject1, localPollingWatchKey);
/*     */         }
/*     */         else {
/* 171 */           localPollingWatchKey.disable();
/*     */         }
/*     */       }
/* 174 */       localPollingWatchKey.enable(paramSet, paramSensitivityWatchEventModifier.sensitivityValueInSeconds());
/* 175 */       return localPollingWatchKey;
/*     */     }
/*     */   }
/*     */ 
/*     */   void implClose()
/*     */     throws IOException
/*     */   {
/* 182 */     synchronized (this.map) {
/* 183 */       for (Map.Entry localEntry : this.map.entrySet()) {
/* 184 */         PollingWatchKey localPollingWatchKey = (PollingWatchKey)localEntry.getValue();
/* 185 */         localPollingWatchKey.disable();
/* 186 */         localPollingWatchKey.invalidate();
/*     */       }
/* 188 */       this.map.clear();
/*     */     }
/* 190 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 193 */         PollingWatchService.this.scheduledExecutor.shutdown();
/* 194 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static class CacheEntry
/*     */   {
/*     */     private long lastModified;
/*     */     private int lastTickCount;
/*     */ 
/*     */     CacheEntry(long paramLong, int paramInt)
/*     */     {
/* 207 */       this.lastModified = paramLong;
/* 208 */       this.lastTickCount = paramInt;
/*     */     }
/*     */ 
/*     */     int lastTickCount() {
/* 212 */       return this.lastTickCount;
/*     */     }
/*     */ 
/*     */     long lastModified() {
/* 216 */       return this.lastModified;
/*     */     }
/*     */ 
/*     */     void update(long paramLong, int paramInt) {
/* 220 */       this.lastModified = paramLong;
/* 221 */       this.lastTickCount = paramInt;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PollingWatchKey extends AbstractWatchKey
/*     */   {
/*     */     private final Object fileKey;
/*     */     private Set<? extends WatchEvent.Kind<?>> events;
/*     */     private ScheduledFuture<?> poller;
/*     */     private volatile boolean valid;
/*     */     private int tickCount;
/*     */     private Map<Path, PollingWatchService.CacheEntry> entries;
/*     */ 
/*     */     PollingWatchKey(Path paramPollingWatchService, PollingWatchService paramObject, Object arg4)
/*     */       throws IOException
/*     */     {
/* 251 */       super(paramObject);
/*     */       Object localObject1;
/* 252 */       this.fileKey = localObject1;
/* 253 */       this.valid = true;
/* 254 */       this.tickCount = 0;
/* 255 */       this.entries = new HashMap();
/*     */       try
/*     */       {
/* 258 */         DirectoryStream localDirectoryStream = Files.newDirectoryStream(paramPollingWatchService); Object localObject2 = null;
/*     */         try { for (Path localPath : localDirectoryStream)
/*     */           {
/* 261 */             long l = Files.getLastModifiedTime(localPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }).toMillis();
/*     */ 
/* 263 */             this.entries.put(localPath.getFileName(), new PollingWatchService.CacheEntry(l, this.tickCount));
/*     */           }
/*     */         }
/*     */         catch (Throwable localThrowable2)
/*     */         {
/* 258 */           localObject2 = localThrowable2; throw localThrowable2;
/*     */         }
/*     */         finally
/*     */         {
/* 265 */           if (localDirectoryStream != null) if (localObject2 != null) try { localDirectoryStream.close(); } catch (Throwable localThrowable3) { localObject2.addSuppressed(localThrowable3); } else localDirectoryStream.close();  
/*     */         } } catch (DirectoryIteratorException localDirectoryIteratorException) { throw localDirectoryIteratorException.getCause(); }
/*     */     }
/*     */ 
/*     */     Object fileKey()
/*     */     {
/* 271 */       return this.fileKey;
/*     */     }
/*     */ 
/*     */     public boolean isValid()
/*     */     {
/* 276 */       return this.valid;
/*     */     }
/*     */ 
/*     */     void invalidate() {
/* 280 */       this.valid = false;
/*     */     }
/*     */ 
/*     */     void enable(Set<? extends WatchEvent.Kind<?>> paramSet, long paramLong)
/*     */     {
/* 285 */       synchronized (this)
/*     */       {
/* 287 */         this.events = paramSet;
/*     */ 
/* 290 */         Runnable local1 = new Runnable() { public void run() { PollingWatchService.PollingWatchKey.this.poll(); }
/*     */ 
/*     */         };
/* 291 */         this.poller = PollingWatchService.this.scheduledExecutor.scheduleAtFixedRate(local1, paramLong, paramLong, TimeUnit.SECONDS);
/*     */       }
/*     */     }
/*     */ 
/*     */     void disable()
/*     */     {
/* 298 */       synchronized (this) {
/* 299 */         if (this.poller != null)
/* 300 */           this.poller.cancel(false);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void cancel()
/*     */     {
/* 306 */       this.valid = false;
/* 307 */       synchronized (PollingWatchService.this.map) {
/* 308 */         PollingWatchService.this.map.remove(fileKey());
/*     */       }
/* 310 */       disable();
/*     */     }
/*     */ 
/*     */     synchronized void poll()
/*     */     {
/* 318 */       if (!this.valid) {
/* 319 */         return;
/*     */       }
/*     */ 
/* 323 */       this.tickCount += 1;
/*     */ 
/* 326 */       DirectoryStream localDirectoryStream = null;
/*     */       try {
/* 328 */         localDirectoryStream = Files.newDirectoryStream(watchable());
/*     */       }
/* 331 */       catch (IOException localIOException1) { cancel();
/* 332 */         signal();
/*     */         return;
/*     */       }
/*     */       Object localObject1;
/*     */       try {
/* 338 */         for (localIterator1 = localDirectoryStream.iterator(); localIterator1.hasNext(); ) { localObject1 = (Path)localIterator1.next();
/* 339 */           long l = 0L;
/*     */           try {
/* 341 */             l = Files.getLastModifiedTime((Path)localObject1, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }).toMillis();
/*     */           }
/*     */           catch (IOException localIOException4)
/*     */           {
/*     */           }
/*     */ 
/* 347 */           continue;
/*     */ 
/* 351 */           PollingWatchService.CacheEntry localCacheEntry2 = (PollingWatchService.CacheEntry)this.entries.get(((Path)localObject1).getFileName());
/* 352 */           if (localCacheEntry2 == null)
/*     */           {
/* 354 */             this.entries.put(((Path)localObject1).getFileName(), new PollingWatchService.CacheEntry(l, this.tickCount));
/*     */ 
/* 358 */             if (this.events.contains(StandardWatchEventKinds.ENTRY_CREATE)) {
/* 359 */               signalEvent(StandardWatchEventKinds.ENTRY_CREATE, ((Path)localObject1).getFileName());
/*     */             }
/* 366 */             else if (this.events.contains(StandardWatchEventKinds.ENTRY_MODIFY)) {
/* 367 */               signalEvent(StandardWatchEventKinds.ENTRY_MODIFY, ((Path)localObject1).getFileName());
/*     */             }
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 374 */             if ((localCacheEntry2.lastModified != l) && 
/* 375 */               (this.events.contains(StandardWatchEventKinds.ENTRY_MODIFY))) {
/* 376 */               signalEvent(StandardWatchEventKinds.ENTRY_MODIFY, ((Path)localObject1).getFileName());
/*     */             }
/*     */ 
/* 381 */             localCacheEntry2.update(l, this.tickCount);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (DirectoryIteratorException localDirectoryIteratorException)
/*     */       {
/*     */       }
/*     */       finally
/*     */       {
/*     */         try
/*     */         {
/*     */           Iterator localIterator1;
/* 391 */           localDirectoryStream.close();
/*     */         }
/*     */         catch (IOException localIOException5)
/*     */         {
/*     */         }
/*     */       }
/*     */ 
/* 398 */       Iterator localIterator2 = this.entries.entrySet().iterator();
/* 399 */       while (localIterator2.hasNext()) {
/* 400 */         localObject1 = (Map.Entry)localIterator2.next();
/* 401 */         PollingWatchService.CacheEntry localCacheEntry1 = (PollingWatchService.CacheEntry)((Map.Entry)localObject1).getValue();
/* 402 */         if (localCacheEntry1.lastTickCount() != this.tickCount) {
/* 403 */           Path localPath = (Path)((Map.Entry)localObject1).getKey();
/*     */ 
/* 405 */           localIterator2.remove();
/* 406 */           if (this.events.contains(StandardWatchEventKinds.ENTRY_DELETE))
/* 407 */             signalEvent(StandardWatchEventKinds.ENTRY_DELETE, localPath);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.fs.PollingWatchService
 * JD-Core Version:    0.6.2
 */