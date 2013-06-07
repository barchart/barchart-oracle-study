/*    */ package com.sun.jndi.ldap;
/*    */ 
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URLClassLoader;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ 
/*    */ final class VersionHelper12 extends VersionHelper
/*    */ {
/*    */   private static final String TRUST_URL_CODEBASE_PROPERTY = "com.sun.jndi.ldap.object.trustURLCodebase";
/* 42 */   private static final String trustURLCodebase = (String)AccessController.doPrivileged(new PrivilegedAction()
/*    */   {
/*    */     public String run()
/*    */     {
/* 46 */       return System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
/*    */     }
/*    */   });
/*    */ 
/*    */   ClassLoader getURLClassLoader(String[] paramArrayOfString)
/*    */     throws MalformedURLException
/*    */   {
/* 56 */     ClassLoader localClassLoader = getContextClassLoader();
/*    */ 
/* 62 */     if ((paramArrayOfString != null) && ("true".equalsIgnoreCase(trustURLCodebase))) {
/* 63 */       return URLClassLoader.newInstance(getUrlArray(paramArrayOfString), localClassLoader);
/*    */     }
/* 65 */     return localClassLoader;
/*    */   }
/*    */ 
/*    */   Class loadClass(String paramString) throws ClassNotFoundException
/*    */   {
/* 70 */     ClassLoader localClassLoader = getContextClassLoader();
/* 71 */     return Class.forName(paramString, true, localClassLoader);
/*    */   }
/*    */ 
/*    */   private ClassLoader getContextClassLoader() {
/* 75 */     return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*    */     {
/*    */       public Object run() {
/* 78 */         return Thread.currentThread().getContextClassLoader();
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   Thread createThread(final Runnable paramRunnable)
/*    */   {
/* 85 */     return (Thread)AccessController.doPrivileged(new PrivilegedAction()
/*    */     {
/*    */       public Object run() {
/* 88 */         return new Thread(paramRunnable);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.VersionHelper12
 * JD-Core Version:    0.6.2
 */