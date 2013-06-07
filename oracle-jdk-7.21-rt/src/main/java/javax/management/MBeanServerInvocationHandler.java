/*     */ package javax.management;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.MXBeanProxy;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Arrays;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ public class MBeanServerInvocationHandler
/*     */   implements InvocationHandler
/*     */ {
/* 352 */   private static final WeakHashMap<Class<?>, WeakReference<MXBeanProxy>> mxbeanProxies = new WeakHashMap();
/*     */   private final MBeanServerConnection connection;
/*     */   private final ObjectName objectName;
/*     */   private final boolean isMXBean;
/*     */ 
/*     */   public MBeanServerInvocationHandler(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName)
/*     */   {
/* 114 */     this(paramMBeanServerConnection, paramObjectName, false);
/*     */   }
/*     */ 
/*     */   public MBeanServerInvocationHandler(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, boolean paramBoolean)
/*     */   {
/* 141 */     if (paramMBeanServerConnection == null) {
/* 142 */       throw new IllegalArgumentException("Null connection");
/*     */     }
/* 144 */     if (paramObjectName == null) {
/* 145 */       throw new IllegalArgumentException("Null object name");
/*     */     }
/* 147 */     this.connection = paramMBeanServerConnection;
/* 148 */     this.objectName = paramObjectName;
/* 149 */     this.isMXBean = paramBoolean;
/*     */   }
/*     */ 
/*     */   public MBeanServerConnection getMBeanServerConnection()
/*     */   {
/* 161 */     return this.connection;
/*     */   }
/*     */ 
/*     */   public ObjectName getObjectName()
/*     */   {
/* 173 */     return this.objectName;
/*     */   }
/*     */ 
/*     */   public boolean isMXBean()
/*     */   {
/* 185 */     return this.isMXBean;
/*     */   }
/*     */ 
/*     */   public static <T> T newProxyInstance(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass, boolean paramBoolean)
/*     */   {
/* 234 */     MBeanServerInvocationHandler localMBeanServerInvocationHandler = new MBeanServerInvocationHandler(paramMBeanServerConnection, paramObjectName);
/*     */     Class[] arrayOfClass;
/* 237 */     if (paramBoolean) {
/* 238 */       arrayOfClass = new Class[] { paramClass, NotificationEmitter.class };
/*     */     }
/*     */     else {
/* 241 */       arrayOfClass = new Class[] { paramClass };
/*     */     }
/* 243 */     Object localObject = Proxy.newProxyInstance(paramClass.getClassLoader(), arrayOfClass, localMBeanServerInvocationHandler);
/*     */ 
/* 247 */     return paramClass.cast(localObject);
/*     */   }
/*     */ 
/*     */   public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable
/*     */   {
/* 252 */     Class localClass1 = paramMethod.getDeclaringClass();
/*     */ 
/* 254 */     if ((localClass1.equals(NotificationBroadcaster.class)) || (localClass1.equals(NotificationEmitter.class)))
/*     */     {
/* 256 */       return invokeBroadcasterMethod(paramObject, paramMethod, paramArrayOfObject);
/*     */     }
/*     */ 
/* 259 */     if (shouldDoLocally(paramObject, paramMethod))
/* 260 */       return doLocally(paramObject, paramMethod, paramArrayOfObject);
/*     */     try
/*     */     {
/* 263 */       if (isMXBean()) {
/* 264 */         localObject1 = findMXBeanProxy(localClass1);
/* 265 */         return ((MXBeanProxy)localObject1).invoke(this.connection, this.objectName, paramMethod, paramArrayOfObject);
/*     */       }
/* 267 */       Object localObject1 = paramMethod.getName();
/* 268 */       Class[] arrayOfClass = paramMethod.getParameterTypes();
/* 269 */       Class localClass2 = paramMethod.getReturnType();
/*     */ 
/* 274 */       int i = paramArrayOfObject == null ? 0 : paramArrayOfObject.length;
/*     */ 
/* 276 */       if ((((String)localObject1).startsWith("get")) && (((String)localObject1).length() > 3) && (i == 0) && (!localClass2.equals(Void.TYPE)))
/*     */       {
/* 280 */         return this.connection.getAttribute(this.objectName, ((String)localObject1).substring(3));
/*     */       }
/*     */ 
/* 284 */       if ((((String)localObject1).startsWith("is")) && (((String)localObject1).length() > 2) && (i == 0) && ((localClass2.equals(Boolean.TYPE)) || (localClass2.equals(Boolean.class))))
/*     */       {
/* 289 */         return this.connection.getAttribute(this.objectName, ((String)localObject1).substring(2));
/*     */       }
/*     */ 
/* 293 */       if ((((String)localObject1).startsWith("set")) && (((String)localObject1).length() > 3) && (i == 1) && (localClass2.equals(Void.TYPE)))
/*     */       {
/* 297 */         localObject2 = new Attribute(((String)localObject1).substring(3), paramArrayOfObject[0]);
/* 298 */         this.connection.setAttribute(this.objectName, (Attribute)localObject2);
/* 299 */         return null;
/*     */       }
/*     */ 
/* 302 */       Object localObject2 = new String[arrayOfClass.length];
/* 303 */       for (int j = 0; j < arrayOfClass.length; j++)
/* 304 */         localObject2[j] = arrayOfClass[j].getName();
/* 305 */       return this.connection.invoke(this.objectName, (String)localObject1, paramArrayOfObject, (String[])localObject2);
/*     */     }
/*     */     catch (MBeanException localMBeanException)
/*     */     {
/* 309 */       throw localMBeanException.getTargetException();
/*     */     } catch (RuntimeMBeanException localRuntimeMBeanException) {
/* 311 */       throw localRuntimeMBeanException.getTargetException();
/*     */     } catch (RuntimeErrorException localRuntimeErrorException) {
/* 313 */       throw localRuntimeErrorException.getTargetError();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static MXBeanProxy findMXBeanProxy(Class<?> paramClass)
/*     */   {
/* 330 */     synchronized (mxbeanProxies) {
/* 331 */       WeakReference localWeakReference = (WeakReference)mxbeanProxies.get(paramClass);
/*     */ 
/* 333 */       MXBeanProxy localMXBeanProxy = localWeakReference == null ? null : (MXBeanProxy)localWeakReference.get();
/* 334 */       if (localMXBeanProxy == null) {
/*     */         try {
/* 336 */           localMXBeanProxy = new MXBeanProxy(paramClass);
/*     */         } catch (IllegalArgumentException localIllegalArgumentException1) {
/* 338 */           String str = "Cannot make MXBean proxy for " + paramClass.getName() + ": " + localIllegalArgumentException1.getMessage();
/*     */ 
/* 340 */           IllegalArgumentException localIllegalArgumentException2 = new IllegalArgumentException(str, localIllegalArgumentException1.getCause());
/*     */ 
/* 342 */           localIllegalArgumentException2.setStackTrace(localIllegalArgumentException1.getStackTrace());
/* 343 */           throw localIllegalArgumentException2;
/*     */         }
/* 345 */         mxbeanProxies.put(paramClass, new WeakReference(localMXBeanProxy));
/*     */       }
/*     */ 
/* 348 */       return localMXBeanProxy;
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object invokeBroadcasterMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
/*     */     throws Exception
/*     */   {
/* 356 */     String str1 = paramMethod.getName();
/* 357 */     int i = paramArrayOfObject == null ? 0 : paramArrayOfObject.length;
/*     */     Object localObject1;
/*     */     NotificationFilter localNotificationFilter;
/*     */     Object localObject2;
/* 359 */     if (str1.equals("addNotificationListener"))
/*     */     {
/* 364 */       if (i != 3) {
/* 365 */         localObject1 = "Bad arg count to addNotificationListener: " + i;
/*     */ 
/* 367 */         throw new IllegalArgumentException((String)localObject1);
/*     */       }
/*     */ 
/* 372 */       localObject1 = (NotificationListener)paramArrayOfObject[0];
/* 373 */       localNotificationFilter = (NotificationFilter)paramArrayOfObject[1];
/* 374 */       localObject2 = paramArrayOfObject[2];
/* 375 */       this.connection.addNotificationListener(this.objectName, (NotificationListener)localObject1, localNotificationFilter, localObject2);
/*     */ 
/* 379 */       return null;
/*     */     }
/* 381 */     if (str1.equals("removeNotificationListener"))
/*     */     {
/* 385 */       localObject1 = (NotificationListener)paramArrayOfObject[0];
/*     */ 
/* 387 */       switch (i) {
/*     */       case 1:
/* 389 */         this.connection.removeNotificationListener(this.objectName, (NotificationListener)localObject1);
/* 390 */         return null;
/*     */       case 3:
/* 393 */         localNotificationFilter = (NotificationFilter)paramArrayOfObject[1];
/* 394 */         localObject2 = paramArrayOfObject[2];
/* 395 */         this.connection.removeNotificationListener(this.objectName, (NotificationListener)localObject1, localNotificationFilter, localObject2);
/*     */ 
/* 399 */         return null;
/*     */       }
/*     */ 
/* 402 */       String str2 = "Bad arg count to removeNotificationListener: " + i;
/*     */ 
/* 404 */       throw new IllegalArgumentException(str2);
/*     */     }
/*     */ 
/* 407 */     if (str1.equals("getNotificationInfo"))
/*     */     {
/* 409 */       if (paramArrayOfObject != null) {
/* 410 */         throw new IllegalArgumentException("getNotificationInfo has args");
/*     */       }
/*     */ 
/* 414 */       localObject1 = this.connection.getMBeanInfo(this.objectName);
/* 415 */       return ((MBeanInfo)localObject1).getNotifications();
/*     */     }
/*     */ 
/* 418 */     throw new IllegalArgumentException("Bad method name: " + str1);
/*     */   }
/*     */ 
/*     */   private boolean shouldDoLocally(Object paramObject, Method paramMethod)
/*     */   {
/* 424 */     String str = paramMethod.getName();
/* 425 */     if (((str.equals("hashCode")) || (str.equals("toString"))) && (paramMethod.getParameterTypes().length == 0) && (isLocal(paramObject, paramMethod)))
/*     */     {
/* 428 */       return true;
/* 429 */     }if (str.equals("equals")) if ((Arrays.equals(paramMethod.getParameterTypes(), new Class[] { Object.class })) && (isLocal(paramObject, paramMethod)))
/*     */       {
/* 433 */         return true;
/*     */       } return false;
/*     */   }
/*     */ 
/*     */   private Object doLocally(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) {
/* 438 */     String str = paramMethod.getName();
/*     */ 
/* 440 */     if (str.equals("equals"))
/*     */     {
/* 442 */       if (this == paramArrayOfObject[0]) {
/* 443 */         return Boolean.valueOf(true);
/*     */       }
/*     */ 
/* 446 */       if (!(paramArrayOfObject[0] instanceof Proxy)) {
/* 447 */         return Boolean.valueOf(false);
/*     */       }
/*     */ 
/* 450 */       InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(paramArrayOfObject[0]);
/*     */ 
/* 453 */       if ((localInvocationHandler == null) || (!(localInvocationHandler instanceof MBeanServerInvocationHandler)))
/*     */       {
/* 455 */         return Boolean.valueOf(false);
/*     */       }
/*     */ 
/* 458 */       MBeanServerInvocationHandler localMBeanServerInvocationHandler = (MBeanServerInvocationHandler)localInvocationHandler;
/*     */ 
/* 461 */       return Boolean.valueOf((this.connection.equals(localMBeanServerInvocationHandler.connection)) && (this.objectName.equals(localMBeanServerInvocationHandler.objectName)) && (paramObject.getClass().equals(paramArrayOfObject[0].getClass())));
/*     */     }
/*     */ 
/* 464 */     if (str.equals("toString")) {
/* 465 */       return (isMXBean() ? "MX" : "M") + "BeanProxy(" + this.connection + "[" + this.objectName + "])";
/*     */     }
/* 467 */     if (str.equals("hashCode")) {
/* 468 */       return Integer.valueOf(this.objectName.hashCode() + this.connection.hashCode());
/*     */     }
/*     */ 
/* 471 */     throw new RuntimeException("Unexpected method name: " + str);
/*     */   }
/*     */ 
/*     */   private static boolean isLocal(Object paramObject, Method paramMethod) {
/* 475 */     Class[] arrayOfClass1 = paramObject.getClass().getInterfaces();
/* 476 */     if (arrayOfClass1 == null) {
/* 477 */       return true;
/*     */     }
/*     */ 
/* 480 */     String str = paramMethod.getName();
/* 481 */     Class[] arrayOfClass2 = paramMethod.getParameterTypes();
/* 482 */     for (Class localClass : arrayOfClass1) {
/*     */       try {
/* 484 */         localClass.getMethod(str, arrayOfClass2);
/* 485 */         return false;
/*     */       }
/*     */       catch (NoSuchMethodException localNoSuchMethodException)
/*     */       {
/*     */       }
/*     */     }
/* 491 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanServerInvocationHandler
 * JD-Core Version:    0.6.2
 */