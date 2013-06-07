/*      */ package java.beans;
/*      */ 
/*      */ import com.sun.beans.finder.BeanInfoFinder;
/*      */ import java.awt.Image;
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.SoftReference;
/*      */ 
/*      */ class GenericBeanInfo extends SimpleBeanInfo
/*      */ {
/*      */   private BeanDescriptor beanDescriptor;
/*      */   private EventSetDescriptor[] events;
/*      */   private int defaultEvent;
/*      */   private PropertyDescriptor[] properties;
/*      */   private int defaultProperty;
/*      */   private MethodDescriptor[] methods;
/*      */   private Reference<BeanInfo> targetBeanInfoRef;
/*      */ 
/*      */   public GenericBeanInfo(BeanDescriptor paramBeanDescriptor, EventSetDescriptor[] paramArrayOfEventSetDescriptor, int paramInt1, PropertyDescriptor[] paramArrayOfPropertyDescriptor, int paramInt2, MethodDescriptor[] paramArrayOfMethodDescriptor, BeanInfo paramBeanInfo)
/*      */   {
/* 1448 */     this.beanDescriptor = paramBeanDescriptor;
/* 1449 */     this.events = paramArrayOfEventSetDescriptor;
/* 1450 */     this.defaultEvent = paramInt1;
/* 1451 */     this.properties = paramArrayOfPropertyDescriptor;
/* 1452 */     this.defaultProperty = paramInt2;
/* 1453 */     this.methods = paramArrayOfMethodDescriptor;
/* 1454 */     this.targetBeanInfoRef = (paramBeanInfo != null ? new SoftReference(paramBeanInfo) : null);
/*      */   }
/*      */ 
/*      */   GenericBeanInfo(GenericBeanInfo paramGenericBeanInfo)
/*      */   {
/* 1465 */     this.beanDescriptor = new BeanDescriptor(paramGenericBeanInfo.beanDescriptor);
/*      */     int i;
/*      */     int j;
/* 1466 */     if (paramGenericBeanInfo.events != null) {
/* 1467 */       i = paramGenericBeanInfo.events.length;
/* 1468 */       this.events = new EventSetDescriptor[i];
/* 1469 */       for (j = 0; j < i; j++) {
/* 1470 */         this.events[j] = new EventSetDescriptor(paramGenericBeanInfo.events[j]);
/*      */       }
/*      */     }
/* 1473 */     this.defaultEvent = paramGenericBeanInfo.defaultEvent;
/* 1474 */     if (paramGenericBeanInfo.properties != null) {
/* 1475 */       i = paramGenericBeanInfo.properties.length;
/* 1476 */       this.properties = new PropertyDescriptor[i];
/* 1477 */       for (j = 0; j < i; j++) {
/* 1478 */         PropertyDescriptor localPropertyDescriptor = paramGenericBeanInfo.properties[j];
/* 1479 */         if ((localPropertyDescriptor instanceof IndexedPropertyDescriptor)) {
/* 1480 */           this.properties[j] = new IndexedPropertyDescriptor((IndexedPropertyDescriptor)localPropertyDescriptor);
/*      */         }
/*      */         else {
/* 1483 */           this.properties[j] = new PropertyDescriptor(localPropertyDescriptor);
/*      */         }
/*      */       }
/*      */     }
/* 1487 */     this.defaultProperty = paramGenericBeanInfo.defaultProperty;
/* 1488 */     if (paramGenericBeanInfo.methods != null) {
/* 1489 */       i = paramGenericBeanInfo.methods.length;
/* 1490 */       this.methods = new MethodDescriptor[i];
/* 1491 */       for (j = 0; j < i; j++) {
/* 1492 */         this.methods[j] = new MethodDescriptor(paramGenericBeanInfo.methods[j]);
/*      */       }
/*      */     }
/* 1495 */     this.targetBeanInfoRef = paramGenericBeanInfo.targetBeanInfoRef;
/*      */   }
/*      */ 
/*      */   public PropertyDescriptor[] getPropertyDescriptors() {
/* 1499 */     return this.properties;
/*      */   }
/*      */ 
/*      */   public int getDefaultPropertyIndex() {
/* 1503 */     return this.defaultProperty;
/*      */   }
/*      */ 
/*      */   public EventSetDescriptor[] getEventSetDescriptors() {
/* 1507 */     return this.events;
/*      */   }
/*      */ 
/*      */   public int getDefaultEventIndex() {
/* 1511 */     return this.defaultEvent;
/*      */   }
/*      */ 
/*      */   public MethodDescriptor[] getMethodDescriptors() {
/* 1515 */     return this.methods;
/*      */   }
/*      */ 
/*      */   public BeanDescriptor getBeanDescriptor() {
/* 1519 */     return this.beanDescriptor;
/*      */   }
/*      */ 
/*      */   public Image getIcon(int paramInt) {
/* 1523 */     BeanInfo localBeanInfo = getTargetBeanInfo();
/* 1524 */     if (localBeanInfo != null) {
/* 1525 */       return localBeanInfo.getIcon(paramInt);
/*      */     }
/* 1527 */     return super.getIcon(paramInt);
/*      */   }
/*      */ 
/*      */   private BeanInfo getTargetBeanInfo() {
/* 1531 */     if (this.targetBeanInfoRef == null) {
/* 1532 */       return null;
/*      */     }
/* 1534 */     BeanInfo localBeanInfo = (BeanInfo)this.targetBeanInfoRef.get();
/* 1535 */     if (localBeanInfo == null) {
/* 1536 */       localBeanInfo = (BeanInfo)ThreadGroupContext.getContext().getBeanInfoFinder().find(this.beanDescriptor.getBeanClass());
/*      */ 
/* 1538 */       if (localBeanInfo != null) {
/* 1539 */         this.targetBeanInfoRef = new SoftReference(localBeanInfo);
/*      */       }
/*      */     }
/* 1542 */     return localBeanInfo;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.GenericBeanInfo
 * JD-Core Version:    0.6.2
 */