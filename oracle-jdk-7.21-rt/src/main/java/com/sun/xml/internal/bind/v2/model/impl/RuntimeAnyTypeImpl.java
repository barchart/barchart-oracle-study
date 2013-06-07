/*    */ package com.sun.xml.internal.bind.v2.model.impl;
/*    */ 
/*    */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*    */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
/*    */ import com.sun.xml.internal.bind.v2.runtime.Transducer;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ final class RuntimeAnyTypeImpl extends AnyTypeImpl<Type, Class>
/*    */   implements RuntimeNonElement
/*    */ {
/* 46 */   static final RuntimeNonElement theInstance = new RuntimeAnyTypeImpl();
/*    */ 
/*    */   private RuntimeAnyTypeImpl()
/*    */   {
/* 39 */     super(Navigator.REFLECTION);
/*    */   }
/*    */ 
/*    */   public <V> Transducer<V> getTransducer() {
/* 43 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.impl.RuntimeAnyTypeImpl
 * JD-Core Version:    0.6.2
 */