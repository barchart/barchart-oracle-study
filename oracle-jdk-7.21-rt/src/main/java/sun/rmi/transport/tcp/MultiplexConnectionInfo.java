/*    */ package sun.rmi.transport.tcp;
/*    */ 
/*    */ class MultiplexConnectionInfo
/*    */ {
/*    */   int id;
/* 39 */   MultiplexInputStream in = null;
/*    */ 
/* 42 */   MultiplexOutputStream out = null;
/*    */ 
/* 45 */   boolean closed = false;
/*    */ 
/*    */   MultiplexConnectionInfo(int paramInt)
/*    */   {
/* 53 */     this.id = paramInt;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.tcp.MultiplexConnectionInfo
 * JD-Core Version:    0.6.2
 */