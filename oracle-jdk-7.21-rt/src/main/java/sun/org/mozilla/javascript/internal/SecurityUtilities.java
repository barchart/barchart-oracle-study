/*     */ package sun.org.mozilla.javascript.internal;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProtectionDomain;
/*     */ 
/*     */ public class SecurityUtilities
/*     */ {
/*     */   public static String getSystemProperty(String paramString)
/*     */   {
/*  59 */     return (String)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public String run()
/*     */       {
/*  64 */         return System.getProperty(this.val$name);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static ProtectionDomain getProtectionDomain(Class<?> paramClass)
/*     */   {
/*  71 */     return (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public ProtectionDomain run()
/*     */       {
/*  76 */         return this.val$clazz.getProtectionDomain();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static ProtectionDomain getScriptProtectionDomain()
/*     */   {
/*  89 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  90 */     if ((localSecurityManager instanceof RhinoSecurityManager)) {
/*  91 */       return (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public ProtectionDomain run() {
/*  94 */           Class localClass = ((RhinoSecurityManager)this.val$securityManager).getCurrentScriptClass();
/*     */ 
/*  96 */           return localClass == null ? null : localClass.getProtectionDomain();
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/* 101 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.SecurityUtilities
 * JD-Core Version:    0.6.2
 */