/*     */ package sun.tracing;
/*     */ 
/*     */ import com.sun.tracing.Probe;
/*     */ import com.sun.tracing.Provider;
/*     */ import com.sun.tracing.ProviderName;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public abstract class ProviderSkeleton
/*     */   implements InvocationHandler, Provider
/*     */ {
/*     */   protected boolean active;
/*     */   protected Class<? extends Provider> providerType;
/*     */   protected HashMap<Method, ProbeSkeleton> probes;
/*     */ 
/*     */   protected abstract ProbeSkeleton createProbe(Method paramMethod);
/*     */ 
/*     */   protected ProviderSkeleton(Class<? extends Provider> paramClass)
/*     */   {
/*  92 */     this.active = false;
/*  93 */     this.providerType = paramClass;
/*  94 */     this.probes = new HashMap();
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/* 104 */     Method[] arrayOfMethod1 = (Method[])AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Method[] run() {
/* 106 */         return ProviderSkeleton.this.providerType.getDeclaredMethods();
/*     */       }
/*     */     });
/* 110 */     for (Method localMethod : arrayOfMethod1) {
/* 111 */       if (localMethod.getReturnType() != Void.TYPE) {
/* 112 */         throw new IllegalArgumentException("Return value of method is not void");
/*     */       }
/*     */ 
/* 115 */       this.probes.put(localMethod, createProbe(localMethod));
/*     */     }
/*     */ 
/* 118 */     this.active = true;
/*     */   }
/*     */ 
/*     */   public <T extends Provider> T newProxyInstance()
/*     */   {
/* 133 */     return (Provider)Proxy.newProxyInstance(this.providerType.getClassLoader(), new Class[] { this.providerType }, this);
/*     */   }
/*     */ 
/*     */   public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
/*     */   {
/* 153 */     if (paramMethod.getDeclaringClass() != this.providerType) {
/*     */       try {
/* 155 */         return paramMethod.invoke(this, paramArrayOfObject);
/*     */       } catch (IllegalAccessException localIllegalAccessException) {
/* 157 */         if (!$assertionsDisabled) throw new AssertionError(); 
/*     */       }
/* 159 */       catch (InvocationTargetException localInvocationTargetException) { if (!$assertionsDisabled) throw new AssertionError(); 
/*     */       }
/*     */     }
/* 161 */     else if (this.active) {
/* 162 */       ProbeSkeleton localProbeSkeleton = (ProbeSkeleton)this.probes.get(paramMethod);
/* 163 */       if (localProbeSkeleton != null)
/*     */       {
/* 165 */         localProbeSkeleton.uncheckedTrigger(paramArrayOfObject);
/*     */       }
/*     */     }
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */   public Probe getProbe(Method paramMethod)
/*     */   {
/* 178 */     return this.active ? (ProbeSkeleton)this.probes.get(paramMethod) : null;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 187 */     this.active = false;
/* 188 */     this.probes.clear();
/*     */   }
/*     */ 
/*     */   protected String getProviderName()
/*     */   {
/* 199 */     return getAnnotationString(this.providerType, ProviderName.class, this.providerType.getSimpleName());
/*     */   }
/*     */ 
/*     */   protected static String getAnnotationString(AnnotatedElement paramAnnotatedElement, Class<? extends Annotation> paramClass, String paramString)
/*     */   {
/* 216 */     String str = (String)getAnnotationValue(paramAnnotatedElement, paramClass, "value", paramString);
/*     */ 
/* 218 */     return str.isEmpty() ? paramString : str;
/*     */   }
/*     */ 
/*     */   protected static Object getAnnotationValue(AnnotatedElement paramAnnotatedElement, Class<? extends Annotation> paramClass, String paramString, Object paramObject)
/*     */   {
/* 235 */     Object localObject = paramObject;
/*     */     try {
/* 237 */       Method localMethod = paramClass.getMethod(paramString, new Class[0]);
/* 238 */       Annotation localAnnotation = paramAnnotatedElement.getAnnotation(paramClass);
/* 239 */       localObject = localMethod.invoke(localAnnotation, new Object[0]);
/*     */     } catch (NoSuchMethodException localNoSuchMethodException) {
/* 241 */       if (!$assertionsDisabled) throw new AssertionError(); 
/*     */     }
/* 243 */     catch (IllegalAccessException localIllegalAccessException) { if (!$assertionsDisabled) throw new AssertionError();  } catch (InvocationTargetException localInvocationTargetException)
/*     */     {
/* 245 */       if (!$assertionsDisabled) throw new AssertionError(); 
/*     */     }
/* 247 */     catch (NullPointerException localNullPointerException) { if (!$assertionsDisabled) throw new AssertionError();
/*     */     }
/* 249 */     return localObject;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.tracing.ProviderSkeleton
 * JD-Core Version:    0.6.2
 */