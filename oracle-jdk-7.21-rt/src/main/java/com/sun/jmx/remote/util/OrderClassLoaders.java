/*    */ package com.sun.jmx.remote.util;
/*    */ 
/*    */ public class OrderClassLoaders extends ClassLoader
/*    */ {
/*    */   private ClassLoader cl2;
/*    */ 
/*    */   public OrderClassLoaders(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
/*    */   {
/* 30 */     super(paramClassLoader1);
/*    */ 
/* 32 */     this.cl2 = paramClassLoader2;
/*    */   }
/*    */ 
/*    */   protected Class<?> findClass(String paramString) throws ClassNotFoundException {
/*    */     try {
/* 37 */       return super.findClass(paramString);
/*    */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 39 */       if (this.cl2 != null) {
/* 40 */         return this.cl2.loadClass(paramString);
/*    */       }
/* 42 */       throw localClassNotFoundException;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.remote.util.OrderClassLoaders
 * JD-Core Version:    0.6.2
 */