/*     */ package sun.rmi.transport;
/*     */ 
/*     */ import java.rmi.NoSuchObjectException;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.dgc.VMID;
/*     */ import java.rmi.server.ObjID;
/*     */ import java.rmi.server.Unreferenced;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import sun.rmi.runtime.Log;
/*     */ import sun.rmi.runtime.NewThreadAction;
/*     */ import sun.rmi.server.Dispatcher;
/*     */ 
/*     */ public final class Target
/*     */ {
/*     */   private final ObjID id;
/*     */   private final boolean permanent;
/*     */   private final WeakRef weakImpl;
/*     */   private volatile Dispatcher disp;
/*     */   private final Remote stub;
/*  56 */   private final Vector refSet = new Vector();
/*     */ 
/*  58 */   private final Hashtable sequenceTable = new Hashtable(5);
/*     */   private final AccessControlContext acc;
/*     */   private final ClassLoader ccl;
/*  64 */   private int callCount = 0;
/*     */ 
/*  66 */   private boolean removed = false;
/*     */ 
/*  71 */   private volatile Transport exportedTransport = null;
/*     */ 
/*  74 */   private static int nextThreadNum = 0;
/*     */ 
/*     */   public Target(Remote paramRemote1, Dispatcher paramDispatcher, Remote paramRemote2, ObjID paramObjID, boolean paramBoolean)
/*     */   {
/*  89 */     this.weakImpl = new WeakRef(paramRemote1, ObjectTable.reapQueue);
/*  90 */     this.disp = paramDispatcher;
/*  91 */     this.stub = paramRemote2;
/*  92 */     this.id = paramObjID;
/*  93 */     this.acc = AccessController.getContext();
/*     */ 
/* 107 */     ClassLoader localClassLoader1 = Thread.currentThread().getContextClassLoader();
/*     */ 
/* 109 */     ClassLoader localClassLoader2 = paramRemote1.getClass().getClassLoader();
/* 110 */     if (checkLoaderAncestry(localClassLoader1, localClassLoader2))
/* 111 */       this.ccl = localClassLoader1;
/*     */     else {
/* 113 */       this.ccl = localClassLoader2;
/*     */     }
/*     */ 
/* 116 */     this.permanent = paramBoolean;
/* 117 */     if (paramBoolean)
/* 118 */       pinImpl();
/*     */   }
/*     */ 
/*     */   private static boolean checkLoaderAncestry(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*     */   {
/* 132 */     if (paramClassLoader2 == null)
/* 133 */       return true;
/* 134 */     if (paramClassLoader1 == null) {
/* 135 */       return false;
/*     */     }
/* 137 */     for (ClassLoader localClassLoader = paramClassLoader1; 
/* 138 */       localClassLoader != null; 
/* 139 */       localClassLoader = localClassLoader.getParent())
/*     */     {
/* 141 */       if (localClassLoader == paramClassLoader2) {
/* 142 */         return true;
/*     */       }
/*     */     }
/* 145 */     return false;
/*     */   }
/*     */ 
/*     */   public Remote getStub()
/*     */   {
/* 152 */     return this.stub;
/*     */   }
/*     */ 
/*     */   ObjectEndpoint getObjectEndpoint()
/*     */   {
/* 159 */     return new ObjectEndpoint(this.id, this.exportedTransport);
/*     */   }
/*     */ 
/*     */   WeakRef getWeakImpl()
/*     */   {
/* 166 */     return this.weakImpl;
/*     */   }
/*     */ 
/*     */   Dispatcher getDispatcher()
/*     */   {
/* 173 */     return this.disp;
/*     */   }
/*     */ 
/*     */   AccessControlContext getAccessControlContext() {
/* 177 */     return this.acc;
/*     */   }
/*     */ 
/*     */   ClassLoader getContextClassLoader() {
/* 181 */     return this.ccl;
/*     */   }
/*     */ 
/*     */   Remote getImpl()
/*     */   {
/* 190 */     return (Remote)this.weakImpl.get();
/*     */   }
/*     */ 
/*     */   boolean isPermanent()
/*     */   {
/* 197 */     return this.permanent;
/*     */   }
/*     */ 
/*     */   synchronized void pinImpl()
/*     */   {
/* 207 */     this.weakImpl.pin();
/*     */   }
/*     */ 
/*     */   synchronized void unpinImpl()
/*     */   {
/* 222 */     if ((!this.permanent) && (this.refSet.isEmpty()))
/* 223 */       this.weakImpl.unpin();
/*     */   }
/*     */ 
/*     */   void setExportedTransport(Transport paramTransport)
/*     */   {
/* 232 */     if (this.exportedTransport == null)
/* 233 */       this.exportedTransport = paramTransport;
/*     */   }
/*     */ 
/*     */   synchronized void referenced(long paramLong, VMID paramVMID)
/*     */   {
/* 244 */     SequenceEntry localSequenceEntry = (SequenceEntry)this.sequenceTable.get(paramVMID);
/* 245 */     if (localSequenceEntry == null)
/* 246 */       this.sequenceTable.put(paramVMID, new SequenceEntry(paramLong));
/* 247 */     else if (localSequenceEntry.sequenceNum < paramLong) {
/* 248 */       localSequenceEntry.update(paramLong);
/*     */     }
/*     */     else {
/* 251 */       return;
/*     */     }
/*     */ 
/* 254 */     if (!this.refSet.contains(paramVMID))
/*     */     {
/* 262 */       pinImpl();
/* 263 */       if (getImpl() == null) {
/* 264 */         return;
/*     */       }
/* 266 */       if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
/* 267 */         DGCImpl.dgcLog.log(Log.VERBOSE, "add to dirty set: " + paramVMID);
/*     */       }
/*     */ 
/* 270 */       this.refSet.addElement(paramVMID);
/*     */ 
/* 272 */       DGCImpl.getDGCImpl().registerTarget(paramVMID, this);
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized void unreferenced(long paramLong, VMID paramVMID, boolean paramBoolean)
/*     */   {
/* 283 */     SequenceEntry localSequenceEntry = (SequenceEntry)this.sequenceTable.get(paramVMID);
/* 284 */     if ((localSequenceEntry == null) || (localSequenceEntry.sequenceNum > paramLong))
/*     */     {
/* 286 */       return;
/* 287 */     }if (paramBoolean)
/*     */     {
/* 289 */       localSequenceEntry.retain(paramLong);
/* 290 */     } else if (!localSequenceEntry.keep)
/*     */     {
/* 292 */       this.sequenceTable.remove(paramVMID);
/*     */     }
/*     */ 
/* 295 */     if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
/* 296 */       DGCImpl.dgcLog.log(Log.VERBOSE, "remove from dirty set: " + paramVMID);
/*     */     }
/*     */ 
/* 299 */     refSetRemove(paramVMID);
/*     */   }
/*     */ 
/*     */   private synchronized void refSetRemove(VMID paramVMID)
/*     */   {
/* 307 */     DGCImpl.getDGCImpl().unregisterTarget(paramVMID, this);
/*     */ 
/* 309 */     if ((this.refSet.removeElement(paramVMID)) && (this.refSet.isEmpty()))
/*     */     {
/* 312 */       if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
/* 313 */         DGCImpl.dgcLog.log(Log.VERBOSE, "reference set is empty: target = " + this);
/*     */       }
/*     */ 
/* 321 */       Remote localRemote = getImpl();
/* 322 */       if ((localRemote instanceof Unreferenced)) {
/* 323 */         final Unreferenced localUnreferenced = (Unreferenced)localRemote;
/* 324 */         final Thread localThread = (Thread)AccessController.doPrivileged(new NewThreadAction(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 328 */             localUnreferenced.unreferenced();
/*     */           }
/*     */         }
/*     */         , "Unreferenced-" + nextThreadNum++, false, true));
/*     */ 
/* 336 */         AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Void run() {
/* 339 */             localThread.setContextClassLoader(Target.this.ccl);
/* 340 */             return null;
/*     */           }
/*     */         });
/* 344 */         localThread.start();
/*     */       }
/*     */ 
/* 347 */       unpinImpl();
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized boolean unexport(boolean paramBoolean)
/*     */   {
/* 360 */     if ((paramBoolean == true) || (this.callCount == 0) || (this.disp == null)) {
/* 361 */       this.disp = null;
/*     */ 
/* 367 */       unpinImpl();
/* 368 */       DGCImpl localDGCImpl = DGCImpl.getDGCImpl();
/* 369 */       Enumeration localEnumeration = this.refSet.elements();
/* 370 */       while (localEnumeration.hasMoreElements()) {
/* 371 */         VMID localVMID = (VMID)localEnumeration.nextElement();
/* 372 */         localDGCImpl.unregisterTarget(localVMID, this);
/*     */       }
/* 374 */       return true;
/*     */     }
/* 376 */     return false;
/*     */   }
/*     */ 
/*     */   synchronized void markRemoved()
/*     */   {
/* 384 */     if (this.removed) throw new AssertionError();
/*     */ 
/* 386 */     this.removed = true;
/* 387 */     if ((!this.permanent) && (this.callCount == 0)) {
/* 388 */       ObjectTable.decrementKeepAliveCount();
/*     */     }
/*     */ 
/* 391 */     if (this.exportedTransport != null)
/* 392 */       this.exportedTransport.targetUnexported();
/*     */   }
/*     */ 
/*     */   synchronized void incrementCallCount()
/*     */     throws NoSuchObjectException
/*     */   {
/* 401 */     if (this.disp != null)
/* 402 */       this.callCount += 1;
/*     */     else
/* 404 */       throw new NoSuchObjectException("object not accepting new calls");
/*     */   }
/*     */ 
/*     */   synchronized void decrementCallCount()
/*     */   {
/* 413 */     if (--this.callCount < 0) {
/* 414 */       throw new Error("internal error: call count less than zero");
/*     */     }
/*     */ 
/* 425 */     if ((!this.permanent) && (this.removed) && (this.callCount == 0))
/* 426 */       ObjectTable.decrementKeepAliveCount();
/*     */   }
/*     */ 
/*     */   boolean isEmpty()
/*     */   {
/* 435 */     return this.refSet.isEmpty();
/*     */   }
/*     */ 
/*     */   public synchronized void vmidDead(VMID paramVMID)
/*     */   {
/* 444 */     if (DGCImpl.dgcLog.isLoggable(Log.BRIEF)) {
/* 445 */       DGCImpl.dgcLog.log(Log.BRIEF, "removing endpoint " + paramVMID + " from reference set");
/*     */     }
/*     */ 
/* 449 */     this.sequenceTable.remove(paramVMID);
/* 450 */     refSetRemove(paramVMID);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.Target
 * JD-Core Version:    0.6.2
 */