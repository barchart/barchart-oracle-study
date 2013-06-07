/*      */ package java.util.prefs;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ import java.util.TreeMap;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ class FileSystemPreferences extends AbstractPreferences
/*      */ {
/*   54 */   private static final int SYNC_INTERVAL = Math.max(1, Integer.parseInt((String)AccessController.doPrivileged(new GetPropertyAction("java.util.prefs.syncInterval", "30"))));
/*      */   private static File systemRootDir;
/*      */   private static boolean isSystemRootWritable;
/*      */   private static File userRootDir;
/*      */   private static boolean isUserRootWritable;
/*   91 */   static Preferences userRoot = null;
/*      */   static Preferences systemRoot;
/*      */   private static final int USER_READ_WRITE = 384;
/*      */   private static final int USER_RW_ALL_READ = 420;
/*      */   private static final int USER_RWX_ALL_RX = 493;
/*      */   private static final int USER_RWX = 448;
/*      */   static File userLockFile;
/*      */   static File systemLockFile;
/*  245 */   private static int userRootLockHandle = 0;
/*      */ 
/*  252 */   private static int systemRootLockHandle = 0;
/*      */   private final File dir;
/*      */   private final File prefsFile;
/*      */   private final File tmpFile;
/*      */   private static File userRootModFile;
/*  288 */   private static boolean isUserRootModified = false;
/*      */   private static long userRootModTime;
/*      */   private static File systemRootModFile;
/*  305 */   private static boolean isSystemRootModified = false;
/*      */   private static long systemRootModTime;
/*  321 */   private Map<String, String> prefsCache = null;
/*      */ 
/*  332 */   private long lastSyncTime = 0L;
/*      */   private static final int EAGAIN = 11;
/*      */   private static final int EACCES = 13;
/*      */   private static final int LOCK_HANDLE = 0;
/*      */   private static final int ERROR_CODE = 1;
/*  357 */   final List<Change> changeLog = new ArrayList();
/*      */ 
/*  416 */   NodeCreate nodeCreate = null;
/*      */ 
/*  426 */   private static Timer syncTimer = new Timer(true);
/*      */   private final boolean isUserNode;
/*  661 */   private static final String[] EMPTY_STRING_ARRAY = new String[0];
/*      */ 
/*  969 */   private static int INIT_SLEEP_TIME = 50;
/*      */ 
/*  974 */   private static int MAX_ATTEMPTS = 5;
/*      */ 
/*      */   private static PlatformLogger getLogger()
/*      */   {
/*   65 */     return PlatformLogger.getLogger("java.util.prefs");
/*      */   }
/*      */ 
/*      */   static synchronized Preferences getUserRoot()
/*      */   {
/*   94 */     if (userRoot == null) {
/*   95 */       setupUserRoot();
/*   96 */       userRoot = new FileSystemPreferences(true);
/*      */     }
/*   98 */     return userRoot;
/*      */   }
/*      */ 
/*      */   private static void setupUserRoot() {
/*  102 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/*  104 */         FileSystemPreferences.access$002(new File(System.getProperty("java.util.prefs.userRoot", System.getProperty("user.home")), ".java/.userPrefs"));
/*      */ 
/*  108 */         if (!FileSystemPreferences.userRootDir.exists()) {
/*  109 */           if (FileSystemPreferences.userRootDir.mkdirs()) {
/*      */             try {
/*  111 */               FileSystemPreferences.chmod(FileSystemPreferences.userRootDir.getCanonicalPath(), 448);
/*      */             } catch (IOException localIOException1) {
/*  113 */               FileSystemPreferences.access$200().warning("Could not change permissions on userRoot directory. ");
/*      */             }
/*      */ 
/*  116 */             FileSystemPreferences.access$200().info("Created user preferences directory.");
/*      */           }
/*      */           else {
/*  119 */             FileSystemPreferences.access$200().warning("Couldn't create user preferences directory. User preferences are unusable.");
/*      */           }
/*      */         }
/*  122 */         FileSystemPreferences.access$302(FileSystemPreferences.userRootDir.canWrite());
/*  123 */         String str = System.getProperty("user.name");
/*  124 */         FileSystemPreferences.userLockFile = new File(FileSystemPreferences.userRootDir, ".user.lock." + str);
/*  125 */         FileSystemPreferences.access$402(new File(FileSystemPreferences.userRootDir, ".userRootModFile." + str));
/*      */ 
/*  127 */         if (!FileSystemPreferences.userRootModFile.exists())
/*      */           try
/*      */           {
/*  130 */             FileSystemPreferences.userRootModFile.createNewFile();
/*      */ 
/*  132 */             int i = FileSystemPreferences.chmod(FileSystemPreferences.userRootModFile.getCanonicalPath(), 384);
/*      */ 
/*  134 */             if (i != 0) {
/*  135 */               FileSystemPreferences.access$200().warning("Problem creating userRoot mod file. Chmod failed on " + FileSystemPreferences.userRootModFile.getCanonicalPath() + " Unix error code " + i);
/*      */             }
/*      */           }
/*      */           catch (IOException localIOException2)
/*      */           {
/*  140 */             FileSystemPreferences.access$200().warning(localIOException2.toString());
/*      */           }
/*  142 */         FileSystemPreferences.access$502(FileSystemPreferences.userRootModFile.lastModified());
/*  143 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   static synchronized Preferences getSystemRoot()
/*      */   {
/*  155 */     if (systemRoot == null) {
/*  156 */       setupSystemRoot();
/*  157 */       systemRoot = new FileSystemPreferences(false);
/*      */     }
/*  159 */     return systemRoot;
/*      */   }
/*      */ 
/*      */   private static void setupSystemRoot() {
/*  163 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/*  165 */         String str = System.getProperty("java.util.prefs.systemRoot", "/etc/.java");
/*      */ 
/*  167 */         FileSystemPreferences.access$602(new File(str, ".systemPrefs"));
/*      */ 
/*  170 */         if (!FileSystemPreferences.systemRootDir.exists())
/*      */         {
/*  173 */           FileSystemPreferences.access$602(new File(System.getProperty("java.home"), ".systemPrefs"));
/*      */ 
/*  176 */           if (!FileSystemPreferences.systemRootDir.exists()) {
/*  177 */             if (FileSystemPreferences.systemRootDir.mkdirs()) {
/*  178 */               FileSystemPreferences.access$200().info("Created system preferences directory in java.home.");
/*      */               try
/*      */               {
/*  182 */                 FileSystemPreferences.chmod(FileSystemPreferences.systemRootDir.getCanonicalPath(), 493);
/*      */               } catch (IOException localIOException1) {
/*      */               }
/*      */             }
/*      */             else {
/*  187 */               FileSystemPreferences.access$200().warning("Could not create system preferences directory. System preferences are unusable.");
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  193 */         FileSystemPreferences.access$702(FileSystemPreferences.systemRootDir.canWrite());
/*  194 */         FileSystemPreferences.systemLockFile = new File(FileSystemPreferences.systemRootDir, ".system.lock");
/*  195 */         FileSystemPreferences.access$802(new File(FileSystemPreferences.systemRootDir, ".systemRootModFile"));
/*      */ 
/*  197 */         if ((!FileSystemPreferences.systemRootModFile.exists()) && (FileSystemPreferences.isSystemRootWritable))
/*      */           try
/*      */           {
/*  200 */             FileSystemPreferences.systemRootModFile.createNewFile();
/*  201 */             int i = FileSystemPreferences.chmod(FileSystemPreferences.systemRootModFile.getCanonicalPath(), 420);
/*      */ 
/*  203 */             if (i != 0)
/*  204 */               FileSystemPreferences.access$200().warning("Chmod failed on " + FileSystemPreferences.systemRootModFile.getCanonicalPath() + " Unix error code " + i);
/*      */           }
/*      */           catch (IOException localIOException2) {
/*  207 */             FileSystemPreferences.access$200().warning(localIOException2.toString());
/*      */           }
/*  209 */         FileSystemPreferences.access$902(FileSystemPreferences.systemRootModFile.lastModified());
/*  210 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private void replayChanges()
/*      */   {
/*  422 */     int i = 0; for (int j = this.changeLog.size(); i < j; i++)
/*  423 */       ((Change)this.changeLog.get(i)).replay();
/*      */   }
/*      */ 
/*      */   private static void syncWorld()
/*      */   {
/*      */     Preferences localPreferences1;
/*      */     Preferences localPreferences2;
/*  457 */     synchronized (FileSystemPreferences.class) {
/*  458 */       localPreferences1 = userRoot;
/*  459 */       localPreferences2 = systemRoot;
/*      */     }
/*      */     try
/*      */     {
/*  463 */       if (localPreferences1 != null)
/*  464 */         localPreferences1.flush();
/*      */     } catch (BackingStoreException localBackingStoreException1) {
/*  466 */       getLogger().warning("Couldn't flush user prefs: " + localBackingStoreException1);
/*      */     }
/*      */     try
/*      */     {
/*  470 */       if (localPreferences2 != null)
/*  471 */         localPreferences2.flush();
/*      */     } catch (BackingStoreException localBackingStoreException2) {
/*  473 */       getLogger().warning("Couldn't flush system prefs: " + localBackingStoreException2);
/*      */     }
/*      */   }
/*      */ 
/*      */   private FileSystemPreferences(boolean paramBoolean)
/*      */   {
/*  484 */     super(null, "");
/*  485 */     this.isUserNode = paramBoolean;
/*  486 */     this.dir = (paramBoolean ? userRootDir : systemRootDir);
/*  487 */     this.prefsFile = new File(this.dir, "prefs.xml");
/*  488 */     this.tmpFile = new File(this.dir, "prefs.tmp");
/*      */   }
/*      */ 
/*      */   private FileSystemPreferences(FileSystemPreferences paramFileSystemPreferences, String paramString)
/*      */   {
/*  497 */     super(paramFileSystemPreferences, paramString);
/*  498 */     this.isUserNode = paramFileSystemPreferences.isUserNode;
/*  499 */     this.dir = new File(paramFileSystemPreferences.dir, dirName(paramString));
/*  500 */     this.prefsFile = new File(this.dir, "prefs.xml");
/*  501 */     this.tmpFile = new File(this.dir, "prefs.tmp");
/*  502 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/*  504 */         FileSystemPreferences.this.newNode = (!FileSystemPreferences.this.dir.exists());
/*  505 */         return null;
/*      */       }
/*      */     });
/*  508 */     if (this.newNode)
/*      */     {
/*  510 */       this.prefsCache = new TreeMap();
/*  511 */       this.nodeCreate = new NodeCreate(null);
/*  512 */       this.changeLog.add(this.nodeCreate);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isUserNode() {
/*  517 */     return this.isUserNode;
/*      */   }
/*      */ 
/*      */   protected void putSpi(String paramString1, String paramString2) {
/*  521 */     initCacheIfNecessary();
/*  522 */     this.changeLog.add(new Put(paramString1, paramString2));
/*  523 */     this.prefsCache.put(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   protected String getSpi(String paramString) {
/*  527 */     initCacheIfNecessary();
/*  528 */     return (String)this.prefsCache.get(paramString);
/*      */   }
/*      */ 
/*      */   protected void removeSpi(String paramString) {
/*  532 */     initCacheIfNecessary();
/*  533 */     this.changeLog.add(new Remove(paramString));
/*  534 */     this.prefsCache.remove(paramString);
/*      */   }
/*      */ 
/*      */   private void initCacheIfNecessary()
/*      */   {
/*  546 */     if (this.prefsCache != null)
/*  547 */       return;
/*      */     try
/*      */     {
/*  550 */       loadCache();
/*      */     }
/*      */     catch (Exception localException) {
/*  553 */       this.prefsCache = new TreeMap();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void loadCache()
/*      */     throws BackingStoreException
/*      */   {
/*      */     try
/*      */     {
/*  567 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Void run() throws BackingStoreException {
/*  570 */           TreeMap localTreeMap = new TreeMap();
/*  571 */           long l = 0L;
/*      */           try {
/*  573 */             l = FileSystemPreferences.this.prefsFile.lastModified();
/*  574 */             FileInputStream localFileInputStream = new FileInputStream(FileSystemPreferences.this.prefsFile); Object localObject1 = null;
/*      */             try { XmlSupport.importMap(localFileInputStream, localTreeMap); }
/*      */             catch (Throwable localThrowable2)
/*      */             {
/*  574 */               localObject1 = localThrowable2; throw localThrowable2;
/*      */             } finally {
/*  576 */               if (localFileInputStream != null) if (localObject1 != null) try { localFileInputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localFileInputStream.close();  
/*      */             }
/*      */           } catch (Exception localException) { if ((localException instanceof InvalidPreferencesFormatException)) {
/*  579 */               FileSystemPreferences.access$200().warning("Invalid preferences format in " + FileSystemPreferences.this.prefsFile.getPath());
/*      */ 
/*  581 */               FileSystemPreferences.this.prefsFile.renameTo(new File(FileSystemPreferences.this.prefsFile.getParentFile(), "IncorrectFormatPrefs.xml"));
/*      */ 
/*  584 */               localTreeMap = new TreeMap();
/*  585 */             } else if ((localException instanceof FileNotFoundException)) {
/*  586 */               FileSystemPreferences.access$200().warning("Prefs file removed in background " + FileSystemPreferences.this.prefsFile.getPath());
/*      */             }
/*      */             else {
/*  589 */               throw new BackingStoreException(localException);
/*      */             }
/*      */           }
/*      */ 
/*  593 */           FileSystemPreferences.this.prefsCache = localTreeMap;
/*  594 */           FileSystemPreferences.this.lastSyncTime = l;
/*  595 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  599 */       throw ((BackingStoreException)localPrivilegedActionException.getException());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void writeBackCache()
/*      */     throws BackingStoreException
/*      */   {
/*      */     try
/*      */     {
/*  614 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Void run() throws BackingStoreException {
/*      */           try {
/*  618 */             if ((!FileSystemPreferences.this.dir.exists()) && (!FileSystemPreferences.this.dir.mkdirs())) {
/*  619 */               throw new BackingStoreException(FileSystemPreferences.this.dir + " create failed.");
/*      */             }
/*  621 */             FileOutputStream localFileOutputStream = new FileOutputStream(FileSystemPreferences.this.tmpFile); Object localObject1 = null;
/*      */             try { XmlSupport.exportMap(localFileOutputStream, FileSystemPreferences.this.prefsCache); }
/*      */             catch (Throwable localThrowable2)
/*      */             {
/*  621 */               localObject1 = localThrowable2; throw localThrowable2;
/*      */             } finally {
/*  623 */               if (localFileOutputStream != null) if (localObject1 != null) try { localFileOutputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localFileOutputStream.close(); 
/*      */             }
/*  624 */             if (!FileSystemPreferences.this.tmpFile.renameTo(FileSystemPreferences.this.prefsFile))
/*  625 */               throw new BackingStoreException("Can't rename " + FileSystemPreferences.this.tmpFile + " to " + FileSystemPreferences.this.prefsFile);
/*      */           }
/*      */           catch (Exception localException) {
/*  628 */             if ((localException instanceof BackingStoreException))
/*  629 */               throw ((BackingStoreException)localException);
/*  630 */             throw new BackingStoreException(localException);
/*      */           }
/*  632 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  636 */       throw ((BackingStoreException)localPrivilegedActionException.getException());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String[] keysSpi() {
/*  641 */     initCacheIfNecessary();
/*  642 */     return (String[])this.prefsCache.keySet().toArray(new String[this.prefsCache.size()]);
/*      */   }
/*      */ 
/*      */   protected String[] childrenNamesSpi() {
/*  646 */     return (String[])AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public String[] run() {
/*  649 */         ArrayList localArrayList = new ArrayList();
/*  650 */         File[] arrayOfFile = FileSystemPreferences.this.dir.listFiles();
/*  651 */         if (arrayOfFile != null) {
/*  652 */           for (int i = 0; i < arrayOfFile.length; i++)
/*  653 */             if (arrayOfFile[i].isDirectory())
/*  654 */               localArrayList.add(FileSystemPreferences.nodeName(arrayOfFile[i].getName()));
/*      */         }
/*  656 */         return (String[])localArrayList.toArray(FileSystemPreferences.EMPTY_STRING_ARRAY);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   protected AbstractPreferences childSpi(String paramString)
/*      */   {
/*  664 */     return new FileSystemPreferences(this, paramString);
/*      */   }
/*      */ 
/*      */   public void removeNode() throws BackingStoreException {
/*  668 */     synchronized (isUserNode() ? userLockFile : systemLockFile)
/*      */     {
/*  670 */       if (!lockFile(false))
/*  671 */         throw new BackingStoreException("Couldn't get file lock.");
/*      */       try {
/*  673 */         super.removeNode();
/*      */       } finally {
/*  675 */         unlockFile();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void removeNodeSpi()
/*      */     throws BackingStoreException
/*      */   {
/*      */     try
/*      */     {
/*  685 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Void run() throws BackingStoreException {
/*  688 */           if (FileSystemPreferences.this.changeLog.contains(FileSystemPreferences.this.nodeCreate)) {
/*  689 */             FileSystemPreferences.this.changeLog.remove(FileSystemPreferences.this.nodeCreate);
/*  690 */             FileSystemPreferences.this.nodeCreate = null;
/*  691 */             return null;
/*      */           }
/*  693 */           if (!FileSystemPreferences.this.dir.exists())
/*  694 */             return null;
/*  695 */           FileSystemPreferences.this.prefsFile.delete();
/*  696 */           FileSystemPreferences.this.tmpFile.delete();
/*      */ 
/*  698 */           File[] arrayOfFile = FileSystemPreferences.this.dir.listFiles();
/*  699 */           if (arrayOfFile.length != 0) {
/*  700 */             FileSystemPreferences.access$200().warning("Found extraneous files when removing node: " + Arrays.asList(arrayOfFile));
/*      */ 
/*  703 */             for (int i = 0; i < arrayOfFile.length; i++)
/*  704 */               arrayOfFile[i].delete();
/*      */           }
/*  706 */           if (!FileSystemPreferences.this.dir.delete()) {
/*  707 */             throw new BackingStoreException("Couldn't delete dir: " + FileSystemPreferences.this.dir);
/*      */           }
/*  709 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  713 */       throw ((BackingStoreException)localPrivilegedActionException.getException());
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void sync() throws BackingStoreException {
/*  718 */     boolean bool1 = isUserNode();
/*      */     boolean bool2;
/*  721 */     if (bool1) {
/*  722 */       bool2 = false;
/*      */     }
/*      */     else
/*      */     {
/*  726 */       bool2 = !isSystemRootWritable;
/*      */     }
/*  728 */     synchronized (isUserNode() ? userLockFile : systemLockFile) {
/*  729 */       if (!lockFile(bool2))
/*  730 */         throw new BackingStoreException("Couldn't get file lock.");
/*  731 */       final Long localLong = (Long)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Long run()
/*      */         {
/*      */           long l;
/*  736 */           if (FileSystemPreferences.this.isUserNode()) {
/*  737 */             l = FileSystemPreferences.userRootModFile.lastModified();
/*  738 */             FileSystemPreferences.access$2102(FileSystemPreferences.userRootModTime == l);
/*      */           } else {
/*  740 */             l = FileSystemPreferences.systemRootModFile.lastModified();
/*  741 */             FileSystemPreferences.access$2202(FileSystemPreferences.systemRootModTime == l);
/*      */           }
/*  743 */           return new Long(l);
/*      */         }
/*      */       });
/*      */       try {
/*  747 */         super.sync();
/*  748 */         AccessController.doPrivileged(new PrivilegedAction() {
/*      */           public Void run() {
/*  750 */             if (FileSystemPreferences.this.isUserNode()) {
/*  751 */               FileSystemPreferences.access$502(localLong.longValue() + 1000L);
/*  752 */               FileSystemPreferences.userRootModFile.setLastModified(FileSystemPreferences.userRootModTime);
/*      */             } else {
/*  754 */               FileSystemPreferences.access$902(localLong.longValue() + 1000L);
/*  755 */               FileSystemPreferences.systemRootModFile.setLastModified(FileSystemPreferences.systemRootModTime);
/*      */             }
/*  757 */             return null;
/*      */           } } );
/*      */       }
/*      */       finally {
/*  761 */         unlockFile();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void syncSpi() throws BackingStoreException {
/*      */     try {
/*  768 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Void run() throws BackingStoreException {
/*  771 */           FileSystemPreferences.this.syncSpiPrivileged();
/*  772 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException) {
/*  776 */       throw ((BackingStoreException)localPrivilegedActionException.getException());
/*      */     }
/*      */   }
/*      */ 
/*  780 */   private void syncSpiPrivileged() throws BackingStoreException { if (isRemoved())
/*  781 */       throw new IllegalStateException("Node has been removed");
/*  782 */     if (this.prefsCache == null)
/*      */       return;
/*      */     long l;
/*  785 */     if (isUserNode() ? isUserRootModified : isSystemRootModified) {
/*  786 */       l = this.prefsFile.lastModified();
/*  787 */       if (l != this.lastSyncTime)
/*      */       {
/*  790 */         loadCache();
/*  791 */         replayChanges();
/*  792 */         this.lastSyncTime = l;
/*      */       }
/*  794 */     } else if ((this.lastSyncTime != 0L) && (!this.dir.exists()))
/*      */     {
/*  797 */       this.prefsCache = new TreeMap();
/*  798 */       replayChanges();
/*      */     }
/*  800 */     if (!this.changeLog.isEmpty()) {
/*  801 */       writeBackCache();
/*      */ 
/*  807 */       l = this.prefsFile.lastModified();
/*      */ 
/*  813 */       if (this.lastSyncTime <= l) {
/*  814 */         this.lastSyncTime = (l + 1000L);
/*  815 */         this.prefsFile.setLastModified(this.lastSyncTime);
/*      */       }
/*  817 */       this.changeLog.clear();
/*      */     } }
/*      */ 
/*      */   public void flush() throws BackingStoreException
/*      */   {
/*  822 */     if (isRemoved())
/*  823 */       return;
/*  824 */     sync();
/*      */   }
/*      */ 
/*      */   protected void flushSpi()
/*      */     throws BackingStoreException
/*      */   {
/*      */   }
/*      */ 
/*      */   private static boolean isDirChar(char paramChar)
/*      */   {
/*  838 */     return (paramChar > '\037') && (paramChar < '') && (paramChar != '/') && (paramChar != '.') && (paramChar != '_');
/*      */   }
/*      */ 
/*      */   private static String dirName(String paramString)
/*      */   {
/*  848 */     int i = 0; for (int j = paramString.length(); i < j; i++)
/*  849 */       if (!isDirChar(paramString.charAt(i)))
/*  850 */         return "_" + Base64.byteArrayToAltBase64(byteArray(paramString));
/*  851 */     return paramString;
/*      */   }
/*      */ 
/*      */   private static byte[] byteArray(String paramString)
/*      */   {
/*  859 */     int i = paramString.length();
/*  860 */     byte[] arrayOfByte = new byte[2 * i];
/*  861 */     int j = 0; for (int k = 0; j < i; j++) {
/*  862 */       int m = paramString.charAt(j);
/*  863 */       arrayOfByte[(k++)] = ((byte)(m >> 8));
/*  864 */       arrayOfByte[(k++)] = ((byte)m);
/*      */     }
/*  866 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   private static String nodeName(String paramString)
/*      */   {
/*  874 */     if (paramString.charAt(0) != '_')
/*  875 */       return paramString;
/*  876 */     byte[] arrayOfByte = Base64.altBase64ToByteArray(paramString.substring(1));
/*  877 */     StringBuffer localStringBuffer = new StringBuffer(arrayOfByte.length / 2);
/*  878 */     for (int i = 0; i < arrayOfByte.length; ) {
/*  879 */       int j = arrayOfByte[(i++)] & 0xFF;
/*  880 */       int k = arrayOfByte[(i++)] & 0xFF;
/*  881 */       localStringBuffer.append((char)(j << 8 | k));
/*      */     }
/*  883 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private boolean lockFile(boolean paramBoolean)
/*      */     throws SecurityException
/*      */   {
/*  894 */     boolean bool = isUserNode();
/*      */ 
/*  896 */     int i = 0;
/*  897 */     File localFile = bool ? userLockFile : systemLockFile;
/*  898 */     long l = INIT_SLEEP_TIME;
/*  899 */     for (int j = 0; j < MAX_ATTEMPTS; j++) {
/*      */       try {
/*  901 */         int k = bool ? 384 : 420;
/*  902 */         int[] arrayOfInt = lockFile0(localFile.getCanonicalPath(), k, paramBoolean);
/*      */ 
/*  904 */         i = arrayOfInt[1];
/*  905 */         if (arrayOfInt[0] != 0) {
/*  906 */           if (bool)
/*  907 */             userRootLockHandle = arrayOfInt[0];
/*      */           else {
/*  909 */             systemRootLockHandle = arrayOfInt[0];
/*      */           }
/*  911 */           return true;
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*      */       }
/*      */       try {
/*  918 */         Thread.sleep(l);
/*      */       } catch (InterruptedException localInterruptedException) {
/*  920 */         checkLockFile0ErrorCode(i);
/*  921 */         return false;
/*      */       }
/*  923 */       l *= 2L;
/*      */     }
/*  925 */     checkLockFile0ErrorCode(i);
/*  926 */     return false;
/*      */   }
/*      */ 
/*      */   private void checkLockFile0ErrorCode(int paramInt)
/*      */     throws SecurityException
/*      */   {
/*  935 */     if (paramInt == 13) {
/*  936 */       throw new SecurityException("Could not lock " + (isUserNode() ? "User prefs." : "System prefs.") + " Lock file access denied.");
/*      */     }
/*      */ 
/*  939 */     if (paramInt != 11)
/*  940 */       getLogger().warning("Could not lock " + (isUserNode() ? "User prefs. " : "System prefs.") + " Unix error code " + paramInt + ".");
/*      */   }
/*      */ 
/*      */   private static native int[] lockFile0(String paramString, int paramInt, boolean paramBoolean);
/*      */ 
/*      */   private static native int unlockFile0(int paramInt);
/*      */ 
/*      */   private static native int chmod(String paramString, int paramInt);
/*      */ 
/*      */   private void unlockFile()
/*      */   {
/*  982 */     boolean bool = isUserNode();
/*  983 */     File localFile = bool ? userLockFile : systemLockFile;
/*  984 */     int j = bool ? userRootLockHandle : systemRootLockHandle;
/*  985 */     if (j == 0) {
/*  986 */       getLogger().warning("Unlock: zero lockHandle for " + (bool ? "user" : "system") + " preferences.)");
/*      */ 
/*  988 */       return;
/*      */     }
/*  990 */     int i = unlockFile0(j);
/*  991 */     if (i != 0) {
/*  992 */       getLogger().warning("Could not drop file-lock on " + (isUserNode() ? "user" : "system") + " preferences." + " Unix error code " + i + ".");
/*      */ 
/*  995 */       if (i == 13) {
/*  996 */         throw new SecurityException("Could not unlock" + (isUserNode() ? "User prefs." : "System prefs.") + " Lock file access denied.");
/*      */       }
/*      */     }
/*      */ 
/* 1000 */     if (isUserNode())
/* 1001 */       userRootLockHandle = 0;
/*      */     else
/* 1003 */       systemRootLockHandle = 0;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  430 */     syncTimer.schedule(new TimerTask() {
/*      */       public void run() {
/*  432 */         FileSystemPreferences.access$1200();
/*      */       }
/*      */     }
/*      */     , SYNC_INTERVAL * 1000, SYNC_INTERVAL * 1000);
/*      */ 
/*  437 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/*  439 */         Runtime.getRuntime().addShutdownHook(new Thread() {
/*      */           public void run() {
/*  441 */             FileSystemPreferences.syncTimer.cancel();
/*  442 */             FileSystemPreferences.access$1200();
/*      */           }
/*      */         });
/*  445 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private abstract class Change
/*      */   {
/*      */     private Change()
/*      */     {
/*      */     }
/*      */ 
/*      */     abstract void replay();
/*      */   }
/*      */ 
/*      */   private class NodeCreate extends FileSystemPreferences.Change
/*      */   {
/*      */     private NodeCreate()
/*      */     {
/*  403 */       super(null);
/*      */     }
/*      */ 
/*      */     void replay()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Put extends FileSystemPreferences.Change
/*      */   {
/*      */     String key;
/*      */     String value;
/*      */ 
/*      */     Put(String paramString1, String arg3)
/*      */     {
/*  375 */       super(null);
/*  376 */       this.key = paramString1;
/*      */       Object localObject;
/*  377 */       this.value = localObject;
/*      */     }
/*      */ 
/*      */     void replay() {
/*  381 */       FileSystemPreferences.this.prefsCache.put(this.key, this.value);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Remove extends FileSystemPreferences.Change
/*      */   {
/*      */     String key;
/*      */ 
/*      */     Remove(String arg2)
/*      */     {
/*  391 */       super(null);
/*      */       Object localObject;
/*  392 */       this.key = localObject;
/*      */     }
/*      */ 
/*      */     void replay() {
/*  396 */       FileSystemPreferences.this.prefsCache.remove(this.key);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.prefs.FileSystemPreferences
 * JD-Core Version:    0.6.2
 */