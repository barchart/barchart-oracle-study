/*      */ package com.sun.corba.se.impl.protocol;
/*      */ 
/*      */ import com.sun.corba.se.impl.encoding.CDRInputObject;
/*      */ import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
/*      */ import com.sun.corba.se.impl.encoding.CodeSetComponentInfo.CodeSetContext;
/*      */ import com.sun.corba.se.impl.encoding.CodeSetConversion;
/*      */ import com.sun.corba.se.impl.encoding.EncapsInputStream;
/*      */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*      */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*      */ import com.sun.corba.se.pept.broker.Broker;
/*      */ import com.sun.corba.se.pept.encoding.InputObject;
/*      */ import com.sun.corba.se.pept.encoding.OutputObject;
/*      */ import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
/*      */ import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
/*      */ import com.sun.corba.se.pept.protocol.MessageMediator;
/*      */ import com.sun.corba.se.pept.transport.Connection;
/*      */ import com.sun.corba.se.pept.transport.ContactInfo;
/*      */ import com.sun.corba.se.pept.transport.OutboundConnectionCache;
/*      */ import com.sun.corba.se.pept.transport.Selector;
/*      */ import com.sun.corba.se.pept.transport.TransportManager;
/*      */ import com.sun.corba.se.spi.ior.IOR;
/*      */ import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
/*      */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*      */ import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
/*      */ import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
/*      */ import com.sun.corba.se.spi.orb.ORB;
/*      */ import com.sun.corba.se.spi.orb.ORBData;
/*      */ import com.sun.corba.se.spi.orb.ORBVersion;
/*      */ import com.sun.corba.se.spi.orb.ORBVersionFactory;
/*      */ import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
/*      */ import com.sun.corba.se.spi.protocol.PIHandler;
/*      */ import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
/*      */ import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
/*      */ import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
/*      */ import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
/*      */ import com.sun.corba.se.spi.servicecontext.ServiceContext;
/*      */ import com.sun.corba.se.spi.servicecontext.ServiceContexts;
/*      */ import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
/*      */ import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
/*      */ import com.sun.corba.se.spi.transport.CorbaConnection;
/*      */ import com.sun.corba.se.spi.transport.CorbaContactInfo;
/*      */ import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
/*      */ import java.io.IOException;
/*      */ import java.util.Iterator;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import org.omg.CORBA.SystemException;
/*      */ import org.omg.CORBA.portable.ApplicationException;
/*      */ import org.omg.CORBA.portable.RemarshalException;
/*      */ import org.omg.CORBA.portable.UnknownException;
/*      */ 
/*      */ public class CorbaClientRequestDispatcherImpl
/*      */   implements ClientRequestDispatcher
/*      */ {
/*  128 */   private ConcurrentMap<ContactInfo, Object> locks = new ConcurrentHashMap();
/*      */ 
/*      */   public OutputObject beginRequest(Object paramObject, String paramString, boolean paramBoolean, ContactInfo paramContactInfo)
/*      */   {
/*  134 */     ORB localORB = null;
/*      */     try {
/*  136 */       CorbaContactInfo localCorbaContactInfo = (CorbaContactInfo)paramContactInfo;
/*  137 */       localORB = (ORB)paramContactInfo.getBroker();
/*      */ 
/*  139 */       if (localORB.subcontractDebugFlag) {
/*  140 */         dprint(".beginRequest->: op/" + paramString);
/*      */       }
/*      */ 
/*  147 */       localORB.getPIHandler().initiateClientPIRequest(false);
/*      */ 
/*  153 */       CorbaConnection localCorbaConnection = null;
/*      */ 
/*  161 */       Object localObject1 = this.locks.get(paramContactInfo);
/*      */ 
/*  163 */       if (localObject1 == null) {
/*  164 */         Object localObject2 = new Object();
/*  165 */         localObject1 = this.locks.putIfAbsent(paramContactInfo, localObject2);
/*  166 */         if (localObject1 == null)
/*  167 */           localObject1 = localObject2;
/*      */       }
/*      */       Object localObject5;
/*  171 */       synchronized (localObject1) {
/*  172 */         if (paramContactInfo.isConnectionBased()) {
/*  173 */           if (paramContactInfo.shouldCacheConnection()) {
/*  174 */             localCorbaConnection = (CorbaConnection)localORB.getTransportManager().getOutboundConnectionCache(paramContactInfo).get(paramContactInfo);
/*      */           }
/*      */ 
/*  178 */           if (localCorbaConnection != null) {
/*  179 */             if (localORB.subcontractDebugFlag)
/*  180 */               dprint(".beginRequest: op/" + paramString + ": Using cached connection: " + localCorbaConnection);
/*      */           }
/*      */           else
/*      */           {
/*      */             try {
/*  185 */               localCorbaConnection = (CorbaConnection)paramContactInfo.createConnection();
/*      */ 
/*  187 */               if (localORB.subcontractDebugFlag)
/*  188 */                 dprint(".beginRequest: op/" + paramString + ": Using created connection: " + localCorbaConnection);
/*      */             }
/*      */             catch (RuntimeException localRuntimeException)
/*      */             {
/*  192 */               if (localORB.subcontractDebugFlag) {
/*  193 */                 dprint(".beginRequest: op/" + paramString + ": failed to create connection: " + localRuntimeException);
/*      */               }
/*      */ 
/*  197 */               boolean bool = getContactInfoListIterator(localORB).reportException(paramContactInfo, localRuntimeException);
/*      */ 
/*  201 */               if (bool) {
/*  202 */                 if (getContactInfoListIterator(localORB).hasNext()) {
/*  203 */                   paramContactInfo = (ContactInfo)getContactInfoListIterator(localORB).next();
/*      */ 
/*  205 */                   unregisterWaiter(localORB);
/*  206 */                   return beginRequest(paramObject, paramString, paramBoolean, paramContactInfo);
/*      */                 }
/*      */ 
/*  209 */                 throw localRuntimeException;
/*      */               }
/*      */ 
/*  212 */               throw localRuntimeException;
/*      */             }
/*      */ 
/*  215 */             if (localCorbaConnection.shouldRegisterReadEvent())
/*      */             {
/*  217 */               localORB.getTransportManager().getSelector(0).registerForEvent(localCorbaConnection.getEventHandler());
/*      */ 
/*  219 */               localCorbaConnection.setState("ESTABLISHED");
/*      */             }
/*      */ 
/*  224 */             if (paramContactInfo.shouldCacheConnection()) {
/*  225 */               localObject3 = localORB.getTransportManager().getOutboundConnectionCache(paramContactInfo);
/*      */ 
/*  228 */               ((OutboundConnectionCache)localObject3).stampTime(localCorbaConnection);
/*  229 */               ((OutboundConnectionCache)localObject3).put(paramContactInfo, localCorbaConnection);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  236 */       ??? = (CorbaMessageMediator)paramContactInfo.createMessageMediator(localORB, paramContactInfo, localCorbaConnection, paramString, paramBoolean);
/*      */ 
/*  239 */       if (localORB.subcontractDebugFlag) {
/*  240 */         dprint(".beginRequest: " + opAndId((CorbaMessageMediator)???) + ": created message mediator: " + ???);
/*      */       }
/*      */ 
/*  254 */       localORB.getInvocationInfo().setMessageMediator((MessageMediator)???);
/*      */ 
/*  256 */       if ((localCorbaConnection != null) && (localCorbaConnection.getCodeSetContext() == null)) {
/*  257 */         performCodeSetNegotiation((CorbaMessageMediator)???);
/*      */       }
/*      */ 
/*  260 */       addServiceContexts((CorbaMessageMediator)???);
/*      */ 
/*  262 */       Object localObject3 = paramContactInfo.createOutputObject((MessageMediator)???);
/*      */ 
/*  264 */       if (localORB.subcontractDebugFlag) {
/*  265 */         dprint(".beginRequest: " + opAndId((CorbaMessageMediator)???) + ": created output object: " + localObject3);
/*      */       }
/*      */ 
/*  273 */       registerWaiter((CorbaMessageMediator)???);
/*      */ 
/*  276 */       synchronized (localObject1) {
/*  277 */         if ((paramContactInfo.isConnectionBased()) && 
/*  278 */           (paramContactInfo.shouldCacheConnection())) {
/*  279 */           localObject5 = localORB.getTransportManager().getOutboundConnectionCache(paramContactInfo);
/*      */ 
/*  282 */           ((OutboundConnectionCache)localObject5).reclaim();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  287 */       localORB.getPIHandler().setClientPIInfo((CorbaMessageMediator)???);
/*      */       try
/*      */       {
/*  292 */         localORB.getPIHandler().invokeClientPIStartingPoint();
/*      */       } catch (RemarshalException localRemarshalException) {
/*  294 */         if (localORB.subcontractDebugFlag) {
/*  295 */           dprint(".beginRequest: " + opAndId((CorbaMessageMediator)???) + ": Remarshal");
/*      */         }
/*      */ 
/*  312 */         if (getContactInfoListIterator(localORB).hasNext()) {
/*  313 */           paramContactInfo = (ContactInfo)getContactInfoListIterator(localORB).next();
/*  314 */           if (localORB.subcontractDebugFlag) {
/*  315 */             dprint("RemarshalException: hasNext true\ncontact info " + paramContactInfo);
/*      */           }
/*      */ 
/*  319 */           localORB.getPIHandler().makeCompletedClientRequest(3, null);
/*      */ 
/*  321 */           unregisterWaiter(localORB);
/*  322 */           localORB.getPIHandler().cleanupClientPIRequest();
/*      */ 
/*  324 */           return beginRequest(paramObject, paramString, paramBoolean, paramContactInfo);
/*      */         }
/*  326 */         if (localORB.subcontractDebugFlag) {
/*  327 */           dprint("RemarshalException: hasNext false");
/*      */         }
/*  329 */         localObject5 = ORBUtilSystemException.get(localORB, "rpc.protocol");
/*      */ 
/*  332 */         throw ((ORBUtilSystemException)localObject5).remarshalWithNowhereToGo();
/*      */       }
/*      */ 
/*  336 */       ((CorbaMessageMediator)???).initializeMessage();
/*  337 */       if (localORB.subcontractDebugFlag) {
/*  338 */         dprint(".beginRequest: " + opAndId((CorbaMessageMediator)???) + ": initialized message");
/*      */       }
/*      */ 
/*  342 */       return localObject3;
/*      */     }
/*      */     finally {
/*  345 */       if (localORB.subcontractDebugFlag)
/*  346 */         dprint(".beginRequest<-: op/" + paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */   public InputObject marshalingComplete(Object paramObject, OutputObject paramOutputObject)
/*      */     throws ApplicationException, RemarshalException
/*      */   {
/*  357 */     ORB localORB = null;
/*  358 */     CorbaMessageMediator localCorbaMessageMediator = null;
/*      */     try {
/*  360 */       localCorbaMessageMediator = (CorbaMessageMediator)paramOutputObject.getMessageMediator();
/*      */ 
/*  363 */       localORB = (ORB)localCorbaMessageMediator.getBroker();
/*      */ 
/*  365 */       if (localORB.subcontractDebugFlag) {
/*  366 */         dprint(".marshalingComplete->: " + opAndId(localCorbaMessageMediator));
/*      */       }
/*      */ 
/*  369 */       InputObject localInputObject1 = marshalingComplete1(localORB, localCorbaMessageMediator);
/*      */ 
/*  372 */       return processResponse(localORB, localCorbaMessageMediator, localInputObject1);
/*      */     }
/*      */     finally {
/*  375 */       if (localORB.subcontractDebugFlag)
/*  376 */         dprint(".marshalingComplete<-: " + opAndId(localCorbaMessageMediator));
/*      */     }
/*      */   }
/*      */ 
/*      */   public InputObject marshalingComplete1(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator)
/*      */     throws ApplicationException, RemarshalException
/*      */   {
/*      */     try
/*      */     {
/*  388 */       paramCorbaMessageMediator.finishSendingRequest();
/*      */ 
/*  390 */       if (paramORB.subcontractDebugFlag) {
/*  391 */         dprint(".marshalingComplete: " + opAndId(paramCorbaMessageMediator) + ": finished sending request");
/*      */       }
/*      */ 
/*  395 */       return paramCorbaMessageMediator.waitForResponse();
/*      */     }
/*      */     catch (RuntimeException localRuntimeException)
/*      */     {
/*  399 */       if (paramORB.subcontractDebugFlag) {
/*  400 */         dprint(".marshalingComplete: " + opAndId(paramCorbaMessageMediator) + ": exception: " + localRuntimeException.toString());
/*      */       }
/*      */ 
/*  404 */       boolean bool = getContactInfoListIterator(paramORB).reportException(paramCorbaMessageMediator.getContactInfo(), localRuntimeException);
/*      */ 
/*  411 */       Exception localException = paramORB.getPIHandler().invokeClientPIEndingPoint(2, localRuntimeException);
/*      */ 
/*  415 */       if (bool) {
/*  416 */         if (localException == localRuntimeException) {
/*  417 */           continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
/*      */         }
/*      */         else
/*  420 */           continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, localException);
/*      */       }
/*      */       else
/*      */       {
/*  424 */         if ((localException instanceof RuntimeException)) {
/*  425 */           throw ((RuntimeException)localException);
/*      */         }
/*  427 */         if ((localException instanceof RemarshalException))
/*      */         {
/*  429 */           throw ((RemarshalException)localException);
/*      */         }
/*      */ 
/*  433 */         throw localRuntimeException;
/*      */       }
/*      */     }
/*  435 */     return null;
/*      */   }
/*      */ 
/*      */   protected InputObject processResponse(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator, InputObject paramInputObject)
/*      */     throws ApplicationException, RemarshalException
/*      */   {
/*  446 */     ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.protocol");
/*      */ 
/*  450 */     if (paramORB.subcontractDebugFlag) {
/*  451 */       dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": response received");
/*      */     }
/*      */ 
/*  457 */     if (paramCorbaMessageMediator.getConnection() != null) {
/*  458 */       ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setPostInitialContexts();
/*      */     }
/*      */ 
/*  467 */     Object localObject1 = null;
/*      */ 
/*  469 */     if (paramCorbaMessageMediator.isOneWay()) {
/*  470 */       getContactInfoListIterator(paramORB).reportSuccess(paramCorbaMessageMediator.getContactInfo());
/*      */ 
/*  473 */       localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(0, (Exception)localObject1);
/*      */ 
/*  475 */       continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
/*  476 */       return null;
/*      */     }
/*      */ 
/*  479 */     consumeServiceContexts(paramORB, paramCorbaMessageMediator);
/*      */ 
/*  484 */     ((CDRInputObject)paramInputObject).performORBVersionSpecificInit();
/*      */     Object localObject2;
/*      */     Object localObject3;
/*  486 */     if (paramCorbaMessageMediator.isSystemExceptionReply())
/*      */     {
/*  488 */       localObject2 = paramCorbaMessageMediator.getSystemExceptionReply();
/*      */ 
/*  490 */       if (paramORB.subcontractDebugFlag) {
/*  491 */         dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received system exception: " + localObject2);
/*      */       }
/*      */ 
/*  495 */       boolean bool = getContactInfoListIterator(paramORB).reportException(paramCorbaMessageMediator.getContactInfo(), (RuntimeException)localObject2);
/*      */ 
/*  499 */       if (bool)
/*      */       {
/*  502 */         localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(2, (Exception)localObject2);
/*      */ 
/*  507 */         if (localObject2 == localObject1)
/*      */         {
/*  510 */           localObject1 = null;
/*  511 */           continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
/*      */ 
/*  513 */           throw localORBUtilSystemException.statementNotReachable1();
/*      */         }
/*      */ 
/*  516 */         continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
/*      */ 
/*  518 */         throw localORBUtilSystemException.statementNotReachable2();
/*      */       }
/*      */ 
/*  524 */       localObject3 = paramCorbaMessageMediator.getReplyServiceContexts();
/*      */ 
/*  526 */       if (localObject3 != null) {
/*  527 */         UEInfoServiceContext localUEInfoServiceContext = (UEInfoServiceContext)((ServiceContexts)localObject3).get(9);
/*      */ 
/*  531 */         if (localUEInfoServiceContext != null) {
/*  532 */           Throwable localThrowable = localUEInfoServiceContext.getUE();
/*  533 */           UnknownException localUnknownException = new UnknownException(localThrowable);
/*      */ 
/*  536 */           localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(2, localUnknownException);
/*      */ 
/*  539 */           continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
/*  540 */           throw localORBUtilSystemException.statementNotReachable3();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  548 */       localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(2, (Exception)localObject2);
/*      */ 
/*  551 */       continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
/*      */ 
/*  555 */       throw localORBUtilSystemException.statementNotReachable4();
/*  556 */     }if (paramCorbaMessageMediator.isUserExceptionReply())
/*      */     {
/*  558 */       if (paramORB.subcontractDebugFlag) {
/*  559 */         dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received user exception");
/*      */       }
/*      */ 
/*  563 */       getContactInfoListIterator(paramORB).reportSuccess(paramCorbaMessageMediator.getContactInfo());
/*      */ 
/*  566 */       localObject2 = peekUserExceptionId(paramInputObject);
/*  567 */       Exception localException = null;
/*      */ 
/*  569 */       if (paramCorbaMessageMediator.isDIIRequest()) {
/*  570 */         localObject1 = paramCorbaMessageMediator.unmarshalDIIUserException((String)localObject2, (org.omg.CORBA_2_3.portable.InputStream)paramInputObject);
/*      */ 
/*  572 */         localException = paramORB.getPIHandler().invokeClientPIEndingPoint(1, (Exception)localObject1);
/*      */ 
/*  574 */         paramCorbaMessageMediator.setDIIException(localException);
/*      */       }
/*      */       else {
/*  577 */         localObject3 = new ApplicationException((String)localObject2, (org.omg.CORBA.portable.InputStream)paramInputObject);
/*      */ 
/*  581 */         localObject1 = localObject3;
/*  582 */         localException = paramORB.getPIHandler().invokeClientPIEndingPoint(1, (Exception)localObject3);
/*      */       }
/*      */ 
/*  586 */       if (localException != localObject1) {
/*  587 */         continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, localException);
/*      */       }
/*      */ 
/*  590 */       if ((localException instanceof ApplicationException)) {
/*  591 */         throw ((ApplicationException)localException);
/*      */       }
/*      */ 
/*  595 */       return paramInputObject;
/*      */     }
/*  597 */     if (paramCorbaMessageMediator.isLocationForwardReply())
/*      */     {
/*  599 */       if (paramORB.subcontractDebugFlag) {
/*  600 */         dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received location forward");
/*      */       }
/*      */ 
/*  605 */       getContactInfoListIterator(paramORB).reportRedirect((CorbaContactInfo)paramCorbaMessageMediator.getContactInfo(), paramCorbaMessageMediator.getForwardedIOR());
/*      */ 
/*  610 */       localObject2 = paramORB.getPIHandler().invokeClientPIEndingPoint(3, null);
/*      */ 
/*  613 */       if (!(localObject2 instanceof RemarshalException)) {
/*  614 */         localObject1 = localObject2;
/*      */       }
/*      */ 
/*  620 */       if (localObject1 != null) {
/*  621 */         continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
/*      */       }
/*  623 */       continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
/*      */ 
/*  625 */       throw localORBUtilSystemException.statementNotReachable5();
/*      */     }
/*  627 */     if (paramCorbaMessageMediator.isDifferentAddrDispositionRequestedReply())
/*      */     {
/*  629 */       if (paramORB.subcontractDebugFlag) {
/*  630 */         dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received different addressing dispostion request");
/*      */       }
/*      */ 
/*  635 */       getContactInfoListIterator(paramORB).reportAddrDispositionRetry((CorbaContactInfo)paramCorbaMessageMediator.getContactInfo(), paramCorbaMessageMediator.getAddrDispositionReply());
/*      */ 
/*  640 */       localObject2 = paramORB.getPIHandler().invokeClientPIEndingPoint(5, null);
/*      */ 
/*  644 */       if (!(localObject2 instanceof RemarshalException)) {
/*  645 */         localObject1 = localObject2;
/*      */       }
/*      */ 
/*  651 */       if (localObject1 != null) {
/*  652 */         continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
/*      */       }
/*  654 */       continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, new RemarshalException());
/*      */ 
/*  656 */       throw localORBUtilSystemException.statementNotReachable6();
/*      */     }
/*      */ 
/*  659 */     if (paramORB.subcontractDebugFlag) {
/*  660 */       dprint(".processResponse: " + opAndId(paramCorbaMessageMediator) + ": received normal response");
/*      */     }
/*      */ 
/*  664 */     getContactInfoListIterator(paramORB).reportSuccess(paramCorbaMessageMediator.getContactInfo());
/*      */ 
/*  667 */     paramCorbaMessageMediator.handleDIIReply((org.omg.CORBA_2_3.portable.InputStream)paramInputObject);
/*      */ 
/*  670 */     localObject1 = paramORB.getPIHandler().invokeClientPIEndingPoint(0, null);
/*      */ 
/*  674 */     continueOrThrowSystemOrRemarshal(paramCorbaMessageMediator, (Exception)localObject1);
/*      */ 
/*  676 */     return paramInputObject;
/*      */   }
/*      */ 
/*      */   protected void continueOrThrowSystemOrRemarshal(CorbaMessageMediator paramCorbaMessageMediator, Exception paramException)
/*      */     throws SystemException, RemarshalException
/*      */   {
/*  693 */     ORB localORB = (ORB)paramCorbaMessageMediator.getBroker();
/*      */ 
/*  695 */     if (paramException != null)
/*      */     {
/*  699 */       if ((paramException instanceof RemarshalException))
/*      */       {
/*  702 */         localORB.getInvocationInfo().setIsRetryInvocation(true);
/*      */ 
/*  708 */         unregisterWaiter(localORB);
/*      */ 
/*  710 */         if (localORB.subcontractDebugFlag) {
/*  711 */           dprint(".continueOrThrowSystemOrRemarshal: " + opAndId(paramCorbaMessageMediator) + ": throwing Remarshal");
/*      */         }
/*      */ 
/*  716 */         throw ((RemarshalException)paramException);
/*      */       }
/*      */ 
/*  720 */       if (localORB.subcontractDebugFlag) {
/*  721 */         dprint(".continueOrThrowSystemOrRemarshal: " + opAndId(paramCorbaMessageMediator) + ": throwing sex:" + paramException);
/*      */       }
/*      */ 
/*  727 */       throw ((SystemException)paramException);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected CorbaContactInfoListIterator getContactInfoListIterator(ORB paramORB)
/*      */   {
/*  733 */     return (CorbaContactInfoListIterator)((CorbaInvocationInfo)paramORB.getInvocationInfo()).getContactInfoListIterator();
/*      */   }
/*      */ 
/*      */   protected void registerWaiter(CorbaMessageMediator paramCorbaMessageMediator)
/*      */   {
/*  740 */     if (paramCorbaMessageMediator.getConnection() != null)
/*  741 */       paramCorbaMessageMediator.getConnection().registerWaiter(paramCorbaMessageMediator);
/*      */   }
/*      */ 
/*      */   protected void unregisterWaiter(ORB paramORB)
/*      */   {
/*  747 */     MessageMediator localMessageMediator = paramORB.getInvocationInfo().getMessageMediator();
/*      */ 
/*  749 */     if ((localMessageMediator != null) && (localMessageMediator.getConnection() != null))
/*      */     {
/*  753 */       localMessageMediator.getConnection().unregisterWaiter(localMessageMediator);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void addServiceContexts(CorbaMessageMediator paramCorbaMessageMediator)
/*      */   {
/*  759 */     ORB localORB = (ORB)paramCorbaMessageMediator.getBroker();
/*  760 */     CorbaConnection localCorbaConnection = (CorbaConnection)paramCorbaMessageMediator.getConnection();
/*  761 */     GIOPVersion localGIOPVersion = paramCorbaMessageMediator.getGIOPVersion();
/*      */ 
/*  763 */     ServiceContexts localServiceContexts = paramCorbaMessageMediator.getRequestServiceContexts();
/*      */ 
/*  765 */     addCodeSetServiceContext(localCorbaConnection, localServiceContexts, localGIOPVersion);
/*      */ 
/*  771 */     localServiceContexts.put(MaxStreamFormatVersionServiceContext.singleton);
/*      */ 
/*  774 */     ORBVersionServiceContext localORBVersionServiceContext = new ORBVersionServiceContext(ORBVersionFactory.getORBVersion());
/*      */ 
/*  776 */     localServiceContexts.put(localORBVersionServiceContext);
/*      */ 
/*  779 */     if ((localCorbaConnection != null) && (!localCorbaConnection.isPostInitialContexts()))
/*      */     {
/*  784 */       SendingContextServiceContext localSendingContextServiceContext = new SendingContextServiceContext(localORB.getFVDCodeBaseIOR());
/*      */ 
/*  786 */       localServiceContexts.put(localSendingContextServiceContext);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void consumeServiceContexts(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator)
/*      */   {
/*  793 */     ServiceContexts localServiceContexts = paramCorbaMessageMediator.getReplyServiceContexts();
/*      */ 
/*  795 */     ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.protocol");
/*      */ 
/*  798 */     if (localServiceContexts == null) {
/*  799 */       return;
/*      */     }
/*      */ 
/*  802 */     ServiceContext localServiceContext = localServiceContexts.get(6);
/*      */     Object localObject1;
/*      */     Object localObject2;
/*  804 */     if (localServiceContext != null) {
/*  805 */       localObject1 = (SendingContextServiceContext)localServiceContext;
/*      */ 
/*  807 */       localObject2 = ((SendingContextServiceContext)localObject1).getIOR();
/*      */       try
/*      */       {
/*  811 */         if (paramCorbaMessageMediator.getConnection() != null)
/*  812 */           ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setCodeBaseIOR((IOR)localObject2);
/*      */       }
/*      */       catch (ThreadDeath localThreadDeath) {
/*  815 */         throw localThreadDeath;
/*      */       } catch (Throwable localThrowable) {
/*  817 */         throw localORBUtilSystemException.badStringifiedIor(localThrowable);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  823 */     localServiceContext = localServiceContexts.get(1313165056);
/*      */ 
/*  825 */     if (localServiceContext != null) {
/*  826 */       localObject1 = (ORBVersionServiceContext)localServiceContext;
/*      */ 
/*  829 */       localObject2 = ((ORBVersionServiceContext)localObject1).getVersion();
/*  830 */       paramORB.setORBVersion((ORBVersion)localObject2);
/*      */     }
/*      */ 
/*  833 */     getExceptionDetailMessage(paramCorbaMessageMediator, localORBUtilSystemException);
/*      */   }
/*      */ 
/*      */   protected void getExceptionDetailMessage(CorbaMessageMediator paramCorbaMessageMediator, ORBUtilSystemException paramORBUtilSystemException)
/*      */   {
/*  840 */     ServiceContext localServiceContext = paramCorbaMessageMediator.getReplyServiceContexts().get(14);
/*      */ 
/*  842 */     if (localServiceContext == null) {
/*  843 */       return;
/*      */     }
/*  845 */     if (!(localServiceContext instanceof UnknownServiceContext)) {
/*  846 */       throw paramORBUtilSystemException.badExceptionDetailMessageServiceContextType();
/*      */     }
/*  848 */     byte[] arrayOfByte = ((UnknownServiceContext)localServiceContext).getData();
/*  849 */     EncapsInputStream localEncapsInputStream = new EncapsInputStream((ORB)paramCorbaMessageMediator.getBroker(), arrayOfByte, arrayOfByte.length);
/*      */ 
/*  852 */     localEncapsInputStream.consumeEndian();
/*      */ 
/*  854 */     String str = "----------BEGIN server-side stack trace----------\n" + localEncapsInputStream.read_wstring() + "\n" + "----------END server-side stack trace----------";
/*      */ 
/*  859 */     paramCorbaMessageMediator.setReplyExceptionDetailMessage(str);
/*      */   }
/*      */ 
/*      */   public void endRequest(Broker paramBroker, Object paramObject, InputObject paramInputObject)
/*      */   {
/*  864 */     ORB localORB = (ORB)paramBroker;
/*      */     try
/*      */     {
/*  867 */       if (localORB.subcontractDebugFlag) {
/*  868 */         dprint(".endRequest->");
/*      */       }
/*      */ 
/*  875 */       MessageMediator localMessageMediator = localORB.getInvocationInfo().getMessageMediator();
/*      */ 
/*  877 */       if (localMessageMediator != null)
/*      */       {
/*  879 */         if (localMessageMediator.getConnection() != null)
/*      */         {
/*  881 */           ((CorbaMessageMediator)localMessageMediator).sendCancelRequestIfFinalFragmentNotSent();
/*      */         }
/*      */ 
/*  887 */         InputObject localInputObject = localMessageMediator.getInputObject();
/*  888 */         if (localInputObject != null) {
/*  889 */           localInputObject.close();
/*      */         }
/*      */ 
/*  892 */         OutputObject localOutputObject = localMessageMediator.getOutputObject();
/*  893 */         if (localOutputObject != null) {
/*  894 */           localOutputObject.close();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  908 */       unregisterWaiter(localORB);
/*      */ 
/*  914 */       localORB.getPIHandler().cleanupClientPIRequest();
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  920 */       if (localORB.subcontractDebugFlag)
/*      */       {
/*  922 */         dprint(".endRequest: ignoring IOException - " + localIOException.toString());
/*      */       }
/*      */     } finally {
/*  925 */       if (localORB.subcontractDebugFlag)
/*  926 */         dprint(".endRequest<-");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void performCodeSetNegotiation(CorbaMessageMediator paramCorbaMessageMediator)
/*      */   {
/*  934 */     CorbaConnection localCorbaConnection = (CorbaConnection)paramCorbaMessageMediator.getConnection();
/*      */ 
/*  936 */     IOR localIOR = ((CorbaContactInfo)paramCorbaMessageMediator.getContactInfo()).getEffectiveTargetIOR();
/*      */ 
/*  939 */     GIOPVersion localGIOPVersion = paramCorbaMessageMediator.getGIOPVersion();
/*      */ 
/*  945 */     if ((localCorbaConnection != null) && (localCorbaConnection.getCodeSetContext() == null) && (!localGIOPVersion.equals(GIOPVersion.V1_0)))
/*      */     {
/*  949 */       synchronized (localCorbaConnection)
/*      */       {
/*  953 */         if (localCorbaConnection.getCodeSetContext() != null) {
/*  954 */           return;
/*      */         }
/*      */ 
/*  959 */         IIOPProfileTemplate localIIOPProfileTemplate = (IIOPProfileTemplate)localIOR.getProfile().getTaggedProfileTemplate();
/*      */ 
/*  962 */         Iterator localIterator = localIIOPProfileTemplate.iteratorById(1);
/*  963 */         if (!localIterator.hasNext())
/*      */         {
/*  967 */           return;
/*      */         }
/*      */ 
/*  972 */         CodeSetComponentInfo localCodeSetComponentInfo = ((CodeSetsComponent)localIterator.next()).getCodeSetComponentInfo();
/*      */ 
/*  977 */         CodeSetComponentInfo.CodeSetContext localCodeSetContext = CodeSetConversion.impl().negotiate(localCorbaConnection.getBroker().getORBData().getCodeSetComponentInfo(), localCodeSetComponentInfo);
/*      */ 
/*  982 */         localCorbaConnection.setCodeSetContext(localCodeSetContext);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void addCodeSetServiceContext(CorbaConnection paramCorbaConnection, ServiceContexts paramServiceContexts, GIOPVersion paramGIOPVersion)
/*      */   {
/* 1008 */     if ((paramGIOPVersion.equals(GIOPVersion.V1_0)) || (paramCorbaConnection == null)) {
/* 1009 */       return;
/*      */     }
/* 1011 */     CodeSetComponentInfo.CodeSetContext localCodeSetContext = null;
/*      */ 
/* 1013 */     if ((paramCorbaConnection.getBroker().getORBData().alwaysSendCodeSetServiceContext()) || (!paramCorbaConnection.isPostInitialContexts()))
/*      */     {
/* 1017 */       localCodeSetContext = paramCorbaConnection.getCodeSetContext();
/*      */     }
/*      */ 
/* 1024 */     if (localCodeSetContext == null) {
/* 1025 */       return;
/*      */     }
/* 1027 */     CodeSetServiceContext localCodeSetServiceContext = new CodeSetServiceContext(localCodeSetContext);
/* 1028 */     paramServiceContexts.put(localCodeSetServiceContext);
/*      */   }
/*      */ 
/*      */   protected String peekUserExceptionId(InputObject paramInputObject)
/*      */   {
/* 1033 */     CDRInputObject localCDRInputObject = (CDRInputObject)paramInputObject;
/*      */ 
/* 1035 */     localCDRInputObject.mark(2147483647);
/* 1036 */     String str = localCDRInputObject.read_string();
/* 1037 */     localCDRInputObject.reset();
/* 1038 */     return str;
/*      */   }
/*      */ 
/*      */   protected void dprint(String paramString)
/*      */   {
/* 1043 */     ORBUtility.dprint("CorbaClientRequestDispatcherImpl", paramString);
/*      */   }
/*      */ 
/*      */   protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator)
/*      */   {
/* 1048 */     return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.protocol.CorbaClientRequestDispatcherImpl
 * JD-Core Version:    0.6.2
 */