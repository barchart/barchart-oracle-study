/*    */ package javax.management;
/*    */ 
/*    */ @Deprecated
/*    */ public class DefaultLoaderRepository
/*    */ {
/*    */   public static Class<?> loadClass(String paramString)
/*    */     throws ClassNotFoundException
/*    */   {
/* 67 */     return javax.management.loading.DefaultLoaderRepository.loadClass(paramString);
/*    */   }
/*    */ 
/*    */   public static Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString)
/*    */     throws ClassNotFoundException
/*    */   {
/* 87 */     return javax.management.loading.DefaultLoaderRepository.loadClassWithout(paramClassLoader, paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.DefaultLoaderRepository
 * JD-Core Version:    0.6.2
 */