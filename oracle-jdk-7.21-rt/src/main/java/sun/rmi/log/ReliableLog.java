/*     */ package sun.rmi.log;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public class ReliableLog
/*     */ {
/*     */   public static final int PreferredMajorVersion = 0;
/*     */   public static final int PreferredMinorVersion = 2;
/*  87 */   private boolean Debug = false;
/*     */ 
/*  89 */   private static String snapshotPrefix = "Snapshot.";
/*  90 */   private static String logfilePrefix = "Logfile.";
/*  91 */   private static String versionFile = "Version_Number";
/*  92 */   private static String newVersionFile = "New_Version_Number";
/*  93 */   private static int intBytes = 4;
/*  94 */   private static long diskPageSize = 512L;
/*     */   private File dir;
/*  97 */   private int version = 0;
/*  98 */   private String logName = null;
/*  99 */   private LogFile log = null;
/* 100 */   private long snapshotBytes = 0L;
/* 101 */   private long logBytes = 0L;
/* 102 */   private int logEntries = 0;
/* 103 */   private long lastSnapshot = 0L;
/* 104 */   private long lastLog = 0L;
/*     */   private LogHandler handler;
/* 107 */   private final byte[] intBuf = new byte[4];
/*     */ 
/* 110 */   private int majorFormatVersion = 0;
/* 111 */   private int minorFormatVersion = 0;
/*     */ 
/* 122 */   private static final Constructor<? extends LogFile> logClassConstructor = getLogClassConstructor();
/*     */ 
/*     */   public ReliableLog(String paramString, LogHandler paramLogHandler, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 143 */     this.Debug = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.log.debug"))).booleanValue();
/*     */ 
/* 145 */     this.dir = new File(paramString);
/* 146 */     if ((!this.dir.exists()) || (!this.dir.isDirectory()))
/*     */     {
/* 148 */       if (!this.dir.mkdir()) {
/* 149 */         throw new IOException("could not create directory for log: " + paramString);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 154 */     this.handler = paramLogHandler;
/* 155 */     this.lastSnapshot = 0L;
/* 156 */     this.lastLog = 0L;
/* 157 */     getVersion();
/* 158 */     if (this.version == 0)
/*     */       try {
/* 160 */         snapshot(paramLogHandler.initialSnapshot());
/*     */       } catch (IOException localIOException) {
/* 162 */         throw localIOException;
/*     */       } catch (Exception localException) {
/* 164 */         throw new IOException("initial snapshot failed with exception: " + localException);
/*     */       }
/*     */   }
/*     */ 
/*     */   public ReliableLog(String paramString, LogHandler paramLogHandler)
/*     */     throws IOException
/*     */   {
/* 184 */     this(paramString, paramLogHandler, false);
/*     */   }
/*     */ 
/*     */   public synchronized Object recover()
/*     */     throws IOException
/*     */   {
/* 202 */     if (this.Debug) {
/* 203 */       System.err.println("log.debug: recover()");
/*     */     }
/* 205 */     if (this.version == 0) {
/* 206 */       return null;
/*     */     }
/*     */ 
/* 209 */     String str = versionName(snapshotPrefix);
/* 210 */     File localFile = new File(str);
/* 211 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(localFile));
/*     */ 
/* 214 */     if (this.Debug)
/* 215 */       System.err.println("log.debug: recovering from " + str);
/*     */     Object localObject1;
/*     */     try {
/*     */       try {
/* 219 */         localObject1 = this.handler.recover(localBufferedInputStream);
/*     */       }
/*     */       catch (IOException localIOException) {
/* 222 */         throw localIOException;
/*     */       }
/*     */     } catch (Exception localException) { if (this.Debug)
/* 225 */         System.err.println("log.debug: recovery failed: " + localException);
/* 226 */       throw new IOException("log recover failed with exception: " + localException);
/*     */ 
/* 229 */       this.snapshotBytes = localFile.length();
/*     */     } finally {
/* 231 */       localBufferedInputStream.close();
/*     */     }
/*     */ 
/* 234 */     return recoverUpdates(localObject1);
/*     */   }
/*     */ 
/*     */   public synchronized void update(Object paramObject)
/*     */     throws IOException
/*     */   {
/* 248 */     update(paramObject, true);
/*     */   }
/*     */ 
/*     */   public synchronized void update(Object paramObject, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/* 267 */     if (this.log == null) {
/* 268 */       throw new IOException("log is inaccessible, it may have been corrupted or closed");
/*     */     }
/*     */ 
/* 277 */     long l1 = this.log.getFilePointer();
/* 278 */     boolean bool = this.log.checkSpansBoundary(l1);
/* 279 */     writeInt(this.log, bool ? -2147483648 : 0);
/*     */     try
/*     */     {
/* 285 */       this.handler.writeUpdate(new LogOutputStream(this.log), paramObject);
/*     */     } catch (IOException localIOException) {
/* 287 */       throw localIOException;
/*     */     } catch (Exception localException) {
/* 289 */       throw ((IOException)new IOException("write update failed").initCause(localException));
/*     */     }
/*     */ 
/* 292 */     this.log.sync();
/*     */ 
/* 294 */     long l2 = this.log.getFilePointer();
/* 295 */     int i = (int)(l2 - l1 - intBytes);
/* 296 */     this.log.seek(l1);
/*     */ 
/* 298 */     if (bool)
/*     */     {
/* 306 */       writeInt(this.log, i | 0x80000000);
/* 307 */       this.log.sync();
/*     */ 
/* 309 */       this.log.seek(l1);
/* 310 */       this.log.writeByte(i >> 24);
/* 311 */       this.log.sync();
/*     */     }
/*     */     else
/*     */     {
/* 317 */       writeInt(this.log, i);
/* 318 */       this.log.sync();
/*     */     }
/*     */ 
/* 321 */     this.log.seek(l2);
/* 322 */     this.logBytes = l2;
/* 323 */     this.lastLog = System.currentTimeMillis();
/* 324 */     this.logEntries += 1;
/*     */   }
/*     */ 
/*     */   private static Constructor<? extends LogFile> getLogClassConstructor()
/*     */   {
/* 336 */     String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.log.class"));
/*     */ 
/* 338 */     if (str != null) {
/*     */       try {
/* 340 */         ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public ClassLoader run()
/*     */           {
/* 344 */             return ClassLoader.getSystemClassLoader();
/*     */           }
/*     */         });
/* 347 */         Class localClass = localClassLoader.loadClass(str);
/* 348 */         if (LogFile.class.isAssignableFrom(localClass))
/* 349 */           return localClass.getConstructor(new Class[] { String.class, String.class });
/*     */       }
/*     */       catch (Exception localException) {
/* 352 */         System.err.println("Exception occurred:");
/* 353 */         localException.printStackTrace();
/*     */       }
/*     */     }
/* 356 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized void snapshot(Object paramObject)
/*     */     throws IOException
/*     */   {
/* 371 */     int i = this.version;
/* 372 */     incrVersion();
/*     */ 
/* 374 */     String str = versionName(snapshotPrefix);
/* 375 */     File localFile = new File(str);
/* 376 */     FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
/*     */     try {
/*     */       try {
/* 379 */         this.handler.snapshot(localFileOutputStream, paramObject);
/*     */       } catch (IOException localIOException) {
/* 381 */         throw localIOException;
/*     */       }
/*     */     } catch (Exception localException) { throw new IOException("snapshot failed", localException);
/*     */ 
/* 385 */       this.lastSnapshot = System.currentTimeMillis();
/*     */     } finally {
/* 387 */       localFileOutputStream.close();
/* 388 */       this.snapshotBytes = localFile.length();
/*     */     }
/*     */ 
/* 391 */     openLogFile(true);
/* 392 */     writeVersionFile(true);
/* 393 */     commitToNewVersion();
/* 394 */     deleteSnapshot(i);
/* 395 */     deleteLogFile(i);
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */     throws IOException
/*     */   {
/* 405 */     if (this.log == null) return; try
/*     */     {
/* 407 */       this.log.close();
/*     */     } finally {
/* 409 */       this.log = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long snapshotSize()
/*     */   {
/* 417 */     return this.snapshotBytes;
/*     */   }
/*     */ 
/*     */   public long logSize()
/*     */   {
/* 424 */     return this.logBytes;
/*     */   }
/*     */ 
/*     */   private void writeInt(DataOutput paramDataOutput, int paramInt)
/*     */     throws IOException
/*     */   {
/* 440 */     this.intBuf[0] = ((byte)(paramInt >> 24));
/* 441 */     this.intBuf[1] = ((byte)(paramInt >> 16));
/* 442 */     this.intBuf[2] = ((byte)(paramInt >> 8));
/* 443 */     this.intBuf[3] = ((byte)paramInt);
/* 444 */     paramDataOutput.write(this.intBuf);
/*     */   }
/*     */ 
/*     */   private String fName(String paramString)
/*     */   {
/* 453 */     return this.dir.getPath() + File.separator + paramString;
/*     */   }
/*     */ 
/*     */   private String versionName(String paramString)
/*     */   {
/* 463 */     return versionName(paramString, 0);
/*     */   }
/*     */ 
/*     */   private String versionName(String paramString, int paramInt)
/*     */   {
/* 474 */     paramInt = paramInt == 0 ? this.version : paramInt;
/* 475 */     return fName(paramString) + String.valueOf(paramInt);
/*     */   }
/*     */ 
/*     */   private void incrVersion()
/*     */   {
/*     */     do
/* 482 */       this.version += 1; while (this.version == 0);
/*     */   }
/*     */ 
/*     */   private void deleteFile(String paramString)
/*     */     throws IOException
/*     */   {
/* 493 */     File localFile = new File(paramString);
/* 494 */     if (!localFile.delete())
/* 495 */       throw new IOException("couldn't remove file: " + paramString);
/*     */   }
/*     */ 
/*     */   private void deleteNewVersionFile()
/*     */     throws IOException
/*     */   {
/* 504 */     deleteFile(fName(newVersionFile));
/*     */   }
/*     */ 
/*     */   private void deleteSnapshot(int paramInt)
/*     */     throws IOException
/*     */   {
/* 514 */     if (paramInt == 0) return;
/* 515 */     deleteFile(versionName(snapshotPrefix, paramInt));
/*     */   }
/*     */ 
/*     */   private void deleteLogFile(int paramInt)
/*     */     throws IOException
/*     */   {
/* 525 */     if (paramInt == 0) return;
/* 526 */     deleteFile(versionName(logfilePrefix, paramInt));
/*     */   }
/*     */ 
/*     */   private void openLogFile(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 539 */       close();
/*     */     }
/*     */     catch (IOException localIOException) {
/*     */     }
/* 543 */     this.logName = versionName(logfilePrefix);
/*     */     try
/*     */     {
/* 546 */       this.log = (logClassConstructor == null ? new LogFile(this.logName, "rw") : (LogFile)logClassConstructor.newInstance(new Object[] { this.logName, "rw" }));
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 550 */       throw ((IOException)new IOException("unable to construct LogFile instance").initCause(localException));
/*     */     }
/*     */ 
/* 554 */     if (paramBoolean)
/* 555 */       initializeLogFile();
/*     */   }
/*     */ 
/*     */   private void initializeLogFile()
/*     */     throws IOException
/*     */   {
/* 575 */     this.log.setLength(0L);
/* 576 */     this.majorFormatVersion = 0;
/* 577 */     writeInt(this.log, 0);
/* 578 */     this.minorFormatVersion = 2;
/* 579 */     writeInt(this.log, 2);
/* 580 */     this.logBytes = (intBytes * 2);
/* 581 */     this.logEntries = 0;
/*     */   }
/*     */ 
/*     */   private void writeVersionFile(boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*     */     String str;
/* 593 */     if (paramBoolean)
/* 594 */       str = newVersionFile;
/*     */     else {
/* 596 */       str = versionFile;
/*     */     }
/* 598 */     DataOutputStream localDataOutputStream = new DataOutputStream(new FileOutputStream(fName(str)));
/*     */ 
/* 600 */     writeInt(localDataOutputStream, this.version);
/* 601 */     localDataOutputStream.close();
/*     */   }
/*     */ 
/*     */   private void createFirstVersion()
/*     */     throws IOException
/*     */   {
/* 610 */     this.version = 0;
/* 611 */     writeVersionFile(false);
/*     */   }
/*     */ 
/*     */   private void commitToNewVersion()
/*     */     throws IOException
/*     */   {
/* 620 */     writeVersionFile(false);
/* 621 */     deleteNewVersionFile();
/*     */   }
/*     */ 
/*     */   private int readVersion(String paramString)
/*     */     throws IOException
/*     */   {
/* 632 */     DataInputStream localDataInputStream = new DataInputStream(new FileInputStream(paramString));
/*     */     try {
/* 634 */       return localDataInputStream.readInt();
/*     */     } finally {
/* 636 */       localDataInputStream.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void getVersion()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 648 */       this.version = readVersion(fName(newVersionFile));
/* 649 */       commitToNewVersion();
/*     */     } catch (IOException localIOException1) {
/*     */       try {
/* 652 */         deleteNewVersionFile();
/*     */       }
/*     */       catch (IOException localIOException2)
/*     */       {
/*     */       }
/*     */       try {
/* 658 */         this.version = readVersion(fName(versionFile));
/*     */       }
/*     */       catch (IOException localIOException3) {
/* 661 */         createFirstVersion();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object recoverUpdates(Object paramObject)
/*     */     throws IOException
/*     */   {
/* 678 */     this.logBytes = 0L;
/* 679 */     this.logEntries = 0;
/*     */ 
/* 681 */     if (this.version == 0) return paramObject;
/*     */ 
/* 683 */     String str = versionName(logfilePrefix);
/* 684 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(str));
/*     */ 
/* 686 */     DataInputStream localDataInputStream = new DataInputStream(localBufferedInputStream);
/*     */ 
/* 688 */     if (this.Debug)
/* 689 */       System.err.println("log.debug: reading updates from " + str);
/*     */     try
/*     */     {
/* 692 */       this.majorFormatVersion = localDataInputStream.readInt(); this.logBytes += intBytes;
/* 693 */       this.minorFormatVersion = localDataInputStream.readInt(); this.logBytes += intBytes;
/*     */     }
/*     */     catch (EOFException localEOFException1)
/*     */     {
/* 698 */       openLogFile(true);
/* 699 */       localBufferedInputStream = null;
/*     */     }
/*     */ 
/* 707 */     if (this.majorFormatVersion != 0) {
/* 708 */       if (this.Debug) {
/* 709 */         System.err.println("log.debug: major version mismatch: " + this.majorFormatVersion + "." + this.minorFormatVersion);
/*     */       }
/*     */ 
/* 712 */       throw new IOException("Log file " + this.logName + " has a " + "version " + this.majorFormatVersion + "." + this.minorFormatVersion + " format, and this implementation " + " understands only version " + 0 + "." + 2);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 722 */       while (localBufferedInputStream != null) {
/* 723 */         int i = 0;
/*     */         try
/*     */         {
/* 726 */           i = localDataInputStream.readInt();
/*     */         } catch (EOFException localEOFException2) {
/* 728 */           if (this.Debug)
/* 729 */             System.err.println("log.debug: log was sync'd cleanly");
/* 730 */           break;
/*     */         }
/* 732 */         if (i <= 0) {
/* 733 */           if (!this.Debug) break;
/* 734 */           System.err.println("log.debug: last update incomplete, updateLen = 0x" + Integer.toHexString(i)); break;
/*     */         }
/*     */ 
/* 746 */         if (localBufferedInputStream.available() < i)
/*     */         {
/* 750 */           if (!this.Debug) break;
/* 751 */           System.err.println("log.debug: log was truncated"); break;
/*     */         }
/*     */ 
/* 755 */         if (this.Debug)
/* 756 */           System.err.println("log.debug: rdUpdate size " + i);
/*     */         try {
/* 758 */           paramObject = this.handler.readUpdate(new LogInputStream(localBufferedInputStream, i), paramObject);
/*     */         }
/*     */         catch (IOException localIOException) {
/* 761 */           throw localIOException;
/*     */         } catch (Exception localException) {
/* 763 */           localException.printStackTrace();
/* 764 */           throw new IOException("read update failed with exception: " + localException);
/*     */         }
/*     */ 
/* 767 */         this.logBytes += intBytes + i;
/* 768 */         this.logEntries += 1;
/*     */       }
/*     */     } finally {
/* 771 */       if (localBufferedInputStream != null) {
/* 772 */         localBufferedInputStream.close();
/*     */       }
/*     */     }
/* 775 */     if (this.Debug) {
/* 776 */       System.err.println("log.debug: recovered updates: " + this.logEntries);
/*     */     }
/*     */ 
/* 779 */     openLogFile(false);
/*     */ 
/* 782 */     if (this.log == null) {
/* 783 */       throw new IOException("rmid's log is inaccessible, it may have been corrupted or closed");
/*     */     }
/*     */ 
/* 787 */     this.log.seek(this.logBytes);
/* 788 */     this.log.setLength(this.logBytes);
/*     */ 
/* 790 */     return paramObject;
/*     */   }
/*     */ 
/*     */   public static class LogFile extends RandomAccessFile
/*     */   {
/*     */     private final FileDescriptor fd;
/*     */ 
/*     */     public LogFile(String paramString1, String paramString2)
/*     */       throws FileNotFoundException, IOException
/*     */     {
/* 807 */       super(paramString2);
/* 808 */       this.fd = getFD();
/*     */     }
/*     */ 
/*     */     protected void sync()
/*     */       throws IOException
/*     */     {
/* 815 */       this.fd.sync();
/*     */     }
/*     */ 
/*     */     protected boolean checkSpansBoundary(long paramLong)
/*     */     {
/* 824 */       return paramLong % 512L > 508L;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.log.ReliableLog
 * JD-Core Version:    0.6.2
 */