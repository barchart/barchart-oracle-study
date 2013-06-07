/*     */ package sun.net.www.protocol.http.ntlm;
/*     */ 
/*     */ import sun.misc.BASE64Encoder;
/*     */ 
/*     */ class B64Encoder extends BASE64Encoder
/*     */ {
/*     */   protected int bytesPerLine()
/*     */   {
/* 254 */     return 1024;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.http.ntlm.B64Encoder
 * JD-Core Version:    0.6.2
 */