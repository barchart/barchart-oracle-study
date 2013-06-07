/*     */ package com.sun.corba.se.impl.interceptors;
/*     */ 
/*     */ import com.sun.corba.se.impl.corba.RequestImpl;
/*     */ import com.sun.corba.se.impl.logging.InterceptorsSystemException;
/*     */ import com.sun.corba.se.impl.logging.OMGSystemException;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
/*     */ import com.sun.corba.se.spi.ior.IOR;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
/*     */ import com.sun.corba.se.spi.oa.ObjectAdapter;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBData;
/*     */ import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
/*     */ import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
/*     */ import com.sun.corba.se.spi.protocol.ForwardException;
/*     */ import com.sun.corba.se.spi.protocol.PIHandler;
/*     */ import com.sun.corba.se.spi.protocol.RetryType;
/*     */ import com.sun.corba.se.spi.resolver.LocalResolver;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Stack;
/*     */ import org.omg.CORBA.Any;
/*     */ import org.omg.CORBA.BAD_PARAM;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.NVList;
/*     */ import org.omg.CORBA.Policy;
/*     */ import org.omg.CORBA.PolicyError;
/*     */ import org.omg.CORBA.SystemException;
/*     */ import org.omg.CORBA.UserException;
/*     */ import org.omg.CORBA.portable.ApplicationException;
/*     */ import org.omg.CORBA.portable.RemarshalException;
/*     */ import org.omg.IOP.CodecFactory;
/*     */ import org.omg.PortableInterceptor.Current;
/*     */ import org.omg.PortableInterceptor.Interceptor;
/*     */ import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
/*     */ import org.omg.PortableInterceptor.ORBInitializer;
/*     */ import org.omg.PortableInterceptor.ObjectReferenceTemplate;
/*     */ import org.omg.PortableInterceptor.PolicyFactory;
/*     */ 
/*     */ public class PIHandlerImpl
/*     */   implements PIHandler
/*     */ {
/*  92 */   boolean printPushPopEnabled = false;
/*  93 */   int pushLevel = 0;
/*     */   private ORB orb;
/*     */   InterceptorsSystemException wrapper;
/*     */   ORBUtilSystemException orbutilWrapper;
/*     */   OMGSystemException omgWrapper;
/* 122 */   private int serverRequestIdCounter = 0;
/*     */ 
/* 125 */   CodecFactory codecFactory = null;
/*     */ 
/* 129 */   String[] arguments = null;
/*     */   private InterceptorList interceptorList;
/*     */   private boolean hasIORInterceptors;
/*     */   private boolean hasClientInterceptors;
/*     */   private boolean hasServerInterceptors;
/*     */   private InterceptorInvoker interceptorInvoker;
/*     */   private PICurrent current;
/*     */   private HashMap policyFactoryTable;
/* 155 */   private static final short[] REPLY_MESSAGE_TO_PI_REPLY_STATUS = { 0, 2, 1, 3, 3, 4 };
/*     */ 
/* 166 */   private ThreadLocal threadLocalClientRequestInfoStack = new ThreadLocal()
/*     */   {
/*     */     protected Object initialValue() {
/* 169 */       return new PIHandlerImpl.RequestInfoStack(PIHandlerImpl.this, null);
/*     */     }
/* 166 */   };
/*     */ 
/* 174 */   private ThreadLocal threadLocalServerRequestInfoStack = new ThreadLocal()
/*     */   {
/*     */     protected Object initialValue() {
/* 177 */       return new PIHandlerImpl.RequestInfoStack(PIHandlerImpl.this, null);
/*     */     }
/* 174 */   };
/*     */ 
/*     */   private void printPush()
/*     */   {
/*  96 */     if (!this.printPushPopEnabled) return;
/*  97 */     printSpaces(this.pushLevel);
/*  98 */     this.pushLevel += 1;
/*  99 */     System.out.println("PUSH");
/*     */   }
/*     */ 
/*     */   private void printPop() {
/* 103 */     if (!this.printPushPopEnabled) return;
/* 104 */     this.pushLevel -= 1;
/* 105 */     printSpaces(this.pushLevel);
/* 106 */     System.out.println("POP");
/*     */   }
/*     */ 
/*     */   private void printSpaces(int paramInt) {
/* 110 */     for (int i = 0; i < paramInt; i++)
/* 111 */       System.out.print(" ");
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 182 */     this.orb = null;
/* 183 */     this.wrapper = null;
/* 184 */     this.orbutilWrapper = null;
/* 185 */     this.omgWrapper = null;
/* 186 */     this.codecFactory = null;
/* 187 */     this.arguments = null;
/* 188 */     this.interceptorList = null;
/* 189 */     this.interceptorInvoker = null;
/* 190 */     this.current = null;
/* 191 */     this.policyFactoryTable = null;
/* 192 */     this.threadLocalClientRequestInfoStack = null;
/* 193 */     this.threadLocalServerRequestInfoStack = null;
/*     */   }
/*     */ 
/*     */   public PIHandlerImpl(ORB paramORB, String[] paramArrayOfString)
/*     */   {
/* 210 */     this.orb = paramORB;
/* 211 */     this.wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
/*     */ 
/* 213 */     this.orbutilWrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
/*     */ 
/* 215 */     this.omgWrapper = OMGSystemException.get(paramORB, "rpc.protocol");
/*     */ 
/* 217 */     this.arguments = paramArrayOfString;
/*     */ 
/* 220 */     this.codecFactory = new CodecFactoryImpl(paramORB);
/*     */ 
/* 223 */     this.interceptorList = new InterceptorList(this.wrapper);
/*     */ 
/* 226 */     this.current = new PICurrent(paramORB);
/*     */ 
/* 229 */     this.interceptorInvoker = new InterceptorInvoker(paramORB, this.interceptorList, this.current);
/*     */ 
/* 233 */     paramORB.getLocalResolver().register("PICurrent", ClosureFactory.makeConstant(this.current));
/*     */ 
/* 235 */     paramORB.getLocalResolver().register("CodecFactory", ClosureFactory.makeConstant(this.codecFactory));
/*     */   }
/*     */ 
/*     */   public void initialize()
/*     */   {
/* 241 */     if (this.orb.getORBData().getORBInitializers() != null)
/*     */     {
/* 243 */       ORBInitInfoImpl localORBInitInfoImpl = createORBInitInfo();
/*     */ 
/* 247 */       this.current.setORBInitializing(true);
/*     */ 
/* 250 */       preInitORBInitializers(localORBInitInfoImpl);
/*     */ 
/* 253 */       postInitORBInitializers(localORBInitInfoImpl);
/*     */ 
/* 256 */       this.interceptorList.sortInterceptors();
/*     */ 
/* 260 */       this.current.setORBInitializing(false);
/*     */ 
/* 263 */       localORBInitInfoImpl.setStage(2);
/*     */ 
/* 267 */       this.hasIORInterceptors = this.interceptorList.hasInterceptorsOfType(2);
/*     */ 
/* 276 */       this.hasClientInterceptors = true;
/* 277 */       this.hasServerInterceptors = this.interceptorList.hasInterceptorsOfType(1);
/*     */ 
/* 283 */       this.interceptorInvoker.setEnabled(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void destroyInterceptors()
/*     */   {
/* 297 */     this.interceptorList.destroyAll();
/*     */   }
/*     */ 
/*     */   public void objectAdapterCreated(ObjectAdapter paramObjectAdapter)
/*     */   {
/* 302 */     if (!this.hasIORInterceptors) {
/* 303 */       return;
/*     */     }
/* 305 */     this.interceptorInvoker.objectAdapterCreated(paramObjectAdapter);
/*     */   }
/*     */ 
/*     */   public void adapterManagerStateChanged(int paramInt, short paramShort)
/*     */   {
/* 311 */     if (!this.hasIORInterceptors) {
/* 312 */       return;
/*     */     }
/* 314 */     this.interceptorInvoker.adapterManagerStateChanged(paramInt, paramShort);
/*     */   }
/*     */ 
/*     */   public void adapterStateChanged(ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate, short paramShort)
/*     */   {
/* 320 */     if (!this.hasIORInterceptors) {
/* 321 */       return;
/*     */     }
/* 323 */     this.interceptorInvoker.adapterStateChanged(paramArrayOfObjectReferenceTemplate, paramShort);
/*     */   }
/*     */ 
/*     */   public void disableInterceptorsThisThread()
/*     */   {
/* 332 */     if (!this.hasClientInterceptors) return;
/*     */ 
/* 334 */     RequestInfoStack localRequestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
/*     */ 
/* 336 */     localRequestInfoStack.disableCount += 1;
/*     */   }
/*     */ 
/*     */   public void enableInterceptorsThisThread() {
/* 340 */     if (!this.hasClientInterceptors) return;
/*     */ 
/* 342 */     RequestInfoStack localRequestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
/*     */ 
/* 344 */     localRequestInfoStack.disableCount -= 1;
/*     */   }
/*     */ 
/*     */   public void invokeClientPIStartingPoint()
/*     */     throws RemarshalException
/*     */   {
/* 350 */     if (!this.hasClientInterceptors) return;
/* 351 */     if (!isClientPIEnabledForThisThread()) return;
/*     */ 
/* 355 */     ClientRequestInfoImpl localClientRequestInfoImpl = peekClientRequestInfoImplStack();
/* 356 */     this.interceptorInvoker.invokeClientInterceptorStartingPoint(localClientRequestInfoImpl);
/*     */ 
/* 360 */     short s = localClientRequestInfoImpl.getReplyStatus();
/* 361 */     if ((s == 1) || (s == 3))
/*     */     {
/* 367 */       Exception localException = invokeClientPIEndingPoint(convertPIReplyStatusToReplyMessage(s), localClientRequestInfoImpl.getException());
/*     */ 
/* 370 */       if ((localException != null) || 
/* 373 */         ((localException instanceof SystemException)))
/* 374 */         throw ((SystemException)localException);
/* 375 */       if ((localException instanceof RemarshalException))
/* 376 */         throw ((RemarshalException)localException);
/* 377 */       if (((localException instanceof UserException)) || ((localException instanceof ApplicationException)))
/*     */       {
/* 383 */         throw this.wrapper.exceptionInvalid();
/*     */       }
/*     */     }
/* 386 */     else if (s != -1) {
/* 387 */       throw this.wrapper.replyStatusNotInit();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Exception makeCompletedClientRequest(int paramInt, Exception paramException)
/*     */   {
/* 397 */     return handleClientPIEndingPoint(paramInt, paramException, false);
/*     */   }
/*     */ 
/*     */   public Exception invokeClientPIEndingPoint(int paramInt, Exception paramException)
/*     */   {
/* 404 */     return handleClientPIEndingPoint(paramInt, paramException, true);
/*     */   }
/*     */ 
/*     */   public Exception handleClientPIEndingPoint(int paramInt, Exception paramException, boolean paramBoolean)
/*     */   {
/* 409 */     if (!this.hasClientInterceptors) return paramException;
/* 410 */     if (!isClientPIEnabledForThisThread()) return paramException;
/*     */ 
/* 415 */     short s = REPLY_MESSAGE_TO_PI_REPLY_STATUS[paramInt];
/*     */ 
/* 419 */     ClientRequestInfoImpl localClientRequestInfoImpl = peekClientRequestInfoImplStack();
/* 420 */     localClientRequestInfoImpl.setReplyStatus(s);
/* 421 */     localClientRequestInfoImpl.setException(paramException);
/*     */ 
/* 423 */     if (paramBoolean)
/*     */     {
/* 425 */       this.interceptorInvoker.invokeClientInterceptorEndingPoint(localClientRequestInfoImpl);
/* 426 */       s = localClientRequestInfoImpl.getReplyStatus();
/*     */     }
/*     */ 
/* 430 */     if ((s == 3) || (s == 4))
/*     */     {
/* 434 */       localClientRequestInfoImpl.reset();
/*     */ 
/* 437 */       if (paramBoolean)
/* 438 */         localClientRequestInfoImpl.setRetryRequest(RetryType.AFTER_RESPONSE);
/*     */       else {
/* 440 */         localClientRequestInfoImpl.setRetryRequest(RetryType.BEFORE_RESPONSE);
/*     */       }
/*     */ 
/* 444 */       paramException = new RemarshalException();
/* 445 */     } else if ((s == 1) || (s == 2))
/*     */     {
/* 447 */       paramException = localClientRequestInfoImpl.getException();
/*     */     }
/*     */ 
/* 450 */     return paramException;
/*     */   }
/*     */ 
/*     */   public void initiateClientPIRequest(boolean paramBoolean) {
/* 454 */     if (!this.hasClientInterceptors) return;
/* 455 */     if (!isClientPIEnabledForThisThread()) return;
/*     */ 
/* 459 */     RequestInfoStack localRequestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
/*     */ 
/* 461 */     ClientRequestInfoImpl localClientRequestInfoImpl = null;
/*     */ 
/* 463 */     if (!localRequestInfoStack.empty()) {
/* 464 */       localClientRequestInfoImpl = (ClientRequestInfoImpl)localRequestInfoStack.peek();
/*     */     }
/*     */ 
/* 467 */     if ((!paramBoolean) && (localClientRequestInfoImpl != null) && (localClientRequestInfoImpl.isDIIInitiate()))
/*     */     {
/* 470 */       localClientRequestInfoImpl.setDIIInitiate(false);
/*     */     }
/*     */     else
/*     */     {
/* 476 */       if ((localClientRequestInfoImpl == null) || (!localClientRequestInfoImpl.getRetryRequest().isRetry())) {
/* 477 */         localClientRequestInfoImpl = new ClientRequestInfoImpl(this.orb);
/* 478 */         localRequestInfoStack.push(localClientRequestInfoImpl);
/* 479 */         printPush();
/*     */       }
/*     */ 
/* 486 */       localClientRequestInfoImpl.setRetryRequest(RetryType.NONE);
/* 487 */       localClientRequestInfoImpl.incrementEntryCount();
/*     */ 
/* 493 */       localClientRequestInfoImpl.setReplyStatus((short)-1);
/*     */ 
/* 496 */       if (paramBoolean)
/* 497 */         localClientRequestInfoImpl.setDIIInitiate(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cleanupClientPIRequest()
/*     */   {
/* 503 */     if (!this.hasClientInterceptors) return;
/* 504 */     if (!isClientPIEnabledForThisThread()) return;
/*     */ 
/* 506 */     ClientRequestInfoImpl localClientRequestInfoImpl = peekClientRequestInfoImplStack();
/* 507 */     RetryType localRetryType = localClientRequestInfoImpl.getRetryRequest();
/*     */ 
/* 510 */     if (!localRetryType.equals(RetryType.BEFORE_RESPONSE))
/*     */     {
/* 520 */       int i = localClientRequestInfoImpl.getReplyStatus();
/* 521 */       if (i == -1) {
/* 522 */         invokeClientPIEndingPoint(2, this.wrapper.unknownRequestInvoke(CompletionStatus.COMPLETED_MAYBE));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 529 */     localClientRequestInfoImpl.decrementEntryCount();
/*     */ 
/* 532 */     if ((localClientRequestInfoImpl.getEntryCount() == 0) && (!localClientRequestInfoImpl.getRetryRequest().isRetry()))
/*     */     {
/* 535 */       RequestInfoStack localRequestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
/*     */ 
/* 537 */       localRequestInfoStack.pop();
/* 538 */       printPop();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setClientPIInfo(CorbaMessageMediator paramCorbaMessageMediator)
/*     */   {
/* 544 */     if (!this.hasClientInterceptors) return;
/* 545 */     if (!isClientPIEnabledForThisThread()) return;
/*     */ 
/* 547 */     peekClientRequestInfoImplStack().setInfo(paramCorbaMessageMediator);
/*     */   }
/*     */ 
/*     */   public void setClientPIInfo(RequestImpl paramRequestImpl) {
/* 551 */     if (!this.hasClientInterceptors) return;
/* 552 */     if (!isClientPIEnabledForThisThread()) return;
/*     */ 
/* 554 */     peekClientRequestInfoImplStack().setDIIRequest(paramRequestImpl);
/*     */   }
/*     */ 
/*     */   public void invokeServerPIStartingPoint()
/*     */   {
/* 564 */     if (!this.hasServerInterceptors) return;
/*     */ 
/* 566 */     ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
/* 567 */     this.interceptorInvoker.invokeServerInterceptorStartingPoint(localServerRequestInfoImpl);
/*     */ 
/* 570 */     serverPIHandleExceptions(localServerRequestInfoImpl);
/*     */   }
/*     */ 
/*     */   public void invokeServerPIIntermediatePoint()
/*     */   {
/* 575 */     if (!this.hasServerInterceptors) return;
/*     */ 
/* 577 */     ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
/* 578 */     this.interceptorInvoker.invokeServerInterceptorIntermediatePoint(localServerRequestInfoImpl);
/*     */ 
/* 582 */     localServerRequestInfoImpl.releaseServant();
/*     */ 
/* 585 */     serverPIHandleExceptions(localServerRequestInfoImpl);
/*     */   }
/*     */ 
/*     */   public void invokeServerPIEndingPoint(ReplyMessage paramReplyMessage)
/*     */   {
/* 590 */     if (!this.hasServerInterceptors) return;
/* 591 */     ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
/*     */ 
/* 594 */     localServerRequestInfoImpl.setReplyMessage(paramReplyMessage);
/*     */ 
/* 598 */     localServerRequestInfoImpl.setCurrentExecutionPoint(2);
/*     */ 
/* 603 */     if (!localServerRequestInfoImpl.getAlreadyExecuted()) {
/* 604 */       int i = paramReplyMessage.getReplyStatus();
/*     */ 
/* 610 */       short s = REPLY_MESSAGE_TO_PI_REPLY_STATUS[i];
/*     */ 
/* 614 */       if ((s == 3) || (s == 4))
/*     */       {
/* 617 */         localServerRequestInfoImpl.setForwardRequest(paramReplyMessage.getIOR());
/*     */       }
/*     */ 
/* 625 */       Exception localException1 = localServerRequestInfoImpl.getException();
/*     */ 
/* 630 */       if ((!localServerRequestInfoImpl.isDynamic()) && (s == 2))
/*     */       {
/* 633 */         localServerRequestInfoImpl.setException(this.omgWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE));
/*     */       }
/*     */ 
/* 638 */       localServerRequestInfoImpl.setReplyStatus(s);
/* 639 */       this.interceptorInvoker.invokeServerInterceptorEndingPoint(localServerRequestInfoImpl);
/* 640 */       int j = localServerRequestInfoImpl.getReplyStatus();
/* 641 */       Exception localException2 = localServerRequestInfoImpl.getException();
/*     */ 
/* 646 */       if ((j == 1) && (localException2 != localException1))
/*     */       {
/* 649 */         throw ((SystemException)localException2);
/*     */       }
/*     */ 
/* 653 */       if (j == 3) {
/* 654 */         if (s != 3)
/*     */         {
/* 656 */           IOR localIOR = localServerRequestInfoImpl.getForwardRequestIOR();
/* 657 */           throw new ForwardException(this.orb, localIOR);
/*     */         }
/* 659 */         if (localServerRequestInfoImpl.isForwardRequestRaisedInEnding())
/*     */         {
/* 661 */           paramReplyMessage.setIOR(localServerRequestInfoImpl.getForwardRequestIOR());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setServerPIInfo(Exception paramException) {
/* 668 */     if (!this.hasServerInterceptors) return;
/*     */ 
/* 670 */     ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
/* 671 */     localServerRequestInfoImpl.setException(paramException);
/*     */   }
/*     */ 
/*     */   public void setServerPIInfo(NVList paramNVList)
/*     */   {
/* 676 */     if (!this.hasServerInterceptors) return;
/*     */ 
/* 678 */     ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
/* 679 */     localServerRequestInfoImpl.setDSIArguments(paramNVList);
/*     */   }
/*     */ 
/*     */   public void setServerPIExceptionInfo(Any paramAny)
/*     */   {
/* 684 */     if (!this.hasServerInterceptors) return;
/*     */ 
/* 686 */     ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
/* 687 */     localServerRequestInfoImpl.setDSIException(paramAny);
/*     */   }
/*     */ 
/*     */   public void setServerPIInfo(Any paramAny)
/*     */   {
/* 692 */     if (!this.hasServerInterceptors) return;
/*     */ 
/* 694 */     ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
/* 695 */     localServerRequestInfoImpl.setDSIResult(paramAny);
/*     */   }
/*     */ 
/*     */   public void initializeServerPIInfo(CorbaMessageMediator paramCorbaMessageMediator, ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, ObjectKeyTemplate paramObjectKeyTemplate)
/*     */   {
/* 701 */     if (!this.hasServerInterceptors) return;
/*     */ 
/* 703 */     RequestInfoStack localRequestInfoStack = (RequestInfoStack)this.threadLocalServerRequestInfoStack.get();
/*     */ 
/* 705 */     ServerRequestInfoImpl localServerRequestInfoImpl = new ServerRequestInfoImpl(this.orb);
/* 706 */     localRequestInfoStack.push(localServerRequestInfoImpl);
/* 707 */     printPush();
/*     */ 
/* 711 */     paramCorbaMessageMediator.setExecutePIInResponseConstructor(true);
/*     */ 
/* 713 */     localServerRequestInfoImpl.setInfo(paramCorbaMessageMediator, paramObjectAdapter, paramArrayOfByte, paramObjectKeyTemplate);
/*     */   }
/*     */ 
/*     */   public void setServerPIInfo(Object paramObject, String paramString)
/*     */   {
/* 719 */     if (!this.hasServerInterceptors) return;
/*     */ 
/* 721 */     ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
/* 722 */     localServerRequestInfoImpl.setInfo(paramObject, paramString);
/*     */   }
/*     */ 
/*     */   public void cleanupServerPIRequest() {
/* 726 */     if (!this.hasServerInterceptors) return;
/*     */ 
/* 728 */     RequestInfoStack localRequestInfoStack = (RequestInfoStack)this.threadLocalServerRequestInfoStack.get();
/*     */ 
/* 730 */     localRequestInfoStack.pop();
/* 731 */     printPop();
/*     */   }
/*     */ 
/*     */   private void serverPIHandleExceptions(ServerRequestInfoImpl paramServerRequestInfoImpl)
/*     */   {
/* 748 */     int i = paramServerRequestInfoImpl.getEndingPointCall();
/* 749 */     if (i == 1)
/*     */     {
/* 751 */       throw ((SystemException)paramServerRequestInfoImpl.getException());
/*     */     }
/* 753 */     if ((i == 2) && (paramServerRequestInfoImpl.getForwardRequestException() != null))
/*     */     {
/* 758 */       IOR localIOR = paramServerRequestInfoImpl.getForwardRequestIOR();
/* 759 */       throw new ForwardException(this.orb, localIOR);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int convertPIReplyStatusToReplyMessage(short paramShort)
/*     */   {
/* 771 */     int i = 0;
/* 772 */     for (int j = 0; j < REPLY_MESSAGE_TO_PI_REPLY_STATUS.length; j++) {
/* 773 */       if (REPLY_MESSAGE_TO_PI_REPLY_STATUS[j] == paramShort) {
/* 774 */         i = j;
/* 775 */         break;
/*     */       }
/*     */     }
/* 778 */     return i;
/*     */   }
/*     */ 
/*     */   private ClientRequestInfoImpl peekClientRequestInfoImplStack()
/*     */   {
/* 787 */     RequestInfoStack localRequestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
/*     */ 
/* 789 */     ClientRequestInfoImpl localClientRequestInfoImpl = null;
/* 790 */     if (!localRequestInfoStack.empty())
/* 791 */       localClientRequestInfoImpl = (ClientRequestInfoImpl)localRequestInfoStack.peek();
/*     */     else {
/* 793 */       throw this.wrapper.clientInfoStackNull();
/*     */     }
/*     */ 
/* 796 */     return localClientRequestInfoImpl;
/*     */   }
/*     */ 
/*     */   private ServerRequestInfoImpl peekServerRequestInfoImplStack()
/*     */   {
/* 804 */     RequestInfoStack localRequestInfoStack = (RequestInfoStack)this.threadLocalServerRequestInfoStack.get();
/*     */ 
/* 806 */     ServerRequestInfoImpl localServerRequestInfoImpl = null;
/*     */ 
/* 808 */     if (!localRequestInfoStack.empty())
/* 809 */       localServerRequestInfoImpl = (ServerRequestInfoImpl)localRequestInfoStack.peek();
/*     */     else {
/* 811 */       throw this.wrapper.serverInfoStackNull();
/*     */     }
/*     */ 
/* 814 */     return localServerRequestInfoImpl;
/*     */   }
/*     */ 
/*     */   private boolean isClientPIEnabledForThisThread()
/*     */   {
/* 822 */     RequestInfoStack localRequestInfoStack = (RequestInfoStack)this.threadLocalClientRequestInfoStack.get();
/*     */ 
/* 824 */     return localRequestInfoStack.disableCount == 0;
/*     */   }
/*     */ 
/*     */   private void preInitORBInitializers(ORBInitInfoImpl paramORBInitInfoImpl)
/*     */   {
/* 833 */     paramORBInitInfoImpl.setStage(0);
/*     */ 
/* 837 */     for (int i = 0; i < this.orb.getORBData().getORBInitializers().length; 
/* 838 */       i++) {
/* 839 */       ORBInitializer localORBInitializer = this.orb.getORBData().getORBInitializers()[i];
/* 840 */       if (localORBInitializer != null)
/*     */         try {
/* 842 */           localORBInitializer.pre_init(paramORBInitInfoImpl);
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void postInitORBInitializers(ORBInitInfoImpl paramORBInitInfoImpl)
/*     */   {
/* 858 */     paramORBInitInfoImpl.setStage(1);
/*     */ 
/* 862 */     for (int i = 0; i < this.orb.getORBData().getORBInitializers().length; 
/* 863 */       i++) {
/* 864 */       ORBInitializer localORBInitializer = this.orb.getORBData().getORBInitializers()[i];
/* 865 */       if (localORBInitializer != null)
/*     */         try {
/* 867 */           localORBInitializer.post_init(paramORBInitInfoImpl);
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private ORBInitInfoImpl createORBInitInfo()
/*     */   {
/* 882 */     ORBInitInfoImpl localORBInitInfoImpl = null;
/*     */ 
/* 889 */     String str = this.orb.getORBData().getORBId();
/*     */ 
/* 891 */     localORBInitInfoImpl = new ORBInitInfoImpl(this.orb, this.arguments, str, this.codecFactory);
/*     */ 
/* 893 */     return localORBInitInfoImpl;
/*     */   }
/*     */ 
/*     */   public void register_interceptor(Interceptor paramInterceptor, int paramInt)
/*     */     throws DuplicateName
/*     */   {
/* 913 */     if ((paramInt >= 3) || (paramInt < 0)) {
/* 914 */       throw this.wrapper.typeOutOfRange(new Integer(paramInt));
/*     */     }
/*     */ 
/* 917 */     String str = paramInterceptor.name();
/*     */ 
/* 919 */     if (str == null) {
/* 920 */       throw this.wrapper.nameNull();
/*     */     }
/*     */ 
/* 924 */     this.interceptorList.register_interceptor(paramInterceptor, paramInt);
/*     */   }
/*     */ 
/*     */   public Current getPICurrent() {
/* 928 */     return this.current;
/*     */   }
/*     */ 
/*     */   private void nullParam()
/*     */     throws BAD_PARAM
/*     */   {
/* 938 */     throw this.orbutilWrapper.nullParam();
/*     */   }
/*     */ 
/*     */   public Policy create_policy(int paramInt, Any paramAny)
/*     */     throws PolicyError
/*     */   {
/* 952 */     if (paramAny == null) {
/* 953 */       nullParam();
/*     */     }
/* 955 */     if (this.policyFactoryTable == null) {
/* 956 */       throw new PolicyError("There is no PolicyFactory Registered for type " + paramInt, (short)0);
/*     */     }
/*     */ 
/* 960 */     PolicyFactory localPolicyFactory = (PolicyFactory)this.policyFactoryTable.get(new Integer(paramInt));
/*     */ 
/* 962 */     if (localPolicyFactory == null) {
/* 963 */       throw new PolicyError(" Could Not Find PolicyFactory for the Type " + paramInt, (short)0);
/*     */     }
/*     */ 
/* 967 */     Policy localPolicy = localPolicyFactory.create_policy(paramInt, paramAny);
/* 968 */     return localPolicy;
/*     */   }
/*     */ 
/*     */   public void registerPolicyFactory(int paramInt, PolicyFactory paramPolicyFactory)
/*     */   {
/* 976 */     if (this.policyFactoryTable == null) {
/* 977 */       this.policyFactoryTable = new HashMap();
/*     */     }
/* 979 */     Integer localInteger = new Integer(paramInt);
/* 980 */     Object localObject = this.policyFactoryTable.get(localInteger);
/* 981 */     if (localObject == null) {
/* 982 */       this.policyFactoryTable.put(localInteger, paramPolicyFactory);
/*     */     }
/*     */     else
/* 985 */       throw this.omgWrapper.policyFactoryRegFailed(new Integer(paramInt));
/*     */   }
/*     */ 
/*     */   public synchronized int allocateServerRequestId()
/*     */   {
/* 991 */     return this.serverRequestIdCounter++;
/*     */   }
/*     */ 
/*     */   private final class RequestInfoStack extends Stack
/*     */   {
/* 206 */     public int disableCount = 0;
/*     */ 
/*     */     private RequestInfoStack()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.interceptors.PIHandlerImpl
 * JD-Core Version:    0.6.2
 */