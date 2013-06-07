/*     */ package com.sun.xml.internal.bind.v2.model.impl;
/*     */ 
/*     */ import com.sun.xml.internal.bind.v2.model.core.Adapter;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
/*     */ import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
/*     */ import com.sun.xml.internal.bind.v2.runtime.Transducer;
/*     */ import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.xml.bind.JAXBElement;
/*     */ import javax.xml.bind.annotation.adapters.XmlAdapter;
/*     */ 
/*     */ final class RuntimeElementInfoImpl extends ElementInfoImpl<Type, Class, Field, Method>
/*     */   implements RuntimeElementInfo
/*     */ {
/*     */   private final Class<? extends XmlAdapter> adapterType;
/*     */ 
/*     */   public RuntimeElementInfoImpl(RuntimeModelBuilder modelBuilder, RegistryInfoImpl registry, Method method)
/*     */     throws IllegalAnnotationException
/*     */   {
/*  57 */     super(modelBuilder, registry, method);
/*     */ 
/*  59 */     Adapter a = getProperty().getAdapter();
/*     */ 
/*  61 */     if (a != null)
/*  62 */       this.adapterType = ((Class)a.adapterType);
/*     */     else
/*  64 */       this.adapterType = null;
/*     */   }
/*     */ 
/*     */   protected ElementInfoImpl<Type, Class, Field, Method>.PropertyImpl createPropertyImpl()
/*     */   {
/*  69 */     return new RuntimePropertyImpl();
/*     */   }
/*     */ 
/*     */   public RuntimeElementPropertyInfo getProperty()
/*     */   {
/* 121 */     return (RuntimeElementPropertyInfo)super.getProperty();
/*     */   }
/*     */ 
/*     */   public Class<? extends JAXBElement> getType() {
/* 125 */     return Navigator.REFLECTION.erasure((Type)super.getType());
/*     */   }
/*     */ 
/*     */   public RuntimeClassInfo getScope() {
/* 129 */     return (RuntimeClassInfo)super.getScope();
/*     */   }
/*     */ 
/*     */   public RuntimeNonElement getContentType() {
/* 133 */     return (RuntimeNonElement)super.getContentType();
/*     */   }
/*     */ 
/*     */   class RuntimePropertyImpl extends ElementInfoImpl<Type, Class, Field, Method>.PropertyImpl
/*     */     implements RuntimeElementPropertyInfo, RuntimeTypeRef
/*     */   {
/*     */     RuntimePropertyImpl()
/*     */     {
/*  72 */       super();
/*     */     }
/*  74 */     public Accessor getAccessor() { if (RuntimeElementInfoImpl.this.adapterType == null) {
/*  75 */         return Accessor.JAXB_ELEMENT_VALUE;
/*     */       }
/*  77 */       return Accessor.JAXB_ELEMENT_VALUE.adapt((Class)getAdapter().defaultType, RuntimeElementInfoImpl.this.adapterType);
/*     */     }
/*     */ 
/*     */     public Type getRawType()
/*     */     {
/*  82 */       return Collection.class;
/*     */     }
/*     */ 
/*     */     public Type getIndividualType() {
/*  86 */       return (Type)RuntimeElementInfoImpl.this.getContentType().getType();
/*     */     }
/*     */ 
/*     */     public boolean elementOnlyContent()
/*     */     {
/*  91 */       return false;
/*     */     }
/*     */ 
/*     */     public List<? extends RuntimeTypeRef> getTypes() {
/*  95 */       return Collections.singletonList(this);
/*     */     }
/*     */ 
/*     */     public List<? extends RuntimeNonElement> ref() {
/*  99 */       return super.ref();
/*     */     }
/*     */ 
/*     */     public RuntimeNonElement getTarget() {
/* 103 */       return (RuntimeNonElement)super.getTarget();
/*     */     }
/*     */ 
/*     */     public RuntimePropertyInfo getSource() {
/* 107 */       return this;
/*     */     }
/*     */ 
/*     */     public Transducer getTransducer() {
/* 111 */       return RuntimeModelBuilder.createTransducer(this);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.impl.RuntimeElementInfoImpl
 * JD-Core Version:    0.6.2
 */