/*     */ package sun.net;
/*     */ 
/*     */ import java.net.URL;
/*     */ 
/*     */ class DefaultProgressMeteringPolicy
/*     */   implements ProgressMeteringPolicy
/*     */ {
/*     */   public boolean shouldMeterInput(URL paramURL, String paramString)
/*     */   {
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   public int getProgressUpdateThreshold()
/*     */   {
/* 255 */     return 8192;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.DefaultProgressMeteringPolicy
 * JD-Core Version:    0.6.2
 */