/*      */ package java.lang;
/*      */ 
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ 
/*      */ class SystemClassLoaderAction
/*      */   implements PrivilegedExceptionAction<ClassLoader>
/*      */ {
/*      */   private ClassLoader parent;
/*      */ 
/*      */   SystemClassLoaderAction(ClassLoader paramClassLoader)
/*      */   {
/* 2199 */     this.parent = paramClassLoader;
/*      */   }
/*      */ 
/*      */   public ClassLoader run() throws Exception {
/* 2203 */     String str = System.getProperty("java.system.class.loader");
/* 2204 */     if (str == null) {
/* 2205 */       return this.parent;
/*      */     }
/*      */ 
/* 2208 */     Constructor localConstructor = Class.forName(str, true, this.parent).getDeclaredConstructor(new Class[] { ClassLoader.class });
/*      */ 
/* 2210 */     ClassLoader localClassLoader = (ClassLoader)localConstructor.newInstance(new Object[] { this.parent });
/*      */ 
/* 2212 */     Thread.currentThread().setContextClassLoader(localClassLoader);
/* 2213 */     return localClassLoader;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.SystemClassLoaderAction
 * JD-Core Version:    0.6.2
 */