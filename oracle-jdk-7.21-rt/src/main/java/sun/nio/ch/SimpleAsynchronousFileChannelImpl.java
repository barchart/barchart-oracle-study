/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.nio.channels.AsynchronousFileChannel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.NonReadableChannelException;
/*     */ import java.nio.channels.NonWritableChannelException;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ 
/*     */ public class SimpleAsynchronousFileChannelImpl extends AsynchronousFileChannelImpl
/*     */ {
/*  51 */   private static final FileDispatcher nd = new FileDispatcherImpl();
/*     */ 
/*  54 */   private final NativeThreadSet threads = new NativeThreadSet(2);
/*     */ 
/*     */   SimpleAsynchronousFileChannelImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ExecutorService paramExecutorService)
/*     */   {
/*  62 */     super(paramFileDescriptor, paramBoolean1, paramBoolean2, paramExecutorService);
/*     */   }
/*     */ 
/*     */   public static AsynchronousFileChannel open(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ThreadPool paramThreadPool)
/*     */   {
/*  71 */     ExecutorService localExecutorService = paramThreadPool == null ? DefaultExecutorHolder.defaultExecutor : paramThreadPool.executor();
/*     */ 
/*  73 */     return new SimpleAsynchronousFileChannelImpl(paramFileDescriptor, paramBoolean1, paramBoolean2, localExecutorService); } 
/*     */   // ERROR //
/*     */   public void close() throws IOException { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 215	sun/nio/ch/SimpleAsynchronousFileChannelImpl:fdObj	Ljava/io/FileDescriptor;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 212	sun/nio/ch/SimpleAsynchronousFileChannelImpl:closed	Z
/*     */     //   11: ifeq +6 -> 17
/*     */     //   14: aload_1
/*     */     //   15: monitorexit
/*     */     //   16: return
/*     */     //   17: aload_0
/*     */     //   18: iconst_1
/*     */     //   19: putfield 212	sun/nio/ch/SimpleAsynchronousFileChannelImpl:closed	Z
/*     */     //   22: aload_1
/*     */     //   23: monitorexit
/*     */     //   24: goto +8 -> 32
/*     */     //   27: astore_2
/*     */     //   28: aload_1
/*     */     //   29: monitorexit
/*     */     //   30: aload_2
/*     */     //   31: athrow
/*     */     //   32: aload_0
/*     */     //   33: invokevirtual 251	sun/nio/ch/SimpleAsynchronousFileChannelImpl:invalidateAllLocks	()V
/*     */     //   36: getstatic 218	sun/nio/ch/SimpleAsynchronousFileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
/*     */     //   39: aload_0
/*     */     //   40: getfield 215	sun/nio/ch/SimpleAsynchronousFileChannelImpl:fdObj	Ljava/io/FileDescriptor;
/*     */     //   43: invokevirtual 235	sun/nio/ch/FileDispatcher:preClose	(Ljava/io/FileDescriptor;)V
/*     */     //   46: aload_0
/*     */     //   47: getfield 219	sun/nio/ch/SimpleAsynchronousFileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
/*     */     //   50: invokevirtual 245	sun/nio/ch/NativeThreadSet:signalAndWait	()V
/*     */     //   53: aload_0
/*     */     //   54: getfield 217	sun/nio/ch/SimpleAsynchronousFileChannelImpl:closeLock	Ljava/util/concurrent/locks/ReadWriteLock;
/*     */     //   57: invokeinterface 264 1 0
/*     */     //   62: invokeinterface 262 1 0
/*     */     //   67: aload_0
/*     */     //   68: getfield 217	sun/nio/ch/SimpleAsynchronousFileChannelImpl:closeLock	Ljava/util/concurrent/locks/ReadWriteLock;
/*     */     //   71: invokeinterface 264 1 0
/*     */     //   76: invokeinterface 263 1 0
/*     */     //   81: goto +20 -> 101
/*     */     //   84: astore_3
/*     */     //   85: aload_0
/*     */     //   86: getfield 217	sun/nio/ch/SimpleAsynchronousFileChannelImpl:closeLock	Ljava/util/concurrent/locks/ReadWriteLock;
/*     */     //   89: invokeinterface 264 1 0
/*     */     //   94: invokeinterface 263 1 0
/*     */     //   99: aload_3
/*     */     //   100: athrow
/*     */     //   101: getstatic 218	sun/nio/ch/SimpleAsynchronousFileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
/*     */     //   104: aload_0
/*     */     //   105: getfield 215	sun/nio/ch/SimpleAsynchronousFileChannelImpl:fdObj	Ljava/io/FileDescriptor;
/*     */     //   108: invokevirtual 234	sun/nio/ch/FileDispatcher:close	(Ljava/io/FileDescriptor;)V
/*     */     //   111: return
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	16	27	finally
/*     */     //   17	24	27	finally
/*     */     //   27	30	27	finally
/*     */     //   84	85	84	finally } 
/* 108 */   public long size() throws IOException { int i = this.threads.add();
/*     */     try {
/* 110 */       long l1 = 0L;
/*     */       try {
/* 112 */         begin();
/*     */         do
/* 114 */           l1 = nd.size(this.fdObj);
/* 115 */         while ((l1 == -3L) && (isOpen()));
/* 116 */         long l2 = l1;
/*     */ 
/* 118 */         end(l1 >= 0L);
/*     */ 
/* 121 */         return l2;
/*     */       }
/*     */       finally
/*     */       {
/* 118 */         end(l1 >= 0L);
/*     */       }
/*     */     } finally {
/* 121 */       this.threads.remove(i);
/*     */     } }
/*     */ 
/*     */   public AsynchronousFileChannel truncate(long paramLong)
/*     */     throws IOException
/*     */   {
/* 127 */     if (paramLong < 0L)
/* 128 */       throw new IllegalArgumentException("Negative size");
/* 129 */     if (!this.writing)
/* 130 */       throw new NonWritableChannelException();
/* 131 */     int i = this.threads.add();
/*     */     try {
/* 133 */       long l = 0L;
/*     */       try {
/* 135 */         begin();
/*     */         do
/* 137 */           l = nd.size(this.fdObj);
/* 138 */         while ((l == -3L) && (isOpen()));
/*     */ 
/* 141 */         if ((paramLong < l) && (isOpen())) {
/*     */           do
/* 143 */             l = nd.truncate(this.fdObj, paramLong);
/* 144 */           while ((l == -3L) && (isOpen()));
/*     */         }
/* 146 */         SimpleAsynchronousFileChannelImpl localSimpleAsynchronousFileChannelImpl = this;
/*     */ 
/* 148 */         end(l > 0L);
/*     */ 
/* 151 */         return localSimpleAsynchronousFileChannelImpl;
/*     */       }
/*     */       finally
/*     */       {
/* 148 */         end(l > 0L);
/*     */       }
/*     */     } finally {
/* 151 */       this.threads.remove(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void force(boolean paramBoolean) throws IOException
/*     */   {
/* 157 */     int i = this.threads.add();
/*     */     try {
/* 159 */       int j = 0;
/*     */       try {
/* 161 */         begin();
/*     */         do {
/* 163 */           j = nd.force(this.fdObj, paramBoolean);
/* 164 */           if (j != -3) break;  } while (isOpen());
/*     */       } finally {
/* 166 */         end(j >= 0);
/*     */       }
/*     */     } finally {
/* 169 */       this.threads.remove(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   <A> Future<FileLock> implLock(final long paramLong1, long paramLong2, final boolean paramBoolean, final A paramA, final CompletionHandler<FileLock, ? super A> paramCompletionHandler)
/*     */   {
/* 180 */     if ((paramBoolean) && (!this.reading))
/* 181 */       throw new NonReadableChannelException();
/* 182 */     if ((!paramBoolean) && (!this.writing)) {
/* 183 */       throw new NonWritableChannelException();
/*     */     }
/*     */ 
/* 186 */     FileLockImpl localFileLockImpl = addToFileLockTable(paramLong1, paramLong2, paramBoolean);
/* 187 */     if (localFileLockImpl == null) {
/* 188 */       localClosedChannelException = new ClosedChannelException();
/* 189 */       if (paramCompletionHandler == null)
/* 190 */         return CompletedFuture.withFailure(localClosedChannelException);
/* 191 */       Invoker.invokeIndirectly(paramCompletionHandler, paramA, null, localClosedChannelException, this.executor);
/* 192 */       return null;
/*     */     }
/*     */ 
/* 195 */     final ClosedChannelException localClosedChannelException = paramCompletionHandler == null ? new PendingFuture(this) : null;
/*     */ 
/* 197 */     Runnable local1 = new Runnable() {
/*     */       public void run() {
/* 199 */         Object localObject1 = null;
/*     */ 
/* 201 */         int i = SimpleAsynchronousFileChannelImpl.this.threads.add();
/*     */         try {
/*     */           try { SimpleAsynchronousFileChannelImpl.this.begin();
/*     */             int j;
/*     */             do
/* 207 */               j = SimpleAsynchronousFileChannelImpl.nd.lock(SimpleAsynchronousFileChannelImpl.this.fdObj, true, paramLong1, paramBoolean, paramCompletionHandler);
/* 208 */             while ((j == 2) && (SimpleAsynchronousFileChannelImpl.this.isOpen()));
/* 209 */             if ((j != 0) || (!SimpleAsynchronousFileChannelImpl.this.isOpen()))
/* 210 */               throw new AsynchronousCloseException();
/*     */           } catch (IOException localIOException)
/*     */           {
/* 213 */             SimpleAsynchronousFileChannelImpl.this.removeFromFileLockTable(localClosedChannelException);
/*     */             AsynchronousCloseException localAsynchronousCloseException;
/* 214 */             if (!SimpleAsynchronousFileChannelImpl.this.isOpen())
/* 215 */               localAsynchronousCloseException = new AsynchronousCloseException();
/* 216 */             localObject1 = localAsynchronousCloseException;
/*     */           } finally {
/* 218 */             SimpleAsynchronousFileChannelImpl.this.end();
/*     */           }
/*     */         } finally {
/* 221 */           SimpleAsynchronousFileChannelImpl.this.threads.remove(i);
/*     */         }
/* 223 */         if (paramA == null)
/* 224 */           this.val$result.setResult(localClosedChannelException, localObject1);
/*     */         else
/* 226 */           Invoker.invokeUnchecked(paramA, this.val$attachment, localClosedChannelException, localObject1);
/*     */       }
/*     */     };
/* 230 */     int i = 0;
/*     */     try {
/* 232 */       this.executor.execute(local1);
/* 233 */       i = 1;
/*     */     } finally {
/* 235 */       if (i == 0)
/*     */       {
/* 237 */         removeFromFileLockTable(localFileLockImpl);
/*     */       }
/*     */     }
/* 240 */     return localClosedChannelException;
/*     */   }
/*     */ 
/*     */   public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 247 */     if ((paramBoolean) && (!this.reading))
/* 248 */       throw new NonReadableChannelException();
/* 249 */     if ((!paramBoolean) && (!this.writing)) {
/* 250 */       throw new NonWritableChannelException();
/*     */     }
/*     */ 
/* 253 */     FileLockImpl localFileLockImpl1 = addToFileLockTable(paramLong1, paramLong2, paramBoolean);
/* 254 */     if (localFileLockImpl1 == null) {
/* 255 */       throw new ClosedChannelException();
/*     */     }
/* 257 */     int i = this.threads.add();
/* 258 */     int j = 0;
/*     */     try { begin();
/*     */       int k;
/*     */       do
/* 263 */         k = nd.lock(this.fdObj, false, paramLong1, paramLong2, paramBoolean);
/* 264 */       while ((k == 2) && (isOpen()));
/*     */       FileLockImpl localFileLockImpl2;
/* 265 */       if ((k == 0) && (isOpen())) {
/* 266 */         j = 1;
/* 267 */         return localFileLockImpl1;
/*     */       }
/* 269 */       if (k == -1)
/* 270 */         return null;
/* 271 */       if (k == 2) {
/* 272 */         throw new AsynchronousCloseException();
/*     */       }
/* 274 */       throw new AssertionError();
/*     */     } finally {
/* 276 */       if (j == 0)
/* 277 */         removeFromFileLockTable(localFileLockImpl1);
/* 278 */       end();
/* 279 */       this.threads.remove(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void implRelease(FileLockImpl paramFileLockImpl) throws IOException
/*     */   {
/* 285 */     nd.release(this.fdObj, paramFileLockImpl.position(), paramFileLockImpl.size());
/*     */   }
/*     */ 
/*     */   <A> Future<Integer> implRead(final ByteBuffer paramByteBuffer, final long paramLong, final A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
/*     */   {
/* 294 */     if (paramLong < 0L)
/* 295 */       throw new IllegalArgumentException("Negative position");
/* 296 */     if (!this.reading)
/* 297 */       throw new NonReadableChannelException();
/* 298 */     if (paramByteBuffer.isReadOnly()) {
/* 299 */       throw new IllegalArgumentException("Read-only buffer");
/*     */     }
/*     */ 
/* 302 */     if ((!isOpen()) || (paramByteBuffer.remaining() == 0)) {
/* 303 */       localClosedChannelException = isOpen() ? null : new ClosedChannelException();
/* 304 */       if (paramCompletionHandler == null)
/* 305 */         return CompletedFuture.withResult(Integer.valueOf(0), localClosedChannelException);
/* 306 */       Invoker.invokeIndirectly(paramCompletionHandler, paramA, Integer.valueOf(0), localClosedChannelException, this.executor);
/* 307 */       return null;
/*     */     }
/*     */ 
/* 310 */     final ClosedChannelException localClosedChannelException = paramCompletionHandler == null ? new PendingFuture(this) : null;
/*     */ 
/* 312 */     Runnable local2 = new Runnable() {
/*     */       public void run() {
/* 314 */         int i = 0;
/* 315 */         Object localObject1 = null;
/*     */ 
/* 317 */         int j = SimpleAsynchronousFileChannelImpl.this.threads.add();
/*     */         try {
/* 319 */           SimpleAsynchronousFileChannelImpl.this.begin();
/*     */           do
/* 321 */             i = IOUtil.read(SimpleAsynchronousFileChannelImpl.this.fdObj, paramByteBuffer, paramLong, SimpleAsynchronousFileChannelImpl.nd, null);
/* 322 */           while ((i == -3) && (SimpleAsynchronousFileChannelImpl.this.isOpen()));
/* 323 */           if ((i < 0) && (!SimpleAsynchronousFileChannelImpl.this.isOpen()))
/* 324 */             throw new AsynchronousCloseException();
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/*     */           AsynchronousCloseException localAsynchronousCloseException;
/* 326 */           if (!SimpleAsynchronousFileChannelImpl.this.isOpen())
/* 327 */             localAsynchronousCloseException = new AsynchronousCloseException();
/* 328 */           localObject1 = localAsynchronousCloseException;
/*     */         } finally {
/* 330 */           SimpleAsynchronousFileChannelImpl.this.end();
/* 331 */           SimpleAsynchronousFileChannelImpl.this.threads.remove(j);
/*     */         }
/* 333 */         if (localClosedChannelException == null)
/* 334 */           paramA.setResult(Integer.valueOf(i), localObject1);
/*     */         else
/* 336 */           Invoker.invokeUnchecked(localClosedChannelException, this.val$attachment, Integer.valueOf(i), localObject1);
/*     */       }
/*     */     };
/* 340 */     this.executor.execute(local2);
/* 341 */     return localClosedChannelException;
/*     */   }
/*     */ 
/*     */   <A> Future<Integer> implWrite(final ByteBuffer paramByteBuffer, final long paramLong, final A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
/*     */   {
/* 350 */     if (paramLong < 0L)
/* 351 */       throw new IllegalArgumentException("Negative position");
/* 352 */     if (!this.writing) {
/* 353 */       throw new NonWritableChannelException();
/*     */     }
/*     */ 
/* 356 */     if ((!isOpen()) || (paramByteBuffer.remaining() == 0)) {
/* 357 */       localClosedChannelException = isOpen() ? null : new ClosedChannelException();
/* 358 */       if (paramCompletionHandler == null)
/* 359 */         return CompletedFuture.withResult(Integer.valueOf(0), localClosedChannelException);
/* 360 */       Invoker.invokeIndirectly(paramCompletionHandler, paramA, Integer.valueOf(0), localClosedChannelException, this.executor);
/* 361 */       return null;
/*     */     }
/*     */ 
/* 364 */     final ClosedChannelException localClosedChannelException = paramCompletionHandler == null ? new PendingFuture(this) : null;
/*     */ 
/* 366 */     Runnable local3 = new Runnable() {
/*     */       public void run() {
/* 368 */         int i = 0;
/* 369 */         Object localObject1 = null;
/*     */ 
/* 371 */         int j = SimpleAsynchronousFileChannelImpl.this.threads.add();
/*     */         try {
/* 373 */           SimpleAsynchronousFileChannelImpl.this.begin();
/*     */           do
/* 375 */             i = IOUtil.write(SimpleAsynchronousFileChannelImpl.this.fdObj, paramByteBuffer, paramLong, SimpleAsynchronousFileChannelImpl.nd, null);
/* 376 */           while ((i == -3) && (SimpleAsynchronousFileChannelImpl.this.isOpen()));
/* 377 */           if ((i < 0) && (!SimpleAsynchronousFileChannelImpl.this.isOpen()))
/* 378 */             throw new AsynchronousCloseException();
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/*     */           AsynchronousCloseException localAsynchronousCloseException;
/* 380 */           if (!SimpleAsynchronousFileChannelImpl.this.isOpen())
/* 381 */             localAsynchronousCloseException = new AsynchronousCloseException();
/* 382 */           localObject1 = localAsynchronousCloseException;
/*     */         } finally {
/* 384 */           SimpleAsynchronousFileChannelImpl.this.end();
/* 385 */           SimpleAsynchronousFileChannelImpl.this.threads.remove(j);
/*     */         }
/* 387 */         if (localClosedChannelException == null)
/* 388 */           paramA.setResult(Integer.valueOf(i), localObject1);
/*     */         else
/* 390 */           Invoker.invokeUnchecked(localClosedChannelException, this.val$attachment, Integer.valueOf(i), localObject1);
/*     */       }
/*     */     };
/* 394 */     this.executor.execute(local3);
/* 395 */     return localClosedChannelException;
/*     */   }
/*     */ 
/*     */   private static class DefaultExecutorHolder
/*     */   {
/*  46 */     static final ExecutorService defaultExecutor = ThreadPool.createDefault().executor();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SimpleAsynchronousFileChannelImpl
 * JD-Core Version:    0.6.2
 */