/*     */ package com.sun.corba.se.impl.transport;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.CDRInputObject;
/*     */ import com.sun.corba.se.impl.encoding.CDROutputObject;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.impl.oa.poa.Policies;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.pept.broker.Broker;
/*     */ import com.sun.corba.se.pept.encoding.InputObject;
/*     */ import com.sun.corba.se.pept.encoding.OutputObject;
/*     */ import com.sun.corba.se.pept.protocol.MessageMediator;
/*     */ import com.sun.corba.se.pept.transport.Acceptor;
/*     */ import com.sun.corba.se.pept.transport.Connection;
/*     */ import com.sun.corba.se.pept.transport.ContactInfo;
/*     */ import com.sun.corba.se.pept.transport.EventHandler;
/*     */ import com.sun.corba.se.pept.transport.InboundConnectionCache;
/*     */ import com.sun.corba.se.pept.transport.Selector;
/*     */ import com.sun.corba.se.pept.transport.TransportManager;
/*     */ import com.sun.corba.se.spi.extension.RequestPartitioningPolicy;
/*     */ import com.sun.corba.se.spi.ior.IORTemplate;
/*     */ import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
/*     */ import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
/*     */ import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
/*     */ import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBData;
/*     */ import com.sun.corba.se.spi.orbutil.threadpool.Work;
/*     */ import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
/*     */ import com.sun.corba.se.spi.transport.CorbaAcceptor;
/*     */ import com.sun.corba.se.spi.transport.CorbaConnection;
/*     */ import com.sun.corba.se.spi.transport.CorbaTransportManager;
/*     */ import com.sun.corba.se.spi.transport.ORBSocketFactory;
/*     */ import com.sun.corba.se.spi.transport.SocketInfo;
/*     */ import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public class SocketOrChannelAcceptorImpl extends EventHandlerBase
/*     */   implements CorbaAcceptor, SocketOrChannelAcceptor, Work, SocketInfo, LegacyServerSocketEndPointInfo
/*     */ {
/*     */   protected ServerSocketChannel serverSocketChannel;
/*     */   protected ServerSocket serverSocket;
/*     */   protected int port;
/*     */   protected long enqueueTime;
/*     */   protected boolean initialized;
/*     */   protected ORBUtilSystemException wrapper;
/*     */   protected InboundConnectionCache connectionCache;
/* 101 */   protected String type = "";
/* 102 */   protected String name = "";
/*     */   protected String hostname;
/*     */   protected int locatorPort;
/*     */ 
/*     */   public SocketOrChannelAcceptorImpl(ORB paramORB)
/*     */   {
/* 109 */     this.orb = paramORB;
/* 110 */     this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
/*     */ 
/* 113 */     setWork(this);
/* 114 */     this.initialized = false;
/*     */ 
/* 117 */     this.hostname = paramORB.getORBData().getORBServerHost();
/* 118 */     this.name = "NO_NAME";
/* 119 */     this.locatorPort = -1;
/*     */   }
/*     */ 
/*     */   public SocketOrChannelAcceptorImpl(ORB paramORB, int paramInt)
/*     */   {
/* 125 */     this(paramORB);
/* 126 */     this.port = paramInt;
/*     */   }
/*     */ 
/*     */   public SocketOrChannelAcceptorImpl(ORB paramORB, int paramInt, String paramString1, String paramString2)
/*     */   {
/* 133 */     this(paramORB, paramInt);
/* 134 */     this.name = paramString1;
/* 135 */     this.type = paramString2;
/*     */   }
/*     */ 
/*     */   public boolean initialize()
/*     */   {
/* 146 */     if (this.initialized) {
/* 147 */       return false;
/*     */     }
/* 149 */     if (this.orb.transportDebugFlag) {
/* 150 */       dprint(".initialize: " + this);
/*     */     }
/* 152 */     InetSocketAddress localInetSocketAddress = null;
/*     */     try {
/* 154 */       if (this.orb.getORBData().getListenOnAllInterfaces().equals("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces")) {
/* 155 */         localInetSocketAddress = new InetSocketAddress(this.port);
/*     */       } else {
/* 157 */         String str = this.orb.getORBData().getORBServerHost();
/* 158 */         localInetSocketAddress = new InetSocketAddress(str, this.port);
/*     */       }
/* 160 */       this.serverSocket = this.orb.getORBData().getSocketFactory().createServerSocket(this.type, localInetSocketAddress);
/*     */ 
/* 162 */       internalInitialize();
/*     */     } catch (Throwable localThrowable) {
/* 164 */       throw this.wrapper.createListenerFailed(localThrowable, Integer.toString(this.port));
/*     */     }
/* 166 */     this.initialized = true;
/* 167 */     return true;
/*     */   }
/*     */ 
/*     */   protected void internalInitialize()
/*     */     throws Exception
/*     */   {
/* 177 */     this.port = this.serverSocket.getLocalPort();
/*     */ 
/* 181 */     this.orb.getCorbaTransportManager().getInboundConnectionCache(this);
/*     */ 
/* 185 */     this.serverSocketChannel = this.serverSocket.getChannel();
/*     */ 
/* 187 */     if (this.serverSocketChannel != null) {
/* 188 */       setUseSelectThreadToWait(this.orb.getORBData().acceptorSocketUseSelectThreadToWait());
/*     */ 
/* 190 */       this.serverSocketChannel.configureBlocking(!this.orb.getORBData().acceptorSocketUseSelectThreadToWait());
/*     */     }
/*     */     else
/*     */     {
/* 194 */       setUseSelectThreadToWait(false);
/*     */     }
/* 196 */     setUseWorkerThreadForEvent(this.orb.getORBData().acceptorSocketUseWorkerThreadForEvent());
/*     */   }
/*     */ 
/*     */   public boolean initialized()
/*     */   {
/* 203 */     return this.initialized;
/*     */   }
/*     */ 
/*     */   public String getConnectionCacheType()
/*     */   {
/* 208 */     return getClass().toString();
/*     */   }
/*     */ 
/*     */   public void setConnectionCache(InboundConnectionCache paramInboundConnectionCache)
/*     */   {
/* 213 */     this.connectionCache = paramInboundConnectionCache;
/*     */   }
/*     */ 
/*     */   public InboundConnectionCache getConnectionCache()
/*     */   {
/* 218 */     return this.connectionCache;
/*     */   }
/*     */ 
/*     */   public boolean shouldRegisterAcceptEvent()
/*     */   {
/* 223 */     return true;
/*     */   }
/*     */ 
/*     */   public void accept()
/*     */   {
/*     */     try {
/* 229 */       SocketChannel localSocketChannel = null;
/* 230 */       Socket localSocket = null;
/* 231 */       if (this.serverSocketChannel == null) {
/* 232 */         localSocket = this.serverSocket.accept();
/*     */       } else {
/* 234 */         localSocketChannel = this.serverSocketChannel.accept();
/* 235 */         localSocket = localSocketChannel.socket();
/*     */       }
/* 237 */       this.orb.getORBData().getSocketFactory().setAcceptedSocketOptions(this, this.serverSocket, localSocket);
/*     */ 
/* 239 */       if (this.orb.transportDebugFlag) {
/* 240 */         dprint(".accept: " + (this.serverSocketChannel == null ? this.serverSocket.toString() : this.serverSocketChannel.toString()));
/*     */       }
/*     */ 
/* 246 */       SocketOrChannelConnectionImpl localSocketOrChannelConnectionImpl = new SocketOrChannelConnectionImpl(this.orb, this, localSocket);
/*     */ 
/* 248 */       if (this.orb.transportDebugFlag) {
/* 249 */         dprint(".accept: new: " + localSocketOrChannelConnectionImpl);
/*     */       }
/*     */ 
/* 256 */       getConnectionCache().put(this, localSocketOrChannelConnectionImpl);
/*     */ 
/* 258 */       if (localSocketOrChannelConnectionImpl.shouldRegisterServerReadEvent()) {
/* 259 */         Selector localSelector = this.orb.getTransportManager().getSelector(0);
/* 260 */         localSelector.registerForEvent(localSocketOrChannelConnectionImpl.getEventHandler());
/*     */       }
/*     */ 
/* 263 */       getConnectionCache().reclaim();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 266 */       if (this.orb.transportDebugFlag) {
/* 267 */         dprint(".accept:", localIOException);
/*     */       }
/* 269 */       this.orb.getTransportManager().getSelector(0).unregisterForEvent(this);
/*     */ 
/* 271 */       this.orb.getTransportManager().getSelector(0).registerForEvent(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */     try
/*     */     {
/* 281 */       if (this.orb.transportDebugFlag) {
/* 282 */         dprint(".close->:");
/*     */       }
/* 284 */       Selector localSelector = this.orb.getTransportManager().getSelector(0);
/* 285 */       localSelector.unregisterForEvent(this);
/* 286 */       if (this.serverSocketChannel != null) {
/* 287 */         this.serverSocketChannel.close();
/*     */       }
/* 289 */       if (this.serverSocket != null)
/* 290 */         this.serverSocket.close();
/*     */     }
/*     */     catch (IOException localIOException) {
/* 293 */       if (this.orb.transportDebugFlag)
/* 294 */         dprint(".close:", localIOException);
/*     */     }
/*     */     finally {
/* 297 */       if (this.orb.transportDebugFlag)
/* 298 */         dprint(".close<-:");
/*     */     }
/*     */   }
/*     */ 
/*     */   public EventHandler getEventHandler()
/*     */   {
/* 305 */     return this;
/*     */   }
/*     */ 
/*     */   public String getObjectAdapterId()
/*     */   {
/* 315 */     return null;
/*     */   }
/*     */ 
/*     */   public String getObjectAdapterManagerId()
/*     */   {
/* 320 */     return null;
/*     */   }
/*     */ 
/*     */   public void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString)
/*     */   {
/* 327 */     Iterator localIterator = paramIORTemplate.iteratorById(0);
/*     */ 
/* 330 */     String str = this.orb.getORBData().getORBServerHost();
/*     */     Object localObject1;
/*     */     Object localObject2;
/* 332 */     if (localIterator.hasNext())
/*     */     {
/* 334 */       localObject1 = IIOPFactories.makeIIOPAddress(this.orb, str, this.port);
/*     */ 
/* 336 */       AlternateIIOPAddressComponent localAlternateIIOPAddressComponent = IIOPFactories.makeAlternateIIOPAddressComponent((IIOPAddress)localObject1);
/*     */ 
/* 339 */       while (localIterator.hasNext()) {
/* 340 */         localObject2 = (TaggedProfileTemplate)localIterator.next();
/*     */ 
/* 342 */         ((TaggedProfileTemplate)localObject2).add(localAlternateIIOPAddressComponent);
/*     */       }
/*     */     } else {
/* 345 */       localObject1 = this.orb.getORBData().getGIOPVersion();
/*     */       int i;
/* 347 */       if (paramPolicies.forceZeroPort())
/* 348 */         i = 0;
/* 349 */       else if (paramPolicies.isTransient())
/* 350 */         i = this.port;
/*     */       else {
/* 352 */         i = this.orb.getLegacyServerSocketManager().legacyGetPersistentServerPort("IIOP_CLEAR_TEXT");
/*     */       }
/*     */ 
/* 355 */       localObject2 = IIOPFactories.makeIIOPAddress(this.orb, str, i);
/*     */ 
/* 357 */       IIOPProfileTemplate localIIOPProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(this.orb, (GIOPVersion)localObject1, (IIOPAddress)localObject2);
/*     */ 
/* 359 */       if (((GIOPVersion)localObject1).supportsIORIIOPProfileComponents()) {
/* 360 */         localIIOPProfileTemplate.add(IIOPFactories.makeCodeSetsComponent(this.orb));
/* 361 */         localIIOPProfileTemplate.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
/* 362 */         RequestPartitioningPolicy localRequestPartitioningPolicy = (RequestPartitioningPolicy)paramPolicies.get_effective_policy(1398079491);
/*     */ 
/* 365 */         if (localRequestPartitioningPolicy != null) {
/* 366 */           localIIOPProfileTemplate.add(IIOPFactories.makeRequestPartitioningComponent(localRequestPartitioningPolicy.getValue()));
/*     */         }
/*     */ 
/* 370 */         if ((paramString != null) && (paramString != "")) {
/* 371 */           localIIOPProfileTemplate.add(IIOPFactories.makeJavaCodebaseComponent(paramString));
/*     */         }
/* 373 */         if (this.orb.getORBData().isJavaSerializationEnabled()) {
/* 374 */           localIIOPProfileTemplate.add(IIOPFactories.makeJavaSerializationComponent());
/*     */         }
/*     */       }
/*     */ 
/* 378 */       paramIORTemplate.add(localIIOPProfileTemplate);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getMonitoringName()
/*     */   {
/* 384 */     return "AcceptedConnections";
/*     */   }
/*     */ 
/*     */   public SelectableChannel getChannel()
/*     */   {
/* 394 */     return this.serverSocketChannel;
/*     */   }
/*     */ 
/*     */   public int getInterestOps()
/*     */   {
/* 399 */     return 16;
/*     */   }
/*     */ 
/*     */   public Acceptor getAcceptor()
/*     */   {
/* 404 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */   {
/* 409 */     throw new RuntimeException("Should not happen.");
/*     */   }
/*     */ 
/*     */   public void doWork()
/*     */   {
/*     */     try
/*     */     {
/* 427 */       if (this.orb.transportDebugFlag) {
/* 428 */         dprint(".doWork->: " + this);
/*     */       }
/* 430 */       if (this.selectionKey.isAcceptable()) {
/* 431 */         accept();
/*     */       }
/* 433 */       else if (this.orb.transportDebugFlag)
/* 434 */         dprint(".doWork: ! selectionKey.isAcceptable: " + this);
/*     */     }
/*     */     catch (SecurityException localSecurityException)
/*     */     {
/*     */       Selector localSelector1;
/* 438 */       if (this.orb.transportDebugFlag) {
/* 439 */         dprint(".doWork: ignoring SecurityException: " + localSecurityException + " " + this);
/*     */       }
/*     */ 
/* 443 */       String str = ORBUtility.getClassSecurityInfo(getClass());
/* 444 */       this.wrapper.securityExceptionInAccept(localSecurityException, str);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */       Selector localSelector2;
/* 446 */       if (this.orb.transportDebugFlag) {
/* 447 */         dprint(".doWork: ignoring Exception: " + localException + " " + this);
/*     */       }
/*     */ 
/* 451 */       this.wrapper.exceptionInAccept(localException);
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/*     */       Selector localSelector3;
/* 453 */       if (this.orb.transportDebugFlag)
/* 454 */         dprint(".doWork: ignoring Throwable: " + localThrowable + " " + this);
/*     */     }
/*     */     finally
/*     */     {
/*     */       Selector localSelector4;
/* 475 */       Selector localSelector5 = this.orb.getTransportManager().getSelector(0);
/* 476 */       localSelector5.registerInterestOps(this);
/*     */ 
/* 478 */       if (this.orb.transportDebugFlag)
/* 479 */         dprint(".doWork<-:" + this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setEnqueueTime(long paramLong)
/*     */   {
/* 486 */     this.enqueueTime = paramLong;
/*     */   }
/*     */ 
/*     */   public long getEnqueueTime()
/*     */   {
/* 491 */     return this.enqueueTime;
/*     */   }
/*     */ 
/*     */   public MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection)
/*     */   {
/* 505 */     SocketOrChannelContactInfoImpl localSocketOrChannelContactInfoImpl = new SocketOrChannelContactInfoImpl();
/* 506 */     return localSocketOrChannelContactInfoImpl.createMessageMediator(paramBroker, paramConnection);
/*     */   }
/*     */ 
/*     */   public MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator)
/*     */   {
/* 516 */     SocketOrChannelContactInfoImpl localSocketOrChannelContactInfoImpl = new SocketOrChannelContactInfoImpl();
/* 517 */     return localSocketOrChannelContactInfoImpl.finishCreatingMessageMediator(paramBroker, paramConnection, paramMessageMediator);
/*     */   }
/*     */ 
/*     */   public InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator)
/*     */   {
/* 524 */     CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
/*     */ 
/* 526 */     return new CDRInputObject((ORB)paramBroker, (CorbaConnection)paramMessageMediator.getConnection(), localCorbaMessageMediator.getDispatchBuffer(), localCorbaMessageMediator.getDispatchHeader());
/*     */   }
/*     */ 
/*     */   public OutputObject createOutputObject(Broker paramBroker, MessageMediator paramMessageMediator)
/*     */   {
/* 535 */     CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
/*     */ 
/* 537 */     return new CDROutputObject((ORB)paramBroker, localCorbaMessageMediator, localCorbaMessageMediator.getReplyHeader(), localCorbaMessageMediator.getStreamFormatVersion());
/*     */   }
/*     */ 
/*     */   public ServerSocket getServerSocket()
/*     */   {
/* 549 */     return this.serverSocket;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     String str;
/* 560 */     if (this.serverSocketChannel == null) {
/* 561 */       if (this.serverSocket == null)
/* 562 */         str = "(not initialized)";
/*     */       else
/* 564 */         str = this.serverSocket.toString();
/*     */     }
/*     */     else {
/* 567 */       str = this.serverSocketChannel.toString();
/*     */     }
/*     */ 
/* 570 */     return toStringName() + "[" + str + " " + this.type + " " + shouldUseSelectThreadToWait() + " " + shouldUseWorkerThreadForEvent() + "]";
/*     */   }
/*     */ 
/*     */   protected String toStringName()
/*     */   {
/* 582 */     return "SocketOrChannelAcceptorImpl";
/*     */   }
/*     */ 
/*     */   protected void dprint(String paramString)
/*     */   {
/* 587 */     ORBUtility.dprint(toStringName(), paramString);
/*     */   }
/*     */ 
/*     */   protected void dprint(String paramString, Throwable paramThrowable)
/*     */   {
/* 592 */     dprint(paramString);
/* 593 */     paramThrowable.printStackTrace(System.out);
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/* 604 */     return this.type;
/*     */   }
/*     */ 
/*     */   public String getHostName()
/*     */   {
/* 609 */     return this.hostname;
/*     */   }
/*     */ 
/*     */   public String getHost()
/*     */   {
/* 614 */     return this.hostname;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 619 */     return this.port;
/*     */   }
/*     */ 
/*     */   public int getLocatorPort()
/*     */   {
/* 624 */     return this.locatorPort;
/*     */   }
/*     */ 
/*     */   public void setLocatorPort(int paramInt)
/*     */   {
/* 629 */     this.locatorPort = paramInt;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 637 */     String str = this.name.equals("NO_NAME") ? toString() : this.name;
/*     */ 
/* 640 */     return str;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl
 * JD-Core Version:    0.6.2
 */