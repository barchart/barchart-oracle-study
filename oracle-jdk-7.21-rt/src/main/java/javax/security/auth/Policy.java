/*     */ package javax.security.auth;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.CodeSource;
/*     */ import java.security.PermissionCollection;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.security.Security;
/*     */ import sun.security.util.Debug;
/*     */ import sun.security.util.ResourcesMgr;
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class Policy
/*     */ {
/*     */   private static Policy policy;
/* 170 */   private static ClassLoader contextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public ClassLoader run() {
/* 173 */       return Thread.currentThread().getContextClassLoader();
/*     */     }
/*     */   });
/*     */   private static boolean isCustomPolicy;
/*     */ 
/*     */   public static Policy getPolicy()
/*     */   {
/* 202 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 203 */     if (localSecurityManager != null) localSecurityManager.checkPermission(new AuthPermission("getPolicy"));
/* 204 */     return getPolicyNoCheck();
/*     */   }
/*     */ 
/*     */   static Policy getPolicyNoCheck()
/*     */   {
/* 214 */     if (policy == null)
/*     */     {
/* 216 */       synchronized (Policy.class)
/*     */       {
/* 218 */         if (policy == null) {
/* 219 */           String str1 = null;
/* 220 */           str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */           {
/*     */             public String run() {
/* 223 */               return Security.getProperty("auth.policy.provider");
/*     */             }
/*     */           });
/* 227 */           if (str1 == null) {
/* 228 */             str1 = "com.sun.security.auth.PolicyFile";
/*     */           }
/*     */           try
/*     */           {
/* 232 */             String str2 = str1;
/* 233 */             policy = (Policy)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */             {
/*     */               public Policy run()
/*     */                 throws ClassNotFoundException, InstantiationException, IllegalAccessException
/*     */               {
/* 238 */                 return (Policy)Class.forName(this.val$finalClass, true, Policy.contextClassLoader).newInstance();
/*     */               }
/*     */             });
/* 244 */             isCustomPolicy = !str2.equals("com.sun.security.auth.PolicyFile");
/*     */           }
/*     */           catch (Exception localException) {
/* 247 */             throw new SecurityException(ResourcesMgr.getString("unable.to.instantiate.Subject.based.policy"));
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 254 */     return policy;
/*     */   }
/*     */ 
/*     */   public static void setPolicy(Policy paramPolicy)
/*     */   {
/* 274 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 275 */     if (localSecurityManager != null) localSecurityManager.checkPermission(new AuthPermission("setPolicy"));
/* 276 */     policy = paramPolicy;
/*     */ 
/* 278 */     isCustomPolicy = paramPolicy != null;
/*     */   }
/*     */ 
/*     */   static boolean isCustomPolicySet(Debug paramDebug)
/*     */   {
/* 292 */     if (policy != null) {
/* 293 */       if ((paramDebug != null) && (isCustomPolicy)) {
/* 294 */         paramDebug.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + policy.toString());
/*     */       }
/*     */ 
/* 298 */       return isCustomPolicy;
/*     */     }
/*     */ 
/* 301 */     String str = (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run() {
/* 304 */         return Security.getProperty("auth.policy.provider");
/*     */       }
/*     */     });
/* 307 */     if ((str != null) && (!str.equals("com.sun.security.auth.PolicyFile")))
/*     */     {
/* 309 */       if (paramDebug != null) {
/* 310 */         paramDebug.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + str);
/*     */       }
/*     */ 
/* 314 */       return true;
/*     */     }
/* 316 */     return false;
/*     */   }
/*     */ 
/*     */   public abstract PermissionCollection getPermissions(Subject paramSubject, CodeSource paramCodeSource);
/*     */ 
/*     */   public abstract void refresh();
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.Policy
 * JD-Core Version:    0.6.2
 */