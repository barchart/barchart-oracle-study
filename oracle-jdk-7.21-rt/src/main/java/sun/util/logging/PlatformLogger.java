/*     */ package sun.util.logging;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import sun.misc.JavaLangAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ 
/*     */ public class PlatformLogger
/*     */ {
/*     */   public static final int OFF = 2147483647;
/*     */   public static final int SEVERE = 1000;
/*     */   public static final int WARNING = 900;
/*     */   public static final int INFO = 800;
/*     */   public static final int CONFIG = 700;
/*     */   public static final int FINE = 500;
/*     */   public static final int FINER = 400;
/*     */   public static final int FINEST = 300;
/*     */   public static final int ALL = -2147483648;
/*     */   private static final int defaultLevel = 800;
/* 104 */   private static boolean loggingEnabled = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Boolean run() {
/* 107 */       String str1 = System.getProperty("java.util.logging.config.class");
/* 108 */       String str2 = System.getProperty("java.util.logging.config.file");
/* 109 */       return Boolean.valueOf((str1 != null) || (str2 != null));
/*     */     }
/*     */   })).booleanValue();
/*     */ 
/* 115 */   private static Map<String, WeakReference<PlatformLogger>> loggers = new HashMap();
/*     */   private volatile LoggerProxy logger;
/*     */ 
/*     */   public static synchronized PlatformLogger getLogger(String paramString)
/*     */   {
/* 122 */     PlatformLogger localPlatformLogger = null;
/* 123 */     WeakReference localWeakReference = (WeakReference)loggers.get(paramString);
/* 124 */     if (localWeakReference != null) {
/* 125 */       localPlatformLogger = (PlatformLogger)localWeakReference.get();
/*     */     }
/* 127 */     if (localPlatformLogger == null) {
/* 128 */       localPlatformLogger = new PlatformLogger(paramString);
/* 129 */       loggers.put(paramString, new WeakReference(localPlatformLogger));
/*     */     }
/* 131 */     return localPlatformLogger;
/*     */   }
/*     */ 
/*     */   public static synchronized void redirectPlatformLoggers()
/*     */   {
/* 139 */     if ((loggingEnabled) || (!LoggingSupport.isAvailable())) return;
/*     */ 
/* 141 */     loggingEnabled = true;
/* 142 */     for (Map.Entry localEntry : loggers.entrySet()) {
/* 143 */       WeakReference localWeakReference = (WeakReference)localEntry.getValue();
/* 144 */       PlatformLogger localPlatformLogger = (PlatformLogger)localWeakReference.get();
/* 145 */       if (localPlatformLogger != null)
/* 146 */         localPlatformLogger.newJavaLogger();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void newJavaLogger()
/*     */   {
/* 155 */     this.logger = new JavaLogger(this.logger.name, this.logger.effectiveLevel);
/*     */   }
/*     */ 
/*     */   private PlatformLogger(String paramString)
/*     */   {
/* 163 */     if (loggingEnabled)
/* 164 */       this.logger = new JavaLogger(paramString);
/*     */     else
/* 166 */       this.logger = new LoggerProxy(paramString);
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 175 */     return this.logger.isEnabled();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 182 */     return this.logger.name;
/*     */   }
/*     */ 
/*     */   public boolean isLoggable(int paramInt)
/*     */   {
/* 190 */     return this.logger.isLoggable(paramInt);
/*     */   }
/*     */ 
/*     */   public int getLevel()
/*     */   {
/* 198 */     return this.logger.getLevel();
/*     */   }
/*     */ 
/*     */   public void setLevel(int paramInt)
/*     */   {
/* 205 */     this.logger.setLevel(paramInt);
/*     */   }
/*     */ 
/*     */   public void severe(String paramString)
/*     */   {
/* 212 */     this.logger.doLog(1000, paramString);
/*     */   }
/*     */ 
/*     */   public void severe(String paramString, Throwable paramThrowable) {
/* 216 */     this.logger.doLog(1000, paramString, paramThrowable);
/*     */   }
/*     */ 
/*     */   public void severe(String paramString, Object[] paramArrayOfObject) {
/* 220 */     this.logger.doLog(1000, paramString, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public void warning(String paramString)
/*     */   {
/* 227 */     this.logger.doLog(900, paramString);
/*     */   }
/*     */ 
/*     */   public void warning(String paramString, Throwable paramThrowable) {
/* 231 */     this.logger.doLog(900, paramString, paramThrowable);
/*     */   }
/*     */ 
/*     */   public void warning(String paramString, Object[] paramArrayOfObject) {
/* 235 */     this.logger.doLog(900, paramString, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public void info(String paramString)
/*     */   {
/* 242 */     this.logger.doLog(800, paramString);
/*     */   }
/*     */ 
/*     */   public void info(String paramString, Throwable paramThrowable) {
/* 246 */     this.logger.doLog(800, paramString, paramThrowable);
/*     */   }
/*     */ 
/*     */   public void info(String paramString, Object[] paramArrayOfObject) {
/* 250 */     this.logger.doLog(800, paramString, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public void config(String paramString)
/*     */   {
/* 257 */     this.logger.doLog(700, paramString);
/*     */   }
/*     */ 
/*     */   public void config(String paramString, Throwable paramThrowable) {
/* 261 */     this.logger.doLog(700, paramString, paramThrowable);
/*     */   }
/*     */ 
/*     */   public void config(String paramString, Object[] paramArrayOfObject) {
/* 265 */     this.logger.doLog(700, paramString, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public void fine(String paramString)
/*     */   {
/* 272 */     this.logger.doLog(500, paramString);
/*     */   }
/*     */ 
/*     */   public void fine(String paramString, Throwable paramThrowable) {
/* 276 */     this.logger.doLog(500, paramString, paramThrowable);
/*     */   }
/*     */ 
/*     */   public void fine(String paramString, Object[] paramArrayOfObject) {
/* 280 */     this.logger.doLog(500, paramString, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public void finer(String paramString)
/*     */   {
/* 287 */     this.logger.doLog(400, paramString);
/*     */   }
/*     */ 
/*     */   public void finer(String paramString, Throwable paramThrowable) {
/* 291 */     this.logger.doLog(400, paramString, paramThrowable);
/*     */   }
/*     */ 
/*     */   public void finer(String paramString, Object[] paramArrayOfObject) {
/* 295 */     this.logger.doLog(400, paramString, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public void finest(String paramString)
/*     */   {
/* 302 */     this.logger.doLog(300, paramString);
/*     */   }
/*     */ 
/*     */   public void finest(String paramString, Throwable paramThrowable) {
/* 306 */     this.logger.doLog(300, paramString, paramThrowable);
/*     */   }
/*     */ 
/*     */   public void finest(String paramString, Object[] paramArrayOfObject) {
/* 310 */     this.logger.doLog(300, paramString, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   private static String getLevelName(int paramInt)
/*     */   {
/* 557 */     switch (paramInt) { case 2147483647:
/* 558 */       return "OFF";
/*     */     case 1000:
/* 559 */       return "SEVERE";
/*     */     case 900:
/* 560 */       return "WARNING";
/*     */     case 800:
/* 561 */       return "INFO";
/*     */     case 700:
/* 562 */       return "CONFIG";
/*     */     case 500:
/* 563 */       return "FINE";
/*     */     case 400:
/* 564 */       return "FINER";
/*     */     case 300:
/* 565 */       return "FINEST";
/*     */     case -2147483648:
/* 566 */       return "ALL"; }
/* 567 */     return "UNKNOWN";
/*     */   }
/*     */ 
/*     */   static class JavaLogger extends PlatformLogger.LoggerProxy
/*     */   {
/* 471 */     private static final Map<Integer, Object> levelObjects = new HashMap();
/*     */     private final Object javaLogger;
/*     */ 
/*     */     private static void getLevelObjects()
/*     */     {
/* 483 */       int[] arrayOfInt1 = { 2147483647, 1000, 900, 800, 700, 500, 400, 300, -2147483648 };
/* 484 */       for (int k : arrayOfInt1) {
/* 485 */         Object localObject = LoggingSupport.parseLevel(PlatformLogger.getLevelName(k));
/* 486 */         levelObjects.put(Integer.valueOf(k), localObject);
/*     */       }
/*     */     }
/*     */ 
/*     */     JavaLogger(String paramString)
/*     */     {
/* 492 */       this(paramString, 0);
/*     */     }
/*     */ 
/*     */     JavaLogger(String paramString, int paramInt) {
/* 496 */       super(paramInt);
/* 497 */       this.javaLogger = LoggingSupport.getLogger(paramString);
/* 498 */       if (paramInt != 0)
/*     */       {
/* 500 */         LoggingSupport.setLevel(this.javaLogger, levelObjects.get(Integer.valueOf(paramInt)));
/*     */       }
/*     */     }
/*     */ 
/*     */     void doLog(int paramInt, String paramString)
/*     */     {
/* 511 */       LoggingSupport.log(this.javaLogger, levelObjects.get(Integer.valueOf(paramInt)), paramString);
/*     */     }
/*     */ 
/*     */     void doLog(int paramInt, String paramString, Throwable paramThrowable) {
/* 515 */       LoggingSupport.log(this.javaLogger, levelObjects.get(Integer.valueOf(paramInt)), paramString, paramThrowable);
/*     */     }
/*     */ 
/*     */     void doLog(int paramInt, String paramString, Object[] paramArrayOfObject)
/*     */     {
/* 521 */       int i = paramArrayOfObject != null ? paramArrayOfObject.length : 0;
/* 522 */       String[] arrayOfString = new String[i];
/* 523 */       for (int j = 0; j < i; j++) {
/* 524 */         arrayOfString[j] = String.valueOf(paramArrayOfObject[j]);
/*     */       }
/* 526 */       LoggingSupport.log(this.javaLogger, levelObjects.get(Integer.valueOf(paramInt)), paramString, arrayOfString);
/*     */     }
/*     */ 
/*     */     boolean isEnabled() {
/* 530 */       Object localObject = LoggingSupport.getLevel(this.javaLogger);
/* 531 */       return (localObject == null) || (!localObject.equals(levelObjects.get(Integer.valueOf(2147483647))));
/*     */     }
/*     */ 
/*     */     int getLevel() {
/* 535 */       Object localObject = LoggingSupport.getLevel(this.javaLogger);
/* 536 */       if (localObject != null) {
/* 537 */         for (Map.Entry localEntry : levelObjects.entrySet()) {
/* 538 */           if (localObject == localEntry.getValue()) {
/* 539 */             return ((Integer)localEntry.getKey()).intValue();
/*     */           }
/*     */         }
/*     */       }
/* 543 */       return 0;
/*     */     }
/*     */ 
/*     */     void setLevel(int paramInt) {
/* 547 */       this.levelValue = paramInt;
/* 548 */       LoggingSupport.setLevel(this.javaLogger, levelObjects.get(Integer.valueOf(paramInt)));
/*     */     }
/*     */ 
/*     */     public boolean isLoggable(int paramInt) {
/* 552 */       return LoggingSupport.isLoggable(this.javaLogger, levelObjects.get(Integer.valueOf(paramInt)));
/*     */     }
/*     */ 
/*     */     static
/*     */     {
/* 475 */       if (LoggingSupport.isAvailable())
/*     */       {
/* 477 */         getLevelObjects();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class LoggerProxy
/*     */   {
/* 318 */     private static final PrintStream defaultStream = System.err;
/*     */     final String name;
/*     */     volatile int levelValue;
/* 322 */     volatile int effectiveLevel = 0;
/*     */ 
/* 399 */     private static final String formatString = LoggingSupport.getSimpleFormat(false);
/*     */ 
/* 403 */     private Date date = new Date();
/*     */ 
/*     */     LoggerProxy(String paramString)
/*     */     {
/* 325 */       this(paramString, 800);
/*     */     }
/*     */ 
/*     */     LoggerProxy(String paramString, int paramInt) {
/* 329 */       this.name = paramString;
/* 330 */       this.levelValue = (paramInt == 0 ? 800 : paramInt);
/*     */     }
/*     */ 
/*     */     boolean isEnabled() {
/* 334 */       return this.levelValue != 2147483647;
/*     */     }
/*     */ 
/*     */     int getLevel() {
/* 338 */       return this.effectiveLevel;
/*     */     }
/*     */ 
/*     */     void setLevel(int paramInt) {
/* 342 */       this.levelValue = paramInt;
/* 343 */       this.effectiveLevel = paramInt;
/*     */     }
/*     */ 
/*     */     void doLog(int paramInt, String paramString) {
/* 347 */       if ((paramInt < this.levelValue) || (this.levelValue == 2147483647)) {
/* 348 */         return;
/*     */       }
/* 350 */       defaultStream.print(format(paramInt, paramString, null));
/*     */     }
/*     */ 
/*     */     void doLog(int paramInt, String paramString, Throwable paramThrowable) {
/* 354 */       if ((paramInt < this.levelValue) || (this.levelValue == 2147483647)) {
/* 355 */         return;
/*     */       }
/* 357 */       defaultStream.print(format(paramInt, paramString, paramThrowable));
/*     */     }
/*     */ 
/*     */     void doLog(int paramInt, String paramString, Object[] paramArrayOfObject) {
/* 361 */       if ((paramInt < this.levelValue) || (this.levelValue == 2147483647)) {
/* 362 */         return;
/*     */       }
/* 364 */       String str = formatMessage(paramString, paramArrayOfObject);
/* 365 */       defaultStream.print(format(paramInt, str, null));
/*     */     }
/*     */ 
/*     */     public boolean isLoggable(int paramInt) {
/* 369 */       if ((paramInt < this.levelValue) || (this.levelValue == 2147483647)) {
/* 370 */         return false;
/*     */       }
/* 372 */       return true;
/*     */     }
/*     */ 
/*     */     private String formatMessage(String paramString, Object[] paramArrayOfObject)
/*     */     {
/*     */       try
/*     */       {
/* 379 */         if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0))
/*     */         {
/* 381 */           return paramString;
/*     */         }
/*     */ 
/* 388 */         if ((paramString.indexOf("{0") >= 0) || (paramString.indexOf("{1") >= 0) || (paramString.indexOf("{2") >= 0) || (paramString.indexOf("{3") >= 0))
/*     */         {
/* 390 */           return MessageFormat.format(paramString, paramArrayOfObject);
/*     */         }
/* 392 */         return paramString;
/*     */       } catch (Exception localException) {
/*     */       }
/* 395 */       return paramString;
/*     */     }
/*     */ 
/*     */     private synchronized String format(int paramInt, String paramString, Throwable paramThrowable)
/*     */     {
/* 405 */       this.date.setTime(System.currentTimeMillis());
/* 406 */       String str = "";
/* 407 */       if (paramThrowable != null) {
/* 408 */         StringWriter localStringWriter = new StringWriter();
/* 409 */         PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
/* 410 */         localPrintWriter.println();
/* 411 */         paramThrowable.printStackTrace(localPrintWriter);
/* 412 */         localPrintWriter.close();
/* 413 */         str = localStringWriter.toString();
/*     */       }
/*     */ 
/* 416 */       return String.format(formatString, new Object[] { this.date, getCallerInfo(), this.name, PlatformLogger.getLevelName(paramInt), paramString, str });
/*     */     }
/*     */ 
/*     */     private String getCallerInfo()
/*     */     {
/* 428 */       Object localObject = null;
/* 429 */       String str1 = null;
/*     */ 
/* 431 */       JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
/* 432 */       Throwable localThrowable = new Throwable();
/* 433 */       int i = localJavaLangAccess.getStackTraceDepth(localThrowable);
/*     */ 
/* 435 */       String str2 = "sun.util.logging.PlatformLogger";
/* 436 */       int j = 1;
/* 437 */       for (int k = 0; k < i; k++)
/*     */       {
/* 440 */         StackTraceElement localStackTraceElement = localJavaLangAccess.getStackTraceElement(localThrowable, k);
/*     */ 
/* 442 */         String str3 = localStackTraceElement.getClassName();
/* 443 */         if (j != 0)
/*     */         {
/* 445 */           if (str3.equals(str2)) {
/* 446 */             j = 0;
/*     */           }
/*     */         }
/* 449 */         else if (!str3.equals(str2))
/*     */         {
/* 451 */           localObject = str3;
/* 452 */           str1 = localStackTraceElement.getMethodName();
/* 453 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 458 */       if (localObject != null) {
/* 459 */         return localObject + " " + str1;
/*     */       }
/* 461 */       return this.name;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.util.logging.PlatformLogger
 * JD-Core Version:    0.6.2
 */