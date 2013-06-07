/*      */ package javax.management.remote.rmi;
/*      */ 
/*      */ import com.sun.jmx.mbeanserver.Util;
/*      */ import com.sun.jmx.remote.internal.ServerCommunicatorAdmin;
/*      */ import com.sun.jmx.remote.internal.ServerNotifForwarder;
/*      */ import com.sun.jmx.remote.security.JMXSubjectDomainCombiner;
/*      */ import com.sun.jmx.remote.security.SubjectDelegator;
/*      */ import com.sun.jmx.remote.util.ClassLoaderWithRepository;
/*      */ import com.sun.jmx.remote.util.ClassLogger;
/*      */ import com.sun.jmx.remote.util.EnvHelp;
/*      */ import com.sun.jmx.remote.util.OrderClassLoaders;
/*      */ import java.io.IOException;
/*      */ import java.rmi.MarshalledObject;
/*      */ import java.rmi.UnmarshalException;
/*      */ import java.rmi.server.Unreferenced;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.Permission;
/*      */ import java.security.Permissions;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
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
/*      */ import javax.management.NotCompliantMBeanException;
/*      */ import javax.management.NotificationFilter;
/*      */ import javax.management.ObjectInstance;
/*      */ import javax.management.ObjectName;
/*      */ import javax.management.QueryExp;
/*      */ import javax.management.ReflectionException;
/*      */ import javax.management.RuntimeOperationsException;
/*      */ import javax.management.remote.JMXServerErrorException;
/*      */ import javax.management.remote.NotificationResult;
/*      */ import javax.management.remote.TargetedNotification;
/*      */ import javax.security.auth.Subject;
/*      */ 
/*      */ public class RMIConnectionImpl
/*      */   implements RMIConnection, Unreferenced
/*      */ {
/* 1655 */   private static final Object[] NO_OBJECTS = new Object[0];
/* 1656 */   private static final String[] NO_STRINGS = new String[0];
/*      */   private final Subject subject;
/*      */   private final SubjectDelegator subjectDelegator;
/*      */   private final boolean removeCallerContext;
/*      */   private final AccessControlContext acc;
/*      */   private final RMIServerImpl rmiServer;
/*      */   private final MBeanServer mbeanServer;
/*      */   private final ClassLoader defaultClassLoader;
/*      */   private final ClassLoader defaultContextClassLoader;
/*      */   private final ClassLoaderWithRepository classLoaderWithRepository;
/* 1711 */   private boolean terminated = false;
/*      */   private final String connectionId;
/*      */   private final ServerCommunicatorAdmin serverCommunicatorAdmin;
/*      */   private static final int ADD_NOTIFICATION_LISTENERS = 1;
/*      */   private static final int ADD_NOTIFICATION_LISTENER_OBJECTNAME = 2;
/*      */   private static final int CREATE_MBEAN = 3;
/*      */   private static final int CREATE_MBEAN_PARAMS = 4;
/*      */   private static final int CREATE_MBEAN_LOADER = 5;
/*      */   private static final int CREATE_MBEAN_LOADER_PARAMS = 6;
/*      */   private static final int GET_ATTRIBUTE = 7;
/*      */   private static final int GET_ATTRIBUTES = 8;
/*      */   private static final int GET_DEFAULT_DOMAIN = 9;
/*      */   private static final int GET_DOMAINS = 10;
/*      */   private static final int GET_MBEAN_COUNT = 11;
/*      */   private static final int GET_MBEAN_INFO = 12;
/*      */   private static final int GET_OBJECT_INSTANCE = 13;
/*      */   private static final int INVOKE = 14;
/*      */   private static final int IS_INSTANCE_OF = 15;
/*      */   private static final int IS_REGISTERED = 16;
/*      */   private static final int QUERY_MBEANS = 17;
/*      */   private static final int QUERY_NAMES = 18;
/*      */   private static final int REMOVE_NOTIFICATION_LISTENER = 19;
/*      */   private static final int REMOVE_NOTIFICATION_LISTENER_OBJECTNAME = 20;
/*      */   private static final int REMOVE_NOTIFICATION_LISTENER_OBJECTNAME_FILTER_HANDBACK = 21;
/*      */   private static final int SET_ATTRIBUTE = 22;
/*      */   private static final int SET_ATTRIBUTES = 23;
/*      */   private static final int UNREGISTER_MBEAN = 24;
/*      */   private ServerNotifForwarder serverNotifForwarder;
/*      */   private Map<String, ?> env;
/* 1789 */   private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnectionImpl");
/*      */ 
/*      */   public RMIConnectionImpl(RMIServerImpl paramRMIServerImpl, String paramString, ClassLoader paramClassLoader, Subject paramSubject, Map<String, ?> paramMap)
/*      */   {
/*  126 */     if ((paramRMIServerImpl == null) || (paramString == null))
/*  127 */       throw new NullPointerException("Illegal null argument");
/*  128 */     if (paramMap == null)
/*  129 */       paramMap = Collections.emptyMap();
/*  130 */     this.rmiServer = paramRMIServerImpl;
/*  131 */     this.connectionId = paramString;
/*  132 */     this.defaultClassLoader = paramClassLoader;
/*      */ 
/*  134 */     this.subjectDelegator = new SubjectDelegator();
/*  135 */     this.subject = paramSubject;
/*  136 */     if (paramSubject == null) {
/*  137 */       this.acc = null;
/*  138 */       this.removeCallerContext = false;
/*      */     } else {
/*  140 */       this.removeCallerContext = SubjectDelegator.checkRemoveCallerContext(paramSubject);
/*      */ 
/*  142 */       if (this.removeCallerContext) {
/*  143 */         this.acc = JMXSubjectDomainCombiner.getDomainCombinerContext(paramSubject);
/*      */       }
/*      */       else {
/*  146 */         this.acc = JMXSubjectDomainCombiner.getContext(paramSubject);
/*      */       }
/*      */     }
/*      */ 
/*  150 */     this.mbeanServer = paramRMIServerImpl.getMBeanServer();
/*      */ 
/*  152 */     final ClassLoader localClassLoader = paramClassLoader;
/*      */ 
/*  154 */     this.classLoaderWithRepository = ((ClassLoaderWithRepository)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public ClassLoaderWithRepository run()
/*      */       {
/*  158 */         return new ClassLoaderWithRepository(RMIConnectionImpl.this.mbeanServer.getClassLoaderRepository(), localClassLoader);
/*      */       }
/*      */     }
/*      */     , withPermissions(new Permission[] { new MBeanPermission("*", "getClassLoaderRepository"), new RuntimePermission("createClassLoader") })));
/*      */ 
/*  169 */     this.defaultContextClassLoader = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public ClassLoader run()
/*      */       {
/*  174 */         return new RMIConnectionImpl.CombinedClassLoader(Thread.currentThread().getContextClassLoader(), localClassLoader, null);
/*      */       }
/*      */     }));
/*  179 */     this.serverCommunicatorAdmin = new RMIServerCommunicatorAdmin(EnvHelp.getServerConnectionTimeout(paramMap));
/*      */ 
/*  182 */     this.env = paramMap;
/*      */   }
/*      */ 
/*      */   private static AccessControlContext withPermissions(Permission[] paramArrayOfPermission) {
/*  186 */     Permissions localPermissions = new Permissions();
/*      */ 
/*  188 */     for (Permission localPermission : paramArrayOfPermission) {
/*  189 */       localPermissions.add(localPermission);
/*      */     }
/*      */ 
/*  192 */     ??? = new ProtectionDomain(null, localPermissions);
/*  193 */     return new AccessControlContext(new ProtectionDomain[] { ??? });
/*      */   }
/*      */ 
/*      */   private synchronized ServerNotifForwarder getServerNotifFwd()
/*      */   {
/*  199 */     if (this.serverNotifForwarder == null) {
/*  200 */       this.serverNotifForwarder = new ServerNotifForwarder(this.mbeanServer, this.env, this.rmiServer.getNotifBuffer(), this.connectionId);
/*      */     }
/*      */ 
/*  205 */     return this.serverNotifForwarder;
/*      */   }
/*      */ 
/*      */   public String getConnectionId() throws IOException
/*      */   {
/*  210 */     return this.connectionId;
/*      */   }
/*      */ 
/*      */   public void close() throws IOException {
/*  214 */     boolean bool = logger.debugOn();
/*  215 */     String str = bool ? "[" + toString() + "]" : null;
/*      */ 
/*  217 */     synchronized (this) {
/*  218 */       if (this.terminated) {
/*  219 */         if (bool) logger.debug("close", str + " already terminated.");
/*  220 */         return;
/*      */       }
/*      */ 
/*  223 */       if (bool) logger.debug("close", str + " closing.");
/*      */ 
/*  225 */       this.terminated = true;
/*      */ 
/*  227 */       if (this.serverCommunicatorAdmin != null) {
/*  228 */         this.serverCommunicatorAdmin.terminate();
/*      */       }
/*      */ 
/*  231 */       if (this.serverNotifForwarder != null) {
/*  232 */         this.serverNotifForwarder.terminate();
/*      */       }
/*      */     }
/*      */ 
/*  236 */     this.rmiServer.clientClosed(this);
/*      */ 
/*  238 */     if (bool) logger.debug("close", str + " closed."); 
/*      */   }
/*      */ 
/*      */   public void unreferenced()
/*      */   {
/*  242 */     logger.debug("unreferenced", "called");
/*      */     try {
/*  244 */       close();
/*  245 */       logger.debug("unreferenced", "done");
/*      */     } catch (IOException localIOException) {
/*  247 */       logger.fine("unreferenced", localIOException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Subject paramSubject)
/*      */     throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
/*      */   {
/*      */     try
/*      */     {
/*  266 */       Object[] arrayOfObject = { paramString, paramObjectName };
/*      */ 
/*  269 */       if (logger.debugOn()) {
/*  270 */         logger.debug("createMBean(String,ObjectName)", "connectionId=" + this.connectionId + ", className=" + paramString + ", name=" + paramObjectName);
/*      */       }
/*      */ 
/*  274 */       return (ObjectInstance)doPrivilegedOperation(3, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  280 */       Exception localException = extractException(localPrivilegedActionException);
/*  281 */       if ((localException instanceof ReflectionException))
/*  282 */         throw ((ReflectionException)localException);
/*  283 */       if ((localException instanceof InstanceAlreadyExistsException))
/*  284 */         throw ((InstanceAlreadyExistsException)localException);
/*  285 */       if ((localException instanceof MBeanRegistrationException))
/*  286 */         throw ((MBeanRegistrationException)localException);
/*  287 */       if ((localException instanceof MBeanException))
/*  288 */         throw ((MBeanException)localException);
/*  289 */       if ((localException instanceof NotCompliantMBeanException))
/*  290 */         throw ((NotCompliantMBeanException)localException);
/*  291 */       if ((localException instanceof IOException))
/*  292 */         throw ((IOException)localException);
/*  293 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject)
/*      */     throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
/*      */   {
/*      */     try
/*      */     {
/*  310 */       Object[] arrayOfObject = { paramString, paramObjectName1, paramObjectName2 };
/*      */ 
/*  313 */       if (logger.debugOn()) {
/*  314 */         logger.debug("createMBean(String,ObjectName,ObjectName)", "connectionId=" + this.connectionId + ", className=" + paramString + ", name=" + paramObjectName1 + ", loaderName=" + paramObjectName2);
/*      */       }
/*      */ 
/*  320 */       return (ObjectInstance)doPrivilegedOperation(5, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  326 */       Exception localException = extractException(localPrivilegedActionException);
/*  327 */       if ((localException instanceof ReflectionException))
/*  328 */         throw ((ReflectionException)localException);
/*  329 */       if ((localException instanceof InstanceAlreadyExistsException))
/*  330 */         throw ((InstanceAlreadyExistsException)localException);
/*  331 */       if ((localException instanceof MBeanRegistrationException))
/*  332 */         throw ((MBeanRegistrationException)localException);
/*  333 */       if ((localException instanceof MBeanException))
/*  334 */         throw ((MBeanException)localException);
/*  335 */       if ((localException instanceof NotCompliantMBeanException))
/*  336 */         throw ((NotCompliantMBeanException)localException);
/*  337 */       if ((localException instanceof InstanceNotFoundException))
/*  338 */         throw ((InstanceNotFoundException)localException);
/*  339 */       if ((localException instanceof IOException))
/*  340 */         throw ((IOException)localException);
/*  341 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject)
/*      */     throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
/*      */   {
/*  360 */     boolean bool = logger.debugOn();
/*      */ 
/*  362 */     if (bool) logger.debug("createMBean(String,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", unwrapping parameters using classLoaderWithRepository.");
/*      */ 
/*  367 */     Object[] arrayOfObject1 = nullIsEmpty((Object[])unwrap(paramMarshalledObject, this.classLoaderWithRepository, [Ljava.lang.Object.class));
/*      */     try
/*      */     {
/*  371 */       Object[] arrayOfObject2 = { paramString, paramObjectName, arrayOfObject1, nullIsEmpty(paramArrayOfString) };
/*      */ 
/*  375 */       if (bool) {
/*  376 */         logger.debug("createMBean(String,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", className=" + paramString + ", name=" + paramObjectName + ", params=" + objects(arrayOfObject1) + ", signature=" + strings(paramArrayOfString));
/*      */       }
/*      */ 
/*  383 */       return (ObjectInstance)doPrivilegedOperation(4, arrayOfObject2, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  389 */       Exception localException = extractException(localPrivilegedActionException);
/*  390 */       if ((localException instanceof ReflectionException))
/*  391 */         throw ((ReflectionException)localException);
/*  392 */       if ((localException instanceof InstanceAlreadyExistsException))
/*  393 */         throw ((InstanceAlreadyExistsException)localException);
/*  394 */       if ((localException instanceof MBeanRegistrationException))
/*  395 */         throw ((MBeanRegistrationException)localException);
/*  396 */       if ((localException instanceof MBeanException))
/*  397 */         throw ((MBeanException)localException);
/*  398 */       if ((localException instanceof NotCompliantMBeanException))
/*  399 */         throw ((NotCompliantMBeanException)localException);
/*  400 */       if ((localException instanceof IOException))
/*  401 */         throw ((IOException)localException);
/*  402 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject)
/*      */     throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
/*      */   {
/*  423 */     boolean bool = logger.debugOn();
/*      */ 
/*  425 */     if (bool) logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", unwrapping params with MBean extended ClassLoader.");
/*      */ 
/*  430 */     Object[] arrayOfObject1 = nullIsEmpty((Object[])unwrap(paramMarshalledObject, getClassLoader(paramObjectName2), this.defaultClassLoader, [Ljava.lang.Object.class));
/*      */     try
/*      */     {
/*  436 */       Object[] arrayOfObject2 = { paramString, paramObjectName1, paramObjectName2, arrayOfObject1, nullIsEmpty(paramArrayOfString) };
/*      */ 
/*  440 */       if (bool) logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", className=" + paramString + ", name=" + paramObjectName1 + ", loaderName=" + paramObjectName2 + ", params=" + objects(arrayOfObject1) + ", signature=" + strings(paramArrayOfString));
/*      */ 
/*  449 */       return (ObjectInstance)doPrivilegedOperation(6, arrayOfObject2, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  455 */       Exception localException = extractException(localPrivilegedActionException);
/*  456 */       if ((localException instanceof ReflectionException))
/*  457 */         throw ((ReflectionException)localException);
/*  458 */       if ((localException instanceof InstanceAlreadyExistsException))
/*  459 */         throw ((InstanceAlreadyExistsException)localException);
/*  460 */       if ((localException instanceof MBeanRegistrationException))
/*  461 */         throw ((MBeanRegistrationException)localException);
/*  462 */       if ((localException instanceof MBeanException))
/*  463 */         throw ((MBeanException)localException);
/*  464 */       if ((localException instanceof NotCompliantMBeanException))
/*  465 */         throw ((NotCompliantMBeanException)localException);
/*  466 */       if ((localException instanceof InstanceNotFoundException))
/*  467 */         throw ((InstanceNotFoundException)localException);
/*  468 */       if ((localException instanceof IOException))
/*  469 */         throw ((IOException)localException);
/*  470 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void unregisterMBean(ObjectName paramObjectName, Subject paramSubject)
/*      */     throws InstanceNotFoundException, MBeanRegistrationException, IOException
/*      */   {
/*      */     try
/*      */     {
/*  480 */       Object[] arrayOfObject = { paramObjectName };
/*      */ 
/*  482 */       if (logger.debugOn()) logger.debug("unregisterMBean", "connectionId=" + this.connectionId + ", name=" + paramObjectName);
/*      */ 
/*  486 */       doPrivilegedOperation(24, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  491 */       Exception localException = extractException(localPrivilegedActionException);
/*  492 */       if ((localException instanceof InstanceNotFoundException))
/*  493 */         throw ((InstanceNotFoundException)localException);
/*  494 */       if ((localException instanceof MBeanRegistrationException))
/*  495 */         throw ((MBeanRegistrationException)localException);
/*  496 */       if ((localException instanceof IOException))
/*  497 */         throw ((IOException)localException);
/*  498 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ObjectInstance getObjectInstance(ObjectName paramObjectName, Subject paramSubject)
/*      */     throws InstanceNotFoundException, IOException
/*      */   {
/*  508 */     checkNonNull("ObjectName", paramObjectName);
/*      */     try
/*      */     {
/*  511 */       Object[] arrayOfObject = { paramObjectName };
/*      */ 
/*  513 */       if (logger.debugOn()) logger.debug("getObjectInstance", "connectionId=" + this.connectionId + ", name=" + paramObjectName);
/*      */ 
/*  517 */       return (ObjectInstance)doPrivilegedOperation(13, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  523 */       Exception localException = extractException(localPrivilegedActionException);
/*  524 */       if ((localException instanceof InstanceNotFoundException))
/*  525 */         throw ((InstanceNotFoundException)localException);
/*  526 */       if ((localException instanceof IOException))
/*  527 */         throw ((IOException)localException);
/*  528 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject)
/*      */     throws IOException
/*      */   {
/*  539 */     boolean bool = logger.debugOn();
/*      */ 
/*  541 */     if (bool) logger.debug("queryMBeans", "connectionId=" + this.connectionId + " unwrapping query with defaultClassLoader.");
/*      */ 
/*  545 */     QueryExp localQueryExp = (QueryExp)unwrap(paramMarshalledObject, this.defaultContextClassLoader, QueryExp.class);
/*      */     try
/*      */     {
/*  548 */       Object[] arrayOfObject = { paramObjectName, localQueryExp };
/*      */ 
/*  550 */       if (bool) logger.debug("queryMBeans", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", query=" + paramMarshalledObject);
/*      */ 
/*  554 */       return (Set)Util.cast(doPrivilegedOperation(17, arrayOfObject, paramSubject));
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  560 */       Exception localException = extractException(localPrivilegedActionException);
/*  561 */       if ((localException instanceof IOException))
/*  562 */         throw ((IOException)localException);
/*  563 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Set<ObjectName> queryNames(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject)
/*      */     throws IOException
/*      */   {
/*  574 */     boolean bool = logger.debugOn();
/*      */ 
/*  576 */     if (bool) logger.debug("queryNames", "connectionId=" + this.connectionId + " unwrapping query with defaultClassLoader.");
/*      */ 
/*  580 */     QueryExp localQueryExp = (QueryExp)unwrap(paramMarshalledObject, this.defaultContextClassLoader, QueryExp.class);
/*      */     try
/*      */     {
/*  583 */       Object[] arrayOfObject = { paramObjectName, localQueryExp };
/*      */ 
/*  585 */       if (bool) logger.debug("queryNames", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", query=" + paramMarshalledObject);
/*      */ 
/*  589 */       return (Set)Util.cast(doPrivilegedOperation(18, arrayOfObject, paramSubject));
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  595 */       Exception localException = extractException(localPrivilegedActionException);
/*  596 */       if ((localException instanceof IOException))
/*  597 */         throw ((IOException)localException);
/*  598 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isRegistered(ObjectName paramObjectName, Subject paramSubject) throws IOException
/*      */   {
/*      */     try {
/*  605 */       Object[] arrayOfObject = { paramObjectName };
/*  606 */       return ((Boolean)doPrivilegedOperation(16, arrayOfObject, paramSubject)).booleanValue();
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  612 */       Exception localException = extractException(localPrivilegedActionException);
/*  613 */       if ((localException instanceof IOException))
/*  614 */         throw ((IOException)localException);
/*  615 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Integer getMBeanCount(Subject paramSubject) throws IOException
/*      */   {
/*      */     try {
/*  622 */       Object[] arrayOfObject = new Object[0];
/*      */ 
/*  624 */       if (logger.debugOn()) logger.debug("getMBeanCount", "connectionId=" + this.connectionId);
/*      */ 
/*  627 */       return (Integer)doPrivilegedOperation(11, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  633 */       Exception localException = extractException(localPrivilegedActionException);
/*  634 */       if ((localException instanceof IOException))
/*  635 */         throw ((IOException)localException);
/*  636 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getAttribute(ObjectName paramObjectName, String paramString, Subject paramSubject)
/*      */     throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
/*      */   {
/*      */     try
/*      */     {
/*  650 */       Object[] arrayOfObject = { paramObjectName, paramString };
/*  651 */       if (logger.debugOn()) logger.debug("getAttribute", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", attribute=" + paramString);
/*      */ 
/*  656 */       return doPrivilegedOperation(7, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  662 */       Exception localException = extractException(localPrivilegedActionException);
/*  663 */       if ((localException instanceof MBeanException))
/*  664 */         throw ((MBeanException)localException);
/*  665 */       if ((localException instanceof AttributeNotFoundException))
/*  666 */         throw ((AttributeNotFoundException)localException);
/*  667 */       if ((localException instanceof InstanceNotFoundException))
/*  668 */         throw ((InstanceNotFoundException)localException);
/*  669 */       if ((localException instanceof ReflectionException))
/*  670 */         throw ((ReflectionException)localException);
/*  671 */       if ((localException instanceof IOException))
/*  672 */         throw ((IOException)localException);
/*  673 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString, Subject paramSubject)
/*      */     throws InstanceNotFoundException, ReflectionException, IOException
/*      */   {
/*      */     try
/*      */     {
/*  685 */       Object[] arrayOfObject = { paramObjectName, paramArrayOfString };
/*      */ 
/*  687 */       if (logger.debugOn()) logger.debug("getAttributes", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", attributes=" + strings(paramArrayOfString));
/*      */ 
/*  692 */       return (AttributeList)doPrivilegedOperation(8, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  698 */       Exception localException = extractException(localPrivilegedActionException);
/*  699 */       if ((localException instanceof InstanceNotFoundException))
/*  700 */         throw ((InstanceNotFoundException)localException);
/*  701 */       if ((localException instanceof ReflectionException))
/*  702 */         throw ((ReflectionException)localException);
/*  703 */       if ((localException instanceof IOException))
/*  704 */         throw ((IOException)localException);
/*  705 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAttribute(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject)
/*      */     throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
/*      */   {
/*  721 */     boolean bool = logger.debugOn();
/*      */ 
/*  723 */     if (bool) logger.debug("setAttribute", "connectionId=" + this.connectionId + " unwrapping attribute with MBean extended ClassLoader.");
/*      */ 
/*  727 */     Attribute localAttribute = (Attribute)unwrap(paramMarshalledObject, getClassLoaderFor(paramObjectName), this.defaultClassLoader, Attribute.class);
/*      */     try
/*      */     {
/*  733 */       Object[] arrayOfObject = { paramObjectName, localAttribute };
/*      */ 
/*  735 */       if (bool) logger.debug("setAttribute", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", attribute=" + localAttribute);
/*      */ 
/*  740 */       doPrivilegedOperation(22, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  745 */       Exception localException = extractException(localPrivilegedActionException);
/*  746 */       if ((localException instanceof InstanceNotFoundException))
/*  747 */         throw ((InstanceNotFoundException)localException);
/*  748 */       if ((localException instanceof AttributeNotFoundException))
/*  749 */         throw ((AttributeNotFoundException)localException);
/*  750 */       if ((localException instanceof InvalidAttributeValueException))
/*  751 */         throw ((InvalidAttributeValueException)localException);
/*  752 */       if ((localException instanceof MBeanException))
/*  753 */         throw ((MBeanException)localException);
/*  754 */       if ((localException instanceof ReflectionException))
/*  755 */         throw ((ReflectionException)localException);
/*  756 */       if ((localException instanceof IOException))
/*  757 */         throw ((IOException)localException);
/*  758 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public AttributeList setAttributes(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject)
/*      */     throws InstanceNotFoundException, ReflectionException, IOException
/*      */   {
/*  771 */     boolean bool = logger.debugOn();
/*      */ 
/*  773 */     if (bool) logger.debug("setAttributes", "connectionId=" + this.connectionId + " unwrapping attributes with MBean extended ClassLoader.");
/*      */ 
/*  777 */     AttributeList localAttributeList = (AttributeList)unwrap(paramMarshalledObject, getClassLoaderFor(paramObjectName), this.defaultClassLoader, AttributeList.class);
/*      */     try
/*      */     {
/*  784 */       Object[] arrayOfObject = { paramObjectName, localAttributeList };
/*      */ 
/*  786 */       if (bool) logger.debug("setAttributes", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", attributes=" + localAttributeList);
/*      */ 
/*  791 */       return (AttributeList)doPrivilegedOperation(23, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  797 */       Exception localException = extractException(localPrivilegedActionException);
/*  798 */       if ((localException instanceof InstanceNotFoundException))
/*  799 */         throw ((InstanceNotFoundException)localException);
/*  800 */       if ((localException instanceof ReflectionException))
/*  801 */         throw ((ReflectionException)localException);
/*  802 */       if ((localException instanceof IOException))
/*  803 */         throw ((IOException)localException);
/*  804 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object invoke(ObjectName paramObjectName, String paramString, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject)
/*      */     throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
/*      */   {
/*  820 */     checkNonNull("ObjectName", paramObjectName);
/*  821 */     checkNonNull("Operation name", paramString);
/*      */ 
/*  824 */     boolean bool = logger.debugOn();
/*      */ 
/*  826 */     if (bool) logger.debug("invoke", "connectionId=" + this.connectionId + " unwrapping params with MBean extended ClassLoader.");
/*      */ 
/*  830 */     Object[] arrayOfObject1 = nullIsEmpty((Object[])unwrap(paramMarshalledObject, getClassLoaderFor(paramObjectName), this.defaultClassLoader, [Ljava.lang.Object.class));
/*      */     try
/*      */     {
/*  836 */       Object[] arrayOfObject2 = { paramObjectName, paramString, arrayOfObject1, nullIsEmpty(paramArrayOfString) };
/*      */ 
/*  840 */       if (bool) logger.debug("invoke", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", operationName=" + paramString + ", params=" + objects(arrayOfObject1) + ", signature=" + strings(paramArrayOfString));
/*      */ 
/*  847 */       return doPrivilegedOperation(14, arrayOfObject2, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  853 */       Exception localException = extractException(localPrivilegedActionException);
/*  854 */       if ((localException instanceof InstanceNotFoundException))
/*  855 */         throw ((InstanceNotFoundException)localException);
/*  856 */       if ((localException instanceof MBeanException))
/*  857 */         throw ((MBeanException)localException);
/*  858 */       if ((localException instanceof ReflectionException))
/*  859 */         throw ((ReflectionException)localException);
/*  860 */       if ((localException instanceof IOException))
/*  861 */         throw ((IOException)localException);
/*  862 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getDefaultDomain(Subject paramSubject) throws IOException
/*      */   {
/*      */     try {
/*  869 */       Object[] arrayOfObject = new Object[0];
/*      */ 
/*  871 */       if (logger.debugOn()) logger.debug("getDefaultDomain", "connectionId=" + this.connectionId);
/*      */ 
/*  874 */       return (String)doPrivilegedOperation(9, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  880 */       Exception localException = extractException(localPrivilegedActionException);
/*  881 */       if ((localException instanceof IOException))
/*  882 */         throw ((IOException)localException);
/*  883 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String[] getDomains(Subject paramSubject) throws IOException {
/*      */     try {
/*  889 */       Object[] arrayOfObject = new Object[0];
/*      */ 
/*  891 */       if (logger.debugOn()) logger.debug("getDomains", "connectionId=" + this.connectionId);
/*      */ 
/*  894 */       return (String[])doPrivilegedOperation(10, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  900 */       Exception localException = extractException(localPrivilegedActionException);
/*  901 */       if ((localException instanceof IOException))
/*  902 */         throw ((IOException)localException);
/*  903 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public MBeanInfo getMBeanInfo(ObjectName paramObjectName, Subject paramSubject)
/*      */     throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
/*      */   {
/*  914 */     checkNonNull("ObjectName", paramObjectName);
/*      */     try
/*      */     {
/*  917 */       Object[] arrayOfObject = { paramObjectName };
/*      */ 
/*  919 */       if (logger.debugOn()) logger.debug("getMBeanInfo", "connectionId=" + this.connectionId + ", name=" + paramObjectName);
/*      */ 
/*  923 */       return (MBeanInfo)doPrivilegedOperation(12, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  929 */       Exception localException = extractException(localPrivilegedActionException);
/*  930 */       if ((localException instanceof InstanceNotFoundException))
/*  931 */         throw ((InstanceNotFoundException)localException);
/*  932 */       if ((localException instanceof IntrospectionException))
/*  933 */         throw ((IntrospectionException)localException);
/*  934 */       if ((localException instanceof ReflectionException))
/*  935 */         throw ((ReflectionException)localException);
/*  936 */       if ((localException instanceof IOException))
/*  937 */         throw ((IOException)localException);
/*  938 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isInstanceOf(ObjectName paramObjectName, String paramString, Subject paramSubject)
/*      */     throws InstanceNotFoundException, IOException
/*      */   {
/*  947 */     checkNonNull("ObjectName", paramObjectName);
/*      */     try
/*      */     {
/*  950 */       Object[] arrayOfObject = { paramObjectName, paramString };
/*      */ 
/*  952 */       if (logger.debugOn()) logger.debug("isInstanceOf", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", className=" + paramString);
/*      */ 
/*  957 */       return ((Boolean)doPrivilegedOperation(15, arrayOfObject, paramSubject)).booleanValue();
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/*  963 */       Exception localException = extractException(localPrivilegedActionException);
/*  964 */       if ((localException instanceof InstanceNotFoundException))
/*  965 */         throw ((InstanceNotFoundException)localException);
/*  966 */       if ((localException instanceof IOException))
/*  967 */         throw ((IOException)localException);
/*  968 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Integer[] addNotificationListeners(ObjectName[] paramArrayOfObjectName, MarshalledObject[] paramArrayOfMarshalledObject, Subject[] paramArrayOfSubject)
/*      */     throws InstanceNotFoundException, IOException
/*      */   {
/*  978 */     if ((paramArrayOfObjectName == null) || (paramArrayOfMarshalledObject == null)) {
/*  979 */       throw new IllegalArgumentException("Got null arguments.");
/*      */     }
/*      */ 
/*  982 */     Subject[] arrayOfSubject = paramArrayOfSubject != null ? paramArrayOfSubject : new Subject[paramArrayOfObjectName.length];
/*      */ 
/*  984 */     if ((paramArrayOfObjectName.length != paramArrayOfMarshalledObject.length) || (paramArrayOfMarshalledObject.length != arrayOfSubject.length))
/*      */     {
/*  987 */       throw new IllegalArgumentException("The value lengths of 3 parameters are not same.");
/*      */     }
/*      */ 
/*  990 */     for (int i = 0; i < paramArrayOfObjectName.length; i++) {
/*  991 */       if (paramArrayOfObjectName[i] == null) {
/*  992 */         throw new IllegalArgumentException("Null Object name.");
/*      */       }
/*      */     }
/*      */ 
/*  996 */     i = 0;
/*      */ 
/*  998 */     NotificationFilter[] arrayOfNotificationFilter = new NotificationFilter[paramArrayOfObjectName.length];
/*      */ 
/* 1000 */     Integer[] arrayOfInteger = new Integer[paramArrayOfObjectName.length];
/* 1001 */     boolean bool = logger.debugOn();
/*      */     try
/*      */     {
/* 1004 */       for (; i < paramArrayOfObjectName.length; i++) {
/* 1005 */         ClassLoader localClassLoader = getClassLoaderFor(paramArrayOfObjectName[i]);
/*      */ 
/* 1007 */         if (bool) logger.debug("addNotificationListener(ObjectName,NotificationFilter)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader.");
/*      */ 
/* 1012 */         arrayOfNotificationFilter[i] = ((NotificationFilter)unwrap(paramArrayOfMarshalledObject[i], localClassLoader, this.defaultClassLoader, NotificationFilter.class));
/*      */ 
/* 1016 */         if (bool) logger.debug("addNotificationListener(ObjectName,NotificationFilter)", "connectionId=" + this.connectionId + ", name=" + paramArrayOfObjectName[i] + ", filter=" + arrayOfNotificationFilter[i]);
/*      */ 
/* 1022 */         arrayOfInteger[i] = ((Integer)doPrivilegedOperation(1, new Object[] { paramArrayOfObjectName[i], arrayOfNotificationFilter[i] }, arrayOfSubject[i]));
/*      */       }
/*      */ 
/* 1029 */       return arrayOfInteger;
/*      */     }
/*      */     catch (Exception localException1) {
/* 1032 */       for (int j = 0; j < i; j++)
/*      */         try {
/* 1034 */           getServerNotifFwd().removeNotificationListener(paramArrayOfObjectName[j], arrayOfInteger[j]);
/*      */         }
/*      */         catch (Exception localException3)
/*      */         {
/*      */         }
/*      */       Exception localException2;
/* 1041 */       if ((localException1 instanceof PrivilegedActionException)) {
/* 1042 */         localException2 = extractException(localException1);
/*      */       }
/*      */ 
/* 1045 */       if ((localException2 instanceof ClassCastException))
/* 1046 */         throw ((ClassCastException)localException2);
/* 1047 */       if ((localException2 instanceof IOException))
/* 1048 */         throw ((IOException)localException2);
/* 1049 */       if ((localException2 instanceof InstanceNotFoundException))
/* 1050 */         throw ((InstanceNotFoundException)localException2);
/* 1051 */       if ((localException2 instanceof RuntimeException)) {
/* 1052 */         throw ((RuntimeException)localException2);
/*      */       }
/* 1054 */       throw newIOException("Got unexpected server exception: " + localException2, localException2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject)
/*      */     throws InstanceNotFoundException, IOException
/*      */   {
/* 1067 */     checkNonNull("Target MBean name", paramObjectName1);
/* 1068 */     checkNonNull("Listener MBean name", paramObjectName2);
/*      */ 
/* 1072 */     boolean bool = logger.debugOn();
/*      */ 
/* 1074 */     ClassLoader localClassLoader = getClassLoaderFor(paramObjectName1);
/*      */ 
/* 1076 */     if (bool) logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader.");
/*      */ 
/* 1081 */     NotificationFilter localNotificationFilter = (NotificationFilter)unwrap(paramMarshalledObject1, localClassLoader, this.defaultClassLoader, NotificationFilter.class);
/*      */ 
/* 1084 */     if (bool) logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping handback with target extended ClassLoader.");
/*      */ 
/* 1089 */     Object localObject = unwrap(paramMarshalledObject2, localClassLoader, this.defaultClassLoader, Object.class);
/*      */     try
/*      */     {
/* 1093 */       Object[] arrayOfObject = { paramObjectName1, paramObjectName2, localNotificationFilter, localObject };
/*      */ 
/* 1096 */       if (bool) logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + ", name=" + paramObjectName1 + ", listenerName=" + paramObjectName2 + ", filter=" + localNotificationFilter + ", handback=" + localObject);
/*      */ 
/* 1104 */       doPrivilegedOperation(2, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/* 1109 */       Exception localException = extractException(localPrivilegedActionException);
/* 1110 */       if ((localException instanceof InstanceNotFoundException))
/* 1111 */         throw ((InstanceNotFoundException)localException);
/* 1112 */       if ((localException instanceof IOException))
/* 1113 */         throw ((IOException)localException);
/* 1114 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeNotificationListeners(ObjectName paramObjectName, Integer[] paramArrayOfInteger, Subject paramSubject)
/*      */     throws InstanceNotFoundException, ListenerNotFoundException, IOException
/*      */   {
/* 1126 */     if ((paramObjectName == null) || (paramArrayOfInteger == null)) {
/* 1127 */       throw new IllegalArgumentException("Illegal null parameter");
/*      */     }
/* 1129 */     for (int i = 0; i < paramArrayOfInteger.length; i++) {
/* 1130 */       if (paramArrayOfInteger[i] == null)
/* 1131 */         throw new IllegalArgumentException("Null listener ID");
/*      */     }
/*      */     try
/*      */     {
/* 1135 */       Object[] arrayOfObject = { paramObjectName, paramArrayOfInteger };
/*      */ 
/* 1137 */       if (logger.debugOn()) logger.debug("removeNotificationListener(ObjectName,Integer[])", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", listenerIDs=" + objects(paramArrayOfInteger));
/*      */ 
/* 1143 */       doPrivilegedOperation(19, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/* 1148 */       Exception localException = extractException(localPrivilegedActionException);
/* 1149 */       if ((localException instanceof InstanceNotFoundException))
/* 1150 */         throw ((InstanceNotFoundException)localException);
/* 1151 */       if ((localException instanceof ListenerNotFoundException))
/* 1152 */         throw ((ListenerNotFoundException)localException);
/* 1153 */       if ((localException instanceof IOException))
/* 1154 */         throw ((IOException)localException);
/* 1155 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject)
/*      */     throws InstanceNotFoundException, ListenerNotFoundException, IOException
/*      */   {
/* 1167 */     checkNonNull("Target MBean name", paramObjectName1);
/* 1168 */     checkNonNull("Listener MBean name", paramObjectName2);
/*      */     try
/*      */     {
/* 1171 */       Object[] arrayOfObject = { paramObjectName1, paramObjectName2 };
/*      */ 
/* 1173 */       if (logger.debugOn()) logger.debug("removeNotificationListener(ObjectName,ObjectName)", "connectionId=" + this.connectionId + ", name=" + paramObjectName1 + ", listenerName=" + paramObjectName2);
/*      */ 
/* 1179 */       doPrivilegedOperation(20, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/* 1184 */       Exception localException = extractException(localPrivilegedActionException);
/* 1185 */       if ((localException instanceof InstanceNotFoundException))
/* 1186 */         throw ((InstanceNotFoundException)localException);
/* 1187 */       if ((localException instanceof ListenerNotFoundException))
/* 1188 */         throw ((ListenerNotFoundException)localException);
/* 1189 */       if ((localException instanceof IOException))
/* 1190 */         throw ((IOException)localException);
/* 1191 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject)
/*      */     throws InstanceNotFoundException, ListenerNotFoundException, IOException
/*      */   {
/* 1206 */     checkNonNull("Target MBean name", paramObjectName1);
/* 1207 */     checkNonNull("Listener MBean name", paramObjectName2);
/*      */ 
/* 1211 */     boolean bool = logger.debugOn();
/*      */ 
/* 1213 */     ClassLoader localClassLoader = getClassLoaderFor(paramObjectName1);
/*      */ 
/* 1215 */     if (bool) logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader.");
/*      */ 
/* 1220 */     NotificationFilter localNotificationFilter = (NotificationFilter)unwrap(paramMarshalledObject1, localClassLoader, this.defaultClassLoader, NotificationFilter.class);
/*      */ 
/* 1223 */     if (bool) logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping handback with target extended ClassLoader.");
/*      */ 
/* 1228 */     Object localObject = unwrap(paramMarshalledObject2, localClassLoader, this.defaultClassLoader, Object.class);
/*      */     try
/*      */     {
/* 1232 */       Object[] arrayOfObject = { paramObjectName1, paramObjectName2, localNotificationFilter, localObject };
/*      */ 
/* 1235 */       if (bool) logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + ", name=" + paramObjectName1 + ", listenerName=" + paramObjectName2 + ", filter=" + localNotificationFilter + ", handback=" + localObject);
/*      */ 
/* 1243 */       doPrivilegedOperation(21, arrayOfObject, paramSubject);
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/* 1248 */       Exception localException = extractException(localPrivilegedActionException);
/* 1249 */       if ((localException instanceof InstanceNotFoundException))
/* 1250 */         throw ((InstanceNotFoundException)localException);
/* 1251 */       if ((localException instanceof ListenerNotFoundException))
/* 1252 */         throw ((ListenerNotFoundException)localException);
/* 1253 */       if ((localException instanceof IOException))
/* 1254 */         throw ((IOException)localException);
/* 1255 */       throw newIOException("Got unexpected server exception: " + localException, localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public NotificationResult fetchNotifications(long paramLong1, int paramInt, long paramLong2)
/*      */     throws IOException
/*      */   {
/* 1264 */     if (logger.debugOn()) logger.debug("fetchNotifications", "connectionId=" + this.connectionId + ", timeout=" + paramLong2);
/*      */ 
/* 1268 */     if ((paramInt < 0) || (paramLong2 < 0L)) {
/* 1269 */       throw new IllegalArgumentException("Illegal negative argument");
/*      */     }
/* 1271 */     boolean bool = this.serverCommunicatorAdmin.reqIncoming();
/*      */     try
/*      */     {
/* 1274 */       if (bool)
/*      */       {
/* 1278 */         return new NotificationResult(0L, 0L, new TargetedNotification[0]);
/*      */       }
/*      */ 
/* 1282 */       final long l1 = paramLong1;
/* 1283 */       final int i = paramInt;
/* 1284 */       long l2 = paramLong2;
/* 1285 */       PrivilegedAction local3 = new PrivilegedAction()
/*      */       {
/*      */         public NotificationResult run() {
/* 1288 */           return RMIConnectionImpl.this.getServerNotifFwd().fetchNotifs(l1, i, this.val$mn);
/*      */         }
/*      */       };
/*      */       NotificationResult localNotificationResult2;
/* 1291 */       if (this.acc == null) {
/* 1292 */         return (NotificationResult)local3.run();
/*      */       }
/* 1294 */       return (NotificationResult)AccessController.doPrivileged(local3, this.acc);
/*      */     } finally {
/* 1296 */       this.serverCommunicatorAdmin.rspOutgoing();
/*      */     }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1311 */     return super.toString() + ": connectionId=" + this.connectionId;
/*      */   }
/*      */ 
/*      */   private ClassLoader getClassLoader(final ObjectName paramObjectName)
/*      */     throws InstanceNotFoundException
/*      */   {
/*      */     try
/*      */     {
/* 1362 */       return (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public ClassLoader run() throws InstanceNotFoundException
/*      */         {
/* 1366 */           return RMIConnectionImpl.this.mbeanServer.getClassLoader(paramObjectName);
/*      */         }
/*      */       }
/*      */       , withPermissions(new Permission[] { new MBeanPermission("*", "getClassLoader") }));
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/* 1372 */       throw ((InstanceNotFoundException)extractException(localPrivilegedActionException));
/*      */     }
/*      */   }
/*      */ 
/*      */   private ClassLoader getClassLoaderFor(final ObjectName paramObjectName) throws InstanceNotFoundException
/*      */   {
/*      */     try {
/* 1379 */       return (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Object run() throws InstanceNotFoundException
/*      */         {
/* 1383 */           return RMIConnectionImpl.this.mbeanServer.getClassLoaderFor(paramObjectName);
/*      */         }
/*      */       }
/*      */       , withPermissions(new Permission[] { new MBeanPermission("*", "getClassLoaderFor") }));
/*      */     }
/*      */     catch (PrivilegedActionException localPrivilegedActionException)
/*      */     {
/* 1389 */       throw ((InstanceNotFoundException)extractException(localPrivilegedActionException));
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object doPrivilegedOperation(int paramInt, Object[] paramArrayOfObject, Subject paramSubject)
/*      */     throws PrivilegedActionException, IOException
/*      */   {
/* 1398 */     this.serverCommunicatorAdmin.reqIncoming();
/*      */     try
/*      */     {
/*      */       AccessControlContext localAccessControlContext;
/* 1402 */       if (paramSubject == null) {
/* 1403 */         localAccessControlContext = this.acc;
/*      */       } else {
/* 1405 */         if (this.subject == null)
/*      */         {
/* 1409 */           throw new SecurityException("Subject delegation cannot be enabled unless an authenticated subject is put in place");
/*      */         }
/* 1411 */         localAccessControlContext = this.subjectDelegator.delegatedContext(this.acc, paramSubject, this.removeCallerContext);
/*      */       }
/*      */ 
/* 1415 */       PrivilegedOperation localPrivilegedOperation = new PrivilegedOperation(paramInt, paramArrayOfObject);
/*      */ 
/* 1417 */       if (localAccessControlContext == null) {
/*      */         try {
/* 1419 */           return localPrivilegedOperation.run();
/*      */         } catch (Exception localException) {
/* 1421 */           if ((localException instanceof RuntimeException))
/* 1422 */             throw ((RuntimeException)localException);
/* 1423 */           throw new PrivilegedActionException(localException);
/*      */         }
/*      */       }
/* 1426 */       return AccessController.doPrivileged(localPrivilegedOperation, localAccessControlContext);
/*      */     }
/*      */     catch (Error localError) {
/* 1429 */       throw new JMXServerErrorException(localError.toString(), localError);
/*      */     } finally {
/* 1431 */       this.serverCommunicatorAdmin.rspOutgoing();
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object doOperation(int paramInt, Object[] paramArrayOfObject)
/*      */     throws Exception
/*      */   {
/* 1438 */     switch (paramInt)
/*      */     {
/*      */     case 3:
/* 1441 */       return this.mbeanServer.createMBean((String)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1]);
/*      */     case 5:
/* 1445 */       return this.mbeanServer.createMBean((String)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (ObjectName)paramArrayOfObject[2]);
/*      */     case 4:
/* 1450 */       return this.mbeanServer.createMBean((String)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (Object[])paramArrayOfObject[2], (String[])paramArrayOfObject[3]);
/*      */     case 6:
/* 1456 */       return this.mbeanServer.createMBean((String)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (ObjectName)paramArrayOfObject[2], (Object[])paramArrayOfObject[3], (String[])paramArrayOfObject[4]);
/*      */     case 7:
/* 1463 */       return this.mbeanServer.getAttribute((ObjectName)paramArrayOfObject[0], (String)paramArrayOfObject[1]);
/*      */     case 8:
/* 1467 */       return this.mbeanServer.getAttributes((ObjectName)paramArrayOfObject[0], (String[])paramArrayOfObject[1]);
/*      */     case 9:
/* 1471 */       return this.mbeanServer.getDefaultDomain();
/*      */     case 10:
/* 1474 */       return this.mbeanServer.getDomains();
/*      */     case 11:
/* 1477 */       return this.mbeanServer.getMBeanCount();
/*      */     case 12:
/* 1480 */       return this.mbeanServer.getMBeanInfo((ObjectName)paramArrayOfObject[0]);
/*      */     case 13:
/* 1483 */       return this.mbeanServer.getObjectInstance((ObjectName)paramArrayOfObject[0]);
/*      */     case 14:
/* 1486 */       return this.mbeanServer.invoke((ObjectName)paramArrayOfObject[0], (String)paramArrayOfObject[1], (Object[])paramArrayOfObject[2], (String[])paramArrayOfObject[3]);
/*      */     case 15:
/* 1492 */       return this.mbeanServer.isInstanceOf((ObjectName)paramArrayOfObject[0], (String)paramArrayOfObject[1]) ? Boolean.TRUE : Boolean.FALSE;
/*      */     case 16:
/* 1497 */       return this.mbeanServer.isRegistered((ObjectName)paramArrayOfObject[0]) ? Boolean.TRUE : Boolean.FALSE;
/*      */     case 17:
/* 1501 */       return this.mbeanServer.queryMBeans((ObjectName)paramArrayOfObject[0], (QueryExp)paramArrayOfObject[1]);
/*      */     case 18:
/* 1505 */       return this.mbeanServer.queryNames((ObjectName)paramArrayOfObject[0], (QueryExp)paramArrayOfObject[1]);
/*      */     case 22:
/* 1509 */       this.mbeanServer.setAttribute((ObjectName)paramArrayOfObject[0], (Attribute)paramArrayOfObject[1]);
/*      */ 
/* 1511 */       return null;
/*      */     case 23:
/* 1514 */       return this.mbeanServer.setAttributes((ObjectName)paramArrayOfObject[0], (AttributeList)paramArrayOfObject[1]);
/*      */     case 24:
/* 1518 */       this.mbeanServer.unregisterMBean((ObjectName)paramArrayOfObject[0]);
/* 1519 */       return null;
/*      */     case 1:
/* 1522 */       return getServerNotifFwd().addNotificationListener((ObjectName)paramArrayOfObject[0], (NotificationFilter)paramArrayOfObject[1]);
/*      */     case 2:
/* 1527 */       this.mbeanServer.addNotificationListener((ObjectName)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (NotificationFilter)paramArrayOfObject[2], paramArrayOfObject[3]);
/*      */ 
/* 1531 */       return null;
/*      */     case 19:
/* 1534 */       getServerNotifFwd().removeNotificationListener((ObjectName)paramArrayOfObject[0], (Integer[])paramArrayOfObject[1]);
/*      */ 
/* 1537 */       return null;
/*      */     case 20:
/* 1540 */       this.mbeanServer.removeNotificationListener((ObjectName)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1]);
/*      */ 
/* 1542 */       return null;
/*      */     case 21:
/* 1545 */       this.mbeanServer.removeNotificationListener((ObjectName)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (NotificationFilter)paramArrayOfObject[2], paramArrayOfObject[3]);
/*      */ 
/* 1550 */       return null;
/*      */     }
/*      */ 
/* 1553 */     throw new IllegalArgumentException("Invalid operation");
/*      */   }
/*      */ 
/*      */   private static <T> T unwrap(MarshalledObject<?> paramMarshalledObject, ClassLoader paramClassLoader, Class<T> paramClass)
/*      */     throws IOException
/*      */   {
/* 1576 */     if (paramMarshalledObject == null)
/* 1577 */       return null;
/*      */     try
/*      */     {
/* 1580 */       ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new SetCcl(paramClassLoader));
/*      */       try {
/* 1582 */         return paramClass.cast(paramMarshalledObject.get());
/*      */       } catch (ClassNotFoundException localClassNotFoundException) {
/* 1584 */         throw new UnmarshalException(localClassNotFoundException.toString(), localClassNotFoundException);
/*      */       } finally {
/* 1586 */         AccessController.doPrivileged(new SetCcl(localClassLoader));
/*      */       }
/*      */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 1589 */       Exception localException = extractException(localPrivilegedActionException);
/* 1590 */       if ((localException instanceof IOException)) {
/* 1591 */         throw ((IOException)localException);
/*      */       }
/* 1593 */       if ((localException instanceof ClassNotFoundException)) {
/* 1594 */         throw new UnmarshalException(localException.toString(), localException);
/*      */       }
/* 1596 */       logger.warning("unwrap", "Failed to unmarshall object: " + localException);
/* 1597 */       logger.debug("unwrap", localException);
/*      */     }
/* 1599 */     return null;
/*      */   }
/*      */ 
/*      */   private static <T> T unwrap(MarshalledObject<?> paramMarshalledObject, ClassLoader paramClassLoader1, final ClassLoader paramClassLoader2, Class<T> paramClass)
/*      */     throws IOException
/*      */   {
/* 1607 */     if (paramMarshalledObject == null)
/* 1608 */       return null;
/*      */     try
/*      */     {
/* 1611 */       ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public ClassLoader run() throws Exception {
/* 1614 */           return new RMIConnectionImpl.CombinedClassLoader(Thread.currentThread().getContextClassLoader(), new OrderClassLoaders(this.val$cl1, paramClassLoader2), null);
/*      */         }
/*      */       });
/* 1619 */       return unwrap(paramMarshalledObject, localClassLoader, paramClass);
/*      */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 1621 */       Exception localException = extractException(localPrivilegedActionException);
/* 1622 */       if ((localException instanceof IOException)) {
/* 1623 */         throw ((IOException)localException);
/*      */       }
/* 1625 */       if ((localException instanceof ClassNotFoundException)) {
/* 1626 */         throw new UnmarshalException(localException.toString(), localException);
/*      */       }
/* 1628 */       logger.warning("unwrap", "Failed to unmarshall object: " + localException);
/* 1629 */       logger.debug("unwrap", localException);
/*      */     }
/* 1631 */     return null;
/*      */   }
/*      */ 
/*      */   private static IOException newIOException(String paramString, Throwable paramThrowable)
/*      */   {
/* 1640 */     IOException localIOException = new IOException(paramString);
/* 1641 */     return (IOException)EnvHelp.initCause(localIOException, paramThrowable);
/*      */   }
/*      */ 
/*      */   private static Exception extractException(Exception paramException)
/*      */   {
/* 1649 */     while ((paramException instanceof PrivilegedActionException)) {
/* 1650 */       paramException = ((PrivilegedActionException)paramException).getException();
/*      */     }
/* 1652 */     return paramException;
/*      */   }
/*      */ 
/*      */   private static Object[] nullIsEmpty(Object[] paramArrayOfObject)
/*      */   {
/* 1667 */     return paramArrayOfObject == null ? NO_OBJECTS : paramArrayOfObject;
/*      */   }
/*      */ 
/*      */   private static String[] nullIsEmpty(String[] paramArrayOfString) {
/* 1671 */     return paramArrayOfString == null ? NO_STRINGS : paramArrayOfString;
/*      */   }
/*      */ 
/*      */   private static void checkNonNull(String paramString, Object paramObject)
/*      */   {
/* 1682 */     if (paramObject == null) {
/* 1683 */       IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException(paramString + " must not be null");
/*      */ 
/* 1685 */       throw new RuntimeOperationsException(localIllegalArgumentException);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static String objects(Object[] paramArrayOfObject)
/*      */   {
/* 1779 */     if (paramArrayOfObject == null) {
/* 1780 */       return "null";
/*      */     }
/* 1782 */     return Arrays.asList(paramArrayOfObject).toString();
/*      */   }
/*      */ 
/*      */   private static String strings(String[] paramArrayOfString) {
/* 1786 */     return objects(paramArrayOfString);
/*      */   }
/*      */ 
/*      */   private static final class CombinedClassLoader extends ClassLoader
/*      */   {
/*      */     final ClassLoaderWrapper defaultCL;
/*      */ 
/*      */     private CombinedClassLoader(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*      */     {
/* 1809 */       super();
/* 1810 */       this.defaultCL = new ClassLoaderWrapper(paramClassLoader2);
/*      */     }
/*      */ 
/*      */     protected Class<?> loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException
/*      */     {
/*      */       Object localObject;
/*      */       try {
/* 1817 */         super.loadClass(paramString, paramBoolean);
/*      */       } catch (Exception localException) {
/* 1819 */         localObject = localException; } for (; localObject != null; localObject = ((Throwable)localObject).getCause()) {
/* 1820 */         if ((localObject instanceof SecurityException)) {
/* 1821 */           throw (localObject == localException ? (SecurityException)localObject : new SecurityException(((Throwable)localObject).getMessage(), localException));
/*      */         }
/*      */       }
/*      */ 
/* 1825 */       Class localClass = this.defaultCL.loadClass(paramString, paramBoolean);
/* 1826 */       return localClass;
/*      */     }
/*      */ 
/*      */     private static final class ClassLoaderWrapper extends ClassLoader
/*      */     {
/*      */       ClassLoaderWrapper(ClassLoader paramClassLoader)
/*      */       {
/* 1796 */         super();
/*      */       }
/*      */ 
/*      */       protected Class<?> loadClass(String paramString, boolean paramBoolean)
/*      */         throws ClassNotFoundException
/*      */       {
/* 1802 */         return super.loadClass(paramString, paramBoolean);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class PrivilegedOperation
/*      */     implements PrivilegedExceptionAction<Object>
/*      */   {
/*      */     private int operation;
/*      */     private Object[] params;
/*      */ 
/*      */     public PrivilegedOperation(int paramArrayOfObject, Object[] arg3)
/*      */     {
/* 1322 */       this.operation = paramArrayOfObject;
/*      */       Object localObject;
/* 1323 */       this.params = localObject;
/*      */     }
/*      */ 
/*      */     public Object run() throws Exception {
/* 1327 */       return RMIConnectionImpl.this.doOperation(this.operation, this.params);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class RMIServerCommunicatorAdmin extends ServerCommunicatorAdmin
/*      */   {
/*      */     public RMIServerCommunicatorAdmin(long arg2)
/*      */     {
/* 1339 */       super();
/*      */     }
/*      */ 
/*      */     protected void doStop() {
/*      */       try {
/* 1344 */         RMIConnectionImpl.this.close();
/*      */       } catch (IOException localIOException) {
/* 1346 */         RMIConnectionImpl.logger.warning("RMIServerCommunicatorAdmin-doStop", "Failed to close: " + localIOException);
/*      */ 
/* 1348 */         RMIConnectionImpl.logger.debug("RMIServerCommunicatorAdmin-doStop", localIOException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SetCcl
/*      */     implements PrivilegedExceptionAction<ClassLoader>
/*      */   {
/*      */     private final ClassLoader classLoader;
/*      */ 
/*      */     SetCcl(ClassLoader paramClassLoader)
/*      */     {
/* 1561 */       this.classLoader = paramClassLoader;
/*      */     }
/*      */ 
/*      */     public ClassLoader run() {
/* 1565 */       Thread localThread = Thread.currentThread();
/* 1566 */       ClassLoader localClassLoader = localThread.getContextClassLoader();
/* 1567 */       localThread.setContextClassLoader(this.classLoader);
/* 1568 */       return localClassLoader;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.remote.rmi.RMIConnectionImpl
 * JD-Core Version:    0.6.2
 */