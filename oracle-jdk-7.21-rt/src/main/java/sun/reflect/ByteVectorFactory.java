/*    */ package sun.reflect;
/*    */ 
/*    */ class ByteVectorFactory
/*    */ {
/*    */   static ByteVector create()
/*    */   {
/* 30 */     return new ByteVectorImpl();
/*    */   }
/*    */ 
/*    */   static ByteVector create(int paramInt) {
/* 34 */     return new ByteVectorImpl(paramInt);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.ByteVectorFactory
 * JD-Core Version:    0.6.2
 */