/*     */ package javax.management;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.io.StreamCorruptedException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ public class MBeanInfo
/*     */   implements Cloneable, Serializable, DescriptorRead
/*     */ {
/*     */   static final long serialVersionUID = -6451021435135161911L;
/*     */   private transient Descriptor descriptor;
/*     */   private final String description;
/*     */   private final String className;
/*     */   private final MBeanAttributeInfo[] attributes;
/*     */   private final MBeanOperationInfo[] operations;
/*     */   private final MBeanConstructorInfo[] constructors;
/*     */   private final MBeanNotificationInfo[] notifications;
/*     */   private transient int hashCode;
/*     */   private final transient boolean arrayGettersSafe;
/* 541 */   private static final Map<Class<?>, Boolean> arrayGettersSafeMap = new WeakHashMap();
/*     */ 
/*     */   public MBeanInfo(String paramString1, String paramString2, MBeanAttributeInfo[] paramArrayOfMBeanAttributeInfo, MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, MBeanOperationInfo[] paramArrayOfMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo)
/*     */     throws IllegalArgumentException
/*     */   {
/* 192 */     this(paramString1, paramString2, paramArrayOfMBeanAttributeInfo, paramArrayOfMBeanConstructorInfo, paramArrayOfMBeanOperationInfo, paramArrayOfMBeanNotificationInfo, null);
/*     */   }
/*     */ 
/*     */   public MBeanInfo(String paramString1, String paramString2, MBeanAttributeInfo[] paramArrayOfMBeanAttributeInfo, MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, MBeanOperationInfo[] paramArrayOfMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo, Descriptor paramDescriptor)
/*     */     throws IllegalArgumentException
/*     */   {
/* 232 */     this.className = paramString1;
/*     */ 
/* 234 */     this.description = paramString2;
/*     */ 
/* 236 */     if (paramArrayOfMBeanAttributeInfo == null)
/* 237 */       paramArrayOfMBeanAttributeInfo = MBeanAttributeInfo.NO_ATTRIBUTES;
/* 238 */     this.attributes = paramArrayOfMBeanAttributeInfo;
/*     */ 
/* 240 */     if (paramArrayOfMBeanOperationInfo == null)
/* 241 */       paramArrayOfMBeanOperationInfo = MBeanOperationInfo.NO_OPERATIONS;
/* 242 */     this.operations = paramArrayOfMBeanOperationInfo;
/*     */ 
/* 244 */     if (paramArrayOfMBeanConstructorInfo == null)
/* 245 */       paramArrayOfMBeanConstructorInfo = MBeanConstructorInfo.NO_CONSTRUCTORS;
/* 246 */     this.constructors = paramArrayOfMBeanConstructorInfo;
/*     */ 
/* 248 */     if (paramArrayOfMBeanNotificationInfo == null)
/* 249 */       paramArrayOfMBeanNotificationInfo = MBeanNotificationInfo.NO_NOTIFICATIONS;
/* 250 */     this.notifications = paramArrayOfMBeanNotificationInfo;
/*     */ 
/* 252 */     if (paramDescriptor == null)
/* 253 */       paramDescriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
/* 254 */     this.descriptor = paramDescriptor;
/*     */ 
/* 256 */     this.arrayGettersSafe = arrayGettersSafe(getClass(), MBeanInfo.class);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 273 */       return super.clone();
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */     }
/* 276 */     return null;
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/* 288 */     return this.className;
/*     */   }
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 297 */     return this.description;
/*     */   }
/*     */ 
/*     */   public MBeanAttributeInfo[] getAttributes()
/*     */   {
/* 312 */     MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = nonNullAttributes();
/* 313 */     if (arrayOfMBeanAttributeInfo.length == 0) {
/* 314 */       return arrayOfMBeanAttributeInfo;
/*     */     }
/* 316 */     return (MBeanAttributeInfo[])arrayOfMBeanAttributeInfo.clone();
/*     */   }
/*     */ 
/*     */   private MBeanAttributeInfo[] fastGetAttributes() {
/* 320 */     if (this.arrayGettersSafe) {
/* 321 */       return nonNullAttributes();
/*     */     }
/* 323 */     return getAttributes();
/*     */   }
/*     */ 
/*     */   private MBeanAttributeInfo[] nonNullAttributes()
/*     */   {
/* 338 */     return this.attributes == null ? MBeanAttributeInfo.NO_ATTRIBUTES : this.attributes;
/*     */   }
/*     */ 
/*     */   public MBeanOperationInfo[] getOperations()
/*     */   {
/* 354 */     MBeanOperationInfo[] arrayOfMBeanOperationInfo = nonNullOperations();
/* 355 */     if (arrayOfMBeanOperationInfo.length == 0) {
/* 356 */       return arrayOfMBeanOperationInfo;
/*     */     }
/* 358 */     return (MBeanOperationInfo[])arrayOfMBeanOperationInfo.clone();
/*     */   }
/*     */ 
/*     */   private MBeanOperationInfo[] fastGetOperations() {
/* 362 */     if (this.arrayGettersSafe) {
/* 363 */       return nonNullOperations();
/*     */     }
/* 365 */     return getOperations();
/*     */   }
/*     */ 
/*     */   private MBeanOperationInfo[] nonNullOperations() {
/* 369 */     return this.operations == null ? MBeanOperationInfo.NO_OPERATIONS : this.operations;
/*     */   }
/*     */ 
/*     */   public MBeanConstructorInfo[] getConstructors()
/*     */   {
/* 393 */     MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = nonNullConstructors();
/* 394 */     if (arrayOfMBeanConstructorInfo.length == 0) {
/* 395 */       return arrayOfMBeanConstructorInfo;
/*     */     }
/* 397 */     return (MBeanConstructorInfo[])arrayOfMBeanConstructorInfo.clone();
/*     */   }
/*     */ 
/*     */   private MBeanConstructorInfo[] fastGetConstructors() {
/* 401 */     if (this.arrayGettersSafe) {
/* 402 */       return nonNullConstructors();
/*     */     }
/* 404 */     return getConstructors();
/*     */   }
/*     */ 
/*     */   private MBeanConstructorInfo[] nonNullConstructors() {
/* 408 */     return this.constructors == null ? MBeanConstructorInfo.NO_CONSTRUCTORS : this.constructors;
/*     */   }
/*     */ 
/*     */   public MBeanNotificationInfo[] getNotifications()
/*     */   {
/* 424 */     MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = nonNullNotifications();
/* 425 */     if (arrayOfMBeanNotificationInfo.length == 0) {
/* 426 */       return arrayOfMBeanNotificationInfo;
/*     */     }
/* 428 */     return (MBeanNotificationInfo[])arrayOfMBeanNotificationInfo.clone();
/*     */   }
/*     */ 
/*     */   private MBeanNotificationInfo[] fastGetNotifications() {
/* 432 */     if (this.arrayGettersSafe) {
/* 433 */       return nonNullNotifications();
/*     */     }
/* 435 */     return getNotifications();
/*     */   }
/*     */ 
/*     */   private MBeanNotificationInfo[] nonNullNotifications() {
/* 439 */     return this.notifications == null ? MBeanNotificationInfo.NO_NOTIFICATIONS : this.notifications;
/*     */   }
/*     */ 
/*     */   public Descriptor getDescriptor()
/*     */   {
/* 452 */     return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 457 */     return getClass().getName() + "[" + "description=" + getDescription() + ", " + "attributes=" + Arrays.asList(fastGetAttributes()) + ", " + "constructors=" + Arrays.asList(fastGetConstructors()) + ", " + "operations=" + Arrays.asList(fastGetOperations()) + ", " + "notifications=" + Arrays.asList(fastGetNotifications()) + ", " + "descriptor=" + getDescriptor() + "]";
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 489 */     if (paramObject == this)
/* 490 */       return true;
/* 491 */     if (!(paramObject instanceof MBeanInfo))
/* 492 */       return false;
/* 493 */     MBeanInfo localMBeanInfo = (MBeanInfo)paramObject;
/* 494 */     if ((!isEqual(getClassName(), localMBeanInfo.getClassName())) || (!isEqual(getDescription(), localMBeanInfo.getDescription())) || (!getDescriptor().equals(localMBeanInfo.getDescriptor())))
/*     */     {
/* 497 */       return false;
/*     */     }
/*     */ 
/* 500 */     return (Arrays.equals(localMBeanInfo.fastGetAttributes(), fastGetAttributes())) && (Arrays.equals(localMBeanInfo.fastGetOperations(), fastGetOperations())) && (Arrays.equals(localMBeanInfo.fastGetConstructors(), fastGetConstructors())) && (Arrays.equals(localMBeanInfo.fastGetNotifications(), fastGetNotifications()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 515 */     if (this.hashCode != 0) {
/* 516 */       return this.hashCode;
/*     */     }
/* 518 */     this.hashCode = (getClassName().hashCode() ^ getDescriptor().hashCode() ^ arrayHashCode(fastGetAttributes()) ^ arrayHashCode(fastGetOperations()) ^ arrayHashCode(fastGetConstructors()) ^ arrayHashCode(fastGetNotifications()));
/*     */ 
/* 526 */     return this.hashCode;
/*     */   }
/*     */ 
/*     */   private static int arrayHashCode(Object[] paramArrayOfObject) {
/* 530 */     int i = 0;
/* 531 */     for (int j = 0; j < paramArrayOfObject.length; j++)
/* 532 */       i ^= paramArrayOfObject[j].hashCode();
/* 533 */     return i;
/*     */   }
/*     */ 
/*     */   static boolean arrayGettersSafe(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 555 */     if (paramClass1 == paramClass2)
/* 556 */       return true;
/* 557 */     synchronized (arrayGettersSafeMap) {
/* 558 */       Boolean localBoolean = (Boolean)arrayGettersSafeMap.get(paramClass1);
/* 559 */       if (localBoolean == null) {
/*     */         try {
/* 561 */           ArrayGettersSafeAction localArrayGettersSafeAction = new ArrayGettersSafeAction(paramClass1, paramClass2);
/*     */ 
/* 563 */           localBoolean = (Boolean)AccessController.doPrivileged(localArrayGettersSafeAction);
/*     */         }
/*     */         catch (Exception localException) {
/* 566 */           localBoolean = Boolean.valueOf(false);
/*     */         }
/* 568 */         arrayGettersSafeMap.put(paramClass1, localBoolean);
/*     */       }
/* 570 */       return localBoolean.booleanValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isEqual(String paramString1, String paramString2)
/*     */   {
/*     */     boolean bool;
/* 617 */     if (paramString1 == null)
/* 618 */       bool = paramString2 == null;
/*     */     else {
/* 620 */       bool = paramString1.equals(paramString2);
/*     */     }
/*     */ 
/* 623 */     return bool;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 652 */     paramObjectOutputStream.defaultWriteObject();
/*     */ 
/* 654 */     if (this.descriptor.getClass() == ImmutableDescriptor.class) {
/* 655 */       paramObjectOutputStream.write(1);
/*     */ 
/* 657 */       String[] arrayOfString = this.descriptor.getFieldNames();
/*     */ 
/* 659 */       paramObjectOutputStream.writeObject(arrayOfString);
/* 660 */       paramObjectOutputStream.writeObject(this.descriptor.getFieldValues(arrayOfString));
/*     */     } else {
/* 662 */       paramObjectOutputStream.write(0);
/*     */ 
/* 664 */       paramObjectOutputStream.writeObject(this.descriptor);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 701 */     paramObjectInputStream.defaultReadObject();
/*     */ 
/* 703 */     switch (paramObjectInputStream.read()) {
/*     */     case 1:
/* 705 */       String[] arrayOfString = (String[])paramObjectInputStream.readObject();
/*     */ 
/* 707 */       if (arrayOfString.length == 0) {
/* 708 */         this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
/*     */       } else {
/* 710 */         Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
/* 711 */         this.descriptor = new ImmutableDescriptor(arrayOfString, arrayOfObject);
/*     */       }
/*     */ 
/* 714 */       break;
/*     */     case 0:
/* 716 */       this.descriptor = ((Descriptor)paramObjectInputStream.readObject());
/*     */ 
/* 718 */       if (this.descriptor == null)
/* 719 */         this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR; break;
/*     */     case -1:
/* 724 */       this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
/*     */ 
/* 726 */       break;
/*     */     default:
/* 728 */       throw new StreamCorruptedException("Got unexpected byte.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ArrayGettersSafeAction
/*     */     implements PrivilegedAction<Boolean>
/*     */   {
/*     */     private final Class<?> subclass;
/*     */     private final Class<?> immutableClass;
/*     */ 
/*     */     ArrayGettersSafeAction(Class<?> paramClass1, Class<?> paramClass2)
/*     */     {
/* 588 */       this.subclass = paramClass1;
/* 589 */       this.immutableClass = paramClass2;
/*     */     }
/*     */ 
/*     */     public Boolean run() {
/* 593 */       Method[] arrayOfMethod = this.immutableClass.getMethods();
/* 594 */       for (int i = 0; i < arrayOfMethod.length; i++) {
/* 595 */         Method localMethod1 = arrayOfMethod[i];
/* 596 */         String str = localMethod1.getName();
/* 597 */         if ((str.startsWith("get")) && (localMethod1.getParameterTypes().length == 0) && (localMethod1.getReturnType().isArray()))
/*     */         {
/*     */           try
/*     */           {
/* 601 */             Method localMethod2 = this.subclass.getMethod(str, new Class[0]);
/*     */ 
/* 603 */             if (!localMethod2.equals(localMethod1))
/* 604 */               return Boolean.valueOf(false);
/*     */           } catch (NoSuchMethodException localNoSuchMethodException) {
/* 606 */             return Boolean.valueOf(false);
/*     */           }
/*     */         }
/*     */       }
/* 610 */       return Boolean.valueOf(true);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanInfo
 * JD-Core Version:    0.6.2
 */