/*    */ package com.sun.xml.internal.bind.v2.runtime.property;
/*    */ 
/*    */ import com.sun.xml.internal.bind.api.AccessorException;
/*    */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*    */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*    */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
/*    */ import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
/*    */ import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
/*    */ import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
/*    */ 
/*    */ abstract class ArrayProperty<BeanT, ListT, ItemT> extends PropertyImpl<BeanT>
/*    */ {
/*    */   protected final Accessor<BeanT, ListT> acc;
/*    */   protected final Lister<BeanT, ListT, ItemT, Object> lister;
/*    */ 
/*    */   protected ArrayProperty(JAXBContextImpl context, RuntimePropertyInfo prop)
/*    */   {
/* 48 */     super(context, prop);
/*    */ 
/* 50 */     assert (prop.isCollection());
/* 51 */     this.lister = Lister.create(Navigator.REFLECTION.erasure(prop.getRawType()), prop.id(), prop.getAdapter());
/*    */ 
/* 53 */     assert (this.lister != null);
/* 54 */     this.acc = prop.getAccessor().optimize(context);
/* 55 */     assert (this.acc != null);
/*    */   }
/*    */ 
/*    */   public void reset(BeanT o) throws AccessorException {
/* 59 */     this.lister.reset(o, this.acc);
/*    */   }
/*    */ 
/*    */   public final String getIdValue(BeanT bean)
/*    */   {
/* 64 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.property.ArrayProperty
 * JD-Core Version:    0.6.2
 */