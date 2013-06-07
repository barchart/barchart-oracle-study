/*    */ package com.sun.jmx.mbeanserver;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectStreamClass;
/*    */ 
/*    */ class ObjectInputStreamWithLoader extends ObjectInputStream
/*    */ {
/*    */   private ClassLoader loader;
/*    */ 
/*    */   public ObjectInputStreamWithLoader(InputStream paramInputStream, ClassLoader paramClassLoader)
/*    */     throws IOException
/*    */   {
/* 53 */     super(paramInputStream);
/* 54 */     this.loader = paramClassLoader;
/*    */   }
/*    */ 
/*    */   protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
/*    */     throws IOException, ClassNotFoundException
/*    */   {
/* 60 */     if (this.loader == null) {
/* 61 */       return super.resolveClass(paramObjectStreamClass);
/*    */     }
/* 63 */     String str = paramObjectStreamClass.getName();
/*    */ 
/* 65 */     return Class.forName(str, false, this.loader);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.ObjectInputStreamWithLoader
 * JD-Core Version:    0.6.2
 */