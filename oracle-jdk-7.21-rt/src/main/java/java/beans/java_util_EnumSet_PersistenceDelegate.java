/*     */ package java.beans;
/*     */ 
/*     */ import java.util.EnumSet;
/*     */ 
/*     */ class java_util_EnumSet_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 593 */     return (super.mutatesTo(paramObject1, paramObject2)) && (getType(paramObject1) == getType(paramObject2));
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 597 */     return new Expression(paramObject, EnumSet.class, "noneOf", new Object[] { getType(paramObject) });
/*     */   }
/*     */ 
/*     */   private static Object getType(Object paramObject) {
/* 601 */     return MetaData.getPrivateFieldValue(paramObject, "java.util.EnumSet.elementType");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_util_EnumSet_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */