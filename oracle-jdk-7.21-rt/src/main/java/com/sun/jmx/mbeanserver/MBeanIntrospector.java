/*     */ package com.sun.jmx.mbeanserver;
/*     */ 
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.management.Descriptor;
/*     */ import javax.management.ImmutableDescriptor;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanConstructorInfo;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.NotCompliantMBeanException;
/*     */ import javax.management.NotificationBroadcaster;
/*     */ import javax.management.ReflectionException;
/*     */ 
/*     */ abstract class MBeanIntrospector<M>
/*     */ {
/*     */   abstract PerInterfaceMap<M> getPerInterfaceMap();
/*     */ 
/*     */   abstract MBeanInfoMap getMBeanInfoMap();
/*     */ 
/*     */   abstract MBeanAnalyzer<M> getAnalyzer(Class<?> paramClass)
/*     */     throws NotCompliantMBeanException;
/*     */ 
/*     */   abstract boolean isMXBean();
/*     */ 
/*     */   abstract M mFrom(Method paramMethod);
/*     */ 
/*     */   abstract String getName(M paramM);
/*     */ 
/*     */   abstract Type getGenericReturnType(M paramM);
/*     */ 
/*     */   abstract Type[] getGenericParameterTypes(M paramM);
/*     */ 
/*     */   abstract String[] getSignature(M paramM);
/*     */ 
/*     */   abstract void checkMethod(M paramM);
/*     */ 
/*     */   abstract Object invokeM2(M paramM, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2)
/*     */     throws InvocationTargetException, IllegalAccessException, MBeanException;
/*     */ 
/*     */   abstract boolean validParameter(M paramM, Object paramObject1, int paramInt, Object paramObject2);
/*     */ 
/*     */   abstract MBeanAttributeInfo getMBeanAttributeInfo(String paramString, M paramM1, M paramM2);
/*     */ 
/*     */   abstract MBeanOperationInfo getMBeanOperationInfo(String paramString, M paramM);
/*     */ 
/*     */   abstract Descriptor getBasicMBeanDescriptor();
/*     */ 
/*     */   abstract Descriptor getMBeanDescriptor(Class<?> paramClass);
/*     */ 
/*     */   List<Method> getMethods(Class<?> paramClass)
/*     */   {
/* 179 */     return Arrays.asList(paramClass.getMethods());
/*     */   }
/*     */ 
/*     */   final PerInterface<M> getPerInterface(Class<?> paramClass) throws NotCompliantMBeanException
/*     */   {
/* 184 */     PerInterfaceMap localPerInterfaceMap = getPerInterfaceMap();
/* 185 */     synchronized (localPerInterfaceMap) {
/* 186 */       WeakReference localWeakReference = (WeakReference)localPerInterfaceMap.get(paramClass);
/* 187 */       PerInterface localPerInterface = localWeakReference == null ? null : (PerInterface)localWeakReference.get();
/* 188 */       if (localPerInterface == null) {
/*     */         try {
/* 190 */           MBeanAnalyzer localMBeanAnalyzer = getAnalyzer(paramClass);
/* 191 */           MBeanInfo localMBeanInfo = makeInterfaceMBeanInfo(paramClass, localMBeanAnalyzer);
/*     */ 
/* 193 */           localPerInterface = new PerInterface(paramClass, this, localMBeanAnalyzer, localMBeanInfo);
/*     */ 
/* 195 */           localWeakReference = new WeakReference(localPerInterface);
/* 196 */           localPerInterfaceMap.put(paramClass, localWeakReference);
/*     */         } catch (Exception localException) {
/* 198 */           throw Introspector.throwException(paramClass, localException);
/*     */         }
/*     */       }
/* 201 */       return localPerInterface;
/*     */     }
/*     */   }
/*     */ 
/*     */   private MBeanInfo makeInterfaceMBeanInfo(Class<?> paramClass, MBeanAnalyzer<M> paramMBeanAnalyzer)
/*     */   {
/* 215 */     MBeanInfoMaker localMBeanInfoMaker = new MBeanInfoMaker(null);
/* 216 */     paramMBeanAnalyzer.visit(localMBeanInfoMaker);
/*     */ 
/* 219 */     return localMBeanInfoMaker.makeMBeanInfo(paramClass, "Information on the management interface of the MBean");
/*     */   }
/*     */ 
/*     */   final boolean consistent(M paramM1, M paramM2)
/*     */   {
/* 224 */     return (paramM1 == null) || (paramM2 == null) || (getGenericReturnType(paramM1).equals(getGenericParameterTypes(paramM2)[0]));
/*     */   }
/*     */ 
/*     */   final Object invokeM(M paramM, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/*     */     try
/*     */     {
/* 235 */       return invokeM2(paramM, paramObject1, paramArrayOfObject, paramObject2);
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 237 */       unwrapInvocationTargetException(localInvocationTargetException);
/* 238 */       throw new RuntimeException(localInvocationTargetException);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 240 */       throw new ReflectionException(localIllegalAccessException, localIllegalAccessException.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   final void invokeSetter(String paramString, M paramM, Object paramObject1, Object paramObject2, Object paramObject3)
/*     */     throws MBeanException, ReflectionException, InvalidAttributeValueException
/*     */   {
/*     */     try
/*     */     {
/* 265 */       invokeM2(paramM, paramObject1, new Object[] { paramObject2 }, paramObject3);
/*     */     } catch (IllegalAccessException localIllegalAccessException) {
/* 267 */       throw new ReflectionException(localIllegalAccessException, localIllegalAccessException.toString());
/*     */     } catch (RuntimeException localRuntimeException) {
/* 269 */       maybeInvalidParameter(paramString, paramM, paramObject2, paramObject3);
/* 270 */       throw localRuntimeException;
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 272 */       maybeInvalidParameter(paramString, paramM, paramObject2, paramObject3);
/* 273 */       unwrapInvocationTargetException(localInvocationTargetException);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void maybeInvalidParameter(String paramString, M paramM, Object paramObject1, Object paramObject2)
/*     */     throws InvalidAttributeValueException
/*     */   {
/* 280 */     if (!validParameter(paramM, paramObject1, 0, paramObject2)) {
/* 281 */       String str = "Invalid value for attribute " + paramString + ": " + paramObject1;
/*     */ 
/* 283 */       throw new InvalidAttributeValueException(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean isValidParameter(Method paramMethod, Object paramObject, int paramInt) {
/* 288 */     Class localClass = paramMethod.getParameterTypes()[paramInt];
/*     */     try
/*     */     {
/* 293 */       Object localObject = Array.newInstance(localClass, 1);
/* 294 */       Array.set(localObject, 0, paramObject);
/* 295 */       return true; } catch (IllegalArgumentException localIllegalArgumentException) {
/*     */     }
/* 297 */     return false;
/*     */   }
/*     */ 
/*     */   private static void unwrapInvocationTargetException(InvocationTargetException paramInvocationTargetException)
/*     */     throws MBeanException
/*     */   {
/* 304 */     Throwable localThrowable = paramInvocationTargetException.getCause();
/* 305 */     if ((localThrowable instanceof RuntimeException))
/* 306 */       throw ((RuntimeException)localThrowable);
/* 307 */     if ((localThrowable instanceof Error)) {
/* 308 */       throw ((Error)localThrowable);
/*     */     }
/* 310 */     throw new MBeanException((Exception)localThrowable, localThrowable == null ? null : localThrowable.toString());
/*     */   }
/*     */ 
/*     */   final MBeanInfo getMBeanInfo(Object paramObject, PerInterface<M> paramPerInterface)
/*     */   {
/* 389 */     MBeanInfo localMBeanInfo = getClassMBeanInfo(paramObject.getClass(), paramPerInterface);
/*     */ 
/* 391 */     MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = findNotifications(paramObject);
/* 392 */     if ((arrayOfMBeanNotificationInfo == null) || (arrayOfMBeanNotificationInfo.length == 0)) {
/* 393 */       return localMBeanInfo;
/*     */     }
/* 395 */     return new MBeanInfo(localMBeanInfo.getClassName(), localMBeanInfo.getDescription(), localMBeanInfo.getAttributes(), localMBeanInfo.getConstructors(), localMBeanInfo.getOperations(), arrayOfMBeanNotificationInfo, localMBeanInfo.getDescriptor());
/*     */   }
/*     */ 
/*     */   final MBeanInfo getClassMBeanInfo(Class<?> paramClass, PerInterface<M> paramPerInterface)
/*     */   {
/* 414 */     MBeanInfoMap localMBeanInfoMap = getMBeanInfoMap();
/* 415 */     synchronized (localMBeanInfoMap) {
/* 416 */       WeakHashMap localWeakHashMap = (WeakHashMap)localMBeanInfoMap.get(paramClass);
/* 417 */       if (localWeakHashMap == null) {
/* 418 */         localWeakHashMap = new WeakHashMap();
/* 419 */         localMBeanInfoMap.put(paramClass, localWeakHashMap);
/*     */       }
/* 421 */       Class localClass = paramPerInterface.getMBeanInterface();
/* 422 */       MBeanInfo localMBeanInfo1 = (MBeanInfo)localWeakHashMap.get(localClass);
/* 423 */       if (localMBeanInfo1 == null) {
/* 424 */         MBeanInfo localMBeanInfo2 = paramPerInterface.getMBeanInfo();
/* 425 */         ImmutableDescriptor localImmutableDescriptor = ImmutableDescriptor.union(new Descriptor[] { localMBeanInfo2.getDescriptor(), getMBeanDescriptor(paramClass) });
/*     */ 
/* 428 */         localMBeanInfo1 = new MBeanInfo(paramClass.getName(), localMBeanInfo2.getDescription(), localMBeanInfo2.getAttributes(), findConstructors(paramClass), localMBeanInfo2.getOperations(), (MBeanNotificationInfo[])null, localImmutableDescriptor);
/*     */ 
/* 435 */         localWeakHashMap.put(localClass, localMBeanInfo1);
/*     */       }
/* 437 */       return localMBeanInfo1;
/*     */     }
/*     */   }
/*     */ 
/*     */   static MBeanNotificationInfo[] findNotifications(Object paramObject) {
/* 442 */     if (!(paramObject instanceof NotificationBroadcaster))
/* 443 */       return null;
/* 444 */     MBeanNotificationInfo[] arrayOfMBeanNotificationInfo1 = ((NotificationBroadcaster)paramObject).getNotificationInfo();
/*     */ 
/* 446 */     if (arrayOfMBeanNotificationInfo1 == null)
/* 447 */       return null;
/* 448 */     MBeanNotificationInfo[] arrayOfMBeanNotificationInfo2 = new MBeanNotificationInfo[arrayOfMBeanNotificationInfo1.length];
/*     */ 
/* 450 */     for (int i = 0; i < arrayOfMBeanNotificationInfo1.length; i++) {
/* 451 */       MBeanNotificationInfo localMBeanNotificationInfo = arrayOfMBeanNotificationInfo1[i];
/* 452 */       if (localMBeanNotificationInfo.getClass() != MBeanNotificationInfo.class)
/* 453 */         localMBeanNotificationInfo = (MBeanNotificationInfo)localMBeanNotificationInfo.clone();
/* 454 */       arrayOfMBeanNotificationInfo2[i] = localMBeanNotificationInfo;
/*     */     }
/* 456 */     return arrayOfMBeanNotificationInfo2;
/*     */   }
/*     */ 
/*     */   private static MBeanConstructorInfo[] findConstructors(Class<?> paramClass) {
/* 460 */     Constructor[] arrayOfConstructor = paramClass.getConstructors();
/* 461 */     MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = new MBeanConstructorInfo[arrayOfConstructor.length];
/* 462 */     for (int i = 0; i < arrayOfConstructor.length; i++)
/*     */     {
/* 464 */       arrayOfMBeanConstructorInfo[i] = new MBeanConstructorInfo("Public constructor of the MBean", arrayOfConstructor[i]);
/*     */     }
/* 466 */     return arrayOfMBeanConstructorInfo;
/*     */   }
/*     */ 
/*     */   private class MBeanInfoMaker
/*     */     implements MBeanAnalyzer.MBeanVisitor<M>
/*     */   {
/* 365 */     private final List<MBeanAttributeInfo> attrs = Util.newList();
/* 366 */     private final List<MBeanOperationInfo> ops = Util.newList();
/*     */ 
/*     */     private MBeanInfoMaker()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void visitAttribute(String paramString, M paramM1, M paramM2)
/*     */     {
/* 321 */       MBeanAttributeInfo localMBeanAttributeInfo = MBeanIntrospector.this.getMBeanAttributeInfo(paramString, paramM1, paramM2);
/*     */ 
/* 324 */       this.attrs.add(localMBeanAttributeInfo);
/*     */     }
/*     */ 
/*     */     public void visitOperation(String paramString, M paramM)
/*     */     {
/* 329 */       MBeanOperationInfo localMBeanOperationInfo = MBeanIntrospector.this.getMBeanOperationInfo(paramString, paramM);
/*     */ 
/* 332 */       this.ops.add(localMBeanOperationInfo);
/*     */     }
/*     */ 
/*     */     MBeanInfo makeMBeanInfo(Class<?> paramClass, String paramString)
/*     */     {
/* 339 */       MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = (MBeanAttributeInfo[])this.attrs.toArray(new MBeanAttributeInfo[0]);
/*     */ 
/* 341 */       MBeanOperationInfo[] arrayOfMBeanOperationInfo = (MBeanOperationInfo[])this.ops.toArray(new MBeanOperationInfo[0]);
/*     */ 
/* 343 */       String str = "interfaceClassName=" + paramClass.getName();
/*     */ 
/* 345 */       ImmutableDescriptor localImmutableDescriptor1 = new ImmutableDescriptor(new String[] { str });
/*     */ 
/* 347 */       Descriptor localDescriptor1 = MBeanIntrospector.this.getBasicMBeanDescriptor();
/* 348 */       Descriptor localDescriptor2 = Introspector.descriptorForElement(paramClass);
/*     */ 
/* 350 */       ImmutableDescriptor localImmutableDescriptor2 = DescriptorCache.getInstance().union(new Descriptor[] { localImmutableDescriptor1, localDescriptor1, localDescriptor2 });
/*     */ 
/* 356 */       return new MBeanInfo(paramClass.getName(), paramString, arrayOfMBeanAttributeInfo, null, arrayOfMBeanOperationInfo, null, localImmutableDescriptor2);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class MBeanInfoMap extends WeakHashMap<Class<?>, WeakHashMap<Class<?>, MBeanInfo>>
/*     */   {
/*     */   }
/*     */ 
/*     */   static final class PerInterfaceMap<M> extends WeakHashMap<Class<?>, WeakReference<PerInterface<M>>>
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.MBeanIntrospector
 * JD-Core Version:    0.6.2
 */