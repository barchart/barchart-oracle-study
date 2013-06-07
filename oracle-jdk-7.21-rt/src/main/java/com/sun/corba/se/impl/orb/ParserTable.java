/*      */ package com.sun.corba.se.impl.orb;
/*      */ 
/*      */ import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
/*      */ import com.sun.corba.se.impl.legacy.connection.USLPort;
/*      */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*      */ import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
/*      */ import com.sun.corba.se.impl.orbutil.ORBClassLoader;
/*      */ import com.sun.corba.se.impl.transport.DefaultIORToSocketInfoImpl;
/*      */ import com.sun.corba.se.impl.transport.DefaultSocketFactoryImpl;
/*      */ import com.sun.corba.se.pept.broker.Broker;
/*      */ import com.sun.corba.se.pept.encoding.InputObject;
/*      */ import com.sun.corba.se.pept.encoding.OutputObject;
/*      */ import com.sun.corba.se.pept.protocol.MessageMediator;
/*      */ import com.sun.corba.se.pept.transport.Acceptor;
/*      */ import com.sun.corba.se.pept.transport.Connection;
/*      */ import com.sun.corba.se.pept.transport.ContactInfo;
/*      */ import com.sun.corba.se.pept.transport.EventHandler;
/*      */ import com.sun.corba.se.pept.transport.InboundConnectionCache;
/*      */ import com.sun.corba.se.spi.ior.IOR;
/*      */ import com.sun.corba.se.spi.ior.ObjectKey;
/*      */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*      */ import com.sun.corba.se.spi.orb.Operation;
/*      */ import com.sun.corba.se.spi.orb.OperationFactory;
/*      */ import com.sun.corba.se.spi.orb.ParserData;
/*      */ import com.sun.corba.se.spi.orb.ParserDataFactory;
/*      */ import com.sun.corba.se.spi.orb.StringPair;
/*      */ import com.sun.corba.se.spi.transport.CorbaContactInfoList;
/*      */ import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
/*      */ import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
/*      */ import com.sun.corba.se.spi.transport.IORToSocketInfo;
/*      */ import com.sun.corba.se.spi.transport.ReadTimeouts;
/*      */ import com.sun.corba.se.spi.transport.ReadTimeoutsFactory;
/*      */ import com.sun.corba.se.spi.transport.SocketInfo;
/*      */ import com.sun.corba.se.spi.transport.TransportDefault;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.omg.CORBA.LocalObject;
/*      */ import org.omg.PortableInterceptor.ORBInitInfo;
/*      */ import org.omg.PortableInterceptor.ORBInitializer;
/*      */ 
/*      */ public class ParserTable
/*      */ {
/*   94 */   private static String MY_CLASS_NAME = ParserTable.class.getName();
/*      */ 
/*   96 */   private static ParserTable myInstance = new ParserTable();
/*      */   private ORBUtilSystemException wrapper;
/*      */   private ParserData[] parserData;
/*      */ 
/*      */   public static ParserTable get()
/*      */   {
/*  102 */     return myInstance;
/*      */   }
/*      */ 
/*      */   public ParserData[] getParserData()
/*      */   {
/*  109 */     ParserData[] arrayOfParserData = new ParserData[this.parserData.length];
/*  110 */     System.arraycopy(this.parserData, 0, arrayOfParserData, 0, this.parserData.length);
/*  111 */     return arrayOfParserData;
/*      */   }
/*      */ 
/*      */   private ParserTable() {
/*  115 */     this.wrapper = ORBUtilSystemException.get("orb.lifecycle");
/*      */ 
/*  117 */     String str1 = "65537,65801,65568";
/*      */ 
/*  122 */     String[] arrayOfString = { "subcontract", "poa", "transport" };
/*      */ 
/*  124 */     USLPort[] arrayOfUSLPort = { new USLPort("FOO", 2701), new USLPort("BAR", 3333) };
/*      */ 
/*  126 */     ReadTimeouts localReadTimeouts = TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20);
/*      */ 
/*  133 */     ORBInitializer[] arrayOfORBInitializer = { null, new TestORBInitializer1(), new TestORBInitializer2() };
/*      */ 
/*  137 */     StringPair[] arrayOfStringPair1 = { new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(MY_CLASS_NAME + "$TestORBInitializer1", "dummy"), new StringPair(MY_CLASS_NAME + "$TestORBInitializer2", "dummy") };
/*      */ 
/*  142 */     Acceptor[] arrayOfAcceptor = { new TestAcceptor2(), new TestAcceptor1(), null };
/*      */ 
/*  148 */     StringPair[] arrayOfStringPair2 = { new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(MY_CLASS_NAME + "$TestAcceptor1", "dummy"), new StringPair(MY_CLASS_NAME + "$TestAcceptor2", "dummy") };
/*      */ 
/*  153 */     StringPair[] arrayOfStringPair3 = { new StringPair("Foo", "ior:930492049394"), new StringPair("Bar", "ior:3453465785633576") };
/*      */ 
/*  157 */     URL localURL = null;
/*  158 */     String str2 = "corbaloc::camelot/NameService";
/*      */     try
/*      */     {
/*  161 */       localURL = new URL(str2);
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/*      */ 
/*  169 */     ParserData[] arrayOfParserData = { ParserDataFactory.make("com.sun.CORBA.ORBDebug", OperationFactory.listAction(",", OperationFactory.stringAction()), "debugFlags", new String[0], arrayOfString, "subcontract,poa,transport"), ParserDataFactory.make("org.omg.CORBA.ORBInitialHost", OperationFactory.stringAction(), "ORBInitialHost", "", "Foo", "Foo"), ParserDataFactory.make("org.omg.CORBA.ORBInitialPort", OperationFactory.integerAction(), "ORBInitialPort", new Integer(900), new Integer(27314), "27314"), ParserDataFactory.make("com.sun.CORBA.ORBServerHost", OperationFactory.stringAction(), "ORBServerHost", "", "camelot", "camelot"), ParserDataFactory.make("com.sun.CORBA.ORBServerPort", OperationFactory.integerAction(), "ORBServerPort", new Integer(0), new Integer(38143), "38143"), ParserDataFactory.make("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", OperationFactory.stringAction(), "listenOnAllInterfaces", "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBId", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.ORBid", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(-1), new Integer(1234), "1234"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "1234"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "1234"), ParserDataFactory.make("com.sun.CORBA.connection.ORBHighWaterMark", OperationFactory.integerAction(), "highWaterMark", new Integer(240), new Integer(3745), "3745"), ParserDataFactory.make("com.sun.CORBA.connection.ORBLowWaterMark", OperationFactory.integerAction(), "lowWaterMark", new Integer(100), new Integer(12), "12"), ParserDataFactory.make("com.sun.CORBA.connection.ORBNumberToReclaim", OperationFactory.integerAction(), "numberToReclaim", new Integer(5), new Integer(231), "231"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOPVersion", makeGVOperation(), "giopVersion", GIOPVersion.DEFAULT_VERSION, new GIOPVersion(2, 3), "2.3"), ParserDataFactory.make("com.sun.CORBA.giop.ORBFragmentSize", makeFSOperation(), "giopFragmentSize", new Integer(1024), new Integer(65536), "65536"), ParserDataFactory.make("com.sun.CORBA.giop.ORBBufferSize", OperationFactory.integerAction(), "giopBufferSize", new Integer(1024), new Integer(234000), "234000"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP11BuffMgr", makeBMGROperation(), "giop11BuffMgr", new Integer(0), new Integer(1), "CLCT"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP12BuffMgr", makeBMGROperation(), "giop12BuffMgr", new Integer(2), new Integer(0), "GROW"), ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", OperationFactory.compose(OperationFactory.integerRangeAction(0, 3), OperationFactory.convertIntegerToShort()), "giopTargetAddressPreference", new Short(3), new Short(2), "2"), ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", makeADOperation(), "giopAddressDisposition", new Short(0), new Short(2), "2"), ParserDataFactory.make("com.sun.CORBA.codeset.AlwaysSendCodeSetCtx", OperationFactory.booleanAction(), "alwaysSendCodeSetCtx", Boolean.TRUE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkers", OperationFactory.booleanAction(), "useByteOrderMarkers", Boolean.valueOf(true), Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkersInEncaps", OperationFactory.booleanAction(), "useByteOrderMarkersInEncaps", Boolean.valueOf(false), Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.charsets", makeCSOperation(), "charData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getCharComponent(), CodeSetComponentInfo.createFromString(str1), str1), ParserDataFactory.make("com.sun.CORBA.codeset.wcharsets", makeCSOperation(), "wcharData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getWCharComponent(), CodeSetComponentInfo.createFromString(str1), str1), ParserDataFactory.make("com.sun.CORBA.ORBAllowLocalOptimization", OperationFactory.booleanAction(), "allowLocalOptimization", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.legacy.connection.ORBSocketFactoryClass", makeLegacySocketFactoryOperation(), "legacySocketFactory", null, new TestLegacyORBSocketFactory(), MY_CLASS_NAME + "$TestLegacyORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBSocketFactoryClass", makeSocketFactoryOperation(), "socketFactory", new DefaultSocketFactoryImpl(), new TestORBSocketFactory(), MY_CLASS_NAME + "$TestORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBListenSocket", makeUSLOperation(), "userSpecifiedListenPorts", new USLPort[0], arrayOfUSLPort, "FOO:2701,BAR:3333"), ParserDataFactory.make("com.sun.CORBA.transport.ORBIORToSocketInfoClass", makeIORToSocketInfoOperation(), "iorToSocketInfo", new DefaultIORToSocketInfoImpl(), new TestIORToSocketInfo(), MY_CLASS_NAME + "$TestIORToSocketInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBIIOPPrimaryToContactInfoClass", makeIIOPPrimaryToContactInfoOperation(), "iiopPrimaryToContactInfo", null, new TestIIOPPrimaryToContactInfo(), MY_CLASS_NAME + "$TestIIOPPrimaryToContactInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBContactInfoList", makeContactInfoListFactoryOperation(), "corbaContactInfoListFactory", null, new TestContactInfoListFactory(), MY_CLASS_NAME + "$TestContactInfoListFactory"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.integerAction(), "persistentServerPort", new Integer(0), new Integer(2743), "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.setFlagAction(), "persistentPortInitialized", Boolean.FALSE, Boolean.TRUE, "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(0), new Integer(294), "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBActivated", OperationFactory.booleanAction(), "serverIsORBActivated", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.POA.ORBBadServerIdHandlerClass", OperationFactory.classAction(), "badServerIdHandlerClass", null, TestBadServerIdHandler.class, MY_CLASS_NAME + "$TestBadServerIdHandler"), ParserDataFactory.make("org.omg.PortableInterceptor.ORBInitializerClass.", makeROIOperation(), "orbInitializers", new ORBInitializer[0], arrayOfORBInitializer, arrayOfStringPair1, ORBInitializer.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptor", makeAcceptorInstantiationOperation(), "acceptors", new Acceptor[0], arrayOfAcceptor, arrayOfStringPair2, Acceptor.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketType", OperationFactory.stringAction(), "acceptorSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "acceptorSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "acceptorSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketType", OperationFactory.stringAction(), "connectionSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "connectionSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "connectionSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBDisableDirectByteBufferUse", OperationFactory.booleanAction(), "disableDirectByteBufferUse", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBTCPReadTimeouts", makeTTCPRTOperation(), "readTimeouts", TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20), localReadTimeouts, "100:3000:300:20"), ParserDataFactory.make("com.sun.CORBA.encoding.ORBEnableJavaSerialization", OperationFactory.booleanAction(), "enableJavaSerialization", Boolean.FALSE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.ORBUseRepId", OperationFactory.booleanAction(), "useRepId", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("org.omg.CORBA.ORBInitRef", OperationFactory.identityAction(), "orbInitialReferences", new StringPair[0], arrayOfStringPair3, arrayOfStringPair3, StringPair.class) };
/*      */ 
/*  448 */     this.parserData = arrayOfParserData;
/*      */   }
/*      */ 
/*      */   private Operation makeTTCPRTOperation()
/*      */   {
/*  465 */     Operation[] arrayOfOperation = { OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction() };
/*      */ 
/*  470 */     Operation localOperation1 = OperationFactory.sequenceAction(":", arrayOfOperation);
/*      */ 
/*  472 */     Operation local1 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  475 */         Object[] arrayOfObject = (Object[])paramAnonymousObject;
/*  476 */         Integer localInteger1 = (Integer)arrayOfObject[0];
/*  477 */         Integer localInteger2 = (Integer)arrayOfObject[1];
/*  478 */         Integer localInteger3 = (Integer)arrayOfObject[2];
/*  479 */         Integer localInteger4 = (Integer)arrayOfObject[3];
/*  480 */         return TransportDefault.makeReadTimeoutsFactory().create(localInteger1.intValue(), localInteger2.intValue(), localInteger3.intValue(), localInteger4.intValue());
/*      */       }
/*      */     };
/*  488 */     Operation localOperation2 = OperationFactory.compose(localOperation1, local1);
/*  489 */     return localOperation2;
/*      */   }
/*      */ 
/*      */   private Operation makeUSLOperation()
/*      */   {
/*  494 */     Operation[] arrayOfOperation = { OperationFactory.stringAction(), OperationFactory.integerAction() };
/*      */ 
/*  496 */     Operation localOperation1 = OperationFactory.sequenceAction(":", arrayOfOperation);
/*      */ 
/*  498 */     Operation local2 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  501 */         Object[] arrayOfObject = (Object[])paramAnonymousObject;
/*  502 */         String str = (String)arrayOfObject[0];
/*  503 */         Integer localInteger = (Integer)arrayOfObject[1];
/*  504 */         return new USLPort(str, localInteger.intValue());
/*      */       }
/*      */     };
/*  508 */     Operation localOperation2 = OperationFactory.compose(localOperation1, local2);
/*  509 */     Operation localOperation3 = OperationFactory.listAction(",", localOperation2);
/*  510 */     return localOperation3;
/*      */   }
/*      */ 
/*      */   private Operation makeMapOperation(final Map paramMap)
/*      */   {
/*  618 */     return new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  621 */         return paramMap.get(paramAnonymousObject);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private Operation makeBMGROperation()
/*      */   {
/*  628 */     HashMap localHashMap = new HashMap();
/*  629 */     localHashMap.put("GROW", new Integer(0));
/*  630 */     localHashMap.put("CLCT", new Integer(1));
/*  631 */     localHashMap.put("STRM", new Integer(2));
/*  632 */     return makeMapOperation(localHashMap);
/*      */   }
/*      */ 
/*      */   private Operation makeLegacySocketFactoryOperation()
/*      */   {
/*  637 */     Operation local4 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  640 */         String str = (String)paramAnonymousObject;
/*      */         try
/*      */         {
/*  643 */           Class localClass = ORBClassLoader.loadClass(str);
/*      */ 
/*  648 */           if (com.sun.corba.se.spi.legacy.connection.ORBSocketFactory.class.isAssignableFrom(localClass)) {
/*  649 */             return localClass.newInstance();
/*      */           }
/*  651 */           throw ParserTable.this.wrapper.illegalSocketFactoryType(localClass.toString());
/*      */         }
/*      */         catch (Exception localException)
/*      */         {
/*  657 */           throw ParserTable.this.wrapper.badCustomSocketFactory(localException, str);
/*      */         }
/*      */       }
/*      */     };
/*  662 */     return local4;
/*      */   }
/*      */ 
/*      */   private Operation makeSocketFactoryOperation()
/*      */   {
/*  667 */     Operation local5 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  670 */         String str = (String)paramAnonymousObject;
/*      */         try
/*      */         {
/*  673 */           Class localClass = ORBClassLoader.loadClass(str);
/*      */ 
/*  677 */           if (com.sun.corba.se.spi.transport.ORBSocketFactory.class.isAssignableFrom(localClass)) {
/*  678 */             return localClass.newInstance();
/*      */           }
/*  680 */           throw ParserTable.this.wrapper.illegalSocketFactoryType(localClass.toString());
/*      */         }
/*      */         catch (Exception localException)
/*      */         {
/*  686 */           throw ParserTable.this.wrapper.badCustomSocketFactory(localException, str);
/*      */         }
/*      */       }
/*      */     };
/*  691 */     return local5;
/*      */   }
/*      */ 
/*      */   private Operation makeIORToSocketInfoOperation()
/*      */   {
/*  696 */     Operation local6 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  699 */         String str = (String)paramAnonymousObject;
/*      */         try
/*      */         {
/*  702 */           Class localClass = ORBClassLoader.loadClass(str);
/*      */ 
/*  706 */           if (IORToSocketInfo.class.isAssignableFrom(localClass)) {
/*  707 */             return localClass.newInstance();
/*      */           }
/*  709 */           throw ParserTable.this.wrapper.illegalIorToSocketInfoType(localClass.toString());
/*      */         }
/*      */         catch (Exception localException)
/*      */         {
/*  715 */           throw ParserTable.this.wrapper.badCustomIorToSocketInfo(localException, str);
/*      */         }
/*      */       }
/*      */     };
/*  720 */     return local6;
/*      */   }
/*      */ 
/*      */   private Operation makeIIOPPrimaryToContactInfoOperation()
/*      */   {
/*  725 */     Operation local7 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  728 */         String str = (String)paramAnonymousObject;
/*      */         try
/*      */         {
/*  731 */           Class localClass = ORBClassLoader.loadClass(str);
/*      */ 
/*  735 */           if (IIOPPrimaryToContactInfo.class.isAssignableFrom(localClass)) {
/*  736 */             return localClass.newInstance();
/*      */           }
/*  738 */           throw ParserTable.this.wrapper.illegalIiopPrimaryToContactInfoType(localClass.toString());
/*      */         }
/*      */         catch (Exception localException)
/*      */         {
/*  744 */           throw ParserTable.this.wrapper.badCustomIiopPrimaryToContactInfo(localException, str);
/*      */         }
/*      */       }
/*      */     };
/*  749 */     return local7;
/*      */   }
/*      */ 
/*      */   private Operation makeContactInfoListFactoryOperation()
/*      */   {
/*  754 */     Operation local8 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  757 */         String str = (String)paramAnonymousObject;
/*      */         try
/*      */         {
/*  760 */           Class localClass = ORBClassLoader.loadClass(str);
/*      */ 
/*  765 */           if (CorbaContactInfoListFactory.class.isAssignableFrom(localClass))
/*      */           {
/*  767 */             return localClass.newInstance();
/*      */           }
/*  769 */           throw ParserTable.this.wrapper.illegalContactInfoListFactoryType(localClass.toString());
/*      */         }
/*      */         catch (Exception localException)
/*      */         {
/*  776 */           throw ParserTable.this.wrapper.badContactInfoListFactory(localException, str);
/*      */         }
/*      */       }
/*      */     };
/*  781 */     return local8;
/*      */   }
/*      */ 
/*      */   private Operation makeCSOperation()
/*      */   {
/*  786 */     Operation local9 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  789 */         String str = (String)paramAnonymousObject;
/*  790 */         return CodeSetComponentInfo.createFromString(str);
/*      */       }
/*      */     };
/*  794 */     return local9;
/*      */   }
/*      */ 
/*      */   private Operation makeADOperation()
/*      */   {
/*  799 */     Operation local10 = new Operation() {
/*  800 */       private Integer[] map = { new Integer(0), new Integer(1), new Integer(2), new Integer(0) };
/*      */ 
/*      */       public Object operate(Object paramAnonymousObject)
/*      */       {
/*  808 */         int i = ((Integer)paramAnonymousObject).intValue();
/*  809 */         return this.map[i];
/*      */       }
/*      */     };
/*  813 */     Operation localOperation1 = OperationFactory.integerRangeAction(0, 3);
/*  814 */     Operation localOperation2 = OperationFactory.compose(localOperation1, local10);
/*  815 */     Operation localOperation3 = OperationFactory.compose(localOperation2, OperationFactory.convertIntegerToShort());
/*  816 */     return localOperation3;
/*      */   }
/*      */ 
/*      */   private Operation makeFSOperation() {
/*  820 */     Operation local11 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  823 */         int i = ((Integer)paramAnonymousObject).intValue();
/*  824 */         if (i < 32) {
/*  825 */           throw ParserTable.this.wrapper.fragmentSizeMinimum(new Integer(i), new Integer(32));
/*      */         }
/*      */ 
/*  829 */         if (i % 8 != 0) {
/*  830 */           throw ParserTable.this.wrapper.fragmentSizeDiv(new Integer(i), new Integer(8));
/*      */         }
/*      */ 
/*  833 */         return paramAnonymousObject;
/*      */       }
/*      */     };
/*  837 */     Operation localOperation = OperationFactory.compose(OperationFactory.integerAction(), local11);
/*      */ 
/*  839 */     return localOperation;
/*      */   }
/*      */ 
/*      */   private Operation makeGVOperation() {
/*  843 */     Operation localOperation1 = OperationFactory.listAction(".", OperationFactory.integerAction());
/*      */ 
/*  845 */     Operation local12 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  848 */         Object[] arrayOfObject = (Object[])paramAnonymousObject;
/*  849 */         int i = ((Integer)arrayOfObject[0]).intValue();
/*  850 */         int j = ((Integer)arrayOfObject[1]).intValue();
/*      */ 
/*  852 */         return new GIOPVersion(i, j);
/*      */       }
/*      */     };
/*  856 */     Operation localOperation2 = OperationFactory.compose(localOperation1, local12);
/*  857 */     return localOperation2;
/*      */   }
/*      */ 
/*      */   private Operation makeROIOperation()
/*      */   {
/*  895 */     Operation localOperation1 = OperationFactory.classAction();
/*  896 */     Operation localOperation2 = OperationFactory.suffixAction();
/*  897 */     Operation localOperation3 = OperationFactory.compose(localOperation2, localOperation1);
/*  898 */     Operation localOperation4 = OperationFactory.maskErrorAction(localOperation3);
/*      */ 
/*  900 */     Operation local13 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/*  903 */         final Class localClass = (Class)paramAnonymousObject;
/*  904 */         if (localClass == null) {
/*  905 */           return null;
/*      */         }
/*      */ 
/*  910 */         if (ORBInitializer.class.isAssignableFrom(localClass))
/*      */         {
/*  914 */           ORBInitializer localORBInitializer = null;
/*      */           try
/*      */           {
/*  917 */             localORBInitializer = (ORBInitializer)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */             {
/*      */               public Object run()
/*      */                 throws InstantiationException, IllegalAccessException
/*      */               {
/*  922 */                 return localClass.newInstance();
/*      */               }
/*      */             });
/*      */           }
/*      */           catch (PrivilegedActionException localPrivilegedActionException)
/*      */           {
/*  928 */             throw ParserTable.this.wrapper.orbInitializerFailure(localPrivilegedActionException.getException(), localClass.getName());
/*      */           }
/*      */           catch (Exception localException) {
/*  931 */             throw ParserTable.this.wrapper.orbInitializerFailure(localException, localClass.getName());
/*      */           }
/*      */ 
/*  934 */           return localORBInitializer;
/*      */         }
/*  936 */         throw ParserTable.this.wrapper.orbInitializerType(localClass.getName());
/*      */       }
/*      */     };
/*  941 */     Operation localOperation5 = OperationFactory.compose(localOperation4, local13);
/*      */ 
/*  943 */     return localOperation5;
/*      */   }
/*      */ 
/*      */   private Operation makeAcceptorInstantiationOperation()
/*      */   {
/* 1010 */     Operation localOperation1 = OperationFactory.classAction();
/* 1011 */     Operation localOperation2 = OperationFactory.suffixAction();
/* 1012 */     Operation localOperation3 = OperationFactory.compose(localOperation2, localOperation1);
/* 1013 */     Operation localOperation4 = OperationFactory.maskErrorAction(localOperation3);
/*      */ 
/* 1015 */     Operation local14 = new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject) {
/* 1018 */         final Class localClass = (Class)paramAnonymousObject;
/* 1019 */         if (localClass == null) {
/* 1020 */           return null;
/*      */         }
/*      */ 
/* 1025 */         if (Acceptor.class.isAssignableFrom(localClass))
/*      */         {
/* 1028 */           Acceptor localAcceptor = null;
/*      */           try
/*      */           {
/* 1031 */             localAcceptor = (Acceptor)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */             {
/*      */               public Object run()
/*      */                 throws InstantiationException, IllegalAccessException
/*      */               {
/* 1036 */                 return localClass.newInstance();
/*      */               }
/*      */             });
/*      */           }
/*      */           catch (PrivilegedActionException localPrivilegedActionException)
/*      */           {
/* 1042 */             throw ParserTable.this.wrapper.acceptorInstantiationFailure(localPrivilegedActionException.getException(), localClass.getName());
/*      */           }
/*      */           catch (Exception localException) {
/* 1045 */             throw ParserTable.this.wrapper.acceptorInstantiationFailure(localException, localClass.getName());
/*      */           }
/*      */ 
/* 1048 */           return localAcceptor;
/*      */         }
/* 1050 */         throw ParserTable.this.wrapper.acceptorInstantiationTypeFailure(localClass.getName());
/*      */       }
/*      */     };
/* 1055 */     Operation localOperation5 = OperationFactory.compose(localOperation4, local14);
/*      */ 
/* 1057 */     return localOperation5;
/*      */   }
/*      */ 
/*      */   private Operation makeInitRefOperation() {
/* 1061 */     return new Operation()
/*      */     {
/*      */       public Object operate(Object paramAnonymousObject)
/*      */       {
/* 1065 */         String[] arrayOfString = (String[])paramAnonymousObject;
/* 1066 */         if (arrayOfString.length != 2) {
/* 1067 */           throw ParserTable.this.wrapper.orbInitialreferenceSyntax();
/*      */         }
/* 1069 */         return arrayOfString[0] + "=" + arrayOfString[1];
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static final class TestAcceptor1
/*      */     implements Acceptor
/*      */   {
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  951 */       return paramObject instanceof TestAcceptor1;
/*      */     }
/*  953 */     public boolean initialize() { return true; } 
/*  954 */     public boolean initialized() { return true; } 
/*  955 */     public String getConnectionCacheType() { return "FOO"; } 
/*      */     public void setConnectionCache(InboundConnectionCache paramInboundConnectionCache) {  } 
/*  957 */     public InboundConnectionCache getConnectionCache() { return null; } 
/*  958 */     public boolean shouldRegisterAcceptEvent() { return true; } 
/*      */     public void setUseSelectThreadForConnections(boolean paramBoolean) {  } 
/*  960 */     public boolean shouldUseSelectThreadForConnections() { return true; } 
/*      */     public void setUseWorkerThreadForConnections(boolean paramBoolean) {  } 
/*  962 */     public boolean shouldUseWorkerThreadForConnections() { return true; } 
/*      */     public void accept() {  } 
/*      */     public void close() {  } 
/*  965 */     public EventHandler getEventHandler() { return null; } 
/*      */     public MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection) {
/*  967 */       return null;
/*      */     }
/*      */     public MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator) {
/*  970 */       return null;
/*      */     }
/*  972 */     public InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator) { return null; } 
/*      */     public OutputObject createOutputObject(Broker paramBroker, MessageMediator paramMessageMediator) {
/*  974 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class TestAcceptor2 implements Acceptor
/*      */   {
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  982 */       return paramObject instanceof TestAcceptor2;
/*      */     }
/*  984 */     public boolean initialize() { return true; } 
/*  985 */     public boolean initialized() { return true; } 
/*  986 */     public String getConnectionCacheType() { return "FOO"; } 
/*      */     public void setConnectionCache(InboundConnectionCache paramInboundConnectionCache) {  } 
/*  988 */     public InboundConnectionCache getConnectionCache() { return null; } 
/*  989 */     public boolean shouldRegisterAcceptEvent() { return true; } 
/*      */     public void setUseSelectThreadForConnections(boolean paramBoolean) {  } 
/*  991 */     public boolean shouldUseSelectThreadForConnections() { return true; } 
/*      */     public void setUseWorkerThreadForConnections(boolean paramBoolean) {  } 
/*  993 */     public boolean shouldUseWorkerThreadForConnections() { return true; } 
/*      */     public void accept() {  } 
/*      */     public void close() {  } 
/*  996 */     public EventHandler getEventHandler() { return null; } 
/*      */     public MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection) {
/*  998 */       return null;
/*      */     }
/*      */     public MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator) {
/* 1001 */       return null;
/*      */     }
/* 1003 */     public InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator) { return null; } 
/*      */     public OutputObject createOutputObject(Broker paramBroker, MessageMediator paramMessageMediator) {
/* 1005 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public final class TestBadServerIdHandler
/*      */     implements BadServerIdHandler
/*      */   {
/*      */     public TestBadServerIdHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  455 */       return paramObject instanceof TestBadServerIdHandler;
/*      */     }
/*      */ 
/*      */     public void handle(ObjectKey paramObjectKey)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class TestContactInfoListFactory
/*      */     implements CorbaContactInfoListFactory
/*      */   {
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  608 */       return paramObject instanceof TestContactInfoListFactory;
/*      */     }
/*      */     public void setORB(com.sun.corba.se.spi.orb.ORB paramORB) {
/*      */     }
/*      */     public CorbaContactInfoList create(IOR paramIOR) {
/*  613 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class TestIIOPPrimaryToContactInfo
/*      */     implements IIOPPrimaryToContactInfo
/*      */   {
/*      */     public void reset(ContactInfo paramContactInfo)
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean hasNext(ContactInfo paramContactInfo1, ContactInfo paramContactInfo2, List paramList)
/*      */     {
/*  592 */       return true;
/*      */     }
/*      */ 
/*      */     public ContactInfo next(ContactInfo paramContactInfo1, ContactInfo paramContactInfo2, List paramList)
/*      */     {
/*  599 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class TestIORToSocketInfo
/*      */     implements IORToSocketInfo
/*      */   {
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  572 */       return paramObject instanceof TestIORToSocketInfo;
/*      */     }
/*      */ 
/*      */     public List getSocketInfo(IOR paramIOR)
/*      */     {
/*  577 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class TestLegacyORBSocketFactory
/*      */     implements com.sun.corba.se.spi.legacy.connection.ORBSocketFactory
/*      */   {
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  518 */       return paramObject instanceof TestLegacyORBSocketFactory;
/*      */     }
/*      */ 
/*      */     public ServerSocket createServerSocket(String paramString, int paramInt)
/*      */     {
/*  523 */       return null;
/*      */     }
/*      */ 
/*      */     public SocketInfo getEndPointInfo(org.omg.CORBA.ORB paramORB, IOR paramIOR, SocketInfo paramSocketInfo)
/*      */     {
/*  529 */       return null;
/*      */     }
/*      */ 
/*      */     public Socket createSocket(SocketInfo paramSocketInfo)
/*      */     {
/*  534 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class TestORBInitializer1 extends LocalObject
/*      */     implements ORBInitializer
/*      */   {
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  865 */       return paramObject instanceof TestORBInitializer1;
/*      */     }
/*      */ 
/*      */     public void pre_init(ORBInitInfo paramORBInitInfo)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void post_init(ORBInitInfo paramORBInitInfo)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class TestORBInitializer2 extends LocalObject
/*      */     implements ORBInitializer
/*      */   {
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  882 */       return paramObject instanceof TestORBInitializer2;
/*      */     }
/*      */ 
/*      */     public void pre_init(ORBInitInfo paramORBInitInfo)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void post_init(ORBInitInfo paramORBInitInfo)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class TestORBSocketFactory
/*      */     implements com.sun.corba.se.spi.transport.ORBSocketFactory
/*      */   {
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*  543 */       return paramObject instanceof TestORBSocketFactory;
/*      */     }
/*      */ 
/*      */     public void setORB(com.sun.corba.se.spi.orb.ORB paramORB)
/*      */     {
/*      */     }
/*      */ 
/*      */     public ServerSocket createServerSocket(String paramString, InetSocketAddress paramInetSocketAddress)
/*      */     {
/*  552 */       return null;
/*      */     }
/*      */ 
/*      */     public Socket createSocket(String paramString, InetSocketAddress paramInetSocketAddress)
/*      */     {
/*  557 */       return null;
/*      */     }
/*      */ 
/*      */     public void setAcceptedSocketOptions(Acceptor paramAcceptor, ServerSocket paramServerSocket, Socket paramSocket)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orb.ParserTable
 * JD-Core Version:    0.6.2
 */