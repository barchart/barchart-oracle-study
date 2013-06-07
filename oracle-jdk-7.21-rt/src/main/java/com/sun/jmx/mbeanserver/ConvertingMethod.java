/*     */ package com.sun.jmx.mbeanserver;
/*     */ 
/*     */ import java.io.InvalidObjectException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import javax.management.Descriptor;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.openmbean.OpenDataException;
/*     */ import javax.management.openmbean.OpenType;
/*     */ 
/*     */ final class ConvertingMethod
/*     */ {
/* 222 */   private static final String[] noStrings = new String[0];
/*     */   private final Method method;
/*     */   private final MXBeanMapping returnMapping;
/*     */   private final MXBeanMapping[] paramMappings;
/*     */   private final boolean paramConversionIsIdentity;
/*     */ 
/*     */   static ConvertingMethod from(Method paramMethod)
/*     */   {
/*     */     try
/*     */     {
/*  40 */       return new ConvertingMethod(paramMethod);
/*     */     } catch (OpenDataException localOpenDataException) {
/*  42 */       String str = "Method " + paramMethod.getDeclaringClass().getName() + "." + paramMethod.getName() + " has parameter or return type that " + "cannot be translated into an open type";
/*     */ 
/*  45 */       throw new IllegalArgumentException(str, localOpenDataException);
/*     */     }
/*     */   }
/*     */ 
/*     */   Method getMethod() {
/*  50 */     return this.method;
/*     */   }
/*     */ 
/*     */   Descriptor getDescriptor() {
/*  54 */     return Introspector.descriptorForElement(this.method);
/*     */   }
/*     */ 
/*     */   Type getGenericReturnType() {
/*  58 */     return this.method.getGenericReturnType();
/*     */   }
/*     */ 
/*     */   Type[] getGenericParameterTypes() {
/*  62 */     return this.method.getGenericParameterTypes();
/*     */   }
/*     */ 
/*     */   String getName() {
/*  66 */     return this.method.getName();
/*     */   }
/*     */ 
/*     */   OpenType<?> getOpenReturnType() {
/*  70 */     return this.returnMapping.getOpenType();
/*     */   }
/*     */ 
/*     */   OpenType<?>[] getOpenParameterTypes() {
/*  74 */     OpenType[] arrayOfOpenType = new OpenType[this.paramMappings.length];
/*  75 */     for (int i = 0; i < this.paramMappings.length; i++)
/*  76 */       arrayOfOpenType[i] = this.paramMappings[i].getOpenType();
/*  77 */     return arrayOfOpenType;
/*     */   }
/*     */ 
/*     */   void checkCallFromOpen()
/*     */   {
/*     */     try
/*     */     {
/*  90 */       for (MXBeanMapping localMXBeanMapping : this.paramMappings)
/*  91 */         localMXBeanMapping.checkReconstructible();
/*     */     } catch (InvalidObjectException localInvalidObjectException) {
/*  93 */       throw new IllegalArgumentException(localInvalidObjectException);
/*     */     }
/*     */   }
/*     */ 
/*     */   void checkCallToOpen()
/*     */   {
/*     */     try
/*     */     {
/* 107 */       this.returnMapping.checkReconstructible();
/*     */     } catch (InvalidObjectException localInvalidObjectException) {
/* 109 */       throw new IllegalArgumentException(localInvalidObjectException);
/*     */     }
/*     */   }
/*     */ 
/*     */   String[] getOpenSignature() {
/* 114 */     if (this.paramMappings.length == 0) {
/* 115 */       return noStrings;
/*     */     }
/* 117 */     String[] arrayOfString = new String[this.paramMappings.length];
/* 118 */     for (int i = 0; i < this.paramMappings.length; i++)
/* 119 */       arrayOfString[i] = this.paramMappings[i].getOpenClass().getName();
/* 120 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   final Object toOpenReturnValue(MXBeanLookup paramMXBeanLookup, Object paramObject) throws OpenDataException
/*     */   {
/* 125 */     return this.returnMapping.toOpenValue(paramObject);
/*     */   }
/*     */ 
/*     */   final Object fromOpenReturnValue(MXBeanLookup paramMXBeanLookup, Object paramObject) throws InvalidObjectException
/*     */   {
/* 130 */     return this.returnMapping.fromOpenValue(paramObject);
/*     */   }
/*     */ 
/*     */   final Object[] toOpenParameters(MXBeanLookup paramMXBeanLookup, Object[] paramArrayOfObject) throws OpenDataException
/*     */   {
/* 135 */     if ((this.paramConversionIsIdentity) || (paramArrayOfObject == null))
/* 136 */       return paramArrayOfObject;
/* 137 */     Object[] arrayOfObject = new Object[paramArrayOfObject.length];
/* 138 */     for (int i = 0; i < paramArrayOfObject.length; i++)
/* 139 */       arrayOfObject[i] = this.paramMappings[i].toOpenValue(paramArrayOfObject[i]);
/* 140 */     return arrayOfObject;
/*     */   }
/*     */ 
/*     */   final Object[] fromOpenParameters(Object[] paramArrayOfObject) throws InvalidObjectException
/*     */   {
/* 145 */     if ((this.paramConversionIsIdentity) || (paramArrayOfObject == null))
/* 146 */       return paramArrayOfObject;
/* 147 */     Object[] arrayOfObject = new Object[paramArrayOfObject.length];
/* 148 */     for (int i = 0; i < paramArrayOfObject.length; i++)
/* 149 */       arrayOfObject[i] = this.paramMappings[i].fromOpenValue(paramArrayOfObject[i]);
/* 150 */     return arrayOfObject;
/*     */   }
/*     */ 
/*     */   final Object toOpenParameter(MXBeanLookup paramMXBeanLookup, Object paramObject, int paramInt)
/*     */     throws OpenDataException
/*     */   {
/* 157 */     return this.paramMappings[paramInt].toOpenValue(paramObject);
/*     */   }
/*     */ 
/*     */   final Object fromOpenParameter(MXBeanLookup paramMXBeanLookup, Object paramObject, int paramInt)
/*     */     throws InvalidObjectException
/*     */   {
/* 164 */     return this.paramMappings[paramInt].fromOpenValue(paramObject);
/*     */   }
/*     */ 
/*     */   Object invokeWithOpenReturn(MXBeanLookup paramMXBeanLookup, Object paramObject, Object[] paramArrayOfObject)
/*     */     throws MBeanException, IllegalAccessException, InvocationTargetException
/*     */   {
/* 171 */     MXBeanLookup localMXBeanLookup = MXBeanLookup.getLookup();
/*     */     try {
/* 173 */       MXBeanLookup.setLookup(paramMXBeanLookup);
/* 174 */       return invokeWithOpenReturn(paramObject, paramArrayOfObject);
/*     */     } finally {
/* 176 */       MXBeanLookup.setLookup(localMXBeanLookup);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object invokeWithOpenReturn(Object paramObject, Object[] paramArrayOfObject) throws MBeanException, IllegalAccessException, InvocationTargetException
/*     */   {
/*     */     Object[] arrayOfObject;
/*     */     try
/*     */     {
/* 185 */       arrayOfObject = fromOpenParameters(paramArrayOfObject);
/*     */     }
/*     */     catch (InvalidObjectException localInvalidObjectException) {
/* 188 */       String str1 = methodName() + ": cannot convert parameters " + "from open values: " + localInvalidObjectException;
/*     */ 
/* 190 */       throw new MBeanException(localInvalidObjectException, str1);
/*     */     }
/* 192 */     Object localObject = this.method.invoke(paramObject, arrayOfObject);
/*     */     try {
/* 194 */       return this.returnMapping.toOpenValue(localObject);
/*     */     }
/*     */     catch (OpenDataException localOpenDataException) {
/* 197 */       String str2 = methodName() + ": cannot convert return " + "value to open value: " + localOpenDataException;
/*     */ 
/* 199 */       throw new MBeanException(localOpenDataException, str2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String methodName() {
/* 204 */     return this.method.getDeclaringClass() + "." + this.method.getName();
/*     */   }
/*     */ 
/*     */   private ConvertingMethod(Method paramMethod) throws OpenDataException {
/* 208 */     this.method = paramMethod;
/* 209 */     MXBeanMappingFactory localMXBeanMappingFactory = MXBeanMappingFactory.DEFAULT;
/* 210 */     this.returnMapping = localMXBeanMappingFactory.mappingForType(paramMethod.getGenericReturnType(), localMXBeanMappingFactory);
/*     */ 
/* 212 */     Type[] arrayOfType = paramMethod.getGenericParameterTypes();
/* 213 */     this.paramMappings = new MXBeanMapping[arrayOfType.length];
/* 214 */     boolean bool = true;
/* 215 */     for (int i = 0; i < arrayOfType.length; i++) {
/* 216 */       this.paramMappings[i] = localMXBeanMappingFactory.mappingForType(arrayOfType[i], localMXBeanMappingFactory);
/* 217 */       bool &= DefaultMXBeanMappingFactory.isIdentity(this.paramMappings[i]);
/*     */     }
/* 219 */     this.paramConversionIsIdentity = bool;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.ConvertingMethod
 * JD-Core Version:    0.6.2
 */