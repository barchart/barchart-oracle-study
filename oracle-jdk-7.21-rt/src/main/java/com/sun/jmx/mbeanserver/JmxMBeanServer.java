/*      */ package com.sun.jmx.mbeanserver;
/*      */ 
/*      */ import com.sun.jmx.defaults.JmxProperties;
/*      */ import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.management.Attribute;
/*      */ import javax.management.AttributeList;
/*      */ import javax.management.AttributeNotFoundException;
/*      */ import javax.management.InstanceAlreadyExistsException;
/*      */ import javax.management.InstanceNotFoundException;
/*      */ import javax.management.IntrospectionException;
/*      */ import javax.management.InvalidAttributeValueException;
/*      */ import javax.management.ListenerNotFoundException;
/*      */ import javax.management.MBeanException;
/*      */ import javax.management.MBeanInfo;
/*      */ import javax.management.MBeanPermission;
/*      */ import javax.management.MBeanRegistrationException;
/*      */ import javax.management.MBeanServer;
/*      */ import javax.management.MBeanServerDelegate;
/*      */ import javax.management.MBeanServerPermission;
/*      */ import javax.management.NotCompliantMBeanException;
/*      */ import javax.management.NotificationFilter;
/*      */ import javax.management.NotificationListener;
/*      */ import javax.management.ObjectInstance;
/*      */ import javax.management.ObjectName;
/*      */ import javax.management.OperationsException;
/*      */ import javax.management.QueryExp;
/*      */ import javax.management.ReflectionException;
/*      */ import javax.management.RuntimeOperationsException;
/*      */ import javax.management.loading.ClassLoaderRepository;
/*      */ 
/*      */ public final class JmxMBeanServer
/*      */   implements SunJmxMBeanServer
/*      */ {
/*      */   public static final boolean DEFAULT_FAIR_LOCK_POLICY = true;
/*      */   private final MBeanInstantiator instantiator;
/*      */   private final SecureClassLoaderRepository secureClr;
/*      */   private final boolean interceptorsEnabled;
/*      */   private final MBeanServer outerShell;
/*  107 */   private volatile MBeanServer mbsInterceptor = null;
/*      */   private final MBeanServerDelegate mBeanServerDelegateObject;
/*      */ 
/*      */   JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate)
/*      */   {
/*  138 */     this(paramString, paramMBeanServer, paramMBeanServerDelegate, null, false);
/*      */   }
/*      */ 
/*      */   JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, boolean paramBoolean)
/*      */   {
/*  170 */     this(paramString, paramMBeanServer, paramMBeanServerDelegate, null, false);
/*      */   }
/*      */ 
/*      */   JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, MBeanInstantiator paramMBeanInstantiator, boolean paramBoolean)
/*      */   {
/*  195 */     this(paramString, paramMBeanServer, paramMBeanServerDelegate, paramMBeanInstantiator, paramBoolean, true);
/*      */   }
/*      */ 
/*      */   JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, MBeanInstantiator paramMBeanInstantiator, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  225 */     if (paramMBeanInstantiator == null)
/*      */     {
/*  227 */       localObject = new ClassLoaderRepositorySupport();
/*  228 */       paramMBeanInstantiator = new MBeanInstantiator((ModifiableClassLoaderRepository)localObject);
/*      */     }
/*  230 */     this.secureClr = new SecureClassLoaderRepository(paramMBeanInstantiator.getClassLoaderRepository());
/*      */ 
/*  232 */     if (paramMBeanServerDelegate == null)
/*  233 */       paramMBeanServerDelegate = new MBeanServerDelegateImpl();
/*  234 */     if (paramMBeanServer == null) {
/*  235 */       paramMBeanServer = this;
/*      */     }
/*  237 */     this.instantiator = paramMBeanInstantiator;
/*  238 */     this.mBeanServerDelegateObject = paramMBeanServerDelegate;
/*  239 */     this.outerShell = paramMBeanServer;
/*      */ 
/*  241 */     Object localObject = new Repository(paramString);
/*  242 */     this.mbsInterceptor = new DefaultMBeanServerInterceptor(paramMBeanServer, paramMBeanServerDelegate, paramMBeanInstantiator, (Repository)localObject);
/*      */ 
/*  245 */     this.interceptorsEnabled = paramBoolean1;
/*  246 */     initialize();
/*      */   }
/*      */ 
/*      */   public boolean interceptorsEnabled()
/*      */   {
/*  257 */     return this.interceptorsEnabled;
/*      */   }
/*      */ 
/*      */   public MBeanInstantiator getMBeanInstantiator()
/*      */   {
/*  268 */     if (this.interceptorsEnabled) return this.instantiator;
/*  269 */     throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
/*      */   }
/*      */ 
/*      */   public ObjectInstance createMBean(String paramString, ObjectName paramObjectName)
/*      */     throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException
/*      */   {
/*  317 */     return this.mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName), (Object[])null, (String[])null);
/*      */   }
/*      */ 
/*      */   public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2)
/*      */     throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException
/*      */   {
/*  370 */     return this.mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName1), paramObjectName2, (Object[])null, (String[])null);
/*      */   }
/*      */ 
/*      */   public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*      */     throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException
/*      */   {
/*  424 */     return this.mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName), paramArrayOfObject, paramArrayOfString);
/*      */   }
/*      */ 
/*      */   public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*      */     throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException
/*      */   {
/*  479 */     return this.mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName1), paramObjectName2, paramArrayOfObject, paramArrayOfString);
/*      */   }
/*      */ 
/*      */   public ObjectInstance registerMBean(Object paramObject, ObjectName paramObjectName)
/*      */     throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
/*      */   {
/*  513 */     return this.mbsInterceptor.registerMBean(paramObject, cloneObjectName(paramObjectName));
/*      */   }
/*      */ 
/*      */   public void unregisterMBean(ObjectName paramObjectName)
/*      */     throws InstanceNotFoundException, MBeanRegistrationException
/*      */   {
/*  537 */     this.mbsInterceptor.unregisterMBean(cloneObjectName(paramObjectName));
/*      */   }
/*      */ 
/*      */   public ObjectInstance getObjectInstance(ObjectName paramObjectName)
/*      */     throws InstanceNotFoundException
/*      */   {
/*  555 */     return this.mbsInterceptor.getObjectInstance(cloneObjectName(paramObjectName));
/*      */   }
/*      */ 
/*      */   public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp)
/*      */   {
/*  583 */     return this.mbsInterceptor.queryMBeans(cloneObjectName(paramObjectName), paramQueryExp);
/*      */   }
/*      */ 
/*      */   public Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp)
/*      */   {
/*  610 */     return this.mbsInterceptor.queryNames(cloneObjectName(paramObjectName), paramQueryExp);
/*      */   }
/*      */ 
/*      */   public boolean isRegistered(ObjectName paramObjectName)
/*      */   {
/*  629 */     return this.mbsInterceptor.isRegistered(paramObjectName);
/*      */   }
/*      */ 
/*      */   public Integer getMBeanCount()
/*      */   {
/*  637 */     return this.mbsInterceptor.getMBeanCount();
/*      */   }
/*      */ 
/*      */   public Object getAttribute(ObjectName paramObjectName, String paramString)
/*      */     throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException
/*      */   {
/*  669 */     return this.mbsInterceptor.getAttribute(cloneObjectName(paramObjectName), paramString);
/*      */   }
/*      */ 
/*      */   public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString)
/*      */     throws InstanceNotFoundException, ReflectionException
/*      */   {
/*  696 */     return this.mbsInterceptor.getAttributes(cloneObjectName(paramObjectName), paramArrayOfString);
/*      */   }
/*      */ 
/*      */   public void setAttribute(ObjectName paramObjectName, Attribute paramAttribute)
/*      */     throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*      */   {
/*  730 */     this.mbsInterceptor.setAttribute(cloneObjectName(paramObjectName), cloneAttribute(paramAttribute));
/*      */   }
/*      */ 
/*      */   public AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList)
/*      */     throws InstanceNotFoundException, ReflectionException
/*      */   {
/*  759 */     return this.mbsInterceptor.setAttributes(cloneObjectName(paramObjectName), cloneAttributeList(paramAttributeList));
/*      */   }
/*      */ 
/*      */   public Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*      */     throws InstanceNotFoundException, MBeanException, ReflectionException
/*      */   {
/*  792 */     return this.mbsInterceptor.invoke(cloneObjectName(paramObjectName), paramString, paramArrayOfObject, paramArrayOfString);
/*      */   }
/*      */ 
/*      */   public String getDefaultDomain()
/*      */   {
/*  802 */     return this.mbsInterceptor.getDefaultDomain();
/*      */   }
/*      */ 
/*      */   public String[] getDomains()
/*      */   {
/*  807 */     return this.mbsInterceptor.getDomains();
/*      */   }
/*      */ 
/*      */   public void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */     throws InstanceNotFoundException
/*      */   {
/*  830 */     this.mbsInterceptor.addNotificationListener(cloneObjectName(paramObjectName), paramNotificationListener, paramNotificationFilter, paramObject);
/*      */   }
/*      */ 
/*      */   public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */     throws InstanceNotFoundException
/*      */   {
/*  853 */     this.mbsInterceptor.addNotificationListener(cloneObjectName(paramObjectName1), paramObjectName2, paramNotificationFilter, paramObject);
/*      */   }
/*      */ 
/*      */   public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener)
/*      */     throws InstanceNotFoundException, ListenerNotFoundException
/*      */   {
/*  861 */     this.mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName), paramNotificationListener);
/*      */   }
/*      */ 
/*      */   public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */     throws InstanceNotFoundException, ListenerNotFoundException
/*      */   {
/*  871 */     this.mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName), paramNotificationListener, paramNotificationFilter, paramObject);
/*      */   }
/*      */ 
/*      */   public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2)
/*      */     throws InstanceNotFoundException, ListenerNotFoundException
/*      */   {
/*  879 */     this.mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName1), paramObjectName2);
/*      */   }
/*      */ 
/*      */   public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */     throws InstanceNotFoundException, ListenerNotFoundException
/*      */   {
/*  889 */     this.mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName1), paramObjectName2, paramNotificationFilter, paramObject);
/*      */   }
/*      */ 
/*      */   public MBeanInfo getMBeanInfo(ObjectName paramObjectName)
/*      */     throws InstanceNotFoundException, IntrospectionException, ReflectionException
/*      */   {
/*  911 */     return this.mbsInterceptor.getMBeanInfo(cloneObjectName(paramObjectName));
/*      */   }
/*      */ 
/*      */   public Object instantiate(String paramString)
/*      */     throws ReflectionException, MBeanException
/*      */   {
/*  941 */     checkMBeanPermission(paramString, null, null, "instantiate");
/*      */ 
/*  943 */     return this.instantiator.instantiate(paramString);
/*      */   }
/*      */ 
/*      */   public Object instantiate(String paramString, ObjectName paramObjectName)
/*      */     throws ReflectionException, MBeanException, InstanceNotFoundException
/*      */   {
/*  978 */     checkMBeanPermission(paramString, null, null, "instantiate");
/*      */ 
/*  980 */     ClassLoader localClassLoader = this.outerShell.getClass().getClassLoader();
/*  981 */     return this.instantiator.instantiate(paramString, paramObjectName, localClassLoader);
/*      */   }
/*      */ 
/*      */   public Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*      */     throws ReflectionException, MBeanException
/*      */   {
/* 1016 */     checkMBeanPermission(paramString, null, null, "instantiate");
/*      */ 
/* 1018 */     ClassLoader localClassLoader = this.outerShell.getClass().getClassLoader();
/* 1019 */     return this.instantiator.instantiate(paramString, paramArrayOfObject, paramArrayOfString, localClassLoader);
/*      */   }
/*      */ 
/*      */   public Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*      */     throws ReflectionException, MBeanException, InstanceNotFoundException
/*      */   {
/* 1059 */     checkMBeanPermission(paramString, null, null, "instantiate");
/*      */ 
/* 1061 */     ClassLoader localClassLoader = this.outerShell.getClass().getClassLoader();
/* 1062 */     return this.instantiator.instantiate(paramString, paramObjectName, paramArrayOfObject, paramArrayOfString, localClassLoader);
/*      */   }
/*      */ 
/*      */   public boolean isInstanceOf(ObjectName paramObjectName, String paramString)
/*      */     throws InstanceNotFoundException
/*      */   {
/* 1082 */     return this.mbsInterceptor.isInstanceOf(cloneObjectName(paramObjectName), paramString);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectInputStream deserialize(ObjectName paramObjectName, byte[] paramArrayOfByte)
/*      */     throws InstanceNotFoundException, OperationsException
/*      */   {
/* 1107 */     ClassLoader localClassLoader = getClassLoaderFor(paramObjectName);
/*      */ 
/* 1109 */     return this.instantiator.deserialize(localClassLoader, paramArrayOfByte);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectInputStream deserialize(String paramString, byte[] paramArrayOfByte)
/*      */     throws OperationsException, ReflectionException
/*      */   {
/* 1132 */     if (paramString == null) {
/* 1133 */       throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter");
/*      */     }
/*      */ 
/* 1140 */     ClassLoaderRepository localClassLoaderRepository = getClassLoaderRepository();
/*      */     Class localClass;
/*      */     try
/*      */     {
/* 1144 */       if (localClassLoaderRepository == null) throw new ClassNotFoundException(paramString);
/* 1145 */       localClass = localClassLoaderRepository.loadClass(paramString);
/*      */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 1147 */       throw new ReflectionException(localClassNotFoundException, "The given class could not be loaded by the default loader repository");
/*      */     }
/*      */ 
/* 1153 */     return this.instantiator.deserialize(localClass.getClassLoader(), paramArrayOfByte);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte)
/*      */     throws InstanceNotFoundException, OperationsException, ReflectionException
/*      */   {
/* 1188 */     paramObjectName = cloneObjectName(paramObjectName);
/*      */     try
/*      */     {
/* 1194 */       getClassLoader(paramObjectName);
/*      */     } catch (SecurityException localSecurityException) {
/* 1196 */       throw localSecurityException;
/*      */     }
/*      */     catch (Exception localException) {
/*      */     }
/* 1200 */     ClassLoader localClassLoader = this.outerShell.getClass().getClassLoader();
/* 1201 */     return this.instantiator.deserialize(paramString, paramObjectName, paramArrayOfByte, localClassLoader);
/*      */   }
/*      */ 
/*      */   private void initialize()
/*      */   {
/* 1209 */     if (this.instantiator == null) throw new IllegalStateException("instantiator must not be null.");
/*      */ 
/*      */     try
/*      */     {
/* 1214 */       AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*      */         public Object run() throws Exception {
/* 1216 */           JmxMBeanServer.this.mbsInterceptor.registerMBean(JmxMBeanServer.this.mBeanServerDelegateObject, MBeanServerDelegate.DELEGATE_NAME);
/*      */ 
/* 1219 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (SecurityException localSecurityException) {
/* 1223 */       if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
/* 1224 */         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", "Unexpected security exception occurred", localSecurityException);
/*      */       }
/*      */ 
/* 1228 */       throw localSecurityException;
/*      */     } catch (Exception localException) {
/* 1230 */       if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
/* 1231 */         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", "Unexpected exception occurred", localException);
/*      */       }
/*      */ 
/* 1235 */       throw new IllegalStateException("Can't register delegate.", localException);
/*      */     }
/*      */ 
/* 1244 */     ClassLoader localClassLoader1 = this.outerShell.getClass().getClassLoader();
/* 1245 */     ModifiableClassLoaderRepository localModifiableClassLoaderRepository = this.instantiator.getClassLoaderRepository();
/*      */ 
/* 1247 */     if (localModifiableClassLoaderRepository != null) {
/* 1248 */       localModifiableClassLoaderRepository.addClassLoader(localClassLoader1);
/*      */ 
/* 1265 */       ClassLoader localClassLoader2 = ClassLoader.getSystemClassLoader();
/* 1266 */       if (localClassLoader2 != localClassLoader1)
/* 1267 */         localModifiableClassLoaderRepository.addClassLoader(localClassLoader2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized MBeanServer getMBeanServerInterceptor()
/*      */   {
/* 1279 */     if (this.interceptorsEnabled) return this.mbsInterceptor;
/* 1280 */     throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
/*      */   }
/*      */ 
/*      */   public synchronized void setMBeanServerInterceptor(MBeanServer paramMBeanServer)
/*      */   {
/* 1293 */     if (!this.interceptorsEnabled) throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
/*      */ 
/* 1295 */     if (paramMBeanServer == null) throw new IllegalArgumentException("MBeanServerInterceptor is null");
/*      */ 
/* 1297 */     this.mbsInterceptor = paramMBeanServer;
/*      */   }
/*      */ 
/*      */   public ClassLoader getClassLoaderFor(ObjectName paramObjectName)
/*      */     throws InstanceNotFoundException
/*      */   {
/* 1309 */     return this.mbsInterceptor.getClassLoaderFor(cloneObjectName(paramObjectName));
/*      */   }
/*      */ 
/*      */   public ClassLoader getClassLoader(ObjectName paramObjectName)
/*      */     throws InstanceNotFoundException
/*      */   {
/* 1321 */     return this.mbsInterceptor.getClassLoader(cloneObjectName(paramObjectName));
/*      */   }
/*      */ 
/*      */   public ClassLoaderRepository getClassLoaderRepository()
/*      */   {
/* 1330 */     checkMBeanPermission(null, null, null, "getClassLoaderRepository");
/* 1331 */     return this.secureClr;
/*      */   }
/*      */ 
/*      */   public MBeanServerDelegate getMBeanServerDelegate() {
/* 1335 */     if (!this.interceptorsEnabled) throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
/*      */ 
/* 1337 */     return this.mBeanServerDelegateObject;
/*      */   }
/*      */ 
/*      */   public static MBeanServerDelegate newMBeanServerDelegate()
/*      */   {
/* 1359 */     return new MBeanServerDelegateImpl();
/*      */   }
/*      */ 
/*      */   public static MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, boolean paramBoolean)
/*      */   {
/* 1413 */     checkNewMBeanServerPermission();
/*      */ 
/* 1422 */     return new JmxMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, null, paramBoolean, true);
/*      */   }
/*      */ 
/*      */   private ObjectName cloneObjectName(ObjectName paramObjectName)
/*      */   {
/* 1433 */     if (paramObjectName != null) {
/* 1434 */       return ObjectName.getInstance(paramObjectName);
/*      */     }
/* 1436 */     return paramObjectName;
/*      */   }
/*      */ 
/*      */   private Attribute cloneAttribute(Attribute paramAttribute)
/*      */   {
/* 1443 */     if ((paramAttribute != null) && 
/* 1444 */       (!paramAttribute.getClass().equals(Attribute.class))) {
/* 1445 */       return new Attribute(paramAttribute.getName(), paramAttribute.getValue());
/*      */     }
/*      */ 
/* 1448 */     return paramAttribute;
/*      */   }
/*      */ 
/*      */   private AttributeList cloneAttributeList(AttributeList paramAttributeList)
/*      */   {
/* 1455 */     if (paramAttributeList != null) {
/* 1456 */       List localList = paramAttributeList.asList();
/*      */       Object localObject;
/* 1457 */       if (!paramAttributeList.getClass().equals(AttributeList.class))
/*      */       {
/* 1460 */         AttributeList localAttributeList = new AttributeList(localList.size());
/*      */ 
/* 1464 */         for (localObject = localList.iterator(); ((Iterator)localObject).hasNext(); ) { Attribute localAttribute = (Attribute)((Iterator)localObject).next();
/* 1465 */           localAttributeList.add(cloneAttribute(localAttribute)); }
/* 1466 */         return localAttributeList;
/*      */       }
/*      */ 
/* 1470 */       for (int i = 0; i < localList.size(); i++) {
/* 1471 */         localObject = (Attribute)localList.get(i);
/* 1472 */         if (!localObject.getClass().equals(Attribute.class)) {
/* 1473 */           paramAttributeList.set(i, cloneAttribute((Attribute)localObject));
/*      */         }
/*      */       }
/* 1476 */       return paramAttributeList;
/*      */     }
/*      */ 
/* 1479 */     return paramAttributeList;
/*      */   }
/*      */ 
/*      */   private static void checkMBeanPermission(String paramString1, String paramString2, ObjectName paramObjectName, String paramString3)
/*      */     throws SecurityException
/*      */   {
/* 1490 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1491 */     if (localSecurityManager != null) {
/* 1492 */       MBeanPermission localMBeanPermission = new MBeanPermission(paramString1, paramString2, paramObjectName, paramString3);
/*      */ 
/* 1496 */       localSecurityManager.checkPermission(localMBeanPermission);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void checkNewMBeanServerPermission() {
/* 1501 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 1502 */     if (localSecurityManager != null) {
/* 1503 */       MBeanServerPermission localMBeanServerPermission = new MBeanServerPermission("newMBeanServer");
/* 1504 */       localSecurityManager.checkPermission(localMBeanServerPermission);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.JmxMBeanServer
 * JD-Core Version:    0.6.2
 */