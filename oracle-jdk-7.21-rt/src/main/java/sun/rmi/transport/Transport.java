/*     */ package sun.rmi.transport;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutput;
/*     */ import java.rmi.MarshalException;
/*     */ import java.rmi.NoSuchObjectException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.LogStream;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.rmi.server.RemoteCall;
/*     */ import java.rmi.server.RemoteServer;
/*     */ import java.rmi.server.ServerNotActiveException;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.server.Dispatcher;
/*     */ import sun.rmi.server.UnicastServerRef;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public abstract class Transport
/*     */ {
/*  53 */   static final int logLevel = LogStream.parseLevel(getLogLevel());
/*     */ 
/*  61 */   static final Log transportLog = Log.getLog("sun.rmi.transport.misc", "transport", logLevel);
/*     */ 
/*  65 */   private static final ThreadLocal currentTransport = new ThreadLocal();
/*     */ 
/*  68 */   private static final ObjID dgcID = new ObjID(2);
/*     */ 
/*     */   private static String getLogLevel()
/*     */   {
/*  56 */     return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.logLevel"));
/*     */   }
/*     */ 
/*     */   public abstract Channel getChannel(Endpoint paramEndpoint);
/*     */ 
/*     */   public abstract void free(Endpoint paramEndpoint);
/*     */ 
/*     */   public void exportObject(Target paramTarget)
/*     */     throws RemoteException
/*     */   {
/*  91 */     paramTarget.setExportedTransport(this);
/*  92 */     ObjectTable.putTarget(paramTarget);
/*     */   }
/*     */ 
/*     */   protected void targetUnexported()
/*     */   {
/*     */   }
/*     */ 
/*     */   static Transport currentTransport()
/*     */   {
/* 107 */     return (Transport)currentTransport.get();
/*     */   }
/*     */ 
/*     */   protected abstract void checkAcceptPermission(AccessControlContext paramAccessControlContext);
/*     */ 
/*     */   public boolean serviceCall(final RemoteCall paramRemoteCall)
/*     */   {
/*     */     try
/*     */     {
/*     */       try
/*     */       {
/* 142 */         localObject1 = ObjID.read(paramRemoteCall.getInputStream());
/*     */       } catch (IOException localIOException2) {
/* 144 */         throw new MarshalException("unable to read objID", localIOException2);
/*     */       }
/*     */ 
/* 148 */       Transport localTransport = ((ObjID)localObject1).equals(dgcID) ? null : this;
/* 149 */       Target localTarget = ObjectTable.getTarget(new ObjectEndpoint((ObjID)localObject1, localTransport));
/*     */       final Remote localRemote;
/* 152 */       if ((localTarget == null) || ((localRemote = localTarget.getImpl()) == null)) {
/* 153 */         throw new NoSuchObjectException("no such object in table");
/*     */       }
/*     */ 
/* 156 */       final Dispatcher localDispatcher = localTarget.getDispatcher();
/* 157 */       localTarget.incrementCallCount();
/*     */       try
/*     */       {
/* 160 */         transportLog.log(Log.VERBOSE, "call dispatcher");
/*     */ 
/* 162 */         final AccessControlContext localAccessControlContext = localTarget.getAccessControlContext();
/*     */ 
/* 164 */         ClassLoader localClassLoader1 = localTarget.getContextClassLoader();
/*     */ 
/* 166 */         Thread localThread = Thread.currentThread();
/* 167 */         ClassLoader localClassLoader2 = localThread.getContextClassLoader();
/*     */         try
/*     */         {
/* 170 */           localThread.setContextClassLoader(localClassLoader1);
/* 171 */           currentTransport.set(this);
/*     */           try {
/* 173 */             AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */             {
/*     */               public Void run() throws IOException {
/* 176 */                 Transport.this.checkAcceptPermission(localAccessControlContext);
/* 177 */                 localDispatcher.dispatch(localRemote, paramRemoteCall);
/* 178 */                 return null;
/*     */               }
/*     */             }
/*     */             , localAccessControlContext);
/*     */           }
/*     */           catch (PrivilegedActionException localPrivilegedActionException)
/*     */           {
/* 182 */             throw ((IOException)localPrivilegedActionException.getException());
/*     */           }
/*     */         } finally {
/* 185 */           localThread.setContextClassLoader(localClassLoader2);
/* 186 */           currentTransport.set(null);
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException3) {
/* 190 */         transportLog.log(Log.BRIEF, "exception thrown by dispatcher: ", localIOException3);
/*     */ 
/* 192 */         return false;
/*     */       } finally {
/* 194 */         localTarget.decrementCallCount();
/*     */       }
/*     */     }
/*     */     catch (RemoteException localRemoteException)
/*     */     {
/*     */       Object localObject1;
/* 200 */       if (UnicastServerRef.callLog.isLoggable(Log.BRIEF))
/*     */       {
/* 202 */         localObject1 = "";
/*     */         try {
/* 204 */           localObject1 = "[" + RemoteServer.getClientHost() + "] ";
/*     */         }
/*     */         catch (ServerNotActiveException localServerNotActiveException) {
/*     */         }
/* 208 */         String str = (String)localObject1 + "exception: ";
/* 209 */         UnicastServerRef.callLog.log(Log.BRIEF, str, localRemoteException);
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 221 */         localObject1 = paramRemoteCall.getResultStream(false);
/* 222 */         UnicastServerRef.clearStackTraces(localRemoteException);
/* 223 */         ((ObjectOutput)localObject1).writeObject(localRemoteException);
/* 224 */         paramRemoteCall.releaseOutputStream();
/*     */       }
/*     */       catch (IOException localIOException1) {
/* 227 */         transportLog.log(Log.BRIEF, "exception thrown marshalling exception: ", localIOException1);
/*     */ 
/* 229 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 233 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.Transport
 * JD-Core Version:    0.6.2
 */