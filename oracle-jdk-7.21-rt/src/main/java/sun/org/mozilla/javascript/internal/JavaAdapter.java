/*      */ package sun.org.mozilla.javascript.internal;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.security.CodeSource;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import sun.org.mozilla.classfile.internal.ClassFileWriter;
/*      */ 
/*      */ public final class JavaAdapter
/*      */   implements IdFunctionCall
/*      */ {
/* 1154 */   private static final Object FTAG = "JavaAdapter";
/*      */   private static final int Id_JavaAdapter = 1;
/*      */ 
/*      */   public static void init(Context paramContext, Scriptable paramScriptable, boolean paramBoolean)
/*      */   {
/*  110 */     JavaAdapter localJavaAdapter = new JavaAdapter();
/*  111 */     IdFunctionObject localIdFunctionObject = new IdFunctionObject(localJavaAdapter, FTAG, 1, "JavaAdapter", 1, paramScriptable);
/*      */ 
/*  113 */     localIdFunctionObject.markAsConstructor(null);
/*  114 */     if (paramBoolean) {
/*  115 */       localIdFunctionObject.sealObject();
/*      */     }
/*  117 */     localIdFunctionObject.exportAsScopeProperty();
/*      */   }
/*      */ 
/*      */   public Object execIdCall(IdFunctionObject paramIdFunctionObject, Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject)
/*      */   {
/*  123 */     if ((paramIdFunctionObject.hasTag(FTAG)) && 
/*  124 */       (paramIdFunctionObject.methodId() == 1)) {
/*  125 */       return js_createAdapter(paramContext, paramScriptable1, paramArrayOfObject);
/*      */     }
/*      */ 
/*  128 */     throw paramIdFunctionObject.unknown();
/*      */   }
/*      */ 
/*      */   public static Object convertResult(Object paramObject, Class<?> paramClass)
/*      */   {
/*  133 */     if ((paramObject == Undefined.instance) && (paramClass != ScriptRuntime.ObjectClass) && (paramClass != ScriptRuntime.StringClass))
/*      */     {
/*  138 */       return null;
/*      */     }
/*  140 */     return Context.jsToJava(paramObject, paramClass);
/*      */   }
/*      */ 
/*      */   public static Scriptable createAdapterWrapper(Scriptable paramScriptable, Object paramObject)
/*      */   {
/*  146 */     Scriptable localScriptable = ScriptableObject.getTopLevelScope(paramScriptable);
/*  147 */     NativeJavaObject localNativeJavaObject = new NativeJavaObject(localScriptable, paramObject, null, true);
/*  148 */     localNativeJavaObject.setPrototype(paramScriptable);
/*  149 */     return localNativeJavaObject;
/*      */   }
/*      */ 
/*      */   public static Object getAdapterSelf(Class<?> paramClass, Object paramObject)
/*      */     throws NoSuchFieldException, IllegalAccessException
/*      */   {
/*  155 */     Field localField = paramClass.getDeclaredField("self");
/*  156 */     return localField.get(paramObject);
/*      */   }
/*      */ 
/*      */   static Object js_createAdapter(Context paramContext, Scriptable paramScriptable, Object[] paramArrayOfObject)
/*      */   {
/*  161 */     int i = paramArrayOfObject.length;
/*  162 */     if (i == 0) {
/*  163 */       throw ScriptRuntime.typeError0("msg.adapter.zero.args");
/*      */     }
/*      */ 
/*  166 */     Object localObject1 = null;
/*  167 */     Class[] arrayOfClass1 = new Class[i - 1];
/*  168 */     int j = 0;
/*  169 */     for (int k = 0; k != i - 1; k++) {
/*  170 */       localObject2 = paramArrayOfObject[k];
/*  171 */       if (!(localObject2 instanceof NativeJavaClass)) {
/*  172 */         throw ScriptRuntime.typeError2("msg.not.java.class.arg", String.valueOf(k), ScriptRuntime.toString(localObject2));
/*      */       }
/*      */ 
/*  176 */       localClass = ((NativeJavaClass)localObject2).getClassObject();
/*  177 */       if (!localClass.isInterface()) {
/*  178 */         if (localObject1 != null) {
/*  179 */           throw ScriptRuntime.typeError2("msg.only.one.super", ((Class)localObject1).getName(), localClass.getName());
/*      */         }
/*      */ 
/*  182 */         localObject1 = localClass;
/*      */       } else {
/*  184 */         arrayOfClass1[(j++)] = localClass;
/*      */       }
/*      */     }
/*      */ 
/*  188 */     if (localObject1 == null) {
/*  189 */       localObject1 = ScriptRuntime.ObjectClass;
/*      */     }
/*  191 */     Class[] arrayOfClass2 = new Class[j];
/*  192 */     System.arraycopy(arrayOfClass1, 0, arrayOfClass2, 0, j);
/*  193 */     Object localObject2 = ScriptRuntime.toObject(paramContext, paramScriptable, paramArrayOfObject[(i - 1)]);
/*      */ 
/*  195 */     Class localClass = getAdapterClass(paramScriptable, (Class)localObject1, arrayOfClass2, (Scriptable)localObject2);
/*      */ 
/*  198 */     Class[] arrayOfClass3 = { ScriptRuntime.ContextFactoryClass, ScriptRuntime.ScriptableClass };
/*      */ 
/*  202 */     Object[] arrayOfObject = { paramContext.getFactory(), localObject2 };
/*      */     try {
/*  204 */       Object localObject3 = localClass.getConstructor(arrayOfClass3).newInstance(arrayOfObject);
/*      */ 
/*  206 */       Object localObject4 = getAdapterSelf(localClass, localObject3);
/*      */ 
/*  208 */       if ((localObject4 instanceof Wrapper)) {
/*  209 */         Object localObject5 = ((Wrapper)localObject4).unwrap();
/*  210 */         if ((localObject5 instanceof Scriptable)) {
/*  211 */           return localObject5;
/*      */         }
/*      */       }
/*  214 */       return localObject4;
/*      */     } catch (Exception localException) {
/*  216 */       throw Context.throwAsScriptRuntimeEx(localException);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void writeAdapterObject(Object paramObject, ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/*  225 */     Class localClass = paramObject.getClass();
/*  226 */     paramObjectOutputStream.writeObject(localClass.getSuperclass().getName());
/*      */ 
/*  228 */     Class[] arrayOfClass = localClass.getInterfaces();
/*  229 */     String[] arrayOfString = new String[arrayOfClass.length];
/*      */ 
/*  231 */     for (int i = 0; i < arrayOfClass.length; i++) {
/*  232 */       arrayOfString[i] = arrayOfClass[i].getName();
/*      */     }
/*  234 */     paramObjectOutputStream.writeObject(arrayOfString);
/*      */     try
/*      */     {
/*  237 */       Object localObject = localClass.getField("delegee").get(paramObject);
/*  238 */       paramObjectOutputStream.writeObject(localObject);
/*  239 */       return;
/*      */     } catch (IllegalAccessException localIllegalAccessException) {
/*      */     } catch (NoSuchFieldException localNoSuchFieldException) {
/*      */     }
/*  243 */     throw new IOException();
/*      */   }
/*      */ 
/*      */   public static Object readAdapterObject(Scriptable paramScriptable, ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  252 */     Context localContext = Context.getCurrentContext();
/*      */     ContextFactory localContextFactory;
/*  253 */     if (localContext != null)
/*  254 */       localContextFactory = localContext.getFactory();
/*      */     else {
/*  256 */       localContextFactory = null;
/*      */     }
/*      */ 
/*  259 */     Class localClass1 = Class.forName((String)paramObjectInputStream.readObject());
/*      */ 
/*  261 */     String[] arrayOfString = (String[])paramObjectInputStream.readObject();
/*  262 */     Class[] arrayOfClass1 = new Class[arrayOfString.length];
/*      */ 
/*  264 */     for (int i = 0; i < arrayOfString.length; i++) {
/*  265 */       arrayOfClass1[i] = Class.forName(arrayOfString[i]);
/*      */     }
/*  267 */     Scriptable localScriptable = (Scriptable)paramObjectInputStream.readObject();
/*      */ 
/*  269 */     Class localClass2 = getAdapterClass(paramScriptable, localClass1, arrayOfClass1, localScriptable);
/*      */ 
/*  272 */     Class[] arrayOfClass2 = { ScriptRuntime.ContextFactoryClass, ScriptRuntime.ScriptableClass, ScriptRuntime.ScriptableClass };
/*      */ 
/*  277 */     Object[] arrayOfObject = { localContextFactory, localScriptable, paramScriptable };
/*      */     try {
/*  279 */       return localClass2.getConstructor(arrayOfClass2).newInstance(arrayOfObject);
/*      */     } catch (InstantiationException localInstantiationException) {
/*      */     } catch (IllegalAccessException localIllegalAccessException) {
/*      */     } catch (InvocationTargetException localInvocationTargetException) {
/*      */     }
/*      */     catch (NoSuchMethodException localNoSuchMethodException) {
/*      */     }
/*  286 */     throw new ClassNotFoundException("adapter");
/*      */   }
/*      */ 
/*      */   private static ObjToIntMap getObjectFunctionNames(Scriptable paramScriptable)
/*      */   {
/*  291 */     Object[] arrayOfObject = ScriptableObject.getPropertyIds(paramScriptable);
/*  292 */     ObjToIntMap localObjToIntMap = new ObjToIntMap(arrayOfObject.length);
/*  293 */     for (int i = 0; i != arrayOfObject.length; i++)
/*  294 */       if ((arrayOfObject[i] instanceof String))
/*      */       {
/*  296 */         String str = (String)arrayOfObject[i];
/*  297 */         Object localObject = ScriptableObject.getProperty(paramScriptable, str);
/*  298 */         if ((localObject instanceof Function)) {
/*  299 */           Function localFunction = (Function)localObject;
/*  300 */           int j = ScriptRuntime.toInt32(ScriptableObject.getProperty(localFunction, "length"));
/*      */ 
/*  302 */           if (j < 0) {
/*  303 */             j = 0;
/*      */           }
/*  305 */           localObjToIntMap.put(str, j);
/*      */         }
/*      */       }
/*  308 */     return localObjToIntMap;
/*      */   }
/*      */ 
/*      */   private static Class<?> getAdapterClass(Scriptable paramScriptable1, Class<?> paramClass, Class<?>[] paramArrayOfClass, Scriptable paramScriptable2)
/*      */   {
/*  314 */     ClassCache localClassCache = ClassCache.get(paramScriptable1);
/*  315 */     Map localMap = localClassCache.getInterfaceAdapterCacheMap();
/*      */ 
/*  318 */     ObjToIntMap localObjToIntMap = getObjectFunctionNames(paramScriptable2);
/*      */ 
/*  320 */     JavaAdapterSignature localJavaAdapterSignature = new JavaAdapterSignature(paramClass, paramArrayOfClass, localObjToIntMap);
/*  321 */     Class localClass = (Class)localMap.get(localJavaAdapterSignature);
/*  322 */     if (localClass == null) {
/*  323 */       String str = "adapter" + localClassCache.newClassSerialNumber();
/*      */ 
/*  325 */       byte[] arrayOfByte = createAdapterCode(localObjToIntMap, str, paramClass, paramArrayOfClass, null);
/*      */ 
/*  328 */       localClass = loadAdapterClass(str, arrayOfByte);
/*  329 */       if (localClassCache.isCachingEnabled()) {
/*  330 */         localMap.put(localJavaAdapterSignature, localClass);
/*      */       }
/*      */     }
/*  333 */     return localClass;
/*      */   }
/*      */ 
/*      */   public static byte[] createAdapterCode(ObjToIntMap paramObjToIntMap, String paramString1, Class<?> paramClass, Class<?>[] paramArrayOfClass, String paramString2)
/*      */   {
/*  342 */     ClassFileWriter localClassFileWriter = new ClassFileWriter(paramString1, paramClass.getName(), "<adapter>");
/*      */ 
/*  345 */     localClassFileWriter.addField("factory", "Lsun/org/mozilla/javascript/internal/ContextFactory;", (short)17);
/*      */ 
/*  348 */     localClassFileWriter.addField("delegee", "Lsun/org/mozilla/javascript/internal/Scriptable;", (short)17);
/*      */ 
/*  351 */     localClassFileWriter.addField("self", "Lsun/org/mozilla/javascript/internal/Scriptable;", (short)17);
/*      */ 
/*  354 */     int i = paramArrayOfClass == null ? 0 : paramArrayOfClass.length;
/*  355 */     for (int j = 0; j < i; j++) {
/*  356 */       if (paramArrayOfClass[j] != null) {
/*  357 */         localClassFileWriter.addInterface(paramArrayOfClass[j].getName());
/*      */       }
/*      */     }
/*  360 */     String str1 = paramClass.getName().replace('.', '/');
/*  361 */     generateCtor(localClassFileWriter, paramString1, str1);
/*  362 */     generateSerialCtor(localClassFileWriter, paramString1, str1);
/*  363 */     if (paramString2 != null) {
/*  364 */       generateEmptyCtor(localClassFileWriter, paramString1, str1, paramString2);
/*      */     }
/*  366 */     ObjToIntMap localObjToIntMap1 = new ObjToIntMap();
/*  367 */     ObjToIntMap localObjToIntMap2 = new ObjToIntMap();
/*      */     String str2;
/*      */     Class[] arrayOfClass2;
/*      */     String str3;
/*      */     String str4;
/*  370 */     for (int k = 0; k < i; k++) {
/*  371 */       Method[] arrayOfMethod2 = paramArrayOfClass[k].getMethods();
/*  372 */       for (int n = 0; n < arrayOfMethod2.length; n++) {
/*  373 */         Method localMethod = arrayOfMethod2[n];
/*  374 */         int i2 = localMethod.getModifiers();
/*  375 */         if ((!Modifier.isStatic(i2)) && (!Modifier.isFinal(i2)))
/*      */         {
/*  378 */           str2 = localMethod.getName();
/*  379 */           arrayOfClass2 = localMethod.getParameterTypes();
/*  380 */           if (!paramObjToIntMap.has(str2)) {
/*      */             try {
/*  382 */               paramClass.getMethod(str2, arrayOfClass2);
/*      */             }
/*      */             catch (NoSuchMethodException localNoSuchMethodException)
/*      */             {
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  393 */             str3 = getMethodSignature(localMethod, arrayOfClass2);
/*  394 */             str4 = str2 + str3;
/*  395 */             if (!localObjToIntMap1.has(str4)) {
/*  396 */               generateMethod(localClassFileWriter, paramString1, str2, arrayOfClass2, localMethod.getReturnType());
/*      */ 
/*  398 */               localObjToIntMap1.put(str4, 0);
/*  399 */               localObjToIntMap2.put(str2, 0);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  408 */     Method[] arrayOfMethod1 = getOverridableMethods(paramClass);
/*      */     Object localObject;
/*      */     int i1;
/*  409 */     for (int m = 0; m < arrayOfMethod1.length; m++) {
/*  410 */       localObject = arrayOfMethod1[m];
/*  411 */       i1 = ((Method)localObject).getModifiers();
/*      */ 
/*  415 */       boolean bool = Modifier.isAbstract(i1);
/*  416 */       str2 = ((Method)localObject).getName();
/*  417 */       if ((bool) || (paramObjToIntMap.has(str2)))
/*      */       {
/*  420 */         arrayOfClass2 = ((Method)localObject).getParameterTypes();
/*  421 */         str3 = getMethodSignature((Method)localObject, arrayOfClass2);
/*  422 */         str4 = str2 + str3;
/*  423 */         if (!localObjToIntMap1.has(str4)) {
/*  424 */           generateMethod(localClassFileWriter, paramString1, str2, arrayOfClass2, ((Method)localObject).getReturnType());
/*      */ 
/*  426 */           localObjToIntMap1.put(str4, 0);
/*  427 */           localObjToIntMap2.put(str2, 0);
/*      */ 
/*  431 */           if (!bool) {
/*  432 */             generateSuper(localClassFileWriter, paramString1, str1, str2, str3, arrayOfClass2, ((Method)localObject).getReturnType());
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  442 */     ObjToIntMap.Iterator localIterator = new ObjToIntMap.Iterator(paramObjToIntMap);
/*  443 */     for (localIterator.start(); !localIterator.done(); localIterator.next()) {
/*  444 */       localObject = (String)localIterator.getKey();
/*  445 */       if (!localObjToIntMap2.has(localObject))
/*      */       {
/*  447 */         i1 = localIterator.getValue();
/*  448 */         Class[] arrayOfClass1 = new Class[i1];
/*  449 */         for (int i3 = 0; i3 < i1; i3++)
/*  450 */           arrayOfClass1[i3] = ScriptRuntime.ObjectClass;
/*  451 */         generateMethod(localClassFileWriter, paramString1, (String)localObject, arrayOfClass1, ScriptRuntime.ObjectClass);
/*      */       }
/*      */     }
/*  454 */     return localClassFileWriter.toByteArray();
/*      */   }
/*      */ 
/*      */   static Method[] getOverridableMethods(Class<?> paramClass)
/*      */   {
/*  459 */     ArrayList localArrayList = new ArrayList();
/*  460 */     HashSet localHashSet = new HashSet();
/*      */ 
/*  465 */     for (Object localObject = paramClass; localObject != null; localObject = ((Class)localObject).getSuperclass()) {
/*  466 */       appendOverridableMethods((Class)localObject, localArrayList, localHashSet);
/*      */     }
/*  468 */     for (localObject = paramClass; localObject != null; localObject = ((Class)localObject).getSuperclass()) {
/*  469 */       for (Class localClass : ((Class)localObject).getInterfaces())
/*  470 */         appendOverridableMethods(localClass, localArrayList, localHashSet);
/*      */     }
/*  472 */     return (Method[])localArrayList.toArray(new Method[localArrayList.size()]);
/*      */   }
/*      */ 
/*      */   private static void appendOverridableMethods(Class<?> paramClass, ArrayList<Method> paramArrayList, HashSet<String> paramHashSet)
/*      */   {
/*  478 */     Method[] arrayOfMethod = paramClass.getDeclaredMethods();
/*  479 */     for (int i = 0; i < arrayOfMethod.length; i++) {
/*  480 */       String str = arrayOfMethod[i].getName() + getMethodSignature(arrayOfMethod[i], arrayOfMethod[i].getParameterTypes());
/*      */ 
/*  483 */       if (!paramHashSet.contains(str))
/*      */       {
/*  485 */         int j = arrayOfMethod[i].getModifiers();
/*  486 */         if (!Modifier.isStatic(j))
/*      */         {
/*  488 */           if (Modifier.isFinal(j))
/*      */           {
/*  491 */             paramHashSet.add(str);
/*      */           }
/*  494 */           else if ((Modifier.isPublic(j)) || (Modifier.isProtected(j))) {
/*  495 */             paramArrayList.add(arrayOfMethod[i]);
/*  496 */             paramHashSet.add(str);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static Class<?> loadAdapterClass(String paramString, byte[] paramArrayOfByte) {
/*  504 */     Class localClass1 = SecurityController.getStaticSecurityDomainClass();
/*      */     Object localObject1;
/*  505 */     if ((localClass1 == CodeSource.class) || (localClass1 == ProtectionDomain.class))
/*      */     {
/*  507 */       localObject2 = SecurityUtilities.getScriptProtectionDomain();
/*  508 */       if (localObject2 == null) {
/*  509 */         localObject2 = JavaAdapter.class.getProtectionDomain();
/*      */       }
/*  511 */       if (localClass1 == CodeSource.class) {
/*  512 */         localObject1 = localObject2 == null ? null : ((ProtectionDomain)localObject2).getCodeSource();
/*      */       }
/*      */       else
/*  515 */         localObject1 = localObject2;
/*      */     }
/*      */     else
/*      */     {
/*  519 */       localObject1 = null;
/*      */     }
/*  521 */     Object localObject2 = SecurityController.createLoader(null, localObject1);
/*      */ 
/*  523 */     Class localClass2 = ((GeneratedClassLoader)localObject2).defineClass(paramString, paramArrayOfByte);
/*  524 */     ((GeneratedClassLoader)localObject2).linkClass(localClass2);
/*  525 */     return localClass2;
/*      */   }
/*      */ 
/*      */   public static Function getFunction(Scriptable paramScriptable, String paramString)
/*      */   {
/*  530 */     Object localObject = ScriptableObject.getProperty(paramScriptable, paramString);
/*  531 */     if (localObject == Scriptable.NOT_FOUND)
/*      */     {
/*  537 */       return null;
/*      */     }
/*  539 */     if (!(localObject instanceof Function)) {
/*  540 */       throw ScriptRuntime.notFunctionError(localObject, paramString);
/*      */     }
/*  542 */     return (Function)localObject;
/*      */   }
/*      */ 
/*      */   public static Object callMethod(ContextFactory paramContextFactory, final Scriptable paramScriptable, final Function paramFunction, final Object[] paramArrayOfObject, final long paramLong)
/*      */   {
/*  554 */     if (paramFunction == null)
/*      */     {
/*  556 */       return Undefined.instance;
/*      */     }
/*  558 */     if (paramContextFactory == null) {
/*  559 */       paramContextFactory = ContextFactory.getGlobal();
/*      */     }
/*      */ 
/*  562 */     Scriptable localScriptable = paramFunction.getParentScope();
/*  563 */     if (paramLong == 0L) {
/*  564 */       return Context.call(paramContextFactory, paramFunction, localScriptable, paramScriptable, paramArrayOfObject);
/*      */     }
/*      */ 
/*  567 */     Context localContext = Context.getCurrentContext();
/*  568 */     if (localContext != null) {
/*  569 */       return doCall(localContext, localScriptable, paramScriptable, paramFunction, paramArrayOfObject, paramLong);
/*      */     }
/*  571 */     return paramContextFactory.call(new ContextAction()
/*      */     {
/*      */       public Object run(Context paramAnonymousContext) {
/*  574 */         return JavaAdapter.doCall(paramAnonymousContext, this.val$scope, paramScriptable, paramFunction, paramArrayOfObject, paramLong);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static Object doCall(Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Function paramFunction, Object[] paramArrayOfObject, long paramLong)
/*      */   {
/*  585 */     for (int i = 0; i != paramArrayOfObject.length; i++) {
/*  586 */       if (0L != (paramLong & 1 << i)) {
/*  587 */         Object localObject = paramArrayOfObject[i];
/*  588 */         if (!(localObject instanceof Scriptable)) {
/*  589 */           paramArrayOfObject[i] = paramContext.getWrapFactory().wrap(paramContext, paramScriptable1, localObject, null);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  594 */     return paramFunction.call(paramContext, paramScriptable1, paramScriptable2, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public static Scriptable runScript(Script paramScript)
/*      */   {
/*  599 */     return (Scriptable)ContextFactory.getGlobal().call(new ContextAction()
/*      */     {
/*      */       public Object run(Context paramAnonymousContext)
/*      */       {
/*  603 */         ScriptableObject localScriptableObject = ScriptRuntime.getGlobal(paramAnonymousContext);
/*  604 */         this.val$script.exec(paramAnonymousContext, localScriptableObject);
/*  605 */         return localScriptableObject;
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static void generateCtor(ClassFileWriter paramClassFileWriter, String paramString1, String paramString2)
/*      */   {
/*  613 */     paramClassFileWriter.startMethod("<init>", "(Lsun/org/mozilla/javascript/internal/ContextFactory;Lsun/org/mozilla/javascript/internal/Scriptable;)V", (short)1);
/*      */ 
/*  619 */     paramClassFileWriter.add(42);
/*  620 */     paramClassFileWriter.addInvoke(183, paramString2, "<init>", "()V");
/*      */ 
/*  623 */     paramClassFileWriter.add(42);
/*  624 */     paramClassFileWriter.add(43);
/*  625 */     paramClassFileWriter.add(181, paramString1, "factory", "Lsun/org/mozilla/javascript/internal/ContextFactory;");
/*      */ 
/*  629 */     paramClassFileWriter.add(42);
/*  630 */     paramClassFileWriter.add(44);
/*  631 */     paramClassFileWriter.add(181, paramString1, "delegee", "Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  634 */     paramClassFileWriter.add(42);
/*      */ 
/*  636 */     paramClassFileWriter.add(44);
/*  637 */     paramClassFileWriter.add(42);
/*  638 */     paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/JavaAdapter", "createAdapterWrapper", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/Object;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  644 */     paramClassFileWriter.add(181, paramString1, "self", "Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  647 */     paramClassFileWriter.add(177);
/*  648 */     paramClassFileWriter.stopMethod((short)3);
/*      */   }
/*      */ 
/*      */   private static void generateSerialCtor(ClassFileWriter paramClassFileWriter, String paramString1, String paramString2)
/*      */   {
/*  655 */     paramClassFileWriter.startMethod("<init>", "(Lsun/org/mozilla/javascript/internal/ContextFactory;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;)V", (short)1);
/*      */ 
/*  663 */     paramClassFileWriter.add(42);
/*  664 */     paramClassFileWriter.addInvoke(183, paramString2, "<init>", "()V");
/*      */ 
/*  667 */     paramClassFileWriter.add(42);
/*  668 */     paramClassFileWriter.add(43);
/*  669 */     paramClassFileWriter.add(181, paramString1, "factory", "Lsun/org/mozilla/javascript/internal/ContextFactory;");
/*      */ 
/*  673 */     paramClassFileWriter.add(42);
/*  674 */     paramClassFileWriter.add(44);
/*  675 */     paramClassFileWriter.add(181, paramString1, "delegee", "Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  678 */     paramClassFileWriter.add(42);
/*  679 */     paramClassFileWriter.add(45);
/*  680 */     paramClassFileWriter.add(181, paramString1, "self", "Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  683 */     paramClassFileWriter.add(177);
/*  684 */     paramClassFileWriter.stopMethod((short)4);
/*      */   }
/*      */ 
/*      */   private static void generateEmptyCtor(ClassFileWriter paramClassFileWriter, String paramString1, String paramString2, String paramString3)
/*      */   {
/*  692 */     paramClassFileWriter.startMethod("<init>", "()V", (short)1);
/*      */ 
/*  695 */     paramClassFileWriter.add(42);
/*  696 */     paramClassFileWriter.addInvoke(183, paramString2, "<init>", "()V");
/*      */ 
/*  699 */     paramClassFileWriter.add(42);
/*  700 */     paramClassFileWriter.add(1);
/*  701 */     paramClassFileWriter.add(181, paramString1, "factory", "Lsun/org/mozilla/javascript/internal/ContextFactory;");
/*      */ 
/*  705 */     paramClassFileWriter.add(187, paramString3);
/*  706 */     paramClassFileWriter.add(89);
/*  707 */     paramClassFileWriter.addInvoke(183, paramString3, "<init>", "()V");
/*      */ 
/*  710 */     paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/JavaAdapter", "runScript", "(Lsun/org/mozilla/javascript/internal/Script;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  715 */     paramClassFileWriter.add(76);
/*      */ 
/*  718 */     paramClassFileWriter.add(42);
/*  719 */     paramClassFileWriter.add(43);
/*  720 */     paramClassFileWriter.add(181, paramString1, "delegee", "Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  723 */     paramClassFileWriter.add(42);
/*      */ 
/*  725 */     paramClassFileWriter.add(43);
/*  726 */     paramClassFileWriter.add(42);
/*  727 */     paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/JavaAdapter", "createAdapterWrapper", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/Object;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  733 */     paramClassFileWriter.add(181, paramString1, "self", "Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  736 */     paramClassFileWriter.add(177);
/*  737 */     paramClassFileWriter.stopMethod((short)2);
/*      */   }
/*      */ 
/*      */   static void generatePushWrappedArgs(ClassFileWriter paramClassFileWriter, Class<?>[] paramArrayOfClass, int paramInt)
/*      */   {
/*  750 */     paramClassFileWriter.addPush(paramInt);
/*  751 */     paramClassFileWriter.add(189, "java/lang/Object");
/*  752 */     int i = 1;
/*  753 */     for (int j = 0; j != paramArrayOfClass.length; j++) {
/*  754 */       paramClassFileWriter.add(89);
/*  755 */       paramClassFileWriter.addPush(j);
/*  756 */       i += generateWrapArg(paramClassFileWriter, i, paramArrayOfClass[j]);
/*  757 */       paramClassFileWriter.add(83);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static int generateWrapArg(ClassFileWriter paramClassFileWriter, int paramInt, Class<?> paramClass)
/*      */   {
/*  769 */     int i = 1;
/*  770 */     if (!paramClass.isPrimitive()) {
/*  771 */       paramClassFileWriter.add(25, paramInt);
/*      */     }
/*  773 */     else if (paramClass == Boolean.TYPE)
/*      */     {
/*  775 */       paramClassFileWriter.add(187, "java/lang/Boolean");
/*  776 */       paramClassFileWriter.add(89);
/*  777 */       paramClassFileWriter.add(21, paramInt);
/*  778 */       paramClassFileWriter.addInvoke(183, "java/lang/Boolean", "<init>", "(Z)V");
/*      */     }
/*  781 */     else if (paramClass == Character.TYPE)
/*      */     {
/*  783 */       paramClassFileWriter.add(21, paramInt);
/*  784 */       paramClassFileWriter.addInvoke(184, "java/lang/String", "valueOf", "(C)Ljava/lang/String;");
/*      */     }
/*      */     else
/*      */     {
/*  789 */       paramClassFileWriter.add(187, "java/lang/Double");
/*  790 */       paramClassFileWriter.add(89);
/*  791 */       String str = paramClass.getName();
/*  792 */       switch (str.charAt(0))
/*      */       {
/*      */       case 'b':
/*      */       case 'i':
/*      */       case 's':
/*  797 */         paramClassFileWriter.add(21, paramInt);
/*  798 */         paramClassFileWriter.add(135);
/*  799 */         break;
/*      */       case 'l':
/*  802 */         paramClassFileWriter.add(22, paramInt);
/*  803 */         paramClassFileWriter.add(138);
/*  804 */         i = 2;
/*  805 */         break;
/*      */       case 'f':
/*  808 */         paramClassFileWriter.add(23, paramInt);
/*  809 */         paramClassFileWriter.add(141);
/*  810 */         break;
/*      */       case 'd':
/*  812 */         paramClassFileWriter.add(24, paramInt);
/*  813 */         i = 2;
/*      */       case 'c':
/*      */       case 'e':
/*      */       case 'g':
/*      */       case 'h':
/*      */       case 'j':
/*      */       case 'k':
/*      */       case 'm':
/*      */       case 'n':
/*      */       case 'o':
/*      */       case 'p':
/*      */       case 'q':
/*  816 */       case 'r': } paramClassFileWriter.addInvoke(183, "java/lang/Double", "<init>", "(D)V");
/*      */     }
/*      */ 
/*  819 */     return i;
/*      */   }
/*      */ 
/*      */   static void generateReturnResult(ClassFileWriter paramClassFileWriter, Class<?> paramClass, boolean paramBoolean)
/*      */   {
/*  832 */     if (paramClass == Void.TYPE) {
/*  833 */       paramClassFileWriter.add(87);
/*  834 */       paramClassFileWriter.add(177);
/*      */     }
/*  836 */     else if (paramClass == Boolean.TYPE) {
/*  837 */       paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/Context", "toBoolean", "(Ljava/lang/Object;)Z");
/*      */ 
/*  840 */       paramClassFileWriter.add(172);
/*      */     }
/*  842 */     else if (paramClass == Character.TYPE)
/*      */     {
/*  846 */       paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/Context", "toString", "(Ljava/lang/Object;)Ljava/lang/String;");
/*      */ 
/*  850 */       paramClassFileWriter.add(3);
/*  851 */       paramClassFileWriter.addInvoke(182, "java/lang/String", "charAt", "(I)C");
/*      */ 
/*  853 */       paramClassFileWriter.add(172);
/*      */     }
/*      */     else
/*      */     {
/*      */       String str;
/*  855 */       if (paramClass.isPrimitive()) {
/*  856 */         paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/Context", "toNumber", "(Ljava/lang/Object;)D");
/*      */ 
/*  859 */         str = paramClass.getName();
/*  860 */         switch (str.charAt(0)) {
/*      */         case 'b':
/*      */         case 'i':
/*      */         case 's':
/*  864 */           paramClassFileWriter.add(142);
/*  865 */           paramClassFileWriter.add(172);
/*  866 */           break;
/*      */         case 'l':
/*  868 */           paramClassFileWriter.add(143);
/*  869 */           paramClassFileWriter.add(173);
/*  870 */           break;
/*      */         case 'f':
/*  872 */           paramClassFileWriter.add(144);
/*  873 */           paramClassFileWriter.add(174);
/*  874 */           break;
/*      */         case 'd':
/*  876 */           paramClassFileWriter.add(175);
/*  877 */           break;
/*      */         case 'c':
/*      */         case 'e':
/*      */         case 'g':
/*      */         case 'h':
/*      */         case 'j':
/*      */         case 'k':
/*      */         case 'm':
/*      */         case 'n':
/*      */         case 'o':
/*      */         case 'p':
/*      */         case 'q':
/*      */         case 'r':
/*      */         default:
/*  879 */           throw new RuntimeException("Unexpected return type " + paramClass.toString());
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  884 */         str = paramClass.getName();
/*  885 */         if (paramBoolean) {
/*  886 */           paramClassFileWriter.addLoadConstant(str);
/*  887 */           paramClassFileWriter.addInvoke(184, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
/*      */ 
/*  892 */           paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/JavaAdapter", "convertResult", "(Ljava/lang/Object;Ljava/lang/Class;)Ljava/lang/Object;");
/*      */         }
/*      */ 
/*  900 */         paramClassFileWriter.add(192, str);
/*  901 */         paramClassFileWriter.add(176);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void generateMethod(ClassFileWriter paramClassFileWriter, String paramString1, String paramString2, Class<?>[] paramArrayOfClass, Class<?> paramClass)
/*      */   {
/*  909 */     StringBuffer localStringBuffer = new StringBuffer();
/*  910 */     int i = appendMethodSignature(paramArrayOfClass, paramClass, localStringBuffer);
/*  911 */     String str = localStringBuffer.toString();
/*  912 */     paramClassFileWriter.startMethod(paramString2, str, (short)1);
/*      */ 
/*  918 */     paramClassFileWriter.add(42);
/*  919 */     paramClassFileWriter.add(180, paramString1, "factory", "Lsun/org/mozilla/javascript/internal/ContextFactory;");
/*      */ 
/*  923 */     paramClassFileWriter.add(42);
/*  924 */     paramClassFileWriter.add(180, paramString1, "self", "Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  928 */     paramClassFileWriter.add(42);
/*  929 */     paramClassFileWriter.add(180, paramString1, "delegee", "Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  931 */     paramClassFileWriter.addPush(paramString2);
/*  932 */     paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/JavaAdapter", "getFunction", "(Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/String;)Lsun/org/mozilla/javascript/internal/Function;");
/*      */ 
/*  940 */     generatePushWrappedArgs(paramClassFileWriter, paramArrayOfClass, paramArrayOfClass.length);
/*      */ 
/*  943 */     if (paramArrayOfClass.length > 64)
/*      */     {
/*  946 */       throw Context.reportRuntimeError0("JavaAdapter can not subclass methods with more then 64 arguments.");
/*      */     }
/*      */ 
/*  950 */     long l = 0L;
/*  951 */     for (int j = 0; j != paramArrayOfClass.length; j++) {
/*  952 */       if (!paramArrayOfClass[j].isPrimitive()) {
/*  953 */         l |= 1 << j;
/*      */       }
/*      */     }
/*  956 */     paramClassFileWriter.addPush(l);
/*      */ 
/*  960 */     paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/JavaAdapter", "callMethod", "(Lsun/org/mozilla/javascript/internal/ContextFactory;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Function;[Ljava/lang/Object;J)Ljava/lang/Object;");
/*      */ 
/*  970 */     generateReturnResult(paramClassFileWriter, paramClass, true);
/*      */ 
/*  972 */     paramClassFileWriter.stopMethod((short)i);
/*      */   }
/*      */ 
/*      */   private static int generatePushParam(ClassFileWriter paramClassFileWriter, int paramInt, Class<?> paramClass)
/*      */   {
/*  982 */     if (!paramClass.isPrimitive()) {
/*  983 */       paramClassFileWriter.addALoad(paramInt);
/*  984 */       return 1;
/*      */     }
/*  986 */     String str = paramClass.getName();
/*  987 */     switch (str.charAt(0))
/*      */     {
/*      */     case 'b':
/*      */     case 'c':
/*      */     case 'i':
/*      */     case 's':
/*      */     case 'z':
/*  994 */       paramClassFileWriter.addILoad(paramInt);
/*  995 */       return 1;
/*      */     case 'l':
/*  998 */       paramClassFileWriter.addLLoad(paramInt);
/*  999 */       return 2;
/*      */     case 'f':
/* 1002 */       paramClassFileWriter.addFLoad(paramInt);
/* 1003 */       return 1;
/*      */     case 'd':
/* 1005 */       paramClassFileWriter.addDLoad(paramInt);
/* 1006 */       return 2;
/*      */     case 'e':
/*      */     case 'g':
/*      */     case 'h':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'm':
/*      */     case 'n':
/*      */     case 'o':
/*      */     case 'p':
/*      */     case 'q':
/*      */     case 'r':
/*      */     case 't':
/*      */     case 'u':
/*      */     case 'v':
/*      */     case 'w':
/*      */     case 'x':
/* 1008 */     case 'y': } throw Kit.codeBug();
/*      */   }
/*      */ 
/*      */   private static void generatePopResult(ClassFileWriter paramClassFileWriter, Class<?> paramClass)
/*      */   {
/* 1019 */     if (paramClass.isPrimitive()) {
/* 1020 */       String str = paramClass.getName();
/* 1021 */       switch (str.charAt(0)) {
/*      */       case 'b':
/*      */       case 'c':
/*      */       case 'i':
/*      */       case 's':
/*      */       case 'z':
/* 1027 */         paramClassFileWriter.add(172);
/* 1028 */         break;
/*      */       case 'l':
/* 1030 */         paramClassFileWriter.add(173);
/* 1031 */         break;
/*      */       case 'f':
/* 1033 */         paramClassFileWriter.add(174);
/* 1034 */         break;
/*      */       case 'd':
/* 1036 */         paramClassFileWriter.add(175);
/*      */       case 'e':
/*      */       case 'g':
/*      */       case 'h':
/*      */       case 'j':
/*      */       case 'k':
/*      */       case 'm':
/*      */       case 'n':
/*      */       case 'o':
/*      */       case 'p':
/*      */       case 'q':
/*      */       case 'r':
/*      */       case 't':
/*      */       case 'u':
/*      */       case 'v':
/*      */       case 'w':
/*      */       case 'x':
/* 1040 */       case 'y': }  } else { paramClassFileWriter.add(176); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private static void generateSuper(ClassFileWriter paramClassFileWriter, String paramString1, String paramString2, String paramString3, String paramString4, Class<?>[] paramArrayOfClass, Class<?> paramClass)
/*      */   {
/* 1054 */     paramClassFileWriter.startMethod("super$" + paramString3, paramString4, (short)1);
/*      */ 
/* 1058 */     paramClassFileWriter.add(25, 0);
/*      */ 
/* 1061 */     int i = 1;
/* 1062 */     for (int j = 0; j < paramArrayOfClass.length; j++) {
/* 1063 */       i += generatePushParam(paramClassFileWriter, i, paramArrayOfClass[j]);
/*      */     }
/*      */ 
/* 1067 */     paramClassFileWriter.addInvoke(183, paramString2, paramString3, paramString4);
/*      */ 
/* 1073 */     Class<?> localClass = paramClass;
/* 1074 */     if (!localClass.equals(Void.TYPE))
/* 1075 */       generatePopResult(paramClassFileWriter, localClass);
/*      */     else {
/* 1077 */       paramClassFileWriter.add(177);
/*      */     }
/* 1079 */     paramClassFileWriter.stopMethod((short)(i + 1));
/*      */   }
/*      */ 
/*      */   private static String getMethodSignature(Method paramMethod, Class<?>[] paramArrayOfClass)
/*      */   {
/* 1087 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1088 */     appendMethodSignature(paramArrayOfClass, paramMethod.getReturnType(), localStringBuffer);
/* 1089 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   static int appendMethodSignature(Class<?>[] paramArrayOfClass, Class<?> paramClass, StringBuffer paramStringBuffer)
/*      */   {
/* 1096 */     paramStringBuffer.append('(');
/* 1097 */     int i = 1 + paramArrayOfClass.length;
/* 1098 */     for (int j = 0; j < paramArrayOfClass.length; j++) {
/* 1099 */       Class<?> localClass = paramArrayOfClass[j];
/* 1100 */       appendTypeString(paramStringBuffer, localClass);
/* 1101 */       if ((localClass == Long.TYPE) || (localClass == Double.TYPE))
/*      */       {
/* 1103 */         i++;
/*      */       }
/*      */     }
/* 1106 */     paramStringBuffer.append(')');
/* 1107 */     appendTypeString(paramStringBuffer, paramClass);
/* 1108 */     return i;
/*      */   }
/*      */ 
/*      */   private static StringBuffer appendTypeString(StringBuffer paramStringBuffer, Class<?> paramClass)
/*      */   {
/* 1113 */     while (paramClass.isArray()) {
/* 1114 */       paramStringBuffer.append('[');
/* 1115 */       paramClass = paramClass.getComponentType();
/*      */     }
/* 1117 */     if (paramClass.isPrimitive())
/*      */     {
/*      */       char c;
/* 1119 */       if (paramClass == Boolean.TYPE) {
/* 1120 */         c = 'Z';
/* 1121 */       } else if (paramClass == Long.TYPE) {
/* 1122 */         c = 'J';
/*      */       } else {
/* 1124 */         String str = paramClass.getName();
/* 1125 */         c = Character.toUpperCase(str.charAt(0));
/*      */       }
/* 1127 */       paramStringBuffer.append(c);
/*      */     } else {
/* 1129 */       paramStringBuffer.append('L');
/* 1130 */       paramStringBuffer.append(paramClass.getName().replace('.', '/'));
/* 1131 */       paramStringBuffer.append(';');
/*      */     }
/* 1133 */     return paramStringBuffer;
/*      */   }
/*      */ 
/*      */   static int[] getArgsToConvert(Class<?>[] paramArrayOfClass)
/*      */   {
/* 1138 */     int i = 0;
/* 1139 */     for (int j = 0; j != paramArrayOfClass.length; j++) {
/* 1140 */       if (!paramArrayOfClass[j].isPrimitive())
/* 1141 */         i++;
/*      */     }
/* 1143 */     if (i == 0)
/* 1144 */       return null;
/* 1145 */     int[] arrayOfInt = new int[i];
/* 1146 */     i = 0;
/* 1147 */     for (int k = 0; k != paramArrayOfClass.length; k++) {
/* 1148 */       if (!paramArrayOfClass[k].isPrimitive())
/* 1149 */         arrayOfInt[(i++)] = k;
/*      */     }
/* 1151 */     return arrayOfInt;
/*      */   }
/*      */ 
/*      */   static class JavaAdapterSignature
/*      */   {
/*      */     Class<?> superClass;
/*      */     Class<?>[] interfaces;
/*      */     ObjToIntMap names;
/*      */ 
/*      */     JavaAdapterSignature(Class<?> paramClass, Class<?>[] paramArrayOfClass, ObjToIntMap paramObjToIntMap)
/*      */     {
/*   69 */       this.superClass = paramClass;
/*   70 */       this.interfaces = paramArrayOfClass;
/*   71 */       this.names = paramObjToIntMap;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject)
/*      */     {
/*   77 */       if (!(paramObject instanceof JavaAdapterSignature))
/*   78 */         return false;
/*   79 */       JavaAdapterSignature localJavaAdapterSignature = (JavaAdapterSignature)paramObject;
/*   80 */       if (this.superClass != localJavaAdapterSignature.superClass)
/*   81 */         return false;
/*   82 */       if (this.interfaces != localJavaAdapterSignature.interfaces) {
/*   83 */         if (this.interfaces.length != localJavaAdapterSignature.interfaces.length)
/*   84 */           return false;
/*   85 */         for (int i = 0; i < this.interfaces.length; i++)
/*   86 */           if (this.interfaces[i] != localJavaAdapterSignature.interfaces[i])
/*   87 */             return false;
/*      */       }
/*   89 */       if (this.names.size() != localJavaAdapterSignature.names.size())
/*   90 */         return false;
/*   91 */       ObjToIntMap.Iterator localIterator = new ObjToIntMap.Iterator(this.names);
/*   92 */       for (localIterator.start(); !localIterator.done(); localIterator.next()) {
/*   93 */         String str = (String)localIterator.getKey();
/*   94 */         int j = localIterator.getValue();
/*   95 */         if (j != localJavaAdapterSignature.names.get(str, j + 1))
/*   96 */           return false;
/*      */       }
/*   98 */       return true;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/*  104 */       return this.superClass.hashCode() + Arrays.hashCode(this.interfaces) ^ this.names.size();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.JavaAdapter
 * JD-Core Version:    0.6.2
 */