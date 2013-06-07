/*     */ package sun.rmi.transport.proxy;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ 
/*     */ final class CGIForwardCommand
/*     */   implements CGICommandHandler
/*     */ {
/*     */   public String getName()
/*     */   {
/* 208 */     return "forward";
/*     */   }
/*     */ 
/*     */   public void execute(String paramString) throws CGIClientException, CGIServerException
/*     */   {
/* 213 */     if (!CGIHandler.RequestMethod.equals("POST"))
/* 214 */       throw new CGIClientException("can only forward POST requests");
/*     */     int i;
/*     */     try
/*     */     {
/* 218 */       i = Integer.parseInt(paramString);
/*     */     } catch (NumberFormatException localNumberFormatException) {
/* 220 */       throw new CGIClientException("invalid port number.");
/*     */     }
/* 222 */     if ((i <= 0) || (i > 65535))
/* 223 */       throw new CGIClientException("invalid port: " + i);
/* 224 */     if (i < 1024) {
/* 225 */       throw new CGIClientException("permission denied for port: " + i);
/*     */     }
/*     */ 
/*     */     Socket localSocket;
/*     */     try
/*     */     {
/* 231 */       localSocket = new Socket(InetAddress.getLocalHost(), i);
/*     */     } catch (IOException localIOException1) {
/* 233 */       throw new CGIServerException("could not connect to local port");
/*     */     }
/*     */ 
/* 239 */     DataInputStream localDataInputStream1 = new DataInputStream(System.in);
/* 240 */     byte[] arrayOfByte = new byte[CGIHandler.ContentLength];
/*     */     try {
/* 242 */       localDataInputStream1.readFully(arrayOfByte);
/*     */     } catch (EOFException localEOFException1) {
/* 244 */       throw new CGIClientException("unexpected EOF reading request body");
/*     */     } catch (IOException localIOException2) {
/* 246 */       throw new CGIClientException("error reading request body");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 253 */       DataOutputStream localDataOutputStream = new DataOutputStream(localSocket.getOutputStream());
/*     */ 
/* 255 */       localDataOutputStream.writeBytes("POST / HTTP/1.0\r\n");
/* 256 */       localDataOutputStream.writeBytes("Content-length: " + CGIHandler.ContentLength + "\r\n\r\n");
/*     */ 
/* 258 */       localDataOutputStream.write(arrayOfByte);
/* 259 */       localDataOutputStream.flush();
/*     */     } catch (IOException localIOException3) {
/* 261 */       throw new CGIServerException("error writing to server");
/*     */     }
/*     */ 
/*     */     DataInputStream localDataInputStream2;
/*     */     try
/*     */     {
/* 269 */       localDataInputStream2 = new DataInputStream(localSocket.getInputStream());
/*     */     } catch (IOException localIOException4) {
/* 271 */       throw new CGIServerException("error reading from server"); } String str1 = "Content-length:".toLowerCase();
/* 274 */     int j = 0;
/*     */ 
/* 276 */     int k = -1;
/*     */     String str2;
/*     */     do { try { str2 = localDataInputStream2.readLine();
/*     */       } catch (IOException localIOException5) {
/* 281 */         throw new CGIServerException("error reading from server");
/*     */       }
/* 283 */       if (str2 == null) {
/* 284 */         throw new CGIServerException("unexpected EOF reading server response");
/*     */       }
/*     */ 
/* 287 */       if (str2.toLowerCase().startsWith(str1)) {
/* 288 */         if (j != 0) {
/* 289 */           throw new CGIServerException("Multiple Content-length entries found.");
/*     */         }
/*     */ 
/* 292 */         k = Integer.parseInt(str2.substring(str1.length()).trim());
/*     */ 
/* 294 */         j = 1;
/*     */       }
/*     */     }
/*     */ 
/* 298 */     while ((str2.length() != 0) && (str2.charAt(0) != '\r') && (str2.charAt(0) != '\n'));
/*     */ 
/* 300 */     if ((j == 0) || (k < 0)) {
/* 301 */       throw new CGIServerException("missing or invalid content length in server response");
/*     */     }
/* 303 */     arrayOfByte = new byte[k];
/*     */     try {
/* 305 */       localDataInputStream2.readFully(arrayOfByte);
/*     */     } catch (EOFException localEOFException2) {
/* 307 */       throw new CGIServerException("unexpected EOF reading server response");
/*     */     }
/*     */     catch (IOException localIOException6) {
/* 310 */       throw new CGIServerException("error reading from server");
/*     */     }
/*     */ 
/* 316 */     System.out.println("Status: 200 OK");
/* 317 */     System.out.println("Content-type: application/octet-stream");
/* 318 */     System.out.println("");
/*     */     try {
/* 320 */       System.out.write(arrayOfByte);
/*     */     } catch (IOException localIOException7) {
/* 322 */       throw new CGIServerException("error writing response");
/*     */     }
/* 324 */     System.out.flush();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.proxy.CGIForwardCommand
 * JD-Core Version:    0.6.2
 */