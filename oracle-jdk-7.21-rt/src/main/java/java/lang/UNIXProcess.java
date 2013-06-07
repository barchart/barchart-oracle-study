/*     */ package java.lang;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import sun.misc.JavaIOFileDescriptorAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ 
/*     */ final class UNIXProcess extends Process
/*     */ {
/*  55 */   private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
/*     */   private final int pid;
/*     */   private int exitcode;
/*     */   private boolean hasExited;
/*     */   private OutputStream stdin;
/*     */   private InputStream stdout;
/*     */   private InputStream stderr;
/* 120 */   private static final Executor processReaperExecutor = (Executor)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Executor run() {
/* 123 */       return Executors.newCachedThreadPool(new UNIXProcess.ProcessReaperThreadFactory(null));
/*     */     }
/*     */   });
/*     */ 
/*     */   private native int waitForProcessExit(int paramInt);
/*     */ 
/*     */   private native int forkAndExec(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, byte[] paramArrayOfByte3, int paramInt2, byte[] paramArrayOfByte4, int[] paramArrayOfInt, boolean paramBoolean)
/*     */     throws IOException;
/*     */ 
/*     */   UNIXProcess(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, byte[] paramArrayOfByte3, int paramInt2, byte[] paramArrayOfByte4, final int[] paramArrayOfInt, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 135 */     this.pid = forkAndExec(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramArrayOfByte3, paramInt2, paramArrayOfByte4, paramArrayOfInt, paramBoolean);
/*     */     try
/*     */     {
/* 143 */       AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */         public Void run() throws IOException {
/* 145 */           UNIXProcess.this.initStreams(paramArrayOfInt);
/* 146 */           return null;
/*     */         } } );
/*     */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 149 */       throw ((IOException)localPrivilegedActionException.getException());
/*     */     }
/*     */   }
/*     */ 
/*     */   static FileDescriptor newFileDescriptor(int paramInt) {
/* 154 */     FileDescriptor localFileDescriptor = new FileDescriptor();
/* 155 */     fdAccess.set(localFileDescriptor, paramInt);
/* 156 */     return localFileDescriptor;
/*     */   }
/*     */ 
/*     */   void initStreams(int[] paramArrayOfInt) throws IOException {
/* 160 */     this.stdin = (paramArrayOfInt[0] == -1 ? ProcessBuilder.NullOutputStream.INSTANCE : new ProcessPipeOutputStream(paramArrayOfInt[0]));
/*     */ 
/* 164 */     this.stdout = (paramArrayOfInt[1] == -1 ? ProcessBuilder.NullInputStream.INSTANCE : new ProcessPipeInputStream(paramArrayOfInt[1]));
/*     */ 
/* 168 */     this.stderr = (paramArrayOfInt[2] == -1 ? ProcessBuilder.NullInputStream.INSTANCE : new ProcessPipeInputStream(paramArrayOfInt[2]));
/*     */ 
/* 172 */     processReaperExecutor.execute(new Runnable() {
/*     */       public void run() {
/* 174 */         int i = UNIXProcess.this.waitForProcessExit(UNIXProcess.this.pid);
/* 175 */         UNIXProcess.this.processExited(i);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   void processExited(int paramInt) {
/* 180 */     synchronized (this) {
/* 181 */       this.exitcode = paramInt;
/* 182 */       this.hasExited = true;
/* 183 */       notifyAll();
/*     */     }
/*     */ 
/* 186 */     if ((this.stdout instanceof ProcessPipeInputStream)) {
/* 187 */       ((ProcessPipeInputStream)this.stdout).processExited();
/*     */     }
/* 189 */     if ((this.stderr instanceof ProcessPipeInputStream)) {
/* 190 */       ((ProcessPipeInputStream)this.stderr).processExited();
/*     */     }
/* 192 */     if ((this.stdin instanceof ProcessPipeOutputStream))
/* 193 */       ((ProcessPipeOutputStream)this.stdin).processExited();
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream() {
/* 197 */     return this.stdin;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream() {
/* 201 */     return this.stdout;
/*     */   }
/*     */ 
/*     */   public InputStream getErrorStream() {
/* 205 */     return this.stderr;
/*     */   }
/*     */ 
/*     */   public synchronized int waitFor() throws InterruptedException {
/* 209 */     while (!this.hasExited) {
/* 210 */       wait();
/*     */     }
/* 212 */     return this.exitcode;
/*     */   }
/*     */ 
/*     */   public synchronized int exitValue() {
/* 216 */     if (!this.hasExited) {
/* 217 */       throw new IllegalThreadStateException("process hasn't exited");
/*     */     }
/* 219 */     return this.exitcode;
/*     */   }
/*     */ 
/*     */   private static native void destroyProcess(int paramInt);
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 230 */     synchronized (this) {
/* 231 */       if (!this.hasExited)
/* 232 */         destroyProcess(this.pid); 
/*     */     }
/*     */     try { this.stdin.close(); } catch (IOException localIOException1) {
/*     */     }try { this.stdout.close(); } catch (IOException localIOException2) {
/*     */     }try { this.stderr.close(); } catch (IOException localIOException3) {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   static {
/* 243 */     initIDs();
/*     */   }
/*     */ 
/*     */   static class ProcessPipeInputStream extends BufferedInputStream
/*     */   {
/*     */     ProcessPipeInputStream(int paramInt)
/*     */     {
/* 257 */       super();
/*     */     }
/*     */ 
/*     */     private static byte[] drainInputStream(InputStream paramInputStream) throws IOException
/*     */     {
/* 262 */       if (paramInputStream == null) return null;
/* 263 */       int i = 0;
/*     */ 
/* 265 */       byte[] arrayOfByte = null;
/*     */       int j;
/* 266 */       while ((j = paramInputStream.available()) > 0) {
/* 267 */         arrayOfByte = arrayOfByte == null ? new byte[j] : Arrays.copyOf(arrayOfByte, i + j);
/* 268 */         i += paramInputStream.read(arrayOfByte, i, j);
/*     */       }
/* 270 */       return (arrayOfByte == null) || (i == arrayOfByte.length) ? arrayOfByte : Arrays.copyOf(arrayOfByte, i);
/*     */     }
/*     */ 
/*     */     synchronized void processExited()
/*     */     {
/*     */       try
/*     */       {
/* 278 */         InputStream localInputStream = this.in;
/* 279 */         if (localInputStream != null) {
/* 280 */           byte[] arrayOfByte = drainInputStream(localInputStream);
/* 281 */           localInputStream.close();
/* 282 */           this.in = (arrayOfByte == null ? ProcessBuilder.NullInputStream.INSTANCE : new ByteArrayInputStream(arrayOfByte));
/*     */ 
/* 285 */           if (this.buf == null)
/* 286 */             this.in = null;
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ProcessPipeOutputStream extends BufferedOutputStream
/*     */   {
/*     */     ProcessPipeOutputStream(int paramInt)
/*     */     {
/* 301 */       super();
/*     */     }
/*     */ 
/*     */     synchronized void processExited()
/*     */     {
/* 306 */       OutputStream localOutputStream = this.out;
/* 307 */       if (localOutputStream != null) {
/*     */         try {
/* 309 */           localOutputStream.close();
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/*     */         }
/* 314 */         this.out = ProcessBuilder.NullOutputStream.INSTANCE;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ProcessReaperThreadFactory
/*     */     implements ThreadFactory
/*     */   {
/*  95 */     private static final ThreadGroup group = getRootThreadGroup();
/*     */ 
/*     */     private static ThreadGroup getRootThreadGroup() {
/*  98 */       return (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public ThreadGroup run() {
/* 100 */           ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
/* 101 */           while (localThreadGroup.getParent() != null)
/* 102 */             localThreadGroup = localThreadGroup.getParent();
/* 103 */           return localThreadGroup;
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public Thread newThread(Runnable paramRunnable) {
/* 109 */       Thread localThread = new Thread(group, paramRunnable, "process reaper", 32768L);
/* 110 */       localThread.setDaemon(true);
/*     */ 
/* 112 */       localThread.setPriority(10);
/* 113 */       return localThread;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.UNIXProcess
 * JD-Core Version:    0.6.2
 */