/*     */ package java.security;
/*     */ 
/*     */ import sun.reflect.Reflection;
/*     */ import sun.security.util.Debug;
/*     */ 
/*     */ public final class AccessController
/*     */ {
/*     */   public static native <T> T doPrivileged(PrivilegedAction<T> paramPrivilegedAction);
/*     */ 
/*     */   public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> paramPrivilegedAction)
/*     */   {
/* 293 */     AccessControlContext localAccessControlContext = getStackAccessControlContext();
/* 294 */     if (localAccessControlContext == null) {
/* 295 */       return doPrivileged(paramPrivilegedAction);
/*     */     }
/* 297 */     DomainCombiner localDomainCombiner = localAccessControlContext.getAssignedCombiner();
/* 298 */     return doPrivileged(paramPrivilegedAction, preserveCombiner(localDomainCombiner));
/*     */   }
/*     */ 
/*     */   public static native <T> T doPrivileged(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext);
/*     */ 
/*     */   public static native <T> T doPrivileged(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction)
/*     */     throws PrivilegedActionException;
/*     */ 
/*     */   public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction)
/*     */     throws PrivilegedActionException
/*     */   {
/* 389 */     AccessControlContext localAccessControlContext = getStackAccessControlContext();
/* 390 */     if (localAccessControlContext == null) {
/* 391 */       return doPrivileged(paramPrivilegedExceptionAction);
/*     */     }
/* 393 */     DomainCombiner localDomainCombiner = localAccessControlContext.getAssignedCombiner();
/* 394 */     return doPrivileged(paramPrivilegedExceptionAction, preserveCombiner(localDomainCombiner));
/*     */   }
/*     */ 
/*     */   private static AccessControlContext preserveCombiner(DomainCombiner paramDomainCombiner)
/*     */   {
/* 409 */     Class localClass = Reflection.getCallerClass(3);
/* 410 */     ProtectionDomain localProtectionDomain = (ProtectionDomain)doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public ProtectionDomain run() {
/* 413 */         return this.val$callerClass.getProtectionDomain();
/*     */       }
/*     */     });
/* 419 */     ProtectionDomain[] arrayOfProtectionDomain = { localProtectionDomain };
/* 420 */     if (paramDomainCombiner == null) {
/* 421 */       return new AccessControlContext(arrayOfProtectionDomain);
/*     */     }
/* 423 */     return new AccessControlContext(paramDomainCombiner.combine(arrayOfProtectionDomain, null), paramDomainCombiner);
/*     */   }
/*     */ 
/*     */   public static native <T> T doPrivileged(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext)
/*     */     throws PrivilegedActionException;
/*     */ 
/*     */   private static native AccessControlContext getStackAccessControlContext();
/*     */ 
/*     */   static native AccessControlContext getInheritedAccessControlContext();
/*     */ 
/*     */   public static AccessControlContext getContext()
/*     */   {
/* 496 */     AccessControlContext localAccessControlContext = getStackAccessControlContext();
/* 497 */     if (localAccessControlContext == null)
/*     */     {
/* 500 */       return new AccessControlContext(null, true);
/*     */     }
/* 502 */     return localAccessControlContext.optimize();
/*     */   }
/*     */ 
/*     */   public static void checkPermission(Permission paramPermission)
/*     */     throws AccessControlException
/*     */   {
/* 530 */     if (paramPermission == null) {
/* 531 */       throw new NullPointerException("permission can't be null");
/*     */     }
/*     */ 
/* 534 */     AccessControlContext localAccessControlContext = getStackAccessControlContext();
/*     */ 
/* 536 */     if (localAccessControlContext == null) {
/* 537 */       localObject = AccessControlContext.getDebug();
/* 538 */       int i = 0;
/* 539 */       if (localObject != null) {
/* 540 */         i = !Debug.isOn("codebase=") ? 1 : 0;
/* 541 */         i &= ((!Debug.isOn("permission=")) || (Debug.isOn("permission=" + paramPermission.getClass().getCanonicalName())) ? 1 : 0);
/*     */       }
/*     */ 
/* 545 */       if ((i != 0) && (Debug.isOn("stack"))) {
/* 546 */         Thread.currentThread(); Thread.dumpStack();
/*     */       }
/*     */ 
/* 549 */       if ((i != 0) && (Debug.isOn("domain"))) {
/* 550 */         ((Debug)localObject).println("domain (context is null)");
/*     */       }
/*     */ 
/* 553 */       if (i != 0) {
/* 554 */         ((Debug)localObject).println("access allowed " + paramPermission);
/*     */       }
/* 556 */       return;
/*     */     }
/*     */ 
/* 559 */     Object localObject = localAccessControlContext.optimize();
/* 560 */     ((AccessControlContext)localObject).checkPermission(paramPermission);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.AccessController
 * JD-Core Version:    0.6.2
 */