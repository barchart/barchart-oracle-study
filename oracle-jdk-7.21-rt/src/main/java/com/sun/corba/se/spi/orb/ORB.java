/*     */ package com.sun.corba.se.spi.orb;
/*     */ 
/*     */ import com.sun.corba.se.impl.corba.TypeCodeFactory;
/*     */ import com.sun.corba.se.impl.corba.TypeCodeImpl;
/*     */ import com.sun.corba.se.impl.logging.OMGSystemException;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
/*     */ import com.sun.corba.se.impl.orbutil.ORBClassLoader;
/*     */ import com.sun.corba.se.impl.presentation.rmi.PresentationManagerImpl;
/*     */ import com.sun.corba.se.impl.transport.ByteBufferPoolImpl;
/*     */ import com.sun.corba.se.pept.broker.Broker;
/*     */ import com.sun.corba.se.pept.transport.ByteBufferPool;
/*     */ import com.sun.corba.se.spi.copyobject.CopierManager;
/*     */ import com.sun.corba.se.spi.ior.IOR;
/*     */ import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
/*     */ import com.sun.corba.se.spi.ior.ObjectKey;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyFactory;
/*     */ import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
/*     */ import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
/*     */ import com.sun.corba.se.spi.logging.LogWrapperBase;
/*     */ import com.sun.corba.se.spi.logging.LogWrapperFactory;
/*     */ import com.sun.corba.se.spi.monitoring.MonitoringFactories;
/*     */ import com.sun.corba.se.spi.monitoring.MonitoringManager;
/*     */ import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;
/*     */ import com.sun.corba.se.spi.oa.OAInvocationInfo;
/*     */ import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
/*     */ import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
/*     */ import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
/*     */ import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactoryFactory;
/*     */ import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
/*     */ import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
/*     */ import com.sun.corba.se.spi.protocol.PIHandler;
/*     */ import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
/*     */ import com.sun.corba.se.spi.resolver.LocalResolver;
/*     */ import com.sun.corba.se.spi.resolver.Resolver;
/*     */ import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
/*     */ import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
/*     */ import com.sun.corba.se.spi.transport.CorbaTransportManager;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.logging.Logger;
/*     */ import sun.awt.AppContext;
/*     */ 
/*     */ public abstract class ORB extends com.sun.corba.se.org.omg.CORBA.ORB
/*     */   implements Broker, TypeCodeFactory
/*     */ {
/* 119 */   public static boolean ORBInitDebug = false;
/*     */ 
/* 125 */   public boolean transportDebugFlag = false;
/* 126 */   public boolean subcontractDebugFlag = false;
/* 127 */   public boolean poaDebugFlag = false;
/* 128 */   public boolean poaConcurrencyDebugFlag = false;
/* 129 */   public boolean poaFSMDebugFlag = false;
/* 130 */   public boolean orbdDebugFlag = false;
/* 131 */   public boolean namingDebugFlag = false;
/* 132 */   public boolean serviceContextDebugFlag = false;
/* 133 */   public boolean transientObjectManagerDebugFlag = false;
/* 134 */   public boolean giopVersionDebugFlag = false;
/* 135 */   public boolean shutdownDebugFlag = false;
/* 136 */   public boolean giopDebugFlag = false;
/* 137 */   public boolean invocationTimingDebugFlag = false;
/*     */   protected static ORBUtilSystemException staticWrapper;
/*     */   protected ORBUtilSystemException wrapper;
/*     */   protected OMGSystemException omgWrapper;
/*     */   private Map typeCodeMap;
/*     */   private TypeCodeImpl[] primitiveTypeCodeConstants;
/*     */   ByteBufferPool byteBufferPool;
/*     */   private Map wrapperMap;
/* 173 */   private static Map staticWrapperMap = new ConcurrentHashMap();
/*     */   protected MonitoringManager monitoringManager;
/*     */ 
/*     */   public abstract boolean isLocalHost(String paramString);
/*     */ 
/*     */   public abstract boolean isLocalServerId(int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract OAInvocationInfo peekInvocationInfo();
/*     */ 
/*     */   public abstract void pushInvocationInfo(OAInvocationInfo paramOAInvocationInfo);
/*     */ 
/*     */   public abstract OAInvocationInfo popInvocationInfo();
/*     */ 
/*     */   public abstract CorbaTransportManager getCorbaTransportManager();
/*     */ 
/*     */   public abstract LegacyServerSocketManager getLegacyServerSocketManager();
/*     */ 
/*     */   private static PresentationManager setupPresentationManager()
/*     */   {
/* 178 */     staticWrapper = ORBUtilSystemException.get("rpc.presentation");
/*     */ 
/* 181 */     boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/* 185 */         return Boolean.valueOf(Boolean.getBoolean("com.sun.CORBA.ORBUseDynamicStub"));
/*     */       }
/*     */     })).booleanValue();
/*     */ 
/* 191 */     PresentationManager.StubFactoryFactory localStubFactoryFactory = (PresentationManager.StubFactoryFactory)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/* 195 */         PresentationManager.StubFactoryFactory localStubFactoryFactory = PresentationDefaults.getProxyStubFactoryFactory();
/*     */ 
/* 198 */         String str = System.getProperty("com.sun.CORBA.ORBDynamicStubFactoryFactoryClass", "com.sun.corba.se.impl.presentation.rmi.bcel.StubFactoryFactoryBCELImpl");
/*     */         try
/*     */         {
/* 204 */           Class localClass = ORBClassLoader.loadClass(str);
/* 205 */           localStubFactoryFactory = (PresentationManager.StubFactoryFactory)localClass.newInstance();
/*     */         }
/*     */         catch (Exception localException) {
/* 208 */           ORB.staticWrapper.errorInSettingDynamicStubFactoryFactory(localException, str);
/*     */         }
/*     */ 
/* 212 */         return localStubFactoryFactory;
/*     */       }
/*     */     });
/* 217 */     PresentationManagerImpl localPresentationManagerImpl = new PresentationManagerImpl(bool);
/* 218 */     localPresentationManagerImpl.setStubFactoryFactory(false, PresentationDefaults.getStaticStubFactoryFactory());
/*     */ 
/* 220 */     localPresentationManagerImpl.setStubFactoryFactory(true, localStubFactoryFactory);
/* 221 */     return localPresentationManagerImpl;
/*     */   }
/*     */ 
/*     */   public void destroy() {
/* 225 */     this.wrapper = null;
/* 226 */     this.omgWrapper = null;
/* 227 */     this.typeCodeMap = null;
/* 228 */     this.primitiveTypeCodeConstants = null;
/* 229 */     this.byteBufferPool = null;
/*     */   }
/*     */ 
/*     */   public static PresentationManager getPresentationManager()
/*     */   {
/* 238 */     AppContext localAppContext = AppContext.getAppContext();
/* 239 */     PresentationManager localPresentationManager = (PresentationManager)localAppContext.get(PresentationManager.class);
/* 240 */     if (localPresentationManager == null) {
/* 241 */       localPresentationManager = setupPresentationManager();
/* 242 */       localAppContext.put(PresentationManager.class, localPresentationManager);
/*     */     }
/* 244 */     return localPresentationManager;
/*     */   }
/*     */ 
/*     */   public static PresentationManager.StubFactoryFactory getStubFactoryFactory()
/*     */   {
/* 254 */     PresentationManager localPresentationManager = getPresentationManager();
/* 255 */     boolean bool = localPresentationManager.useDynamicStubs();
/* 256 */     return localPresentationManager.getStubFactoryFactory(bool);
/*     */   }
/*     */ 
/*     */   protected ORB()
/*     */   {
/* 263 */     this.wrapperMap = new ConcurrentHashMap();
/* 264 */     this.wrapper = ORBUtilSystemException.get(this, "rpc.presentation");
/*     */ 
/* 266 */     this.omgWrapper = OMGSystemException.get(this, "rpc.presentation");
/*     */ 
/* 269 */     this.typeCodeMap = new HashMap();
/*     */ 
/* 271 */     this.primitiveTypeCodeConstants = new TypeCodeImpl[] { new TypeCodeImpl(this, 0), new TypeCodeImpl(this, 1), new TypeCodeImpl(this, 2), new TypeCodeImpl(this, 3), new TypeCodeImpl(this, 4), new TypeCodeImpl(this, 5), new TypeCodeImpl(this, 6), new TypeCodeImpl(this, 7), new TypeCodeImpl(this, 8), new TypeCodeImpl(this, 9), new TypeCodeImpl(this, 10), new TypeCodeImpl(this, 11), new TypeCodeImpl(this, 12), new TypeCodeImpl(this, 13), new TypeCodeImpl(this, 14), null, null, null, new TypeCodeImpl(this, 18), null, null, null, null, new TypeCodeImpl(this, 23), new TypeCodeImpl(this, 24), new TypeCodeImpl(this, 25), new TypeCodeImpl(this, 26), new TypeCodeImpl(this, 27), new TypeCodeImpl(this, 28), new TypeCodeImpl(this, 29), new TypeCodeImpl(this, 30), new TypeCodeImpl(this, 31), new TypeCodeImpl(this, 32) };
/*     */ 
/* 307 */     this.monitoringManager = MonitoringFactories.getMonitoringManagerFactory().createMonitoringManager("orb", "ORB Management and Monitoring Root");
/*     */   }
/*     */ 
/*     */   public TypeCodeImpl get_primitive_tc(int paramInt)
/*     */   {
/* 317 */     synchronized (this) {
/* 318 */       checkShutdownState();
/*     */     }
/*     */     try {
/* 321 */       return this.primitiveTypeCodeConstants[paramInt];
/*     */     } catch (Throwable localThrowable) {
/* 323 */       throw this.wrapper.invalidTypecodeKind(localThrowable, new Integer(paramInt));
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setTypeCode(String paramString, TypeCodeImpl paramTypeCodeImpl)
/*     */   {
/* 329 */     checkShutdownState();
/* 330 */     this.typeCodeMap.put(paramString, paramTypeCodeImpl);
/*     */   }
/*     */ 
/*     */   public synchronized TypeCodeImpl getTypeCode(String paramString)
/*     */   {
/* 335 */     checkShutdownState();
/* 336 */     return (TypeCodeImpl)this.typeCodeMap.get(paramString);
/*     */   }
/*     */ 
/*     */   public MonitoringManager getMonitoringManager() {
/* 340 */     synchronized (this) {
/* 341 */       checkShutdownState();
/*     */     }
/* 343 */     return this.monitoringManager;
/*     */   }
/*     */ 
/*     */   public abstract void set_parameters(Properties paramProperties);
/*     */ 
/*     */   public abstract ORBVersion getORBVersion();
/*     */ 
/*     */   public abstract void setORBVersion(ORBVersion paramORBVersion);
/*     */ 
/*     */   public abstract IOR getFVDCodeBaseIOR();
/*     */ 
/*     */   public abstract void handleBadServerId(ObjectKey paramObjectKey);
/*     */ 
/*     */   public abstract void setBadServerIdHandler(BadServerIdHandler paramBadServerIdHandler);
/*     */ 
/*     */   public abstract void initBadServerIdHandler();
/*     */ 
/*     */   public abstract void notifyORB();
/*     */ 
/*     */   public abstract PIHandler getPIHandler();
/*     */ 
/*     */   public abstract void checkShutdownState();
/*     */ 
/*     */   public abstract boolean isDuringDispatch();
/*     */ 
/*     */   public abstract void startingDispatch();
/*     */ 
/*     */   public abstract void finishedDispatch();
/*     */ 
/*     */   public abstract int getTransientServerId();
/*     */ 
/*     */   public abstract ServiceContextRegistry getServiceContextRegistry();
/*     */ 
/*     */   public abstract RequestDispatcherRegistry getRequestDispatcherRegistry();
/*     */ 
/*     */   public abstract ORBData getORBData();
/*     */ 
/*     */   public abstract void setClientDelegateFactory(ClientDelegateFactory paramClientDelegateFactory);
/*     */ 
/*     */   public abstract ClientDelegateFactory getClientDelegateFactory();
/*     */ 
/*     */   public abstract void setCorbaContactInfoListFactory(CorbaContactInfoListFactory paramCorbaContactInfoListFactory);
/*     */ 
/*     */   public abstract CorbaContactInfoListFactory getCorbaContactInfoListFactory();
/*     */ 
/*     */   public abstract void setResolver(Resolver paramResolver);
/*     */ 
/*     */   public abstract Resolver getResolver();
/*     */ 
/*     */   public abstract void setLocalResolver(LocalResolver paramLocalResolver);
/*     */ 
/*     */   public abstract LocalResolver getLocalResolver();
/*     */ 
/*     */   public abstract void setURLOperation(Operation paramOperation);
/*     */ 
/*     */   public abstract Operation getURLOperation();
/*     */ 
/*     */   public abstract void setINSDelegate(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher);
/*     */ 
/*     */   public abstract TaggedComponentFactoryFinder getTaggedComponentFactoryFinder();
/*     */ 
/*     */   public abstract IdentifiableFactoryFinder getTaggedProfileFactoryFinder();
/*     */ 
/*     */   public abstract IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder();
/*     */ 
/*     */   public abstract ObjectKeyFactory getObjectKeyFactory();
/*     */ 
/*     */   public abstract void setObjectKeyFactory(ObjectKeyFactory paramObjectKeyFactory);
/*     */ 
/*     */   public Logger getLogger(String paramString)
/*     */   {
/* 457 */     synchronized (this) {
/* 458 */       checkShutdownState();
/*     */     }
/* 460 */     ??? = getORBData();
/*     */     String str;
/* 472 */     if (??? == null) {
/* 473 */       str = "_INITIALIZING_";
/*     */     } else {
/* 475 */       str = ((ORBData)???).getORBId();
/* 476 */       if (str.equals("")) {
/* 477 */         str = "_DEFAULT_";
/*     */       }
/*     */     }
/* 480 */     return getCORBALogger(str, paramString);
/*     */   }
/*     */ 
/*     */   public static Logger staticGetLogger(String paramString)
/*     */   {
/* 485 */     return getCORBALogger("_CORBA_", paramString);
/*     */   }
/*     */ 
/*     */   private static Logger getCORBALogger(String paramString1, String paramString2)
/*     */   {
/* 490 */     String str = "javax.enterprise.resource.corba." + paramString1 + "." + paramString2;
/*     */ 
/* 493 */     return Logger.getLogger(str, "com.sun.corba.se.impl.logging.LogStrings");
/*     */   }
/*     */ 
/*     */   public LogWrapperBase getLogWrapper(String paramString1, String paramString2, LogWrapperFactory paramLogWrapperFactory)
/*     */   {
/* 502 */     StringPair localStringPair = new StringPair(paramString1, paramString2);
/*     */ 
/* 504 */     LogWrapperBase localLogWrapperBase = (LogWrapperBase)this.wrapperMap.get(localStringPair);
/* 505 */     if (localLogWrapperBase == null) {
/* 506 */       localLogWrapperBase = paramLogWrapperFactory.create(getLogger(paramString1));
/* 507 */       this.wrapperMap.put(localStringPair, localLogWrapperBase);
/*     */     }
/*     */ 
/* 510 */     return localLogWrapperBase;
/*     */   }
/*     */ 
/*     */   public static LogWrapperBase staticGetLogWrapper(String paramString1, String paramString2, LogWrapperFactory paramLogWrapperFactory)
/*     */   {
/* 519 */     StringPair localStringPair = new StringPair(paramString1, paramString2);
/*     */ 
/* 521 */     LogWrapperBase localLogWrapperBase = (LogWrapperBase)staticWrapperMap.get(localStringPair);
/* 522 */     if (localLogWrapperBase == null) {
/* 523 */       localLogWrapperBase = paramLogWrapperFactory.create(staticGetLogger(paramString1));
/* 524 */       staticWrapperMap.put(localStringPair, localLogWrapperBase);
/*     */     }
/*     */ 
/* 527 */     return localLogWrapperBase;
/*     */   }
/*     */ 
/*     */   public ByteBufferPool getByteBufferPool()
/*     */   {
/* 536 */     synchronized (this) {
/* 537 */       checkShutdownState();
/*     */     }
/* 539 */     if (this.byteBufferPool == null) {
/* 540 */       this.byteBufferPool = new ByteBufferPoolImpl(this);
/*     */     }
/* 542 */     return this.byteBufferPool;
/*     */   }
/*     */ 
/*     */   public abstract void setThreadPoolManager(ThreadPoolManager paramThreadPoolManager);
/*     */ 
/*     */   public abstract ThreadPoolManager getThreadPoolManager();
/*     */ 
/*     */   public abstract CopierManager getCopierManager();
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orb.ORB
 * JD-Core Version:    0.6.2
 */