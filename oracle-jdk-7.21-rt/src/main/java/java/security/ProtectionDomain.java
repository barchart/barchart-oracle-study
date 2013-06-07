/*     */ package java.security;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import sun.misc.JavaSecurityAccess;
/*     */ import sun.misc.JavaSecurityProtectionDomainAccess;
/*     */ import sun.misc.JavaSecurityProtectionDomainAccess.ProtectionDomainCache;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.SecurityConstants;
/*     */ 
/*     */ public class ProtectionDomain
/*     */ {
/*     */   private CodeSource codesource;
/*     */   private ClassLoader classloader;
/*     */   private Principal[] principals;
/*     */   private PermissionCollection permissions;
/* 107 */   private boolean hasAllPerm = false;
/*     */   private boolean staticPermissions;
/* 116 */   final Key key = new Key();
/*     */   private static final Debug debug;
/*     */ 
/*     */   public ProtectionDomain(CodeSource paramCodeSource, PermissionCollection paramPermissionCollection)
/*     */   {
/* 132 */     this.codesource = paramCodeSource;
/* 133 */     if (paramPermissionCollection != null) {
/* 134 */       this.permissions = paramPermissionCollection;
/* 135 */       this.permissions.setReadOnly();
/* 136 */       if (((paramPermissionCollection instanceof Permissions)) && (((Permissions)paramPermissionCollection).allPermission != null))
/*     */       {
/* 138 */         this.hasAllPerm = true;
/*     */       }
/*     */     }
/* 141 */     this.classloader = null;
/* 142 */     this.principals = new Principal[0];
/* 143 */     this.staticPermissions = true;
/*     */   }
/*     */ 
/*     */   public ProtectionDomain(CodeSource paramCodeSource, PermissionCollection paramPermissionCollection, ClassLoader paramClassLoader, Principal[] paramArrayOfPrincipal)
/*     */   {
/* 179 */     this.codesource = paramCodeSource;
/* 180 */     if (paramPermissionCollection != null) {
/* 181 */       this.permissions = paramPermissionCollection;
/* 182 */       this.permissions.setReadOnly();
/* 183 */       if (((paramPermissionCollection instanceof Permissions)) && (((Permissions)paramPermissionCollection).allPermission != null))
/*     */       {
/* 185 */         this.hasAllPerm = true;
/*     */       }
/*     */     }
/* 188 */     this.classloader = paramClassLoader;
/* 189 */     this.principals = (paramArrayOfPrincipal != null ? (Principal[])paramArrayOfPrincipal.clone() : new Principal[0]);
/*     */ 
/* 191 */     this.staticPermissions = false;
/*     */   }
/*     */ 
/*     */   public final CodeSource getCodeSource()
/*     */   {
/* 200 */     return this.codesource;
/*     */   }
/*     */ 
/*     */   public final ClassLoader getClassLoader()
/*     */   {
/* 211 */     return this.classloader;
/*     */   }
/*     */ 
/*     */   public final Principal[] getPrincipals()
/*     */   {
/* 223 */     return (Principal[])this.principals.clone();
/*     */   }
/*     */ 
/*     */   public final PermissionCollection getPermissions()
/*     */   {
/* 234 */     return this.permissions;
/*     */   }
/*     */ 
/*     */   public boolean implies(Permission paramPermission)
/*     */   {
/* 266 */     if (this.hasAllPerm)
/*     */     {
/* 269 */       return true;
/*     */     }
/*     */ 
/* 272 */     if ((!this.staticPermissions) && (Policy.getPolicyNoCheck().implies(this, paramPermission)))
/*     */     {
/* 274 */       return true;
/* 275 */     }if (this.permissions != null) {
/* 276 */       return this.permissions.implies(paramPermission);
/*     */     }
/* 278 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 285 */     String str = "<no principals>";
/* 286 */     if ((this.principals != null) && (this.principals.length > 0)) {
/* 287 */       localObject = new StringBuilder("(principals ");
/*     */ 
/* 289 */       for (int i = 0; i < this.principals.length; i++) {
/* 290 */         ((StringBuilder)localObject).append(this.principals[i].getClass().getName() + " \"" + this.principals[i].getName() + "\"");
/*     */ 
/* 293 */         if (i < this.principals.length - 1)
/* 294 */           ((StringBuilder)localObject).append(",\n");
/*     */         else
/* 296 */           ((StringBuilder)localObject).append(")\n");
/*     */       }
/* 298 */       str = ((StringBuilder)localObject).toString();
/*     */     }
/*     */ 
/* 303 */     Object localObject = (Policy.isSet()) && (seeAllp()) ? mergePermissions() : getPermissions();
/*     */ 
/* 307 */     return "ProtectionDomain  " + this.codesource + "\n" + " " + this.classloader + "\n" + " " + str + "\n" + " " + localObject + "\n";
/*     */   }
/*     */ 
/*     */   private static boolean seeAllp()
/*     */   {
/* 330 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*     */ 
/* 332 */     if (localSecurityManager == null) {
/* 333 */       return true;
/*     */     }
/* 335 */     if (debug != null) {
/* 336 */       if ((localSecurityManager.getClass().getClassLoader() == null) && (Policy.getPolicyNoCheck().getClass().getClassLoader() == null))
/*     */       {
/* 339 */         return true;
/*     */       }
/*     */     }
/*     */     else try {
/* 343 */         localSecurityManager.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
/* 344 */         return true;
/*     */       }
/*     */       catch (SecurityException localSecurityException)
/*     */       {
/*     */       }
/*     */ 
/*     */ 
/* 351 */     return false;
/*     */   }
/*     */ 
/*     */   private PermissionCollection mergePermissions() {
/* 355 */     if (this.staticPermissions) {
/* 356 */       return this.permissions;
/*     */     }
/* 358 */     PermissionCollection localPermissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public PermissionCollection run()
/*     */       {
/* 362 */         Policy localPolicy = Policy.getPolicyNoCheck();
/* 363 */         return localPolicy.getPermissions(ProtectionDomain.this);
/*     */       }
/*     */     });
/* 367 */     Permissions localPermissions = new Permissions();
/* 368 */     int i = 32;
/* 369 */     int j = 8;
/*     */ 
/* 371 */     ArrayList localArrayList1 = new ArrayList(j);
/* 372 */     ArrayList localArrayList2 = new ArrayList(i);
/*     */     Enumeration localEnumeration;
/* 376 */     if (this.permissions != null) {
/* 377 */       synchronized (this.permissions) {
/* 378 */         localEnumeration = this.permissions.elements();
/* 379 */         while (localEnumeration.hasMoreElements()) {
/* 380 */           localArrayList1.add(localEnumeration.nextElement());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 387 */     if (localPermissionCollection != null) {
/* 388 */       synchronized (localPermissionCollection) {
/* 389 */         localEnumeration = localPermissionCollection.elements();
/* 390 */         while (localEnumeration.hasMoreElements()) {
/* 391 */           localArrayList2.add(localEnumeration.nextElement());
/* 392 */           j++;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 397 */     if ((localPermissionCollection != null) && (this.permissions != null))
/*     */     {
/* 402 */       synchronized (this.permissions) {
/* 403 */         localEnumeration = this.permissions.elements();
/* 404 */         while (localEnumeration.hasMoreElements()) {
/* 405 */           Permission localPermission1 = (Permission)localEnumeration.nextElement();
/* 406 */           Class localClass = localPermission1.getClass();
/* 407 */           String str1 = localPermission1.getActions();
/* 408 */           String str2 = localPermission1.getName();
/* 409 */           for (int m = 0; m < localArrayList2.size(); m++) {
/* 410 */             Permission localPermission2 = (Permission)localArrayList2.get(m);
/* 411 */             if (localClass.isInstance(localPermission2))
/*     */             {
/* 415 */               if ((str2.equals(localPermission2.getName())) && (str1.equals(localPermission2.getActions())))
/*     */               {
/* 417 */                 localArrayList2.remove(m);
/* 418 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     int k;
/* 426 */     if (localPermissionCollection != null)
/*     */     {
/* 430 */       for (k = localArrayList2.size() - 1; k >= 0; k--) {
/* 431 */         localPermissions.add((Permission)localArrayList2.get(k));
/*     */       }
/*     */     }
/* 434 */     if (this.permissions != null) {
/* 435 */       for (k = localArrayList1.size() - 1; k >= 0; k--) {
/* 436 */         localPermissions.add((Permission)localArrayList1.get(k));
/*     */       }
/*     */     }
/*     */ 
/* 440 */     return localPermissions;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  66 */     SharedSecrets.setJavaSecurityAccess(new JavaSecurityAccess()
/*     */     {
/*     */       public <T> T doIntersectionPrivilege(PrivilegedAction<T> paramAnonymousPrivilegedAction, AccessControlContext paramAnonymousAccessControlContext1, AccessControlContext paramAnonymousAccessControlContext2)
/*     */       {
/*  73 */         if (paramAnonymousPrivilegedAction == null) {
/*  74 */           throw new NullPointerException();
/*     */         }
/*  76 */         return AccessController.doPrivileged(paramAnonymousPrivilegedAction, new AccessControlContext(paramAnonymousAccessControlContext1.getContext(), paramAnonymousAccessControlContext2).optimize());
/*     */       }
/*     */ 
/*     */       public <T> T doIntersectionPrivilege(PrivilegedAction<T> paramAnonymousPrivilegedAction, AccessControlContext paramAnonymousAccessControlContext)
/*     */       {
/*  87 */         return doIntersectionPrivilege(paramAnonymousPrivilegedAction, AccessController.getContext(), paramAnonymousAccessControlContext);
/*     */       }
/*     */     });
/* 118 */     debug = Debug.getInstance("domain");
/*     */ 
/* 449 */     SharedSecrets.setJavaSecurityProtectionDomainAccess(new JavaSecurityProtectionDomainAccess()
/*     */     {
/*     */       public JavaSecurityProtectionDomainAccess.ProtectionDomainCache getProtectionDomainCache() {
/* 452 */         return new JavaSecurityProtectionDomainAccess.ProtectionDomainCache() {
/* 453 */           private final Map<ProtectionDomain.Key, PermissionCollection> map = Collections.synchronizedMap(new WeakHashMap());
/*     */ 
/*     */           public void put(ProtectionDomain paramAnonymous2ProtectionDomain, PermissionCollection paramAnonymous2PermissionCollection)
/*     */           {
/* 458 */             this.map.put(paramAnonymous2ProtectionDomain == null ? null : paramAnonymous2ProtectionDomain.key, paramAnonymous2PermissionCollection);
/*     */           }
/*     */           public PermissionCollection get(ProtectionDomain paramAnonymous2ProtectionDomain) {
/* 461 */             return paramAnonymous2ProtectionDomain == null ? (PermissionCollection)this.map.get(null) : (PermissionCollection)this.map.get(paramAnonymous2ProtectionDomain.key);
/*     */           }
/*     */         };
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   final class Key
/*     */   {
/*     */     Key()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.ProtectionDomain
 * JD-Core Version:    0.6.2
 */