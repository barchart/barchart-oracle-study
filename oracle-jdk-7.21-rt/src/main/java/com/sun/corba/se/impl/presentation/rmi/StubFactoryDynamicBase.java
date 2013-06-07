/*    */ package com.sun.corba.se.impl.presentation.rmi;
/*    */ 
/*    */ import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
/*    */ import org.omg.CORBA.Object;
/*    */ 
/*    */ public abstract class StubFactoryDynamicBase extends StubFactoryBase
/*    */ {
/*    */   protected final ClassLoader loader;
/*    */ 
/*    */   public StubFactoryDynamicBase(PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader)
/*    */   {
/* 44 */     super(paramClassData);
/*    */ 
/* 48 */     if (paramClassLoader == null) {
/* 49 */       ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/* 50 */       if (localClassLoader == null)
/* 51 */         localClassLoader = ClassLoader.getSystemClassLoader();
/* 52 */       this.loader = localClassLoader;
/*    */     } else {
/* 54 */       this.loader = paramClassLoader;
/*    */     }
/*    */   }
/*    */ 
/*    */   public abstract Object makeStub();
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.presentation.rmi.StubFactoryDynamicBase
 * JD-Core Version:    0.6.2
 */