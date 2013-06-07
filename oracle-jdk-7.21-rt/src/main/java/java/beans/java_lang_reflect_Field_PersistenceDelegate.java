/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ class java_lang_reflect_Field_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 233 */     return paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 237 */     Field localField = (Field)paramObject;
/* 238 */     return new Expression(paramObject, localField.getDeclaringClass(), "getField", new Object[] { localField.getName() });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_lang_reflect_Field_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */