/*    */ package sun.org.mozilla.javascript.internal;
/*    */ 
/*    */ public class DefiningClassLoader extends ClassLoader
/*    */   implements GeneratedClassLoader
/*    */ {
/*    */   private final ClassLoader parentLoader;
/*    */ 
/*    */   public DefiningClassLoader()
/*    */   {
/* 51 */     this.parentLoader = getClass().getClassLoader();
/*    */   }
/*    */ 
/*    */   public DefiningClassLoader(ClassLoader paramClassLoader) {
/* 55 */     this.parentLoader = paramClassLoader;
/*    */   }
/*    */ 
/*    */   public Class<?> defineClass(String paramString, byte[] paramArrayOfByte)
/*    */   {
/* 62 */     return super.defineClass(paramString, paramArrayOfByte, 0, paramArrayOfByte.length, SecurityUtilities.getProtectionDomain(getClass()));
/*    */   }
/*    */ 
/*    */   public void linkClass(Class<?> paramClass)
/*    */   {
/* 67 */     resolveClass(paramClass);
/*    */   }
/*    */ 
/*    */   public Class<?> loadClass(String paramString, boolean paramBoolean)
/*    */     throws ClassNotFoundException
/*    */   {
/* 74 */     Class localClass = findLoadedClass(paramString);
/* 75 */     if (localClass == null) {
/* 76 */       if (this.parentLoader != null)
/* 77 */         localClass = this.parentLoader.loadClass(paramString);
/*    */       else {
/* 79 */         localClass = findSystemClass(paramString);
/*    */       }
/*    */     }
/* 82 */     if (paramBoolean) {
/* 83 */       resolveClass(localClass);
/*    */     }
/* 85 */     return localClass;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.DefiningClassLoader
 * JD-Core Version:    0.6.2
 */