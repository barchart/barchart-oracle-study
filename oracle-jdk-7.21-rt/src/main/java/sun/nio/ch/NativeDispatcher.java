/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.io.FileDescriptor;
/*    */ import java.io.IOException;
/*    */ 
/*    */ abstract class NativeDispatcher
/*    */ {
/*    */   abstract int read(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   int pread(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2, Object paramObject)
/*    */     throws IOException
/*    */   {
/* 44 */     throw new IOException("Operation Unsupported");
/*    */   }
/*    */ 
/*    */   abstract long readv(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   abstract int write(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   int pwrite(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2, Object paramObject)
/*    */     throws IOException
/*    */   {
/* 56 */     throw new IOException("Operation Unsupported");
/*    */   }
/*    */ 
/*    */   abstract long writev(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   abstract void close(FileDescriptor paramFileDescriptor)
/*    */     throws IOException;
/*    */ 
/*    */   void preClose(FileDescriptor paramFileDescriptor)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.NativeDispatcher
 * JD-Core Version:    0.6.2
 */