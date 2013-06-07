/*    */ package com.sun.corba.se.spi.orbutil.proxy;
/*    */ 
/*    */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CompositeInvocationHandlerImpl
/*    */   implements CompositeInvocationHandler
/*    */ {
/* 43 */   private Map classToInvocationHandler = new LinkedHashMap();
/* 44 */   private InvocationHandler defaultHandler = null;
/*    */ 
/*    */   public void addInvocationHandler(Class paramClass, InvocationHandler paramInvocationHandler)
/*    */   {
/* 49 */     this.classToInvocationHandler.put(paramClass, paramInvocationHandler);
/*    */   }
/*    */ 
/*    */   public void setDefaultHandler(InvocationHandler paramInvocationHandler)
/*    */   {
/* 54 */     this.defaultHandler = paramInvocationHandler;
/*    */   }
/*    */ 
/*    */   public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
/*    */     throws Throwable
/*    */   {
/* 62 */     Class localClass = paramMethod.getDeclaringClass();
/* 63 */     InvocationHandler localInvocationHandler = (InvocationHandler)this.classToInvocationHandler.get(localClass);
/*    */ 
/* 66 */     if (localInvocationHandler == null) {
/* 67 */       if (this.defaultHandler != null) {
/* 68 */         localInvocationHandler = this.defaultHandler;
/*    */       } else {
/* 70 */         ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get("util");
/*    */ 
/* 72 */         throw localORBUtilSystemException.noInvocationHandler("\"" + paramMethod.toString() + "\"");
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 79 */     return localInvocationHandler.invoke(paramObject, paramMethod, paramArrayOfObject);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl
 * JD-Core Version:    0.6.2
 */