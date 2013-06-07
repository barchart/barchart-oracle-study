/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ShutdownChannelGroupException;
/*     */ import java.nio.channels.spi.AsynchronousChannelProvider;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ 
/*     */ abstract class Port extends AsynchronousChannelGroupImpl
/*     */ {
/*     */   static final short POLLIN = 1;
/*     */   static final short POLLOUT = 4;
/*     */   static final short POLLERR = 8;
/*     */   static final short POLLHUP = 16;
/*  56 */   protected final ReadWriteLock fdToChannelLock = new ReentrantReadWriteLock();
/*  57 */   protected final Map<Integer, PollableChannel> fdToChannel = new HashMap();
/*     */ 
/*     */   Port(AsynchronousChannelProvider paramAsynchronousChannelProvider, ThreadPool paramThreadPool)
/*     */   {
/*  62 */     super(paramAsynchronousChannelProvider, paramThreadPool);
/*     */   }
/*     */ 
/*     */   final void register(int paramInt, PollableChannel paramPollableChannel)
/*     */   {
/*  69 */     this.fdToChannelLock.writeLock().lock();
/*     */     try {
/*  71 */       if (isShutdown())
/*  72 */         throw new ShutdownChannelGroupException();
/*  73 */       this.fdToChannel.put(Integer.valueOf(paramInt), paramPollableChannel);
/*     */     } finally {
/*  75 */       this.fdToChannelLock.writeLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   final void unregister(int paramInt)
/*     */   {
/*  83 */     int i = 0;
/*     */ 
/*  85 */     this.fdToChannelLock.writeLock().lock();
/*     */     try {
/*  87 */       this.fdToChannel.remove(Integer.valueOf(paramInt));
/*     */ 
/*  90 */       if (this.fdToChannel.isEmpty())
/*  91 */         i = 1;
/*     */     }
/*     */     finally {
/*  94 */       this.fdToChannelLock.writeLock().unlock();
/*     */     }
/*     */ 
/*  98 */     if ((i != 0) && (isShutdown()))
/*     */       try {
/* 100 */         shutdownNow();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   abstract void startPoll(int paramInt1, int paramInt2);
/*     */ 
/*     */   final boolean isEmpty()
/*     */   {
/* 112 */     this.fdToChannelLock.writeLock().lock();
/*     */     try {
/* 114 */       return this.fdToChannel.isEmpty();
/*     */     } finally {
/* 116 */       this.fdToChannelLock.writeLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   final Object attachForeignChannel(final Channel paramChannel, FileDescriptor paramFileDescriptor)
/*     */   {
/* 122 */     int i = IOUtil.fdVal(paramFileDescriptor);
/* 123 */     register(i, new PollableChannel() {
/*     */       public void onEvent(int paramAnonymousInt, boolean paramAnonymousBoolean) {
/*     */       }
/* 126 */       public void close() throws IOException { paramChannel.close(); }
/*     */ 
/*     */     });
/* 129 */     return Integer.valueOf(i);
/*     */   }
/*     */ 
/*     */   final void detachForeignChannel(Object paramObject)
/*     */   {
/* 134 */     unregister(((Integer)paramObject).intValue());
/*     */   }
/*     */ 
/*     */   final void closeAllChannels()
/*     */   {
/* 144 */     PollableChannel[] arrayOfPollableChannel = new PollableChannel[''];
/*     */     int i;
/*     */     do
/*     */     {
/* 148 */       this.fdToChannelLock.writeLock().lock();
/* 149 */       i = 0;
/*     */       try {
/* 151 */         for (Integer localInteger : this.fdToChannel.keySet()) {
/* 152 */           arrayOfPollableChannel[(i++)] = ((PollableChannel)this.fdToChannel.get(localInteger));
/* 153 */           if (i >= 128) break;
/*     */         }
/*     */       }
/*     */       finally {
/* 157 */         this.fdToChannelLock.writeLock().unlock();
/*     */       }
/*     */ 
/* 161 */       for (int j = 0; j < i; j++)
/*     */         try {
/* 163 */           arrayOfPollableChannel[j].close(); } catch (IOException localIOException) {
/*     */         }
/*     */     }
/* 166 */     while (i > 0);
/*     */   }
/*     */ 
/*     */   static abstract interface PollableChannel extends Closeable
/*     */   {
/*     */     public abstract void onEvent(int paramInt, boolean paramBoolean);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.Port
 * JD-Core Version:    0.6.2
 */