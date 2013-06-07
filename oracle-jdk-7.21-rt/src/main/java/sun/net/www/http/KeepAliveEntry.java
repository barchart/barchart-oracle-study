/*     */ package sun.net.www.http;
/*     */ 
/*     */ class KeepAliveEntry
/*     */ {
/*     */   HttpClient hc;
/*     */   long idleStartTime;
/*     */ 
/*     */   KeepAliveEntry(HttpClient paramHttpClient, long paramLong)
/*     */   {
/* 344 */     this.hc = paramHttpClient;
/* 345 */     this.idleStartTime = paramLong;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.http.KeepAliveEntry
 * JD-Core Version:    0.6.2
 */