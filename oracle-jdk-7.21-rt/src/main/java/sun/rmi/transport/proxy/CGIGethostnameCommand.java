/*     */ package sun.rmi.transport.proxy;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ final class CGIGethostnameCommand
/*     */   implements CGICommandHandler
/*     */ {
/*     */   public String getName()
/*     */   {
/* 335 */     return "gethostname";
/*     */   }
/*     */ 
/*     */   public void execute(String paramString)
/*     */   {
/* 340 */     System.out.println("Status: 200 OK");
/* 341 */     System.out.println("Content-type: application/octet-stream");
/* 342 */     System.out.println("Content-length: " + CGIHandler.ServerName.length());
/*     */ 
/* 344 */     System.out.println("");
/* 345 */     System.out.print(CGIHandler.ServerName);
/* 346 */     System.out.flush();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.proxy.CGIGethostnameCommand
 * JD-Core Version:    0.6.2
 */