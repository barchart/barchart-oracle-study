/*      */ package java.beans;
/*      */ 
/*      */ import com.sun.beans.WeakCache;
/*      */ import com.sun.beans.finder.BeanInfoFinder;
/*      */ import com.sun.beans.finder.ClassFinder;
/*      */ import java.awt.Component;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.EventListener;
/*      */ import java.util.EventObject;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TooManyListenersException;
/*      */ import java.util.TreeMap;
/*      */ import sun.reflect.misc.ReflectUtil;
/*      */ 
/*      */ public class Introspector
/*      */ {
/*      */   public static final int USE_ALL_BEANINFO = 1;
/*      */   public static final int IGNORE_IMMEDIATE_BEANINFO = 2;
/*      */   public static final int IGNORE_ALL_BEANINFO = 3;
/*   98 */   private static final WeakCache<Class<?>, Method[]> declaredMethodCache = new WeakCache();
/*      */   private Class beanClass;
/*      */   private BeanInfo explicitBeanInfo;
/*      */   private BeanInfo superBeanInfo;
/*      */   private BeanInfo[] additionalBeanInfo;
/*  105 */   private boolean propertyChangeSource = false;
/*  106 */   private static Class eventListenerType = EventListener.class;
/*      */   private String defaultEventName;
/*      */   private String defaultPropertyName;
/*  111 */   private int defaultEventIndex = -1;
/*  112 */   private int defaultPropertyIndex = -1;
/*      */   private Map methods;
/*      */   private Map properties;
/*      */   private Map events;
/*  123 */   private static final EventSetDescriptor[] EMPTY_EVENTSETDESCRIPTORS = new EventSetDescriptor[0];
/*      */   static final String ADD_PREFIX = "add";
/*      */   static final String REMOVE_PREFIX = "remove";
/*      */   static final String GET_PREFIX = "get";
/*      */   static final String SET_PREFIX = "set";
/*      */   static final String IS_PREFIX = "is";
/*  559 */   private HashMap pdStore = new HashMap();
/*      */ 
/*      */   public static BeanInfo getBeanInfo(Class<?> paramClass)
/*      */     throws IntrospectionException
/*      */   {
/*  152 */     if (!ReflectUtil.isPackageAccessible(paramClass)) {
/*  153 */       return new Introspector(paramClass, null, 1).getBeanInfo();
/*      */     }
/*  155 */     ThreadGroupContext localThreadGroupContext = ThreadGroupContext.getContext();
/*      */     BeanInfo localBeanInfo;
/*  157 */     synchronized (declaredMethodCache) {
/*  158 */       localBeanInfo = localThreadGroupContext.getBeanInfo(paramClass);
/*      */     }
/*  160 */     if (localBeanInfo == null) {
/*  161 */       localBeanInfo = new Introspector(paramClass, null, 1).getBeanInfo();
/*  162 */       synchronized (declaredMethodCache) {
/*  163 */         localThreadGroupContext.putBeanInfo(paramClass, localBeanInfo);
/*      */       }
/*      */     }
/*  166 */     return localBeanInfo;
/*      */   }
/*      */ 
/*      */   public static BeanInfo getBeanInfo(Class<?> paramClass, int paramInt)
/*      */     throws IntrospectionException
/*      */   {
/*  192 */     return getBeanInfo(paramClass, null, paramInt);
/*      */   }
/*      */ 
/*      */   public static BeanInfo getBeanInfo(Class<?> paramClass1, Class<?> paramClass2)
/*      */     throws IntrospectionException
/*      */   {
/*  212 */     return getBeanInfo(paramClass1, paramClass2, 1);
/*      */   }
/*      */ 
/*      */   public static BeanInfo getBeanInfo(Class<?> paramClass1, Class<?> paramClass2, int paramInt)
/*      */     throws IntrospectionException
/*      */   {
/*      */     BeanInfo localBeanInfo;
/*  246 */     if ((paramClass2 == null) && (paramInt == 1))
/*      */     {
/*  248 */       localBeanInfo = getBeanInfo(paramClass1);
/*      */     }
/*  250 */     else localBeanInfo = new Introspector(paramClass1, paramClass2, paramInt).getBeanInfo();
/*      */ 
/*  252 */     return localBeanInfo;
/*      */   }
/*      */ 
/*      */   public static String decapitalize(String paramString)
/*      */   {
/*  273 */     if ((paramString == null) || (paramString.length() == 0)) {
/*  274 */       return paramString;
/*      */     }
/*  276 */     if ((paramString.length() > 1) && (Character.isUpperCase(paramString.charAt(1))) && (Character.isUpperCase(paramString.charAt(0))))
/*      */     {
/*  278 */       return paramString;
/*      */     }
/*  280 */     char[] arrayOfChar = paramString.toCharArray();
/*  281 */     arrayOfChar[0] = Character.toLowerCase(arrayOfChar[0]);
/*  282 */     return new String(arrayOfChar);
/*      */   }
/*      */ 
/*      */   public static String[] getBeanInfoSearchPath()
/*      */   {
/*  296 */     return ThreadGroupContext.getContext().getBeanInfoFinder().getPackages();
/*      */   }
/*      */ 
/*      */   public static void setBeanInfoSearchPath(String[] paramArrayOfString)
/*      */   {
/*  316 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  317 */     if (localSecurityManager != null) {
/*  318 */       localSecurityManager.checkPropertiesAccess();
/*      */     }
/*  320 */     ThreadGroupContext.getContext().getBeanInfoFinder().setPackages(paramArrayOfString);
/*      */   }
/*      */ 
/*      */   public static void flushCaches()
/*      */   {
/*  332 */     synchronized (declaredMethodCache) {
/*  333 */       ThreadGroupContext.getContext().clearBeanInfoCache();
/*  334 */       declaredMethodCache.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void flushFromCaches(Class<?> paramClass)
/*      */   {
/*  354 */     if (paramClass == null) {
/*  355 */       throw new NullPointerException();
/*      */     }
/*  357 */     synchronized (declaredMethodCache) {
/*  358 */       ThreadGroupContext.getContext().removeBeanInfo(paramClass);
/*  359 */       declaredMethodCache.put(paramClass, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Introspector(Class paramClass1, Class paramClass2, int paramInt)
/*      */     throws IntrospectionException
/*      */   {
/*  369 */     this.beanClass = paramClass1;
/*      */ 
/*  372 */     if (paramClass2 != null) {
/*  373 */       int i = 0;
/*  374 */       for (Class localClass2 = paramClass1.getSuperclass(); localClass2 != null; localClass2 = localClass2.getSuperclass()) {
/*  375 */         if (localClass2 == paramClass2) {
/*  376 */           i = 1;
/*      */         }
/*      */       }
/*  379 */       if (i == 0) {
/*  380 */         throw new IntrospectionException(paramClass2.getName() + " not superclass of " + paramClass1.getName());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  385 */     if (paramInt == 1) {
/*  386 */       this.explicitBeanInfo = findExplicitBeanInfo(paramClass1);
/*      */     }
/*      */ 
/*  389 */     Class localClass1 = paramClass1.getSuperclass();
/*  390 */     if (localClass1 != paramClass2) {
/*  391 */       int j = paramInt;
/*  392 */       if (j == 2) {
/*  393 */         j = 1;
/*      */       }
/*  395 */       this.superBeanInfo = getBeanInfo(localClass1, paramClass2, j);
/*      */     }
/*  397 */     if (this.explicitBeanInfo != null) {
/*  398 */       this.additionalBeanInfo = this.explicitBeanInfo.getAdditionalBeanInfo();
/*      */     }
/*  400 */     if (this.additionalBeanInfo == null)
/*  401 */       this.additionalBeanInfo = new BeanInfo[0];
/*      */   }
/*      */ 
/*      */   private BeanInfo getBeanInfo()
/*      */     throws IntrospectionException
/*      */   {
/*  413 */     BeanDescriptor localBeanDescriptor = getTargetBeanDescriptor();
/*  414 */     MethodDescriptor[] arrayOfMethodDescriptor = getTargetMethodInfo();
/*  415 */     EventSetDescriptor[] arrayOfEventSetDescriptor = getTargetEventInfo();
/*  416 */     PropertyDescriptor[] arrayOfPropertyDescriptor = getTargetPropertyInfo();
/*      */ 
/*  418 */     int i = getTargetDefaultEventIndex();
/*  419 */     int j = getTargetDefaultPropertyIndex();
/*      */ 
/*  421 */     return new GenericBeanInfo(localBeanDescriptor, arrayOfEventSetDescriptor, i, arrayOfPropertyDescriptor, j, arrayOfMethodDescriptor, this.explicitBeanInfo);
/*      */   }
/*      */ 
/*      */   private static BeanInfo findExplicitBeanInfo(Class paramClass)
/*      */   {
/*  436 */     return (BeanInfo)ThreadGroupContext.getContext().getBeanInfoFinder().find(paramClass);
/*      */   }
/*      */ 
/*      */   private PropertyDescriptor[] getTargetPropertyInfo()
/*      */   {
/*  448 */     PropertyDescriptor[] arrayOfPropertyDescriptor = null;
/*  449 */     if (this.explicitBeanInfo != null) {
/*  450 */       arrayOfPropertyDescriptor = getPropertyDescriptors(this.explicitBeanInfo);
/*      */     }
/*      */ 
/*  453 */     if ((arrayOfPropertyDescriptor == null) && (this.superBeanInfo != null))
/*      */     {
/*  455 */       addPropertyDescriptors(getPropertyDescriptors(this.superBeanInfo));
/*      */     }
/*      */ 
/*  458 */     for (int i = 0; i < this.additionalBeanInfo.length; i++)
/*  459 */       addPropertyDescriptors(this.additionalBeanInfo[i].getPropertyDescriptors());
/*      */     int j;
/*  462 */     if (arrayOfPropertyDescriptor != null)
/*      */     {
/*  464 */       addPropertyDescriptors(arrayOfPropertyDescriptor);
/*      */     }
/*      */     else
/*      */     {
/*  471 */       localObject1 = getPublicDeclaredMethods(this.beanClass);
/*      */ 
/*  474 */       for (j = 0; j < localObject1.length; j++) {
/*  475 */         Method localMethod = localObject1[j];
/*  476 */         if (localMethod != null)
/*      */         {
/*  480 */           int k = localMethod.getModifiers();
/*  481 */           if (!Modifier.isStatic(k))
/*      */           {
/*  484 */             String str = localMethod.getName();
/*  485 */             Class[] arrayOfClass = localMethod.getParameterTypes();
/*  486 */             Class localClass = localMethod.getReturnType();
/*  487 */             int m = arrayOfClass.length;
/*  488 */             Object localObject2 = null;
/*      */ 
/*  490 */             if ((str.length() > 3) || (str.startsWith("is")))
/*      */             {
/*      */               try
/*      */               {
/*  497 */                 if (m == 0) {
/*  498 */                   if (str.startsWith("get"))
/*      */                   {
/*  500 */                     localObject2 = new PropertyDescriptor(this.beanClass, str.substring(3), localMethod, null);
/*  501 */                   } else if ((localClass == Boolean.TYPE) && (str.startsWith("is")))
/*      */                   {
/*  503 */                     localObject2 = new PropertyDescriptor(this.beanClass, str.substring(2), localMethod, null);
/*      */                   }
/*  505 */                 } else if (m == 1) {
/*  506 */                   if ((Integer.TYPE.equals(arrayOfClass[0])) && (str.startsWith("get"))) {
/*  507 */                     localObject2 = new IndexedPropertyDescriptor(this.beanClass, str.substring(3), null, null, localMethod, null);
/*  508 */                   } else if ((Void.TYPE.equals(localClass)) && (str.startsWith("set")))
/*      */                   {
/*  510 */                     localObject2 = new PropertyDescriptor(this.beanClass, str.substring(3), null, localMethod);
/*  511 */                     if (throwsException(localMethod, PropertyVetoException.class))
/*  512 */                       ((PropertyDescriptor)localObject2).setConstrained(true);
/*      */                   }
/*      */                 }
/*  515 */                 else if ((m == 2) && 
/*  516 */                   (Void.TYPE.equals(localClass)) && (Integer.TYPE.equals(arrayOfClass[0])) && (str.startsWith("set"))) {
/*  517 */                   localObject2 = new IndexedPropertyDescriptor(this.beanClass, str.substring(3), null, null, null, localMethod);
/*  518 */                   if (throwsException(localMethod, PropertyVetoException.class)) {
/*  519 */                     ((PropertyDescriptor)localObject2).setConstrained(true);
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/*      */               }
/*      */               catch (IntrospectionException localIntrospectionException)
/*      */               {
/*  528 */                 localObject2 = null;
/*      */               }
/*      */ 
/*  531 */               if (localObject2 != null)
/*      */               {
/*  534 */                 if (this.propertyChangeSource) {
/*  535 */                   ((PropertyDescriptor)localObject2).setBound(true);
/*      */                 }
/*  537 */                 addPropertyDescriptor((PropertyDescriptor)localObject2);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  541 */     processPropertyDescriptors();
/*      */ 
/*  544 */     Object localObject1 = new PropertyDescriptor[this.properties.size()];
/*  545 */     localObject1 = (PropertyDescriptor[])this.properties.values().toArray((Object[])localObject1);
/*      */ 
/*  548 */     if (this.defaultPropertyName != null) {
/*  549 */       for (j = 0; j < localObject1.length; j++) {
/*  550 */         if (this.defaultPropertyName.equals(localObject1[j].getName())) {
/*  551 */           this.defaultPropertyIndex = j;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  556 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private void addPropertyDescriptor(PropertyDescriptor paramPropertyDescriptor)
/*      */   {
/*  565 */     String str = paramPropertyDescriptor.getName();
/*  566 */     Object localObject = (List)this.pdStore.get(str);
/*  567 */     if (localObject == null) {
/*  568 */       localObject = new ArrayList();
/*  569 */       this.pdStore.put(str, localObject);
/*      */     }
/*  571 */     if (this.beanClass != paramPropertyDescriptor.getClass0())
/*      */     {
/*  575 */       Method localMethod1 = paramPropertyDescriptor.getReadMethod();
/*  576 */       Method localMethod2 = paramPropertyDescriptor.getWriteMethod();
/*  577 */       int i = 1;
/*  578 */       if (localMethod1 != null) i = (i != 0) && ((localMethod1.getGenericReturnType() instanceof Class)) ? 1 : 0;
/*  579 */       if (localMethod2 != null) i = (i != 0) && ((localMethod2.getGenericParameterTypes()[0] instanceof Class)) ? 1 : 0;
/*  580 */       if ((paramPropertyDescriptor instanceof IndexedPropertyDescriptor)) {
/*  581 */         IndexedPropertyDescriptor localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)paramPropertyDescriptor;
/*  582 */         Method localMethod3 = localIndexedPropertyDescriptor.getIndexedReadMethod();
/*  583 */         Method localMethod4 = localIndexedPropertyDescriptor.getIndexedWriteMethod();
/*  584 */         if (localMethod3 != null) i = (i != 0) && ((localMethod3.getGenericReturnType() instanceof Class)) ? 1 : 0;
/*  585 */         if (localMethod4 != null) i = (i != 0) && ((localMethod4.getGenericParameterTypes()[1] instanceof Class)) ? 1 : 0;
/*  586 */         if (i == 0) {
/*  587 */           paramPropertyDescriptor = new IndexedPropertyDescriptor((IndexedPropertyDescriptor)paramPropertyDescriptor);
/*  588 */           paramPropertyDescriptor.updateGenericsFor(this.beanClass);
/*      */         }
/*      */       }
/*  591 */       else if (i == 0) {
/*  592 */         paramPropertyDescriptor = new PropertyDescriptor(paramPropertyDescriptor);
/*  593 */         paramPropertyDescriptor.updateGenericsFor(this.beanClass);
/*      */       }
/*      */     }
/*  596 */     ((List)localObject).add(paramPropertyDescriptor);
/*      */   }
/*      */ 
/*      */   private void addPropertyDescriptors(PropertyDescriptor[] paramArrayOfPropertyDescriptor) {
/*  600 */     if (paramArrayOfPropertyDescriptor != null)
/*  601 */       for (PropertyDescriptor localPropertyDescriptor : paramArrayOfPropertyDescriptor)
/*  602 */         addPropertyDescriptor(localPropertyDescriptor);
/*      */   }
/*      */ 
/*      */   private PropertyDescriptor[] getPropertyDescriptors(BeanInfo paramBeanInfo)
/*      */   {
/*  608 */     PropertyDescriptor[] arrayOfPropertyDescriptor = paramBeanInfo.getPropertyDescriptors();
/*  609 */     int i = paramBeanInfo.getDefaultPropertyIndex();
/*  610 */     if ((0 <= i) && (i < arrayOfPropertyDescriptor.length)) {
/*  611 */       this.defaultPropertyName = arrayOfPropertyDescriptor[i].getName();
/*      */     }
/*  613 */     return arrayOfPropertyDescriptor;
/*      */   }
/*      */ 
/*      */   private void processPropertyDescriptors()
/*      */   {
/*  621 */     if (this.properties == null) {
/*  622 */       this.properties = new TreeMap();
/*      */     }
/*      */ 
/*  630 */     Iterator localIterator = this.pdStore.values().iterator();
/*  631 */     while (localIterator.hasNext()) {
/*  632 */       Object localObject1 = null; Object localObject2 = null; Object localObject3 = null;
/*  633 */       IndexedPropertyDescriptor localIndexedPropertyDescriptor = null; Object localObject4 = null; Object localObject5 = null;
/*      */ 
/*  635 */       List localList = (List)localIterator.next();
/*      */ 
/*  639 */       for (int i = 0; i < localList.size(); i++) {
/*  640 */         localObject1 = (PropertyDescriptor)localList.get(i);
/*  641 */         if ((localObject1 instanceof IndexedPropertyDescriptor)) {
/*  642 */           localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)localObject1;
/*  643 */           if (localIndexedPropertyDescriptor.getIndexedReadMethod() != null) {
/*  644 */             if (localObject4 != null)
/*  645 */               localObject4 = new IndexedPropertyDescriptor((PropertyDescriptor)localObject4, localIndexedPropertyDescriptor);
/*      */             else {
/*  647 */               localObject4 = localIndexedPropertyDescriptor;
/*      */             }
/*      */           }
/*      */         }
/*  651 */         else if (((PropertyDescriptor)localObject1).getReadMethod() != null) {
/*  652 */           if (localObject2 != null)
/*      */           {
/*  655 */             Method localMethod = ((PropertyDescriptor)localObject2).getReadMethod();
/*  656 */             if (!localMethod.getName().startsWith("is"))
/*  657 */               localObject2 = new PropertyDescriptor((PropertyDescriptor)localObject2, (PropertyDescriptor)localObject1);
/*      */           }
/*      */           else {
/*  660 */             localObject2 = localObject1;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  668 */       for (i = 0; i < localList.size(); i++) {
/*  669 */         localObject1 = (PropertyDescriptor)localList.get(i);
/*  670 */         if ((localObject1 instanceof IndexedPropertyDescriptor)) {
/*  671 */           localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)localObject1;
/*  672 */           if (localIndexedPropertyDescriptor.getIndexedWriteMethod() != null) {
/*  673 */             if (localObject4 != null) {
/*  674 */               if (((IndexedPropertyDescriptor)localObject4).getIndexedPropertyType() == localIndexedPropertyDescriptor.getIndexedPropertyType())
/*      */               {
/*  676 */                 if (localObject5 != null)
/*  677 */                   localObject5 = new IndexedPropertyDescriptor((PropertyDescriptor)localObject5, localIndexedPropertyDescriptor);
/*      */                 else {
/*  679 */                   localObject5 = localIndexedPropertyDescriptor;
/*      */                 }
/*      */               }
/*      */             }
/*  683 */             else if (localObject5 != null)
/*  684 */               localObject5 = new IndexedPropertyDescriptor((PropertyDescriptor)localObject5, localIndexedPropertyDescriptor);
/*      */             else {
/*  686 */               localObject5 = localIndexedPropertyDescriptor;
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*  691 */         else if (((PropertyDescriptor)localObject1).getWriteMethod() != null) {
/*  692 */           if (localObject2 != null) {
/*  693 */             if (((PropertyDescriptor)localObject2).getPropertyType() == ((PropertyDescriptor)localObject1).getPropertyType()) {
/*  694 */               if (localObject3 != null)
/*  695 */                 localObject3 = new PropertyDescriptor((PropertyDescriptor)localObject3, (PropertyDescriptor)localObject1);
/*      */               else {
/*  697 */                 localObject3 = localObject1;
/*      */               }
/*      */             }
/*      */           }
/*  701 */           else if (localObject3 != null)
/*  702 */             localObject3 = new PropertyDescriptor((PropertyDescriptor)localObject3, (PropertyDescriptor)localObject1);
/*      */           else {
/*  704 */             localObject3 = localObject1;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  715 */       localObject1 = null; localIndexedPropertyDescriptor = null;
/*      */ 
/*  717 */       if ((localObject4 != null) && (localObject5 != null))
/*      */       {
/*      */         PropertyDescriptor localPropertyDescriptor;
/*  720 */         if (localObject2 != null) {
/*  721 */           localPropertyDescriptor = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject4, (PropertyDescriptor)localObject2);
/*  722 */           if ((localPropertyDescriptor instanceof IndexedPropertyDescriptor)) {
/*  723 */             localObject4 = (IndexedPropertyDescriptor)localPropertyDescriptor;
/*      */           }
/*      */         }
/*  726 */         if (localObject3 != null) {
/*  727 */           localPropertyDescriptor = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject5, (PropertyDescriptor)localObject3);
/*  728 */           if ((localPropertyDescriptor instanceof IndexedPropertyDescriptor)) {
/*  729 */             localObject5 = (IndexedPropertyDescriptor)localPropertyDescriptor;
/*      */           }
/*      */         }
/*  732 */         if (localObject4 == localObject5)
/*  733 */           localObject1 = localObject4;
/*      */         else
/*  735 */           localObject1 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject4, (IndexedPropertyDescriptor)localObject5);
/*      */       }
/*  737 */       else if ((localObject2 != null) && (localObject3 != null))
/*      */       {
/*  739 */         if (localObject2 == localObject3)
/*  740 */           localObject1 = localObject2;
/*      */         else
/*  742 */           localObject1 = mergePropertyDescriptor((PropertyDescriptor)localObject2, (PropertyDescriptor)localObject3);
/*      */       }
/*  744 */       else if (localObject5 != null)
/*      */       {
/*  746 */         localObject1 = localObject5;
/*      */ 
/*  748 */         if (localObject3 != null) {
/*  749 */           localObject1 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject5, (PropertyDescriptor)localObject3);
/*      */         }
/*  751 */         if (localObject2 != null)
/*  752 */           localObject1 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject5, (PropertyDescriptor)localObject2);
/*      */       }
/*  754 */       else if (localObject4 != null)
/*      */       {
/*  756 */         localObject1 = localObject4;
/*      */ 
/*  758 */         if (localObject2 != null) {
/*  759 */           localObject1 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject4, (PropertyDescriptor)localObject2);
/*      */         }
/*  761 */         if (localObject3 != null)
/*  762 */           localObject1 = mergePropertyDescriptor((IndexedPropertyDescriptor)localObject4, (PropertyDescriptor)localObject3);
/*      */       }
/*  764 */       else if (localObject3 != null)
/*      */       {
/*  766 */         localObject1 = localObject3;
/*  767 */       } else if (localObject2 != null)
/*      */       {
/*  769 */         localObject1 = localObject2;
/*      */       }
/*      */ 
/*  776 */       if ((localObject1 instanceof IndexedPropertyDescriptor)) {
/*  777 */         localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)localObject1;
/*  778 */         if ((localIndexedPropertyDescriptor.getIndexedReadMethod() == null) && (localIndexedPropertyDescriptor.getIndexedWriteMethod() == null)) {
/*  779 */           localObject1 = new PropertyDescriptor(localIndexedPropertyDescriptor);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  786 */       if ((localObject1 == null) && (localList.size() > 0)) {
/*  787 */         localObject1 = (PropertyDescriptor)localList.get(0);
/*      */       }
/*      */ 
/*  790 */       if (localObject1 != null)
/*  791 */         this.properties.put(((PropertyDescriptor)localObject1).getName(), localObject1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private PropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor paramIndexedPropertyDescriptor, PropertyDescriptor paramPropertyDescriptor)
/*      */   {
/*  804 */     Object localObject = null;
/*      */ 
/*  806 */     Class localClass1 = paramPropertyDescriptor.getPropertyType();
/*  807 */     Class localClass2 = paramIndexedPropertyDescriptor.getIndexedPropertyType();
/*      */ 
/*  809 */     if ((localClass1.isArray()) && (localClass1.getComponentType() == localClass2)) {
/*  810 */       if (paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0()))
/*  811 */         localObject = new IndexedPropertyDescriptor(paramPropertyDescriptor, paramIndexedPropertyDescriptor);
/*      */       else {
/*  813 */         localObject = new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor, paramPropertyDescriptor);
/*      */       }
/*      */ 
/*      */     }
/*  818 */     else if (paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0())) {
/*  819 */       localObject = paramIndexedPropertyDescriptor;
/*      */     } else {
/*  821 */       localObject = paramPropertyDescriptor;
/*      */ 
/*  824 */       Method localMethod1 = ((PropertyDescriptor)localObject).getWriteMethod();
/*  825 */       Method localMethod2 = ((PropertyDescriptor)localObject).getReadMethod();
/*      */ 
/*  827 */       if ((localMethod2 == null) && (localMethod1 != null)) {
/*  828 */         localMethod2 = findMethod(((PropertyDescriptor)localObject).getClass0(), "get" + NameGenerator.capitalize(((PropertyDescriptor)localObject).getName()), 0);
/*      */ 
/*  830 */         if (localMethod2 != null)
/*      */           try {
/*  832 */             ((PropertyDescriptor)localObject).setReadMethod(localMethod2);
/*      */           }
/*      */           catch (IntrospectionException localIntrospectionException1)
/*      */           {
/*      */           }
/*      */       }
/*  838 */       if ((localMethod1 == null) && (localMethod2 != null)) {
/*  839 */         localMethod1 = findMethod(((PropertyDescriptor)localObject).getClass0(), "set" + NameGenerator.capitalize(((PropertyDescriptor)localObject).getName()), 1, new Class[] { FeatureDescriptor.getReturnType(((PropertyDescriptor)localObject).getClass0(), localMethod2) });
/*      */ 
/*  842 */         if (localMethod1 != null) {
/*      */           try {
/*  844 */             ((PropertyDescriptor)localObject).setWriteMethod(localMethod1);
/*      */           }
/*      */           catch (IntrospectionException localIntrospectionException2)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  852 */     return localObject;
/*      */   }
/*      */ 
/*      */   private PropertyDescriptor mergePropertyDescriptor(PropertyDescriptor paramPropertyDescriptor1, PropertyDescriptor paramPropertyDescriptor2)
/*      */   {
/*  858 */     if (paramPropertyDescriptor1.getClass0().isAssignableFrom(paramPropertyDescriptor2.getClass0())) {
/*  859 */       return new PropertyDescriptor(paramPropertyDescriptor1, paramPropertyDescriptor2);
/*      */     }
/*  861 */     return new PropertyDescriptor(paramPropertyDescriptor2, paramPropertyDescriptor1);
/*      */   }
/*      */ 
/*      */   private PropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor paramIndexedPropertyDescriptor1, IndexedPropertyDescriptor paramIndexedPropertyDescriptor2)
/*      */   {
/*  868 */     if (paramIndexedPropertyDescriptor1.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor2.getClass0())) {
/*  869 */       return new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor1, paramIndexedPropertyDescriptor2);
/*      */     }
/*  871 */     return new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor2, paramIndexedPropertyDescriptor1);
/*      */   }
/*      */ 
/*      */   private EventSetDescriptor[] getTargetEventInfo()
/*      */     throws IntrospectionException
/*      */   {
/*  880 */     if (this.events == null) {
/*  881 */       this.events = new HashMap();
/*      */     }
/*      */ 
/*  886 */     EventSetDescriptor[] arrayOfEventSetDescriptor1 = null;
/*  887 */     if (this.explicitBeanInfo != null) {
/*  888 */       arrayOfEventSetDescriptor1 = this.explicitBeanInfo.getEventSetDescriptors();
/*  889 */       int i = this.explicitBeanInfo.getDefaultEventIndex();
/*  890 */       if ((i >= 0) && (i < arrayOfEventSetDescriptor1.length)) {
/*  891 */         this.defaultEventName = arrayOfEventSetDescriptor1[i].getName();
/*      */       }
/*      */     }
/*      */ 
/*  895 */     if ((arrayOfEventSetDescriptor1 == null) && (this.superBeanInfo != null))
/*      */     {
/*  897 */       EventSetDescriptor[] arrayOfEventSetDescriptor2 = this.superBeanInfo.getEventSetDescriptors();
/*  898 */       for (int k = 0; k < arrayOfEventSetDescriptor2.length; k++) {
/*  899 */         addEvent(arrayOfEventSetDescriptor2[k]);
/*      */       }
/*  901 */       k = this.superBeanInfo.getDefaultEventIndex();
/*  902 */       if ((k >= 0) && (k < arrayOfEventSetDescriptor2.length))
/*  903 */         this.defaultEventName = arrayOfEventSetDescriptor2[k].getName();
/*      */     }
/*      */     Object localObject2;
/*  907 */     for (int j = 0; j < this.additionalBeanInfo.length; j++) {
/*  908 */       localObject2 = this.additionalBeanInfo[j].getEventSetDescriptors();
/*  909 */       if (localObject2 != null)
/*  910 */         for (int n = 0; n < localObject2.length; n++)
/*  911 */           addEvent(localObject2[n]);
/*      */     }
/*      */     Object localObject1;
/*  916 */     if (arrayOfEventSetDescriptor1 != null)
/*      */     {
/*  918 */       for (j = 0; j < arrayOfEventSetDescriptor1.length; j++) {
/*  919 */         addEvent(arrayOfEventSetDescriptor1[j]);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  927 */       localObject1 = getPublicDeclaredMethods(this.beanClass);
/*      */ 
/*  932 */       localObject2 = null;
/*  933 */       HashMap localHashMap1 = null;
/*  934 */       HashMap localHashMap2 = null;
/*      */       Object localObject3;
/*      */       Object localObject4;
/*      */       Object localObject5;
/*      */       Object localObject6;
/*      */       Object localObject7;
/*  936 */       for (int i1 = 0; i1 < localObject1.length; i1++) {
/*  937 */         localObject3 = localObject1[i1];
/*  938 */         if (localObject3 != null)
/*      */         {
/*  942 */           int i2 = ((Method)localObject3).getModifiers();
/*  943 */           if (!Modifier.isStatic(i2))
/*      */           {
/*  946 */             localObject4 = ((Method)localObject3).getName();
/*      */ 
/*  948 */             if ((((String)localObject4).startsWith("add")) || (((String)localObject4).startsWith("remove")) || (((String)localObject4).startsWith("get")))
/*      */             {
/*  953 */               localObject5 = FeatureDescriptor.getParameterTypes(this.beanClass, (Method)localObject3);
/*  954 */               localObject6 = FeatureDescriptor.getReturnType(this.beanClass, (Method)localObject3);
/*      */ 
/*  956 */               if ((((String)localObject4).startsWith("add")) && (localObject5.length == 1) && (localObject6 == Void.TYPE) && (isSubclass(localObject5[0], eventListenerType)))
/*      */               {
/*  959 */                 localObject7 = ((String)localObject4).substring(3);
/*  960 */                 if ((((String)localObject7).length() > 0) && (localObject5[0].getName().endsWith((String)localObject7)))
/*      */                 {
/*  962 */                   if (localObject2 == null) {
/*  963 */                     localObject2 = new HashMap();
/*      */                   }
/*  965 */                   ((Map)localObject2).put(localObject7, localObject3);
/*      */                 }
/*      */               }
/*  968 */               else if ((((String)localObject4).startsWith("remove")) && (localObject5.length == 1) && (localObject6 == Void.TYPE) && (isSubclass(localObject5[0], eventListenerType)))
/*      */               {
/*  971 */                 localObject7 = ((String)localObject4).substring(6);
/*  972 */                 if ((((String)localObject7).length() > 0) && (localObject5[0].getName().endsWith((String)localObject7)))
/*      */                 {
/*  974 */                   if (localHashMap1 == null) {
/*  975 */                     localHashMap1 = new HashMap();
/*      */                   }
/*  977 */                   localHashMap1.put(localObject7, localObject3);
/*      */                 }
/*      */               }
/*  980 */               else if ((((String)localObject4).startsWith("get")) && (localObject5.length == 0) && (((Class)localObject6).isArray()) && (isSubclass(((Class)localObject6).getComponentType(), eventListenerType)))
/*      */               {
/*  984 */                 localObject7 = ((String)localObject4).substring(3, ((String)localObject4).length() - 1);
/*  985 */                 if ((((String)localObject7).length() > 0) && (((Class)localObject6).getComponentType().getName().endsWith((String)localObject7)))
/*      */                 {
/*  987 */                   if (localHashMap2 == null) {
/*  988 */                     localHashMap2 = new HashMap();
/*      */                   }
/*  990 */                   localHashMap2.put(localObject7, localObject3);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  995 */       if ((localObject2 != null) && (localHashMap1 != null))
/*      */       {
/*  998 */         Iterator localIterator = ((Map)localObject2).keySet().iterator();
/*  999 */         while (localIterator.hasNext()) {
/* 1000 */           localObject3 = (String)localIterator.next();
/*      */ 
/* 1003 */           if ((localHashMap1.get(localObject3) != null) && (((String)localObject3).endsWith("Listener")))
/*      */           {
/* 1006 */             String str = decapitalize(((String)localObject3).substring(0, ((String)localObject3).length() - 8));
/* 1007 */             localObject4 = (Method)((Map)localObject2).get(localObject3);
/* 1008 */             localObject5 = (Method)localHashMap1.get(localObject3);
/* 1009 */             localObject6 = null;
/* 1010 */             if (localHashMap2 != null) {
/* 1011 */               localObject6 = (Method)localHashMap2.get(localObject3);
/*      */             }
/* 1013 */             localObject7 = FeatureDescriptor.getParameterTypes(this.beanClass, localObject4)[0];
/*      */ 
/* 1016 */             Method[] arrayOfMethod1 = getPublicDeclaredMethods((Class)localObject7);
/* 1017 */             ArrayList localArrayList = new ArrayList(arrayOfMethod1.length);
/* 1018 */             for (int i3 = 0; i3 < arrayOfMethod1.length; i3++) {
/* 1019 */               if (arrayOfMethod1[i3] != null)
/*      */               {
/* 1023 */                 if (isEventHandler(arrayOfMethod1[i3]))
/* 1024 */                   localArrayList.add(arrayOfMethod1[i3]);
/*      */               }
/*      */             }
/* 1027 */             Method[] arrayOfMethod2 = (Method[])localArrayList.toArray(new Method[localArrayList.size()]);
/*      */ 
/* 1029 */             EventSetDescriptor localEventSetDescriptor = new EventSetDescriptor(str, (Class)localObject7, arrayOfMethod2, (Method)localObject4, (Method)localObject5, (Method)localObject6);
/*      */ 
/* 1036 */             if (throwsException((Method)localObject4, TooManyListenersException.class))
/*      */             {
/* 1038 */               localEventSetDescriptor.setUnicast(true);
/*      */             }
/* 1040 */             addEvent(localEventSetDescriptor);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1045 */     if (this.events.size() == 0) {
/* 1046 */       localObject1 = EMPTY_EVENTSETDESCRIPTORS;
/*      */     }
/*      */     else {
/* 1049 */       localObject1 = new EventSetDescriptor[this.events.size()];
/* 1050 */       localObject1 = (EventSetDescriptor[])this.events.values().toArray((Object[])localObject1);
/*      */ 
/* 1053 */       if (this.defaultEventName != null) {
/* 1054 */         for (int m = 0; m < localObject1.length; m++) {
/* 1055 */           if (this.defaultEventName.equals(localObject1[m].getName())) {
/* 1056 */             this.defaultEventIndex = m;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1061 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private void addEvent(EventSetDescriptor paramEventSetDescriptor) {
/* 1065 */     String str = paramEventSetDescriptor.getName();
/* 1066 */     if (paramEventSetDescriptor.getName().equals("propertyChange")) {
/* 1067 */       this.propertyChangeSource = true;
/*      */     }
/* 1069 */     EventSetDescriptor localEventSetDescriptor1 = (EventSetDescriptor)this.events.get(str);
/* 1070 */     if (localEventSetDescriptor1 == null) {
/* 1071 */       this.events.put(str, paramEventSetDescriptor);
/* 1072 */       return;
/*      */     }
/* 1074 */     EventSetDescriptor localEventSetDescriptor2 = new EventSetDescriptor(localEventSetDescriptor1, paramEventSetDescriptor);
/* 1075 */     this.events.put(str, localEventSetDescriptor2);
/*      */   }
/*      */ 
/*      */   private MethodDescriptor[] getTargetMethodInfo()
/*      */   {
/* 1083 */     if (this.methods == null) {
/* 1084 */       this.methods = new HashMap(100);
/*      */     }
/*      */ 
/* 1089 */     MethodDescriptor[] arrayOfMethodDescriptor1 = null;
/* 1090 */     if (this.explicitBeanInfo != null) {
/* 1091 */       arrayOfMethodDescriptor1 = this.explicitBeanInfo.getMethodDescriptors();
/*      */     }
/*      */ 
/* 1094 */     if ((arrayOfMethodDescriptor1 == null) && (this.superBeanInfo != null))
/*      */     {
/* 1096 */       MethodDescriptor[] arrayOfMethodDescriptor2 = this.superBeanInfo.getMethodDescriptors();
/* 1097 */       for (int j = 0; j < arrayOfMethodDescriptor2.length; j++) {
/* 1098 */         addMethod(arrayOfMethodDescriptor2[j]);
/*      */       }
/*      */     }
/*      */ 
/* 1102 */     for (int i = 0; i < this.additionalBeanInfo.length; i++) {
/* 1103 */       MethodDescriptor[] arrayOfMethodDescriptor3 = this.additionalBeanInfo[i].getMethodDescriptors();
/* 1104 */       if (arrayOfMethodDescriptor3 != null) {
/* 1105 */         for (int m = 0; m < arrayOfMethodDescriptor3.length; m++) {
/* 1106 */           addMethod(arrayOfMethodDescriptor3[m]);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1111 */     if (arrayOfMethodDescriptor1 != null)
/*      */     {
/* 1113 */       for (i = 0; i < arrayOfMethodDescriptor1.length; i++) {
/* 1114 */         addMethod(arrayOfMethodDescriptor1[i]);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1122 */       localObject = getPublicDeclaredMethods(this.beanClass);
/*      */ 
/* 1125 */       for (int k = 0; k < localObject.length; k++) {
/* 1126 */         Method localMethod = localObject[k];
/* 1127 */         if (localMethod != null)
/*      */         {
/* 1130 */           MethodDescriptor localMethodDescriptor = new MethodDescriptor(localMethod);
/* 1131 */           addMethod(localMethodDescriptor);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1136 */     Object localObject = new MethodDescriptor[this.methods.size()];
/* 1137 */     localObject = (MethodDescriptor[])this.methods.values().toArray((Object[])localObject);
/*      */ 
/* 1139 */     return localObject;
/*      */   }
/*      */ 
/*      */   private void addMethod(MethodDescriptor paramMethodDescriptor)
/*      */   {
/* 1146 */     String str = paramMethodDescriptor.getName();
/*      */ 
/* 1148 */     MethodDescriptor localMethodDescriptor1 = (MethodDescriptor)this.methods.get(str);
/* 1149 */     if (localMethodDescriptor1 == null)
/*      */     {
/* 1151 */       this.methods.put(str, paramMethodDescriptor);
/* 1152 */       return;
/*      */     }
/*      */ 
/* 1158 */     String[] arrayOfString1 = paramMethodDescriptor.getParamNames();
/* 1159 */     String[] arrayOfString2 = localMethodDescriptor1.getParamNames();
/*      */ 
/* 1161 */     int i = 0;
/* 1162 */     if (arrayOfString1.length == arrayOfString2.length) {
/* 1163 */       i = 1;
/* 1164 */       for (int j = 0; j < arrayOfString1.length; j++) {
/* 1165 */         if (arrayOfString1[j] != arrayOfString2[j]) {
/* 1166 */           i = 0;
/* 1167 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1171 */     if (i != 0) {
/* 1172 */       localObject = new MethodDescriptor(localMethodDescriptor1, paramMethodDescriptor);
/* 1173 */       this.methods.put(str, localObject);
/* 1174 */       return;
/*      */     }
/*      */ 
/* 1180 */     Object localObject = makeQualifiedMethodName(str, arrayOfString1);
/* 1181 */     localMethodDescriptor1 = (MethodDescriptor)this.methods.get(localObject);
/* 1182 */     if (localMethodDescriptor1 == null) {
/* 1183 */       this.methods.put(localObject, paramMethodDescriptor);
/* 1184 */       return;
/*      */     }
/* 1186 */     MethodDescriptor localMethodDescriptor2 = new MethodDescriptor(localMethodDescriptor1, paramMethodDescriptor);
/* 1187 */     this.methods.put(localObject, localMethodDescriptor2);
/*      */   }
/*      */ 
/*      */   private static String makeQualifiedMethodName(String paramString, String[] paramArrayOfString)
/*      */   {
/* 1194 */     StringBuffer localStringBuffer = new StringBuffer(paramString);
/* 1195 */     localStringBuffer.append('=');
/* 1196 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 1197 */       localStringBuffer.append(':');
/* 1198 */       localStringBuffer.append(paramArrayOfString[i]);
/*      */     }
/* 1200 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   private int getTargetDefaultEventIndex() {
/* 1204 */     return this.defaultEventIndex;
/*      */   }
/*      */ 
/*      */   private int getTargetDefaultPropertyIndex() {
/* 1208 */     return this.defaultPropertyIndex;
/*      */   }
/*      */ 
/*      */   private BeanDescriptor getTargetBeanDescriptor()
/*      */   {
/* 1213 */     if (this.explicitBeanInfo != null) {
/* 1214 */       BeanDescriptor localBeanDescriptor = this.explicitBeanInfo.getBeanDescriptor();
/* 1215 */       if (localBeanDescriptor != null) {
/* 1216 */         return localBeanDescriptor;
/*      */       }
/*      */     }
/*      */ 
/* 1220 */     return new BeanDescriptor(this.beanClass, findCustomizerClass(this.beanClass));
/*      */   }
/*      */ 
/*      */   private static Class<?> findCustomizerClass(Class<?> paramClass) {
/* 1224 */     String str = paramClass.getName() + "Customizer";
/*      */     try {
/* 1226 */       paramClass = ClassFinder.findClass(str, paramClass.getClassLoader());
/*      */ 
/* 1229 */       if ((Component.class.isAssignableFrom(paramClass)) && (Customizer.class.isAssignableFrom(paramClass))) {
/* 1230 */         return paramClass;
/*      */       }
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */     }
/* 1236 */     return null;
/*      */   }
/*      */ 
/*      */   private boolean isEventHandler(Method paramMethod)
/*      */   {
/* 1242 */     Class[] arrayOfClass = FeatureDescriptor.getParameterTypes(this.beanClass, paramMethod);
/* 1243 */     if (arrayOfClass.length != 1) {
/* 1244 */       return false;
/*      */     }
/* 1246 */     return isSubclass(arrayOfClass[0], EventObject.class);
/*      */   }
/*      */ 
/*      */   private static Method[] getPublicDeclaredMethods(Class paramClass)
/*      */   {
/* 1255 */     if (!ReflectUtil.isPackageAccessible(paramClass)) {
/* 1256 */       return new Method[0];
/*      */     }
/* 1258 */     synchronized (declaredMethodCache) {
/* 1259 */       Method[] arrayOfMethod = (Method[])declaredMethodCache.get(paramClass);
/* 1260 */       if (arrayOfMethod == null) {
/* 1261 */         arrayOfMethod = paramClass.getMethods();
/* 1262 */         for (int i = 0; i < arrayOfMethod.length; i++) {
/* 1263 */           Method localMethod = arrayOfMethod[i];
/* 1264 */           if (!localMethod.getDeclaringClass().equals(paramClass)) {
/* 1265 */             arrayOfMethod[i] = null;
/*      */           }
/*      */         }
/* 1268 */         declaredMethodCache.put(paramClass, arrayOfMethod);
/*      */       }
/* 1270 */       return arrayOfMethod;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Method internalFindMethod(Class paramClass, String paramString, int paramInt, Class[] paramArrayOfClass)
/*      */   {
/* 1287 */     Method localMethod = null;
/*      */ 
/* 1289 */     for (Object localObject = paramClass; localObject != null; localObject = ((Class)localObject).getSuperclass()) {
/* 1290 */       Method[] arrayOfMethod = getPublicDeclaredMethods((Class)localObject);
/* 1291 */       for (int j = 0; j < arrayOfMethod.length; j++) {
/* 1292 */         localMethod = arrayOfMethod[j];
/* 1293 */         if (localMethod != null)
/*      */         {
/* 1298 */           Class[] arrayOfClass = FeatureDescriptor.getParameterTypes(paramClass, localMethod);
/* 1299 */           if ((localMethod.getName().equals(paramString)) && (arrayOfClass.length == paramInt))
/*      */           {
/* 1301 */             if (paramArrayOfClass != null) {
/* 1302 */               int k = 0;
/* 1303 */               if (paramInt > 0) {
/* 1304 */                 for (int m = 0; m < paramInt; m++) {
/* 1305 */                   if (arrayOfClass[m] != paramArrayOfClass[m]) {
/* 1306 */                     k = 1;
/*      */                   }
/*      */                 }
/*      */ 
/* 1310 */                 if (k != 0) {
/*      */                   continue;
/*      */                 }
/*      */               }
/*      */             }
/* 1315 */             return localMethod;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1319 */     localMethod = null;
/*      */ 
/* 1324 */     localObject = paramClass.getInterfaces();
/* 1325 */     for (int i = 0; i < localObject.length; i++)
/*      */     {
/* 1329 */       localMethod = internalFindMethod(localObject[i], paramString, paramInt, null);
/* 1330 */       if (localMethod != null) {
/*      */         break;
/*      */       }
/*      */     }
/* 1334 */     return localMethod;
/*      */   }
/*      */ 
/*      */   static Method findMethod(Class paramClass, String paramString, int paramInt)
/*      */   {
/* 1341 */     return findMethod(paramClass, paramString, paramInt, null);
/*      */   }
/*      */ 
/*      */   static Method findMethod(Class paramClass, String paramString, int paramInt, Class[] paramArrayOfClass)
/*      */   {
/* 1358 */     if (paramString == null) {
/* 1359 */       return null;
/*      */     }
/* 1361 */     return internalFindMethod(paramClass, paramString, paramInt, paramArrayOfClass);
/*      */   }
/*      */ 
/*      */   static boolean isSubclass(Class paramClass1, Class paramClass2)
/*      */   {
/* 1374 */     if (paramClass1 == paramClass2) {
/* 1375 */       return true;
/*      */     }
/* 1377 */     if ((paramClass1 == null) || (paramClass2 == null)) {
/* 1378 */       return false;
/*      */     }
/* 1380 */     for (Class localClass = paramClass1; localClass != null; localClass = localClass.getSuperclass()) {
/* 1381 */       if (localClass == paramClass2) {
/* 1382 */         return true;
/*      */       }
/* 1384 */       if (paramClass2.isInterface()) {
/* 1385 */         Class[] arrayOfClass = localClass.getInterfaces();
/* 1386 */         for (int i = 0; i < arrayOfClass.length; i++) {
/* 1387 */           if (isSubclass(arrayOfClass[i], paramClass2)) {
/* 1388 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1393 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean throwsException(Method paramMethod, Class paramClass)
/*      */   {
/* 1400 */     Class[] arrayOfClass = paramMethod.getExceptionTypes();
/* 1401 */     for (int i = 0; i < arrayOfClass.length; i++) {
/* 1402 */       if (arrayOfClass[i] == paramClass) {
/* 1403 */         return true;
/*      */       }
/*      */     }
/* 1406 */     return false;
/*      */   }
/*      */ 
/*      */   static Object instantiate(Class paramClass, String paramString)
/*      */     throws InstantiationException, IllegalAccessException, ClassNotFoundException
/*      */   {
/* 1418 */     ClassLoader localClassLoader = paramClass.getClassLoader();
/* 1419 */     Class localClass = ClassFinder.findClass(paramString, localClassLoader);
/* 1420 */     return localClass.newInstance();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.Introspector
 * JD-Core Version:    0.6.2
 */