/*      */ package java.lang;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.FilePermission;
/*      */ import java.net.InetAddress;
/*      */ import java.net.SocketPermission;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.Permission;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.Security;
/*      */ import java.security.SecurityPermission;
/*      */ import java.util.PropertyPermission;
/*      */ import java.util.StringTokenizer;
/*      */ import sun.security.util.SecurityConstants;
/*      */ import sun.security.util.SecurityConstants.AWT;
/*      */ 
/*      */ public class SecurityManager
/*      */ {
/*      */ 
/*      */   @Deprecated
/*      */   protected boolean inCheck;
/*  243 */   private boolean initialized = false;
/*      */ 
/*  619 */   private static ThreadGroup rootGroup = getRootGroup();
/*      */ 
/* 1434 */   private static boolean packageAccessValid = false;
/*      */   private static String[] packageAccess;
/* 1436 */   private static final Object packageAccessLock = new Object();
/*      */ 
/* 1438 */   private static boolean packageDefinitionValid = false;
/*      */   private static String[] packageDefinition;
/* 1440 */   private static final Object packageDefinitionLock = new Object();
/*      */ 
/*      */   private boolean hasAllPermission()
/*      */   {
/*      */     try
/*      */     {
/*  252 */       checkPermission(SecurityConstants.ALL_PERMISSION);
/*  253 */       return true; } catch (SecurityException localSecurityException) {
/*      */     }
/*  255 */     return false;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean getInCheck()
/*      */   {
/*  273 */     return this.inCheck;
/*      */   }
/*      */ 
/*      */   public SecurityManager()
/*      */   {
/*  294 */     synchronized (SecurityManager.class) {
/*  295 */       SecurityManager localSecurityManager = System.getSecurityManager();
/*  296 */       if (localSecurityManager != null)
/*      */       {
/*  299 */         localSecurityManager.checkPermission(new RuntimePermission("createSecurityManager"));
/*      */       }
/*      */ 
/*  302 */       this.initialized = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected native Class[] getClassContext();
/*      */ 
/*      */   @Deprecated
/*      */   protected ClassLoader currentClassLoader()
/*      */   {
/*  357 */     ClassLoader localClassLoader = currentClassLoader0();
/*  358 */     if ((localClassLoader != null) && (hasAllPermission()))
/*  359 */       localClassLoader = null;
/*  360 */     return localClassLoader;
/*      */   }
/*      */ 
/*      */   private native ClassLoader currentClassLoader0();
/*      */ 
/*      */   @Deprecated
/*      */   protected Class<?> currentLoadedClass()
/*      */   {
/*  403 */     Class localClass = currentLoadedClass0();
/*  404 */     if ((localClass != null) && (hasAllPermission()))
/*  405 */       localClass = null;
/*  406 */     return localClass;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   protected native int classDepth(String paramString);
/*      */ 
/*      */   @Deprecated
/*      */   protected int classLoaderDepth()
/*      */   {
/*  462 */     int i = classLoaderDepth0();
/*  463 */     if (i != -1) {
/*  464 */       if (hasAllPermission())
/*  465 */         i = -1;
/*      */       else
/*  467 */         i--;
/*      */     }
/*  469 */     return i;
/*      */   }
/*      */ 
/*      */   private native int classLoaderDepth0();
/*      */ 
/*      */   @Deprecated
/*      */   protected boolean inClass(String paramString)
/*      */   {
/*  487 */     return classDepth(paramString) >= 0;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   protected boolean inClassLoader()
/*      */   {
/*  504 */     return currentClassLoader() != null;
/*      */   }
/*      */ 
/*      */   public Object getSecurityContext()
/*      */   {
/*  530 */     return AccessController.getContext();
/*      */   }
/*      */ 
/*      */   public void checkPermission(Permission paramPermission)
/*      */   {
/*  549 */     AccessController.checkPermission(paramPermission);
/*      */   }
/*      */ 
/*      */   public void checkPermission(Permission paramPermission, Object paramObject)
/*      */   {
/*  584 */     if ((paramObject instanceof AccessControlContext))
/*  585 */       ((AccessControlContext)paramObject).checkPermission(paramPermission);
/*      */     else
/*  587 */       throw new SecurityException();
/*      */   }
/*      */ 
/*      */   public void checkCreateClassLoader()
/*      */   {
/*  611 */     checkPermission(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
/*      */   }
/*      */ 
/*      */   private static ThreadGroup getRootGroup()
/*      */   {
/*  622 */     ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
/*  623 */     while (localThreadGroup.getParent() != null) {
/*  624 */       localThreadGroup = localThreadGroup.getParent();
/*      */     }
/*  626 */     return localThreadGroup;
/*      */   }
/*      */ 
/*      */   public void checkAccess(Thread paramThread)
/*      */   {
/*  672 */     if (paramThread == null) {
/*  673 */       throw new NullPointerException("thread can't be null");
/*      */     }
/*  675 */     if (paramThread.getThreadGroup() == rootGroup)
/*  676 */       checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
/*      */   }
/*      */ 
/*      */   public void checkAccess(ThreadGroup paramThreadGroup)
/*      */   {
/*  725 */     if (paramThreadGroup == null) {
/*  726 */       throw new NullPointerException("thread group can't be null");
/*      */     }
/*  728 */     if (paramThreadGroup == rootGroup)
/*  729 */       checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
/*      */   }
/*      */ 
/*      */   public void checkExit(int paramInt)
/*      */   {
/*  761 */     checkPermission(new RuntimePermission("exitVM." + paramInt));
/*      */   }
/*      */ 
/*      */   public void checkExec(String paramString)
/*      */   {
/*  794 */     File localFile = new File(paramString);
/*  795 */     if (localFile.isAbsolute()) {
/*  796 */       checkPermission(new FilePermission(paramString, "execute"));
/*      */     }
/*      */     else
/*  799 */       checkPermission(new FilePermission("<<ALL FILES>>", "execute"));
/*      */   }
/*      */ 
/*      */   public void checkLink(String paramString)
/*      */   {
/*  832 */     if (paramString == null) {
/*  833 */       throw new NullPointerException("library can't be null");
/*      */     }
/*  835 */     checkPermission(new RuntimePermission("loadLibrary." + paramString));
/*      */   }
/*      */ 
/*      */   public void checkRead(FileDescriptor paramFileDescriptor)
/*      */   {
/*  861 */     if (paramFileDescriptor == null) {
/*  862 */       throw new NullPointerException("file descriptor can't be null");
/*      */     }
/*  864 */     checkPermission(new RuntimePermission("readFileDescriptor"));
/*      */   }
/*      */ 
/*      */   public void checkRead(String paramString)
/*      */   {
/*  888 */     checkPermission(new FilePermission(paramString, "read"));
/*      */   }
/*      */ 
/*      */   public void checkRead(String paramString, Object paramObject)
/*      */   {
/*  923 */     checkPermission(new FilePermission(paramString, "read"), paramObject);
/*      */   }
/*      */ 
/*      */   public void checkWrite(FileDescriptor paramFileDescriptor)
/*      */   {
/*  951 */     if (paramFileDescriptor == null) {
/*  952 */       throw new NullPointerException("file descriptor can't be null");
/*      */     }
/*  954 */     checkPermission(new RuntimePermission("writeFileDescriptor"));
/*      */   }
/*      */ 
/*      */   public void checkWrite(String paramString)
/*      */   {
/*  979 */     checkPermission(new FilePermission(paramString, "write"));
/*      */   }
/*      */ 
/*      */   public void checkDelete(String paramString)
/*      */   {
/* 1007 */     checkPermission(new FilePermission(paramString, "delete"));
/*      */   }
/*      */ 
/*      */   public void checkConnect(String paramString, int paramInt)
/*      */   {
/* 1041 */     if (paramString == null) {
/* 1042 */       throw new NullPointerException("host can't be null");
/*      */     }
/* 1044 */     if ((!paramString.startsWith("[")) && (paramString.indexOf(':') != -1)) {
/* 1045 */       paramString = "[" + paramString + "]";
/*      */     }
/* 1047 */     if (paramInt == -1) {
/* 1048 */       checkPermission(new SocketPermission(paramString, "resolve"));
/*      */     }
/*      */     else
/* 1051 */       checkPermission(new SocketPermission(paramString + ":" + paramInt, "connect"));
/*      */   }
/*      */ 
/*      */   public void checkConnect(String paramString, int paramInt, Object paramObject)
/*      */   {
/* 1096 */     if (paramString == null) {
/* 1097 */       throw new NullPointerException("host can't be null");
/*      */     }
/* 1099 */     if ((!paramString.startsWith("[")) && (paramString.indexOf(':') != -1)) {
/* 1100 */       paramString = "[" + paramString + "]";
/*      */     }
/* 1102 */     if (paramInt == -1) {
/* 1103 */       checkPermission(new SocketPermission(paramString, "resolve"), paramObject);
/*      */     }
/*      */     else
/*      */     {
/* 1107 */       checkPermission(new SocketPermission(paramString + ":" + paramInt, "connect"), paramObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void checkListen(int paramInt)
/*      */   {
/* 1134 */     if (paramInt == 0)
/* 1135 */       checkPermission(SecurityConstants.LOCAL_LISTEN_PERMISSION);
/*      */     else
/* 1137 */       checkPermission(new SocketPermission("localhost:" + paramInt, "listen"));
/*      */   }
/*      */ 
/*      */   public void checkAccept(String paramString, int paramInt)
/*      */   {
/* 1168 */     if (paramString == null) {
/* 1169 */       throw new NullPointerException("host can't be null");
/*      */     }
/* 1171 */     if ((!paramString.startsWith("[")) && (paramString.indexOf(':') != -1)) {
/* 1172 */       paramString = "[" + paramString + "]";
/*      */     }
/* 1174 */     checkPermission(new SocketPermission(paramString + ":" + paramInt, "accept"));
/*      */   }
/*      */ 
/*      */   public void checkMulticast(InetAddress paramInetAddress)
/*      */   {
/* 1201 */     String str = paramInetAddress.getHostAddress();
/* 1202 */     if ((!str.startsWith("[")) && (str.indexOf(':') != -1)) {
/* 1203 */       str = "[" + str + "]";
/*      */     }
/* 1205 */     checkPermission(new SocketPermission(str, "connect,accept"));
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void checkMulticast(InetAddress paramInetAddress, byte paramByte)
/*      */   {
/* 1237 */     String str = paramInetAddress.getHostAddress();
/* 1238 */     if ((!str.startsWith("[")) && (str.indexOf(':') != -1)) {
/* 1239 */       str = "[" + str + "]";
/*      */     }
/* 1241 */     checkPermission(new SocketPermission(str, "connect,accept"));
/*      */   }
/*      */ 
/*      */   public void checkPropertiesAccess()
/*      */   {
/* 1269 */     checkPermission(new PropertyPermission("*", "read,write"));
/*      */   }
/*      */ 
/*      */   public void checkPropertyAccess(String paramString)
/*      */   {
/* 1302 */     checkPermission(new PropertyPermission(paramString, "read"));
/*      */   }
/*      */ 
/*      */   public boolean checkTopLevelWindow(Object paramObject)
/*      */   {
/* 1340 */     if (paramObject == null)
/* 1341 */       throw new NullPointerException("window can't be null");
/*      */     try
/*      */     {
/* 1344 */       checkPermission(SecurityConstants.AWT.TOPLEVEL_WINDOW_PERMISSION);
/* 1345 */       return true;
/*      */     }
/*      */     catch (SecurityException localSecurityException) {
/*      */     }
/* 1349 */     return false;
/*      */   }
/*      */ 
/*      */   public void checkPrintJobAccess()
/*      */   {
/* 1372 */     checkPermission(new RuntimePermission("queuePrintJob"));
/*      */   }
/*      */ 
/*      */   public void checkSystemClipboardAccess()
/*      */   {
/* 1394 */     checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
/*      */   }
/*      */ 
/*      */   public void checkAwtEventQueueAccess()
/*      */   {
/* 1415 */     checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION);
/*      */   }
/*      */ 
/*      */   private static String[] getPackages(String paramString)
/*      */   {
/* 1443 */     String[] arrayOfString = null;
/* 1444 */     if ((paramString != null) && (!paramString.equals(""))) {
/* 1445 */       StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
/*      */ 
/* 1447 */       int i = localStringTokenizer.countTokens();
/* 1448 */       if (i > 0) {
/* 1449 */         arrayOfString = new String[i];
/* 1450 */         int j = 0;
/* 1451 */         while (localStringTokenizer.hasMoreElements()) {
/* 1452 */           String str = localStringTokenizer.nextToken().trim();
/* 1453 */           arrayOfString[(j++)] = str;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1458 */     if (arrayOfString == null)
/* 1459 */       arrayOfString = new String[0];
/* 1460 */     return arrayOfString;
/*      */   }
/*      */ 
/*      */   public void checkPackageAccess(String paramString)
/*      */   {
/* 1496 */     if (paramString == null)
/* 1497 */       throw new NullPointerException("package name can't be null");
/*      */     String[] arrayOfString;
/* 1501 */     synchronized (packageAccessLock)
/*      */     {
/* 1505 */       if (!packageAccessValid) {
/* 1506 */         String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public String run()
/*      */           {
/* 1510 */             return Security.getProperty("package.access");
/*      */           }
/*      */         });
/* 1515 */         packageAccess = getPackages(str);
/* 1516 */         packageAccessValid = true;
/*      */       }
/*      */ 
/* 1521 */       arrayOfString = packageAccess;
/*      */     }
/*      */ 
/* 1527 */     for (int i = 0; i < arrayOfString.length; i++)
/* 1528 */       if ((paramString.startsWith(arrayOfString[i])) || (arrayOfString[i].equals(paramString + "."))) {
/* 1529 */         checkPermission(new RuntimePermission("accessClassInPackage." + paramString));
/*      */ 
/* 1531 */         break;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void checkPackageDefinition(String paramString)
/*      */   {
/* 1565 */     if (paramString == null)
/* 1566 */       throw new NullPointerException("package name can't be null");
/*      */     String[] arrayOfString;
/* 1570 */     synchronized (packageDefinitionLock)
/*      */     {
/* 1574 */       if (!packageDefinitionValid) {
/* 1575 */         String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public String run()
/*      */           {
/* 1579 */             return Security.getProperty("package.definition");
/*      */           }
/*      */         });
/* 1584 */         packageDefinition = getPackages(str);
/* 1585 */         packageDefinitionValid = true;
/*      */       }
/*      */ 
/* 1589 */       arrayOfString = packageDefinition;
/*      */     }
/*      */ 
/* 1595 */     for (int i = 0; i < arrayOfString.length; i++)
/* 1596 */       if ((paramString.startsWith(arrayOfString[i])) || (arrayOfString[i].equals(paramString + "."))) {
/* 1597 */         checkPermission(new RuntimePermission("defineClassInPackage." + paramString));
/*      */ 
/* 1599 */         break;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void checkSetFactory()
/*      */   {
/* 1629 */     checkPermission(new RuntimePermission("setFactory"));
/*      */   }
/*      */ 
/*      */   public void checkMemberAccess(Class<?> paramClass, int paramInt)
/*      */   {
/* 1661 */     if (paramClass == null) {
/* 1662 */       throw new NullPointerException("class can't be null");
/*      */     }
/* 1664 */     if (paramInt != 0) {
/* 1665 */       Class[] arrayOfClass = getClassContext();
/*      */ 
/* 1677 */       if ((arrayOfClass.length < 4) || (arrayOfClass[3].getClassLoader() != paramClass.getClassLoader()))
/*      */       {
/* 1679 */         checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void checkSecurityAccess(String paramString)
/*      */   {
/* 1715 */     checkPermission(new SecurityPermission(paramString));
/*      */   }
/*      */ 
/*      */   private native Class currentLoadedClass0();
/*      */ 
/*      */   public ThreadGroup getThreadGroup()
/*      */   {
/* 1732 */     return Thread.currentThread().getThreadGroup();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.SecurityManager
 * JD-Core Version:    0.6.2
 */