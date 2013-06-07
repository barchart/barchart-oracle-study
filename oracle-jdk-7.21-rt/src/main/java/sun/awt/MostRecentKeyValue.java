/*     */ package sun.awt;
/*     */ 
/*     */ final class MostRecentKeyValue
/*     */ {
/*     */   Object key;
/*     */   Object value;
/*     */ 
/*     */   MostRecentKeyValue(Object paramObject1, Object paramObject2)
/*     */   {
/* 838 */     this.key = paramObject1;
/* 839 */     this.value = paramObject2;
/*     */   }
/*     */   void setPair(Object paramObject1, Object paramObject2) {
/* 842 */     this.key = paramObject1;
/* 843 */     this.value = paramObject2;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.MostRecentKeyValue
 * JD-Core Version:    0.6.2
 */