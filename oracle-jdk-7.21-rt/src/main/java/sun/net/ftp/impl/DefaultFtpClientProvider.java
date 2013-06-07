/*    */ package sun.net.ftp.impl;
/*    */ 
/*    */ import sun.net.ftp.FtpClientProvider;
/*    */ 
/*    */ public class DefaultFtpClientProvider extends FtpClientProvider
/*    */ {
/*    */   public sun.net.ftp.FtpClient createFtpClient()
/*    */   {
/* 35 */     return FtpClient.create();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.ftp.impl.DefaultFtpClientProvider
 * JD-Core Version:    0.6.2
 */