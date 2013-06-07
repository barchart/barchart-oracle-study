/*    */ package com.sun.xml.internal.bind.v2.model.impl;
/*    */ 
/*    */ import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
/*    */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*    */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*    */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
/*    */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
/*    */ import java.lang.reflect.Field;
/*    */ import java.lang.reflect.Method;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Map;
/*    */ import javax.xml.namespace.QName;
/*    */ 
/*    */ final class RuntimeTypeInfoSetImpl extends TypeInfoSetImpl<Type, Class, Field, Method>
/*    */   implements RuntimeTypeInfoSet
/*    */ {
/*    */   public RuntimeTypeInfoSetImpl(AnnotationReader<Type, Class, Field, Method> reader)
/*    */   {
/* 50 */     super(Navigator.REFLECTION, reader, RuntimeBuiltinLeafInfoImpl.LEAVES);
/*    */   }
/*    */ 
/*    */   protected RuntimeNonElement createAnyType()
/*    */   {
/* 55 */     return RuntimeAnyTypeImpl.theInstance;
/*    */   }
/*    */ 
/*    */   public ReflectionNavigator getNavigator() {
/* 59 */     return (ReflectionNavigator)super.getNavigator();
/*    */   }
/*    */ 
/*    */   public RuntimeNonElement getTypeInfo(Type type) {
/* 63 */     return (RuntimeNonElement)super.getTypeInfo(type);
/*    */   }
/*    */ 
/*    */   public RuntimeNonElement getAnyTypeInfo() {
/* 67 */     return (RuntimeNonElement)super.getAnyTypeInfo();
/*    */   }
/*    */ 
/*    */   public RuntimeNonElement getClassInfo(Class clazz) {
/* 71 */     return (RuntimeNonElement)super.getClassInfo(clazz);
/*    */   }
/*    */ 
/*    */   public Map<Class, RuntimeClassInfoImpl> beans() {
/* 75 */     return super.beans();
/*    */   }
/*    */ 
/*    */   public Map<Type, RuntimeBuiltinLeafInfoImpl<?>> builtins() {
/* 79 */     return super.builtins();
/*    */   }
/*    */ 
/*    */   public Map<Class, RuntimeEnumLeafInfoImpl<?, ?>> enums() {
/* 83 */     return super.enums();
/*    */   }
/*    */ 
/*    */   public Map<Class, RuntimeArrayInfoImpl> arrays() {
/* 87 */     return super.arrays();
/*    */   }
/*    */ 
/*    */   public RuntimeElementInfoImpl getElementInfo(Class scope, QName name) {
/* 91 */     return (RuntimeElementInfoImpl)super.getElementInfo(scope, name);
/*    */   }
/*    */ 
/*    */   public Map<QName, RuntimeElementInfoImpl> getElementMappings(Class scope) {
/* 95 */     return super.getElementMappings(scope);
/*    */   }
/*    */ 
/*    */   public Iterable<RuntimeElementInfoImpl> getAllElements() {
/* 99 */     return super.getAllElements();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.impl.RuntimeTypeInfoSetImpl
 * JD-Core Version:    0.6.2
 */