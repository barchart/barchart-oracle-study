/*     */ package com.sun.corba.se.impl.orb;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
/*     */ import com.sun.corba.se.impl.encoding.CodeSetComponentInfo.CodeSetComponent;
/*     */ import com.sun.corba.se.impl.legacy.connection.USLPort;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.pept.transport.Acceptor;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.orb.DataCollector;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBData;
/*     */ import com.sun.corba.se.spi.orb.ParserImplTableBase;
/*     */ import com.sun.corba.se.spi.orb.StringPair;
/*     */ import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
/*     */ import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
/*     */ import com.sun.corba.se.spi.transport.IORToSocketInfo;
/*     */ import com.sun.corba.se.spi.transport.ReadTimeouts;
/*     */ import java.net.URL;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.PortableInterceptor.ORBInitializer;
/*     */ 
/*     */ public class ORBDataParserImpl extends ParserImplTableBase
/*     */   implements ORBData
/*     */ {
/*     */   private ORB orb;
/*     */   private ORBUtilSystemException wrapper;
/*     */   private String ORBInitialHost;
/*     */   private int ORBInitialPort;
/*     */   private String ORBServerHost;
/*     */   private int ORBServerPort;
/*     */   private String listenOnAllInterfaces;
/*     */   private com.sun.corba.se.spi.legacy.connection.ORBSocketFactory legacySocketFactory;
/*     */   private com.sun.corba.se.spi.transport.ORBSocketFactory socketFactory;
/*     */   private USLPort[] userSpecifiedListenPorts;
/*     */   private IORToSocketInfo iorToSocketInfo;
/*     */   private IIOPPrimaryToContactInfo iiopPrimaryToContactInfo;
/*     */   private String orbId;
/*     */   private boolean orbServerIdPropertySpecified;
/*     */   private URL servicesURL;
/*     */   private String propertyInitRef;
/*     */   private boolean allowLocalOptimization;
/*     */   private GIOPVersion giopVersion;
/*     */   private int highWaterMark;
/*     */   private int lowWaterMark;
/*     */   private int numberToReclaim;
/*     */   private int giopFragmentSize;
/*     */   private int giopBufferSize;
/*     */   private int giop11BuffMgr;
/*     */   private int giop12BuffMgr;
/*     */   private short giopTargetAddressPreference;
/*     */   private short giopAddressDisposition;
/*     */   private boolean useByteOrderMarkers;
/*     */   private boolean useByteOrderMarkersInEncaps;
/*     */   private boolean alwaysSendCodeSetCtx;
/*     */   private boolean persistentPortInitialized;
/*     */   private int persistentServerPort;
/*     */   private boolean persistentServerIdInitialized;
/*     */   private int persistentServerId;
/*     */   private boolean serverIsORBActivated;
/*     */   private Class badServerIdHandlerClass;
/*     */   private CodeSetComponentInfo.CodeSetComponent charData;
/*     */   private CodeSetComponentInfo.CodeSetComponent wcharData;
/*     */   private ORBInitializer[] orbInitializers;
/*     */   private StringPair[] orbInitialReferences;
/*     */   private String defaultInitRef;
/*     */   private String[] debugFlags;
/*     */   private Acceptor[] acceptors;
/*     */   private CorbaContactInfoListFactory corbaContactInfoListFactory;
/*     */   private String acceptorSocketType;
/*     */   private boolean acceptorSocketUseSelectThreadToWait;
/*     */   private boolean acceptorSocketUseWorkerThreadForEvent;
/*     */   private String connectionSocketType;
/*     */   private boolean connectionSocketUseSelectThreadToWait;
/*     */   private boolean connectionSocketUseWorkerThreadForEvent;
/*     */   private ReadTimeouts readTimeouts;
/*     */   private boolean disableDirectByteBufferUse;
/*     */   private boolean enableJavaSerialization;
/*     */   private boolean useRepId;
/*     */   private CodeSetComponentInfo codesets;
/*     */ 
/*     */   public String getORBInitialHost()
/*     */   {
/* 120 */     return this.ORBInitialHost;
/*     */   }
/*     */ 
/*     */   public int getORBInitialPort()
/*     */   {
/* 125 */     return this.ORBInitialPort;
/*     */   }
/*     */ 
/*     */   public String getORBServerHost()
/*     */   {
/* 130 */     return this.ORBServerHost;
/*     */   }
/*     */ 
/*     */   public String getListenOnAllInterfaces()
/*     */   {
/* 135 */     return this.listenOnAllInterfaces;
/*     */   }
/*     */ 
/*     */   public int getORBServerPort()
/*     */   {
/* 140 */     return this.ORBServerPort;
/*     */   }
/*     */ 
/*     */   public com.sun.corba.se.spi.legacy.connection.ORBSocketFactory getLegacySocketFactory()
/*     */   {
/* 145 */     return this.legacySocketFactory;
/*     */   }
/*     */ 
/*     */   public com.sun.corba.se.spi.transport.ORBSocketFactory getSocketFactory()
/*     */   {
/* 150 */     return this.socketFactory;
/*     */   }
/*     */ 
/*     */   public USLPort[] getUserSpecifiedListenPorts()
/*     */   {
/* 155 */     return this.userSpecifiedListenPorts;
/*     */   }
/*     */ 
/*     */   public IORToSocketInfo getIORToSocketInfo()
/*     */   {
/* 160 */     return this.iorToSocketInfo;
/*     */   }
/*     */ 
/*     */   public IIOPPrimaryToContactInfo getIIOPPrimaryToContactInfo()
/*     */   {
/* 165 */     return this.iiopPrimaryToContactInfo;
/*     */   }
/*     */ 
/*     */   public String getORBId()
/*     */   {
/* 170 */     return this.orbId;
/*     */   }
/*     */ 
/*     */   public boolean getORBServerIdPropertySpecified()
/*     */   {
/* 175 */     return this.orbServerIdPropertySpecified;
/*     */   }
/*     */ 
/*     */   public boolean isLocalOptimizationAllowed()
/*     */   {
/* 180 */     return this.allowLocalOptimization;
/*     */   }
/*     */ 
/*     */   public GIOPVersion getGIOPVersion()
/*     */   {
/* 185 */     return this.giopVersion;
/*     */   }
/*     */ 
/*     */   public int getHighWaterMark()
/*     */   {
/* 190 */     return this.highWaterMark;
/*     */   }
/*     */ 
/*     */   public int getLowWaterMark()
/*     */   {
/* 195 */     return this.lowWaterMark;
/*     */   }
/*     */ 
/*     */   public int getNumberToReclaim()
/*     */   {
/* 200 */     return this.numberToReclaim;
/*     */   }
/*     */ 
/*     */   public int getGIOPFragmentSize()
/*     */   {
/* 205 */     return this.giopFragmentSize;
/*     */   }
/*     */ 
/*     */   public int getGIOPBufferSize()
/*     */   {
/* 210 */     return this.giopBufferSize;
/*     */   }
/*     */ 
/*     */   public int getGIOPBuffMgrStrategy(GIOPVersion paramGIOPVersion)
/*     */   {
/* 215 */     if (paramGIOPVersion != null) {
/* 216 */       if (paramGIOPVersion.equals(GIOPVersion.V1_0)) return 0;
/* 217 */       if (paramGIOPVersion.equals(GIOPVersion.V1_1)) return this.giop11BuffMgr;
/* 218 */       if (paramGIOPVersion.equals(GIOPVersion.V1_2)) return this.giop12BuffMgr;
/*     */     }
/*     */ 
/* 221 */     return 0;
/*     */   }
/*     */ 
/*     */   public short getGIOPTargetAddressPreference()
/*     */   {
/* 231 */     return this.giopTargetAddressPreference;
/*     */   }
/*     */ 
/*     */   public short getGIOPAddressDisposition()
/*     */   {
/* 236 */     return this.giopAddressDisposition;
/*     */   }
/*     */ 
/*     */   public boolean useByteOrderMarkers()
/*     */   {
/* 241 */     return this.useByteOrderMarkers;
/*     */   }
/*     */ 
/*     */   public boolean useByteOrderMarkersInEncapsulations()
/*     */   {
/* 246 */     return this.useByteOrderMarkersInEncaps;
/*     */   }
/*     */ 
/*     */   public boolean alwaysSendCodeSetServiceContext()
/*     */   {
/* 251 */     return this.alwaysSendCodeSetCtx;
/*     */   }
/*     */ 
/*     */   public boolean getPersistentPortInitialized()
/*     */   {
/* 256 */     return this.persistentPortInitialized;
/*     */   }
/*     */ 
/*     */   public int getPersistentServerPort()
/*     */   {
/* 267 */     if (this.persistentPortInitialized) {
/* 268 */       return this.persistentServerPort;
/*     */     }
/* 270 */     throw this.wrapper.persistentServerportNotSet(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public boolean getPersistentServerIdInitialized()
/*     */   {
/* 277 */     return this.persistentServerIdInitialized;
/*     */   }
/*     */ 
/*     */   public int getPersistentServerId()
/*     */   {
/* 304 */     if (this.persistentServerIdInitialized) {
/* 305 */       return this.persistentServerId;
/*     */     }
/* 307 */     throw this.wrapper.persistentServeridNotSet(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public boolean getServerIsORBActivated()
/*     */   {
/* 314 */     return this.serverIsORBActivated;
/*     */   }
/*     */ 
/*     */   public Class getBadServerIdHandler()
/*     */   {
/* 319 */     return this.badServerIdHandlerClass;
/*     */   }
/*     */ 
/*     */   public CodeSetComponentInfo getCodeSetComponentInfo()
/*     */   {
/* 328 */     return this.codesets;
/*     */   }
/*     */ 
/*     */   public ORBInitializer[] getORBInitializers()
/*     */   {
/* 333 */     return this.orbInitializers;
/*     */   }
/*     */ 
/*     */   public StringPair[] getORBInitialReferences()
/*     */   {
/* 338 */     return this.orbInitialReferences;
/*     */   }
/*     */ 
/*     */   public String getORBDefaultInitialReference()
/*     */   {
/* 343 */     return this.defaultInitRef;
/*     */   }
/*     */ 
/*     */   public String[] getORBDebugFlags()
/*     */   {
/* 348 */     return this.debugFlags;
/*     */   }
/*     */ 
/*     */   public Acceptor[] getAcceptors()
/*     */   {
/* 353 */     return this.acceptors;
/*     */   }
/*     */ 
/*     */   public CorbaContactInfoListFactory getCorbaContactInfoListFactory()
/*     */   {
/* 358 */     return this.corbaContactInfoListFactory;
/*     */   }
/*     */ 
/*     */   public String acceptorSocketType()
/*     */   {
/* 363 */     return this.acceptorSocketType;
/*     */   }
/*     */ 
/*     */   public boolean acceptorSocketUseSelectThreadToWait() {
/* 367 */     return this.acceptorSocketUseSelectThreadToWait;
/*     */   }
/*     */ 
/*     */   public boolean acceptorSocketUseWorkerThreadForEvent() {
/* 371 */     return this.acceptorSocketUseWorkerThreadForEvent;
/*     */   }
/*     */ 
/*     */   public String connectionSocketType() {
/* 375 */     return this.connectionSocketType;
/*     */   }
/*     */ 
/*     */   public boolean connectionSocketUseSelectThreadToWait() {
/* 379 */     return this.connectionSocketUseSelectThreadToWait;
/*     */   }
/*     */ 
/*     */   public boolean connectionSocketUseWorkerThreadForEvent() {
/* 383 */     return this.connectionSocketUseWorkerThreadForEvent;
/*     */   }
/*     */ 
/*     */   public boolean isJavaSerializationEnabled() {
/* 387 */     return this.enableJavaSerialization;
/*     */   }
/*     */ 
/*     */   public ReadTimeouts getTransportTCPReadTimeouts() {
/* 391 */     return this.readTimeouts;
/*     */   }
/*     */ 
/*     */   public boolean disableDirectByteBufferUse() {
/* 395 */     return this.disableDirectByteBufferUse;
/*     */   }
/*     */ 
/*     */   public boolean useRepId() {
/* 399 */     return this.useRepId;
/*     */   }
/*     */ 
/*     */   public ORBDataParserImpl(ORB paramORB, DataCollector paramDataCollector)
/*     */   {
/* 406 */     super(ParserTable.get().getParserData());
/* 407 */     this.orb = paramORB;
/* 408 */     this.wrapper = ORBUtilSystemException.get(paramORB, "orb.lifecycle");
/* 409 */     init(paramDataCollector);
/* 410 */     complete();
/*     */   }
/*     */ 
/*     */   public void complete()
/*     */   {
/* 415 */     this.codesets = new CodeSetComponentInfo(this.charData, this.wcharData);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orb.ORBDataParserImpl
 * JD-Core Version:    0.6.2
 */