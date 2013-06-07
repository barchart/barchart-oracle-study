/*     */ package java.beans;
/*     */ 
/*     */ class PrimitivePersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 103 */     return paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 107 */     return new Expression(paramObject, paramObject.getClass(), "new", new Object[] { paramObject.toString() });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.PrimitivePersistenceDelegate
 * JD-Core Version:    0.6.2
 */