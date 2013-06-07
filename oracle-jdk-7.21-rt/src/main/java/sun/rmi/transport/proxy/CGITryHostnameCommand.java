/*     */ package sun.rmi.transport.proxy;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ 
/*     */ final class CGITryHostnameCommand
/*     */   implements CGICommandHandler
/*     */ {
/*     */   public String getName()
/*     */   {
/* 376 */     return "tryhostname";
/*     */   }
/*     */ 
/*     */   public void execute(String paramString)
/*     */   {
/* 381 */     System.out.println("Status: 200 OK");
/* 382 */     System.out.println("Content-type: text/html");
/* 383 */     System.out.println("");
/* 384 */     System.out.println("<HTML><HEAD><TITLE>Java RMI Server Hostname Info</TITLE></HEAD><BODY>");
/*     */ 
/* 388 */     System.out.println("<H1>Java RMI Server Hostname Info</H1>");
/* 389 */     System.out.println("<H2>Local host name available to Java VM:</H2>");
/* 390 */     System.out.print("<P>InetAddress.getLocalHost().getHostName()");
/*     */     try {
/* 392 */       String str = InetAddress.getLocalHost().getHostName();
/*     */ 
/* 394 */       System.out.println(" = " + str);
/*     */     } catch (UnknownHostException localUnknownHostException) {
/* 396 */       System.out.println(" threw java.net.UnknownHostException");
/*     */     }
/*     */ 
/* 399 */     System.out.println("<H2>Server host information obtained through CGI interface from HTTP server:</H2>");
/* 400 */     System.out.println("<P>SERVER_NAME = " + CGIHandler.ServerName);
/* 401 */     System.out.println("<P>SERVER_PORT = " + CGIHandler.ServerPort);
/* 402 */     System.out.println("</BODY></HTML>");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.proxy.CGITryHostnameCommand
 * JD-Core Version:    0.6.2
 */