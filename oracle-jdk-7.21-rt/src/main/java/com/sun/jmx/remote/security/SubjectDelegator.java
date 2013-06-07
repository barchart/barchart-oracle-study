/*     */ package com.sun.jmx.remote.security;
/*     */ 
/*     */ import com.sun.jmx.remote.util.CacheMap;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.Principal;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Set;
/*     */ import javax.management.remote.SubjectDelegationPermission;
/*     */ import javax.security.auth.Subject;
/*     */ 
/*     */ public class SubjectDelegator
/*     */ {
/*     */   private static final int PRINCIPALS_CACHE_SIZE = 10;
/*     */   private static final int ACC_CACHE_SIZE = 10;
/*     */   private CacheMap<Subject, Principal[]> principalsCache;
/*     */   private CacheMap<Subject, AccessControlContext> accCache;
/*     */ 
/*     */   public synchronized AccessControlContext delegatedContext(AccessControlContext paramAccessControlContext, Subject paramSubject, boolean paramBoolean)
/*     */     throws SecurityException
/*     */   {
/*  56 */     if ((this.principalsCache == null) || (this.accCache == null)) {
/*  57 */       this.principalsCache = new CacheMap(10);
/*     */ 
/*  59 */       this.accCache = new CacheMap(10);
/*     */     }
/*     */ 
/*  66 */     Principal[] arrayOfPrincipal1 = (Principal[])this.principalsCache.get(paramSubject);
/*     */ 
/*  72 */     if (arrayOfPrincipal1 == null) {
/*  73 */       arrayOfPrincipal1 = (Principal[])paramSubject.getPrincipals().toArray(new Principal[0]);
/*     */ 
/*  75 */       this.principalsCache.put(paramSubject, arrayOfPrincipal1);
/*     */     }
/*     */ 
/*  81 */     AccessControlContext localAccessControlContext = (AccessControlContext)this.accCache.get(paramSubject);
/*     */ 
/*  87 */     if (localAccessControlContext == null) {
/*  88 */       if (paramBoolean) {
/*  89 */         localAccessControlContext = JMXSubjectDomainCombiner.getDomainCombinerContext(paramSubject);
/*     */       }
/*     */       else
/*     */       {
/*  93 */         localAccessControlContext = JMXSubjectDomainCombiner.getContext(paramSubject);
/*     */       }
/*     */ 
/*  96 */       this.accCache.put(paramSubject, localAccessControlContext);
/*     */     }
/*     */ 
/* 103 */     final Principal[] arrayOfPrincipal2 = arrayOfPrincipal1;
/* 104 */     PrivilegedAction local1 = new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 107 */         for (int i = 0; i < arrayOfPrincipal2.length; i++) {
/* 108 */           String str = arrayOfPrincipal2[i].getClass().getName() + "." + arrayOfPrincipal2[i].getName();
/*     */ 
/* 110 */           SubjectDelegationPermission localSubjectDelegationPermission = new SubjectDelegationPermission(str);
/*     */ 
/* 112 */           AccessController.checkPermission(localSubjectDelegationPermission);
/*     */         }
/* 114 */         return null;
/*     */       }
/*     */     };
/* 117 */     AccessController.doPrivileged(local1, paramAccessControlContext);
/*     */ 
/* 119 */     return localAccessControlContext;
/*     */   }
/*     */ 
/*     */   public static synchronized boolean checkRemoveCallerContext(Subject paramSubject)
/*     */   {
/*     */     try
/*     */     {
/* 134 */       Principal[] arrayOfPrincipal = (Principal[])paramSubject.getPrincipals().toArray(new Principal[0]);
/*     */ 
/* 136 */       for (int i = 0; i < arrayOfPrincipal.length; i++) {
/* 137 */         String str = arrayOfPrincipal[i].getClass().getName() + "." + arrayOfPrincipal[i].getName();
/*     */ 
/* 139 */         SubjectDelegationPermission localSubjectDelegationPermission = new SubjectDelegationPermission(str);
/*     */ 
/* 141 */         AccessController.checkPermission(localSubjectDelegationPermission);
/*     */       }
/*     */     } catch (SecurityException localSecurityException) {
/* 144 */       return false;
/*     */     }
/* 146 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.remote.security.SubjectDelegator
 * JD-Core Version:    0.6.2
 */