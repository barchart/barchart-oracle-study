/*     */ package java.beans;
/*     */ 
/*     */ import com.sun.beans.finder.PrimitiveWrapperMap;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ class java_lang_Class_PersistenceDelegate extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 199 */     return paramObject1.equals(paramObject2);
/*     */   }
/*     */ 
/*     */   protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 203 */     Class localClass = (Class)paramObject;
/*     */ 
/* 207 */     if (localClass.isPrimitive()) {
/* 208 */       localObject = null;
/*     */       try {
/* 210 */         localObject = PrimitiveWrapperMap.getType(localClass.getName()).getDeclaredField("TYPE");
/*     */       } catch (NoSuchFieldException localNoSuchFieldException) {
/* 212 */         System.err.println("Unknown primitive type: " + localClass);
/*     */       }
/* 214 */       return new Expression(paramObject, localObject, "get", new Object[] { null });
/*     */     }
/* 216 */     if (paramObject == String.class) {
/* 217 */       return new Expression(paramObject, "", "getClass", new Object[0]);
/*     */     }
/* 219 */     if (paramObject == Class.class) {
/* 220 */       return new Expression(paramObject, String.class, "getClass", new Object[0]);
/*     */     }
/*     */ 
/* 223 */     Object localObject = new Expression(paramObject, Class.class, "forName", new Object[] { localClass.getName() });
/* 224 */     ((Expression)localObject).loader = localClass.getClassLoader();
/* 225 */     return localObject;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_lang_Class_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */