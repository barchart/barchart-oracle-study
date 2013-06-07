/*     */ package java.security;
/*     */ 
/*     */ public abstract class PolicySpi
/*     */ {
/*     */   protected abstract boolean engineImplies(ProtectionDomain paramProtectionDomain, Permission paramPermission);
/*     */ 
/*     */   protected void engineRefresh()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected PermissionCollection engineGetPermissions(CodeSource paramCodeSource)
/*     */   {
/*  92 */     return Policy.UNSUPPORTED_EMPTY_COLLECTION;
/*     */   }
/*     */ 
/*     */   protected PermissionCollection engineGetPermissions(ProtectionDomain paramProtectionDomain)
/*     */   {
/* 116 */     return Policy.UNSUPPORTED_EMPTY_COLLECTION;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.PolicySpi
 * JD-Core Version:    0.6.2
 */