/*      */ package com.sun.jmx.mbeanserver;
/*      */ 
/*      */ import com.sun.jmx.remote.util.EnvHelp;
/*      */ import java.beans.ConstructorProperties;
/*      */ import java.io.InvalidObjectException;
/*      */ import java.lang.annotation.ElementType;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.GenericArrayType;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.lang.reflect.ParameterizedType;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.lang.reflect.Type;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.BitSet;
/*      */ import java.util.Collection;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.SortedSet;
/*      */ import java.util.TreeSet;
/*      */ import java.util.WeakHashMap;
/*      */ import javax.management.JMX;
/*      */ import javax.management.ObjectName;
/*      */ import javax.management.openmbean.ArrayType;
/*      */ import javax.management.openmbean.CompositeData;
/*      */ import javax.management.openmbean.CompositeDataInvocationHandler;
/*      */ import javax.management.openmbean.CompositeDataSupport;
/*      */ import javax.management.openmbean.CompositeDataView;
/*      */ import javax.management.openmbean.CompositeType;
/*      */ import javax.management.openmbean.OpenDataException;
/*      */ import javax.management.openmbean.OpenType;
/*      */ import javax.management.openmbean.SimpleType;
/*      */ import javax.management.openmbean.TabularData;
/*      */ import javax.management.openmbean.TabularDataSupport;
/*      */ import javax.management.openmbean.TabularType;
/*      */ 
/*      */ public class DefaultMXBeanMappingFactory extends MXBeanMappingFactory
/*      */ {
/*      */   private static final Mappings mappings;
/*      */   private static final List<MXBeanMapping> permanentMappings;
/*  348 */   private static final String[] keyArray = { "key" };
/*  349 */   private static final String[] keyValueArray = { "key", "value" };
/*      */ 
/* 1490 */   private static final Map<Type, Type> inProgress = Util.newIdentityHashMap();
/*      */ 
/*      */   static boolean isIdentity(MXBeanMapping paramMXBeanMapping)
/*      */   {
/*  157 */     return ((paramMXBeanMapping instanceof NonNullMXBeanMapping)) && (((NonNullMXBeanMapping)paramMXBeanMapping).isIdentity());
/*      */   }
/*      */ 
/*      */   private static synchronized MXBeanMapping getMapping(Type paramType)
/*      */   {
/*  171 */     WeakReference localWeakReference = (WeakReference)mappings.get(paramType);
/*  172 */     return localWeakReference == null ? null : (MXBeanMapping)localWeakReference.get();
/*      */   }
/*      */ 
/*      */   private static synchronized void putMapping(Type paramType, MXBeanMapping paramMXBeanMapping) {
/*  176 */     WeakReference localWeakReference = new WeakReference(paramMXBeanMapping);
/*      */ 
/*  178 */     mappings.put(paramType, localWeakReference);
/*      */   }
/*      */ 
/*      */   private static synchronized void putPermanentMapping(Type paramType, MXBeanMapping paramMXBeanMapping)
/*      */   {
/*  183 */     putMapping(paramType, paramMXBeanMapping);
/*  184 */     permanentMappings.add(paramMXBeanMapping);
/*      */   }
/*      */ 
/*      */   public synchronized MXBeanMapping mappingForType(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory)
/*      */     throws OpenDataException
/*      */   {
/*  242 */     if (inProgress.containsKey(paramType)) {
/*  243 */       throw new OpenDataException("Recursive data structure, including " + MXBeanIntrospector.typeName(paramType));
/*      */     }
/*      */ 
/*  249 */     MXBeanMapping localMXBeanMapping = getMapping(paramType);
/*  250 */     if (localMXBeanMapping != null) {
/*  251 */       return localMXBeanMapping;
/*      */     }
/*  253 */     inProgress.put(paramType, paramType);
/*      */     try {
/*  255 */       localMXBeanMapping = makeMapping(paramType, paramMXBeanMappingFactory);
/*      */     } catch (OpenDataException localOpenDataException) {
/*  257 */       throw openDataException("Cannot convert type: " + MXBeanIntrospector.typeName(paramType), localOpenDataException);
/*      */     } finally {
/*  259 */       inProgress.remove(paramType);
/*      */     }
/*      */ 
/*  262 */     putMapping(paramType, localMXBeanMapping);
/*  263 */     return localMXBeanMapping;
/*      */   }
/*      */ 
/*      */   private MXBeanMapping makeMapping(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory)
/*      */     throws OpenDataException
/*      */   {
/*      */     Object localObject;
/*  272 */     if ((paramType instanceof GenericArrayType)) {
/*  273 */       localObject = ((GenericArrayType)paramType).getGenericComponentType();
/*      */ 
/*  275 */       return makeArrayOrCollectionMapping(paramType, (Type)localObject, paramMXBeanMappingFactory);
/*  276 */     }if ((paramType instanceof Class)) {
/*  277 */       localObject = (Class)paramType;
/*  278 */       if (((Class)localObject).isEnum())
/*      */       {
/*  282 */         return makeEnumMapping((Class)localObject, ElementType.class);
/*  283 */       }if (((Class)localObject).isArray()) {
/*  284 */         Class localClass = ((Class)localObject).getComponentType();
/*  285 */         return makeArrayOrCollectionMapping((Type)localObject, localClass, paramMXBeanMappingFactory);
/*      */       }
/*  287 */       if (JMX.isMXBeanInterface((Class)localObject)) {
/*  288 */         return makeMXBeanRefMapping((Type)localObject);
/*      */       }
/*  290 */       return makeCompositeMapping((Class)localObject, paramMXBeanMappingFactory);
/*      */     }
/*  292 */     if ((paramType instanceof ParameterizedType)) {
/*  293 */       return makeParameterizedTypeMapping((ParameterizedType)paramType, paramMXBeanMappingFactory);
/*      */     }
/*      */ 
/*  296 */     throw new OpenDataException("Cannot map type: " + paramType);
/*      */   }
/*      */ 
/*      */   private static <T extends Enum<T>> MXBeanMapping makeEnumMapping(Class<?> paramClass, Class<T> paramClass1)
/*      */   {
/*  301 */     return new EnumMapping((Class)Util.cast(paramClass));
/*      */   }
/*      */ 
/*      */   private MXBeanMapping makeArrayOrCollectionMapping(Type paramType1, Type paramType2, MXBeanMappingFactory paramMXBeanMappingFactory)
/*      */     throws OpenDataException
/*      */   {
/*  314 */     MXBeanMapping localMXBeanMapping = paramMXBeanMappingFactory.mappingForType(paramType2, paramMXBeanMappingFactory);
/*  315 */     OpenType localOpenType = localMXBeanMapping.getOpenType();
/*  316 */     ArrayType localArrayType = ArrayType.getArrayType(localOpenType);
/*  317 */     Class localClass1 = localMXBeanMapping.getOpenClass();
/*      */     String str;
/*  321 */     if (localClass1.isArray())
/*  322 */       str = "[" + localClass1.getName();
/*      */     else
/*  324 */       str = "[L" + localClass1.getName() + ";"; Class localClass2;
/*      */     try {
/*  326 */       localClass2 = Class.forName(str);
/*      */     } catch (ClassNotFoundException localClassNotFoundException) {
/*  328 */       throw openDataException("Cannot obtain array class", localClassNotFoundException);
/*      */     }
/*      */ 
/*  331 */     if ((paramType1 instanceof ParameterizedType)) {
/*  332 */       return new CollectionMapping(paramType1, localArrayType, localClass2, localMXBeanMapping);
/*      */     }
/*      */ 
/*  336 */     if (isIdentity(localMXBeanMapping)) {
/*  337 */       return new IdentityMapping(paramType1, localArrayType);
/*      */     }
/*      */ 
/*  340 */     return new ArrayMapping(paramType1, localArrayType, localClass2, localMXBeanMapping);
/*      */   }
/*      */ 
/*      */   private MXBeanMapping makeTabularMapping(Type paramType1, boolean paramBoolean, Type paramType2, Type paramType3, MXBeanMappingFactory paramMXBeanMappingFactory)
/*      */     throws OpenDataException
/*      */   {
/*  357 */     String str = MXBeanIntrospector.typeName(paramType1);
/*  358 */     MXBeanMapping localMXBeanMapping1 = paramMXBeanMappingFactory.mappingForType(paramType2, paramMXBeanMappingFactory);
/*  359 */     MXBeanMapping localMXBeanMapping2 = paramMXBeanMappingFactory.mappingForType(paramType3, paramMXBeanMappingFactory);
/*  360 */     OpenType localOpenType1 = localMXBeanMapping1.getOpenType();
/*  361 */     OpenType localOpenType2 = localMXBeanMapping2.getOpenType();
/*  362 */     CompositeType localCompositeType = new CompositeType(str, str, keyValueArray, keyValueArray, new OpenType[] { localOpenType1, localOpenType2 });
/*      */ 
/*  368 */     TabularType localTabularType = new TabularType(str, str, localCompositeType, keyArray);
/*      */ 
/*  370 */     return new TabularMapping(paramType1, paramBoolean, localTabularType, localMXBeanMapping1, localMXBeanMapping2);
/*      */   }
/*      */ 
/*      */   private MXBeanMapping makeParameterizedTypeMapping(ParameterizedType paramParameterizedType, MXBeanMappingFactory paramMXBeanMappingFactory)
/*      */     throws OpenDataException
/*      */   {
/*  384 */     Type localType = paramParameterizedType.getRawType();
/*      */ 
/*  386 */     if ((localType instanceof Class)) {
/*  387 */       Class localClass = (Class)localType;
/*  388 */       if ((localClass == List.class) || (localClass == Set.class) || (localClass == SortedSet.class)) {
/*  389 */         Type[] arrayOfType1 = paramParameterizedType.getActualTypeArguments();
/*  390 */         assert (arrayOfType1.length == 1);
/*  391 */         if (localClass == SortedSet.class)
/*  392 */           mustBeComparable(localClass, arrayOfType1[0]);
/*  393 */         return makeArrayOrCollectionMapping(paramParameterizedType, arrayOfType1[0], paramMXBeanMappingFactory);
/*      */       }
/*  395 */       boolean bool = localClass == SortedMap.class;
/*  396 */       if ((localClass == Map.class) || (bool)) {
/*  397 */         Type[] arrayOfType2 = paramParameterizedType.getActualTypeArguments();
/*  398 */         assert (arrayOfType2.length == 2);
/*  399 */         if (bool)
/*  400 */           mustBeComparable(localClass, arrayOfType2[0]);
/*  401 */         return makeTabularMapping(paramParameterizedType, bool, arrayOfType2[0], arrayOfType2[1], paramMXBeanMappingFactory);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  406 */     throw new OpenDataException("Cannot convert type: " + paramParameterizedType);
/*      */   }
/*      */ 
/*      */   private static MXBeanMapping makeMXBeanRefMapping(Type paramType) throws OpenDataException
/*      */   {
/*  411 */     return new MXBeanRefMapping(paramType);
/*      */   }
/*      */ 
/*      */   private MXBeanMapping makeCompositeMapping(Class<?> paramClass, MXBeanMappingFactory paramMXBeanMappingFactory)
/*      */     throws OpenDataException
/*      */   {
/*  421 */     int i = (paramClass.getName().equals("com.sun.management.GcInfo")) && (paramClass.getClassLoader() == null) ? 1 : 0;
/*      */ 
/*  425 */     List localList = MBeanAnalyzer.eliminateCovariantMethods(Arrays.asList(paramClass.getMethods()));
/*      */ 
/*  427 */     SortedMap localSortedMap = Util.newSortedMap();
/*      */ 
/*  432 */     for (Iterator localIterator = localList.iterator(); localIterator.hasNext(); ) { localObject1 = (Method)localIterator.next();
/*  433 */       localObject2 = propertyName((Method)localObject1);
/*      */ 
/*  435 */       if ((localObject2 != null) && (
/*  437 */         (i == 0) || (!((String)localObject2).equals("CompositeType"))))
/*      */       {
/*  440 */         localObject3 = (Method)localSortedMap.put(decapitalize((String)localObject2), localObject1);
/*      */ 
/*  443 */         if (localObject3 != null) {
/*  444 */           String str = "Class " + paramClass.getName() + " has method name clash: " + ((Method)localObject3).getName() + ", " + ((Method)localObject1).getName();
/*      */ 
/*  447 */           throw new OpenDataException(str);
/*      */         }
/*      */       }
/*      */     }
/*  451 */     int j = localSortedMap.size();
/*      */ 
/*  453 */     if (j == 0) {
/*  454 */       throw new OpenDataException("Can't map " + paramClass.getName() + " to an open data type");
/*      */     }
/*      */ 
/*  458 */     Object localObject1 = new Method[j];
/*  459 */     Object localObject2 = new String[j];
/*  460 */     Object localObject3 = new OpenType[j];
/*  461 */     int k = 0;
/*  462 */     for (Object localObject4 = localSortedMap.entrySet().iterator(); ((Iterator)localObject4).hasNext(); ) { Map.Entry localEntry = (Map.Entry)((Iterator)localObject4).next();
/*  463 */       localObject2[k] = ((String)localEntry.getKey());
/*  464 */       Method localMethod = (Method)localEntry.getValue();
/*  465 */       localObject1[k] = localMethod;
/*  466 */       Type localType = localMethod.getGenericReturnType();
/*  467 */       localObject3[k] = paramMXBeanMappingFactory.mappingForType(localType, paramMXBeanMappingFactory).getOpenType();
/*  468 */       k++;
/*      */     }
/*      */ 
/*  471 */     localObject4 = new CompositeType(paramClass.getName(), paramClass.getName(), (String[])localObject2, (String[])localObject2, (OpenType[])localObject3);
/*      */ 
/*  478 */     return new CompositeMapping(paramClass, (CompositeType)localObject4, (String[])localObject2, (Method[])localObject1, paramMXBeanMappingFactory);
/*      */   }
/*      */ 
/*      */   static InvalidObjectException invalidObjectException(String paramString, Throwable paramThrowable)
/*      */   {
/* 1408 */     return (InvalidObjectException)EnvHelp.initCause(new InvalidObjectException(paramString), paramThrowable);
/*      */   }
/*      */ 
/*      */   static InvalidObjectException invalidObjectException(Throwable paramThrowable) {
/* 1412 */     return invalidObjectException(paramThrowable.getMessage(), paramThrowable);
/*      */   }
/*      */ 
/*      */   static OpenDataException openDataException(String paramString, Throwable paramThrowable) {
/* 1416 */     return (OpenDataException)EnvHelp.initCause(new OpenDataException(paramString), paramThrowable);
/*      */   }
/*      */ 
/*      */   static OpenDataException openDataException(Throwable paramThrowable) {
/* 1420 */     return openDataException(paramThrowable.getMessage(), paramThrowable);
/*      */   }
/*      */ 
/*      */   static void mustBeComparable(Class<?> paramClass, Type paramType) throws OpenDataException
/*      */   {
/* 1425 */     if ((!(paramType instanceof Class)) || (!Comparable.class.isAssignableFrom((Class)paramType)))
/*      */     {
/* 1427 */       String str = "Parameter class " + paramType + " of " + paramClass.getName() + " does not implement " + Comparable.class.getName();
/*      */ 
/* 1431 */       throw new OpenDataException(str);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String decapitalize(String paramString)
/*      */   {
/* 1449 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 1450 */       return paramString;
/*      */     }
/* 1452 */     int i = Character.offsetByCodePoints(paramString, 0, 1);
/*      */ 
/* 1454 */     if ((i < paramString.length()) && (Character.isUpperCase(paramString.codePointAt(i))))
/*      */     {
/* 1456 */       return paramString;
/* 1457 */     }return paramString.substring(0, i).toLowerCase() + paramString.substring(i);
/*      */   }
/*      */ 
/*      */   static String capitalize(String paramString)
/*      */   {
/* 1468 */     if ((paramString == null) || (paramString.length() == 0))
/* 1469 */       return paramString;
/* 1470 */     int i = paramString.offsetByCodePoints(0, 1);
/* 1471 */     return paramString.substring(0, i).toUpperCase() + paramString.substring(i);
/*      */   }
/*      */ 
/*      */   public static String propertyName(Method paramMethod)
/*      */   {
/* 1476 */     String str1 = null;
/* 1477 */     String str2 = paramMethod.getName();
/* 1478 */     if (str2.startsWith("get"))
/* 1479 */       str1 = str2.substring(3);
/* 1480 */     else if ((str2.startsWith("is")) && (paramMethod.getReturnType() == Boolean.TYPE))
/* 1481 */       str1 = str2.substring(2);
/* 1482 */     if ((str1 == null) || (str1.length() == 0) || (paramMethod.getParameterTypes().length > 0) || (paramMethod.getReturnType() == Void.TYPE) || (str2.equals("getClass")))
/*      */     {
/* 1486 */       return null;
/* 1487 */     }return str1;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  164 */     mappings = new Mappings(null);
/*      */ 
/*  168 */     permanentMappings = Util.newList();
/*      */ 
/*  190 */     OpenType[] arrayOfOpenType = { SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID };
/*      */ 
/*  196 */     for (int i = 0; i < arrayOfOpenType.length; i++) { OpenType localOpenType = arrayOfOpenType[i];
/*      */       Class localClass1;
/*      */       try {
/*  200 */         localClass1 = Class.forName(localOpenType.getClassName(), false, ObjectName.class.getClassLoader());
/*      */       }
/*      */       catch (ClassNotFoundException localClassNotFoundException)
/*      */       {
/*  204 */         throw new Error(localClassNotFoundException);
/*      */       }
/*  206 */       IdentityMapping localIdentityMapping1 = new IdentityMapping(localClass1, localOpenType);
/*  207 */       putPermanentMapping(localClass1, localIdentityMapping1);
/*      */ 
/*  209 */       if (localClass1.getName().startsWith("java.lang."))
/*      */         try {
/*  211 */           Field localField = localClass1.getField("TYPE");
/*  212 */           Class localClass2 = (Class)localField.get(null);
/*  213 */           IdentityMapping localIdentityMapping2 = new IdentityMapping(localClass2, localOpenType);
/*      */ 
/*  215 */           putPermanentMapping(localClass2, localIdentityMapping2);
/*  216 */           if (localClass2 != Void.TYPE) {
/*  217 */             Class localClass3 = Array.newInstance(localClass2, 0).getClass();
/*      */ 
/*  219 */             ArrayType localArrayType = ArrayType.getPrimitiveArrayType(localClass3);
/*      */ 
/*  221 */             IdentityMapping localIdentityMapping3 = new IdentityMapping(localClass3, localArrayType);
/*      */ 
/*  224 */             putPermanentMapping(localClass3, localIdentityMapping3);
/*      */           }
/*      */         }
/*      */         catch (NoSuchFieldException localNoSuchFieldException)
/*      */         {
/*      */         }
/*      */         catch (IllegalAccessException localIllegalAccessException) {
/*  231 */           if (!$assertionsDisabled) throw new AssertionError();
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class ArrayMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
/*      */   {
/*      */     private final MXBeanMapping elementMapping;
/*      */ 
/*      */     ArrayMapping(Type paramType, ArrayType<?> paramArrayType, Class<?> paramClass, MXBeanMapping paramMXBeanMapping)
/*      */     {
/*  543 */       super(paramArrayType);
/*  544 */       this.elementMapping = paramMXBeanMapping;
/*      */     }
/*      */ 
/*      */     final Object toNonNullOpenValue(Object paramObject)
/*      */       throws OpenDataException
/*      */     {
/*  550 */       Object[] arrayOfObject1 = (Object[])paramObject;
/*  551 */       int i = arrayOfObject1.length;
/*  552 */       Object[] arrayOfObject2 = (Object[])Array.newInstance(getOpenClass().getComponentType(), i);
/*      */ 
/*  554 */       for (int j = 0; j < i; j++)
/*  555 */         arrayOfObject2[j] = this.elementMapping.toOpenValue(arrayOfObject1[j]);
/*  556 */       return arrayOfObject2;
/*      */     }
/*      */ 
/*      */     final Object fromNonNullOpenValue(Object paramObject)
/*      */       throws InvalidObjectException
/*      */     {
/*  562 */       Object[] arrayOfObject1 = (Object[])paramObject;
/*  563 */       Type localType = getJavaType();
/*      */       Object localObject;
/*  566 */       if ((localType instanceof GenericArrayType)) {
/*  567 */         localObject = ((GenericArrayType)localType).getGenericComponentType();
/*      */       }
/*  569 */       else if (((localType instanceof Class)) && (((Class)localType).isArray()))
/*      */       {
/*  571 */         localObject = ((Class)localType).getComponentType();
/*      */       }
/*  573 */       else throw new IllegalArgumentException("Not an array: " + localType);
/*      */ 
/*  576 */       Object[] arrayOfObject2 = (Object[])Array.newInstance((Class)localObject, arrayOfObject1.length);
/*      */ 
/*  578 */       for (int i = 0; i < arrayOfObject1.length; i++)
/*  579 */         arrayOfObject2[i] = this.elementMapping.fromOpenValue(arrayOfObject1[i]);
/*  580 */       return arrayOfObject2;
/*      */     }
/*      */ 
/*      */     public void checkReconstructible() throws InvalidObjectException {
/*  584 */       this.elementMapping.checkReconstructible();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class CollectionMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
/*      */   {
/*      */     private final Class<? extends Collection<?>> collectionClass;
/*      */     private final MXBeanMapping elementMapping;
/*      */ 
/*      */     CollectionMapping(Type paramType, ArrayType<?> paramArrayType, Class<?> paramClass, MXBeanMapping paramMXBeanMapping)
/*      */     {
/*  600 */       super(paramArrayType);
/*  601 */       this.elementMapping = paramMXBeanMapping;
/*      */ 
/*  607 */       Type localType = ((ParameterizedType)paramType).getRawType();
/*  608 */       Class localClass = (Class)localType;
/*      */       Object localObject;
/*  610 */       if (localClass == List.class) {
/*  611 */         localObject = ArrayList.class;
/*  612 */       } else if (localClass == Set.class) {
/*  613 */         localObject = HashSet.class;
/*  614 */       } else if (localClass == SortedSet.class) {
/*  615 */         localObject = TreeSet.class;
/*      */       } else {
/*  617 */         if (!$assertionsDisabled) throw new AssertionError();
/*  618 */         localObject = null;
/*      */       }
/*  620 */       this.collectionClass = ((Class)Util.cast(localObject));
/*      */     }
/*      */ 
/*      */     final Object toNonNullOpenValue(Object paramObject)
/*      */       throws OpenDataException
/*      */     {
/*  626 */       Collection localCollection = (Collection)paramObject;
/*  627 */       if ((localCollection instanceof SortedSet)) {
/*  628 */         localObject1 = ((SortedSet)localCollection).comparator();
/*      */ 
/*  630 */         if (localObject1 != null) {
/*  631 */           String str = "Cannot convert SortedSet with non-null comparator: " + localObject1;
/*      */ 
/*  634 */           throw DefaultMXBeanMappingFactory.openDataException(str, new IllegalArgumentException(str));
/*      */         }
/*      */       }
/*  637 */       Object localObject1 = (Object[])Array.newInstance(getOpenClass().getComponentType(), localCollection.size());
/*      */ 
/*  640 */       int i = 0;
/*  641 */       for (Iterator localIterator = localCollection.iterator(); localIterator.hasNext(); ) { Object localObject2 = localIterator.next();
/*  642 */         localObject1[(i++)] = this.elementMapping.toOpenValue(localObject2); }
/*  643 */       return localObject1;
/*      */     }
/*      */ 
/*      */     final Object fromNonNullOpenValue(Object paramObject) throws InvalidObjectException
/*      */     {
/*  649 */       Object[] arrayOfObject1 = (Object[])paramObject;
/*      */       Collection localCollection;
/*      */       try {
/*  652 */         localCollection = (Collection)Util.cast(this.collectionClass.newInstance());
/*      */       } catch (Exception localException) {
/*  654 */         throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot create collection", localException);
/*      */       }
/*  656 */       for (Object localObject1 : arrayOfObject1) {
/*  657 */         Object localObject2 = this.elementMapping.fromOpenValue(localObject1);
/*  658 */         if (!localCollection.add(localObject2)) {
/*  659 */           String str = "Could not add " + localObject1 + " to " + this.collectionClass.getName() + " (duplicate set element?)";
/*      */ 
/*  663 */           throw new InvalidObjectException(str);
/*      */         }
/*      */       }
/*  666 */       return localCollection;
/*      */     }
/*      */ 
/*      */     public void checkReconstructible() throws InvalidObjectException {
/*  670 */       this.elementMapping.checkReconstructible();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class CompositeBuilder
/*      */   {
/*      */     private final Class<?> targetClass;
/*      */     private final String[] itemNames;
/*      */ 
/*      */     CompositeBuilder(Class<?> paramClass, String[] paramArrayOfString)
/*      */     {
/*  928 */       this.targetClass = paramClass;
/*  929 */       this.itemNames = paramArrayOfString;
/*      */     }
/*      */ 
/*      */     Class<?> getTargetClass() {
/*  933 */       return this.targetClass;
/*      */     }
/*      */ 
/*      */     String[] getItemNames() {
/*  937 */       return this.itemNames;
/*      */     }
/*      */ 
/*      */     abstract String applicable(Method[] paramArrayOfMethod)
/*      */       throws InvalidObjectException;
/*      */ 
/*      */     Throwable possibleCause()
/*      */     {
/*  955 */       return null;
/*      */     }
/*      */ 
/*      */     abstract Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
/*      */       throws InvalidObjectException;
/*      */   }
/*      */ 
/*      */   private static class CompositeBuilderCheckGetters extends DefaultMXBeanMappingFactory.CompositeBuilder
/*      */   {
/*      */     private final MXBeanMapping[] getterConverters;
/*      */     private Throwable possibleCause;
/*      */ 
/*      */     CompositeBuilderCheckGetters(Class<?> paramClass, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
/*      */     {
/* 1036 */       super(paramArrayOfString);
/* 1037 */       this.getterConverters = paramArrayOfMXBeanMapping;
/*      */     }
/*      */ 
/*      */     String applicable(Method[] paramArrayOfMethod) {
/* 1041 */       for (int i = 0; i < paramArrayOfMethod.length; i++) {
/*      */         try {
/* 1043 */           this.getterConverters[i].checkReconstructible();
/*      */         } catch (InvalidObjectException localInvalidObjectException) {
/* 1045 */           this.possibleCause = localInvalidObjectException;
/* 1046 */           return "method " + paramArrayOfMethod[i].getName() + " returns type " + "that cannot be mapped back from OpenData";
/*      */         }
/*      */       }
/*      */ 
/* 1050 */       return "";
/*      */     }
/*      */ 
/*      */     Throwable possibleCause()
/*      */     {
/* 1055 */       return this.possibleCause;
/*      */     }
/*      */ 
/*      */     final Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
/*      */     {
/* 1061 */       throw new Error();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class CompositeBuilderViaConstructor extends DefaultMXBeanMappingFactory.CompositeBuilder
/*      */   {
/*      */     private List<Constr> annotatedConstructors;
/*      */ 
/*      */     CompositeBuilderViaConstructor(Class<?> paramClass, String[] paramArrayOfString)
/*      */     {
/* 1134 */       super(paramArrayOfString);
/*      */     }
/*      */ 
/*      */     String applicable(Method[] paramArrayOfMethod) throws InvalidObjectException
/*      */     {
/* 1139 */       ConstructorProperties localConstructorProperties = ConstructorProperties.class;
/*      */ 
/* 1141 */       Class localClass = getTargetClass();
/* 1142 */       Constructor[] arrayOfConstructor = localClass.getConstructors();
/*      */ 
/* 1145 */       List localList = Util.newList();
/* 1146 */       for (localObject2 : arrayOfConstructor) {
/* 1147 */         if ((Modifier.isPublic(((Constructor)localObject2).getModifiers())) && (((Constructor)localObject2).getAnnotation(localConstructorProperties) != null))
/*      */         {
/* 1149 */           localList.add(localObject2);
/*      */         }
/*      */       }
/* 1152 */       if (localList.isEmpty()) {
/* 1153 */         return "no constructor has @ConstructorProperties annotation";
/*      */       }
/* 1155 */       this.annotatedConstructors = Util.newList();
/*      */ 
/* 1161 */       ??? = Util.newMap();
/* 1162 */       String[] arrayOfString1 = getItemNames();
/* 1163 */       for (??? = 0; ??? < arrayOfString1.length; ???++) {
/* 1164 */         ((Map)???).put(arrayOfString1[???], Integer.valueOf(???));
/*      */       }
/*      */ 
/* 1175 */       Set localSet = Util.newSet();
/* 1176 */       for (Object localObject2 = localList.iterator(); ((Iterator)localObject2).hasNext(); ) { localObject3 = (Constructor)((Iterator)localObject2).next();
/* 1177 */         String[] arrayOfString2 = ((ConstructorProperties)((Constructor)localObject3).getAnnotation(localConstructorProperties)).value();
/*      */ 
/* 1180 */         localObject4 = ((Constructor)localObject3).getGenericParameterTypes();
/* 1181 */         if (localObject4.length != arrayOfString2.length) {
/* 1182 */           localObject5 = "Number of constructor params does not match @ConstructorProperties annotation: " + localObject3;
/*      */ 
/* 1185 */           throw new InvalidObjectException((String)localObject5);
/*      */         }
/*      */ 
/* 1188 */         localObject5 = new int[paramArrayOfMethod.length];
/* 1189 */         for (int m = 0; m < paramArrayOfMethod.length; m++)
/* 1190 */           localObject5[m] = -1;
/* 1191 */         localBitSet = new BitSet();
/*      */ 
/* 1193 */         for (int n = 0; n < arrayOfString2.length; n++) {
/* 1194 */           String str1 = arrayOfString2[n];
/* 1195 */           if (!((Map)???).containsKey(str1)) {
/* 1196 */             String str3 = "@ConstructorProperties includes name " + str1 + " which does not correspond to a property";
/*      */ 
/* 1199 */             for (localObject7 = ((Map)???).keySet().iterator(); ((Iterator)localObject7).hasNext(); ) { localObject8 = (String)((Iterator)localObject7).next();
/* 1200 */               if (((String)localObject8).equalsIgnoreCase(str1)) {
/* 1201 */                 str3 = str3 + " (differs only in case from property " + (String)localObject8 + ")";
/*      */               }
/*      */             }
/*      */ 
/* 1205 */             str3 = str3 + ": " + localObject3;
/* 1206 */             throw new InvalidObjectException(str3);
/*      */           }
/* 1208 */           int i2 = ((Integer)((Map)???).get(str1)).intValue();
/* 1209 */           localObject5[i2] = n;
/* 1210 */           if (localBitSet.get(i2)) {
/* 1211 */             localObject7 = "@ConstructorProperties contains property " + str1 + " more than once: " + localObject3;
/*      */ 
/* 1214 */             throw new InvalidObjectException((String)localObject7);
/*      */           }
/* 1216 */           localBitSet.set(i2);
/* 1217 */           Object localObject7 = paramArrayOfMethod[i2];
/* 1218 */           Object localObject8 = ((Method)localObject7).getGenericReturnType();
/* 1219 */           if (!localObject8.equals(localObject4[n])) {
/* 1220 */             String str4 = "@ConstructorProperties gives property " + str1 + " of type " + localObject8 + " for parameter " + " of type " + localObject4[n] + ": " + localObject3;
/*      */ 
/* 1224 */             throw new InvalidObjectException(str4);
/*      */           }
/*      */         }
/*      */ 
/* 1228 */         if (!localSet.add(localBitSet)) {
/* 1229 */           localObject6 = "More than one constructor has a @ConstructorProperties annotation with this set of names: " + Arrays.toString(arrayOfString2);
/*      */ 
/* 1233 */           throw new InvalidObjectException((String)localObject6);
/*      */         }
/*      */ 
/* 1236 */         localObject6 = new Constr((Constructor)localObject3, (int[])localObject5, localBitSet);
/* 1237 */         this.annotatedConstructors.add(localObject6);
/*      */       }
/* 1255 */       Object localObject3;
/*      */       Object localObject4;
/*      */       Object localObject5;
/*      */       BitSet localBitSet;
/*      */       Object localObject6;
/* 1255 */       for (localObject2 = localSet.iterator(); ((Iterator)localObject2).hasNext(); ) { localObject3 = (BitSet)((Iterator)localObject2).next();
/* 1256 */         k = 0;
/* 1257 */         for (localObject4 = localSet.iterator(); ((Iterator)localObject4).hasNext(); ) { localObject5 = (BitSet)((Iterator)localObject4).next();
/* 1258 */           if (localObject3 == localObject5) {
/* 1259 */             k = 1;
/* 1260 */           } else if (k != 0) {
/* 1261 */             localBitSet = new BitSet();
/* 1262 */             localBitSet.or((BitSet)localObject3); localBitSet.or((BitSet)localObject5);
/* 1263 */             if (!localSet.contains(localBitSet)) {
/* 1264 */               localObject6 = new TreeSet();
/* 1265 */               for (int i1 = localBitSet.nextSetBit(0); i1 >= 0; 
/* 1266 */                 i1 = localBitSet.nextSetBit(i1 + 1))
/* 1267 */                 ((Set)localObject6).add(arrayOfString1[i1]);
/* 1268 */               String str2 = "Constructors with @ConstructorProperties annotation  would be ambiguous for these items: " + localObject6;
/*      */ 
/* 1272 */               throw new InvalidObjectException(str2);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       int k;
/* 1278 */       return null;
/*      */     }
/*      */ 
/*      */     final Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
/*      */       throws InvalidObjectException
/*      */     {
/* 1290 */       CompositeType localCompositeType = paramCompositeData.getCompositeType();
/* 1291 */       BitSet localBitSet = new BitSet();
/* 1292 */       for (int i = 0; i < paramArrayOfString.length; i++) {
/* 1293 */         if (localCompositeType.getType(paramArrayOfString[i]) != null) {
/* 1294 */           localBitSet.set(i);
/*      */         }
/*      */       }
/* 1297 */       Object localObject1 = null;
/* 1298 */       for (Object localObject2 = this.annotatedConstructors.iterator(); ((Iterator)localObject2).hasNext(); ) { Constr localConstr = (Constr)((Iterator)localObject2).next();
/* 1299 */         if ((subset(localConstr.presentParams, localBitSet)) && ((localObject1 == null) || (subset(localObject1.presentParams, localConstr.presentParams))))
/*      */         {
/* 1302 */           localObject1 = localConstr;
/*      */         }
/*      */       }
/* 1305 */       if (localObject1 == null) {
/* 1306 */         localObject2 = "No constructor has a @ConstructorProperties for this set of items: " + localCompositeType.keySet();
/*      */ 
/* 1309 */         throw new InvalidObjectException((String)localObject2);
/*      */       }
/*      */ 
/* 1312 */       localObject2 = new Object[localObject1.presentParams.cardinality()];
/*      */       Object localObject3;
/* 1313 */       for (int j = 0; j < paramArrayOfString.length; j++)
/* 1314 */         if (localObject1.presentParams.get(j))
/*      */         {
/* 1316 */           localObject3 = paramCompositeData.get(paramArrayOfString[j]);
/* 1317 */           Object localObject4 = paramArrayOfMXBeanMapping[j].fromOpenValue(localObject3);
/* 1318 */           int k = localObject1.paramIndexes[j];
/* 1319 */           if (k >= 0)
/* 1320 */             localObject2[k] = localObject4;
/*      */         }
/*      */       try
/*      */       {
/* 1324 */         return localObject1.constructor.newInstance((Object[])localObject2);
/*      */       } catch (Exception localException) {
/* 1326 */         localObject3 = "Exception constructing " + getTargetClass().getName();
/*      */ 
/* 1328 */         throw DefaultMXBeanMappingFactory.invalidObjectException((String)localObject3, localException);
/*      */       }
/*      */     }
/*      */ 
/*      */     private static boolean subset(BitSet paramBitSet1, BitSet paramBitSet2) {
/* 1333 */       BitSet localBitSet = (BitSet)paramBitSet1.clone();
/* 1334 */       localBitSet.andNot(paramBitSet2);
/* 1335 */       return localBitSet.isEmpty();
/*      */     }
/*      */     private static class Constr {
/*      */       final Constructor<?> constructor;
/*      */       final int[] paramIndexes;
/*      */       final BitSet presentParams;
/*      */ 
/* 1344 */       Constr(Constructor<?> paramConstructor, int[] paramArrayOfInt, BitSet paramBitSet) { this.constructor = paramConstructor;
/* 1345 */         this.paramIndexes = paramArrayOfInt;
/* 1346 */         this.presentParams = paramBitSet;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class CompositeBuilderViaFrom extends DefaultMXBeanMappingFactory.CompositeBuilder
/*      */   {
/*      */     private Method fromMethod;
/*      */ 
/*      */     CompositeBuilderViaFrom(Class<?> paramClass, String[] paramArrayOfString)
/*      */     {
/*  973 */       super(paramArrayOfString);
/*      */     }
/*      */ 
/*      */     String applicable(Method[] paramArrayOfMethod)
/*      */       throws InvalidObjectException
/*      */     {
/*  979 */       Class localClass = getTargetClass();
/*      */       try {
/*  981 */         Method localMethod = localClass.getMethod("from", new Class[] { CompositeData.class });
/*      */ 
/*  984 */         if (!Modifier.isStatic(localMethod.getModifiers()))
/*      */         {
/*  987 */           throw new InvalidObjectException("Method from(CompositeData) is not static");
/*      */         }
/*      */ 
/*  990 */         if (localMethod.getReturnType() != getTargetClass()) {
/*  991 */           String str = "Method from(CompositeData) returns " + MXBeanIntrospector.typeName(localMethod.getReturnType()) + " not " + MXBeanIntrospector.typeName(localClass);
/*      */ 
/*  995 */           throw new InvalidObjectException(str);
/*      */         }
/*      */ 
/*  998 */         this.fromMethod = localMethod;
/*  999 */         return null;
/*      */       } catch (InvalidObjectException localInvalidObjectException) {
/* 1001 */         throw localInvalidObjectException;
/*      */       } catch (Exception localException) {
/*      */       }
/* 1004 */       return "no method from(CompositeData)";
/*      */     }
/*      */ 
/*      */     final Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
/*      */       throws InvalidObjectException
/*      */     {
/*      */       try
/*      */       {
/* 1013 */         return this.fromMethod.invoke(null, new Object[] { paramCompositeData });
/*      */       }
/*      */       catch (Exception localException) {
/* 1016 */         throw DefaultMXBeanMappingFactory.invalidObjectException("Failed to invoke from(CompositeData)", localException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class CompositeBuilderViaProxy extends DefaultMXBeanMappingFactory.CompositeBuilder
/*      */   {
/*      */     CompositeBuilderViaProxy(Class<?> paramClass, String[] paramArrayOfString)
/*      */     {
/* 1361 */       super(paramArrayOfString);
/*      */     }
/*      */ 
/*      */     String applicable(Method[] paramArrayOfMethod) {
/* 1365 */       Class localClass = getTargetClass();
/* 1366 */       if (!localClass.isInterface())
/* 1367 */         return "not an interface";
/* 1368 */       Set localSet = Util.newSet(Arrays.asList(localClass.getMethods()));
/*      */ 
/* 1370 */       localSet.removeAll(Arrays.asList(paramArrayOfMethod));
/*      */ 
/* 1374 */       Object localObject = null;
/* 1375 */       for (Method localMethod1 : localSet) {
/* 1376 */         String str = localMethod1.getName();
/* 1377 */         Class[] arrayOfClass = localMethod1.getParameterTypes();
/*      */         try {
/* 1379 */           Method localMethod2 = Object.class.getMethod(str, arrayOfClass);
/* 1380 */           if (!Modifier.isPublic(localMethod2.getModifiers()))
/* 1381 */             localObject = str;
/*      */         } catch (NoSuchMethodException localNoSuchMethodException) {
/* 1383 */           localObject = str;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1390 */       if (localObject != null)
/* 1391 */         return "contains methods other than getters (" + localObject + ")";
/* 1392 */       return null;
/*      */     }
/*      */ 
/*      */     final Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
/*      */     {
/* 1398 */       Class localClass = getTargetClass();
/* 1399 */       return Proxy.newProxyInstance(localClass.getClassLoader(), new Class[] { localClass }, new CompositeDataInvocationHandler(paramCompositeData));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CompositeBuilderViaSetters extends DefaultMXBeanMappingFactory.CompositeBuilder
/*      */   {
/*      */     private Method[] setters;
/*      */ 
/*      */     CompositeBuilderViaSetters(Class<?> paramClass, String[] paramArrayOfString)
/*      */     {
/* 1072 */       super(paramArrayOfString);
/*      */     }
/*      */ 
/*      */     String applicable(Method[] paramArrayOfMethod) {
/*      */       try {
/* 1077 */         Constructor localConstructor = getTargetClass().getConstructor(new Class[0]);
/*      */       } catch (Exception localException1) {
/* 1079 */         return "does not have a public no-arg constructor";
/*      */       }
/*      */ 
/* 1082 */       Method[] arrayOfMethod = new Method[paramArrayOfMethod.length];
/* 1083 */       for (int i = 0; i < paramArrayOfMethod.length; i++) { Method localMethod1 = paramArrayOfMethod[i];
/* 1085 */         Class localClass = localMethod1.getReturnType();
/* 1086 */         String str1 = DefaultMXBeanMappingFactory.propertyName(localMethod1);
/* 1087 */         String str2 = "set" + str1;
/*      */         Method localMethod2;
/*      */         try { localMethod2 = getTargetClass().getMethod(str2, new Class[] { localClass });
/* 1091 */           if (localMethod2.getReturnType() != Void.TYPE)
/* 1092 */             throw new Exception();
/*      */         } catch (Exception localException2) {
/* 1094 */           return "not all getters have corresponding setters (" + localMethod1 + ")";
/*      */         }
/*      */ 
/* 1097 */         arrayOfMethod[i] = localMethod2;
/*      */       }
/* 1099 */       this.setters = arrayOfMethod;
/* 1100 */       return null;
/*      */     }
/*      */ 
/*      */     Object fromCompositeData(CompositeData paramCompositeData, String[] paramArrayOfString, MXBeanMapping[] paramArrayOfMXBeanMapping)
/*      */       throws InvalidObjectException
/*      */     {
/*      */       Object localObject1;
/*      */       try
/*      */       {
/* 1109 */         localObject1 = getTargetClass().newInstance();
/* 1110 */         for (int i = 0; i < paramArrayOfString.length; i++)
/* 1111 */           if (paramCompositeData.containsKey(paramArrayOfString[i])) {
/* 1112 */             Object localObject2 = paramCompositeData.get(paramArrayOfString[i]);
/* 1113 */             Object localObject3 = paramArrayOfMXBeanMapping[i].fromOpenValue(localObject2);
/*      */ 
/* 1115 */             this.setters[i].invoke(localObject1, new Object[] { localObject3 });
/*      */           }
/*      */       }
/*      */       catch (Exception localException) {
/* 1119 */         throw DefaultMXBeanMappingFactory.invalidObjectException(localException);
/*      */       }
/* 1121 */       return localObject1;
/*      */     }
/*      */   }
/*      */ 
/*      */   private final class CompositeMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
/*      */   {
/*      */     private final String[] itemNames;
/*      */     private final Method[] getters;
/*      */     private final MXBeanMapping[] getterMappings;
/*      */     private DefaultMXBeanMappingFactory.CompositeBuilder compositeBuilder;
/*      */ 
/*      */     CompositeMapping(CompositeType paramArrayOfString, String[] paramArrayOfMethod, Method[] paramMXBeanMappingFactory, MXBeanMappingFactory arg5)
/*      */       throws OpenDataException
/*      */     {
/*  805 */       super(paramArrayOfMethod);
/*      */       Object localObject;
/*  807 */       assert (paramMXBeanMappingFactory.length == localObject.length);
/*      */ 
/*  809 */       this.itemNames = paramMXBeanMappingFactory;
/*  810 */       this.getters = localObject;
/*  811 */       this.getterMappings = new MXBeanMapping[localObject.length];
/*  812 */       for (int i = 0; i < localObject.length; i++) {
/*  813 */         Type localType = localObject[i].getGenericReturnType();
/*      */         MXBeanMappingFactory localMXBeanMappingFactory;
/*  814 */         this.getterMappings[i] = localMXBeanMappingFactory.mappingForType(localType, localMXBeanMappingFactory);
/*      */       }
/*      */     }
/*      */ 
/*      */     final Object toNonNullOpenValue(Object paramObject)
/*      */       throws OpenDataException
/*      */     {
/*  821 */       CompositeType localCompositeType = (CompositeType)getOpenType();
/*  822 */       if ((paramObject instanceof CompositeDataView))
/*  823 */         return ((CompositeDataView)paramObject).toCompositeData(localCompositeType);
/*  824 */       if (paramObject == null) {
/*  825 */         return null;
/*      */       }
/*  827 */       Object[] arrayOfObject = new Object[this.getters.length];
/*  828 */       for (int i = 0; i < this.getters.length; i++) {
/*      */         try {
/*  830 */           Object localObject = this.getters[i].invoke(paramObject, (Object[])null);
/*  831 */           arrayOfObject[i] = this.getterMappings[i].toOpenValue(localObject);
/*      */         } catch (Exception localException) {
/*  833 */           throw DefaultMXBeanMappingFactory.openDataException("Error calling getter for " + this.itemNames[i] + ": " + localException, localException);
/*      */         }
/*      */       }
/*      */ 
/*  837 */       return new CompositeDataSupport(localCompositeType, this.itemNames, arrayOfObject);
/*      */     }
/*      */ 
/*      */     private synchronized void makeCompositeBuilder()
/*      */       throws InvalidObjectException
/*      */     {
/*  846 */       if (this.compositeBuilder != null) {
/*  847 */         return;
/*      */       }
/*  849 */       Class localClass = (Class)getJavaType();
/*      */ 
/*  853 */       DefaultMXBeanMappingFactory.CompositeBuilder[][] arrayOfCompositeBuilder; = { { new DefaultMXBeanMappingFactory.CompositeBuilderViaFrom(localClass, this.itemNames) }, { new DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor(localClass, this.itemNames) }, { new DefaultMXBeanMappingFactory.CompositeBuilderCheckGetters(localClass, this.itemNames, this.getterMappings), new DefaultMXBeanMappingFactory.CompositeBuilderViaSetters(localClass, this.itemNames), new DefaultMXBeanMappingFactory.CompositeBuilderViaProxy(localClass, this.itemNames) } };
/*      */ 
/*  867 */       Object localObject1 = null;
/*      */ 
/*  871 */       StringBuilder localStringBuilder = new StringBuilder();
/*  872 */       Object localObject2 = null;
/*      */ 
/*  874 */       for (Object localObject4 : arrayOfCompositeBuilder;) {
/*  875 */         for (int k = 0; k < localObject4.length; k++) {
/*  876 */           Object localObject5 = localObject4[k];
/*  877 */           String str = localObject5.applicable(this.getters);
/*  878 */           if (str == null) {
/*  879 */             localObject1 = localObject5;
/*  880 */             break label268;
/*      */           }
/*  882 */           Throwable localThrowable = localObject5.possibleCause();
/*  883 */           if (localThrowable != null)
/*  884 */             localObject2 = localThrowable;
/*  885 */           if (str.length() > 0) {
/*  886 */             if (localStringBuilder.length() > 0)
/*  887 */               localStringBuilder.append("; ");
/*  888 */             localStringBuilder.append(str);
/*  889 */             if (k == 0)
/*      */               break;
/*      */           }
/*      */         }
/*      */       }
/*  894 */       label268: if (localObject1 == null) {
/*  895 */         ??? = "Do not know how to make a " + localClass.getName() + " from a CompositeData: " + localStringBuilder;
/*      */ 
/*  898 */         if (localObject2 != null)
/*  899 */           ??? = (String)??? + ". Remaining exceptions show a POSSIBLE cause.";
/*  900 */         throw DefaultMXBeanMappingFactory.invalidObjectException((String)???, localObject2);
/*      */       }
/*  902 */       this.compositeBuilder = localObject1;
/*      */     }
/*      */ 
/*      */     public void checkReconstructible() throws InvalidObjectException
/*      */     {
/*  907 */       makeCompositeBuilder();
/*      */     }
/*      */ 
/*      */     final Object fromNonNullOpenValue(Object paramObject)
/*      */       throws InvalidObjectException
/*      */     {
/*  913 */       makeCompositeBuilder();
/*  914 */       return this.compositeBuilder.fromCompositeData((CompositeData)paramObject, this.itemNames, this.getterMappings);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class EnumMapping<T extends Enum<T>> extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
/*      */   {
/*      */     private final Class<T> enumClass;
/*      */ 
/*      */     EnumMapping(Class<T> paramClass)
/*      */     {
/*  516 */       super(SimpleType.STRING);
/*  517 */       this.enumClass = paramClass;
/*      */     }
/*      */ 
/*      */     final Object toNonNullOpenValue(Object paramObject)
/*      */     {
/*  522 */       return ((Enum)paramObject).name();
/*      */     }
/*      */ 
/*      */     final T fromNonNullOpenValue(Object paramObject) throws InvalidObjectException
/*      */     {
/*      */       try
/*      */       {
/*  529 */         return Enum.valueOf(this.enumClass, (String)paramObject);
/*      */       } catch (Exception localException) {
/*  531 */         throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot convert to enum: " + paramObject, localException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class IdentityMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
/*      */   {
/*      */     IdentityMapping(Type paramType, OpenType<?> paramOpenType)
/*      */     {
/*  493 */       super(paramOpenType);
/*      */     }
/*      */ 
/*      */     boolean isIdentity() {
/*  497 */       return true;
/*      */     }
/*      */ 
/*      */     Object fromNonNullOpenValue(Object paramObject)
/*      */       throws InvalidObjectException
/*      */     {
/*  503 */       return paramObject;
/*      */     }
/*      */ 
/*      */     Object toNonNullOpenValue(Object paramObject) throws OpenDataException
/*      */     {
/*  508 */       return paramObject;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class MXBeanRefMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
/*      */   {
/*      */     MXBeanRefMapping(Type paramType)
/*      */     {
/*  679 */       super(SimpleType.OBJECTNAME);
/*      */     }
/*      */ 
/*      */     final Object toNonNullOpenValue(Object paramObject)
/*      */       throws OpenDataException
/*      */     {
/*  685 */       MXBeanLookup localMXBeanLookup = lookupNotNull(OpenDataException.class);
/*  686 */       ObjectName localObjectName = localMXBeanLookup.mxbeanToObjectName(paramObject);
/*  687 */       if (localObjectName == null)
/*  688 */         throw new OpenDataException("No name for object: " + paramObject);
/*  689 */       return localObjectName;
/*      */     }
/*      */ 
/*      */     final Object fromNonNullOpenValue(Object paramObject)
/*      */       throws InvalidObjectException
/*      */     {
/*  695 */       MXBeanLookup localMXBeanLookup = lookupNotNull(InvalidObjectException.class);
/*  696 */       ObjectName localObjectName = (ObjectName)paramObject;
/*  697 */       Object localObject = localMXBeanLookup.objectNameToMXBean(localObjectName, (Class)getJavaType());
/*      */ 
/*  699 */       if (localObject == null) {
/*  700 */         String str = "No MXBean for name: " + localObjectName;
/*      */ 
/*  702 */         throw new InvalidObjectException(str);
/*      */       }
/*  704 */       return localObject;
/*      */     }
/*      */ 
/*      */     private <T extends Exception> MXBeanLookup lookupNotNull(Class<T> paramClass)
/*      */       throws Exception
/*      */     {
/*  710 */       MXBeanLookup localMXBeanLookup = MXBeanLookup.getLookup();
/*  711 */       if (localMXBeanLookup == null)
/*      */       {
/*      */         Exception localException1;
/*      */         try
/*      */         {
/*  716 */           Constructor localConstructor = paramClass.getConstructor(new Class[] { String.class });
/*  717 */           localException1 = (Exception)localConstructor.newInstance(new Object[] { "Cannot convert MXBean interface in this context" });
/*      */         } catch (Exception localException2) {
/*  719 */           throw new RuntimeException(localException2);
/*      */         }
/*  721 */         throw localException1;
/*      */       }
/*  723 */       return localMXBeanLookup;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class Mappings extends WeakHashMap<Type, WeakReference<MXBeanMapping>>
/*      */   {
/*      */   }
/*      */ 
/*      */   static abstract class NonNullMXBeanMapping extends MXBeanMapping
/*      */   {
/*      */     NonNullMXBeanMapping(Type paramType, OpenType<?> paramOpenType)
/*      */     {
/*  121 */       super(paramOpenType);
/*      */     }
/*      */ 
/*      */     public final Object fromOpenValue(Object paramObject)
/*      */       throws InvalidObjectException
/*      */     {
/*  127 */       if (paramObject == null) {
/*  128 */         return null;
/*      */       }
/*  130 */       return fromNonNullOpenValue(paramObject);
/*      */     }
/*      */ 
/*      */     public final Object toOpenValue(Object paramObject) throws OpenDataException
/*      */     {
/*  135 */       if (paramObject == null) {
/*  136 */         return null;
/*      */       }
/*  138 */       return toNonNullOpenValue(paramObject);
/*      */     }
/*      */ 
/*      */     abstract Object fromNonNullOpenValue(Object paramObject)
/*      */       throws InvalidObjectException;
/*      */ 
/*      */     abstract Object toNonNullOpenValue(Object paramObject)
/*      */       throws OpenDataException;
/*      */ 
/*      */     boolean isIdentity()
/*      */     {
/*  152 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class TabularMapping extends DefaultMXBeanMappingFactory.NonNullMXBeanMapping
/*      */   {
/*      */     private final boolean sortedMap;
/*      */     private final MXBeanMapping keyMapping;
/*      */     private final MXBeanMapping valueMapping;
/*      */ 
/*      */     TabularMapping(Type paramType, boolean paramBoolean, TabularType paramTabularType, MXBeanMapping paramMXBeanMapping1, MXBeanMapping paramMXBeanMapping2)
/*      */     {
/*  733 */       super(paramTabularType);
/*  734 */       this.sortedMap = paramBoolean;
/*  735 */       this.keyMapping = paramMXBeanMapping1;
/*  736 */       this.valueMapping = paramMXBeanMapping2;
/*      */     }
/*      */ 
/*      */     final Object toNonNullOpenValue(Object paramObject) throws OpenDataException
/*      */     {
/*  741 */       Map localMap = (Map)Util.cast(paramObject);
/*  742 */       if ((localMap instanceof SortedMap)) {
/*  743 */         localObject1 = ((SortedMap)localMap).comparator();
/*  744 */         if (localObject1 != null) {
/*  745 */           localObject2 = "Cannot convert SortedMap with non-null comparator: " + localObject1;
/*      */ 
/*  748 */           throw DefaultMXBeanMappingFactory.openDataException((String)localObject2, new IllegalArgumentException((String)localObject2));
/*      */         }
/*      */       }
/*  751 */       Object localObject1 = (TabularType)getOpenType();
/*  752 */       Object localObject2 = new TabularDataSupport((TabularType)localObject1);
/*  753 */       CompositeType localCompositeType = ((TabularType)localObject1).getRowType();
/*  754 */       for (Map.Entry localEntry : localMap.entrySet()) {
/*  755 */         Object localObject3 = this.keyMapping.toOpenValue(localEntry.getKey());
/*  756 */         Object localObject4 = this.valueMapping.toOpenValue(localEntry.getValue());
/*      */ 
/*  758 */         CompositeDataSupport localCompositeDataSupport = new CompositeDataSupport(localCompositeType, DefaultMXBeanMappingFactory.keyValueArray, new Object[] { localObject3, localObject4 });
/*      */ 
/*  762 */         ((TabularData)localObject2).put(localCompositeDataSupport);
/*      */       }
/*  764 */       return localObject2;
/*      */     }
/*      */ 
/*      */     final Object fromNonNullOpenValue(Object paramObject)
/*      */       throws InvalidObjectException
/*      */     {
/*  770 */       TabularData localTabularData = (TabularData)paramObject;
/*  771 */       Collection localCollection = (Collection)Util.cast(localTabularData.values());
/*  772 */       Map localMap = this.sortedMap ? Util.newSortedMap() : Util.newInsertionOrderMap();
/*      */ 
/*  774 */       for (CompositeData localCompositeData : localCollection) {
/*  775 */         Object localObject1 = this.keyMapping.fromOpenValue(localCompositeData.get("key"));
/*      */ 
/*  777 */         Object localObject2 = this.valueMapping.fromOpenValue(localCompositeData.get("value"));
/*      */ 
/*  779 */         if (localMap.put(localObject1, localObject2) != null) {
/*  780 */           String str = "Duplicate entry in TabularData: key=" + localObject1;
/*      */ 
/*  782 */           throw new InvalidObjectException(str);
/*      */         }
/*      */       }
/*  785 */       return localMap;
/*      */     }
/*      */ 
/*      */     public void checkReconstructible() throws InvalidObjectException
/*      */     {
/*  790 */       this.keyMapping.checkReconstructible();
/*  791 */       this.valueMapping.checkReconstructible();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory
 * JD-Core Version:    0.6.2
 */