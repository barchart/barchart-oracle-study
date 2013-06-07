/*    */ package java.nio;
/*    */ 
/*    */ public final class ByteOrder
/*    */ {
/*    */   private String name;
/* 50 */   public static final ByteOrder BIG_ENDIAN = new ByteOrder("BIG_ENDIAN");
/*    */ 
/* 58 */   public static final ByteOrder LITTLE_ENDIAN = new ByteOrder("LITTLE_ENDIAN");
/*    */ 
/*    */   private ByteOrder(String paramString)
/*    */   {
/* 42 */     this.name = paramString;
/*    */   }
/*    */ 
/*    */   public static ByteOrder nativeOrder()
/*    */   {
/* 73 */     return Bits.byteOrder();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 86 */     return this.name;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.ByteOrder
 * JD-Core Version:    0.6.2
 */