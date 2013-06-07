/*      */ package sun.rmi.server;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.security.AccessController;
/*      */ import java.util.Date;
/*      */ import sun.rmi.runtime.NewThreadAction;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ class PipeWriter
/*      */   implements Runnable
/*      */ {
/*      */   private ByteArrayOutputStream bufOut;
/*      */   private int cLast;
/*      */   private byte[] currSep;
/*      */   private PrintWriter out;
/*      */   private InputStream in;
/*      */   private String pipeString;
/*      */   private String execString;
/* 2339 */   private static String lineSeparator = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
/*      */ 
/* 2341 */   private static int lineSeparatorLength = lineSeparator.length();
/*      */ 
/* 2336 */   private static int numExecs = 0;
/*      */ 
/*      */   private PipeWriter(InputStream paramInputStream, OutputStream paramOutputStream, String paramString, int paramInt)
/*      */   {
/* 2358 */     this.in = paramInputStream;
/* 2359 */     this.out = new PrintWriter(paramOutputStream);
/*      */ 
/* 2361 */     this.bufOut = new ByteArrayOutputStream();
/* 2362 */     this.currSep = new byte[lineSeparatorLength];
/*      */ 
/* 2365 */     this.execString = (":ExecGroup-" + Integer.toString(paramInt) + ':' + paramString + ':');
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/* 2377 */     byte[] arrayOfByte = new byte[256];
/*      */     try
/*      */     {
/*      */       int i;
/* 2382 */       while ((i = this.in.read(arrayOfByte)) != -1) {
/* 2383 */         write(arrayOfByte, 0, i);
/*      */       }
/*      */ 
/* 2390 */       String str = this.bufOut.toString();
/* 2391 */       this.bufOut.reset();
/* 2392 */       if (str.length() > 0) {
/* 2393 */         this.out.println(createAnnotation() + str);
/* 2394 */         this.out.flush();
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException
/*      */   {
/* 2407 */     if (paramInt2 < 0) {
/* 2408 */       throw new ArrayIndexOutOfBoundsException(paramInt2);
/*      */     }
/* 2410 */     for (int i = 0; i < paramInt2; i++)
/* 2411 */       write(paramArrayOfByte[(paramInt1 + i)]);
/*      */   }
/*      */ 
/*      */   private void write(byte paramByte)
/*      */     throws IOException
/*      */   {
/* 2423 */     int i = 0;
/*      */ 
/* 2426 */     for (i = 1; i < this.currSep.length; i++) {
/* 2427 */       this.currSep[(i - 1)] = this.currSep[i];
/*      */     }
/* 2429 */     this.currSep[(i - 1)] = paramByte;
/* 2430 */     this.bufOut.write(paramByte);
/*      */ 
/* 2433 */     if ((this.cLast >= lineSeparatorLength - 1) && (lineSeparator.equals(new String(this.currSep))))
/*      */     {
/* 2436 */       this.cLast = 0;
/*      */ 
/* 2439 */       this.out.print(createAnnotation() + this.bufOut.toString());
/* 2440 */       this.out.flush();
/* 2441 */       this.bufOut.reset();
/*      */ 
/* 2443 */       if (this.out.checkError()) {
/* 2444 */         throw new IOException("PipeWriter: IO Exception when writing to output stream.");
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 2450 */       this.cLast += 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   private String createAnnotation()
/*      */   {
/* 2463 */     return new Date().toString() + this.execString;
/*      */   }
/*      */ 
/*      */   static void plugTogetherPair(InputStream paramInputStream1, OutputStream paramOutputStream1, InputStream paramInputStream2, OutputStream paramOutputStream2)
/*      */   {
/* 2484 */     Thread localThread1 = null;
/* 2485 */     Thread localThread2 = null;
/*      */ 
/* 2487 */     int i = getNumExec();
/*      */ 
/* 2490 */     localThread1 = (Thread)AccessController.doPrivileged(new NewThreadAction(new PipeWriter(paramInputStream1, paramOutputStream1, "out", i), "out", true));
/*      */ 
/* 2493 */     localThread2 = (Thread)AccessController.doPrivileged(new NewThreadAction(new PipeWriter(paramInputStream2, paramOutputStream2, "err", i), "err", true));
/*      */ 
/* 2496 */     localThread1.start();
/* 2497 */     localThread2.start();
/*      */   }
/*      */ 
/*      */   private static synchronized int getNumExec() {
/* 2501 */     return numExecs++;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.server.PipeWriter
 * JD-Core Version:    0.6.2
 */