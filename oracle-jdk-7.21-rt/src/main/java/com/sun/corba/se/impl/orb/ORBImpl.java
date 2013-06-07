/*      */ package com.sun.corba.se.impl.orb;
/*      */ 
/*      */ import com.sun.corba.se.impl.copyobject.CopierManagerImpl;
/*      */ import com.sun.corba.se.impl.corba.AnyImpl;
/*      */ import com.sun.corba.se.impl.corba.AsynchInvoke;
/*      */ import com.sun.corba.se.impl.corba.ContextListImpl;
/*      */ import com.sun.corba.se.impl.corba.EnvironmentImpl;
/*      */ import com.sun.corba.se.impl.corba.ExceptionListImpl;
/*      */ import com.sun.corba.se.impl.corba.NVListImpl;
/*      */ import com.sun.corba.se.impl.corba.NamedValueImpl;
/*      */ import com.sun.corba.se.impl.corba.RequestImpl;
/*      */ import com.sun.corba.se.impl.corba.TypeCodeImpl;
/*      */ import com.sun.corba.se.impl.encoding.CachedCodeBase;
/*      */ import com.sun.corba.se.impl.encoding.EncapsOutputStream;
/*      */ import com.sun.corba.se.impl.interceptors.PIHandlerImpl;
/*      */ import com.sun.corba.se.impl.interceptors.PINoOpHandlerImpl;
/*      */ import com.sun.corba.se.impl.ior.TaggedComponentFactoryFinderImpl;
/*      */ import com.sun.corba.se.impl.ior.TaggedProfileFactoryFinderImpl;
/*      */ import com.sun.corba.se.impl.ior.TaggedProfileTemplateFactoryFinderImpl;
/*      */ import com.sun.corba.se.impl.legacy.connection.LegacyServerSocketManagerImpl;
/*      */ import com.sun.corba.se.impl.logging.OMGSystemException;
/*      */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*      */ import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
/*      */ import com.sun.corba.se.impl.oa.poa.POAFactory;
/*      */ import com.sun.corba.se.impl.oa.toa.TOA;
/*      */ import com.sun.corba.se.impl.oa.toa.TOAFactory;
/*      */ import com.sun.corba.se.impl.orbutil.ORBConstants;
/*      */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*      */ import com.sun.corba.se.impl.orbutil.StackImpl;
/*      */ import com.sun.corba.se.impl.orbutil.threadpool.ThreadPoolManagerImpl;
/*      */ import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
/*      */ import com.sun.corba.se.impl.protocol.RequestDispatcherRegistryImpl;
/*      */ import com.sun.corba.se.impl.transport.CorbaTransportManagerImpl;
/*      */ import com.sun.corba.se.impl.util.Utility;
/*      */ import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
/*      */ import com.sun.corba.se.pept.transport.TransportManager;
/*      */ import com.sun.corba.se.spi.copyobject.CopierManager;
/*      */ import com.sun.corba.se.spi.ior.IOR;
/*      */ import com.sun.corba.se.spi.ior.IORFactories;
/*      */ import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
/*      */ import com.sun.corba.se.spi.ior.ObjectKey;
/*      */ import com.sun.corba.se.spi.ior.ObjectKeyFactory;
/*      */ import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
/*      */ import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
/*      */ import com.sun.corba.se.spi.monitoring.MonitoringManager;
/*      */ import com.sun.corba.se.spi.oa.OAInvocationInfo;
/*      */ import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
/*      */ import com.sun.corba.se.spi.orb.DataCollector;
/*      */ import com.sun.corba.se.spi.orb.ORBConfigurator;
/*      */ import com.sun.corba.se.spi.orb.ORBData;
/*      */ import com.sun.corba.se.spi.orb.ORBVersion;
/*      */ import com.sun.corba.se.spi.orb.ORBVersionFactory;
/*      */ import com.sun.corba.se.spi.orb.Operation;
/*      */ import com.sun.corba.se.spi.orb.OperationFactory;
/*      */ import com.sun.corba.se.spi.orb.ParserImplBase;
/*      */ import com.sun.corba.se.spi.orb.PropertyParser;
/*      */ import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
/*      */ import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
/*      */ import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
/*      */ import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
/*      */ import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
/*      */ import com.sun.corba.se.spi.protocol.PIHandler;
/*      */ import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
/*      */ import com.sun.corba.se.spi.resolver.LocalResolver;
/*      */ import com.sun.corba.se.spi.resolver.Resolver;
/*      */ import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
/*      */ import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
/*      */ import com.sun.corba.se.spi.transport.CorbaTransportManager;
/*      */ import com.sun.org.omg.SendingContext.CodeBase;
/*      */ import java.applet.Applet;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.net.InetAddress;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashSet;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import java.util.WeakHashMap;
/*      */ import javax.rmi.CORBA.Util;
/*      */ import javax.rmi.CORBA.ValueHandler;
/*      */ import org.omg.CORBA.Any;
/*      */ import org.omg.CORBA.BAD_PARAM;
/*      */ import org.omg.CORBA.CompletionStatus;
/*      */ import org.omg.CORBA.Context;
/*      */ import org.omg.CORBA.ContextList;
/*      */ import org.omg.CORBA.Current;
/*      */ import org.omg.CORBA.Environment;
/*      */ import org.omg.CORBA.ExceptionList;
/*      */ import org.omg.CORBA.MARSHAL;
/*      */ import org.omg.CORBA.NVList;
/*      */ import org.omg.CORBA.NamedValue;
/*      */ import org.omg.CORBA.ORBPackage.InvalidName;
/*      */ import org.omg.CORBA.Policy;
/*      */ import org.omg.CORBA.PolicyError;
/*      */ import org.omg.CORBA.Request;
/*      */ import org.omg.CORBA.StructMember;
/*      */ import org.omg.CORBA.TCKind;
/*      */ import org.omg.CORBA.TypeCode;
/*      */ import org.omg.CORBA.UnionMember;
/*      */ import org.omg.CORBA.ValueMember;
/*      */ import org.omg.CORBA.WrongTransaction;
/*      */ import org.omg.CORBA.portable.OutputStream;
/*      */ import org.omg.CORBA.portable.ValueFactory;
/*      */ import org.omg.PortableServer.Servant;
/*      */ 
/*      */ public class ORBImpl extends com.sun.corba.se.spi.orb.ORB
/*      */ {
/*      */   protected TransportManager transportManager;
/*      */   protected LegacyServerSocketManager legacyServerSocketManager;
/*      */   private ThreadLocal OAInvocationInfoStack;
/*      */   private ThreadLocal clientInvocationInfoStack;
/*      */   private static IOR codeBaseIOR;
/*      */   private Vector dynamicRequests;
/*      */   private SynchVariable svResponseReceived;
/*  189 */   private java.lang.Object runObj = new java.lang.Object();
/*  190 */   private java.lang.Object shutdownObj = new java.lang.Object();
/*  191 */   private java.lang.Object waitForCompletionObj = new java.lang.Object();
/*      */   private static final byte STATUS_OPERATING = 1;
/*      */   private static final byte STATUS_SHUTTING_DOWN = 2;
/*      */   private static final byte STATUS_SHUTDOWN = 3;
/*      */   private static final byte STATUS_DESTROYED = 4;
/*  196 */   private byte status = 1;
/*      */ 
/*  199 */   private java.lang.Object invocationObj = new java.lang.Object();
/*  200 */   private int numInvocations = 0;
/*      */ 
/*  204 */   private ThreadLocal isProcessingInvocation = new ThreadLocal() {
/*      */     protected java.lang.Object initialValue() {
/*  206 */       return Boolean.FALSE;
/*      */     }
/*  204 */   };
/*      */   private Map typeCodeForClassMap;
/*  215 */   private Hashtable valueFactoryCache = new Hashtable();
/*      */   private ThreadLocal orbVersionThreadLocal;
/*      */   private RequestDispatcherRegistry requestDispatcherRegistry;
/*      */   private CopierManager copierManager;
/*      */   private int transientServerId;
/*      */   private ServiceContextRegistry serviceContextRegistry;
/*      */   private TOAFactory toaFactory;
/*      */   private POAFactory poaFactory;
/*      */   private PIHandler pihandler;
/*      */   private ORBData configData;
/*      */   private BadServerIdHandler badServerIdHandler;
/*      */   private ClientDelegateFactory clientDelegateFactory;
/*      */   private CorbaContactInfoListFactory corbaContactInfoListFactory;
/*      */   private Resolver resolver;
/*      */   private LocalResolver localResolver;
/*      */   private Operation urlOperation;
/*  268 */   private final java.lang.Object urlOperationLock = new java.lang.Object();
/*      */   private CorbaServerRequestDispatcher insNamingDelegate;
/*  276 */   private final java.lang.Object resolverLock = new java.lang.Object();
/*      */   private TaggedComponentFactoryFinder taggedComponentFactoryFinder;
/*      */   private IdentifiableFactoryFinder taggedProfileFactoryFinder;
/*      */   private IdentifiableFactoryFinder taggedProfileTemplateFactoryFinder;
/*      */   private ObjectKeyFactory objectKeyFactory;
/*  286 */   private boolean orbOwnsThreadPoolManager = false;
/*      */   private ThreadPoolManager threadpoolMgr;
/* 1561 */   private java.lang.Object badServerIdHandlerAccessLock = new java.lang.Object();
/*      */ 
/* 1735 */   private static String localHostString = null;
/*      */ 
/* 1854 */   private java.lang.Object clientDelegateFactoryAccessorLock = new java.lang.Object();
/*      */ 
/* 1876 */   private java.lang.Object corbaContactInfoListFactoryAccessLock = new java.lang.Object();
/*      */ 
/* 2006 */   private java.lang.Object objectKeyFactoryAccessLock = new java.lang.Object();
/*      */ 
/* 2028 */   private java.lang.Object transportManagerAccessorLock = new java.lang.Object();
/*      */ 
/* 2045 */   private java.lang.Object legacyServerSocketManagerAccessLock = new java.lang.Object();
/*      */ 
/* 2060 */   private java.lang.Object threadPoolManagerAccessLock = new java.lang.Object();
/*      */ 
/*      */   private void dprint(String paramString)
/*      */   {
/*  292 */     ORBUtility.dprint(this, paramString);
/*      */   }
/*      */ 
/*      */   public ORBData getORBData()
/*      */   {
/*  312 */     return this.configData;
/*      */   }
/*      */ 
/*      */   public PIHandler getPIHandler()
/*      */   {
/*  317 */     return this.pihandler;
/*      */   }
/*      */ 
/*      */   public ORBVersion getORBVersion()
/*      */   {
/*  331 */     synchronized (this) {
/*  332 */       checkShutdownState();
/*      */     }
/*  334 */     return (ORBVersion)this.orbVersionThreadLocal.get();
/*      */   }
/*      */ 
/*      */   public void setORBVersion(ORBVersion paramORBVersion)
/*      */   {
/*  339 */     synchronized (this) {
/*  340 */       checkShutdownState();
/*      */     }
/*  342 */     this.orbVersionThreadLocal.set(paramORBVersion);
/*      */   }
/*      */ 
/*      */   private void preInit(String[] paramArrayOfString, Properties paramProperties)
/*      */   {
/*  360 */     this.pihandler = new PINoOpHandlerImpl();
/*      */ 
/*  375 */     this.transientServerId = ((int)System.currentTimeMillis());
/*      */ 
/*  377 */     this.orbVersionThreadLocal = new ThreadLocal()
/*      */     {
/*      */       protected java.lang.Object initialValue() {
/*  380 */         return ORBVersionFactory.getORBVersion();
/*      */       }
/*      */     };
/*  385 */     this.requestDispatcherRegistry = new RequestDispatcherRegistryImpl(this, 2);
/*      */ 
/*  387 */     this.copierManager = new CopierManagerImpl(this);
/*      */ 
/*  389 */     this.taggedComponentFactoryFinder = new TaggedComponentFactoryFinderImpl(this);
/*      */ 
/*  391 */     this.taggedProfileFactoryFinder = new TaggedProfileFactoryFinderImpl(this);
/*      */ 
/*  393 */     this.taggedProfileTemplateFactoryFinder = new TaggedProfileTemplateFactoryFinderImpl(this);
/*      */ 
/*  396 */     this.dynamicRequests = new Vector();
/*  397 */     this.svResponseReceived = new SynchVariable();
/*      */ 
/*  399 */     this.OAInvocationInfoStack = new ThreadLocal()
/*      */     {
/*      */       protected java.lang.Object initialValue()
/*      */       {
/*  403 */         return new StackImpl();
/*      */       }
/*      */     };
/*  407 */     this.clientInvocationInfoStack = new ThreadLocal()
/*      */     {
/*      */       protected java.lang.Object initialValue() {
/*  410 */         return new StackImpl();
/*      */       }
/*      */     };
/*  414 */     this.serviceContextRegistry = new ServiceContextRegistry(this);
/*      */   }
/*      */ 
/*      */   protected void setDebugFlags(String[] paramArrayOfString)
/*      */   {
/*  419 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/*  420 */       String str = paramArrayOfString[i];
/*      */       try
/*      */       {
/*  425 */         Field localField = getClass().getField(str + "DebugFlag");
/*  426 */         int j = localField.getModifiers();
/*  427 */         if ((Modifier.isPublic(j)) && (!Modifier.isStatic(j)) && 
/*  428 */           (localField.getType() == Boolean.TYPE))
/*  429 */           localField.setBoolean(this, true);
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void postInit(String[] paramArrayOfString, DataCollector paramDataCollector)
/*      */   {
/*  457 */     this.configData = new ORBDataParserImpl(this, paramDataCollector);
/*      */ 
/*  461 */     setDebugFlags(this.configData.getORBDebugFlags());
/*      */ 
/*  465 */     getTransportManager();
/*  466 */     getLegacyServerSocketManager();
/*      */ 
/*  469 */     ConfigParser localConfigParser = new ConfigParser(null);
/*  470 */     localConfigParser.init(paramDataCollector);
/*      */ 
/*  472 */     ORBConfigurator localORBConfigurator = null;
/*      */     try {
/*  474 */       localORBConfigurator = (ORBConfigurator)localConfigParser.configurator.newInstance();
/*      */     }
/*      */     catch (Exception localException1) {
/*  477 */       throw this.wrapper.badOrbConfigurator(localException1, localConfigParser.configurator.getName());
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  484 */       localORBConfigurator.configure(paramDataCollector, this);
/*      */     } catch (Exception localException2) {
/*  486 */       throw this.wrapper.orbConfiguratorError(localException2);
/*      */     }
/*      */ 
/*  490 */     this.pihandler = new PIHandlerImpl(this, paramArrayOfString);
/*  491 */     this.pihandler.initialize();
/*      */ 
/*  495 */     getThreadPoolManager();
/*      */ 
/*  497 */     super.getByteBufferPool();
/*      */   }
/*      */ 
/*      */   private synchronized POAFactory getPOAFactory()
/*      */   {
/*  502 */     if (this.poaFactory == null) {
/*  503 */       this.poaFactory = ((POAFactory)this.requestDispatcherRegistry.getObjectAdapterFactory(32));
/*      */     }
/*      */ 
/*  507 */     return this.poaFactory;
/*      */   }
/*      */ 
/*      */   private synchronized TOAFactory getTOAFactory()
/*      */   {
/*  512 */     if (this.toaFactory == null) {
/*  513 */       this.toaFactory = ((TOAFactory)this.requestDispatcherRegistry.getObjectAdapterFactory(2));
/*      */     }
/*      */ 
/*  517 */     return this.toaFactory;
/*      */   }
/*      */ 
/*      */   public void set_parameters(Properties paramProperties)
/*      */   {
/*  522 */     synchronized (this) {
/*  523 */       checkShutdownState();
/*      */     }
/*  525 */     preInit(null, paramProperties);
/*  526 */     ??? = DataCollectorFactory.create(paramProperties, getLocalHostName());
/*      */ 
/*  528 */     postInit(null, (DataCollector)???);
/*      */   }
/*      */ 
/*      */   protected void set_parameters(Applet paramApplet, Properties paramProperties)
/*      */   {
/*  533 */     preInit(null, paramProperties);
/*  534 */     DataCollector localDataCollector = DataCollectorFactory.create(paramApplet, paramProperties, getLocalHostName());
/*      */ 
/*  536 */     postInit(null, localDataCollector);
/*      */   }
/*      */ 
/*      */   protected void set_parameters(String[] paramArrayOfString, Properties paramProperties)
/*      */   {
/*  541 */     preInit(paramArrayOfString, paramProperties);
/*  542 */     DataCollector localDataCollector = DataCollectorFactory.create(paramArrayOfString, paramProperties, getLocalHostName());
/*      */ 
/*  544 */     postInit(paramArrayOfString, localDataCollector);
/*      */   }
/*      */ 
/*      */   public synchronized OutputStream create_output_stream()
/*      */   {
/*  553 */     checkShutdownState();
/*  554 */     return new EncapsOutputStream(this);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public synchronized Current get_current()
/*      */   {
/*  569 */     checkShutdownState();
/*      */ 
/*  581 */     throw this.wrapper.genericNoImpl();
/*      */   }
/*      */ 
/*      */   public synchronized NVList create_list(int paramInt)
/*      */   {
/*  594 */     checkShutdownState();
/*  595 */     return new NVListImpl(this, paramInt);
/*      */   }
/*      */ 
/*      */   public synchronized NVList create_operation_list(org.omg.CORBA.Object paramObject)
/*      */   {
/*  608 */     checkShutdownState();
/*  609 */     throw this.wrapper.genericNoImpl();
/*      */   }
/*      */ 
/*      */   public synchronized NamedValue create_named_value(String paramString, Any paramAny, int paramInt)
/*      */   {
/*  619 */     checkShutdownState();
/*  620 */     return new NamedValueImpl(this, paramString, paramAny, paramInt);
/*      */   }
/*      */ 
/*      */   public synchronized ExceptionList create_exception_list()
/*      */   {
/*  630 */     checkShutdownState();
/*  631 */     return new ExceptionListImpl();
/*      */   }
/*      */ 
/*      */   public synchronized ContextList create_context_list()
/*      */   {
/*  641 */     checkShutdownState();
/*  642 */     return new ContextListImpl(this);
/*      */   }
/*      */ 
/*      */   public synchronized Context get_default_context()
/*      */   {
/*  652 */     checkShutdownState();
/*  653 */     throw this.wrapper.genericNoImpl();
/*      */   }
/*      */ 
/*      */   public synchronized Environment create_environment()
/*      */   {
/*  663 */     checkShutdownState();
/*  664 */     return new EnvironmentImpl();
/*      */   }
/*      */ 
/*      */   public synchronized void send_multiple_requests_oneway(Request[] paramArrayOfRequest)
/*      */   {
/*  669 */     checkShutdownState();
/*      */ 
/*  672 */     for (int i = 0; i < paramArrayOfRequest.length; i++)
/*  673 */       paramArrayOfRequest[i].send_oneway();
/*      */   }
/*      */ 
/*      */   public synchronized void send_multiple_requests_deferred(Request[] paramArrayOfRequest)
/*      */   {
/*  684 */     checkShutdownState();
/*      */ 
/*  687 */     for (int i = 0; i < paramArrayOfRequest.length; i++) {
/*  688 */       this.dynamicRequests.addElement(paramArrayOfRequest[i]);
/*      */     }
/*      */ 
/*  692 */     for (i = 0; i < paramArrayOfRequest.length; i++) {
/*  693 */       AsynchInvoke localAsynchInvoke = new AsynchInvoke(this, (RequestImpl)paramArrayOfRequest[i], true);
/*      */ 
/*  695 */       new Thread(localAsynchInvoke).start();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized boolean poll_next_response()
/*      */   {
/*  704 */     checkShutdownState();
/*      */ 
/*  709 */     Enumeration localEnumeration = this.dynamicRequests.elements();
/*  710 */     while (localEnumeration.hasMoreElements() == true) {
/*  711 */       Request localRequest = (Request)localEnumeration.nextElement();
/*  712 */       if (localRequest.poll_response() == true) {
/*  713 */         return true;
/*      */       }
/*      */     }
/*  716 */     return false;
/*      */   }
/*      */ 
/*      */   public Request get_next_response()
/*      */     throws WrongTransaction
/*      */   {
/*  727 */     synchronized (this) {
/*  728 */       checkShutdownState();
/*      */     }
/*      */ 
/*      */     while (true)
/*      */     {
/*  733 */       synchronized (this.dynamicRequests) {
/*  734 */         Enumeration localEnumeration = this.dynamicRequests.elements();
/*  735 */         if (localEnumeration.hasMoreElements()) {
/*  736 */           Request localRequest = (Request)localEnumeration.nextElement();
/*  737 */           if (localRequest.poll_response())
/*      */           {
/*  739 */             localRequest.get_response();
/*  740 */             this.dynamicRequests.removeElement(localRequest);
/*  741 */             return localRequest;
/*      */           }
/*  743 */           continue;
/*      */         }
/*      */       }
/*      */ 
/*  747 */       synchronized (this.svResponseReceived) {
/*  748 */         while (!this.svResponseReceived.value()) {
/*      */           try {
/*  750 */             this.svResponseReceived.wait();
/*      */           }
/*      */           catch (InterruptedException localInterruptedException)
/*      */           {
/*      */           }
/*      */         }
/*  756 */         this.svResponseReceived.reset();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void notifyORB()
/*      */   {
/*  766 */     synchronized (this) {
/*  767 */       checkShutdownState();
/*      */     }
/*  769 */     synchronized (this.svResponseReceived) {
/*  770 */       this.svResponseReceived.set();
/*  771 */       this.svResponseReceived.notify();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized String object_to_string(org.omg.CORBA.Object paramObject)
/*      */   {
/*  782 */     checkShutdownState();
/*      */ 
/*  785 */     if (paramObject == null) {
/*  786 */       localIOR = IORFactories.makeIOR(this);
/*  787 */       return localIOR.stringify();
/*      */     }
/*      */ 
/*  790 */     IOR localIOR = null;
/*      */     try
/*      */     {
/*  793 */       localIOR = ORBUtility.connectAndGetIOR(this, paramObject);
/*      */     }
/*      */     catch (BAD_PARAM localBAD_PARAM) {
/*  796 */       if (localBAD_PARAM.minor == 1398079694) {
/*  797 */         throw this.omgWrapper.notAnObjectImpl(localBAD_PARAM);
/*      */       }
/*      */ 
/*  802 */       throw localBAD_PARAM;
/*      */     }
/*      */ 
/*  805 */     return localIOR.stringify();
/*      */   }
/*      */ 
/*      */   public org.omg.CORBA.Object string_to_object(String paramString)
/*      */   {
/*      */     Operation localOperation;
/*  817 */     synchronized (this) {
/*  818 */       checkShutdownState();
/*  819 */       localOperation = this.urlOperation;
/*      */     }
/*      */ 
/*  822 */     if (paramString == null) {
/*  823 */       throw this.wrapper.nullParam();
/*      */     }
/*  825 */     synchronized (this.urlOperationLock) {
/*  826 */       org.omg.CORBA.Object localObject = (org.omg.CORBA.Object)localOperation.operate(paramString);
/*  827 */       return localObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized IOR getFVDCodeBaseIOR()
/*      */   {
/*  835 */     checkShutdownState();
/*      */ 
/*  837 */     if (codeBaseIOR != null) {
/*  838 */       return codeBaseIOR;
/*      */     }
/*      */ 
/*  843 */     ValueHandler localValueHandler = ORBUtility.createValueHandler();
/*      */ 
/*  845 */     CodeBase localCodeBase = (CodeBase)localValueHandler.getRunTimeCodeBase();
/*  846 */     return ORBUtility.connectAndGetIOR(this, localCodeBase);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode get_primitive_tc(TCKind paramTCKind)
/*      */   {
/*  857 */     checkShutdownState();
/*  858 */     return get_primitive_tc(paramTCKind.value());
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_struct_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember)
/*      */   {
/*  873 */     checkShutdownState();
/*  874 */     return new TypeCodeImpl(this, 15, paramString1, paramString2, paramArrayOfStructMember);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_union_tc(String paramString1, String paramString2, TypeCode paramTypeCode, UnionMember[] paramArrayOfUnionMember)
/*      */   {
/*  892 */     checkShutdownState();
/*  893 */     return new TypeCodeImpl(this, 16, paramString1, paramString2, paramTypeCode, paramArrayOfUnionMember);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_enum_tc(String paramString1, String paramString2, String[] paramArrayOfString)
/*      */   {
/*  913 */     checkShutdownState();
/*  914 */     return new TypeCodeImpl(this, 17, paramString1, paramString2, paramArrayOfString);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_alias_tc(String paramString1, String paramString2, TypeCode paramTypeCode)
/*      */   {
/*  930 */     checkShutdownState();
/*  931 */     return new TypeCodeImpl(this, 21, paramString1, paramString2, paramTypeCode);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_exception_tc(String paramString1, String paramString2, StructMember[] paramArrayOfStructMember)
/*      */   {
/*  946 */     checkShutdownState();
/*  947 */     return new TypeCodeImpl(this, 22, paramString1, paramString2, paramArrayOfStructMember);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_interface_tc(String paramString1, String paramString2)
/*      */   {
/*  960 */     checkShutdownState();
/*  961 */     return new TypeCodeImpl(this, 14, paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_string_tc(int paramInt)
/*      */   {
/*  972 */     checkShutdownState();
/*  973 */     return new TypeCodeImpl(this, 18, paramInt);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_wstring_tc(int paramInt)
/*      */   {
/*  984 */     checkShutdownState();
/*  985 */     return new TypeCodeImpl(this, 27, paramInt);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_sequence_tc(int paramInt, TypeCode paramTypeCode)
/*      */   {
/*  999 */     checkShutdownState();
/* 1000 */     return new TypeCodeImpl(this, 19, paramInt, paramTypeCode);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_recursive_sequence_tc(int paramInt1, int paramInt2)
/*      */   {
/* 1015 */     checkShutdownState();
/* 1016 */     return new TypeCodeImpl(this, 19, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_array_tc(int paramInt, TypeCode paramTypeCode)
/*      */   {
/* 1031 */     checkShutdownState();
/* 1032 */     return new TypeCodeImpl(this, 20, paramInt, paramTypeCode);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_native_tc(String paramString1, String paramString2)
/*      */   {
/* 1039 */     checkShutdownState();
/* 1040 */     return new TypeCodeImpl(this, 31, paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_abstract_interface_tc(String paramString1, String paramString2)
/*      */   {
/* 1047 */     checkShutdownState();
/* 1048 */     return new TypeCodeImpl(this, 32, paramString1, paramString2);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_fixed_tc(short paramShort1, short paramShort2)
/*      */   {
/* 1053 */     checkShutdownState();
/* 1054 */     return new TypeCodeImpl(this, 28, paramShort1, paramShort2);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_value_tc(String paramString1, String paramString2, short paramShort, TypeCode paramTypeCode, ValueMember[] paramArrayOfValueMember)
/*      */   {
/* 1063 */     checkShutdownState();
/* 1064 */     return new TypeCodeImpl(this, 29, paramString1, paramString2, paramShort, paramTypeCode, paramArrayOfValueMember);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_recursive_tc(String paramString)
/*      */   {
/* 1069 */     checkShutdownState();
/* 1070 */     return new TypeCodeImpl(this, paramString);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCode create_value_box_tc(String paramString1, String paramString2, TypeCode paramTypeCode)
/*      */   {
/* 1077 */     checkShutdownState();
/* 1078 */     return new TypeCodeImpl(this, 30, paramString1, paramString2, paramTypeCode);
/*      */   }
/*      */ 
/*      */   public synchronized Any create_any()
/*      */   {
/* 1089 */     checkShutdownState();
/* 1090 */     return new AnyImpl(this);
/*      */   }
/*      */ 
/*      */   public synchronized void setTypeCodeForClass(Class paramClass, TypeCodeImpl paramTypeCodeImpl)
/*      */   {
/* 1101 */     checkShutdownState();
/*      */ 
/* 1103 */     if (this.typeCodeForClassMap == null) {
/* 1104 */       this.typeCodeForClassMap = Collections.synchronizedMap(new WeakHashMap(64));
/*      */     }
/*      */ 
/* 1107 */     if (!this.typeCodeForClassMap.containsKey(paramClass))
/* 1108 */       this.typeCodeForClassMap.put(paramClass, paramTypeCodeImpl);
/*      */   }
/*      */ 
/*      */   public synchronized TypeCodeImpl getTypeCodeForClass(Class paramClass)
/*      */   {
/* 1113 */     checkShutdownState();
/*      */ 
/* 1115 */     if (this.typeCodeForClassMap == null)
/* 1116 */       return null;
/* 1117 */     return (TypeCodeImpl)this.typeCodeForClassMap.get(paramClass);
/*      */   }
/*      */ 
/*      */   public String[] list_initial_services()
/*      */   {
/*      */     Resolver localResolver1;
/* 1138 */     synchronized (this) {
/* 1139 */       checkShutdownState();
/* 1140 */       localResolver1 = this.resolver;
/*      */     }
/*      */ 
/* 1143 */     synchronized (this.resolverLock) {
/* 1144 */       Set localSet = localResolver1.list();
/* 1145 */       return (String[])localSet.toArray(new String[localSet.size()]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public org.omg.CORBA.Object resolve_initial_references(String paramString)
/*      */     throws InvalidName
/*      */   {
/*      */     Resolver localResolver1;
/* 1164 */     synchronized (this) {
/* 1165 */       checkShutdownState();
/* 1166 */       localResolver1 = this.resolver;
/*      */     }
/*      */ 
/* 1169 */     synchronized (this.resolverLock) {
/* 1170 */       org.omg.CORBA.Object localObject = localResolver1.resolve(paramString);
/*      */ 
/* 1172 */       if (localObject == null) {
/* 1173 */         throw new InvalidName();
/*      */       }
/* 1175 */       return localObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void register_initial_reference(String paramString, org.omg.CORBA.Object paramObject)
/*      */     throws InvalidName
/*      */   {
/* 1197 */     synchronized (this) {
/* 1198 */       checkShutdownState();
/*      */     }
/*      */ 
/* 1201 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 1202 */       throw new InvalidName();
/*      */     }
/* 1204 */     synchronized (this) {
/* 1205 */       checkShutdownState();
/*      */     }
/*      */     CorbaServerRequestDispatcher localCorbaServerRequestDispatcher;
/* 1208 */     synchronized (this.resolverLock) {
/* 1209 */       localCorbaServerRequestDispatcher = this.insNamingDelegate;
/*      */ 
/* 1211 */       org.omg.CORBA.Object localObject = this.localResolver.resolve(paramString);
/* 1212 */       if (localObject != null) {
/* 1213 */         throw new InvalidName(paramString + " already registered");
/*      */       }
/* 1215 */       this.localResolver.register(paramString, ClosureFactory.makeConstant(paramObject));
/*      */     }
/*      */ 
/* 1218 */     synchronized (this) {
/* 1219 */       if (StubAdapter.isStub(paramObject))
/*      */       {
/* 1221 */         this.requestDispatcherRegistry.registerServerRequestDispatcher(localCorbaServerRequestDispatcher, paramString);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/* 1233 */     synchronized (this) {
/* 1234 */       checkShutdownState();
/*      */     }
/*      */ 
/* 1237 */     synchronized (this.runObj) {
/*      */       try {
/* 1239 */         this.runObj.wait(); } catch (InterruptedException localInterruptedException) {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void shutdown(boolean paramBoolean) {
/* 1245 */     int i = 0;
/*      */ 
/* 1247 */     synchronized (this) {
/* 1248 */       checkShutdownState();
/*      */ 
/* 1254 */       if ((paramBoolean) && (this.isProcessingInvocation.get() == Boolean.TRUE))
/*      */       {
/* 1256 */         throw this.omgWrapper.shutdownWaitForCompletionDeadlock();
/*      */       }
/*      */ 
/* 1259 */       if (this.status == 2) {
/* 1260 */         if (paramBoolean)
/* 1261 */           i = 1;
/*      */         else {
/* 1263 */           return;
/*      */         }
/*      */       }
/*      */ 
/* 1267 */       this.status = 2;
/*      */     }
/*      */ 
/* 1271 */     synchronized (this.shutdownObj)
/*      */     {
/* 1275 */       if (i != 0) {
/*      */         while (true) {
/* 1277 */           synchronized (this) {
/* 1278 */             if (this.status == 3)
/* 1279 */               break;
/*      */           }
/*      */           try
/*      */           {
/* 1283 */             this.shutdownObj.wait();
/*      */           }
/*      */           catch (InterruptedException localObject1)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/* 1290 */       shutdownServants(paramBoolean);
/*      */ 
/* 1292 */       if (paramBoolean)
/* 1293 */         synchronized (this.waitForCompletionObj) {
/* 1294 */           while (this.numInvocations > 0)
/*      */             try {
/* 1296 */               this.waitForCompletionObj.wait();
/*      */             }
/*      */             catch (InterruptedException localInterruptedException)
/*      */             {
/*      */             }
/*      */         }
/* 1302 */       synchronized (this.runObj) {
/* 1303 */         this.runObj.notifyAll();
/*      */       }
/*      */ 
/* 1306 */       this.status = 3;
/*      */ 
/* 1308 */       this.shutdownObj.notifyAll();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void shutdownServants(boolean paramBoolean)
/*      */   {
/*      */     HashSet localHashSet;
/* 1318 */     synchronized (this) {
/* 1319 */       localHashSet = new HashSet(this.requestDispatcherRegistry.getObjectAdapterFactories());
/*      */     }
/*      */ 
/* 1322 */     for (??? = localHashSet.iterator(); ((Iterator)???).hasNext(); ) { ObjectAdapterFactory localObjectAdapterFactory = (ObjectAdapterFactory)((Iterator)???).next();
/* 1323 */       localObjectAdapterFactory.shutdown(paramBoolean);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void checkShutdownState()
/*      */   {
/* 1329 */     if (this.status == 4) {
/* 1330 */       throw this.wrapper.orbDestroyed();
/*      */     }
/*      */ 
/* 1333 */     if (this.status == 3)
/* 1334 */       throw this.omgWrapper.badOperationAfterShutdown();
/*      */   }
/*      */ 
/*      */   public boolean isDuringDispatch()
/*      */   {
/* 1340 */     synchronized (this) {
/* 1341 */       checkShutdownState();
/*      */     }
/* 1343 */     ??? = (Boolean)this.isProcessingInvocation.get();
/* 1344 */     return ((Boolean)???).booleanValue();
/*      */   }
/*      */ 
/*      */   public void startingDispatch()
/*      */   {
/* 1349 */     synchronized (this) {
/* 1350 */       checkShutdownState();
/*      */     }
/* 1352 */     synchronized (this.invocationObj) {
/* 1353 */       this.isProcessingInvocation.set(Boolean.TRUE);
/* 1354 */       this.numInvocations += 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void finishedDispatch()
/*      */   {
/* 1360 */     synchronized (this) {
/* 1361 */       checkShutdownState();
/*      */     }
/* 1363 */     synchronized (this.invocationObj) {
/* 1364 */       this.numInvocations -= 1;
/* 1365 */       this.isProcessingInvocation.set(Boolean.valueOf(false));
/* 1366 */       if (this.numInvocations == 0)
/* 1367 */         synchronized (this.waitForCompletionObj) {
/* 1368 */           this.waitForCompletionObj.notifyAll();
/*      */         }
/* 1370 */       else if (this.numInvocations < 0)
/* 1371 */         throw this.wrapper.numInvocationsAlreadyZero(CompletionStatus.COMPLETED_YES);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void destroy()
/*      */   {
/* 1384 */     int i = 0;
/*      */ 
/* 1386 */     synchronized (this) {
/* 1387 */       i = this.status == 1 ? 1 : 0;
/*      */     }
/*      */ 
/* 1390 */     if (i != 0) {
/* 1391 */       shutdown(true);
/*      */     }
/*      */ 
/* 1394 */     synchronized (this) {
/* 1395 */       if (this.status < 4) {
/* 1396 */         getCorbaTransportManager().close();
/* 1397 */         getPIHandler().destroyInterceptors();
/* 1398 */         this.status = 4;
/*      */       }
/*      */     }
/* 1401 */     synchronized (this.threadPoolManagerAccessLock) {
/* 1402 */       if (this.orbOwnsThreadPoolManager) {
/*      */         try {
/* 1404 */           this.threadpoolMgr.close();
/* 1405 */           this.threadpoolMgr = null;
/*      */         } catch (IOException localIOException3) {
/* 1407 */           this.wrapper.ioExceptionOnClose(localIOException3);
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1413 */       this.monitoringManager.close();
/* 1414 */       this.monitoringManager = null;
/*      */     } catch (IOException localIOException1) {
/* 1416 */       this.wrapper.ioExceptionOnClose(localIOException1);
/*      */     }
/*      */ 
/* 1419 */     CachedCodeBase.cleanCache(this);
/*      */     try {
/* 1421 */       this.pihandler.close();
/*      */     } catch (IOException localIOException2) {
/* 1423 */       this.wrapper.ioExceptionOnClose(localIOException2);
/*      */     }
/*      */ 
/* 1426 */     super.destroy();
/*      */ 
/* 1428 */     this.badServerIdHandlerAccessLock = null;
/* 1429 */     this.clientDelegateFactoryAccessorLock = null;
/* 1430 */     this.corbaContactInfoListFactoryAccessLock = null;
/*      */ 
/* 1432 */     this.objectKeyFactoryAccessLock = null;
/* 1433 */     this.legacyServerSocketManagerAccessLock = null;
/* 1434 */     this.threadPoolManagerAccessLock = null;
/* 1435 */     this.transportManager = null;
/* 1436 */     this.legacyServerSocketManager = null;
/* 1437 */     this.OAInvocationInfoStack = null;
/* 1438 */     this.clientInvocationInfoStack = null;
/* 1439 */     codeBaseIOR = null;
/* 1440 */     this.dynamicRequests = null;
/* 1441 */     this.svResponseReceived = null;
/* 1442 */     this.runObj = null;
/* 1443 */     this.shutdownObj = null;
/* 1444 */     this.waitForCompletionObj = null;
/* 1445 */     this.invocationObj = null;
/* 1446 */     this.isProcessingInvocation = null;
/* 1447 */     this.typeCodeForClassMap = null;
/* 1448 */     this.valueFactoryCache = null;
/* 1449 */     this.orbVersionThreadLocal = null;
/* 1450 */     this.requestDispatcherRegistry = null;
/* 1451 */     this.copierManager = null;
/* 1452 */     this.toaFactory = null;
/* 1453 */     this.poaFactory = null;
/* 1454 */     this.pihandler = null;
/* 1455 */     this.configData = null;
/* 1456 */     this.badServerIdHandler = null;
/* 1457 */     this.clientDelegateFactory = null;
/* 1458 */     this.corbaContactInfoListFactory = null;
/* 1459 */     this.resolver = null;
/* 1460 */     this.localResolver = null;
/* 1461 */     this.insNamingDelegate = null;
/* 1462 */     this.urlOperation = null;
/* 1463 */     this.taggedComponentFactoryFinder = null;
/* 1464 */     this.taggedProfileFactoryFinder = null;
/* 1465 */     this.taggedProfileTemplateFactoryFinder = null;
/* 1466 */     this.objectKeyFactory = null;
/*      */   }
/*      */ 
/*      */   public synchronized ValueFactory register_value_factory(String paramString, ValueFactory paramValueFactory)
/*      */   {
/* 1481 */     checkShutdownState();
/*      */ 
/* 1483 */     if ((paramString == null) || (paramValueFactory == null)) {
/* 1484 */       throw this.omgWrapper.unableRegisterValueFactory();
/*      */     }
/* 1486 */     return (ValueFactory)this.valueFactoryCache.put(paramString, paramValueFactory);
/*      */   }
/*      */ 
/*      */   public synchronized void unregister_value_factory(String paramString)
/*      */   {
/* 1496 */     checkShutdownState();
/*      */ 
/* 1498 */     if (this.valueFactoryCache.remove(paramString) == null)
/* 1499 */       throw this.wrapper.nullParam();
/*      */   }
/*      */ 
/*      */   public synchronized ValueFactory lookup_value_factory(String paramString)
/*      */   {
/* 1513 */     checkShutdownState();
/*      */ 
/* 1515 */     ValueFactory localValueFactory = (ValueFactory)this.valueFactoryCache.get(paramString);
/*      */ 
/* 1518 */     if (localValueFactory == null) {
/*      */       try {
/* 1520 */         localValueFactory = Utility.getFactory(null, null, null, paramString);
/*      */       } catch (MARSHAL localMARSHAL) {
/* 1522 */         throw this.wrapper.unableFindValueFactory(localMARSHAL);
/*      */       }
/*      */     }
/*      */ 
/* 1526 */     return localValueFactory;
/*      */   }
/*      */ 
/*      */   public OAInvocationInfo peekInvocationInfo()
/*      */   {
/* 1531 */     synchronized (this) {
/* 1532 */       checkShutdownState();
/*      */     }
/* 1534 */     ??? = (StackImpl)this.OAInvocationInfoStack.get();
/* 1535 */     return (OAInvocationInfo)((StackImpl)???).peek();
/*      */   }
/*      */ 
/*      */   public void pushInvocationInfo(OAInvocationInfo paramOAInvocationInfo)
/*      */   {
/* 1540 */     synchronized (this) {
/* 1541 */       checkShutdownState();
/*      */     }
/* 1543 */     ??? = (StackImpl)this.OAInvocationInfoStack.get();
/* 1544 */     ((StackImpl)???).push(paramOAInvocationInfo);
/*      */   }
/*      */ 
/*      */   public OAInvocationInfo popInvocationInfo()
/*      */   {
/* 1549 */     synchronized (this) {
/* 1550 */       checkShutdownState();
/*      */     }
/* 1552 */     ??? = (StackImpl)this.OAInvocationInfoStack.get();
/* 1553 */     return (OAInvocationInfo)((StackImpl)???).pop();
/*      */   }
/*      */ 
/*      */   public void initBadServerIdHandler()
/*      */   {
/* 1565 */     synchronized (this) {
/* 1566 */       checkShutdownState();
/*      */     }
/* 1568 */     synchronized (this.badServerIdHandlerAccessLock) {
/* 1569 */       Class localClass = this.configData.getBadServerIdHandler();
/* 1570 */       if (localClass != null)
/*      */         try {
/* 1572 */           Class[] arrayOfClass = { org.omg.CORBA.ORB.class };
/* 1573 */           java.lang.Object[] arrayOfObject = { this };
/* 1574 */           Constructor localConstructor = localClass.getConstructor(arrayOfClass);
/* 1575 */           this.badServerIdHandler = ((BadServerIdHandler)localConstructor.newInstance(arrayOfObject));
/*      */         }
/*      */         catch (Exception localException) {
/* 1578 */           throw this.wrapper.errorInitBadserveridhandler(localException);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBadServerIdHandler(BadServerIdHandler paramBadServerIdHandler)
/*      */   {
/* 1586 */     synchronized (this) {
/* 1587 */       checkShutdownState();
/*      */     }
/* 1589 */     synchronized (this.badServerIdHandlerAccessLock) {
/* 1590 */       this.badServerIdHandler = paramBadServerIdHandler;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleBadServerId(ObjectKey paramObjectKey)
/*      */   {
/* 1596 */     synchronized (this) {
/* 1597 */       checkShutdownState();
/*      */     }
/* 1599 */     synchronized (this.badServerIdHandlerAccessLock) {
/* 1600 */       if (this.badServerIdHandler == null) {
/* 1601 */         throw this.wrapper.badServerId();
/*      */       }
/* 1603 */       this.badServerIdHandler.handle(paramObjectKey);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized Policy create_policy(int paramInt, Any paramAny)
/*      */     throws PolicyError
/*      */   {
/* 1610 */     checkShutdownState();
/*      */ 
/* 1612 */     return this.pihandler.create_policy(paramInt, paramAny);
/*      */   }
/*      */ 
/*      */   public synchronized void connect(org.omg.CORBA.Object paramObject)
/*      */   {
/* 1620 */     checkShutdownState();
/* 1621 */     if (getTOAFactory() == null)
/* 1622 */       throw this.wrapper.noToa();
/*      */     try
/*      */     {
/* 1625 */       String str = Util.getCodebase(paramObject.getClass());
/* 1626 */       getTOAFactory().getTOA(str).connect(paramObject);
/*      */     } catch (Exception localException) {
/* 1628 */       throw this.wrapper.orbConnectError(localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void disconnect(org.omg.CORBA.Object paramObject)
/*      */   {
/* 1634 */     checkShutdownState();
/* 1635 */     if (getTOAFactory() == null)
/* 1636 */       throw this.wrapper.noToa();
/*      */     try
/*      */     {
/* 1639 */       getTOAFactory().getTOA().disconnect(paramObject);
/*      */     } catch (Exception localException) {
/* 1641 */       throw this.wrapper.orbConnectError(localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getTransientServerId()
/*      */   {
/* 1647 */     synchronized (this) {
/* 1648 */       checkShutdownState();
/*      */     }
/* 1650 */     if (this.configData.getORBServerIdPropertySpecified())
/*      */     {
/* 1652 */       return this.configData.getPersistentServerId();
/*      */     }
/* 1654 */     return this.transientServerId;
/*      */   }
/*      */ 
/*      */   public RequestDispatcherRegistry getRequestDispatcherRegistry()
/*      */   {
/* 1659 */     synchronized (this) {
/* 1660 */       checkShutdownState();
/*      */     }
/* 1662 */     return this.requestDispatcherRegistry;
/*      */   }
/*      */ 
/*      */   public ServiceContextRegistry getServiceContextRegistry()
/*      */   {
/* 1667 */     synchronized (this) {
/* 1668 */       checkShutdownState();
/*      */     }
/* 1670 */     return this.serviceContextRegistry;
/*      */   }
/*      */ 
/*      */   public boolean isLocalHost(String paramString)
/*      */   {
/* 1687 */     synchronized (this) {
/* 1688 */       checkShutdownState();
/*      */     }
/* 1690 */     return (paramString.equals(this.configData.getORBServerHost())) || (paramString.equals(getLocalHostName()));
/*      */   }
/*      */ 
/*      */   public boolean isLocalServerId(int paramInt1, int paramInt2)
/*      */   {
/* 1696 */     synchronized (this) {
/* 1697 */       checkShutdownState();
/*      */     }
/* 1699 */     if ((paramInt1 < 32) || (paramInt1 > 63))
/*      */     {
/* 1701 */       return paramInt2 == getTransientServerId();
/*      */     }
/*      */ 
/* 1704 */     if (ORBConstants.isTransient(paramInt1))
/* 1705 */       return paramInt2 == getTransientServerId();
/* 1706 */     if (this.configData.getPersistentServerIdInitialized()) {
/* 1707 */       return paramInt2 == this.configData.getPersistentServerId();
/*      */     }
/* 1709 */     return false;
/*      */   }
/*      */ 
/*      */   private String getHostName(String paramString)
/*      */     throws UnknownHostException
/*      */   {
/* 1719 */     return InetAddress.getByName(paramString).getHostAddress();
/*      */   }
/*      */ 
/*      */   private synchronized String getLocalHostName()
/*      */   {
/* 1739 */     if (localHostString == null) {
/*      */       try {
/* 1741 */         localHostString = InetAddress.getLocalHost().getHostAddress();
/*      */       } catch (Exception localException) {
/* 1743 */         throw this.wrapper.getLocalHostFailed(localException);
/*      */       }
/*      */     }
/* 1746 */     return localHostString;
/*      */   }
/*      */ 
/*      */   public synchronized boolean work_pending()
/*      */   {
/* 1759 */     checkShutdownState();
/* 1760 */     throw this.wrapper.genericNoImpl();
/*      */   }
/*      */ 
/*      */   public synchronized void perform_work()
/*      */   {
/* 1767 */     checkShutdownState();
/* 1768 */     throw this.wrapper.genericNoImpl();
/*      */   }
/*      */ 
/*      */   public synchronized void set_delegate(java.lang.Object paramObject) {
/* 1772 */     checkShutdownState();
/*      */ 
/* 1774 */     POAFactory localPOAFactory = getPOAFactory();
/* 1775 */     if (localPOAFactory != null) {
/* 1776 */       ((Servant)paramObject)._set_delegate(localPOAFactory.getDelegateImpl());
/*      */     }
/*      */     else
/* 1779 */       throw this.wrapper.noPoa();
/*      */   }
/*      */ 
/*      */   public ClientInvocationInfo createOrIncrementInvocationInfo()
/*      */   {
/* 1789 */     synchronized (this) {
/* 1790 */       checkShutdownState();
/*      */     }
/* 1792 */     ??? = (StackImpl)this.clientInvocationInfoStack.get();
/*      */ 
/* 1794 */     java.lang.Object localObject2 = null;
/* 1795 */     if (!((StackImpl)???).empty()) {
/* 1796 */       localObject2 = (ClientInvocationInfo)((StackImpl)???).peek();
/*      */     }
/*      */ 
/* 1799 */     if ((localObject2 == null) || (!((ClientInvocationInfo)localObject2).isRetryInvocation()))
/*      */     {
/* 1803 */       localObject2 = new CorbaInvocationInfo(this);
/* 1804 */       startingDispatch();
/* 1805 */       ((StackImpl)???).push(localObject2);
/*      */     }
/*      */ 
/* 1808 */     ((ClientInvocationInfo)localObject2).setIsRetryInvocation(false);
/* 1809 */     ((ClientInvocationInfo)localObject2).incrementEntryCount();
/* 1810 */     return localObject2;
/*      */   }
/*      */ 
/*      */   public void releaseOrDecrementInvocationInfo()
/*      */   {
/* 1815 */     synchronized (this) {
/* 1816 */       checkShutdownState();
/*      */     }
/* 1818 */     int i = -1;
/* 1819 */     ClientInvocationInfo localClientInvocationInfo = null;
/* 1820 */     StackImpl localStackImpl = (StackImpl)this.clientInvocationInfoStack.get();
/*      */ 
/* 1822 */     if (!localStackImpl.empty()) {
/* 1823 */       localClientInvocationInfo = (ClientInvocationInfo)localStackImpl.peek();
/*      */     }
/*      */     else {
/* 1826 */       throw this.wrapper.invocationInfoStackEmpty();
/*      */     }
/* 1828 */     localClientInvocationInfo.decrementEntryCount();
/* 1829 */     i = localClientInvocationInfo.getEntryCount();
/* 1830 */     if (localClientInvocationInfo.getEntryCount() == 0)
/*      */     {
/* 1832 */       if (!localClientInvocationInfo.isRetryInvocation()) {
/* 1833 */         localStackImpl.pop();
/*      */       }
/* 1835 */       finishedDispatch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ClientInvocationInfo getInvocationInfo()
/*      */   {
/* 1841 */     synchronized (this) {
/* 1842 */       checkShutdownState();
/*      */     }
/* 1844 */     ??? = (StackImpl)this.clientInvocationInfoStack.get();
/*      */ 
/* 1846 */     return (ClientInvocationInfo)((StackImpl)???).peek();
/*      */   }
/*      */ 
/*      */   public void setClientDelegateFactory(ClientDelegateFactory paramClientDelegateFactory)
/*      */   {
/* 1858 */     synchronized (this) {
/* 1859 */       checkShutdownState();
/*      */     }
/* 1861 */     synchronized (this.clientDelegateFactoryAccessorLock) {
/* 1862 */       this.clientDelegateFactory = paramClientDelegateFactory;
/*      */     }
/*      */   }
/*      */ 
/*      */   public ClientDelegateFactory getClientDelegateFactory()
/*      */   {
/* 1868 */     synchronized (this) {
/* 1869 */       checkShutdownState();
/*      */     }
/* 1871 */     synchronized (this.clientDelegateFactoryAccessorLock) {
/* 1872 */       return this.clientDelegateFactory;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCorbaContactInfoListFactory(CorbaContactInfoListFactory paramCorbaContactInfoListFactory)
/*      */   {
/* 1880 */     synchronized (this) {
/* 1881 */       checkShutdownState();
/*      */     }
/* 1883 */     synchronized (this.corbaContactInfoListFactoryAccessLock) {
/* 1884 */       this.corbaContactInfoListFactory = paramCorbaContactInfoListFactory;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized CorbaContactInfoListFactory getCorbaContactInfoListFactory()
/*      */   {
/* 1890 */     checkShutdownState();
/* 1891 */     return this.corbaContactInfoListFactory;
/*      */   }
/*      */ 
/*      */   public void setResolver(Resolver paramResolver)
/*      */   {
/* 1899 */     synchronized (this) {
/* 1900 */       checkShutdownState();
/*      */     }
/* 1902 */     synchronized (this.resolverLock) {
/* 1903 */       this.resolver = paramResolver;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Resolver getResolver()
/*      */   {
/* 1912 */     synchronized (this) {
/* 1913 */       checkShutdownState();
/*      */     }
/* 1915 */     synchronized (this.resolverLock) {
/* 1916 */       return this.resolver;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setLocalResolver(LocalResolver paramLocalResolver)
/*      */   {
/* 1925 */     synchronized (this) {
/* 1926 */       checkShutdownState();
/*      */     }
/* 1928 */     synchronized (this.resolverLock) {
/* 1929 */       this.localResolver = paramLocalResolver;
/*      */     }
/*      */   }
/*      */ 
/*      */   public LocalResolver getLocalResolver()
/*      */   {
/* 1938 */     synchronized (this) {
/* 1939 */       checkShutdownState();
/*      */     }
/* 1941 */     synchronized (this.resolverLock) {
/* 1942 */       return this.localResolver;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setURLOperation(Operation paramOperation)
/*      */   {
/* 1951 */     synchronized (this) {
/* 1952 */       checkShutdownState();
/*      */     }
/* 1954 */     synchronized (this.urlOperationLock) {
/* 1955 */       this.urlOperation = paramOperation;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Operation getURLOperation()
/*      */   {
/* 1964 */     synchronized (this) {
/* 1965 */       checkShutdownState();
/*      */     }
/* 1967 */     synchronized (this.urlOperationLock) {
/* 1968 */       return this.urlOperation;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setINSDelegate(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher)
/*      */   {
/* 1974 */     synchronized (this) {
/* 1975 */       checkShutdownState();
/*      */     }
/* 1977 */     synchronized (this.resolverLock) {
/* 1978 */       this.insNamingDelegate = paramCorbaServerRequestDispatcher;
/*      */     }
/*      */   }
/*      */ 
/*      */   public TaggedComponentFactoryFinder getTaggedComponentFactoryFinder()
/*      */   {
/* 1984 */     synchronized (this) {
/* 1985 */       checkShutdownState();
/*      */     }
/* 1987 */     return this.taggedComponentFactoryFinder;
/*      */   }
/*      */ 
/*      */   public IdentifiableFactoryFinder getTaggedProfileFactoryFinder()
/*      */   {
/* 1992 */     synchronized (this) {
/* 1993 */       checkShutdownState();
/*      */     }
/* 1995 */     return this.taggedProfileFactoryFinder;
/*      */   }
/*      */ 
/*      */   public IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder()
/*      */   {
/* 2000 */     synchronized (this) {
/* 2001 */       checkShutdownState();
/*      */     }
/* 2003 */     return this.taggedProfileTemplateFactoryFinder;
/*      */   }
/*      */ 
/*      */   public ObjectKeyFactory getObjectKeyFactory()
/*      */   {
/* 2010 */     synchronized (this) {
/* 2011 */       checkShutdownState();
/*      */     }
/* 2013 */     synchronized (this.objectKeyFactoryAccessLock) {
/* 2014 */       return this.objectKeyFactory;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObjectKeyFactory(ObjectKeyFactory paramObjectKeyFactory)
/*      */   {
/* 2020 */     synchronized (this) {
/* 2021 */       checkShutdownState();
/*      */     }
/* 2023 */     synchronized (this.objectKeyFactoryAccessLock) {
/* 2024 */       this.objectKeyFactory = paramObjectKeyFactory;
/*      */     }
/*      */   }
/*      */ 
/*      */   public TransportManager getTransportManager()
/*      */   {
/* 2032 */     synchronized (this.transportManagerAccessorLock) {
/* 2033 */       if (this.transportManager == null) {
/* 2034 */         this.transportManager = new CorbaTransportManagerImpl(this);
/*      */       }
/* 2036 */       return this.transportManager;
/*      */     }
/*      */   }
/*      */ 
/*      */   public CorbaTransportManager getCorbaTransportManager()
/*      */   {
/* 2042 */     return (CorbaTransportManager)getTransportManager();
/*      */   }
/*      */ 
/*      */   public LegacyServerSocketManager getLegacyServerSocketManager()
/*      */   {
/* 2049 */     synchronized (this) {
/* 2050 */       checkShutdownState();
/*      */     }
/* 2052 */     synchronized (this.legacyServerSocketManagerAccessLock) {
/* 2053 */       if (this.legacyServerSocketManager == null) {
/* 2054 */         this.legacyServerSocketManager = new LegacyServerSocketManagerImpl(this);
/*      */       }
/* 2056 */       return this.legacyServerSocketManager;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setThreadPoolManager(ThreadPoolManager paramThreadPoolManager)
/*      */   {
/* 2064 */     synchronized (this) {
/* 2065 */       checkShutdownState();
/*      */     }
/* 2067 */     synchronized (this.threadPoolManagerAccessLock) {
/* 2068 */       this.threadpoolMgr = paramThreadPoolManager;
/*      */     }
/*      */   }
/*      */ 
/*      */   public ThreadPoolManager getThreadPoolManager()
/*      */   {
/* 2074 */     synchronized (this) {
/* 2075 */       checkShutdownState();
/*      */     }
/* 2077 */     synchronized (this.threadPoolManagerAccessLock) {
/* 2078 */       if (this.threadpoolMgr == null) {
/* 2079 */         this.threadpoolMgr = new ThreadPoolManagerImpl();
/* 2080 */         this.orbOwnsThreadPoolManager = true;
/*      */       }
/* 2082 */       return this.threadpoolMgr;
/*      */     }
/*      */   }
/*      */ 
/*      */   public CopierManager getCopierManager()
/*      */   {
/* 2088 */     synchronized (this) {
/* 2089 */       checkShutdownState();
/*      */     }
/* 2091 */     return this.copierManager;
/*      */   }
/*      */ 
/*      */   private static class ConfigParser extends ParserImplBase
/*      */   {
/*  441 */     public Class configurator = ORBConfiguratorImpl.class;
/*      */ 
/*      */     public PropertyParser makeParser()
/*      */     {
/*  445 */       PropertyParser localPropertyParser = new PropertyParser();
/*  446 */       localPropertyParser.add("com.sun.CORBA.ORBConfigurator", OperationFactory.classAction(), "configurator");
/*      */ 
/*  448 */       return localPropertyParser;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orb.ORBImpl
 * JD-Core Version:    0.6.2
 */