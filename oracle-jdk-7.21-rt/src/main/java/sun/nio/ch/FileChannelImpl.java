/*      */ package sun.nio.ch;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.MappedByteBuffer;
/*      */ import java.nio.channels.ClosedByInterruptException;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.FileChannel.MapMode;
/*      */ import java.nio.channels.FileLock;
/*      */ import java.nio.channels.FileLockInterruptionException;
/*      */ import java.nio.channels.NonReadableChannelException;
/*      */ import java.nio.channels.NonWritableChannelException;
/*      */ import java.nio.channels.OverlappingFileLockException;
/*      */ import java.nio.channels.ReadableByteChannel;
/*      */ import java.nio.channels.WritableByteChannel;
/*      */ import java.security.AccessController;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import sun.misc.Cleaner;
/*      */ import sun.misc.JavaNioAccess.BufferPool;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ public class FileChannelImpl extends FileChannel
/*      */ {
/* 1133 */   private static final long allocationGranularity = initIDs();
/*      */   private final FileDispatcher nd;
/*      */   private final FileDescriptor fd;
/*      */   private final boolean writable;
/*      */   private final boolean readable;
/*      */   private final boolean append;
/*      */   private final Object parent;
/*   60 */   private final NativeThreadSet threads = new NativeThreadSet(2);
/*      */ 
/*   63 */   private final Object positionLock = new Object();
/*      */   private static volatile boolean transferSupported;
/*      */   private static volatile boolean pipeSupported;
/*      */   private static volatile boolean fileSupported;
/*      */   private static final long MAPPED_TRANSFER_SIZE = 8388608L;
/*      */   private static final int TRANSFER_SIZE = 8192;
/*      */   private static final int MAP_RO = 0;
/*      */   private static final int MAP_RW = 1;
/*      */   private static final int MAP_PV = 2;
/*      */   private volatile FileLockTable fileLockTable;
/*      */   private static boolean isSharedFileLockTable;
/*      */   private static volatile boolean propertyChecked;
/*      */ 
/*      */   private FileChannelImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject)
/*      */   {
/*   68 */     this.fd = paramFileDescriptor;
/*   69 */     this.readable = paramBoolean1;
/*   70 */     this.writable = paramBoolean2;
/*   71 */     this.append = paramBoolean3;
/*   72 */     this.parent = paramObject;
/*   73 */     this.nd = new FileDispatcherImpl(paramBoolean3);
/*      */   }
/*      */ 
/*      */   public static FileChannel open(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, Object paramObject)
/*      */   {
/*   81 */     return new FileChannelImpl(paramFileDescriptor, paramBoolean1, paramBoolean2, false, paramObject);
/*      */   }
/*      */ 
/*      */   public static FileChannel open(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject)
/*      */   {
/*   89 */     return new FileChannelImpl(paramFileDescriptor, paramBoolean1, paramBoolean2, paramBoolean3, paramObject);
/*      */   }
/*      */ 
/*      */   private void ensureOpen() throws IOException {
/*   93 */     if (!isOpen())
/*   94 */       throw new ClosedChannelException();
/*      */   }
/*      */ 
/*      */   protected void implCloseChannel()
/*      */     throws IOException
/*      */   {
/*  102 */     if (this.fileLockTable != null) {
/*  103 */       for (FileLock localFileLock : this.fileLockTable.removeAll()) {
/*  104 */         synchronized (localFileLock) {
/*  105 */           if (localFileLock.isValid()) {
/*  106 */             this.nd.release(this.fd, localFileLock.position(), localFileLock.size());
/*  107 */             ((FileLockImpl)localFileLock).invalidate();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  113 */     this.nd.preClose(this.fd);
/*  114 */     this.threads.signalAndWait();
/*      */ 
/*  116 */     if (this.parent != null)
/*      */     {
/*  123 */       ((Closeable)this.parent).close();
/*      */     }
/*  125 */     else this.nd.close(this.fd);
/*      */   }
/*      */ 
/*      */   public int read(ByteBuffer paramByteBuffer)
/*      */     throws IOException
/*      */   {
/*  131 */     ensureOpen();
/*  132 */     if (!this.readable)
/*  133 */       throw new NonReadableChannelException();
/*  134 */     synchronized (this.positionLock) {
/*  135 */       int i = 0;
/*  136 */       int j = -1;
/*      */       try {
/*  138 */         begin();
/*  139 */         j = this.threads.add();
/*  140 */         if (!isOpen()) {
/*  141 */           k = 0;
/*      */ 
/*  147 */           this.threads.remove(j);
/*  148 */           end(i > 0);
/*  149 */           assert (IOStatus.check(i)); return k;
/*      */         }
/*      */         do
/*  143 */           i = IOUtil.read(this.fd, paramByteBuffer, -1L, this.nd, this.positionLock);
/*  144 */         while ((i == -3) && (isOpen()));
/*  145 */         int k = IOStatus.normalize(i);
/*      */ 
/*  147 */         this.threads.remove(j);
/*  148 */         end(i > 0);
/*  149 */         assert (IOStatus.check(i)); return k;
/*      */       }
/*      */       finally
/*      */       {
/*  147 */         this.threads.remove(j);
/*  148 */         end(i > 0);
/*  149 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  157 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  158 */       throw new IndexOutOfBoundsException();
/*  159 */     ensureOpen();
/*  160 */     if (!this.readable)
/*  161 */       throw new NonReadableChannelException();
/*  162 */     synchronized (this.positionLock) {
/*  163 */       long l1 = 0L;
/*  164 */       int i = -1;
/*      */       try {
/*  166 */         begin();
/*  167 */         i = this.threads.add();
/*  168 */         if (!isOpen()) {
/*  169 */           l2 = 0L;
/*      */ 
/*  175 */           this.threads.remove(i);
/*  176 */           end(l1 > 0L);
/*  177 */           assert (IOStatus.check(l1)); return l2;
/*      */         }
/*      */         do
/*  171 */           l1 = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, this.nd);
/*  172 */         while ((l1 == -3L) && (isOpen()));
/*  173 */         long l2 = IOStatus.normalize(l1);
/*      */ 
/*  175 */         this.threads.remove(i);
/*  176 */         end(l1 > 0L);
/*  177 */         assert (IOStatus.check(l1)); return l2;
/*      */       }
/*      */       finally
/*      */       {
/*  175 */         this.threads.remove(i);
/*  176 */         end(l1 > 0L);
/*  177 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError(); 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/*  183 */     ensureOpen();
/*  184 */     if (!this.writable)
/*  185 */       throw new NonWritableChannelException();
/*  186 */     synchronized (this.positionLock) {
/*  187 */       int i = 0;
/*  188 */       int j = -1;
/*      */       try {
/*  190 */         begin();
/*  191 */         j = this.threads.add();
/*  192 */         if (!isOpen()) {
/*  193 */           k = 0;
/*      */ 
/*  199 */           this.threads.remove(j);
/*  200 */           end(i > 0);
/*  201 */           assert (IOStatus.check(i)); return k;
/*      */         }
/*      */         do
/*  195 */           i = IOUtil.write(this.fd, paramByteBuffer, -1L, this.nd, this.positionLock);
/*  196 */         while ((i == -3) && (isOpen()));
/*  197 */         int k = IOStatus.normalize(i);
/*      */ 
/*  199 */         this.threads.remove(j);
/*  200 */         end(i > 0);
/*  201 */         assert (IOStatus.check(i)); return k;
/*      */       }
/*      */       finally
/*      */       {
/*  199 */         this.threads.remove(j);
/*  200 */         end(i > 0);
/*  201 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/*  209 */     if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2))
/*  210 */       throw new IndexOutOfBoundsException();
/*  211 */     ensureOpen();
/*  212 */     if (!this.writable)
/*  213 */       throw new NonWritableChannelException();
/*  214 */     synchronized (this.positionLock) {
/*  215 */       long l1 = 0L;
/*  216 */       int i = -1;
/*      */       try {
/*  218 */         begin();
/*  219 */         i = this.threads.add();
/*  220 */         if (!isOpen()) {
/*  221 */           l2 = 0L;
/*      */ 
/*  227 */           this.threads.remove(i);
/*  228 */           end(l1 > 0L);
/*  229 */           assert (IOStatus.check(l1)); return l2;
/*      */         }
/*      */         do
/*  223 */           l1 = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, this.nd);
/*  224 */         while ((l1 == -3L) && (isOpen()));
/*  225 */         long l2 = IOStatus.normalize(l1);
/*      */ 
/*  227 */         this.threads.remove(i);
/*  228 */         end(l1 > 0L);
/*  229 */         assert (IOStatus.check(l1)); return l2;
/*      */       }
/*      */       finally
/*      */       {
/*  227 */         this.threads.remove(i);
/*  228 */         end(l1 > 0L);
/*  229 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public long position()
/*      */     throws IOException
/*      */   {
/*  237 */     ensureOpen();
/*  238 */     synchronized (this.positionLock) {
/*  239 */       long l1 = -1L;
/*  240 */       int i = -1;
/*      */       try {
/*  242 */         begin();
/*  243 */         i = this.threads.add();
/*  244 */         if (!isOpen()) {
/*  245 */           l2 = 0L;
/*      */ 
/*  252 */           this.threads.remove(i);
/*  253 */           end(l1 > -1L);
/*  254 */           assert (IOStatus.check(l1)); return l2;
/*      */         }
/*      */         do
/*  248 */           l1 = this.append ? this.nd.size(this.fd) : position0(this.fd, -1L);
/*  249 */         while ((l1 == -3L) && (isOpen()));
/*  250 */         long l2 = IOStatus.normalize(l1);
/*      */ 
/*  252 */         this.threads.remove(i);
/*  253 */         end(l1 > -1L);
/*  254 */         assert (IOStatus.check(l1)); return l2;
/*      */       }
/*      */       finally
/*      */       {
/*  252 */         this.threads.remove(i);
/*  253 */         end(l1 > -1L);
/*  254 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError(); 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public FileChannel position(long paramLong) throws IOException {
/*  260 */     ensureOpen();
/*  261 */     if (paramLong < 0L)
/*  262 */       throw new IllegalArgumentException();
/*  263 */     synchronized (this.positionLock) {
/*  264 */       long l = -1L;
/*  265 */       int i = -1;
/*      */       try {
/*  267 */         begin();
/*  268 */         i = this.threads.add();
/*  269 */         if (!isOpen()) {
/*  270 */           localObject1 = null;
/*      */ 
/*  276 */           this.threads.remove(i);
/*  277 */           end(l > -1L);
/*  278 */           assert (IOStatus.check(l)); return localObject1;
/*      */         }
/*      */         do
/*  272 */           l = position0(this.fd, paramLong);
/*  273 */         while ((l == -3L) && (isOpen()));
/*  274 */         Object localObject1 = this;
/*      */ 
/*  276 */         this.threads.remove(i);
/*  277 */         end(l > -1L);
/*  278 */         assert (IOStatus.check(l)); return localObject1;
/*      */       }
/*      */       finally
/*      */       {
/*  276 */         this.threads.remove(i);
/*  277 */         end(l > -1L);
/*  278 */         if ((!$assertionsDisabled) && (!IOStatus.check(l))) throw new AssertionError(); 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public long size() throws IOException {
/*  284 */     ensureOpen();
/*  285 */     synchronized (this.positionLock) {
/*  286 */       long l1 = -1L;
/*  287 */       int i = -1;
/*      */       try {
/*  289 */         begin();
/*  290 */         i = this.threads.add();
/*  291 */         if (!isOpen()) {
/*  292 */           l2 = -1L;
/*      */ 
/*  298 */           this.threads.remove(i);
/*  299 */           end(l1 > -1L);
/*  300 */           assert (IOStatus.check(l1)); return l2;
/*      */         }
/*      */         do
/*  294 */           l1 = this.nd.size(this.fd);
/*  295 */         while ((l1 == -3L) && (isOpen()));
/*  296 */         long l2 = IOStatus.normalize(l1);
/*      */ 
/*  298 */         this.threads.remove(i);
/*  299 */         end(l1 > -1L);
/*  300 */         assert (IOStatus.check(l1)); return l2;
/*      */       }
/*      */       finally
/*      */       {
/*  298 */         this.threads.remove(i);
/*  299 */         end(l1 > -1L);
/*  300 */         if ((!$assertionsDisabled) && (!IOStatus.check(l1))) throw new AssertionError(); 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public FileChannel truncate(long paramLong) throws IOException {
/*  306 */     ensureOpen();
/*  307 */     if (paramLong < 0L)
/*  308 */       throw new IllegalArgumentException();
/*  309 */     if (paramLong > size())
/*  310 */       return this;
/*  311 */     if (!this.writable)
/*  312 */       throw new NonWritableChannelException();
/*  313 */     synchronized (this.positionLock) {
/*  314 */       int i = -1;
/*  315 */       long l = -1L;
/*  316 */       int j = -1;
/*      */       try {
/*  318 */         begin();
/*  319 */         j = this.threads.add();
/*  320 */         if (!isOpen()) {
/*  321 */           localObject1 = null;
/*      */ 
/*  346 */           this.threads.remove(j);
/*  347 */           end(i > -1);
/*  348 */           assert (IOStatus.check(i)); return localObject1;
/*      */         }
/*      */         do
/*  325 */           l = position0(this.fd, -1L);
/*  326 */         while ((l == -3L) && (isOpen()));
/*  327 */         if (!isOpen()) {
/*  328 */           localObject1 = null;
/*      */ 
/*  346 */           this.threads.remove(j);
/*  347 */           end(i > -1);
/*  348 */           assert (IOStatus.check(i)); return localObject1;
/*      */         }
/*  329 */         assert (l >= 0L);
/*      */         do
/*      */         {
/*  333 */           i = this.nd.truncate(this.fd, paramLong);
/*  334 */         }while ((i == -3) && (isOpen()));
/*  335 */         if (!isOpen()) {
/*  336 */           localObject1 = null;
/*      */ 
/*  346 */           this.threads.remove(j);
/*  347 */           end(i > -1);
/*  348 */           assert (IOStatus.check(i)); return localObject1;
/*      */         }
/*  339 */         if (l > paramLong)
/*  340 */           l = paramLong;
/*      */         do
/*  342 */           i = (int)position0(this.fd, l);
/*  343 */         while ((i == -3) && (isOpen()));
/*  344 */         Object localObject1 = this;
/*      */ 
/*  346 */         this.threads.remove(j);
/*  347 */         end(i > -1);
/*  348 */         assert (IOStatus.check(i)); return localObject1;
/*      */       }
/*      */       finally
/*      */       {
/*  346 */         this.threads.remove(j);
/*  347 */         end(i > -1);
/*  348 */         if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError(); 
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void force(boolean paramBoolean) throws IOException {
/*  354 */     ensureOpen();
/*  355 */     int i = -1;
/*  356 */     int j = -1;
/*      */     try {
/*  358 */       begin();
/*  359 */       j = this.threads.add();
/*  360 */       if (!isOpen())
/*      */         return;
/*      */       do {
/*  363 */         i = this.nd.force(this.fd, paramBoolean);
/*  364 */         if (i != -3) break;  } while (isOpen());
/*      */     } finally {
/*  366 */       this.threads.remove(j);
/*  367 */       end(i > -1);
/*  368 */       if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */     }
/*      */   }
/*      */ 
/*      */   private long transferToDirectly(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel)
/*      */     throws IOException
/*      */   {
/*  391 */     if (!transferSupported) {
/*  392 */       return -4L;
/*      */     }
/*  394 */     FileDescriptor localFileDescriptor = null;
/*  395 */     if ((paramWritableByteChannel instanceof FileChannelImpl)) {
/*  396 */       if (!fileSupported)
/*  397 */         return -6L;
/*  398 */       localFileDescriptor = ((FileChannelImpl)paramWritableByteChannel).fd;
/*  399 */     } else if ((paramWritableByteChannel instanceof SelChImpl))
/*      */     {
/*  401 */       if (((paramWritableByteChannel instanceof SinkChannelImpl)) && (!pipeSupported))
/*  402 */         return -6L;
/*  403 */       localFileDescriptor = ((SelChImpl)paramWritableByteChannel).getFD();
/*      */     }
/*  405 */     if (localFileDescriptor == null)
/*  406 */       return -4L;
/*  407 */     int i = IOUtil.fdVal(this.fd);
/*  408 */     int j = IOUtil.fdVal(localFileDescriptor);
/*  409 */     if (i == j) {
/*  410 */       return -4L;
/*      */     }
/*  412 */     long l1 = -1L;
/*  413 */     int k = -1;
/*      */     try {
/*  415 */       begin();
/*  416 */       k = this.threads.add();
/*      */       long l2;
/*  417 */       if (!isOpen())
/*  418 */         return -1L;
/*      */       do
/*  420 */         l1 = transferTo0(i, paramLong, paramInt, j);
/*  421 */       while ((l1 == -3L) && (isOpen()));
/*  422 */       if (l1 == -6L) {
/*  423 */         if ((paramWritableByteChannel instanceof SinkChannelImpl))
/*  424 */           pipeSupported = false;
/*  425 */         if ((paramWritableByteChannel instanceof FileChannelImpl))
/*  426 */           fileSupported = false;
/*  427 */         return -6L;
/*      */       }
/*  429 */       if (l1 == -4L)
/*      */       {
/*  431 */         transferSupported = false;
/*  432 */         return -4L;
/*      */       }
/*  434 */       return IOStatus.normalize(l1);
/*      */     } finally {
/*  436 */       this.threads.remove(k);
/*  437 */       end(l1 > -1L);
/*      */     }
/*      */   }
/*      */ 
/*      */   private long transferToTrustedChannel(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
/*      */     throws IOException
/*      */   {
/*  448 */     boolean bool = paramWritableByteChannel instanceof SelChImpl;
/*  449 */     if ((!(paramWritableByteChannel instanceof FileChannelImpl)) && (!bool)) {
/*  450 */       return -4L;
/*      */     }
/*      */ 
/*  453 */     long l1 = paramLong2;
/*  454 */     while (l1 > 0L) {
/*  455 */       long l2 = Math.min(l1, 8388608L);
/*      */       try {
/*  457 */         MappedByteBuffer localMappedByteBuffer = map(FileChannel.MapMode.READ_ONLY, paramLong1, l2);
/*      */         try
/*      */         {
/*  460 */           int i = paramWritableByteChannel.write(localMappedByteBuffer);
/*  461 */           assert (i >= 0);
/*  462 */           l1 -= i;
/*  463 */           if (bool)
/*      */           {
/*  470 */             unmap(localMappedByteBuffer); break;
/*      */           }
/*  467 */           assert (i > 0);
/*  468 */           paramLong1 += i;
/*      */         } finally {
/*  470 */           unmap(localMappedByteBuffer);
/*      */         }
/*      */       }
/*      */       catch (ClosedByInterruptException localClosedByInterruptException)
/*      */       {
/*  475 */         assert (!paramWritableByteChannel.isOpen());
/*      */         try {
/*  477 */           close();
/*      */         } catch (Throwable localThrowable) {
/*  479 */           localClosedByInterruptException.addSuppressed(localThrowable);
/*      */         }
/*  481 */         throw localClosedByInterruptException;
/*      */       }
/*      */       catch (IOException localIOException) {
/*  484 */         if (l1 == paramLong2)
/*  485 */           throw localIOException;
/*  486 */         break;
/*      */       }
/*      */     }
/*  489 */     return paramLong2 - l1;
/*      */   }
/*      */ 
/*      */   private long transferToArbitraryChannel(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel)
/*      */     throws IOException
/*      */   {
/*  497 */     int i = Math.min(paramInt, 8192);
/*  498 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(i);
/*  499 */     long l1 = 0L;
/*  500 */     long l2 = paramLong;
/*      */     try {
/*  502 */       Util.erase(localByteBuffer);
/*  503 */       while (l1 < paramInt) {
/*  504 */         localByteBuffer.limit(Math.min((int)(paramInt - l1), 8192));
/*  505 */         int j = read(localByteBuffer, l2);
/*  506 */         if (j <= 0)
/*      */           break;
/*  508 */         localByteBuffer.flip();
/*      */ 
/*  511 */         int k = paramWritableByteChannel.write(localByteBuffer);
/*  512 */         l1 += k;
/*  513 */         if (k != j)
/*      */           break;
/*  515 */         l2 += k;
/*  516 */         localByteBuffer.clear();
/*      */       }
/*  518 */       return l1;
/*      */     } catch (IOException localIOException) {
/*  520 */       if (l1 > 0L)
/*  521 */         return l1;
/*  522 */       throw localIOException;
/*      */     } finally {
/*  524 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */ 
/*      */   public long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
/*      */     throws IOException
/*      */   {
/*  532 */     ensureOpen();
/*  533 */     if (!paramWritableByteChannel.isOpen())
/*  534 */       throw new ClosedChannelException();
/*  535 */     if (!this.readable)
/*  536 */       throw new NonReadableChannelException();
/*  537 */     if (((paramWritableByteChannel instanceof FileChannelImpl)) && (!((FileChannelImpl)paramWritableByteChannel).writable))
/*      */     {
/*  539 */       throw new NonWritableChannelException();
/*  540 */     }if ((paramLong1 < 0L) || (paramLong2 < 0L))
/*  541 */       throw new IllegalArgumentException();
/*  542 */     long l1 = size();
/*  543 */     if (paramLong1 > l1)
/*  544 */       return 0L;
/*  545 */     int i = (int)Math.min(paramLong2, 2147483647L);
/*  546 */     if (l1 - paramLong1 < i)
/*  547 */       i = (int)(l1 - paramLong1);
/*      */     long l2;
/*  552 */     if ((l2 = transferToDirectly(paramLong1, i, paramWritableByteChannel)) >= 0L) {
/*  553 */       return l2;
/*      */     }
/*      */ 
/*  556 */     if ((l2 = transferToTrustedChannel(paramLong1, i, paramWritableByteChannel)) >= 0L) {
/*  557 */       return l2;
/*      */     }
/*      */ 
/*  560 */     return transferToArbitraryChannel(paramLong1, i, paramWritableByteChannel);
/*      */   }
/*      */ 
/*      */   private long transferFromFileChannel(FileChannelImpl paramFileChannelImpl, long paramLong1, long paramLong2)
/*      */     throws IOException
/*      */   {
/*  567 */     if (!paramFileChannelImpl.readable)
/*  568 */       throw new NonReadableChannelException();
/*  569 */     synchronized (paramFileChannelImpl.positionLock) {
/*  570 */       long l1 = paramFileChannelImpl.position();
/*  571 */       long l2 = Math.min(paramLong2, paramFileChannelImpl.size() - l1);
/*      */ 
/*  573 */       long l3 = l2;
/*  574 */       long l4 = l1;
/*  575 */       if (l3 > 0L) {
/*  576 */         l5 = Math.min(l3, 8388608L);
/*      */ 
/*  578 */         MappedByteBuffer localMappedByteBuffer = paramFileChannelImpl.map(FileChannel.MapMode.READ_ONLY, l4, l5);
/*      */         try {
/*  580 */           long l6 = write(localMappedByteBuffer, paramLong1);
/*  581 */           assert (l6 > 0L);
/*  582 */           l4 += l6;
/*  583 */           paramLong1 += l6;
/*  584 */           l3 -= l6;
/*      */ 
/*  591 */           unmap(localMappedByteBuffer);
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*  587 */           if (l3 == l2)
/*  588 */             throw localIOException;
/*      */         }
/*      */         finally {
/*  591 */           unmap(localMappedByteBuffer);
/*      */         }
/*      */       }
/*  594 */       long l5 = l2 - l3;
/*  595 */       paramFileChannelImpl.position(l1 + l5);
/*  596 */       return l5;
/*      */     }
/*      */   }
/*      */ 
/*      */   private long transferFromArbitraryChannel(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2)
/*      */     throws IOException
/*      */   {
/*  607 */     int i = (int)Math.min(paramLong2, 8192L);
/*  608 */     ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(i);
/*  609 */     long l1 = 0L;
/*  610 */     long l2 = paramLong1;
/*      */     try {
/*  612 */       Util.erase(localByteBuffer);
/*  613 */       while (l1 < paramLong2) {
/*  614 */         localByteBuffer.limit((int)Math.min(paramLong2 - l1, 8192L));
/*      */ 
/*  617 */         int j = paramReadableByteChannel.read(localByteBuffer);
/*  618 */         if (j <= 0)
/*      */           break;
/*  620 */         localByteBuffer.flip();
/*  621 */         int k = write(localByteBuffer, l2);
/*  622 */         l1 += k;
/*  623 */         if (k != j)
/*      */           break;
/*  625 */         l2 += k;
/*  626 */         localByteBuffer.clear();
/*      */       }
/*  628 */       return l1;
/*      */     } catch (IOException localIOException) {
/*  630 */       if (l1 > 0L)
/*  631 */         return l1;
/*  632 */       throw localIOException;
/*      */     } finally {
/*  634 */       Util.releaseTemporaryDirectBuffer(localByteBuffer);
/*      */     }
/*      */   }
/*      */ 
/*      */   public long transferFrom(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2)
/*      */     throws IOException
/*      */   {
/*  642 */     ensureOpen();
/*  643 */     if (!paramReadableByteChannel.isOpen())
/*  644 */       throw new ClosedChannelException();
/*  645 */     if (!this.writable)
/*  646 */       throw new NonWritableChannelException();
/*  647 */     if ((paramLong1 < 0L) || (paramLong2 < 0L))
/*  648 */       throw new IllegalArgumentException();
/*  649 */     if (paramLong1 > size())
/*  650 */       return 0L;
/*  651 */     if ((paramReadableByteChannel instanceof FileChannelImpl)) {
/*  652 */       return transferFromFileChannel((FileChannelImpl)paramReadableByteChannel, paramLong1, paramLong2);
/*      */     }
/*      */ 
/*  655 */     return transferFromArbitraryChannel(paramReadableByteChannel, paramLong1, paramLong2);
/*      */   }
/*      */ 
/*      */   public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/*  659 */     if (paramByteBuffer == null)
/*  660 */       throw new NullPointerException();
/*  661 */     if (paramLong < 0L)
/*  662 */       throw new IllegalArgumentException("Negative position");
/*  663 */     if (!this.readable)
/*  664 */       throw new NonReadableChannelException();
/*  665 */     ensureOpen();
/*  666 */     int i = 0;
/*  667 */     int j = -1;
/*      */     try {
/*  669 */       begin();
/*  670 */       j = this.threads.add();
/*      */       int k;
/*  671 */       if (!isOpen())
/*  672 */         return -1;
/*      */       do
/*  674 */         i = IOUtil.read(this.fd, paramByteBuffer, paramLong, this.nd, this.positionLock);
/*  675 */       while ((i == -3) && (isOpen()));
/*  676 */       return IOStatus.normalize(i);
/*      */     } finally {
/*  678 */       this.threads.remove(j);
/*  679 */       end(i > 0);
/*  680 */       if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError(); 
/*      */     }
/*      */   }
/*      */ 
/*      */   public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/*  685 */     if (paramByteBuffer == null)
/*  686 */       throw new NullPointerException();
/*  687 */     if (paramLong < 0L)
/*  688 */       throw new IllegalArgumentException("Negative position");
/*  689 */     if (!this.writable)
/*  690 */       throw new NonWritableChannelException();
/*  691 */     ensureOpen();
/*  692 */     int i = 0;
/*  693 */     int j = -1;
/*      */     try {
/*  695 */       begin();
/*  696 */       j = this.threads.add();
/*      */       int k;
/*  697 */       if (!isOpen())
/*  698 */         return -1;
/*      */       do
/*  700 */         i = IOUtil.write(this.fd, paramByteBuffer, paramLong, this.nd, this.positionLock);
/*  701 */       while ((i == -3) && (isOpen()));
/*  702 */       return IOStatus.normalize(i);
/*      */     } finally {
/*  704 */       this.threads.remove(j);
/*  705 */       end(i > 0);
/*  706 */       if ((!$assertionsDisabled) && (!IOStatus.check(i))) throw new AssertionError();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void unmap(MappedByteBuffer paramMappedByteBuffer)
/*      */   {
/*  769 */     Cleaner localCleaner = ((DirectBuffer)paramMappedByteBuffer).cleaner();
/*  770 */     if (localCleaner != null)
/*  771 */       localCleaner.clean();
/*      */   }
/*      */ 
/*      */   public MappedByteBuffer map(FileChannel.MapMode paramMapMode, long paramLong1, long paramLong2)
/*      */     throws IOException
/*      */   {
/*  781 */     ensureOpen();
/*  782 */     if (paramLong1 < 0L)
/*  783 */       throw new IllegalArgumentException("Negative position");
/*  784 */     if (paramLong2 < 0L)
/*  785 */       throw new IllegalArgumentException("Negative size");
/*  786 */     if (paramLong1 + paramLong2 < 0L)
/*  787 */       throw new IllegalArgumentException("Position + size overflow");
/*  788 */     if (paramLong2 > 2147483647L)
/*  789 */       throw new IllegalArgumentException("Size exceeds Integer.MAX_VALUE");
/*  790 */     int i = -1;
/*  791 */     if (paramMapMode == FileChannel.MapMode.READ_ONLY)
/*  792 */       i = 0;
/*  793 */     else if (paramMapMode == FileChannel.MapMode.READ_WRITE)
/*  794 */       i = 1;
/*  795 */     else if (paramMapMode == FileChannel.MapMode.PRIVATE)
/*  796 */       i = 2;
/*  797 */     assert (i >= 0);
/*  798 */     if ((paramMapMode != FileChannel.MapMode.READ_ONLY) && (!this.writable))
/*  799 */       throw new NonWritableChannelException();
/*  800 */     if (!this.readable) {
/*  801 */       throw new NonReadableChannelException();
/*      */     }
/*  803 */     long l1 = -1L;
/*  804 */     int j = -1;
/*      */     try {
/*  806 */       begin();
/*  807 */       j = this.threads.add();
/*  808 */       if (!isOpen())
/*  809 */         return null;
/*  810 */       if (size() < paramLong1 + paramLong2) {
/*  811 */         if (!this.writable) {
/*  812 */           throw new IOException("Channel not open for writing - cannot extend file to required size");
/*      */         }
/*      */         int k;
/*      */         do
/*      */         {
/*  817 */           k = this.nd.truncate(this.fd, paramLong1 + paramLong2);
/*  818 */         }while ((k == -3) && (isOpen()));
/*      */       }
/*  820 */       if (paramLong2 == 0L) {
/*  821 */         l1 = 0L;
/*      */ 
/*  823 */         FileDescriptor localFileDescriptor1 = new FileDescriptor();
/*      */         MappedByteBuffer localMappedByteBuffer2;
/*  824 */         if ((!this.writable) || (i == 0)) {
/*  825 */           return Util.newMappedByteBufferR(0, 0L, localFileDescriptor1, null);
/*      */         }
/*  827 */         return Util.newMappedByteBuffer(0, 0L, localFileDescriptor1, null);
/*      */       }
/*      */ 
/*  830 */       int m = (int)(paramLong1 % allocationGranularity);
/*  831 */       long l2 = paramLong1 - m;
/*  832 */       long l3 = paramLong2 + m;
/*      */       try
/*      */       {
/*  835 */         l1 = map0(i, l2, l3);
/*      */       }
/*      */       catch (OutOfMemoryError localOutOfMemoryError1)
/*      */       {
/*  839 */         System.gc();
/*      */         try {
/*  841 */           Thread.sleep(100L);
/*      */         } catch (InterruptedException localInterruptedException) {
/*  843 */           Thread.currentThread().interrupt();
/*      */         }
/*      */         try {
/*  846 */           l1 = map0(i, l2, l3);
/*      */         }
/*      */         catch (OutOfMemoryError localOutOfMemoryError2) {
/*  849 */           throw new IOException("Map failed", localOutOfMemoryError2);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */       FileDescriptor localFileDescriptor2;
/*      */       try
/*      */       {
/*  857 */         localFileDescriptor2 = this.nd.duplicateForMapping(this.fd);
/*      */       } catch (IOException localIOException) {
/*  859 */         unmap0(l1, l3);
/*  860 */         throw localIOException;
/*      */       }
/*      */ 
/*  863 */       assert (IOStatus.checkAll(l1));
/*  864 */       assert (l1 % allocationGranularity == 0L);
/*  865 */       int n = (int)paramLong2;
/*  866 */       Unmapper localUnmapper = new Unmapper(l1, l3, n, localFileDescriptor2, null);
/*      */       MappedByteBuffer localMappedByteBuffer3;
/*  867 */       if ((!this.writable) || (i == 0)) {
/*  868 */         return Util.newMappedByteBufferR(n, l1 + m, localFileDescriptor2, localUnmapper);
/*      */       }
/*      */ 
/*  873 */       return Util.newMappedByteBuffer(n, l1 + m, localFileDescriptor2, localUnmapper);
/*      */     }
/*      */     finally
/*      */     {
/*  879 */       this.threads.remove(j);
/*  880 */       end(IOStatus.checkAll(l1));
/*      */     }
/*      */   }
/*      */ 
/*      */   public static JavaNioAccess.BufferPool getMappedBufferPool()
/*      */   {
/*  889 */     return new JavaNioAccess.BufferPool()
/*      */     {
/*      */       public String getName() {
/*  892 */         return "mapped";
/*      */       }
/*      */ 
/*      */       public long getCount() {
/*  896 */         return FileChannelImpl.Unmapper.count;
/*      */       }
/*      */ 
/*      */       public long getTotalCapacity() {
/*  900 */         return FileChannelImpl.Unmapper.totalCapacity;
/*      */       }
/*      */ 
/*      */       public long getMemoryUsed() {
/*  904 */         return FileChannelImpl.Unmapper.totalSize;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private static boolean isSharedFileLockTable()
/*      */   {
/*  927 */     if (!propertyChecked) {
/*  928 */       synchronized (FileChannelImpl.class) {
/*  929 */         if (!propertyChecked) {
/*  930 */           String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.ch.disableSystemWideOverlappingFileLockCheck"));
/*      */ 
/*  933 */           isSharedFileLockTable = (str == null) || (str.equals("false"));
/*  934 */           propertyChecked = true;
/*      */         }
/*      */       }
/*      */     }
/*  938 */     return isSharedFileLockTable;
/*      */   }
/*      */ 
/*      */   private FileLockTable fileLockTable() throws IOException {
/*  942 */     if (this.fileLockTable == null) {
/*  943 */       synchronized (this) {
/*  944 */         if (this.fileLockTable == null) {
/*  945 */           if (isSharedFileLockTable()) {
/*  946 */             int i = this.threads.add();
/*      */             try {
/*  948 */               ensureOpen();
/*  949 */               this.fileLockTable = FileLockTable.newSharedFileLockTable(this, this.fd);
/*      */             } finally {
/*  951 */               this.threads.remove(i);
/*      */             }
/*      */           } else {
/*  954 */             this.fileLockTable = new SimpleFileLockTable();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  959 */     return this.fileLockTable;
/*      */   }
/*      */ 
/*      */   public FileLock lock(long paramLong1, long paramLong2, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  965 */     ensureOpen();
/*  966 */     if ((paramBoolean) && (!this.readable))
/*  967 */       throw new NonReadableChannelException();
/*  968 */     if ((!paramBoolean) && (!this.writable))
/*  969 */       throw new NonWritableChannelException();
/*  970 */     Object localObject1 = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
/*  971 */     FileLockTable localFileLockTable = fileLockTable();
/*  972 */     localFileLockTable.add((FileLock)localObject1);
/*  973 */     boolean bool = false;
/*  974 */     int i = -1;
/*      */     try {
/*  976 */       begin();
/*  977 */       i = this.threads.add();
/*  978 */       if (!isOpen())
/*  979 */         return null;
/*      */       int j;
/*      */       do
/*  982 */         j = this.nd.lock(this.fd, true, paramLong1, paramLong2, paramBoolean);
/*  983 */       while ((j == 2) && (isOpen()));
/*  984 */       if (isOpen()) {
/*  985 */         if (j == 1) {
/*  986 */           assert (paramBoolean);
/*  987 */           FileLockImpl localFileLockImpl = new FileLockImpl(this, paramLong1, paramLong2, false);
/*      */ 
/*  989 */           localFileLockTable.replace((FileLock)localObject1, localFileLockImpl);
/*  990 */           localObject1 = localFileLockImpl;
/*      */         }
/*  992 */         bool = true;
/*      */       }
/*      */     } finally {
/*  995 */       if (!bool)
/*  996 */         localFileLockTable.remove((FileLock)localObject1);
/*  997 */       this.threads.remove(i);
/*      */       try {
/*  999 */         end(bool);
/*      */       } catch (ClosedByInterruptException localClosedByInterruptException3) {
/* 1001 */         throw new FileLockInterruptionException();
/*      */       }
/*      */     }
/* 1004 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 1010 */     ensureOpen();
/* 1011 */     if ((paramBoolean) && (!this.readable))
/* 1012 */       throw new NonReadableChannelException();
/* 1013 */     if ((!paramBoolean) && (!this.writable))
/* 1014 */       throw new NonWritableChannelException();
/* 1015 */     FileLockImpl localFileLockImpl = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
/* 1016 */     FileLockTable localFileLockTable = fileLockTable();
/* 1017 */     localFileLockTable.add(localFileLockImpl);
/*      */ 
/* 1020 */     int j = this.threads.add();
/*      */     try {
/*      */       int i;
/*      */       try { ensureOpen();
/* 1024 */         i = this.nd.lock(this.fd, false, paramLong1, paramLong2, paramBoolean);
/*      */       } catch (IOException localIOException) {
/* 1026 */         localFileLockTable.remove(localFileLockImpl);
/* 1027 */         throw localIOException;
/*      */       }
/*      */       Object localObject1;
/* 1029 */       if (i == -1) {
/* 1030 */         localFileLockTable.remove(localFileLockImpl);
/* 1031 */         return null;
/*      */       }
/* 1033 */       if (i == 1) {
/* 1034 */         assert (paramBoolean);
/* 1035 */         localObject1 = new FileLockImpl(this, paramLong1, paramLong2, false);
/*      */ 
/* 1037 */         localFileLockTable.replace(localFileLockImpl, (FileLock)localObject1);
/* 1038 */         return localObject1;
/*      */       }
/* 1040 */       return localFileLockImpl;
/*      */     } finally {
/* 1042 */       this.threads.remove(j);
/*      */     }
/*      */   }
/*      */ 
/*      */   void release(FileLockImpl paramFileLockImpl) throws IOException {
/* 1047 */     int i = this.threads.add();
/*      */     try {
/* 1049 */       ensureOpen();
/* 1050 */       this.nd.release(this.fd, paramFileLockImpl.position(), paramFileLockImpl.size());
/*      */     } finally {
/* 1052 */       this.threads.remove(i);
/*      */     }
/* 1054 */     assert (this.fileLockTable != null);
/* 1055 */     this.fileLockTable.remove(paramFileLockImpl);
/*      */   }
/*      */ 
/*      */   private native long map0(int paramInt, long paramLong1, long paramLong2)
/*      */     throws IOException;
/*      */ 
/*      */   private static native int unmap0(long paramLong1, long paramLong2);
/*      */ 
/*      */   private native long transferTo0(int paramInt1, long paramLong1, long paramLong2, int paramInt2);
/*      */ 
/*      */   private native long position0(FileDescriptor paramFileDescriptor, long paramLong);
/*      */ 
/*      */   private static native long initIDs();
/*      */ 
/*      */   static
/*      */   {
/*  375 */     transferSupported = true;
/*      */ 
/*  380 */     pipeSupported = true;
/*      */ 
/*  385 */     fileSupported = true;
/*      */ 
/* 1132 */     Util.load();
/*      */   }
/*      */ 
/*      */   private static class SimpleFileLockTable extends FileLockTable
/*      */   {
/* 1066 */     private final List<FileLock> lockList = new ArrayList(2);
/*      */ 
/*      */     private void checkList(long paramLong1, long paramLong2)
/*      */       throws OverlappingFileLockException
/*      */     {
/* 1074 */       assert (Thread.holdsLock(this.lockList));
/* 1075 */       for (FileLock localFileLock : this.lockList)
/* 1076 */         if (localFileLock.overlaps(paramLong1, paramLong2))
/* 1077 */           throw new OverlappingFileLockException();
/*      */     }
/*      */ 
/*      */     public void add(FileLock paramFileLock)
/*      */       throws OverlappingFileLockException
/*      */     {
/* 1083 */       synchronized (this.lockList) {
/* 1084 */         checkList(paramFileLock.position(), paramFileLock.size());
/* 1085 */         this.lockList.add(paramFileLock);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void remove(FileLock paramFileLock) {
/* 1090 */       synchronized (this.lockList) {
/* 1091 */         this.lockList.remove(paramFileLock);
/*      */       }
/*      */     }
/*      */ 
/*      */     public List<FileLock> removeAll() {
/* 1096 */       synchronized (this.lockList) {
/* 1097 */         ArrayList localArrayList = new ArrayList(this.lockList);
/* 1098 */         this.lockList.clear();
/* 1099 */         return localArrayList;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void replace(FileLock paramFileLock1, FileLock paramFileLock2) {
/* 1104 */       synchronized (this.lockList) {
/* 1105 */         this.lockList.remove(paramFileLock1);
/* 1106 */         this.lockList.add(paramFileLock2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Unmapper
/*      */     implements Runnable
/*      */   {
/*  717 */     private static final NativeDispatcher nd = new FileDispatcherImpl();
/*      */     static volatile int count;
/*      */     static volatile long totalSize;
/*      */     static volatile long totalCapacity;
/*      */     private volatile long address;
/*      */     private final long size;
/*      */     private final int cap;
/*      */     private final FileDescriptor fd;
/*      */ 
/*      */     private Unmapper(long paramLong1, long paramLong2, int paramInt, FileDescriptor paramFileDescriptor)
/*      */     {
/*  732 */       assert (paramLong1 != 0L);
/*  733 */       this.address = paramLong1;
/*  734 */       this.size = paramLong2;
/*  735 */       this.cap = paramInt;
/*  736 */       this.fd = paramFileDescriptor;
/*      */ 
/*  738 */       synchronized (Unmapper.class) {
/*  739 */         count += 1;
/*  740 */         totalSize += paramLong2;
/*  741 */         totalCapacity += paramInt;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void run() {
/*  746 */       if (this.address == 0L)
/*  747 */         return;
/*  748 */       FileChannelImpl.unmap0(this.address, this.size);
/*  749 */       this.address = 0L;
/*      */ 
/*  752 */       if (this.fd.valid()) {
/*      */         try {
/*  754 */           nd.close(this.fd);
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*      */         }
/*      */       }
/*  760 */       synchronized (Unmapper.class) {
/*  761 */         count -= 1;
/*  762 */         totalSize -= this.size;
/*  763 */         totalCapacity -= this.cap;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.FileChannelImpl
 * JD-Core Version:    0.6.2
 */