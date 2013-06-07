/*     */ package javax.management;
/*     */ 
/*     */ public class StandardEmitterMBean extends StandardMBean
/*     */   implements NotificationEmitter
/*     */ {
/*     */   private final NotificationEmitter emitter;
/*     */   private final MBeanNotificationInfo[] notificationInfo;
/*     */ 
/*     */   public <T> StandardEmitterMBean(T paramT, Class<T> paramClass, NotificationEmitter paramNotificationEmitter)
/*     */   {
/* 102 */     super(paramT, paramClass, false);
/* 103 */     if (paramNotificationEmitter == null)
/* 104 */       throw new IllegalArgumentException("Null emitter");
/* 105 */     this.emitter = paramNotificationEmitter;
/* 106 */     this.notificationInfo = paramNotificationEmitter.getNotificationInfo();
/*     */   }
/*     */ 
/*     */   public <T> StandardEmitterMBean(T paramT, Class<T> paramClass, boolean paramBoolean, NotificationEmitter paramNotificationEmitter)
/*     */   {
/* 147 */     super(paramT, paramClass, paramBoolean);
/* 148 */     if (paramNotificationEmitter == null)
/* 149 */       throw new IllegalArgumentException("Null emitter");
/* 150 */     this.emitter = paramNotificationEmitter;
/* 151 */     this.notificationInfo = paramNotificationEmitter.getNotificationInfo();
/*     */   }
/*     */ 
/*     */   protected StandardEmitterMBean(Class<?> paramClass, NotificationEmitter paramNotificationEmitter)
/*     */   {
/* 187 */     super(paramClass, false);
/* 188 */     if (paramNotificationEmitter == null)
/* 189 */       throw new IllegalArgumentException("Null emitter");
/* 190 */     this.emitter = paramNotificationEmitter;
/* 191 */     this.notificationInfo = paramNotificationEmitter.getNotificationInfo();
/*     */   }
/*     */ 
/*     */   protected StandardEmitterMBean(Class<?> paramClass, boolean paramBoolean, NotificationEmitter paramNotificationEmitter)
/*     */   {
/* 230 */     super(paramClass, paramBoolean);
/* 231 */     if (paramNotificationEmitter == null)
/* 232 */       throw new IllegalArgumentException("Null emitter");
/* 233 */     this.emitter = paramNotificationEmitter;
/* 234 */     this.notificationInfo = paramNotificationEmitter.getNotificationInfo();
/*     */   }
/*     */ 
/*     */   public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException
/*     */   {
/* 239 */     this.emitter.removeNotificationListener(paramNotificationListener);
/*     */   }
/*     */ 
/*     */   public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*     */     throws ListenerNotFoundException
/*     */   {
/* 246 */     this.emitter.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
/*     */   }
/*     */ 
/*     */   public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*     */   {
/* 252 */     this.emitter.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
/*     */   }
/*     */ 
/*     */   public MBeanNotificationInfo[] getNotificationInfo() {
/* 256 */     return this.notificationInfo;
/*     */   }
/*     */ 
/*     */   public void sendNotification(Notification paramNotification)
/*     */   {
/* 274 */     if ((this.emitter instanceof NotificationBroadcasterSupport)) {
/* 275 */       ((NotificationBroadcasterSupport)this.emitter).sendNotification(paramNotification);
/*     */     } else {
/* 277 */       String str = "Cannot sendNotification when emitter is not an instance of NotificationBroadcasterSupport: " + this.emitter.getClass().getName();
/*     */ 
/* 281 */       throw new ClassCastException(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   MBeanNotificationInfo[] getNotifications(MBeanInfo paramMBeanInfo)
/*     */   {
/* 297 */     return getNotificationInfo();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.StandardEmitterMBean
 * JD-Core Version:    0.6.2
 */