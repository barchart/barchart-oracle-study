/*     */ package java.security.cert;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ 
/*     */ public class CollectionCertStoreParameters
/*     */   implements CertStoreParameters
/*     */ {
/*     */   private Collection<?> coll;
/*     */ 
/*     */   public CollectionCertStoreParameters(Collection<?> paramCollection)
/*     */   {
/*  87 */     if (paramCollection == null)
/*  88 */       throw new NullPointerException();
/*  89 */     this.coll = paramCollection;
/*     */   }
/*     */ 
/*     */   public CollectionCertStoreParameters()
/*     */   {
/*  98 */     this.coll = Collections.EMPTY_SET;
/*     */   }
/*     */ 
/*     */   public Collection<?> getCollection()
/*     */   {
/* 111 */     return this.coll;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 122 */       return super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 125 */       throw new InternalError(localCloneNotSupportedException.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 135 */     StringBuffer localStringBuffer = new StringBuffer();
/* 136 */     localStringBuffer.append("CollectionCertStoreParameters: [\n");
/* 137 */     localStringBuffer.append("  collection: " + this.coll + "\n");
/* 138 */     localStringBuffer.append("]");
/* 139 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.cert.CollectionCertStoreParameters
 * JD-Core Version:    0.6.2
 */