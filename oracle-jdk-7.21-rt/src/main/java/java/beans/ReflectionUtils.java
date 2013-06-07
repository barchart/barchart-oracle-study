/*    */ package java.beans;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ 
/*    */ class ReflectionUtils
/*    */ {
/*    */   public static boolean isPrimitive(Class paramClass)
/*    */   {
/* 36 */     return primitiveTypeFor(paramClass) != null;
/*    */   }
/*    */ 
/*    */   public static Class primitiveTypeFor(Class paramClass) {
/* 40 */     if (paramClass == Boolean.class) return Boolean.TYPE;
/* 41 */     if (paramClass == Byte.class) return Byte.TYPE;
/* 42 */     if (paramClass == Character.class) return Character.TYPE;
/* 43 */     if (paramClass == Short.class) return Short.TYPE;
/* 44 */     if (paramClass == Integer.class) return Integer.TYPE;
/* 45 */     if (paramClass == Long.class) return Long.TYPE;
/* 46 */     if (paramClass == Float.class) return Float.TYPE;
/* 47 */     if (paramClass == Double.class) return Double.TYPE;
/* 48 */     if (paramClass == Void.class) return Void.TYPE;
/* 49 */     return null;
/*    */   }
/*    */ 
/*    */   public static Object getPrivateField(Object paramObject, Class paramClass, String paramString, ExceptionListener paramExceptionListener)
/*    */   {
/*    */     try
/*    */     {
/* 64 */       Field localField = paramClass.getDeclaredField(paramString);
/* 65 */       localField.setAccessible(true);
/* 66 */       return localField.get(paramObject);
/*    */     }
/*    */     catch (Exception localException) {
/* 69 */       if (paramExceptionListener != null) {
/* 70 */         paramExceptionListener.exceptionThrown(localException);
/*    */       }
/*    */     }
/* 73 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.ReflectionUtils
 * JD-Core Version:    0.6.2
 */