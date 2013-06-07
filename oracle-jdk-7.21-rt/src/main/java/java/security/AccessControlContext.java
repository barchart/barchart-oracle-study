/*     */ package java.security;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.SecurityConstants;
/*     */ 
/*     */ public final class AccessControlContext
/*     */ {
/*     */   private ProtectionDomain[] context;
/*     */   private boolean isPrivileged;
/*     */   private AccessControlContext privilegedContext;
/*  88 */   private DomainCombiner combiner = null;
/*     */ 
/*  90 */   private static boolean debugInit = false;
/*  91 */   private static Debug debug = null;
/*     */ 
/*     */   static Debug getDebug()
/*     */   {
/*  95 */     if (debugInit) {
/*  96 */       return debug;
/*     */     }
/*  98 */     if (Policy.isSet()) {
/*  99 */       debug = Debug.getInstance("access");
/* 100 */       debugInit = true;
/*     */     }
/* 102 */     return debug;
/*     */   }
/*     */ 
/*     */   public AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain)
/*     */   {
/* 118 */     if (paramArrayOfProtectionDomain.length == 0) {
/* 119 */       this.context = null;
/* 120 */     } else if (paramArrayOfProtectionDomain.length == 1) {
/* 121 */       if (paramArrayOfProtectionDomain[0] != null)
/* 122 */         this.context = ((ProtectionDomain[])paramArrayOfProtectionDomain.clone());
/*     */       else
/* 124 */         this.context = null;
/*     */     }
/*     */     else {
/* 127 */       ArrayList localArrayList = new ArrayList(paramArrayOfProtectionDomain.length);
/* 128 */       for (int i = 0; i < paramArrayOfProtectionDomain.length; i++) {
/* 129 */         if ((paramArrayOfProtectionDomain[i] != null) && (!localArrayList.contains(paramArrayOfProtectionDomain[i])))
/* 130 */           localArrayList.add(paramArrayOfProtectionDomain[i]);
/*     */       }
/* 132 */       if (!localArrayList.isEmpty()) {
/* 133 */         this.context = new ProtectionDomain[localArrayList.size()];
/* 134 */         this.context = ((ProtectionDomain[])localArrayList.toArray(this.context));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public AccessControlContext(AccessControlContext paramAccessControlContext, DomainCombiner paramDomainCombiner)
/*     */   {
/* 165 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 166 */     if (localSecurityManager != null) {
/* 167 */       localSecurityManager.checkPermission(SecurityConstants.CREATE_ACC_PERMISSION);
/*     */     }
/*     */ 
/* 170 */     this.context = paramAccessControlContext.context;
/*     */ 
/* 178 */     this.combiner = paramDomainCombiner;
/*     */   }
/*     */ 
/*     */   AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain, DomainCombiner paramDomainCombiner)
/*     */   {
/* 185 */     if (paramArrayOfProtectionDomain != null) {
/* 186 */       this.context = ((ProtectionDomain[])paramArrayOfProtectionDomain.clone());
/*     */     }
/* 188 */     this.combiner = paramDomainCombiner;
/*     */   }
/*     */ 
/*     */   AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain, boolean paramBoolean)
/*     */   {
/* 198 */     this.context = paramArrayOfProtectionDomain;
/* 199 */     this.isPrivileged = paramBoolean;
/*     */   }
/*     */ 
/*     */   AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain, AccessControlContext paramAccessControlContext)
/*     */   {
/* 208 */     this.context = paramArrayOfProtectionDomain;
/* 209 */     this.privilegedContext = paramAccessControlContext;
/* 210 */     this.isPrivileged = true;
/*     */   }
/*     */ 
/*     */   ProtectionDomain[] getContext()
/*     */   {
/* 217 */     return this.context;
/*     */   }
/*     */ 
/*     */   boolean isPrivileged()
/*     */   {
/* 225 */     return this.isPrivileged;
/*     */   }
/*     */ 
/*     */   DomainCombiner getAssignedCombiner()
/*     */   {
/*     */     AccessControlContext localAccessControlContext;
/* 233 */     if (this.isPrivileged)
/* 234 */       localAccessControlContext = this.privilegedContext;
/*     */     else {
/* 236 */       localAccessControlContext = AccessController.getInheritedAccessControlContext();
/*     */     }
/* 238 */     if (localAccessControlContext != null) {
/* 239 */       return localAccessControlContext.combiner;
/*     */     }
/* 241 */     return null;
/*     */   }
/*     */ 
/*     */   public DomainCombiner getDomainCombiner()
/*     */   {
/* 261 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 262 */     if (localSecurityManager != null) {
/* 263 */       localSecurityManager.checkPermission(SecurityConstants.GET_COMBINER_PERMISSION);
/*     */     }
/* 265 */     return this.combiner;
/*     */   }
/*     */ 
/*     */   public void checkPermission(Permission paramPermission)
/*     */     throws AccessControlException
/*     */   {
/* 290 */     int i = 0;
/*     */ 
/* 292 */     if (paramPermission == null) {
/* 293 */       throw new NullPointerException("permission can't be null");
/*     */     }
/* 295 */     if (getDebug() != null)
/*     */     {
/* 297 */       i = !Debug.isOn("codebase=") ? 1 : 0;
/* 298 */       if (i == 0)
/*     */       {
/* 301 */         for (j = 0; (this.context != null) && (j < this.context.length); j++) {
/* 302 */           if ((this.context[j].getCodeSource() != null) && (this.context[j].getCodeSource().getLocation() != null) && (Debug.isOn("codebase=" + this.context[j].getCodeSource().getLocation().toString())))
/*     */           {
/* 305 */             i = 1;
/* 306 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 311 */       i &= ((!Debug.isOn("permission=")) || (Debug.isOn("permission=" + paramPermission.getClass().getCanonicalName())) ? 1 : 0);
/*     */ 
/* 314 */       if ((i != 0) && (Debug.isOn("stack"))) {
/* 315 */         Thread.currentThread(); Thread.dumpStack();
/*     */       }
/*     */ 
/* 318 */       if ((i != 0) && (Debug.isOn("domain"))) {
/* 319 */         if (this.context == null)
/* 320 */           debug.println("domain (context is null)");
/*     */         else {
/* 322 */           for (j = 0; j < this.context.length; j++) {
/* 323 */             debug.println("domain " + j + " " + this.context[j]);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 340 */     if (this.context == null) {
/* 341 */       return;
/*     */     }
/* 343 */     for (int j = 0; j < this.context.length; j++) {
/* 344 */       if ((this.context[j] != null) && (!this.context[j].implies(paramPermission))) {
/* 345 */         if (i != 0) {
/* 346 */           debug.println("access denied " + paramPermission);
/*     */         }
/*     */ 
/* 349 */         if ((Debug.isOn("failure")) && (debug != null))
/*     */         {
/* 353 */           if (i == 0) {
/* 354 */             debug.println("access denied " + paramPermission);
/*     */           }
/* 356 */           Thread.currentThread(); Thread.dumpStack();
/* 357 */           final ProtectionDomain localProtectionDomain = this.context[j];
/* 358 */           final Debug localDebug = debug;
/* 359 */           AccessController.doPrivileged(new PrivilegedAction() {
/*     */             public Void run() {
/* 361 */               localDebug.println("domain that failed " + localProtectionDomain);
/* 362 */               return null;
/*     */             }
/*     */           });
/*     */         }
/* 366 */         throw new AccessControlException("access denied " + paramPermission, paramPermission);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 371 */     if (i != 0)
/* 372 */       debug.println("access allowed " + paramPermission);
/*     */   }
/*     */ 
/*     */   AccessControlContext optimize()
/*     */   {
/*     */     AccessControlContext localAccessControlContext;
/* 385 */     if (this.isPrivileged)
/* 386 */       localAccessControlContext = this.privilegedContext;
/*     */     else {
/* 388 */       localAccessControlContext = AccessController.getInheritedAccessControlContext();
/*     */     }
/*     */ 
/* 393 */     int i = this.context == null ? 1 : 0;
/*     */ 
/* 397 */     int j = (localAccessControlContext == null) || (localAccessControlContext.context == null) ? 1 : 0;
/*     */ 
/* 399 */     if ((localAccessControlContext != null) && (localAccessControlContext.combiner != null))
/*     */     {
/* 401 */       return goCombiner(this.context, localAccessControlContext);
/*     */     }
/*     */ 
/* 406 */     if ((j != 0) && (i != 0)) {
/* 407 */       return this;
/*     */     }
/*     */ 
/* 412 */     if (i != 0) {
/* 413 */       return localAccessControlContext;
/*     */     }
/*     */ 
/* 416 */     int k = this.context.length;
/*     */ 
/* 421 */     if ((j != 0) && (k <= 2)) {
/* 422 */       return this;
/*     */     }
/*     */ 
/* 427 */     if ((k == 1) && (this.context[0] == localAccessControlContext.context[0])) {
/* 428 */       return localAccessControlContext;
/*     */     }
/*     */ 
/* 431 */     int m = j != 0 ? 0 : localAccessControlContext.context.length;
/*     */ 
/* 434 */     Object localObject = new ProtectionDomain[k + m];
/*     */ 
/* 437 */     if (j == 0) {
/* 438 */       System.arraycopy(localAccessControlContext.context, 0, localObject, 0, m);
/*     */     }
/*     */ 
/* 443 */     label236: for (int n = 0; n < this.context.length; n++) {
/* 444 */       ProtectionDomain localProtectionDomain = this.context[n];
/* 445 */       if (localProtectionDomain != null) {
/* 446 */         for (int i1 = 0; i1 < m; i1++) {
/* 447 */           if (localProtectionDomain == localObject[i1]) {
/*     */             break label236;
/*     */           }
/*     */         }
/* 451 */         localObject[(m++)] = localProtectionDomain;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 456 */     if (m != localObject.length)
/*     */     {
/* 458 */       if ((j == 0) && (m == localAccessControlContext.context.length))
/* 459 */         return localAccessControlContext;
/* 460 */       if ((j != 0) && (m == k)) {
/* 461 */         return this;
/*     */       }
/* 463 */       ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[m];
/* 464 */       System.arraycopy(localObject, 0, arrayOfProtectionDomain, 0, m);
/* 465 */       localObject = arrayOfProtectionDomain;
/*     */     }
/*     */ 
/* 472 */     this.context = ((ProtectionDomain[])localObject);
/* 473 */     this.combiner = null;
/* 474 */     this.isPrivileged = false;
/*     */ 
/* 476 */     return this;
/*     */   }
/*     */ 
/*     */   private AccessControlContext goCombiner(ProtectionDomain[] paramArrayOfProtectionDomain, AccessControlContext paramAccessControlContext)
/*     */   {
/* 487 */     if (getDebug() != null) {
/* 488 */       debug.println("AccessControlContext invoking the Combiner");
/*     */     }
/*     */ 
/* 493 */     ProtectionDomain[] arrayOfProtectionDomain = paramAccessControlContext.combiner.combine(paramArrayOfProtectionDomain, paramAccessControlContext.context);
/*     */ 
/* 499 */     this.context = arrayOfProtectionDomain;
/* 500 */     this.combiner = paramAccessControlContext.combiner;
/* 501 */     this.isPrivileged = false;
/*     */ 
/* 503 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 517 */     if (paramObject == this) {
/* 518 */       return true;
/*     */     }
/* 520 */     if (!(paramObject instanceof AccessControlContext)) {
/* 521 */       return false;
/*     */     }
/* 523 */     AccessControlContext localAccessControlContext = (AccessControlContext)paramObject;
/*     */ 
/* 526 */     if (this.context == null) {
/* 527 */       return localAccessControlContext.context == null;
/*     */     }
/*     */ 
/* 530 */     if (localAccessControlContext.context == null) {
/* 531 */       return false;
/*     */     }
/* 533 */     if ((!containsAllPDs(localAccessControlContext)) || (!localAccessControlContext.containsAllPDs(this))) {
/* 534 */       return false;
/*     */     }
/* 536 */     if (this.combiner == null) {
/* 537 */       return localAccessControlContext.combiner == null;
/*     */     }
/* 539 */     if (localAccessControlContext.combiner == null) {
/* 540 */       return false;
/*     */     }
/* 542 */     if (!this.combiner.equals(localAccessControlContext.combiner)) {
/* 543 */       return false;
/*     */     }
/* 545 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean containsAllPDs(AccessControlContext paramAccessControlContext) {
/* 549 */     boolean bool = false;
/*     */ 
/* 557 */     for (int i = 0; i < this.context.length; i++) {
/* 558 */       bool = false;
/*     */       ProtectionDomain localProtectionDomain1;
/* 559 */       if ((localProtectionDomain1 = this.context[i]) == null) {
/* 560 */         for (int j = 0; (j < paramAccessControlContext.context.length) && (!bool); j++)
/* 561 */           bool = paramAccessControlContext.context[j] == null;
/*     */       }
/*     */       else {
/* 564 */         Class localClass = localProtectionDomain1.getClass();
/*     */ 
/* 566 */         for (int k = 0; (k < paramAccessControlContext.context.length) && (!bool); k++) {
/* 567 */           ProtectionDomain localProtectionDomain2 = paramAccessControlContext.context[k];
/*     */ 
/* 570 */           bool = (localProtectionDomain2 != null) && (localClass == localProtectionDomain2.getClass()) && (localProtectionDomain1.equals(localProtectionDomain2));
/*     */         }
/*     */       }
/*     */ 
/* 574 */       if (!bool) return false;
/*     */     }
/* 576 */     return bool;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 587 */     int i = 0;
/*     */ 
/* 589 */     if (this.context == null) {
/* 590 */       return i;
/*     */     }
/* 592 */     for (int j = 0; j < this.context.length; j++) {
/* 593 */       if (this.context[j] != null)
/* 594 */         i ^= this.context[j].hashCode();
/*     */     }
/* 596 */     return i;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.AccessControlContext
 * JD-Core Version:    0.6.2
 */