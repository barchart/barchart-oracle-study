/*     */ package com.sun.corba.se.impl.transport;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.BufferManagerReadStream;
/*     */ import com.sun.corba.se.impl.encoding.CDRInputObject;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
/*     */ import com.sun.corba.se.pept.encoding.InputObject;
/*     */ import com.sun.corba.se.pept.protocol.MessageMediator;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
/*     */ import com.sun.corba.se.spi.transport.CorbaConnection;
/*     */ import com.sun.corba.se.spi.transport.CorbaResponseWaitingRoom;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.SystemException;
/*     */ 
/*     */ public class CorbaResponseWaitingRoomImpl
/*     */   implements CorbaResponseWaitingRoom
/*     */ {
/*     */   private ORB orb;
/*     */   private ORBUtilSystemException wrapper;
/*     */   private CorbaConnection connection;
/*  71 */   private Hashtable out_calls = null;
/*     */ 
/*     */   public CorbaResponseWaitingRoomImpl(ORB paramORB, CorbaConnection paramCorbaConnection)
/*     */   {
/*  75 */     this.orb = paramORB;
/*  76 */     this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
/*     */ 
/*  78 */     this.connection = paramCorbaConnection;
/*  79 */     this.out_calls = new Hashtable();
/*     */   }
/*     */ 
/*     */   public void registerWaiter(MessageMediator paramMessageMediator)
/*     */   {
/*  89 */     CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
/*     */ 
/*  91 */     if (this.orb.transportDebugFlag) {
/*  92 */       dprint(".registerWaiter: " + opAndId(localCorbaMessageMediator));
/*     */     }
/*     */ 
/*  95 */     Integer localInteger = localCorbaMessageMediator.getRequestIdInteger();
/*     */ 
/*  97 */     OutCallDesc localOutCallDesc = new OutCallDesc();
/*  98 */     localOutCallDesc.thread = Thread.currentThread();
/*  99 */     localOutCallDesc.messageMediator = localCorbaMessageMediator;
/* 100 */     this.out_calls.put(localInteger, localOutCallDesc);
/*     */   }
/*     */ 
/*     */   public void unregisterWaiter(MessageMediator paramMessageMediator)
/*     */   {
/* 105 */     CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
/*     */ 
/* 107 */     if (this.orb.transportDebugFlag) {
/* 108 */       dprint(".unregisterWaiter: " + opAndId(localCorbaMessageMediator));
/*     */     }
/*     */ 
/* 111 */     Integer localInteger = localCorbaMessageMediator.getRequestIdInteger();
/*     */ 
/* 113 */     this.out_calls.remove(localInteger);
/*     */   }
/*     */ 
/*     */   public InputObject waitForResponse(MessageMediator paramMessageMediator)
/*     */   {
/* 118 */     CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
/*     */     try
/*     */     {
/* 122 */       InputObject localInputObject = null;
/*     */ 
/* 124 */       if (this.orb.transportDebugFlag) {
/* 125 */         dprint(".waitForResponse->: " + opAndId(localCorbaMessageMediator));
/*     */       }
/*     */ 
/* 128 */       Integer localInteger = localCorbaMessageMediator.getRequestIdInteger();
/*     */ 
/* 130 */       if (localCorbaMessageMediator.isOneWay())
/*     */       {
/* 134 */         if (this.orb.transportDebugFlag) {
/* 135 */           dprint(".waitForResponse: one way - not waiting: " + opAndId(localCorbaMessageMediator));
/*     */         }
/*     */ 
/* 139 */         return null;
/*     */       }
/*     */ 
/* 142 */       Object localObject1 = (OutCallDesc)this.out_calls.get(localInteger);
/* 143 */       if (localObject1 == null) {
/* 144 */         throw this.wrapper.nullOutCall(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */ 
/* 147 */       synchronized (((OutCallDesc)localObject1).done)
/*     */       {
/* 149 */         while ((((OutCallDesc)localObject1).inputObject == null) && (((OutCallDesc)localObject1).exception == null))
/*     */         {
/*     */           try
/*     */           {
/* 154 */             if (this.orb.transportDebugFlag) {
/* 155 */               dprint(".waitForResponse: waiting: " + opAndId(localCorbaMessageMediator));
/*     */             }
/*     */ 
/* 158 */             ((OutCallDesc)localObject1).done.wait();
/*     */           } catch (InterruptedException localInterruptedException) {
/*     */           }
/*     */         }
/* 162 */         if (((OutCallDesc)localObject1).exception != null)
/*     */         {
/* 167 */           throw ((OutCallDesc)localObject1).exception;
/*     */         }
/*     */ 
/* 170 */         localInputObject = ((OutCallDesc)localObject1).inputObject;
/*     */       }
/*     */ 
/* 176 */       if (localInputObject != null)
/*     */       {
/* 182 */         ((CDRInputObject)localInputObject).unmarshalHeader();
/*     */       }
/*     */ 
/* 185 */       return localInputObject;
/*     */     }
/*     */     finally {
/* 188 */       if (this.orb.transportDebugFlag)
/* 189 */         dprint(".waitForResponse<-: " + opAndId(localCorbaMessageMediator));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void responseReceived(InputObject paramInputObject)
/*     */   {
/* 196 */     CDRInputObject localCDRInputObject = (CDRInputObject)paramInputObject;
/* 197 */     LocateReplyOrReplyMessage localLocateReplyOrReplyMessage = (LocateReplyOrReplyMessage)localCDRInputObject.getMessageHeader();
/*     */ 
/* 199 */     Integer localInteger = new Integer(localLocateReplyOrReplyMessage.getRequestId());
/* 200 */     OutCallDesc localOutCallDesc = (OutCallDesc)this.out_calls.get(localInteger);
/*     */ 
/* 202 */     if (this.orb.transportDebugFlag) {
/* 203 */       dprint(".responseReceived: id/" + localInteger + ": " + localLocateReplyOrReplyMessage);
/*     */     }
/*     */ 
/* 216 */     if (localOutCallDesc == null) {
/* 217 */       if (this.orb.transportDebugFlag) {
/* 218 */         dprint(".responseReceived: id/" + localInteger + ": no waiter: " + localLocateReplyOrReplyMessage);
/*     */       }
/*     */ 
/* 223 */       return;
/*     */     }
/*     */ 
/* 231 */     synchronized (localOutCallDesc.done) {
/* 232 */       CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)localOutCallDesc.messageMediator;
/*     */ 
/* 235 */       if (this.orb.transportDebugFlag) {
/* 236 */         dprint(".responseReceived: " + opAndId(localCorbaMessageMediator) + ": notifying waiters");
/*     */       }
/*     */ 
/* 241 */       localCorbaMessageMediator.setReplyHeader(localLocateReplyOrReplyMessage);
/* 242 */       localCorbaMessageMediator.setInputObject(paramInputObject);
/* 243 */       localCDRInputObject.setMessageMediator(localCorbaMessageMediator);
/* 244 */       localOutCallDesc.inputObject = paramInputObject;
/* 245 */       localOutCallDesc.done.notify();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int numberRegistered()
/*     */   {
/* 252 */     return this.out_calls.size();
/*     */   }
/*     */ 
/*     */   public void signalExceptionToAllWaiters(SystemException paramSystemException)
/*     */   {
/* 263 */     if (this.orb.transportDebugFlag) {
/* 264 */       dprint(".signalExceptionToAllWaiters: " + paramSystemException);
/*     */     }
/*     */ 
/* 268 */     Enumeration localEnumeration = this.out_calls.elements();
/* 269 */     while (localEnumeration.hasMoreElements()) {
/* 270 */       OutCallDesc localOutCallDesc = (OutCallDesc)localEnumeration.nextElement();
/*     */ 
/* 272 */       synchronized (localOutCallDesc.done)
/*     */       {
/* 275 */         CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)localOutCallDesc.messageMediator;
/*     */ 
/* 277 */         CDRInputObject localCDRInputObject = (CDRInputObject)localCorbaMessageMediator.getInputObject();
/*     */ 
/* 281 */         if (localCDRInputObject != null) {
/* 282 */           BufferManagerReadStream localBufferManagerReadStream = (BufferManagerReadStream)localCDRInputObject.getBufferManager();
/*     */ 
/* 284 */           int i = localCorbaMessageMediator.getRequestId();
/* 285 */           localBufferManagerReadStream.cancelProcessing(i);
/*     */         }
/* 287 */         localOutCallDesc.inputObject = null;
/* 288 */         localOutCallDesc.exception = paramSystemException;
/* 289 */         localOutCallDesc.done.notify();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public MessageMediator getMessageMediator(int paramInt)
/*     */   {
/* 296 */     Integer localInteger = new Integer(paramInt);
/* 297 */     OutCallDesc localOutCallDesc = (OutCallDesc)this.out_calls.get(localInteger);
/* 298 */     if (localOutCallDesc == null)
/*     */     {
/* 301 */       return null;
/*     */     }
/* 303 */     return localOutCallDesc.messageMediator;
/*     */   }
/*     */ 
/*     */   protected void dprint(String paramString)
/*     */   {
/* 313 */     ORBUtility.dprint("CorbaResponseWaitingRoomImpl", paramString);
/*     */   }
/*     */ 
/*     */   protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator)
/*     */   {
/* 318 */     return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator);
/*     */   }
/*     */ 
/*     */   static final class OutCallDesc
/*     */   {
/*  59 */     Object done = new Object();
/*     */     Thread thread;
/*     */     MessageMediator messageMediator;
/*     */     SystemException exception;
/*     */     InputObject inputObject;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.transport.CorbaResponseWaitingRoomImpl
 * JD-Core Version:    0.6.2
 */