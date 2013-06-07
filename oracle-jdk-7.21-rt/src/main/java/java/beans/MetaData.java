/*      */ package java.beans;
/*      */ 
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Collections;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Map;
/*      */ import java.util.WeakHashMap;
/*      */ 
/*      */ class MetaData
/*      */ {
/* 1277 */   private static final Map<String, Field> fields = Collections.synchronizedMap(new WeakHashMap());
/* 1278 */   private static Hashtable internalPersistenceDelegates = new Hashtable();
/*      */ 
/* 1280 */   private static PersistenceDelegate nullPersistenceDelegate = new NullPersistenceDelegate();
/* 1281 */   private static PersistenceDelegate enumPersistenceDelegate = new EnumPersistenceDelegate();
/* 1282 */   private static PersistenceDelegate primitivePersistenceDelegate = new PrimitivePersistenceDelegate();
/* 1283 */   private static PersistenceDelegate defaultPersistenceDelegate = new DefaultPersistenceDelegate();
/*      */   private static PersistenceDelegate arrayPersistenceDelegate;
/*      */   private static PersistenceDelegate proxyPersistenceDelegate;
/*      */ 
/*      */   public static synchronized PersistenceDelegate getPersistenceDelegate(Class paramClass)
/*      */   {
/* 1312 */     if (paramClass == null) {
/* 1313 */       return nullPersistenceDelegate;
/*      */     }
/* 1315 */     if (Enum.class.isAssignableFrom(paramClass)) {
/* 1316 */       return enumPersistenceDelegate;
/*      */     }
/* 1318 */     if (ReflectionUtils.isPrimitive(paramClass)) {
/* 1319 */       return primitivePersistenceDelegate;
/*      */     }
/*      */ 
/* 1322 */     if (paramClass.isArray()) {
/* 1323 */       if (arrayPersistenceDelegate == null) {
/* 1324 */         arrayPersistenceDelegate = new ArrayPersistenceDelegate();
/*      */       }
/* 1326 */       return arrayPersistenceDelegate;
/*      */     }
/*      */     try
/*      */     {
/* 1330 */       if (Proxy.isProxyClass(paramClass)) {
/* 1331 */         if (proxyPersistenceDelegate == null) {
/* 1332 */           proxyPersistenceDelegate = new ProxyPersistenceDelegate();
/*      */         }
/* 1334 */         return proxyPersistenceDelegate;
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception localException1)
/*      */     {
/*      */     }
/*      */ 
/* 1342 */     String str1 = paramClass.getName();
/* 1343 */     Object localObject1 = (PersistenceDelegate)getBeanAttribute(paramClass, "persistenceDelegate");
/* 1344 */     if (localObject1 == null) {
/* 1345 */       localObject1 = (PersistenceDelegate)internalPersistenceDelegates.get(str1);
/* 1346 */       if (localObject1 != null) {
/* 1347 */         return localObject1;
/*      */       }
/* 1349 */       internalPersistenceDelegates.put(str1, defaultPersistenceDelegate);
/*      */       try {
/* 1351 */         String str2 = paramClass.getName();
/* 1352 */         localObject2 = Class.forName("java.beans." + str2.replace('.', '_') + "_PersistenceDelegate");
/*      */ 
/* 1354 */         localObject1 = (PersistenceDelegate)((Class)localObject2).newInstance();
/* 1355 */         internalPersistenceDelegates.put(str1, localObject1);
/*      */       }
/*      */       catch (ClassNotFoundException localClassNotFoundException) {
/* 1358 */         Object localObject2 = getConstructorProperties(paramClass);
/* 1359 */         if (localObject2 != null) {
/* 1360 */           localObject1 = new DefaultPersistenceDelegate((String[])localObject2);
/* 1361 */           internalPersistenceDelegates.put(str1, localObject1);
/*      */         }
/*      */       }
/*      */       catch (Exception localException2) {
/* 1365 */         System.err.println("Internal error: " + localException2);
/*      */       }
/*      */     }
/*      */ 
/* 1369 */     return localObject1 != null ? localObject1 : defaultPersistenceDelegate;
/*      */   }
/*      */ 
/*      */   private static String[] getConstructorProperties(Class paramClass) {
/* 1373 */     Object localObject = null;
/* 1374 */     int i = 0;
/* 1375 */     for (Constructor localConstructor : paramClass.getConstructors()) {
/* 1376 */       String[] arrayOfString = getAnnotationValue(localConstructor);
/* 1377 */       if ((arrayOfString != null) && (i < arrayOfString.length) && (isValid(localConstructor, arrayOfString))) {
/* 1378 */         localObject = arrayOfString;
/* 1379 */         i = arrayOfString.length;
/*      */       }
/*      */     }
/* 1382 */     return localObject;
/*      */   }
/*      */ 
/*      */   private static String[] getAnnotationValue(Constructor<?> paramConstructor) {
/* 1386 */     ConstructorProperties localConstructorProperties = (ConstructorProperties)paramConstructor.getAnnotation(ConstructorProperties.class);
/* 1387 */     return localConstructorProperties != null ? localConstructorProperties.value() : null;
/*      */   }
/*      */ 
/*      */   private static boolean isValid(Constructor<?> paramConstructor, String[] paramArrayOfString)
/*      */   {
/* 1393 */     Class[] arrayOfClass = paramConstructor.getParameterTypes();
/* 1394 */     if (paramArrayOfString.length != arrayOfClass.length) {
/* 1395 */       return false;
/*      */     }
/* 1397 */     for (String str : paramArrayOfString) {
/* 1398 */       if (str == null) {
/* 1399 */         return false;
/*      */       }
/*      */     }
/* 1402 */     return true;
/*      */   }
/*      */ 
/*      */   private static Object getBeanAttribute(Class paramClass, String paramString) {
/*      */     try {
/* 1407 */       return Introspector.getBeanInfo(paramClass).getBeanDescriptor().getValue(paramString); } catch (IntrospectionException localIntrospectionException) {
/*      */     }
/* 1409 */     return null;
/*      */   }
/*      */ 
/*      */   static Object getPrivateFieldValue(Object paramObject, String paramString)
/*      */   {
/* 1414 */     Field localField = (Field)fields.get(paramString);
/* 1415 */     if (localField == null) {
/* 1416 */       int i = paramString.lastIndexOf('.');
/* 1417 */       String str1 = paramString.substring(0, i);
/* 1418 */       final String str2 = paramString.substring(1 + i);
/* 1419 */       localField = (Field)AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Field run() {
/*      */           try {
/* 1422 */             Field localField = Class.forName(this.val$className).getDeclaredField(str2);
/* 1423 */             localField.setAccessible(true);
/* 1424 */             return localField;
/*      */           }
/*      */           catch (ClassNotFoundException localClassNotFoundException) {
/* 1427 */             throw new IllegalStateException("Could not find class", localClassNotFoundException);
/*      */           }
/*      */           catch (NoSuchFieldException localNoSuchFieldException) {
/* 1430 */             throw new IllegalStateException("Could not find field", localNoSuchFieldException);
/*      */           }
/*      */         }
/*      */       });
/* 1434 */       fields.put(paramString, localField);
/*      */     }
/*      */     try {
/* 1437 */       return localField.get(paramObject);
/*      */     }
/*      */     catch (IllegalAccessException localIllegalAccessException) {
/* 1440 */       throw new IllegalStateException("Could not get value of the field", localIllegalAccessException);
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/* 1289 */     internalPersistenceDelegates.put("java.net.URI", new PrimitivePersistenceDelegate());
/*      */ 
/* 1293 */     internalPersistenceDelegates.put("javax.swing.plaf.BorderUIResource$MatteBorderUIResource", new javax_swing_border_MatteBorder_PersistenceDelegate());
/*      */ 
/* 1297 */     internalPersistenceDelegates.put("javax.swing.plaf.FontUIResource", new java_awt_Font_PersistenceDelegate());
/*      */ 
/* 1301 */     internalPersistenceDelegates.put("javax.swing.KeyStroke", new java_awt_AWTKeyStroke_PersistenceDelegate());
/*      */ 
/* 1304 */     internalPersistenceDelegates.put("java.sql.Date", new java_util_Date_PersistenceDelegate());
/* 1305 */     internalPersistenceDelegates.put("java.sql.Time", new java_util_Date_PersistenceDelegate());
/*      */ 
/* 1307 */     internalPersistenceDelegates.put("java.util.JumboEnumSet", new java_util_EnumSet_PersistenceDelegate());
/* 1308 */     internalPersistenceDelegates.put("java.util.RegularEnumSet", new java_util_EnumSet_PersistenceDelegate());
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.MetaData
 * JD-Core Version:    0.6.2
 */