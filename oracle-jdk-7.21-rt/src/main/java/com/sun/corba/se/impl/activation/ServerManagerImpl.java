/*     */ package com.sun.corba.se.impl.activation;
/*     */ 
/*     */ import com.sun.corba.se.impl.logging.ActivationSystemException;
/*     */ import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
/*     */ import com.sun.corba.se.spi.activation.EndPointInfo;
/*     */ import com.sun.corba.se.spi.activation.InvalidORBid;
/*     */ import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
/*     */ import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
/*     */ import com.sun.corba.se.spi.activation.NoSuchEndPoint;
/*     */ import com.sun.corba.se.spi.activation.ORBAlreadyRegistered;
/*     */ import com.sun.corba.se.spi.activation.ORBPortInfo;
/*     */ import com.sun.corba.se.spi.activation.Repository;
/*     */ import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
/*     */ import com.sun.corba.se.spi.activation.Server;
/*     */ import com.sun.corba.se.spi.activation.ServerAlreadyActive;
/*     */ import com.sun.corba.se.spi.activation.ServerAlreadyInstalled;
/*     */ import com.sun.corba.se.spi.activation.ServerAlreadyUninstalled;
/*     */ import com.sun.corba.se.spi.activation.ServerHeldDown;
/*     */ import com.sun.corba.se.spi.activation.ServerNotActive;
/*     */ import com.sun.corba.se.spi.activation.ServerNotRegistered;
/*     */ import com.sun.corba.se.spi.activation._ServerManagerImplBase;
/*     */ import com.sun.corba.se.spi.ior.IOR;
/*     */ import com.sun.corba.se.spi.ior.IORFactories;
/*     */ import com.sun.corba.se.spi.ior.IORTemplate;
/*     */ import com.sun.corba.se.spi.ior.ObjectKey;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
/*     */ import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
/*     */ import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBData;
/*     */ import com.sun.corba.se.spi.protocol.ForwardException;
/*     */ import com.sun.corba.se.spi.transport.CorbaTransportManager;
/*     */ import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
/*     */ import java.io.PrintStream;
/*     */ import java.net.ServerSocket;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ServerManagerImpl extends _ServerManagerImplBase
/*     */   implements BadServerIdHandler
/*     */ {
/*     */   HashMap serverTable;
/*     */   Repository repository;
/*     */   CorbaTransportManager transportManager;
/*     */   int initialPort;
/*     */   ORB orb;
/*     */   ActivationSystemException wrapper;
/*     */   String dbDirName;
/* 101 */   boolean debug = false;
/*     */   private int serverStartupDelay;
/*     */ 
/*     */   ServerManagerImpl(ORB paramORB, CorbaTransportManager paramCorbaTransportManager, Repository paramRepository, String paramString, boolean paramBoolean)
/*     */   {
/* 108 */     this.orb = paramORB;
/* 109 */     this.wrapper = ActivationSystemException.get(paramORB, "orbd.activator");
/*     */ 
/* 111 */     this.transportManager = paramCorbaTransportManager;
/* 112 */     this.repository = paramRepository;
/* 113 */     this.dbDirName = paramString;
/* 114 */     this.debug = paramBoolean;
/*     */ 
/* 116 */     LegacyServerSocketEndPointInfo localLegacyServerSocketEndPointInfo = paramORB.getLegacyServerSocketManager().legacyGetEndpoint("BOOT_NAMING");
/*     */ 
/* 120 */     this.initialPort = ((SocketOrChannelAcceptor)localLegacyServerSocketEndPointInfo).getServerSocket().getLocalPort();
/*     */ 
/* 122 */     this.serverTable = new HashMap(256);
/*     */ 
/* 127 */     this.serverStartupDelay = 1000;
/* 128 */     String str = System.getProperty("com.sun.CORBA.activation.ServerStartupDelay");
/* 129 */     if (str != null) {
/*     */       try {
/* 131 */         this.serverStartupDelay = Integer.parseInt(str);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */     }
/* 137 */     Class localClass = paramORB.getORBData().getBadServerIdHandler();
/* 138 */     if (localClass == null)
/* 139 */       paramORB.setBadServerIdHandler(this);
/*     */     else {
/* 141 */       paramORB.initBadServerIdHandler();
/*     */     }
/*     */ 
/* 144 */     paramORB.connect(this);
/* 145 */     ProcessMonitorThread.start(this.serverTable);
/*     */   }
/*     */ 
/*     */   public void activate(int paramInt)
/*     */     throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown
/*     */   {
/* 154 */     Integer localInteger = new Integer(paramInt);
/*     */     ServerTableEntry localServerTableEntry;
/* 156 */     synchronized (this.serverTable) {
/* 157 */       localServerTableEntry = (ServerTableEntry)this.serverTable.get(localInteger);
/*     */     }
/*     */ 
/* 160 */     if ((localServerTableEntry != null) && (localServerTableEntry.isActive())) {
/* 161 */       if (this.debug) {
/* 162 */         System.out.println("ServerManagerImpl: activate for server Id " + paramInt + " failed because server is already active. " + "entry = " + localServerTableEntry);
/*     */       }
/*     */ 
/* 166 */       throw new ServerAlreadyActive(paramInt);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 175 */       localServerTableEntry = getEntry(paramInt);
/*     */ 
/* 177 */       if (this.debug) {
/* 178 */         System.out.println("ServerManagerImpl: locateServer called with  serverId=" + paramInt + " endpointType=" + "IIOP_CLEAR_TEXT" + " block=false");
/*     */       }
/*     */ 
/* 182 */       ServerLocation localServerLocation = locateServer(localServerTableEntry, "IIOP_CLEAR_TEXT", false);
/*     */ 
/* 184 */       if (this.debug)
/* 185 */         System.out.println("ServerManagerImpl: activate for server Id " + paramInt + " found location " + localServerLocation.hostname + " and activated it");
/*     */     }
/*     */     catch (NoSuchEndPoint localNoSuchEndPoint)
/*     */     {
/* 189 */       if (this.debug)
/* 190 */         System.out.println("ServerManagerImpl: activate for server Id  threw NoSuchEndpoint exception, which was ignored");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void active(int paramInt, Server paramServer)
/*     */     throws ServerNotRegistered
/*     */   {
/* 198 */     Integer localInteger = new Integer(paramInt);
/*     */ 
/* 200 */     synchronized (this.serverTable) {
/* 201 */       ServerTableEntry localServerTableEntry = (ServerTableEntry)this.serverTable.get(localInteger);
/*     */ 
/* 203 */       if (localServerTableEntry == null) {
/* 204 */         if (this.debug) {
/* 205 */           System.out.println("ServerManagerImpl: active for server Id " + paramInt + " called, but no such server is registered.");
/*     */         }
/*     */ 
/* 208 */         throw this.wrapper.serverNotExpectedToRegister();
/*     */       }
/* 210 */       if (this.debug) {
/* 211 */         System.out.println("ServerManagerImpl: active for server Id " + paramInt + " called.  This server is now active.");
/*     */       }
/*     */ 
/* 214 */       localServerTableEntry.register(paramServer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void registerEndpoints(int paramInt, String paramString, EndPointInfo[] paramArrayOfEndPointInfo)
/*     */     throws NoSuchEndPoint, ServerNotRegistered, ORBAlreadyRegistered
/*     */   {
/* 225 */     Integer localInteger = new Integer(paramInt);
/*     */ 
/* 227 */     synchronized (this.serverTable) {
/* 228 */       ServerTableEntry localServerTableEntry = (ServerTableEntry)this.serverTable.get(localInteger);
/*     */ 
/* 230 */       if (localServerTableEntry == null) {
/* 231 */         if (this.debug) {
/* 232 */           System.out.println("ServerManagerImpl: registerEndpoint for server Id " + paramInt + " called, but no such server is registered.");
/*     */         }
/*     */ 
/* 236 */         throw this.wrapper.serverNotExpectedToRegister();
/*     */       }
/* 238 */       if (this.debug) {
/* 239 */         System.out.println("ServerManagerImpl: registerEndpoints for server Id " + paramInt + " called.  This server is now active.");
/*     */       }
/*     */ 
/* 243 */       localServerTableEntry.registerPorts(paramString, paramArrayOfEndPointInfo);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int[] getActiveServers()
/*     */   {
/* 252 */     int[] arrayOfInt = null;
/*     */ 
/* 254 */     synchronized (this.serverTable) {
/* 257 */       ArrayList localArrayList = new ArrayList(0);
/*     */ 
/* 259 */       Iterator localIterator = this.serverTable.keySet().iterator();
/*     */       ServerTableEntry localServerTableEntry;
/*     */       try {
/* 262 */         while (localIterator.hasNext()) {
/* 263 */           Integer localInteger = (Integer)localIterator.next();
/*     */ 
/* 265 */           localServerTableEntry = (ServerTableEntry)this.serverTable.get(localInteger);
/*     */ 
/* 267 */           if ((localServerTableEntry.isValid()) && (localServerTableEntry.isActive())) {
/* 268 */             localArrayList.add(localServerTableEntry);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (NoSuchElementException localNoSuchElementException)
/*     */       {
/*     */       }
/*     */ 
/* 276 */       arrayOfInt = new int[localArrayList.size()];
/* 277 */       for (int j = 0; j < localArrayList.size(); j++) {
/* 278 */         localServerTableEntry = (ServerTableEntry)localArrayList.get(j);
/* 279 */         arrayOfInt[j] = localServerTableEntry.getServerId();
/*     */       }
/*     */     }
/*     */ 
/* 283 */     if (this.debug) {
/* 284 */       ??? = new StringBuffer();
/* 285 */       for (int i = 0; i < arrayOfInt.length; i++) {
/* 286 */         ((StringBuffer)???).append(' ');
/* 287 */         ((StringBuffer)???).append(arrayOfInt[i]);
/*     */       }
/*     */ 
/* 290 */       System.out.println("ServerManagerImpl: getActiveServers returns" + ((StringBuffer)???).toString());
/*     */     }
/*     */ 
/* 294 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public void shutdown(int paramInt)
/*     */     throws ServerNotActive
/*     */   {
/* 300 */     Integer localInteger = new Integer(paramInt);
/*     */ 
/* 302 */     synchronized (this.serverTable) {
/* 303 */       ServerTableEntry localServerTableEntry = (ServerTableEntry)this.serverTable.remove(localInteger);
/*     */ 
/* 305 */       if (localServerTableEntry == null) {
/* 306 */         if (this.debug) {
/* 307 */           System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " throws ServerNotActive.");
/*     */         }
/*     */ 
/* 310 */         throw new ServerNotActive(paramInt);
/*     */       }
/*     */       try
/*     */       {
/* 314 */         localServerTableEntry.destroy();
/*     */ 
/* 316 */         if (this.debug)
/* 317 */           System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " completed.");
/*     */       }
/*     */       catch (Exception localException) {
/* 320 */         if (this.debug)
/* 321 */           System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " threw exception " + localException);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private ServerTableEntry getEntry(int paramInt)
/*     */     throws ServerNotRegistered
/*     */   {
/* 330 */     Integer localInteger = new Integer(paramInt);
/* 331 */     ServerTableEntry localServerTableEntry = null;
/*     */ 
/* 333 */     synchronized (this.serverTable) {
/* 334 */       localServerTableEntry = (ServerTableEntry)this.serverTable.get(localInteger);
/*     */ 
/* 336 */       if (this.debug) {
/* 337 */         if (localServerTableEntry == null) {
/* 338 */           System.out.println("ServerManagerImpl: getEntry: no active server found.");
/*     */         }
/*     */         else {
/* 341 */           System.out.println("ServerManagerImpl: getEntry:  active server found " + localServerTableEntry + ".");
/*     */         }
/*     */       }
/*     */ 
/* 345 */       if ((localServerTableEntry != null) && (!localServerTableEntry.isValid())) {
/* 346 */         this.serverTable.remove(localInteger);
/* 347 */         localServerTableEntry = null;
/*     */       }
/*     */ 
/* 350 */       if (localServerTableEntry == null) {
/* 351 */         ServerDef localServerDef = this.repository.getServer(paramInt);
/*     */ 
/* 353 */         localServerTableEntry = new ServerTableEntry(this.wrapper, paramInt, localServerDef, this.initialPort, this.dbDirName, false, this.debug);
/*     */ 
/* 355 */         this.serverTable.put(localInteger, localServerTableEntry);
/* 356 */         localServerTableEntry.activate();
/*     */       }
/*     */     }
/*     */ 
/* 360 */     return localServerTableEntry;
/*     */   }
/*     */ 
/*     */   private ServerLocation locateServer(ServerTableEntry paramServerTableEntry, String paramString, boolean paramBoolean)
/*     */     throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown
/*     */   {
/* 367 */     ServerLocation localServerLocation = new ServerLocation();
/*     */ 
/* 373 */     if (paramBoolean) {
/*     */       ORBPortInfo[] arrayOfORBPortInfo;
/*     */       try { arrayOfORBPortInfo = paramServerTableEntry.lookup(paramString);
/*     */       } catch (Exception localException) {
/* 377 */         if (this.debug) {
/* 378 */           System.out.println("ServerManagerImpl: locateServer: server held down");
/*     */         }
/*     */ 
/* 381 */         throw new ServerHeldDown(paramServerTableEntry.getServerId());
/*     */       }
/*     */ 
/* 384 */       String str = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
/*     */ 
/* 387 */       localServerLocation.hostname = str;
/*     */       int i;
/* 389 */       if (arrayOfORBPortInfo != null)
/* 390 */         i = arrayOfORBPortInfo.length;
/*     */       else {
/* 392 */         i = 0;
/*     */       }
/* 394 */       localServerLocation.ports = new ORBPortInfo[i];
/* 395 */       for (int j = 0; j < i; j++) {
/* 396 */         localServerLocation.ports[j] = new ORBPortInfo(arrayOfORBPortInfo[j].orbId, arrayOfORBPortInfo[j].port);
/*     */ 
/* 399 */         if (this.debug) {
/* 400 */           System.out.println("ServerManagerImpl: locateServer: server located at location " + localServerLocation.hostname + " ORBid  " + arrayOfORBPortInfo[j].orbId + " Port " + arrayOfORBPortInfo[j].port);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 408 */     return localServerLocation;
/*     */   }
/*     */ 
/*     */   private ServerLocationPerORB locateServerForORB(ServerTableEntry paramServerTableEntry, String paramString, boolean paramBoolean)
/*     */     throws InvalidORBid, ServerNotRegistered, ServerHeldDown
/*     */   {
/* 415 */     ServerLocationPerORB localServerLocationPerORB = new ServerLocationPerORB();
/*     */ 
/* 421 */     if (paramBoolean) {
/*     */       EndPointInfo[] arrayOfEndPointInfo;
/*     */       try { arrayOfEndPointInfo = paramServerTableEntry.lookupForORB(paramString);
/*     */       } catch (InvalidORBid localInvalidORBid) {
/* 425 */         throw localInvalidORBid;
/*     */       } catch (Exception localException) {
/* 427 */         if (this.debug) {
/* 428 */           System.out.println("ServerManagerImpl: locateServerForORB: server held down");
/*     */         }
/*     */ 
/* 431 */         throw new ServerHeldDown(paramServerTableEntry.getServerId());
/*     */       }
/*     */ 
/* 434 */       String str = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("DEFAULT_ENDPOINT").getHostName();
/*     */ 
/* 437 */       localServerLocationPerORB.hostname = str;
/*     */       int i;
/* 439 */       if (arrayOfEndPointInfo != null)
/* 440 */         i = arrayOfEndPointInfo.length;
/*     */       else {
/* 442 */         i = 0;
/*     */       }
/* 444 */       localServerLocationPerORB.ports = new EndPointInfo[i];
/* 445 */       for (int j = 0; j < i; j++) {
/* 446 */         localServerLocationPerORB.ports[j] = new EndPointInfo(arrayOfEndPointInfo[j].endpointType, arrayOfEndPointInfo[j].port);
/*     */ 
/* 449 */         if (this.debug) {
/* 450 */           System.out.println("ServerManagerImpl: locateServer: server located at location " + localServerLocationPerORB.hostname + " endpointType  " + arrayOfEndPointInfo[j].endpointType + " Port " + arrayOfEndPointInfo[j].port);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 458 */     return localServerLocationPerORB;
/*     */   }
/*     */ 
/*     */   public String[] getORBNames(int paramInt) throws ServerNotRegistered
/*     */   {
/*     */     try
/*     */     {
/* 465 */       ServerTableEntry localServerTableEntry = getEntry(paramInt);
/* 466 */       return localServerTableEntry.getORBList(); } catch (Exception localException) {
/*     */     }
/* 468 */     throw new ServerNotRegistered(paramInt);
/*     */   }
/*     */ 
/*     */   private ServerTableEntry getRunningEntry(int paramInt)
/*     */     throws ServerNotRegistered
/*     */   {
/* 475 */     ServerTableEntry localServerTableEntry = getEntry(paramInt);
/*     */     try
/*     */     {
/* 479 */       ORBPortInfo[] arrayOfORBPortInfo = localServerTableEntry.lookup("IIOP_CLEAR_TEXT");
/*     */     } catch (Exception localException) {
/* 481 */       return null;
/*     */     }
/* 483 */     return localServerTableEntry;
/*     */   }
/*     */ 
/*     */   public void install(int paramInt)
/*     */     throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled
/*     */   {
/* 490 */     ServerTableEntry localServerTableEntry = getRunningEntry(paramInt);
/* 491 */     if (localServerTableEntry != null) {
/* 492 */       this.repository.install(paramInt);
/* 493 */       localServerTableEntry.install();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void uninstall(int paramInt)
/*     */     throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled
/*     */   {
/* 500 */     ServerTableEntry localServerTableEntry = (ServerTableEntry)this.serverTable.get(new Integer(paramInt));
/*     */ 
/* 503 */     if (localServerTableEntry != null)
/*     */     {
/* 505 */       localServerTableEntry = (ServerTableEntry)this.serverTable.remove(new Integer(paramInt));
/*     */ 
/* 508 */       if (localServerTableEntry == null) {
/* 509 */         if (this.debug) {
/* 510 */           System.out.println("ServerManagerImpl: shutdown for server Id " + paramInt + " throws ServerNotActive.");
/*     */         }
/*     */ 
/* 513 */         throw new ServerHeldDown(paramInt);
/*     */       }
/*     */ 
/* 516 */       localServerTableEntry.uninstall();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ServerLocation locateServer(int paramInt, String paramString)
/*     */     throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown
/*     */   {
/* 523 */     ServerTableEntry localServerTableEntry = getEntry(paramInt);
/* 524 */     if (this.debug) {
/* 525 */       System.out.println("ServerManagerImpl: locateServer called with  serverId=" + paramInt + " endpointType=" + paramString + " block=true");
/*     */     }
/*     */ 
/* 532 */     return locateServer(localServerTableEntry, paramString, true);
/*     */   }
/*     */ 
/*     */   public ServerLocationPerORB locateServerForORB(int paramInt, String paramString)
/*     */     throws InvalidORBid, ServerNotRegistered, ServerHeldDown
/*     */   {
/* 541 */     ServerTableEntry localServerTableEntry = getEntry(paramInt);
/*     */ 
/* 546 */     if (this.debug) {
/* 547 */       System.out.println("ServerManagerImpl: locateServerForORB called with  serverId=" + paramInt + " orbId=" + paramString + " block=true");
/*     */     }
/*     */ 
/* 550 */     return locateServerForORB(localServerTableEntry, paramString, true);
/*     */   }
/*     */ 
/*     */   public void handle(ObjectKey paramObjectKey)
/*     */   {
/* 556 */     IOR localIOR = null;
/*     */ 
/* 560 */     ObjectKeyTemplate localObjectKeyTemplate = paramObjectKey.getTemplate();
/* 561 */     int i = localObjectKeyTemplate.getServerId();
/* 562 */     String str = localObjectKeyTemplate.getORBId();
/*     */     try
/*     */     {
/* 567 */       ServerTableEntry localServerTableEntry = getEntry(i);
/* 568 */       ServerLocationPerORB localServerLocationPerORB = locateServerForORB(localServerTableEntry, str, true);
/*     */ 
/* 570 */       if (this.debug) {
/* 571 */         System.out.println("ServerManagerImpl: handle called for server id" + i + "  orbid  " + str);
/*     */       }
/*     */ 
/* 579 */       int j = 0;
/* 580 */       EndPointInfo[] arrayOfEndPointInfo = localServerLocationPerORB.ports;
/* 581 */       for (int k = 0; k < arrayOfEndPointInfo.length; k++) {
/* 582 */         if (arrayOfEndPointInfo[k].endpointType.equals("IIOP_CLEAR_TEXT")) {
/* 583 */           j = arrayOfEndPointInfo[k].port;
/* 584 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 590 */       IIOPAddress localIIOPAddress = IIOPFactories.makeIIOPAddress(this.orb, localServerLocationPerORB.hostname, j);
/*     */ 
/* 592 */       IIOPProfileTemplate localIIOPProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(this.orb, GIOPVersion.V1_2, localIIOPAddress);
/*     */ 
/* 595 */       if (GIOPVersion.V1_2.supportsIORIIOPProfileComponents()) {
/* 596 */         localIIOPProfileTemplate.add(IIOPFactories.makeCodeSetsComponent(this.orb));
/* 597 */         localIIOPProfileTemplate.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
/*     */       }
/* 599 */       IORTemplate localIORTemplate = IORFactories.makeIORTemplate(localObjectKeyTemplate);
/* 600 */       localIORTemplate.add(localIIOPProfileTemplate);
/*     */ 
/* 602 */       localIOR = localIORTemplate.makeIOR(this.orb, "IDL:org/omg/CORBA/Object:1.0", paramObjectKey.getId());
/*     */     }
/*     */     catch (Exception localException1) {
/* 605 */       throw this.wrapper.errorInBadServerIdHandler(localException1);
/*     */     }
/*     */ 
/* 608 */     if (this.debug) {
/* 609 */       System.out.println("ServerManagerImpl: handle throws ForwardException");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 618 */       Thread.sleep(this.serverStartupDelay);
/*     */     } catch (Exception localException2) {
/* 620 */       System.out.println("Exception = " + localException2);
/* 621 */       localException2.printStackTrace();
/*     */     }
/*     */ 
/* 624 */     throw new ForwardException(this.orb, localIOR);
/*     */   }
/*     */ 
/*     */   public int getEndpoint(String paramString) throws NoSuchEndPoint
/*     */   {
/* 629 */     return this.orb.getLegacyServerSocketManager().legacyGetTransientServerPort(paramString);
/*     */   }
/*     */ 
/*     */   public int getServerPortForType(ServerLocationPerORB paramServerLocationPerORB, String paramString)
/*     */     throws NoSuchEndPoint
/*     */   {
/* 637 */     EndPointInfo[] arrayOfEndPointInfo = paramServerLocationPerORB.ports;
/* 638 */     for (int i = 0; i < arrayOfEndPointInfo.length; i++) {
/* 639 */       if (arrayOfEndPointInfo[i].endpointType.equals(paramString)) {
/* 640 */         return arrayOfEndPointInfo[i].port;
/*     */       }
/*     */     }
/* 643 */     throw new NoSuchEndPoint();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.activation.ServerManagerImpl
 * JD-Core Version:    0.6.2
 */