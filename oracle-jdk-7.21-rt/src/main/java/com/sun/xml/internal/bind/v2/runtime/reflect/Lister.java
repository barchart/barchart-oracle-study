/*     */ package com.sun.xml.internal.bind.v2.runtime.reflect;
/*     */ 
/*     */ import com.sun.istack.internal.SAXException2;
/*     */ import com.sun.xml.internal.bind.api.AccessorException;
/*     */ import com.sun.xml.internal.bind.v2.ClassFactory;
/*     */ import com.sun.xml.internal.bind.v2.TODO;
/*     */ import com.sun.xml.internal.bind.v2.model.core.Adapter;
/*     */ import com.sun.xml.internal.bind.v2.model.core.ID;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*     */ import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
/*     */ import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
/*     */ import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx.Snapshot;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Patcher;
/*     */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Stack;
/*     */ import java.util.TreeSet;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.Callable;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public abstract class Lister<BeanT, PropT, ItemT, PackT>
/*     */ {
/*     */   private static final Map<Class, WeakReference<Lister>> arrayListerCache;
/*     */   static final Map<Class, Lister> primitiveArrayListers;
/* 457 */   public static final Lister ERROR = new Lister() {
/*     */     public ListIterator iterator(Object o, XMLSerializer context) {
/* 459 */       return Lister.EMPTY_ITERATOR;
/*     */     }
/*     */ 
/*     */     public Object startPacking(Object o, Accessor accessor) {
/* 463 */       return null;
/*     */     }
/*     */ 
/*     */     public void addToPack(Object o, Object o1)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void endPacking(Object o, Object o1, Accessor accessor)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void reset(Object o, Accessor accessor)
/*     */     {
/*     */     }
/* 457 */   };
/*     */ 
/* 476 */   private static final ListIterator EMPTY_ITERATOR = new ListIterator() {
/*     */     public boolean hasNext() {
/* 478 */       return false;
/*     */     }
/*     */ 
/*     */     public Object next() {
/* 482 */       throw new IllegalStateException();
/*     */     }
/* 476 */   };
/*     */ 
/* 486 */   private static final Class[] COLLECTION_IMPL_CLASSES = { ArrayList.class, LinkedList.class, HashSet.class, TreeSet.class, Stack.class };
/*     */ 
/*     */   public abstract ListIterator<ItemT> iterator(PropT paramPropT, XMLSerializer paramXMLSerializer);
/*     */ 
/*     */   public abstract PackT startPacking(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
/*     */     throws AccessorException;
/*     */ 
/*     */   public abstract void addToPack(PackT paramPackT, ItemT paramItemT)
/*     */     throws AccessorException;
/*     */ 
/*     */   public abstract void endPacking(PackT paramPackT, BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
/*     */     throws AccessorException;
/*     */ 
/*     */   public abstract void reset(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
/*     */     throws AccessorException;
/*     */ 
/*     */   public static <BeanT, PropT, ItemT, PackT> Lister<BeanT, PropT, ItemT, PackT> create(Type fieldType, ID idness, Adapter<Type, Class> adapter)
/*     */   {
/* 119 */     Class rawType = Navigator.REFLECTION.erasure(fieldType);
/*     */     Lister l;
/* 123 */     if (rawType.isArray()) {
/* 124 */       Class itemType = rawType.getComponentType();
/* 125 */       l = getArrayLister(itemType);
/*     */     }
/*     */     else
/*     */     {
/*     */       Lister l;
/* 127 */       if (Collection.class.isAssignableFrom(rawType)) {
/* 128 */         Type bt = Navigator.REFLECTION.getBaseClass(fieldType, Collection.class);
/*     */         Class itemType;
/*     */         Class itemType;
/* 129 */         if ((bt instanceof ParameterizedType))
/* 130 */           itemType = Navigator.REFLECTION.erasure(((ParameterizedType)bt).getActualTypeArguments()[0]);
/*     */         else
/* 132 */           itemType = Object.class;
/* 133 */         l = new CollectionLister(getImplClass(rawType));
/*     */       } else {
/* 135 */         return null;
/*     */       }
/*     */     }
/*     */     Lister l;
/*     */     Class itemType;
/* 137 */     if (idness == ID.IDREF) {
/* 138 */       l = new IDREFS(l, itemType);
/*     */     }
/* 140 */     if (adapter != null) {
/* 141 */       l = new AdaptedLister(l, (Class)adapter.adapterType);
/*     */     }
/* 143 */     return l;
/*     */   }
/*     */ 
/*     */   private static Class getImplClass(Class<?> fieldType) {
/* 147 */     return ClassFactory.inferImplClass(fieldType, COLLECTION_IMPL_CLASSES);
/*     */   }
/*     */ 
/*     */   private static Lister getArrayLister(Class componentType)
/*     */   {
/* 160 */     Lister l = null;
/* 161 */     if (componentType.isPrimitive()) {
/* 162 */       l = (Lister)primitiveArrayListers.get(componentType);
/*     */     } else {
/* 164 */       WeakReference wr = (WeakReference)arrayListerCache.get(componentType);
/* 165 */       if (wr != null)
/* 166 */         l = (Lister)wr.get();
/* 167 */       if (l == null) {
/* 168 */         l = new ArrayLister(componentType);
/* 169 */         arrayListerCache.put(componentType, new WeakReference(l));
/*     */       }
/*     */     }
/* 172 */     assert (l != null);
/* 173 */     return l;
/*     */   }
/*     */ 
/*     */   public static <A, B, C, D> Lister<A, B, C, D> getErrorInstance()
/*     */   {
/* 454 */     return ERROR;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 153 */     arrayListerCache = Collections.synchronizedMap(new WeakHashMap());
/*     */ 
/* 237 */     primitiveArrayListers = new HashMap();
/*     */ 
/* 241 */     PrimitiveArrayListerBoolean.register();
/* 242 */     PrimitiveArrayListerByte.register();
/* 243 */     PrimitiveArrayListerCharacter.register();
/* 244 */     PrimitiveArrayListerDouble.register();
/* 245 */     PrimitiveArrayListerFloat.register();
/* 246 */     PrimitiveArrayListerInteger.register();
/* 247 */     PrimitiveArrayListerLong.register();
/* 248 */     PrimitiveArrayListerShort.register();
/*     */   }
/*     */ 
/*     */   private static final class ArrayLister<BeanT, ItemT> extends Lister<BeanT, ItemT[], ItemT, Lister.Pack<ItemT>>
/*     */   {
/*     */     private final Class<ItemT> itemType;
/*     */ 
/*     */     public ArrayLister(Class<ItemT> itemType)
/*     */     {
/* 188 */       this.itemType = itemType;
/*     */     }
/*     */ 
/*     */     public ListIterator<ItemT> iterator(final ItemT[] objects, XMLSerializer context) {
/* 192 */       return new ListIterator() {
/* 193 */         int idx = 0;
/*     */ 
/* 195 */         public boolean hasNext() { return this.idx < objects.length; }
/*     */ 
/*     */         public ItemT next()
/*     */         {
/* 199 */           return objects[(this.idx++)];
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public Lister.Pack startPacking(BeanT current, Accessor<BeanT, ItemT[]> acc) {
/* 205 */       return new Lister.Pack(this.itemType);
/*     */     }
/*     */ 
/*     */     public void addToPack(Lister.Pack<ItemT> objects, ItemT o) {
/* 209 */       objects.add(o);
/*     */     }
/*     */ 
/*     */     public void endPacking(Lister.Pack<ItemT> pack, BeanT bean, Accessor<BeanT, ItemT[]> acc) throws AccessorException {
/* 213 */       acc.set(bean, pack.build());
/*     */     }
/*     */ 
/*     */     public void reset(BeanT o, Accessor<BeanT, ItemT[]> acc) throws AccessorException {
/* 217 */       acc.set(o, (Object[])Array.newInstance(this.itemType, 0));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class CollectionLister<BeanT, T extends Collection> extends Lister<BeanT, T, Object, T>
/*     */   {
/*     */     private final Class<? extends T> implClass;
/*     */ 
/*     */     public CollectionLister(Class<? extends T> implClass)
/*     */     {
/* 263 */       this.implClass = implClass;
/*     */     }
/*     */ 
/*     */     public ListIterator iterator(T collection, XMLSerializer context) {
/* 267 */       final Iterator itr = collection.iterator();
/* 268 */       return new ListIterator() {
/*     */         public boolean hasNext() {
/* 270 */           return itr.hasNext();
/*     */         }
/*     */         public Object next() {
/* 273 */           return itr.next();
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public T startPacking(BeanT bean, Accessor<BeanT, T> acc) throws AccessorException {
/* 279 */       Collection collection = (Collection)acc.get(bean);
/* 280 */       if (collection == null) {
/* 281 */         collection = (Collection)ClassFactory.create(this.implClass);
/* 282 */         if (!acc.isAdapted())
/* 283 */           acc.set(bean, collection);
/*     */       }
/* 285 */       collection.clear();
/* 286 */       return collection;
/*     */     }
/*     */ 
/*     */     public void addToPack(T collection, Object o) {
/* 290 */       collection.add(o);
/*     */     }
/*     */ 
/*     */     public void endPacking(T collection, BeanT bean, Accessor<BeanT, T> acc)
/*     */       throws AccessorException
/*     */     {
/*     */       try
/*     */       {
/* 303 */         acc.set(bean, collection);
/*     */       } catch (AccessorException ae) {
/* 305 */         if (acc.isAdapted()) throw ae; 
/*     */       }
/*     */     }
/*     */ 
/*     */     public void reset(BeanT bean, Accessor<BeanT, T> acc) throws AccessorException {
/* 310 */       Collection collection = (Collection)acc.get(bean);
/* 311 */       if (collection == null) {
/* 312 */         return;
/*     */       }
/* 314 */       collection.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class IDREFS<BeanT, PropT> extends Lister<BeanT, PropT, String, IDREFS<BeanT, PropT>.Pack>
/*     */   {
/*     */     private final Lister<BeanT, PropT, Object, Object> core;
/*     */     private final Class itemType;
/*     */ 
/*     */     public IDREFS(Lister core, Class itemType)
/*     */     {
/* 329 */       this.core = core;
/* 330 */       this.itemType = itemType;
/*     */     }
/*     */ 
/*     */     public ListIterator<String> iterator(PropT prop, XMLSerializer context) {
/* 334 */       ListIterator i = this.core.iterator(prop, context);
/*     */ 
/* 336 */       return new Lister.IDREFSIterator(i, context, null);
/*     */     }
/*     */ 
/*     */     public IDREFS<BeanT, PropT>.Pack startPacking(BeanT bean, Accessor<BeanT, PropT> acc) {
/* 340 */       return new Pack(bean, acc);
/*     */     }
/*     */ 
/*     */     public void addToPack(IDREFS<BeanT, PropT>.Pack pack, String item) {
/* 344 */       pack.add(item);
/*     */     }
/*     */ 
/*     */     public void endPacking(IDREFS<BeanT, PropT>.Pack pack, BeanT bean, Accessor<BeanT, PropT> acc) {
/*     */     }
/*     */ 
/*     */     public void reset(BeanT bean, Accessor<BeanT, PropT> acc) throws AccessorException {
/* 351 */       this.core.reset(bean, acc);
/*     */     }
/*     */     private class Pack implements Patcher {
/*     */       private final BeanT bean;
/* 359 */       private final List<String> idrefs = new ArrayList();
/*     */       private final UnmarshallingContext context;
/*     */       private final Accessor<BeanT, PropT> acc;
/*     */       private final LocatorEx location;
/*     */ 
/*     */       public Pack(Accessor<BeanT, PropT> bean) {
/* 365 */         this.bean = bean;
/* 366 */         this.acc = acc;
/* 367 */         this.context = UnmarshallingContext.getInstance();
/* 368 */         this.location = new LocatorEx.Snapshot(this.context.getLocator());
/* 369 */         this.context.addPatcher(this);
/*     */       }
/*     */ 
/*     */       public void add(String item) {
/* 373 */         this.idrefs.add(item);
/*     */       }
/*     */ 
/*     */       public void run()
/*     */         throws SAXException
/*     */       {
/*     */         try
/*     */         {
/* 381 */           Object pack = Lister.this.startPacking(this.bean, this.acc);
/*     */ 
/* 383 */           for (String id : this.idrefs) {
/* 384 */             Callable callable = this.context.getObjectFromId(id, Lister.IDREFS.this.itemType);
/*     */             Object t;
/*     */             try {
/* 388 */               t = callable != null ? callable.call() : null;
/*     */             } catch (SAXException e) {
/* 390 */               throw e;
/*     */             } catch (Exception e) {
/* 392 */               throw new SAXException2(e);
/*     */             }
/*     */ 
/* 395 */             if (t == null) {
/* 396 */               this.context.errorUnresolvedIDREF(this.bean, id, this.location);
/*     */             } else {
/* 398 */               TODO.prototype();
/* 399 */               Lister.this.addToPack(pack, t);
/*     */             }
/*     */           }
/*     */ 
/* 403 */           Lister.this.endPacking(pack, this.bean, this.acc);
/*     */         } catch (AccessorException e) {
/* 405 */           this.context.handleError(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IDREFSIterator
/*     */     implements ListIterator<String>
/*     */   {
/*     */     private final ListIterator i;
/*     */     private final XMLSerializer context;
/*     */     private Object last;
/*     */ 
/*     */     private IDREFSIterator(ListIterator i, XMLSerializer context)
/*     */     {
/* 424 */       this.i = i;
/* 425 */       this.context = context;
/*     */     }
/*     */ 
/*     */     public boolean hasNext() {
/* 429 */       return this.i.hasNext();
/*     */     }
/*     */ 
/*     */     public Object last()
/*     */     {
/* 436 */       return this.last;
/*     */     }
/*     */ 
/*     */     public String next() throws SAXException, JAXBException {
/* 440 */       this.last = this.i.next();
/* 441 */       String id = this.context.grammar.getBeanInfo(this.last, true).getId(this.last, this.context);
/* 442 */       if (id == null) {
/* 443 */         this.context.errorMissingId(this.last);
/*     */       }
/* 445 */       return id;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Pack<ItemT> extends ArrayList<ItemT>
/*     */   {
/*     */     private final Class<ItemT> itemType;
/*     */ 
/*     */     public Pack(Class<ItemT> itemType)
/*     */     {
/* 226 */       this.itemType = itemType;
/*     */     }
/*     */ 
/*     */     public ItemT[] build() {
/* 230 */       return super.toArray((Object[])Array.newInstance(this.itemType, size()));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.reflect.Lister
 * JD-Core Version:    0.6.2
 */