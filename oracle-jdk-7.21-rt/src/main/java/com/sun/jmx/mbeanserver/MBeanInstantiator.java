/*     */ package com.sun.jmx.mbeanserver;
/*     */ 
/*     */ import com.sun.jmx.defaults.JmxProperties;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.management.InstanceNotFoundException;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanPermission;
/*     */ import javax.management.NotCompliantMBeanException;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.OperationsException;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeErrorException;
/*     */ import javax.management.RuntimeMBeanException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import sun.reflect.misc.ConstructorUtil;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ 
/*     */ public class MBeanInstantiator
/*     */ {
/*     */   private final ModifiableClassLoaderRepository clr;
/* 724 */   private static final Map<String, Class<?>> primitiveClasses = Util.newMap();
/*     */ 
/*     */   MBeanInstantiator(ModifiableClassLoaderRepository paramModifiableClassLoaderRepository)
/*     */   {
/*  66 */     this.clr = paramModifiableClassLoaderRepository;
/*     */   }
/*     */ 
/*     */   public void testCreation(Class<?> paramClass)
/*     */     throws NotCompliantMBeanException
/*     */   {
/*  76 */     Introspector.testCreation(paramClass);
/*     */   }
/*     */ 
/*     */   public Class<?> findClassWithDefaultLoaderRepository(String paramString)
/*     */     throws ReflectionException
/*     */   {
/*  87 */     if (paramString == null) {
/*  88 */       throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation");
/*     */     }
/*     */ 
/*  93 */     ReflectUtil.checkPackageAccess(paramString);
/*     */     Class localClass;
/*     */     try {
/*  95 */       if (this.clr == null) throw new ClassNotFoundException(paramString);
/*  96 */       localClass = this.clr.loadClass(paramString);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/*  99 */       throw new ReflectionException(localClassNotFoundException, "The MBean class could not be loaded by the default loader repository");
/*     */     }
/*     */ 
/* 103 */     return localClass;
/*     */   }
/*     */ 
/*     */   public Class<?> findClass(String paramString, ClassLoader paramClassLoader)
/*     */     throws ReflectionException
/*     */   {
/* 114 */     return loadClass(paramString, paramClassLoader);
/*     */   }
/*     */ 
/*     */   public Class<?> findClass(String paramString, ObjectName paramObjectName)
/*     */     throws ReflectionException, InstanceNotFoundException
/*     */   {
/* 124 */     if (paramObjectName == null) {
/* 125 */       throw new RuntimeOperationsException(new IllegalArgumentException(), "Null loader passed in parameter");
/*     */     }
/*     */ 
/* 129 */     ClassLoader localClassLoader = null;
/* 130 */     synchronized (this) {
/* 131 */       if (this.clr != null)
/* 132 */         localClassLoader = this.clr.getClassLoader(paramObjectName);
/*     */     }
/* 134 */     if (localClassLoader == null) {
/* 135 */       throw new InstanceNotFoundException("The loader named " + paramObjectName + " is not registered in the MBeanServer");
/*     */     }
/*     */ 
/* 138 */     return findClass(paramString, localClassLoader);
/*     */   }
/*     */ 
/*     */   public Class<?>[] findSignatureClasses(String[] paramArrayOfString, ClassLoader paramClassLoader)
/*     */     throws ReflectionException
/*     */   {
/* 150 */     if (paramArrayOfString == null) return null;
/* 151 */     ClassLoader localClassLoader = paramClassLoader;
/* 152 */     int i = paramArrayOfString.length;
/* 153 */     Class[] arrayOfClass = new Class[i];
/*     */ 
/* 155 */     if (i == 0) return arrayOfClass; try
/*     */     {
/* 157 */       for (int j = 0; j < i; j++)
/*     */       {
/* 162 */         Class localClass = (Class)primitiveClasses.get(paramArrayOfString[j]);
/* 163 */         if (localClass != null) {
/* 164 */           arrayOfClass[j] = localClass;
/*     */         }
/*     */         else
/*     */         {
/* 168 */           ReflectUtil.checkPackageAccess(paramArrayOfString[j]);
/*     */ 
/* 172 */           if (localClassLoader != null)
/*     */           {
/* 176 */             arrayOfClass[j] = Class.forName(paramArrayOfString[j], false, localClassLoader);
/*     */           }
/*     */           else
/*     */           {
/* 180 */             arrayOfClass[j] = findClass(paramArrayOfString[j], getClass().getClassLoader());
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 185 */       if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
/* 186 */         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "The parameter class could not be found", localClassNotFoundException);
/*     */       }
/*     */ 
/* 191 */       throw new ReflectionException(localClassNotFoundException, "The parameter class could not be found");
/*     */     }
/*     */     catch (RuntimeException localRuntimeException) {
/* 194 */       if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
/* 195 */         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "Unexpected exception", localRuntimeException);
/*     */       }
/*     */ 
/* 200 */       throw localRuntimeException;
/*     */     }
/* 202 */     return arrayOfClass;
/*     */   }
/*     */ 
/*     */   public Object instantiate(Class<?> paramClass)
/*     */     throws ReflectionException, MBeanException
/*     */   {
/* 213 */     checkMBeanPermission(paramClass, null, null, "instantiate");
/*     */ 
/* 219 */     Constructor localConstructor = findConstructor(paramClass, null);
/* 220 */     if (localConstructor == null) {
/* 221 */       throw new ReflectionException(new NoSuchMethodException("No such constructor"));
/*     */     }
/*     */     Object localObject;
/*     */     try
/*     */     {
/* 226 */       ReflectUtil.checkPackageAccess(paramClass);
/* 227 */       ensureClassAccess(paramClass);
/* 228 */       localObject = localConstructor.newInstance(new Object[0]);
/*     */     }
/*     */     catch (InvocationTargetException localInvocationTargetException) {
/* 231 */       Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 232 */       if ((localThrowable instanceof RuntimeException)) {
/* 233 */         throw new RuntimeMBeanException((RuntimeException)localThrowable, "RuntimeException thrown in the MBean's empty constructor");
/*     */       }
/* 235 */       if ((localThrowable instanceof Error)) {
/* 236 */         throw new RuntimeErrorException((Error)localThrowable, "Error thrown in the MBean's empty constructor");
/*     */       }
/*     */ 
/* 239 */       throw new MBeanException((Exception)localThrowable, "Exception thrown in the MBean's empty constructor");
/*     */     }
/*     */     catch (NoSuchMethodError localNoSuchMethodError)
/*     */     {
/* 243 */       throw new ReflectionException(new NoSuchMethodException("No constructor"), "No such constructor");
/*     */     }
/*     */     catch (InstantiationException localInstantiationException)
/*     */     {
/* 247 */       throw new ReflectionException(localInstantiationException, "Exception thrown trying to invoke the MBean's empty constructor");
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException) {
/* 250 */       throw new ReflectionException(localIllegalAccessException, "Exception thrown trying to invoke the MBean's empty constructor");
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException) {
/* 253 */       throw new ReflectionException(localIllegalArgumentException, "Exception thrown trying to invoke the MBean's empty constructor");
/*     */     }
/*     */ 
/* 256 */     return localObject;
/*     */   }
/*     */ 
/*     */   public Object instantiate(Class<?> paramClass, Object[] paramArrayOfObject, String[] paramArrayOfString, ClassLoader paramClassLoader)
/*     */     throws ReflectionException, MBeanException
/*     */   {
/* 271 */     checkMBeanPermission(paramClass, null, null, "instantiate");
/*     */     Class[] arrayOfClass;
/*     */     try
/*     */     {
/* 281 */       ClassLoader localClassLoader = paramClass.getClassLoader();
/*     */ 
/* 284 */       arrayOfClass = paramArrayOfString == null ? null : findSignatureClasses(paramArrayOfString, localClassLoader);
/*     */     }
/*     */     catch (IllegalArgumentException localIllegalArgumentException)
/*     */     {
/* 290 */       throw new ReflectionException(localIllegalArgumentException, "The constructor parameter classes could not be loaded");
/*     */     }
/*     */ 
/* 295 */     Constructor localConstructor = findConstructor(paramClass, arrayOfClass);
/*     */ 
/* 297 */     if (localConstructor == null)
/* 298 */       throw new ReflectionException(new NoSuchMethodException("No such constructor"));
/*     */     Object localObject;
/*     */     try
/*     */     {
/* 302 */       ReflectUtil.checkPackageAccess(paramClass);
/* 303 */       ensureClassAccess(paramClass);
/* 304 */       localObject = localConstructor.newInstance(paramArrayOfObject);
/*     */     }
/*     */     catch (NoSuchMethodError localNoSuchMethodError) {
/* 307 */       throw new ReflectionException(new NoSuchMethodException("No such constructor found"), "No such constructor");
/*     */     }
/*     */     catch (InstantiationException localInstantiationException)
/*     */     {
/* 312 */       throw new ReflectionException(localInstantiationException, "Exception thrown trying to invoke the MBean's constructor");
/*     */     }
/*     */     catch (IllegalAccessException localIllegalAccessException)
/*     */     {
/* 316 */       throw new ReflectionException(localIllegalAccessException, "Exception thrown trying to invoke the MBean's constructor");
/*     */     }
/*     */     catch (InvocationTargetException localInvocationTargetException)
/*     */     {
/* 321 */       Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 322 */       if ((localThrowable instanceof RuntimeException)) {
/* 323 */         throw new RuntimeMBeanException((RuntimeException)localThrowable, "RuntimeException thrown in the MBean's constructor");
/*     */       }
/* 325 */       if ((localThrowable instanceof Error)) {
/* 326 */         throw new RuntimeErrorException((Error)localThrowable, "Error thrown in the MBean's constructor");
/*     */       }
/*     */ 
/* 329 */       throw new MBeanException((Exception)localThrowable, "Exception thrown in the MBean's constructor");
/*     */     }
/*     */ 
/* 333 */     return localObject;
/*     */   }
/*     */ 
/*     */   public ObjectInputStream deserialize(ClassLoader paramClassLoader, byte[] paramArrayOfByte)
/*     */     throws OperationsException
/*     */   {
/* 351 */     if (paramArrayOfByte == null) {
/* 352 */       throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter");
/*     */     }
/*     */ 
/* 355 */     if (paramArrayOfByte.length == 0) {
/* 356 */       throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter");
/*     */     }
/*     */ 
/* 364 */     ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
/*     */     ObjectInputStreamWithLoader localObjectInputStreamWithLoader;
/*     */     try
/*     */     {
/* 366 */       localObjectInputStreamWithLoader = new ObjectInputStreamWithLoader(localByteArrayInputStream, paramClassLoader);
/*     */     } catch (IOException localIOException) {
/* 368 */       throw new OperationsException("An IOException occurred trying to de-serialize the data");
/*     */     }
/*     */ 
/* 372 */     return localObjectInputStreamWithLoader;
/*     */   }
/*     */ 
/*     */   public ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte, ClassLoader paramClassLoader)
/*     */     throws InstanceNotFoundException, OperationsException, ReflectionException
/*     */   {
/* 408 */     if (paramArrayOfByte == null) {
/* 409 */       throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter");
/*     */     }
/*     */ 
/* 412 */     if (paramArrayOfByte.length == 0) {
/* 413 */       throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter");
/*     */     }
/*     */ 
/* 416 */     if (paramString == null) {
/* 417 */       throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter");
/*     */     }
/*     */ 
/* 421 */     ReflectUtil.checkPackageAccess(paramString);
/*     */     Class localClass;
/* 423 */     if (paramObjectName == null)
/*     */     {
/* 425 */       localClass = findClass(paramString, paramClassLoader);
/*     */     }
/*     */     else {
/*     */       try
/*     */       {
/* 430 */         ClassLoader localClassLoader = null;
/*     */ 
/* 432 */         if (this.clr != null)
/* 433 */           localClassLoader = this.clr.getClassLoader(paramObjectName);
/* 434 */         if (localClassLoader == null)
/* 435 */           throw new ClassNotFoundException(paramString);
/* 436 */         localClass = Class.forName(paramString, false, localClassLoader);
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException) {
/* 439 */         throw new ReflectionException(localClassNotFoundException, "The MBean class could not be loaded by the " + paramObjectName.toString() + " class loader");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 449 */     ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
/*     */     ObjectInputStreamWithLoader localObjectInputStreamWithLoader;
/*     */     try
/*     */     {
/* 451 */       localObjectInputStreamWithLoader = new ObjectInputStreamWithLoader(localByteArrayInputStream, localClass.getClassLoader());
/*     */     }
/*     */     catch (IOException localIOException) {
/* 454 */       throw new OperationsException("An IOException occurred trying to de-serialize the data");
/*     */     }
/*     */ 
/* 458 */     return localObjectInputStreamWithLoader;
/*     */   }
/*     */ 
/*     */   public Object instantiate(String paramString)
/*     */     throws ReflectionException, MBeanException
/*     */   {
/* 488 */     return instantiate(paramString, (Object[])null, (String[])null, null);
/*     */   }
/*     */ 
/*     */   public Object instantiate(String paramString, ObjectName paramObjectName, ClassLoader paramClassLoader)
/*     */     throws ReflectionException, MBeanException, InstanceNotFoundException
/*     */   {
/* 524 */     return instantiate(paramString, paramObjectName, (Object[])null, (String[])null, paramClassLoader);
/*     */   }
/*     */ 
/*     */   public Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString, ClassLoader paramClassLoader)
/*     */     throws ReflectionException, MBeanException
/*     */   {
/* 562 */     Class localClass = findClassWithDefaultLoaderRepository(paramString);
/* 563 */     return instantiate(localClass, paramArrayOfObject, paramArrayOfString, paramClassLoader);
/*     */   }
/*     */ 
/*     */   public Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString, ClassLoader paramClassLoader)
/*     */     throws ReflectionException, MBeanException, InstanceNotFoundException
/*     */   {
/*     */     Class localClass;
/* 612 */     if (paramObjectName == null)
/* 613 */       localClass = findClass(paramString, paramClassLoader);
/*     */     else {
/* 615 */       localClass = findClass(paramString, paramObjectName);
/*     */     }
/* 617 */     return instantiate(localClass, paramArrayOfObject, paramArrayOfString, paramClassLoader);
/*     */   }
/*     */ 
/*     */   public ModifiableClassLoaderRepository getClassLoaderRepository()
/*     */   {
/* 625 */     return this.clr;
/*     */   }
/*     */ 
/*     */   static Class<?> loadClass(String paramString, ClassLoader paramClassLoader)
/*     */     throws ReflectionException
/*     */   {
/* 635 */     if (paramString == null) {
/* 636 */       throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation");
/* 640 */     }
/*     */ ReflectUtil.checkPackageAccess(paramString);
/*     */     Class localClass;
/*     */     try {
/* 642 */       if (paramClassLoader == null)
/* 643 */         paramClassLoader = MBeanInstantiator.class.getClassLoader();
/* 644 */       if (paramClassLoader != null)
/* 645 */         localClass = Class.forName(paramString, false, paramClassLoader);
/*     */       else
/* 647 */         localClass = Class.forName(paramString);
/*     */     }
/*     */     catch (ClassNotFoundException localClassNotFoundException) {
/* 650 */       throw new ReflectionException(localClassNotFoundException, "The MBean class could not be loaded");
/*     */     }
/*     */ 
/* 653 */     return localClass;
/*     */   }
/*     */ 
/*     */   static Class<?>[] loadSignatureClasses(String[] paramArrayOfString, ClassLoader paramClassLoader)
/*     */     throws ReflectionException
/*     */   {
/* 666 */     if (paramArrayOfString == null) return null;
/* 667 */     ClassLoader localClassLoader = paramClassLoader == null ? MBeanInstantiator.class.getClassLoader() : paramClassLoader;
/*     */ 
/* 669 */     int i = paramArrayOfString.length;
/* 670 */     Class[] arrayOfClass = new Class[i];
/*     */ 
/* 672 */     if (i == 0) return arrayOfClass; try
/*     */     {
/* 674 */       for (int j = 0; j < i; j++)
/*     */       {
/* 679 */         Class localClass = (Class)primitiveClasses.get(paramArrayOfString[j]);
/* 680 */         if (localClass != null) {
/* 681 */           arrayOfClass[j] = localClass;
/*     */         }
/*     */         else
/*     */         {
/* 691 */           ReflectUtil.checkPackageAccess(paramArrayOfString[j]);
/* 692 */           arrayOfClass[j] = Class.forName(paramArrayOfString[j], false, localClassLoader);
/*     */         }
/*     */       }
/*     */     } catch (ClassNotFoundException localClassNotFoundException) { if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
/* 696 */         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "The parameter class could not be found", localClassNotFoundException);
/*     */       }
/*     */ 
/* 701 */       throw new ReflectionException(localClassNotFoundException, "The parameter class could not be found");
/*     */     } catch (RuntimeException localRuntimeException)
/*     */     {
/* 704 */       if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
/* 705 */         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "Unexpected exception", localRuntimeException);
/*     */       }
/*     */ 
/* 710 */       throw localRuntimeException;
/*     */     }
/* 712 */     return arrayOfClass;
/*     */   }
/*     */ 
/*     */   private Constructor<?> findConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass) {
/*     */     try {
/* 717 */       return ConstructorUtil.getConstructor(paramClass, paramArrayOfClass); } catch (Exception localException) {
/*     */     }
/* 719 */     return null;
/*     */   }
/*     */ 
/*     */   private static void checkMBeanPermission(Class<?> paramClass, String paramString1, ObjectName paramObjectName, String paramString2)
/*     */   {
/* 736 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 737 */     if ((paramClass != null) && (localSecurityManager != null)) {
/* 738 */       MBeanPermission localMBeanPermission = new MBeanPermission(paramClass.getName(), paramString1, paramObjectName, paramString2);
/*     */ 
/* 742 */       localSecurityManager.checkPermission(localMBeanPermission);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void ensureClassAccess(Class paramClass)
/*     */     throws IllegalAccessException
/*     */   {
/* 749 */     int i = paramClass.getModifiers();
/* 750 */     if (!Modifier.isPublic(i))
/* 751 */       throw new IllegalAccessException("Class is not public and can't be instantiated");
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 726 */     for (Class localClass : new Class[] { Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Character.TYPE, Boolean.TYPE })
/*     */     {
/* 729 */       primitiveClasses.put(localClass.getName(), localClass);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.MBeanInstantiator
 * JD-Core Version:    0.6.2
 */