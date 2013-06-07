/*     */ package com.sun.corba.se.impl.presentation.rmi;
/*     */ 
/*     */ import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandler;
/*     */ import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl;
/*     */ import com.sun.corba.se.spi.orbutil.proxy.DelegateInvocationHandlerImpl;
/*     */ import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
/*     */ import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
/*     */ import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
/*     */ import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
/*     */ import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
/*     */ import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Proxy;
/*     */ 
/*     */ public class InvocationHandlerFactoryImpl
/*     */   implements InvocationHandlerFactory
/*     */ {
/*     */   private final PresentationManager.ClassData classData;
/*     */   private final PresentationManager pm;
/*     */   private Class[] proxyInterfaces;
/*     */ 
/*     */   public InvocationHandlerFactoryImpl(PresentationManager paramPresentationManager, PresentationManager.ClassData paramClassData)
/*     */   {
/*  56 */     this.classData = paramClassData;
/*  57 */     this.pm = paramPresentationManager;
/*     */ 
/*  59 */     Class[] arrayOfClass = paramClassData.getIDLNameTranslator().getInterfaces();
/*     */ 
/*  61 */     this.proxyInterfaces = new Class[arrayOfClass.length + 1];
/*  62 */     for (int i = 0; i < arrayOfClass.length; i++) {
/*  63 */       this.proxyInterfaces[i] = arrayOfClass[i];
/*     */     }
/*  65 */     this.proxyInterfaces[arrayOfClass.length] = DynamicStub.class;
/*     */   }
/*     */ 
/*     */   public InvocationHandler getInvocationHandler()
/*     */   {
/* 104 */     DynamicStubImpl localDynamicStubImpl = new DynamicStubImpl(this.classData.getTypeIds());
/*     */ 
/* 107 */     return getInvocationHandler(localDynamicStubImpl);
/*     */   }
/*     */ 
/*     */   InvocationHandler getInvocationHandler(DynamicStub paramDynamicStub)
/*     */   {
/* 117 */     InvocationHandler localInvocationHandler = DelegateInvocationHandlerImpl.create(paramDynamicStub);
/*     */ 
/* 122 */     StubInvocationHandlerImpl localStubInvocationHandlerImpl = new StubInvocationHandlerImpl(this.pm, this.classData, paramDynamicStub);
/*     */ 
/* 127 */     CustomCompositeInvocationHandlerImpl localCustomCompositeInvocationHandlerImpl = new CustomCompositeInvocationHandlerImpl(paramDynamicStub);
/*     */ 
/* 129 */     localCustomCompositeInvocationHandlerImpl.addInvocationHandler(DynamicStub.class, localInvocationHandler);
/*     */ 
/* 131 */     localCustomCompositeInvocationHandlerImpl.addInvocationHandler(org.omg.CORBA.Object.class, localInvocationHandler);
/*     */ 
/* 133 */     localCustomCompositeInvocationHandlerImpl.addInvocationHandler(java.lang.Object.class, localInvocationHandler);
/*     */ 
/* 149 */     localCustomCompositeInvocationHandlerImpl.setDefaultHandler(localStubInvocationHandlerImpl);
/*     */ 
/* 151 */     return localCustomCompositeInvocationHandlerImpl;
/*     */   }
/*     */ 
/*     */   public Class[] getProxyInterfaces()
/*     */   {
/* 156 */     return this.proxyInterfaces;
/*     */   }
/*     */ 
/*     */   private class CustomCompositeInvocationHandlerImpl extends CompositeInvocationHandlerImpl
/*     */     implements LinkedInvocationHandler, Serializable
/*     */   {
/*     */     private transient DynamicStub stub;
/*     */ 
/*     */     public void setProxy(Proxy paramProxy)
/*     */     {
/*  76 */       ((DynamicStubImpl)this.stub).setSelf((DynamicStub)paramProxy);
/*     */     }
/*     */ 
/*     */     public Proxy getProxy()
/*     */     {
/*  81 */       return (Proxy)((DynamicStubImpl)this.stub).getSelf();
/*     */     }
/*     */ 
/*     */     public CustomCompositeInvocationHandlerImpl(DynamicStub arg2)
/*     */     {
/*     */       java.lang.Object localObject;
/*  86 */       this.stub = localObject;
/*     */     }
/*     */ 
/*     */     public java.lang.Object writeReplace()
/*     */       throws ObjectStreamException
/*     */     {
/*  98 */       return this.stub;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.presentation.rmi.InvocationHandlerFactoryImpl
 * JD-Core Version:    0.6.2
 */