/*     */ package javax.management;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Introspector;
/*     */ import java.lang.reflect.Proxy;
/*     */ 
/*     */ public class JMX
/*     */ {
/*  41 */   static final JMX proof = new JMX();
/*     */   public static final String DEFAULT_VALUE_FIELD = "defaultValue";
/*     */   public static final String IMMUTABLE_INFO_FIELD = "immutableInfo";
/*     */   public static final String INTERFACE_CLASS_NAME_FIELD = "interfaceClassName";
/*     */   public static final String LEGAL_VALUES_FIELD = "legalValues";
/*     */   public static final String MAX_VALUE_FIELD = "maxValue";
/*     */   public static final String MIN_VALUE_FIELD = "minValue";
/*     */   public static final String MXBEAN_FIELD = "mxbean";
/*     */   public static final String OPEN_TYPE_FIELD = "openType";
/*     */   public static final String ORIGINAL_TYPE_FIELD = "originalType";
/*     */ 
/*     */   public static <T> T newMBeanProxy(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass)
/*     */   {
/* 165 */     return newMBeanProxy(paramMBeanServerConnection, paramObjectName, paramClass, false);
/*     */   }
/*     */ 
/*     */   public static <T> T newMBeanProxy(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass, boolean paramBoolean)
/*     */   {
/* 206 */     return MBeanServerInvocationHandler.newProxyInstance(paramMBeanServerConnection, paramObjectName, paramClass, paramBoolean);
/*     */   }
/*     */ 
/*     */   public static <T> T newMXBeanProxy(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass)
/*     */   {
/* 307 */     return newMXBeanProxy(paramMBeanServerConnection, paramObjectName, paramClass, false);
/*     */   }
/*     */ 
/*     */   public static <T> T newMXBeanProxy(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass, boolean paramBoolean)
/*     */   {
/*     */     try
/*     */     {
/* 351 */       Introspector.testComplianceMXBeanInterface(paramClass);
/*     */     } catch (NotCompliantMBeanException localNotCompliantMBeanException) {
/* 353 */       throw new IllegalArgumentException(localNotCompliantMBeanException);
/*     */     }
/* 355 */     MBeanServerInvocationHandler localMBeanServerInvocationHandler = new MBeanServerInvocationHandler(paramMBeanServerConnection, paramObjectName, true);
/*     */     Class[] arrayOfClass;
/* 358 */     if (paramBoolean) {
/* 359 */       arrayOfClass = new Class[] { paramClass, NotificationEmitter.class };
/*     */     }
/*     */     else
/* 362 */       arrayOfClass = new Class[] { paramClass };
/* 363 */     Object localObject = Proxy.newProxyInstance(paramClass.getClassLoader(), arrayOfClass, localMBeanServerInvocationHandler);
/*     */ 
/* 367 */     return paramClass.cast(localObject);
/*     */   }
/*     */ 
/*     */   public static boolean isMXBeanInterface(Class<?> paramClass)
/*     */   {
/* 385 */     if (!paramClass.isInterface())
/* 386 */       return false;
/* 387 */     MXBean localMXBean = (MXBean)paramClass.getAnnotation(MXBean.class);
/* 388 */     if (localMXBean != null)
/* 389 */       return localMXBean.value();
/* 390 */     return paramClass.getName().endsWith("MXBean");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.JMX
 * JD-Core Version:    0.6.2
 */