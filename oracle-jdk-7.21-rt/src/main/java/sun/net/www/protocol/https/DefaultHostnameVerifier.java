/*    */ package sun.net.www.protocol.https;
/*    */ 
/*    */ import javax.net.ssl.HostnameVerifier;
/*    */ import javax.net.ssl.SSLSession;
/*    */ 
/*    */ public final class DefaultHostnameVerifier
/*    */   implements HostnameVerifier
/*    */ {
/*    */   public boolean verify(String paramString, SSLSession paramSSLSession)
/*    */   {
/* 43 */     return false;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.https.DefaultHostnameVerifier
 * JD-Core Version:    0.6.2
 */