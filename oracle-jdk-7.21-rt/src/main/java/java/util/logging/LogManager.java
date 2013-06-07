/*      */ package java.util.logging;
/*      */ 
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.beans.PropertyChangeSupport;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.security.AccessController;
/*      */ import java.security.Permission;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Properties;
/*      */ import java.util.Vector;
/*      */ import sun.misc.JavaAWTAccess;
/*      */ import sun.misc.SharedSecrets;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ public class LogManager
/*      */ {
/*      */   private static LogManager manager;
/*  154 */   private static final Handler[] emptyHandlers = new Handler[0];
/*  155 */   private Properties props = new Properties();
/*  156 */   private PropertyChangeSupport changes = new PropertyChangeSupport(LogManager.class);
/*      */ 
/*  158 */   private static final Level defaultLevel = Level.INFO;
/*      */ 
/*  161 */   private final LoggerContext systemContext = new SystemLoggerContext();
/*  162 */   private final LoggerContext userContext = new LoggerContext(null);
/*      */   private Logger rootLogger;
/*      */   private volatile boolean readPrimordialConfiguration;
/*  171 */   private boolean initializedGlobalHandlers = true;
/*      */   private boolean deathImminent;
/*  714 */   private final ReferenceQueue<Logger> loggerRefQueue = new ReferenceQueue();
/*      */   private static final int MAX_ITERATIONS = 400;
/* 1252 */   private final Permission controlPermission = new LoggingPermission("control", null);
/*      */ 
/* 1366 */   private static LoggingMXBean loggingMXBean = null;
/*      */   public static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";
/*      */ 
/*      */   protected LogManager()
/*      */   {
/*      */     try
/*      */     {
/*  256 */       Runtime.getRuntime().addShutdownHook(new Cleaner(null));
/*      */     }
/*      */     catch (IllegalStateException localIllegalStateException)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static LogManager getLogManager()
/*      */   {
/*  267 */     if (manager != null) {
/*  268 */       manager.readPrimordialConfiguration();
/*      */     }
/*  270 */     return manager;
/*      */   }
/*      */ 
/*      */   private void readPrimordialConfiguration() {
/*  274 */     if (!this.readPrimordialConfiguration)
/*  275 */       synchronized (this) {
/*  276 */         if (!this.readPrimordialConfiguration)
/*      */         {
/*  280 */           if (System.out == null) {
/*  281 */             return;
/*      */           }
/*  283 */           this.readPrimordialConfiguration = true;
/*      */           try
/*      */           {
/*  286 */             AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*      */               public Void run() throws Exception {
/*  288 */                 LogManager.this.readConfiguration();
/*      */ 
/*  291 */                 PlatformLogger.redirectPlatformLoggers();
/*  292 */                 return null;
/*      */               }
/*      */             });
/*      */           }
/*      */           catch (Exception localException)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */     throws SecurityException
/*      */   {
/*  316 */     if (paramPropertyChangeListener == null) {
/*  317 */       throw new NullPointerException();
/*      */     }
/*  319 */     checkPermission();
/*  320 */     this.changes.addPropertyChangeListener(paramPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */     throws SecurityException
/*      */   {
/*  338 */     checkPermission();
/*  339 */     this.changes.removePropertyChangeListener(paramPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   private LoggerContext getUserContext()
/*      */   {
/*  345 */     LoggerContext localLoggerContext = null;
/*      */ 
/*  347 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  348 */     JavaAWTAccess localJavaAWTAccess = SharedSecrets.getJavaAWTAccess();
/*  349 */     if ((localSecurityManager != null) && (localJavaAWTAccess != null))
/*  350 */       synchronized (localJavaAWTAccess)
/*      */       {
/*  355 */         Object localObject1 = localJavaAWTAccess.getExecutionContext();
/*  356 */         if (localObject1 == null)
/*      */         {
/*  358 */           localObject1 = localJavaAWTAccess.getContext();
/*      */         }
/*  360 */         localLoggerContext = (LoggerContext)localJavaAWTAccess.get(localObject1, LoggerContext.class);
/*  361 */         if (localLoggerContext == null) {
/*  362 */           if (localJavaAWTAccess.isMainAppContext()) {
/*  363 */             localLoggerContext = this.userContext;
/*      */           } else {
/*  365 */             localLoggerContext = new LoggerContext(null);
/*      */ 
/*  368 */             if (manager.rootLogger != null)
/*  369 */               localLoggerContext.addLocalLogger(manager.rootLogger);
/*      */           }
/*  371 */           localJavaAWTAccess.put(localObject1, LoggerContext.class, localLoggerContext);
/*      */         }
/*      */       }
/*      */     else {
/*  375 */       localLoggerContext = this.userContext;
/*      */     }
/*  377 */     return localLoggerContext;
/*      */   }
/*      */ 
/*      */   private List<LoggerContext> contexts() {
/*  381 */     ArrayList localArrayList = new ArrayList();
/*  382 */     localArrayList.add(this.systemContext);
/*  383 */     localArrayList.add(getUserContext());
/*  384 */     return localArrayList;
/*      */   }
/*      */ 
/*      */   Logger demandLogger(String paramString1, String paramString2)
/*      */   {
/*  400 */     Logger localLogger1 = getLogger(paramString1);
/*  401 */     if (localLogger1 == null)
/*      */     {
/*  403 */       Logger localLogger2 = new Logger(paramString1, paramString2);
/*      */       do {
/*  405 */         if (addLogger(localLogger2))
/*      */         {
/*  408 */           return localLogger2;
/*      */         }
/*      */ 
/*  422 */         localLogger1 = getLogger(paramString1);
/*  423 */       }while (localLogger1 == null);
/*      */     }
/*  425 */     return localLogger1;
/*      */   }
/*      */ 
/*      */   Logger demandSystemLogger(String paramString1, String paramString2)
/*      */   {
/*  430 */     final Logger localLogger1 = this.systemContext.demandLogger(paramString1, paramString2);
/*      */     Logger localLogger2;
/*      */     do {
/*  441 */       if (addLogger(localLogger1))
/*      */       {
/*  443 */         localLogger2 = localLogger1;
/*      */       }
/*  445 */       else localLogger2 = getLogger(paramString1);
/*      */     }
/*  447 */     while (localLogger2 == null);
/*      */ 
/*  450 */     if ((localLogger2 != localLogger1) && (localLogger1.getHandlers().length == 0))
/*      */     {
/*  452 */       final Logger localLogger3 = localLogger2;
/*  453 */       AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Void run() {
/*  455 */           for (Handler localHandler : localLogger3.getHandlers()) {
/*  456 */             localLogger1.addHandler(localHandler);
/*      */           }
/*  458 */           return null;
/*      */         }
/*      */       });
/*      */     }
/*  462 */     return localLogger1;
/*      */   }
/*      */ 
/*      */   private void loadLoggerHandlers(final Logger paramLogger, String paramString1, final String paramString2)
/*      */   {
/*  678 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Object run() {
/*  680 */         String[] arrayOfString = LogManager.this.parseClassNames(paramString2);
/*  681 */         for (int i = 0; i < arrayOfString.length; i++) {
/*  682 */           String str1 = arrayOfString[i];
/*      */           try {
/*  684 */             Class localClass = ClassLoader.getSystemClassLoader().loadClass(str1);
/*  685 */             Handler localHandler = (Handler)localClass.newInstance();
/*      */ 
/*  688 */             String str2 = LogManager.this.getProperty(str1 + ".level");
/*  689 */             if (str2 != null) {
/*  690 */               Level localLevel = Level.findLevel(str2);
/*  691 */               if (localLevel != null) {
/*  692 */                 localHandler.setLevel(localLevel);
/*      */               }
/*      */               else {
/*  695 */                 System.err.println("Can't set level for " + str1);
/*      */               }
/*      */             }
/*      */ 
/*  699 */             paramLogger.addHandler(localHandler);
/*      */           } catch (Exception localException) {
/*  701 */             System.err.println("Can't load log handler \"" + str1 + "\"");
/*  702 */             System.err.println("" + localException);
/*  703 */             localException.printStackTrace();
/*      */           }
/*      */         }
/*  706 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   final synchronized void drainLoggerRefQueueBounded()
/*      */   {
/*  811 */     for (int i = 0; (i < 400) && 
/*  812 */       (this.loggerRefQueue != null); i++)
/*      */     {
/*  817 */       LoggerWeakRef localLoggerWeakRef = (LoggerWeakRef)this.loggerRefQueue.poll();
/*  818 */       if (localLoggerWeakRef == null)
/*      */       {
/*      */         break;
/*      */       }
/*  822 */       localLoggerWeakRef.dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean addLogger(Logger paramLogger)
/*      */   {
/*  843 */     String str = paramLogger.getName();
/*  844 */     if (str == null) {
/*  845 */       throw new NullPointerException();
/*      */     }
/*  847 */     LoggerContext localLoggerContext = getUserContext();
/*  848 */     if (localLoggerContext.addLocalLogger(paramLogger))
/*      */     {
/*  851 */       loadLoggerHandlers(paramLogger, str, str + ".handlers");
/*  852 */       return true;
/*      */     }
/*  854 */     return false;
/*      */   }
/*      */ 
/*      */   private static void doSetLevel(Logger paramLogger, final Level paramLevel)
/*      */   {
/*  861 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  862 */     if (localSecurityManager == null)
/*      */     {
/*  864 */       paramLogger.setLevel(paramLevel);
/*  865 */       return;
/*      */     }
/*      */ 
/*  869 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Object run() {
/*  871 */         this.val$logger.setLevel(paramLevel);
/*  872 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static void doSetParent(Logger paramLogger1, final Logger paramLogger2)
/*      */   {
/*  879 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  880 */     if (localSecurityManager == null)
/*      */     {
/*  882 */       paramLogger1.setParent(paramLogger2);
/*  883 */       return;
/*      */     }
/*      */ 
/*  887 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Object run() {
/*  889 */         this.val$logger.setParent(paramLogger2);
/*  890 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public Logger getLogger(String paramString)
/*      */   {
/*  910 */     return getUserContext().findLogger(paramString);
/*      */   }
/*      */ 
/*      */   public Enumeration<String> getLoggerNames()
/*      */   {
/*  930 */     return getUserContext().getLoggerNames();
/*      */   }
/*      */ 
/*      */   public void readConfiguration()
/*      */     throws IOException, SecurityException
/*      */   {
/*  950 */     checkPermission();
/*      */ 
/*  953 */     String str1 = System.getProperty("java.util.logging.config.class");
/*  954 */     if (str1 != null)
/*      */     {
/*      */       try
/*      */       {
/*  960 */         Class localClass = ClassLoader.getSystemClassLoader().loadClass(str1);
/*  961 */         localClass.newInstance();
/*  962 */         return;
/*      */       } catch (ClassNotFoundException localClassNotFoundException) {
/*  964 */         localObject1 = Thread.currentThread().getContextClassLoader().loadClass(str1);
/*  965 */         ((Class)localObject1).newInstance();
/*  966 */         return;
/*      */       }
/*      */       catch (Exception localException) {
/*  969 */         System.err.println("Logging configuration class \"" + str1 + "\" failed");
/*  970 */         System.err.println("" + localException);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  975 */     String str2 = System.getProperty("java.util.logging.config.file");
/*  976 */     if (str2 == null) {
/*  977 */       str2 = System.getProperty("java.home");
/*  978 */       if (str2 == null) {
/*  979 */         throw new Error("Can't find java.home ??");
/*      */       }
/*  981 */       localObject1 = new File(str2, "lib");
/*  982 */       localObject1 = new File((File)localObject1, "logging.properties");
/*  983 */       str2 = ((File)localObject1).getCanonicalPath();
/*      */     }
/*  985 */     Object localObject1 = new FileInputStream(str2);
/*  986 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream((InputStream)localObject1);
/*      */     try {
/*  988 */       readConfiguration(localBufferedInputStream);
/*      */     } finally {
/*  990 */       if (localObject1 != null)
/*  991 */         ((InputStream)localObject1).close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */     throws SecurityException
/*      */   {
/* 1008 */     checkPermission();
/* 1009 */     synchronized (this) {
/* 1010 */       this.props = new Properties();
/*      */ 
/* 1013 */       this.initializedGlobalHandlers = true;
/*      */     }
/* 1015 */     for (??? = contexts().iterator(); ((Iterator)???).hasNext(); ) { LoggerContext localLoggerContext = (LoggerContext)((Iterator)???).next();
/* 1016 */       Enumeration localEnumeration = localLoggerContext.getLoggerNames();
/* 1017 */       while (localEnumeration.hasMoreElements()) {
/* 1018 */         String str = (String)localEnumeration.nextElement();
/* 1019 */         Logger localLogger = localLoggerContext.findLogger(str);
/* 1020 */         if (localLogger != null)
/* 1021 */           resetLogger(localLogger);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void resetLogger(Logger paramLogger)
/*      */   {
/* 1030 */     Handler[] arrayOfHandler = paramLogger.getHandlers();
/* 1031 */     for (int i = 0; i < arrayOfHandler.length; i++) {
/* 1032 */       Handler localHandler = arrayOfHandler[i];
/* 1033 */       paramLogger.removeHandler(localHandler);
/*      */       try {
/* 1035 */         localHandler.close();
/*      */       }
/*      */       catch (Exception localException) {
/*      */       }
/*      */     }
/* 1040 */     String str = paramLogger.getName();
/* 1041 */     if ((str != null) && (str.equals("")))
/*      */     {
/* 1043 */       paramLogger.setLevel(defaultLevel);
/*      */     }
/* 1045 */     else paramLogger.setLevel(null);
/*      */   }
/*      */ 
/*      */   private String[] parseClassNames(String paramString)
/*      */   {
/* 1051 */     String str1 = getProperty(paramString);
/* 1052 */     if (str1 == null) {
/* 1053 */       return new String[0];
/*      */     }
/* 1055 */     str1 = str1.trim();
/* 1056 */     int i = 0;
/* 1057 */     Vector localVector = new Vector();
/* 1058 */     while (i < str1.length()) {
/* 1059 */       int j = i;
/* 1060 */       while ((j < str1.length()) && 
/* 1061 */         (!Character.isWhitespace(str1.charAt(j))))
/*      */       {
/* 1064 */         if (str1.charAt(j) == ',') {
/*      */           break;
/*      */         }
/* 1067 */         j++;
/*      */       }
/* 1069 */       String str2 = str1.substring(i, j);
/* 1070 */       i = j + 1;
/* 1071 */       str2 = str2.trim();
/* 1072 */       if (str2.length() != 0)
/*      */       {
/* 1075 */         localVector.add(str2);
/*      */       }
/*      */     }
/* 1077 */     return (String[])localVector.toArray(new String[localVector.size()]);
/*      */   }
/*      */ 
/*      */   public void readConfiguration(InputStream paramInputStream)
/*      */     throws IOException, SecurityException
/*      */   {
/* 1094 */     checkPermission();
/* 1095 */     reset();
/*      */ 
/* 1098 */     this.props.load(paramInputStream);
/*      */ 
/* 1100 */     String[] arrayOfString = parseClassNames("config");
/*      */ 
/* 1102 */     for (int i = 0; i < arrayOfString.length; i++) {
/* 1103 */       String str = arrayOfString[i];
/*      */       try {
/* 1105 */         Class localClass = ClassLoader.getSystemClassLoader().loadClass(str);
/* 1106 */         localClass.newInstance();
/*      */       } catch (Exception localException) {
/* 1108 */         System.err.println("Can't load config class \"" + str + "\"");
/* 1109 */         System.err.println("" + localException);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1115 */     setLevelsOnExistingLoggers();
/*      */ 
/* 1118 */     this.changes.firePropertyChange(null, null, null);
/*      */ 
/* 1122 */     synchronized (this) {
/* 1123 */       this.initializedGlobalHandlers = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getProperty(String paramString)
/*      */   {
/* 1134 */     return this.props.getProperty(paramString);
/*      */   }
/*      */ 
/*      */   String getStringProperty(String paramString1, String paramString2)
/*      */   {
/* 1141 */     String str = getProperty(paramString1);
/* 1142 */     if (str == null) {
/* 1143 */       return paramString2;
/*      */     }
/* 1145 */     return str.trim();
/*      */   }
/*      */ 
/*      */   int getIntProperty(String paramString, int paramInt)
/*      */   {
/* 1152 */     String str = getProperty(paramString);
/* 1153 */     if (str == null)
/* 1154 */       return paramInt;
/*      */     try
/*      */     {
/* 1157 */       return Integer.parseInt(str.trim()); } catch (Exception localException) {
/*      */     }
/* 1159 */     return paramInt;
/*      */   }
/*      */ 
/*      */   boolean getBooleanProperty(String paramString, boolean paramBoolean)
/*      */   {
/* 1167 */     String str = getProperty(paramString);
/* 1168 */     if (str == null) {
/* 1169 */       return paramBoolean;
/*      */     }
/* 1171 */     str = str.toLowerCase();
/* 1172 */     if ((str.equals("true")) || (str.equals("1")))
/* 1173 */       return true;
/* 1174 */     if ((str.equals("false")) || (str.equals("0"))) {
/* 1175 */       return false;
/*      */     }
/* 1177 */     return paramBoolean;
/*      */   }
/*      */ 
/*      */   Level getLevelProperty(String paramString, Level paramLevel)
/*      */   {
/* 1184 */     String str = getProperty(paramString);
/* 1185 */     if (str == null) {
/* 1186 */       return paramLevel;
/*      */     }
/* 1188 */     Level localLevel = Level.findLevel(str.trim());
/* 1189 */     return localLevel != null ? localLevel : paramLevel;
/*      */   }
/*      */ 
/*      */   Filter getFilterProperty(String paramString, Filter paramFilter)
/*      */   {
/* 1197 */     String str = getProperty(paramString);
/*      */     try {
/* 1199 */       if (str != null) {
/* 1200 */         Class localClass = ClassLoader.getSystemClassLoader().loadClass(str);
/* 1201 */         return (Filter)localClass.newInstance();
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*      */ 
/* 1209 */     return paramFilter;
/*      */   }
/*      */ 
/*      */   Formatter getFormatterProperty(String paramString, Formatter paramFormatter)
/*      */   {
/* 1218 */     String str = getProperty(paramString);
/*      */     try {
/* 1220 */       if (str != null) {
/* 1221 */         Class localClass = ClassLoader.getSystemClassLoader().loadClass(str);
/* 1222 */         return (Formatter)localClass.newInstance();
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*      */ 
/* 1230 */     return paramFormatter;
/*      */   }
/*      */ 
/*      */   private synchronized void initializeGlobalHandlers()
/*      */   {
/* 1237 */     if (this.initializedGlobalHandlers) {
/* 1238 */       return;
/*      */     }
/*      */ 
/* 1241 */     this.initializedGlobalHandlers = true;
/*      */ 
/* 1243 */     if (this.deathImminent)
/*      */     {
/* 1247 */       return;
/*      */     }
/* 1249 */     loadLoggerHandlers(this.rootLogger, null, "handlers");
/*      */   }
/*      */ 
/*      */   void checkPermission()
/*      */   {
/* 1255 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1256 */     if (localSecurityManager != null)
/* 1257 */       localSecurityManager.checkPermission(this.controlPermission);
/*      */   }
/*      */ 
/*      */   public void checkAccess()
/*      */     throws SecurityException
/*      */   {
/* 1271 */     checkPermission();
/*      */   }
/*      */ 
/*      */   private synchronized void setLevelsOnExistingLoggers()
/*      */   {
/* 1341 */     Enumeration localEnumeration = this.props.propertyNames();
/*      */     String str2;
/*      */     Level localLevel;
/* 1342 */     while (localEnumeration.hasMoreElements()) {
/* 1343 */       String str1 = (String)localEnumeration.nextElement();
/* 1344 */       if (str1.endsWith(".level"))
/*      */       {
/* 1348 */         int i = str1.length() - 6;
/* 1349 */         str2 = str1.substring(0, i);
/* 1350 */         localLevel = getLevelProperty(str1, null);
/* 1351 */         if (localLevel == null) {
/* 1352 */           System.err.println("Bad level value for property: " + str1);
/*      */         }
/*      */         else
/* 1355 */           for (LoggerContext localLoggerContext : contexts()) {
/* 1356 */             Logger localLogger = localLoggerContext.findLogger(str2);
/* 1357 */             if (localLogger != null)
/*      */             {
/* 1360 */               localLogger.setLevel(localLevel);
/*      */             }
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static synchronized LoggingMXBean getLoggingMXBean()
/*      */   {
/* 1396 */     if (loggingMXBean == null) {
/* 1397 */       loggingMXBean = new Logging();
/*      */     }
/* 1399 */     return loggingMXBean;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  176 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Object run() {
/*  178 */         String str = null;
/*      */         try {
/*  180 */           str = System.getProperty("java.util.logging.manager");
/*  181 */           if (str != null)
/*      */             try {
/*  183 */               Class localClass1 = ClassLoader.getSystemClassLoader().loadClass(str);
/*  184 */               LogManager.access$102((LogManager)localClass1.newInstance());
/*      */             } catch (ClassNotFoundException localClassNotFoundException) {
/*  186 */               Class localClass2 = Thread.currentThread().getContextClassLoader().loadClass(str);
/*  187 */               LogManager.access$102((LogManager)localClass2.newInstance());
/*      */             }
/*      */         }
/*      */         catch (Exception localException) {
/*  191 */           System.err.println("Could not load Logmanager \"" + str + "\"");
/*  192 */           localException.printStackTrace();
/*      */         }
/*  194 */         if (LogManager.manager == null)
/*  195 */           LogManager.access$102(new LogManager());
/*      */         LogManager tmp122_119 = LogManager.manager; tmp122_119.getClass(); LogManager.manager.rootLogger = new LogManager.RootLogger(tmp122_119, null);
/*  200 */         LogManager.manager.addLogger(LogManager.manager.rootLogger);
/*  201 */         LogManager.manager.systemContext.addLocalLogger(LogManager.manager.rootLogger);
/*      */ 
/*  205 */         Logger.global.setLogManager(LogManager.manager);
/*  206 */         LogManager.manager.addLogger(Logger.global);
/*      */ 
/*  211 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private class Cleaner extends Thread
/*      */   {
/*      */     private Cleaner()
/*      */     {
/*  225 */       setContextClassLoader(null);
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*  231 */       LogManager localLogManager = LogManager.manager;
/*      */ 
/*  235 */       synchronized (LogManager.this)
/*      */       {
/*  237 */         LogManager.this.deathImminent = true;
/*  238 */         LogManager.this.initializedGlobalHandlers = true;
/*      */       }
/*      */ 
/*  242 */       LogManager.this.reset();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LogNode
/*      */   {
/*      */     HashMap<String, LogNode> children;
/*      */     LogManager.LoggerWeakRef loggerRef;
/*      */     LogNode parent;
/*      */     final LogManager.LoggerContext context;
/*      */ 
/*      */     LogNode(LogNode paramLogNode, LogManager.LoggerContext paramLoggerContext)
/*      */     {
/* 1282 */       this.parent = paramLogNode;
/* 1283 */       this.context = paramLoggerContext;
/*      */     }
/*      */ 
/*      */     void walkAndSetParent(Logger paramLogger)
/*      */     {
/* 1289 */       if (this.children == null) {
/* 1290 */         return;
/*      */       }
/* 1292 */       Iterator localIterator = this.children.values().iterator();
/* 1293 */       while (localIterator.hasNext()) {
/* 1294 */         LogNode localLogNode = (LogNode)localIterator.next();
/* 1295 */         LogManager.LoggerWeakRef localLoggerWeakRef = localLogNode.loggerRef;
/* 1296 */         Logger localLogger = localLoggerWeakRef == null ? null : (Logger)localLoggerWeakRef.get();
/* 1297 */         if (localLogger == null)
/* 1298 */           localLogNode.walkAndSetParent(paramLogger);
/*      */         else
/* 1300 */           LogManager.doSetParent(localLogger, paramLogger);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static class LoggerContext
/*      */   {
/*  474 */     private final Hashtable<String, LogManager.LoggerWeakRef> namedLoggers = new Hashtable();
/*      */     private final LogManager.LogNode root;
/*      */ 
/*      */     private LoggerContext()
/*      */     {
/*  479 */       this.root = new LogManager.LogNode(null, this);
/*      */     }
/*      */ 
/*      */     Logger demandLogger(String paramString1, String paramString2)
/*      */     {
/*  485 */       return LogManager.manager.demandLogger(paramString1, paramString2);
/*      */     }
/*      */ 
/*      */     synchronized Logger findLogger(String paramString) {
/*  489 */       LogManager.LoggerWeakRef localLoggerWeakRef = (LogManager.LoggerWeakRef)this.namedLoggers.get(paramString);
/*  490 */       if (localLoggerWeakRef == null) {
/*  491 */         return null;
/*      */       }
/*  493 */       Logger localLogger = (Logger)localLoggerWeakRef.get();
/*  494 */       if (localLogger == null)
/*      */       {
/*  497 */         removeLogger(paramString);
/*      */       }
/*  499 */       return localLogger;
/*      */     }
/*      */ 
/*      */     synchronized boolean addLocalLogger(Logger paramLogger)
/*      */     {
/*  505 */       String str = paramLogger.getName();
/*  506 */       if (str == null) {
/*  507 */         throw new NullPointerException();
/*      */       }
/*      */ 
/*  511 */       LogManager.manager.drainLoggerRefQueueBounded();
/*      */ 
/*  513 */       LogManager.LoggerWeakRef localLoggerWeakRef1 = (LogManager.LoggerWeakRef)this.namedLoggers.get(str);
/*  514 */       if (localLoggerWeakRef1 != null)
/*  515 */         if (localLoggerWeakRef1.get() == null)
/*      */         {
/*  519 */           removeLogger(str);
/*      */         }
/*      */         else
/*  522 */           return false;
/*      */       LogManager tmp63_60 = LogManager.manager; tmp63_60.getClass(); localLoggerWeakRef1 = new LogManager.LoggerWeakRef(tmp63_60, paramLogger);
/*  529 */       this.namedLoggers.put(str, localLoggerWeakRef1);
/*      */ 
/*  532 */       Level localLevel = LogManager.manager.getLevelProperty(str + ".level", null);
/*  533 */       if (localLevel != null) {
/*  534 */         LogManager.doSetLevel(paramLogger, localLevel);
/*      */       }
/*      */ 
/*  537 */       processParentHandlers(paramLogger, str);
/*      */ 
/*  540 */       LogManager.LogNode localLogNode1 = getNode(str);
/*  541 */       localLogNode1.loggerRef = localLoggerWeakRef1;
/*  542 */       Logger localLogger = null;
/*  543 */       LogManager.LogNode localLogNode2 = localLogNode1.parent;
/*  544 */       while (localLogNode2 != null) {
/*  545 */         LogManager.LoggerWeakRef localLoggerWeakRef2 = localLogNode2.loggerRef;
/*  546 */         if (localLoggerWeakRef2 != null) {
/*  547 */           localLogger = (Logger)localLoggerWeakRef2.get();
/*  548 */           if (localLogger != null) {
/*      */             break;
/*      */           }
/*      */         }
/*  552 */         localLogNode2 = localLogNode2.parent;
/*      */       }
/*      */ 
/*  555 */       if (localLogger != null) {
/*  556 */         LogManager.doSetParent(paramLogger, localLogger);
/*      */       }
/*      */ 
/*  559 */       localLogNode1.walkAndSetParent(paramLogger);
/*      */ 
/*  561 */       localLoggerWeakRef1.setNode(localLogNode1);
/*  562 */       return true;
/*      */     }
/*      */ 
/*      */     void removeLogger(String paramString) {
/*  566 */       this.namedLoggers.remove(paramString);
/*      */     }
/*      */ 
/*      */     synchronized Enumeration<String> getLoggerNames() {
/*  570 */       return this.namedLoggers.keys();
/*      */     }
/*      */ 
/*      */     private void processParentHandlers(final Logger paramLogger, final String paramString)
/*      */     {
/*  576 */       AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Void run() {
/*  578 */           if (paramLogger != LogManager.manager.rootLogger) {
/*  579 */             boolean bool = LogManager.manager.getBooleanProperty(paramString + ".useParentHandlers", true);
/*  580 */             if (!bool) {
/*  581 */               paramLogger.setUseParentHandlers(false);
/*      */             }
/*      */           }
/*  584 */           return null;
/*      */         }
/*      */       });
/*  588 */       int i = 1;
/*      */       while (true) {
/*  590 */         int j = paramString.indexOf(".", i);
/*  591 */         if (j < 0) {
/*      */           break;
/*      */         }
/*  594 */         String str = paramString.substring(0, j);
/*  595 */         if ((LogManager.manager.getProperty(str + ".level") != null) || (LogManager.manager.getProperty(str + ".handlers") != null))
/*      */         {
/*  599 */           demandLogger(str, null);
/*      */         }
/*  601 */         i = j + 1;
/*      */       }
/*      */     }
/*      */ 
/*      */     LogManager.LogNode getNode(String paramString)
/*      */     {
/*  608 */       if ((paramString == null) || (paramString.equals(""))) {
/*  609 */         return this.root;
/*      */       }
/*  611 */       Object localObject = this.root;
/*  612 */       while (paramString.length() > 0) {
/*  613 */         int i = paramString.indexOf(".");
/*      */         String str;
/*  615 */         if (i > 0) {
/*  616 */           str = paramString.substring(0, i);
/*  617 */           paramString = paramString.substring(i + 1);
/*      */         } else {
/*  619 */           str = paramString;
/*  620 */           paramString = "";
/*      */         }
/*  622 */         if (((LogManager.LogNode)localObject).children == null) {
/*  623 */           ((LogManager.LogNode)localObject).children = new HashMap();
/*      */         }
/*  625 */         LogManager.LogNode localLogNode = (LogManager.LogNode)((LogManager.LogNode)localObject).children.get(str);
/*  626 */         if (localLogNode == null) {
/*  627 */           localLogNode = new LogManager.LogNode((LogManager.LogNode)localObject, this);
/*  628 */           ((LogManager.LogNode)localObject).children.put(str, localLogNode);
/*      */         }
/*  630 */         localObject = localLogNode;
/*      */       }
/*  632 */       return localObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   final class LoggerWeakRef extends WeakReference<Logger>
/*      */   {
/*      */     private String name;
/*      */     private LogManager.LogNode node;
/*      */     private WeakReference<Logger> parentRef;
/*      */ 
/*      */     LoggerWeakRef(Logger arg2)
/*      */     {
/*  743 */       super(LogManager.this.loggerRefQueue);
/*      */ 
/*  745 */       this.name = localObject.getName();
/*      */     }
/*      */ 
/*      */     void dispose()
/*      */     {
/*  750 */       if (this.node != null)
/*      */       {
/*  753 */         this.node.context.removeLogger(this.name);
/*  754 */         this.name = null;
/*      */ 
/*  756 */         this.node.loggerRef = null;
/*  757 */         this.node = null;
/*      */       }
/*      */ 
/*  760 */       if (this.parentRef != null)
/*      */       {
/*  762 */         Logger localLogger = (Logger)this.parentRef.get();
/*  763 */         if (localLogger != null)
/*      */         {
/*  766 */           localLogger.removeChildLogger(this);
/*      */         }
/*  768 */         this.parentRef = null;
/*      */       }
/*      */     }
/*      */ 
/*      */     void setNode(LogManager.LogNode paramLogNode)
/*      */     {
/*  774 */       this.node = paramLogNode;
/*      */     }
/*      */ 
/*      */     void setParentRef(WeakReference<Logger> paramWeakReference)
/*      */     {
/*  779 */       this.parentRef = paramWeakReference;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class RootLogger extends Logger
/*      */   {
/*      */     private RootLogger()
/*      */     {
/* 1311 */       super(null);
/* 1312 */       setLevel(LogManager.defaultLevel);
/*      */     }
/*      */ 
/*      */     public void log(LogRecord paramLogRecord)
/*      */     {
/* 1317 */       LogManager.this.initializeGlobalHandlers();
/* 1318 */       super.log(paramLogRecord);
/*      */     }
/*      */ 
/*      */     public void addHandler(Handler paramHandler) {
/* 1322 */       LogManager.this.initializeGlobalHandlers();
/* 1323 */       super.addHandler(paramHandler);
/*      */     }
/*      */ 
/*      */     public void removeHandler(Handler paramHandler) {
/* 1327 */       LogManager.this.initializeGlobalHandlers();
/* 1328 */       super.removeHandler(paramHandler);
/*      */     }
/*      */ 
/*      */     public Handler[] getHandlers() {
/* 1332 */       LogManager.this.initializeGlobalHandlers();
/* 1333 */       return super.getHandlers();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class SystemLoggerContext extends LogManager.LoggerContext
/*      */   {
/*      */     SystemLoggerContext()
/*      */     {
/*  636 */       super();
/*      */     }
/*      */ 
/*      */     Logger demandLogger(String paramString1, String paramString2)
/*      */     {
/*  642 */       Object localObject = findLogger(paramString1);
/*  643 */       if (localObject == null)
/*      */       {
/*  645 */         Logger localLogger = new Logger(paramString1, paramString2);
/*      */         do
/*  647 */           if (addLocalLogger(localLogger))
/*      */           {
/*  650 */             localObject = localLogger;
/*      */           }
/*      */           else
/*      */           {
/*  663 */             localObject = findLogger(paramString1);
/*      */           }
/*  665 */         while (localObject == null);
/*      */       }
/*  667 */       return localObject;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.logging.LogManager
 * JD-Core Version:    0.6.2
 */