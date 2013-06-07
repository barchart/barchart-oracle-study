/*    */ package com.sun.corba.se.impl.presentation.rmi;
/*    */ 
/*    */ import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
/*    */ import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
/*    */ import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
/*    */ 
/*    */ public class StubFactoryFactoryProxyImpl extends StubFactoryFactoryDynamicBase
/*    */ {
/*    */   public PresentationManager.StubFactory makeDynamicStubFactory(PresentationManager paramPresentationManager, PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader)
/*    */   {
/* 36 */     return new StubFactoryProxyImpl(paramClassData, paramClassLoader);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.presentation.rmi.StubFactoryFactoryProxyImpl
 * JD-Core Version:    0.6.2
 */