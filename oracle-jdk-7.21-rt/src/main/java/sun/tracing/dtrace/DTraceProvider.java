/*     */ package sun.tracing.dtrace;
/*     */ 
/*     */ import com.sun.tracing.ProbeName;
/*     */ import com.sun.tracing.Provider;
/*     */ import com.sun.tracing.dtrace.Attributes;
/*     */ import com.sun.tracing.dtrace.DependencyClass;
/*     */ import com.sun.tracing.dtrace.FunctionName;
/*     */ import com.sun.tracing.dtrace.ModuleName;
/*     */ import com.sun.tracing.dtrace.StabilityLevel;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import sun.misc.ProxyGenerator;
/*     */ import sun.tracing.ProbeSkeleton;
/*     */ import sun.tracing.ProviderSkeleton;
/*     */ 
/*     */ class DTraceProvider extends ProviderSkeleton
/*     */ {
/*     */   private Activation activation;
/*     */   private Object proxy;
/*  56 */   private static final Class[] constructorParams = { InvocationHandler.class };
/*  57 */   private final String proxyClassNamePrefix = "$DTraceTracingProxy";
/*     */   static final String DEFAULT_MODULE = "java_tracing";
/*     */   static final String DEFAULT_FUNCTION = "unspecified";
/*  62 */   private static long nextUniqueNumber = 0L;
/*     */ 
/*  64 */   private static synchronized long getUniqueNumber() { return nextUniqueNumber++; }
/*     */ 
/*     */   protected ProbeSkeleton createProbe(Method paramMethod)
/*     */   {
/*  68 */     return new DTraceProbe(this.proxy, paramMethod);
/*     */   }
/*     */ 
/*     */   DTraceProvider(Class<? extends Provider> paramClass) {
/*  72 */     super(paramClass);
/*     */   }
/*     */ 
/*     */   void setProxy(Object paramObject) {
/*  76 */     this.proxy = paramObject;
/*     */   }
/*     */ 
/*     */   void setActivation(Activation paramActivation) {
/*  80 */     this.activation = paramActivation;
/*     */   }
/*     */ 
/*     */   public void dispose() {
/*  84 */     if (this.activation != null) {
/*  85 */       this.activation.disposeProvider(this);
/*  86 */       this.activation = null;
/*     */     }
/*  88 */     super.dispose();
/*     */   }
/*     */ 
/*     */   public <T extends Provider> T newProxyInstance()
/*     */   {
/* 105 */     long l = getUniqueNumber();
/*     */ 
/* 107 */     String str1 = "";
/* 108 */     if (!Modifier.isPublic(this.providerType.getModifiers())) {
/* 109 */       str2 = this.providerType.getName();
/* 110 */       int i = str2.lastIndexOf('.');
/* 111 */       str1 = i == -1 ? "" : str2.substring(0, i + 1);
/*     */     }
/*     */ 
/* 114 */     String str2 = str1 + "$DTraceTracingProxy" + l;
/*     */ 
/* 119 */     Class localClass = null;
/* 120 */     byte[] arrayOfByte = ProxyGenerator.generateProxyClass(str2, new Class[] { this.providerType });
/*     */     try
/*     */     {
/* 123 */       localClass = JVM.defineClass(this.providerType.getClassLoader(), str2, arrayOfByte, 0, arrayOfByte.length);
/*     */     }
/*     */     catch (ClassFormatError localClassFormatError)
/*     */     {
/* 134 */       throw new IllegalArgumentException(localClassFormatError.toString());
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 141 */       Constructor localConstructor = localClass.getConstructor(constructorParams);
/* 142 */       return (Provider)localConstructor.newInstance(new Object[] { this });
/*     */     } catch (NoSuchMethodException localNoSuchMethodException) {
/* 144 */       throw new InternalError(localNoSuchMethodException.toString());
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 146 */       throw new InternalError(localIllegalAccessException.toString());
/*     */     } catch (InstantiationException localInstantiationException) {
/* 148 */       throw new InternalError(localInstantiationException.toString());
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 150 */       throw new InternalError(localInvocationTargetException.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
/*     */   {
/* 164 */     if (paramMethod.getDeclaringClass() != this.providerType)
/*     */       try {
/* 166 */         return paramMethod.invoke(this, paramArrayOfObject);
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/* 168 */         if (!$assertionsDisabled) throw new AssertionError(); 
/*     */       }
/* 170 */       catch (InvocationTargetException localInvocationTargetException) { if (!$assertionsDisabled) throw new AssertionError();
/*     */       }
/* 172 */     else if ((this.active) && 
/* 173 */       (!$assertionsDisabled)) throw new AssertionError("This method should have been overridden by the JVM");
/*     */ 
/* 175 */     return null;
/*     */   }
/*     */ 
/*     */   public String getProviderName() {
/* 179 */     return super.getProviderName();
/*     */   }
/*     */ 
/*     */   String getModuleName() {
/* 183 */     return getAnnotationString(this.providerType, ModuleName.class, "java_tracing");
/*     */   }
/*     */ 
/*     */   static String getProbeName(Method paramMethod)
/*     */   {
/* 188 */     return getAnnotationString(paramMethod, ProbeName.class, paramMethod.getName());
/*     */   }
/*     */ 
/*     */   static String getFunctionName(Method paramMethod)
/*     */   {
/* 193 */     return getAnnotationString(paramMethod, FunctionName.class, "unspecified");
/*     */   }
/*     */ 
/*     */   DTraceProbe[] getProbes()
/*     */   {
/* 198 */     return (DTraceProbe[])this.probes.values().toArray(new DTraceProbe[0]);
/*     */   }
/*     */ 
/*     */   StabilityLevel getNameStabilityFor(Class<? extends Annotation> paramClass) {
/* 202 */     Attributes localAttributes = (Attributes)getAnnotationValue(this.providerType, paramClass, "value", null);
/*     */ 
/* 204 */     if (localAttributes == null) {
/* 205 */       return StabilityLevel.PRIVATE;
/*     */     }
/* 207 */     return localAttributes.name();
/*     */   }
/*     */ 
/*     */   StabilityLevel getDataStabilityFor(Class<? extends Annotation> paramClass)
/*     */   {
/* 212 */     Attributes localAttributes = (Attributes)getAnnotationValue(this.providerType, paramClass, "value", null);
/*     */ 
/* 214 */     if (localAttributes == null) {
/* 215 */       return StabilityLevel.PRIVATE;
/*     */     }
/* 217 */     return localAttributes.data();
/*     */   }
/*     */ 
/*     */   DependencyClass getDependencyClassFor(Class<? extends Annotation> paramClass)
/*     */   {
/* 222 */     Attributes localAttributes = (Attributes)getAnnotationValue(this.providerType, paramClass, "value", null);
/*     */ 
/* 224 */     if (localAttributes == null) {
/* 225 */       return DependencyClass.UNKNOWN;
/*     */     }
/* 227 */     return localAttributes.dependency();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.tracing.dtrace.DTraceProvider
 * JD-Core Version:    0.6.2
 */