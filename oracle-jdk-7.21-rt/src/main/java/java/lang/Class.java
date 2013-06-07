/*      */ package java.lang;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectStreamField;
/*      */ import java.io.Serializable;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.lang.reflect.AnnotatedElement;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.GenericArrayType;
/*      */ import java.lang.reflect.GenericDeclaration;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.lang.reflect.Type;
/*      */ import java.lang.reflect.TypeVariable;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.Permissions;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.reflect.ConstantPool;
/*      */ import sun.reflect.Reflection;
/*      */ import sun.reflect.ReflectionFactory;
/*      */ import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
/*      */ import sun.reflect.annotation.AnnotationParser;
/*      */ import sun.reflect.annotation.AnnotationType;
/*      */ import sun.reflect.generics.factory.CoreReflectionFactory;
/*      */ import sun.reflect.generics.factory.GenericsFactory;
/*      */ import sun.reflect.generics.repository.ClassRepository;
/*      */ import sun.reflect.generics.repository.ConstructorRepository;
/*      */ import sun.reflect.generics.repository.MethodRepository;
/*      */ import sun.reflect.generics.scope.ClassScope;
/*      */ import sun.reflect.misc.ReflectUtil;
/*      */ import sun.security.util.SecurityConstants;
/*      */ 
/*      */ public final class Class<T>
/*      */   implements Serializable, GenericDeclaration, Type, AnnotatedElement
/*      */ {
/*      */   private static final int ANNOTATION = 8192;
/*      */   private static final int ENUM = 16384;
/*      */   private static final int SYNTHETIC = 4096;
/*      */   private volatile transient Constructor<T> cachedConstructor;
/*      */   private volatile transient Class<?> newInstanceCallerCache;
/*      */   private transient String name;
/*      */   private static ProtectionDomain allPermDomain;
/* 2226 */   private static boolean useCaches = true;
/*      */   private volatile transient SoftReference<Field[]> declaredFields;
/*      */   private volatile transient SoftReference<Field[]> publicFields;
/*      */   private volatile transient SoftReference<Method[]> declaredMethods;
/*      */   private volatile transient SoftReference<Method[]> publicMethods;
/*      */   private volatile transient SoftReference<Constructor<T>[]> declaredConstructors;
/*      */   private volatile transient SoftReference<Constructor<T>[]> publicConstructors;
/*      */   private volatile transient SoftReference<Field[]> declaredPublicFields;
/*      */   private volatile transient SoftReference<Method[]> declaredPublicMethods;
/* 2239 */   private volatile transient int classRedefinedCount = 0;
/*      */ 
/* 2243 */   private volatile transient int lastRedefinedCount = 0;
/*      */   private transient ClassRepository genericInfo;
/*      */   private static final long serialVersionUID = 3206093459760846163L;
/* 2827 */   private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];
/*      */   private static ReflectionFactory reflectionFactory;
/* 2902 */   private static boolean initted = false;
/*      */ 
/* 2976 */   private volatile transient T[] enumConstants = null;
/*      */ 
/* 2998 */   private volatile transient Map<String, T> enumConstantDirectory = null;
/*      */   private transient Map<Class<? extends Annotation>, Annotation> annotations;
/*      */   private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
/*      */   private AnnotationType annotationType;
/*      */   transient ClassValue.ClassValueMap classValueMap;
/*      */ 
/*      */   private static native void registerNatives();
/*      */ 
/*      */   public String toString()
/*      */   {
/*  150 */     return (isPrimitive() ? "" : isInterface() ? "interface " : "class ") + getName();
/*      */   }
/*      */ 
/*      */   public static Class<?> forName(String paramString)
/*      */     throws ClassNotFoundException
/*      */   {
/*  188 */     return forName0(paramString, true, ClassLoader.getCallerClassLoader());
/*      */   }
/*      */ 
/*      */   public static Class<?> forName(String paramString, boolean paramBoolean, ClassLoader paramClassLoader)
/*      */     throws ClassNotFoundException
/*      */   {
/*  256 */     if (paramClassLoader == null) {
/*  257 */       SecurityManager localSecurityManager = System.getSecurityManager();
/*  258 */       if (localSecurityManager != null) {
/*  259 */         ClassLoader localClassLoader = ClassLoader.getCallerClassLoader();
/*  260 */         if (localClassLoader != null) {
/*  261 */           localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  266 */     return forName0(paramString, paramBoolean, paramClassLoader);
/*      */   }
/*      */ 
/*      */   private static native Class<?> forName0(String paramString, boolean paramBoolean, ClassLoader paramClassLoader)
/*      */     throws ClassNotFoundException;
/*      */ 
/*      */   public T newInstance()
/*      */     throws InstantiationException, IllegalAccessException
/*      */   {
/*  324 */     if (System.getSecurityManager() != null) {
/*  325 */       checkMemberAccess(0, ClassLoader.getCallerClassLoader(), false);
/*      */     }
/*  327 */     return newInstance0();
/*      */   }
/*      */ 
/*      */   private T newInstance0()
/*      */     throws InstantiationException, IllegalAccessException
/*      */   {
/*  337 */     if (this.cachedConstructor == null) {
/*  338 */       if (this == Class.class) {
/*  339 */         throw new IllegalAccessException("Can not call newInstance() on the Class for java.lang.Class");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  344 */         Class[] arrayOfClass = new Class[0];
/*  345 */         final Constructor localConstructor2 = getConstructor0(arrayOfClass, 1);
/*      */ 
/*  350 */         AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Void run() {
/*  353 */             localConstructor2.setAccessible(true);
/*  354 */             return null;
/*      */           }
/*      */         });
/*  357 */         this.cachedConstructor = localConstructor2;
/*      */       } catch (NoSuchMethodException localNoSuchMethodException) {
/*  359 */         throw new InstantiationException(getName());
/*      */       }
/*      */     }
/*  362 */     Constructor localConstructor1 = this.cachedConstructor;
/*      */ 
/*  364 */     int i = localConstructor1.getModifiers();
/*  365 */     if (!Reflection.quickCheckMemberAccess(this, i)) {
/*  366 */       Class localClass = Reflection.getCallerClass(3);
/*  367 */       if (this.newInstanceCallerCache != localClass) {
/*  368 */         Reflection.ensureMemberAccess(localClass, this, null, i);
/*  369 */         this.newInstanceCallerCache = localClass;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  374 */       return localConstructor1.newInstance((Object[])null);
/*      */     } catch (InvocationTargetException localInvocationTargetException) {
/*  376 */       Unsafe.getUnsafe().throwException(localInvocationTargetException.getTargetException());
/*      */     }
/*  378 */     return null;
/*      */   }
/*      */ 
/*      */   public native boolean isInstance(Object paramObject);
/*      */ 
/*      */   public native boolean isAssignableFrom(Class<?> paramClass);
/*      */ 
/*      */   public native boolean isInterface();
/*      */ 
/*      */   public native boolean isArray();
/*      */ 
/*      */   public native boolean isPrimitive();
/*      */ 
/*      */   public boolean isAnnotation()
/*      */   {
/*  505 */     return (getModifiers() & 0x2000) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isSynthetic()
/*      */   {
/*  516 */     return (getModifiers() & 0x1000) != 0;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  571 */     String str = this.name;
/*  572 */     if (str == null)
/*  573 */       this.name = (str = getName0());
/*  574 */     return str;
/*      */   }
/*      */ 
/*      */   private native String getName0();
/*      */ 
/*      */   public ClassLoader getClassLoader()
/*      */   {
/*  608 */     ClassLoader localClassLoader1 = getClassLoader0();
/*  609 */     if (localClassLoader1 == null)
/*  610 */       return null;
/*  611 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  612 */     if (localSecurityManager != null) {
/*  613 */       ClassLoader localClassLoader2 = ClassLoader.getCallerClassLoader();
/*  614 */       if ((localClassLoader2 != null) && (localClassLoader2 != localClassLoader1) && (!localClassLoader1.isAncestor(localClassLoader2))) {
/*  615 */         localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
/*      */       }
/*      */     }
/*  618 */     return localClassLoader1;
/*      */   }
/*      */ 
/*      */   native ClassLoader getClassLoader0();
/*      */ 
/*      */   public TypeVariable<Class<T>>[] getTypeParameters()
/*      */   {
/*  641 */     if (getGenericSignature() != null) {
/*  642 */       return (TypeVariable[])getGenericInfo().getTypeParameters();
/*      */     }
/*  644 */     return (TypeVariable[])new TypeVariable[0];
/*      */   }
/*      */ 
/*      */   public native Class<? super T> getSuperclass();
/*      */ 
/*      */   public Type getGenericSuperclass()
/*      */   {
/*  692 */     if (getGenericSignature() != null)
/*      */     {
/*  696 */       if (isInterface())
/*  697 */         return null;
/*  698 */       return getGenericInfo().getSuperclass();
/*      */     }
/*  700 */     return getSuperclass();
/*      */   }
/*      */ 
/*      */   public Package getPackage()
/*      */   {
/*  719 */     return Package.getPackage(this);
/*      */   }
/*      */ 
/*      */   public native Class<?>[] getInterfaces();
/*      */ 
/*      */   public Type[] getGenericInterfaces()
/*      */   {
/*  815 */     if (getGenericSignature() != null) {
/*  816 */       return getGenericInfo().getSuperInterfaces();
/*      */     }
/*  818 */     return getInterfaces();
/*      */   }
/*      */ 
/*      */   public native Class<?> getComponentType();
/*      */ 
/*      */   public native int getModifiers();
/*      */ 
/*      */   public native Object[] getSigners();
/*      */ 
/*      */   native void setSigners(Object[] paramArrayOfObject);
/*      */ 
/*      */   public Method getEnclosingMethod()
/*      */   {
/*  898 */     EnclosingMethodInfo localEnclosingMethodInfo = getEnclosingMethodInfo();
/*      */ 
/*  900 */     if (localEnclosingMethodInfo == null) {
/*  901 */       return null;
/*      */     }
/*  903 */     if (!localEnclosingMethodInfo.isMethod()) {
/*  904 */       return null;
/*      */     }
/*  906 */     MethodRepository localMethodRepository = MethodRepository.make(localEnclosingMethodInfo.getDescriptor(), getFactory());
/*      */ 
/*  908 */     Class localClass = toClass(localMethodRepository.getReturnType());
/*  909 */     Type[] arrayOfType = localMethodRepository.getParameterTypes();
/*  910 */     Class[] arrayOfClass1 = new Class[arrayOfType.length];
/*      */ 
/*  915 */     for (int i = 0; i < arrayOfClass1.length; i++) {
/*  916 */       arrayOfClass1[i] = toClass(arrayOfType[i]);
/*      */     }
/*      */ 
/*  924 */     for (Method localMethod : localEnclosingMethodInfo.getEnclosingClass().getDeclaredMethods()) {
/*  925 */       if (localMethod.getName().equals(localEnclosingMethodInfo.getName())) {
/*  926 */         Class[] arrayOfClass2 = localMethod.getParameterTypes();
/*  927 */         if (arrayOfClass2.length == arrayOfClass1.length) {
/*  928 */           int m = 1;
/*  929 */           for (int n = 0; n < arrayOfClass2.length; n++) {
/*  930 */             if (!arrayOfClass2[n].equals(arrayOfClass1[n])) {
/*  931 */               m = 0;
/*  932 */               break;
/*      */             }
/*      */           }
/*      */ 
/*  936 */           if ((m != 0) && 
/*  937 */             (localMethod.getReturnType().equals(localClass))) {
/*  938 */             return localMethod;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  944 */     throw new InternalError("Enclosing method not found");
/*      */   }
/*      */ 
/*      */   private native Object[] getEnclosingMethod0();
/*      */ 
/*      */   private EnclosingMethodInfo getEnclosingMethodInfo()
/*      */   {
/*  951 */     Object[] arrayOfObject = getEnclosingMethod0();
/*  952 */     if (arrayOfObject == null) {
/*  953 */       return null;
/*      */     }
/*  955 */     return new EnclosingMethodInfo(arrayOfObject, null);
/*      */   }
/*      */ 
/*      */   private static Class<?> toClass(Type paramType)
/*      */   {
/* 1004 */     if ((paramType instanceof GenericArrayType)) {
/* 1005 */       return Array.newInstance(toClass(((GenericArrayType)paramType).getGenericComponentType()), 0).getClass();
/*      */     }
/*      */ 
/* 1008 */     return (Class)paramType;
/*      */   }
/*      */ 
/*      */   public Constructor<?> getEnclosingConstructor()
/*      */   {
/* 1026 */     EnclosingMethodInfo localEnclosingMethodInfo = getEnclosingMethodInfo();
/*      */ 
/* 1028 */     if (localEnclosingMethodInfo == null) {
/* 1029 */       return null;
/*      */     }
/* 1031 */     if (!localEnclosingMethodInfo.isConstructor()) {
/* 1032 */       return null;
/*      */     }
/* 1034 */     ConstructorRepository localConstructorRepository = ConstructorRepository.make(localEnclosingMethodInfo.getDescriptor(), getFactory());
/*      */ 
/* 1036 */     Type[] arrayOfType = localConstructorRepository.getParameterTypes();
/* 1037 */     Class[] arrayOfClass1 = new Class[arrayOfType.length];
/*      */ 
/* 1042 */     for (int i = 0; i < arrayOfClass1.length; i++) {
/* 1043 */       arrayOfClass1[i] = toClass(arrayOfType[i]);
/*      */     }
/*      */ 
/* 1049 */     for (Constructor localConstructor : localEnclosingMethodInfo.getEnclosingClass().getDeclaredConstructors()) {
/* 1050 */       Class[] arrayOfClass2 = localConstructor.getParameterTypes();
/* 1051 */       if (arrayOfClass2.length == arrayOfClass1.length) {
/* 1052 */         int m = 1;
/* 1053 */         for (int n = 0; n < arrayOfClass2.length; n++) {
/* 1054 */           if (!arrayOfClass2[n].equals(arrayOfClass1[n])) {
/* 1055 */             m = 0;
/* 1056 */             break;
/*      */           }
/*      */         }
/*      */ 
/* 1060 */         if (m != 0) {
/* 1061 */           return localConstructor;
/*      */         }
/*      */       }
/*      */     }
/* 1065 */     throw new InternalError("Enclosing constructor not found");
/*      */   }
/*      */ 
/*      */   public native Class<?> getDeclaringClass();
/*      */ 
/*      */   public Class<?> getEnclosingClass()
/*      */   {
/* 1103 */     EnclosingMethodInfo localEnclosingMethodInfo = getEnclosingMethodInfo();
/*      */ 
/* 1105 */     if (localEnclosingMethodInfo == null)
/*      */     {
/* 1107 */       return getDeclaringClass();
/*      */     }
/* 1109 */     Class localClass = localEnclosingMethodInfo.getEnclosingClass();
/*      */ 
/* 1111 */     if ((localClass == this) || (localClass == null)) {
/* 1112 */       throw new InternalError("Malformed enclosing method information");
/*      */     }
/* 1114 */     return localClass;
/*      */   }
/*      */ 
/*      */   public String getSimpleName()
/*      */   {
/* 1131 */     if (isArray()) {
/* 1132 */       return getComponentType().getSimpleName() + "[]";
/*      */     }
/* 1134 */     String str = getSimpleBinaryName();
/* 1135 */     if (str == null) {
/* 1136 */       str = getName();
/* 1137 */       return str.substring(str.lastIndexOf(".") + 1);
/*      */     }
/*      */ 
/* 1153 */     int i = str.length();
/* 1154 */     if ((i < 1) || (str.charAt(0) != '$'))
/* 1155 */       throw new InternalError("Malformed class name");
/* 1156 */     int j = 1;
/* 1157 */     while ((j < i) && (isAsciiDigit(str.charAt(j)))) {
/* 1158 */       j++;
/*      */     }
/* 1160 */     return str.substring(j);
/*      */   }
/*      */ 
/*      */   private static boolean isAsciiDigit(char paramChar)
/*      */   {
/* 1168 */     return ('0' <= paramChar) && (paramChar <= '9');
/*      */   }
/*      */ 
/*      */   public String getCanonicalName()
/*      */   {
/* 1182 */     if (isArray()) {
/* 1183 */       localObject = getComponentType().getCanonicalName();
/* 1184 */       if (localObject != null) {
/* 1185 */         return (String)localObject + "[]";
/*      */       }
/* 1187 */       return null;
/*      */     }
/* 1189 */     if (isLocalOrAnonymousClass())
/* 1190 */       return null;
/* 1191 */     Object localObject = getEnclosingClass();
/* 1192 */     if (localObject == null) {
/* 1193 */       return getName();
/*      */     }
/* 1195 */     String str = ((Class)localObject).getCanonicalName();
/* 1196 */     if (str == null)
/* 1197 */       return null;
/* 1198 */     return str + "." + getSimpleName();
/*      */   }
/*      */ 
/*      */   public boolean isAnonymousClass()
/*      */   {
/* 1210 */     return "".equals(getSimpleName());
/*      */   }
/*      */ 
/*      */   public boolean isLocalClass()
/*      */   {
/* 1221 */     return (isLocalOrAnonymousClass()) && (!isAnonymousClass());
/*      */   }
/*      */ 
/*      */   public boolean isMemberClass()
/*      */   {
/* 1232 */     return (getSimpleBinaryName() != null) && (!isLocalOrAnonymousClass());
/*      */   }
/*      */ 
/*      */   private String getSimpleBinaryName()
/*      */   {
/* 1242 */     Class localClass = getEnclosingClass();
/* 1243 */     if (localClass == null)
/* 1244 */       return null;
/*      */     try
/*      */     {
/* 1247 */       return getName().substring(localClass.getName().length()); } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*      */     }
/* 1249 */     throw new InternalError("Malformed class name");
/*      */   }
/*      */ 
/*      */   private boolean isLocalOrAnonymousClass()
/*      */   {
/* 1261 */     return getEnclosingMethodInfo() != null;
/*      */   }
/*      */ 
/*      */   public Class<?>[] getClasses()
/*      */   {
/* 1302 */     checkMemberAccess(0, ClassLoader.getCallerClassLoader(), false);
/*      */ 
/* 1310 */     return (Class[])AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Class[] run() {
/* 1313 */         ArrayList localArrayList = new ArrayList();
/* 1314 */         Class localClass = Class.this;
/* 1315 */         while (localClass != null) {
/* 1316 */           Class[] arrayOfClass = localClass.getDeclaredClasses();
/* 1317 */           for (int i = 0; i < arrayOfClass.length; i++) {
/* 1318 */             if (Modifier.isPublic(arrayOfClass[i].getModifiers())) {
/* 1319 */               localArrayList.add(arrayOfClass[i]);
/*      */             }
/*      */           }
/* 1322 */           localClass = localClass.getSuperclass();
/*      */         }
/* 1324 */         return (Class[])localArrayList.toArray(new Class[0]);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public Field[] getFields()
/*      */     throws SecurityException
/*      */   {
/* 1377 */     checkMemberAccess(0, ClassLoader.getCallerClassLoader(), true);
/* 1378 */     return copyFields(privateGetPublicFields(null));
/*      */   }
/*      */ 
/*      */   public Method[] getMethods()
/*      */     throws SecurityException
/*      */   {
/* 1428 */     checkMemberAccess(0, ClassLoader.getCallerClassLoader(), true);
/* 1429 */     return copyMethods(privateGetPublicMethods());
/*      */   }
/*      */ 
/*      */   public Constructor<?>[] getConstructors()
/*      */     throws SecurityException
/*      */   {
/* 1477 */     checkMemberAccess(0, ClassLoader.getCallerClassLoader(), true);
/* 1478 */     return copyConstructors(privateGetDeclaredConstructors(true));
/*      */   }
/*      */ 
/*      */   public Field getField(String paramString)
/*      */     throws NoSuchFieldException, SecurityException
/*      */   {
/* 1536 */     checkMemberAccess(0, ClassLoader.getCallerClassLoader(), true);
/* 1537 */     Field localField = getField0(paramString);
/* 1538 */     if (localField == null) {
/* 1539 */       throw new NoSuchFieldException(paramString);
/*      */     }
/* 1541 */     return localField;
/*      */   }
/*      */ 
/*      */   public Method getMethod(String paramString, Class<?>[] paramArrayOfClass)
/*      */     throws NoSuchMethodException, SecurityException
/*      */   {
/* 1621 */     checkMemberAccess(0, ClassLoader.getCallerClassLoader(), true);
/* 1622 */     Method localMethod = getMethod0(paramString, paramArrayOfClass);
/* 1623 */     if (localMethod == null) {
/* 1624 */       throw new NoSuchMethodException(getName() + "." + paramString + argumentTypesToString(paramArrayOfClass));
/*      */     }
/* 1626 */     return localMethod;
/*      */   }
/*      */ 
/*      */   public Constructor<T> getConstructor(Class<?>[] paramArrayOfClass)
/*      */     throws NoSuchMethodException, SecurityException
/*      */   {
/* 1675 */     checkMemberAccess(0, ClassLoader.getCallerClassLoader(), true);
/* 1676 */     return getConstructor0(paramArrayOfClass, 0);
/*      */   }
/*      */ 
/*      */   public Class<?>[] getDeclaredClasses()
/*      */     throws SecurityException
/*      */   {
/* 1717 */     checkMemberAccess(1, ClassLoader.getCallerClassLoader(), false);
/* 1718 */     return getDeclaredClasses0();
/*      */   }
/*      */ 
/*      */   public Field[] getDeclaredFields()
/*      */     throws SecurityException
/*      */   {
/* 1761 */     checkMemberAccess(1, ClassLoader.getCallerClassLoader(), true);
/* 1762 */     return copyFields(privateGetDeclaredFields(false));
/*      */   }
/*      */ 
/*      */   public Method[] getDeclaredMethods()
/*      */     throws SecurityException
/*      */   {
/* 1809 */     checkMemberAccess(1, ClassLoader.getCallerClassLoader(), true);
/* 1810 */     return copyMethods(privateGetDeclaredMethods(false));
/*      */   }
/*      */ 
/*      */   public Constructor<?>[] getDeclaredConstructors()
/*      */     throws SecurityException
/*      */   {
/* 1854 */     checkMemberAccess(1, ClassLoader.getCallerClassLoader(), true);
/* 1855 */     return copyConstructors(privateGetDeclaredConstructors(false));
/*      */   }
/*      */ 
/*      */   public Field getDeclaredField(String paramString)
/*      */     throws NoSuchFieldException, SecurityException
/*      */   {
/* 1898 */     checkMemberAccess(1, ClassLoader.getCallerClassLoader(), true);
/* 1899 */     Field localField = searchFields(privateGetDeclaredFields(false), paramString);
/* 1900 */     if (localField == null) {
/* 1901 */       throw new NoSuchFieldException(paramString);
/*      */     }
/* 1903 */     return localField;
/*      */   }
/*      */ 
/*      */   public Method getDeclaredMethod(String paramString, Class<?>[] paramArrayOfClass)
/*      */     throws NoSuchMethodException, SecurityException
/*      */   {
/* 1953 */     checkMemberAccess(1, ClassLoader.getCallerClassLoader(), true);
/* 1954 */     Method localMethod = searchMethods(privateGetDeclaredMethods(false), paramString, paramArrayOfClass);
/* 1955 */     if (localMethod == null) {
/* 1956 */       throw new NoSuchMethodException(getName() + "." + paramString + argumentTypesToString(paramArrayOfClass));
/*      */     }
/* 1958 */     return localMethod;
/*      */   }
/*      */ 
/*      */   public Constructor<T> getDeclaredConstructor(Class<?>[] paramArrayOfClass)
/*      */     throws NoSuchMethodException, SecurityException
/*      */   {
/* 2003 */     checkMemberAccess(1, ClassLoader.getCallerClassLoader(), true);
/* 2004 */     return getConstructor0(paramArrayOfClass, 1);
/*      */   }
/*      */ 
/*      */   public InputStream getResourceAsStream(String paramString)
/*      */   {
/* 2043 */     paramString = resolveName(paramString);
/* 2044 */     ClassLoader localClassLoader = getClassLoader0();
/* 2045 */     if (localClassLoader == null)
/*      */     {
/* 2047 */       return ClassLoader.getSystemResourceAsStream(paramString);
/*      */     }
/* 2049 */     return localClassLoader.getResourceAsStream(paramString);
/*      */   }
/*      */ 
/*      */   public URL getResource(String paramString)
/*      */   {
/* 2087 */     paramString = resolveName(paramString);
/* 2088 */     ClassLoader localClassLoader = getClassLoader0();
/* 2089 */     if (localClassLoader == null)
/*      */     {
/* 2091 */       return ClassLoader.getSystemResource(paramString);
/*      */     }
/* 2093 */     return localClassLoader.getResource(paramString);
/*      */   }
/*      */ 
/*      */   public ProtectionDomain getProtectionDomain()
/*      */   {
/* 2123 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 2124 */     if (localSecurityManager != null) {
/* 2125 */       localSecurityManager.checkPermission(SecurityConstants.GET_PD_PERMISSION);
/*      */     }
/* 2127 */     ProtectionDomain localProtectionDomain = getProtectionDomain0();
/* 2128 */     if (localProtectionDomain == null) {
/* 2129 */       if (allPermDomain == null) {
/* 2130 */         Permissions localPermissions = new Permissions();
/*      */ 
/* 2132 */         localPermissions.add(SecurityConstants.ALL_PERMISSION);
/* 2133 */         allPermDomain = new ProtectionDomain(null, localPermissions);
/*      */       }
/*      */ 
/* 2136 */       localProtectionDomain = allPermDomain;
/*      */     }
/* 2138 */     return localProtectionDomain;
/*      */   }
/*      */ 
/*      */   private native ProtectionDomain getProtectionDomain0();
/*      */ 
/*      */   native void setProtectionDomain0(ProtectionDomain paramProtectionDomain);
/*      */ 
/*      */   static native Class getPrimitiveClass(String paramString);
/*      */ 
/*      */   private void checkMemberAccess(int paramInt, ClassLoader paramClassLoader, boolean paramBoolean)
/*      */   {
/* 2174 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 2175 */     if (localSecurityManager != null) {
/* 2176 */       localSecurityManager.checkMemberAccess(this, paramInt);
/* 2177 */       ClassLoader localClassLoader = getClassLoader0();
/* 2178 */       if (ReflectUtil.needsPackageAccessCheck(paramClassLoader, localClassLoader)) {
/* 2179 */         String str1 = getName();
/* 2180 */         int i = str1.lastIndexOf('.');
/* 2181 */         if (i != -1)
/*      */         {
/* 2183 */           String str2 = str1.substring(0, i);
/* 2184 */           if ((!Proxy.isProxyClass(this)) || (!str2.equals("com.sun.proxy"))) {
/* 2185 */             localSecurityManager.checkPackageAccess(str2);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2190 */       if ((paramBoolean) && (Proxy.isProxyClass(this)))
/* 2191 */         ReflectUtil.checkProxyPackageAccess(paramClassLoader, getInterfaces());
/*      */     }
/*      */   }
/*      */ 
/*      */   private String resolveName(String paramString)
/*      */   {
/* 2201 */     if (paramString == null) {
/* 2202 */       return paramString;
/*      */     }
/* 2204 */     if (!paramString.startsWith("/")) {
/* 2205 */       Class localClass = this;
/* 2206 */       while (localClass.isArray()) {
/* 2207 */         localClass = localClass.getComponentType();
/*      */       }
/* 2209 */       String str = localClass.getName();
/* 2210 */       int i = str.lastIndexOf('.');
/* 2211 */       if (i != -1)
/* 2212 */         paramString = str.substring(0, i).replace('.', '/') + "/" + paramString;
/*      */     }
/*      */     else
/*      */     {
/* 2216 */       paramString = paramString.substring(1);
/*      */     }
/* 2218 */     return paramString;
/*      */   }
/*      */ 
/*      */   private void clearCachesOnClassRedefinition()
/*      */   {
/* 2248 */     if (this.lastRedefinedCount != this.classRedefinedCount) {
/* 2249 */       this.declaredFields = (this.publicFields = this.declaredPublicFields = null);
/* 2250 */       this.declaredMethods = (this.publicMethods = this.declaredPublicMethods = null);
/* 2251 */       this.declaredConstructors = (this.publicConstructors = null);
/* 2252 */       this.annotations = (this.declaredAnnotations = null);
/*      */ 
/* 2259 */       this.lastRedefinedCount = this.classRedefinedCount;
/*      */     }
/*      */   }
/*      */ 
/*      */   private native String getGenericSignature();
/*      */ 
/*      */   private GenericsFactory getFactory()
/*      */   {
/* 2272 */     return CoreReflectionFactory.make(this, ClassScope.make(this));
/*      */   }
/*      */ 
/*      */   private ClassRepository getGenericInfo()
/*      */   {
/* 2278 */     if (this.genericInfo == null)
/*      */     {
/* 2280 */       this.genericInfo = ClassRepository.make(getGenericSignature(), getFactory());
/*      */     }
/*      */ 
/* 2283 */     return this.genericInfo;
/*      */   }
/*      */ 
/*      */   private native byte[] getRawAnnotations();
/*      */ 
/*      */   native ConstantPool getConstantPool();
/*      */ 
/*      */   private Field[] privateGetDeclaredFields(boolean paramBoolean)
/*      */   {
/* 2301 */     checkInitted();
/* 2302 */     Field[] arrayOfField = null;
/* 2303 */     if (useCaches) {
/* 2304 */       clearCachesOnClassRedefinition();
/* 2305 */       if (paramBoolean) {
/* 2306 */         if (this.declaredPublicFields != null) {
/* 2307 */           arrayOfField = (Field[])this.declaredPublicFields.get();
/*      */         }
/*      */       }
/* 2310 */       else if (this.declaredFields != null) {
/* 2311 */         arrayOfField = (Field[])this.declaredFields.get();
/*      */       }
/*      */ 
/* 2314 */       if (arrayOfField != null) return arrayOfField;
/*      */     }
/*      */ 
/* 2317 */     arrayOfField = Reflection.filterFields(this, getDeclaredFields0(paramBoolean));
/* 2318 */     if (useCaches) {
/* 2319 */       if (paramBoolean)
/* 2320 */         this.declaredPublicFields = new SoftReference(arrayOfField);
/*      */       else {
/* 2322 */         this.declaredFields = new SoftReference(arrayOfField);
/*      */       }
/*      */     }
/* 2325 */     return arrayOfField;
/*      */   }
/*      */ 
/*      */   private Field[] privateGetPublicFields(Set<Class<?>> paramSet)
/*      */   {
/* 2332 */     checkInitted();
/* 2333 */     Field[] arrayOfField1 = null;
/* 2334 */     if (useCaches) {
/* 2335 */       clearCachesOnClassRedefinition();
/* 2336 */       if (this.publicFields != null) {
/* 2337 */         arrayOfField1 = (Field[])this.publicFields.get();
/*      */       }
/* 2339 */       if (arrayOfField1 != null) return arrayOfField1;
/*      */ 
/*      */     }
/*      */ 
/* 2344 */     ArrayList localArrayList = new ArrayList();
/* 2345 */     if (paramSet == null) {
/* 2346 */       paramSet = new HashSet();
/*      */     }
/*      */ 
/* 2350 */     Field[] arrayOfField2 = privateGetDeclaredFields(true);
/* 2351 */     addAll(localArrayList, arrayOfField2);
/*      */ 
/* 2354 */     for (Object localObject2 : getInterfaces()) {
/* 2355 */       if (!paramSet.contains(localObject2)) {
/* 2356 */         paramSet.add(localObject2);
/* 2357 */         addAll(localArrayList, localObject2.privateGetPublicFields(paramSet));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2362 */     if (!isInterface()) {
/* 2363 */       ??? = getSuperclass();
/* 2364 */       if (??? != null) {
/* 2365 */         addAll(localArrayList, ((Class)???).privateGetPublicFields(paramSet));
/*      */       }
/*      */     }
/*      */ 
/* 2369 */     arrayOfField1 = new Field[localArrayList.size()];
/* 2370 */     localArrayList.toArray(arrayOfField1);
/* 2371 */     if (useCaches) {
/* 2372 */       this.publicFields = new SoftReference(arrayOfField1);
/*      */     }
/* 2374 */     return arrayOfField1;
/*      */   }
/*      */ 
/*      */   private static void addAll(Collection<Field> paramCollection, Field[] paramArrayOfField) {
/* 2378 */     for (int i = 0; i < paramArrayOfField.length; i++)
/* 2379 */       paramCollection.add(paramArrayOfField[i]);
/*      */   }
/*      */ 
/*      */   private Constructor<T>[] privateGetDeclaredConstructors(boolean paramBoolean)
/*      */   {
/* 2394 */     checkInitted();
/* 2395 */     Constructor[] arrayOfConstructor = null;
/* 2396 */     if (useCaches) {
/* 2397 */       clearCachesOnClassRedefinition();
/* 2398 */       if (paramBoolean) {
/* 2399 */         if (this.publicConstructors != null) {
/* 2400 */           arrayOfConstructor = (Constructor[])this.publicConstructors.get();
/*      */         }
/*      */       }
/* 2403 */       else if (this.declaredConstructors != null) {
/* 2404 */         arrayOfConstructor = (Constructor[])this.declaredConstructors.get();
/*      */       }
/*      */ 
/* 2407 */       if (arrayOfConstructor != null) return arrayOfConstructor;
/*      */     }
/*      */ 
/* 2410 */     if (isInterface())
/* 2411 */       arrayOfConstructor = new Constructor[0];
/*      */     else {
/* 2413 */       arrayOfConstructor = getDeclaredConstructors0(paramBoolean);
/*      */     }
/* 2415 */     if (useCaches) {
/* 2416 */       if (paramBoolean)
/* 2417 */         this.publicConstructors = new SoftReference(arrayOfConstructor);
/*      */       else {
/* 2419 */         this.declaredConstructors = new SoftReference(arrayOfConstructor);
/*      */       }
/*      */     }
/* 2422 */     return arrayOfConstructor;
/*      */   }
/*      */ 
/*      */   private Method[] privateGetDeclaredMethods(boolean paramBoolean)
/*      */   {
/* 2435 */     checkInitted();
/* 2436 */     Method[] arrayOfMethod = null;
/* 2437 */     if (useCaches) {
/* 2438 */       clearCachesOnClassRedefinition();
/* 2439 */       if (paramBoolean) {
/* 2440 */         if (this.declaredPublicMethods != null) {
/* 2441 */           arrayOfMethod = (Method[])this.declaredPublicMethods.get();
/*      */         }
/*      */       }
/* 2444 */       else if (this.declaredMethods != null) {
/* 2445 */         arrayOfMethod = (Method[])this.declaredMethods.get();
/*      */       }
/*      */ 
/* 2448 */       if (arrayOfMethod != null) return arrayOfMethod;
/*      */     }
/*      */ 
/* 2451 */     arrayOfMethod = Reflection.filterMethods(this, getDeclaredMethods0(paramBoolean));
/* 2452 */     if (useCaches) {
/* 2453 */       if (paramBoolean)
/* 2454 */         this.declaredPublicMethods = new SoftReference(arrayOfMethod);
/*      */       else {
/* 2456 */         this.declaredMethods = new SoftReference(arrayOfMethod);
/*      */       }
/*      */     }
/* 2459 */     return arrayOfMethod;
/*      */   }
/*      */ 
/*      */   private Method[] privateGetPublicMethods()
/*      */   {
/* 2557 */     checkInitted();
/* 2558 */     Method[] arrayOfMethod = null;
/* 2559 */     if (useCaches) {
/* 2560 */       clearCachesOnClassRedefinition();
/* 2561 */       if (this.publicMethods != null) {
/* 2562 */         arrayOfMethod = (Method[])this.publicMethods.get();
/*      */       }
/* 2564 */       if (arrayOfMethod != null) return arrayOfMethod;
/*      */ 
/*      */     }
/*      */ 
/* 2569 */     MethodArray localMethodArray = new MethodArray();
/*      */ 
/* 2571 */     Object localObject1 = privateGetDeclaredMethods(true);
/* 2572 */     localMethodArray.addAll((Method[])localObject1);
/*      */ 
/* 2578 */     localObject1 = new MethodArray();
/* 2579 */     Class[] arrayOfClass = getInterfaces();
/* 2580 */     for (int i = 0; i < arrayOfClass.length; i++)
/* 2581 */       ((MethodArray)localObject1).addAll(arrayOfClass[i].privateGetPublicMethods());
/*      */     Object localObject2;
/* 2583 */     if (!isInterface()) {
/* 2584 */       Class localClass = getSuperclass();
/* 2585 */       if (localClass != null) {
/* 2586 */         localObject2 = new MethodArray();
/* 2587 */         ((MethodArray)localObject2).addAll(localClass.privateGetPublicMethods());
/*      */ 
/* 2590 */         for (int k = 0; k < ((MethodArray)localObject2).length(); k++) {
/* 2591 */           Method localMethod = ((MethodArray)localObject2).get(k);
/* 2592 */           if ((localMethod != null) && (!Modifier.isAbstract(localMethod.getModifiers()))) {
/* 2593 */             ((MethodArray)localObject1).removeByNameAndSignature(localMethod);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2599 */         ((MethodArray)localObject2).addAll((MethodArray)localObject1);
/* 2600 */         localObject1 = localObject2;
/*      */       }
/*      */     }
/*      */ 
/* 2604 */     for (int j = 0; j < localMethodArray.length(); j++) {
/* 2605 */       localObject2 = localMethodArray.get(j);
/* 2606 */       ((MethodArray)localObject1).removeByNameAndSignature((Method)localObject2);
/*      */     }
/* 2608 */     localMethodArray.addAllIfNotPresent((MethodArray)localObject1);
/* 2609 */     localMethodArray.compactAndTrim();
/* 2610 */     arrayOfMethod = localMethodArray.getArray();
/* 2611 */     if (useCaches) {
/* 2612 */       this.publicMethods = new SoftReference(arrayOfMethod);
/*      */     }
/* 2614 */     return arrayOfMethod;
/*      */   }
/*      */ 
/*      */   private Field searchFields(Field[] paramArrayOfField, String paramString)
/*      */   {
/* 2623 */     String str = paramString.intern();
/* 2624 */     for (int i = 0; i < paramArrayOfField.length; i++) {
/* 2625 */       if (paramArrayOfField[i].getName() == str) {
/* 2626 */         return getReflectionFactory().copyField(paramArrayOfField[i]);
/*      */       }
/*      */     }
/* 2629 */     return null;
/*      */   }
/*      */ 
/*      */   private Field getField0(String paramString)
/*      */     throws NoSuchFieldException
/*      */   {
/* 2640 */     Field localField = null;
/*      */ 
/* 2642 */     if ((localField = searchFields(privateGetDeclaredFields(true), paramString)) != null) {
/* 2643 */       return localField;
/*      */     }
/*      */ 
/* 2646 */     Class[] arrayOfClass = getInterfaces();
/* 2647 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 2648 */       Class localClass2 = arrayOfClass[i];
/* 2649 */       if ((localField = localClass2.getField0(paramString)) != null) {
/* 2650 */         return localField;
/*      */       }
/*      */     }
/*      */ 
/* 2654 */     if (!isInterface()) {
/* 2655 */       Class localClass1 = getSuperclass();
/* 2656 */       if ((localClass1 != null) && 
/* 2657 */         ((localField = localClass1.getField0(paramString)) != null)) {
/* 2658 */         return localField;
/*      */       }
/*      */     }
/*      */ 
/* 2662 */     return null;
/*      */   }
/*      */ 
/*      */   private static Method searchMethods(Method[] paramArrayOfMethod, String paramString, Class<?>[] paramArrayOfClass)
/*      */   {
/* 2669 */     Object localObject = null;
/* 2670 */     String str = paramString.intern();
/* 2671 */     for (int i = 0; i < paramArrayOfMethod.length; i++) {
/* 2672 */       Method localMethod = paramArrayOfMethod[i];
/* 2673 */       if ((localMethod.getName() == str) && (arrayContentsEq(paramArrayOfClass, localMethod.getParameterTypes())) && ((localObject == null) || (localObject.getReturnType().isAssignableFrom(localMethod.getReturnType()))))
/*      */       {
/* 2677 */         localObject = localMethod;
/*      */       }
/*      */     }
/* 2680 */     return localObject == null ? localObject : getReflectionFactory().copyMethod(localObject);
/*      */   }
/*      */ 
/*      */   private Method getMethod0(String paramString, Class<?>[] paramArrayOfClass)
/*      */   {
/* 2692 */     Method localMethod = null;
/*      */ 
/* 2694 */     if ((localMethod = searchMethods(privateGetDeclaredMethods(true), paramString, paramArrayOfClass)) != null)
/*      */     {
/* 2697 */       return localMethod;
/*      */     }
/*      */ 
/* 2700 */     if (!isInterface()) {
/* 2701 */       localObject1 = getSuperclass();
/* 2702 */       if ((localObject1 != null) && 
/* 2703 */         ((localMethod = ((Class)localObject1).getMethod0(paramString, paramArrayOfClass)) != null)) {
/* 2704 */         return localMethod;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2709 */     Object localObject1 = getInterfaces();
/* 2710 */     for (int i = 0; i < localObject1.length; i++) {
/* 2711 */       Object localObject2 = localObject1[i];
/* 2712 */       if ((localMethod = localObject2.getMethod0(paramString, paramArrayOfClass)) != null) {
/* 2713 */         return localMethod;
/*      */       }
/*      */     }
/*      */ 
/* 2717 */     return null;
/*      */   }
/*      */ 
/*      */   private Constructor<T> getConstructor0(Class<?>[] paramArrayOfClass, int paramInt)
/*      */     throws NoSuchMethodException
/*      */   {
/* 2723 */     Constructor[] arrayOfConstructor1 = privateGetDeclaredConstructors(paramInt == 0);
/* 2724 */     for (Constructor localConstructor : arrayOfConstructor1) {
/* 2725 */       if (arrayContentsEq(paramArrayOfClass, localConstructor.getParameterTypes()))
/*      */       {
/* 2727 */         return getReflectionFactory().copyConstructor(localConstructor);
/*      */       }
/*      */     }
/* 2730 */     throw new NoSuchMethodException(getName() + ".<init>" + argumentTypesToString(paramArrayOfClass));
/*      */   }
/*      */ 
/*      */   private static boolean arrayContentsEq(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2)
/*      */   {
/* 2738 */     if (paramArrayOfObject1 == null) {
/* 2739 */       return (paramArrayOfObject2 == null) || (paramArrayOfObject2.length == 0);
/*      */     }
/*      */ 
/* 2742 */     if (paramArrayOfObject2 == null) {
/* 2743 */       return paramArrayOfObject1.length == 0;
/*      */     }
/*      */ 
/* 2746 */     if (paramArrayOfObject1.length != paramArrayOfObject2.length) {
/* 2747 */       return false;
/*      */     }
/*      */ 
/* 2750 */     for (int i = 0; i < paramArrayOfObject1.length; i++) {
/* 2751 */       if (paramArrayOfObject1[i] != paramArrayOfObject2[i]) {
/* 2752 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 2756 */     return true;
/*      */   }
/*      */ 
/*      */   private static Field[] copyFields(Field[] paramArrayOfField) {
/* 2760 */     Field[] arrayOfField = new Field[paramArrayOfField.length];
/* 2761 */     ReflectionFactory localReflectionFactory = getReflectionFactory();
/* 2762 */     for (int i = 0; i < paramArrayOfField.length; i++) {
/* 2763 */       arrayOfField[i] = localReflectionFactory.copyField(paramArrayOfField[i]);
/*      */     }
/* 2765 */     return arrayOfField;
/*      */   }
/*      */ 
/*      */   private static Method[] copyMethods(Method[] paramArrayOfMethod) {
/* 2769 */     Method[] arrayOfMethod = new Method[paramArrayOfMethod.length];
/* 2770 */     ReflectionFactory localReflectionFactory = getReflectionFactory();
/* 2771 */     for (int i = 0; i < paramArrayOfMethod.length; i++) {
/* 2772 */       arrayOfMethod[i] = localReflectionFactory.copyMethod(paramArrayOfMethod[i]);
/*      */     }
/* 2774 */     return arrayOfMethod;
/*      */   }
/*      */ 
/*      */   private static <U> Constructor<U>[] copyConstructors(Constructor<U>[] paramArrayOfConstructor) {
/* 2778 */     Constructor[] arrayOfConstructor = (Constructor[])paramArrayOfConstructor.clone();
/* 2779 */     ReflectionFactory localReflectionFactory = getReflectionFactory();
/* 2780 */     for (int i = 0; i < arrayOfConstructor.length; i++) {
/* 2781 */       arrayOfConstructor[i] = localReflectionFactory.copyConstructor(arrayOfConstructor[i]);
/*      */     }
/* 2783 */     return arrayOfConstructor; } 
/*      */   private native Field[] getDeclaredFields0(boolean paramBoolean);
/*      */ 
/*      */   private native Method[] getDeclaredMethods0(boolean paramBoolean);
/*      */ 
/*      */   private native Constructor<T>[] getDeclaredConstructors0(boolean paramBoolean);
/*      */ 
/*      */   private native Class<?>[] getDeclaredClasses0();
/*      */ 
/* 2792 */   private static String argumentTypesToString(Class<?>[] paramArrayOfClass) { StringBuilder localStringBuilder = new StringBuilder();
/* 2793 */     localStringBuilder.append("(");
/* 2794 */     if (paramArrayOfClass != null) {
/* 2795 */       for (int i = 0; i < paramArrayOfClass.length; i++) {
/* 2796 */         if (i > 0) {
/* 2797 */           localStringBuilder.append(", ");
/*      */         }
/* 2799 */         Class<?> localClass = paramArrayOfClass[i];
/* 2800 */         localStringBuilder.append(localClass == null ? "null" : localClass.getName());
/*      */       }
/*      */     }
/* 2803 */     localStringBuilder.append(")");
/* 2804 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   public boolean desiredAssertionStatus()
/*      */   {
/* 2856 */     ClassLoader localClassLoader = getClassLoader();
/*      */ 
/* 2858 */     if (localClassLoader == null) {
/* 2859 */       return desiredAssertionStatus0(this);
/*      */     }
/*      */ 
/* 2863 */     synchronized (localClassLoader.assertionLock) {
/* 2864 */       if (localClassLoader.classAssertionStatus != null) {
/* 2865 */         return localClassLoader.desiredAssertionStatus(getName());
/*      */       }
/*      */     }
/* 2868 */     return desiredAssertionStatus0(this);
/*      */   }
/*      */ 
/*      */   private static native boolean desiredAssertionStatus0(Class<?> paramClass);
/*      */ 
/*      */   public boolean isEnum()
/*      */   {
/* 2886 */     return ((getModifiers() & 0x4000) != 0) && (getSuperclass() == Enum.class);
/*      */   }
/*      */ 
/*      */   private static ReflectionFactory getReflectionFactory()
/*      */   {
/* 2892 */     if (reflectionFactory == null) {
/* 2893 */       reflectionFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
/*      */     }
/*      */ 
/* 2897 */     return reflectionFactory;
/*      */   }
/*      */ 
/*      */   private static void checkInitted()
/*      */   {
/* 2904 */     if (initted) return;
/* 2905 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Void run()
/*      */       {
/* 2916 */         if (System.out == null)
/*      */         {
/* 2918 */           return null;
/*      */         }
/*      */ 
/* 2921 */         String str = System.getProperty("sun.reflect.noCaches");
/*      */ 
/* 2923 */         if ((str != null) && (str.equals("true"))) {
/* 2924 */           Class.access$202(false);
/*      */         }
/*      */ 
/* 2927 */         Class.access$302(true);
/* 2928 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public T[] getEnumConstants()
/*      */   {
/* 2944 */     Object[] arrayOfObject = getEnumConstantsShared();
/* 2945 */     return arrayOfObject != null ? (Object[])arrayOfObject.clone() : null;
/*      */   }
/*      */ 
/*      */   T[] getEnumConstantsShared()
/*      */   {
/* 2955 */     if (this.enumConstants == null) {
/* 2956 */       if (!isEnum()) return null; try
/*      */       {
/* 2958 */         final Method localMethod = getMethod("values", new Class[0]);
/* 2959 */         AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Void run() {
/* 2962 */             localMethod.setAccessible(true);
/* 2963 */             return null;
/*      */           }
/*      */         });
/* 2966 */         this.enumConstants = ((Object[])localMethod.invoke(null, new Object[0]));
/*      */       }
/*      */       catch (InvocationTargetException localInvocationTargetException)
/*      */       {
/* 2970 */         return null; } catch (NoSuchMethodException localNoSuchMethodException) {
/* 2971 */         return null; } catch (IllegalAccessException localIllegalAccessException) {
/* 2972 */         return null;
/*      */       }
/*      */     }
/* 2974 */     return this.enumConstants;
/*      */   }
/*      */ 
/*      */   Map<String, T> enumConstantDirectory()
/*      */   {
/* 2986 */     if (this.enumConstantDirectory == null) {
/* 2987 */       Object[] arrayOfObject1 = getEnumConstantsShared();
/* 2988 */       if (arrayOfObject1 == null) {
/* 2989 */         throw new IllegalArgumentException(getName() + " is not an enum type");
/*      */       }
/* 2991 */       HashMap localHashMap = new HashMap(2 * arrayOfObject1.length);
/* 2992 */       for (Object localObject : arrayOfObject1)
/* 2993 */         localHashMap.put(((Enum)localObject).name(), localObject);
/* 2994 */       this.enumConstantDirectory = localHashMap;
/*      */     }
/* 2996 */     return this.enumConstantDirectory;
/*      */   }
/*      */ 
/*      */   public T cast(Object paramObject)
/*      */   {
/* 3013 */     if ((paramObject != null) && (!isInstance(paramObject)))
/* 3014 */       throw new ClassCastException(cannotCastMsg(paramObject));
/* 3015 */     return paramObject;
/*      */   }
/*      */ 
/*      */   private String cannotCastMsg(Object paramObject) {
/* 3019 */     return "Cannot cast " + paramObject.getClass().getName() + " to " + getName();
/*      */   }
/*      */ 
/*      */   public <U> Class<? extends U> asSubclass(Class<U> paramClass)
/*      */   {
/* 3043 */     if (paramClass.isAssignableFrom(this)) {
/* 3044 */       return this;
/*      */     }
/* 3046 */     throw new ClassCastException(toString());
/*      */   }
/*      */ 
/*      */   public <A extends Annotation> A getAnnotation(Class<A> paramClass)
/*      */   {
/* 3054 */     if (paramClass == null) {
/* 3055 */       throw new NullPointerException();
/*      */     }
/* 3057 */     initAnnotationsIfNecessary();
/* 3058 */     return (Annotation)this.annotations.get(paramClass);
/*      */   }
/*      */ 
/*      */   public boolean isAnnotationPresent(Class<? extends Annotation> paramClass)
/*      */   {
/* 3067 */     if (paramClass == null) {
/* 3068 */       throw new NullPointerException();
/*      */     }
/* 3070 */     return getAnnotation(paramClass) != null;
/*      */   }
/*      */ 
/*      */   public Annotation[] getAnnotations()
/*      */   {
/* 3078 */     initAnnotationsIfNecessary();
/* 3079 */     return AnnotationParser.toArray(this.annotations);
/*      */   }
/*      */ 
/*      */   public Annotation[] getDeclaredAnnotations()
/*      */   {
/* 3086 */     initAnnotationsIfNecessary();
/* 3087 */     return AnnotationParser.toArray(this.declaredAnnotations);
/*      */   }
/*      */ 
/*      */   private synchronized void initAnnotationsIfNecessary()
/*      */   {
/* 3095 */     clearCachesOnClassRedefinition();
/* 3096 */     if (this.annotations != null)
/* 3097 */       return;
/* 3098 */     this.declaredAnnotations = AnnotationParser.parseAnnotations(getRawAnnotations(), getConstantPool(), this);
/*      */ 
/* 3100 */     Class localClass1 = getSuperclass();
/* 3101 */     if (localClass1 == null) {
/* 3102 */       this.annotations = this.declaredAnnotations;
/*      */     } else {
/* 3104 */       this.annotations = new HashMap();
/* 3105 */       localClass1.initAnnotationsIfNecessary();
/* 3106 */       for (Map.Entry localEntry : localClass1.annotations.entrySet()) {
/* 3107 */         Class localClass2 = (Class)localEntry.getKey();
/* 3108 */         if (AnnotationType.getInstance(localClass2).isInherited())
/* 3109 */           this.annotations.put(localClass2, localEntry.getValue());
/*      */       }
/* 3111 */       this.annotations.putAll(this.declaredAnnotations);
/*      */     }
/*      */   }
/*      */ 
/*      */   void setAnnotationType(AnnotationType paramAnnotationType)
/*      */   {
/* 3120 */     this.annotationType = paramAnnotationType;
/*      */   }
/*      */ 
/*      */   AnnotationType getAnnotationType() {
/* 3124 */     return this.annotationType;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  128 */     registerNatives();
/*      */   }
/*      */ 
/*      */   private static final class EnclosingMethodInfo
/*      */   {
/*      */     private Class<?> enclosingClass;
/*      */     private String name;
/*      */     private String descriptor;
/*      */ 
/*      */     private EnclosingMethodInfo(Object[] paramArrayOfObject)
/*      */     {
/*  965 */       if (paramArrayOfObject.length != 3) {
/*  966 */         throw new InternalError("Malformed enclosing method information");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  971 */         this.enclosingClass = ((Class)paramArrayOfObject[0]);
/*  972 */         assert (this.enclosingClass != null);
/*      */ 
/*  976 */         this.name = ((String)paramArrayOfObject[1]);
/*      */ 
/*  980 */         this.descriptor = ((String)paramArrayOfObject[2]);
/*  981 */         if ((!$assertionsDisabled) && ((this.name == null) || (this.descriptor == null)) && (this.name != this.descriptor)) throw new AssertionError(); 
/*      */       }
/*  983 */       catch (ClassCastException localClassCastException) { throw new InternalError("Invalid type in enclosing method information"); }
/*      */     }
/*      */ 
/*      */     boolean isPartial()
/*      */     {
/*  988 */       return (this.enclosingClass == null) || (this.name == null) || (this.descriptor == null);
/*      */     }
/*      */     boolean isConstructor() {
/*  991 */       return (!isPartial()) && ("<init>".equals(this.name));
/*      */     }
/*  993 */     boolean isMethod() { return (!isPartial()) && (!isConstructor()) && (!"<clinit>".equals(this.name)); } 
/*      */     Class<?> getEnclosingClass() {
/*  995 */       return this.enclosingClass;
/*      */     }
/*  997 */     String getName() { return this.name; } 
/*      */     String getDescriptor() {
/*  999 */       return this.descriptor;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class MethodArray
/*      */   {
/*      */     private Method[] methods;
/*      */     private int length;
/*      */ 
/*      */     MethodArray()
/*      */     {
/* 2467 */       this.methods = new Method[20];
/* 2468 */       this.length = 0;
/*      */     }
/*      */ 
/*      */     void add(Method paramMethod) {
/* 2472 */       if (this.length == this.methods.length) {
/* 2473 */         this.methods = ((Method[])Arrays.copyOf(this.methods, 2 * this.methods.length));
/*      */       }
/* 2475 */       this.methods[(this.length++)] = paramMethod;
/*      */     }
/*      */ 
/*      */     void addAll(Method[] paramArrayOfMethod) {
/* 2479 */       for (int i = 0; i < paramArrayOfMethod.length; i++)
/* 2480 */         add(paramArrayOfMethod[i]);
/*      */     }
/*      */ 
/*      */     void addAll(MethodArray paramMethodArray)
/*      */     {
/* 2485 */       for (int i = 0; i < paramMethodArray.length(); i++)
/* 2486 */         add(paramMethodArray.get(i));
/*      */     }
/*      */ 
/*      */     void addIfNotPresent(Method paramMethod)
/*      */     {
/* 2491 */       for (int i = 0; i < this.length; i++) {
/* 2492 */         Method localMethod = this.methods[i];
/* 2493 */         if ((localMethod == paramMethod) || ((localMethod != null) && (localMethod.equals(paramMethod)))) {
/* 2494 */           return;
/*      */         }
/*      */       }
/* 2497 */       add(paramMethod);
/*      */     }
/*      */ 
/*      */     void addAllIfNotPresent(MethodArray paramMethodArray) {
/* 2501 */       for (int i = 0; i < paramMethodArray.length(); i++) {
/* 2502 */         Method localMethod = paramMethodArray.get(i);
/* 2503 */         if (localMethod != null)
/* 2504 */           addIfNotPresent(localMethod);
/*      */       }
/*      */     }
/*      */ 
/*      */     int length()
/*      */     {
/* 2510 */       return this.length;
/*      */     }
/*      */ 
/*      */     Method get(int paramInt) {
/* 2514 */       return this.methods[paramInt];
/*      */     }
/*      */ 
/*      */     void removeByNameAndSignature(Method paramMethod) {
/* 2518 */       for (int i = 0; i < this.length; i++) {
/* 2519 */         Method localMethod = this.methods[i];
/* 2520 */         if ((localMethod != null) && (localMethod.getReturnType() == paramMethod.getReturnType()) && (localMethod.getName() == paramMethod.getName()) && (Class.arrayContentsEq(localMethod.getParameterTypes(), paramMethod.getParameterTypes())))
/*      */         {
/* 2525 */           this.methods[i] = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     void compactAndTrim() {
/* 2531 */       int i = 0;
/*      */ 
/* 2533 */       for (int j = 0; j < this.length; j++) {
/* 2534 */         Method localMethod = this.methods[j];
/* 2535 */         if (localMethod != null) {
/* 2536 */           if (j != i) {
/* 2537 */             this.methods[i] = localMethod;
/*      */           }
/* 2539 */           i++;
/*      */         }
/*      */       }
/* 2542 */       if (i != this.methods.length)
/* 2543 */         this.methods = ((Method[])Arrays.copyOf(this.methods, i));
/*      */     }
/*      */ 
/*      */     Method[] getArray()
/*      */     {
/* 2548 */       return this.methods;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.Class
 * JD-Core Version:    0.6.2
 */