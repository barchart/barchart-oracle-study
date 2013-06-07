/*      */ package sun.rmi.server;
/*      */ 
/*      */ import com.sun.rmi.rmid.ExecOptionPermission;
/*      */ import com.sun.rmi.rmid.ExecPermission;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.InetAddress;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketAddress;
/*      */ import java.net.SocketException;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.channels.ServerSocketChannel;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.rmi.AccessException;
/*      */ import java.rmi.AlreadyBoundException;
/*      */ import java.rmi.ConnectException;
/*      */ import java.rmi.ConnectIOException;
/*      */ import java.rmi.MarshalledObject;
/*      */ import java.rmi.NoSuchObjectException;
/*      */ import java.rmi.NotBoundException;
/*      */ import java.rmi.Remote;
/*      */ import java.rmi.RemoteException;
/*      */ import java.rmi.activation.ActivationDesc;
/*      */ import java.rmi.activation.ActivationException;
/*      */ import java.rmi.activation.ActivationGroup;
/*      */ import java.rmi.activation.ActivationGroupDesc;
/*      */ import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
/*      */ import java.rmi.activation.ActivationGroupID;
/*      */ import java.rmi.activation.ActivationID;
/*      */ import java.rmi.activation.ActivationInstantiator;
/*      */ import java.rmi.activation.ActivationMonitor;
/*      */ import java.rmi.activation.ActivationSystem;
/*      */ import java.rmi.activation.Activator;
/*      */ import java.rmi.activation.UnknownGroupException;
/*      */ import java.rmi.activation.UnknownObjectException;
/*      */ import java.rmi.registry.Registry;
/*      */ import java.rmi.server.ObjID;
/*      */ import java.rmi.server.RMIClassLoader;
/*      */ import java.rmi.server.RMIClientSocketFactory;
/*      */ import java.rmi.server.RMIServerSocketFactory;
/*      */ import java.rmi.server.RemoteObject;
/*      */ import java.rmi.server.RemoteServer;
/*      */ import java.rmi.server.UnicastRemoteObject;
/*      */ import java.security.AccessControlException;
/*      */ import java.security.AccessController;
/*      */ import java.security.AllPermission;
/*      */ import java.security.CodeSource;
/*      */ import java.security.Permission;
/*      */ import java.security.PermissionCollection;
/*      */ import java.security.Permissions;
/*      */ import java.security.Policy;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.cert.Certificate;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.Properties;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import sun.rmi.log.LogHandler;
/*      */ import sun.rmi.log.ReliableLog;
/*      */ import sun.rmi.registry.RegistryImpl;
/*      */ import sun.rmi.transport.LiveRef;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.action.GetIntegerAction;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.security.provider.PolicyFile;
/*      */ 
/*      */ public class Activation
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 2921265612698155191L;
/*      */   private static final byte MAJOR_VERSION = 1;
/*      */   private static final byte MINOR_VERSION = 0;
/*      */   private static Object execPolicy;
/*      */   private static Method execPolicyMethod;
/*      */   private static boolean debugExec;
/*  152 */   private Map<ActivationID, ActivationGroupID> idTable = new ConcurrentHashMap();
/*      */ 
/*  155 */   private Map<ActivationGroupID, GroupEntry> groupTable = new ConcurrentHashMap();
/*      */ 
/*  158 */   private byte majorVersion = 1;
/*  159 */   private byte minorVersion = 0;
/*      */   private transient int groupSemaphore;
/*      */   private transient int groupCounter;
/*      */   private transient ReliableLog log;
/*      */   private transient int numUpdates;
/*      */   private transient String[] command;
/*  174 */   private static final long groupTimeout = getInt("sun.rmi.activation.groupTimeout", 60000);
/*      */ 
/*  177 */   private static final int snapshotInterval = getInt("sun.rmi.activation.snapshotInterval", 200);
/*      */ 
/*  180 */   private static final long execTimeout = getInt("sun.rmi.activation.execTimeout", 30000);
/*      */ 
/*  183 */   private static final Object initLock = new Object();
/*  184 */   private static boolean initDone = false;
/*      */   private transient Activator activator;
/*      */   private transient Activator activatorStub;
/*      */   private transient ActivationSystem system;
/*      */   private transient ActivationSystem systemStub;
/*      */   private transient ActivationMonitor monitor;
/*      */   private transient Registry registry;
/*  197 */   private volatile transient boolean shuttingDown = false;
/*      */   private volatile transient Object startupLock;
/*      */   private transient Thread shutdownHook;
/*  201 */   private static ResourceBundle resources = null;
/*      */ 
/*      */   private static int getInt(String paramString, int paramInt)
/*      */   {
/*  188 */     return ((Integer)AccessController.doPrivileged(new GetIntegerAction(paramString, paramInt))).intValue();
/*      */   }
/*      */ 
/*      */   private static void startActivation(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory, String paramString, String[] paramArrayOfString)
/*      */     throws Exception
/*      */   {
/*  220 */     ReliableLog localReliableLog = new ReliableLog(paramString, new ActLogHandler());
/*  221 */     Activation localActivation = (Activation)localReliableLog.recover();
/*  222 */     localActivation.init(paramInt, paramRMIServerSocketFactory, localReliableLog, paramArrayOfString);
/*      */   }
/*      */ 
/*      */   private void init(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory, ReliableLog paramReliableLog, String[] paramArrayOfString)
/*      */     throws Exception
/*      */   {
/*  236 */     this.log = paramReliableLog;
/*  237 */     this.numUpdates = 0;
/*  238 */     this.shutdownHook = new ShutdownHook();
/*  239 */     this.groupSemaphore = getInt("sun.rmi.activation.groupThrottle", 3);
/*  240 */     this.groupCounter = 0;
/*  241 */     Runtime.getRuntime().addShutdownHook(this.shutdownHook);
/*      */ 
/*  245 */     ActivationGroupID[] arrayOfActivationGroupID = (ActivationGroupID[])this.groupTable.keySet().toArray(new ActivationGroupID[0]);
/*      */ 
/*  248 */     synchronized (this.startupLock = new Object())
/*      */     {
/*  253 */       this.activator = new ActivatorImpl(paramInt, paramRMIServerSocketFactory);
/*  254 */       this.activatorStub = ((Activator)RemoteObject.toStub(this.activator));
/*  255 */       this.system = new ActivationSystemImpl(paramInt, paramRMIServerSocketFactory);
/*  256 */       this.systemStub = ((ActivationSystem)RemoteObject.toStub(this.system));
/*  257 */       this.monitor = new ActivationMonitorImpl(paramInt, paramRMIServerSocketFactory);
/*  258 */       initCommand(paramArrayOfString);
/*  259 */       this.registry = new SystemRegistryImpl(paramInt, null, paramRMIServerSocketFactory, this.systemStub);
/*      */ 
/*  261 */       if (paramRMIServerSocketFactory != null) {
/*  262 */         synchronized (initLock) {
/*  263 */           initDone = true;
/*  264 */           initLock.notifyAll();
/*      */         }
/*      */       }
/*      */     }
/*  268 */     this.startupLock = null;
/*      */ 
/*  271 */     int i = arrayOfActivationGroupID.length;
/*      */     while (true) { i--; if (i < 0) break;
/*      */       try {
/*  273 */         getGroupEntry(arrayOfActivationGroupID[i]).restartServices();
/*      */       } catch (UnknownGroupException localUnknownGroupException) {
/*  275 */         System.err.println(getTextResource("rmid.restart.group.warning"));
/*      */ 
/*  277 */         localUnknownGroupException.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  290 */     paramObjectInputStream.defaultReadObject();
/*  291 */     if (!(this.groupTable instanceof ConcurrentHashMap)) {
/*  292 */       this.groupTable = new ConcurrentHashMap(this.groupTable);
/*      */     }
/*  294 */     if (!(this.idTable instanceof ConcurrentHashMap))
/*  295 */       this.idTable = new ConcurrentHashMap(this.idTable);
/*      */   }
/*      */ 
/*      */   private void checkShutdown()
/*      */     throws ActivationException
/*      */   {
/*  612 */     Object localObject1 = this.startupLock;
/*  613 */     if (localObject1 != null) {
/*  614 */       synchronized (localObject1)
/*      */       {
/*      */       }
/*      */     }
/*      */ 
/*  619 */     if (this.shuttingDown == true)
/*  620 */       throw new ActivationException("activation system shutting down");
/*      */   }
/*      */ 
/*      */   private static void unexport(Remote paramRemote)
/*      */   {
/*      */     while (true)
/*      */       try
/*      */       {
/*  628 */         if (UnicastRemoteObject.unexportObject(paramRemote, false) == true) {
/*      */           break;
/*      */         }
/*  631 */         Thread.sleep(100L);
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   private ActivationGroupID getGroupID(ActivationID paramActivationID)
/*      */     throws UnknownObjectException
/*      */   {
/*  720 */     ActivationGroupID localActivationGroupID = (ActivationGroupID)this.idTable.get(paramActivationID);
/*  721 */     if (localActivationGroupID != null) {
/*  722 */       return localActivationGroupID;
/*      */     }
/*  724 */     throw new UnknownObjectException("unknown object: " + paramActivationID);
/*      */   }
/*      */ 
/*      */   private GroupEntry getGroupEntry(ActivationGroupID paramActivationGroupID, boolean paramBoolean)
/*      */     throws UnknownGroupException
/*      */   {
/*  734 */     if (paramActivationGroupID.getClass() == ActivationGroupID.class)
/*      */     {
/*      */       GroupEntry localGroupEntry;
/*  736 */       if (paramBoolean)
/*  737 */         localGroupEntry = (GroupEntry)this.groupTable.remove(paramActivationGroupID);
/*      */       else {
/*  739 */         localGroupEntry = (GroupEntry)this.groupTable.get(paramActivationGroupID);
/*      */       }
/*  741 */       if ((localGroupEntry != null) && (!localGroupEntry.removed)) {
/*  742 */         return localGroupEntry;
/*      */       }
/*      */     }
/*  745 */     throw new UnknownGroupException("group unknown");
/*      */   }
/*      */ 
/*      */   private GroupEntry getGroupEntry(ActivationGroupID paramActivationGroupID)
/*      */     throws UnknownGroupException
/*      */   {
/*  755 */     return getGroupEntry(paramActivationGroupID, false);
/*      */   }
/*      */ 
/*      */   private GroupEntry removeGroupEntry(ActivationGroupID paramActivationGroupID)
/*      */     throws UnknownGroupException
/*      */   {
/*  765 */     return getGroupEntry(paramActivationGroupID, true);
/*      */   }
/*      */ 
/*      */   private GroupEntry getGroupEntry(ActivationID paramActivationID)
/*      */     throws UnknownObjectException
/*      */   {
/*  776 */     ActivationGroupID localActivationGroupID = getGroupID(paramActivationID);
/*  777 */     GroupEntry localGroupEntry = (GroupEntry)this.groupTable.get(localActivationGroupID);
/*  778 */     if ((localGroupEntry != null) && (!localGroupEntry.removed)) {
/*  779 */       return localGroupEntry;
/*      */     }
/*  781 */     throw new UnknownObjectException("object's group removed");
/*      */   }
/*      */ 
/*      */   private String[] activationArgs(ActivationGroupDesc paramActivationGroupDesc)
/*      */   {
/* 1353 */     ActivationGroupDesc.CommandEnvironment localCommandEnvironment = paramActivationGroupDesc.getCommandEnvironment();
/*      */ 
/* 1356 */     ArrayList localArrayList = new ArrayList();
/*      */ 
/* 1359 */     localArrayList.add((localCommandEnvironment != null) && (localCommandEnvironment.getCommandPath() != null) ? localCommandEnvironment.getCommandPath() : this.command[0]);
/*      */ 
/* 1364 */     if ((localCommandEnvironment != null) && (localCommandEnvironment.getCommandOptions() != null)) {
/* 1365 */       localArrayList.addAll(Arrays.asList(localCommandEnvironment.getCommandOptions()));
/*      */     }
/*      */ 
/* 1369 */     Properties localProperties = paramActivationGroupDesc.getPropertyOverrides();
/* 1370 */     if (localProperties != null) {
/* 1371 */       Enumeration localEnumeration = localProperties.propertyNames();
/* 1372 */       while (localEnumeration.hasMoreElements())
/*      */       {
/* 1374 */         String str = (String)localEnumeration.nextElement();
/*      */ 
/* 1380 */         localArrayList.add("-D" + str + "=" + localProperties.getProperty(str));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1387 */     for (int i = 1; i < this.command.length; i++) {
/* 1388 */       localArrayList.add(this.command[i]);
/*      */     }
/*      */ 
/* 1391 */     String[] arrayOfString = new String[localArrayList.size()];
/* 1392 */     System.arraycopy(localArrayList.toArray(), 0, arrayOfString, 0, arrayOfString.length);
/*      */ 
/* 1394 */     return arrayOfString;
/*      */   }
/*      */ 
/*      */   private void checkArgs(ActivationGroupDesc paramActivationGroupDesc, String[] paramArrayOfString)
/*      */     throws SecurityException, ActivationException
/*      */   {
/* 1403 */     if (execPolicyMethod != null) {
/* 1404 */       if (paramArrayOfString == null)
/* 1405 */         paramArrayOfString = activationArgs(paramActivationGroupDesc);
/*      */       try
/*      */       {
/* 1408 */         execPolicyMethod.invoke(execPolicy, new Object[] { paramActivationGroupDesc, paramArrayOfString });
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/* 1410 */         Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 1411 */         if ((localThrowable instanceof SecurityException)) {
/* 1412 */           throw ((SecurityException)localThrowable);
/*      */         }
/* 1414 */         throw new ActivationException(execPolicyMethod.getName() + ": unexpected exception", localInvocationTargetException);
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/* 1419 */         throw new ActivationException(execPolicyMethod.getName() + ": unexpected exception", localException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addLogRecord(LogRecord paramLogRecord)
/*      */     throws ActivationException
/*      */   {
/* 1471 */     synchronized (this.log) {
/* 1472 */       checkShutdown();
/*      */       try {
/* 1474 */         this.log.update(paramLogRecord, true);
/*      */       } catch (Exception localException1) {
/* 1476 */         this.numUpdates = snapshotInterval;
/* 1477 */         System.err.println(getTextResource("rmid.log.update.warning"));
/* 1478 */         localException1.printStackTrace();
/*      */       }
/* 1480 */       if (++this.numUpdates < snapshotInterval)
/* 1481 */         return;
/*      */       try
/*      */       {
/* 1484 */         this.log.snapshot(this);
/* 1485 */         this.numUpdates = 0;
/*      */       } catch (Exception localException2) {
/* 1487 */         System.err.println(getTextResource("rmid.log.snapshot.warning"));
/*      */ 
/* 1489 */         localException2.printStackTrace();
/*      */         try
/*      */         {
/* 1492 */           this.system.shutdown();
/*      */         }
/*      */         catch (RemoteException localRemoteException)
/*      */         {
/*      */         }
/* 1497 */         throw new ActivationException("log snapshot failed", localException2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initCommand(String[] paramArrayOfString)
/*      */   {
/* 1738 */     this.command = new String[paramArrayOfString.length + 2];
/* 1739 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/*      */         try {
/* 1742 */           Activation.this.command[0] = (System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
/*      */         }
/*      */         catch (Exception localException) {
/* 1745 */           System.err.println(Activation.getTextResource("rmid.unfound.java.home.property"));
/*      */ 
/* 1747 */           Activation.this.command[0] = "java";
/*      */         }
/* 1749 */         return null;
/*      */       }
/*      */     });
/* 1752 */     System.arraycopy(paramArrayOfString, 0, this.command, 1, paramArrayOfString.length);
/* 1753 */     this.command[(this.command.length - 1)] = "sun.rmi.server.ActivationGroupInit";
/*      */   }
/*      */ 
/*      */   private static void bomb(String paramString) {
/* 1757 */     System.err.println("rmid: " + paramString);
/* 1758 */     System.err.println(MessageFormat.format(getTextResource("rmid.usage"), new Object[] { "rmid" }));
/*      */ 
/* 1760 */     System.exit(1);
/*      */   }
/*      */ 
/*      */   public static void main(String[] paramArrayOfString)
/*      */   {
/* 1907 */     int i = 0;
/*      */ 
/* 1911 */     if (System.getSecurityManager() == null) {
/* 1912 */       System.setSecurityManager(new SecurityManager());
/*      */     }
/*      */     try
/*      */     {
/* 1916 */       Exception localException1 = 1098;
/* 1917 */       ActivationServerSocketFactory localActivationServerSocketFactory = null;
/*      */ 
/* 1924 */       Channel localChannel = (Channel)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Channel run() throws IOException {
/* 1927 */           return System.inheritedChannel();
/*      */         }
/*      */       });
/* 1931 */       if ((localChannel != null) && ((localChannel instanceof ServerSocketChannel)))
/*      */       {
/* 1937 */         AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */         {
/*      */           public Void run() throws IOException {
/* 1940 */             File localFile = Files.createTempFile("rmid-err", null, new FileAttribute[0]).toFile();
/*      */ 
/* 1942 */             PrintStream localPrintStream = new PrintStream(new FileOutputStream(localFile));
/*      */ 
/* 1944 */             System.setErr(localPrintStream);
/* 1945 */             return null;
/*      */           }
/*      */         });
/* 1949 */         localObject = ((ServerSocketChannel)localChannel).socket();
/*      */ 
/* 1951 */         localException1 = ((ServerSocket)localObject).getLocalPort();
/* 1952 */         localActivationServerSocketFactory = new ActivationServerSocketFactory((ServerSocket)localObject);
/*      */ 
/* 1954 */         System.err.println(new Date());
/* 1955 */         System.err.println(getTextResource("rmid.inherited.channel.info") + ": " + localChannel);
/*      */       }
/*      */ 
/* 1960 */       Object localObject = null;
/* 1961 */       ArrayList localArrayList = new ArrayList();
/*      */ 
/* 1966 */       for (int j = 0; j < paramArrayOfString.length; j++) {
/* 1967 */         if (paramArrayOfString[j].equals("-port")) {
/* 1968 */           if (localActivationServerSocketFactory != null) {
/* 1969 */             bomb(getTextResource("rmid.syntax.port.badarg"));
/*      */           }
/* 1971 */           if (j + 1 < paramArrayOfString.length)
/*      */             try {
/* 1973 */               localException1 = Integer.parseInt(paramArrayOfString[(++j)]);
/*      */             } catch (NumberFormatException localNumberFormatException) {
/* 1975 */               bomb(getTextResource("rmid.syntax.port.badnumber"));
/*      */             }
/*      */           else {
/* 1978 */             bomb(getTextResource("rmid.syntax.port.missing"));
/*      */           }
/*      */         }
/* 1981 */         else if (paramArrayOfString[j].equals("-log")) {
/* 1982 */           if (j + 1 < paramArrayOfString.length)
/* 1983 */             localObject = paramArrayOfString[(++j)];
/*      */           else {
/* 1985 */             bomb(getTextResource("rmid.syntax.log.missing"));
/*      */           }
/*      */         }
/* 1988 */         else if (paramArrayOfString[j].equals("-stop")) {
/* 1989 */           i = 1;
/*      */         }
/* 1991 */         else if (paramArrayOfString[j].startsWith("-C")) {
/* 1992 */           localArrayList.add(paramArrayOfString[j].substring(2));
/*      */         }
/*      */         else {
/* 1995 */           bomb(MessageFormat.format(getTextResource("rmid.syntax.illegal.option"), new Object[] { paramArrayOfString[j] }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2001 */       if (localObject == null) {
/* 2002 */         if (localActivationServerSocketFactory != null)
/* 2003 */           bomb(getTextResource("rmid.syntax.log.required"));
/*      */         else {
/* 2005 */           localObject = "log";
/*      */         }
/*      */       }
/*      */ 
/* 2009 */       debugExec = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.activation.debugExec"))).booleanValue();
/*      */ 
/* 2015 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.activation.execPolicy", null));
/*      */ 
/* 2017 */       if (str == null) {
/* 2018 */         if (i == 0) {
/* 2019 */           DefaultExecPolicy.checkConfiguration();
/*      */         }
/* 2021 */         str = "default";
/*      */       }
/*      */ 
/* 2027 */       if (!str.equals("none")) {
/* 2028 */         if ((str.equals("")) || (str.equals("default")))
/*      */         {
/* 2031 */           str = DefaultExecPolicy.class.getName();
/*      */         }
/*      */         try
/*      */         {
/* 2035 */           Class localClass = RMIClassLoader.loadClass(str);
/*      */ 
/* 2037 */           execPolicy = localClass.newInstance();
/* 2038 */           execPolicyMethod = localClass.getMethod("checkExecCommand", new Class[] { ActivationGroupDesc.class, [Ljava.lang.String.class });
/*      */         }
/*      */         catch (Exception localException3)
/*      */         {
/* 2043 */           if (debugExec) {
/* 2044 */             System.err.println(getTextResource("rmid.exec.policy.exception"));
/*      */ 
/* 2046 */             localException3.printStackTrace();
/*      */           }
/* 2048 */           bomb(getTextResource("rmid.exec.policy.invalid"));
/*      */         }
/*      */       }
/*      */ 
/* 2052 */       if (i == 1) {
/* 2053 */         localException3 = localException1;
/* 2054 */         AccessController.doPrivileged(new PrivilegedAction() {
/*      */           public Void run() {
/* 2056 */             System.setProperty("java.rmi.activation.port", Integer.toString(this.val$finalPort));
/*      */ 
/* 2058 */             return null;
/*      */           }
/*      */         });
/* 2061 */         ActivationSystem localActivationSystem = ActivationGroup.getSystem();
/* 2062 */         localActivationSystem.shutdown();
/* 2063 */         System.exit(0);
/*      */       }
/*      */ 
/* 2079 */       startActivation(localException1, localActivationServerSocketFactory, (String)localObject, (String[])localArrayList.toArray(new String[localArrayList.size()]));
/*      */       while (true)
/*      */       {
/*      */         try
/*      */         {
/* 2085 */           Thread.sleep(9223372036854775807L);
/*      */         } catch (InterruptedException localInterruptedException) {
/*      */         }
/*      */       }
/*      */     } catch (Exception localException2) {
/* 2090 */       System.err.println(MessageFormat.format(getTextResource("rmid.unexpected.exception"), new Object[] { localException2 }));
/*      */ 
/* 2093 */       localException2.printStackTrace();
/*      */ 
/* 2095 */       System.exit(1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static String getTextResource(String paramString)
/*      */   {
/* 2102 */     if (resources == null) {
/*      */       try {
/* 2104 */         resources = ResourceBundle.getBundle("sun.rmi.server.resources.rmid");
/*      */       }
/*      */       catch (MissingResourceException localMissingResourceException1) {
/*      */       }
/* 2108 */       if (resources == null)
/*      */       {
/* 2110 */         return "[missing resource file: " + paramString + "]";
/*      */       }
/*      */     }
/*      */ 
/* 2114 */     String str = null;
/*      */     try {
/* 2116 */       str = resources.getString(paramString);
/*      */     }
/*      */     catch (MissingResourceException localMissingResourceException2) {
/*      */     }
/* 2120 */     if (str == null) {
/* 2121 */       return "[missing resource: " + paramString + "]";
/*      */     }
/* 2123 */     return str;
/*      */   }
/*      */ 
/*      */   private synchronized String Pstartgroup()
/*      */     throws ActivationException
/*      */   {
/*      */     while (true)
/*      */     {
/* 2140 */       checkShutdown();
/*      */ 
/* 2142 */       if (this.groupSemaphore > 0) {
/* 2143 */         this.groupSemaphore -= 1;
/* 2144 */         return "Group-" + this.groupCounter++;
/*      */       }
/*      */       try
/*      */       {
/* 2148 */         wait();
/*      */       }
/*      */       catch (InterruptedException localInterruptedException)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void Vstartgroup()
/*      */   {
/* 2161 */     this.groupSemaphore += 1;
/* 2162 */     notifyAll();
/*      */   }
/*      */ 
/*      */   private static class ActLogHandler extends LogHandler
/*      */   {
/*      */     public Object initialSnapshot()
/*      */     {
/* 1517 */       return new Activation(null);
/*      */     }
/*      */ 
/*      */     public Object applyUpdate(Object paramObject1, Object paramObject2)
/*      */       throws Exception
/*      */     {
/* 1523 */       return ((Activation.LogRecord)paramObject1).apply(paramObject2);
/*      */     }
/*      */   }
/*      */ 
/*      */   class ActivationMonitorImpl extends UnicastRemoteObject
/*      */     implements ActivationMonitor
/*      */   {
/*      */     private static final long serialVersionUID = -6214940464757948867L;
/*      */ 
/*      */     ActivationMonitorImpl(int paramRMIServerSocketFactory, RMIServerSocketFactory arg3)
/*      */       throws RemoteException
/*      */     {
/*  418 */       super(null, localRMIServerSocketFactory);
/*      */     }
/*      */ 
/*      */     public void inactiveObject(ActivationID paramActivationID) throws UnknownObjectException, RemoteException
/*      */     {
/*      */       try
/*      */       {
/*  425 */         Activation.this.checkShutdown();
/*      */       } catch (ActivationException localActivationException) {
/*  427 */         return;
/*      */       }
/*  429 */       RegistryImpl.checkAccess("Activator.inactiveObject");
/*  430 */       Activation.this.getGroupEntry(paramActivationID).inactiveObject(paramActivationID);
/*      */     }
/*      */ 
/*      */     public void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
/*      */       throws UnknownObjectException, RemoteException
/*      */     {
/*      */       try
/*      */       {
/*  438 */         Activation.this.checkShutdown();
/*      */       } catch (ActivationException localActivationException) {
/*  440 */         return;
/*      */       }
/*  442 */       RegistryImpl.checkAccess("ActivationSystem.activeObject");
/*  443 */       Activation.this.getGroupEntry(paramActivationID).activeObject(paramActivationID, paramMarshalledObject);
/*      */     }
/*      */ 
/*      */     public void inactiveGroup(ActivationGroupID paramActivationGroupID, long paramLong)
/*      */       throws UnknownGroupException, RemoteException
/*      */     {
/*      */       try
/*      */       {
/*  451 */         Activation.this.checkShutdown();
/*      */       } catch (ActivationException localActivationException) {
/*  453 */         return;
/*      */       }
/*  455 */       RegistryImpl.checkAccess("ActivationMonitor.inactiveGroup");
/*  456 */       Activation.this.getGroupEntry(paramActivationGroupID).inactiveGroup(paramLong, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ActivationServerSocketFactory
/*      */     implements RMIServerSocketFactory
/*      */   {
/*      */     private final ServerSocket serverSocket;
/*      */ 
/*      */     ActivationServerSocketFactory(ServerSocket paramServerSocket)
/*      */     {
/* 2185 */       this.serverSocket = paramServerSocket;
/*      */     }
/*      */ 
/*      */     public ServerSocket createServerSocket(int paramInt)
/*      */       throws IOException
/*      */     {
/* 2195 */       return new Activation.DelayedAcceptServerSocket(this.serverSocket);
/*      */     }
/*      */   }
/*      */ 
/*      */   class ActivationSystemImpl extends RemoteServer
/*      */     implements ActivationSystem
/*      */   {
/*      */     private static final long serialVersionUID = 9100152600327688967L;
/*      */ 
/*      */     ActivationSystemImpl(int paramRMIServerSocketFactory, RMIServerSocketFactory arg3)
/*      */       throws RemoteException
/*      */     {
/*      */       RMIServerSocketFactory localRMIServerSocketFactory;
/*  477 */       LiveRef localLiveRef = new LiveRef(new ObjID(4), paramRMIServerSocketFactory, null, localRMIServerSocketFactory);
/*  478 */       UnicastServerRef localUnicastServerRef = new UnicastServerRef(localLiveRef);
/*  479 */       this.ref = localUnicastServerRef;
/*  480 */       localUnicastServerRef.exportObject(this, null);
/*      */     }
/*      */ 
/*      */     public ActivationID registerObject(ActivationDesc paramActivationDesc)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  486 */       Activation.this.checkShutdown();
/*  487 */       RegistryImpl.checkAccess("ActivationSystem.registerObject");
/*      */ 
/*  489 */       ActivationGroupID localActivationGroupID = paramActivationDesc.getGroupID();
/*  490 */       ActivationID localActivationID = new ActivationID(Activation.this.activatorStub);
/*  491 */       Activation.this.getGroupEntry(localActivationGroupID).registerObject(localActivationID, paramActivationDesc, true);
/*  492 */       return localActivationID;
/*      */     }
/*      */ 
/*      */     public void unregisterObject(ActivationID paramActivationID)
/*      */       throws ActivationException, UnknownObjectException, RemoteException
/*      */     {
/*  498 */       Activation.this.checkShutdown();
/*  499 */       RegistryImpl.checkAccess("ActivationSystem.unregisterObject");
/*  500 */       Activation.this.getGroupEntry(paramActivationID).unregisterObject(paramActivationID, true);
/*      */     }
/*      */ 
/*      */     public ActivationGroupID registerGroup(ActivationGroupDesc paramActivationGroupDesc)
/*      */       throws ActivationException, RemoteException
/*      */     {
/*  506 */       Activation.this.checkShutdown();
/*  507 */       RegistryImpl.checkAccess("ActivationSystem.registerGroup");
/*  508 */       Activation.this.checkArgs(paramActivationGroupDesc, null);
/*      */ 
/*  510 */       ActivationGroupID localActivationGroupID = new ActivationGroupID(Activation.this.systemStub);
/*  511 */       Activation.GroupEntry localGroupEntry = new Activation.GroupEntry(Activation.this, localActivationGroupID, paramActivationGroupDesc);
/*      */ 
/*  513 */       Activation.this.groupTable.put(localActivationGroupID, localGroupEntry);
/*  514 */       Activation.this.addLogRecord(new Activation.LogRegisterGroup(localActivationGroupID, paramActivationGroupDesc));
/*  515 */       return localActivationGroupID;
/*      */     }
/*      */ 
/*      */     public ActivationMonitor activeGroup(ActivationGroupID paramActivationGroupID, ActivationInstantiator paramActivationInstantiator, long paramLong)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  523 */       Activation.this.checkShutdown();
/*  524 */       RegistryImpl.checkAccess("ActivationSystem.activeGroup");
/*      */ 
/*  526 */       Activation.this.getGroupEntry(paramActivationGroupID).activeGroup(paramActivationInstantiator, paramLong);
/*  527 */       return Activation.this.monitor;
/*      */     }
/*      */ 
/*      */     public void unregisterGroup(ActivationGroupID paramActivationGroupID)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  533 */       Activation.this.checkShutdown();
/*  534 */       RegistryImpl.checkAccess("ActivationSystem.unregisterGroup");
/*      */ 
/*  538 */       Activation.this.removeGroupEntry(paramActivationGroupID).unregisterGroup(true);
/*      */     }
/*      */ 
/*      */     public ActivationDesc setActivationDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
/*      */       throws ActivationException, UnknownObjectException, RemoteException
/*      */     {
/*  545 */       Activation.this.checkShutdown();
/*  546 */       RegistryImpl.checkAccess("ActivationSystem.setActivationDesc");
/*      */ 
/*  548 */       if (!Activation.this.getGroupID(paramActivationID).equals(paramActivationDesc.getGroupID())) {
/*  549 */         throw new ActivationException("ActivationDesc contains wrong group");
/*      */       }
/*      */ 
/*  552 */       return Activation.this.getGroupEntry(paramActivationID).setActivationDesc(paramActivationID, paramActivationDesc, true);
/*      */     }
/*      */ 
/*      */     public ActivationGroupDesc setActivationGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  559 */       Activation.this.checkShutdown();
/*  560 */       RegistryImpl.checkAccess("ActivationSystem.setActivationGroupDesc");
/*      */ 
/*  563 */       Activation.this.checkArgs(paramActivationGroupDesc, null);
/*  564 */       return Activation.this.getGroupEntry(paramActivationGroupID).setActivationGroupDesc(paramActivationGroupID, paramActivationGroupDesc, true);
/*      */     }
/*      */ 
/*      */     public ActivationDesc getActivationDesc(ActivationID paramActivationID)
/*      */       throws ActivationException, UnknownObjectException, RemoteException
/*      */     {
/*  570 */       Activation.this.checkShutdown();
/*  571 */       RegistryImpl.checkAccess("ActivationSystem.getActivationDesc");
/*      */ 
/*  573 */       return Activation.this.getGroupEntry(paramActivationID).getActivationDesc(paramActivationID);
/*      */     }
/*      */ 
/*      */     public ActivationGroupDesc getActivationGroupDesc(ActivationGroupID paramActivationGroupID)
/*      */       throws ActivationException, UnknownGroupException, RemoteException
/*      */     {
/*  579 */       Activation.this.checkShutdown();
/*  580 */       RegistryImpl.checkAccess("ActivationSystem.getActivationGroupDesc");
/*      */ 
/*  583 */       return Activation.this.getGroupEntry(paramActivationGroupID).desc;
/*      */     }
/*      */ 
/*      */     public void shutdown()
/*      */       throws AccessException
/*      */     {
/*  591 */       RegistryImpl.checkAccess("ActivationSystem.shutdown");
/*      */ 
/*  593 */       Object localObject1 = Activation.this.startupLock;
/*  594 */       if (localObject1 != null) {
/*  595 */         synchronized (localObject1)
/*      */         {
/*      */         }
/*      */       }
/*      */ 
/*  600 */       synchronized (Activation.this) {
/*  601 */         if (!Activation.this.shuttingDown) {
/*  602 */           Activation.this.shuttingDown = true;
/*  603 */           new Activation.Shutdown(Activation.this).start();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class ActivatorImpl extends RemoteServer
/*      */     implements Activator
/*      */   {
/*      */     private static final long serialVersionUID = -3654244726254566136L;
/*      */ 
/*      */     ActivatorImpl(int paramRMIServerSocketFactory, RMIServerSocketFactory arg3)
/*      */       throws RemoteException
/*      */     {
/*      */       RMIServerSocketFactory localRMIServerSocketFactory;
/*  394 */       LiveRef localLiveRef = new LiveRef(new ObjID(1), paramRMIServerSocketFactory, null, localRMIServerSocketFactory);
/*      */ 
/*  396 */       UnicastServerRef localUnicastServerRef = new UnicastServerRef(localLiveRef);
/*  397 */       this.ref = localUnicastServerRef;
/*  398 */       localUnicastServerRef.exportObject(this, null, false);
/*      */     }
/*      */ 
/*      */     public MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean)
/*      */       throws ActivationException, UnknownObjectException, RemoteException
/*      */     {
/*  405 */       Activation.this.checkShutdown();
/*  406 */       return Activation.this.getGroupEntry(paramActivationID).activate(paramActivationID, paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class DefaultExecPolicy
/*      */   {
/*      */     public void checkExecCommand(ActivationGroupDesc paramActivationGroupDesc, String[] paramArrayOfString)
/*      */       throws SecurityException
/*      */     {
/* 1773 */       PermissionCollection localPermissionCollection = getExecPermissions();
/*      */ 
/* 1778 */       Properties localProperties = paramActivationGroupDesc.getPropertyOverrides();
/*      */       String str1;
/*      */       Object localObject3;
/* 1779 */       if (localProperties != null) {
/* 1780 */         localObject1 = localProperties.propertyNames();
/* 1781 */         while (((Enumeration)localObject1).hasMoreElements()) {
/* 1782 */           localObject2 = (String)((Enumeration)localObject1).nextElement();
/* 1783 */           str1 = localProperties.getProperty((String)localObject2);
/* 1784 */           localObject3 = "-D" + (String)localObject2 + "=" + str1;
/*      */           try {
/* 1786 */             checkPermission(localPermissionCollection, new ExecOptionPermission((String)localObject3));
/*      */           }
/*      */           catch (AccessControlException localAccessControlException) {
/* 1789 */             if (str1.equals("")) {
/* 1790 */               checkPermission(localPermissionCollection, new ExecOptionPermission("-D" + (String)localObject2));
/*      */             }
/*      */             else {
/* 1793 */               throw localAccessControlException;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1803 */       Object localObject1 = paramActivationGroupDesc.getClassName();
/* 1804 */       if (((localObject1 != null) && (!((String)localObject1).equals(ActivationGroupImpl.class.getName()))) || (paramActivationGroupDesc.getLocation() != null) || (paramActivationGroupDesc.getData() != null))
/*      */       {
/* 1810 */         throw new AccessControlException("access denied (custom group implementation not allowed)");
/*      */       }
/*      */ 
/* 1819 */       Object localObject2 = paramActivationGroupDesc.getCommandEnvironment();
/* 1820 */       if (localObject2 != null) {
/* 1821 */         str1 = ((ActivationGroupDesc.CommandEnvironment)localObject2).getCommandPath();
/* 1822 */         if (str1 != null) {
/* 1823 */           checkPermission(localPermissionCollection, new ExecPermission(str1));
/*      */         }
/*      */ 
/* 1826 */         localObject3 = ((ActivationGroupDesc.CommandEnvironment)localObject2).getCommandOptions();
/* 1827 */         if (localObject3 != null)
/* 1828 */           for (String str2 : localObject3)
/* 1829 */             checkPermission(localPermissionCollection, new ExecOptionPermission(str2));
/*      */       }
/*      */     }
/*      */ 
/*      */     static void checkConfiguration()
/*      */     {
/* 1842 */       Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public Policy run() {
/* 1845 */           return Policy.getPolicy();
/*      */         }
/*      */       });
/* 1848 */       if (!(localPolicy instanceof PolicyFile)) {
/* 1849 */         return;
/*      */       }
/* 1851 */       PermissionCollection localPermissionCollection = getExecPermissions();
/* 1852 */       Enumeration localEnumeration = localPermissionCollection.elements();
/* 1853 */       while (localEnumeration.hasMoreElements())
/*      */       {
/* 1855 */         Permission localPermission = (Permission)localEnumeration.nextElement();
/* 1856 */         if (((localPermission instanceof AllPermission)) || ((localPermission instanceof ExecPermission)) || ((localPermission instanceof ExecOptionPermission)))
/*      */         {
/* 1860 */           return;
/*      */         }
/*      */       }
/* 1863 */       System.err.println(Activation.getTextResource("rmid.exec.perms.inadequate"));
/*      */     }
/*      */ 
/*      */     private static PermissionCollection getExecPermissions()
/*      */     {
/* 1874 */       PermissionCollection localPermissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
/*      */       {
/*      */         public PermissionCollection run() {
/* 1877 */           CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
/*      */ 
/* 1879 */           Policy localPolicy = Policy.getPolicy();
/* 1880 */           if (localPolicy != null) {
/* 1881 */             return localPolicy.getPermissions(localCodeSource);
/*      */           }
/* 1883 */           return new Permissions();
/*      */         }
/*      */       });
/* 1888 */       return localPermissionCollection;
/*      */     }
/*      */ 
/*      */     private static void checkPermission(PermissionCollection paramPermissionCollection, Permission paramPermission)
/*      */       throws AccessControlException
/*      */     {
/* 1895 */       if (!paramPermissionCollection.implies(paramPermission))
/* 1896 */         throw new AccessControlException("access denied " + paramPermission.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class DelayedAcceptServerSocket extends ServerSocket
/*      */   {
/*      */     private final ServerSocket serverSocket;
/*      */ 
/*      */     DelayedAcceptServerSocket(ServerSocket paramServerSocket)
/*      */       throws IOException
/*      */     {
/* 2213 */       this.serverSocket = paramServerSocket;
/*      */     }
/*      */ 
/*      */     public void bind(SocketAddress paramSocketAddress) throws IOException {
/* 2217 */       this.serverSocket.bind(paramSocketAddress);
/*      */     }
/*      */ 
/*      */     public void bind(SocketAddress paramSocketAddress, int paramInt)
/*      */       throws IOException
/*      */     {
/* 2223 */       this.serverSocket.bind(paramSocketAddress, paramInt);
/*      */     }
/*      */ 
/*      */     public InetAddress getInetAddress() {
/* 2227 */       return this.serverSocket.getInetAddress();
/*      */     }
/*      */ 
/*      */     public int getLocalPort() {
/* 2231 */       return this.serverSocket.getLocalPort();
/*      */     }
/*      */ 
/*      */     public SocketAddress getLocalSocketAddress() {
/* 2235 */       return this.serverSocket.getLocalSocketAddress();
/*      */     }
/*      */ 
/*      */     public Socket accept()
/*      */       throws IOException
/*      */     {
/* 2243 */       synchronized (Activation.initLock) {
/*      */         try {
/* 2245 */           while (!Activation.initDone)
/* 2246 */             Activation.initLock.wait();
/*      */         }
/*      */         catch (InterruptedException localInterruptedException) {
/* 2249 */           throw new AssertionError(localInterruptedException);
/*      */         }
/*      */       }
/* 2252 */       return this.serverSocket.accept();
/*      */     }
/*      */ 
/*      */     public void close() throws IOException {
/* 2256 */       this.serverSocket.close();
/*      */     }
/*      */ 
/*      */     public ServerSocketChannel getChannel() {
/* 2260 */       return this.serverSocket.getChannel();
/*      */     }
/*      */ 
/*      */     public boolean isBound() {
/* 2264 */       return this.serverSocket.isBound();
/*      */     }
/*      */ 
/*      */     public boolean isClosed() {
/* 2268 */       return this.serverSocket.isClosed();
/*      */     }
/*      */ 
/*      */     public void setSoTimeout(int paramInt)
/*      */       throws SocketException
/*      */     {
/* 2274 */       this.serverSocket.setSoTimeout(paramInt);
/*      */     }
/*      */ 
/*      */     public int getSoTimeout() throws IOException {
/* 2278 */       return this.serverSocket.getSoTimeout();
/*      */     }
/*      */ 
/*      */     public void setReuseAddress(boolean paramBoolean) throws SocketException {
/* 2282 */       this.serverSocket.setReuseAddress(paramBoolean);
/*      */     }
/*      */ 
/*      */     public boolean getReuseAddress() throws SocketException {
/* 2286 */       return this.serverSocket.getReuseAddress();
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 2290 */       return this.serverSocket.toString();
/*      */     }
/*      */ 
/*      */     public void setReceiveBufferSize(int paramInt)
/*      */       throws SocketException
/*      */     {
/* 2296 */       this.serverSocket.setReceiveBufferSize(paramInt);
/*      */     }
/*      */ 
/*      */     public int getReceiveBufferSize()
/*      */       throws SocketException
/*      */     {
/* 2302 */       return this.serverSocket.getReceiveBufferSize();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class GroupEntry
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 7222464070032993304L;
/*      */     private static final int MAX_TRIES = 2;
/*      */     private static final int NORMAL = 0;
/*      */     private static final int CREATING = 1;
/*      */     private static final int TERMINATE = 2;
/*      */     private static final int TERMINATING = 3;
/*  805 */     ActivationGroupDesc desc = null;
/*  806 */     ActivationGroupID groupID = null;
/*  807 */     long incarnation = 0L;
/*  808 */     Map<ActivationID, Activation.ObjectEntry> objects = new HashMap();
/*      */ 
/*  810 */     Set<ActivationID> restartSet = new HashSet();
/*      */ 
/*  812 */     transient ActivationInstantiator group = null;
/*  813 */     transient int status = 0;
/*  814 */     transient long waitTime = 0L;
/*  815 */     transient String groupName = null;
/*  816 */     transient Process child = null;
/*  817 */     transient boolean removed = false;
/*  818 */     transient Watchdog watchdog = null;
/*      */ 
/*      */     GroupEntry(ActivationGroupID paramActivationGroupDesc, ActivationGroupDesc arg3) {
/*  821 */       this.groupID = paramActivationGroupDesc;
/*      */       Object localObject;
/*  822 */       this.desc = localObject;
/*      */     }
/*      */ 
/*      */     void restartServices() {
/*  826 */       Iterator localIterator = null;
/*      */ 
/*  828 */       synchronized (this) {
/*  829 */         if (this.restartSet.isEmpty()) {
/*  830 */           return;
/*      */         }
/*      */ 
/*  839 */         localIterator = new HashSet(this.restartSet).iterator();
/*      */       }
/*      */ 
/*  842 */       while (localIterator.hasNext()) {
/*  843 */         ??? = (ActivationID)localIterator.next();
/*      */         try {
/*  845 */           activate((ActivationID)???, true);
/*      */         } catch (Exception localException) {
/*  847 */           if (Activation.this.shuttingDown) {
/*  848 */             return;
/*      */           }
/*  850 */           System.err.println(Activation.getTextResource("rmid.restart.service.warning"));
/*      */ 
/*  852 */           localException.printStackTrace();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     synchronized void activeGroup(ActivationInstantiator paramActivationInstantiator, long paramLong)
/*      */       throws ActivationException, UnknownGroupException
/*      */     {
/*  861 */       if (this.incarnation != paramLong) {
/*  862 */         throw new ActivationException("invalid incarnation");
/*      */       }
/*      */ 
/*  865 */       if (this.group != null) {
/*  866 */         if (this.group.equals(paramActivationInstantiator)) {
/*  867 */           return;
/*      */         }
/*  869 */         throw new ActivationException("group already active");
/*      */       }
/*      */ 
/*  873 */       if ((this.child != null) && (this.status != 1)) {
/*  874 */         throw new ActivationException("group not being created");
/*      */       }
/*      */ 
/*  877 */       this.group = paramActivationInstantiator;
/*  878 */       this.status = 0;
/*  879 */       notifyAll();
/*      */     }
/*      */ 
/*      */     private void checkRemoved() throws UnknownGroupException {
/*  883 */       if (this.removed)
/*  884 */         throw new UnknownGroupException("group removed");
/*      */     }
/*      */ 
/*      */     private Activation.ObjectEntry getObjectEntry(ActivationID paramActivationID)
/*      */       throws UnknownObjectException
/*      */     {
/*  891 */       if (this.removed) {
/*  892 */         throw new UnknownObjectException("object's group removed");
/*      */       }
/*  894 */       Activation.ObjectEntry localObjectEntry = (Activation.ObjectEntry)this.objects.get(paramActivationID);
/*  895 */       if (localObjectEntry == null) {
/*  896 */         throw new UnknownObjectException("object unknown");
/*      */       }
/*  898 */       return localObjectEntry;
/*      */     }
/*      */ 
/*      */     synchronized void registerObject(ActivationID paramActivationID, ActivationDesc paramActivationDesc, boolean paramBoolean)
/*      */       throws UnknownGroupException, ActivationException
/*      */     {
/*  906 */       checkRemoved();
/*  907 */       this.objects.put(paramActivationID, new Activation.ObjectEntry(paramActivationDesc));
/*  908 */       if (paramActivationDesc.getRestartMode() == true) {
/*  909 */         this.restartSet.add(paramActivationID);
/*      */       }
/*      */ 
/*  913 */       Activation.this.idTable.put(paramActivationID, this.groupID);
/*      */ 
/*  915 */       if (paramBoolean)
/*  916 */         Activation.this.addLogRecord(new Activation.LogRegisterObject(paramActivationID, paramActivationDesc));
/*      */     }
/*      */ 
/*      */     synchronized void unregisterObject(ActivationID paramActivationID, boolean paramBoolean)
/*      */       throws UnknownGroupException, ActivationException
/*      */     {
/*  923 */       Activation.ObjectEntry localObjectEntry = getObjectEntry(paramActivationID);
/*  924 */       localObjectEntry.removed = true;
/*  925 */       this.objects.remove(paramActivationID);
/*  926 */       if (localObjectEntry.desc.getRestartMode() == true) {
/*  927 */         this.restartSet.remove(paramActivationID);
/*      */       }
/*      */ 
/*  931 */       Activation.this.idTable.remove(paramActivationID);
/*  932 */       if (paramBoolean)
/*  933 */         Activation.this.addLogRecord(new Activation.LogUnregisterObject(paramActivationID));
/*      */     }
/*      */ 
/*      */     synchronized void unregisterGroup(boolean paramBoolean)
/*      */       throws UnknownGroupException, ActivationException
/*      */     {
/*  940 */       checkRemoved();
/*  941 */       this.removed = true;
/*      */ 
/*  943 */       for (Map.Entry localEntry : this.objects.entrySet())
/*      */       {
/*  945 */         ActivationID localActivationID = (ActivationID)localEntry.getKey();
/*  946 */         Activation.this.idTable.remove(localActivationID);
/*  947 */         Activation.ObjectEntry localObjectEntry = (Activation.ObjectEntry)localEntry.getValue();
/*  948 */         localObjectEntry.removed = true;
/*      */       }
/*  950 */       this.objects.clear();
/*  951 */       this.restartSet.clear();
/*  952 */       reset();
/*  953 */       childGone();
/*      */ 
/*  956 */       if (paramBoolean)
/*  957 */         Activation.this.addLogRecord(new Activation.LogUnregisterGroup(this.groupID));
/*      */     }
/*      */ 
/*      */     synchronized ActivationDesc setActivationDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc, boolean paramBoolean)
/*      */       throws UnknownObjectException, UnknownGroupException, ActivationException
/*      */     {
/*  967 */       Activation.ObjectEntry localObjectEntry = getObjectEntry(paramActivationID);
/*  968 */       ActivationDesc localActivationDesc = localObjectEntry.desc;
/*  969 */       localObjectEntry.desc = paramActivationDesc;
/*  970 */       if (paramActivationDesc.getRestartMode() == true)
/*  971 */         this.restartSet.add(paramActivationID);
/*      */       else {
/*  973 */         this.restartSet.remove(paramActivationID);
/*      */       }
/*      */ 
/*  976 */       if (paramBoolean) {
/*  977 */         Activation.this.addLogRecord(new Activation.LogUpdateDesc(paramActivationID, paramActivationDesc));
/*      */       }
/*      */ 
/*  980 */       return localActivationDesc;
/*      */     }
/*      */ 
/*      */     synchronized ActivationDesc getActivationDesc(ActivationID paramActivationID)
/*      */       throws UnknownObjectException, UnknownGroupException
/*      */     {
/*  986 */       return getObjectEntry(paramActivationID).desc;
/*      */     }
/*      */ 
/*      */     synchronized ActivationGroupDesc setActivationGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc, boolean paramBoolean)
/*      */       throws UnknownGroupException, ActivationException
/*      */     {
/*  995 */       checkRemoved();
/*  996 */       ActivationGroupDesc localActivationGroupDesc = this.desc;
/*  997 */       this.desc = paramActivationGroupDesc;
/*      */ 
/*  999 */       if (paramBoolean) {
/* 1000 */         Activation.this.addLogRecord(new Activation.LogUpdateGroupDesc(paramActivationGroupID, paramActivationGroupDesc));
/*      */       }
/* 1002 */       return localActivationGroupDesc;
/*      */     }
/*      */ 
/*      */     synchronized void inactiveGroup(long paramLong, boolean paramBoolean)
/*      */       throws UnknownGroupException
/*      */     {
/* 1008 */       checkRemoved();
/* 1009 */       if (this.incarnation != paramLong) {
/* 1010 */         throw new UnknownGroupException("invalid incarnation");
/*      */       }
/*      */ 
/* 1013 */       reset();
/* 1014 */       if (paramBoolean) {
/* 1015 */         terminate();
/* 1016 */       } else if ((this.child != null) && (this.status == 0)) {
/* 1017 */         this.status = 2;
/* 1018 */         this.watchdog.noRestart();
/*      */       }
/*      */     }
/*      */ 
/*      */     synchronized void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
/*      */       throws UnknownObjectException
/*      */     {
/* 1026 */       getObjectEntry(paramActivationID).stub = paramMarshalledObject;
/*      */     }
/*      */ 
/*      */     synchronized void inactiveObject(ActivationID paramActivationID)
/*      */       throws UnknownObjectException
/*      */     {
/* 1032 */       getObjectEntry(paramActivationID).reset();
/*      */     }
/*      */ 
/*      */     private synchronized void reset() {
/* 1036 */       this.group = null;
/* 1037 */       for (Activation.ObjectEntry localObjectEntry : this.objects.values())
/* 1038 */         localObjectEntry.reset();
/*      */     }
/*      */ 
/*      */     private void childGone()
/*      */     {
/* 1043 */       if (this.child != null) {
/* 1044 */         this.child = null;
/* 1045 */         this.watchdog.dispose();
/* 1046 */         this.watchdog = null;
/* 1047 */         this.status = 0;
/* 1048 */         notifyAll();
/*      */       }
/*      */     }
/*      */ 
/*      */     private void terminate() {
/* 1053 */       if ((this.child != null) && (this.status != 3)) {
/* 1054 */         this.child.destroy();
/* 1055 */         this.status = 3;
/* 1056 */         this.waitTime = (System.currentTimeMillis() + Activation.groupTimeout);
/* 1057 */         notifyAll();
/*      */       }
/*      */     }
/*      */ 
/*      */     private void await() {
/*      */       while (true)
/* 1063 */         switch (this.status) {
/*      */         case 0:
/* 1065 */           return;
/*      */         case 2:
/* 1067 */           terminate();
/*      */         case 3:
/*      */           try {
/* 1070 */             this.child.exitValue();
/*      */           } catch (IllegalThreadStateException localIllegalThreadStateException) {
/* 1072 */             long l = System.currentTimeMillis();
/* 1073 */             if (this.waitTime > l) {
/*      */               try {
/* 1075 */                 wait(this.waitTime - l);
/*      */               } catch (InterruptedException localInterruptedException2) {
/*      */               }
/* 1078 */               continue;
/*      */             }
/*      */           }
/*      */ 
/* 1082 */           childGone();
/* 1083 */           return;
/*      */         case 1:
/*      */           try {
/* 1086 */             wait();
/*      */           }
/*      */           catch (InterruptedException localInterruptedException1)
/*      */           {
/*      */           }
/*      */         }
/*      */     }
/*      */ 
/*      */     void shutdownFast() {
/* 1095 */       Process localProcess = this.child;
/* 1096 */       if (localProcess != null)
/* 1097 */         localProcess.destroy();
/*      */     }
/*      */ 
/*      */     synchronized void shutdown()
/*      */     {
/* 1102 */       reset();
/* 1103 */       terminate();
/* 1104 */       await();
/*      */     }
/*      */ 
/*      */     MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean)
/*      */       throws ActivationException
/*      */     {
/* 1111 */       Object localObject1 = null;
/*      */ 
/* 1117 */       for (int i = 2; i > 0; i--)
/*      */       {
/*      */         Activation.ObjectEntry localObjectEntry;
/*      */         ActivationInstantiator localActivationInstantiator;
/*      */         long l;
/* 1123 */         synchronized (this) {
/* 1124 */           localObjectEntry = getObjectEntry(paramActivationID);
/*      */ 
/* 1126 */           if ((!paramBoolean) && (localObjectEntry.stub != null)) {
/* 1127 */             return localObjectEntry.stub;
/*      */           }
/* 1129 */           localActivationInstantiator = getInstantiator(this.groupID);
/* 1130 */           l = this.incarnation;
/*      */         }
/*      */ 
/* 1133 */         int j = 0;
/* 1134 */         boolean bool = false;
/*      */         try
/*      */         {
/* 1137 */           return localObjectEntry.activate(paramActivationID, paramBoolean, localActivationInstantiator);
/*      */         } catch (NoSuchObjectException localNoSuchObjectException) {
/* 1139 */           j = 1;
/* 1140 */           localObject1 = localNoSuchObjectException;
/*      */         } catch (ConnectException localConnectException) {
/* 1142 */           j = 1;
/* 1143 */           bool = true;
/* 1144 */           localObject1 = localConnectException;
/*      */         } catch (ConnectIOException localConnectIOException) {
/* 1146 */           j = 1;
/* 1147 */           bool = true;
/* 1148 */           localObject1 = localConnectIOException;
/*      */         } catch (InactiveGroupException localInactiveGroupException) {
/* 1150 */           j = 1;
/* 1151 */           localObject1 = localInactiveGroupException;
/*      */         }
/*      */         catch (RemoteException localRemoteException) {
/* 1154 */           if (localObject1 == null) {
/* 1155 */             localObject1 = localRemoteException;
/*      */           }
/*      */         }
/*      */ 
/* 1159 */         if (j != 0) {
/*      */           try
/*      */           {
/* 1162 */             System.err.println(MessageFormat.format(Activation.getTextResource("rmid.group.inactive"), new Object[] { localObject1.toString() }));
/*      */ 
/* 1166 */             localObject1.printStackTrace();
/* 1167 */             Activation.this.getGroupEntry(this.groupID).inactiveGroup(l, bool);
/*      */           }
/*      */           catch (UnknownGroupException localUnknownGroupException)
/*      */           {
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1180 */       throw new ActivationException("object activation failed after 2 tries", localObject1);
/*      */     }
/*      */ 
/*      */     private ActivationInstantiator getInstantiator(ActivationGroupID paramActivationGroupID)
/*      */       throws ActivationException
/*      */     {
/* 1192 */       assert (Thread.holdsLock(this));
/*      */ 
/* 1194 */       await();
/* 1195 */       if (this.group != null) {
/* 1196 */         return this.group;
/*      */       }
/* 1198 */       checkRemoved();
/* 1199 */       int i = 0;
/*      */       try
/*      */       {
/* 1202 */         this.groupName = Activation.this.Pstartgroup();
/* 1203 */         i = 1;
/* 1204 */         String[] arrayOfString = Activation.this.activationArgs(this.desc);
/* 1205 */         Activation.this.checkArgs(this.desc, arrayOfString);
/*      */         Object localObject1;
/* 1207 */         if (Activation.debugExec) {
/* 1208 */           localObject1 = new StringBuffer(arrayOfString[0]);
/*      */ 
/* 1210 */           for (int j = 1; j < arrayOfString.length; j++) {
/* 1211 */             ((StringBuffer)localObject1).append(' ');
/* 1212 */             ((StringBuffer)localObject1).append(arrayOfString[j]);
/*      */           }
/* 1214 */           System.err.println(MessageFormat.format(Activation.getTextResource("rmid.exec.command"), new Object[] { ((StringBuffer)localObject1).toString() }));
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1221 */           this.child = Runtime.getRuntime().exec(arrayOfString);
/* 1222 */           this.status = 1;
/* 1223 */           this.incarnation += 1L;
/* 1224 */           this.watchdog = new Watchdog();
/* 1225 */           this.watchdog.start();
/* 1226 */           Activation.this.addLogRecord(new Activation.LogGroupIncarnation(paramActivationGroupID, this.incarnation));
/*      */ 
/* 1229 */           PipeWriter.plugTogetherPair(this.child.getInputStream(), System.out, this.child.getErrorStream(), System.err);
/*      */ 
/* 1233 */           localObject1 = new MarshalOutputStream(this.child.getOutputStream());
/*      */ 
/* 1235 */           ((MarshalOutputStream)localObject1).writeObject(paramActivationGroupID);
/* 1236 */           ((MarshalOutputStream)localObject1).writeObject(this.desc);
/* 1237 */           ((MarshalOutputStream)localObject1).writeLong(this.incarnation);
/* 1238 */           ((MarshalOutputStream)localObject1).flush();
/* 1239 */           ((MarshalOutputStream)localObject1).close();
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/* 1243 */           terminate();
/* 1244 */           throw new ActivationException("unable to create activation group", localIOException);
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1249 */           long l1 = System.currentTimeMillis();
/* 1250 */           long l2 = l1 + Activation.execTimeout;
/*      */           do {
/* 1252 */             wait(l2 - l1);
/* 1253 */             if (this.group != null) {
/* 1254 */               return this.group;
/*      */             }
/* 1256 */             l1 = System.currentTimeMillis();
/* 1257 */             if (this.status != 1) break;  } while (l1 < l2);
/*      */         }
/*      */         catch (InterruptedException localInterruptedException) {
/*      */         }
/* 1261 */         terminate();
/* 1262 */         throw new ActivationException(this.removed ? "activation group unregistered" : "timeout creating child process");
/*      */       }
/*      */       finally
/*      */       {
/* 1267 */         if (i != 0)
/* 1268 */           Activation.this.Vstartgroup();
/*      */       }
/*      */     }
/*      */ 
/*      */     private class Watchdog extends Thread
/*      */     {
/* 1277 */       private final Process groupProcess = Activation.GroupEntry.this.child;
/* 1278 */       private final long groupIncarnation = Activation.GroupEntry.this.incarnation;
/* 1279 */       private boolean canInterrupt = true;
/* 1280 */       private boolean shouldQuit = false;
/* 1281 */       private boolean shouldRestart = true;
/*      */ 
/*      */       Watchdog() {
/* 1284 */         super();
/* 1285 */         setDaemon(true);
/*      */       }
/*      */ 
/*      */       public void run()
/*      */       {
/* 1290 */         if (this.shouldQuit) {
/* 1291 */           return;
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1298 */           this.groupProcess.waitFor();
/*      */         } catch (InterruptedException localInterruptedException) {
/* 1300 */           return;
/*      */         }
/*      */ 
/* 1303 */         int i = 0;
/* 1304 */         synchronized (Activation.GroupEntry.this) {
/* 1305 */           if (this.shouldQuit) {
/* 1306 */             return;
/*      */           }
/* 1308 */           this.canInterrupt = false;
/* 1309 */           interrupted();
/*      */ 
/* 1314 */           if (this.groupIncarnation == Activation.GroupEntry.this.incarnation) {
/* 1315 */             i = (this.shouldRestart) && (!Activation.this.shuttingDown) ? 1 : 0;
/* 1316 */             Activation.GroupEntry.this.reset();
/* 1317 */             Activation.GroupEntry.this.childGone();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1325 */         if (i != 0)
/* 1326 */           Activation.GroupEntry.this.restartServices();
/*      */       }
/*      */ 
/*      */       void dispose()
/*      */       {
/* 1336 */         this.shouldQuit = true;
/* 1337 */         if (this.canInterrupt)
/* 1338 */           interrupt();
/*      */       }
/*      */ 
/*      */       void noRestart()
/*      */       {
/* 1346 */         this.shouldRestart = false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LogGroupIncarnation extends Activation.LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = 4146872747377631897L;
/*      */     private ActivationGroupID id;
/*      */     private long inc;
/*      */ 
/*      */     LogGroupIncarnation(ActivationGroupID paramActivationGroupID, long paramLong)
/*      */     {
/* 1714 */       super();
/* 1715 */       this.id = paramActivationGroupID;
/* 1716 */       this.inc = paramLong;
/*      */     }
/*      */ 
/*      */     Object apply(Object paramObject) {
/*      */       try {
/* 1721 */         Activation.GroupEntry localGroupEntry = ((Activation)paramObject).getGroupEntry(this.id);
/* 1722 */         localGroupEntry.incarnation = this.inc;
/*      */       } catch (Exception localException) {
/* 1724 */         System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogGroupIncarnation" }));
/*      */ 
/* 1728 */         localException.printStackTrace();
/*      */       }
/* 1730 */       return paramObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class LogRecord
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 8395140512322687529L;
/*      */ 
/*      */     abstract Object apply(Object paramObject)
/*      */       throws Exception;
/*      */   }
/*      */ 
/*      */   private static class LogRegisterGroup extends Activation.LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = -1966827458515403625L;
/*      */     private ActivationGroupID id;
/*      */     private ActivationGroupDesc desc;
/*      */ 
/*      */     LogRegisterGroup(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc)
/*      */     {
/* 1605 */       super();
/* 1606 */       this.id = paramActivationGroupID;
/* 1607 */       this.desc = paramActivationGroupDesc;
/*      */     }
/*      */ 
/*      */     Object apply(Object paramObject)
/*      */     {
/*      */       Activation tmp19_16 = ((Activation)paramObject); tmp19_16.getClass(); ((Activation)paramObject).groupTable.put(this.id, new Activation.GroupEntry(tmp19_16, this.id, this.desc));
/*      */ 
/* 1615 */       return paramObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LogRegisterObject extends Activation.LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = -6280336276146085143L;
/*      */     private ActivationID id;
/*      */     private ActivationDesc desc;
/*      */ 
/*      */     LogRegisterObject(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
/*      */     {
/* 1549 */       super();
/* 1550 */       this.id = paramActivationID;
/* 1551 */       this.desc = paramActivationDesc;
/*      */     }
/*      */ 
/*      */     Object apply(Object paramObject) {
/*      */       try {
/* 1556 */         ((Activation)paramObject).getGroupEntry(this.desc.getGroupID()).registerObject(this.id, this.desc, false);
/*      */       }
/*      */       catch (Exception localException) {
/* 1559 */         System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogRegisterObject" }));
/*      */ 
/* 1563 */         localException.printStackTrace();
/*      */       }
/* 1565 */       return paramObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LogUnregisterGroup extends Activation.LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = -3356306586522147344L;
/*      */     private ActivationGroupID id;
/*      */ 
/*      */     LogUnregisterGroup(ActivationGroupID paramActivationGroupID)
/*      */     {
/* 1686 */       super();
/* 1687 */       this.id = paramActivationGroupID;
/*      */     }
/*      */ 
/*      */     Object apply(Object paramObject) {
/* 1691 */       Activation.GroupEntry localGroupEntry = (Activation.GroupEntry)((Activation)paramObject).groupTable.remove(this.id);
/*      */       try {
/* 1693 */         localGroupEntry.unregisterGroup(false);
/*      */       } catch (Exception localException) {
/* 1695 */         System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUnregisterGroup" }));
/*      */ 
/* 1699 */         localException.printStackTrace();
/*      */       }
/* 1701 */       return paramObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LogUnregisterObject extends Activation.LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = 6269824097396935501L;
/*      */     private ActivationID id;
/*      */ 
/*      */     LogUnregisterObject(ActivationID paramActivationID)
/*      */     {
/* 1577 */       super();
/* 1578 */       this.id = paramActivationID;
/*      */     }
/*      */ 
/*      */     Object apply(Object paramObject) {
/*      */       try {
/* 1583 */         ((Activation)paramObject).getGroupEntry(this.id).unregisterObject(this.id, false);
/*      */       }
/*      */       catch (Exception localException) {
/* 1586 */         System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUnregisterObject" }));
/*      */ 
/* 1590 */         localException.printStackTrace();
/*      */       }
/* 1592 */       return paramObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LogUpdateDesc extends Activation.LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = 545511539051179885L;
/*      */     private ActivationID id;
/*      */     private ActivationDesc desc;
/*      */ 
/*      */     LogUpdateDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc)
/*      */     {
/* 1629 */       super();
/* 1630 */       this.id = paramActivationID;
/* 1631 */       this.desc = paramActivationDesc;
/*      */     }
/*      */ 
/*      */     Object apply(Object paramObject) {
/*      */       try {
/* 1636 */         ((Activation)paramObject).getGroupEntry(this.id).setActivationDesc(this.id, this.desc, false);
/*      */       }
/*      */       catch (Exception localException) {
/* 1639 */         System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUpdateDesc" }));
/*      */ 
/* 1643 */         localException.printStackTrace();
/*      */       }
/* 1645 */       return paramObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LogUpdateGroupDesc extends Activation.LogRecord
/*      */   {
/*      */     private static final long serialVersionUID = -1271300989218424337L;
/*      */     private ActivationGroupID id;
/*      */     private ActivationGroupDesc desc;
/*      */ 
/*      */     LogUpdateGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc) {
/* 1658 */       super();
/* 1659 */       this.id = paramActivationGroupID;
/* 1660 */       this.desc = paramActivationGroupDesc;
/*      */     }
/*      */ 
/*      */     Object apply(Object paramObject) {
/*      */       try {
/* 1665 */         ((Activation)paramObject).getGroupEntry(this.id).setActivationGroupDesc(this.id, this.desc, false);
/*      */       }
/*      */       catch (Exception localException) {
/* 1668 */         System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUpdateGroupDesc" }));
/*      */ 
/* 1672 */         localException.printStackTrace();
/*      */       }
/* 1674 */       return paramObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ObjectEntry
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = -5500114225321357856L;
/*      */     ActivationDesc desc;
/* 1432 */     volatile transient MarshalledObject<? extends Remote> stub = null;
/* 1433 */     volatile transient boolean removed = false;
/*      */ 
/*      */     ObjectEntry(ActivationDesc paramActivationDesc) {
/* 1436 */       this.desc = paramActivationDesc;
/*      */     }
/*      */ 
/*      */     synchronized MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean, ActivationInstantiator paramActivationInstantiator)
/*      */       throws RemoteException, ActivationException
/*      */     {
/* 1445 */       MarshalledObject localMarshalledObject = this.stub;
/* 1446 */       if (this.removed)
/* 1447 */         throw new UnknownObjectException("object removed");
/* 1448 */       if ((!paramBoolean) && (localMarshalledObject != null)) {
/* 1449 */         return localMarshalledObject;
/*      */       }
/*      */ 
/* 1452 */       localMarshalledObject = paramActivationInstantiator.newInstance(paramActivationID, this.desc);
/* 1453 */       this.stub = localMarshalledObject;
/*      */ 
/* 1458 */       return localMarshalledObject;
/*      */     }
/*      */ 
/*      */     void reset() {
/* 1462 */       this.stub = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Shutdown extends Thread
/*      */   {
/*      */     Shutdown()
/*      */     {
/*  644 */       super();
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       try
/*      */       {
/*  652 */         Activation.unexport(Activation.this.activator);
/*  653 */         Activation.unexport(Activation.this.system);
/*      */ 
/*  656 */         for (Activation.GroupEntry localGroupEntry : Activation.this.groupTable.values()) {
/*  657 */           localGroupEntry.shutdown();
/*      */         }
/*      */ 
/*  660 */         Runtime.getRuntime().removeShutdownHook(Activation.this.shutdownHook);
/*      */ 
/*  665 */         Activation.unexport(Activation.this.monitor);
/*      */         try
/*      */         {
/*  676 */           synchronized (Activation.this.log) {
/*  677 */             Activation.this.log.close();
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*  689 */         System.err.println(Activation.getTextResource("rmid.daemon.shutdown"));
/*  690 */         System.exit(0);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ShutdownHook extends Thread
/*      */   {
/*      */     ShutdownHook() {
/*  698 */       super();
/*      */     }
/*      */ 
/*      */     public void run() {
/*  702 */       synchronized (Activation.this) {
/*  703 */         Activation.this.shuttingDown = true;
/*      */       }
/*      */ 
/*  707 */       for (??? = Activation.this.groupTable.values().iterator(); ((Iterator)???).hasNext(); ) { Activation.GroupEntry localGroupEntry = (Activation.GroupEntry)((Iterator)???).next();
/*  708 */         localGroupEntry.shutdownFast();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SystemRegistryImpl extends RegistryImpl
/*      */   {
/*  301 */     private static final String NAME = ActivationSystem.class.getName();
/*      */     private final ActivationSystem systemStub;
/*      */ 
/*      */     SystemRegistryImpl(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory, ActivationSystem paramActivationSystem)
/*      */       throws RemoteException
/*      */     {
/*  310 */       super(paramRMIClientSocketFactory, paramRMIServerSocketFactory);
/*  311 */       this.systemStub = paramActivationSystem;
/*      */     }
/*      */ 
/*      */     public Remote lookup(String paramString)
/*      */       throws RemoteException, NotBoundException
/*      */     {
/*  323 */       if (paramString.equals(NAME)) {
/*  324 */         return this.systemStub;
/*      */       }
/*  326 */       return super.lookup(paramString);
/*      */     }
/*      */ 
/*      */     public String[] list() throws RemoteException
/*      */     {
/*  331 */       String[] arrayOfString1 = super.list();
/*  332 */       int i = arrayOfString1.length;
/*  333 */       String[] arrayOfString2 = new String[i + 1];
/*  334 */       if (i > 0) {
/*  335 */         System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, i);
/*      */       }
/*  337 */       arrayOfString2[i] = NAME;
/*  338 */       return arrayOfString2;
/*      */     }
/*      */ 
/*      */     public void bind(String paramString, Remote paramRemote)
/*      */       throws RemoteException, AlreadyBoundException, AccessException
/*      */     {
/*  344 */       if (paramString.equals(NAME)) {
/*  345 */         throw new AccessException("binding ActivationSystem is disallowed");
/*      */       }
/*      */ 
/*  348 */       super.bind(paramString, paramRemote);
/*      */     }
/*      */ 
/*      */     public void unbind(String paramString)
/*      */       throws RemoteException, NotBoundException, AccessException
/*      */     {
/*  355 */       if (paramString.equals(NAME)) {
/*  356 */         throw new AccessException("unbinding ActivationSystem is disallowed");
/*      */       }
/*      */ 
/*  359 */       super.unbind(paramString);
/*      */     }
/*      */ 
/*      */     public void rebind(String paramString, Remote paramRemote)
/*      */       throws RemoteException, AccessException
/*      */     {
/*  367 */       if (paramString.equals(NAME)) {
/*  368 */         throw new AccessException("binding ActivationSystem is disallowed");
/*      */       }
/*      */ 
/*  371 */       super.rebind(paramString, paramRemote);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.server.Activation
 * JD-Core Version:    0.6.2
 */