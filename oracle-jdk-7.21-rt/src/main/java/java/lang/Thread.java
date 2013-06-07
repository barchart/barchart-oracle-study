/*      */ package java.lang;
/*      */ 
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import sun.misc.VM;
/*      */ import sun.nio.ch.Interruptible;
/*      */ import sun.security.util.SecurityConstants;
/*      */ 
/*      */ public class Thread
/*      */   implements Runnable
/*      */ {
/*      */   private char[] name;
/*      */   private int priority;
/*      */   private Thread threadQ;
/*      */   private long eetop;
/*      */   private boolean single_step;
/*  155 */   private boolean daemon = false;
/*      */ 
/*  158 */   private boolean stillborn = false;
/*      */   private Runnable target;
/*      */   private ThreadGroup group;
/*      */   private ClassLoader contextClassLoader;
/*      */   private AccessControlContext inheritedAccessControlContext;
/*      */   private static int threadInitNumber;
/*  180 */   ThreadLocal.ThreadLocalMap threadLocals = null;
/*      */ 
/*  186 */   ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
/*      */   private long stackSize;
/*      */   private long nativeParkEventPointer;
/*      */   private long tid;
/*      */   private static long threadSeqNumber;
/*  212 */   private volatile int threadStatus = 0;
/*      */   volatile Object parkBlocker;
/*      */   private volatile Interruptible blocker;
/*  232 */   private final Object blockerLock = new Object();
/*      */   public static final int MIN_PRIORITY = 1;
/*      */   public static final int NORM_PRIORITY = 5;
/*      */   public static final int MAX_PRIORITY = 10;
/* 1505 */   private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
/*      */ 
/* 1631 */   private static final RuntimePermission SUBCLASS_IMPLEMENTATION_PERMISSION = new RuntimePermission("enableContextClassLoaderOverride");
/*      */   private volatile UncaughtExceptionHandler uncaughtExceptionHandler;
/*      */   private static volatile UncaughtExceptionHandler defaultUncaughtExceptionHandler;
/*      */ 
/*      */   private static native void registerNatives();
/*      */ 
/*      */   private static synchronized int nextThreadNum()
/*      */   {
/*  175 */     return threadInitNumber++;
/*      */   }
/*      */ 
/*      */   private static synchronized long nextThreadID()
/*      */   {
/*  216 */     return ++threadSeqNumber;
/*      */   }
/*      */ 
/*      */   void blockedOn(Interruptible paramInterruptible)
/*      */   {
/*  237 */     synchronized (this.blockerLock) {
/*  238 */       this.blocker = paramInterruptible;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static native Thread currentThread();
/*      */ 
/*      */   public static native void yield();
/*      */ 
/*      */   public static native void sleep(long paramLong)
/*      */     throws InterruptedException;
/*      */ 
/*      */   public static void sleep(long paramLong, int paramInt)
/*      */     throws InterruptedException
/*      */   {
/*  325 */     if (paramLong < 0L) {
/*  326 */       throw new IllegalArgumentException("timeout value is negative");
/*      */     }
/*      */ 
/*  329 */     if ((paramInt < 0) || (paramInt > 999999)) {
/*  330 */       throw new IllegalArgumentException("nanosecond timeout value out of range");
/*      */     }
/*      */ 
/*  334 */     if ((paramInt >= 500000) || ((paramInt != 0) && (paramLong == 0L))) {
/*  335 */       paramLong += 1L;
/*      */     }
/*      */ 
/*  338 */     sleep(paramLong);
/*      */   }
/*      */ 
/*      */   private void init(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, long paramLong)
/*      */   {
/*  352 */     if (paramString == null) {
/*  353 */       throw new NullPointerException("name cannot be null");
/*      */     }
/*      */ 
/*  356 */     Thread localThread = currentThread();
/*  357 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  358 */     if (paramThreadGroup == null)
/*      */     {
/*  363 */       if (localSecurityManager != null) {
/*  364 */         paramThreadGroup = localSecurityManager.getThreadGroup();
/*      */       }
/*      */ 
/*  369 */       if (paramThreadGroup == null) {
/*  370 */         paramThreadGroup = localThread.getThreadGroup();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  376 */     paramThreadGroup.checkAccess();
/*      */ 
/*  381 */     if ((localSecurityManager != null) && 
/*  382 */       (isCCLOverridden(getClass()))) {
/*  383 */       localSecurityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
/*      */     }
/*      */ 
/*  387 */     paramThreadGroup.addUnstarted();
/*      */ 
/*  389 */     this.group = paramThreadGroup;
/*  390 */     this.daemon = localThread.isDaemon();
/*  391 */     this.priority = localThread.getPriority();
/*  392 */     this.name = paramString.toCharArray();
/*  393 */     if ((localSecurityManager == null) || (isCCLOverridden(localThread.getClass())))
/*  394 */       this.contextClassLoader = localThread.getContextClassLoader();
/*      */     else
/*  396 */       this.contextClassLoader = localThread.contextClassLoader;
/*  397 */     this.inheritedAccessControlContext = AccessController.getContext();
/*  398 */     this.target = paramRunnable;
/*  399 */     setPriority(this.priority);
/*  400 */     if (localThread.inheritableThreadLocals != null) {
/*  401 */       this.inheritableThreadLocals = ThreadLocal.createInheritedMap(localThread.inheritableThreadLocals);
/*      */     }
/*      */ 
/*  404 */     this.stackSize = paramLong;
/*      */ 
/*  407 */     this.tid = nextThreadID();
/*      */   }
/*      */ 
/*      */   protected Object clone()
/*      */     throws CloneNotSupportedException
/*      */   {
/*  419 */     throw new CloneNotSupportedException();
/*      */   }
/*      */ 
/*      */   public Thread()
/*      */   {
/*  430 */     init(null, null, "Thread-" + nextThreadNum(), 0L);
/*      */   }
/*      */ 
/*      */   public Thread(Runnable paramRunnable)
/*      */   {
/*  446 */     init(null, paramRunnable, "Thread-" + nextThreadNum(), 0L);
/*      */   }
/*      */ 
/*      */   public Thread(ThreadGroup paramThreadGroup, Runnable paramRunnable)
/*      */   {
/*  473 */     init(paramThreadGroup, paramRunnable, "Thread-" + nextThreadNum(), 0L);
/*      */   }
/*      */ 
/*      */   public Thread(String paramString)
/*      */   {
/*  485 */     init(null, null, paramString, 0L);
/*      */   }
/*      */ 
/*      */   public Thread(ThreadGroup paramThreadGroup, String paramString)
/*      */   {
/*  509 */     init(paramThreadGroup, null, paramString, 0L);
/*      */   }
/*      */ 
/*      */   public Thread(Runnable paramRunnable, String paramString)
/*      */   {
/*  525 */     init(null, paramRunnable, paramString, 0L);
/*      */   }
/*      */ 
/*      */   public Thread(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString)
/*      */   {
/*  573 */     init(paramThreadGroup, paramRunnable, paramString, 0L);
/*      */   }
/*      */ 
/*      */   public Thread(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, long paramLong)
/*      */   {
/*  652 */     init(paramThreadGroup, paramRunnable, paramString, paramLong);
/*      */   }
/*      */ 
/*      */   public synchronized void start()
/*      */   {
/*  681 */     if (this.threadStatus != 0) {
/*  682 */       throw new IllegalThreadStateException();
/*      */     }
/*      */ 
/*  687 */     this.group.add(this);
/*      */ 
/*  689 */     int i = 0;
/*      */     try {
/*  691 */       start0();
/*  692 */       i = 1;
/*      */     } finally {
/*      */       try {
/*  695 */         if (i == 0)
/*  696 */           this.group.threadStartFailed(this);
/*      */       }
/*      */       catch (Throwable localThrowable2)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private native void start0();
/*      */ 
/*      */   public void run()
/*      */   {
/*  721 */     if (this.target != null)
/*  722 */       this.target.run();
/*      */   }
/*      */ 
/*      */   private void exit()
/*      */   {
/*  731 */     if (this.group != null) {
/*  732 */       this.group.threadTerminated(this);
/*  733 */       this.group = null;
/*      */     }
/*      */ 
/*  736 */     this.target = null;
/*      */ 
/*  738 */     this.threadLocals = null;
/*  739 */     this.inheritableThreadLocals = null;
/*  740 */     this.inheritedAccessControlContext = null;
/*  741 */     this.blocker = null;
/*  742 */     this.uncaughtExceptionHandler = null;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public final void stop()
/*      */   {
/*  813 */     stop(new ThreadDeath());
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public final synchronized void stop(Throwable paramThrowable)
/*      */   {
/*  867 */     if (paramThrowable == null) {
/*  868 */       throw new NullPointerException();
/*      */     }
/*  870 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  871 */     if (localSecurityManager != null) {
/*  872 */       checkAccess();
/*  873 */       if ((this != currentThread()) || (!(paramThrowable instanceof ThreadDeath)))
/*      */       {
/*  875 */         localSecurityManager.checkPermission(SecurityConstants.STOP_THREAD_PERMISSION);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  880 */     if (this.threadStatus != 0) {
/*  881 */       resume();
/*      */     }
/*      */ 
/*  885 */     stop0(paramThrowable);
/*      */   }
/*      */ 
/*      */   public void interrupt()
/*      */   {
/*  928 */     if (this != currentThread()) {
/*  929 */       checkAccess();
/*      */     }
/*  931 */     synchronized (this.blockerLock) {
/*  932 */       Interruptible localInterruptible = this.blocker;
/*  933 */       if (localInterruptible != null) {
/*  934 */         interrupt0();
/*  935 */         localInterruptible.interrupt(this);
/*  936 */         return;
/*      */       }
/*      */     }
/*  939 */     interrupt0();
/*      */   }
/*      */ 
/*      */   public static boolean interrupted()
/*      */   {
/*  960 */     return currentThread().isInterrupted(true);
/*      */   }
/*      */ 
/*      */   public boolean isInterrupted()
/*      */   {
/*  977 */     return isInterrupted(false);
/*      */   }
/*      */ 
/*      */   private native boolean isInterrupted(boolean paramBoolean);
/*      */ 
/*      */   @Deprecated
/*      */   public void destroy()
/*      */   {
/* 1006 */     throw new NoSuchMethodError();
/*      */   }
/*      */ 
/*      */   public final native boolean isAlive();
/*      */ 
/*      */   @Deprecated
/*      */   public final void suspend()
/*      */   {
/* 1044 */     checkAccess();
/* 1045 */     suspend0();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public final void resume()
/*      */   {
/* 1070 */     checkAccess();
/* 1071 */     resume0();
/*      */   }
/*      */ 
/*      */   public final void setPriority(int paramInt)
/*      */   {
/* 1100 */     checkAccess();
/* 1101 */     if ((paramInt > 10) || (paramInt < 1))
/* 1102 */       throw new IllegalArgumentException();
/*      */     ThreadGroup localThreadGroup;
/* 1104 */     if ((localThreadGroup = getThreadGroup()) != null) {
/* 1105 */       if (paramInt > localThreadGroup.getMaxPriority()) {
/* 1106 */         paramInt = localThreadGroup.getMaxPriority();
/*      */       }
/* 1108 */       setPriority0(this.priority = paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   public final int getPriority()
/*      */   {
/* 1119 */     return this.priority;
/*      */   }
/*      */ 
/*      */   public final void setName(String paramString)
/*      */   {
/* 1137 */     checkAccess();
/* 1138 */     this.name = paramString.toCharArray();
/*      */   }
/*      */ 
/*      */   public final String getName()
/*      */   {
/* 1148 */     return String.valueOf(this.name);
/*      */   }
/*      */ 
/*      */   public final ThreadGroup getThreadGroup()
/*      */   {
/* 1159 */     return this.group;
/*      */   }
/*      */ 
/*      */   public static int activeCount()
/*      */   {
/* 1179 */     return currentThread().getThreadGroup().activeCount();
/*      */   }
/*      */ 
/*      */   public static int enumerate(Thread[] paramArrayOfThread)
/*      */   {
/* 1209 */     return currentThread().getThreadGroup().enumerate(paramArrayOfThread);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public native int countStackFrames();
/*      */ 
/*      */   public final synchronized void join(long paramLong)
/*      */     throws InterruptedException
/*      */   {
/* 1249 */     long l1 = System.currentTimeMillis();
/* 1250 */     long l2 = 0L;
/*      */ 
/* 1252 */     if (paramLong < 0L) {
/* 1253 */       throw new IllegalArgumentException("timeout value is negative");
/*      */     }
/*      */ 
/* 1256 */     if (paramLong == 0L) {
/* 1257 */       while (isAlive()) {
/* 1258 */         wait(0L);
/*      */       }
/*      */     }
/* 1261 */     while (isAlive()) {
/* 1262 */       long l3 = paramLong - l2;
/* 1263 */       if (l3 <= 0L) {
/*      */         break;
/*      */       }
/* 1266 */       wait(l3);
/* 1267 */       l2 = System.currentTimeMillis() - l1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public final synchronized void join(long paramLong, int paramInt)
/*      */     throws InterruptedException
/*      */   {
/* 1300 */     if (paramLong < 0L) {
/* 1301 */       throw new IllegalArgumentException("timeout value is negative");
/*      */     }
/*      */ 
/* 1304 */     if ((paramInt < 0) || (paramInt > 999999)) {
/* 1305 */       throw new IllegalArgumentException("nanosecond timeout value out of range");
/*      */     }
/*      */ 
/* 1309 */     if ((paramInt >= 500000) || ((paramInt != 0) && (paramLong == 0L))) {
/* 1310 */       paramLong += 1L;
/*      */     }
/*      */ 
/* 1313 */     join(paramLong);
/*      */   }
/*      */ 
/*      */   public final void join()
/*      */     throws InterruptedException
/*      */   {
/* 1332 */     join(0L);
/*      */   }
/*      */ 
/*      */   public static void dumpStack()
/*      */   {
/* 1342 */     new Exception("Stack trace").printStackTrace();
/*      */   }
/*      */ 
/*      */   public final void setDaemon(boolean paramBoolean)
/*      */   {
/* 1363 */     checkAccess();
/* 1364 */     if (isAlive()) {
/* 1365 */       throw new IllegalThreadStateException();
/*      */     }
/* 1367 */     this.daemon = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final boolean isDaemon()
/*      */   {
/* 1378 */     return this.daemon;
/*      */   }
/*      */ 
/*      */   public final void checkAccess()
/*      */   {
/* 1394 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1395 */     if (localSecurityManager != null)
/* 1396 */       localSecurityManager.checkAccess(this);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1407 */     ThreadGroup localThreadGroup = getThreadGroup();
/* 1408 */     if (localThreadGroup != null) {
/* 1409 */       return "Thread[" + getName() + "," + getPriority() + "," + localThreadGroup.getName() + "]";
/*      */     }
/*      */ 
/* 1412 */     return "Thread[" + getName() + "," + getPriority() + "," + "" + "]";
/*      */   }
/*      */ 
/*      */   public ClassLoader getContextClassLoader()
/*      */   {
/* 1444 */     if (this.contextClassLoader == null)
/* 1445 */       return null;
/* 1446 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1447 */     if (localSecurityManager != null) {
/* 1448 */       ClassLoader localClassLoader = ClassLoader.getCallerClassLoader();
/* 1449 */       if ((localClassLoader != null) && (localClassLoader != this.contextClassLoader) && (!this.contextClassLoader.isAncestor(localClassLoader)))
/*      */       {
/* 1451 */         localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
/*      */       }
/*      */     }
/* 1454 */     return this.contextClassLoader;
/*      */   }
/*      */ 
/*      */   public void setContextClassLoader(ClassLoader paramClassLoader)
/*      */   {
/* 1480 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1481 */     if (localSecurityManager != null) {
/* 1482 */       localSecurityManager.checkPermission(new RuntimePermission("setContextClassLoader"));
/*      */     }
/* 1484 */     this.contextClassLoader = paramClassLoader;
/*      */   }
/*      */ 
/*      */   public static native boolean holdsLock(Object paramObject);
/*      */ 
/*      */   public StackTraceElement[] getStackTrace()
/*      */   {
/* 1545 */     if (this != currentThread())
/*      */     {
/* 1547 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 1548 */       if (localSecurityManager != null) {
/* 1549 */         localSecurityManager.checkPermission(SecurityConstants.GET_STACK_TRACE_PERMISSION);
/*      */       }
/*      */ 
/* 1554 */       if (!isAlive()) {
/* 1555 */         return EMPTY_STACK_TRACE;
/*      */       }
/* 1557 */       StackTraceElement[][] arrayOfStackTraceElement = dumpThreads(new Thread[] { this });
/* 1558 */       StackTraceElement[] arrayOfStackTraceElement1 = arrayOfStackTraceElement[0];
/*      */ 
/* 1561 */       if (arrayOfStackTraceElement1 == null) {
/* 1562 */         arrayOfStackTraceElement1 = EMPTY_STACK_TRACE;
/*      */       }
/* 1564 */       return arrayOfStackTraceElement1;
/*      */     }
/*      */ 
/* 1567 */     return new Exception().getStackTrace();
/*      */   }
/*      */ 
/*      */   public static Map<Thread, StackTraceElement[]> getAllStackTraces()
/*      */   {
/* 1608 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1609 */     if (localSecurityManager != null) {
/* 1610 */       localSecurityManager.checkPermission(SecurityConstants.GET_STACK_TRACE_PERMISSION);
/*      */ 
/* 1612 */       localSecurityManager.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
/*      */     }
/*      */ 
/* 1617 */     Thread[] arrayOfThread = getThreads();
/* 1618 */     StackTraceElement[][] arrayOfStackTraceElement = dumpThreads(arrayOfThread);
/* 1619 */     HashMap localHashMap = new HashMap(arrayOfThread.length);
/* 1620 */     for (int i = 0; i < arrayOfThread.length; i++) {
/* 1621 */       StackTraceElement[] arrayOfStackTraceElement1 = arrayOfStackTraceElement[i];
/* 1622 */       if (arrayOfStackTraceElement1 != null) {
/* 1623 */         localHashMap.put(arrayOfThread[i], arrayOfStackTraceElement1);
/*      */       }
/*      */     }
/*      */ 
/* 1627 */     return localHashMap;
/*      */   }
/*      */ 
/*      */   private static boolean isCCLOverridden(Class paramClass)
/*      */   {
/* 1654 */     if (paramClass == Thread.class) {
/* 1655 */       return false;
/*      */     }
/* 1657 */     processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
/* 1658 */     WeakClassKey localWeakClassKey = new WeakClassKey(paramClass, Caches.subclassAuditsQueue);
/* 1659 */     Boolean localBoolean = (Boolean)Caches.subclassAudits.get(localWeakClassKey);
/* 1660 */     if (localBoolean == null) {
/* 1661 */       localBoolean = Boolean.valueOf(auditSubclass(paramClass));
/* 1662 */       Caches.subclassAudits.putIfAbsent(localWeakClassKey, localBoolean);
/*      */     }
/*      */ 
/* 1665 */     return localBoolean.booleanValue();
/*      */   }
/*      */ 
/*      */   private static boolean auditSubclass(Class paramClass)
/*      */   {
/* 1674 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Boolean run() {
/* 1677 */         for (Class localClass = this.val$subcl; 
/* 1678 */           localClass != Thread.class; 
/* 1679 */           localClass = localClass.getSuperclass())
/*      */           try
/*      */           {
/* 1682 */             localClass.getDeclaredMethod("getContextClassLoader", new Class[0]);
/* 1683 */             return Boolean.TRUE;
/*      */           }
/*      */           catch (NoSuchMethodException localNoSuchMethodException1) {
/*      */             try {
/* 1687 */               Class[] arrayOfClass = { ClassLoader.class };
/* 1688 */               localClass.getDeclaredMethod("setContextClassLoader", arrayOfClass);
/* 1689 */               return Boolean.TRUE;
/*      */             } catch (NoSuchMethodException localNoSuchMethodException2) {
/*      */             }
/*      */           }
/* 1693 */         return Boolean.FALSE;
/*      */       }
/*      */     });
/* 1697 */     return localBoolean.booleanValue();
/*      */   }
/*      */ 
/*      */   private static native StackTraceElement[][] dumpThreads(Thread[] paramArrayOfThread);
/*      */ 
/*      */   private static native Thread[] getThreads();
/*      */ 
/*      */   public long getId()
/*      */   {
/* 1713 */     return this.tid;
/*      */   }
/*      */ 
/*      */   public State getState()
/*      */   {
/* 1825 */     return VM.toThreadState(this.threadStatus);
/*      */   }
/*      */ 
/*      */   public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler paramUncaughtExceptionHandler)
/*      */   {
/* 1905 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1906 */     if (localSecurityManager != null) {
/* 1907 */       localSecurityManager.checkPermission(new RuntimePermission("setDefaultUncaughtExceptionHandler"));
/*      */     }
/*      */ 
/* 1912 */     defaultUncaughtExceptionHandler = paramUncaughtExceptionHandler;
/*      */   }
/*      */ 
/*      */   public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler()
/*      */   {
/* 1923 */     return defaultUncaughtExceptionHandler;
/*      */   }
/*      */ 
/*      */   public UncaughtExceptionHandler getUncaughtExceptionHandler()
/*      */   {
/* 1935 */     return this.uncaughtExceptionHandler != null ? this.uncaughtExceptionHandler : this.group;
/*      */   }
/*      */ 
/*      */   public void setUncaughtExceptionHandler(UncaughtExceptionHandler paramUncaughtExceptionHandler)
/*      */   {
/* 1955 */     checkAccess();
/* 1956 */     this.uncaughtExceptionHandler = paramUncaughtExceptionHandler;
/*      */   }
/*      */ 
/*      */   private void dispatchUncaughtException(Throwable paramThrowable)
/*      */   {
/* 1964 */     getUncaughtExceptionHandler().uncaughtException(this, paramThrowable);
/*      */   }
/*      */ 
/*      */   static void processQueue(ReferenceQueue<Class<?>> paramReferenceQueue, ConcurrentMap<? extends WeakReference<Class<?>>, ?> paramConcurrentMap)
/*      */   {
/*      */     Reference localReference;
/* 1976 */     while ((localReference = paramReferenceQueue.poll()) != null)
/* 1977 */       paramConcurrentMap.remove(localReference);
/*      */   }
/*      */ 
/*      */   private native void setPriority0(int paramInt);
/*      */ 
/*      */   private native void stop0(Object paramObject);
/*      */ 
/*      */   private native void suspend0();
/*      */ 
/*      */   private native void resume0();
/*      */ 
/*      */   private native void interrupt0();
/*      */ 
/*      */   private native void setNativeName(String paramString);
/*      */ 
/*      */   static
/*      */   {
/*  143 */     registerNatives();
/*      */   }
/*      */ 
/*      */   private static class Caches
/*      */   {
/* 1639 */     static final ConcurrentMap<Thread.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
/*      */ 
/* 1643 */     static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
/*      */   }
/*      */ 
/*      */   public static enum State
/*      */   {
/* 1754 */     NEW, 
/*      */ 
/* 1762 */     RUNNABLE, 
/*      */ 
/* 1771 */     BLOCKED, 
/*      */ 
/* 1792 */     WAITING, 
/*      */ 
/* 1806 */     TIMED_WAITING, 
/*      */ 
/* 1812 */     TERMINATED;
/*      */   }
/*      */ 
/*      */   public static abstract interface UncaughtExceptionHandler
/*      */   {
/*      */     public abstract void uncaughtException(Thread paramThread, Throwable paramThrowable);
/*      */   }
/*      */ 
/*      */   static class WeakClassKey extends WeakReference<Class<?>>
/*      */   {
/*      */     private final int hash;
/*      */ 
/*      */     WeakClassKey(Class<?> paramClass, ReferenceQueue<Class<?>> paramReferenceQueue)
/*      */     {
/* 1996 */       super(paramReferenceQueue);
/* 1997 */       this.hash = System.identityHashCode(paramClass);
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 2005 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject)
/*      */     {
/* 2016 */       if (paramObject == this) {
/* 2017 */         return true;
/*      */       }
/* 2019 */       if ((paramObject instanceof WeakClassKey)) {
/* 2020 */         Object localObject = get();
/* 2021 */         return (localObject != null) && (localObject == ((WeakClassKey)paramObject).get());
/*      */       }
/*      */ 
/* 2024 */       return false;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.Thread
 * JD-Core Version:    0.6.2
 */