/*      */ package java.lang;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.Console;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.channels.spi.SelectorProvider;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.PropertyPermission;
/*      */ import sun.misc.JavaIOAccess;
/*      */ import sun.misc.JavaLangAccess;
/*      */ import sun.misc.SharedSecrets;
/*      */ import sun.misc.VM;
/*      */ import sun.misc.Version;
/*      */ import sun.nio.ch.Interruptible;
/*      */ import sun.reflect.ConstantPool;
/*      */ import sun.reflect.Reflection;
/*      */ import sun.reflect.annotation.AnnotationType;
/*      */ import sun.security.util.SecurityConstants;
/*      */ 
/*      */ public final class System
/*      */ {
/*   78 */   public static final InputStream in = null;
/*      */ 
/*  105 */   public static final PrintStream out = null;
/*      */ 
/*  119 */   public static final PrintStream err = null;
/*      */ 
/*  123 */   private static volatile SecurityManager security = null;
/*      */ 
/*  198 */   private static volatile Console cons = null;
/*      */   private static Properties props;
/*      */   private static String lineSeparator;
/*      */ 
/*      */   private static native void registerNatives();
/*      */ 
/*      */   public static void setIn(InputStream paramInputStream)
/*      */   {
/*  146 */     checkIO();
/*  147 */     setIn0(paramInputStream);
/*      */   }
/*      */ 
/*      */   public static void setOut(PrintStream paramPrintStream)
/*      */   {
/*  170 */     checkIO();
/*  171 */     setOut0(paramPrintStream);
/*      */   }
/*      */ 
/*      */   public static void setErr(PrintStream paramPrintStream)
/*      */   {
/*  194 */     checkIO();
/*  195 */     setErr0(paramPrintStream);
/*      */   }
/*      */ 
/*      */   public static Console console()
/*      */   {
/*  208 */     if (cons == null) {
/*  209 */       synchronized (System.class) {
/*  210 */         cons = SharedSecrets.getJavaIOAccess().console();
/*      */       }
/*      */     }
/*  213 */     return cons;
/*      */   }
/*      */ 
/*      */   public static Channel inheritedChannel()
/*      */     throws IOException
/*      */   {
/*  242 */     return SelectorProvider.provider().inheritedChannel();
/*      */   }
/*      */ 
/*      */   private static void checkIO() {
/*  246 */     SecurityManager localSecurityManager = getSecurityManager();
/*  247 */     if (localSecurityManager != null)
/*  248 */       localSecurityManager.checkPermission(new RuntimePermission("setIO"));
/*      */   }
/*      */ 
/*      */   private static native void setIn0(InputStream paramInputStream);
/*      */ 
/*      */   private static native void setOut0(PrintStream paramPrintStream);
/*      */ 
/*      */   private static native void setErr0(PrintStream paramPrintStream);
/*      */ 
/*      */   public static void setSecurityManager(SecurityManager paramSecurityManager)
/*      */   {
/*      */     try
/*      */     {
/*  282 */       paramSecurityManager.checkPackageAccess("java.lang");
/*      */     }
/*      */     catch (Exception localException) {
/*      */     }
/*  286 */     setSecurityManager0(paramSecurityManager);
/*      */   }
/*      */ 
/*      */   private static synchronized void setSecurityManager0(SecurityManager paramSecurityManager)
/*      */   {
/*  291 */     SecurityManager localSecurityManager = getSecurityManager();
/*  292 */     if (localSecurityManager != null)
/*      */     {
/*  295 */       localSecurityManager.checkPermission(new RuntimePermission("setSecurityManager"));
/*      */     }
/*      */ 
/*  299 */     if ((paramSecurityManager != null) && (paramSecurityManager.getClass().getClassLoader() != null))
/*      */     {
/*  308 */       AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Object run() {
/*  310 */           this.val$s.getClass().getProtectionDomain().implies(SecurityConstants.ALL_PERMISSION);
/*      */ 
/*  312 */           return null;
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*  317 */     security = paramSecurityManager;
/*      */   }
/*      */ 
/*      */   public static SecurityManager getSecurityManager()
/*      */   {
/*  329 */     return security;
/*      */   }
/*      */ 
/*      */   public static native long currentTimeMillis();
/*      */ 
/*      */   public static native long nanoTime();
/*      */ 
/*      */   public static native void arraycopy(Object paramObject1, int paramInt1, Object paramObject2, int paramInt2, int paramInt3);
/*      */ 
/*      */   public static native int identityHashCode(Object paramObject);
/*      */ 
/*      */   private static native Properties initProperties(Properties paramProperties);
/*      */ 
/*      */   public static Properties getProperties()
/*      */   {
/*  620 */     SecurityManager localSecurityManager = getSecurityManager();
/*  621 */     if (localSecurityManager != null) {
/*  622 */       localSecurityManager.checkPropertiesAccess();
/*      */     }
/*      */ 
/*  625 */     return props;
/*      */   }
/*      */ 
/*      */   public static String lineSeparator()
/*      */   {
/*  637 */     return lineSeparator;
/*      */   }
/*      */ 
/*      */   public static void setProperties(Properties paramProperties)
/*      */   {
/*  665 */     SecurityManager localSecurityManager = getSecurityManager();
/*  666 */     if (localSecurityManager != null) {
/*  667 */       localSecurityManager.checkPropertiesAccess();
/*      */     }
/*  669 */     if (paramProperties == null) {
/*  670 */       paramProperties = new Properties();
/*  671 */       initProperties(paramProperties);
/*      */     }
/*  673 */     props = paramProperties;
/*      */   }
/*      */ 
/*      */   public static String getProperty(String paramString)
/*      */   {
/*  703 */     checkKey(paramString);
/*  704 */     SecurityManager localSecurityManager = getSecurityManager();
/*  705 */     if (localSecurityManager != null) {
/*  706 */       localSecurityManager.checkPropertyAccess(paramString);
/*      */     }
/*      */ 
/*  709 */     return props.getProperty(paramString);
/*      */   }
/*      */ 
/*      */   public static String getProperty(String paramString1, String paramString2)
/*      */   {
/*  739 */     checkKey(paramString1);
/*  740 */     SecurityManager localSecurityManager = getSecurityManager();
/*  741 */     if (localSecurityManager != null) {
/*  742 */       localSecurityManager.checkPropertyAccess(paramString1);
/*      */     }
/*      */ 
/*  745 */     return props.getProperty(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public static String setProperty(String paramString1, String paramString2)
/*      */   {
/*  778 */     checkKey(paramString1);
/*  779 */     SecurityManager localSecurityManager = getSecurityManager();
/*  780 */     if (localSecurityManager != null) {
/*  781 */       localSecurityManager.checkPermission(new PropertyPermission(paramString1, "write"));
/*      */     }
/*      */ 
/*  785 */     return (String)props.setProperty(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public static String clearProperty(String paramString)
/*      */   {
/*  816 */     checkKey(paramString);
/*  817 */     SecurityManager localSecurityManager = getSecurityManager();
/*  818 */     if (localSecurityManager != null) {
/*  819 */       localSecurityManager.checkPermission(new PropertyPermission(paramString, "write"));
/*      */     }
/*      */ 
/*  822 */     return (String)props.remove(paramString);
/*      */   }
/*      */ 
/*      */   private static void checkKey(String paramString) {
/*  826 */     if (paramString == null) {
/*  827 */       throw new NullPointerException("key can't be null");
/*      */     }
/*  829 */     if (paramString.equals(""))
/*  830 */       throw new IllegalArgumentException("key can't be empty");
/*      */   }
/*      */ 
/*      */   public static String getenv(String paramString)
/*      */   {
/*  881 */     SecurityManager localSecurityManager = getSecurityManager();
/*  882 */     if (localSecurityManager != null) {
/*  883 */       localSecurityManager.checkPermission(new RuntimePermission("getenv." + paramString));
/*      */     }
/*      */ 
/*  886 */     return ProcessEnvironment.getenv(paramString);
/*      */   }
/*      */ 
/*      */   public static Map<String, String> getenv()
/*      */   {
/*  931 */     SecurityManager localSecurityManager = getSecurityManager();
/*  932 */     if (localSecurityManager != null) {
/*  933 */       localSecurityManager.checkPermission(new RuntimePermission("getenv.*"));
/*      */     }
/*      */ 
/*  936 */     return ProcessEnvironment.getenv();
/*      */   }
/*      */ 
/*      */   public static void exit(int paramInt)
/*      */   {
/*  960 */     Runtime.getRuntime().exit(paramInt);
/*      */   }
/*      */ 
/*      */   public static void gc()
/*      */   {
/*  982 */     Runtime.getRuntime().gc();
/*      */   }
/*      */ 
/*      */   public static void runFinalization()
/*      */   {
/* 1004 */     Runtime.getRuntime().runFinalization();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static void runFinalizersOnExit(boolean paramBoolean)
/*      */   {
/* 1034 */     Runtime.getRuntime(); Runtime.runFinalizersOnExit(paramBoolean);
/*      */   }
/*      */ 
/*      */   public static void load(String paramString)
/*      */   {
/* 1059 */     Runtime.getRuntime().load0(getCallerClass(), paramString);
/*      */   }
/*      */ 
/*      */   public static void loadLibrary(String paramString)
/*      */   {
/* 1084 */     Runtime.getRuntime().loadLibrary0(getCallerClass(), paramString);
/*      */   }
/*      */ 
/*      */   public static native String mapLibraryName(String paramString);
/*      */ 
/*      */   private static void initializeSystemClass()
/*      */   {
/* 1114 */     props = new Properties();
/* 1115 */     initProperties(props);
/*      */ 
/* 1131 */     VM.saveAndRemoveProperties(props);
/*      */ 
/* 1134 */     lineSeparator = props.getProperty("line.separator");
/* 1135 */     Version.init();
/*      */ 
/* 1137 */     FileInputStream localFileInputStream = new FileInputStream(FileDescriptor.in);
/* 1138 */     FileOutputStream localFileOutputStream1 = new FileOutputStream(FileDescriptor.out);
/* 1139 */     FileOutputStream localFileOutputStream2 = new FileOutputStream(FileDescriptor.err);
/* 1140 */     setIn0(new BufferedInputStream(localFileInputStream));
/* 1141 */     setOut0(new PrintStream(new BufferedOutputStream(localFileOutputStream1, 128), true));
/* 1142 */     setErr0(new PrintStream(new BufferedOutputStream(localFileOutputStream2, 128), true));
/*      */ 
/* 1145 */     loadLibrary("zip");
/*      */ 
/* 1148 */     Terminator.setup();
/*      */ 
/* 1154 */     VM.initializeOSEnvironment();
/*      */ 
/* 1158 */     Thread localThread = Thread.currentThread();
/* 1159 */     localThread.getThreadGroup().add(localThread);
/*      */ 
/* 1162 */     setJavaLangAccess();
/*      */ 
/* 1168 */     VM.booted();
/*      */   }
/*      */ 
/*      */   private static void setJavaLangAccess()
/*      */   {
/* 1173 */     SharedSecrets.setJavaLangAccess(new JavaLangAccess() {
/*      */       public ConstantPool getConstantPool(Class paramAnonymousClass) {
/* 1175 */         return paramAnonymousClass.getConstantPool();
/*      */       }
/*      */       public void setAnnotationType(Class paramAnonymousClass, AnnotationType paramAnonymousAnnotationType) {
/* 1178 */         paramAnonymousClass.setAnnotationType(paramAnonymousAnnotationType);
/*      */       }
/*      */       public AnnotationType getAnnotationType(Class paramAnonymousClass) {
/* 1181 */         return paramAnonymousClass.getAnnotationType();
/*      */       }
/*      */ 
/*      */       public <E extends Enum<E>> E[] getEnumConstantsShared(Class<E> paramAnonymousClass) {
/* 1185 */         return (Enum[])paramAnonymousClass.getEnumConstantsShared();
/*      */       }
/*      */       public void blockedOn(Thread paramAnonymousThread, Interruptible paramAnonymousInterruptible) {
/* 1188 */         paramAnonymousThread.blockedOn(paramAnonymousInterruptible);
/*      */       }
/*      */       public void registerShutdownHook(int paramAnonymousInt, boolean paramAnonymousBoolean, Runnable paramAnonymousRunnable) {
/* 1191 */         Shutdown.add(paramAnonymousInt, paramAnonymousBoolean, paramAnonymousRunnable);
/*      */       }
/*      */       public int getStackTraceDepth(Throwable paramAnonymousThrowable) {
/* 1194 */         return paramAnonymousThrowable.getStackTraceDepth();
/*      */       }
/*      */       public StackTraceElement getStackTraceElement(Throwable paramAnonymousThrowable, int paramAnonymousInt) {
/* 1197 */         return paramAnonymousThrowable.getStackTraceElement(paramAnonymousInt);
/*      */       }
/*      */       public int getStringHash32(String paramAnonymousString) {
/* 1200 */         return paramAnonymousString.hash32();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   static Class<?> getCallerClass()
/*      */   {
/* 1208 */     return Reflection.getCallerClass(3);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   65 */     registerNatives();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.System
 * JD-Core Version:    0.6.2
 */