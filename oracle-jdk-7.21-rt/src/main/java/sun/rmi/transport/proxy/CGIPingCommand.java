/*     */ package sun.rmi.transport.proxy;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ final class CGIPingCommand
/*     */   implements CGICommandHandler
/*     */ {
/*     */   public String getName()
/*     */   {
/* 357 */     return "ping";
/*     */   }
/*     */ 
/*     */   public void execute(String paramString)
/*     */   {
/* 362 */     System.out.println("Status: 200 OK");
/* 363 */     System.out.println("Content-type: application/octet-stream");
/* 364 */     System.out.println("Content-length: 0");
/* 365 */     System.out.println("");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.proxy.CGIPingCommand
 * JD-Core Version:    0.6.2
 */