/*     */ package com.sun.jmx.mbeanserver;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.management.Descriptor;
/*     */ import javax.management.ImmutableDescriptor;
/*     */ import javax.management.IntrospectionException;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.NotCompliantMBeanException;
/*     */ import javax.management.NotificationBroadcaster;
/*     */ import javax.management.NotificationBroadcasterSupport;
/*     */ 
/*     */ class StandardMBeanIntrospector extends MBeanIntrospector<Method>
/*     */ {
/*  46 */   private static final StandardMBeanIntrospector instance = new StandardMBeanIntrospector();
/*     */ 
/* 185 */   private static final WeakHashMap<Class<?>, Boolean> definitelyImmutable = new WeakHashMap();
/*     */ 
/* 189 */   private static final MBeanIntrospector.PerInterfaceMap<Method> perInterfaceMap = new MBeanIntrospector.PerInterfaceMap();
/*     */ 
/* 191 */   private static final MBeanIntrospector.MBeanInfoMap mbeanInfoMap = new MBeanIntrospector.MBeanInfoMap();
/*     */ 
/*     */   static StandardMBeanIntrospector getInstance()
/*     */   {
/*  50 */     return instance;
/*     */   }
/*     */ 
/*     */   MBeanIntrospector.PerInterfaceMap<Method> getPerInterfaceMap()
/*     */   {
/*  55 */     return perInterfaceMap;
/*     */   }
/*     */ 
/*     */   MBeanIntrospector.MBeanInfoMap getMBeanInfoMap()
/*     */   {
/*  60 */     return mbeanInfoMap;
/*     */   }
/*     */ 
/*     */   MBeanAnalyzer<Method> getAnalyzer(Class<?> paramClass)
/*     */     throws NotCompliantMBeanException
/*     */   {
/*  66 */     return MBeanAnalyzer.analyzer(paramClass, this);
/*     */   }
/*     */ 
/*     */   boolean isMXBean()
/*     */   {
/*  71 */     return false;
/*     */   }
/*     */ 
/*     */   Method mFrom(Method paramMethod)
/*     */   {
/*  76 */     return paramMethod;
/*     */   }
/*     */ 
/*     */   String getName(Method paramMethod)
/*     */   {
/*  81 */     return paramMethod.getName();
/*     */   }
/*     */ 
/*     */   Type getGenericReturnType(Method paramMethod)
/*     */   {
/*  86 */     return paramMethod.getGenericReturnType();
/*     */   }
/*     */ 
/*     */   Type[] getGenericParameterTypes(Method paramMethod)
/*     */   {
/*  91 */     return paramMethod.getGenericParameterTypes();
/*     */   }
/*     */ 
/*     */   String[] getSignature(Method paramMethod)
/*     */   {
/*  96 */     Class[] arrayOfClass = paramMethod.getParameterTypes();
/*  97 */     String[] arrayOfString = new String[arrayOfClass.length];
/*  98 */     for (int i = 0; i < arrayOfClass.length; i++)
/*  99 */       arrayOfString[i] = arrayOfClass[i].getName();
/* 100 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */   void checkMethod(Method paramMethod)
/*     */   {
/*     */   }
/*     */ 
/*     */   Object invokeM2(Method paramMethod, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2)
/*     */     throws InvocationTargetException, IllegalAccessException, MBeanException
/*     */   {
/* 111 */     return paramMethod.invoke(paramObject1, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   boolean validParameter(Method paramMethod, Object paramObject1, int paramInt, Object paramObject2)
/*     */   {
/* 116 */     return isValidParameter(paramMethod, paramObject1, paramInt);
/*     */   }
/*     */ 
/*     */   MBeanAttributeInfo getMBeanAttributeInfo(String paramString, Method paramMethod1, Method paramMethod2)
/*     */   {
/*     */     try
/*     */     {
/* 125 */       return new MBeanAttributeInfo(paramString, "Attribute exposed for management", paramMethod1, paramMethod2);
/*     */     }
/*     */     catch (IntrospectionException localIntrospectionException) {
/* 128 */       throw new RuntimeException(localIntrospectionException);
/*     */     }
/*     */   }
/*     */ 
/*     */   MBeanOperationInfo getMBeanOperationInfo(String paramString, Method paramMethod)
/*     */   {
/* 136 */     return new MBeanOperationInfo("Operation exposed for management", paramMethod);
/*     */   }
/*     */ 
/*     */   Descriptor getBasicMBeanDescriptor()
/*     */   {
/* 144 */     return ImmutableDescriptor.EMPTY_DESCRIPTOR;
/*     */   }
/*     */ 
/*     */   Descriptor getMBeanDescriptor(Class<?> paramClass)
/*     */   {
/* 149 */     boolean bool = isDefinitelyImmutableInfo(paramClass);
/* 150 */     return new ImmutableDescriptor(new String[] { "mxbean=false", "immutableInfo=" + bool });
/*     */   }
/*     */ 
/*     */   static boolean isDefinitelyImmutableInfo(Class<?> paramClass)
/*     */   {
/* 163 */     if (!NotificationBroadcaster.class.isAssignableFrom(paramClass))
/* 164 */       return true;
/* 165 */     synchronized (definitelyImmutable) {
/* 166 */       Boolean localBoolean = (Boolean)definitelyImmutable.get(paramClass);
/* 167 */       if (localBoolean == null) {
/* 168 */         NotificationBroadcasterSupport localNotificationBroadcasterSupport = NotificationBroadcasterSupport.class;
/*     */ 
/* 170 */         if (localNotificationBroadcasterSupport.isAssignableFrom(paramClass))
/*     */           try {
/* 172 */             Method localMethod = paramClass.getMethod("getNotificationInfo", new Class[0]);
/* 173 */             localBoolean = Boolean.valueOf(localMethod.getDeclaringClass() == localNotificationBroadcasterSupport);
/*     */           }
/*     */           catch (Exception localException) {
/* 176 */             return false;
/*     */           }
/*     */         else
/* 179 */           localBoolean = Boolean.valueOf(false);
/* 180 */         definitelyImmutable.put(paramClass, localBoolean);
/*     */       }
/* 182 */       return localBoolean.booleanValue();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.StandardMBeanIntrospector
 * JD-Core Version:    0.6.2
 */