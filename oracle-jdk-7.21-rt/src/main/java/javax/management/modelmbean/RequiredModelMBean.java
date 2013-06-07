/*      */ package javax.management.modelmbean;
/*      */ 
/*      */ import com.sun.jmx.defaults.JmxProperties;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.security.AccessControlContext;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Vector;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.management.Attribute;
/*      */ import javax.management.AttributeChangeNotification;
/*      */ import javax.management.AttributeChangeNotificationFilter;
/*      */ import javax.management.AttributeList;
/*      */ import javax.management.AttributeNotFoundException;
/*      */ import javax.management.Descriptor;
/*      */ import javax.management.InstanceNotFoundException;
/*      */ import javax.management.InvalidAttributeValueException;
/*      */ import javax.management.ListenerNotFoundException;
/*      */ import javax.management.MBeanAttributeInfo;
/*      */ import javax.management.MBeanConstructorInfo;
/*      */ import javax.management.MBeanException;
/*      */ import javax.management.MBeanInfo;
/*      */ import javax.management.MBeanNotificationInfo;
/*      */ import javax.management.MBeanOperationInfo;
/*      */ import javax.management.MBeanRegistration;
/*      */ import javax.management.MBeanServer;
/*      */ import javax.management.MBeanServerFactory;
/*      */ import javax.management.Notification;
/*      */ import javax.management.NotificationBroadcasterSupport;
/*      */ import javax.management.NotificationEmitter;
/*      */ import javax.management.NotificationFilter;
/*      */ import javax.management.NotificationListener;
/*      */ import javax.management.ObjectName;
/*      */ import javax.management.ReflectionException;
/*      */ import javax.management.RuntimeErrorException;
/*      */ import javax.management.RuntimeOperationsException;
/*      */ import javax.management.ServiceNotFoundException;
/*      */ import javax.management.loading.ClassLoaderRepository;
/*      */ import sun.misc.JavaSecurityAccess;
/*      */ import sun.misc.SharedSecrets;
/*      */ import sun.reflect.misc.MethodUtil;
/*      */ import sun.reflect.misc.ReflectUtil;
/*      */ 
/*      */ public class RequiredModelMBean
/*      */   implements ModelMBean, MBeanRegistration, NotificationEmitter
/*      */ {
/*      */   ModelMBeanInfo modelMBeanInfo;
/*  131 */   private NotificationBroadcasterSupport generalBroadcaster = null;
/*      */ 
/*  134 */   private NotificationBroadcasterSupport attributeBroadcaster = null;
/*      */ 
/*  138 */   private Object managedResource = null;
/*      */ 
/*  142 */   private boolean registered = false;
/*  143 */   private transient MBeanServer server = null;
/*      */ 
/*  145 */   private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
/*  146 */   private final AccessControlContext acc = AccessController.getContext();
/*      */ 
/* 1163 */   private static final Class<?>[] primitiveClasses = { Integer.TYPE, Long.TYPE, Boolean.TYPE, Double.TYPE, Float.TYPE, Short.TYPE, Byte.TYPE, Character.TYPE };
/*      */ 
/* 1167 */   private static final Map<String, Class<?>> primitiveClassMap = new HashMap();
/*      */   private static Set<String> rmmbMethodNames;
/* 2977 */   private static final String[] primitiveTypes = { Boolean.TYPE.getName(), Byte.TYPE.getName(), Character.TYPE.getName(), Short.TYPE.getName(), Integer.TYPE.getName(), Long.TYPE.getName(), Float.TYPE.getName(), Double.TYPE.getName(), Void.TYPE.getName() };
/*      */ 
/* 2988 */   private static final String[] primitiveWrappers = { Boolean.class.getName(), Byte.class.getName(), Character.class.getName(), Short.class.getName(), Integer.class.getName(), Long.class.getName(), Float.class.getName(), Double.class.getName(), Void.class.getName() };
/*      */ 
/*      */   public RequiredModelMBean()
/*      */     throws MBeanException, RuntimeOperationsException
/*      */   {
/*  169 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  170 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Entry");
/*      */     }
/*      */ 
/*  174 */     this.modelMBeanInfo = createDefaultModelMBeanInfo();
/*  175 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
/*  176 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean()", "Exit");
/*      */   }
/*      */ 
/*      */   public RequiredModelMBean(ModelMBeanInfo paramModelMBeanInfo)
/*      */     throws MBeanException, RuntimeOperationsException
/*      */   {
/*  205 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  206 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Entry");
/*      */     }
/*      */ 
/*  210 */     setModelMBeanInfo(paramModelMBeanInfo);
/*      */ 
/*  212 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
/*  213 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "RequiredModelMBean(MBeanInfo)", "Exit");
/*      */   }
/*      */ 
/*      */   public void setModelMBeanInfo(ModelMBeanInfo paramModelMBeanInfo)
/*      */     throws MBeanException, RuntimeOperationsException
/*      */   {
/*  260 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  261 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Entry");
/*      */     }
/*      */ 
/*  266 */     if (paramModelMBeanInfo == null) {
/*  267 */       if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  268 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo is null: Raising exception.");
/*      */       }
/*      */ 
/*  273 */       IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("ModelMBeanInfo must not be null");
/*      */ 
/*  278 */       throw new RuntimeOperationsException(localIllegalArgumentException, "Exception occurred trying to initialize the ModelMBeanInfo of the RequiredModelMBean");
/*      */     }
/*      */ 
/*  281 */     if (this.registered) {
/*  282 */       if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  283 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "RequiredMBean is registered: Raising exception.");
/*      */       }
/*      */ 
/*  291 */       IllegalStateException localIllegalStateException = new IllegalStateException("cannot call setModelMBeanInfo while ModelMBean is registered");
/*      */ 
/*  293 */       throw new RuntimeOperationsException(localIllegalStateException, "Exception occurred trying to set the ModelMBeanInfo of the RequiredModelMBean");
/*      */     }
/*      */ 
/*  296 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  297 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Setting ModelMBeanInfo to " + printModelMBeanInfo(paramModelMBeanInfo));
/*      */ 
/*  301 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo notifications has " + paramModelMBeanInfo.getNotifications().length + " elements");
/*      */     }
/*      */ 
/*  308 */     this.modelMBeanInfo = ((ModelMBeanInfo)paramModelMBeanInfo.clone());
/*      */ 
/*  310 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  311 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "set mbeanInfo to: " + printModelMBeanInfo(this.modelMBeanInfo));
/*      */ 
/*  315 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setModelMBeanInfo(ModelMBeanInfo)", "Exit");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setManagedResource(Object paramObject, String paramString)
/*      */     throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException
/*      */   {
/*  345 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  346 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Entry");
/*      */     }
/*      */ 
/*  353 */     if ((paramString == null) || (!paramString.equalsIgnoreCase("objectReference")))
/*      */     {
/*  355 */       if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  356 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resouce Type is not supported: " + paramString);
/*      */       }
/*      */ 
/*  361 */       throw new InvalidTargetObjectTypeException(paramString);
/*      */     }
/*      */ 
/*  364 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  365 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object,String)", "Managed Resouce is valid");
/*      */     }
/*      */ 
/*  370 */     this.managedResource = paramObject;
/*      */ 
/*  372 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
/*  373 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setManagedResource(Object, String)", "Exit");
/*      */   }
/*      */ 
/*      */   public void load()
/*      */     throws MBeanException, RuntimeOperationsException, InstanceNotFoundException
/*      */   {
/*  402 */     ServiceNotFoundException localServiceNotFoundException = new ServiceNotFoundException("Persistence not supported for this MBean");
/*      */ 
/*  404 */     throw new MBeanException(localServiceNotFoundException, localServiceNotFoundException.getMessage());
/*      */   }
/*      */ 
/*      */   public void store()
/*      */     throws MBeanException, RuntimeOperationsException, InstanceNotFoundException
/*      */   {
/*  444 */     ServiceNotFoundException localServiceNotFoundException = new ServiceNotFoundException("Persistence not supported for this MBean");
/*      */ 
/*  446 */     throw new MBeanException(localServiceNotFoundException, localServiceNotFoundException.getMessage());
/*      */   }
/*      */ 
/*      */   private Object resolveForCacheValue(Descriptor paramDescriptor)
/*      */     throws MBeanException, RuntimeOperationsException
/*      */   {
/*  478 */     boolean bool1 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
/*      */ 
/*  480 */     if (bool1) {
/*  481 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Entry");
/*      */     }
/*      */ 
/*  485 */     Object localObject1 = null;
/*  486 */     boolean bool2 = false; boolean bool3 = true;
/*  487 */     long l1 = 0L;
/*      */ 
/*  489 */     if (paramDescriptor == null) {
/*  490 */       if (bool1) {
/*  491 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Input Descriptor is null");
/*      */       }
/*      */ 
/*  495 */       return localObject1;
/*      */     }
/*      */ 
/*  498 */     if (bool1) {
/*  499 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "descriptor is " + paramDescriptor);
/*      */     }
/*      */ 
/*  504 */     Descriptor localDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
/*  505 */     if ((localDescriptor == null) && 
/*  506 */       (bool1)) {
/*  507 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "MBean Descriptor is null");
/*      */     }
/*      */ 
/*  514 */     Object localObject2 = paramDescriptor.getFieldValue("currencyTimeLimit");
/*      */     String str1;
/*  517 */     if (localObject2 != null)
/*  518 */       str1 = localObject2.toString();
/*      */     else {
/*  520 */       str1 = null;
/*      */     }
/*      */ 
/*  523 */     if ((str1 == null) && (localDescriptor != null)) {
/*  524 */       localObject2 = localDescriptor.getFieldValue("currencyTimeLimit");
/*  525 */       if (localObject2 != null)
/*  526 */         str1 = localObject2.toString();
/*      */       else {
/*  528 */         str1 = null;
/*      */       }
/*      */     }
/*      */ 
/*  532 */     if (str1 != null) {
/*  533 */       if (bool1) {
/*  534 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyTimeLimit: " + str1);
/*      */       }
/*      */ 
/*  540 */       l1 = new Long(str1).longValue() * 1000L;
/*      */       Object localObject3;
/*  541 */       if (l1 < 0L)
/*      */       {
/*  543 */         bool3 = false;
/*  544 */         bool2 = true;
/*  545 */         if (bool1) {
/*  546 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", l1 + ": never Cached");
/*      */         }
/*      */ 
/*      */       }
/*  550 */       else if (l1 == 0L)
/*      */       {
/*  552 */         bool3 = true;
/*  553 */         bool2 = false;
/*  554 */         if (bool1) {
/*  555 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "always valid Cache");
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  560 */         localObject3 = paramDescriptor.getFieldValue("lastUpdatedTimeStamp");
/*      */         String str2;
/*  564 */         if (localObject3 != null) str2 = localObject3.toString(); else {
/*  565 */           str2 = null;
/*      */         }
/*  567 */         if (bool1) {
/*  568 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "lastUpdatedTimeStamp: " + str2);
/*      */         }
/*      */ 
/*  573 */         if (str2 == null) {
/*  574 */           str2 = "0";
/*      */         }
/*  576 */         long l2 = new Long(str2).longValue();
/*      */ 
/*  578 */         if (bool1) {
/*  579 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "currencyPeriod:" + l1 + " lastUpdatedTimeStamp:" + l2);
/*      */         }
/*      */ 
/*  585 */         long l3 = new Date().getTime();
/*      */ 
/*  587 */         if (l3 < l2 + l1) {
/*  588 */           bool3 = true;
/*  589 */           bool2 = false;
/*  590 */           if (bool1) {
/*  591 */             JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", " timed valid Cache for " + l3 + " < " + (l2 + l1));
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  597 */           bool3 = false;
/*  598 */           bool2 = true;
/*  599 */           if (bool1) {
/*  600 */             JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "timed expired cache for " + l3 + " > " + (l2 + l1));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  607 */       if (bool1) {
/*  608 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "returnCachedValue:" + bool3 + " resetValue: " + bool2);
/*      */       }
/*      */ 
/*  614 */       if (bool3 == true) {
/*  615 */         localObject3 = paramDescriptor.getFieldValue("value");
/*  616 */         if (localObject3 != null)
/*      */         {
/*  618 */           localObject1 = localObject3;
/*      */ 
/*  620 */           if (bool1) {
/*  621 */             JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "valid Cache value: " + localObject3);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  627 */           localObject1 = null;
/*  628 */           if (bool1) {
/*  629 */             JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "no Cached value");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  636 */       if (bool2 == true)
/*      */       {
/*  638 */         paramDescriptor.removeField("lastUpdatedTimeStamp");
/*  639 */         paramDescriptor.removeField("value");
/*  640 */         localObject1 = null;
/*  641 */         this.modelMBeanInfo.setDescriptor(paramDescriptor, null);
/*  642 */         if (bool1) {
/*  643 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "reset cached value to null");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  650 */     if (bool1) {
/*  651 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveForCacheValue(Descriptor)", "Exit");
/*      */     }
/*      */ 
/*  655 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public MBeanInfo getMBeanInfo()
/*      */   {
/*  668 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  669 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "Entry");
/*      */     }
/*      */ 
/*  674 */     if (this.modelMBeanInfo == null) {
/*  675 */       if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  676 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "modelMBeanInfo is null");
/*      */       }
/*      */ 
/*  680 */       this.modelMBeanInfo = createDefaultModelMBeanInfo();
/*      */     }
/*      */ 
/*  684 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  685 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", "ModelMBeanInfo is " + this.modelMBeanInfo.getClassName() + " for " + this.modelMBeanInfo.getDescription());
/*      */ 
/*  690 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getMBeanInfo()", printModelMBeanInfo(this.modelMBeanInfo));
/*      */     }
/*      */ 
/*  695 */     return (MBeanInfo)this.modelMBeanInfo.clone();
/*      */   }
/*      */ 
/*      */   private String printModelMBeanInfo(ModelMBeanInfo paramModelMBeanInfo) {
/*  699 */     StringBuilder localStringBuilder = new StringBuilder();
/*  700 */     if (paramModelMBeanInfo == null) {
/*  701 */       if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/*  702 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "printModelMBeanInfo(ModelMBeanInfo)", "ModelMBeanInfo to print is null, printing local ModelMBeanInfo");
/*      */       }
/*      */ 
/*  708 */       paramModelMBeanInfo = this.modelMBeanInfo;
/*      */     }
/*      */ 
/*  711 */     localStringBuilder.append("\nMBeanInfo for ModelMBean is:");
/*  712 */     localStringBuilder.append("\nCLASSNAME: \t" + paramModelMBeanInfo.getClassName());
/*  713 */     localStringBuilder.append("\nDESCRIPTION: \t" + paramModelMBeanInfo.getDescription());
/*      */     try
/*      */     {
/*  717 */       localStringBuilder.append("\nMBEAN DESCRIPTOR: \t" + paramModelMBeanInfo.getMBeanDescriptor());
/*      */     }
/*      */     catch (Exception localException) {
/*  720 */       localStringBuilder.append("\nMBEAN DESCRIPTOR: \t is invalid");
/*      */     }
/*      */ 
/*  723 */     localStringBuilder.append("\nATTRIBUTES");
/*      */ 
/*  725 */     MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = paramModelMBeanInfo.getAttributes();
/*  726 */     if ((arrayOfMBeanAttributeInfo != null) && (arrayOfMBeanAttributeInfo.length > 0)) {
/*  727 */       for (int i = 0; i < arrayOfMBeanAttributeInfo.length; i++) {
/*  728 */         ModelMBeanAttributeInfo localModelMBeanAttributeInfo = (ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[i];
/*      */ 
/*  730 */         localStringBuilder.append(" ** NAME: \t" + localModelMBeanAttributeInfo.getName());
/*  731 */         localStringBuilder.append("    DESCR: \t" + localModelMBeanAttributeInfo.getDescription());
/*  732 */         localStringBuilder.append("    TYPE: \t" + localModelMBeanAttributeInfo.getType() + "    READ: \t" + localModelMBeanAttributeInfo.isReadable() + "    WRITE: \t" + localModelMBeanAttributeInfo.isWritable());
/*      */ 
/*  735 */         localStringBuilder.append("    DESCRIPTOR: " + localModelMBeanAttributeInfo.getDescriptor().toString());
/*      */       }
/*      */     }
/*      */     else {
/*  739 */       localStringBuilder.append(" ** No attributes **");
/*      */     }
/*      */ 
/*  742 */     localStringBuilder.append("\nCONSTRUCTORS");
/*  743 */     MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = paramModelMBeanInfo.getConstructors();
/*  744 */     if ((arrayOfMBeanConstructorInfo != null) && (arrayOfMBeanConstructorInfo.length > 0)) {
/*  745 */       for (int j = 0; j < arrayOfMBeanConstructorInfo.length; j++) {
/*  746 */         ModelMBeanConstructorInfo localModelMBeanConstructorInfo = (ModelMBeanConstructorInfo)arrayOfMBeanConstructorInfo[j];
/*      */ 
/*  748 */         localStringBuilder.append(" ** NAME: \t" + localModelMBeanConstructorInfo.getName());
/*  749 */         localStringBuilder.append("    DESCR: \t" + localModelMBeanConstructorInfo.getDescription());
/*      */ 
/*  751 */         localStringBuilder.append("    PARAM: \t" + localModelMBeanConstructorInfo.getSignature().length + " parameter(s)");
/*      */ 
/*  754 */         localStringBuilder.append("    DESCRIPTOR: " + localModelMBeanConstructorInfo.getDescriptor().toString());
/*      */       }
/*      */     }
/*      */     else {
/*  758 */       localStringBuilder.append(" ** No Constructors **");
/*      */     }
/*      */ 
/*  761 */     localStringBuilder.append("\nOPERATIONS");
/*  762 */     MBeanOperationInfo[] arrayOfMBeanOperationInfo = paramModelMBeanInfo.getOperations();
/*  763 */     if ((arrayOfMBeanOperationInfo != null) && (arrayOfMBeanOperationInfo.length > 0)) {
/*  764 */       for (int k = 0; k < arrayOfMBeanOperationInfo.length; k++) {
/*  765 */         ModelMBeanOperationInfo localModelMBeanOperationInfo = (ModelMBeanOperationInfo)arrayOfMBeanOperationInfo[k];
/*      */ 
/*  767 */         localStringBuilder.append(" ** NAME: \t" + localModelMBeanOperationInfo.getName());
/*  768 */         localStringBuilder.append("    DESCR: \t" + localModelMBeanOperationInfo.getDescription());
/*  769 */         localStringBuilder.append("    PARAM: \t" + localModelMBeanOperationInfo.getSignature().length + " parameter(s)");
/*      */ 
/*  772 */         localStringBuilder.append("    DESCRIPTOR: " + localModelMBeanOperationInfo.getDescriptor().toString());
/*      */       }
/*      */     }
/*      */     else {
/*  776 */       localStringBuilder.append(" ** No operations ** ");
/*      */     }
/*      */ 
/*  779 */     localStringBuilder.append("\nNOTIFICATIONS");
/*      */ 
/*  781 */     MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = paramModelMBeanInfo.getNotifications();
/*  782 */     if ((arrayOfMBeanNotificationInfo != null) && (arrayOfMBeanNotificationInfo.length > 0)) {
/*  783 */       for (int m = 0; m < arrayOfMBeanNotificationInfo.length; m++) {
/*  784 */         ModelMBeanNotificationInfo localModelMBeanNotificationInfo = (ModelMBeanNotificationInfo)arrayOfMBeanNotificationInfo[m];
/*      */ 
/*  786 */         localStringBuilder.append(" ** NAME: \t" + localModelMBeanNotificationInfo.getName());
/*  787 */         localStringBuilder.append("    DESCR: \t" + localModelMBeanNotificationInfo.getDescription());
/*  788 */         localStringBuilder.append("    DESCRIPTOR: " + localModelMBeanNotificationInfo.getDescriptor().toString());
/*      */       }
/*      */     }
/*      */     else {
/*  792 */       localStringBuilder.append(" ** No notifications **");
/*      */     }
/*      */ 
/*  795 */     localStringBuilder.append(" ** ModelMBean: End of MBeanInfo ** ");
/*      */ 
/*  797 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*      */     throws MBeanException, ReflectionException
/*      */   {
/*  910 */     boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
/*      */ 
/*  913 */     if (bool) {
/*  914 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Entry");
/*      */     }
/*      */ 
/*  918 */     if (paramString == null) {
/*  919 */       localObject1 = new IllegalArgumentException("Method name must not be null");
/*      */ 
/*  921 */       throw new RuntimeOperationsException((RuntimeException)localObject1, "An exception occurred while trying to invoke a method on a RequiredModelMBean");
/*      */     }
/*      */ 
/*  926 */     Object localObject1 = null;
/*      */ 
/*  930 */     int i = paramString.lastIndexOf(".");
/*  931 */     if (i > 0) {
/*  932 */       localObject1 = paramString.substring(0, i);
/*  933 */       str1 = paramString.substring(i + 1);
/*      */     } else {
/*  935 */       str1 = paramString;
/*      */     }
/*      */ 
/*  939 */     i = str1.indexOf("(");
/*  940 */     if (i > 0) {
/*  941 */       str1 = str1.substring(0, i);
/*      */     }
/*  943 */     if (bool) {
/*  944 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Finding operation " + paramString + " as " + str1);
/*      */     }
/*      */ 
/*  949 */     ModelMBeanOperationInfo localModelMBeanOperationInfo = this.modelMBeanInfo.getOperation(str1);
/*      */ 
/*  951 */     if (localModelMBeanOperationInfo == null) {
/*  952 */       localObject2 = "Operation " + paramString + " not in ModelMBeanInfo";
/*      */ 
/*  954 */       throw new MBeanException(new ServiceNotFoundException((String)localObject2), (String)localObject2);
/*      */     }
/*      */ 
/*  957 */     Object localObject2 = localModelMBeanOperationInfo.getDescriptor();
/*  958 */     if (localObject2 == null)
/*      */     {
/*  960 */       throw new MBeanException(new ServiceNotFoundException("Operation descriptor null"), "Operation descriptor null");
/*      */     }
/*      */ 
/*  963 */     Object localObject3 = resolveForCacheValue((Descriptor)localObject2);
/*  964 */     if (localObject3 != null) {
/*  965 */       if (bool) {
/*  966 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Returning cached value");
/*      */       }
/*      */ 
/*  971 */       return localObject3;
/*      */     }
/*      */ 
/*  974 */     if (localObject1 == null) {
/*  975 */       localObject1 = (String)((Descriptor)localObject2).getFieldValue("class");
/*      */     }
/*      */ 
/*  978 */     String str1 = (String)((Descriptor)localObject2).getFieldValue("name");
/*  979 */     if (str1 == null)
/*      */     {
/*  982 */       throw new MBeanException(new ServiceNotFoundException("Method descriptor must include `name' field"), "Method descriptor must include `name' field");
/*      */     }
/*      */ 
/*  985 */     String str2 = (String)((Descriptor)localObject2).getFieldValue("targetType");
/*      */ 
/*  987 */     if ((str2 != null) && (!str2.equalsIgnoreCase("objectReference")))
/*      */     {
/*  989 */       localObject4 = "Target type must be objectReference: " + str2;
/*      */ 
/*  991 */       throw new MBeanException(new InvalidTargetObjectTypeException((String)localObject4), (String)localObject4);
/*      */     }
/*      */ 
/*  995 */     Object localObject4 = ((Descriptor)localObject2).getFieldValue("targetObject");
/*  996 */     if ((bool) && (localObject4 != null)) {
/*  997 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "Found target object in descriptor");
/*      */     }
/*      */ 
/* 1007 */     Method localMethod = findRMMBMethod(str1, localObject4, (String)localObject1, paramArrayOfString);
/*      */     Object localObject5;
/* 1010 */     if (localMethod != null) {
/* 1011 */       localObject5 = this;
/*      */     } else {
/* 1013 */       if (bool)
/* 1014 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in managedResource class");
/*      */       Object localObject7;
/* 1018 */       if (localObject4 != null) {
/* 1019 */         localObject5 = localObject4;
/*      */       } else {
/* 1021 */         localObject5 = this.managedResource;
/* 1022 */         if (localObject5 == null) {
/* 1023 */           localObject6 = "managedResource for invoke " + paramString + " is null";
/*      */ 
/* 1026 */           localObject7 = new ServiceNotFoundException((String)localObject6);
/* 1027 */           throw new MBeanException((Exception)localObject7);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1033 */       if (localObject1 != null)
/*      */         try {
/* 1035 */           localObject7 = AccessController.getContext();
/* 1036 */           localObject8 = localObject5;
/* 1037 */           final Object localObject9 = localObject1;
/* 1038 */           final ClassNotFoundException[] arrayOfClassNotFoundException = new ClassNotFoundException[1];
/*      */ 
/* 1040 */           localObject6 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
/*      */           {
/*      */             public Class<?> run()
/*      */             {
/*      */               try {
/* 1045 */                 ReflectUtil.checkPackageAccess(localObject9);
/* 1046 */                 ClassLoader localClassLoader = localObject8.getClass().getClassLoader();
/*      */ 
/* 1048 */                 return Class.forName(localObject9, false, localClassLoader);
/*      */               }
/*      */               catch (ClassNotFoundException localClassNotFoundException) {
/* 1051 */                 arrayOfClassNotFoundException[0] = localClassNotFoundException;
/*      */               }
/* 1053 */               return null;
/*      */             }
/*      */           }
/*      */           , (AccessControlContext)localObject7, this.acc);
/*      */ 
/* 1057 */           if (arrayOfClassNotFoundException[0] != null)
/* 1058 */             throw arrayOfClassNotFoundException[0];
/*      */         }
/*      */         catch (ClassNotFoundException localClassNotFoundException) {
/* 1061 */           final Object localObject8 = "class for invoke " + paramString + " not found";
/*      */ 
/* 1063 */           throw new ReflectionException(localClassNotFoundException, (String)localObject8);
/*      */         }
/*      */       else {
/* 1066 */         localObject6 = localObject5.getClass();
/*      */       }
/* 1068 */       localMethod = resolveMethod((Class)localObject6, str1, paramArrayOfString);
/*      */     }
/*      */ 
/* 1071 */     if (bool) {
/* 1072 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "found " + str1 + ", now invoking");
/*      */     }
/*      */ 
/* 1077 */     Object localObject6 = invokeMethod(paramString, localMethod, localObject5, paramArrayOfObject);
/*      */ 
/* 1080 */     if (bool) {
/* 1081 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "successfully invoked method");
/*      */     }
/*      */ 
/* 1086 */     if (localObject6 != null) {
/* 1087 */       cacheResult(localModelMBeanOperationInfo, (Descriptor)localObject2, localObject6);
/*      */     }
/* 1089 */     return localObject6;
/*      */   }
/*      */ 
/*      */   private Method resolveMethod(Class<?> paramClass, String paramString, final String[] paramArrayOfString)
/*      */     throws ReflectionException
/*      */   {
/* 1096 */     final boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
/*      */ 
/* 1098 */     if (bool)
/* 1099 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolving " + paramClass.getName() + "." + paramString);
/*      */     final Class[] arrayOfClass;
/*      */     Object localObject;
/* 1106 */     if (paramArrayOfString == null) {
/* 1107 */       arrayOfClass = null;
/*      */     } else {
/* 1109 */       AccessControlContext localAccessControlContext = AccessController.getContext();
/* 1110 */       localObject = new ReflectionException[1];
/* 1111 */       final ClassLoader localClassLoader = paramClass.getClassLoader();
/* 1112 */       arrayOfClass = new Class[paramArrayOfString.length];
/*      */ 
/* 1114 */       javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
/*      */       {
/*      */         public Void run()
/*      */         {
/* 1118 */           for (int i = 0; i < paramArrayOfString.length; i++) {
/* 1119 */             if (bool) {
/* 1120 */               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "resolve type " + paramArrayOfString[i]);
/*      */             }
/*      */ 
/* 1124 */             arrayOfClass[i] = ((Class)RequiredModelMBean.primitiveClassMap.get(paramArrayOfString[i]));
/* 1125 */             if (arrayOfClass[i] == null) {
/*      */               try {
/* 1127 */                 ReflectUtil.checkPackageAccess(paramArrayOfString[i]);
/* 1128 */                 arrayOfClass[i] = Class.forName(paramArrayOfString[i], false, localClassLoader);
/*      */               }
/*      */               catch (ClassNotFoundException localClassNotFoundException) {
/* 1131 */                 if (bool) {
/* 1132 */                   JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "resolveMethod", "class not found");
/*      */                 }
/*      */ 
/* 1138 */                 this.val$caughtException[0] = new ReflectionException(localClassNotFoundException, "Parameter class not found");
/*      */               }
/*      */             }
/*      */           }
/* 1142 */           return null;
/*      */         }
/*      */       }
/*      */       , localAccessControlContext, this.acc);
/*      */ 
/* 1146 */       if (localObject[0] != null) {
/* 1147 */         throw localObject[0];
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1152 */       return paramClass.getMethod(paramString, arrayOfClass);
/*      */     } catch (NoSuchMethodException localNoSuchMethodException) {
/* 1154 */       localObject = "Target method not found: " + paramClass.getName() + "." + paramString;
/*      */ 
/* 1157 */       throw new ReflectionException(localNoSuchMethodException, (String)localObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Method findRMMBMethod(String paramString1, Object paramObject, String paramString2, String[] paramArrayOfString)
/*      */   {
/* 1183 */     boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
/*      */ 
/* 1185 */     if (bool) {
/* 1186 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String, Object[], String[])", "looking for method in RequiredModelMBean class");
/*      */     }
/*      */ 
/* 1192 */     if (!isRMMBMethodName(paramString1))
/* 1193 */       return null;
/* 1194 */     if (paramObject != null)
/* 1195 */       return null;
/* 1196 */     final RequiredModelMBean localRequiredModelMBean = RequiredModelMBean.class;
/*      */     Object localObject;
/* 1198 */     if (paramString2 == null) {
/* 1199 */       localObject = localRequiredModelMBean;
/*      */     } else {
/* 1201 */       AccessControlContext localAccessControlContext = AccessController.getContext();
/* 1202 */       final String str = paramString2;
/* 1203 */       localObject = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
/*      */       {
/*      */         public Class<?> run()
/*      */         {
/*      */           try {
/* 1208 */             ReflectUtil.checkPackageAccess(str);
/* 1209 */             ClassLoader localClassLoader = localRequiredModelMBean.getClassLoader();
/*      */ 
/* 1211 */             Class localClass = Class.forName(str, false, localClassLoader);
/*      */ 
/* 1213 */             if (!localRequiredModelMBean.isAssignableFrom(localClass))
/* 1214 */               return null;
/* 1215 */             return localClass; } catch (ClassNotFoundException localClassNotFoundException) {
/*      */           }
/* 1217 */           return null;
/*      */         }
/*      */       }
/*      */       , localAccessControlContext, this.acc);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1223 */       return localObject != null ? resolveMethod((Class)localObject, paramString1, paramArrayOfString) : null; } catch (ReflectionException localReflectionException) {
/*      */     }
/* 1225 */     return null;
/*      */   }
/*      */ 
/*      */   private Object invokeMethod(String paramString, final Method paramMethod, final Object paramObject, final Object[] paramArrayOfObject)
/*      */     throws MBeanException, ReflectionException
/*      */   {
/*      */     try
/*      */     {
/* 1237 */       final Throwable[] arrayOfThrowable = new Throwable[1];
/* 1238 */       localObject1 = AccessController.getContext();
/* 1239 */       Object localObject2 = javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
/*      */       {
/*      */         public Object run()
/*      */         {
/*      */           try {
/* 1244 */             ReflectUtil.checkPackageAccess(paramMethod.getDeclaringClass());
/* 1245 */             return MethodUtil.invoke(paramMethod, paramObject, paramArrayOfObject);
/*      */           } catch (InvocationTargetException localInvocationTargetException) {
/* 1247 */             arrayOfThrowable[0] = localInvocationTargetException;
/*      */           } catch (IllegalAccessException localIllegalAccessException) {
/* 1249 */             arrayOfThrowable[0] = localIllegalAccessException;
/*      */           }
/* 1251 */           return null;
/*      */         }
/*      */       }
/*      */       , (AccessControlContext)localObject1, this.acc);
/*      */ 
/* 1254 */       if (arrayOfThrowable[0] != null) {
/* 1255 */         if ((arrayOfThrowable[0] instanceof Exception))
/* 1256 */           throw ((Exception)arrayOfThrowable[0]);
/* 1257 */         if ((arrayOfThrowable[0] instanceof Error)) {
/* 1258 */           throw ((Error)arrayOfThrowable[0]);
/*      */         }
/*      */       }
/* 1261 */       return localObject2;
/*      */     } catch (RuntimeErrorException localRuntimeErrorException) {
/* 1263 */       throw new RuntimeOperationsException(localRuntimeErrorException, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + paramString);
/*      */     }
/*      */     catch (RuntimeException localRuntimeException)
/*      */     {
/* 1267 */       throw new RuntimeOperationsException(localRuntimeException, "RuntimeException occurred in RequiredModelMBean while trying to invoke operation " + paramString);
/*      */     }
/*      */     catch (IllegalAccessException localIllegalAccessException)
/*      */     {
/* 1271 */       throw new ReflectionException(localIllegalAccessException, "IllegalAccessException occurred in RequiredModelMBean while trying to invoke operation " + paramString);
/*      */     }
/*      */     catch (InvocationTargetException localInvocationTargetException)
/*      */     {
/* 1276 */       Object localObject1 = localInvocationTargetException.getTargetException();
/* 1277 */       if ((localObject1 instanceof RuntimeException)) {
/* 1278 */         throw new MBeanException((RuntimeException)localObject1, "RuntimeException thrown in RequiredModelMBean while trying to invoke operation " + paramString);
/*      */       }
/*      */ 
/* 1281 */       if ((localObject1 instanceof Error)) {
/* 1282 */         throw new RuntimeErrorException((Error)localObject1, "Error occurred in RequiredModelMBean while trying to invoke operation " + paramString);
/*      */       }
/*      */ 
/* 1285 */       if ((localObject1 instanceof ReflectionException)) {
/* 1286 */         throw ((ReflectionException)localObject1);
/*      */       }
/* 1288 */       throw new MBeanException((Exception)localObject1, "Exception thrown in RequiredModelMBean while trying to invoke operation " + paramString);
/*      */     }
/*      */     catch (Error localError)
/*      */     {
/* 1293 */       throw new RuntimeErrorException(localError, "Error occurred in RequiredModelMBean while trying to invoke operation " + paramString);
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 1297 */       throw new ReflectionException(localException, "Exception occurred in RequiredModelMBean while trying to invoke operation " + paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void cacheResult(ModelMBeanOperationInfo paramModelMBeanOperationInfo, Descriptor paramDescriptor, Object paramObject)
/*      */     throws MBeanException
/*      */   {
/* 1313 */     Descriptor localDescriptor = this.modelMBeanInfo.getMBeanDescriptor();
/*      */ 
/* 1316 */     Object localObject = paramDescriptor.getFieldValue("currencyTimeLimit");
/*      */     String str;
/* 1319 */     if (localObject != null)
/* 1320 */       str = localObject.toString();
/*      */     else {
/* 1322 */       str = null;
/*      */     }
/* 1324 */     if ((str == null) && (localDescriptor != null)) {
/* 1325 */       localObject = localDescriptor.getFieldValue("currencyTimeLimit");
/*      */ 
/* 1327 */       if (localObject != null)
/* 1328 */         str = localObject.toString();
/*      */       else {
/* 1330 */         str = null;
/*      */       }
/*      */     }
/* 1333 */     if ((str != null) && (!str.equals("-1"))) {
/* 1334 */       paramDescriptor.setField("value", paramObject);
/* 1335 */       paramDescriptor.setField("lastUpdatedTimeStamp", String.valueOf(new Date().getTime()));
/*      */ 
/* 1339 */       this.modelMBeanInfo.setDescriptor(paramDescriptor, "operation");
/*      */ 
/* 1341 */       if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
/* 1342 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "invoke(String,Object[],Object[])", "new descriptor is " + paramDescriptor);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static synchronized boolean isRMMBMethodName(String paramString)
/*      */   {
/* 1366 */     if (rmmbMethodNames == null) {
/*      */       try {
/* 1368 */         HashSet localHashSet = new HashSet();
/* 1369 */         Method[] arrayOfMethod = RequiredModelMBean.class.getMethods();
/* 1370 */         for (int i = 0; i < arrayOfMethod.length; i++)
/* 1371 */           localHashSet.add(arrayOfMethod[i].getName());
/* 1372 */         rmmbMethodNames = localHashSet;
/*      */       } catch (Exception localException) {
/* 1374 */         return true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1379 */     return rmmbMethodNames.contains(paramString);
/*      */   }
/*      */ 
/*      */   public Object getAttribute(String paramString)
/*      */     throws AttributeNotFoundException, MBeanException, ReflectionException
/*      */   {
/* 1491 */     if (paramString == null) {
/* 1492 */       throw new RuntimeOperationsException(new IllegalArgumentException("attributeName must not be null"), "Exception occurred trying to get attribute of a RequiredModelMBean");
/*      */     }
/*      */ 
/* 1497 */     boolean bool1 = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
/* 1498 */     if (bool1) {
/* 1499 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Entry with " + paramString);
/*      */     }
/*      */ 
/*      */     Object localObject1;
/*      */     try
/*      */     {
/* 1508 */       if (this.modelMBeanInfo == null) {
/* 1509 */         throw new AttributeNotFoundException("getAttribute failed: ModelMBeanInfo not found for " + paramString);
/*      */       }
/*      */ 
/* 1513 */       ModelMBeanAttributeInfo localModelMBeanAttributeInfo = this.modelMBeanInfo.getAttribute(paramString);
/* 1514 */       Descriptor localDescriptor1 = this.modelMBeanInfo.getMBeanDescriptor();
/*      */ 
/* 1516 */       if (localModelMBeanAttributeInfo == null) {
/* 1517 */         throw new AttributeNotFoundException("getAttribute failed: ModelMBeanAttributeInfo not found for " + paramString);
/*      */       }
/*      */ 
/* 1520 */       Descriptor localDescriptor2 = localModelMBeanAttributeInfo.getDescriptor();
/* 1521 */       if (localDescriptor2 != null) {
/* 1522 */         if (!localModelMBeanAttributeInfo.isReadable()) {
/* 1523 */           throw new AttributeNotFoundException("getAttribute failed: " + paramString + " is not readable ");
/*      */         }
/*      */ 
/* 1527 */         localObject1 = resolveForCacheValue(localDescriptor2);
/*      */ 
/* 1530 */         if (bool1)
/* 1531 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "*** cached value is " + localObject1);
/*      */         Object localObject2;
/* 1536 */         if (localObject1 == null)
/*      */         {
/* 1538 */           if (bool1) {
/* 1539 */             JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "**** cached value is null - getting getMethod");
/*      */           }
/*      */ 
/* 1543 */           str1 = (String)localDescriptor2.getFieldValue("getMethod");
/*      */ 
/* 1546 */           if (str1 != null)
/*      */           {
/* 1548 */             if (bool1) {
/* 1549 */               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "invoking a getMethod for " + paramString);
/*      */             }
/*      */ 
/* 1554 */             localObject2 = invoke(str1, new Object[0], new String[0]);
/*      */ 
/* 1558 */             if (localObject2 != null)
/*      */             {
/* 1560 */               if (bool1) {
/* 1561 */                 JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a non-null response from getMethod\n");
/*      */               }
/*      */ 
/* 1567 */               localObject1 = localObject2;
/*      */ 
/* 1570 */               Object localObject3 = localDescriptor2.getFieldValue("currencyTimeLimit");
/*      */               String str2;
/* 1574 */               if (localObject3 != null) str2 = localObject3.toString(); else {
/* 1575 */                 str2 = null;
/*      */               }
/* 1577 */               if ((str2 == null) && (localDescriptor1 != null)) {
/* 1578 */                 localObject3 = localDescriptor1.getFieldValue("currencyTimeLimit");
/*      */ 
/* 1580 */                 if (localObject3 != null) str2 = localObject3.toString(); else {
/* 1581 */                   str2 = null;
/*      */                 }
/*      */               }
/* 1584 */               if ((str2 != null) && (!str2.equals("-1"))) {
/* 1585 */                 if (bool1) {
/* 1586 */                   JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "setting cached value and lastUpdatedTime in descriptor");
/*      */                 }
/*      */ 
/* 1592 */                 localDescriptor2.setField("value", localObject1);
/* 1593 */                 String str3 = String.valueOf(new Date().getTime());
/*      */ 
/* 1595 */                 localDescriptor2.setField("lastUpdatedTimeStamp", str3);
/*      */ 
/* 1597 */                 localModelMBeanAttributeInfo.setDescriptor(localDescriptor2);
/* 1598 */                 this.modelMBeanInfo.setDescriptor(localDescriptor2, "attribute");
/*      */ 
/* 1600 */                 if (bool1) {
/* 1601 */                   JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "new descriptor is " + localDescriptor2);
/*      */ 
/* 1604 */                   JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "AttributeInfo descriptor is " + localModelMBeanAttributeInfo.getDescriptor());
/*      */ 
/* 1608 */                   String str4 = this.modelMBeanInfo.getDescriptor(paramString, "attribute").toString();
/*      */ 
/* 1611 */                   JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "modelMBeanInfo: AttributeInfo descriptor is " + str4);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 1620 */               if (bool1) {
/* 1621 */                 JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "got a null response from getMethod\n");
/*      */               }
/*      */ 
/* 1625 */               localObject1 = null;
/*      */             }
/*      */           }
/*      */           else {
/* 1629 */             localObject2 = "";
/* 1630 */             localObject1 = localDescriptor2.getFieldValue("value");
/* 1631 */             if (localObject1 == null) {
/* 1632 */               localObject2 = "default ";
/* 1633 */               localObject1 = localDescriptor2.getFieldValue("default");
/*      */             }
/* 1635 */             if (bool1) {
/* 1636 */               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "could not find getMethod for " + paramString + ", returning descriptor " + (String)localObject2 + "value");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1646 */         final String str1 = localModelMBeanAttributeInfo.getType();
/* 1647 */         if (localObject1 != null) {
/* 1648 */           localObject2 = localObject1.getClass().getName();
/* 1649 */           if (!str1.equals(localObject2)) {
/* 1650 */             int i = 0;
/* 1651 */             int j = 0;
/* 1652 */             int k = 0;
/* 1653 */             for (int m = 0; m < primitiveTypes.length; m++) {
/* 1654 */               if (str1.equals(primitiveTypes[m])) {
/* 1655 */                 j = 1;
/* 1656 */                 if (!((String)localObject2).equals(primitiveWrappers[m])) break;
/* 1657 */                 k = 1; break;
/*      */               }
/*      */             }
/*      */ 
/* 1661 */             if (j != 0)
/*      */             {
/* 1663 */               if (k == 0)
/* 1664 */                 i = 1;
/*      */             }
/*      */             else {
/*      */               boolean bool2;
/*      */               try {
/* 1669 */                 final Class localClass1 = localObject1.getClass();
/* 1670 */                 final Exception[] arrayOfException = new Exception[1];
/*      */ 
/* 1672 */                 AccessControlContext localAccessControlContext = AccessController.getContext();
/*      */ 
/* 1674 */                 Class localClass2 = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
/*      */                 {
/*      */                   public Class<?> run()
/*      */                   {
/*      */                     try {
/* 1679 */                       ReflectUtil.checkPackageAccess(str1);
/* 1680 */                       ClassLoader localClassLoader = localClass1.getClassLoader();
/*      */ 
/* 1682 */                       return Class.forName(str1, true, localClassLoader);
/*      */                     } catch (Exception localException) {
/* 1684 */                       arrayOfException[0] = localException;
/*      */                     }
/* 1686 */                     return null;
/*      */                   }
/*      */                 }
/*      */                 , localAccessControlContext, this.acc);
/*      */ 
/* 1690 */                 if (arrayOfException[0] != null) {
/* 1691 */                   throw arrayOfException[0];
/*      */                 }
/*      */ 
/* 1694 */                 bool2 = localClass2.isInstance(localObject1);
/*      */               } catch (Exception localException2) {
/* 1696 */                 bool2 = false;
/*      */ 
/* 1698 */                 if (bool1) {
/* 1699 */                   JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Exception: ", localException2);
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 1704 */               if (!bool2)
/* 1705 */                 i = 1;
/*      */             }
/* 1707 */             if (i != 0) {
/* 1708 */               if (bool1) {
/* 1709 */                 JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Wrong response type '" + str1 + "'");
/*      */               }
/*      */ 
/* 1715 */               throw new MBeanException(new InvalidAttributeValueException("Wrong value type received for get attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1724 */         if (bool1) {
/* 1725 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed " + paramString + " not in attributeDescriptor\n");
/*      */         }
/*      */ 
/* 1730 */         throw new MBeanException(new InvalidAttributeValueException("Unable to resolve attribute value, no getMethod defined in descriptor for attribute"), "An exception occurred while trying to get an attribute value through a RequiredModelMBean");
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (MBeanException localMBeanException)
/*      */     {
/* 1739 */       throw localMBeanException;
/*      */     } catch (AttributeNotFoundException localAttributeNotFoundException) {
/* 1741 */       throw localAttributeNotFoundException;
/*      */     } catch (Exception localException1) {
/* 1743 */       if (bool1) {
/* 1744 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "getMethod failed with " + localException1.getMessage() + " exception type " + localException1.getClass().toString());
/*      */       }
/*      */ 
/* 1749 */       throw new MBeanException(localException1, "An exception occurred while trying to get an attribute value: " + localException1.getMessage());
/*      */     }
/*      */ 
/* 1753 */     if (bool1) {
/* 1754 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttribute(String)", "Exit");
/*      */     }
/*      */ 
/* 1758 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public AttributeList getAttributes(String[] paramArrayOfString)
/*      */   {
/* 1778 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 1779 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Entry");
/*      */     }
/*      */ 
/* 1784 */     if (paramArrayOfString == null) {
/* 1785 */       throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames must not be null"), "Exception occurred trying to get attributes of a RequiredModelMBean");
/*      */     }
/*      */ 
/* 1790 */     AttributeList localAttributeList = new AttributeList();
/* 1791 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/*      */       try {
/* 1793 */         localAttributeList.add(new Attribute(paramArrayOfString[i], getAttribute(paramArrayOfString[i])));
/*      */       }
/*      */       catch (Exception localException)
/*      */       {
/* 1798 */         if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 1799 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Failed to get \"" + paramArrayOfString[i] + "\": ", localException);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1807 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 1808 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getAttributes(String[])", "Exit");
/*      */     }
/*      */ 
/* 1813 */     return localAttributeList;
/*      */   }
/*      */ 
/*      */   public void setAttribute(Attribute paramAttribute)
/*      */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*      */   {
/* 1895 */     boolean bool = JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER);
/* 1896 */     if (bool) {
/* 1897 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute()", "Entry");
/*      */     }
/*      */ 
/* 1902 */     if (paramAttribute == null) {
/* 1903 */       throw new RuntimeOperationsException(new IllegalArgumentException("attribute must not be null"), "Exception occurred trying to set an attribute of a RequiredModelMBean");
/*      */     }
/*      */ 
/* 1913 */     String str1 = paramAttribute.getName();
/* 1914 */     Object localObject1 = paramAttribute.getValue();
/* 1915 */     int i = 0;
/*      */ 
/* 1917 */     ModelMBeanAttributeInfo localModelMBeanAttributeInfo = this.modelMBeanInfo.getAttribute(str1);
/*      */ 
/* 1920 */     if (localModelMBeanAttributeInfo == null) {
/* 1921 */       throw new AttributeNotFoundException("setAttribute failed: " + str1 + " is not found ");
/*      */     }
/*      */ 
/* 1924 */     Descriptor localDescriptor1 = this.modelMBeanInfo.getMBeanDescriptor();
/* 1925 */     Descriptor localDescriptor2 = localModelMBeanAttributeInfo.getDescriptor();
/*      */ 
/* 1927 */     if (localDescriptor2 != null) {
/* 1928 */       if (!localModelMBeanAttributeInfo.isWritable()) {
/* 1929 */         throw new AttributeNotFoundException("setAttribute failed: " + str1 + " is not writable ");
/*      */       }
/*      */ 
/* 1932 */       String str2 = (String)localDescriptor2.getFieldValue("setMethod");
/*      */ 
/* 1934 */       String str3 = (String)localDescriptor2.getFieldValue("getMethod");
/*      */ 
/* 1937 */       String str4 = localModelMBeanAttributeInfo.getType();
/* 1938 */       Object localObject2 = "Unknown";
/*      */       try
/*      */       {
/* 1941 */         localObject2 = getAttribute(str1);
/*      */       }
/*      */       catch (Throwable localThrowable)
/*      */       {
/*      */       }
/* 1946 */       Attribute localAttribute = new Attribute(str1, localObject2);
/*      */ 
/* 1949 */       if (str2 == null) {
/* 1950 */         if (localObject1 != null) {
/*      */           try {
/* 1952 */             Class localClass = loadClass(str4);
/* 1953 */             if (!localClass.isInstance(localObject1)) throw new InvalidAttributeValueException(localClass.getName() + " expected, " + localObject1.getClass().getName() + " received.");
/*      */ 
/*      */           }
/*      */           catch (ClassNotFoundException localClassNotFoundException)
/*      */           {
/* 1959 */             if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 1960 */               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Class " + str4 + " for attribute " + str1 + " not found: ", localClassNotFoundException);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1968 */         i = 1;
/*      */       } else {
/* 1970 */         invoke(str2, new Object[] { localObject1 }, new String[] { str4 });
/*      */       }
/*      */ 
/* 1976 */       Object localObject3 = localDescriptor2.getFieldValue("currencyTimeLimit");
/*      */       String str5;
/* 1978 */       if (localObject3 != null) str5 = localObject3.toString(); else {
/* 1979 */         str5 = null;
/*      */       }
/* 1981 */       if ((str5 == null) && (localDescriptor1 != null)) {
/* 1982 */         localObject3 = localDescriptor1.getFieldValue("currencyTimeLimit");
/* 1983 */         if (localObject3 != null) str5 = localObject3.toString(); else {
/* 1984 */           str5 = null;
/*      */         }
/*      */       }
/* 1987 */       int j = (str5 != null) && (!str5.equals("-1")) ? 1 : 0;
/*      */ 
/* 1989 */       if ((str2 == null) && (j == 0) && (str3 != null)) {
/* 1990 */         throw new MBeanException(new ServiceNotFoundException("No setMethod field is defined in the descriptor for " + str1 + " attribute and caching is not enabled " + "for it"));
/*      */       }
/*      */ 
/* 1995 */       if ((j != 0) || (i != 0)) {
/* 1996 */         if (bool) {
/* 1997 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setting cached value of " + str1 + " to " + localObject1);
/*      */         }
/*      */ 
/* 2004 */         localDescriptor2.setField("value", localObject1);
/*      */         Object localObject4;
/* 2006 */         if (j != 0) {
/* 2007 */           localObject4 = String.valueOf(new Date().getTime());
/*      */ 
/* 2010 */           localDescriptor2.setField("lastUpdatedTimeStamp", localObject4);
/*      */         }
/*      */ 
/* 2013 */         localModelMBeanAttributeInfo.setDescriptor(localDescriptor2);
/*      */ 
/* 2015 */         this.modelMBeanInfo.setDescriptor(localDescriptor2, "attribute");
/* 2016 */         if (bool) {
/* 2017 */           localObject4 = new StringBuilder().append("new descriptor is ").append(localDescriptor2).append(". AttributeInfo descriptor is ").append(localModelMBeanAttributeInfo.getDescriptor()).append(". AttributeInfo descriptor is ").append(this.modelMBeanInfo.getDescriptor(str1, "attribute"));
/*      */ 
/* 2023 */           JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", ((StringBuilder)localObject4).toString());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2030 */       if (bool) {
/* 2031 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "sending sendAttributeNotification");
/*      */       }
/*      */ 
/* 2035 */       sendAttributeChangeNotification(localAttribute, paramAttribute);
/*      */     }
/*      */     else
/*      */     {
/* 2039 */       if (bool) {
/* 2040 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "setMethod failed " + str1 + " not in attributeDescriptor\n");
/*      */       }
/*      */ 
/* 2046 */       throw new InvalidAttributeValueException("Unable to resolve attribute value, no defined in descriptor for attribute");
/*      */     }
/*      */ 
/* 2051 */     if (bool)
/* 2052 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Exit");
/*      */   }
/*      */ 
/*      */   public AttributeList setAttributes(AttributeList paramAttributeList)
/*      */   {
/* 2077 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2078 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "setAttribute(Attribute)", "Entry");
/*      */     }
/*      */ 
/* 2083 */     if (paramAttributeList == null) {
/* 2084 */       throw new RuntimeOperationsException(new IllegalArgumentException("attributes must not be null"), "Exception occurred trying to set attributes of a RequiredModelMBean");
/*      */     }
/*      */ 
/* 2089 */     AttributeList localAttributeList = new AttributeList();
/*      */ 
/* 2092 */     for (Attribute localAttribute : paramAttributeList.asList()) {
/*      */       try {
/* 2094 */         setAttribute(localAttribute);
/* 2095 */         localAttributeList.add(localAttribute);
/*      */       } catch (Exception localException) {
/* 2097 */         localAttributeList.remove(localAttribute);
/*      */       }
/*      */     }
/*      */ 
/* 2101 */     return localAttributeList;
/*      */   }
/*      */ 
/*      */   private ModelMBeanInfo createDefaultModelMBeanInfo()
/*      */   {
/* 2107 */     return new ModelMBeanInfoSupport(getClass().getName(), "Default ModelMBean", null, null, null, null);
/*      */   }
/*      */ 
/*      */   private synchronized void writeToLog(String paramString1, String paramString2)
/*      */     throws Exception
/*      */   {
/* 2119 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2120 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Notification Logging to " + paramString1 + ": " + paramString2);
/*      */     }
/*      */ 
/* 2125 */     if ((paramString1 == null) || (paramString2 == null)) {
/* 2126 */       if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2127 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Bad input parameters, will not log this entry.");
/*      */       }
/*      */ 
/* 2132 */       return;
/*      */     }
/*      */ 
/* 2135 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramString1, true);
/*      */     try {
/* 2137 */       PrintStream localPrintStream = new PrintStream(localFileOutputStream);
/* 2138 */       localPrintStream.println(paramString2);
/* 2139 */       localPrintStream.close();
/* 2140 */       if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2141 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Successfully opened log " + paramString1);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 2147 */       if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2148 */         JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "writeToLog(String, String)", "Exception " + localException.toString() + " trying to write to the Notification log file " + paramString1);
/*      */       }
/*      */ 
/* 2155 */       throw localException;
/*      */     } finally {
/* 2157 */       localFileOutputStream.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */     throws IllegalArgumentException
/*      */   {
/* 2187 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2188 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry");
/*      */     }
/*      */ 
/* 2192 */     if (paramNotificationListener == null) {
/* 2193 */       throw new IllegalArgumentException("notification listener must not be null");
/*      */     }
/*      */ 
/* 2196 */     if (this.generalBroadcaster == null) {
/* 2197 */       this.generalBroadcaster = new NotificationBroadcasterSupport();
/*      */     }
/* 2199 */     this.generalBroadcaster.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
/*      */ 
/* 2201 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2202 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "NotificationListener added");
/*      */ 
/* 2205 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeNotificationListener(NotificationListener paramNotificationListener)
/*      */     throws ListenerNotFoundException
/*      */   {
/* 2224 */     if (paramNotificationListener == null) {
/* 2225 */       throw new ListenerNotFoundException("Notification listener is null");
/*      */     }
/*      */ 
/* 2229 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2230 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Entry");
/*      */     }
/*      */ 
/* 2234 */     if (this.generalBroadcaster == null) {
/* 2235 */       throw new ListenerNotFoundException("No notification listeners registered");
/*      */     }
/*      */ 
/* 2239 */     this.generalBroadcaster.removeNotificationListener(paramNotificationListener);
/* 2240 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
/* 2241 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener)", "Exit");
/*      */   }
/*      */ 
/*      */   public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
/*      */     throws ListenerNotFoundException
/*      */   {
/* 2252 */     if (paramNotificationListener == null) {
/* 2253 */       throw new ListenerNotFoundException("Notification listener is null");
/*      */     }
/*      */ 
/* 2259 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2260 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Entry");
/*      */     }
/*      */ 
/* 2264 */     if (this.generalBroadcaster == null) {
/* 2265 */       throw new ListenerNotFoundException("No notification listeners registered");
/*      */     }
/*      */ 
/* 2269 */     this.generalBroadcaster.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
/*      */ 
/* 2272 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
/* 2273 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeNotificationListener(NotificationListener, NotificationFilter, Object)", "Exit");
/*      */   }
/*      */ 
/*      */   public void sendNotification(Notification paramNotification)
/*      */     throws MBeanException, RuntimeOperationsException
/*      */   {
/* 2281 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2282 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "Entry");
/*      */     }
/*      */ 
/* 2287 */     if (paramNotification == null) {
/* 2288 */       throw new RuntimeOperationsException(new IllegalArgumentException("notification object must not be null"), "Exception occurred trying to send a notification from a RequiredModelMBean");
/*      */     }
/*      */ 
/* 2296 */     Descriptor localDescriptor1 = this.modelMBeanInfo.getDescriptor(paramNotification.getType(), "notification");
/*      */ 
/* 2298 */     Descriptor localDescriptor2 = this.modelMBeanInfo.getMBeanDescriptor();
/*      */ 
/* 2300 */     if (localDescriptor1 != null) {
/* 2301 */       String str1 = (String)localDescriptor1.getFieldValue("log");
/*      */ 
/* 2303 */       if ((str1 == null) && 
/* 2304 */         (localDescriptor2 != null)) {
/* 2305 */         str1 = (String)localDescriptor2.getFieldValue("log");
/*      */       }
/*      */ 
/* 2308 */       if ((str1 != null) && ((str1.equalsIgnoreCase("t")) || (str1.equalsIgnoreCase("true"))))
/*      */       {
/* 2312 */         String str2 = (String)localDescriptor1.getFieldValue("logfile");
/* 2313 */         if ((str2 == null) && 
/* 2314 */           (localDescriptor2 != null)) {
/* 2315 */           str2 = (String)localDescriptor2.getFieldValue("logfile");
/*      */         }
/* 2317 */         if (str2 != null) {
/*      */           try {
/* 2319 */             writeToLog(str2, "LogMsg: " + new Date(paramNotification.getTimeStamp()).toString() + " " + paramNotification.getType() + " " + paramNotification.getMessage() + " Severity = " + (String)localDescriptor1.getFieldValue("severity"));
/*      */           }
/*      */           catch (Exception localException)
/*      */           {
/* 2325 */             if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
/* 2326 */               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "Failed to log " + paramNotification.getType() + " notification: ", localException);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2336 */     if (this.generalBroadcaster != null) {
/* 2337 */       this.generalBroadcaster.sendNotification(paramNotification);
/*      */     }
/*      */ 
/* 2340 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2341 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", "sendNotification sent provided notification object");
/*      */ 
/* 2345 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(Notification)", " Exit");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void sendNotification(String paramString)
/*      */     throws MBeanException, RuntimeOperationsException
/*      */   {
/* 2355 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2356 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Entry");
/*      */     }
/*      */ 
/* 2361 */     if (paramString == null) {
/* 2362 */       throw new RuntimeOperationsException(new IllegalArgumentException("notification message must not be null"), "Exception occurred trying to send a text notification from a ModelMBean");
/*      */     }
/*      */ 
/* 2368 */     Notification localNotification = new Notification("jmx.modelmbean.generic", this, 1L, paramString);
/*      */ 
/* 2370 */     sendNotification(localNotification);
/* 2371 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2372 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Notification sent");
/*      */ 
/* 2375 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendNotification(String)", "Exit");
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final boolean hasNotification(ModelMBeanInfo paramModelMBeanInfo, String paramString)
/*      */   {
/*      */     try
/*      */     {
/* 2389 */       if (paramModelMBeanInfo == null) return false;
/* 2390 */       return paramModelMBeanInfo.getNotification(paramString) != null;
/*      */     } catch (MBeanException localMBeanException) {
/* 2392 */       return false; } catch (RuntimeOperationsException localRuntimeOperationsException) {
/*      */     }
/* 2394 */     return false;
/*      */   }
/*      */ 
/*      */   private static final ModelMBeanNotificationInfo makeGenericInfo()
/*      */   {
/* 2403 */     DescriptorSupport localDescriptorSupport = new DescriptorSupport(new String[] { "name=GENERIC", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.modelmbean.generic" });
/*      */ 
/* 2411 */     return new ModelMBeanNotificationInfo(new String[] { "jmx.modelmbean.generic" }, "GENERIC", "A text notification has been issued by the managed resource", localDescriptorSupport);
/*      */   }
/*      */ 
/*      */   private static final ModelMBeanNotificationInfo makeAttributeChangeInfo()
/*      */   {
/* 2424 */     DescriptorSupport localDescriptorSupport = new DescriptorSupport(new String[] { "name=ATTRIBUTE_CHANGE", "descriptorType=notification", "log=T", "severity=6", "displayName=jmx.attribute.change" });
/*      */ 
/* 2432 */     return new ModelMBeanNotificationInfo(new String[] { "jmx.attribute.change" }, "ATTRIBUTE_CHANGE", "Signifies that an observed MBean attribute value has changed", localDescriptorSupport);
/*      */   }
/*      */ 
/*      */   public MBeanNotificationInfo[] getNotificationInfo()
/*      */   {
/* 2457 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2458 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Entry");
/*      */     }
/*      */ 
/* 2468 */     boolean bool1 = hasNotification(this.modelMBeanInfo, "GENERIC");
/*      */ 
/* 2473 */     boolean bool2 = hasNotification(this.modelMBeanInfo, "ATTRIBUTE_CHANGE");
/*      */ 
/* 2478 */     ModelMBeanNotificationInfo[] arrayOfModelMBeanNotificationInfo1 = (ModelMBeanNotificationInfo[])this.modelMBeanInfo.getNotifications();
/*      */ 
/* 2485 */     int i = (arrayOfModelMBeanNotificationInfo1 == null ? 0 : arrayOfModelMBeanNotificationInfo1.length) + (bool1 ? 0 : 1) + (bool2 ? 0 : 1);
/*      */ 
/* 2490 */     ModelMBeanNotificationInfo[] arrayOfModelMBeanNotificationInfo2 = new ModelMBeanNotificationInfo[i];
/*      */ 
/* 2499 */     int j = 0;
/* 2500 */     if (!bool1)
/*      */     {
/* 2503 */       arrayOfModelMBeanNotificationInfo2[(j++)] = makeGenericInfo();
/*      */     }
/*      */ 
/* 2506 */     if (!bool2)
/*      */     {
/* 2509 */       arrayOfModelMBeanNotificationInfo2[(j++)] = makeAttributeChangeInfo();
/*      */     }
/*      */ 
/* 2513 */     int k = arrayOfModelMBeanNotificationInfo1.length;
/* 2514 */     int m = j;
/* 2515 */     for (int n = 0; n < k; n++) {
/* 2516 */       arrayOfModelMBeanNotificationInfo2[(m + n)] = arrayOfModelMBeanNotificationInfo1[n];
/*      */     }
/*      */ 
/* 2519 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2520 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "getNotificationInfo()", "Exit");
/*      */     }
/*      */ 
/* 2525 */     return arrayOfModelMBeanNotificationInfo2;
/*      */   }
/*      */ 
/*      */   public void addAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString, Object paramObject)
/*      */     throws MBeanException, RuntimeOperationsException, IllegalArgumentException
/*      */   {
/* 2539 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2540 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Entry");
/*      */     }
/*      */ 
/* 2544 */     if (paramNotificationListener == null) {
/* 2545 */       throw new IllegalArgumentException("Listener to be registered must not be null");
/*      */     }
/*      */ 
/* 2549 */     if (this.attributeBroadcaster == null) {
/* 2550 */       this.attributeBroadcaster = new NotificationBroadcasterSupport();
/*      */     }
/* 2552 */     AttributeChangeNotificationFilter localAttributeChangeNotificationFilter = new AttributeChangeNotificationFilter();
/*      */ 
/* 2555 */     MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanInfo.getAttributes();
/* 2556 */     int i = 0;
/*      */     int j;
/* 2557 */     if (paramString == null) {
/* 2558 */       if ((arrayOfMBeanAttributeInfo != null) && (arrayOfMBeanAttributeInfo.length > 0))
/* 2559 */         for (j = 0; j < arrayOfMBeanAttributeInfo.length; j++)
/* 2560 */           localAttributeChangeNotificationFilter.enableAttribute(arrayOfMBeanAttributeInfo[j].getName());
/*      */     }
/*      */     else
/*      */     {
/* 2564 */       if ((arrayOfMBeanAttributeInfo != null) && (arrayOfMBeanAttributeInfo.length > 0)) {
/* 2565 */         for (j = 0; j < arrayOfMBeanAttributeInfo.length; j++) {
/* 2566 */           if (paramString.equals(arrayOfMBeanAttributeInfo[j].getName())) {
/* 2567 */             i = 1;
/* 2568 */             localAttributeChangeNotificationFilter.enableAttribute(paramString);
/* 2569 */             break;
/*      */           }
/*      */         }
/*      */       }
/* 2573 */       if (i == 0) {
/* 2574 */         throw new RuntimeOperationsException(new IllegalArgumentException("The attribute name does not exist"), "Exception occurred trying to add an AttributeChangeNotification listener");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2582 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2583 */       Vector localVector = localAttributeChangeNotificationFilter.getEnabledAttributes();
/* 2584 */       String str = localVector.size() > 1 ? "[" + (String)localVector.firstElement() + ", ...]" : localVector.toString();
/*      */ 
/* 2587 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Set attribute change filter to " + str);
/*      */     }
/*      */ 
/* 2592 */     this.attributeBroadcaster.addNotificationListener(paramNotificationListener, localAttributeChangeNotificationFilter, paramObject);
/*      */ 
/* 2594 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2595 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Notification listener added for " + paramString);
/*      */ 
/* 2598 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "addAttributeChangeNotificationListener(NotificationListener, String, Object)", "Exit");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString)
/*      */     throws MBeanException, RuntimeOperationsException, ListenerNotFoundException
/*      */   {
/* 2607 */     if (paramNotificationListener == null) throw new ListenerNotFoundException("Notification listener is null");
/*      */ 
/* 2613 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2614 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Entry");
/*      */     }
/*      */ 
/* 2619 */     if (this.attributeBroadcaster == null) {
/* 2620 */       throw new ListenerNotFoundException("No attribute change notification listeners registered");
/*      */     }
/*      */ 
/* 2624 */     MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanInfo.getAttributes();
/* 2625 */     int i = 0;
/* 2626 */     if ((arrayOfMBeanAttributeInfo != null) && (arrayOfMBeanAttributeInfo.length > 0)) {
/* 2627 */       for (int j = 0; j < arrayOfMBeanAttributeInfo.length; j++) {
/* 2628 */         if (arrayOfMBeanAttributeInfo[j].getName().equals(paramString)) {
/* 2629 */           i = 1;
/* 2630 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2635 */     if ((i == 0) && (paramString != null)) {
/* 2636 */       throw new RuntimeOperationsException(new IllegalArgumentException("Invalid attribute name"), "Exception occurred trying to remove attribute change notification listener");
/*      */     }
/*      */ 
/* 2647 */     this.attributeBroadcaster.removeNotificationListener(paramNotificationListener);
/*      */ 
/* 2649 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
/* 2650 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "removeAttributeChangeNotificationListener(NotificationListener, String)", "Exit");
/*      */   }
/*      */ 
/*      */   public void sendAttributeChangeNotification(AttributeChangeNotification paramAttributeChangeNotification)
/*      */     throws MBeanException, RuntimeOperationsException
/*      */   {
/* 2661 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2662 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Entry");
/*      */     }
/*      */ 
/* 2666 */     if (paramAttributeChangeNotification == null) {
/* 2667 */       throw new RuntimeOperationsException(new IllegalArgumentException("attribute change notification object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean");
/*      */     }
/*      */ 
/* 2673 */     Object localObject1 = paramAttributeChangeNotification.getOldValue();
/* 2674 */     Object localObject2 = paramAttributeChangeNotification.getNewValue();
/*      */ 
/* 2676 */     if (localObject1 == null) localObject1 = "null";
/* 2677 */     if (localObject2 == null) localObject2 = "null";
/*      */ 
/* 2679 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2680 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Sending AttributeChangeNotification with " + paramAttributeChangeNotification.getAttributeName() + paramAttributeChangeNotification.getAttributeType() + paramAttributeChangeNotification.getNewValue() + paramAttributeChangeNotification.getOldValue());
/*      */     }
/*      */ 
/* 2688 */     Descriptor localDescriptor1 = this.modelMBeanInfo.getDescriptor(paramAttributeChangeNotification.getType(), "notification");
/*      */ 
/* 2690 */     Descriptor localDescriptor2 = this.modelMBeanInfo.getMBeanDescriptor();
/*      */     String str1;
/*      */     String str2;
/* 2694 */     if (localDescriptor1 != null) {
/* 2695 */       str1 = (String)localDescriptor1.getFieldValue("log");
/* 2696 */       if ((str1 == null) && 
/* 2697 */         (localDescriptor2 != null)) {
/* 2698 */         str1 = (String)localDescriptor2.getFieldValue("log");
/*      */       }
/* 2700 */       if ((str1 != null) && ((str1.equalsIgnoreCase("t")) || (str1.equalsIgnoreCase("true"))))
/*      */       {
/* 2703 */         str2 = (String)localDescriptor1.getFieldValue("logfile");
/* 2704 */         if ((str2 == null) && 
/* 2705 */           (localDescriptor2 != null)) {
/* 2706 */           str2 = (String)localDescriptor2.getFieldValue("logfile");
/*      */         }
/*      */ 
/* 2709 */         if (str2 != null) {
/*      */           try {
/* 2711 */             writeToLog(str2, "LogMsg: " + new Date(paramAttributeChangeNotification.getTimeStamp()).toString() + " " + paramAttributeChangeNotification.getType() + " " + paramAttributeChangeNotification.getMessage() + " Name = " + paramAttributeChangeNotification.getAttributeName() + " Old value = " + localObject1 + " New value = " + localObject2);
/*      */           }
/*      */           catch (Exception localException1)
/*      */           {
/* 2719 */             if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
/* 2720 */               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Failed to log " + paramAttributeChangeNotification.getType() + " notification: ", localException1);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/* 2728 */     else if (localDescriptor2 != null) {
/* 2729 */       str1 = (String)localDescriptor2.getFieldValue("log");
/* 2730 */       if ((str1 != null) && ((str1.equalsIgnoreCase("t")) || (str1.equalsIgnoreCase("true"))))
/*      */       {
/* 2733 */         str2 = (String)localDescriptor2.getFieldValue("logfile");
/*      */ 
/* 2735 */         if (str2 != null) {
/*      */           try {
/* 2737 */             writeToLog(str2, "LogMsg: " + new Date(paramAttributeChangeNotification.getTimeStamp()).toString() + " " + paramAttributeChangeNotification.getType() + " " + paramAttributeChangeNotification.getMessage() + " Name = " + paramAttributeChangeNotification.getAttributeName() + " Old value = " + localObject1 + " New value = " + localObject2);
/*      */           }
/*      */           catch (Exception localException2)
/*      */           {
/* 2745 */             if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINE)) {
/* 2746 */               JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINE, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Failed to log " + paramAttributeChangeNotification.getType() + " notification: ", localException2);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2755 */     if (this.attributeBroadcaster != null) {
/* 2756 */       this.attributeBroadcaster.sendNotification(paramAttributeChangeNotification);
/*      */     }
/*      */ 
/* 2765 */     if (this.generalBroadcaster != null) {
/* 2766 */       this.generalBroadcaster.sendNotification(paramAttributeChangeNotification);
/*      */     }
/*      */ 
/* 2769 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2770 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "sent notification");
/*      */ 
/* 2773 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(AttributeChangeNotification)", "Exit");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void sendAttributeChangeNotification(Attribute paramAttribute1, Attribute paramAttribute2)
/*      */     throws MBeanException, RuntimeOperationsException
/*      */   {
/* 2784 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
/* 2785 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Entry");
/*      */     }
/*      */ 
/* 2791 */     if ((paramAttribute1 == null) || (paramAttribute2 == null)) {
/* 2792 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute object must not be null"), "Exception occurred trying to send attribute change notification of a ModelMBean");
/*      */     }
/*      */ 
/* 2798 */     if (!paramAttribute1.getName().equals(paramAttribute2.getName())) {
/* 2799 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute names are not the same"), "Exception occurred trying to send attribute change notification of a ModelMBean");
/*      */     }
/*      */ 
/* 2805 */     Object localObject1 = paramAttribute2.getValue();
/* 2806 */     Object localObject2 = paramAttribute1.getValue();
/* 2807 */     String str = "unknown";
/* 2808 */     if (localObject1 != null)
/* 2809 */       str = localObject1.getClass().getName();
/* 2810 */     if (localObject2 != null) {
/* 2811 */       str = localObject2.getClass().getName();
/*      */     }
/* 2813 */     AttributeChangeNotification localAttributeChangeNotification = new AttributeChangeNotification(this, 1L, new Date().getTime(), "AttributeChangeDetected", paramAttribute1.getName(), str, paramAttribute1.getValue(), paramAttribute2.getValue());
/*      */ 
/* 2823 */     sendAttributeChangeNotification(localAttributeChangeNotification);
/*      */ 
/* 2825 */     if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
/* 2826 */       JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, RequiredModelMBean.class.getName(), "sendAttributeChangeNotification(Attribute, Attribute)", "Exit");
/*      */   }
/*      */ 
/*      */   protected ClassLoaderRepository getClassLoaderRepository()
/*      */   {
/* 2843 */     return MBeanServerFactory.getClassLoaderRepository(this.server);
/*      */   }
/*      */ 
/*      */   private Class<?> loadClass(final String paramString) throws ClassNotFoundException
/*      */   {
/* 2848 */     AccessControlContext localAccessControlContext = AccessController.getContext();
/* 2849 */     final ClassNotFoundException[] arrayOfClassNotFoundException = new ClassNotFoundException[1];
/*      */ 
/* 2851 */     Class localClass = (Class)javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
/*      */     {
/*      */       public Class<?> run()
/*      */       {
/*      */         try {
/* 2856 */           ReflectUtil.checkPackageAccess(paramString);
/* 2857 */           return Class.forName(paramString);
/*      */         } catch (ClassNotFoundException localClassNotFoundException1) {
/* 2859 */           ClassLoaderRepository localClassLoaderRepository = RequiredModelMBean.this.getClassLoaderRepository();
/*      */           try
/*      */           {
/* 2862 */             if (localClassLoaderRepository == null) throw new ClassNotFoundException(paramString);
/* 2863 */             return localClassLoaderRepository.loadClass(paramString);
/*      */           } catch (ClassNotFoundException localClassNotFoundException2) {
/* 2865 */             arrayOfClassNotFoundException[0] = localClassNotFoundException2;
/*      */           }
/*      */         }
/* 2868 */         return null;
/*      */       }
/*      */     }
/*      */     , localAccessControlContext, this.acc);
/*      */ 
/* 2872 */     if (arrayOfClassNotFoundException[0] != null) {
/* 2873 */       throw arrayOfClassNotFoundException[0];
/*      */     }
/*      */ 
/* 2876 */     return localClass;
/*      */   }
/*      */ 
/*      */   public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
/*      */     throws Exception
/*      */   {
/* 2920 */     if (paramObjectName == null) throw new NullPointerException("name of RequiredModelMBean to registered is null");
/*      */ 
/* 2922 */     this.server = paramMBeanServer;
/* 2923 */     return paramObjectName;
/*      */   }
/*      */ 
/*      */   public void postRegister(Boolean paramBoolean)
/*      */   {
/* 2940 */     this.registered = paramBoolean.booleanValue();
/*      */   }
/*      */ 
/*      */   public void preDeregister()
/*      */     throws Exception
/*      */   {
/*      */   }
/*      */ 
/*      */   public void postDeregister()
/*      */   {
/* 2970 */     this.registered = false;
/* 2971 */     this.server = null;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/* 1170 */     for (int i = 0; i < primitiveClasses.length; i++) {
/* 1171 */       Class localClass = primitiveClasses[i];
/* 1172 */       primitiveClassMap.put(localClass.getName(), localClass);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.modelmbean.RequiredModelMBean
 * JD-Core Version:    0.6.2
 */