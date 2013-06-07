/*     */ package java.beans;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ abstract class java_util_Collections extends PersistenceDelegate
/*     */ {
/*     */   protected boolean mutatesTo(Object paramObject1, Object paramObject2)
/*     */   {
/* 359 */     if (!super.mutatesTo(paramObject1, paramObject2)) {
/* 360 */       return false;
/*     */     }
/* 362 */     if (((paramObject1 instanceof List)) || ((paramObject1 instanceof Set)) || ((paramObject1 instanceof Map))) {
/* 363 */       return paramObject1.equals(paramObject2);
/*     */     }
/* 365 */     Collection localCollection1 = (Collection)paramObject1;
/* 366 */     Collection localCollection2 = (Collection)paramObject2;
/* 367 */     return (localCollection1.size() == localCollection2.size()) && (localCollection1.containsAll(localCollection2));
/*     */   }
/*     */ 
/*     */   static final class CheckedCollection_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 510 */       Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
/* 511 */       ArrayList localArrayList = new ArrayList((Collection)paramObject);
/* 512 */       return new Expression(paramObject, Collections.class, "checkedCollection", new Object[] { localArrayList, localObject });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class CheckedList_PersistenceDelegate extends java_util_Collections {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 518 */       Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
/* 519 */       LinkedList localLinkedList = new LinkedList((Collection)paramObject);
/* 520 */       return new Expression(paramObject, Collections.class, "checkedList", new Object[] { localLinkedList, localObject });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class CheckedMap_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 550 */       Object localObject1 = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedMap.keyType");
/* 551 */       Object localObject2 = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedMap.valueType");
/* 552 */       HashMap localHashMap = new HashMap((Map)paramObject);
/* 553 */       return new Expression(paramObject, Collections.class, "checkedMap", new Object[] { localHashMap, localObject1, localObject2 });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class CheckedRandomAccessList_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 526 */       Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
/* 527 */       ArrayList localArrayList = new ArrayList((Collection)paramObject);
/* 528 */       return new Expression(paramObject, Collections.class, "checkedList", new Object[] { localArrayList, localObject });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class CheckedSet_PersistenceDelegate extends java_util_Collections {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 534 */       Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
/* 535 */       HashSet localHashSet = new HashSet((Set)paramObject);
/* 536 */       return new Expression(paramObject, Collections.class, "checkedSet", new Object[] { localHashSet, localObject });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class CheckedSortedMap_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 559 */       Object localObject1 = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedMap.keyType");
/* 560 */       Object localObject2 = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedMap.valueType");
/* 561 */       TreeMap localTreeMap = new TreeMap((SortedMap)paramObject);
/* 562 */       return new Expression(paramObject, Collections.class, "checkedSortedMap", new Object[] { localTreeMap, localObject1, localObject2 });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class CheckedSortedSet_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 542 */       Object localObject = MetaData.getPrivateFieldValue(paramObject, "java.util.Collections$CheckedCollection.type");
/* 543 */       TreeSet localTreeSet = new TreeSet((SortedSet)paramObject);
/* 544 */       return new Expression(paramObject, Collections.class, "checkedSortedSet", new Object[] { localTreeSet, localObject });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class EmptyList_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 372 */       return new Expression(paramObject, Collections.class, "emptyList", null);
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class EmptyMap_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 384 */       return new Expression(paramObject, Collections.class, "emptyMap", null);
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class EmptySet_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 378 */       return new Expression(paramObject, Collections.class, "emptySet", null);
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SingletonList_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 390 */       List localList = (List)paramObject;
/* 391 */       return new Expression(paramObject, Collections.class, "singletonList", new Object[] { localList.get(0) });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SingletonMap_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 404 */       Map localMap = (Map)paramObject;
/* 405 */       Object localObject = localMap.keySet().iterator().next();
/* 406 */       return new Expression(paramObject, Collections.class, "singletonMap", new Object[] { localObject, localMap.get(localObject) });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SingletonSet_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 397 */       Set localSet = (Set)paramObject;
/* 398 */       return new Expression(paramObject, Collections.class, "singleton", new Object[] { localSet.iterator().next() });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SynchronizedCollection_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 461 */       ArrayList localArrayList = new ArrayList((Collection)paramObject);
/* 462 */       return new Expression(paramObject, Collections.class, "synchronizedCollection", new Object[] { localArrayList });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SynchronizedList_PersistenceDelegate extends java_util_Collections {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 468 */       LinkedList localLinkedList = new LinkedList((Collection)paramObject);
/* 469 */       return new Expression(paramObject, Collections.class, "synchronizedList", new Object[] { localLinkedList });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SynchronizedMap_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 496 */       HashMap localHashMap = new HashMap((Map)paramObject);
/* 497 */       return new Expression(paramObject, Collections.class, "synchronizedMap", new Object[] { localHashMap });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SynchronizedRandomAccessList_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 475 */       ArrayList localArrayList = new ArrayList((Collection)paramObject);
/* 476 */       return new Expression(paramObject, Collections.class, "synchronizedList", new Object[] { localArrayList });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SynchronizedSet_PersistenceDelegate extends java_util_Collections {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 482 */       HashSet localHashSet = new HashSet((Set)paramObject);
/* 483 */       return new Expression(paramObject, Collections.class, "synchronizedSet", new Object[] { localHashSet });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SynchronizedSortedMap_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 503 */       TreeMap localTreeMap = new TreeMap((SortedMap)paramObject);
/* 504 */       return new Expression(paramObject, Collections.class, "synchronizedSortedMap", new Object[] { localTreeMap });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class SynchronizedSortedSet_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 489 */       TreeSet localTreeSet = new TreeSet((SortedSet)paramObject);
/* 490 */       return new Expression(paramObject, Collections.class, "synchronizedSortedSet", new Object[] { localTreeSet });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnmodifiableCollection_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 412 */       ArrayList localArrayList = new ArrayList((Collection)paramObject);
/* 413 */       return new Expression(paramObject, Collections.class, "unmodifiableCollection", new Object[] { localArrayList });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnmodifiableList_PersistenceDelegate extends java_util_Collections {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 419 */       LinkedList localLinkedList = new LinkedList((Collection)paramObject);
/* 420 */       return new Expression(paramObject, Collections.class, "unmodifiableList", new Object[] { localLinkedList });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnmodifiableMap_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 447 */       HashMap localHashMap = new HashMap((Map)paramObject);
/* 448 */       return new Expression(paramObject, Collections.class, "unmodifiableMap", new Object[] { localHashMap });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnmodifiableRandomAccessList_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 426 */       ArrayList localArrayList = new ArrayList((Collection)paramObject);
/* 427 */       return new Expression(paramObject, Collections.class, "unmodifiableList", new Object[] { localArrayList });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnmodifiableSet_PersistenceDelegate extends java_util_Collections {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
/* 433 */       HashSet localHashSet = new HashSet((Set)paramObject);
/* 434 */       return new Expression(paramObject, Collections.class, "unmodifiableSet", new Object[] { localHashSet });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnmodifiableSortedMap_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 454 */       TreeMap localTreeMap = new TreeMap((SortedMap)paramObject);
/* 455 */       return new Expression(paramObject, Collections.class, "unmodifiableSortedMap", new Object[] { localTreeMap });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnmodifiableSortedSet_PersistenceDelegate extends java_util_Collections
/*     */   {
/*     */     protected Expression instantiate(Object paramObject, Encoder paramEncoder)
/*     */     {
/* 440 */       TreeSet localTreeSet = new TreeSet((SortedSet)paramObject);
/* 441 */       return new Expression(paramObject, Collections.class, "unmodifiableSortedSet", new Object[] { localTreeSet });
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.java_util_Collections
 * JD-Core Version:    0.6.2
 */