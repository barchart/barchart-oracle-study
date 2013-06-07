/*     */ package java.sql;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.Vector;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ 
/*     */ public class DriverManager
/*     */ {
/*  83 */   private static final CopyOnWriteArrayList<DriverInfo> registeredDrivers = new CopyOnWriteArrayList();
/*  84 */   private static volatile int loginTimeout = 0;
/*  85 */   private static volatile PrintWriter logWriter = null;
/*  86 */   private static volatile PrintStream logStream = null;
/*     */ 
/*  88 */   private static final Object logSync = new Object();
/*     */ 
/* 108 */   static final SQLPermission SET_LOG_PERMISSION = new SQLPermission("setLog");
/*     */ 
/*     */   public static PrintWriter getLogWriter()
/*     */   {
/* 124 */     return logWriter;
/*     */   }
/*     */ 
/*     */   public static void setLogWriter(PrintWriter paramPrintWriter)
/*     */   {
/* 159 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 160 */     if (localSecurityManager != null) {
/* 161 */       localSecurityManager.checkPermission(SET_LOG_PERMISSION);
/*     */     }
/* 163 */     logStream = null;
/* 164 */     logWriter = paramPrintWriter;
/*     */   }
/*     */ 
/*     */   public static Connection getConnection(String paramString, Properties paramProperties)
/*     */     throws SQLException
/*     */   {
/* 188 */     ClassLoader localClassLoader = getCallerClassLoader();
/*     */ 
/* 190 */     return getConnection(paramString, paramProperties, localClassLoader);
/*     */   }
/*     */ 
/*     */   public static Connection getConnection(String paramString1, String paramString2, String paramString3)
/*     */     throws SQLException
/*     */   {
/* 208 */     Properties localProperties = new Properties();
/*     */ 
/* 212 */     ClassLoader localClassLoader = getCallerClassLoader();
/*     */ 
/* 214 */     if (paramString2 != null) {
/* 215 */       localProperties.put("user", paramString2);
/*     */     }
/* 217 */     if (paramString3 != null) {
/* 218 */       localProperties.put("password", paramString3);
/*     */     }
/*     */ 
/* 221 */     return getConnection(paramString1, localProperties, localClassLoader);
/*     */   }
/*     */ 
/*     */   public static Connection getConnection(String paramString)
/*     */     throws SQLException
/*     */   {
/* 237 */     Properties localProperties = new Properties();
/*     */ 
/* 241 */     ClassLoader localClassLoader = getCallerClassLoader();
/*     */ 
/* 243 */     return getConnection(paramString, localProperties, localClassLoader);
/*     */   }
/*     */ 
/*     */   public static Driver getDriver(String paramString)
/*     */     throws SQLException
/*     */   {
/* 260 */     println("DriverManager.getDriver(\"" + paramString + "\")");
/*     */ 
/* 264 */     ClassLoader localClassLoader = getCallerClassLoader();
/*     */ 
/* 268 */     for (DriverInfo localDriverInfo : registeredDrivers)
/*     */     {
/* 271 */       if (isDriverAllowed(localDriverInfo.driver, localClassLoader))
/*     */         try {
/* 273 */           if (localDriverInfo.driver.acceptsURL(paramString))
/*     */           {
/* 275 */             println("getDriver returning " + localDriverInfo.driver.getClass().getName());
/* 276 */             return localDriverInfo.driver;
/*     */           }
/*     */         }
/*     */         catch (SQLException localSQLException)
/*     */         {
/*     */         }
/*     */       else {
/* 283 */         println("    skipping: " + localDriverInfo.driver.getClass().getName());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 288 */     println("getDriver: no suitable driver");
/* 289 */     throw new SQLException("No suitable driver", "08001");
/*     */   }
/*     */ 
/*     */   public static synchronized void registerDriver(Driver paramDriver)
/*     */     throws SQLException
/*     */   {
/* 307 */     if (paramDriver != null) {
/* 308 */       registeredDrivers.addIfAbsent(new DriverInfo(paramDriver));
/*     */     }
/*     */     else {
/* 311 */       throw new NullPointerException();
/*     */     }
/*     */ 
/* 314 */     println("registerDriver: " + paramDriver);
/*     */   }
/*     */ 
/*     */   public static synchronized void deregisterDriver(Driver paramDriver)
/*     */     throws SQLException
/*     */   {
/* 327 */     if (paramDriver == null) {
/* 328 */       return;
/*     */     }
/*     */ 
/* 333 */     ClassLoader localClassLoader = getCallerClassLoader();
/* 334 */     println("DriverManager.deregisterDriver: " + paramDriver);
/*     */ 
/* 336 */     DriverInfo localDriverInfo = new DriverInfo(paramDriver);
/* 337 */     if (registeredDrivers.contains(localDriverInfo)) {
/* 338 */       if (isDriverAllowed(paramDriver, localClassLoader)) {
/* 339 */         registeredDrivers.remove(localDriverInfo);
/*     */       }
/*     */       else
/*     */       {
/* 343 */         throw new SecurityException();
/*     */       }
/*     */     }
/* 346 */     else println("    couldn't find driver to unload");
/*     */   }
/*     */ 
/*     */   public static Enumeration<Driver> getDrivers()
/*     */   {
/* 360 */     Vector localVector = new Vector();
/*     */ 
/* 364 */     ClassLoader localClassLoader = getCallerClassLoader();
/*     */ 
/* 367 */     for (DriverInfo localDriverInfo : registeredDrivers)
/*     */     {
/* 370 */       if (isDriverAllowed(localDriverInfo.driver, localClassLoader))
/* 371 */         localVector.addElement(localDriverInfo.driver);
/*     */       else {
/* 373 */         println("    skipping: " + localDriverInfo.getClass().getName());
/*     */       }
/*     */     }
/* 376 */     return localVector.elements();
/*     */   }
/*     */ 
/*     */   public static void setLoginTimeout(int paramInt)
/*     */   {
/* 388 */     loginTimeout = paramInt;
/*     */   }
/*     */ 
/*     */   public static int getLoginTimeout()
/*     */   {
/* 399 */     return loginTimeout;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static void setLogStream(PrintStream paramPrintStream)
/*     */   {
/* 423 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 424 */     if (localSecurityManager != null) {
/* 425 */       localSecurityManager.checkPermission(SET_LOG_PERMISSION);
/*     */     }
/*     */ 
/* 428 */     logStream = paramPrintStream;
/* 429 */     if (paramPrintStream != null)
/* 430 */       logWriter = new PrintWriter(paramPrintStream);
/*     */     else
/* 432 */       logWriter = null;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static PrintStream getLogStream()
/*     */   {
/* 444 */     return logStream;
/*     */   }
/*     */ 
/*     */   public static void println(String paramString)
/*     */   {
/* 453 */     synchronized (logSync) {
/* 454 */       if (logWriter != null) {
/* 455 */         logWriter.println(paramString);
/*     */ 
/* 458 */         logWriter.flush();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isDriverAllowed(Driver paramDriver, ClassLoader paramClassLoader)
/*     */   {
/* 468 */     boolean bool = false;
/* 469 */     if (paramDriver != null) {
/* 470 */       Class localClass = null;
/*     */       try {
/* 472 */         localClass = Class.forName(paramDriver.getClass().getName(), true, paramClassLoader);
/*     */       } catch (Exception localException) {
/* 474 */         bool = false;
/*     */       }
/*     */ 
/* 477 */       bool = localClass == paramDriver.getClass();
/*     */     }
/*     */ 
/* 480 */     return bool;
/*     */   }
/*     */ 
/*     */   private static void loadInitialDrivers() {
/*     */     String str1;
/*     */     try {
/* 486 */       str1 = (String)AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public String run() {
/* 488 */           return System.getProperty("jdbc.drivers");
/*     */         } } );
/*     */     }
/*     */     catch (Exception localException1) {
/* 492 */       str1 = null;
/*     */     }
/*     */ 
/* 499 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 502 */         ServiceLoader localServiceLoader = ServiceLoader.load(Driver.class);
/* 503 */         Iterator localIterator = localServiceLoader.iterator();
/*     */         try
/*     */         {
/* 518 */           while (localIterator.hasNext())
/* 519 */             localIterator.next();
/*     */         }
/*     */         catch (Throwable localThrowable)
/*     */         {
/*     */         }
/* 524 */         return null;
/*     */       }
/*     */     });
/* 528 */     println("DriverManager.initialize: jdbc.drivers = " + str1);
/*     */ 
/* 530 */     if ((str1 == null) || (str1.equals(""))) {
/* 531 */       return;
/*     */     }
/* 533 */     String[] arrayOfString1 = str1.split(":");
/* 534 */     println("number of Drivers:" + arrayOfString1.length);
/* 535 */     for (String str2 : arrayOfString1)
/*     */       try {
/* 537 */         println("DriverManager.Initialize: loading " + str2);
/* 538 */         Class.forName(str2, true, ClassLoader.getSystemClassLoader());
/*     */       }
/*     */       catch (Exception localException2) {
/* 541 */         println("DriverManager.Initialize: load failed: " + localException2);
/*     */       }
/*     */   }
/*     */ 
/*     */   private static Connection getConnection(String paramString, Properties paramProperties, ClassLoader paramClassLoader)
/*     */     throws SQLException
/*     */   {
/* 556 */     synchronized (DriverManager.class)
/*     */     {
/* 558 */       if (paramClassLoader == null) {
/* 559 */         paramClassLoader = Thread.currentThread().getContextClassLoader();
/*     */       }
/*     */     }
/*     */ 
/* 563 */     if (paramString == null) {
/* 564 */       throw new SQLException("The url cannot be null", "08001");
/*     */     }
/*     */ 
/* 567 */     println("DriverManager.getConnection(\"" + paramString + "\")");
/*     */ 
/* 571 */     ??? = null;
/*     */ 
/* 573 */     for (DriverInfo localDriverInfo : registeredDrivers)
/*     */     {
/* 576 */       if (isDriverAllowed(localDriverInfo.driver, paramClassLoader)) {
/*     */         try {
/* 578 */           println("    trying " + localDriverInfo.driver.getClass().getName());
/* 579 */           Connection localConnection = localDriverInfo.driver.connect(paramString, paramProperties);
/* 580 */           if (localConnection != null)
/*     */           {
/* 582 */             println("getConnection returning " + localDriverInfo.driver.getClass().getName());
/* 583 */             return localConnection;
/*     */           }
/*     */         } catch (SQLException localSQLException) {
/* 586 */           if (??? == null) {
/* 587 */             ??? = localSQLException;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 592 */         println("    skipping: " + localDriverInfo.getClass().getName());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 598 */     if (??? != null) {
/* 599 */       println("getConnection failed: " + ???);
/* 600 */       throw ???;
/*     */     }
/*     */ 
/* 603 */     println("getConnection: no suitable driver found for " + paramString);
/* 604 */     throw new SQLException("No suitable driver found for " + paramString, "08001");
/*     */   }
/*     */ 
/*     */   private static native ClassLoader getCallerClassLoader();
/*     */ 
/*     */   static
/*     */   {
/*  99 */     loadInitialDrivers();
/* 100 */     println("JDBC DriverManager initialized");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.sql.DriverManager
 * JD-Core Version:    0.6.2
 */