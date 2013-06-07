/*    */ package sun.security.util;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ public class ByteArrayTagOrder
/*    */   implements Comparator<byte[]>
/*    */ {
/*    */   public final int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*    */   {
/* 57 */     return (paramArrayOfByte1[0] | 0x20) - (paramArrayOfByte2[0] | 0x20);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.util.ByteArrayTagOrder
 * JD-Core Version:    0.6.2
 */