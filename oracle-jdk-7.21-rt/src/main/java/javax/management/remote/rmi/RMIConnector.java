/*      */ package javax.management.remote.rmi;
/*      */ 
/*      */ import com.sun.jmx.mbeanserver.Util;
/*      */ import com.sun.jmx.remote.internal.ClientCommunicatorAdmin;
/*      */ import com.sun.jmx.remote.internal.ClientListenerInfo;
/*      */ import com.sun.jmx.remote.internal.ClientNotifForwarder;
/*      */ import com.sun.jmx.remote.internal.IIOPHelper;
/*      */ import com.sun.jmx.remote.internal.ProxyRef;
/*      */ import com.sun.jmx.remote.util.ClassLogger;
/*      */ import com.sun.jmx.remote.util.EnvHelp;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InvalidObjectException;
/*      */ import java.io.NotSerializableException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.ObjectStreamClass;
/*      */ import java.io.Serializable;
/*      */ import java.io.WriteAbortedException;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.net.MalformedURLException;
/*      */ import java.rmi.MarshalException;
/*      */ import java.rmi.MarshalledObject;
/*      */ import java.rmi.NoSuchObjectException;
/*      */ import java.rmi.Remote;
/*      */ import java.rmi.RemoteException;
/*      */ import java.rmi.ServerException;
/*      */ import java.rmi.UnmarshalException;
/*      */ import java.rmi.server.RMIClientSocketFactory;
/*      */ import java.rmi.server.RemoteObject;
/*      */ import java.rmi.server.RemoteObjectInvocationHandler;
/*      */ import java.rmi.server.RemoteRef;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
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
/*      */ import javax.management.MBeanRegistrationException;
/*      */ import javax.management.MBeanServerConnection;
/*      */ import javax.management.MBeanServerDelegate;
/*      */ import javax.management.NotCompliantMBeanException;
/*      */ import javax.management.Notification;
/*      */ import javax.management.NotificationBroadcasterSupport;
/*      */ import javax.management.NotificationFilter;
/*      */ import javax.management.NotificationFilterSupport;
/*      */ import javax.management.NotificationListener;
/*      */ import javax.management.ObjectInstance;
/*      */ import javax.management.ObjectName;
/*      */ import javax.management.QueryExp;
/*      */ import javax.management.ReflectionException;
/*      */ import javax.management.remote.JMXAddressable;
/*      */ import javax.management.remote.JMXConnectionNotification;
/*      */ import javax.management.remote.JMXConnector;
/*      */ import javax.management.remote.JMXServiceURL;
/*      */ import javax.management.remote.NotificationResult;
/*      */ import javax.naming.InitialContext;
/*      */ import javax.naming.NamingException;
/*      */ import javax.rmi.ssl.SslRMIClientSocketFactory;
/*      */ import javax.security.auth.Subject;
/*      */ import sun.rmi.server.UnicastRef2;
/*      */ import sun.rmi.transport.LiveRef;
/*      */ 
/*      */ public class RMIConnector
/*      */   implements JMXConnector, Serializable, JMXAddressable
/*      */ {
/*  121 */   private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnector");
/*      */   private static final long serialVersionUID = 817323035842634473L;
/* 2065 */   private static final String rmiServerImplStubClassName = RMIServer.class.getName() + "Impl_Stub";
/*      */   private static final Class<?> rmiServerImplStubClass;
/* 2068 */   private static final String rmiConnectionImplStubClassName = RMIConnection.class.getName() + "Impl_Stub";
/*      */   private static final Class<?> rmiConnectionImplStubClass;
/*      */   private static final String pRefClassName = "com.sun.jmx.remote.internal.PRef";
/*      */   private static final Constructor<?> proxyRefConstructor;
/*      */   private static final String iiopConnectionStubClassName = "org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub";
/*      */   private static final String proxyStubClassName = "com.sun.jmx.remote.protocol.iiop.ProxyStub";
/*      */   private static final String ProxyInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.ProxyInputStream";
/*      */   private static final String pInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.PInputStream";
/*      */   private static final Class<?> proxyStubClass;
/* 2466 */   private static final byte[] base64ToInt = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
/*      */   private final RMIServer rmiServer;
/*      */   private final JMXServiceURL jmxServiceURL;
/*      */   private transient Map<String, Object> env;
/*      */   private transient ClassLoader defaultClassLoader;
/*      */   private transient RMIConnection connection;
/*      */   private transient String connectionId;
/* 2538 */   private transient long clientNotifSeqNo = 0L;
/*      */   private transient WeakHashMap<Subject, MBeanServerConnection> rmbscMap;
/*      */   private transient RMINotifClient rmiNotifClient;
/* 2545 */   private transient long clientNotifCounter = 0L;
/*      */   private transient boolean connected;
/*      */   private transient boolean terminated;
/*      */   private transient Exception closeException;
/*      */   private transient NotificationBroadcasterSupport connectionBroadcaster;
/*      */   private transient ClientCommunicatorAdmin communicatorAdmin;
/* 2562 */   private static volatile WeakReference<Object> orb = null;
/*      */ 
/*      */   private RMIConnector(RMIServer paramRMIServer, JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
/*      */   {
/*  128 */     if ((paramRMIServer == null) && (paramJMXServiceURL == null)) throw new IllegalArgumentException("rmiServer and jmxServiceURL both null");
/*      */ 
/*  131 */     initTransients();
/*      */ 
/*  133 */     this.rmiServer = paramRMIServer;
/*  134 */     this.jmxServiceURL = paramJMXServiceURL;
/*  135 */     if (paramMap == null) {
/*  136 */       this.env = Collections.emptyMap();
/*      */     } else {
/*  138 */       EnvHelp.checkAttributes(paramMap);
/*  139 */       this.env = Collections.unmodifiableMap(paramMap);
/*      */     }
/*      */   }
/*      */ 
/*      */   public RMIConnector(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
/*      */   {
/*  185 */     this(null, paramJMXServiceURL, paramMap);
/*      */   }
/*      */ 
/*      */   public RMIConnector(RMIServer paramRMIServer, Map<String, ?> paramMap)
/*      */   {
/*  200 */     this(paramRMIServer, null, paramMap);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  214 */     StringBuilder localStringBuilder = new StringBuilder(getClass().getName());
/*  215 */     localStringBuilder.append(":");
/*  216 */     if (this.rmiServer != null) {
/*  217 */       localStringBuilder.append(" rmiServer=").append(this.rmiServer.toString());
/*      */     }
/*  219 */     if (this.jmxServiceURL != null) {
/*  220 */       if (this.rmiServer != null) localStringBuilder.append(",");
/*  221 */       localStringBuilder.append(" jmxServiceURL=").append(this.jmxServiceURL.toString());
/*      */     }
/*  223 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   public JMXServiceURL getAddress()
/*      */   {
/*  235 */     return this.jmxServiceURL;
/*      */   }
/*      */ 
/*      */   public void connect()
/*      */     throws IOException
/*      */   {
/*  242 */     connect(null);
/*      */   }
/*      */ 
/*      */   public synchronized void connect(Map<String, ?> paramMap) throws IOException
/*      */   {
/*  247 */     boolean bool1 = logger.traceOn();
/*  248 */     String str1 = bool1 ? "[" + toString() + "]" : null;
/*      */ 
/*  250 */     if (this.terminated) {
/*  251 */       logger.trace("connect", str1 + " already closed.");
/*  252 */       throw new IOException("Connector closed");
/*      */     }
/*  254 */     if (this.connected) {
/*  255 */       logger.trace("connect", str1 + " already connected.");
/*  256 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  260 */       if (bool1) logger.trace("connect", str1 + " connecting...");
/*      */ 
/*  262 */       HashMap localHashMap = new HashMap(this.env == null ? Collections.emptyMap() : this.env);
/*      */ 
/*  267 */       if (paramMap != null) {
/*  268 */         EnvHelp.checkAttributes(paramMap);
/*  269 */         localHashMap.putAll(paramMap);
/*      */       }
/*      */ 
/*  273 */       if (bool1) logger.trace("connect", str1 + " finding stub...");
/*  274 */       localObject1 = this.rmiServer != null ? this.rmiServer : findRMIServer(this.jmxServiceURL, localHashMap);
/*      */ 
/*  280 */       String str2 = (String)localHashMap.get("jmx.remote.x.check.stub");
/*  281 */       boolean bool2 = EnvHelp.computeBooleanFromString(str2);
/*      */ 
/*  283 */       if (bool2) checkStub((Remote)localObject1, rmiServerImplStubClass);
/*      */ 
/*  286 */       if (bool1) logger.trace("connect", str1 + " connecting stub...");
/*  287 */       localObject1 = connectStub((RMIServer)localObject1, localHashMap);
/*  288 */       str1 = bool1 ? "[" + toString() + "]" : null;
/*      */ 
/*  291 */       if (bool1)
/*  292 */         logger.trace("connect", str1 + " getting connection...");
/*  293 */       Object localObject2 = localHashMap.get("jmx.remote.credentials");
/*      */       try
/*      */       {
/*  296 */         this.connection = getConnection((RMIServer)localObject1, localObject2, bool2);
/*      */       } catch (RemoteException localRemoteException) {
/*  298 */         if (this.jmxServiceURL != null) {
/*  299 */           String str3 = this.jmxServiceURL.getProtocol();
/*  300 */           localObject3 = this.jmxServiceURL.getURLPath();
/*      */ 
/*  302 */           if (("rmi".equals(str3)) && (((String)localObject3).startsWith("/jndi/iiop:")))
/*      */           {
/*  304 */             MalformedURLException localMalformedURLException = new MalformedURLException("Protocol is rmi but JNDI scheme is iiop: " + this.jmxServiceURL);
/*      */ 
/*  306 */             localMalformedURLException.initCause(localRemoteException);
/*  307 */             throw localMalformedURLException;
/*      */           }
/*      */         }
/*  310 */         throw localRemoteException;
/*      */       }
/*      */ 
/*  316 */       if (bool1)
/*  317 */         logger.trace("connect", str1 + " getting class loader...");
/*  318 */       this.defaultClassLoader = EnvHelp.resolveClientClassLoader(localHashMap);
/*      */ 
/*  320 */       localHashMap.put("jmx.remote.default.class.loader", this.defaultClassLoader);
/*      */ 
/*  323 */       this.rmiNotifClient = new RMINotifClient(this.defaultClassLoader, localHashMap);
/*      */ 
/*  325 */       this.env = localHashMap;
/*  326 */       long l = EnvHelp.getConnectionCheckPeriod(localHashMap);
/*  327 */       this.communicatorAdmin = new RMIClientCommunicatorAdmin(l);
/*      */ 
/*  329 */       this.connected = true;
/*      */ 
/*  334 */       this.connectionId = getConnectionId();
/*      */ 
/*  336 */       Object localObject3 = new JMXConnectionNotification("jmx.remote.connection.opened", this, this.connectionId, this.clientNotifSeqNo++, "Successful connection", null);
/*      */ 
/*  343 */       sendNotification((Notification)localObject3);
/*      */ 
/*  345 */       if (bool1) logger.trace("connect", str1 + " done..."); 
/*      */     }
/*  347 */     catch (IOException localIOException) { if (bool1)
/*  348 */         logger.trace("connect", str1 + " failed to connect: " + localIOException);
/*  349 */       throw localIOException;
/*      */     } catch (RuntimeException localRuntimeException) {
/*  351 */       if (bool1)
/*  352 */         logger.trace("connect", str1 + " failed to connect: " + localRuntimeException);
/*  353 */       throw localRuntimeException;
/*      */     } catch (NamingException localNamingException) {
/*  355 */       Object localObject1 = "Failed to retrieve RMIServer stub: " + localNamingException;
/*  356 */       if (bool1) logger.trace("connect", str1 + " " + (String)localObject1);
/*  357 */       throw ((IOException)EnvHelp.initCause(new IOException((String)localObject1), localNamingException));
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized String getConnectionId() throws IOException {
/*  362 */     if ((this.terminated) || (!this.connected)) {
/*  363 */       if (logger.traceOn()) {
/*  364 */         logger.trace("getConnectionId", "[" + toString() + "] not connected.");
/*      */       }
/*      */ 
/*  367 */       throw new IOException("Not connected");
/*      */     }
/*      */ 
/*  372 */     return this.connection.getConnectionId();
/*      */   }
/*      */ 
/*      */   public synchronized MBeanServerConnection getMBeanServerConnection() throws IOException
/*      */   {
/*  377 */     return getMBeanServerConnection(null);
/*      */   }
/*      */ 
/*      */   public synchronized MBeanServerConnection getMBeanServerConnection(Subject paramSubject)
/*      */     throws IOException
/*      */   {
/*  384 */     if (this.terminated) {
/*  385 */       if (logger.traceOn()) {
/*  386 */         logger.trace("getMBeanServerConnection", "[" + toString() + "] already closed.");
/*      */       }
/*  388 */       throw new IOException("Connection closed");
/*  389 */     }if (!this.connected) {
/*  390 */       if (logger.traceOn()) {
/*  391 */         logger.trace("getMBeanServerConnection", "[" + toString() + "] is not connected.");
/*      */       }
/*  393 */       throw new IOException("Not connected");
/*      */     }
/*      */ 
/*  396 */     Object localObject = (MBeanServerConnection)this.rmbscMap.get(paramSubject);
/*  397 */     if (localObject != null) {
/*  398 */       return localObject;
/*      */     }
/*      */ 
/*  401 */     localObject = new RemoteMBeanServerConnection(paramSubject);
/*  402 */     this.rmbscMap.put(paramSubject, localObject);
/*  403 */     return localObject;
/*      */   }
/*      */ 
/*      */   public void addConnectionNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */   {
/*  410 */     if (paramNotificationListener == null)
/*  411 */       throw new NullPointerException("listener");
/*  412 */     this.connectionBroadcaster.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
/*      */   }
/*      */ 
/*      */   public void removeConnectionNotificationListener(NotificationListener paramNotificationListener)
/*      */     throws ListenerNotFoundException
/*      */   {
/*  419 */     if (paramNotificationListener == null)
/*  420 */       throw new NullPointerException("listener");
/*  421 */     this.connectionBroadcaster.removeNotificationListener(paramNotificationListener);
/*      */   }
/*      */ 
/*      */   public void removeConnectionNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */     throws ListenerNotFoundException
/*      */   {
/*  429 */     if (paramNotificationListener == null)
/*  430 */       throw new NullPointerException("listener");
/*  431 */     this.connectionBroadcaster.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
/*      */   }
/*      */ 
/*      */   private void sendNotification(Notification paramNotification)
/*      */   {
/*  436 */     this.connectionBroadcaster.sendNotification(paramNotification);
/*      */   }
/*      */ 
/*      */   public synchronized void close() throws IOException {
/*  440 */     close(false);
/*      */   }
/*      */ 
/*      */   private synchronized void close(boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  446 */     boolean bool1 = logger.traceOn();
/*  447 */     boolean bool2 = logger.debugOn();
/*  448 */     String str1 = bool1 ? "[" + toString() + "]" : null;
/*      */ 
/*  450 */     if (!paramBoolean)
/*      */     {
/*  453 */       if (this.terminated) {
/*  454 */         if (this.closeException == null) {
/*  455 */           if (bool1) logger.trace("close", str1 + " already closed.");
/*      */         }
/*      */       }
/*      */       else {
/*  459 */         this.terminated = true;
/*      */       }
/*      */     }
/*      */ 
/*  463 */     if ((this.closeException != null) && (bool1))
/*      */     {
/*  466 */       if (bool1) {
/*  467 */         logger.trace("close", str1 + " had failed: " + this.closeException);
/*  468 */         logger.trace("close", str1 + " attempting to close again.");
/*      */       }
/*      */     }
/*      */ 
/*  472 */     String str2 = null;
/*  473 */     if (this.connected) {
/*  474 */       str2 = this.connectionId;
/*      */     }
/*      */ 
/*  477 */     this.closeException = null;
/*      */ 
/*  479 */     if (bool1) logger.trace("close", str1 + " closing.");
/*      */ 
/*  481 */     if (this.communicatorAdmin != null) {
/*  482 */       this.communicatorAdmin.terminate();
/*      */     }
/*      */ 
/*  485 */     if (this.rmiNotifClient != null) {
/*      */       try {
/*  487 */         this.rmiNotifClient.terminate();
/*  488 */         if (bool1) logger.trace("close", str1 + " RMI Notification client terminated."); 
/*      */       }
/*      */       catch (RuntimeException localRuntimeException)
/*      */       {
/*  491 */         this.closeException = localRuntimeException;
/*  492 */         if (bool1) logger.trace("close", str1 + " Failed to terminate RMI Notification client: " + localRuntimeException);
/*      */ 
/*  494 */         if (bool2) logger.debug("close", localRuntimeException);
/*      */       }
/*      */     }
/*      */ 
/*  498 */     if (this.connection != null) {
/*      */       try {
/*  500 */         this.connection.close();
/*  501 */         if (bool1) logger.trace("close", str1 + " closed."); 
/*      */       }
/*      */       catch (NoSuchObjectException localNoSuchObjectException) {
/*      */       }
/*  505 */       catch (IOException localIOException) { this.closeException = localIOException;
/*  506 */         if (bool1) logger.trace("close", str1 + " Failed to close RMIServer: " + localIOException);
/*      */ 
/*  508 */         if (bool2) logger.debug("close", localIOException);
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  514 */     this.rmbscMap.clear();
/*      */     Object localObject;
/*  520 */     if (str2 != null) {
/*  521 */       localObject = new JMXConnectionNotification("jmx.remote.connection.closed", this, str2, this.clientNotifSeqNo++, "Client has been closed", null);
/*      */ 
/*  528 */       sendNotification((Notification)localObject);
/*      */     }
/*      */ 
/*  533 */     if (this.closeException != null) {
/*  534 */       if (bool1) logger.trace("close", str1 + " failed to close: " + this.closeException);
/*      */ 
/*  536 */       if ((this.closeException instanceof IOException))
/*  537 */         throw ((IOException)this.closeException);
/*  538 */       if ((this.closeException instanceof RuntimeException))
/*  539 */         throw ((RuntimeException)this.closeException);
/*  540 */       localObject = new IOException("Failed to close: " + this.closeException);
/*      */ 
/*  542 */       throw ((IOException)EnvHelp.initCause((Throwable)localObject, this.closeException));
/*      */     }
/*      */   }
/*      */ 
/*      */   private Integer addListenerWithSubject(ObjectName paramObjectName, MarshalledObject<NotificationFilter> paramMarshalledObject, Subject paramSubject, boolean paramBoolean)
/*      */     throws InstanceNotFoundException, IOException
/*      */   {
/*  553 */     boolean bool = logger.debugOn();
/*  554 */     if (bool) {
/*  555 */       logger.debug("addListenerWithSubject", "(ObjectName,MarshalledObject,Subject)");
/*      */     }
/*      */ 
/*  558 */     ObjectName[] arrayOfObjectName = { paramObjectName };
/*  559 */     MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])Util.cast(new MarshalledObject[] { paramMarshalledObject });
/*      */ 
/*  561 */     Subject[] arrayOfSubject = { paramSubject };
/*      */ 
/*  565 */     Integer[] arrayOfInteger = addListenersWithSubjects(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject, paramBoolean);
/*      */ 
/*  569 */     if (bool) logger.debug("addListenerWithSubject", "listenerID=" + arrayOfInteger[0]);
/*      */ 
/*  571 */     return arrayOfInteger[0];
/*      */   }
/*      */ 
/*      */   private Integer[] addListenersWithSubjects(ObjectName[] paramArrayOfObjectName, MarshalledObject<NotificationFilter>[] paramArrayOfMarshalledObject, Subject[] paramArrayOfSubject, boolean paramBoolean)
/*      */     throws InstanceNotFoundException, IOException
/*      */   {
/*  581 */     boolean bool = logger.debugOn();
/*  582 */     if (bool) {
/*  583 */       logger.debug("addListenersWithSubjects", "(ObjectName[],MarshalledObject[],Subject[])");
/*      */     }
/*      */ 
/*  586 */     ClassLoader localClassLoader = pushDefaultClassLoader();
/*  587 */     Integer[] arrayOfInteger = null;
/*      */     try
/*      */     {
/*  590 */       arrayOfInteger = this.connection.addNotificationListeners(paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject);
/*      */     }
/*      */     catch (NoSuchObjectException localNoSuchObjectException)
/*      */     {
/*  595 */       if (paramBoolean) {
/*  596 */         this.communicatorAdmin.gotIOException(localNoSuchObjectException);
/*      */ 
/*  598 */         arrayOfInteger = this.connection.addNotificationListeners(paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject);
/*      */       }
/*      */       else
/*      */       {
/*  602 */         throw localNoSuchObjectException;
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException) {
/*  606 */       this.communicatorAdmin.gotIOException(localIOException);
/*      */     } finally {
/*  608 */       popDefaultClassLoader(localClassLoader);
/*      */     }
/*      */ 
/*  611 */     if (bool) logger.debug("addListenersWithSubjects", "registered " + (arrayOfInteger == null ? 0 : arrayOfInteger.length) + " listener(s)");
/*      */ 
/*  614 */     return arrayOfInteger;
/*      */   }
/*      */ 
/*      */   static RMIServer connectStub(RMIServer paramRMIServer, Map<String, ?> paramMap)
/*      */     throws IOException
/*      */   {
/* 1709 */     if (IIOPHelper.isStub(paramRMIServer)) {
/*      */       try {
/* 1711 */         IIOPHelper.getOrb(paramRMIServer);
/*      */       }
/*      */       catch (UnsupportedOperationException localUnsupportedOperationException) {
/* 1714 */         IIOPHelper.connect(paramRMIServer, resolveOrb(paramMap));
/*      */       }
/*      */     }
/* 1717 */     return paramRMIServer;
/*      */   }
/*      */ 
/*      */   static Object resolveOrb(Map<String, ?> paramMap)
/*      */     throws IOException
/*      */   {
/* 1743 */     if (paramMap != null) {
/* 1744 */       localObject1 = paramMap.get("java.naming.corba.orb");
/* 1745 */       if ((localObject1 != null) && (!IIOPHelper.isOrb(localObject1))) {
/* 1746 */         throw new IllegalArgumentException("java.naming.corba.orb must be an instance of org.omg.CORBA.ORB.");
/*      */       }
/* 1748 */       if (localObject1 != null) return localObject1;
/*      */     }
/* 1750 */     Object localObject1 = orb == null ? null : orb.get();
/*      */ 
/* 1752 */     if (localObject1 != null) return localObject1;
/*      */ 
/* 1754 */     Object localObject2 = IIOPHelper.createOrb((String[])null, (Properties)null);
/*      */ 
/* 1756 */     orb = new WeakReference(localObject2);
/* 1757 */     return localObject2;
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1773 */     paramObjectInputStream.defaultReadObject();
/*      */ 
/* 1775 */     if ((this.rmiServer == null) && (this.jmxServiceURL == null)) throw new InvalidObjectException("rmiServer and jmxServiceURL both null");
/*      */ 
/* 1778 */     initTransients();
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/* 1814 */     if ((this.rmiServer == null) && (this.jmxServiceURL == null)) throw new InvalidObjectException("rmiServer and jmxServiceURL both null.");
/*      */ 
/* 1816 */     connectStub(this.rmiServer, this.env);
/* 1817 */     paramObjectOutputStream.defaultWriteObject();
/*      */   }
/*      */ 
/*      */   private void initTransients()
/*      */   {
/* 1822 */     this.rmbscMap = new WeakHashMap();
/* 1823 */     this.connected = false;
/* 1824 */     this.terminated = false;
/*      */ 
/* 1826 */     this.connectionBroadcaster = new NotificationBroadcasterSupport();
/*      */   }
/*      */ 
/*      */   private static void checkStub(Remote paramRemote, Class<?> paramClass)
/*      */   {
/* 1838 */     if (paramRemote.getClass() != paramClass) {
/* 1839 */       if (!Proxy.isProxyClass(paramRemote.getClass())) {
/* 1840 */         throw new SecurityException("Expecting a " + paramClass.getName() + " stub!");
/*      */       }
/*      */ 
/* 1843 */       localObject = Proxy.getInvocationHandler(paramRemote);
/* 1844 */       if (localObject.getClass() != RemoteObjectInvocationHandler.class) {
/* 1845 */         throw new SecurityException("Expecting a dynamic proxy instance with a " + RemoteObjectInvocationHandler.class.getName() + " invocation handler!");
/*      */       }
/*      */ 
/* 1850 */       paramRemote = (Remote)localObject;
/*      */     }
/*      */ 
/* 1857 */     Object localObject = ((RemoteObject)paramRemote).getRef();
/* 1858 */     if (localObject.getClass() != UnicastRef2.class) {
/* 1859 */       throw new SecurityException("Expecting a " + UnicastRef2.class.getName() + " remote reference in stub!");
/*      */     }
/*      */ 
/* 1866 */     LiveRef localLiveRef = ((UnicastRef2)localObject).getLiveRef();
/* 1867 */     RMIClientSocketFactory localRMIClientSocketFactory = localLiveRef.getClientSocketFactory();
/* 1868 */     if ((localRMIClientSocketFactory == null) || (localRMIClientSocketFactory.getClass() != SslRMIClientSocketFactory.class))
/* 1869 */       throw new SecurityException("Expecting a " + SslRMIClientSocketFactory.class.getName() + " RMI client socket factory in stub!");
/*      */   }
/*      */ 
/*      */   private RMIServer findRMIServer(JMXServiceURL paramJMXServiceURL, Map<String, Object> paramMap)
/*      */     throws NamingException, IOException
/*      */   {
/* 1881 */     boolean bool = RMIConnectorServer.isIiopURL(paramJMXServiceURL, true);
/* 1882 */     if (bool)
/*      */     {
/* 1884 */       paramMap.put("java.naming.corba.orb", resolveOrb(paramMap));
/*      */     }
/*      */ 
/* 1887 */     String str1 = paramJMXServiceURL.getURLPath();
/* 1888 */     int i = str1.indexOf(';');
/* 1889 */     if (i < 0) i = str1.length();
/* 1890 */     if (str1.startsWith("/jndi/"))
/* 1891 */       return findRMIServerJNDI(str1.substring(6, i), paramMap, bool);
/* 1892 */     if (str1.startsWith("/stub/"))
/* 1893 */       return findRMIServerJRMP(str1.substring(6, i), paramMap, bool);
/* 1894 */     if (str1.startsWith("/ior/")) {
/* 1895 */       if (!IIOPHelper.isAvailable())
/* 1896 */         throw new IOException("iiop protocol not available");
/* 1897 */       return findRMIServerIIOP(str1.substring(5, i), paramMap, bool);
/*      */     }
/* 1899 */     String str2 = "URL path must begin with /jndi/ or /stub/ or /ior/: " + str1;
/*      */ 
/* 1901 */     throw new MalformedURLException(str2);
/*      */   }
/*      */ 
/*      */   private RMIServer findRMIServerJNDI(String paramString, Map<String, ?> paramMap, boolean paramBoolean)
/*      */     throws NamingException
/*      */   {
/* 1922 */     InitialContext localInitialContext = new InitialContext(EnvHelp.mapToHashtable(paramMap));
/*      */ 
/* 1924 */     Object localObject = localInitialContext.lookup(paramString);
/* 1925 */     localInitialContext.close();
/*      */ 
/* 1927 */     if (paramBoolean) {
/* 1928 */       return narrowIIOPServer(localObject);
/*      */     }
/* 1930 */     return narrowJRMPServer(localObject);
/*      */   }
/*      */ 
/*      */   private static RMIServer narrowJRMPServer(Object paramObject)
/*      */   {
/* 1935 */     return (RMIServer)paramObject;
/*      */   }
/*      */ 
/*      */   private static RMIServer narrowIIOPServer(Object paramObject) {
/*      */     try {
/* 1940 */       return (RMIServer)IIOPHelper.narrow(paramObject, RMIServer.class);
/*      */     } catch (ClassCastException localClassCastException) {
/* 1942 */       if (logger.traceOn()) {
/* 1943 */         logger.trace("narrowIIOPServer", "Failed to narrow objref=" + paramObject + ": " + localClassCastException);
/*      */       }
/* 1945 */       if (logger.debugOn()) logger.debug("narrowIIOPServer", localClassCastException); 
/*      */     }
/* 1946 */     return null;
/*      */   }
/*      */ 
/*      */   private RMIServer findRMIServerIIOP(String paramString, Map<String, ?> paramMap, boolean paramBoolean)
/*      */   {
/* 1952 */     Object localObject1 = paramMap.get("java.naming.corba.orb");
/* 1953 */     Object localObject2 = IIOPHelper.stringToObject(localObject1, paramString);
/* 1954 */     return (RMIServer)IIOPHelper.narrow(localObject2, RMIServer.class);
/*      */   }
/*      */ 
/*      */   private RMIServer findRMIServerJRMP(String paramString, Map<String, ?> paramMap, boolean paramBoolean) throws IOException
/*      */   {
/*      */     byte[] arrayOfByte;
/*      */     try
/*      */     {
/* 1962 */       arrayOfByte = base64ToByteArray(paramString);
/*      */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 1964 */       throw new MalformedURLException("Bad BASE64 encoding: " + localIllegalArgumentException.getMessage());
/*      */     }
/*      */ 
/* 1967 */     ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
/*      */ 
/* 1969 */     ClassLoader localClassLoader = EnvHelp.resolveClientClassLoader(paramMap);
/* 1970 */     ObjectInputStreamWithLoader localObjectInputStreamWithLoader = localClassLoader == null ? new ObjectInputStream(localByteArrayInputStream) : new ObjectInputStreamWithLoader(localByteArrayInputStream, localClassLoader);
/*      */     Object localObject;
/*      */     try {
/* 1976 */       localObject = localObjectInputStreamWithLoader.readObject();
/*      */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 1978 */       throw new MalformedURLException("Class not found: " + localClassNotFoundException);
/*      */     }
/* 1980 */     return (RMIServer)localObject;
/*      */   }
/*      */ 
/*      */   private static RMIConnection shadowJrmpStub(RemoteObject paramRemoteObject)
/*      */     throws InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException
/*      */   {
/* 2141 */     RemoteRef localRemoteRef1 = paramRemoteObject.getRef();
/* 2142 */     RemoteRef localRemoteRef2 = (RemoteRef)proxyRefConstructor.newInstance(new Object[] { localRemoteRef1 });
/*      */ 
/* 2144 */     Constructor localConstructor = rmiConnectionImplStubClass.getConstructor(new Class[] { RemoteRef.class });
/*      */ 
/* 2146 */     Object[] arrayOfObject = { localRemoteRef2 };
/* 2147 */     RMIConnection localRMIConnection = (RMIConnection)localConstructor.newInstance(arrayOfObject);
/*      */ 
/* 2149 */     return localRMIConnection;
/*      */   }
/*      */ 
/*      */   private static RMIConnection shadowIiopStub(Object paramObject)
/*      */     throws InstantiationException, IllegalAccessException
/*      */   {
/* 2361 */     Object localObject = proxyStubClass.newInstance();
/* 2362 */     IIOPHelper.setDelegate(localObject, IIOPHelper.getDelegate(paramObject));
/* 2363 */     return (RMIConnection)localObject;
/*      */   }
/*      */ 
/*      */   private static RMIConnection getConnection(RMIServer paramRMIServer, Object paramObject, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 2370 */     RMIConnection localRMIConnection = paramRMIServer.newClient(paramObject);
/* 2371 */     if (paramBoolean) checkStub(localRMIConnection, rmiConnectionImplStubClass); try
/*      */     {
/* 2373 */       if (localRMIConnection.getClass() == rmiConnectionImplStubClass)
/* 2374 */         return shadowJrmpStub((RemoteObject)localRMIConnection);
/* 2375 */       if (localRMIConnection.getClass().getName().equals("org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub"))
/* 2376 */         return shadowIiopStub(localRMIConnection);
/* 2377 */       logger.trace("getConnection", "Did not wrap " + localRMIConnection.getClass() + " to foil " + "stack search for classes: class loading semantics " + "may be incorrect");
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 2382 */       logger.error("getConnection", "Could not wrap " + localRMIConnection.getClass() + " to foil " + "stack search for classes: class loading semantics " + "may be incorrect: " + localException);
/*      */ 
/* 2386 */       logger.debug("getConnection", localException);
/*      */     }
/*      */ 
/* 2390 */     return localRMIConnection;
/*      */   }
/*      */ 
/*      */   private static byte[] base64ToByteArray(String paramString) {
/* 2394 */     int i = paramString.length();
/* 2395 */     int j = i / 4;
/* 2396 */     if (4 * j != i) {
/* 2397 */       throw new IllegalArgumentException("String length must be a multiple of four.");
/*      */     }
/* 2399 */     int k = 0;
/* 2400 */     int m = j;
/* 2401 */     if (i != 0) {
/* 2402 */       if (paramString.charAt(i - 1) == '=') {
/* 2403 */         k++;
/* 2404 */         m--;
/*      */       }
/* 2406 */       if (paramString.charAt(i - 2) == '=')
/* 2407 */         k++;
/*      */     }
/* 2409 */     byte[] arrayOfByte = new byte[3 * j - k];
/*      */ 
/* 2412 */     int n = 0; int i1 = 0;
/*      */     int i3;
/*      */     int i4;
/* 2413 */     for (int i2 = 0; i2 < m; i2++) {
/* 2414 */       i3 = base64toInt(paramString.charAt(n++));
/* 2415 */       i4 = base64toInt(paramString.charAt(n++));
/* 2416 */       int i5 = base64toInt(paramString.charAt(n++));
/* 2417 */       int i6 = base64toInt(paramString.charAt(n++));
/* 2418 */       arrayOfByte[(i1++)] = ((byte)(i3 << 2 | i4 >> 4));
/* 2419 */       arrayOfByte[(i1++)] = ((byte)(i4 << 4 | i5 >> 2));
/* 2420 */       arrayOfByte[(i1++)] = ((byte)(i5 << 6 | i6));
/*      */     }
/*      */ 
/* 2424 */     if (k != 0) {
/* 2425 */       i2 = base64toInt(paramString.charAt(n++));
/* 2426 */       i3 = base64toInt(paramString.charAt(n++));
/* 2427 */       arrayOfByte[(i1++)] = ((byte)(i2 << 2 | i3 >> 4));
/*      */ 
/* 2429 */       if (k == 1) {
/* 2430 */         i4 = base64toInt(paramString.charAt(n++));
/* 2431 */         arrayOfByte[(i1++)] = ((byte)(i3 << 4 | i4 >> 2));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2436 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */   private static int base64toInt(char paramChar)
/*      */   {
/*      */     int i;
/* 2449 */     if (paramChar >= base64ToInt.length)
/* 2450 */       i = -1;
/*      */     else {
/* 2452 */       i = base64ToInt[paramChar];
/*      */     }
/* 2454 */     if (i < 0)
/* 2455 */       throw new IllegalArgumentException("Illegal character " + paramChar);
/* 2456 */     return i;
/*      */   }
/*      */ 
/*      */   private ClassLoader pushDefaultClassLoader()
/*      */   {
/* 2480 */     final Thread localThread = Thread.currentThread();
/* 2481 */     ClassLoader localClassLoader = localThread.getContextClassLoader();
/* 2482 */     if (this.defaultClassLoader != null)
/* 2483 */       AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Void run() {
/* 2485 */           localThread.setContextClassLoader(RMIConnector.this.defaultClassLoader);
/* 2486 */           return null;
/*      */         }
/*      */       });
/* 2489 */     return localClassLoader;
/*      */   }
/*      */ 
/*      */   private void popDefaultClassLoader(final ClassLoader paramClassLoader) {
/* 2493 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/* 2495 */         Thread.currentThread().setContextClassLoader(paramClassLoader);
/* 2496 */         return null;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static String objects(Object[] paramArrayOfObject)
/*      */   {
/* 2567 */     if (paramArrayOfObject == null) {
/* 2568 */       return "null";
/*      */     }
/* 2570 */     return Arrays.asList(paramArrayOfObject).toString();
/*      */   }
/*      */ 
/*      */   private static String strings(String[] paramArrayOfString) {
/* 2574 */     return objects(paramArrayOfString);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/* 2087 */     byte[] arrayOfByte = NoCallStackClassLoader.stringToBytes("");
/*      */ 
/* 2089 */     Object localObject1 = new PrivilegedExceptionAction()
/*      */     {
/*      */       public Constructor<?> run() throws Exception {
/* 2092 */         RMIConnector localRMIConnector = RMIConnector.class;
/* 2093 */         ClassLoader localClassLoader = localRMIConnector.getClassLoader();
/* 2094 */         ProtectionDomain localProtectionDomain = localRMIConnector.getProtectionDomain();
/*      */ 
/* 2096 */         String[] arrayOfString = { ProxyRef.class.getName() };
/* 2097 */         NoCallStackClassLoader localNoCallStackClassLoader = new NoCallStackClassLoader("com.sun.jmx.remote.internal.PRef", this.val$pRefByteCode, arrayOfString, localClassLoader, localProtectionDomain);
/*      */ 
/* 2103 */         Class localClass = localNoCallStackClassLoader.loadClass("com.sun.jmx.remote.internal.PRef");
/* 2104 */         return localClass.getConstructor(new Class[] { RemoteRef.class });
/*      */       }
/*      */ 
/*      */     };
/*      */     try
/*      */     {
/* 2110 */       localObject2 = Class.forName(rmiServerImplStubClassName);
/*      */     } catch (Exception localException1) {
/* 2112 */       logger.error("<clinit>", "Failed to instantiate " + rmiServerImplStubClassName + ": " + localException1);
/*      */ 
/* 2115 */       logger.debug("<clinit>", localException1);
/* 2116 */       localObject2 = null;
/*      */     }
/* 2118 */     rmiServerImplStubClass = (Class)localObject2;
/*      */     try
/*      */     {
/* 2123 */       localObject3 = Class.forName(rmiConnectionImplStubClassName);
/* 2124 */       localObject4 = (Constructor)AccessController.doPrivileged((PrivilegedExceptionAction)localObject1);
/*      */     } catch (Exception localException2) {
/* 2126 */       logger.error("<clinit>", "Failed to initialize proxy reference constructor for " + rmiConnectionImplStubClassName + ": " + localException2);
/*      */ 
/* 2129 */       logger.debug("<clinit>", localException2);
/* 2130 */       localObject3 = null;
/* 2131 */       localObject4 = null;
/*      */     }
/* 2133 */     rmiConnectionImplStubClass = (Class)localObject3;
/* 2134 */     proxyRefConstructor = (Constructor)localObject4;
/*      */ 
/* 2317 */     localObject1 = NoCallStackClassLoader.stringToBytes("");
/*      */ 
/* 2319 */     Object localObject2 = NoCallStackClassLoader.stringToBytes("");
/*      */ 
/* 2321 */     Object localObject3 = { "com.sun.jmx.remote.protocol.iiop.ProxyStub", "com.sun.jmx.remote.protocol.iiop.PInputStream" };
/* 2322 */     Object localObject4 = { localObject1, localObject2 };
/* 2323 */     final String[] arrayOfString = { "org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub", "com.sun.jmx.remote.protocol.iiop.ProxyInputStream" };
/*      */ 
/* 2327 */     if (IIOPHelper.isAvailable()) {
/* 2328 */       PrivilegedExceptionAction local2 = new PrivilegedExceptionAction()
/*      */       {
/*      */         public Class<?> run() throws Exception {
/* 2331 */           RMIConnector localRMIConnector = RMIConnector.class;
/* 2332 */           ClassLoader localClassLoader = localRMIConnector.getClassLoader();
/* 2333 */           ProtectionDomain localProtectionDomain = localRMIConnector.getProtectionDomain();
/*      */ 
/* 2335 */           NoCallStackClassLoader localNoCallStackClassLoader = new NoCallStackClassLoader(this.val$classNames, this.val$byteCodes, arrayOfString, localClassLoader, localProtectionDomain);
/*      */ 
/* 2341 */           return localNoCallStackClassLoader.loadClass("com.sun.jmx.remote.protocol.iiop.ProxyStub");
/*      */         } } ;
/*      */       Class localClass;
/*      */       try {
/* 2346 */         localClass = (Class)AccessController.doPrivileged(local2);
/*      */       } catch (Exception localException3) {
/* 2348 */         logger.error("<clinit>", "Unexpected exception making shadow IIOP stub class: " + localException3);
/*      */ 
/* 2350 */         logger.debug("<clinit>", localException3);
/* 2351 */         localClass = null;
/*      */       }
/* 2353 */       proxyStubClass = localClass;
/*      */     } else {
/* 2355 */       proxyStubClass = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class ObjectInputStreamWithLoader extends ObjectInputStream
/*      */   {
/*      */     private final ClassLoader loader;
/*      */ 
/*      */     ObjectInputStreamWithLoader(InputStream paramInputStream, ClassLoader paramClassLoader)
/*      */       throws IOException
/*      */     {
/* 1987 */       super();
/* 1988 */       this.loader = paramClassLoader;
/*      */     }
/*      */ 
/*      */     protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/* 1994 */       return Class.forName(paramObjectStreamClass.getName(), false, this.loader);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class RMIClientCommunicatorAdmin extends ClientCommunicatorAdmin
/*      */   {
/*      */     public RMIClientCommunicatorAdmin(long arg2)
/*      */     {
/* 1462 */       super();
/*      */     }
/*      */ 
/*      */     public void gotIOException(IOException paramIOException) throws IOException
/*      */     {
/* 1467 */       if ((paramIOException instanceof NoSuchObjectException))
/*      */       {
/* 1469 */         super.gotIOException(paramIOException);
/*      */ 
/* 1471 */         return;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1476 */         RMIConnector.this.connection.getDefaultDomain(null);
/*      */       } catch (IOException localIOException) {
/* 1478 */         int i = 0;
/*      */ 
/* 1480 */         synchronized (this) {
/* 1481 */           if (!RMIConnector.this.terminated) {
/* 1482 */             RMIConnector.this.terminated = true;
/*      */ 
/* 1484 */             i = 1;
/*      */           }
/*      */         }
/*      */ 
/* 1488 */         if (i != 0)
/*      */         {
/* 1491 */           ??? = new JMXConnectionNotification("jmx.remote.connection.failed", this, RMIConnector.this.connectionId, RMIConnector.access$1308(RMIConnector.this), "Failed to communicate with the server: " + paramIOException.toString(), paramIOException);
/*      */ 
/* 1500 */           RMIConnector.this.sendNotification((Notification)???);
/*      */           try
/*      */           {
/* 1503 */             RMIConnector.this.close(true);
/*      */           }
/*      */           catch (Exception localException)
/*      */           {
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1512 */       if ((paramIOException instanceof ServerException))
/*      */       {
/* 1522 */         Throwable localThrowable = ((ServerException)paramIOException).detail;
/*      */ 
/* 1524 */         if ((localThrowable instanceof IOException))
/* 1525 */           throw ((IOException)localThrowable);
/* 1526 */         if ((localThrowable instanceof RuntimeException)) {
/* 1527 */           throw ((RuntimeException)localThrowable);
/*      */         }
/*      */       }
/*      */ 
/* 1531 */       throw paramIOException;
/*      */     }
/*      */ 
/*      */     public void reconnectNotificationListeners(ClientListenerInfo[] paramArrayOfClientListenerInfo) throws IOException {
/* 1535 */       int i = paramArrayOfClientListenerInfo.length;
/*      */ 
/* 1538 */       ClientListenerInfo[] arrayOfClientListenerInfo1 = new ClientListenerInfo[i];
/*      */ 
/* 1540 */       Subject[] arrayOfSubject = new Subject[i];
/* 1541 */       ObjectName[] arrayOfObjectName = new ObjectName[i];
/* 1542 */       NotificationListener[] arrayOfNotificationListener = new NotificationListener[i];
/* 1543 */       NotificationFilter[] arrayOfNotificationFilter = new NotificationFilter[i];
/* 1544 */       MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])Util.cast(new MarshalledObject[i]);
/*      */ 
/* 1546 */       Object[] arrayOfObject = new Object[i];
/*      */ 
/* 1548 */       for (int j = 0; j < i; j++) {
/* 1549 */         arrayOfSubject[j] = paramArrayOfClientListenerInfo[j].getDelegationSubject();
/* 1550 */         arrayOfObjectName[j] = paramArrayOfClientListenerInfo[j].getObjectName();
/* 1551 */         arrayOfNotificationListener[j] = paramArrayOfClientListenerInfo[j].getListener();
/* 1552 */         arrayOfNotificationFilter[j] = paramArrayOfClientListenerInfo[j].getNotificationFilter();
/* 1553 */         arrayOfMarshalledObject[j] = new MarshalledObject(arrayOfNotificationFilter[j]);
/* 1554 */         arrayOfObject[j] = paramArrayOfClientListenerInfo[j].getHandback();
/*      */       }
/*      */       try
/*      */       {
/* 1558 */         Integer[] arrayOfInteger = RMIConnector.this.addListenersWithSubjects(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject, false);
/*      */ 
/* 1560 */         for (j = 0; j < i; j++) {
/* 1561 */           arrayOfClientListenerInfo1[j] = new ClientListenerInfo(arrayOfInteger[j], arrayOfObjectName[j], arrayOfNotificationListener[j], arrayOfNotificationFilter[j], arrayOfObject[j], arrayOfSubject[j]);
/*      */         }
/*      */ 
/* 1569 */         RMIConnector.this.rmiNotifClient.postReconnection(arrayOfClientListenerInfo1);
/*      */ 
/* 1571 */         return;
/*      */       }
/*      */       catch (InstanceNotFoundException localInstanceNotFoundException1)
/*      */       {
/* 1576 */         int k = 0;
/* 1577 */         for (j = 0; j < i; j++) {
/*      */           try {
/* 1579 */             Integer localInteger = RMIConnector.this.addListenerWithSubject(arrayOfObjectName[j], new MarshalledObject(arrayOfNotificationFilter[j]), arrayOfSubject[j], false);
/*      */ 
/* 1584 */             arrayOfClientListenerInfo1[(k++)] = new ClientListenerInfo(localInteger, arrayOfObjectName[j], arrayOfNotificationListener[j], arrayOfNotificationFilter[j], arrayOfObject[j], arrayOfSubject[j]);
/*      */           }
/*      */           catch (InstanceNotFoundException localInstanceNotFoundException2)
/*      */           {
/* 1591 */             RMIConnector.logger.warning("reconnectNotificationListeners", "Can't reconnect listener for " + arrayOfObjectName[j]);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1597 */         if (k != i) {
/* 1598 */           ClientListenerInfo[] arrayOfClientListenerInfo2 = arrayOfClientListenerInfo1;
/* 1599 */           arrayOfClientListenerInfo1 = new ClientListenerInfo[k];
/* 1600 */           System.arraycopy(arrayOfClientListenerInfo2, 0, arrayOfClientListenerInfo1, 0, k);
/*      */         }
/*      */ 
/* 1603 */         RMIConnector.this.rmiNotifClient.postReconnection(arrayOfClientListenerInfo1);
/*      */       }
/*      */     }
/*      */ 
/* 1607 */     protected void checkConnection() throws IOException { if (RMIConnector.logger.debugOn()) {
/* 1608 */         RMIConnector.logger.debug("RMIClientCommunicatorAdmin-checkConnection", "Calling the method getDefaultDomain.");
/*      */       }
/*      */ 
/* 1611 */       RMIConnector.this.connection.getDefaultDomain(null); }
/*      */ 
/*      */     protected void doStart()
/*      */       throws IOException
/*      */     {
/*      */       try
/*      */       {
/* 1618 */         localRMIServer = RMIConnector.this.rmiServer != null ? RMIConnector.this.rmiServer : RMIConnector.this.findRMIServer(RMIConnector.this.jmxServiceURL, RMIConnector.this.env);
/*      */       }
/*      */       catch (NamingException localNamingException) {
/* 1621 */         throw new IOException("Failed to get a RMI stub: " + localNamingException);
/*      */       }
/*      */ 
/* 1625 */       RMIServer localRMIServer = RMIConnector.connectStub(localRMIServer, RMIConnector.this.env);
/*      */ 
/* 1628 */       Object localObject = RMIConnector.this.env.get("jmx.remote.credentials");
/* 1629 */       RMIConnector.this.connection = localRMIServer.newClient(localObject);
/*      */ 
/* 1632 */       ClientListenerInfo[] arrayOfClientListenerInfo = RMIConnector.this.rmiNotifClient.preReconnection();
/*      */ 
/* 1634 */       reconnectNotificationListeners(arrayOfClientListenerInfo);
/*      */ 
/* 1636 */       RMIConnector.this.connectionId = RMIConnector.this.getConnectionId();
/*      */ 
/* 1638 */       JMXConnectionNotification localJMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.opened", this, RMIConnector.this.connectionId, RMIConnector.access$1308(RMIConnector.this), "Reconnected to server", null);
/*      */ 
/* 1645 */       RMIConnector.this.sendNotification(localJMXConnectionNotification);
/*      */     }
/*      */ 
/*      */     protected void doStop()
/*      */     {
/*      */       try {
/* 1651 */         RMIConnector.this.close();
/*      */       } catch (IOException localIOException) {
/* 1653 */         RMIConnector.logger.warning("RMIClientCommunicatorAdmin-doStop", "Failed to call the method close():" + localIOException);
/*      */ 
/* 1655 */         RMIConnector.logger.debug("RMIClientCommunicatorAdmin-doStop", localIOException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class RMINotifClient extends ClientNotifForwarder
/*      */   {
/*      */     public RMINotifClient(Map<String, ?> arg2)
/*      */     {
/* 1326 */       super(localMap);
/*      */     }
/*      */ 
/*      */     protected NotificationResult fetchNotifs(long paramLong1, int paramInt, long paramLong2)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*      */       try
/*      */       {
/* 1337 */         return RMIConnector.this.connection.fetchNotifications(paramLong1, paramInt, paramLong2);
/*      */       } catch (IOException localIOException2) {
/*      */         IOException localIOException1;
/*      */         while (true) {
/* 1341 */           localIOException1 = localIOException2;
/*      */           try
/*      */           {
/* 1345 */             RMIConnector.this.communicatorAdmin.gotIOException(localIOException2);
/*      */           }
/*      */           catch (IOException localIOException3)
/*      */           {
/*      */             Object localObject;
/* 1357 */             if ((localIOException1 instanceof UnmarshalException)) {
/* 1358 */               localObject = (UnmarshalException)localIOException1;
/*      */ 
/* 1360 */               if ((((UnmarshalException)localObject).detail instanceof ClassNotFoundException)) {
/* 1361 */                 throw ((ClassNotFoundException)((UnmarshalException)localObject).detail);
/*      */               }
/*      */ 
/* 1376 */               if ((((UnmarshalException)localObject).detail instanceof WriteAbortedException)) {
/* 1377 */                 WriteAbortedException localWriteAbortedException = (WriteAbortedException)((UnmarshalException)localObject).detail;
/*      */ 
/* 1379 */                 if ((localWriteAbortedException.detail instanceof IOException))
/* 1380 */                   throw ((IOException)localWriteAbortedException.detail);
/*      */               }
/* 1382 */             } else if ((localIOException1 instanceof MarshalException))
/*      */             {
/* 1385 */               localObject = (MarshalException)localIOException1;
/* 1386 */               if ((((MarshalException)localObject).detail instanceof NotSerializableException)) {
/* 1387 */                 throw ((NotSerializableException)((MarshalException)localObject).detail);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1392 */         throw localIOException1; }  } 
/* 1397 */     protected Integer addListenerForMBeanRemovedNotif() throws IOException, InstanceNotFoundException { NotificationFilterSupport localNotificationFilterSupport = new NotificationFilterSupport();
/*      */ 
/* 1399 */       localNotificationFilterSupport.enableType("JMX.mbean.unregistered");
/*      */ 
/* 1401 */       MarshalledObject localMarshalledObject = new MarshalledObject(localNotificationFilterSupport);
/*      */ 
/* 1405 */       ObjectName[] arrayOfObjectName = { MBeanServerDelegate.DELEGATE_NAME };
/*      */ 
/* 1407 */       MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])Util.cast(new MarshalledObject[] { localMarshalledObject });
/*      */ 
/* 1409 */       Subject[] arrayOfSubject = { null };
/*      */       Integer[] arrayOfInteger;
/*      */       try { arrayOfInteger = RMIConnector.this.connection.addNotificationListeners(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject); }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1417 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1419 */         arrayOfInteger = RMIConnector.this.connection.addNotificationListeners(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject);
/*      */       }
/*      */ 
/* 1424 */       return arrayOfInteger[0]; }
/*      */ 
/*      */     protected void removeListenerForMBeanRemovedNotif(Integer paramInteger)
/*      */       throws IOException, InstanceNotFoundException, ListenerNotFoundException
/*      */     {
/*      */       try
/*      */       {
/* 1431 */         RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[] { paramInteger }, null);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1436 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1438 */         RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[] { paramInteger }, null);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void lostNotifs(String paramString, long paramLong)
/*      */     {
/* 1449 */       JMXConnectionNotification localJMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.notifs.lost", RMIConnector.this, RMIConnector.this.connectionId, RMIConnector.access$1008(RMIConnector.this), paramString, Long.valueOf(paramLong));
/*      */ 
/* 1456 */       RMIConnector.this.sendNotification(localJMXConnectionNotification);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class RemoteMBeanServerConnection
/*      */     implements MBeanServerConnection
/*      */   {
/*      */     private Subject delegationSubject;
/*      */ 
/*      */     public RemoteMBeanServerConnection()
/*      */     {
/*  624 */       this(null);
/*      */     }
/*      */ 
/*      */     public RemoteMBeanServerConnection(Subject arg2)
/*      */     {
/*      */       Object localObject;
/*  628 */       this.delegationSubject = localObject;
/*      */     }
/*      */ 
/*      */     public ObjectInstance createMBean(String paramString, ObjectName paramObjectName)
/*      */       throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
/*      */     {
/*  639 */       if (RMIConnector.logger.debugOn()) {
/*  640 */         RMIConnector.logger.debug("createMBean(String,ObjectName)", "className=" + paramString + ", name=" + paramObjectName);
/*      */       }
/*      */ 
/*  644 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  646 */         return RMIConnector.this.connection.createMBean(paramString, paramObjectName, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  650 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  652 */         return RMIConnector.this.connection.createMBean(paramString, paramObjectName, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/*  656 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2)
/*      */       throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
/*      */     {
/*  671 */       if (RMIConnector.logger.debugOn()) {
/*  672 */         RMIConnector.logger.debug("createMBean(String,ObjectName,ObjectName)", "className=" + paramString + ", name=" + paramObjectName1 + ", loaderName=" + paramObjectName2 + ")");
/*      */       }
/*      */ 
/*  677 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  679 */         return RMIConnector.this.connection.createMBean(paramString, paramObjectName1, paramObjectName2, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  685 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  687 */         return RMIConnector.this.connection.createMBean(paramString, paramObjectName1, paramObjectName2, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/*  693 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*      */       throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
/*      */     {
/*  707 */       if (RMIConnector.logger.debugOn()) {
/*  708 */         RMIConnector.logger.debug("createMBean(String,ObjectName,Object[],String[])", "className=" + paramString + ", name=" + paramObjectName + ", params=" + RMIConnector.objects(paramArrayOfObject) + ", signature=" + RMIConnector.strings(paramArrayOfString));
/*      */       }
/*      */ 
/*  714 */       MarshalledObject localMarshalledObject = new MarshalledObject(paramArrayOfObject);
/*      */ 
/*  716 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  718 */         return RMIConnector.this.connection.createMBean(paramString, paramObjectName, localMarshalledObject, paramArrayOfString, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  724 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  726 */         return RMIConnector.this.connection.createMBean(paramString, paramObjectName, localMarshalledObject, paramArrayOfString, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/*  732 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*      */       throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
/*      */     {
/*  748 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "className=" + paramString + ", name=" + paramObjectName1 + ", loaderName=" + paramObjectName2 + ", params=" + RMIConnector.objects(paramArrayOfObject) + ", signature=" + RMIConnector.strings(paramArrayOfString));
/*      */ 
/*  754 */       MarshalledObject localMarshalledObject = new MarshalledObject(paramArrayOfObject);
/*      */ 
/*  756 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  758 */         return RMIConnector.this.connection.createMBean(paramString, paramObjectName1, paramObjectName2, localMarshalledObject, paramArrayOfString, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  765 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  767 */         return RMIConnector.this.connection.createMBean(paramString, paramObjectName1, paramObjectName2, localMarshalledObject, paramArrayOfString, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/*  774 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void unregisterMBean(ObjectName paramObjectName)
/*      */       throws InstanceNotFoundException, MBeanRegistrationException, IOException
/*      */     {
/*  782 */       if (RMIConnector.logger.debugOn()) {
/*  783 */         RMIConnector.logger.debug("unregisterMBean", "name=" + paramObjectName);
/*      */       }
/*  785 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  787 */         RMIConnector.this.connection.unregisterMBean(paramObjectName, this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/*  789 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  791 */         RMIConnector.this.connection.unregisterMBean(paramObjectName, this.delegationSubject);
/*      */       } finally {
/*  793 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public ObjectInstance getObjectInstance(ObjectName paramObjectName)
/*      */       throws InstanceNotFoundException, IOException
/*      */     {
/*  800 */       if (RMIConnector.logger.debugOn()) {
/*  801 */         RMIConnector.logger.debug("getObjectInstance", "name=" + paramObjectName);
/*      */       }
/*  803 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  805 */         return RMIConnector.this.connection.getObjectInstance(paramObjectName, this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/*  807 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  809 */         return RMIConnector.this.connection.getObjectInstance(paramObjectName, this.delegationSubject);
/*      */       } finally {
/*  811 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp)
/*      */       throws IOException
/*      */     {
/*  818 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("queryMBeans", "name=" + paramObjectName + ", query=" + paramQueryExp);
/*      */ 
/*  821 */       MarshalledObject localMarshalledObject = new MarshalledObject(paramQueryExp);
/*      */ 
/*  823 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  825 */         return RMIConnector.this.connection.queryMBeans(paramObjectName, localMarshalledObject, this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/*  827 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  829 */         return RMIConnector.this.connection.queryMBeans(paramObjectName, localMarshalledObject, this.delegationSubject);
/*      */       } finally {
/*  831 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp)
/*      */       throws IOException
/*      */     {
/*  838 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("queryNames", "name=" + paramObjectName + ", query=" + paramQueryExp);
/*      */ 
/*  841 */       MarshalledObject localMarshalledObject = new MarshalledObject(paramQueryExp);
/*      */ 
/*  843 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  845 */         return RMIConnector.this.connection.queryNames(paramObjectName, localMarshalledObject, this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/*  847 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  849 */         return RMIConnector.this.connection.queryNames(paramObjectName, localMarshalledObject, this.delegationSubject);
/*      */       } finally {
/*  851 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean isRegistered(ObjectName paramObjectName) throws IOException
/*      */     {
/*  857 */       if (RMIConnector.logger.debugOn()) {
/*  858 */         RMIConnector.logger.debug("isRegistered", "name=" + paramObjectName);
/*      */       }
/*  860 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  862 */         return RMIConnector.this.connection.isRegistered(paramObjectName, this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/*  864 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  866 */         return RMIConnector.this.connection.isRegistered(paramObjectName, this.delegationSubject);
/*      */       } finally {
/*  868 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Integer getMBeanCount() throws IOException
/*      */     {
/*  874 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("getMBeanCount", "");
/*      */ 
/*  876 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  878 */         return RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/*  880 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  882 */         return RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
/*      */       } finally {
/*  884 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object getAttribute(ObjectName paramObjectName, String paramString)
/*      */       throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
/*      */     {
/*  895 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("getAttribute", "name=" + paramObjectName + ", attribute=" + paramString);
/*      */ 
/*  899 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  901 */         return RMIConnector.this.connection.getAttribute(paramObjectName, paramString, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  905 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  907 */         return RMIConnector.this.connection.getAttribute(paramObjectName, paramString, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/*  911 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString)
/*      */       throws InstanceNotFoundException, ReflectionException, IOException
/*      */     {
/*  920 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("getAttributes", "name=" + paramObjectName + ", attributes=" + RMIConnector.strings(paramArrayOfString));
/*      */ 
/*  924 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  926 */         return RMIConnector.this.connection.getAttributes(paramObjectName, paramArrayOfString, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  931 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  933 */         return RMIConnector.this.connection.getAttributes(paramObjectName, paramArrayOfString, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/*  937 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setAttribute(ObjectName paramObjectName, Attribute paramAttribute)
/*      */       throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
/*      */     {
/*  951 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("setAttribute", "name=" + paramObjectName + ", attribute=" + paramAttribute);
/*      */ 
/*  955 */       MarshalledObject localMarshalledObject = new MarshalledObject(paramAttribute);
/*      */ 
/*  957 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  959 */         RMIConnector.this.connection.setAttribute(paramObjectName, localMarshalledObject, this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/*  961 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  963 */         RMIConnector.this.connection.setAttribute(paramObjectName, localMarshalledObject, this.delegationSubject);
/*      */       } finally {
/*  965 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList)
/*      */       throws InstanceNotFoundException, ReflectionException, IOException
/*      */     {
/*  975 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("setAttributes", "name=" + paramObjectName + ", attributes=" + paramAttributeList);
/*      */ 
/*  979 */       MarshalledObject localMarshalledObject = new MarshalledObject(paramAttributeList);
/*      */ 
/*  981 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/*  983 */         return RMIConnector.this.connection.setAttributes(paramObjectName, localMarshalledObject, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*  987 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/*  989 */         return RMIConnector.this.connection.setAttributes(paramObjectName, localMarshalledObject, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/*  993 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*      */       throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
/*      */     {
/* 1007 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("invoke", "name=" + paramObjectName + ", operationName=" + paramString + ", params=" + RMIConnector.objects(paramArrayOfObject) + ", signature=" + RMIConnector.strings(paramArrayOfString));
/*      */ 
/* 1013 */       MarshalledObject localMarshalledObject = new MarshalledObject(paramArrayOfObject);
/*      */ 
/* 1015 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/* 1017 */         return RMIConnector.this.connection.invoke(paramObjectName, paramString, localMarshalledObject, paramArrayOfString, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1023 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1025 */         return RMIConnector.this.connection.invoke(paramObjectName, paramString, localMarshalledObject, paramArrayOfString, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/* 1031 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public String getDefaultDomain()
/*      */       throws IOException
/*      */     {
/* 1038 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("getDefaultDomain", "");
/*      */ 
/* 1040 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/* 1042 */         return RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/* 1044 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1046 */         return RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
/*      */       } finally {
/* 1048 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public String[] getDomains() throws IOException {
/* 1053 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("getDomains", "");
/*      */ 
/* 1055 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/* 1057 */         return RMIConnector.this.connection.getDomains(this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/* 1059 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1061 */         return RMIConnector.this.connection.getDomains(this.delegationSubject);
/*      */       } finally {
/* 1063 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public MBeanInfo getMBeanInfo(ObjectName paramObjectName)
/*      */       throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
/*      */     {
/* 1073 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("getMBeanInfo", "name=" + paramObjectName);
/* 1074 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/* 1076 */         return RMIConnector.this.connection.getMBeanInfo(paramObjectName, this.delegationSubject);
/*      */       } catch (IOException localIOException) {
/* 1078 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1080 */         return RMIConnector.this.connection.getMBeanInfo(paramObjectName, this.delegationSubject);
/*      */       } finally {
/* 1082 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean isInstanceOf(ObjectName paramObjectName, String paramString)
/*      */       throws InstanceNotFoundException, IOException
/*      */     {
/* 1091 */       if (RMIConnector.logger.debugOn()) {
/* 1092 */         RMIConnector.logger.debug("isInstanceOf", "name=" + paramObjectName + ", className=" + paramString);
/*      */       }
/*      */ 
/* 1095 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/* 1097 */         return RMIConnector.this.connection.isInstanceOf(paramObjectName, paramString, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1101 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1103 */         return RMIConnector.this.connection.isInstanceOf(paramObjectName, paramString, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/* 1107 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */       throws InstanceNotFoundException, IOException
/*      */     {
/* 1118 */       if (RMIConnector.logger.debugOn()) {
/* 1119 */         RMIConnector.logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + paramObjectName1 + ", listener=" + paramObjectName2 + ", filter=" + paramNotificationFilter + ", handback=" + paramObject);
/*      */       }
/*      */ 
/* 1124 */       MarshalledObject localMarshalledObject1 = new MarshalledObject(paramNotificationFilter);
/*      */ 
/* 1126 */       MarshalledObject localMarshalledObject2 = new MarshalledObject(paramObject);
/*      */ 
/* 1128 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/* 1130 */         RMIConnector.this.connection.addNotificationListener(paramObjectName1, paramObjectName2, localMarshalledObject1, localMarshalledObject2, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1136 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1138 */         RMIConnector.this.connection.addNotificationListener(paramObjectName1, paramObjectName2, localMarshalledObject1, localMarshalledObject2, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/* 1144 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2)
/*      */       throws InstanceNotFoundException, ListenerNotFoundException, IOException
/*      */     {
/* 1154 */       if (RMIConnector.logger.debugOn()) RMIConnector.logger.debug("removeNotificationListener(ObjectName,ObjectName)", "name=" + paramObjectName1 + ", listener=" + paramObjectName2);
/*      */ 
/* 1159 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/* 1161 */         RMIConnector.this.connection.removeNotificationListener(paramObjectName1, paramObjectName2, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1165 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1167 */         RMIConnector.this.connection.removeNotificationListener(paramObjectName1, paramObjectName2, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/* 1171 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */       throws InstanceNotFoundException, ListenerNotFoundException, IOException
/*      */     {
/* 1182 */       if (RMIConnector.logger.debugOn()) {
/* 1183 */         RMIConnector.logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + paramObjectName1 + ", listener=" + paramObjectName2 + ", filter=" + paramNotificationFilter + ", handback=" + paramObject);
/*      */       }
/*      */ 
/* 1190 */       MarshalledObject localMarshalledObject1 = new MarshalledObject(paramNotificationFilter);
/*      */ 
/* 1192 */       MarshalledObject localMarshalledObject2 = new MarshalledObject(paramObject);
/*      */ 
/* 1194 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/* 1196 */         RMIConnector.this.connection.removeNotificationListener(paramObjectName1, paramObjectName2, localMarshalledObject1, localMarshalledObject2, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1202 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1204 */         RMIConnector.this.connection.removeNotificationListener(paramObjectName1, paramObjectName2, localMarshalledObject1, localMarshalledObject2, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/* 1210 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */       throws InstanceNotFoundException, IOException
/*      */     {
/* 1223 */       boolean bool = RMIConnector.logger.debugOn();
/*      */ 
/* 1225 */       if (bool) {
/* 1226 */         RMIConnector.logger.debug("addNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + paramObjectName + ", listener=" + paramNotificationListener + ", filter=" + paramNotificationFilter + ", handback=" + paramObject);
/*      */       }
/*      */ 
/* 1234 */       Integer localInteger = RMIConnector.this.addListenerWithSubject(paramObjectName, new MarshalledObject(paramNotificationFilter), this.delegationSubject, true);
/*      */ 
/* 1238 */       RMIConnector.this.rmiNotifClient.addNotificationListener(localInteger, paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject, this.delegationSubject);
/*      */     }
/*      */ 
/*      */     public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener)
/*      */       throws InstanceNotFoundException, ListenerNotFoundException, IOException
/*      */     {
/* 1249 */       boolean bool = RMIConnector.logger.debugOn();
/*      */ 
/* 1251 */       if (bool) RMIConnector.logger.debug("removeNotificationListener(ObjectName,NotificationListener)", "name=" + paramObjectName + ", listener=" + paramNotificationListener);
/*      */ 
/* 1256 */       Integer[] arrayOfInteger = RMIConnector.this.rmiNotifClient.removeNotificationListener(paramObjectName, paramNotificationListener);
/*      */ 
/* 1259 */       if (bool) RMIConnector.logger.debug("removeNotificationListener", "listenerIDs=" + RMIConnector.objects(arrayOfInteger));
/*      */ 
/* 1262 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try
/*      */       {
/* 1265 */         RMIConnector.this.connection.removeNotificationListeners(paramObjectName, arrayOfInteger, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1269 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1271 */         RMIConnector.this.connection.removeNotificationListeners(paramObjectName, arrayOfInteger, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/* 1275 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */       throws InstanceNotFoundException, ListenerNotFoundException, IOException
/*      */     {
/* 1287 */       boolean bool = RMIConnector.logger.debugOn();
/*      */ 
/* 1289 */       if (bool) {
/* 1290 */         RMIConnector.logger.debug("removeNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + paramObjectName + ", listener=" + paramNotificationListener + ", filter=" + paramNotificationFilter + ", handback=" + paramObject);
/*      */       }
/*      */ 
/* 1298 */       Integer localInteger = RMIConnector.this.rmiNotifClient.removeNotificationListener(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject);
/*      */ 
/* 1302 */       if (bool) RMIConnector.logger.debug("removeNotificationListener", "listenerID=" + localInteger);
/*      */ 
/* 1305 */       ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
/*      */       try {
/* 1307 */         RMIConnector.this.connection.removeNotificationListeners(paramObjectName, new Integer[] { localInteger }, this.delegationSubject);
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1311 */         RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
/*      */ 
/* 1313 */         RMIConnector.this.connection.removeNotificationListeners(paramObjectName, new Integer[] { localInteger }, this.delegationSubject);
/*      */       }
/*      */       finally
/*      */       {
/* 1317 */         RMIConnector.this.popDefaultClassLoader(localClassLoader);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.remote.rmi.RMIConnector
 * JD-Core Version:    0.6.2
 */