/*    */ package sun.security.krb5.internal;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class NetClient
/*    */ {
/*    */   public static NetClient getInstance(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*    */     throws IOException
/*    */   {
/* 40 */     if (paramString1.equals("TCP")) {
/* 41 */       return new TCPClient(paramString2, paramInt1, paramInt2);
/*    */     }
/* 43 */     return new UDPClient(paramString2, paramInt1, paramInt2);
/*    */   }
/*    */ 
/*    */   public abstract void send(byte[] paramArrayOfByte)
/*    */     throws IOException;
/*    */ 
/*    */   public abstract byte[] receive()
/*    */     throws IOException;
/*    */ 
/*    */   public abstract void close()
/*    */     throws IOException;
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.NetClient
 * JD-Core Version:    0.6.2
 */