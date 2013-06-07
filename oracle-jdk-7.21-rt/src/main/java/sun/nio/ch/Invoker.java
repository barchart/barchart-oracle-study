/*     */ package sun.nio.ch;
/*     */ 
/*     */ import java.nio.channels.AsynchronousChannel;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.nio.channels.ShutdownChannelGroupException;
/*     */ import java.security.AccessController;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import sun.security.action.GetIntegerAction;
/*     */ 
/*     */ class Invoker
/*     */ {
/*  43 */   private static final int maxHandlerInvokeCount = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.nio.ch.maxCompletionHandlersOnStack", 16))).intValue();
/*     */ 
/*  71 */   private static final ThreadLocal<GroupAndInvokeCount> myGroupAndInvokeCount = new ThreadLocal()
/*     */   {
/*     */     protected Invoker.GroupAndInvokeCount initialValue() {
/*  74 */       return null;
/*     */     }
/*  71 */   };
/*     */ 
/*     */   static void bindToGroup(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
/*     */   {
/*  82 */     myGroupAndInvokeCount.set(new GroupAndInvokeCount(paramAsynchronousChannelGroupImpl));
/*     */   }
/*     */ 
/*     */   static GroupAndInvokeCount getGroupAndInvokeCount()
/*     */   {
/*  89 */     return (GroupAndInvokeCount)myGroupAndInvokeCount.get();
/*     */   }
/*     */ 
/*     */   static boolean isBoundToAnyGroup()
/*     */   {
/*  96 */     return myGroupAndInvokeCount.get() != null;
/*     */   }
/*     */ 
/*     */   static boolean mayInvokeDirect(GroupAndInvokeCount paramGroupAndInvokeCount, AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
/*     */   {
/* 107 */     if ((paramGroupAndInvokeCount != null) && (paramGroupAndInvokeCount.group() == paramAsynchronousChannelGroupImpl) && (paramGroupAndInvokeCount.invokeCount() < maxHandlerInvokeCount))
/*     */     {
/* 111 */       return true;
/*     */     }
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   static <V, A> void invokeUnchecked(CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, V paramV, Throwable paramThrowable)
/*     */   {
/* 125 */     if (paramThrowable == null)
/* 126 */       paramCompletionHandler.completed(paramV, paramA);
/*     */     else {
/* 128 */       paramCompletionHandler.failed(paramThrowable, paramA);
/*     */     }
/*     */ 
/* 132 */     Thread.interrupted();
/*     */   }
/*     */ 
/*     */   static <V, A> void invokeDirect(GroupAndInvokeCount paramGroupAndInvokeCount, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, V paramV, Throwable paramThrowable)
/*     */   {
/* 144 */     paramGroupAndInvokeCount.incrementInvokeCount();
/* 145 */     invokeUnchecked(paramCompletionHandler, paramA, paramV, paramThrowable);
/*     */   }
/*     */ 
/*     */   static <V, A> void invoke(AsynchronousChannel paramAsynchronousChannel, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, V paramV, Throwable paramThrowable)
/*     */   {
/* 159 */     int i = 0;
/* 160 */     int j = 0;
/* 161 */     GroupAndInvokeCount localGroupAndInvokeCount = (GroupAndInvokeCount)myGroupAndInvokeCount.get();
/* 162 */     if (localGroupAndInvokeCount != null) {
/* 163 */       if (localGroupAndInvokeCount.group() == ((Groupable)paramAsynchronousChannel).group())
/* 164 */         j = 1;
/* 165 */       if ((j != 0) && (localGroupAndInvokeCount.invokeCount() < maxHandlerInvokeCount))
/*     */       {
/* 169 */         i = 1;
/*     */       }
/*     */     }
/* 172 */     if (i != 0)
/* 173 */       invokeDirect(localGroupAndInvokeCount, paramCompletionHandler, paramA, paramV, paramThrowable);
/*     */     else
/*     */       try {
/* 176 */         invokeIndirectly(paramAsynchronousChannel, paramCompletionHandler, paramA, paramV, paramThrowable);
/*     */       }
/*     */       catch (RejectedExecutionException localRejectedExecutionException)
/*     */       {
/* 180 */         if (j != 0) {
/* 181 */           invokeDirect(localGroupAndInvokeCount, paramCompletionHandler, paramA, paramV, paramThrowable);
/*     */         }
/*     */         else
/* 184 */           throw new ShutdownChannelGroupException();
/*     */       }
/*     */   }
/*     */ 
/*     */   static <V, A> void invokeIndirectly(AsynchronousChannel paramAsynchronousChannel, CompletionHandler<V, ? super A> paramCompletionHandler, final A paramA, final V paramV, final Throwable paramThrowable)
/*     */   {
/*     */     try
/*     */     {
/* 200 */       ((Groupable)paramAsynchronousChannel).group().executeOnPooledThread(new Runnable() {
/*     */         public void run() {
/* 202 */           Invoker.GroupAndInvokeCount localGroupAndInvokeCount = (Invoker.GroupAndInvokeCount)Invoker.myGroupAndInvokeCount.get();
/*     */ 
/* 204 */           if (localGroupAndInvokeCount != null)
/* 205 */             localGroupAndInvokeCount.setInvokeCount(1);
/* 206 */           Invoker.invokeUnchecked(this.val$handler, paramA, paramV, paramThrowable);
/*     */         } } );
/*     */     }
/*     */     catch (RejectedExecutionException localRejectedExecutionException) {
/* 210 */       throw new ShutdownChannelGroupException();
/*     */     }
/*     */   }
/*     */ 
/*     */   static <V, A> void invokeIndirectly(CompletionHandler<V, ? super A> paramCompletionHandler, final A paramA, final V paramV, final Throwable paramThrowable, Executor paramExecutor)
/*     */   {
/*     */     try
/*     */     {
/* 224 */       paramExecutor.execute(new Runnable() {
/*     */         public void run() {
/* 226 */           Invoker.invokeUnchecked(this.val$handler, paramA, paramV, paramThrowable);
/*     */         } } );
/*     */     }
/*     */     catch (RejectedExecutionException localRejectedExecutionException) {
/* 230 */       throw new ShutdownChannelGroupException();
/*     */     }
/*     */   }
/*     */ 
/*     */   static void invokeOnThreadInThreadPool(Groupable paramGroupable, Runnable paramRunnable)
/*     */   {
/* 243 */     GroupAndInvokeCount localGroupAndInvokeCount = (GroupAndInvokeCount)myGroupAndInvokeCount.get();
/* 244 */     AsynchronousChannelGroupImpl localAsynchronousChannelGroupImpl = paramGroupable.group();
/*     */     int i;
/* 245 */     if (localGroupAndInvokeCount == null)
/* 246 */       i = 0;
/*     */     else
/* 248 */       i = localGroupAndInvokeCount.group == localAsynchronousChannelGroupImpl ? 1 : 0;
/*     */     try
/*     */     {
/* 251 */       if (i != 0)
/* 252 */         paramRunnable.run();
/*     */       else
/* 254 */         localAsynchronousChannelGroupImpl.executeOnPooledThread(paramRunnable);
/*     */     }
/*     */     catch (RejectedExecutionException localRejectedExecutionException) {
/* 257 */       throw new ShutdownChannelGroupException();
/*     */     }
/*     */   }
/*     */ 
/*     */   static <V, A> void invokeUnchecked(PendingFuture<V, A> paramPendingFuture)
/*     */   {
/* 266 */     assert (paramPendingFuture.isDone());
/* 267 */     CompletionHandler localCompletionHandler = paramPendingFuture.handler();
/* 268 */     if (localCompletionHandler != null)
/* 269 */       invokeUnchecked(localCompletionHandler, paramPendingFuture.attachment(), paramPendingFuture.value(), paramPendingFuture.exception());
/*     */   }
/*     */ 
/*     */   static <V, A> void invoke(PendingFuture<V, A> paramPendingFuture)
/*     */   {
/* 282 */     assert (paramPendingFuture.isDone());
/* 283 */     CompletionHandler localCompletionHandler = paramPendingFuture.handler();
/* 284 */     if (localCompletionHandler != null)
/* 285 */       invoke(paramPendingFuture.channel(), localCompletionHandler, paramPendingFuture.attachment(), paramPendingFuture.value(), paramPendingFuture.exception());
/*     */   }
/*     */ 
/*     */   static <V, A> void invokeIndirectly(PendingFuture<V, A> paramPendingFuture)
/*     */   {
/* 298 */     assert (paramPendingFuture.isDone());
/* 299 */     CompletionHandler localCompletionHandler = paramPendingFuture.handler();
/* 300 */     if (localCompletionHandler != null)
/* 301 */       invokeIndirectly(paramPendingFuture.channel(), localCompletionHandler, paramPendingFuture.attachment(), paramPendingFuture.value(), paramPendingFuture.exception());
/*     */   }
/*     */ 
/*     */   static class GroupAndInvokeCount
/*     */   {
/*     */     private final AsynchronousChannelGroupImpl group;
/*     */     private int handlerInvokeCount;
/*     */ 
/*     */     GroupAndInvokeCount(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
/*     */     {
/*  53 */       this.group = paramAsynchronousChannelGroupImpl;
/*     */     }
/*     */     AsynchronousChannelGroupImpl group() {
/*  56 */       return this.group;
/*     */     }
/*     */     int invokeCount() {
/*  59 */       return this.handlerInvokeCount;
/*     */     }
/*     */     void setInvokeCount(int paramInt) {
/*  62 */       this.handlerInvokeCount = paramInt;
/*     */     }
/*     */     void resetInvokeCount() {
/*  65 */       this.handlerInvokeCount = 0;
/*     */     }
/*     */     void incrementInvokeCount() {
/*  68 */       this.handlerInvokeCount += 1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.Invoker
 * JD-Core Version:    0.6.2
 */