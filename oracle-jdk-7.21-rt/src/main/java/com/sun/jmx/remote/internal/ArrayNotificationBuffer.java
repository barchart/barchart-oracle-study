/*     */ package com.sun.jmx.remote.internal;
/*     */ 
/*     */ import com.sun.jmx.remote.util.ClassLogger;
/*     */ import com.sun.jmx.remote.util.EnvHelp;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.management.InstanceNotFoundException;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.MBeanServerDelegate;
/*     */ import javax.management.MBeanServerNotification;
/*     */ import javax.management.Notification;
/*     */ import javax.management.NotificationBroadcaster;
/*     */ import javax.management.NotificationFilter;
/*     */ import javax.management.NotificationFilterSupport;
/*     */ import javax.management.NotificationListener;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.QueryEval;
/*     */ import javax.management.QueryExp;
/*     */ import javax.management.remote.NotificationResult;
/*     */ import javax.management.remote.TargetedNotification;
/*     */ 
/*     */ public class ArrayNotificationBuffer
/*     */   implements NotificationBuffer
/*     */ {
/* 112 */   private boolean disposed = false;
/*     */ 
/* 116 */   private static final Object globalLock = new Object();
/*     */ 
/* 118 */   private static final HashMap<MBeanServer, ArrayNotificationBuffer> mbsToBuffer = new HashMap(1);
/*     */ 
/* 120 */   private final Collection<ShareBuffer> sharers = new HashSet(1);
/*     */ 
/* 761 */   private final NotificationListener bufferListener = new BufferListener(null);
/*     */ 
/* 772 */   private static final QueryExp broadcasterQuery = new BroadcasterQuery(null);
/*     */ 
/* 778 */   private static final NotificationFilter creationFilter = localNotificationFilterSupport;
/*     */ 
/* 781 */   private final NotificationListener creationListener = new NotificationListener()
/*     */   {
/*     */     public void handleNotification(Notification paramAnonymousNotification, Object paramAnonymousObject)
/*     */     {
/* 785 */       ArrayNotificationBuffer.logger.debug("creationListener", "handleNotification called");
/* 786 */       ArrayNotificationBuffer.this.createdNotification((MBeanServerNotification)paramAnonymousNotification);
/*     */     }
/* 781 */   };
/*     */ 
/* 825 */   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ArrayNotificationBuffer");
/*     */   private final MBeanServer mBeanServer;
/*     */   private final ArrayQueue<NamedNotification> queue;
/*     */   private int queueSize;
/*     */   private long earliestSequenceNumber;
/*     */   private long nextSequenceNumber;
/*     */   private Set<ObjectName> createdDuringQuery;
/* 836 */   static final String broadcasterClass = NotificationBroadcaster.class.getName();
/*     */ 
/*     */   public static NotificationBuffer getNotificationBuffer(MBeanServer paramMBeanServer, Map<String, ?> paramMap)
/*     */   {
/* 125 */     if (paramMap == null) {
/* 126 */       paramMap = Collections.emptyMap();
/*     */     }
/*     */ 
/* 129 */     int i = EnvHelp.getNotifBufferSize(paramMap);
/*     */     ArrayNotificationBuffer localArrayNotificationBuffer;
/*     */     int j;
/*     */     ShareBuffer localShareBuffer;
/* 134 */     synchronized (globalLock) {
/* 135 */       localArrayNotificationBuffer = (ArrayNotificationBuffer)mbsToBuffer.get(paramMBeanServer);
/* 136 */       j = localArrayNotificationBuffer == null ? 1 : 0;
/* 137 */       if (j != 0) {
/* 138 */         localArrayNotificationBuffer = new ArrayNotificationBuffer(paramMBeanServer, i);
/* 139 */         mbsToBuffer.put(paramMBeanServer, localArrayNotificationBuffer);
/*     */       }
/*     */       ArrayNotificationBuffer tmp71_70 = localArrayNotificationBuffer; tmp71_70.getClass(); localShareBuffer = new ShareBuffer(i);
/*     */     }
/*     */ 
/* 152 */     if (j != 0)
/* 153 */       localArrayNotificationBuffer.createListeners();
/* 154 */     return localShareBuffer;
/*     */   }
/*     */ 
/*     */   static void removeNotificationBuffer(MBeanServer paramMBeanServer)
/*     */   {
/* 162 */     synchronized (globalLock) {
/* 163 */       mbsToBuffer.remove(paramMBeanServer);
/*     */     }
/*     */   }
/*     */ 
/*     */   void addSharer(ShareBuffer paramShareBuffer) {
/* 168 */     synchronized (globalLock) {
/* 169 */       synchronized (this) {
/* 170 */         if (paramShareBuffer.getSize() > this.queueSize)
/* 171 */           resize(paramShareBuffer.getSize());
/*     */       }
/* 173 */       this.sharers.add(paramShareBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void removeSharer(ShareBuffer paramShareBuffer)
/*     */   {
/*     */     boolean bool;
/* 179 */     synchronized (globalLock) {
/* 180 */       this.sharers.remove(paramShareBuffer);
/* 181 */       bool = this.sharers.isEmpty();
/* 182 */       if (bool) {
/* 183 */         removeNotificationBuffer(this.mBeanServer);
/*     */       } else {
/* 185 */         int i = 0;
/* 186 */         for (ShareBuffer localShareBuffer : this.sharers) {
/* 187 */           int j = localShareBuffer.getSize();
/* 188 */           if (j > i)
/* 189 */             i = j;
/*     */         }
/* 191 */         if (i < this.queueSize)
/* 192 */           resize(i);
/*     */       }
/*     */     }
/* 195 */     if (bool) {
/* 196 */       synchronized (this) {
/* 197 */         this.disposed = true;
/*     */ 
/* 199 */         notifyAll();
/*     */       }
/* 201 */       destroyListeners();
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void resize(int paramInt) {
/* 206 */     if (paramInt == this.queueSize)
/* 207 */       return;
/* 208 */     while (this.queue.size() > paramInt)
/* 209 */       dropNotification();
/* 210 */     this.queue.resize(paramInt);
/* 211 */     this.queueSize = paramInt;
/*     */   }
/*     */ 
/*     */   private ArrayNotificationBuffer(MBeanServer paramMBeanServer, int paramInt)
/*     */   {
/* 246 */     if (logger.traceOn()) {
/* 247 */       logger.trace("Constructor", "queueSize=" + paramInt);
/*     */     }
/* 249 */     if ((paramMBeanServer == null) || (paramInt < 1)) {
/* 250 */       throw new IllegalArgumentException("Bad args");
/*     */     }
/* 252 */     this.mBeanServer = paramMBeanServer;
/* 253 */     this.queueSize = paramInt;
/* 254 */     this.queue = new ArrayQueue(paramInt);
/* 255 */     this.earliestSequenceNumber = System.currentTimeMillis();
/* 256 */     this.nextSequenceNumber = this.earliestSequenceNumber;
/*     */ 
/* 258 */     logger.trace("Constructor", "ends");
/*     */   }
/*     */ 
/*     */   private synchronized boolean isDisposed() {
/* 262 */     return this.disposed;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 269 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public NotificationResult fetchNotifications(NotificationBufferFilter paramNotificationBufferFilter, long paramLong1, long paramLong2, int paramInt)
/*     */     throws InterruptedException
/*     */   {
/* 306 */     logger.trace("fetchNotifications", "starts");
/*     */ 
/* 308 */     if ((paramLong1 < 0L) || (isDisposed())) {
/* 309 */       synchronized (this) {
/* 310 */         return new NotificationResult(earliestSequenceNumber(), nextSequenceNumber(), new TargetedNotification[0]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 317 */     if ((paramNotificationBufferFilter == null) || (paramLong1 < 0L) || (paramLong2 < 0L) || (paramInt < 0))
/*     */     {
/* 320 */       logger.trace("fetchNotifications", "Bad args");
/* 321 */       throw new IllegalArgumentException("Bad args to fetch");
/*     */     }
/*     */ 
/* 324 */     if (logger.debugOn()) {
/* 325 */       logger.trace("fetchNotifications", "filter=" + paramNotificationBufferFilter + "; startSeq=" + paramLong1 + "; timeout=" + paramLong2 + "; max=" + paramInt);
/*     */     }
/*     */ 
/* 331 */     if (paramLong1 > nextSequenceNumber()) {
/* 332 */       ??? = "Start sequence number too big: " + paramLong1 + " > " + nextSequenceNumber();
/*     */ 
/* 334 */       logger.trace("fetchNotifications", (String)???);
/* 335 */       throw new IllegalArgumentException((String)???);
/*     */     }
/*     */ 
/* 343 */     long l1 = System.currentTimeMillis() + paramLong2;
/* 344 */     if (l1 < 0L) {
/* 345 */       l1 = 9223372036854775807L;
/*     */     }
/* 347 */     if (logger.debugOn()) {
/* 348 */       logger.debug("fetchNotifications", "endTime=" + l1);
/*     */     }
/*     */ 
/* 354 */     long l2 = -1L;
/* 355 */     long l3 = paramLong1;
/* 356 */     ArrayList localArrayList1 = new ArrayList();
/*     */     while (true)
/*     */     {
/* 362 */       logger.debug("fetchNotifications", "main loop starts");
/*     */       NamedNotification localNamedNotification;
/* 368 */       synchronized (this)
/*     */       {
/* 372 */         if (l2 < 0L) {
/* 373 */           l2 = earliestSequenceNumber();
/* 374 */           if (logger.debugOn()) {
/* 375 */             logger.debug("fetchNotifications", "earliestSeq=" + l2);
/*     */           }
/*     */ 
/* 378 */           if (l3 < l2) {
/* 379 */             l3 = l2;
/* 380 */             logger.debug("fetchNotifications", "nextSeq=earliestSeq");
/*     */           }
/*     */         }
/*     */         else {
/* 384 */           l2 = earliestSequenceNumber();
/*     */         }
/*     */ 
/* 391 */         if (l3 < l2) {
/* 392 */           logger.trace("fetchNotifications", "nextSeq=" + l3 + " < " + "earliestSeq=" + l2 + " so may have lost notifs");
/*     */ 
/* 395 */           break;
/*     */         }
/*     */ 
/* 398 */         if (l3 < nextSequenceNumber()) {
/* 399 */           localNamedNotification = notificationAt(l3);
/* 400 */           if (logger.debugOn()) {
/* 401 */             logger.debug("fetchNotifications", "candidate: " + localNamedNotification);
/*     */ 
/* 403 */             logger.debug("fetchNotifications", "nextSeq now " + l3);
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 411 */           if (localArrayList1.size() > 0) {
/* 412 */             logger.debug("fetchNotifications", "no more notifs but have some so don't wait");
/*     */ 
/* 414 */             break;
/*     */           }
/* 416 */           long l4 = l1 - System.currentTimeMillis();
/* 417 */           if (l4 <= 0L) {
/* 418 */             logger.debug("fetchNotifications", "timeout");
/* 419 */             break;
/*     */           }
/*     */ 
/* 423 */           if (isDisposed()) {
/* 424 */             if (logger.debugOn()) {
/* 425 */               logger.debug("fetchNotifications", "dispose callled, no wait");
/*     */             }
/* 427 */             return new NotificationResult(earliestSequenceNumber(), nextSequenceNumber(), new TargetedNotification[0]);
/*     */           }
/*     */ 
/* 432 */           if (logger.debugOn()) {
/* 433 */             logger.debug("fetchNotifications", "wait(" + l4 + ")");
/*     */           }
/* 435 */           wait(l4);
/*     */ 
/* 437 */           continue;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 446 */       ??? = localNamedNotification.getObjectName();
/* 447 */       localObject2 = localNamedNotification.getNotification();
/* 448 */       ArrayList localArrayList2 = new ArrayList();
/*     */ 
/* 450 */       logger.debug("fetchNotifications", "applying filter to candidate");
/*     */ 
/* 452 */       paramNotificationBufferFilter.apply(localArrayList2, (ObjectName)???, (Notification)localObject2);
/*     */ 
/* 454 */       if (localArrayList2.size() > 0)
/*     */       {
/* 460 */         if (paramInt <= 0) {
/* 461 */           logger.debug("fetchNotifications", "reached maxNotifications");
/*     */ 
/* 463 */           break;
/*     */         }
/* 465 */         paramInt--;
/* 466 */         if (logger.debugOn()) {
/* 467 */           logger.debug("fetchNotifications", "add: " + localArrayList2);
/*     */         }
/* 469 */         localArrayList1.addAll(localArrayList2);
/*     */       }
/*     */ 
/* 472 */       l3 += 1L;
/*     */     }
/*     */ 
/* 476 */     int i = localArrayList1.size();
/* 477 */     ??? = new TargetedNotification[i];
/*     */ 
/* 479 */     localArrayList1.toArray((Object[])???);
/* 480 */     Object localObject2 = new NotificationResult(l2, l3, (TargetedNotification[])???);
/*     */ 
/* 482 */     if (logger.debugOn())
/* 483 */       logger.debug("fetchNotifications", ((NotificationResult)localObject2).toString());
/* 484 */     logger.trace("fetchNotifications", "ends");
/*     */ 
/* 486 */     return localObject2;
/*     */   }
/*     */ 
/*     */   synchronized long earliestSequenceNumber() {
/* 490 */     return this.earliestSequenceNumber;
/*     */   }
/*     */ 
/*     */   synchronized long nextSequenceNumber() {
/* 494 */     return this.nextSequenceNumber;
/*     */   }
/*     */ 
/*     */   synchronized void addNotification(NamedNotification paramNamedNotification) {
/* 498 */     if (logger.traceOn()) {
/* 499 */       logger.trace("addNotification", paramNamedNotification.toString());
/*     */     }
/* 501 */     while (this.queue.size() >= this.queueSize) {
/* 502 */       dropNotification();
/* 503 */       if (logger.debugOn()) {
/* 504 */         logger.debug("addNotification", "dropped oldest notif, earliestSeq=" + this.earliestSequenceNumber);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 509 */     this.queue.add(paramNamedNotification);
/* 510 */     this.nextSequenceNumber += 1L;
/* 511 */     if (logger.debugOn())
/* 512 */       logger.debug("addNotification", "nextSeq=" + this.nextSequenceNumber);
/* 513 */     notifyAll();
/*     */   }
/*     */ 
/*     */   private void dropNotification() {
/* 517 */     this.queue.remove(0);
/* 518 */     this.earliestSequenceNumber += 1L;
/*     */   }
/*     */ 
/*     */   synchronized NamedNotification notificationAt(long paramLong) {
/* 522 */     long l = paramLong - this.earliestSequenceNumber;
/* 523 */     if ((l < 0L) || (l > 2147483647L)) {
/* 524 */       String str = "Bad sequence number: " + paramLong + " (earliest " + this.earliestSequenceNumber + ")";
/*     */ 
/* 526 */       logger.trace("notificationAt", str);
/* 527 */       throw new IllegalArgumentException(str);
/*     */     }
/* 529 */     return (NamedNotification)this.queue.get((int)l);
/*     */   }
/*     */ 
/*     */   private void createListeners()
/*     */   {
/* 587 */     logger.debug("createListeners", "starts");
/*     */ 
/* 589 */     synchronized (this) {
/* 590 */       this.createdDuringQuery = new HashSet();
/*     */     }
/*     */     Object localObject3;
/*     */     try {
/* 594 */       addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener, creationFilter, null);
/*     */ 
/* 596 */       logger.debug("createListeners", "added creationListener");
/*     */     }
/*     */     catch (Exception localException) {
/* 599 */       localObject3 = new IllegalArgumentException("Can't add listener to MBean server delegate: " + localException);
/* 600 */       EnvHelp.initCause((Throwable)localObject3, localException);
/* 601 */       logger.fine("createListeners", "Can't add listener to MBean server delegate: " + localException);
/* 602 */       logger.debug("createListeners", localException);
/* 603 */       throw ((Throwable)localObject3);
/*     */     }
/*     */ 
/* 608 */     Object localObject1 = queryNames(null, broadcasterQuery);
/* 609 */     localObject1 = new HashSet((Collection)localObject1);
/*     */ 
/* 611 */     synchronized (this) {
/* 612 */       ((Set)localObject1).addAll(this.createdDuringQuery);
/* 613 */       this.createdDuringQuery = null;
/*     */     }
/*     */ 
/* 616 */     for (??? = ((Set)localObject1).iterator(); ((Iterator)???).hasNext(); ) { localObject3 = (ObjectName)((Iterator)???).next();
/* 617 */       addBufferListener((ObjectName)localObject3); }
/* 618 */     logger.debug("createListeners", "ends");
/*     */   }
/*     */ 
/*     */   private void addBufferListener(ObjectName paramObjectName) {
/* 622 */     checkNoLocks();
/* 623 */     if (logger.debugOn())
/* 624 */       logger.debug("addBufferListener", paramObjectName.toString());
/*     */     try {
/* 626 */       addNotificationListener(paramObjectName, this.bufferListener, null, paramObjectName);
/*     */     } catch (Exception localException) {
/* 628 */       logger.trace("addBufferListener", localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void removeBufferListener(ObjectName paramObjectName)
/*     */   {
/* 636 */     checkNoLocks();
/* 637 */     if (logger.debugOn())
/* 638 */       logger.debug("removeBufferListener", paramObjectName.toString());
/*     */     try {
/* 640 */       removeNotificationListener(paramObjectName, this.bufferListener);
/*     */     } catch (Exception localException) {
/* 642 */       logger.trace("removeBufferListener", localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addNotificationListener(final ObjectName paramObjectName, final NotificationListener paramNotificationListener, final NotificationFilter paramNotificationFilter, final Object paramObject)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 652 */       AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */         public Void run() throws InstanceNotFoundException {
/* 654 */           ArrayNotificationBuffer.this.mBeanServer.addNotificationListener(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject);
/*     */ 
/* 658 */           return null;
/*     */         } } );
/*     */     }
/*     */     catch (Exception localException) {
/* 662 */       throw extractException(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void removeNotificationListener(final ObjectName paramObjectName, final NotificationListener paramNotificationListener) throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 670 */       AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */         public Void run() throws Exception {
/* 672 */           ArrayNotificationBuffer.this.mBeanServer.removeNotificationListener(paramObjectName, paramNotificationListener);
/* 673 */           return null;
/*     */         } } );
/*     */     }
/*     */     catch (Exception localException) {
/* 677 */       throw extractException(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Set<ObjectName> queryNames(final ObjectName paramObjectName, final QueryExp paramQueryExp)
/*     */   {
/* 683 */     PrivilegedAction local3 = new PrivilegedAction()
/*     */     {
/*     */       public Set<ObjectName> run() {
/* 686 */         return ArrayNotificationBuffer.this.mBeanServer.queryNames(paramObjectName, paramQueryExp);
/*     */       }
/*     */     };
/*     */     try {
/* 690 */       return (Set)AccessController.doPrivileged(local3);
/*     */     } catch (RuntimeException localRuntimeException) {
/* 692 */       logger.fine("queryNames", "Failed to query names: " + localRuntimeException);
/* 693 */       logger.debug("queryNames", localRuntimeException);
/* 694 */       throw localRuntimeException;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isInstanceOf(MBeanServer paramMBeanServer, final ObjectName paramObjectName, final String paramString)
/*     */   {
/* 701 */     PrivilegedExceptionAction local4 = new PrivilegedExceptionAction()
/*     */     {
/*     */       public Boolean run() throws InstanceNotFoundException {
/* 704 */         return Boolean.valueOf(this.val$mbs.isInstanceOf(paramObjectName, paramString));
/*     */       }
/*     */     };
/*     */     try {
/* 708 */       return ((Boolean)AccessController.doPrivileged(local4)).booleanValue();
/*     */     } catch (Exception localException) {
/* 710 */       logger.fine("isInstanceOf", "failed: " + localException);
/* 711 */       logger.debug("isInstanceOf", localException);
/* 712 */     }return false;
/*     */   }
/*     */ 
/*     */   private void createdNotification(MBeanServerNotification paramMBeanServerNotification)
/*     */   {
/* 727 */     if (!paramMBeanServerNotification.getType().equals("JMX.mbean.registered")) {
/* 728 */       logger.warning("createNotification", "bad type: " + paramMBeanServerNotification.getType());
/* 729 */       return;
/*     */     }
/*     */ 
/* 732 */     ObjectName localObjectName = paramMBeanServerNotification.getMBeanName();
/* 733 */     if (logger.debugOn()) {
/* 734 */       logger.debug("createdNotification", "for: " + localObjectName);
/*     */     }
/* 736 */     synchronized (this) {
/* 737 */       if (this.createdDuringQuery != null) {
/* 738 */         this.createdDuringQuery.add(localObjectName);
/* 739 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 743 */     if (isInstanceOf(this.mBeanServer, localObjectName, broadcasterClass)) {
/* 744 */       addBufferListener(localObjectName);
/* 745 */       if (isDisposed())
/* 746 */         removeBufferListener(localObjectName);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void destroyListeners()
/*     */   {
/* 791 */     checkNoLocks();
/* 792 */     logger.debug("destroyListeners", "starts");
/*     */     try {
/* 794 */       removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener);
/*     */     }
/*     */     catch (Exception localException) {
/* 797 */       logger.warning("remove listener from MBeanServer delegate", localException);
/*     */     }
/* 799 */     Set localSet = queryNames(null, broadcasterQuery);
/* 800 */     for (ObjectName localObjectName : localSet) {
/* 801 */       if (logger.debugOn()) {
/* 802 */         logger.debug("destroyListeners", "remove listener from " + localObjectName);
/*     */       }
/* 804 */       removeBufferListener(localObjectName);
/*     */     }
/* 806 */     logger.debug("destroyListeners", "ends");
/*     */   }
/*     */ 
/*     */   private void checkNoLocks() {
/* 810 */     if ((Thread.holdsLock(this)) || (Thread.holdsLock(globalLock)))
/* 811 */       logger.warning("checkNoLocks", "lock protocol violation");
/*     */   }
/*     */ 
/*     */   private static Exception extractException(Exception paramException)
/*     */   {
/* 819 */     while ((paramException instanceof PrivilegedActionException)) {
/* 820 */       paramException = ((PrivilegedActionException)paramException).getException();
/*     */     }
/* 822 */     return paramException;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 776 */     NotificationFilterSupport localNotificationFilterSupport = new NotificationFilterSupport();
/* 777 */     localNotificationFilterSupport.enableType("JMX.mbean.registered");
/*     */   }
/*     */ 
/*     */   private static class BroadcasterQuery extends QueryEval
/*     */     implements QueryExp
/*     */   {
/*     */     private static final long serialVersionUID = 7378487660587592048L;
/*     */ 
/*     */     public boolean apply(ObjectName paramObjectName)
/*     */     {
/* 768 */       MBeanServer localMBeanServer = QueryEval.getMBeanServer();
/* 769 */       return ArrayNotificationBuffer.isInstanceOf(localMBeanServer, paramObjectName, ArrayNotificationBuffer.broadcasterClass);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class BufferListener
/*     */     implements NotificationListener
/*     */   {
/*     */     private BufferListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void handleNotification(Notification paramNotification, Object paramObject)
/*     */     {
/* 752 */       if (ArrayNotificationBuffer.logger.debugOn()) {
/* 753 */         ArrayNotificationBuffer.logger.debug("BufferListener.handleNotification", "notif=" + paramNotification + "; handback=" + paramObject);
/*     */       }
/*     */ 
/* 756 */       ObjectName localObjectName = (ObjectName)paramObject;
/* 757 */       ArrayNotificationBuffer.this.addNotification(new ArrayNotificationBuffer.NamedNotification(localObjectName, paramNotification));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class NamedNotification
/*     */   {
/*     */     private final ObjectName sender;
/*     */     private final Notification notification;
/*     */ 
/*     */     NamedNotification(ObjectName paramObjectName, Notification paramNotification)
/*     */     {
/* 534 */       this.sender = paramObjectName;
/* 535 */       this.notification = paramNotification;
/*     */     }
/*     */ 
/*     */     ObjectName getObjectName() {
/* 539 */       return this.sender;
/*     */     }
/*     */ 
/*     */     Notification getNotification() {
/* 543 */       return this.notification;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 547 */       return "NamedNotification(" + this.sender + ", " + this.notification + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ShareBuffer
/*     */     implements NotificationBuffer
/*     */   {
/*     */     private final int size;
/*     */ 
/*     */     ShareBuffer(int arg2)
/*     */     {
/*     */       int i;
/* 216 */       this.size = i;
/* 217 */       ArrayNotificationBuffer.this.addSharer(this);
/*     */     }
/*     */ 
/*     */     public NotificationResult fetchNotifications(NotificationBufferFilter paramNotificationBufferFilter, long paramLong1, long paramLong2, int paramInt)
/*     */       throws InterruptedException
/*     */     {
/* 226 */       ArrayNotificationBuffer localArrayNotificationBuffer = ArrayNotificationBuffer.this;
/* 227 */       return localArrayNotificationBuffer.fetchNotifications(paramNotificationBufferFilter, paramLong1, paramLong2, paramInt);
/*     */     }
/*     */ 
/*     */     public void dispose()
/*     */     {
/* 232 */       ArrayNotificationBuffer.this.removeSharer(this);
/*     */     }
/*     */ 
/*     */     int getSize() {
/* 236 */       return this.size;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.remote.internal.ArrayNotificationBuffer
 * JD-Core Version:    0.6.2
 */