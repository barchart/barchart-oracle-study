/*     */ package sun.rmi.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.lang.reflect.Method;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.UnmarshalException;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.rmi.server.Operation;
/*     */ import java.rmi.server.RemoteCall;
/*     */ import java.rmi.server.RemoteObject;
/*     */ import java.rmi.server.RemoteRef;
/*     */ import java.security.AccessController;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.transport.Channel;
/*     */ import sun.rmi.transport.Connection;
/*     */ import sun.rmi.transport.LiveRef;
/*     */ import sun.rmi.transport.StreamRemoteCall;
/*     */ import sun.security.action.GetBooleanAction;
/*     */ 
/*     */ public class UnicastRef
/*     */   implements RemoteRef
/*     */ {
/*  58 */   public static final Log clientRefLog = Log.getLog("sun.rmi.client.ref", "transport", Util.logLevel);
/*     */ 
/*  64 */   public static final Log clientCallLog = Log.getLog("sun.rmi.client.call", "RMI", ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.client.logCalls"))).booleanValue());
/*     */   protected LiveRef ref;
/*     */ 
/*     */   public UnicastRef()
/*     */   {
/*     */   }
/*     */ 
/*     */   public UnicastRef(LiveRef paramLiveRef)
/*     */   {
/*  81 */     this.ref = paramLiveRef;
/*     */   }
/*     */ 
/*     */   public LiveRef getLiveRef()
/*     */   {
/*  93 */     return this.ref;
/*     */   }
/*     */ 
/*     */   public Object invoke(Remote paramRemote, Method paramMethod, Object[] paramArrayOfObject, long paramLong)
/*     */     throws Exception
/*     */   {
/* 120 */     if (clientRefLog.isLoggable(Log.VERBOSE)) {
/* 121 */       clientRefLog.log(Log.VERBOSE, "method: " + paramMethod);
/*     */     }
/*     */ 
/* 124 */     if (clientCallLog.isLoggable(Log.VERBOSE)) {
/* 125 */       logClientCall(paramRemote, paramMethod);
/*     */     }
/*     */ 
/* 128 */     Connection localConnection = this.ref.getChannel().newConnection();
/* 129 */     StreamRemoteCall localStreamRemoteCall = null;
/* 130 */     boolean bool = true;
/*     */ 
/* 135 */     int i = 0;
/*     */     try
/*     */     {
/* 138 */       if (clientRefLog.isLoggable(Log.VERBOSE)) {
/* 139 */         clientRefLog.log(Log.VERBOSE, "opnum = " + paramLong);
/*     */       }
/*     */ 
/* 143 */       localStreamRemoteCall = new StreamRemoteCall(localConnection, this.ref.getObjID(), -1, paramLong);
/*     */       Object localObject1;
/*     */       try
/*     */       {
/* 147 */         ObjectOutput localObjectOutput = localStreamRemoteCall.getOutputStream();
/* 148 */         marshalCustomCallData(localObjectOutput);
/* 149 */         localObject1 = paramMethod.getParameterTypes();
/* 150 */         for (int j = 0; j < localObject1.length; j++)
/* 151 */           marshalValue(localObject1[j], paramArrayOfObject[j], localObjectOutput);
/*     */       }
/*     */       catch (IOException localIOException1) {
/* 154 */         clientRefLog.log(Log.BRIEF, "IOException marshalling arguments: ", localIOException1);
/*     */ 
/* 156 */         throw new MarshalException("error marshalling arguments", localIOException1);
/*     */       }
/*     */ 
/* 160 */       localStreamRemoteCall.executeCall();
/*     */       try
/*     */       {
/* 163 */         Class localClass = paramMethod.getReturnType();
/* 164 */         if (localClass == Void.TYPE) {
/* 165 */           localObject1 = null;
/*     */           try
/*     */           {
/* 199 */             localStreamRemoteCall.done();
/*     */           }
/*     */           catch (IOException localIOException3)
/*     */           {
/* 207 */             bool = false;
/*     */           }
/*     */ 
/* 255 */           return localObject1;
/*     */         }
/* 166 */         localObject1 = localStreamRemoteCall.getInputStream();
/*     */ 
/* 173 */         Object localObject2 = unmarshalValue(localClass, (ObjectInput)localObject1);
/*     */ 
/* 178 */         i = 1;
/*     */ 
/* 181 */         clientRefLog.log(Log.BRIEF, "free connection (reuse = true)");
/*     */ 
/* 184 */         this.ref.getChannel().free(localConnection, true);
/*     */ 
/* 186 */         Object localObject3 = localObject2;
/*     */         try
/*     */         {
/* 199 */           localStreamRemoteCall.done();
/*     */         }
/*     */         catch (IOException localIOException4)
/*     */         {
/* 207 */           bool = false;
/*     */         }
/*     */ 
/* 255 */         return localObject3;
/*     */       }
/*     */       catch (IOException localIOException2)
/*     */       {
/* 189 */         clientRefLog.log(Log.BRIEF, "IOException unmarshalling return: ", localIOException2);
/*     */ 
/* 191 */         throw new UnmarshalException("error unmarshalling return", localIOException2);
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/* 193 */         clientRefLog.log(Log.BRIEF, "ClassNotFoundException unmarshalling return: ", localClassNotFoundException);
/*     */ 
/* 196 */         throw new UnmarshalException("error unmarshalling return", localClassNotFoundException);
/*     */       } finally {
/*     */         try {
/* 199 */           localStreamRemoteCall.done();
/*     */         }
/*     */         catch (IOException localIOException5)
/*     */         {
/* 207 */           bool = false;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (RuntimeException localRuntimeException)
/*     */     {
/* 220 */       if ((localStreamRemoteCall == null) || (((StreamRemoteCall)localStreamRemoteCall).getServerException() != localRuntimeException))
/*     */       {
/* 223 */         bool = false;
/*     */       }
/* 225 */       throw localRuntimeException;
/*     */     }
/*     */     catch (RemoteException localRemoteException)
/*     */     {
/* 235 */       bool = false;
/* 236 */       throw localRemoteException;
/*     */     }
/*     */     catch (Error localError)
/*     */     {
/* 242 */       bool = false;
/* 243 */       throw localError;
/*     */     }
/*     */     finally
/*     */     {
/* 250 */       if (i == 0) {
/* 251 */         if (clientRefLog.isLoggable(Log.BRIEF)) {
/* 252 */           clientRefLog.log(Log.BRIEF, "free connection (reuse = " + bool + ")");
/*     */         }
/*     */ 
/* 255 */         this.ref.getChannel().free(localConnection, bool);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void marshalCustomCallData(ObjectOutput paramObjectOutput)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected static void marshalValue(Class<?> paramClass, Object paramObject, ObjectOutput paramObjectOutput)
/*     */     throws IOException
/*     */   {
/* 271 */     if (paramClass.isPrimitive()) {
/* 272 */       if (paramClass == Integer.TYPE)
/* 273 */         paramObjectOutput.writeInt(((Integer)paramObject).intValue());
/* 274 */       else if (paramClass == Boolean.TYPE)
/* 275 */         paramObjectOutput.writeBoolean(((Boolean)paramObject).booleanValue());
/* 276 */       else if (paramClass == Byte.TYPE)
/* 277 */         paramObjectOutput.writeByte(((Byte)paramObject).byteValue());
/* 278 */       else if (paramClass == Character.TYPE)
/* 279 */         paramObjectOutput.writeChar(((Character)paramObject).charValue());
/* 280 */       else if (paramClass == Short.TYPE)
/* 281 */         paramObjectOutput.writeShort(((Short)paramObject).shortValue());
/* 282 */       else if (paramClass == Long.TYPE)
/* 283 */         paramObjectOutput.writeLong(((Long)paramObject).longValue());
/* 284 */       else if (paramClass == Float.TYPE)
/* 285 */         paramObjectOutput.writeFloat(((Float)paramObject).floatValue());
/* 286 */       else if (paramClass == Double.TYPE)
/* 287 */         paramObjectOutput.writeDouble(((Double)paramObject).doubleValue());
/*     */       else
/* 289 */         throw new Error("Unrecognized primitive type: " + paramClass);
/*     */     }
/*     */     else
/* 292 */       paramObjectOutput.writeObject(paramObject);
/*     */   }
/*     */ 
/*     */   protected static Object unmarshalValue(Class<?> paramClass, ObjectInput paramObjectInput)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 303 */     if (paramClass.isPrimitive()) {
/* 304 */       if (paramClass == Integer.TYPE)
/* 305 */         return Integer.valueOf(paramObjectInput.readInt());
/* 306 */       if (paramClass == Boolean.TYPE)
/* 307 */         return Boolean.valueOf(paramObjectInput.readBoolean());
/* 308 */       if (paramClass == Byte.TYPE)
/* 309 */         return Byte.valueOf(paramObjectInput.readByte());
/* 310 */       if (paramClass == Character.TYPE)
/* 311 */         return Character.valueOf(paramObjectInput.readChar());
/* 312 */       if (paramClass == Short.TYPE)
/* 313 */         return Short.valueOf(paramObjectInput.readShort());
/* 314 */       if (paramClass == Long.TYPE)
/* 315 */         return Long.valueOf(paramObjectInput.readLong());
/* 316 */       if (paramClass == Float.TYPE)
/* 317 */         return Float.valueOf(paramObjectInput.readFloat());
/* 318 */       if (paramClass == Double.TYPE) {
/* 319 */         return Double.valueOf(paramObjectInput.readDouble());
/*     */       }
/* 321 */       throw new Error("Unrecognized primitive type: " + paramClass);
/*     */     }
/*     */ 
/* 324 */     return paramObjectInput.readObject();
/*     */   }
/*     */ 
/*     */   public RemoteCall newCall(RemoteObject paramRemoteObject, Operation[] paramArrayOfOperation, int paramInt, long paramLong)
/*     */     throws RemoteException
/*     */   {
/* 338 */     clientRefLog.log(Log.BRIEF, "get connection");
/*     */ 
/* 340 */     Connection localConnection = this.ref.getChannel().newConnection();
/*     */     try {
/* 342 */       clientRefLog.log(Log.VERBOSE, "create call context");
/*     */ 
/* 345 */       if (clientCallLog.isLoggable(Log.VERBOSE)) {
/* 346 */         logClientCall(paramRemoteObject, paramArrayOfOperation[paramInt]);
/*     */       }
/*     */ 
/* 349 */       StreamRemoteCall localStreamRemoteCall = new StreamRemoteCall(localConnection, this.ref.getObjID(), paramInt, paramLong);
/*     */       try
/*     */       {
/* 352 */         marshalCustomCallData(localStreamRemoteCall.getOutputStream());
/*     */       } catch (IOException localIOException) {
/* 354 */         throw new MarshalException("error marshaling custom call data");
/*     */       }
/*     */ 
/* 357 */       return localStreamRemoteCall;
/*     */     } catch (RemoteException localRemoteException) {
/* 359 */       this.ref.getChannel().free(localConnection, false);
/* 360 */       throw localRemoteException;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void invoke(RemoteCall paramRemoteCall)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 375 */       clientRefLog.log(Log.VERBOSE, "execute call");
/*     */ 
/* 377 */       paramRemoteCall.executeCall();
/*     */     }
/*     */     catch (RemoteException localRemoteException)
/*     */     {
/* 383 */       clientRefLog.log(Log.BRIEF, "exception: ", localRemoteException);
/* 384 */       free(paramRemoteCall, false);
/* 385 */       throw localRemoteException;
/*     */     }
/*     */     catch (Error localError)
/*     */     {
/* 391 */       clientRefLog.log(Log.BRIEF, "error: ", localError);
/* 392 */       free(paramRemoteCall, false);
/* 393 */       throw localError;
/*     */     }
/*     */     catch (RuntimeException localRuntimeException)
/*     */     {
/* 401 */       clientRefLog.log(Log.BRIEF, "exception: ", localRuntimeException);
/* 402 */       free(paramRemoteCall, false);
/* 403 */       throw localRuntimeException;
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 410 */       clientRefLog.log(Log.BRIEF, "exception: ", localException);
/* 411 */       free(paramRemoteCall, true);
/*     */ 
/* 413 */       throw localException;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void free(RemoteCall paramRemoteCall, boolean paramBoolean)
/*     */     throws RemoteException
/*     */   {
/* 428 */     Connection localConnection = ((StreamRemoteCall)paramRemoteCall).getConnection();
/* 429 */     this.ref.getChannel().free(localConnection, paramBoolean);
/*     */   }
/*     */ 
/*     */   public void done(RemoteCall paramRemoteCall)
/*     */     throws RemoteException
/*     */   {
/* 443 */     clientRefLog.log(Log.BRIEF, "free connection (reuse = true)");
/*     */ 
/* 446 */     free(paramRemoteCall, true);
/*     */     try
/*     */     {
/* 449 */       paramRemoteCall.done();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   void logClientCall(Object paramObject1, Object paramObject2)
/*     */   {
/* 464 */     clientCallLog.log(Log.VERBOSE, "outbound call: " + this.ref + " : " + paramObject1.getClass().getName() + this.ref.getObjID().toString() + ": " + paramObject2);
/*     */   }
/*     */ 
/*     */   public String getRefClass(ObjectOutput paramObjectOutput)
/*     */   {
/* 473 */     return "UnicastRef";
/*     */   }
/*     */ 
/*     */   public void writeExternal(ObjectOutput paramObjectOutput)
/*     */     throws IOException
/*     */   {
/* 480 */     this.ref.write(paramObjectOutput, false);
/*     */   }
/*     */ 
/*     */   public void readExternal(ObjectInput paramObjectInput)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 491 */     this.ref = LiveRef.read(paramObjectInput, false);
/*     */   }
/*     */ 
/*     */   public String remoteToString()
/*     */   {
/* 499 */     return Util.getUnqualifiedName(getClass()) + " [liveRef: " + this.ref + "]";
/*     */   }
/*     */ 
/*     */   public int remoteHashCode()
/*     */   {
/* 506 */     return this.ref.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean remoteEquals(RemoteRef paramRemoteRef)
/*     */   {
/* 512 */     if ((paramRemoteRef instanceof UnicastRef))
/* 513 */       return this.ref.remoteEquals(((UnicastRef)paramRemoteRef).ref);
/* 514 */     return false;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.server.UnicastRef
 * JD-Core Version:    0.6.2
 */