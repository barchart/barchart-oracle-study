/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ class java_lang_reflect_Method_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 248 */     return paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 252 */     Method localMethod = (Method)paramObject;
/* 253 */     return new Expression(paramObject, localMethod.getDeclaringClass(), "getMethod", new Object[] { localMethod.getName(), localMethod.getParameterTypes() });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_lang_reflect_Method_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */