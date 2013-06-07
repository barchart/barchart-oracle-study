/*     */ package sun.rmi.transport.proxy;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public final class CGIHandler
/*     */ {
/*     */   static int ContentLength;
/*     */   static String QueryString;
/*     */   static String RequestMethod;
/*     */   static String ServerName;
/*     */   static int ServerPort;
/*     */   private static CGICommandHandler[] commands;
/*     */   private static Hashtable commandLookup;
/*     */ 
/*     */   public static void main(String[] paramArrayOfString)
/*     */   {
/*     */     try
/*     */     {
/* 133 */       int i = QueryString.indexOf("=");
/*     */       String str1;
/*     */       String str2;
/* 134 */       if (i == -1) {
/* 135 */         str1 = QueryString;
/* 136 */         str2 = "";
/*     */       }
/*     */       else {
/* 139 */         str1 = QueryString.substring(0, i);
/* 140 */         str2 = QueryString.substring(i + 1);
/*     */       }
/* 142 */       CGICommandHandler localCGICommandHandler = (CGICommandHandler)commandLookup.get(str1);
/*     */ 
/* 144 */       if (localCGICommandHandler != null)
/*     */         try {
/* 146 */           localCGICommandHandler.execute(str2);
/*     */         } catch (CGIClientException localCGIClientException) {
/* 148 */           returnClientError(localCGIClientException.getMessage());
/*     */         } catch (CGIServerException localCGIServerException) {
/* 150 */           returnServerError(localCGIServerException.getMessage());
/*     */         }
/*     */       else
/* 153 */         returnClientError("invalid command.");
/*     */     } catch (Exception localException) {
/* 155 */       returnServerError("internal error: " + localException.getMessage());
/*     */     }
/* 157 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   private static void returnClientError(String paramString)
/*     */   {
/* 166 */     System.out.println("Status: 400 Bad Request: " + paramString);
/* 167 */     System.out.println("Content-type: text/html");
/* 168 */     System.out.println("");
/* 169 */     System.out.println("<HTML><HEAD><TITLE>Java RMI Client Error</TITLE></HEAD><BODY>");
/*     */ 
/* 173 */     System.out.println("<H1>Java RMI Client Error</H1>");
/* 174 */     System.out.println("");
/* 175 */     System.out.println(paramString);
/* 176 */     System.out.println("</BODY></HTML>");
/* 177 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   private static void returnServerError(String paramString)
/*     */   {
/* 186 */     System.out.println("Status: 500 Server Error: " + paramString);
/* 187 */     System.out.println("Content-type: text/html");
/* 188 */     System.out.println("");
/* 189 */     System.out.println("<HTML><HEAD><TITLE>Java RMI Server Error</TITLE></HEAD><BODY>");
/*     */ 
/* 193 */     System.out.println("<H1>Java RMI Server Error</H1>");
/* 194 */     System.out.println("");
/* 195 */     System.out.println(paramString);
/* 196 */     System.out.println("</BODY></HTML>");
/* 197 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  91 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/*  94 */         CGIHandler.ContentLength = Integer.getInteger("CONTENT_LENGTH", 0).intValue();
/*     */ 
/*  96 */         CGIHandler.QueryString = System.getProperty("QUERY_STRING", "");
/*  97 */         CGIHandler.RequestMethod = System.getProperty("REQUEST_METHOD", "");
/*  98 */         CGIHandler.ServerName = System.getProperty("SERVER_NAME", "");
/*  99 */         CGIHandler.ServerPort = Integer.getInteger("SERVER_PORT", 0).intValue();
/* 100 */         return null;
/*     */       }
/*     */     });
/* 106 */     commands = new CGICommandHandler[] { new CGIForwardCommand(), new CGIGethostnameCommand(), new CGIPingCommand(), new CGITryHostnameCommand() };
/*     */ 
/* 116 */     commandLookup = new Hashtable();
/* 117 */     for (int i = 0; i < commands.length; i++)
/* 118 */       commandLookup.put(commands[i].getName(), commands[i]);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.proxy.CGIHandler
 * JD-Core Version:    0.6.2
 */