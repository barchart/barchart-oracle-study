/*     */ package com.sun.xml.internal.bind.v2.model.impl;
/*     */ 
/*     */ import com.sun.istack.internal.Nullable;
/*     */ import com.sun.xml.internal.bind.WhiteSpaceProcessor;
/*     */ import com.sun.xml.internal.bind.api.AccessorException;
/*     */ import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
/*     */ import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
/*     */ import com.sun.xml.internal.bind.v2.model.core.ID;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
/*     */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
/*     */ import com.sun.xml.internal.bind.v2.runtime.FilterTransducer;
/*     */ import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
/*     */ import com.sun.xml.internal.bind.v2.runtime.InlineBinaryTransducer;
/*     */ import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
/*     */ import com.sun.xml.internal.bind.v2.runtime.MimeTypedTransducer;
/*     */ import com.sun.xml.internal.bind.v2.runtime.SchemaTypeTransducer;
/*     */ import com.sun.xml.internal.bind.v2.runtime.Transducer;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Map;
/*     */ import javax.activation.MimeType;
/*     */ import javax.xml.namespace.QName;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class RuntimeModelBuilder extends ModelBuilder<Type, Class, Field, Method>
/*     */ {
/*     */ 
/*     */   @Nullable
/*     */   public final JAXBContextImpl context;
/*     */ 
/*     */   public RuntimeModelBuilder(JAXBContextImpl context, RuntimeAnnotationReader annotationReader, Map<Class, Class> subclassReplacements, String defaultNamespaceRemap)
/*     */   {
/*  78 */     super(annotationReader, Navigator.REFLECTION, subclassReplacements, defaultNamespaceRemap);
/*  79 */     this.context = context;
/*     */   }
/*     */ 
/*     */   public RuntimeNonElement getClassInfo(Class clazz, Locatable upstream)
/*     */   {
/*  84 */     return (RuntimeNonElement)super.getClassInfo(clazz, upstream);
/*     */   }
/*     */ 
/*     */   public RuntimeNonElement getClassInfo(Class clazz, boolean searchForSuperClass, Locatable upstream)
/*     */   {
/*  89 */     return (RuntimeNonElement)super.getClassInfo(clazz, searchForSuperClass, upstream);
/*     */   }
/*     */ 
/*     */   protected RuntimeEnumLeafInfoImpl createEnumLeafInfo(Class clazz, Locatable upstream)
/*     */   {
/*  94 */     return new RuntimeEnumLeafInfoImpl(this, upstream, clazz);
/*     */   }
/*     */ 
/*     */   protected RuntimeClassInfoImpl createClassInfo(Class clazz, Locatable upstream)
/*     */   {
/*  99 */     return new RuntimeClassInfoImpl(this, upstream, clazz);
/*     */   }
/*     */ 
/*     */   public RuntimeElementInfoImpl createElementInfo(RegistryInfoImpl<Type, Class, Field, Method> registryInfo, Method method) throws IllegalAnnotationException
/*     */   {
/* 104 */     return new RuntimeElementInfoImpl(this, registryInfo, method);
/*     */   }
/*     */ 
/*     */   public RuntimeArrayInfoImpl createArrayInfo(Locatable upstream, Type arrayType)
/*     */   {
/* 109 */     return new RuntimeArrayInfoImpl(this, upstream, (Class)arrayType);
/*     */   }
/*     */ 
/*     */   public ReflectionNavigator getNavigator() {
/* 113 */     return (ReflectionNavigator)this.nav;
/*     */   }
/*     */ 
/*     */   protected RuntimeTypeInfoSetImpl createTypeInfoSet()
/*     */   {
/* 118 */     return new RuntimeTypeInfoSetImpl(this.reader);
/*     */   }
/*     */ 
/*     */   public RuntimeTypeInfoSet link()
/*     */   {
/* 123 */     return (RuntimeTypeInfoSet)super.link();
/*     */   }
/*     */ 
/*     */   public static Transducer createTransducer(RuntimeNonElementRef ref)
/*     */   {
/* 135 */     Transducer t = ref.getTarget().getTransducer();
/* 136 */     RuntimePropertyInfo src = ref.getSource();
/* 137 */     ID id = src.id();
/*     */ 
/* 139 */     if (id == ID.IDREF) {
/* 140 */       return RuntimeBuiltinLeafInfoImpl.STRING;
/*     */     }
/* 142 */     if (id == ID.ID) {
/* 143 */       t = new IDTransducerImpl(t);
/*     */     }
/* 145 */     MimeType emt = src.getExpectedMimeType();
/* 146 */     if (emt != null) {
/* 147 */       t = new MimeTypedTransducer(t, emt);
/*     */     }
/* 149 */     if (src.inlineBinaryData()) {
/* 150 */       t = new InlineBinaryTransducer(t);
/*     */     }
/* 152 */     if (src.getSchemaType() != null) {
/* 153 */       if (src.getSchemaType().equals(createXSSimpleType())) {
/* 154 */         return RuntimeBuiltinLeafInfoImpl.STRING;
/*     */       }
/* 156 */       t = new SchemaTypeTransducer(t, src.getSchemaType());
/*     */     }
/*     */ 
/* 159 */     return t;
/*     */   }
/*     */ 
/*     */   private static QName createXSSimpleType() {
/* 163 */     return new QName("http://www.w3.org/2001/XMLSchema", "anySimpleType");
/*     */   }
/*     */ 
/*     */   private static final class IDTransducerImpl<ValueT> extends FilterTransducer<ValueT>
/*     */   {
/*     */     public IDTransducerImpl(Transducer<ValueT> core)
/*     */     {
/* 174 */       super();
/*     */     }
/*     */ 
/*     */     public ValueT parse(CharSequence lexical) throws AccessorException, SAXException
/*     */     {
/* 179 */       String value = WhiteSpaceProcessor.trim(lexical).toString();
/* 180 */       UnmarshallingContext.getInstance().addToIdTable(value);
/* 181 */       return this.core.parse(value);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.impl.RuntimeModelBuilder
 * JD-Core Version:    0.6.2
 */