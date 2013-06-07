/*     */ package com.sun.script.util;
/*     */ 
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.security.AccessControlContext;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import javax.script.Invocable;
/*     */ import javax.script.ScriptException;
/*     */ 
/*     */ public class InterfaceImplementor
/*     */ {
/*     */   private Invocable engine;
/*     */ 
/*     */   public InterfaceImplementor(Invocable paramInvocable)
/*     */   {
/*  45 */     this.engine = paramInvocable;
/*     */   }
/*     */ 
/*     */   public <T> T getInterface(Object paramObject, Class<T> paramClass)
/*     */     throws ScriptException
/*     */   {
/*  82 */     if ((paramClass == null) || (!paramClass.isInterface())) {
/*  83 */       throw new IllegalArgumentException("interface Class expected");
/*     */     }
/*  85 */     if (!isImplemented(paramObject, paramClass)) {
/*  86 */       return null;
/*     */     }
/*  88 */     AccessControlContext localAccessControlContext = AccessController.getContext();
/*  89 */     return paramClass.cast(Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, new InterfaceImplementorInvocationHandler(paramObject, localAccessControlContext)));
/*     */   }
/*     */ 
/*     */   protected boolean isImplemented(Object paramObject, Class<?> paramClass)
/*     */   {
/*  95 */     return true;
/*     */   }
/*     */ 
/*     */   protected Object convertResult(Method paramMethod, Object paramObject)
/*     */     throws ScriptException
/*     */   {
/* 102 */     return paramObject;
/*     */   }
/*     */ 
/*     */   protected Object[] convertArguments(Method paramMethod, Object[] paramArrayOfObject)
/*     */     throws ScriptException
/*     */   {
/* 109 */     return paramArrayOfObject;
/*     */   }
/*     */ 
/*     */   private final class InterfaceImplementorInvocationHandler
/*     */     implements InvocationHandler
/*     */   {
/*     */     private Object thiz;
/*     */     private AccessControlContext accCtxt;
/*     */ 
/*     */     public InterfaceImplementorInvocationHandler(Object paramAccessControlContext, AccessControlContext arg3)
/*     */     {
/*  55 */       this.thiz = paramAccessControlContext;
/*     */       Object localObject;
/*  56 */       this.accCtxt = localObject;
/*     */     }
/*     */ 
/*     */     public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
/*     */       throws Throwable
/*     */     {
/*  62 */       paramArrayOfObject = InterfaceImplementor.this.convertArguments(paramMethod, paramArrayOfObject);
/*     */ 
/*  64 */       final Method localMethod = paramMethod;
/*  65 */       final Object[] arrayOfObject = paramArrayOfObject;
/*  66 */       Object localObject = AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */         public Object run() throws Exception {
/*  68 */           if (InterfaceImplementor.InterfaceImplementorInvocationHandler.this.thiz == null) {
/*  69 */             return InterfaceImplementor.this.engine.invokeFunction(localMethod.getName(), arrayOfObject);
/*     */           }
/*  71 */           return InterfaceImplementor.this.engine.invokeMethod(InterfaceImplementor.InterfaceImplementorInvocationHandler.this.thiz, localMethod.getName(), arrayOfObject);
/*     */         }
/*     */       }
/*     */       , this.accCtxt);
/*     */ 
/*  76 */       return InterfaceImplementor.this.convertResult(paramMethod, localObject);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.script.util.InterfaceImplementor
 * JD-Core Version:    0.6.2
 */