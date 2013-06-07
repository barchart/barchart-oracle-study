/*    */ package java.security.spec;
/*    */ 
/*    */ public class X509EncodedKeySpec extends EncodedKeySpec
/*    */ {
/*    */   public X509EncodedKeySpec(byte[] paramArrayOfByte)
/*    */   {
/* 64 */     super(paramArrayOfByte);
/*    */   }
/*    */ 
/*    */   public byte[] getEncoded()
/*    */   {
/* 74 */     return super.getEncoded();
/*    */   }
/*    */ 
/*    */   public final String getFormat()
/*    */   {
/* 84 */     return "X.509";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.spec.X509EncodedKeySpec
 * JD-Core Version:    0.6.2
 */