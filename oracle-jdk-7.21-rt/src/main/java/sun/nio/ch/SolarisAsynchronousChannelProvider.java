/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.AsynchronousChannelGroup;
/*    */ import java.nio.channels.AsynchronousServerSocketChannel;
/*    */ import java.nio.channels.AsynchronousSocketChannel;
/*    */ import java.nio.channels.IllegalChannelGroupException;
/*    */ import java.nio.channels.spi.AsynchronousChannelProvider;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ 
/*    */ public class SolarisAsynchronousChannelProvider extends AsynchronousChannelProvider
/*    */ {
/*    */   private static volatile SolarisEventPort defaultEventPort;
/*    */ 
/*    */   private SolarisEventPort defaultEventPort()
/*    */     throws IOException
/*    */   {
/* 40 */     if (defaultEventPort == null) {
/* 41 */       synchronized (SolarisAsynchronousChannelProvider.class) {
/* 42 */         if (defaultEventPort == null) {
/* 43 */           defaultEventPort = new SolarisEventPort(this, ThreadPool.getDefault()).start();
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 48 */     return defaultEventPort;
/*    */   }
/*    */ 
/*    */   public AsynchronousChannelGroup openAsynchronousChannelGroup(int paramInt, ThreadFactory paramThreadFactory)
/*    */     throws IOException
/*    */   {
/* 58 */     return new SolarisEventPort(this, ThreadPool.create(paramInt, paramThreadFactory)).start();
/*    */   }
/*    */ 
/*    */   public AsynchronousChannelGroup openAsynchronousChannelGroup(ExecutorService paramExecutorService, int paramInt)
/*    */     throws IOException
/*    */   {
/* 65 */     return new SolarisEventPort(this, ThreadPool.wrap(paramExecutorService, paramInt)).start();
/*    */   }
/*    */ 
/*    */   private SolarisEventPort toEventPort(AsynchronousChannelGroup paramAsynchronousChannelGroup)
/*    */     throws IOException
/*    */   {
/* 71 */     if (paramAsynchronousChannelGroup == null) {
/* 72 */       return defaultEventPort();
/*    */     }
/* 74 */     if (!(paramAsynchronousChannelGroup instanceof SolarisEventPort))
/* 75 */       throw new IllegalChannelGroupException();
/* 76 */     return (SolarisEventPort)paramAsynchronousChannelGroup;
/*    */   }
/*    */ 
/*    */   public AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(AsynchronousChannelGroup paramAsynchronousChannelGroup)
/*    */     throws IOException
/*    */   {
/* 84 */     return new UnixAsynchronousServerSocketChannelImpl(toEventPort(paramAsynchronousChannelGroup));
/*    */   }
/*    */ 
/*    */   public AsynchronousSocketChannel openAsynchronousSocketChannel(AsynchronousChannelGroup paramAsynchronousChannelGroup)
/*    */     throws IOException
/*    */   {
/* 91 */     return new UnixAsynchronousSocketChannelImpl(toEventPort(paramAsynchronousChannelGroup));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SolarisAsynchronousChannelProvider
 * JD-Core Version:    0.6.2
 */