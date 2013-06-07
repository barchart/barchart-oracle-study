/*    */ package sun.security.krb5.internal.util;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.ByteOrder;
/*    */ 
/*    */ public class KrbDataInputStream extends BufferedInputStream
/*    */ {
/* 46 */   private boolean bigEndian = true;
/*    */ 
/*    */   public void setNativeByteOrder() {
/* 49 */     if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN))
/*    */     {
/* 51 */       this.bigEndian = true;
/*    */     }
/* 53 */     else this.bigEndian = false; 
/*    */   }
/*    */ 
/*    */   public KrbDataInputStream(InputStream paramInputStream)
/*    */   {
/* 57 */     super(paramInputStream);
/*    */   }
/*    */ 
/*    */   public int read(int paramInt)
/*    */     throws IOException
/*    */   {
/* 66 */     byte[] arrayOfByte = new byte[paramInt];
/* 67 */     read(arrayOfByte, 0, paramInt);
/* 68 */     int i = 0;
/* 69 */     for (int j = 0; j < paramInt; j++) {
/* 70 */       if (this.bigEndian)
/* 71 */         i |= (arrayOfByte[j] & 0xFF) << (paramInt - j - 1) * 8;
/*    */       else {
/* 73 */         i |= (arrayOfByte[j] & 0xFF) << j * 8;
/*    */       }
/*    */     }
/* 76 */     return i;
/*    */   }
/*    */ 
/*    */   public int readVersion() throws IOException
/*    */   {
/* 81 */     int i = (read() & 0xFF) << 8;
/* 82 */     return i | read() & 0xFF;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.util.KrbDataInputStream
 * JD-Core Version:    0.6.2
 */