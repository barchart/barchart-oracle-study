/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ final class java_sql_Timestamp_PersistenceDelegate extends java_util_Date_PersistenceDelegate
/*     */ {
/* 294 */   private static final Method getNanosMethod = getNanosMethod();
/*     */ 
/*     */   private static Method getNanosMethod() {
/*     */     try {
/* 298 */       Class localClass = Class.forName("java.sql.Timestamp", true, null);
/* 299 */       return localClass.getMethod("getNanos", new Class[0]);
/*     */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 301 */       return null;
/*     */     } catch (NoSuchMethodException localNoSuchMethodException) {
/* 303 */       throw new AssertionError(localNoSuchMethodException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int getNanos(Object paramObject)
/*     */   {
/* 311 */     if (getNanosMethod == null)
/* 312 */       throw new AssertionError("Should not get here");
/*     */     try {
/* 314 */       return ((Integer)getNanosMethod.invoke(paramObject, new Object[0])).intValue();
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 316 */       Throwable localThrowable = localInvocationTargetException.getCause();
/* 317 */       if ((localThrowable instanceof RuntimeException))
/* 318 */         throw ((RuntimeException)localThrowable);
/* 319 */       if ((localThrowable instanceof Error))
/* 320 */         throw ((Error)localThrowable);
/* 321 */       throw new AssertionError(localInvocationTargetException);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 323 */       throw new AssertionError(localIllegalAccessException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
/*     */   {
/* 329 */     int i = getNanos(paramObject1);
/* 330 */     if (i != getNanos(paramObject2))
/* 331 */       paramEncoder.writeStatement(new Statement(paramObject1, "setNanos", new Object[] { Integer.valueOf(i) }));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_sql_Timestamp_PersistenceDelegate
 * JD-Core Version:    0.6.2
 */