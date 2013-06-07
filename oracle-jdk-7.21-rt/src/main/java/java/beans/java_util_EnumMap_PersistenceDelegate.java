/*     */ package java.beans;
/*     */ 
/*     */ import java.util.EnumMap;
/*     */ 
/*     */ class java_util_EnumMap_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 574 */     return (super.mutatesTo(paramObject1, paramObject2)) && (getType(paramObject1) == getType(paramObject2));
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 578 */     return new Expression(paramObject, EnumMap.class, "new", new Object[] { getType(paramObject) });
/*     */   }
/*     */ 
/*     */   private static Object getType(Object paramObject) {
/* 582 */     return MetaData.getPrivateFieldValue(paramObject, "java.util.EnumMap.keyType");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_util_EnumMap_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */