/*     */ package java.lang;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class Runtime
/*     */ {
/*  45 */   private static Runtime currentRuntime = new Runtime();
/*     */ 
/*     */   public static Runtime getRuntime()
/*     */   {
/*  56 */     return currentRuntime;
/*     */   }
/*     */ 
/*     */   public void exit(int paramInt)
/*     */   {
/* 103 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 104 */     if (localSecurityManager != null) {
/* 105 */       localSecurityManager.checkExit(paramInt);
/*     */     }
/* 107 */     Shutdown.exit(paramInt);
/*     */   }
/*     */ 
/*     */   public void addShutdownHook(Thread paramThread)
/*     */   {
/* 205 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 206 */     if (localSecurityManager != null) {
/* 207 */       localSecurityManager.checkPermission(new RuntimePermission("shutdownHooks"));
/*     */     }
/* 209 */     ApplicationShutdownHooks.add(paramThread);
/*     */   }
/*     */ 
/*     */   public boolean removeShutdownHook(Thread paramThread)
/*     */   {
/* 233 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 234 */     if (localSecurityManager != null) {
/* 235 */       localSecurityManager.checkPermission(new RuntimePermission("shutdownHooks"));
/*     */     }
/* 237 */     return ApplicationShutdownHooks.remove(paramThread);
/*     */   }
/*     */ 
/*     */   public void halt(int paramInt)
/*     */   {
/* 269 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 270 */     if (localSecurityManager != null) {
/* 271 */       localSecurityManager.checkExit(paramInt);
/*     */     }
/* 273 */     Shutdown.halt(paramInt);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void runFinalizersOnExit(boolean paramBoolean)
/*     */   {
/* 304 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 305 */     if (localSecurityManager != null) {
/*     */       try {
/* 307 */         localSecurityManager.checkExit(0);
/*     */       } catch (SecurityException localSecurityException) {
/* 309 */         throw new SecurityException("runFinalizersOnExit");
/*     */       }
/*     */     }
/* 312 */     Shutdown.setRunFinalizersOnExit(paramBoolean);
/*     */   }
/*     */ 
/*     */   public Process exec(String paramString)
/*     */     throws IOException
/*     */   {
/* 345 */     return exec(paramString, null, null);
/*     */   }
/*     */ 
/*     */   public Process exec(String paramString, String[] paramArrayOfString)
/*     */     throws IOException
/*     */   {
/* 386 */     return exec(paramString, paramArrayOfString, null);
/*     */   }
/*     */ 
/*     */   public Process exec(String paramString, String[] paramArrayOfString, File paramFile)
/*     */     throws IOException
/*     */   {
/* 441 */     if (paramString.length() == 0) {
/* 442 */       throw new IllegalArgumentException("Empty command");
/*     */     }
/* 444 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/* 445 */     String[] arrayOfString = new String[localStringTokenizer.countTokens()];
/* 446 */     for (int i = 0; localStringTokenizer.hasMoreTokens(); i++)
/* 447 */       arrayOfString[i] = localStringTokenizer.nextToken();
/* 448 */     return exec(arrayOfString, paramArrayOfString, paramFile);
/*     */   }
/*     */ 
/*     */   public Process exec(String[] paramArrayOfString)
/*     */     throws IOException
/*     */   {
/* 483 */     return exec(paramArrayOfString, null, null);
/*     */   }
/*     */ 
/*     */   public Process exec(String[] paramArrayOfString1, String[] paramArrayOfString2)
/*     */     throws IOException
/*     */   {
/* 526 */     return exec(paramArrayOfString1, paramArrayOfString2, null);
/*     */   }
/*     */ 
/*     */   public Process exec(String[] paramArrayOfString1, String[] paramArrayOfString2, File paramFile)
/*     */     throws IOException
/*     */   {
/* 615 */     return new ProcessBuilder(paramArrayOfString1).environment(paramArrayOfString2).directory(paramFile).start();
/*     */   }
/*     */ 
/*     */   public native int availableProcessors();
/*     */ 
/*     */   public native long freeMemory();
/*     */ 
/*     */   public native long totalMemory();
/*     */ 
/*     */   public native long maxMemory();
/*     */ 
/*     */   public native void gc();
/*     */ 
/*     */   private static native void runFinalization0();
/*     */ 
/*     */   public void runFinalization()
/*     */   {
/* 710 */     runFinalization0();
/*     */   }
/*     */ 
/*     */   public native void traceInstructions(boolean paramBoolean);
/*     */ 
/*     */   public native void traceMethodCalls(boolean paramBoolean);
/*     */ 
/*     */   public void load(String paramString)
/*     */   {
/* 780 */     load0(System.getCallerClass(), paramString);
/*     */   }
/*     */ 
/*     */   synchronized void load0(Class paramClass, String paramString) {
/* 784 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 785 */     if (localSecurityManager != null) {
/* 786 */       localSecurityManager.checkLink(paramString);
/*     */     }
/* 788 */     if (!new File(paramString).isAbsolute()) {
/* 789 */       throw new UnsatisfiedLinkError("Expecting an absolute path of the library: " + paramString);
/*     */     }
/*     */ 
/* 792 */     ClassLoader.loadLibrary(paramClass, paramString, true);
/*     */   }
/*     */ 
/*     */   public void loadLibrary(String paramString)
/*     */   {
/* 833 */     loadLibrary0(System.getCallerClass(), paramString);
/*     */   }
/*     */ 
/*     */   synchronized void loadLibrary0(Class paramClass, String paramString) {
/* 837 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 838 */     if (localSecurityManager != null) {
/* 839 */       localSecurityManager.checkLink(paramString);
/*     */     }
/* 841 */     if (paramString.indexOf(File.separatorChar) != -1) {
/* 842 */       throw new UnsatisfiedLinkError("Directory separator should not appear in library name: " + paramString);
/*     */     }
/*     */ 
/* 845 */     ClassLoader.loadLibrary(paramClass, paramString, false);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public InputStream getLocalizedInputStream(InputStream paramInputStream)
/*     */   {
/* 871 */     return paramInputStream;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public OutputStream getLocalizedOutputStream(OutputStream paramOutputStream)
/*     */   {
/* 899 */     return paramOutputStream;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.Runtime
 * JD-Core Version:    0.6.2
 */