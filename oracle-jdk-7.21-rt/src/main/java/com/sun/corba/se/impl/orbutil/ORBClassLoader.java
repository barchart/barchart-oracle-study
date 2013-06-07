/*    */ package com.sun.corba.se.impl.orbutil;
/*    */ 
/*    */ public class ORBClassLoader
/*    */ {
/*    */   public static Class loadClass(String paramString)
/*    */     throws ClassNotFoundException
/*    */   {
/* 38 */     return getClassLoader().loadClass(paramString);
/*    */   }
/*    */ 
/*    */   public static ClassLoader getClassLoader() {
/* 42 */     if (Thread.currentThread().getContextClassLoader() != null) {
/* 43 */       return Thread.currentThread().getContextClassLoader();
/*    */     }
/* 45 */     return ClassLoader.getSystemClassLoader();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orbutil.ORBClassLoader
 * JD-Core Version:    0.6.2
 */