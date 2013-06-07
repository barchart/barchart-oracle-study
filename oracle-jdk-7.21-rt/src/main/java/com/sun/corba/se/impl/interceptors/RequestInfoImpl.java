/*     */ package com.sun.corba.se.impl.interceptors;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.EncapsOutputStream;
/*     */ import com.sun.corba.se.impl.logging.InterceptorsSystemException;
/*     */ import com.sun.corba.se.impl.logging.OMGSystemException;
/*     */ import com.sun.corba.se.impl.orbutil.ORBClassLoader;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.impl.util.RepositoryId;
/*     */ import com.sun.corba.se.impl.util.RepositoryIdCache;
/*     */ import com.sun.corba.se.spi.ior.IOR;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.legacy.connection.Connection;
/*     */ import com.sun.corba.se.spi.legacy.interceptor.RequestInfoExt;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.protocol.PIHandler;
/*     */ import com.sun.corba.se.spi.servicecontext.ServiceContexts;
/*     */ import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import org.omg.CORBA.Any;
/*     */ import org.omg.CORBA.BAD_INV_ORDER;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.LocalObject;
/*     */ import org.omg.CORBA.NVList;
/*     */ import org.omg.CORBA.NamedValue;
/*     */ import org.omg.CORBA.ParameterMode;
/*     */ import org.omg.CORBA.SystemException;
/*     */ import org.omg.CORBA.TypeCode;
/*     */ import org.omg.CORBA.UNKNOWN;
/*     */ import org.omg.CORBA.UserException;
/*     */ import org.omg.CORBA.portable.ApplicationException;
/*     */ import org.omg.Dynamic.Parameter;
/*     */ import org.omg.IOP.ServiceContextHelper;
/*     */ import org.omg.PortableInterceptor.ForwardRequest;
/*     */ import org.omg.PortableInterceptor.InvalidSlot;
/*     */ import org.omg.PortableInterceptor.RequestInfo;
/*     */ 
/*     */ public abstract class RequestInfoImpl extends LocalObject
/*     */   implements RequestInfo, RequestInfoExt
/*     */ {
/*     */   protected ORB myORB;
/*     */   protected InterceptorsSystemException wrapper;
/*     */   protected OMGSystemException stdWrapper;
/* 117 */   protected int flowStackIndex = 0;
/*     */   protected int startingPointCall;
/*     */   protected int intermediatePointCall;
/*     */   protected int endingPointCall;
/* 137 */   protected short replyStatus = -1;
/*     */   protected static final short UNINITIALIZED = -1;
/*     */   protected int currentExecutionPoint;
/*     */   protected static final int EXECUTION_POINT_STARTING = 0;
/*     */   protected static final int EXECUTION_POINT_INTERMEDIATE = 1;
/*     */   protected static final int EXECUTION_POINT_ENDING = 2;
/*     */   protected boolean alreadyExecuted;
/*     */   protected Connection connection;
/*     */   protected ServiceContexts serviceContexts;
/*     */   protected ForwardRequest forwardRequest;
/*     */   protected IOR forwardRequestIOR;
/*     */   protected SlotTable slotTable;
/*     */   protected Exception exception;
/*     */   protected static final int MID_REQUEST_ID = 0;
/*     */   protected static final int MID_OPERATION = 1;
/*     */   protected static final int MID_ARGUMENTS = 2;
/*     */   protected static final int MID_EXCEPTIONS = 3;
/*     */   protected static final int MID_CONTEXTS = 4;
/*     */   protected static final int MID_OPERATION_CONTEXT = 5;
/*     */   protected static final int MID_RESULT = 6;
/*     */   protected static final int MID_RESPONSE_EXPECTED = 7;
/*     */   protected static final int MID_SYNC_SCOPE = 8;
/*     */   protected static final int MID_REPLY_STATUS = 9;
/*     */   protected static final int MID_FORWARD_REFERENCE = 10;
/*     */   protected static final int MID_GET_SLOT = 11;
/*     */   protected static final int MID_GET_REQUEST_SERVICE_CONTEXT = 12;
/*     */   protected static final int MID_GET_REPLY_SERVICE_CONTEXT = 13;
/*     */   protected static final int MID_RI_LAST = 13;
/*     */ 
/*     */   void reset()
/*     */   {
/* 186 */     this.flowStackIndex = 0;
/* 187 */     this.startingPointCall = 0;
/* 188 */     this.intermediatePointCall = 0;
/* 189 */     this.endingPointCall = 0;
/*     */ 
/* 191 */     setReplyStatus((short)-1);
/* 192 */     this.currentExecutionPoint = 0;
/* 193 */     this.alreadyExecuted = false;
/* 194 */     this.connection = null;
/* 195 */     this.serviceContexts = null;
/* 196 */     this.forwardRequest = null;
/* 197 */     this.forwardRequestIOR = null;
/* 198 */     this.exception = null;
/*     */   }
/*     */ 
/*     */   public RequestInfoImpl(ORB paramORB)
/*     */   {
/* 240 */     this.myORB = paramORB;
/* 241 */     this.wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
/*     */ 
/* 243 */     this.stdWrapper = OMGSystemException.get(paramORB, "rpc.protocol");
/*     */ 
/* 247 */     PICurrent localPICurrent = (PICurrent)paramORB.getPIHandler().getPICurrent();
/* 248 */     this.slotTable = localPICurrent.getSlotTable();
/*     */   }
/*     */ 
/*     */   public abstract int request_id();
/*     */ 
/*     */   public abstract String operation();
/*     */ 
/*     */   public abstract Parameter[] arguments();
/*     */ 
/*     */   public abstract TypeCode[] exceptions();
/*     */ 
/*     */   public abstract String[] contexts();
/*     */ 
/*     */   public abstract String[] operation_context();
/*     */ 
/*     */   public abstract Any result();
/*     */ 
/*     */   public abstract boolean response_expected();
/*     */ 
/*     */   public short sync_scope()
/*     */   {
/* 331 */     checkAccess(8);
/* 332 */     return 1;
/*     */   }
/*     */ 
/*     */   public short reply_status()
/*     */   {
/* 347 */     checkAccess(9);
/* 348 */     return this.replyStatus;
/*     */   }
/*     */ 
/*     */   public abstract org.omg.CORBA.Object forward_reference();
/*     */ 
/*     */   public Any get_slot(int paramInt)
/*     */     throws InvalidSlot
/*     */   {
/* 379 */     return this.slotTable.get_slot(paramInt);
/*     */   }
/*     */ 
/*     */   public abstract org.omg.IOP.ServiceContext get_request_service_context(int paramInt);
/*     */ 
/*     */   public abstract org.omg.IOP.ServiceContext get_reply_service_context(int paramInt);
/*     */ 
/*     */   public Connection connection()
/*     */   {
/* 428 */     return this.connection;
/*     */   }
/*     */ 
/*     */   private void insertApplicationException(ApplicationException paramApplicationException, Any paramAny)
/*     */     throws UNKNOWN
/*     */   {
/*     */     try
/*     */     {
/* 449 */       RepositoryId localRepositoryId = RepositoryId.cache.getId(paramApplicationException.getId());
/*     */ 
/* 451 */       String str1 = localRepositoryId.getClassName();
/*     */ 
/* 454 */       String str2 = str1 + "Helper";
/* 455 */       Class localClass = ORBClassLoader.loadClass(str2);
/* 456 */       Class[] arrayOfClass = new Class[1];
/* 457 */       arrayOfClass[0] = org.omg.CORBA.portable.InputStream.class;
/* 458 */       Method localMethod = localClass.getMethod("read", arrayOfClass);
/*     */ 
/* 463 */       org.omg.CORBA.portable.InputStream localInputStream = paramApplicationException.getInputStream();
/* 464 */       localInputStream.mark(0);
/* 465 */       UserException localUserException = null;
/*     */       try {
/* 467 */         java.lang.Object[] arrayOfObject = new java.lang.Object[1];
/* 468 */         arrayOfObject[0] = localInputStream;
/* 469 */         localUserException = (UserException)localMethod.invoke(null, arrayOfObject);
/*     */       }
/*     */       finally
/*     */       {
/*     */         try {
/* 474 */           localInputStream.reset();
/*     */         }
/*     */         catch (IOException localIOException2) {
/* 477 */           throw this.wrapper.markAndResetFailed(localIOException2);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 483 */       insertUserException(localUserException, paramAny);
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 485 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException);
/*     */     } catch (NoSuchMethodException localNoSuchMethodException) {
/* 487 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localNoSuchMethodException);
/*     */     } catch (SecurityException localSecurityException) {
/* 489 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localSecurityException);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 491 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localIllegalAccessException);
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 493 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localIllegalArgumentException);
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 495 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localInvocationTargetException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void insertUserException(UserException paramUserException, Any paramAny)
/*     */     throws UNKNOWN
/*     */   {
/*     */     try
/*     */     {
/* 511 */       if (paramUserException != null) {
/* 512 */         Class localClass1 = paramUserException.getClass();
/* 513 */         String str1 = localClass1.getName();
/* 514 */         String str2 = str1 + "Helper";
/* 515 */         Class localClass2 = ORBClassLoader.loadClass(str2);
/*     */ 
/* 518 */         Class[] arrayOfClass = new Class[2];
/* 519 */         arrayOfClass[0] = Any.class;
/* 520 */         arrayOfClass[1] = localClass1;
/* 521 */         Method localMethod = localClass2.getMethod("insert", arrayOfClass);
/*     */ 
/* 525 */         java.lang.Object[] arrayOfObject = new java.lang.Object[2];
/*     */ 
/* 527 */         arrayOfObject[0] = paramAny;
/* 528 */         arrayOfObject[1] = paramUserException;
/* 529 */         localMethod.invoke(null, arrayOfObject);
/*     */       }
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 532 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException);
/*     */     } catch (NoSuchMethodException localNoSuchMethodException) {
/* 534 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localNoSuchMethodException);
/*     */     } catch (SecurityException localSecurityException) {
/* 536 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localSecurityException);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 538 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localIllegalAccessException);
/*     */     } catch (IllegalArgumentException localIllegalArgumentException) {
/* 540 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localIllegalArgumentException);
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 542 */       throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localInvocationTargetException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Parameter[] nvListToParameterArray(NVList paramNVList)
/*     */   {
/* 559 */     int i = paramNVList.count();
/* 560 */     Parameter[] arrayOfParameter = new Parameter[i];
/*     */     try {
/* 562 */       for (int j = 0; j < i; j++) {
/* 563 */         Parameter localParameter = new Parameter();
/* 564 */         arrayOfParameter[j] = localParameter;
/* 565 */         NamedValue localNamedValue = paramNVList.item(j);
/* 566 */         arrayOfParameter[j].argument = localNamedValue.value();
/*     */ 
/* 574 */         arrayOfParameter[j].mode = ParameterMode.from_int(localNamedValue.flags() - 1);
/*     */       }
/*     */     } catch (Exception localException) {
/* 577 */       throw this.wrapper.exceptionInArguments(localException);
/*     */     }
/*     */ 
/* 580 */     return arrayOfParameter;
/*     */   }
/*     */ 
/*     */   protected Any exceptionToAny(Exception paramException)
/*     */   {
/* 590 */     Any localAny = this.myORB.create_any();
/*     */ 
/* 592 */     if (paramException == null)
/*     */     {
/* 595 */       throw this.wrapper.exceptionWasNull2();
/* 596 */     }if ((paramException instanceof SystemException)) {
/* 597 */       ORBUtility.insertSystemException((SystemException)paramException, localAny);
/*     */     }
/* 599 */     else if ((paramException instanceof ApplicationException))
/*     */     {
/*     */       try
/*     */       {
/* 605 */         ApplicationException localApplicationException = (ApplicationException)paramException;
/*     */ 
/* 607 */         insertApplicationException(localApplicationException, localAny);
/*     */       }
/*     */       catch (UNKNOWN localUNKNOWN1)
/*     */       {
/* 614 */         ORBUtility.insertSystemException(localUNKNOWN1, localAny);
/*     */       }
/*     */     }
/* 616 */     else if ((paramException instanceof UserException)) {
/*     */       try {
/* 618 */         UserException localUserException = (UserException)paramException;
/* 619 */         insertUserException(localUserException, localAny);
/*     */       } catch (UNKNOWN localUNKNOWN2) {
/* 621 */         ORBUtility.insertSystemException(localUNKNOWN2, localAny);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 626 */     return localAny;
/*     */   }
/*     */ 
/*     */   protected org.omg.IOP.ServiceContext getServiceContext(HashMap paramHashMap, ServiceContexts paramServiceContexts, int paramInt)
/*     */   {
/* 638 */     org.omg.IOP.ServiceContext localServiceContext = null;
/* 639 */     Integer localInteger = new Integer(paramInt);
/*     */ 
/* 642 */     localServiceContext = (org.omg.IOP.ServiceContext)paramHashMap.get(localInteger);
/*     */ 
/* 648 */     if (localServiceContext == null)
/*     */     {
/* 651 */       com.sun.corba.se.spi.servicecontext.ServiceContext localServiceContext1 = paramServiceContexts.get(paramInt);
/*     */ 
/* 653 */       if (localServiceContext1 == null) {
/* 654 */         throw this.stdWrapper.invalidServiceContextId();
/*     */       }
/*     */ 
/* 659 */       EncapsOutputStream localEncapsOutputStream = new EncapsOutputStream(this.myORB);
/*     */ 
/* 661 */       localServiceContext1.write(localEncapsOutputStream, GIOPVersion.V1_2);
/* 662 */       org.omg.CORBA.portable.InputStream localInputStream = localEncapsOutputStream.create_input_stream();
/* 663 */       localServiceContext = ServiceContextHelper.read(localInputStream);
/*     */ 
/* 665 */       paramHashMap.put(localInteger, localServiceContext);
/*     */     }
/*     */ 
/* 672 */     return localServiceContext;
/*     */   }
/*     */ 
/*     */   protected void addServiceContext(HashMap paramHashMap, ServiceContexts paramServiceContexts, org.omg.IOP.ServiceContext paramServiceContext, boolean paramBoolean)
/*     */   {
/* 693 */     int i = 0;
/*     */ 
/* 695 */     EncapsOutputStream localEncapsOutputStream = new EncapsOutputStream(this.myORB);
/*     */ 
/* 697 */     org.omg.CORBA.portable.InputStream localInputStream = null;
/* 698 */     UnknownServiceContext localUnknownServiceContext = null;
/* 699 */     ServiceContextHelper.write(localEncapsOutputStream, paramServiceContext);
/* 700 */     localInputStream = localEncapsOutputStream.create_input_stream();
/*     */ 
/* 703 */     localUnknownServiceContext = new UnknownServiceContext(localInputStream.read_long(), (org.omg.CORBA_2_3.portable.InputStream)localInputStream);
/*     */ 
/* 707 */     i = localUnknownServiceContext.getId();
/*     */ 
/* 709 */     if (paramServiceContexts.get(i) != null) {
/* 710 */       if (paramBoolean)
/* 711 */         paramServiceContexts.delete(i);
/*     */       else
/* 713 */         throw this.stdWrapper.serviceContextAddFailed(new Integer(i));
/*     */     }
/* 715 */     paramServiceContexts.put(localUnknownServiceContext);
/*     */ 
/* 718 */     paramHashMap.put(new Integer(i), paramServiceContext);
/*     */   }
/*     */ 
/*     */   protected void setFlowStackIndex(int paramInt)
/*     */   {
/* 732 */     this.flowStackIndex = paramInt;
/*     */   }
/*     */ 
/*     */   protected int getFlowStackIndex()
/*     */   {
/* 741 */     return this.flowStackIndex;
/*     */   }
/*     */ 
/*     */   protected void setEndingPointCall(int paramInt)
/*     */   {
/* 749 */     this.endingPointCall = paramInt;
/*     */   }
/*     */ 
/*     */   protected int getEndingPointCall()
/*     */   {
/* 757 */     return this.endingPointCall;
/*     */   }
/*     */ 
/*     */   protected void setIntermediatePointCall(int paramInt)
/*     */   {
/* 765 */     this.intermediatePointCall = paramInt;
/*     */   }
/*     */ 
/*     */   protected int getIntermediatePointCall()
/*     */   {
/* 773 */     return this.intermediatePointCall;
/*     */   }
/*     */ 
/*     */   protected void setStartingPointCall(int paramInt)
/*     */   {
/* 781 */     this.startingPointCall = paramInt;
/*     */   }
/*     */ 
/*     */   protected int getStartingPointCall()
/*     */   {
/* 789 */     return this.startingPointCall;
/*     */   }
/*     */ 
/*     */   protected boolean getAlreadyExecuted()
/*     */   {
/* 797 */     return this.alreadyExecuted;
/*     */   }
/*     */ 
/*     */   protected void setAlreadyExecuted(boolean paramBoolean)
/*     */   {
/* 805 */     this.alreadyExecuted = paramBoolean;
/*     */   }
/*     */ 
/*     */   protected void setReplyStatus(short paramShort)
/*     */   {
/* 812 */     this.replyStatus = paramShort;
/*     */   }
/*     */ 
/*     */   protected short getReplyStatus()
/*     */   {
/* 820 */     return this.replyStatus;
/*     */   }
/*     */ 
/*     */   protected void setForwardRequest(ForwardRequest paramForwardRequest)
/*     */   {
/* 828 */     this.forwardRequest = paramForwardRequest;
/* 829 */     this.forwardRequestIOR = null;
/*     */   }
/*     */ 
/*     */   protected void setForwardRequest(IOR paramIOR)
/*     */   {
/* 837 */     this.forwardRequestIOR = paramIOR;
/* 838 */     this.forwardRequest = null;
/*     */   }
/*     */ 
/*     */   protected ForwardRequest getForwardRequestException()
/*     */   {
/* 845 */     if ((this.forwardRequest == null) && 
/* 846 */       (this.forwardRequestIOR != null))
/*     */     {
/* 849 */       org.omg.CORBA.Object localObject = iorToObject(this.forwardRequestIOR);
/* 850 */       this.forwardRequest = new ForwardRequest(localObject);
/*     */     }
/*     */ 
/* 854 */     return this.forwardRequest;
/*     */   }
/*     */ 
/*     */   protected IOR getForwardRequestIOR()
/*     */   {
/* 861 */     if ((this.forwardRequestIOR == null) && 
/* 862 */       (this.forwardRequest != null)) {
/* 863 */       this.forwardRequestIOR = ORBUtility.getIOR(this.forwardRequest.forward);
/*     */     }
/*     */ 
/* 868 */     return this.forwardRequestIOR;
/*     */   }
/*     */ 
/*     */   protected void setException(Exception paramException)
/*     */   {
/* 876 */     this.exception = paramException;
/*     */   }
/*     */ 
/*     */   Exception getException()
/*     */   {
/* 884 */     return this.exception;
/*     */   }
/*     */ 
/*     */   protected void setCurrentExecutionPoint(int paramInt)
/*     */   {
/* 893 */     this.currentExecutionPoint = paramInt;
/*     */   }
/*     */ 
/*     */   protected abstract void checkAccess(int paramInt)
/*     */     throws BAD_INV_ORDER;
/*     */ 
/*     */   void setSlotTable(SlotTable paramSlotTable)
/*     */   {
/* 918 */     this.slotTable = paramSlotTable;
/*     */   }
/*     */ 
/*     */   protected org.omg.CORBA.Object iorToObject(IOR paramIOR)
/*     */   {
/* 923 */     return ORBUtility.makeObjectReference(paramIOR);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.interceptors.RequestInfoImpl
 * JD-Core Version:    0.6.2
 */