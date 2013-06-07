/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.io.FileDescriptor;
/*    */ import java.io.IOException;
/*    */ 
/*    */ class SocketDispatcher extends NativeDispatcher
/*    */ {
/*    */   int read(FileDescriptor paramFileDescriptor, long paramLong, int paramInt)
/*    */     throws IOException
/*    */   {
/* 39 */     return FileDispatcherImpl.read0(paramFileDescriptor, paramLong, paramInt);
/*    */   }
/*    */ 
/*    */   long readv(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
/* 43 */     return FileDispatcherImpl.readv0(paramFileDescriptor, paramLong, paramInt);
/*    */   }
/*    */ 
/*    */   int write(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
/* 47 */     return FileDispatcherImpl.write0(paramFileDescriptor, paramLong, paramInt);
/*    */   }
/*    */ 
/*    */   long writev(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException {
/* 51 */     return FileDispatcherImpl.writev0(paramFileDescriptor, paramLong, paramInt);
/*    */   }
/*    */ 
/*    */   void close(FileDescriptor paramFileDescriptor) throws IOException {
/* 55 */     FileDispatcherImpl.close0(paramFileDescriptor);
/*    */   }
/*    */ 
/*    */   void preClose(FileDescriptor paramFileDescriptor) throws IOException {
/* 59 */     FileDispatcherImpl.preClose0(paramFileDescriptor);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SocketDispatcher
 * JD-Core Version:    0.6.2
 */