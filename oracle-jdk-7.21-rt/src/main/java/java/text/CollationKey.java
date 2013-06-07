/*     */ package java.text;
/*     */ 
/*     */ public abstract class CollationKey
/*     */   implements Comparable<CollationKey>
/*     */ {
/*     */   private final String source;
/*     */ 
/*     */   public abstract int compareTo(CollationKey paramCollationKey);
/*     */ 
/*     */   public String getSourceString()
/*     */   {
/* 117 */     return this.source;
/*     */   }
/*     */ 
/*     */   public abstract byte[] toByteArray();
/*     */ 
/*     */   protected CollationKey(String paramString)
/*     */   {
/* 138 */     if (paramString == null) {
/* 139 */       throw new NullPointerException();
/*     */     }
/* 141 */     this.source = paramString;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.text.CollationKey
 * JD-Core Version:    0.6.2
 */