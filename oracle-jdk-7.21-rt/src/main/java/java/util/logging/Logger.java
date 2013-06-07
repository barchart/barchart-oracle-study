/*      */ package java.util.logging;
/*      */ 
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import sun.reflect.Reflection;
/*      */ 
/*      */ public class Logger
/*      */ {
/*  168 */   private static final Handler[] emptyHandlers = new Handler[0];
/*  169 */   private static final int offValue = Level.OFF.intValue();
/*      */   private LogManager manager;
/*      */   private String name;
/*  172 */   private final CopyOnWriteArrayList<Handler> handlers = new CopyOnWriteArrayList();
/*      */   private String resourceBundleName;
/*  175 */   private volatile boolean useParentHandlers = true;
/*      */   private volatile Filter filter;
/*      */   private boolean anonymous;
/*      */   private ResourceBundle catalog;
/*      */   private String catalogName;
/*      */   private Locale catalogLocale;
/*  185 */   private static Object treeLock = new Object();
/*      */   private volatile Logger parent;
/*      */   private ArrayList<LogManager.LoggerWeakRef> kids;
/*      */   private volatile Level levelObject;
/*      */   private volatile int levelValue;
/*      */   public static final String GLOBAL_LOGGER_NAME = "global";
/*      */ 
/*      */   @Deprecated
/*  232 */   public static final Logger global = new Logger("global");
/*      */   static final String SYSTEM_LOGGER_RB_NAME = "sun.util.logging.resources.logging";
/*      */ 
/*      */   public static final Logger getGlobal()
/*      */   {
/*  207 */     return global;
/*      */   }
/*      */ 
/*      */   protected Logger(String paramString1, String paramString2)
/*      */   {
/*  252 */     this.manager = LogManager.getLogManager();
/*  253 */     if (paramString2 != null)
/*      */     {
/*  255 */       setupResourceInfo(paramString2);
/*      */     }
/*  257 */     this.name = paramString1;
/*  258 */     this.levelValue = Level.INFO.intValue();
/*      */   }
/*      */ 
/*      */   private Logger(String paramString)
/*      */   {
/*  266 */     this.name = paramString;
/*  267 */     this.levelValue = Level.INFO.intValue();
/*      */   }
/*      */ 
/*      */   void setLogManager(LogManager paramLogManager)
/*      */   {
/*  273 */     this.manager = paramLogManager;
/*      */   }
/*      */ 
/*      */   private void checkPermission() throws SecurityException {
/*  277 */     if (!this.anonymous) {
/*  278 */       if (this.manager == null)
/*      */       {
/*  280 */         this.manager = LogManager.getLogManager();
/*      */       }
/*  282 */       this.manager.checkPermission();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Logger demandLogger(String paramString1, String paramString2)
/*      */   {
/*  307 */     LogManager localLogManager = LogManager.getLogManager();
/*  308 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  309 */     if ((localSecurityManager != null) && (!SystemLoggerHelper.disableCallerCheck))
/*      */     {
/*  312 */       Class localClass = Reflection.getCallerClass(3);
/*  313 */       if (localClass.getClassLoader() == null) {
/*  314 */         return localLogManager.demandSystemLogger(paramString1, paramString2);
/*      */       }
/*      */     }
/*  317 */     return localLogManager.demandLogger(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public static Logger getLogger(String paramString)
/*      */   {
/*  361 */     return demandLogger(paramString, null);
/*      */   }
/*      */ 
/*      */   public static Logger getLogger(String paramString1, String paramString2)
/*      */   {
/*  408 */     Logger localLogger = demandLogger(paramString1, paramString2);
/*  409 */     if (localLogger.resourceBundleName == null)
/*      */     {
/*  411 */       localLogger.setupResourceInfo(paramString2);
/*  412 */     } else if (!localLogger.resourceBundleName.equals(paramString2)) {
/*  413 */       throw new IllegalArgumentException(localLogger.resourceBundleName + " != " + paramString2);
/*      */     }
/*      */ 
/*  416 */     return localLogger;
/*      */   }
/*      */ 
/*      */   static Logger getPlatformLogger(String paramString)
/*      */   {
/*  423 */     LogManager localLogManager = LogManager.getLogManager();
/*      */ 
/*  427 */     Logger localLogger = localLogManager.demandSystemLogger(paramString, "sun.util.logging.resources.logging");
/*  428 */     return localLogger;
/*      */   }
/*      */ 
/*      */   public static Logger getAnonymousLogger()
/*      */   {
/*  452 */     return getAnonymousLogger(null);
/*      */   }
/*      */ 
/*      */   public static Logger getAnonymousLogger(String paramString)
/*      */   {
/*  483 */     LogManager localLogManager = LogManager.getLogManager();
/*      */ 
/*  485 */     localLogManager.drainLoggerRefQueueBounded();
/*  486 */     Logger localLogger1 = new Logger(null, paramString);
/*  487 */     localLogger1.anonymous = true;
/*  488 */     Logger localLogger2 = localLogManager.getLogger("");
/*  489 */     localLogger1.doSetParent(localLogger2);
/*  490 */     return localLogger1;
/*      */   }
/*      */ 
/*      */   public ResourceBundle getResourceBundle()
/*      */   {
/*  502 */     return findResourceBundle(getResourceBundleName());
/*      */   }
/*      */ 
/*      */   public String getResourceBundleName()
/*      */   {
/*  513 */     return this.resourceBundleName;
/*      */   }
/*      */ 
/*      */   public void setFilter(Filter paramFilter)
/*      */     throws SecurityException
/*      */   {
/*  528 */     checkPermission();
/*  529 */     this.filter = paramFilter;
/*      */   }
/*      */ 
/*      */   public Filter getFilter()
/*      */   {
/*  538 */     return this.filter;
/*      */   }
/*      */ 
/*      */   public void log(LogRecord paramLogRecord)
/*      */   {
/*  551 */     if ((paramLogRecord.getLevel().intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  552 */       return;
/*      */     }
/*  554 */     Filter localFilter = this.filter;
/*  555 */     if ((localFilter != null) && (!localFilter.isLoggable(paramLogRecord))) {
/*  556 */       return;
/*      */     }
/*      */ 
/*  562 */     Logger localLogger = this;
/*  563 */     while (localLogger != null) {
/*  564 */       for (Handler localHandler : localLogger.getHandlers()) {
/*  565 */         localHandler.publish(paramLogRecord);
/*      */       }
/*      */ 
/*  568 */       if (!localLogger.getUseParentHandlers())
/*      */       {
/*      */         break;
/*      */       }
/*  572 */       localLogger = localLogger.getParent();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void doLog(LogRecord paramLogRecord)
/*      */   {
/*  580 */     paramLogRecord.setLoggerName(this.name);
/*  581 */     String str = getEffectiveResourceBundleName();
/*  582 */     if ((str != null) && (!str.equals("sun.util.logging.resources.logging"))) {
/*  583 */       paramLogRecord.setResourceBundleName(str);
/*  584 */       paramLogRecord.setResourceBundle(findResourceBundle(str));
/*      */     }
/*  586 */     log(paramLogRecord);
/*      */   }
/*      */ 
/*      */   public void log(Level paramLevel, String paramString)
/*      */   {
/*  605 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  606 */       return;
/*      */     }
/*  608 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString);
/*  609 */     doLog(localLogRecord);
/*      */   }
/*      */ 
/*      */   public void log(Level paramLevel, String paramString, Object paramObject)
/*      */   {
/*  624 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  625 */       return;
/*      */     }
/*  627 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString);
/*  628 */     Object[] arrayOfObject = { paramObject };
/*  629 */     localLogRecord.setParameters(arrayOfObject);
/*  630 */     doLog(localLogRecord);
/*      */   }
/*      */ 
/*      */   public void log(Level paramLevel, String paramString, Object[] paramArrayOfObject)
/*      */   {
/*  645 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  646 */       return;
/*      */     }
/*  648 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString);
/*  649 */     localLogRecord.setParameters(paramArrayOfObject);
/*  650 */     doLog(localLogRecord);
/*      */   }
/*      */ 
/*      */   public void log(Level paramLevel, String paramString, Throwable paramThrowable)
/*      */   {
/*  670 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  671 */       return;
/*      */     }
/*  673 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString);
/*  674 */     localLogRecord.setThrown(paramThrowable);
/*  675 */     doLog(localLogRecord);
/*      */   }
/*      */ 
/*      */   public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3)
/*      */   {
/*  696 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  697 */       return;
/*      */     }
/*  699 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
/*  700 */     localLogRecord.setSourceClassName(paramString1);
/*  701 */     localLogRecord.setSourceMethodName(paramString2);
/*  702 */     doLog(localLogRecord);
/*      */   }
/*      */ 
/*      */   public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3, Object paramObject)
/*      */   {
/*  721 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  722 */       return;
/*      */     }
/*  724 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
/*  725 */     localLogRecord.setSourceClassName(paramString1);
/*  726 */     localLogRecord.setSourceMethodName(paramString2);
/*  727 */     Object[] arrayOfObject = { paramObject };
/*  728 */     localLogRecord.setParameters(arrayOfObject);
/*  729 */     doLog(localLogRecord);
/*      */   }
/*      */ 
/*      */   public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject)
/*      */   {
/*  748 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  749 */       return;
/*      */     }
/*  751 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
/*  752 */     localLogRecord.setSourceClassName(paramString1);
/*  753 */     localLogRecord.setSourceMethodName(paramString2);
/*  754 */     localLogRecord.setParameters(paramArrayOfObject);
/*  755 */     doLog(localLogRecord);
/*      */   }
/*      */ 
/*      */   public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3, Throwable paramThrowable)
/*      */   {
/*  779 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  780 */       return;
/*      */     }
/*  782 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
/*  783 */     localLogRecord.setSourceClassName(paramString1);
/*  784 */     localLogRecord.setSourceMethodName(paramString2);
/*  785 */     localLogRecord.setThrown(paramThrowable);
/*  786 */     doLog(localLogRecord);
/*      */   }
/*      */ 
/*      */   private void doLog(LogRecord paramLogRecord, String paramString)
/*      */   {
/*  798 */     paramLogRecord.setLoggerName(this.name);
/*  799 */     if (paramString != null) {
/*  800 */       paramLogRecord.setResourceBundleName(paramString);
/*  801 */       paramLogRecord.setResourceBundle(findResourceBundle(paramString));
/*      */     }
/*  803 */     log(paramLogRecord);
/*      */   }
/*      */ 
/*      */   public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4)
/*      */   {
/*  828 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  829 */       return;
/*      */     }
/*  831 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString4);
/*  832 */     localLogRecord.setSourceClassName(paramString1);
/*  833 */     localLogRecord.setSourceMethodName(paramString2);
/*  834 */     doLog(localLogRecord, paramString3);
/*      */   }
/*      */ 
/*      */   public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject)
/*      */   {
/*  859 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  860 */       return;
/*      */     }
/*  862 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString4);
/*  863 */     localLogRecord.setSourceClassName(paramString1);
/*  864 */     localLogRecord.setSourceMethodName(paramString2);
/*  865 */     Object[] arrayOfObject = { paramObject };
/*  866 */     localLogRecord.setParameters(arrayOfObject);
/*  867 */     doLog(localLogRecord, paramString3);
/*      */   }
/*      */ 
/*      */   public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4, Object[] paramArrayOfObject)
/*      */   {
/*  892 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  893 */       return;
/*      */     }
/*  895 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString4);
/*  896 */     localLogRecord.setSourceClassName(paramString1);
/*  897 */     localLogRecord.setSourceMethodName(paramString2);
/*  898 */     localLogRecord.setParameters(paramArrayOfObject);
/*  899 */     doLog(localLogRecord, paramString3);
/*      */   }
/*      */ 
/*      */   public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4, Throwable paramThrowable)
/*      */   {
/*  929 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/*  930 */       return;
/*      */     }
/*  932 */     LogRecord localLogRecord = new LogRecord(paramLevel, paramString4);
/*  933 */     localLogRecord.setSourceClassName(paramString1);
/*  934 */     localLogRecord.setSourceMethodName(paramString2);
/*  935 */     localLogRecord.setThrown(paramThrowable);
/*  936 */     doLog(localLogRecord, paramString3);
/*      */   }
/*      */ 
/*      */   public void entering(String paramString1, String paramString2)
/*      */   {
/*  955 */     if (Level.FINER.intValue() < this.levelValue) {
/*  956 */       return;
/*      */     }
/*  958 */     logp(Level.FINER, paramString1, paramString2, "ENTRY");
/*      */   }
/*      */ 
/*      */   public void entering(String paramString1, String paramString2, Object paramObject)
/*      */   {
/*  974 */     if (Level.FINER.intValue() < this.levelValue) {
/*  975 */       return;
/*      */     }
/*  977 */     Object[] arrayOfObject = { paramObject };
/*  978 */     logp(Level.FINER, paramString1, paramString2, "ENTRY {0}", arrayOfObject);
/*      */   }
/*      */ 
/*      */   public void entering(String paramString1, String paramString2, Object[] paramArrayOfObject)
/*      */   {
/*  995 */     if (Level.FINER.intValue() < this.levelValue) {
/*  996 */       return;
/*      */     }
/*  998 */     String str = "ENTRY";
/*  999 */     if (paramArrayOfObject == null) {
/* 1000 */       logp(Level.FINER, paramString1, paramString2, str);
/* 1001 */       return;
/*      */     }
/* 1003 */     for (int i = 0; i < paramArrayOfObject.length; i++) {
/* 1004 */       str = str + " {" + i + "}";
/*      */     }
/* 1006 */     logp(Level.FINER, paramString1, paramString2, str, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public void exiting(String paramString1, String paramString2)
/*      */   {
/* 1020 */     if (Level.FINER.intValue() < this.levelValue) {
/* 1021 */       return;
/*      */     }
/* 1023 */     logp(Level.FINER, paramString1, paramString2, "RETURN");
/*      */   }
/*      */ 
/*      */   public void exiting(String paramString1, String paramString2, Object paramObject)
/*      */   {
/* 1040 */     if (Level.FINER.intValue() < this.levelValue) {
/* 1041 */       return;
/*      */     }
/* 1043 */     Object[] arrayOfObject = { paramObject };
/* 1044 */     logp(Level.FINER, paramString1, paramString2, "RETURN {0}", paramObject);
/*      */   }
/*      */ 
/*      */   public void throwing(String paramString1, String paramString2, Throwable paramThrowable)
/*      */   {
/* 1069 */     if ((Level.FINER.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/* 1070 */       return;
/*      */     }
/* 1072 */     LogRecord localLogRecord = new LogRecord(Level.FINER, "THROW");
/* 1073 */     localLogRecord.setSourceClassName(paramString1);
/* 1074 */     localLogRecord.setSourceMethodName(paramString2);
/* 1075 */     localLogRecord.setThrown(paramThrowable);
/* 1076 */     doLog(localLogRecord);
/*      */   }
/*      */ 
/*      */   public void severe(String paramString)
/*      */   {
/* 1093 */     if (Level.SEVERE.intValue() < this.levelValue) {
/* 1094 */       return;
/*      */     }
/* 1096 */     log(Level.SEVERE, paramString);
/*      */   }
/*      */ 
/*      */   public void warning(String paramString)
/*      */   {
/* 1109 */     if (Level.WARNING.intValue() < this.levelValue) {
/* 1110 */       return;
/*      */     }
/* 1112 */     log(Level.WARNING, paramString);
/*      */   }
/*      */ 
/*      */   public void info(String paramString)
/*      */   {
/* 1125 */     if (Level.INFO.intValue() < this.levelValue) {
/* 1126 */       return;
/*      */     }
/* 1128 */     log(Level.INFO, paramString);
/*      */   }
/*      */ 
/*      */   public void config(String paramString)
/*      */   {
/* 1141 */     if (Level.CONFIG.intValue() < this.levelValue) {
/* 1142 */       return;
/*      */     }
/* 1144 */     log(Level.CONFIG, paramString);
/*      */   }
/*      */ 
/*      */   public void fine(String paramString)
/*      */   {
/* 1157 */     if (Level.FINE.intValue() < this.levelValue) {
/* 1158 */       return;
/*      */     }
/* 1160 */     log(Level.FINE, paramString);
/*      */   }
/*      */ 
/*      */   public void finer(String paramString)
/*      */   {
/* 1173 */     if (Level.FINER.intValue() < this.levelValue) {
/* 1174 */       return;
/*      */     }
/* 1176 */     log(Level.FINER, paramString);
/*      */   }
/*      */ 
/*      */   public void finest(String paramString)
/*      */   {
/* 1189 */     if (Level.FINEST.intValue() < this.levelValue) {
/* 1190 */       return;
/*      */     }
/* 1192 */     log(Level.FINEST, paramString);
/*      */   }
/*      */ 
/*      */   public void setLevel(Level paramLevel)
/*      */     throws SecurityException
/*      */   {
/* 1214 */     checkPermission();
/* 1215 */     synchronized (treeLock) {
/* 1216 */       this.levelObject = paramLevel;
/* 1217 */       updateEffectiveLevel();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Level getLevel()
/*      */   {
/* 1229 */     return this.levelObject;
/*      */   }
/*      */ 
/*      */   public boolean isLoggable(Level paramLevel)
/*      */   {
/* 1241 */     if ((paramLevel.intValue() < this.levelValue) || (this.levelValue == offValue)) {
/* 1242 */       return false;
/*      */     }
/* 1244 */     return true;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/* 1252 */     return this.name;
/*      */   }
/*      */ 
/*      */   public void addHandler(Handler paramHandler)
/*      */     throws SecurityException
/*      */   {
/* 1268 */     paramHandler.getClass();
/* 1269 */     checkPermission();
/* 1270 */     this.handlers.add(paramHandler);
/*      */   }
/*      */ 
/*      */   public void removeHandler(Handler paramHandler)
/*      */     throws SecurityException
/*      */   {
/* 1283 */     checkPermission();
/* 1284 */     if (paramHandler == null) {
/* 1285 */       return;
/*      */     }
/* 1287 */     this.handlers.remove(paramHandler);
/*      */   }
/*      */ 
/*      */   public Handler[] getHandlers()
/*      */   {
/* 1296 */     return (Handler[])this.handlers.toArray(emptyHandlers);
/*      */   }
/*      */ 
/*      */   public void setUseParentHandlers(boolean paramBoolean)
/*      */   {
/* 1311 */     checkPermission();
/* 1312 */     this.useParentHandlers = paramBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getUseParentHandlers()
/*      */   {
/* 1322 */     return this.useParentHandlers;
/*      */   }
/*      */ 
/*      */   private static ResourceBundle findSystemResourceBundle(Locale paramLocale)
/*      */   {
/* 1335 */     return (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public ResourceBundle run() {
/*      */         try {
/* 1338 */           return ResourceBundle.getBundle("sun.util.logging.resources.logging", this.val$locale, ClassLoader.getSystemClassLoader());
/*      */         }
/*      */         catch (MissingResourceException localMissingResourceException)
/*      */         {
/* 1342 */           throw new InternalError(localMissingResourceException.toString());
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private synchronized ResourceBundle findResourceBundle(String paramString)
/*      */   {
/* 1350 */     if (paramString == null) {
/* 1351 */       return null;
/*      */     }
/*      */ 
/* 1354 */     Locale localLocale = Locale.getDefault();
/*      */ 
/* 1357 */     if ((this.catalog != null) && (localLocale == this.catalogLocale) && (paramString == this.catalogName))
/*      */     {
/* 1359 */       return this.catalog;
/*      */     }
/*      */ 
/* 1362 */     if (paramString.equals("sun.util.logging.resources.logging")) {
/* 1363 */       this.catalog = findSystemResourceBundle(localLocale);
/* 1364 */       this.catalogName = paramString;
/* 1365 */       this.catalogLocale = localLocale;
/* 1366 */       return this.catalog;
/*      */     }
/*      */ 
/* 1371 */     Object localObject = Thread.currentThread().getContextClassLoader();
/* 1372 */     if (localObject == null)
/* 1373 */       localObject = ClassLoader.getSystemClassLoader();
/*      */     try
/*      */     {
/* 1376 */       this.catalog = ResourceBundle.getBundle(paramString, localLocale, (ClassLoader)localObject);
/* 1377 */       this.catalogName = paramString;
/* 1378 */       this.catalogLocale = localLocale;
/* 1379 */       return this.catalog;
/*      */     }
/*      */     catch (MissingResourceException localMissingResourceException1)
/*      */     {
/* 1387 */       for (int i = 0; ; i++) {
/* 1388 */         Class localClass = Reflection.getCallerClass(i);
/* 1389 */         if (localClass == null) {
/*      */           break;
/*      */         }
/* 1392 */         ClassLoader localClassLoader = localClass.getClassLoader();
/* 1393 */         if (localClassLoader == null) {
/* 1394 */           localClassLoader = ClassLoader.getSystemClassLoader();
/*      */         }
/* 1396 */         if (localObject != localClassLoader)
/*      */         {
/* 1400 */           localObject = localClassLoader;
/*      */           try {
/* 1402 */             this.catalog = ResourceBundle.getBundle(paramString, localLocale, (ClassLoader)localObject);
/* 1403 */             this.catalogName = paramString;
/* 1404 */             this.catalogLocale = localLocale;
/* 1405 */             return this.catalog;
/*      */           }
/*      */           catch (MissingResourceException localMissingResourceException2)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/* 1412 */       if (paramString.equals(this.catalogName))
/*      */       {
/* 1415 */         return this.catalog;
/*      */       }
/*      */     }
/* 1418 */     return null;
/*      */   }
/*      */ 
/*      */   private synchronized void setupResourceInfo(String paramString)
/*      */   {
/* 1427 */     if (paramString == null) {
/* 1428 */       return;
/*      */     }
/* 1430 */     ResourceBundle localResourceBundle = findResourceBundle(paramString);
/* 1431 */     if (localResourceBundle == null)
/*      */     {
/* 1433 */       throw new MissingResourceException("Can't find " + paramString + " bundle", paramString, "");
/*      */     }
/* 1435 */     this.resourceBundleName = paramString;
/*      */   }
/*      */ 
/*      */   public Logger getParent()
/*      */   {
/* 1457 */     return this.parent;
/*      */   }
/*      */ 
/*      */   public void setParent(Logger paramLogger)
/*      */   {
/* 1471 */     if (paramLogger == null) {
/* 1472 */       throw new NullPointerException();
/*      */     }
/* 1474 */     this.manager.checkPermission();
/* 1475 */     doSetParent(paramLogger);
/*      */   }
/*      */ 
/*      */   private void doSetParent(Logger paramLogger)
/*      */   {
/* 1485 */     synchronized (treeLock)
/*      */     {
/* 1488 */       LogManager.LoggerWeakRef localLoggerWeakRef = null;
/*      */       Iterator localIterator;
/* 1489 */       if (this.parent != null)
/*      */       {
/* 1491 */         for (localIterator = this.parent.kids.iterator(); localIterator.hasNext(); ) {
/* 1492 */           localLoggerWeakRef = (LogManager.LoggerWeakRef)localIterator.next();
/* 1493 */           Logger localLogger = (Logger)localLoggerWeakRef.get();
/* 1494 */           if (localLogger == this)
/*      */           {
/* 1496 */             localIterator.remove();
/* 1497 */             break;
/*      */           }
/* 1499 */           localLoggerWeakRef = null;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1506 */       this.parent = paramLogger;
/* 1507 */       if (this.parent.kids == null) {
/* 1508 */         this.parent.kids = new ArrayList(2);
/*      */       }
/* 1510 */       if (localLoggerWeakRef == null)
/*      */       {
/*      */         LogManager tmp120_117 = this.manager; tmp120_117.getClass(); localLoggerWeakRef = new LogManager.LoggerWeakRef(tmp120_117, this);
/*      */       }
/* 1514 */       localLoggerWeakRef.setParentRef(new WeakReference(this.parent));
/* 1515 */       this.parent.kids.add(localLoggerWeakRef);
/*      */ 
/* 1519 */       updateEffectiveLevel();
/*      */     }
/*      */   }
/*      */ 
/*      */   final void removeChildLogger(LogManager.LoggerWeakRef paramLoggerWeakRef)
/*      */   {
/*      */     Iterator localIterator;
/* 1528 */     synchronized (treeLock) {
/* 1529 */       for (localIterator = this.kids.iterator(); localIterator.hasNext(); ) {
/* 1530 */         LogManager.LoggerWeakRef localLoggerWeakRef = (LogManager.LoggerWeakRef)localIterator.next();
/* 1531 */         if (localLoggerWeakRef == paramLoggerWeakRef) {
/* 1532 */           localIterator.remove();
/* 1533 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateEffectiveLevel()
/*      */   {
/*      */     int i;
/* 1547 */     if (this.levelObject != null) {
/* 1548 */       i = this.levelObject.intValue();
/*      */     }
/* 1550 */     else if (this.parent != null) {
/* 1551 */       i = this.parent.levelValue;
/*      */     }
/*      */     else {
/* 1554 */       i = Level.INFO.intValue();
/*      */     }
/*      */ 
/* 1559 */     if (this.levelValue == i) {
/* 1560 */       return;
/*      */     }
/*      */ 
/* 1563 */     this.levelValue = i;
/*      */ 
/* 1568 */     if (this.kids != null)
/* 1569 */       for (int j = 0; j < this.kids.size(); j++) {
/* 1570 */         LogManager.LoggerWeakRef localLoggerWeakRef = (LogManager.LoggerWeakRef)this.kids.get(j);
/* 1571 */         Logger localLogger = (Logger)localLoggerWeakRef.get();
/* 1572 */         if (localLogger != null)
/* 1573 */           localLogger.updateEffectiveLevel();
/*      */       }
/*      */   }
/*      */ 
/*      */   private String getEffectiveResourceBundleName()
/*      */   {
/* 1584 */     Logger localLogger = this;
/* 1585 */     while (localLogger != null) {
/* 1586 */       String str = localLogger.getResourceBundleName();
/* 1587 */       if (str != null) {
/* 1588 */         return str;
/*      */       }
/* 1590 */       localLogger = localLogger.getParent();
/*      */     }
/* 1592 */     return null;
/*      */   }
/*      */ 
/*      */   private static class SystemLoggerHelper
/*      */   {
/*  295 */     static boolean disableCallerCheck = getBooleanProperty("sun.util.logging.disableCallerCheck");
/*      */ 
/*  297 */     private static boolean getBooleanProperty(String paramString) { String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public String run() {
/*  299 */           return System.getProperty(this.val$key);
/*      */         }
/*      */       });
/*  302 */       return Boolean.valueOf(str).booleanValue();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.logging.Logger
 * JD-Core Version:    0.6.2
 */