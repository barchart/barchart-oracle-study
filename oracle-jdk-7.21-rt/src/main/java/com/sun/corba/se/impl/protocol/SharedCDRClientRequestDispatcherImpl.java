/*     */ package com.sun.corba.se.impl.protocol;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
/*     */ import com.sun.corba.se.impl.encoding.CDRInputObject;
/*     */ import com.sun.corba.se.impl.encoding.CDROutputObject;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
/*     */ import com.sun.corba.se.pept.encoding.InputObject;
/*     */ import com.sun.corba.se.pept.encoding.OutputObject;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
/*     */ import java.io.IOException;
/*     */ import org.omg.CORBA.portable.ApplicationException;
/*     */ import org.omg.CORBA.portable.RemarshalException;
/*     */ 
/*     */ public class SharedCDRClientRequestDispatcherImpl extends CorbaClientRequestDispatcherImpl
/*     */ {
/*     */   public InputObject marshalingComplete(Object paramObject, OutputObject paramOutputObject)
/*     */     throws ApplicationException, RemarshalException
/*     */   {
/* 139 */     ORB localORB = null;
/* 140 */     CorbaMessageMediator localCorbaMessageMediator = null;
/*     */     try {
/* 142 */       localCorbaMessageMediator = (CorbaMessageMediator)paramOutputObject.getMessageMediator();
/*     */ 
/* 145 */       localORB = (ORB)localCorbaMessageMediator.getBroker();
/*     */ 
/* 147 */       if (localORB.subcontractDebugFlag) {
/* 148 */         dprint(".marshalingComplete->: " + opAndId(localCorbaMessageMediator));
/*     */       }
/*     */ 
/* 151 */       CDROutputObject localCDROutputObject = (CDROutputObject)paramOutputObject;
/*     */ 
/* 157 */       ByteBufferWithInfo localByteBufferWithInfo = localCDROutputObject.getByteBufferWithInfo();
/* 158 */       localCDROutputObject.getMessageHeader().setSize(localByteBufferWithInfo.byteBuffer, localByteBufferWithInfo.getSize());
/*     */ 
/* 160 */       CDRInputObject localCDRInputObject1 = new CDRInputObject(localORB, null, localByteBufferWithInfo.byteBuffer, localCDROutputObject.getMessageHeader());
/*     */ 
/* 163 */       localCorbaMessageMediator.setInputObject(localCDRInputObject1);
/* 164 */       localCDRInputObject1.setMessageMediator(localCorbaMessageMediator);
/*     */ 
/* 171 */       ((CorbaMessageMediatorImpl)localCorbaMessageMediator).handleRequestRequest(localCorbaMessageMediator);
/*     */       try
/*     */       {
/* 177 */         localCDRInputObject1.close();
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/* 183 */         if (localORB.transportDebugFlag) {
/* 184 */           dprint(".marshalingComplete: ignoring IOException - " + localIOException.toString());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 192 */       localCDROutputObject = (CDROutputObject)localCorbaMessageMediator.getOutputObject();
/* 193 */       localByteBufferWithInfo = localCDROutputObject.getByteBufferWithInfo();
/* 194 */       localCDROutputObject.getMessageHeader().setSize(localByteBufferWithInfo.byteBuffer, localByteBufferWithInfo.getSize());
/* 195 */       localCDRInputObject1 = new CDRInputObject(localORB, null, localByteBufferWithInfo.byteBuffer, localCDROutputObject.getMessageHeader());
/*     */ 
/* 198 */       localCorbaMessageMediator.setInputObject(localCDRInputObject1);
/* 199 */       localCDRInputObject1.setMessageMediator(localCorbaMessageMediator);
/*     */ 
/* 201 */       localCDRInputObject1.unmarshalHeader();
/*     */ 
/* 203 */       CDRInputObject localCDRInputObject2 = localCDRInputObject1;
/*     */ 
/* 205 */       return processResponse(localORB, localCorbaMessageMediator, localCDRInputObject2);
/*     */     }
/*     */     finally {
/* 208 */       if (localORB.subcontractDebugFlag)
/* 209 */         dprint(".marshalingComplete<-: " + opAndId(localCorbaMessageMediator));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void dprint(String paramString)
/*     */   {
/* 216 */     ORBUtility.dprint("SharedCDRClientRequestDispatcherImpl", paramString);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.protocol.SharedCDRClientRequestDispatcherImpl
 * JD-Core Version:    0.6.2
 */