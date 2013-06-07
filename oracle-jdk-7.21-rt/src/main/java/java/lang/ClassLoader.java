/*      */ package java.lang;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.net.URL;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.CodeSource;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.security.cert.Certificate;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.Vector;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import sun.misc.ClassFileTransformer;
/*      */ import sun.misc.CompoundEnumeration;
/*      */ import sun.misc.Launcher;
/*      */ import sun.misc.PerfCounter;
/*      */ import sun.misc.Resource;
/*      */ import sun.misc.URLClassPath;
/*      */ import sun.misc.VM;
/*      */ import sun.reflect.Reflection;
/*      */ import sun.security.util.SecurityConstants;
/*      */ 
/*      */ public abstract class ClassLoader
/*      */ {
/*      */   private final ClassLoader parent;
/*      */   private final ConcurrentHashMap<String, Object> parallelLockMap;
/*      */   private final Map<String, Certificate[]> package2certs;
/*  245 */   private static final Certificate[] nocerts = new Certificate[0];
/*      */ 
/*  249 */   private final Vector<Class<?>> classes = new Vector();
/*      */ 
/*  253 */   private final ProtectionDomain defaultDomain = new ProtectionDomain(new CodeSource(null, (Certificate[])null), null, this, null);
/*      */   private final Set<ProtectionDomain> domains;
/*  268 */   private final HashMap<String, Package> packages = new HashMap();
/*      */   private static ClassLoader scl;
/*      */   private static boolean sclSet;
/* 1767 */   private static Vector<String> loadedLibraryNames = new Vector();
/*      */ 
/* 1770 */   private static Vector<NativeLibrary> systemNativeLibraries = new Vector();
/*      */ 
/* 1774 */   private Vector<NativeLibrary> nativeLibraries = new Vector();
/*      */ 
/* 1777 */   private static Stack<NativeLibrary> nativeLibraryContext = new Stack();
/*      */   private static String[] usr_paths;
/*      */   private static String[] sys_paths;
/*      */   final Object assertionLock;
/* 1976 */   private boolean defaultAssertionStatus = false;
/*      */ 
/* 1984 */   private Map<String, Boolean> packageAssertionStatus = null;
/*      */ 
/* 1991 */   Map<String, Boolean> classAssertionStatus = null;
/*      */ 
/*      */   private static native void registerNatives();
/*      */ 
/*      */   void addClass(Class paramClass)
/*      */   {
/*  262 */     this.classes.addElement(paramClass);
/*      */   }
/*      */ 
/*      */   private static Void checkCreateClassLoader()
/*      */   {
/*  271 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  272 */     if (localSecurityManager != null) {
/*  273 */       localSecurityManager.checkCreateClassLoader();
/*      */     }
/*  275 */     return null;
/*      */   }
/*      */ 
/*      */   private ClassLoader(Void paramVoid, ClassLoader paramClassLoader) {
/*  279 */     this.parent = paramClassLoader;
/*  280 */     if (ParallelLoaders.isRegistered(getClass())) {
/*  281 */       this.parallelLockMap = new ConcurrentHashMap();
/*  282 */       this.package2certs = new ConcurrentHashMap();
/*  283 */       this.domains = Collections.synchronizedSet(new HashSet());
/*      */ 
/*  285 */       this.assertionLock = new Object();
/*      */     }
/*      */     else {
/*  288 */       this.parallelLockMap = null;
/*  289 */       this.package2certs = new Hashtable();
/*  290 */       this.domains = new HashSet();
/*  291 */       this.assertionLock = this;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ClassLoader(ClassLoader paramClassLoader)
/*      */   {
/*  315 */     this(checkCreateClassLoader(), paramClassLoader);
/*      */   }
/*      */ 
/*      */   protected ClassLoader()
/*      */   {
/*  334 */     this(checkCreateClassLoader(), getSystemClassLoader());
/*      */   }
/*      */ 
/*      */   public Class<?> loadClass(String paramString)
/*      */     throws ClassNotFoundException
/*      */   {
/*  356 */     return loadClass(paramString, false);
/*      */   }
/*      */ 
/*      */   protected Class<?> loadClass(String paramString, boolean paramBoolean)
/*      */     throws ClassNotFoundException
/*      */   {
/*  403 */     synchronized (getClassLoadingLock(paramString))
/*      */     {
/*  405 */       Class localClass = findLoadedClass(paramString);
/*  406 */       if (localClass == null) {
/*  407 */         long l1 = System.nanoTime();
/*      */         try {
/*  409 */           if (this.parent != null)
/*  410 */             localClass = this.parent.loadClass(paramString, false);
/*      */           else {
/*  412 */             localClass = findBootstrapClassOrNull(paramString);
/*      */           }
/*      */         }
/*      */         catch (ClassNotFoundException localClassNotFoundException)
/*      */         {
/*      */         }
/*      */ 
/*  419 */         if (localClass == null)
/*      */         {
/*  422 */           long l2 = System.nanoTime();
/*  423 */           localClass = findClass(paramString);
/*      */ 
/*  426 */           PerfCounter.getParentDelegationTime().addTime(l2 - l1);
/*  427 */           PerfCounter.getFindClassTime().addElapsedTimeFrom(l2);
/*  428 */           PerfCounter.getFindClasses().increment();
/*      */         }
/*      */       }
/*  431 */       if (paramBoolean) {
/*  432 */         resolveClass(localClass);
/*      */       }
/*  434 */       return localClass;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Object getClassLoadingLock(String paramString)
/*      */   {
/*  459 */     Object localObject1 = this;
/*  460 */     if (this.parallelLockMap != null) {
/*  461 */       Object localObject2 = new Object();
/*  462 */       localObject1 = this.parallelLockMap.putIfAbsent(paramString, localObject2);
/*  463 */       if (localObject1 == null) {
/*  464 */         localObject1 = localObject2;
/*      */       }
/*      */     }
/*  467 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private Class loadClassInternal(String paramString)
/*      */     throws ClassNotFoundException
/*      */   {
/*  476 */     if (this.parallelLockMap == null) {
/*  477 */       synchronized (this) {
/*  478 */         return loadClass(paramString);
/*      */       }
/*      */     }
/*  481 */     return loadClass(paramString);
/*      */   }
/*      */ 
/*      */   private void checkPackageAccess(Class paramClass, ProtectionDomain paramProtectionDomain)
/*      */   {
/*  487 */     final SecurityManager localSecurityManager = System.getSecurityManager();
/*  488 */     if (localSecurityManager != null) {
/*  489 */       final String str = paramClass.getName();
/*  490 */       final int i = str.lastIndexOf('.');
/*  491 */       if (i != -1) {
/*  492 */         AccessController.doPrivileged(new PrivilegedAction() {
/*      */           public Void run() {
/*  494 */             localSecurityManager.checkPackageAccess(str.substring(0, i));
/*  495 */             return null;
/*      */           }
/*      */         }
/*      */         , new AccessControlContext(new ProtectionDomain[] { paramProtectionDomain }));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  500 */     this.domains.add(paramProtectionDomain);
/*      */   }
/*      */ 
/*      */   protected Class<?> findClass(String paramString)
/*      */     throws ClassNotFoundException
/*      */   {
/*  522 */     throw new ClassNotFoundException(paramString);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   protected final Class<?> defineClass(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws ClassFormatError
/*      */   {
/*  570 */     return defineClass(null, paramArrayOfByte, paramInt1, paramInt2, null);
/*      */   }
/*      */ 
/*      */   protected final Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws ClassFormatError
/*      */   {
/*  634 */     return defineClass(paramString, paramArrayOfByte, paramInt1, paramInt2, null);
/*      */   }
/*      */ 
/*      */   private ProtectionDomain preDefineClass(String paramString, ProtectionDomain paramProtectionDomain)
/*      */   {
/*  645 */     if (!checkName(paramString)) {
/*  646 */       throw new NoClassDefFoundError("IllegalName: " + paramString);
/*      */     }
/*  648 */     if ((paramString != null) && (paramString.startsWith("java."))) {
/*  649 */       throw new SecurityException("Prohibited package name: " + paramString.substring(0, paramString.lastIndexOf('.')));
/*      */     }
/*      */ 
/*  653 */     if (paramProtectionDomain == null) {
/*  654 */       paramProtectionDomain = this.defaultDomain;
/*      */     }
/*      */ 
/*  657 */     if (paramString != null) checkCerts(paramString, paramProtectionDomain.getCodeSource());
/*      */ 
/*  659 */     return paramProtectionDomain;
/*      */   }
/*      */ 
/*      */   private String defineClassSourceLocation(ProtectionDomain paramProtectionDomain)
/*      */   {
/*  664 */     CodeSource localCodeSource = paramProtectionDomain.getCodeSource();
/*  665 */     String str = null;
/*  666 */     if ((localCodeSource != null) && (localCodeSource.getLocation() != null)) {
/*  667 */       str = localCodeSource.getLocation().toString();
/*      */     }
/*  669 */     return str;
/*      */   }
/*      */ 
/*      */   private Class defineTransformedClass(String paramString1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain, ClassFormatError paramClassFormatError, String paramString2)
/*      */     throws ClassFormatError
/*      */   {
/*  680 */     ClassFileTransformer[] arrayOfClassFileTransformer1 = ClassFileTransformer.getTransformers();
/*      */ 
/*  682 */     Class localClass = null;
/*      */ 
/*  684 */     if (arrayOfClassFileTransformer1 != null) {
/*  685 */       for (ClassFileTransformer localClassFileTransformer : arrayOfClassFileTransformer1) {
/*      */         try
/*      */         {
/*  688 */           byte[] arrayOfByte = localClassFileTransformer.transform(paramArrayOfByte, paramInt1, paramInt2);
/*  689 */           localClass = defineClass1(paramString1, arrayOfByte, 0, arrayOfByte.length, paramProtectionDomain, paramString2);
/*      */         }
/*      */         catch (ClassFormatError localClassFormatError)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  701 */     if (localClass == null) {
/*  702 */       throw paramClassFormatError;
/*      */     }
/*  704 */     return localClass;
/*      */   }
/*      */ 
/*      */   private void postDefineClass(Class paramClass, ProtectionDomain paramProtectionDomain)
/*      */   {
/*  709 */     if (paramProtectionDomain.getCodeSource() != null) {
/*  710 */       Certificate[] arrayOfCertificate = paramProtectionDomain.getCodeSource().getCertificates();
/*  711 */       if (arrayOfCertificate != null)
/*  712 */         setSigners(paramClass, arrayOfCertificate);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain)
/*      */     throws ClassFormatError
/*      */   {
/*  785 */     paramProtectionDomain = preDefineClass(paramString, paramProtectionDomain);
/*      */ 
/*  787 */     Class localClass = null;
/*  788 */     String str = defineClassSourceLocation(paramProtectionDomain);
/*      */     try
/*      */     {
/*  791 */       localClass = defineClass1(paramString, paramArrayOfByte, paramInt1, paramInt2, paramProtectionDomain, str);
/*      */     } catch (ClassFormatError localClassFormatError) {
/*  793 */       localClass = defineTransformedClass(paramString, paramArrayOfByte, paramInt1, paramInt2, paramProtectionDomain, localClassFormatError, str);
/*      */     }
/*      */ 
/*  797 */     postDefineClass(localClass, paramProtectionDomain);
/*  798 */     return localClass;
/*      */   }
/*      */ 
/*      */   protected final Class<?> defineClass(String paramString, ByteBuffer paramByteBuffer, ProtectionDomain paramProtectionDomain)
/*      */     throws ClassFormatError
/*      */   {
/*  867 */     int i = paramByteBuffer.remaining();
/*      */ 
/*  870 */     if (!paramByteBuffer.isDirect()) {
/*  871 */       if (paramByteBuffer.hasArray()) {
/*  872 */         return defineClass(paramString, paramByteBuffer.array(), paramByteBuffer.position() + paramByteBuffer.arrayOffset(), i, paramProtectionDomain);
/*      */       }
/*      */ 
/*  877 */       localObject = new byte[i];
/*  878 */       paramByteBuffer.get((byte[])localObject);
/*  879 */       return defineClass(paramString, (byte[])localObject, 0, i, paramProtectionDomain);
/*      */     }
/*      */ 
/*  883 */     paramProtectionDomain = preDefineClass(paramString, paramProtectionDomain);
/*      */ 
/*  885 */     Object localObject = null;
/*  886 */     String str = defineClassSourceLocation(paramProtectionDomain);
/*      */     try
/*      */     {
/*  889 */       localObject = defineClass2(paramString, paramByteBuffer, paramByteBuffer.position(), i, paramProtectionDomain, str);
/*      */     }
/*      */     catch (ClassFormatError localClassFormatError) {
/*  892 */       byte[] arrayOfByte = new byte[i];
/*  893 */       paramByteBuffer.get(arrayOfByte);
/*  894 */       localObject = defineTransformedClass(paramString, arrayOfByte, 0, i, paramProtectionDomain, localClassFormatError, str);
/*      */     }
/*      */ 
/*  898 */     postDefineClass((Class)localObject, paramProtectionDomain);
/*  899 */     return localObject;
/*      */   }
/*      */ 
/*      */   private native Class defineClass0(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain);
/*      */ 
/*      */   private native Class defineClass1(String paramString1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain, String paramString2);
/*      */ 
/*      */   private native Class defineClass2(String paramString1, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, ProtectionDomain paramProtectionDomain, String paramString2);
/*      */ 
/*      */   private boolean checkName(String paramString)
/*      */   {
/*  914 */     if ((paramString == null) || (paramString.length() == 0))
/*  915 */       return true;
/*  916 */     if ((paramString.indexOf('/') != -1) || ((!VM.allowArraySyntax()) && (paramString.charAt(0) == '[')))
/*      */     {
/*  918 */       return false;
/*  919 */     }return true;
/*      */   }
/*      */ 
/*      */   private void checkCerts(String paramString, CodeSource paramCodeSource) {
/*  923 */     int i = paramString.lastIndexOf('.');
/*  924 */     String str = i == -1 ? "" : paramString.substring(0, i);
/*      */ 
/*  926 */     Certificate[] arrayOfCertificate1 = null;
/*  927 */     if (paramCodeSource != null) {
/*  928 */       arrayOfCertificate1 = paramCodeSource.getCertificates();
/*      */     }
/*  930 */     Certificate[] arrayOfCertificate2 = null;
/*  931 */     if (this.parallelLockMap == null) {
/*  932 */       synchronized (this) {
/*  933 */         arrayOfCertificate2 = (Certificate[])this.package2certs.get(str);
/*  934 */         if (arrayOfCertificate2 == null)
/*  935 */           this.package2certs.put(str, arrayOfCertificate1 == null ? nocerts : arrayOfCertificate1);
/*      */       }
/*      */     }
/*      */     else {
/*  939 */       arrayOfCertificate2 = (Certificate[])((ConcurrentHashMap)this.package2certs).putIfAbsent(str, arrayOfCertificate1 == null ? nocerts : arrayOfCertificate1);
/*      */     }
/*      */ 
/*  942 */     if ((arrayOfCertificate2 != null) && (!compareCerts(arrayOfCertificate2, arrayOfCertificate1)))
/*  943 */       throw new SecurityException("class \"" + paramString + "\"'s signer information does not match signer information of other classes in the same package");
/*      */   }
/*      */ 
/*      */   private boolean compareCerts(Certificate[] paramArrayOfCertificate1, Certificate[] paramArrayOfCertificate2)
/*      */   {
/*  956 */     if ((paramArrayOfCertificate2 == null) || (paramArrayOfCertificate2.length == 0)) {
/*  957 */       return paramArrayOfCertificate1.length == 0;
/*      */     }
/*      */ 
/*  961 */     if (paramArrayOfCertificate2.length != paramArrayOfCertificate1.length)
/*  962 */       return false;
/*      */     int i;
/*      */     int k;
/*  967 */     for (int j = 0; j < paramArrayOfCertificate2.length; j++) {
/*  968 */       i = 0;
/*  969 */       for (k = 0; k < paramArrayOfCertificate1.length; k++) {
/*  970 */         if (paramArrayOfCertificate2[j].equals(paramArrayOfCertificate1[k])) {
/*  971 */           i = 1;
/*  972 */           break;
/*      */         }
/*      */       }
/*  975 */       if (i == 0) return false;
/*      */ 
/*      */     }
/*      */ 
/*  979 */     for (j = 0; j < paramArrayOfCertificate1.length; j++) {
/*  980 */       i = 0;
/*  981 */       for (k = 0; k < paramArrayOfCertificate2.length; k++) {
/*  982 */         if (paramArrayOfCertificate1[j].equals(paramArrayOfCertificate2[k])) {
/*  983 */           i = 1;
/*  984 */           break;
/*      */         }
/*      */       }
/*  987 */       if (i == 0) return false;
/*      */     }
/*      */ 
/*  990 */     return true;
/*      */   }
/*      */ 
/*      */   protected final void resolveClass(Class<?> paramClass)
/*      */   {
/* 1010 */     resolveClass0(paramClass);
/*      */   }
/*      */ 
/*      */   private native void resolveClass0(Class paramClass);
/*      */ 
/*      */   protected final Class<?> findSystemClass(String paramString)
/*      */     throws ClassNotFoundException
/*      */   {
/* 1040 */     ClassLoader localClassLoader = getSystemClassLoader();
/* 1041 */     if (localClassLoader == null) {
/* 1042 */       if (!checkName(paramString))
/* 1043 */         throw new ClassNotFoundException(paramString);
/* 1044 */       Class localClass = findBootstrapClass(paramString);
/* 1045 */       if (localClass == null) {
/* 1046 */         throw new ClassNotFoundException(paramString);
/*      */       }
/* 1048 */       return localClass;
/*      */     }
/* 1050 */     return localClassLoader.loadClass(paramString);
/*      */   }
/*      */ 
/*      */   private Class findBootstrapClassOrNull(String paramString)
/*      */   {
/* 1059 */     if (!checkName(paramString)) return null;
/*      */ 
/* 1061 */     return findBootstrapClass(paramString);
/*      */   }
/*      */ 
/*      */   private native Class findBootstrapClass(String paramString);
/*      */ 
/*      */   protected final Class<?> findLoadedClass(String paramString)
/*      */   {
/* 1082 */     if (!checkName(paramString))
/* 1083 */       return null;
/* 1084 */     return findLoadedClass0(paramString);
/*      */   }
/*      */ 
/*      */   private final native Class findLoadedClass0(String paramString);
/*      */ 
/*      */   protected final void setSigners(Class<?> paramClass, Object[] paramArrayOfObject)
/*      */   {
/* 1102 */     paramClass.setSigners(paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public URL getResource(String paramString)
/*      */   {
/*      */     URL localURL;
/* 1132 */     if (this.parent != null)
/* 1133 */       localURL = this.parent.getResource(paramString);
/*      */     else {
/* 1135 */       localURL = getBootstrapResource(paramString);
/*      */     }
/* 1137 */     if (localURL == null) {
/* 1138 */       localURL = findResource(paramString);
/*      */     }
/* 1140 */     return localURL;
/*      */   }
/*      */ 
/*      */   public Enumeration<URL> getResources(String paramString)
/*      */     throws IOException
/*      */   {
/* 1170 */     Enumeration[] arrayOfEnumeration = new Enumeration[2];
/* 1171 */     if (this.parent != null)
/* 1172 */       arrayOfEnumeration[0] = this.parent.getResources(paramString);
/*      */     else {
/* 1174 */       arrayOfEnumeration[0] = getBootstrapResources(paramString);
/*      */     }
/* 1176 */     arrayOfEnumeration[1] = findResources(paramString);
/*      */ 
/* 1178 */     return new CompoundEnumeration(arrayOfEnumeration);
/*      */   }
/*      */ 
/*      */   protected URL findResource(String paramString)
/*      */   {
/* 1194 */     return null;
/*      */   }
/*      */ 
/*      */   protected Enumeration<URL> findResources(String paramString)
/*      */     throws IOException
/*      */   {
/* 1215 */     return Collections.emptyEnumeration();
/*      */   }
/*      */ 
/*      */   private static native Class<? extends ClassLoader> getCaller(int paramInt);
/*      */ 
/*      */   protected static boolean registerAsParallelCapable()
/*      */   {
/* 1239 */     return ParallelLoaders.register(getCaller(1));
/*      */   }
/*      */ 
/*      */   public static URL getSystemResource(String paramString)
/*      */   {
/* 1256 */     ClassLoader localClassLoader = getSystemClassLoader();
/* 1257 */     if (localClassLoader == null) {
/* 1258 */       return getBootstrapResource(paramString);
/*      */     }
/* 1260 */     return localClassLoader.getResource(paramString);
/*      */   }
/*      */ 
/*      */   public static Enumeration<URL> getSystemResources(String paramString)
/*      */     throws IOException
/*      */   {
/* 1286 */     ClassLoader localClassLoader = getSystemClassLoader();
/* 1287 */     if (localClassLoader == null) {
/* 1288 */       return getBootstrapResources(paramString);
/*      */     }
/* 1290 */     return localClassLoader.getResources(paramString);
/*      */   }
/*      */ 
/*      */   private static URL getBootstrapResource(String paramString)
/*      */   {
/* 1297 */     URLClassPath localURLClassPath = getBootstrapClassPath();
/* 1298 */     Resource localResource = localURLClassPath.getResource(paramString);
/* 1299 */     return localResource != null ? localResource.getURL() : null;
/*      */   }
/*      */ 
/*      */   private static Enumeration<URL> getBootstrapResources(String paramString)
/*      */     throws IOException
/*      */   {
/* 1308 */     Enumeration localEnumeration = getBootstrapClassPath().getResources(paramString);
/*      */ 
/* 1310 */     return new Enumeration() {
/*      */       public URL nextElement() {
/* 1312 */         return ((Resource)this.val$e.nextElement()).getURL();
/*      */       }
/*      */       public boolean hasMoreElements() {
/* 1315 */         return this.val$e.hasMoreElements();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   static URLClassPath getBootstrapClassPath()
/*      */   {
/* 1322 */     return Launcher.getBootstrapClassPath();
/*      */   }
/*      */ 
/*      */   public InputStream getResourceAsStream(String paramString)
/*      */   {
/* 1341 */     URL localURL = getResource(paramString);
/*      */     try {
/* 1343 */       return localURL != null ? localURL.openStream() : null; } catch (IOException localIOException) {
/*      */     }
/* 1345 */     return null;
/*      */   }
/*      */ 
/*      */   public static InputStream getSystemResourceAsStream(String paramString)
/*      */   {
/* 1363 */     URL localURL = getSystemResource(paramString);
/*      */     try {
/* 1365 */       return localURL != null ? localURL.openStream() : null; } catch (IOException localIOException) {
/*      */     }
/* 1367 */     return null;
/*      */   }
/*      */ 
/*      */   public final ClassLoader getParent()
/*      */   {
/* 1400 */     if (this.parent == null)
/* 1401 */       return null;
/* 1402 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1403 */     if (localSecurityManager != null) {
/* 1404 */       ClassLoader localClassLoader = getCallerClassLoader();
/* 1405 */       if ((localClassLoader != null) && (!isAncestor(localClassLoader))) {
/* 1406 */         localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
/*      */       }
/*      */     }
/* 1409 */     return this.parent;
/*      */   }
/*      */ 
/*      */   public static ClassLoader getSystemClassLoader()
/*      */   {
/* 1468 */     initSystemClassLoader();
/* 1469 */     if (scl == null) {
/* 1470 */       return null;
/*      */     }
/* 1472 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1473 */     if (localSecurityManager != null) {
/* 1474 */       ClassLoader localClassLoader = getCallerClassLoader();
/* 1475 */       if ((localClassLoader != null) && (localClassLoader != scl) && (!scl.isAncestor(localClassLoader))) {
/* 1476 */         localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
/*      */       }
/*      */     }
/* 1479 */     return scl;
/*      */   }
/*      */ 
/*      */   private static synchronized void initSystemClassLoader() {
/* 1483 */     if (!sclSet) {
/* 1484 */       if (scl != null)
/* 1485 */         throw new IllegalStateException("recursive invocation");
/* 1486 */       Launcher localLauncher = Launcher.getLauncher();
/* 1487 */       if (localLauncher != null) {
/* 1488 */         Throwable localThrowable = null;
/* 1489 */         scl = localLauncher.getClassLoader();
/*      */         try {
/* 1491 */           scl = (ClassLoader)AccessController.doPrivileged(new SystemClassLoaderAction(scl));
/*      */         }
/*      */         catch (PrivilegedActionException localPrivilegedActionException) {
/* 1494 */           localThrowable = localPrivilegedActionException.getCause();
/* 1495 */           if ((localThrowable instanceof InvocationTargetException)) {
/* 1496 */             localThrowable = localThrowable.getCause();
/*      */           }
/*      */         }
/* 1499 */         if (localThrowable != null) {
/* 1500 */           if ((localThrowable instanceof Error)) {
/* 1501 */             throw ((Error)localThrowable);
/*      */           }
/*      */ 
/* 1504 */           throw new Error(localThrowable);
/*      */         }
/*      */       }
/*      */ 
/* 1508 */       sclSet = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean isAncestor(ClassLoader paramClassLoader)
/*      */   {
/* 1515 */     ClassLoader localClassLoader = this;
/*      */     do {
/* 1517 */       localClassLoader = localClassLoader.parent;
/* 1518 */       if (paramClassLoader == localClassLoader)
/* 1519 */         return true;
/*      */     }
/* 1521 */     while (localClassLoader != null);
/* 1522 */     return false;
/*      */   }
/*      */ 
/*      */   static ClassLoader getCallerClassLoader()
/*      */   {
/* 1531 */     Class localClass = Reflection.getCallerClass(3);
/*      */ 
/* 1533 */     if (localClass == null) {
/* 1534 */       return null;
/*      */     }
/*      */ 
/* 1537 */     return localClass.getClassLoader0();
/*      */   }
/*      */ 
/*      */   protected Package definePackage(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, URL paramURL)
/*      */     throws IllegalArgumentException
/*      */   {
/* 1598 */     synchronized (this.packages) {
/* 1599 */       Package localPackage = getPackage(paramString1);
/* 1600 */       if (localPackage != null) {
/* 1601 */         throw new IllegalArgumentException(paramString1);
/*      */       }
/* 1603 */       localPackage = new Package(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramURL, this);
/*      */ 
/* 1606 */       this.packages.put(paramString1, localPackage);
/* 1607 */       return localPackage;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Package getPackage(String paramString)
/*      */   {
/*      */     Object localObject1;
/* 1625 */     synchronized (this.packages) {
/* 1626 */       localObject1 = (Package)this.packages.get(paramString);
/*      */     }
/* 1628 */     if (localObject1 == null) {
/* 1629 */       if (this.parent != null)
/* 1630 */         localObject1 = this.parent.getPackage(paramString);
/*      */       else {
/* 1632 */         localObject1 = Package.getSystemPackage(paramString);
/*      */       }
/* 1634 */       if (localObject1 != null) {
/* 1635 */         synchronized (this.packages) {
/* 1636 */           Package localPackage = (Package)this.packages.get(paramString);
/* 1637 */           if (localPackage == null)
/* 1638 */             this.packages.put(paramString, localObject1);
/*      */           else {
/* 1640 */             localObject1 = localPackage;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1645 */     return localObject1;
/*      */   }
/*      */ 
/*      */   protected Package[] getPackages()
/*      */   {
/*      */     HashMap localHashMap;
/* 1659 */     synchronized (this.packages) {
/* 1660 */       localHashMap = new HashMap(this.packages);
/*      */     }
/*      */ 
/* 1663 */     if (this.parent != null)
/* 1664 */       ??? = this.parent.getPackages();
/*      */     else {
/* 1666 */       ??? = Package.getSystemPackages();
/*      */     }
/* 1668 */     if (??? != null) {
/* 1669 */       for (int i = 0; i < ???.length; i++) {
/* 1670 */         String str = ???[i].getName();
/* 1671 */         if (localHashMap.get(str) == null) {
/* 1672 */           localHashMap.put(str, ???[i]);
/*      */         }
/*      */       }
/*      */     }
/* 1676 */     return (Package[])localHashMap.values().toArray(new Package[localHashMap.size()]);
/*      */   }
/*      */ 
/*      */   protected String findLibrary(String paramString)
/*      */   {
/* 1700 */     return null;
/*      */   }
/*      */ 
/*      */   private static String[] initializePath(String paramString)
/*      */   {
/* 1784 */     String str1 = System.getProperty(paramString, "");
/* 1785 */     String str2 = File.pathSeparator;
/* 1786 */     int i = str1.length();
/*      */ 
/* 1789 */     int j = str1.indexOf(str2);
/* 1790 */     int m = 0;
/* 1791 */     while (j >= 0) {
/* 1792 */       m++;
/* 1793 */       j = str1.indexOf(str2, j + 1);
/*      */     }
/*      */ 
/* 1797 */     String[] arrayOfString = new String[m + 1];
/*      */ 
/* 1800 */     m = j = 0;
/* 1801 */     int k = str1.indexOf(str2);
/* 1802 */     while (k >= 0) {
/* 1803 */       if (k - j > 0)
/* 1804 */         arrayOfString[(m++)] = str1.substring(j, k);
/* 1805 */       else if (k - j == 0) {
/* 1806 */         arrayOfString[(m++)] = ".";
/*      */       }
/* 1808 */       j = k + 1;
/* 1809 */       k = str1.indexOf(str2, j);
/*      */     }
/* 1811 */     arrayOfString[m] = str1.substring(j, i);
/* 1812 */     return arrayOfString;
/*      */   }
/*      */ 
/*      */   static void loadLibrary(Class paramClass, String paramString, boolean paramBoolean)
/*      */   {
/* 1818 */     ClassLoader localClassLoader = paramClass == null ? null : paramClass.getClassLoader();
/*      */ 
/* 1820 */     if (sys_paths == null) {
/* 1821 */       usr_paths = initializePath("java.library.path");
/* 1822 */       sys_paths = initializePath("sun.boot.library.path");
/*      */     }
/* 1824 */     if (paramBoolean) {
/* 1825 */       if (loadLibrary0(paramClass, new File(paramString))) {
/* 1826 */         return;
/*      */       }
/* 1828 */       throw new UnsatisfiedLinkError("Can't load library: " + paramString);
/*      */     }
/*      */     File localFile;
/* 1830 */     if (localClassLoader != null) {
/* 1831 */       String str = localClassLoader.findLibrary(paramString);
/* 1832 */       if (str != null) {
/* 1833 */         localFile = new File(str);
/* 1834 */         if (!localFile.isAbsolute()) {
/* 1835 */           throw new UnsatisfiedLinkError("ClassLoader.findLibrary failed to return an absolute path: " + str);
/*      */         }
/*      */ 
/* 1838 */         if (loadLibrary0(paramClass, localFile)) {
/* 1839 */           return;
/*      */         }
/* 1841 */         throw new UnsatisfiedLinkError("Can't load " + str);
/*      */       }
/*      */     }
/* 1844 */     for (int i = 0; i < sys_paths.length; i++) {
/* 1845 */       localFile = new File(sys_paths[i], System.mapLibraryName(paramString));
/* 1846 */       if (loadLibrary0(paramClass, localFile)) {
/* 1847 */         return;
/*      */       }
/*      */     }
/* 1850 */     if (localClassLoader != null) {
/* 1851 */       for (i = 0; i < usr_paths.length; i++) {
/* 1852 */         localFile = new File(usr_paths[i], System.mapLibraryName(paramString));
/*      */ 
/* 1854 */         if (loadLibrary0(paramClass, localFile)) {
/* 1855 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1860 */     throw new UnsatisfiedLinkError("no " + paramString + " in java.library.path");
/*      */   }
/*      */ 
/*      */   private static boolean loadLibrary0(Class paramClass, File paramFile) {
/* 1864 */     if (loadLibrary1(paramClass, paramFile)) {
/* 1865 */       return true;
/*      */     }
/* 1867 */     File localFile = ClassLoaderHelper.mapAlternativeName(paramFile);
/* 1868 */     if ((localFile != null) && (loadLibrary1(paramClass, localFile))) {
/* 1869 */       return true;
/*      */     }
/* 1871 */     return false;
/*      */   }
/*      */ 
/*      */   private static boolean loadLibrary1(Class paramClass, File paramFile) {
/* 1875 */     int i = AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run() {
/* 1878 */         return this.val$file.exists() ? Boolean.TRUE : null;
/*      */       }
/*      */     }) != null ? 1 : 0;
/*      */ 
/* 1881 */     if (i == 0)
/* 1882 */       return false;
/*      */     String str;
/*      */     try
/*      */     {
/* 1886 */       str = paramFile.getCanonicalPath();
/*      */     } catch (IOException localIOException) {
/* 1888 */       return false;
/*      */     }
/* 1890 */     ClassLoader localClassLoader = paramClass == null ? null : paramClass.getClassLoader();
/*      */ 
/* 1892 */     Vector localVector = localClassLoader != null ? localClassLoader.nativeLibraries : systemNativeLibraries;
/*      */ 
/* 1894 */     synchronized (localVector) {
/* 1895 */       int j = localVector.size();
/* 1896 */       for (int k = 0; k < j; k++) {
/* 1897 */         NativeLibrary localNativeLibrary1 = (NativeLibrary)localVector.elementAt(k);
/* 1898 */         if (str.equals(localNativeLibrary1.name)) {
/* 1899 */           return true;
/*      */         }
/*      */       }
/*      */ 
/* 1903 */       synchronized (loadedLibraryNames) {
/* 1904 */         if (loadedLibraryNames.contains(str)) {
/* 1905 */           throw new UnsatisfiedLinkError("Native Library " + str + " already loaded in another classloader");
/*      */         }
/*      */ 
/* 1922 */         int m = nativeLibraryContext.size();
/* 1923 */         for (int n = 0; n < m; n++) {
/* 1924 */           NativeLibrary localNativeLibrary3 = (NativeLibrary)nativeLibraryContext.elementAt(n);
/* 1925 */           if (str.equals(localNativeLibrary3.name)) {
/* 1926 */             if (localClassLoader == localNativeLibrary3.fromClass.getClassLoader()) {
/* 1927 */               return true;
/*      */             }
/* 1929 */             throw new UnsatisfiedLinkError("Native Library " + str + " is being loaded in another classloader");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1936 */         NativeLibrary localNativeLibrary2 = new NativeLibrary(paramClass, str);
/* 1937 */         nativeLibraryContext.push(localNativeLibrary2);
/*      */         try {
/* 1939 */           localNativeLibrary2.load(str);
/*      */         } finally {
/* 1941 */           nativeLibraryContext.pop();
/*      */         }
/* 1943 */         if (localNativeLibrary2.handle != 0L) {
/* 1944 */           loadedLibraryNames.addElement(str);
/* 1945 */           localVector.addElement(localNativeLibrary2);
/* 1946 */           return true;
/*      */         }
/* 1948 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static long findNative(ClassLoader paramClassLoader, String paramString)
/*      */   {
/* 1955 */     Vector localVector = paramClassLoader != null ? paramClassLoader.nativeLibraries : systemNativeLibraries;
/*      */ 
/* 1957 */     synchronized (localVector) {
/* 1958 */       int i = localVector.size();
/* 1959 */       for (int j = 0; j < i; j++) {
/* 1960 */         NativeLibrary localNativeLibrary = (NativeLibrary)localVector.elementAt(j);
/* 1961 */         long l = localNativeLibrary.find(paramString);
/* 1962 */         if (l != 0L)
/* 1963 */           return l;
/*      */       }
/*      */     }
/* 1966 */     return 0L;
/*      */   }
/*      */ 
/*      */   public void setDefaultAssertionStatus(boolean paramBoolean)
/*      */   {
/* 2009 */     synchronized (this.assertionLock) {
/* 2010 */       if (this.classAssertionStatus == null) {
/* 2011 */         initializeJavaAssertionMaps();
/*      */       }
/* 2013 */       this.defaultAssertionStatus = paramBoolean;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setPackageAssertionStatus(String paramString, boolean paramBoolean)
/*      */   {
/* 2056 */     synchronized (this.assertionLock) {
/* 2057 */       if (this.packageAssertionStatus == null) {
/* 2058 */         initializeJavaAssertionMaps();
/*      */       }
/* 2060 */       this.packageAssertionStatus.put(paramString, Boolean.valueOf(paramBoolean));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClassAssertionStatus(String paramString, boolean paramBoolean)
/*      */   {
/* 2087 */     synchronized (this.assertionLock) {
/* 2088 */       if (this.classAssertionStatus == null) {
/* 2089 */         initializeJavaAssertionMaps();
/*      */       }
/* 2091 */       this.classAssertionStatus.put(paramString, Boolean.valueOf(paramBoolean));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearAssertionStatus()
/*      */   {
/* 2110 */     synchronized (this.assertionLock) {
/* 2111 */       this.classAssertionStatus = new HashMap();
/* 2112 */       this.packageAssertionStatus = new HashMap();
/* 2113 */       this.defaultAssertionStatus = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean desiredAssertionStatus(String paramString)
/*      */   {
/* 2140 */     synchronized (this.assertionLock)
/*      */     {
/* 2145 */       Boolean localBoolean = (Boolean)this.classAssertionStatus.get(paramString);
/* 2146 */       if (localBoolean != null) {
/* 2147 */         return localBoolean.booleanValue();
/*      */       }
/*      */ 
/* 2150 */       int i = paramString.lastIndexOf(".");
/* 2151 */       if (i < 0) {
/* 2152 */         localBoolean = (Boolean)this.packageAssertionStatus.get(null);
/* 2153 */         if (localBoolean != null)
/* 2154 */           return localBoolean.booleanValue();
/*      */       }
/* 2156 */       while (i > 0) {
/* 2157 */         paramString = paramString.substring(0, i);
/* 2158 */         localBoolean = (Boolean)this.packageAssertionStatus.get(paramString);
/* 2159 */         if (localBoolean != null)
/* 2160 */           return localBoolean.booleanValue();
/* 2161 */         i = paramString.lastIndexOf(".", i - 1);
/*      */       }
/*      */ 
/* 2165 */       return this.defaultAssertionStatus;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initializeJavaAssertionMaps()
/*      */   {
/* 2174 */     this.classAssertionStatus = new HashMap();
/* 2175 */     this.packageAssertionStatus = new HashMap();
/* 2176 */     AssertionStatusDirectives localAssertionStatusDirectives = retrieveDirectives();
/*      */ 
/* 2178 */     for (int i = 0; i < localAssertionStatusDirectives.classes.length; i++) {
/* 2179 */       this.classAssertionStatus.put(localAssertionStatusDirectives.classes[i], Boolean.valueOf(localAssertionStatusDirectives.classEnabled[i]));
/*      */     }
/*      */ 
/* 2182 */     for (i = 0; i < localAssertionStatusDirectives.packages.length; i++) {
/* 2183 */       this.packageAssertionStatus.put(localAssertionStatusDirectives.packages[i], Boolean.valueOf(localAssertionStatusDirectives.packageEnabled[i]));
/*      */     }
/*      */ 
/* 2186 */     this.defaultAssertionStatus = localAssertionStatusDirectives.deflt;
/*      */   }
/*      */ 
/*      */   private static native AssertionStatusDirectives retrieveDirectives();
/*      */ 
/*      */   static
/*      */   {
/*  181 */     registerNatives();
/*      */   }
/*      */ 
/*      */   static class NativeLibrary
/*      */   {
/*      */     long handle;
/*      */     private int jniVersion;
/*      */     private Class fromClass;
/*      */     String name;
/*      */ 
/*      */     native void load(String paramString);
/*      */ 
/*      */     native long find(String paramString);
/*      */ 
/*      */     native void unload();
/*      */ 
/*      */     public NativeLibrary(Class paramClass, String paramString)
/*      */     {
/* 1734 */       this.name = paramString;
/* 1735 */       this.fromClass = paramClass;
/*      */     }
/*      */ 
/*      */     protected void finalize() {
/* 1739 */       synchronized (ClassLoader.loadedLibraryNames) {
/* 1740 */         if ((this.fromClass.getClassLoader() != null) && (this.handle != 0L))
/*      */         {
/* 1742 */           int i = ClassLoader.loadedLibraryNames.size();
/* 1743 */           for (int j = 0; j < i; j++) {
/* 1744 */             if (this.name.equals(ClassLoader.loadedLibraryNames.elementAt(j))) {
/* 1745 */               ClassLoader.loadedLibraryNames.removeElementAt(j);
/* 1746 */               break;
/*      */             }
/*      */           }
/*      */ 
/* 1750 */           ClassLoader.nativeLibraryContext.push(this);
/*      */           try {
/* 1752 */             unload();
/*      */           } finally {
/* 1754 */             ClassLoader.nativeLibraryContext.pop();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     static Class getFromClass()
/*      */     {
/* 1762 */       return ((NativeLibrary)ClassLoader.nativeLibraryContext.peek()).fromClass;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ParallelLoaders
/*      */   {
/*  196 */     private static final Set<Class<? extends ClassLoader>> loaderTypes = Collections.newSetFromMap(new WeakHashMap());
/*      */ 
/*      */     static boolean register(Class<? extends ClassLoader> paramClass)
/*      */     {
/*  209 */       synchronized (loaderTypes) {
/*  210 */         if (loaderTypes.contains(paramClass.getSuperclass()))
/*      */         {
/*  216 */           loaderTypes.add(paramClass);
/*  217 */           return true;
/*      */         }
/*  219 */         return false;
/*      */       }
/*      */     }
/*      */ 
/*      */     static boolean isRegistered(Class<? extends ClassLoader> paramClass)
/*      */     {
/*  229 */       synchronized (loaderTypes) {
/*  230 */         return loaderTypes.contains(paramClass);
/*      */       }
/*      */     }
/*      */ 
/*      */     static
/*      */     {
/*  200 */       synchronized (loaderTypes) { loaderTypes.add(ClassLoader.class); }
/*      */ 
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.ClassLoader
 * JD-Core Version:    0.6.2
 */