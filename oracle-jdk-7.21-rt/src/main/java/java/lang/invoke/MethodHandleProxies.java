/*     */ package java.lang.invoke;
/*     */ 
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import sun.invoke.WrapperInstance;
/*     */ import sun.reflect.Reflection;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ 
/*     */ public class MethodHandleProxies
/*     */ {
/*     */   public static <T> T asInterfaceInstance(final Class<T> paramClass, MethodHandle paramMethodHandle)
/*     */   {
/* 142 */     if ((!paramClass.isInterface()) || (!Modifier.isPublic(paramClass.getModifiers())))
/* 143 */       throw new IllegalArgumentException("not a public interface: " + paramClass.getName());
/*     */     MethodHandle localMethodHandle1;
/* 145 */     if (System.getSecurityManager() != null)
/*     */     {
/* 147 */       localObject1 = Reflection.getCallerClass(2);
/* 148 */       localObject2 = localObject1 != null ? ((Class)localObject1).getClassLoader() : null;
/* 149 */       ReflectUtil.checkProxyPackageAccess((ClassLoader)localObject2, new Class[] { paramClass });
/* 150 */       localMethodHandle1 = maybeBindCaller(paramMethodHandle, (Class)localObject1);
/*     */     } else {
/* 152 */       localMethodHandle1 = paramMethodHandle;
/*     */     }
/* 154 */     ClassLoader localClassLoader = paramClass.getClassLoader();
/* 155 */     if (localClassLoader == null) {
/* 156 */       localObject1 = Thread.currentThread().getContextClassLoader();
/* 157 */       localClassLoader = localObject1 != null ? localObject1 : ClassLoader.getSystemClassLoader();
/*     */     }
/* 159 */     Object localObject1 = getSingleNameMethods(paramClass);
/* 160 */     if (localObject1 == null)
/* 161 */       throw new IllegalArgumentException("not a single-method interface: " + paramClass.getName());
/* 162 */     Object localObject2 = new MethodHandle[localObject1.length];
/*     */     Object localObject3;
/*     */     Object localObject4;
/* 163 */     for (int i = 0; i < localObject1.length; i++) {
/* 164 */       localObject3 = localObject1[i];
/* 165 */       localObject4 = MethodType.methodType(localObject3.getReturnType(), localObject3.getParameterTypes());
/* 166 */       MethodHandle localMethodHandle2 = localMethodHandle1.asType((MethodType)localObject4);
/* 167 */       localMethodHandle2 = localMethodHandle2.asType(localMethodHandle2.type().changeReturnType(Object.class));
/* 168 */       localObject2[i] = localMethodHandle2.asSpreader([Ljava.lang.Object.class, ((MethodType)localObject4).parameterCount());
/*     */     }
/* 170 */     final InvocationHandler local1 = new InvocationHandler() {
/*     */       private Object getArg(String paramAnonymousString) {
/* 172 */         if (paramAnonymousString == "getWrapperInstanceTarget") return this.val$target;
/* 173 */         if (paramAnonymousString == "getWrapperInstanceType") return paramClass;
/* 174 */         throw new AssertionError();
/*     */       }
/*     */       public Object invoke(Object paramAnonymousObject, Method paramAnonymousMethod, Object[] paramAnonymousArrayOfObject) throws Throwable {
/* 177 */         for (int i = 0; i < this.val$methods.length; i++) {
/* 178 */           if (paramAnonymousMethod.equals(this.val$methods[i]))
/* 179 */             return this.val$vaTargets[i].invokeExact(paramAnonymousArrayOfObject);
/*     */         }
/* 181 */         if (paramAnonymousMethod.getDeclaringClass() == WrapperInstance.class)
/* 182 */           return getArg(paramAnonymousMethod.getName());
/* 183 */         if (MethodHandleProxies.isObjectMethod(paramAnonymousMethod))
/* 184 */           return MethodHandleProxies.callObjectMethod(this, paramAnonymousMethod, paramAnonymousArrayOfObject);
/* 185 */         throw new InternalError("bad proxy method: " + paramAnonymousMethod);
/*     */       }
/*     */     };
/* 190 */     if (System.getSecurityManager() != null)
/*     */     {
/* 193 */       localObject4 = localClassLoader;
/* 194 */       localObject3 = AccessController.doPrivileged(new PrivilegedAction() {
/*     */         public Object run() {
/* 196 */           return Proxy.newProxyInstance(this.val$loader, new Class[] { paramClass, WrapperInstance.class }, local1);
/*     */         }
/*     */ 
/*     */       });
/*     */     }
/*     */     else
/*     */     {
/* 203 */       localObject3 = Proxy.newProxyInstance(localClassLoader, new Class[] { paramClass, WrapperInstance.class }, local1);
/*     */     }
/*     */ 
/* 207 */     return paramClass.cast(localObject3);
/*     */   }
/*     */ 
/*     */   private static MethodHandle maybeBindCaller(MethodHandle paramMethodHandle, Class<?> paramClass) {
/* 211 */     if ((paramClass == null) || (paramClass.getClassLoader() == null)) {
/* 212 */       return paramMethodHandle;
/*     */     }
/* 214 */     MethodHandle localMethodHandle = MethodHandleImpl.bindCaller(paramMethodHandle, paramClass);
/* 215 */     if (paramMethodHandle.isVarargsCollector()) {
/* 216 */       MethodType localMethodType = localMethodHandle.type();
/* 217 */       int i = localMethodType.parameterCount();
/* 218 */       return localMethodHandle.asVarargsCollector(localMethodType.parameterType(i - 1));
/*     */     }
/* 220 */     return localMethodHandle;
/*     */   }
/*     */ 
/*     */   public static boolean isWrapperInstance(Object paramObject)
/*     */   {
/* 230 */     return paramObject instanceof WrapperInstance;
/*     */   }
/*     */ 
/*     */   private static WrapperInstance asWrapperInstance(Object paramObject) {
/*     */     try {
/* 235 */       if (paramObject != null)
/* 236 */         return (WrapperInstance)paramObject;
/*     */     } catch (ClassCastException localClassCastException) {
/*     */     }
/* 239 */     throw new IllegalArgumentException("not a wrapper instance");
/*     */   }
/*     */ 
/*     */   public static MethodHandle wrapperInstanceTarget(Object paramObject)
/*     */   {
/* 253 */     return asWrapperInstance(paramObject).getWrapperInstanceTarget();
/*     */   }
/*     */ 
/*     */   public static Class<?> wrapperInstanceType(Object paramObject)
/*     */   {
/* 266 */     return asWrapperInstance(paramObject).getWrapperInstanceType();
/*     */   }
/*     */ 
/*     */   private static boolean isObjectMethod(Method paramMethod)
/*     */   {
/* 271 */     switch (paramMethod.getName()) {
/*     */     case "toString":
/* 273 */       return (paramMethod.getReturnType() == String.class) && (paramMethod.getParameterTypes().length == 0);
/*     */     case "hashCode":
/* 276 */       return (paramMethod.getReturnType() == Integer.TYPE) && (paramMethod.getParameterTypes().length == 0);
/*     */     case "equals":
/* 279 */       return (paramMethod.getReturnType() == Boolean.TYPE) && (paramMethod.getParameterTypes().length == 1) && (paramMethod.getParameterTypes()[0] == Object.class);
/*     */     }
/*     */ 
/* 283 */     return false;
/*     */   }
/*     */ 
/*     */   private static Object callObjectMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
/*     */   {
/* 288 */     assert (isObjectMethod(paramMethod)) : paramMethod;
/* 289 */     switch (paramMethod.getName()) {
/*     */     case "toString":
/* 291 */       return paramObject.getClass().getName() + "@" + Integer.toHexString(paramObject.hashCode());
/*     */     case "hashCode":
/* 293 */       return Integer.valueOf(System.identityHashCode(paramObject));
/*     */     case "equals":
/* 295 */       return Boolean.valueOf(paramObject == paramArrayOfObject[0]);
/*     */     }
/* 297 */     return null;
/*     */   }
/*     */ 
/*     */   private static Method[] getSingleNameMethods(Class<?> paramClass)
/*     */   {
/* 302 */     ArrayList localArrayList = new ArrayList();
/* 303 */     Object localObject = null;
/* 304 */     for (Method localMethod : paramClass.getMethods())
/* 305 */       if ((!isObjectMethod(localMethod)) && 
/* 306 */         (Modifier.isAbstract(localMethod.getModifiers()))) {
/* 307 */         String str = localMethod.getName();
/* 308 */         if (localObject == null)
/* 309 */           localObject = str;
/* 310 */         else if (!localObject.equals(str))
/* 311 */           return null;
/* 312 */         localArrayList.add(localMethod);
/*     */       }
/* 314 */     if (localObject == null) return null;
/* 315 */     return (Method[])localArrayList.toArray(new Method[localArrayList.size()]);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.MethodHandleProxies
 * JD-Core Version:    0.6.2
 */